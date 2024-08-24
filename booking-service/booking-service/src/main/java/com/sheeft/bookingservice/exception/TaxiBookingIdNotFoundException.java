package com.sheeft.bookingservice.exception;

/**
 * Custom exception thrown when a taxi booking ID is not found.
 */
public class TaxiBookingIdNotFoundException extends RuntimeException {

    public TaxiBookingIdNotFoundException(String message){
        super(message);
    }

    public TaxiBookingIdNotFoundException(String message, Throwable cause){
        super(message, cause);
    }
}
