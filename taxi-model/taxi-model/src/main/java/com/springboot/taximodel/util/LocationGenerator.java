package com.springboot.taximodel.util;

import com.springboot.taximodel.dto.request.LocationDTO;

import java.util.Random;

/**
 * Utility class for generating random locations within a specified radius.
 */
public class LocationGenerator {

    /**
     * Generates a random location within a given radius around a specified central point.
     * @param x0     The longitude of the central point.
     * @param y0     The latitude of the central point.
     * @param radius The radius in meters within which the location will be generated.
     * @return LocationDTO A Data Transfer Object containing the generated latitude and longitude.
     */
    public static LocationDTO getLocation(double x0, double y0, int radius) {
        Random random = new Random();

        // Convert radius from meters to degrees
        double radiusInDegrees = radius / 111000f;

        // Generate two random numbers
        double u = random.nextDouble();
        double v = random.nextDouble();

        // Calculate random point within the circle
        double w = radiusInDegrees * Math.sqrt(u);
        double t = 2 * Math.PI * v;
        double x = w * Math.cos(t);
        double y = w * Math.sin(t);

        // Adjust the x-coordinate for the shrinking of the east-west distances
        double new_x = x / Math.cos(Math.toRadians(y0));

        // Calculate the final longitude and latitude
        double foundLongitude = new_x + x0;
        double foundLatitude = y + y0;

        // Return a new LocationDTO with the generated coordinates
        return new LocationDTO(foundLatitude, foundLongitude, null);
    }
}
