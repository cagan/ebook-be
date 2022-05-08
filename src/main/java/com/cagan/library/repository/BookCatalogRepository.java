package com.cagan.library.repository;

import com.cagan.library.domain.BookCatalog;
import org.jetbrains.annotations.NotNull;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BookCatalogRepository extends JpaRepository<BookCatalog, Long>, JpaSpecificationExecutor<BookCatalog> {
    Optional<BookCatalog> findByAuthorAndTitle(String author, String title);
    String BOOK_CATALOG_LIST_CACHE = "bookCatalogs";

    @Cacheable(cacheNames = BOOK_CATALOG_LIST_CACHE)
    @NotNull
    List<BookCatalog> findAll(Specification<BookCatalog> spec);


    // TODO: Fix the Filter with cache issue. Maybe KeyGenerator helps
    @Cacheable(cacheNames = BOOK_CATALOG_LIST_CACHE)
    @Override
    @NotNull
    Page<BookCatalog> findAll(Specification<BookCatalog> spec, @NotNull Pageable pageable);
}