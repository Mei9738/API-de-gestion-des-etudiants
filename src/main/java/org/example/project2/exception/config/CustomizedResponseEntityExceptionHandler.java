package org.example.project2.exception.config;


import lombok.extern.slf4j.Slf4j;
import org.example.project2.config.Messages;
import org.example.project2.exception.CustomFeignClientException;
import org.example.project2.exception.FunctionalException;
import org.example.project2.exception.TechnicalException;
import org.example.project2.exception.EmailConflictException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class CustomizedResponseEntityExceptionHandler extends BaseResponseEntityExceptionHandler {
    public CustomizedResponseEntityExceptionHandler(Messages messages) {
        super(messages);
    }

    @ExceptionHandler(TechnicalException.class)
    public ResponseEntity<CustomErrorResponse> onTechnicalException(TechnicalException e) {
        log.error(e.getMessage(), e);
        CustomErrorResponse errorResponse = buildErrorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    @ExceptionHandler(FunctionalException.class)
    public ResponseEntity<CustomErrorResponse> onFunctionalException(FunctionalException e) {
        log.error(e.getMessage(), e);
        CustomErrorResponse errorResponse = buildErrorResponse(e, HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(CustomFeignClientException.class)
    public ResponseEntity<CustomErrorResponse> onEsgClientError(CustomFeignClientException e) {
        log.error(e.getMessage(), e);
        CustomErrorResponse errorResponse = buildErrorResponse(e.getTitle(), HttpStatus.BAD_REQUEST, e.getMsg());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(EmailConflictException.class)
    public ResponseEntity<CustomErrorResponse> onEmailConflictException(EmailConflictException e) {
        log.error(e.getMessage(), e);
        CustomErrorResponse errorResponse = buildErrorResponse(e, HttpStatus.CONFLICT);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<CustomErrorResponse> onGenericException(Exception e) {
        log.error("Unexpected error occurred: {}", e.getMessage(), e);
        CustomErrorResponse errorResponse = buildErrorResponse("internal.server.error", HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}
