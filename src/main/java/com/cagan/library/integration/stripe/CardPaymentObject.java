package com.cagan.library.integration.stripe;

import lombok.Data;

@Data
public class CardPaymentObject {
    private String number;
    private Long expYear;
    private Long expMonth;
    private String cvc;
    private Long orderId;
}
