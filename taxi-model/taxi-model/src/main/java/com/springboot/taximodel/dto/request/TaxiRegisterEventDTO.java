package com.springboot.taximodel.dto.request;

import com.springboot.taximodel.enums.TaxiType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaxiRegisterEventDTO {

    private String taxiId = UUID.randomUUID().toString();

    private TaxiType taxiType;
}
