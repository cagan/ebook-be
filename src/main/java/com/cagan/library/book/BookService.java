package com.cagan.library.book;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookService {
    @Resource
    private final BookRepository bookRepository;
    private final BookViewMapper viewMapper;

    @Autowired
    public BookService(BookRepository bookRepository, BookViewMapper viewMapper) {
        this.bookRepository = bookRepository;
        this.viewMapper = viewMapper;
    }

    // TODO: Make upload book process concurrent
    public List<BookView> bulkCreateBooks(MultipartFile file) throws IOException {
        List<CSVBookDTO> list = CsvFileUtil.extractToList(
                CSVBookDTO.class,
                file.getInputStream(),
                new BookFileFilter()
        );

        Set<Book> books = list.stream()
                .filter(book -> bookRepository.findByAuthorAndTitle(book.getAuthor(), book.getTitle()).isEmpty())
                .map(bookDTO -> Book.builder()
                        .author(bookDTO.getAuthor())
                        .title(bookDTO.getTitle())
                        .genre(bookDTO.getGenre())
                        .height(bookDTO.getHeight())
                        .publisher(bookDTO.getPublisher())
                        .build()).collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(Book::getTitle))));

        bookRepository.saveAll(books);

        return viewMapper.toDto(new ArrayList<>(books));
    }

    public Specification<Book> heightGreaterThanOrEqual(int height) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.ge(root.get("height") ,height);
    }

    @Transactional(readOnly = true)
    public Page<BookView> findAll(int height, Pageable pageable) {
        return bookRepository.findAll(heightGreaterThanOrEqual(height), pageable).map(viewMapper::toDto);
//        return bookRepository.findAll(pageable).map(viewMapper::toDto);
    }
}
