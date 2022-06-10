package com.cagan.library.web.errors;

import lombok.experimental.UtilityClass;

import java.net.URI;

@UtilityClass
public final class ErrorConstants {
    public static final String ERR_CONCURRENCY_FAILURE = "error.concurrencyFailure";
    public static final String ERR_VALIDATION = "error.validation";
    public static final String PROBLEM_BASE_URL = "https://www.library.com/problem";
    public static final URI DEFAULT_TYPE = URI.create(PROBLEM_BASE_URL + "/problem-with-message");
    public static final URI CONSTRAINT_VIOLATION_TYPE = URI.create(PROBLEM_BASE_URL + "/constraint-violation");
    public static final URI FILE_NOT_EXCEPTED_EXCEPTION = URI.create(PROBLEM_BASE_URL + "/file-not-excepted-exception");
    public static final URI INVALID_PASSWORD_TYPE = URI.create(PROBLEM_BASE_URL + "/invalid-password");

    public static final URI EMAIL_ALREADY_USED_TYPE = URI.create(PROBLEM_BASE_URL + "/email-already-used");

    public static final URI USERNAME_ALREADY_USED_TYPE = URI.create(PROBLEM_BASE_URL + "/username-already-used");

    public static final URI NON_ACTIVE_ORDER_SESSION = URI.create(PROBLEM_BASE_URL + "/non-active-order-session");
}