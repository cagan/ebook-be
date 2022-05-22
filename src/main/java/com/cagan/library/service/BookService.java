package com.cagan.library.service;

import com.amazonaws.services.s3.model.S3Object;
import com.cagan.library.domain.Book;
import com.cagan.library.domain.BookCatalog;
import com.cagan.library.domain.BookInSystem;
import com.cagan.library.integration.s3.BookObject;
import com.cagan.library.repository.BookInSystemRepository;
import com.cagan.library.repository.BookRepository;
import com.cagan.library.service.dto.request.BookItemRequest;
import com.cagan.library.integration.s3.ObjectLocator;
import com.cagan.library.integration.s3.ObjectLocatorUtils;
import com.cagan.library.integration.s3.S3ClientService;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.validation.constraints.NotNull;
import java.io.IOException;

@Service
@Transactional
public class BookService {
    @Value("${s3.bucket.name}")
    private String bucketName;
    private final S3ClientService s3ClientService;
    private final BookRepository bookRepository;
    private final BookInSystemRepository bookInSystemRepository;
    private static final Logger log = LoggerFactory.getLogger(BookService.class);

    @Autowired
    public BookService(S3ClientService s3ClientService, BookRepository bookRepository, BookInSystemRepository bookInSystemRepository) {
        this.s3ClientService = s3ClientService;
        this.bookRepository = bookRepository;
        this.bookInSystemRepository = bookInSystemRepository;
    }

    public void uploadBook(BookItemRequest bookItem, @NotNull BookCatalog bookCatalog) throws IOException {
        String fileExtension = FilenameUtils.getExtension(bookItem.getFile().getOriginalFilename());
        String locator = ObjectLocatorUtils.createLocator(bucketName, fileExtension);
        ObjectLocator objectLocator = ObjectLocatorUtils.getObjectLocator(locator);

        s3ClientService.putObject(objectLocator, bookItem.getFile().getInputStream(), bookItem.getFile().getSize());

        var book = new Book();
        book.setObjectLocator(locator);
        bookRepository.save(book);
        log.info("New book created to the table: [BOOK: {}]", book);
//        book.setBookCatalog(bookCatalog);

        var bookInSystem = new BookInSystem();
        bookInSystem.setIsAvailable(true);
        bookInSystem.setBookCatalog(bookCatalog);
        bookInSystem.setBook(book);
        log.info("Book In System created to the table: [BookInSystem: {}]", bookInSystem);
        bookInSystemRepository.save(bookInSystem);
    }

    public boolean isBookInSystem(long bookCatalogId) {
        return bookInSystemRepository.findByBookCatalogIdAndIsAvailable(bookCatalogId, true).isPresent();
    }

    // TODO: FORCE UPLOAD
    public void forceUploadBook(BookItemRequest bookItem) {
        // force upload
    }

    public BookObject downloadBook(String locator) {
        ObjectLocator objectLocator = ObjectLocatorUtils.getObjectLocator(locator);
        S3Object s3Object = s3ClientService.getObject(objectLocator);

        return new BookObject(s3Object.getObjectContent(), s3Object.getObjectMetadata().getContentLength());
    }

    public void deleteBook(String locator) {
        ObjectLocator objectLocator = ObjectLocatorUtils.getObjectLocator(locator);
        s3ClientService.deleteObject(objectLocator);
    }
}