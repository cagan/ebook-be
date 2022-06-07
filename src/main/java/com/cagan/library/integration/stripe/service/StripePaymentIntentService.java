package com.cagan.library.integration.stripe.service;

import com.cagan.library.integration.stripe.CardPaymentObject;
import com.cagan.library.integration.stripe.CheckoutItem;
import com.cagan.library.integration.stripe.PaymentIntentObject;
import com.cagan.library.service.dto.request.paymentintent.InitialPaymentIntentRequest;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.model.checkout.Session;
import com.stripe.param.*;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class StripePaymentIntentService {
    @Value("${stripe.secret_key}")
    private String apiKey;

    public PaymentMethodCreateParams.CardDetails createCardDetails(CardPaymentObject object) {
        Stripe.apiKey = apiKey;
        return PaymentMethodCreateParams.CardDetails.builder()
                .setNumber(object.getNumber())
                .setExpYear(object.getExpYear())
                .setExpMonth(object.getExpMonth())
                .setCvc(object.getCvc())
                .build();
    }

    public PaymentMethod createCardPaymentMethod(CardPaymentObject object) throws StripeException {
        Stripe.apiKey = apiKey;
        PaymentMethodCreateParams.CardDetails cardDetails = createCardDetails(object);

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

    public PaymentIntent updateAmountToPaymentIntent(String paymentIntentId, Long amount) throws StripeException {
        Stripe.apiKey = apiKey;
        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);

        PaymentIntentUpdateParams updateParams = PaymentIntentUpdateParams
                .builder()
                .setAmount(amount)
                .build();

        return paymentIntent.update(updateParams);
    }

    public PaymentIntent addPaymentMethodToPaymentIntent(String paymentIntentId, String paymentMethodId) throws StripeException {
        Stripe.apiKey = apiKey;
        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);

        PaymentIntentUpdateParams updateParams = PaymentIntentUpdateParams
                .builder()
                .setPaymentMethod(paymentMethodId)
                .build();

        paymentIntent.setPaymentMethod(paymentMethodId);
        return paymentIntent.update(updateParams);
    }

    public PaymentIntent createInitialPaymentIntent(InitialPaymentIntentRequest object, Long totalPrice) throws StripeException {
        Stripe.apiKey = apiKey;

        PaymentIntentCreateParams.Builder builder = PaymentIntentCreateParams.builder();

        builder.setAmount(totalPrice * 1000);

        if (object.getDescription() != null) {
            builder.setDescription(object.getDescription());
        }

        builder.addPaymentMethodType(object.getPaymentMethodType().toString());
        builder.setCurrency("try");

//        builder.setAutomaticPaymentMethods(PaymentIntentCreateParams.AutomaticPaymentMethods.builder().setEnabled(true).build());
        PaymentIntentCreateParams params = builder.build();

        return PaymentIntent.create(params);
    }

    public PaymentIntent createFullPaymentIntent(PaymentIntentObject object) throws StripeException {
        Stripe.apiKey = apiKey;
        PaymentIntentCreateParams createParams = PaymentIntentCreateParams
                .builder()
                .setCurrency(object.getCurrency())
                .setAutomaticPaymentMethods(
                        PaymentIntentCreateParams.AutomaticPaymentMethods.builder().setEnabled(true).build()
                )
                .setAmount(object.getAmount())
                .setPaymentMethod(object.getPaymentMethodId())
                .setConfirm(false)
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

        for (E checkoutItemDto : checkoutItemDtoList) {
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
                .setUnitAmount((long) (checkoutItemDto.getPrice() * 100))
                .setCurrency("USD")
                .setProductData(
                        SessionCreateParams.LineItem.PriceData.ProductData
                                .builder()
                                .setName(checkoutItemDto.getProductName())
                                .build()
                ).build();
    }
}
