package com.cagan.library.integration.stripe;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.model.checkout.Session;
import com.stripe.param.PaymentIntentCreateParams;
import com.stripe.param.PaymentMethodCreateParams;
import com.stripe.param.checkout.SessionCreateParams;
import liquibase.pro.packaged.X;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StripePaymentService {
    @Value("${stripe.secret_key}")
    private String apiKey;

    public PaymentMethod createCardPaymentMethod(CardPaymentObject object) throws StripeException {
        Stripe.apiKey = apiKey;
        PaymentMethodCreateParams.CardDetails cardDetails = PaymentMethodCreateParams.CardDetails.builder()
                .setNumber(object.getNumber())
                .setExpYear(object.getExpYear())
                .setExpMonth(object.getExpMonth())
                .setCvc(object.getCvc())
                .build();

        PaymentMethodCreateParams createParams = PaymentMethodCreateParams.builder()
                .setType(PaymentMethodCreateParams.Type.CARD)
                .setCard(cardDetails)
                .build();

        return PaymentMethod.create(createParams);
    }

    public PaymentIntent confirmPaymentIntent(String paymentIntentId) throws StripeException {
        Stripe.apiKey = apiKey;
        return PaymentIntent.retrieve(paymentIntentId).confirm();
    }

    public PaymentIntent createPaymentIntent(PaymentIntentObject object) throws StripeException {
        Stripe.apiKey = apiKey;
        PaymentIntentCreateParams createParams = PaymentIntentCreateParams
                .builder()
                .setCurrency(object.getCurrency())
                .setAmount(object.getAmount())
                .setPaymentMethod(object.getPaymentMethodId())
                .setConfirm(object.isConfirm())
                .setDescription(object.getDescription())
                .addPaymentMethodType(object.getPaymentMethodType().toString())
                .build();

        return PaymentIntent.create(createParams);
    }

    public <E extends CheckoutItem> Session createCheckoutSession(List<E> checkoutItemDtoList) throws StripeException {
        Stripe.apiKey = apiKey;
        String successUrl = "http://localhost:8080?success=true";
        String cancelUrl = "http://localhost:8080?cancel=true";

        List<SessionCreateParams.LineItem> sessionItemList = new ArrayList<>();

        for (E checkoutItemDto: checkoutItemDtoList) {
            sessionItemList.add(createSessionLineItem(checkoutItemDto));
        }

        SessionCreateParams sessionCreateParams = SessionCreateParams
                .builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(successUrl)
                .setCancelUrl(cancelUrl)
                .addAllLineItem(sessionItemList)
                .build();

        return Session.create(sessionCreateParams);
    }

    private <E extends CheckoutItem> SessionCreateParams.LineItem createSessionLineItem(E checkoutItemDto) {
        return SessionCreateParams.LineItem
                .builder()
                .setPriceData(createPriceData(checkoutItemDto))
                .setQuantity(Long.parseLong(String.valueOf(checkoutItemDto.getQuantity())))
                .build();
    }

    private <E extends CheckoutItem> SessionCreateParams.LineItem.PriceData createPriceData(E checkoutItemDto) {
        return SessionCreateParams.LineItem.PriceData
                .builder()
                .setUnitAmount((long)(checkoutItemDto.getPrice() * 100))
                .setCurrency("USD")
                .setProductData(
                        SessionCreateParams.LineItem.PriceData.ProductData
                                .builder()
                                .setName(checkoutItemDto.getProductName())
                                .build()
                ).build();
    }
}
