package com.cagan.library.integration.stripe.service;

import com.cagan.library.domain.User;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.param.CustomerCreateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class StripeCustomerService {
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
}
