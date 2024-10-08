��#   T a x i - A p p 

Taxi Service, including functions such as getting available taxis, finding the status of a taxi, updating taxi location, and registering taxis, and the other one will be for the Taxi Booking Service, which will allow users to book a taxi, cancel a taxi, and so on. These two microservices will be running inside a container platform named Docker and will use a Redis instance for persistence, Spring Data Redis Reactive for the model and repository, and Spring WebFlux for controllers.

The following topics : 
	• Using Spring Data Redis for persistence 
	• Using Spring WebFlux for controllers 
	• Using asynchronous data transfer for cross-microservice communication
	• Using Docker to support microservice

The Taxi Microservice has the following use cases: 
Register Taxi: This use case is required to register a Taxi by a driver, a physical vehicle with a vehicle type to provide a transportation service to passengers 
Update Taxi Location: This use case is required to update the location of a registered Taxi while it moves around 
Update Taxi Status: This use case is required to update the status of a registered Taxi such as available, occupied, and so on 
Get Taxi Status: This use case is required to get the status of a registered Taxi 
Search Taxi: This use case is required to search for registered Taxis close to a passenger, given a geographical coordinate (latitude, longitude) and a radius in kilometers.

The Taxi Booking microservice has the following use cases: 
Book Taxi Ride: This use case is required to book a Taxi ride by a passenger, given a start location, end location, taxi type, and so on 
Accept Taxi Ride: This use case is required to accept a Taxi booking made by a passenger by a driver 
Cancel Taxi Ride: This use case is required to cancel a Taxi booking made by a passenger, either by the driver or by the passenger 
Search Booking: This use case is required to search Taxi Bookings close to a driver, given a geographical coordinate (latitude, longitude) and a radius in kilometers

 
 
