package com.cagan.library.service.dto.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class CartUpdateRequest {

    private Long id;

    @NotNull
    @JsonProperty("book_catalog_id")
    private Long bookCatalogId;

    @NotNull
    @Min(0)
    @JsonProperty("quantity")
    private Integer quantity;
}
