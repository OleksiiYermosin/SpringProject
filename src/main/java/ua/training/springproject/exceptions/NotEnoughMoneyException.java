package ua.training.springproject.exceptions;

/**
 * Exception that occurs when user doesn`t have enough money
 */
public class NotEnoughMoneyException extends RuntimeException{

    public NotEnoughMoneyException(String message) {
        super(message);
    }
}
