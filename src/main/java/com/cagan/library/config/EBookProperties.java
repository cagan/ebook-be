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
}
