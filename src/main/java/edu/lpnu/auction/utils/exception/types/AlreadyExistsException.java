package edu.lpnu.auction.utils.exception.types;

import org.springframework.http.HttpStatus;

public class AlreadyExistsException extends AbstractWebException {
    public AlreadyExistsException(String message) {
        super(message, HttpStatus.CONFLICT);
    }
}
