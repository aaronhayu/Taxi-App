/************Start*****************************************
 Date            Name                 Version        Remarks
 -------------------------------------------------------------
 22-05-2023      Aaron Osikhena        3.0.0        - Author
 ************End*******************************************/
package com.springboot.taxiservice.service;

import com.springboot.taximodel.converter.LocationToPointConverter;
import com.springboot.taximodel.dto.request.LocationDTO;
import com.springboot.taximodel.dto.request.TaxiRegisterEventDTO;
import com.springboot.taximodel.enums.TaxiStatus;
import com.springboot.taximodel.enums.TaxiType;
import com.springboot.taxiservice.exception.TaxiIdNotFoundException;
import com.springboot.taxiservice.model.Taxi;
import com.springboot.taxiservice.repo.TaxiRepository;
import org.springframework.data.geo.*;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
public class TaxiService {
    private final ReactiveRedisTemplate<String, String>reactiveRedisTemplate;
    private final TaxiRepository taxiRepository;
    private final LocationToPointConverter locationToPointConverter = new LocationToPointConverter();

    public TaxiService(ReactiveRedisTemplate<String, String>
                               reactiveRedisTemplate, TaxiRepository taxiRepository) {
        this.reactiveRedisTemplate = reactiveRedisTemplate;
        this.taxiRepository = taxiRepository;
    }

    /**
     * Registers a new Taxi and returns a Mono<Taxi> that emits the saved Taxi object.
     *
     * @param taxiRegisterEventDTO The data transfer object containing the details needed to register a new taxi.
     *                             This includes the taxi ID and taxi type.
     * @return Mono<Taxi> A reactive type that emits the saved Taxi object once the registration is complete.
     */
    public Mono<Taxi> register(TaxiRegisterEventDTO taxiRegisterEventDTO) {
        Taxi taxi = new Taxi(
                taxiRegisterEventDTO.getTaxiId(),    // Get the taxi ID from the DTO
                taxiRegisterEventDTO.getTaxiType(),  // Get the taxi type from the DTO
                TaxiStatus.AVAILABLE);               // Set the taxi status to AVAILABLE
        return Mono.just(taxiRepository.save(taxi));
    }

    /**
     * Updates the location of an existing Taxi and returns a Mono<Taxi> that emits the updated Taxi object.
     *
     * @param taxiId The ID of the taxi whose location is to be updated.
     * @param locationDTO The data transfer object containing the new location details for the taxi.
     * @return Mono<Taxi> A reactive type that emits the updated Taxi object once the location update is complete.
     * @throws TaxiIdNotFoundException if no Taxi is found with the given ID.
     */
    public Mono<Taxi> updateLocation(String taxiId, LocationDTO locationDTO){
        Optional<Taxi> taxiOptional = taxiRepository.findById(taxiId);
        // Check if the Taxi object is present.
        if (taxiOptional.isPresent()){
            Taxi taxi = taxiOptional.get();
            return reactiveRedisTemplate.opsForGeo()
                    .add(taxi.getTaxiType()
                    .toString(),
                    locationToPointConverter.convert(locationDTO), taxiId.toString()).flatMap(l -> Mono.just(taxi));
        } else {
            // Throw an exception if the Taxi with the given ID is not found.
            throw getTaxiIdNotFoundException(taxiId);
        }
    }


    /**
     * Retrieves available Taxis of a specified type within a given radius from a specified location.
     * @param taxiType The type of taxi to search for.
     * @param latitude The latitude of the center point for the search radius.
     * @param longitude The longitude of the center point for the search radius.
     * @param radius The radius (in kilometers) within which to search for available taxis.
     * @return Flux<GeoResult<RedisGeoCommands.GeoLocation<String>>> A reactive type that emits the locations of available taxis
     * within the specified radius. Each emitted item contains geo-location data wrapped in a GeoResult.
     */
    public Flux<GeoResult<RedisGeoCommands.GeoLocation<String>>>
            getAvailableTaxis(TaxiType taxiType, Double latitude, Double longitude, Double radius){
            return reactiveRedisTemplate.opsForGeo().radius(
                    taxiType.toString(),
                    new Circle(new Point(longitude, latitude),   // Define the search area with the specified location and radius
                            new Distance(radius, Metrics.KILOMETERS)));
    }


    /**
     * Retrieves the status of a Taxi by its ID and returns a Mono<TaxiStatus> that emits the status.
     * @param taxiId The ID of the taxi whose status is to be retrieved.
     * @return Mono<TaxiStatus> A reactive type that emits the status of the Taxi if found.
     * @throws TaxiIdNotFoundException if no Taxi is found with the given ID.
     */
    public Mono<TaxiStatus> getTaxiStatus(String taxiId){
        Optional<Taxi> taxiOptional = taxiRepository.findById(taxiId);
        // Check if the Taxi object is present.
        if (taxiOptional.isPresent()){
            Taxi taxi = taxiOptional.get();
            return Mono.just(taxi.getTaxiStatus());
        }else{
            throw getTaxiIdNotFoundException(taxiId);
        }
    }


    /**
     * Updates the status of a Taxi by its ID and returns a Mono<Taxi> that emits the updated Taxi object.
     * @param taxiId The ID of the taxi whose status is to be updated.
     * @param taxiStatus The new status to be set for the taxi.
     * @return Mono<Taxi> A reactive type that emits the updated Taxi object once the status update is complete.
     * @throws TaxiIdNotFoundException if no Taxi is found with the given ID.
     */
    public Mono<Taxi> updateTaxiStatus(String taxiId, TaxiStatus taxiStatus) {
        Optional<Taxi> taxiOptional = taxiRepository.findById(taxiId);
        // Check if the Taxi object is present.
        if (taxiOptional.isPresent()) {
            Taxi taxi = taxiOptional.get();
            // Update the Taxi's status with the new status (OCCUPIED, AVAILABLE).
            taxi.setTaxiStatus(taxiStatus);
            return Mono.just(taxiRepository.save(taxi));
        } else {
            // Throw an exception if the Taxi with the given ID is not found.
            throw getTaxiIdNotFoundException(taxiId);
        }
    }

    private TaxiIdNotFoundException getTaxiIdNotFoundException(String taxiId) {
        return new TaxiIdNotFoundException("Taxi Id "+taxiId+" Not Found");
    }
}
