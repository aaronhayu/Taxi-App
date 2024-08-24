/************Start*****************************************
 Date            Name                 Version        Remarks
 -------------------------------------------------------------
 26-05-2023      Aaron Osikhena        3.0.0        - Author
 ************End*******************************************/

package com.sheeft.bookingservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sheeft.bookingservice.exception.TaxiBookingIdNotFoundException;
import com.sheeft.bookingservice.model.TaxiBooking;
import com.sheeft.bookingservice.repo.TaxiBookingRepository;
import com.saber.taxiconfig.config.RedisConfig;
import com.springboot.taximodel.converter.LocationToPointConverter;
import com.springboot.taximodel.dto.request.TaxiBookedEventDTO;
import com.springboot.taximodel.dto.request.TaxiBookingAcceptedEventDTO;
import com.springboot.taximodel.dto.request.TaxiBookingCanceledEventDTO;
import com.springboot.taximodel.enums.TaxiBookingStatus;
import com.springboot.taximodel.enums.TaxiType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
public class TaxiBookingService {

    private static Logger LOGGER = LoggerFactory.getLogger(TaxiBookingService.class);

    private final RedisTemplate<String, String> redisTemplate;
    private final ReactiveRedisTemplate<String, String> reactiveRedisTemplate;
    private final TaxiBookingRepository taxiBookingRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final LocationToPointConverter locationToPointConverter = new LocationToPointConverter();

    public TaxiBookingService(RedisTemplate<String, String>
                                      redisTemplate, ReactiveRedisTemplate<String, String>
            reactiveRedisTemplate, TaxiBookingRepository taxiBookingRepository) {

        this.redisTemplate = redisTemplate;
        this.reactiveRedisTemplate = reactiveRedisTemplate;
        this.taxiBookingRepository = taxiBookingRepository;

    }

    /**
     * Books a taxi based on the provided TaxiBookedEventDTO and returns a Mono<TaxiBooking>
     * @param taxiBookedEventDTO The data transfer object containing the details needed to book a taxi.
     *                 This includes the start and end locations, booking time, customer ID, and taxi type.
     * @return Mono<TaxiBooking> A reactive type that emits the saved TaxiBooking object once the booking is complete.
     */
    public Mono<TaxiBooking> book(TaxiBookedEventDTO taxiBookedEventDTO) {
        // Create a new TaxiBooking object.
        TaxiBooking taxiBooking = new TaxiBooking();
        taxiBooking.setEnd(locationToPointConverter.convert(taxiBookedEventDTO.getEnd()));
        taxiBooking.setStart(locationToPointConverter.convert(taxiBookedEventDTO.getStart()));
        taxiBooking.setBookedTime(taxiBookedEventDTO.getBookedTime());
        taxiBooking.setCustomerId(taxiBookedEventDTO.getCustomerId());
        taxiBooking.setBookingStatus(TaxiBookingStatus.ACTIVE);
        TaxiBooking savedTaxiBooking = taxiBookingRepository.save(taxiBooking);
        return reactiveRedisTemplate
                .opsForGeo()
                .add(getTaxiTypeBookings(
                 taxiBookedEventDTO.getTaxiType()),
                 taxiBooking.getStart(),     // Use the start location as the point
                 taxiBooking.getTaxiBookingId())
                .flatMap(l -> Mono.just(savedTaxiBooking));
    }


    /**
     * Cancels a taxi booking based on the provided taxiBookingId and canceledEventDTO,
     * and returns a Mono<TaxiBooking>.
     *
     * @param taxiBookingId The ID of the taxi booking to be cancelled.
     * @param canceledEventDTO The data transfer object containing the cancellation details,
     *                         such as the reason for cancellation and the cancellation time.
     * @return Mono<TaxiBooking> A reactive type that emits the updated TaxiBooking
     * object once the cancellation is complete.
     * @throws TaxiBookingIdNotFoundException if no TaxiBooking is found with the given ID.
     */
    public Mono<TaxiBooking> cancel(String taxiBookingId, TaxiBookingCanceledEventDTO canceledEventDTO){
        Optional<TaxiBooking> taxiBookingOptional = taxiBookingRepository.findById(taxiBookingId);
        // Check if the TaxiBooking object is present.
        if (taxiBookingOptional.isPresent()){
            TaxiBooking taxiBooking = taxiBookingOptional.get();
            taxiBooking.setBookingStatus(TaxiBookingStatus.CANCELLED); // Set the booking status of the TaxiBooking to CANCELLED.
            taxiBooking.setReasonToCancel(canceledEventDTO.getReason()); // Set the reason for cancellation based on the canceledEventDTO.
            taxiBooking.setCancelTime(canceledEventDTO.getCancelTime());
            return Mono.just(taxiBookingRepository.save(taxiBooking));
        } else {
            throw getTaxiBookingIdNotFoundException(taxiBookingId);
        }
    }


