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
    private String paymentMethodId;
    private String currency;
    private long amount;
    private boolean confirm;
    private String description;
    private PaymentMethodType paymentMethodType;

    public enum PaymentMethodType {
        card
    }
}
