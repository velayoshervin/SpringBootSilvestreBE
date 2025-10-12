package com.silvestre.web_applicationv1.requests;

import java.util.*;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

public class CloudinarySignatureUtil {

    public static String generateSignature(Map<String, String> params, String apiSecret) throws Exception {
        // Sort parameters by key
        SortedMap<String, String> sortedParams = new TreeMap<>(params);

        // Build the signature string
        StringBuilder toSign = new StringBuilder();
        for (Map.Entry<String, String> entry : sortedParams.entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                toSign.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
        }
        // Remove last '&' and append API secret
        toSign.setLength(toSign.length() - 1);
        toSign.append(apiSecret);

        // Generate SHA-1 hash
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] digest = md.digest(toSign.toString().getBytes(StandardCharsets.UTF_8));

        // Convert to hex
        StringBuilder hexString = new StringBuilder();
        for (byte b : digest) {
            hexString.append(String.format("%02x", b));
        }

        return hexString.toString();
    }

    public static void main(String[] args) throws Exception {
        String apiSecret = "your_api_secret_here";
        long timestamp = System.currentTimeMillis() / 1000L;

        Map<String, String> params = new HashMap<>();
        params.put("timestamp", String.valueOf(timestamp));
        // optionally add: public_id, eager, folder, etc.

        String signature = generateSignature(params, apiSecret);
        System.out.println("Signature: " + signature);
        System.out.println("Timestamp: " + timestamp);
    }
}
