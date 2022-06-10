package com.cagan.library.repository;

import com.cagan.library.domain.Cart;
import com.cagan.library.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    List<Cart> findAllByUserOrderByCreatedDateDesc(User user);
    Optional<Cart> findByBookCatalogId(Long bookCatalogId);
    Optional<Cart> findByBookCatalogIdAndUserId(Long bookCatalogId, Long userId);
    Optional<Cart> findByIdAndUser(Long cartItemId, User user);
    void deleteAllByUser(User user);
}
