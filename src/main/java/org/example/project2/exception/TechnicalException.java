package org.example.project2.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class TechnicalException extends Exception {
    public TechnicalException(String message) {
        super(message);
    }
}
