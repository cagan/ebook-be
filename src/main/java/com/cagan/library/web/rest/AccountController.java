package com.cagan.library.web.rest;

import com.cagan.library.domain.User;
import com.cagan.library.publisher.AccountPublisher;
import com.cagan.library.service.UserService;
import com.cagan.library.web.errors.InvalidPasswordException;
import com.cagan.library.service.dto.vm.ManagedUserVM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/user")
public class AccountController {
    private static class AccountControllerException extends RuntimeException {
        private AccountControllerException(String message) {
            super(message);
        }
    }

    private final Logger logger = LoggerFactory.getLogger(AccountController.class);
    private final UserService userService;
    private final AccountPublisher accountPublisher;

    @Autowired
    public AccountController(UserService userService, AccountPublisher accountPublisher) {
        this.userService = userService;
        this.accountPublisher = accountPublisher;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Void> registerAccount(@Valid @RequestBody ManagedUserVM managedUserVM) {
        if (isPasswordLengthInvalid(managedUserVM.getPassword())) {
            throw new InvalidPasswordException();
        }

        User user = userService.registerUser(managedUserVM, managedUserVM.getPassword());
        // mailService.sendActivationMail(user);
        accountPublisher.publishAccountRegistered(user);
        return ResponseEntity.created(URI.create("/home")).build();
    }

    @GetMapping("/activate")
    public ResponseEntity<Void> activateAccount(@RequestParam(value = "key") String key) {
        Optional<User> user = userService.activateRegistration(key);
        if (user.isEmpty()) {
            throw new AccountControllerException("No user was found for this activation key");
        }
        return ResponseEntity.ok().build();
    }

    private boolean isPasswordLengthInvalid(String password) {
        return !StringUtils.hasText(password) && password.length() >= ManagedUserVM.PASSWORD_MIN_LENGTH &&
                password.length() <= ManagedUserVM.PASSWORD_MAX_LENGTH;
    }
}
