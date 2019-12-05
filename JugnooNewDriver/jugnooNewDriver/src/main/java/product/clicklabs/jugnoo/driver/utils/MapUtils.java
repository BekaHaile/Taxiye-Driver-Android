package product.clicklabs.jugnoo.driver.utils;

import android.location.Location;
import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.driver.MyApplication;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.google.AddressComponent;
import product.clicklabs.jugnoo.driver.google.GAPIAddress;
import product.clicklabs.jugnoo.driver.google.GoogleGeocodeResponse;

public class MapUtils {

	
	public static double distance(LatLng start, LatLng end) {
		try {
			Location location1 = new Location("locationA");
			location1.setLatitude(start.latitude);
			location1.setLongitude(start.longitude);
			Location location2 = new Location("locationA");
			location2.setLatitude(end.latitude);
			location2.setLongitude(end.longitude);

			double distance = location1.distanceTo(location2);
			return distance;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static double distance(Location start, Location end) {
		try {
			double distance = start.distanceTo(end);
			return distance;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static double speed(Location start, Location end){
		double speedMPS = 0;
		if(start != null && end != null) {
			long secondsDiff = Math.abs((end.getTime() - start.getTime()) / 1000);
			double displacement = MapUtils.distance(start, end);
			if (secondsDiff > 0) {
				speedMPS = displacement / secondsDiff;
			}
		}
		return speedMPS;
	}
	
	
	//http://maps.googleapis.com/maps/api/directions/json?origin=30.7342187,76.78088307&destination=30.74571777,76.78635478&sensor=false&mode=driving&alternatives=false

	//http://maps.googleapis.com/maps/api/distancematrix/json?origins=30.75,76.78&destinations=30.78,76.79&language=EN&sensor=false

	
	
	public static List<LatLng> decodeDirectionsPolyline(String encoded) {

	    List<LatLng> poly = new ArrayList<LatLng>();
	    int index = 0, len = encoded.length();
	    int lat = 0, lng = 0;

	    while (index < len) {
	        int bInt, shift = 0, result = 0;
	        do {
	            bInt = encoded.charAt(index++) - 63;
	            result |= (bInt & 0x1f) << shift;
	            shift += 5;
	        } while (bInt >= 0x20);
	        int dlat = ((result & 1) == 0 ? (result >> 1) : ~(result >> 1));
	        lat += dlat;

	        shift = 0;
	        result = 0;
	        do {
	            bInt = encoded.charAt(index++) - 63;
	            result |= (bInt & 0x1f) << shift;
	            shift += 5;
	        } while (bInt >= 0x20);
	        int dlng = ((result & 1) == 0 ? (result >> 1) : ~(result >> 1));
	        lng += dlng;

	        LatLng pLatLng = new LatLng( (((double) lat / 1E5)),
	                 (((double) lng / 1E5) ));
	        poly.add(pLatLng);
	    }
	    return poly;
	}
	
	public static List<LatLng> getLatLngListFromPath(String result){
		try {
	    	 final JSONObject json = new JSONObject(result);
		     JSONArray routeArray = json.getJSONArray("routes");
		     JSONObject routes = routeArray.getJSONObject(0);
		     JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
		     String encodedString = overviewPolylines.getString("points");
		     List<LatLng> list = MapUtils.decodeDirectionsPolyline(encodedString);
		     return list;
	    } 
	    catch (Exception e) {
	    	e.printStackTrace();
	    	return new ArrayList<LatLng>();
	    }
	}

	public static List<LatLng> getLatLngListFromPathJungle(String result){
		try {
			final JSONObject json = new JSONObject(result);
			JSONArray routeArray = json.getJSONObject("data").getJSONArray("paths");
			JSONObject routes = routeArray.getJSONObject(0);
			String encodedString = routes.getString("points");
			List<LatLng> list = MapUtils.decodeDirectionsPolyline(encodedString);
			return list;
		}
		catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
	
	
	


	public static GAPIAddress parseGAPIIAddress(GoogleGeocodeResponse googleGeocodeResponse){
		GAPIAddress fullAddress = new GAPIAddress("Unnamed");
		try {
			String formatStr = MyApplication.getInstance().getString(R.string.geocode_address_format);
			StringBuilder addressSB = new StringBuilder();
			if((googleGeocodeResponse.getStatus() != null && googleGeocodeResponse.getStatus().equalsIgnoreCase("OK"))
					|| (googleGeocodeResponse.getResults() != null && googleGeocodeResponse.getResults().size() > 0)) {
				String addressComparator = "";
				for (int i = googleGeocodeResponse.getResults().get(0).getAddressComponents().size()-1; i >= 0; i--) {
					AddressComponent addressComponent = googleGeocodeResponse.getResults().get(0).getAddressComponents().get(i);
					if(addressComparator.contains(addressComponent.getLongName())){
						addressComponent.setRedundant(true);
					} else {
						addressComparator = addressComparator + addressComponent.getLongName() + ",";
					}
				}

				String address = "";
				Log.e("MapUtils", "getPlaceById from poi response="+googleGeocodeResponse.getResults().get(0).getPlaceName());
				if(!TextUtils.isEmpty(googleGeocodeResponse.getResults().get(0).getPlaceName())){
					addressSB.append(googleGeocodeResponse.getResults().get(0).getPlaceName()).append(", ");
				}
				if(!TextUtils.isEmpty(formatStr) && formatStr.contains(",")) {
					String format[] = formatStr.split(",");
					for (String formatI : format) {
						for (AddressComponent addressComponent : googleGeocodeResponse.getResults().get(0).getAddressComponents()) {
							if(addressComponent.getRedundant()){
								continue;
							}
							for(String type : addressComponent.getTypes()){
								if (type.contains(formatI) && !addressSB.toString().contains(addressComponent.getLongName())) {
									addressSB.append(addressComponent.getLongName()).append(", ");
									break;
								}
							}
						}
					}
					if(addressSB.length() > 2 && googleGeocodeResponse.getResults().get(0).getAddressComponents().size() > 4) {
						address = addressSB.toString().substring(0, addressSB.length() - 2);
					}
				}
				if(TextUtils.isEmpty(address)){
					address = googleGeocodeResponse.getResults().get(0).getFormatted_address();
				}
				Log.e("addressSB", "===="+address);

				fullAddress = new GAPIAddress(address);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fullAddress;
	}


}
