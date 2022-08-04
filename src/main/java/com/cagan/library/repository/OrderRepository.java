package com.cagan.library.repository;

import com.cagan.library.domain.Order;
import com.cagan.library.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findFirstByUserAndOrderCompletedFalse(User user);

    Optional<Order> findByIdAndUser(Long id, User user);
}
