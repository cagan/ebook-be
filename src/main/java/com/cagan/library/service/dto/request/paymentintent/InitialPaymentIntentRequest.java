package com.cagan.library.service.dto.request.paymentintent;

import com.cagan.library.integration.stripe.PaymentIntentObject;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class InitialPaymentIntentRequest {
    private String description;
    @NotNull
    private PaymentIntentObject.PaymentMethodType paymentMethodType;
}
