package com.cagan.library.service.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class DownloadBookRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull
    @JsonProperty("book_catalog_id")
    private Long bookCatalogId;
}
