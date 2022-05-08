package com.cagan.library.repository;

import com.cagan.library.domain.BookInSystem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BookInSystemRepository extends JpaRepository<BookInSystem, Long> {

    Optional<BookInSystem> findByBookCatalogIdAndIsAvailable(long bookCatalogId, boolean isAvailable);
}

