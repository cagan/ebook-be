package com.cagan.library.security;

import com.cagan.library.domain.User;
import com.cagan.library.repository.UserRepository;
import org.hibernate.validator.internal.constraintvalidators.bv.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;

@Component("userDetailsService")
public class DomainUserDetailService implements UserDetailsService {
    private final Logger log = LoggerFactory.getLogger(DomainUserDetailService.class);

    private final UserRepository userRepository;

    @Autowired
    public DomainUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        log.debug("Authenticating {}", login);

        if (new EmailValidator().isValid(login, null)) {
            return userRepository
                    .findOneWithAuthoritiesByEmailIgnoreCase(login)
                    .map(user -> createSpringSecurityUser(login, user))
                    .orElseThrow(() -> new UsernameNotFoundException("User with email " + login + " was not found in the database"));
        }

        String lowercaseLogin = login.toLowerCase(Locale.ENGLISH);
        return userRepository
                .findOneWithAuthoritiesByLoginIgnoreCase(login)
                .map(user -> createSpringSecurityUser(lowercaseLogin, user))
                .orElseThrow(() -> new UsernameNotFoundException("User " + lowercaseLogin + " was not found in the database"));
    }

    // returns UserDetails object
    private org.springframework.security.core.userdetails.User createSpringSecurityUser(String lowercaseLogin, User user) {
         if (!user.isActivated()) {
             throw new UserNotActivatedException("User " + lowercaseLogin + " was not activated");
         }

        List<SimpleGrantedAuthority> grantedAuthorities = user
                .getAuthorities()
                .stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getName()))
                .toList();

         return new org.springframework.security.core.userdetails.User(user.getLogin(), user.getPassword(), grantedAuthorities);
    }
}
