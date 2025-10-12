package com.silvestre.web_applicationv1.service;

import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class ReferenceNumberGeneratorService {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

   private static final SecureRandom RANDOM = new SecureRandom();

   public String generateRef(String quotation){

       int length = 12;
       StringBuilder stringBuilder= new StringBuilder();

       stringBuilder.append(quotation);
       for(int i = 0; i<length; i++){
           stringBuilder.append(CHARACTERS.charAt(RANDOM.nextInt(CHARACTERS.length())));
       }
       return stringBuilder.toString();

   }
}
