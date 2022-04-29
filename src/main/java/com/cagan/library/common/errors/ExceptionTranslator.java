package com.cagan.library.common.errors;

import com.cagan.library.util.HeaderUtil;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.spring.web.advice.ProblemHandling;

@ControllerAdvice
public class ExceptionTranslator implements ProblemHandling {
    @Value("${spring.application.name}")
    private String applicationName;

    @ExceptionHandler(FileNotExceptedException.class)
    public ResponseEntity<Problem> handleFileNotExceptedException(FileNotExceptedException ex, @NonNull NativeWebRequest request) {
        return create(ex, request, HeaderUtil.createFailureAlert(applicationName, true, ex.getEntityName(), ex.getErrorKey(), ex.getMessage()));
    }
}