package com.cagan.library.web.interceptor;

import com.cagan.library.domain.User;
import com.cagan.library.repository.UserRepository;
import com.cagan.library.security.AuthUserObject;
import com.cagan.library.security.AuthoritiesConstants;
import com.cagan.library.security.SecurityUtils;
import com.cagan.library.web.errors.BadRequestAlertException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AuthUserResolver implements HandlerInterceptor {

    private final UserRepository userRepository;
    private static final Logger log = LoggerFactory.getLogger(AuthUserResolver.class);

    @Autowired
    public AuthUserResolver(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (SecurityUtils.hasCurrentUserHasThisAuthority(AuthoritiesConstants.ANONYMOUS)) {
            return true;
        }

        if (SecurityUtils.isAuthenticated()) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            log.info("Resolving Authenticated request: {}", authentication.getPrincipal());
            AuthUserObject.setUser(getAuthenticatedUser());
        }

        return true;
    }

    public User getAuthenticatedUser() {
        return SecurityUtils.getCurrentUserLogin()
                .flatMap(userRepository::findOneByLogin)
                .orElseThrow(() -> new BadRequestAlertException("Bad request", "User", "USER_NOT_FOUND_WITH_LOGIN"));
    }
}
