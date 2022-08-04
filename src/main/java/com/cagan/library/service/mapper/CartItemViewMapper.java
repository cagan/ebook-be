package com.cagan.library.service.mapper;

import com.cagan.library.domain.Cart;
import com.cagan.library.service.dto.view.CartItemView;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface CartItemViewMapper extends EntityMapper<CartItemView, Cart> {

    @BeforeMapping
    default void beforeUpdate(CartItemView dto, @MappingTarget Cart cart) {
        dto = new CartItemView(cart);
        dto.setQuantity(cart.getQuantity());
        dto.setCreatedDate(cart.getCreatedDate());
        dto.setBookCatalog(cart.getBookCatalog());
        dto.setId(cart.getId());
    }
}
