package edu.lpnu.auction.utils.exception.types;

import org.springframework.http.HttpStatus;

public class WrongFormatException extends AbstractWebException {
    public WrongFormatException(String message) {
        super(message, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }
}
