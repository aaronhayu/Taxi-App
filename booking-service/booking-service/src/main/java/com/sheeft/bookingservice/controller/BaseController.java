package com.sheeft.bookingservice.controller;

import com.sheeft.bookingservice.exception.TaxiBookingIdNotFoundException;
import com.springboot.taximodel.dto.response.ErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class BaseController {

    /**
     * Handles TaxiBookingIdNotFoundException and returns a ResponseEntity with an ErrorDTO.
     * @param e The TaxiBookingIdNotFoundException that was thrown.
     * @return ResponseEntity<ErrorDTO> A response entity containing the error details and HTTP status.
     */
    @ExceptionHandler(TaxiBookingIdNotFoundException.class)
    public ResponseEntity<ErrorDTO> handleTaxiBookingIdNotFoundException(TaxiBookingIdNotFoundException e){
        // Create and return a ResponseEntity with an ErrorDTO and HTTP BAD_REQUEST status
        return new ResponseEntity<ErrorDTO>(new ErrorDTO(e.getMessage(),
                HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
    }
}
