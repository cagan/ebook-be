package com.cagan.library.event;

import com.cagan.library.domain.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class AccountRegisteredEvent extends ApplicationEvent {
    private final User user;

    public AccountRegisteredEvent(Object source, User user) {
        super(source);
        this.user = user;
    }
}
