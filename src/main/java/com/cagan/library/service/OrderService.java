package com.cagan.library.service;

import com.cagan.library.domain.Order;
import com.cagan.library.domain.OrderItem;
import com.cagan.library.domain.User;
import com.cagan.library.integration.stripe.StripePaymentService;
import com.cagan.library.repository.OrderItemRepository;
import com.cagan.library.repository.OrderRepository;
import com.cagan.library.service.dto.CheckoutItemDto;
import com.cagan.library.service.dto.view.CartItemView;
import com.cagan.library.service.dto.view.CartView;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Card;
import com.stripe.model.LineItem;
import com.stripe.model.PaymentIntent;
import com.stripe.model.PaymentMethod;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    private final CartService cartService;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    private final StripePaymentService paymentService;

    @Value("${stripe.secret_key}")
    private String apiKey;

    public Session createCheckoutSession(List<CheckoutItemDto> checkoutItemDtoList) throws StripeException {
        return paymentService.createCheckoutSession(checkoutItemDtoList);
    }

//    public void completePayment(String sessionId) throws StripeException {
//        Card card = new Card();
//        card.setExpYear(2025L);
//        card.setExpMonth(11L);
//        card.setCurrency("USD");
//        card.set
//
//
//        PaymentMethod paymentMethod = new PaymentMethod();
//        paymentMethod.setCard();
//
//        PaymentIntent paymentIntent = new PaymentIntent();
//        paymentIntent.setPaymentMethodObject();
//
//        Session.retrieve(sessionId)
//                .setPaymentIntentObject();
//    }

    private SessionCreateParams.LineItem createSessionLineItem(CheckoutItemDto checkoutItemDto) {
        return SessionCreateParams.LineItem
                .builder()
                .setPriceData(createPriceData(checkoutItemDto))
                .setQuantity(Long.parseLong(String.valueOf(checkoutItemDto.getQuantity())))
                .build();
    }

    private SessionCreateParams.LineItem.PriceData createPriceData(CheckoutItemDto checkoutItemDto) {
        return SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency("usd")
                .setUnitAmount((long) (checkoutItemDto.getPrice() * 100))
                .setProductData(
                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                .setName(checkoutItemDto.getProductName())
                                .build()
                )
                .build();
    }

    public Order placeOrder(User user, String sessionId) {
        CartView cartView = cartService.getCartView(user);
        List<CartItemView> cartItemViewList = cartView.getCartItems();

        Order order = Order.builder()
                .sessionId(sessionId)
                .user(user)
                .totalPrice(cartView.getTotalPrice())
                .build();

        orderRepository.save(order);
        log.info("[ORDER: {}] created for [USER: {}]", order, user);

        for (CartItemView cartItemView : cartItemViewList) {
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .bookCatalog(cartItemView.getBookCatalog())
                    .price(cartItemView.getBookCatalog().getPrice())
                    .quantity(cartItemView.getQuantity())
                    .build();

            orderItemRepository.save(orderItem);
            log.info("[ORDER_ITEM: {}] created for [ORDER: {}]", orderItem, order);
        }

        cartService.deleteUserCartItems(user);
        return order;
    }

}
