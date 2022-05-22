package com.cagan.library.service.dto.view;

import com.cagan.library.domain.BookCatalog;
import com.cagan.library.domain.BookInSystem;
import com.cagan.library.domain.Cart;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class CartItemView {
    private long id;
    @JsonIgnore
    private BookCatalog bookCatalog;
    private Integer quantity;
    private Cart cart;

//    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "Asia/Kolkata")
    private Instant createdDate;

    public CartItemView(Cart cart) {
        this.setBookCatalog(cart.getBookCatalog());
        this.setQuantity(cart.getQuantity());
        this.setId(cart.getId());
        this.setCreatedDate(cart.getCreatedDate());
        this.cart = cart;
    }
}
