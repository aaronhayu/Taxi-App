package com.sheeft.bookingservice.controller;

import com.sheeft.bookingservice.service.TaxiBookingService;
import com.springboot.taximodel.dto.request.TaxiBookedEventDTO;
import com.springboot.taximodel.dto.request.TaxiBookingAcceptedEventDTO;
import com.springboot.taximodel.dto.request.TaxiBookingCanceledEventDTO;
import com.springboot.taximodel.dto.response.TaxiBookedEventResponseDTO;
import com.springboot.taximodel.dto.response.TaxiBookingAcceptedEventResponseDTO;
import com.springboot.taximodel.dto.response.TaxiBookingCanceledEventResponseDTO;
import com.springboot.taximodel.dto.response.TaxiBookingResponseDTO;
import com.springboot.taximodel.enums.TaxiType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequestMapping("/taxibookings")
@RestController
public class TaxiBookingController {

    private final TaxiBookingService taxiBookingService;

    public TaxiBookingController(TaxiBookingService taxiBookingService) {
        this.taxiBookingService = taxiBookingService;
    }

    /**
     * Endpoint to book a taxi.
     *
     * @param taxiBookedEventDTO The data transfer object containing the details needed to book a taxi,
     * such as the start and end locations, booked time, customer ID, and taxi type.
     */
    @PostMapping
    public Mono<TaxiBookedEventResponseDTO> book(@RequestBody TaxiBookedEventDTO taxiBookedEventDTO){
        // Book the taxi using the taxiBookingService and map the result to a TaxiBookedEventResponseDTO.
        return taxiBookingService.book(taxiBookedEventDTO)
                .map(t -> new TaxiBookedEventResponseDTO(t
                        .getTaxiBookingId()));
    }

    /**
     * Endpoint to cancel a taxi booking.
     *
     * @param taxiBookingId The ID of the taxi booking to be cancelled.
     * @param taxiBookingCanceledEventDTO The data transfer object containing the details needed to cancel the booking,
     * such as the reason for cancellation and the cancellation time.
     */
    @PutMapping("/{taxiBookingId}/cancel")
    public Mono<TaxiBookingCanceledEventResponseDTO> cancel(@PathVariable("taxiBookingId") String taxiBookingId,
                 @RequestBody TaxiBookingCanceledEventDTO taxiBookingCanceledEventDTO){
        // Cancel the taxi booking using the taxiBookingService and
        // map the result to a TaxiBookingCanceledEventResponseDTO.
        return taxiBookingService.cancel(taxiBookingId, taxiBookingCanceledEventDTO)
                .map(t -> new TaxiBookingCanceledEventResponseDTO(t.getTaxiBookingId()));
    }

    /**
     * Endpoint to accept a taxi booking.
     * @param taxiBookingId The ID of the taxi booking to be accepted.
     * @param taxiBookingAcceptedEventDTO The data transfer object containing the details needed to accept the booking,
     * such as the taxi ID and the acceptance time.
     */
    @PutMapping("/{taxiBookingId}/accept")
    public Mono<TaxiBookingAcceptedEventResponseDTO> accept(@PathVariable("taxiBookingId") String taxiBookingId,
                              @RequestBody TaxiBookingAcceptedEventDTO taxiBookingAcceptedEventDTO) {
        // Accept the taxi booking using the taxiBookingService
        // and map the result to a TaxiBookingAcceptedEventResponseDTO.
        return taxiBookingService.accept(taxiBookingId, taxiBookingAcceptedEventDTO)
                .map(t -> new TaxiBookingAcceptedEventResponseDTO(t.getTaxiBookingId(), t.getTaxiId(), t.getAcceptedTime()));
    }

    /**
     * Endpoint to retrieve taxi bookings of a specified type within a given radius from a specified location.
     * @param taxiType The type of taxi bookings to retrieve.
     * @param latitude The latitude of the center point for the search radius.
     * @param longitude The longitude of the center point for the search radius.
     * @param radius The radius (in kilometers) within which to search for taxi bookings. Defaults to 1 km if not provided.
     */
    @GetMapping
    public Flux<TaxiBookingResponseDTO> getBookings(@RequestParam("type") TaxiType taxiType,
                                                    @RequestParam("latitude") Double latitude,
                                                    @RequestParam("longitude") Double longitude,
                                                    @RequestParam(value = "radius", defaultValue = "1") Double radius) {
        // Map each GeoResult to a TaxiBookingResponseDTO, extracting the taxi booking name from the GeoLocation.
        return taxiBookingService.getBookings(taxiType, latitude, longitude, radius)
                .map(r -> new TaxiBookingResponseDTO(r.getContent().getName()));
    }
}
