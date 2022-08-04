package com.cagan.library.util;

import lombok.NoArgsConstructor;
import lombok.experimental.UtilityClass;
import org.springframework.boot.SpringApplication;

import java.util.HashMap;
import java.util.Map;

@UtilityClass
public final class DefaultProfileUtil {
    private static final String SPRING_PROFILE_DEFAULT = "spring.profiles.default";

    public static void addDefaultProfile(SpringApplication app) {
        Map<String, Object> defProperties = new HashMap<>();
        defProperties.put("spring.profiles.default", "dev");
        app.setDefaultProperties(defProperties);
    }
}
