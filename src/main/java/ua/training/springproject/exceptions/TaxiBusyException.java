package ua.training.springproject.exceptions;

public class TaxiBusyException extends RuntimeException{

    public TaxiBusyException(String message) {
        super(message);
    }
}
