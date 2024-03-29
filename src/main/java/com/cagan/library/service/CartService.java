package com.cagan.library.service;

import com.cagan.library.domain.BookCatalog;
import com.cagan.library.domain.Cart;
import com.cagan.library.domain.User;
import com.cagan.library.repository.CartRepository;
import com.cagan.library.service.dto.request.AddToCartRequest;
import com.cagan.library.service.dto.request.UpdateCartItemRequest;
import com.cagan.library.service.dto.view.CartItemView;
import com.cagan.library.service.dto.view.CartView;
import com.cagan.library.service.mapper.CartItemViewMapper;
import com.cagan.library.service.mapper.CartViewMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

@Service
@AllArgsConstructor
@Transactional
public class CartService {
    private final CartRepository cartRepository;
    private final CartViewMapper cartViewMapper;
    private final CartItemViewMapper cartItemViewMapper;

    public void addToCart(AddToCartRequest request, BookCatalog bookCatalog, User user) {
        cartRepository.findByBookCatalogIdAndUserId(bookCatalog.getId(), user.getId())
                .ifPresentOrElse(catalog -> {
                    int currentQuantity = catalog.getQuantity();
                    currentQuantity += request.getQuantity();
                    catalog.setQuantity(currentQuantity);
                    cartRepository.save(catalog);
                }, () -> {
                    Cart cart = Cart.builder()
                            .bookCatalog(bookCatalog)
                            .quantity(request.getQuantity())
                            .user(user)
                            .build();

                    cartRepository.save(cart);
                });
    }

    public List<CartView> listCartItems(User user) {
        List<Cart> cartList = cartRepository.findAllByUserOrderByCreatedDateDesc(user);
        return cartViewMapper.toDto(cartList);
    }

    public CartView getCartView(User user) {
        List<CartItemView> cartItems = cartRepository
                .findAllByUserOrderByCreatedDateDesc(user)
                .stream()
                .map((CartItemView::new)).toList();

        BigDecimal totalPrice = calculateTotalPrice(cartItems);

        return new CartView(cartItems, totalPrice);
    }

    public BigDecimal calculateTotalPrice(List<CartItemView> cartItems) {
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (CartItemView cartItem : cartItems) {
            totalPrice = totalPrice.add(
                    cartItem.getBookCatalog().getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()))
            );
        }
        return totalPrice;
    }

    public CartView updateCartItem(UpdateCartItemRequest request, Cart cart) {
        cart.setQuantity(request.getQuantity());
        cartRepository.save(cart);
        return cartViewMapper.toDto(cart);
    }

    public void deleteUserCartItems(User user) {
        cartRepository.deleteAllByUser(user);
    }

    public void deleteCartItem(Cart cart) {
        cartRepository.deleteById(cart.getId());
    }
}
