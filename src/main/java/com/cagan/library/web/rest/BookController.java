package com.cagan.library.web.rest;

import com.cagan.library.domain.BookCatalog;
import com.cagan.library.domain.BookInSystem;
import com.cagan.library.domain.User;
import com.cagan.library.integration.s3.BookObject;
import com.cagan.library.repository.BookCatalogRepository;
import com.cagan.library.repository.BookInSystemRepository;
import com.cagan.library.repository.BookRepository;
import com.cagan.library.repository.UserRepository;
import com.cagan.library.security.SecurityUtils;
import com.cagan.library.service.dto.request.BookItemRequest;
import com.cagan.library.security.AuthoritiesConstants;
import com.cagan.library.service.BookService;
import com.cagan.library.service.dto.request.DownloadBookRequest;
import com.cagan.library.util.HeaderUtil;
import com.cagan.library.web.errors.BadRequestAlertException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("/api/v1/book")
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;
    private final BookInSystemRepository bookInSystemRepository;
    private final BookCatalogRepository bookCatalogRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private static final Logger log = LoggerFactory.getLogger(BookController.class);

    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> uploadBook(@Valid @ModelAttribute() BookItemRequest bookItem) throws IOException {
        log.info("[BookItem: {}]", bookItem); if (bookItem.getFile().isEmpty()) {
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
    @PostMapping(value = "/download", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Resource> downloadBook(@Valid @RequestBody DownloadBookRequest request) {
        Optional<BookInSystem> bookInSystem = bookInSystemRepository.getAvailableBooks(request.getBookCatalogId());

        if (bookInSystem.isEmpty()) {
            throw new BadRequestAlertException("Book is not in the system", "BookInSystem", "BOOK_NOT_FOUND_IN_SYSTEM");
        }
        User user = SecurityUtils.getCurrentUserLogin()
                .flatMap(userRepository::findOneByLogin)
                .orElseThrow(() -> new BadRequestAlertException("Bad request", "User", "USER_NOT_FOUND_WITH_LOGIN"));

        // TODO: check if user own this book or is user admin.
        if (!user.getBooks().contains(bookInSystem.get().getBook()) && SecurityUtils.hasCurrentUserNoneOfAuthorities("ROLE_ADMIN")) {
            throw new BadRequestAlertException("You can't download book that you don't own", "Book", "PERMISSION");
        }

        // TODO: Test the download user cases. Non admin and not owner.
        BookObject bookObject = bookService.downloadBook(bookInSystem.get().getBook().getObjectLocator());
        InputStreamResource isr = new InputStreamResource(bookObject.getInputStream());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/pdf");
        httpHeaders.add("Content-Disposition", "attachment;filename=\"" + UUID.randomUUID() + ".pdf\"");

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .headers(httpHeaders)
                .body(isr);
    }

    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    @DeleteMapping("/delete/{book_catalog_id}")
    public ResponseEntity<Void> deleteBook(@PathVariable("book_catalog_id") Long bookCatalogId) {
        BookInSystem bookInSystem = bookInSystemRepository.getAvailableBooks(bookCatalogId)
                .orElseThrow(() -> new BadRequestAlertException("Book is not in the system", "BookInSystem", "BOOK_NOT_FOUND_IN_SYSTEM"));

        // TODO: Handle users books (You should not delete books that users have)
        bookService.deleteBook(bookInSystem.getBook().getObjectLocator());
        bookInSystem.setIsAvailable(false);
        bookInSystemRepository.save(bookInSystem);


        return ResponseEntity
                .ok()
                .headers(HeaderUtil.createEntityDeletionAlert(
                        "ebook-be",
                        true,
                        "Book",
                        String.valueOf(bookCatalogId)))
                .build();
    }

    @GetMapping("/is-system/{book_catalog_id}")
    public Map<String, Boolean> isBookInTheSystem(@PathVariable("book_catalog_id") long bookCatalogId) {
        Map<String, Boolean> responseMap = new HashMap<>();
        boolean exists = bookService.isBookInSystem(bookCatalogId);
        responseMap.put("in_the_system", exists);
        return responseMap;
    }
}
