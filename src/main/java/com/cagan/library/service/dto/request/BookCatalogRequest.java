package com.cagan.library.service.dto.request;

import com.cagan.library.domain.ProductCategory;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BookCatalogRequest implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @JsonProperty("id")
    private Long id;

    @JsonProperty("title")
    @NotBlank
    @Size(max = 50)
    private String title;

    @Column(name = "author", length = 50)
    @NotBlank
    @Size(max = 50)
    private String author;

    @NotBlank
    @Size(max = 100)
    private String genre;

    @NotNull
    @Positive
    @Min(0)
    private Integer height;

    @NotBlank
    @Size(max = 50)
    private String publisher;

    @JsonProperty("product_category")
    private ProductCategory productCategory;
}
