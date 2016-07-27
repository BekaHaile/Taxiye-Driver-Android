package product.clicklabs.jugnoo.driver.utils;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

/**
 * Created by socomo20 on 7/28/15.
 */
public class MapLatLngBoundsCreator {

    public LatLngBounds createBoundsWithMinDiagonal(LatLngBounds.Builder builder, double distance) {
        LatLngBounds tmpBounds = builder.build();
        /** Add 2 points 1000m northEast and southWest of the center.
         * They increase the bounds only, if they are not already larger
         * than this.
         * 1000m on the diagonal translates into about 709m to each direction. */
        LatLng center = tmpBounds.getCenter();
        LatLng northEast = move(center, distance, distance);
        LatLng southWest = move(center, -distance, -distance);
        builder.include(southWest);
        builder.include(northEast);
        return builder.build();
    }

    private final double EARTHRADIUS = 6366198;
    /**
     * Create a new LatLng which lies toNorth meters north and toEast meters
     * east of startLL
     */
    private LatLng move(LatLng startLL, double toNorth, double toEast) {
        double lonDiff = meterToLongitude(toEast, startLL.latitude);
        double latDiff = meterToLatitude(toNorth);
        return new LatLng(startLL.latitude + latDiff, startLL.longitude
                + lonDiff);
    }

    private double meterToLongitude(double meterToEast, double latitude) {
        double latArc = Math.toRadians(latitude);
        double radius = Math.cos(latArc) * EARTHRADIUS;
        double rad = meterToEast / radius;
        return Math.toDegrees(rad);
    }


    private double meterToLatitude(double meterToNorth) {
        double rad = meterToNorth / EARTHRADIUS;
        return Math.toDegrees(rad);
    }

}
