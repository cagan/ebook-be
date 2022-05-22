package com.cagan.library.web.rest;

import com.cagan.library.domain.Order;
import com.cagan.library.domain.User;
import com.cagan.library.integration.stripe.CardPaymentObject;
import com.cagan.library.repository.OrderRepository;
import com.cagan.library.repository.UserRepository;
import com.cagan.library.security.SecurityUtils;
import com.cagan.library.service.OrderService;
import com.cagan.library.service.dto.CheckoutItemDto;
import com.cagan.library.service.dto.request.paymentintent.CompletePaymentIntentRequest;
import com.cagan.library.service.dto.request.paymentintent.InitialPaymentIntentRequest;
import com.cagan.library.service.dto.request.paymentintent.UpdatePaymentIntentAmountRequest;
import com.cagan.library.service.dto.response.StripeSessionResponse;
import com.cagan.library.service.dto.view.MessageResponse;
import com.cagan.library.util.HeaderUtil;
import com.cagan.library.web.errors.BadRequestAlertException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {
    private final UserRepository userRepository;
    private final OrderService orderService;
    private final OrderRepository orderRepository;

    @PostMapping("/create-checkout-session")
    public ResponseEntity<StripeSessionResponse> createCheckoutSession(@Valid @RequestBody List<CheckoutItemDto> checkoutItemDtoList) throws StripeException {
        Session session = orderService.createCheckoutSession(checkoutItemDtoList);
        StripeSessionResponse response = new StripeSessionResponse(session.getId());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/create-payment-intent")
    public ResponseEntity<String> createPaymentIntent(@Valid @RequestBody InitialPaymentIntentRequest paymentIntentObject) throws StripeException {
        User user = SecurityUtils.getCurrentUserLogin()
                .flatMap(userRepository::findOneByLogin)
                .orElseThrow(() -> new BadRequestAlertException("Bad request", "User", "USER_NOT_FOUND_WITH_LOGIN"));

        String paymentIntentId = orderRepository.findFirstByUserAndOrderCompletedFalse(user)
                .map((order) -> {
                    try {
                        return orderService.updateOrder(order, paymentIntentObject);
                    } catch (StripeException e) {
                        throw new RuntimeException(e);
                    }
                })
                .orElseGet(() -> {
                    try {
                        return orderService.createNewOrder(user, paymentIntentObject);
                    } catch (StripeException e) {
                        throw new RuntimeException(e);
                    }
                });

        // TODO: Change return type
        return ResponseEntity.ok().body(paymentIntentId);
    }

    @PostMapping("/add-card-info-payment-method")
    public ResponseEntity<String> addCardPaymentMethod(@Valid @RequestBody CardPaymentObject cardPaymentObject) throws StripeException {
        User user = SecurityUtils.getCurrentUserLogin()
                .flatMap(userRepository::findOneByLogin)
                .orElseThrow(() -> new BadRequestAlertException("Bad request", "User", "USER_NOT_FOUND_WITH_LOGIN"));

        String paymentIntentId = orderRepository.findFirstByUserAndOrderCompletedFalse(user)
                .map(order -> {
                    try {
                        return orderService.addCardPaymentMethod(user, order, cardPaymentObject);
                    } catch (StripeException e) {
                        throw new RuntimeException(e);
                    }
                }).orElseThrow(() -> new BadRequestAlertException("Bad request", "Order", "ORDER_NOT_FOUND_WITH_LOGIN"));

        return ResponseEntity.ok().body(paymentIntentId);
    }

    // TODO: remove this service because cart items already gets the payment amount
    @PostMapping("/update-payment-amount")
    public ResponseEntity<String> updatePaymentAmount() {
        User user = SecurityUtils.getCurrentUserLogin()
                .flatMap(userRepository::findOneByLogin)
                .orElseThrow(() -> new BadRequestAlertException("Bad request", "User", "USER_NOT_FOUND_WITH_LOGIN"));

        String paymentIntentId = orderRepository.findFirstByUserAndOrderCompletedFalse(user)
                .map(order -> {
                    try {
                        return orderService.updateTotalPrice(user, order);
                    } catch (StripeException e) {
                        throw new RuntimeException(e);
                    }
                }).orElseThrow(() -> new BadRequestAlertException("Bad request", "Order", "ORDER_NOT_FOUND_WITH_LOGIN"));

        return ResponseEntity.ok().body(paymentIntentId);
    }

    @PostMapping("/complete-payment-intent")
    public ResponseEntity<Order> completePaymentIntent() throws StripeException {
        User user = SecurityUtils.getCurrentUserLogin()
                .flatMap(userRepository::findOneByLogin)
                .orElseThrow(() -> new BadRequestAlertException("Bad request", "User", "USER_NOT_FOUND_WITH_LOGIN"));

        Order result = orderRepository.findFirstByUserAndOrderCompletedFalse(user)
                .map(order -> {
                    try {
                        return orderService.completeOrder(user, order);
                    } catch (InvalidRequestException exception) {
                        throw new BadRequestAlertException("This payment already confirmed", "Order", "payment_already_confirmed");
                    } catch (StripeException e) {
                        throw new RuntimeException(e);
                    }
                }).orElseThrow(() -> new BadRequestAlertException("Bad request", "Order", "ORDER_NOT_FOUND_WITH_LOGIN"));

        return ResponseEntity.ok().body(result);
    }

    @PostMapping("/complete")
    public ResponseEntity<MessageResponse> placeOrder(@RequestParam("sessionId") String sessionId) {
        User user = SecurityUtils.getCurrentUserLogin()
                .flatMap(userRepository::findOneByLogin)
                .orElseThrow(() -> new BadRequestAlertException("Bad request", "User", "USER_NOT_FOUND_WITH_LOGIN"));

        var order = orderService.placeOrder(user, sessionId);

        ResponseEntity.created(URI.create("/api/v1/order/orderId"))
                .headers(HeaderUtil.createEntityCreationAlert("ebook-be", true, "Order", String.valueOf(order.getId())))
                .body(new MessageResponse("New order created with: " + order.getId()));

        return ResponseEntity.ok(new MessageResponse("New order created with: " + order.getId()));
    }
}
