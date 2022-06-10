package com.cagan.library.service.dto;

import com.cagan.library.integration.stripe.CheckoutItem;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CheckoutItemDto implements CheckoutItem {
    @JsonProperty("product_name")
    private String productName;

    @JsonProperty("quantity")
    private int quantity;

    @JsonProperty("price")
    private double price;

    @JsonProperty("product_id")
    private long productId;

    @JsonProperty("user_id")
    private int userId;
}
