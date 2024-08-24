package com.sheeft.bookingservice;

import com.sheeft.bookingservice.service.TaxiBookingService;
import com.saber.taxiconfig.config.RedisConfig;
import com.springboot.taximodel.dto.request.TaxiBookedEventDTO;
import com.springboot.taximodel.enums.TaxiType;
import com.springboot.taximodel.util.LocationGenerator;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

import java.util.Date;
import java.util.UUID;

@SpringBootApplication
@Import(RedisConfig.class)
public class BookingServiceApplication {

	public static void main(String[] args) {

		SpringApplication.run(BookingServiceApplication.class, args);
	}

	/**
	 * Bean definition for an ApplicationRunner that initializes taxi booking data in the application.
	 * @param taxiBookingService The service responsible for business logic related to taxi bookings.
	 * @return ApplicationRunner An ApplicationRunner instance that performs
	 * initialization tasks when the application starts.
	 */
	@Bean
	public ApplicationRunner applicationRunner(TaxiBookingService taxiBookingService){

		// Return an ApplicationRunner that performs tasks when the application starts.
		return args -> {

			// Loop to create and book 3 MINI taxis with random locations and current date.
			for (int i = 0; i < 3; i++){
				taxiBookingService.book(new TaxiBookedEventDTO(UUID.randomUUID().toString(),
						LocationGenerator.getLocation(79.865072, 6.927610, 3000),
						LocationGenerator.getLocation(79.865072, 6.927610, 3000),
						new Date(), 1l, TaxiType.MINI)).subscribe();
			}

			for (int i = 0;i<3;i++) {
				taxiBookingService.book(new TaxiBookedEventDTO(UUID.randomUUID().toString(),
						LocationGenerator.getLocation(79.865072, 6.927610, 3000),
						LocationGenerator.getLocation(79.865072, 6.927610, 3000),
						new Date(), 1l, TaxiType.NANO)).subscribe();
			}
			for (int i = 0;i<3;i++) {
				taxiBookingService.book(new TaxiBookedEventDTO(UUID.randomUUID().toString(),
						LocationGenerator.getLocation(79.865072, 6.927610, 3000),
						LocationGenerator.getLocation(79.865072, 6.927610, 3000),
						new Date(), 1l,
						TaxiType.VAN)).subscribe(); // Subscribe to trigger the booking operation asynchronously
			}
		};
	}
}
