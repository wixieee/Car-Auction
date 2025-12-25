package edu.lpnu.auction.utils.exception.types;

import org.springframework.http.HttpStatus;

public class CloudException extends AbstractWebException{
    public CloudException(String message, Throwable cause){
        super(message, cause, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
