package org.persac.service.exception;

/**
 * Created by maks on 11.10.15.
 */
public class EmailExistsException extends Exception {

    public EmailExistsException(String message) {
        super(message);
    }
}
