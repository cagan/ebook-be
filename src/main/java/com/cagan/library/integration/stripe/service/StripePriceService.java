package com.cagan.library.integration.stripe.service;

import com.cagan.library.integration.stripe.service.StripeInvoiceService;
import com.stripe.exception.StripeException;
import com.stripe.model.Price;
import com.stripe.param.PriceCreateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class StripePriceService {
    private static final Logger log = LoggerFactory.getLogger(StripeInvoiceService.class);

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
}
