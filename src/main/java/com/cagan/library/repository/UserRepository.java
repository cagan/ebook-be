package com.cagan.library.repository;

import com.cagan.library.domain.User;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    String USERS_BY_EMAIL_CACHE = "usersByEmail";

    @EntityGraph(attributePaths = "authorities")
    @Cacheable(cacheNames = USERS_BY_EMAIL_CACHE)
    Optional<User> findOneWithAuthoritiesByEmailIgnoreCase(String email);


    @EntityGraph(attributePaths = "authorities")
    @Cacheable(cacheNames = USERS_BY_EMAIL_CACHE)
    Optional<User> findOneWithAuthoritiesByLoginIgnoreCase(String login);

    Optional<User> findOneByLogin(String login);

    Optional<User> findOneByEmail(String email); // TODO: check email projection

    Optional<User> findByActivationKey(String activationKey);


    Optional<User> findByEmailOrLogin(String email, String login);

    @Query("select count(u) from User u where u.email = :email")
    long countUserByEmail(String email);

    @Query("select count(u) from User u where u.login = :login")
    long countUserByLogin(String login);

    default boolean isUserExistsByEmail(String email) {
        return countUserByEmail(email.toLowerCase()) > 0;
    }

    default boolean isUserExistsByLogin(String login) {
        return countUserByLogin(login.toLowerCase()) > 0;
    }
}
