package com.cagan.library.publisher;

import com.cagan.library.domain.User;
import com.cagan.library.event.AccountRegisteredEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class AccountPublisher {
    private final ApplicationEventPublisher publisher;
    private static final Logger log = LoggerFactory.getLogger(AccountPublisher.class);

    @Autowired
    public AccountPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void publishAccountRegistered(final User user) {
        log.info("Publishing AccountRegistered event: [USER: {}]", user);
        publisher.publishEvent(new AccountRegisteredEvent(this, user));
    }
}
