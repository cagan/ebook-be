package com.cagan.library.service;

import com.cagan.library.domain.BookCatalog;
import com.cagan.library.domain.User;
import com.cagan.library.integration.stripe.StripeInvoiceService;
import com.cagan.library.repository.BookCatalogRepository;
import com.cagan.library.repository.UserRepository;
import com.cagan.library.service.dto.view.CartItemView;
import com.cagan.library.service.dto.view.CartView;
import com.stripe.exception.StripeException;
import com.stripe.model.Invoice;
import com.stripe.param.InvoiceCreateParams;
import com.stripe.param.InvoiceSendInvoiceParams;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class InvoiceService {
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    private static class InvoiceCreateObject {
        private String productId;
        private String priceId;
        private String invoiceId;
        private BookCatalog bookCatalog;
    }

    private static final Logger log = LoggerFactory.getLogger(StripeInvoiceService.class);

    private final UserRepository userRepository;
    private final BookCatalogRepository bookCatalogRepository;
    private final StripeInvoiceService stripeCheckoutService;
    private final CartService cartService;

    // TODO: Apply complete order logic
    // TODO: Price Conversion?
    public String createNewInvoice(User user, CartView cartView) {
        String customerId = getOrCreateCustomer(user);

        List<String> invoiceItemList = cartView.getCartItems()
                .stream()
                .map(this::getOrCreateProduct)
                .map(this::getOrCreatePrice)
                .map(priceId -> createInvoiceItem(priceId, customerId))
                .toList();

        invoiceItemList.forEach((item) -> log.info("[INVOICE ITEM: {} CREATED FOR [USER: {}]]", item, user));
        cartService.deleteUserCartItems(user);

        String hostedInvoiceUrl = stripeCheckoutService.sendInvoice(customerId);
        log.info("[HOSTED_INVOICE_URL] CREATED: {}", hostedInvoiceUrl);
        return hostedInvoiceUrl;
    }


    private String getOrCreateCustomer(User user) {
        return userRepository.findByIdAndCustomerIdNotNull(user.getId())
                .map(User::getCustomerId)
                .orElseGet(() -> {
                    try {
                        String customerId = stripeCheckoutService.createNewCustomer(user);
                        user.setCustomerId(customerId);
                        userRepository.save(user);
                        log.info("New [CUSTOMER: {}] created for [USER: {}]", customerId, user);
                        return customerId;
                    } catch (StripeException e) {
                        log.error("ERROR CREATING NEW [USER: {}]", user);
                        throw new RuntimeException(e);
                    }
                });
    }

    private InvoiceCreateObject getOrCreateProduct(CartItemView cartItemView) {
        return bookCatalogRepository.findByIdAndProductIdNotNull(cartItemView.getBookCatalog().getId())
                .map((bookCatalog) -> {
                    InvoiceCreateObject ico = new InvoiceCreateObject();
                    ico.setProductId(bookCatalog.getProductId());
                    ico.setBookCatalog(bookCatalog);
                    return ico;
                })
                .orElseGet(() -> {
                    try {
                        String productId = stripeCheckoutService.createNewProduct(cartItemView);
                        BookCatalog bookCatalog = cartItemView.getBookCatalog();
                        bookCatalog.setProductId(productId);
                        bookCatalogRepository.save(bookCatalog);
                        log.info("NEW [PRODUCT: {}] CREATED", productId);

                        InvoiceCreateObject ico = new InvoiceCreateObject();
                        ico.setBookCatalog(bookCatalog);
                        ico.setProductId(productId);
                        return ico;
                    } catch (StripeException e) {
                        log.error("ERROR CREATING NEW [PRODUCT: {}]", cartItemView.getBookCatalog());
                        throw new RuntimeException(e);
                    }
                });
    }

    private InvoiceCreateObject getOrCreatePrice(InvoiceCreateObject ico) {
        return bookCatalogRepository.findByIdAndPriceIdNotNull(ico.getBookCatalog().getId())
                .map(bookCatalog -> {
                    ico.setPriceId(bookCatalog.getPriceId());
                    return ico;
                }).orElseGet(() -> {
                    try {
                        String priceId = stripeCheckoutService.createNewPrice(ico.getBookCatalog().getPrice().longValue(), ico.productId);
                        ico.setPriceId(priceId);
                        BookCatalog bookCatalog = ico.getBookCatalog();
                        bookCatalog.setPriceId(priceId);
                        bookCatalogRepository.save(bookCatalog);
                        return ico;
                    } catch (StripeException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private String createInvoiceItem(InvoiceCreateObject ico, String customerId) {
        try {
            return stripeCheckoutService.createNewInvoiceItem(ico.getPriceId(), customerId);
        } catch (StripeException e) {
            throw new RuntimeException(e);
        }
    }
}
