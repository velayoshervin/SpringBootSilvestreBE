package com.silvestre.web_applicationv1.util;

import java.util.UUID;

public class PaymentRefGenerator {
    public static String generateRef() {
        long millis = System.currentTimeMillis(); // current time in milliseconds
        String randomPart = UUID.randomUUID().toString().substring(0, 8); // random part
        return millis + "-" + randomPart;
    }

    public static void main(String[] args) {
        System.out.println(generateRef());
    }
}
