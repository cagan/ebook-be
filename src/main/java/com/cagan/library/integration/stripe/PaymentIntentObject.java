package com.cagan.library.integration.stripe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentIntentObject {
    private String currency;
    private long amount;
    private String description;
    private PaymentMethodType paymentMethodType;
    private String paymentMethodId;

    public enum PaymentMethodType {
        card
    }
}
