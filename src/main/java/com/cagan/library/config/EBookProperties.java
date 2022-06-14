package com.cagan.library.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.web.cors.CorsConfiguration;

@ConfigurationProperties(
        prefix = "ebook",
        ignoreUnknownFields = false
)
@Getter
@Setter
public class EBookProperties {
    private final Security security = new Security();
    private final CorsConfiguration cors = new CorsConfiguration();
    private final Cache cache = new Cache();

    @Getter
    @Setter
    public static class Application {
        public static final String name = ApplicationDefaults.ClientApp.name;
        public static final String description = "REST API for E-Book Application";
        public static final String version = "1.0.0";
    }

    @Getter
    @Setter
    public static class Security {
        private final String contentSecurityPolicy = "default-src 'self'; frame-src 'self' data:; script-src 'self' 'unsafe-inline' 'unsafe-eval' https://storage.googleapis.com; style-src 'self' 'unsafe-inline'; img-src 'self' data:; font-src 'self' data:";
        private final Authentication authentication = new Authentication();

        public String getContentSecurityPolicy() {
            return contentSecurityPolicy;
        }

        @Getter
        @Setter
        @NoArgsConstructor
        public static class Authentication {
            private final Jwt jwt = new Jwt();

            @Getter
            @Setter
            public static class Jwt {
                private String secret;
                private String base64Secret;
                private long tokenValidityInSeconds;
                private long tokenValidityInSecondsForRememberMe;

                public Jwt () {
                    this.secret = ApplicationDefaults.Security.Authentication.Jwt.secret;
                    this.base64Secret = ApplicationDefaults.Security.Authentication.Jwt.base64Secret;
                    this.tokenValidityInSeconds = 1800L;
                    this.tokenValidityInSecondsForRememberMe = 2592000L;
                }
            }
        }
    }

    @Getter
    @Setter
    public static class Cache {
        private final Redis redis = new Redis();

        public Redis getRedis() {
            return this.redis;
        }

        @Getter
        @Setter
        public static class Redis {
            private String[] server;
            private int expiration;
            private boolean cluster;
            private int connectionPoolSize;
            private int connectionMinimumIdleSize;
            private int subscriptionConnectionPoolSize;
            private int subscriptionConnectionMinimumIdleSize;

            public Redis() {
                this.expiration = 300;
                this.cluster = false;
                this.connectionPoolSize = 64;
                this.connectionMinimumIdleSize = 24;
                this.subscriptionConnectionPoolSize = 50;
                this.subscriptionConnectionMinimumIdleSize = 1;
            }
        }
    }
}
