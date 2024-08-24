/***********Start*****************************************
 Date            Name                 Version        Remarks
 -------------------------------------------------------------
 01-05-2023      Aaron Osikhena        3.0.0        - Author
 ************End*******************************************/
package com.springboot.taxiservice.model;

import com.springboot.taximodel.enums.TaxiStatus;
import com.springboot.taximodel.enums.TaxiType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;


@RedisHash("Taxi")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Taxi implements Serializable {

/*
This is the main domain model, which will store the taxiid, TaxiType,
and TaxiStatus of an actual physical taxi
 */
    @Id
    private String taxiId;

    private TaxiType taxiType;

    private TaxiStatus taxiStatus;
}
