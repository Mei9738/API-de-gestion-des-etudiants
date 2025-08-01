package org.example.project2.exception.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.project2.config.Messages;
import org.example.project2.exception.TechnicalException;
import org.example.project2.util.consts.GlobalConstants;
import org.springframework.http.HttpStatus;

@Slf4j
@RequiredArgsConstructor
public class BaseResponseEntityExceptionHandler {
    private final Messages messages;

    CustomErrorResponse buildErrorResponse(Exception e, HttpStatus status) {
        String title = e.getClass().getSimpleName();
        String code = e.getMessage();
        if (e instanceof TechnicalException) {
            code = GlobalConstants.ERROR_WS_TECHNICAL;
        }
        return buildErrorResponse(title, status, code);
    }

    CustomErrorResponse buildErrorResponse(String title, HttpStatus status, String code) {
        return new CustomErrorResponse(code, status.value(), title);
    }
}