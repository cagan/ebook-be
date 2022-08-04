package com.cagan.library.web.errors;

public class OrderAlreadyCompleted extends BadRequestAlertException {

    public OrderAlreadyCompleted() {
        super(ErrorConstants.NON_ACTIVE_ORDER_SESSION, "Order already completed", "Order", "orderalreadycompleted");
    }
}
