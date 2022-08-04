package com.cagan.library.service.dto.request;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class UpdateCartItemRequest {

    @NotNull
    @Min(0)
    @Max(10)
    private Integer quantity;
}
