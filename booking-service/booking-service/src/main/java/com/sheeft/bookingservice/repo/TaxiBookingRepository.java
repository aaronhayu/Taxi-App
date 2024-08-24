package com.sheeft.bookingservice.repo;

import com.sheeft.bookingservice.model.TaxiBooking;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/*
CrudRepository is part of the Spring Data JPA framework,
which provides convenience methods to perform CRUD (Create, Read, Update, Delete)
The @Repository annotation is used to mark this interface as a data repository component of Spring.
 */
@Repository
public interface TaxiBookingRepository extends CrudRepository<TaxiBooking, String> {
}
