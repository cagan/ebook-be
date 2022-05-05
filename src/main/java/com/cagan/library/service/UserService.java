package com.cagan.library.service;

import com.cagan.library.domain.Authority;
import com.cagan.library.domain.User;
import com.cagan.library.repository.AuthorityRepository;
import com.cagan.library.repository.UserRepository;
import com.cagan.library.security.AuthoritiesConstants;
import com.cagan.library.util.RandomUtil;
import com.cagan.library.web.errors.EmailAlreadyUsedException;
import com.cagan.library.web.errors.UsernameAlreadyUsedException;
import com.cagan.library.service.dto.vm.AdminUserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class UserService {
    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    //    private final CacheManager cacheManager;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, AuthorityRepository authorityRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authorityRepository = authorityRepository;
//        this.cacheManager = cacheManager;
    }

    public Optional<User> activateRegistration(String key) {
        log.debug("Activating user for activation key: {}", key);
        return userRepository
                .findByActivationKey(key)
                .map(user -> {
                    user.setActivated(true);
                    user.setActivationKey(null);
                    // this.clearUserCache(user);
                    log.debug("Activated user: {}", user);
                    return user;
                });
    }

    public User registerUser(AdminUserDTO userDTO, String password) {
        userRepository.findOneByLogin(userDTO.getLogin().toLowerCase())
                .ifPresent(existingUser -> {
                    boolean removed = removeNonActivatedUser(existingUser);
                    if (!removed) {
                        throw new UsernameAlreadyUsedException();
                    }
                });

        userRepository.findOneByEmail(userDTO.getEmail().toLowerCase())
                .ifPresent(existingUser -> {
                    boolean removed = removeNonActivatedUser(existingUser);
                    if (!removed) {
                        throw new EmailAlreadyUsedException();
                    }
                });

        // TODO: Use mapstruct instead
        User newUser = new User();
        String encryptedPassword = passwordEncoder.encode(password);
        newUser.setLogin(userDTO.getLogin().toLowerCase());
        newUser.setPassword(encryptedPassword);
        newUser.setFirstName(userDTO.getFirstName());
        newUser.setLastName(userDTO.getLastName());
        newUser.setEmail(userDTO.getEmail().toLowerCase());
        newUser.setImageUrl(userDTO.getImageUrl());
        newUser.setLangKey(userDTO.getLangKey());
        newUser.setActivated(false);
        newUser.setActivationKey(RandomUtil.generateActivationKey());
        Set<Authority> authorities = new HashSet<>();
        authorityRepository.findById(AuthoritiesConstants.USER).ifPresent(authorities::add);
        newUser.setAuthorities(authorities);
        userRepository.save(newUser);
        // TODO: Handle clear user cache
        // this.clearUserCache(newUser);
        log.debug("Created Information for user: {}", newUser);
        return newUser;
    }

    private boolean removeNonActivatedUser(User existingUser) {
        if (existingUser.isActivated()) {
            return false;
        }
        userRepository.delete(existingUser);
        userRepository.flush();
//        this.clearUserCaches(existingUser);
        return true;
    }

    // TODO: Enable caching
//    private void clearUserCaches(@NotNull User user) {
//        Objects.requireNonNull(cacheManager.getCache(UserRepository.USERS_BY_EMAIL_CACHE), "cache is null")
//                .evict(user.getLogin());
//        if (user.getEmail() != null) {
//
//        }
//    }
}
