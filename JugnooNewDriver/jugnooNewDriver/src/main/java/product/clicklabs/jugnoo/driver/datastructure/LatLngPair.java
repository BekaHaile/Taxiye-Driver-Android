package product.clicklabs.jugnoo.driver.datastructure;

import com.google.android.gms.maps.model.LatLng;

import product.clicklabs.jugnoo.driver.utils.Utils;

public class LatLngPair {

    public LatLng source, destination;
    public double deltaDistance;

    public LatLngPair(LatLng source, LatLng destination, double deltaDistance) {
        this.source = source;
        this.destination = destination;
        this.deltaDistance = deltaDistance;
    }

    @Override
    public boolean equals(Object o) {
        try {
            LatLngPair matchO = (LatLngPair) o;
            if (((Utils.compareDouble(matchO.source.latitude, this.source.latitude) == 0)
                    && (Utils.compareDouble(matchO.source.longitude, this.source.longitude) == 0)) ||
                    ((Utils.compareDouble(matchO.destination.latitude, this.destination.latitude) == 0)
                            && (Utils.compareDouble(matchO.destination.longitude, this.destination.longitude) == 0))) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String toString() {
        return source + " " + destination + " " + deltaDistance;
    }

}
