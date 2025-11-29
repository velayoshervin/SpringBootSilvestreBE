package com.silvestre.web_applicationv1.Dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentAttributes {
    private Long amount;
    private Long net_amount;
    private Long fee;
    private String description;
    private String status;
    private Source source;
    private Long paid_at;

    @Getter
    @Setter
    public static class Source {
        private String type;
        private String brand;
    }
}
