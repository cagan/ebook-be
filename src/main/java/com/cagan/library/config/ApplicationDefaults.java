package com.cagan.library.config;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public interface ApplicationDefaults {
    interface AuditEvents {
        int retentionPolicy = 30;
    }

    interface ClientApp {
        String name = "EBook Backend API";
    }

    interface Registry {
        String password = null;
    }

    interface Ribbon {
        String[] displayOnActiveProfiles = null;
    }

    interface Gateway {
        Map<String, List<String>> authorizeMicroserviceEndpoints = new LinkedHashMap<>();

        interface RateLimiting {
            boolean enabled = false;
            final long limit = 100000L;
            int durationInSeconds = 3600;
        }
    }

    interface Social {
        String redirectAfterSignIn = "/#/home";
    }

    interface Logging {
        boolean useJsonFormat = false;

        interface Logstash {
            boolean enabled = false;
            String host = "localhost";
            int port = 5000;
            int ringBufferSize = 512;
        }
    }

    interface ApiDocs {
        String title = "Application API";
        String description = "API documentation";
        String version = "0.0.1";
        String termsOfServiceUrl = null;
        String contactName = null;
        String contactUrl = null;
        String contactEmail = null;
        String license = null;
        String licenseUrl = null;
        String defaultIncludePattern = "/api/**";
        String managementIncludePattern = "/management/**";
        String host = null;
        String[] protocols = new String[0];
        boolean useDefaultResponseMessage = true;
    }

    interface Security {
        String contentSecurityPolicy = "default-src 'self'; frame-src 'self' data:; script-src 'self' 'unsafe-inline' 'unsafe-eval' https://storage.googleapis.com; style-src 'self' 'unsafe-inline'; img-src 'self' data:; font-src 'self' data:";

        interface RememberMe {
            String key = null;
        }

        interface Authentication {
            interface Jwt {
                String secret = null;
                String base64Secret = null;
                long tokenValidityInSeconds = 1800L;
                long tokenValidityInSecondsForRememberMe = 2592000L;
            }
        }

        interface ClientAuthorization {
            String accessTokenUri = null;
            String tokenServiceId = null;
            String clientId = null;
            String clientSecret = null;
        }
    }

    interface Mail {
        boolean enabled = false;
        String from = "";
        String baseUrl = "";
    }

    interface Cache {
        interface Redis {
            String[] server = new String[]{"redis://localhost:6379"};
            int expiration = 300;
            boolean cluster = false;
            int connectionPoolSize = 64;
            int connectionMinimumIdleSize = 24;
            int subscriptionConnectionPoolSize = 50;
            int subscriptionConnectionMinumumIdleSize = 1;
        }

        interface Memcached {
            boolean enabled = false;
            String servers = "localhost:11211";
            int expiration = 300;
            boolean useBinaryProtocol = true;

            interface Authentication {
                boolean enabled = false;
            }
        }

        interface Infinispan {
            String configFile = "default-configs/default-jgroups-tcp.xml";
            boolean statsEnabled = false;

            interface Replicated {
                long timeToLiveSeconds = 60L;
                long maxEntries = 100L;
            }

            interface Distributes {
                long timeToLiveSeconds = 60L;
                long maxEntries = 100L;
                int instanceCount = 1;
            }

            interface Local {
                long timeToLiveSeconds = 60L;
                long maxEntries = 100L;
            }
        }
    }

    interface Http {
        interface Cache {
            int timeToLiveInDays = 14561;
        }
    }

    interface Async {
        int corePoolSize = 2;
        int maxPoolSize = 50;
        int queueCapacity = 10000;
    }
}