package com.cagan.library.config;

import com.cagan.library.domain.Authority;
import com.cagan.library.domain.BookCatalog;
import com.cagan.library.domain.User;
import com.cagan.library.repository.BookCatalogRepository;
import com.cagan.library.repository.UserRepository;
import org.hibernate.cache.jcache.ConfigSettings;
import org.redisson.Redisson;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.redisson.jcache.configuration.RedissonConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.boot.info.BuildProperties;
import org.springframework.boot.info.GitProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import java.net.URI;
import java.util.concurrent.TimeUnit;

//@Configuration
//@EnableCaching
public class CacheConfiguration {
    private GitProperties gitProperties;
    private BuildProperties buildProperties;

    @Bean
    public CacheManager cacheManager() {
        return Caching.getCachingProvider().getCacheManager();
//        return new ConcurrentMapCacheManager("books");
    }

    @Bean
    public javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration(EBookProperties properties) {
        MutableConfiguration<Object, Object> jcacheConfig = new MutableConfiguration<>();

        URI redisUri = URI.create(properties.getCache().getRedis().getServer()[0]);

        Config config = new Config();

        SingleServerConfig singleServerConfig = config
                .useSingleServer()
                .setConnectionPoolSize(properties.getCache().getRedis().getConnectionPoolSize())
                .setConnectionMinimumIdleSize(properties.getCache().getRedis().getConnectionMinimumIdleSize())
                .setSubscriptionConnectionPoolSize(properties.getCache().getRedis().getSubscriptionConnectionPoolSize())
                .setAddress(properties.getCache().getRedis().getServer()[0]);

        if (redisUri.getUserInfo() != null) {
            singleServerConfig.setPassword(redisUri.getUserInfo().substring(redisUri.getUserInfo().indexOf(':') + 1));
        }

        jcacheConfig.setStatisticsEnabled(true);
        jcacheConfig.setExpiryPolicyFactory(
                CreatedExpiryPolicy.factoryOf(new Duration(TimeUnit.SECONDS, properties.getCache().getRedis().getExpiration()))
        );
        return RedissonConfiguration.fromInstance(Redisson.create(config), jcacheConfig);
    }


    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer(javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration) {
        return cacheManager -> {
            createCache(cacheManager, UserRepository.USERS_BY_EMAIL_CACHE, jcacheConfiguration);
            createCache(cacheManager, UserRepository.USERS_BY_LOGIN_CACHE, jcacheConfiguration);
            createCache(cacheManager, User.class.getName(), jcacheConfiguration);
            createCache(cacheManager, BookCatalog.class.getName(), jcacheConfiguration);
            createCache(cacheManager, BookCatalogRepository.BOOK_CATALOG_LIST_CACHE, jcacheConfiguration);
            createCache(cacheManager, Authority.class.getName(), jcacheConfiguration);
            createCache(cacheManager, "authorities", jcacheConfiguration);
        };
    }

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(javax.cache.CacheManager cm) {
        return hibernateProperties -> hibernateProperties.put(ConfigSettings.CACHE_MANAGER, cm);
    }

    private void createCache(
            javax.cache.CacheManager cm,
            String cacheName,
            javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration
    ) {
        javax.cache.Cache<Object, Object> cache = cm.getCache(cacheName);
        if (cache != null) {
            cache.clear();
        } else {
            cm.createCache(cacheName, jcacheConfiguration);
        }
    }

    @Autowired(required = false)
    public void setGitProperties(GitProperties gitProperties) {
        this.gitProperties = gitProperties;
    }

    @Autowired(required = false)
    public void setBuildProperties(BuildProperties buildProperties) {
        this.buildProperties = buildProperties;
    }

//    @Bean TODO: Implement key generator
//    public KeyGenerator keyGenerator() {
//        return new PrefixedKeyGenerator(this.gitProperties, this.buildProperties);
//    }
}
