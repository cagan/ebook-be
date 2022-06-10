package com.cagan.library.integration.stripe.service;

import com.cagan.library.integration.stripe.service.StripeInvoiceService;
import com.cagan.library.service.dto.view.CartItemView;
import com.stripe.exception.StripeException;
import com.stripe.model.Product;
import com.stripe.param.ProductCreateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class StripeProductService {
    private static final Logger log = LoggerFactory.getLogger(StripeInvoiceService.class);

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
}
