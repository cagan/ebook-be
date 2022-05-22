package com.cagan.library.service;

import com.cagan.library.domain.*;
import com.cagan.library.domain.Order;
import com.cagan.library.domain.OrderItem;
import com.cagan.library.integration.stripe.CardPaymentObject;
import com.cagan.library.integration.stripe.StripePaymentService;
import com.cagan.library.repository.*;
import com.cagan.library.service.dto.CheckoutItemDto;
import com.cagan.library.service.dto.request.paymentintent.InitialPaymentIntentRequest;
import com.cagan.library.service.dto.view.CartItemView;
import com.cagan.library.service.dto.view.CartView;
import com.cagan.library.web.errors.BadRequestAlertException;
import com.cagan.library.web.errors.OrderAlreadyCompleted;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.model.checkout.Session;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
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
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final BookInSystemRepository bookInSystemRepository;

    public Session createCheckoutSession(List<CheckoutItemDto> checkoutItemDtoList) throws StripeException {
        return paymentService.createCheckoutSession(checkoutItemDtoList);
    }

    public String createNewOrder(User user, InitialPaymentIntentRequest paymentIntentObject) throws StripeException {
        Order order = new Order();
        order.setUser(user);
        order.setOrderCompleted(false);

        BigDecimal totalPrice = cartService.getCartView(user).getTotalPrice();
        order.setTotalPrice(totalPrice);

        PaymentIntent paymentIntent = paymentService.createInitialPaymentIntent(paymentIntentObject, totalPrice.longValue() * 1000);

        order.setPaymentIntentId(paymentIntent.getId());

        orderRepository.save(order);

        return paymentIntent.getId();
    }

    public String updateOrder(Order order, InitialPaymentIntentRequest paymentIntentRequest) throws StripeException {
        BigDecimal totalPrice = cartService.getCartView(order.getUser()).getTotalPrice();
        order.setTotalPrice(totalPrice);
        PaymentIntent paymentIntent = paymentService.updateAmountToPaymentIntent(order.getPaymentIntentId(), totalPrice.longValue() * 1000);
        orderRepository.save(order);

        return paymentIntent.getId();
    }

    public String updateTotalPrice(User user, Order order) throws StripeException {
        if (!order.getUser().equals(user)) {
            throw new BadRequestAlertException("Order is not belongs to that user", "Order", "NOT_BELONG_ORDER");
        }

        if (order.isOrderCompleted()) {
            throw new OrderAlreadyCompleted();
        }

        BigDecimal totalPrice = cartService.getCartView(user).getTotalPrice();
        order.setTotalPrice(totalPrice);

        orderRepository.save(order);
        return paymentService.updateAmountToPaymentIntent(order.getPaymentIntentId(), totalPrice.longValue()).getId();
    }

    public String addCardPaymentMethod(User user, Order order, CardPaymentObject cardPaymentObject) throws StripeException {
        if (!order.getUser().equals(user)) {
            throw new BadRequestAlertException("Order is not belongs to that user", "Order", "NOT_BELONG_ORDER");
        }

        if (order.isOrderCompleted()) {
            throw new OrderAlreadyCompleted();
        }

        PaymentMethod paymentMethod = paymentService.createCardPaymentMethod(cardPaymentObject);
        PaymentIntent paymentIntent = paymentService.addPaymentMethodToPaymentIntent(order.getPaymentIntentId(), paymentMethod.getId());
        return paymentIntent.getId();
    }

    public Order completeOrder(User user, Order order) throws StripeException {
        CartView cartView = cartService.getCartView(user);
        List<CartItemView> cartItemViewList = cartView.getCartItems();

        for (CartItemView cartItemView : cartItemViewList) {
            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .bookCatalog(cartItemView.getBookCatalog())
                    .price(cartItemView.getBookCatalog().getPrice())
                    .quantity(cartItemView.getQuantity())
                    .build();

            orderItemRepository.save(orderItem);
            log.info("[ORDER_ITEM: {}] created for [ORDER: {}]", orderItem, order);


            Book book = bookInSystemRepository.findByBookCatalogIdAndIsAvailable(orderItem.getBookCatalog().getId(), true)
                    .orElseThrow(() -> new BadRequestAlertException("Available book Not found", "BooksInSystem", "not_found"))
                    .getBook();

            // TODO: insert purchased books into the user_books table.
            user.getBooks().add(book);
            userRepository.save(user);
            log.info("[BOOK: {}] added to the [USER: {}]'s purchased list", book, user);
        }

        order.setTotalPrice(cartService.calculateTotalPrice(cartItemViewList));

        cartService.deleteUserCartItems(user);
        order.setOrderCompleted(true);

        paymentService.confirmPaymentIntent(order.getPaymentIntentId());

        return order;
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
