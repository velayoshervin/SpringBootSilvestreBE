package com.silvestre.web_applicationv1.config;

import com.silvestre.web_applicationv1.entity.BookingHistory;
import com.silvestre.web_applicationv1.entity.Notification;
import com.silvestre.web_applicationv1.entity.Quotation;
import com.silvestre.web_applicationv1.repository.NotificationRepository;
import com.silvestre.web_applicationv1.service.NotificationService;
import com.silvestre.web_applicationv1.service.QuotationRedisService;
import com.silvestre.web_applicationv1.service.QuotationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.listener.KeyExpirationEventMessageListener;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class RedisKeyExpirationListener extends KeyExpirationEventMessageListener {
    public RedisKeyExpirationListener(RedisMessageListenerContainer listenerContainer) {
        super(listenerContainer);
    }

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private QuotationService quotationService;

    @Autowired
    private QuotationRedisService quotationRedisService;

    @Autowired
    private NotificationRepository notificationRepository;


    @Override
    public void onMessage(Message message, byte[] pattern) {

        System.out.println("Expired quotation detected!");
        String expiredKey = message.toString();

        System.out.println("Raw expired key: '" + message.toString() + "'");

        expiredKey = expiredKey.replaceAll("[^\\p{Print}]", "").trim();

        System.out.println("Cleaned expired key: '" + expiredKey + "'");
        System.out.println("⚠️ Key expired: '" + expiredKey + "'");

        // Example: Check if this is a booking key and handle logic
        if (expiredKey.startsWith("quotation:")) {

            String quotationIdStr = expiredKey.replace("quotation:", "").trim();

            Long quotationId = Long.valueOf(quotationIdStr);
            System.out.println("Quotation ID: " + quotationId);

            quotationService.setExpired(quotationId);

            System.out.println("✅ Successfully processed expired quotation: " + quotationIdStr);


        }
    }
}
