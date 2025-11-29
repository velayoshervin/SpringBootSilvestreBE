package com.silvestre.web_applicationv1.config;


import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BeanConfig {

    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); //
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }

    @Bean
    public CommandLineRunner enableKeyspaceNotifications(RedisConnectionFactory connectionFactory) {
        return args -> {
            var connection = connectionFactory.getConnection();
            var server = connection.serverCommands();

            // Enable keyspace notification for expired keys
            server.setConfig("notify-keyspace-events", "Ex");

            // Safely check config value
            var configMap = server.getConfig("notify-keyspace-events");
            String value = null;

            if (configMap != null && configMap.containsKey("notify-keyspace-events")) {
                Object rawValue = configMap.get("notify-keyspace-events");
                value = (rawValue != null) ? rawValue.toString() : null;
            }

            System.out.println("✅ Keyspace notifications enabled: " + (value != null ? value : "<unknown>"));
        };
    }

}

