package com.cagan.library.service.dto.view;

import com.cagan.library.domain.BookCatalog;
import com.cagan.library.domain.Cart;
import lombok.Data;

@Data
public class CartItemView {
    private Long id;
    private BookCatalog bookCatalog;
    private Integer quantity;

    public CartItemView(Cart cart) {
        this.setBookCatalog(cart.getBookCatalog());
        this.setQuantity(cart.getQuantity());
    }
}
