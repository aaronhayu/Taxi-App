/************Start*****************************************
 Date            Name                 Version        Remarks
 -------------------------------------------------------------
 02-06-2023      Aaron Osikhena        3.0.0        - Author
 ************End*******************************************/
package com.springboot.taxiservice.controller;

import com.springboot.taximodel.dto.request.LocationDTO;
import com.springboot.taximodel.dto.request.TaxiRegisterEventDTO;
import com.springboot.taximodel.dto.response.TaxiAvailableResponseDTO;
import com.springboot.taximodel.dto.response.TaxiLocationUpdatedEventResponseDTO;
import com.springboot.taximodel.dto.response.TaxiRegisterEventResponseDTO;
import com.springboot.taximodel.dto.response.TaxiStatusDTO;
import com.springboot.taximodel.enums.TaxiStatus;
import com.springboot.taximodel.enums.TaxiType;
import com.springboot.taxiservice.service.TaxiService;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RequestMapping("/taxis")
@RestController
public class TaxiController {

    private final TaxiService taxiService;

    public TaxiController(TaxiService taxiService) {

        this.taxiService = taxiService;
    }

    /**
     * Endpoint to register a new taxi.
     * @param taxiRegisterEventDTO The data transfer object containing the details needed to register a new taxi,
     * such as taxi ID and taxi type.
     * @return Mono<TaxiRegisterEventResponseDTO> A reactive type that emits a TaxiRegisterEventResponseDTO object
     * containing the taxi ID after the registration is complete.
     */
    @PostMapping
    public Mono<TaxiRegisterEventResponseDTO> register(
            @RequestBody TaxiRegisterEventDTO taxiRegisterEventDTO) {
        // Register the new taxi using the taxiService and map the result to a TaxiRegisterEventResponseDTO.
        return taxiService.register(taxiRegisterEventDTO).map(t ->
                new TaxiRegisterEventResponseDTO(t.getTaxiId()));
    }

    /**
     * Endpoint to get the status of a taxi based on its ID.
     * @param taxiId The ID of the taxi whose status is to be retrieved.
     * @return Mono<TaxiStatusDTO> A reactive type that emits a TaxiStatusDTO object containing the taxi ID and its status.
     */
    @GetMapping("/{taxiId}/status")
    public Mono<TaxiStatusDTO> getTaxiStatus(@PathVariable("taxiId") String taxiId) {
        // Retrieve the taxi status from the taxiService and map it to a TaxiStatusDTO.
        return taxiService.getTaxiStatus(taxiId).map(s -> new TaxiStatusDTO(taxiId, s));
    }

    /**
     * Endpoint to update the status of a taxi based on its ID.
     * @param taxiId The ID of the taxi whose status is to be updated.
     * @param taxiStatus The new status to be set for the taxi.
     * @return Mono<TaxiStatusDTO> A reactive type that emits a TaxiStatusDTO object containing the taxi ID and its updated status.
     */
    @PutMapping("/{taxiId}/status")
    public Mono<TaxiStatusDTO> updateTaxiStatus(
            @PathVariable("taxiId") String taxiId,
            @RequestParam("taxiStatus") TaxiStatus taxiStatus){

        // Update the taxi status using the taxiService and map the result to a TaxiStatusDTO.
        return taxiService.updateTaxiStatus(taxiId, taxiStatus).map(t ->
                new TaxiStatusDTO(t.getTaxiId(), t.getTaxiStatus()));
    }

    /**
     * Endpoint to update the location of a taxi based on its ID.
     * @param taxiId The ID of the taxi whose location is to be updated.
     * @param locationDTO The data transfer object containing the new location details (latitude and longitude).
     * @return Mono<TaxiLocationUpdatedEventResponseDTO> A reactive type that emits a TaxiLocationUpdatedEventResponseDTO
     * object containing the taxi ID after the location update is complete.
     */
    @PutMapping("/{taxiId}/location")
    public Mono<TaxiLocationUpdatedEventResponseDTO> updateLocation(
            @PathVariable("taxiId") String taxiId, @RequestBody LocationDTO locationDTO){
        // Update the taxi location using the taxiService and map the result to a TaxiLocationUpdatedEventResponseDTO.
        return taxiService.updateLocation(taxiId, locationDTO).map(t ->
                new TaxiLocationUpdatedEventResponseDTO(taxiId));
    }


    /**
     * Endpoint to get/search all available taxis of a specified type within a given radius from a specified location.
     * @param taxiType The type of taxi to search for. MINI,NANO,VAN;
     * @param latitude The latitude of the center point for the search radius.
     * @param longitude The longitude of the center point for the search radius.
     * @param radius The radius (in kilometers) within which to search for available taxis. Defaults to 1 km if not provided.
     * @return Flux<TaxiAvailableResponseDTO> A reactive type that emits a stream of TaxiAvailableResponseDTO objects,
     * representing the available taxis within the specified radius.
     */
    @GetMapping
    public Flux<TaxiAvailableResponseDTO> getAvailableTaxis(
            @RequestParam("type") TaxiType taxiType,
            @RequestParam("latitude") Double latitude,
            @RequestParam("longitute") Double longitute,
            @RequestParam(value = "radius", defaultValue = "1") Double radius){

        // Retrieve available taxis from the taxiService based on the given parameters.
        Flux<GeoResult<RedisGeoCommands.GeoLocation<String>>>
                availableTaxiFlux = taxiService.getAvailableTaxis(taxiType, latitude, longitute, radius);

        // Map each GeoResult to a TaxiAvailableResponseDTO, extracting the taxi's name from the GeoLocation.
        return availableTaxiFlux.map(r -> new TaxiAvailableResponseDTO(r.getContent().getName()));
    }

}
