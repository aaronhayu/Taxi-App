/***********Start*****************************************
 Date            Name                 Version        Remarks
 -------------------------------------------------------------
 01-05-2023      Aaron Osikhena        3.0.0        - Author
 ************End*******************************************/

package com.sheeft.bookingservice.model;

import com.springboot.taximodel.enums.TaxiBookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.core.RedisHash;

import java.util.Date;

@RedisHash("TaxiBooking")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaxiBooking {
/*
   This is the domain model, which will store the taxiBookingId,
   TaxiBookingStatus, start location, end location, start time, end time, and so on

   Note that it has a @RedisHash annotation on its type and a property named id that is annotated with
   org.springframework.data.annotation.Id. Those two items are responsible for creating the actual key
   used to persist the hash.
*/
    @Id
    private String taxiBookingId;

    private Point start;

    private Date startTime;

    private Point end;

    private Date endTime;

    private Date bookedTime;

    private Date acceptedTime;

    private Long customerId;

    private TaxiBookingStatus bookingStatus;

    private String reasonToCancel;

    private Date cancelTime;

    private String taxiId;
}
