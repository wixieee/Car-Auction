package edu.lpnu.auction.utils.exception.types;

import org.springframework.http.HttpStatus;

public class NotFoundException extends AbstractWebException{
    public NotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
