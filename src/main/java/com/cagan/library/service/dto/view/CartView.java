package com.cagan.library.service.dto.view;

import lombok.Data;

@Data
public class CartView {
    private Long id;
    private BookCatalogView bookCatalog;
    private Integer quantity;
}
