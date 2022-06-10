package com.cagan.library.integration.stripe;

public interface CheckoutItem {

    String getProductName();

    int getQuantity();

    double getPrice();

    long getProductId();

    int getUserId();
}
