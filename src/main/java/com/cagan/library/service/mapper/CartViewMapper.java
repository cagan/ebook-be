package com.cagan.library.service.mapper;

import com.cagan.library.domain.Cart;
import com.cagan.library.service.dto.view.CartItemView;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CartViewMapper extends EntityMapper <CartItemView, Cart>{ }
