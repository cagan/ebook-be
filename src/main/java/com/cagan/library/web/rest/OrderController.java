package com.cagan.library.web.rest;

import com.cagan.library.domain.User;
import com.cagan.library.repository.UserRepository;
import com.cagan.library.security.SecurityUtils;
import com.cagan.library.service.OrderService;
import com.cagan.library.service.dto.CheckoutItemDto;
import com.cagan.library.service.dto.response.StripeSessionResponse;
import com.cagan.library.service.dto.view.MessageResponse;
import com.cagan.library.util.HeaderUtil;
import com.cagan.library.web.errors.BadRequestAlertException;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.net.StripeResponse;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionListLineItemsParams;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/order")
@RequiredArgsConstructor
public class OrderController {
    private final UserRepository userRepository;
    private final OrderService orderService;

    // TODO: Add create checkout session method after adding the stripe backend sdk
    @PostMapping("/create-checkout-session")
    public ResponseEntity<StripeSessionResponse> createCheckoutSession(@Valid @RequestBody List<CheckoutItemDto> checkoutItemDtoList) throws StripeException {
        Session session = orderService.createCheckoutSession(checkoutItemDtoList);
        StripeSessionResponse response = new StripeSessionResponse(session.getId());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/add")
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
