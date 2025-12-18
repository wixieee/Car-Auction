package edu.lpnu.auction.utils.exception.types;

import org.springframework.http.HttpStatus;

public class PasswordMismatchException extends AbstractWebException {
    public PasswordMismatchException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
