package edu.lpnu.auction.utils.exception.types;

import org.springframework.http.HttpStatus;

public class BadRequestException extends AbstractWebException {
    public BadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
