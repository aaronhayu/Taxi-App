package com.springboot.taxiservice.repo;

import com.springboot.taxiservice.model.Taxi;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for performing CRUD operations on Taxi entities.
 */
@Repository
public interface TaxiRepository extends CrudRepository<Taxi, String> {
    // This interface inherits CRUD methods for Taxi entities with String type IDs from CrudRepository.
}
