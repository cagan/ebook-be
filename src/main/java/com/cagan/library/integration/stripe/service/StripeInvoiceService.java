package com.cagan.library.integration.stripe.service;

import com.stripe.exception.StripeException;
import com.stripe.model.*;
import com.stripe.param.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class StripeInvoiceService {
    private static final Logger log = LoggerFactory.getLogger(StripeInvoiceService.class);

    public String createNewInvoiceItem(String priceId, String customerId) throws StripeException {
        InvoiceItemCreateParams invoiceItemCreateParams = InvoiceItemCreateParams.builder()
                .setCustomer(customerId)
                .setPrice(priceId)
                .build();

        InvoiceItem invoiceItem = InvoiceItem.create(invoiceItemCreateParams);
        log.info("Created [INVOICE ITEM: {}]", invoiceItem.getId());
        return invoiceItem.getId();
    }

    public Invoice createInvoice(String customerId) {
        InvoiceCreateParams invoiceCreateParams = InvoiceCreateParams.builder()
                .setCustomer(customerId)
                .setCollectionMethod(InvoiceCreateParams.CollectionMethod.SEND_INVOICE)
                .setDaysUntilDue(1L)
                .build();

        try {
            Invoice invoice = Invoice.create(invoiceCreateParams);
            log.info("Created [Invoice: {}]", invoice.getId());
            return invoice;
        } catch (StripeException exception) {
            log.error("Can not create invoice");
            throw new RuntimeException(exception);
        }
    }

    public String sendInvoice(Invoice invoice) {
        invoice.getId();
        try {
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
