package com.cagan.library.bookcatalog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookCatalogService {
    @Resource
    private final BookCatalogRepository bookCatalogRepository;
    private final BookCatalogViewMapper viewMapper;
    private final BookCatalogRequestMapper requestMapper;

    private final Logger logger = LoggerFactory.getLogger(BookCatalogService.class);

    @Autowired
    public BookCatalogService(BookCatalogRepository bookRepository, BookCatalogViewMapper viewMapper, BookCatalogRequestMapper requestMapper) {
        this.bookCatalogRepository = bookRepository;
        this.viewMapper = viewMapper;
        this.requestMapper = requestMapper;
    }

    // TODO: Make upload book process concurrent
    public List<BookCatalogView> bulkCreateBooks(MultipartFile file) throws IOException {
        List<CSVBookCatalogDTO> list = CsvFileUtil.extractToList(
                CSVBookCatalogDTO.class,
                file.getInputStream(),
                new BookCatalogFileFilter()
        );

        Set<BookCatalog> books = list.stream()
                .filter(book -> bookCatalogRepository.findByAuthorAndTitle(book.getAuthor(), book.getTitle()).isEmpty())
                .map(bookDTO -> BookCatalog.builder()
                        .author(bookDTO.getAuthor())
                        .title(bookDTO.getTitle())
                        .genre(bookDTO.getGenre())
                        .height(bookDTO.getHeight())
                        .publisher(bookDTO.getPublisher())
                        .build()).collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(BookCatalog::getTitle))));

        bookCatalogRepository.saveAll(books);

        return viewMapper.toDto(new ArrayList<>(books));
    }

    public BookCatalogView save(BookCatalogRequest request) {
        BookCatalog bookCatalog = requestMapper.toEntity(request);
        bookCatalog = bookCatalogRepository.save(bookCatalog);
        return viewMapper.toDto(bookCatalog);
    }

    public BookCatalogView update(BookCatalogRequest request) {
        logger.debug("Request to save Entity: {} ", request);
        BookCatalog bookCatalog = requestMapper.toEntity(request);
        bookCatalog = bookCatalogRepository.save(bookCatalog);
        return viewMapper.toDto(bookCatalog);
    }

    public Optional<BookCatalogView> findOne(long id) {
        logger.debug("Request to get Event: {}", id);
        return bookCatalogRepository.findById(id).map(viewMapper::toDto);
    }

    public void delete(Long id) {
        logger.debug("Request to delete book catalog: {}", id);
        bookCatalogRepository.deleteById(id);
    }

    public Optional<BookCatalogView> partialUpdate(BookCatalogRequest request) {
        logger.debug("Request to partially update bvook catalog: {}", request);

        return bookCatalogRepository.findById(request.getId())
                .map(existingCatalog -> {
                    requestMapper.partialUpdate(existingCatalog, request);
                    return existingCatalog;
                })
                .map(bookCatalogRepository::save)
                .map(viewMapper::toDto);
    }
}
