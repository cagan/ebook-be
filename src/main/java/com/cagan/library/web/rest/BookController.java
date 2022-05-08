package com.cagan.library.web.rest;

import com.cagan.library.config.ApplicationDefaults;
import com.cagan.library.config.EBookProperties;
import com.cagan.library.domain.BookCatalog;
import com.cagan.library.domain.BookInSystem;
import com.cagan.library.repository.BookCatalogRepository;
import com.cagan.library.repository.BookInSystemRepository;
import com.cagan.library.security.SecurityUtils;
import com.cagan.library.service.BookCatalogService;
import com.cagan.library.service.dto.request.BookItemRequest;
import com.cagan.library.security.AuthoritiesConstants;
import com.cagan.library.service.BookService;
import com.cagan.library.service.dto.request.DownloadBookRequest;
import com.cagan.library.service.dto.view.BookCatalogView;
import com.cagan.library.web.errors.BadRequestAlertException;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper; import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/book")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;
    private final BookInSystemRepository bookInSystemRepository;
    private final BookCatalogRepository bookCatalogRepository;
    private final BookCatalogService bookCatalogService;
    private static final Logger log = LoggerFactory.getLogger(BookController.class);

    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> uploadBook(@Valid @ModelAttribute() BookItemRequest bookItem) throws IOException {
        log.info("[BookItem: {}]", bookItem);
        if (bookItem.getFile().isEmpty()) {
            throw new BadRequestAlertException("File size can not be empty", "Book", "NOT_VALID");
        }
        Optional<BookCatalog> bookCatalog = bookCatalogRepository.findById(bookItem.getBookCatalogId());
        if (bookCatalog.isEmpty()) {
            throw new BadRequestAlertException("No Book Catalog found", "BookCatalog", "BOOK_CATALOG_NOT_FOUND");
        }
        // override current book over existing one.
        boolean bookExists = bookInSystemRepository.findByBookCatalogIdAndIsAvailable(bookItem.getBookCatalogId(), true).isPresent();
        if (bookExists) {
            if (bookItem.isForceToUpload()) {
                // TODO: override current book over existing one.
       //         forceUploadBook();
            } else {
                throw new BadRequestAlertException("Book already uploaded", "BOOK", "BOOK_ALREAD_UPLOADED");
            }
        }

        bookService.uploadBook(bookItem, bookCatalog.get());
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/download")
    public ResponseEntity<Void> downloadBook(@Valid @RequestBody DownloadBookRequest request) {
        // TODO: check if user own this book.
        Optional<BookInSystem> bookInSystem = bookInSystemRepository.findByBookCatalogIdAndIsAvailable(request.getBookCatalogId(), true);

        if (bookInSystem.isEmpty()) {
            throw new BadRequestAlertException("Book is not in the system", "BookInSystem", "BOOK_NOT_FOUND_IN_SYSTEM");
        }
        // TODO: set user id inside details.
        Authentication authentication = SecurityUtils.getCurrentUser();

        return null;
    }

    @GetMapping("/is-system/{book_catalog_id}")
    public Map<String, Boolean> isBookInTheSystem(@PathVariable("book_catalog_id") long bookCatalogId) {
        Map<String, Boolean> responseMap = new HashMap<>();
        boolean exists = bookService.isBookInSystem(bookCatalogId);
        responseMap.put("in_the_system", exists);
        return responseMap;
    }
}
