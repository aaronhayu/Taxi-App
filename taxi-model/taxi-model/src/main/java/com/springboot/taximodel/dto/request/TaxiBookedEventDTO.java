package com.springboot.taximodel.dto.request;
import com.springboot.taximodel.enums.TaxiType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaxiBookedEventDTO {

    public String taxiBookingId = UUID.randomUUID().toString();

    private LocationDTO start;

    private LocationDTO end;

    private Date bookedTime = new Date();

    private Long customerId;

    private TaxiType taxiType;
}
