package com.springboot.taxiservice.exception;

/**
 * Custom exception thrown when a taxi ID is not found.
 */
public class TaxiIdNotFoundException extends RuntimeException{

    public TaxiIdNotFoundException(String message){

        super(message);
    }

    public TaxiIdNotFoundException(String message, Throwable cause){

        super(message, cause);
    }
}
