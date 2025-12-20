package edu.lpnu.auction.utils.exception.types;

import org.springframework.http.HttpStatus;

public class WrongProviderException extends AbstractWebException{
    public WrongProviderException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
