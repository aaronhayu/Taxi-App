/************Start*****************************************
 Date            Name                 Version        Remarks
 -------------------------------------------------------------
 09-05-2023      Aaron Osikhena        3.0.0        - Author
 ************End*******************************************/

package com.springboot.taximodel.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaxiBookingCanceledEventDTO {

    private String taxiBookingId;

    private String reason;

    private Date cancelTime = new Date();
}
