package com.cagan.library.repository;

import com.cagan.library.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

//    Optional<Book> findByBookCatalogId(long bookCatalogId);

}
