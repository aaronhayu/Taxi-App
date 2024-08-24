/************Start*****************************************
 Date            Name                 Version        Remarks
 -------------------------------------------------------------
 27-05-2023      Aaron Osikhena        3.0.0        - Author

 The following Listener implementation is required to take action when a Taxi Booking
 Accepted Event is sent from the Taxi Booking Microservice to the Taxi Microservice:
 ************End*******************************************/

package com.springboot.taxiservice.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.springboot.taximodel.dto.request.TaxiBookingAcceptedEventDTO;
import com.springboot.taximodel.enums.TaxiStatus;
import com.springboot.taxiservice.service.TaxiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class TaxiBookingAcceptedEventMessageListener implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaxiBookingAcceptedEventMessageListener.class);

    public final TaxiService taxiService;
    public final ObjectMapper objectMapper = new ObjectMapper();

    public TaxiBookingAcceptedEventMessageListener(TaxiService taxiService){

        this.taxiService = taxiService;
    }


    /**
     * Handles incoming Redis messages related to taxi bookings being accepted.
     * @param message The incoming message from Redis.
     * The preceding listener's onMessage method will be activated whenever an event is
     * received and will update the status of the Taxi
     */
    @Override
    public void onMessage(Message message, @Nullable byte[] bytes) {
        try {
            TaxiBookingAcceptedEventDTO taxiBookingAcceptedEventDTO = objectMapper
                    .readValue(new String(message.getBody()), TaxiBookingAcceptedEventDTO.class);

            // Log the accepted event.
            LOGGER.info("Accepted Event {}", taxiBookingAcceptedEventDTO);
            // Update the status of the taxi to OCCUPIED using the taxiService.
            taxiService.updateTaxiStatus(taxiBookingAcceptedEventDTO.getTaxiId(), TaxiStatus.OCCUPIED);
        }catch (IOException e){
            // Log any errors that occur during the process.
            LOGGER.error("Error while updating taxi status", e);
        }
    }
}
