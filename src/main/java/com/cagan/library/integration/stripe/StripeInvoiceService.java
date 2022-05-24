package com.cagan.library.integration.stripe;

import com.cagan.library.domain.User;
import com.cagan.library.service.dto.view.CartItemView;
import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.param.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class StripeInvoiceService {
    private static final Logger log = LoggerFactory.getLogger(StripeInvoiceService.class);

    public String createNewCustomer(User user) throws StripeException {
        StringBuilder firstNameLastName = new StringBuilder();

        if (user.getFirstName() != null) {
            firstNameLastName.append(user.getFirstName());
        }

        if (user.getLastName() != null) {
            firstNameLastName.append(user.getLastName());
        }

        CustomerCreateParams customerCreateParams = CustomerCreateParams
                .builder()
                .setName(firstNameLastName.toString())
                .setEmail(user.getEmail())
                .setDescription(firstNameLastName + " account")
                .build();

        Customer customer = Customer.create(customerCreateParams);

        log.info("Created [Customer: {}]", customer.getId());

        return customer.getId();
    }

    public String createNewProduct(CartItemView cartItemView) throws StripeException {
        ProductCreateParams productCreateParams = ProductCreateParams
                .builder()
                .setName(cartItemView.getBookCatalog().getTitle())
                .setType(ProductCreateParams.Type.GOOD)
                .setActive(true)
                .build();

        Product product = Product.create(productCreateParams);

        log.info("Created [Product: {}]", product.getId());
        return product.getId();
    }

    public String createNewPrice(long unitAmount, String productId) throws StripeException {
        PriceCreateParams priceCreateParams = PriceCreateParams
                .builder()
                .setCurrency("try")
                .setUnitAmount(unitAmount)
                .setProduct(productId)
                .build();

        Price price = Price.create(priceCreateParams);
        log.info("Created [Price: {}]", price.getId());
        return price.getId();
    }

    public String createNewInvoiceItem(String priceId, String customerId) throws StripeException {
        InvoiceItemCreateParams invoiceItemCreateParams = InvoiceItemCreateParams.builder()
                .setCustomer(customerId)
                .setPrice(priceId)
                .build();

        InvoiceItem invoiceItem = InvoiceItem.create(invoiceItemCreateParams);
        log.info("Created [INVOICE ITEM: {}]", invoiceItem.getId());
        return invoiceItem.getId();
    }

    public String sendInvoice(String customerId) {
        InvoiceCreateParams invoiceCreateParams = InvoiceCreateParams.builder()
                .setCustomer(customerId)
                .setCollectionMethod(InvoiceCreateParams.CollectionMethod.SEND_INVOICE)
                .setDaysUntilDue(1L)
                .build();

        try {
            Invoice invoice = Invoice.create(invoiceCreateParams);
            log.info("Created [Invoice: {}]", invoice.getId());
            InvoiceSendInvoiceParams invoiceSendInvoiceParams = InvoiceSendInvoiceParams.builder().build();

            Invoice sentInvoice = invoice.sendInvoice(invoiceSendInvoiceParams);
            log.info("Invoice [Invoice: {}] has been sent", invoice.getId());
            return sentInvoice.getHostedInvoiceUrl();
        } catch (StripeException exception) {
            log.error("Can not create invoice");
            throw new RuntimeException(exception);
        }
    }
}
