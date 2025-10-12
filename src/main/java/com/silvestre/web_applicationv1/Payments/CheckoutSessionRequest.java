package com.silvestre.web_applicationv1.Payments;


// CheckoutSessionRequest.java
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CheckoutSessionRequest {
    private Data data;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Data {
        private Attributes attributes;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Attributes {
        private String description;
        private String reference_number;
        private String success_url;
        private String cancel_url;
        private boolean send_email_receipt;
        private boolean show_line_items;
        private boolean show_description;
        private List<String> payment_method_types;
        private List<LineItem> line_items;
        private Map<String, Object> metadata;
        private Long amount;

    }

    

    @Setter
    @Getter
    public static class  LineItem {
        private String name;
        private String description;
        private int amount;
        private int quantity;
        private String currency;

        public LineItem() {}

        public LineItem(String name, String description, int amount, int quantity, String currency) {
            this.name = name;
            this.description = description;
            this.amount = amount;
            this.quantity = quantity;
            this.currency = currency;
        }
    }
}
