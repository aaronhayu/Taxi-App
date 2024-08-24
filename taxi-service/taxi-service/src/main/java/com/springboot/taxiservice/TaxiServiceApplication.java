package com.springboot.taxiservice;

import com.saber.taxiconfig.config.RedisConfig;
import com.springboot.taximodel.enums.TaxiStatus;
import com.springboot.taximodel.enums.TaxiType;
import com.springboot.taximodel.util.LocationGenerator;
import com.springboot.taxiservice.listener.TaxiBookingAcceptedEventMessageListener;
import com.springboot.taxiservice.model.Taxi;
import com.springboot.taxiservice.repo.TaxiRepository;
import com.springboot.taxiservice.service.TaxiService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import java.util.UUID;

@SpringBootApplication
@Import({RedisConfig.class})
public class TaxiServiceApplication {

	public static void main(String[] args) {

		SpringApplication.run(TaxiServiceApplication.class, args);
	}

	/**
	 * Bean definition for an ApplicationRunner that initializes taxi data in the application.
	 * @param taxiRepository The repository interface for managing Taxi entities.
	 * @param taxiService The service responsible for business logic related to taxis.
	 * @return ApplicationRunner An ApplicationRunner instance that performs initialization tasks when the application starts.
	 */
	@Bean
	public ApplicationRunner applicationRunner(TaxiRepository taxiRepository, TaxiService taxiService){

		// Return an ApplicationRunner that performs tasks when the application starts.
		return args -> {
			// Clear all existing taxi data from the repository.
			taxiRepository.deleteAll();

			// Save new Taxi entities with random IDs, types, and initial status as AVAILABLE.
			taxiRepository.save(new Taxi(UUID.randomUUID().toString(), TaxiType.MINI, TaxiStatus.AVAILABLE));
			taxiRepository.save(new Taxi(UUID.randomUUID().toString(), TaxiType.NANO, TaxiStatus.AVAILABLE));
			taxiRepository.save(new Taxi(UUID.randomUUID().toString(), TaxiType.VAN, TaxiStatus.AVAILABLE));

			Iterable<Taxi> taxis = taxiRepository.findAll();

			taxis.forEach(t -> {
				taxiService.updateLocation(t.getTaxiId(),
						LocationGenerator.getLocation(79.865072, 6.927610, 3000)).subscribe();
			});
		};
	}

	/**
	 * Bean definition for configuring a Redis message listener container.
	 * @param connectionFactory The RedisConnectionFactory to be used for connecting to Redis.
	 * @param taxiBookingAcceptedEventMessageListener The message listener responsible for
	 *                         handling accepted taxi booking events.
	 * @return RedisMessageListenerContainer A configured Redis message listener container
	 * that listens to messages from Redis channels.
	 */
	@Bean
	public RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory,
				TaxiBookingAcceptedEventMessageListener taxiBookingAcceptedEventMessageListener){

		RedisMessageListenerContainer container = new RedisMessageListenerContainer();
		container.setConnectionFactory(connectionFactory);
		// Add the taxiBookingAcceptedEventMessageListener to listen for messages on the specified Redis channel.
		container.addMessageListener(taxiBookingAcceptedEventMessageListener,
				new PatternTopic(RedisConfig.ACCEPTED_EVENT_CHANNEL));
		return container;
	}

}
