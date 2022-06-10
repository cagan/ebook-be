package com.cagan.library.web.rest;

import com.cagan.library.domain.User;
import com.cagan.library.repository.UserRepository;
import com.cagan.library.security.AuthUserObject;
import com.cagan.library.security.SecurityUtils;
import com.cagan.library.service.CartService;
import com.cagan.library.service.InvoiceService;
import com.cagan.library.service.dto.view.CartView;
import com.cagan.library.web.errors.BadRequestAlertException;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path = "/api/v1/invoice")
@AllArgsConstructor
@Api(tags = "Invoice")
public class InvoiceController {

    private final InvoiceService checkoutService;
    private final CartService cartService;
    private final UserRepository userRepository;

    @PreAuthorize("isAuthenticated()")
    @PostMapping
    public ResponseEntity<Map<String, Object>> createNewInvoice() {
//        User user = SecurityUtils.getCurrentUserLogin()
//                .flatMap(userRepository::findOneByLogin)
//                .orElseThrow(() -> new BadRequestAlertException("Bad request", "User", "USER_NOT_FOUND_WITH_LOGIN"));

        User user = AuthUserObject.getUser();

        CartView cartView = cartService.getCartView(user);

        Map<String, Object> response = new HashMap<>();
        String invoiceUrl = checkoutService.createNewInvoice(user, cartView);
        response.put("invoice_url", invoiceUrl);

        return ResponseEntity.ok().body(response);
    }
}
