package ua.training.springproject.exceptions;

/**
 * Exception that occurs when user wasn`t found
 */
public class UserNotFoundException extends RuntimeException{

    public UserNotFoundException() {
    }

    public UserNotFoundException(String message) {
        super(message);
    }

}
