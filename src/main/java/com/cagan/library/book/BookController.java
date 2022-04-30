package com.cagan.library.book;

import com.cagan.library.common.errors.FileNotExceptedException;
import com.cagan.library.util.HeaderUtil;
import com.cagan.library.util.PaginationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/books")
public class BookController {
    private final BookService bookService;

    private final BookQueryService bookQueryService;
    @Value("${spring.application.name}")
    private String applicationName;

    private static final String ENTITY_NAME = "books";

    @Autowired
    public BookController(BookService bookService, BookQueryService bookQueryService) {
        this.bookService = bookService;
        this.bookQueryService = bookQueryService;
    }

    // TODO: cagan - Only admin can add books
    // TODO: cagan - Append added book count to the header

    /**
     * {@code GET  /books/:id} : get the "id" book.
     * @param file the csv file to upload on the system which contains book list.
     * @return {@link ResponseEntity} with status {@code 200 (OK)} and with body the book, or with status {@code 404 (Not Found)}.
     * @throws IOException
     * @throws URISyntaxException
     */
    @PostMapping(value = "/upload")
    public ResponseEntity<List<BookView>> uploadBooks(@RequestParam("file")MultipartFile file) throws IOException, URISyntaxException {
        if (file.isEmpty()) {
            throw new FileNotExceptedException("File can not be empty.");
        }
        var result = bookService.bulkCreateBooks(file);

        return ResponseEntity
                .created(new URI("/api/v1/books/"))
                .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, String.valueOf(result.size())))
                .body(result);
    }

    // TODO: cagan - try sorting
    /**
     * {@code GET  /books/search} : get all the books.
     * @param criteria the criteria which the requested entities should match.
     * @param pageable pageable object.
     * @return {@link ResponseEntity} with status {@code 200 (OK)} and the list of books in body.
     */
    @GetMapping("/search")
    public ResponseEntity<List<BookView>> searchBooks(BookCriteria criteria, Pageable pageable) {
        Page<BookView> page = bookQueryService.findByCriteria(criteria, pageable);

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }
}
