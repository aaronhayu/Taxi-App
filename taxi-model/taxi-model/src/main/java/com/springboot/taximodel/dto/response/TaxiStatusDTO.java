package com.springboot.taximodel.dto.response;

import com.springboot.taximodel.enums.TaxiStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaxiStatusDTO {

    private String taxiId;

    private TaxiStatus status;
}
