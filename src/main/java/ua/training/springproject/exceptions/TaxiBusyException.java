package ua.training.springproject.exceptions;

/**
 * Exception that occurs when taxi is busy
 */
public class TaxiBusyException extends RuntimeException{

    public TaxiBusyException(String message) {
        super(message);
    }
}
