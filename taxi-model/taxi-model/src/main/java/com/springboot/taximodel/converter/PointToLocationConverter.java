package com.springboot.taximodel.converter;

import com.springboot.taximodel.dto.request.LocationDTO;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.geo.Point;

/**
 * Converter class for converting a Point object to a LocationDTO object.
 */
public class PointToLocationConverter implements Converter<Point, LocationDTO> {

    /**
     * Converts a Point object to a LocationDTO object.
     * @param point The Point object to be converted.
     * @return LocationDTO The converted LocationDTO object, or null if the input point is null.
     */
    @Override
    public LocationDTO convert(Point point) {
        if (point == null) {
            return null;
        }

        // Return a new LocationDTO with the x and y coordinates from the Point
        return new LocationDTO(point.getX(), point.getY(), null);
    }
}
