package com.cagan.library.web.rest;

import com.cagan.library.repository.UserRepository;
import com.cagan.library.security.AuthoritiesConstants;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.cache.CacheManager;

@RestController
@RequestMapping("/api/admin")
@Api(tags = "AdminUser")
public class AdminUserController {
    private final CacheManager cacheManager;

    public AdminUserController(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @GetMapping("/create")
    @PreAuthorize("hasAuthority(\"" + AuthoritiesConstants.ADMIN + "\")")
    public ResponseEntity<String> createUser() {
        cacheManager.getCache(UserRepository.USERS_BY_LOGIN_CACHE).put("SECOND_CACHE", "This is second cache");
        cacheManager.getCache("OK");
        return ResponseEntity.ok("Created");
    }
}
