package com.cagan.library.bookcatalog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface BookCatalogRepository extends JpaRepository<BookCatalog, Long>, JpaSpecificationExecutor<BookCatalog> {
    Optional<BookCatalog> findByAuthorAndTitle(String author, String title);
}