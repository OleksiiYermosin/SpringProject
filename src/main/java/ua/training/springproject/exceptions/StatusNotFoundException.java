package ua.training.springproject.exceptions;

/**
 * Exception that occurs when status wasn`t found
 */
public class StatusNotFoundException extends RuntimeException{

    public StatusNotFoundException() {
    }

    public StatusNotFoundException(String message) {
        super(message);
    }

}
