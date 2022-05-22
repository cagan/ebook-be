package com.cagan.library.web.rest;

import com.cagan.library.domain.BookCatalog;
import com.cagan.library.domain.Cart;
import com.cagan.library.domain.User;
import com.cagan.library.repository.BookCatalogRepository;
import com.cagan.library.repository.CartRepository;
import com.cagan.library.repository.UserRepository;
import com.cagan.library.security.SecurityUtils;
import com.cagan.library.service.BookService;
import com.cagan.library.service.CartService;
import com.cagan.library.service.dto.request.CartUpdateRequest;
import com.cagan.library.service.dto.view.CartItemView;
import com.cagan.library.service.dto.view.CartView;
import com.cagan.library.service.dto.view.MessageResponse;
import com.cagan.library.web.errors.BadRequestAlertException;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/cart")
@AllArgsConstructor
public class CartController {
    private final UserRepository userRepository;
    private final BookCatalogRepository bookCatalogRepository;
    private final CartService cartService;
    private final CartRepository cartRepository;
    private BookService bookService;

    @PostMapping("/add")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MessageResponse> addToCart(@RequestBody @Valid CartUpdateRequest request) {
        User user = SecurityUtils.getCurrentUserLogin()
                .flatMap(userRepository::findOneByLogin)
                .orElseThrow(() -> new BadRequestAlertException("Bad request", "User", "USER_NOT_FOUND_WITH_LOGIN"));

        BookCatalog bookCatalog = bookCatalogRepository.findById(request.getBookCatalogId())
                .orElseThrow(() -> new BadRequestAlertException("Bad request", "BookCatalog", "BOOK_CATALOG_NOT_FOUND"));

        if (!bookService.isBookInSystem(bookCatalog.getId())) {
            throw new BadRequestAlertException("Bad request", "Book", "BOOK_IS_NOT_IN_THE_SYSTEM");
        }

        cartService.addToCart(request, bookCatalog, user);

        return ResponseEntity.ok(new MessageResponse("Products added to the cart"));
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CartView> getCartItems() {
        User user = SecurityUtils.getCurrentUserLogin()
                .flatMap(userRepository::findOneByLogin)
                .orElseThrow(() -> new BadRequestAlertException("Bad request", "User", "USER_NOT_FOUND_WITH_LOGIN"));

        return ResponseEntity.ok(cartService.getCartView(user));
    }

    @PutMapping("/update/{cartItemId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateCartItem(@PathVariable("cartItemId") Long cartItemId, @Valid @RequestBody CartUpdateRequest request) {
        User user = SecurityUtils.getCurrentUserLogin()
                .flatMap(userRepository::findOneByLogin)
                .orElseThrow(() -> new BadRequestAlertException("Bad request", "User", "USER_NOT_FOUND_WITH_LOGIN"));

       Cart cart = cartRepository.findByIdAndUser(cartItemId, user)
               .orElseThrow(() -> new BadRequestAlertException("Bad request", "Cart", "CART_ITEM_NOT_FOUND"));

       if (request.getQuantity().equals(0)) {
           cartService.deleteCartItem(cart);
           return ResponseEntity.ok().build();
       }

        cartService.updateCartItem(request, cart);
        return ResponseEntity.ok(cartService.updateCartItem(request, cart));
    }

    @DeleteMapping("/delete/{cartItemId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteCartItem(@PathVariable("cartItemId") Long cartItemId) {
        User user = SecurityUtils.getCurrentUserLogin()
                .flatMap(userRepository::findOneByLogin)
                .orElseThrow(() -> new BadRequestAlertException("Bad request", "User", "USER_NOT_FOUND_WITH_LOGIN"));

        Cart cart = cartRepository.findByIdAndUser(cartItemId, user)
                .orElseThrow(() -> new BadRequestAlertException("Bad request", "Cart", "CART_ITEM_NOT_FOUND"));

        cartService.deleteCartItem(cart);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Void> emptyCart() {
        User user = SecurityUtils.getCurrentUserLogin()
                .flatMap(userRepository::findOneByLogin)
                .orElseThrow(() -> new BadRequestAlertException("Bad request", "User", "USER_NOT_FOUND_WITH_LOGIN"));

        cartService.deleteUserCartItems(user);

        return ResponseEntity.ok().build();
    }
}
