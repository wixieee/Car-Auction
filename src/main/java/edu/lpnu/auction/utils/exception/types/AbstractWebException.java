package edu.lpnu.auction.utils.exception.types;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AbstractWebException extends RuntimeException {
    private final HttpStatus status;

    public AbstractWebException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public AbstractWebException(String message, Throwable cause, HttpStatus status) {
        super(message, cause);
        this.status = status;
    }
}
