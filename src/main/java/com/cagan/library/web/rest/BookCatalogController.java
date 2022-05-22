package com.cagan.library.web.rest;

import com.cagan.library.security.AuthoritiesConstants;
import com.cagan.library.service.BookCatalogQueryService;
import com.cagan.library.service.dto.request.BookCatalogRequest;
import com.cagan.library.service.BookCatalogService;
import com.cagan.library.web.errors.BadRequestAlertException;
import com.cagan.library.web.errors.FileNotExceptedException;
import com.cagan.library.repository.BookCatalogRepository;
import com.cagan.library.service.criteria.BookCatalogCriteria;
import com.cagan.library.service.dto.view.BookCatalogView;
import com.cagan.library.util.HeaderUtil;
import com.cagan.library.util.PaginationUtil;
import com.cagan.library.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/book-catalog")
public class BookCatalogController {
    private final BookCatalogService bookCatalogService;
    private final BookCatalogRepository bookCatalogRepository;

    private final BookCatalogQueryService bookCatalogQueryService;
    @Value("${spring.application.name}")
    private String applicationName;

    private static final String ENTITY_NAME = "BookCatalog";

    private final Logger logger = LoggerFactory.getLogger(BookCatalogController.class);

    @Autowired
    public BookCatalogController(BookCatalogService bookService, BookCatalogQueryService bookQueryService, BookCatalogRepository bookCatalogRepository) {
        this.bookCatalogService = bookService;
        this.bookCatalogQueryService = bookQueryService;
        this.bookCatalogRepository = bookCatalogRepository;
    }

    /**
     *
     * {@code GET  /book-catalog/upload} : upload book catalog with csv file.
     *
     * @param file the csv file to upload on the system which contains book list.
     * @return {@link ResponseEntity} with status {@code 200 (OK)} and with body the book, or with status {@code 404 (Not Found)}.
     * @throws IOException
     * @throws URISyntaxException
     */
    @PostMapping("/upload")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<List<BookCatalogView>> uploadBookCatalogs(@RequestParam("file") MultipartFile file) throws IOException, URISyntaxException {
        if (file.isEmpty()) {
            throw new FileNotExceptedException("File can not be empty.");
        }
        var result = bookCatalogService.bulkCreateBooks(file);

        return ResponseEntity
                .created(new URI("/api/v1/book-catalog/"))
                .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, String.valueOf(result.size())))
                .body(result);
    }

    /**
     * {@code POST  /books/:id} : get the "id" book.
     *
     * @param request the request to create book catalog
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new BookCatalogView, or with status {@code 400 (Bad Request) if the book catalog already exists.}
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping(consumes = {"multipart/form-data", "application/json"}, produces = {"application/json"})
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<BookCatalogView> createBookCatalog(@Valid @RequestBody BookCatalogRequest request) throws URISyntaxException {
        logger.debug("REST request to save Book Catalog: {}", request);
        bookCatalogRepository.findByAuthorAndTitle(request.getAuthor(), request.getTitle())
                .ifPresent((error) -> {
                    throw new BadRequestAlertException(
                            String.format("Book category already exists with title: %s, and author: %s", request.getTitle(), request.getAuthor()), ENTITY_NAME, "ALREADY_EXISTS");
                });

        BookCatalogView result = bookCatalogService.save(request);

        return ResponseEntity
                .created(new URI("api/v1/book-catalog/" + result.getId()))
                .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                .body(result);
    }

    /**
     * PUT /book-catalog/:id Updates an existing book catalog.
     *
     * @param id      the id of the request to save.
     * @param request the request to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bookCatalogView.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<BookCatalogView> updateBookCatalog(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody BookCatalogRequest request) {
        logger.debug("REST request to update Event: {}, {}", id, request);
        if (request.getId() == null) {
            throw new BadRequestAlertException("Invalid Id", ENTITY_NAME, "idnull");
        }

        if (!Objects.equals(id, request.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bookCatalogRepository.existsById(id)) {
            throw new BadRequestAlertException("Book catalog not found", ENTITY_NAME, "idnotfound");
        }

        BookCatalogView result = bookCatalogService.update(request);
        return ResponseEntity
                .ok()
                .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                .body(result);
    }

    /**
     * {@code PATCH  /book-catalog/:id} : Partial updates given fields of an existing book category, field will ignore if it is null
     *
     * @param id      the id of the bookCategory to save.
     * @param request the humanDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated bookCategoryView,
     * or with status {@code 400 (Bad Request)} if the bookCategory is not valid,
     * or with status {@code 404 (Not Found)} if the bookCategory is not found,
     * or with status {@code 500 (Internal Server Error)} if the bookCategory couldn't be updated.
     */
    @PatchMapping("/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<BookCatalogView> partialUpdateBookCategory(@PathVariable(value = "id", required = false) final Long id, @RequestBody BookCatalogRequest request) {
        logger.debug("REST request to partial update book category: {}, {}", id, request);
        if (request.getId() == null) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idnull");
        }

        if (!Objects.equals(request.getId(), id)) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!bookCatalogRepository.existsById(id)) {
            throw new BadRequestAlertException("Book catalog not found", ENTITY_NAME, "idnotfound");
        }

        Optional<BookCatalogView> result = bookCatalogService.partialUpdate(request);

        return ResponseUtil.wrapOrNotFound(
                result,
                HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, request.getId().toString())
        );
    }

    /**
     * {@code GET  /book-category/count} : count all the book categories.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public ResponseEntity<Long> countBookCatalogs(BookCatalogCriteria criteria) {
        logger.debug("REST request to count Book Catalog by criteria: {}", criteria);
        return ResponseEntity.ok().body(bookCatalogQueryService.countByCriteria(criteria));
    }

    /**
     * {@code GET  /book-catalog/:id} : get the "id" book catalog.
     *
     * @param id the id of the humanDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the bookCatalogView, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookCatalogView> getBookCatalog(@PathVariable("id") long id) {
        logger.debug("REST request to get Book Catalog : {}", id);
        Optional<BookCatalogView> bookCatalogView = bookCatalogService.findOne(id);
        return ResponseUtil.wrapOrNotFound(bookCatalogView);
    }

    // TODO: cagan - try sorting

    /**
     * {@code GET  /book-catalog} : get all the books.
     *
     * @param criteria the criteria which the requested entities should match.
     * @param pageable pageable object.
     * @return {@link ResponseEntity} with status {@code 200 (OK)} and the list of books in body.
     */
    @GetMapping
    public ResponseEntity<List<BookCatalogView>> getBookCatalogs(BookCatalogCriteria criteria, @PageableDefault(sort = {"id", "title"}, value = 20) Pageable pageable) {
        Page<BookCatalogView> page = bookCatalogQueryService.findByCriteria(criteria, pageable);

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code DELETE  /book-catalog/:id} : delete the "id" book catalog.
     *
     * @param id the id of the book catalog view to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<Void> deleteBookCatalog(@PathVariable("id") Long id) {
        logger.debug("REST request to delete book catalog: {} ", id);
        bookCatalogService.delete(id);
        return ResponseEntity
                .noContent()
                .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                .build();
    }
}
