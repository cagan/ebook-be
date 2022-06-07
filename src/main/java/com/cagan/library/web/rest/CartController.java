package com.cagan.library.web.rest;

import com.cagan.library.domain.BookCatalog;
import com.cagan.library.domain.Cart;
import com.cagan.library.domain.User;
import com.cagan.library.repository.BookCatalogRepository;
import com.cagan.library.repository.CartRepository;
import com.cagan.library.repository.UserRepository;
import com.cagan.library.security.AuthUserObject;
import com.cagan.library.security.AuthoritiesConstants;
import com.cagan.library.security.SecurityUtils;
import com.cagan.library.service.BookService;
import com.cagan.library.service.CartService;
import com.cagan.library.service.dto.request.AddToCartRequest;
import com.cagan.library.service.dto.request.UpdateCartItemRequest;
import com.cagan.library.service.dto.view.CartView;
import com.cagan.library.service.dto.view.MessageResponse;
import com.cagan.library.web.errors.BadRequestAlertException;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

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
    public ResponseEntity<MessageResponse> addToCart(@RequestBody @Valid AddToCartRequest request) {
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
        return ResponseEntity.ok(cartService.getCartView(AuthUserObject.getUser()));
    }

    @PutMapping("/update/{cartItemId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CartView> updateCartItem(@PathVariable("cartItemId") Long cartItemId, @Valid @RequestBody UpdateCartItemRequest request) {
       Cart cart = cartRepository.findByIdAndUser(cartItemId, AuthUserObject.getUser())
               .orElseThrow(() -> new BadRequestAlertException("Bad request", "Cart", "CART_ITEM_NOT_FOUND"));

       if (request.getQuantity().equals(0)) {
           cartService.deleteCartItem(cart);
           return ResponseEntity.ok().build();
       }

        return ResponseEntity.ok(cartService.updateCartItem(request, cart));
    }

    @DeleteMapping("/delete/{cartItemId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteCartItem(@PathVariable("cartItemId") Long cartItemId) {
        Cart cart = cartRepository.findByIdAndUser(cartItemId, AuthUserObject.getUser())
                .orElseThrow(() -> new BadRequestAlertException("Bad request", "Cart", "CART_ITEM_NOT_FOUND"));

        cartService.deleteCartItem(cart);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/delete")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> emptyCart() {
        cartService.deleteUserCartItems(AuthUserObject.getUser());
        return ResponseEntity.ok().build();
    }
}
