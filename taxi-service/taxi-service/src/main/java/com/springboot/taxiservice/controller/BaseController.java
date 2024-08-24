/************Start*****************************************
 Date            Name                 Version        Remarks
 -------------------------------------------------------------
 05-06-2023      Aaron Osikhena        3.0.0        - Author
 ************End*******************************************/
package com.springboot.taxiservice.controller;

import com.springboot.taximodel.dto.response.ErrorDTO;
import com.springboot.taxiservice.exception.TaxiIdNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Global exception handler for handling exceptions across the whole application.
 */
@ControllerAdvice
public class BaseController {

    /**
     * Handles TaxiIdNotFoundException and returns an appropriate error response.
     * @param e The TaxiIdNotFoundException thrown when a taxi ID is not found.
     * @return ResponseEntity<ErrorDTO> A response entity containing an ErrorDTO object
     * with the error message and status code,
     * and an HTTP status code of BAD_REQUEST.
     */
    @ExceptionHandler(TaxiIdNotFoundException.class)
    public ResponseEntity<ErrorDTO> handleTaxiIdNotFoundException(TaxiIdNotFoundException e){
        return new ResponseEntity<ErrorDTO>(new ErrorDTO(e.getMessage(),
                HttpStatus.BAD_REQUEST.value()), HttpStatus.BAD_REQUEST);
    }
}
