package com.cagan.library.service.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
public class BookItemRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    @NotNull(message = "file is required")
    private MultipartFile file;

    @NotNull
    @JsonProperty("book_catalog_id")
    private Long bookCatalogId;

    @JsonProperty("force_to_upload")
    private boolean forceToUpload;

    @Override
    public String toString() {
        return "BookItemRequest{" +
                "file=" + file +
                ", bookCatalogId=" + bookCatalogId +
                ", forceToUpload=" + forceToUpload +
                '}';
    }
}
