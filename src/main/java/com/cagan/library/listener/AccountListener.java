package com.cagan.library.listener;

import com.cagan.library.event.AccountRegisteredEvent;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class AccountListener {

    @Async
    @EventListener(AccountRegisteredEvent.class)
    public void handleAccountRegisteredEvent(AccountRegisteredEvent accountRegistered) throws InterruptedException {
        Thread.sleep(7000);
        System.out.println("Account name: " + accountRegistered.getUser().getFirstName());
        System.out.println("Account email: " + accountRegistered.getUser().getEmail());
        System.out.println("Account password: " + accountRegistered.getUser().getPassword());
        System.out.println("Sending email to the account.");
    }

    @EventListener(ApplicationStartedEvent.class)
    public void handleApplicationStartedEvent(ApplicationStartedEvent event) {
        System.out.println("Application has been started...");
        System.out.println(event.getApplicationContext());
    }
}