    /**
     * Accepts a taxi booking based on the provided taxiBookingId and acceptedEventDTO,
     * and returns a Mono<TaxiBooking>.
     *
     * @param taxiBookingId The ID of the taxi booking to be accepted.
     * @param acceptedEventDTO The data transfer object containing the acceptance details,
     *                         such as the taxi ID and the acceptance time.
     * @return Mono<TaxiBooking> A reactive type that emits the updated TaxiBooking object once the acceptance is complete.
     * @throws TaxiBookingIdNotFoundException if no TaxiBooking is found with the given ID.
     */
    public Mono<TaxiBooking> accept(String taxiBookingId, TaxiBookingAcceptedEventDTO acceptedEventDTO){
    Optional<TaxiBooking> taxiBookingOptional = taxiBookingRepository.findById(taxiBookingId);

        // Check if the TaxiBooking object is present.
        if (taxiBookingOptional.isPresent()){
            TaxiBooking taxiBooking = taxiBookingOptional.get();

            // Set the taxi ID and accepted time based on the acceptedEventDTO.
            taxiBooking.setTaxiId(acceptedEventDTO.getTaxiId());
            taxiBooking.setAcceptedTime(acceptedEventDTO.getAcceptedTime());

            return Mono.just(taxiBookingRepository.save(taxiBooking))
                    .doOnSuccess(t -> {
                try{
                    // Convert the acceptedEventDTO to a JSON string and send it to the Redis channel.
                    redisTemplate.convertAndSend(
                    RedisConfig.ACCEPTED_EVENT_CHANNEL,
                    objectMapper.writeValueAsString(acceptedEventDTO));
                } catch (JsonProcessingException e){
                    // Log an error if there is an issue with JSON processing.
                    LOGGER.error("Error while sending message to Channel {}", RedisConfig.ACCEPTED_EVENT_CHANNEL, e);
                }
            });
        }else{
            throw getTaxiBookingIdNotFoundException(taxiBookingId);
        }
    }


    /**
     * Retrieves bookings of a specified TaxiType within a given radius from a specified location.
     *
     * @param taxiType The type of taxi for which bookings are to be retrieved.
     * @param latitude The latitude of the center point for the search radius.
     * @param longitude The longitude of the center point for the search radius.
     * @param radius The radius (in kilometers) within which to search for bookings.
     * @return Flux<GeoResult<RedisGeoCommands.GeoLocation<String>>>
     * within the specified radius. Each emitted item contains geo-location data wrapped in a GeoResult.
     */
    public Flux<GeoResult<RedisGeoCommands.GeoLocation<String>>> getBookings(
            TaxiType taxiType, Double latitude, Double longitude, Double radius) {

        return reactiveRedisTemplate.opsForGeo().radius(
                getTaxiTypeBookings(taxiType),                      // Use the taxi type to get the bookings key
                new Circle(                                         // Define the search area with the specified location and radius
                        new Point(longitude, latitude),             // Set the center point of the search area
                        new Distance(radius, Metrics.KILOMETERS))); // Set the radius for the search area in kilometers
    }

    /**
     * Updates the status of a taxi booking based on the provided taxiBookingId and taxiBookingStatus,
     * and returns a Mono<TaxiBooking> that emits the updated TaxiBooking object.
     *
     * @param taxiBookingId The ID of the taxi booking whose status is to be updated.
     * @param taxiBookingStatus The new status to be set for the taxi booking.
     * @return Mono<TaxiBooking> A reactive type that emits the updated TaxiBooking object once the status update is complete.
     * @throws TaxiBookingIdNotFoundException if no TaxiBooking is found with the given ID.
     */
    public Mono<TaxiBooking> updateBookingStatus(String taxiBookingId, TaxiBookingStatus taxiBookingStatus) {
        Optional<TaxiBooking> taxiBookingOptional = taxiBookingRepository.findById(taxiBookingId);

        // Check if the TaxiBooking object is present.
        if (taxiBookingOptional.isPresent()) {
            TaxiBooking taxiBooking = taxiBookingOptional.get();

            // Set the booking status of the TaxiBooking to the new status (ACTIVE, CANCELLED, COMPLETED).
            taxiBooking.setBookingStatus(taxiBookingStatus);
            return Mono.just(taxiBookingRepository.save(taxiBooking));
        } else {
            throw getTaxiBookingIdNotFoundException(taxiBookingId);
        }
    }

    private TaxiBookingIdNotFoundException getTaxiBookingIdNotFoundException(String taxiBookingId) {
        return new TaxiBookingIdNotFoundException("Taxi Booking Id "+taxiBookingId+" Not Found");
    }

    private String getTaxiTypeBookings(TaxiType taxiType) {

        return taxiType.toString()+"-Bookings";
    }

}
