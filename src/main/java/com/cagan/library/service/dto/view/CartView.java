package com.cagan.library.service.dto.view;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
public class CartView {
    private List<CartItemView> cartItems;
    private BigDecimal totalPrice;
}
