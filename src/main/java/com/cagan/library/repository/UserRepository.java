package com.cagan.library.repository;

import com.cagan.library.domain.User;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    String USERS_BY_EMAIL_CACHE = "usersByEmail";
    String USERS_BY_LOGIN_CACHE = "usersByLogin";

    @EntityGraph(attributePaths = "authorities")
    @Cacheable(cacheNames = USERS_BY_EMAIL_CACHE)
    Optional<User> findOneWithAuthoritiesByEmailIgnoreCase(String email);


    @EntityGraph(attributePaths = "authorities")
    @Cacheable(cacheNames = USERS_BY_EMAIL_CACHE)
    Optional<User> findOneWithAuthoritiesByLoginIgnoreCase(String login);

    Optional<User> findOneByLogin(String login);

    Optional<User> findOneByEmail(String email); // TODO: check email projection

    Optional<User> findByActivationKey(String activationKey);


    List<User> findAllByActivatedIsFalseAndActivationKeyIsNotNullAndCreatedDateBefore(Instant dateTime);

    @Query("select u from User u where u.activated = false and u.activationKey is null and u.createdDate < :instant")
    List<User> findAllNonActivatedUsers(Instant instant);

    Optional<User> findByEmailOrLogin(String email, String login);

    @Query("select count(u) from User u where u.email = :email")
    long countUserByEmail(String email);

    @Query("select count(u) from User u where u.login = :login")
    long countUserByLogin(String login);

    Optional<User> findByIdAndCustomerIdNotNull(long userId);

    default boolean isUserExistsByEmail(String email) {
        return countUserByEmail(email.toLowerCase()) > 0;
    }

    default boolean isUserExistsByLogin(String login) {
        return countUserByLogin(login.toLowerCase()) > 0;
    }
}
