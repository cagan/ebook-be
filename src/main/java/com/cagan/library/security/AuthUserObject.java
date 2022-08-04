package com.cagan.library.security;

import com.cagan.library.domain.User;
import lombok.*;

@Getter
@Setter
@ToString
public class AuthUserObject {
    public static String email;
    public static User user;

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        AuthUserObject.user = user;
    }
}
