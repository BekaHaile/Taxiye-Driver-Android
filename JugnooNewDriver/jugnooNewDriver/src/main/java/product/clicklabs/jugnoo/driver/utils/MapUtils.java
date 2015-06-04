package product.clicklabs.jugnoo.driver.utils;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.datastructure.SearchResult;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

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
	
	
	//http://maps.googleapis.com/maps/api/directions/json?origin=30.7342187,76.78088307&destination=30.74571777,76.78635478&sensor=false&mode=driving&alternatives=false
	public static String makeDirectionsURL(LatLng source, LatLng destination) {
		StringBuilder urlString = new StringBuilder();
		urlString.append("http://maps.googleapis.com/maps/api/directions/json");
		urlString.append("?origin=");// from
		urlString.append(Double.toString(source.latitude));
		urlString.append(",");
		urlString.append(Double.toString(source.longitude));
		urlString.append("&destination=");// to
		urlString.append(Double.toString(destination.latitude));
		urlString.append(",");
		urlString.append(Double.toString(destination.longitude));
		urlString.append("&sensor=false&mode=driving&alternatives=false");
		return urlString.toString();
	}
	
	
	//https://maps.googleapis.com/maps/api/distancematrix/json?origins=30.75,76.78&destinations=30.78,76.79&language=EN&sensor=false
	
	public static String makeDistanceMatrixURL(LatLng source, LatLng destination){
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/distancematrix/json");
        urlString.append("?origins=");// from
        urlString.append(Double.toString(source.latitude));
        urlString.append(",");
        urlString
                .append(Double.toString(source.longitude));
        urlString.append("&destinations=");// to
        urlString
                .append(Double.toString(destination.latitude));
        urlString.append(",");
        urlString.append(Double.toString(destination.longitude));
        urlString.append("&language=EN&sensor=false&alternatives=false");
        return urlString.toString();
	}
	
	
	
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
	
	
	
	
	//http://maps.googleapis.com/maps/api/geocode/json?latlng=30.75,76.75
	public static String getGAPIAddress(LatLng latLng) {
		String fullAddress = "Unnamed";
		try {

			JSONObject jsonObj = new JSONObject(
					new HttpRequester().getJSONFromUrl("http://maps.googleapis.com/maps/api/geocode/json?"
									+ "latlng="
									+ latLng.latitude
									+ ","
									+ latLng.longitude + "&sensor=true"));

			
			String status = jsonObj.getString("status");
			if (status.equalsIgnoreCase("OK")) {
				JSONArray Results = jsonObj.getJSONArray("results");
				JSONObject zero = Results.getJSONObject(0);

				String streetNumber = "", route = "", subLocality2 = "", subLocality1 = "", locality = "", administrativeArea2 = "", administrativeArea1 = "", country = "", postalCode = "";

				if (zero.has("address_components")) {
					try {

						ArrayList<String> selectedAddressComponentsArr = new ArrayList<String>();
						JSONArray addressComponents = zero.getJSONArray("address_components");

						for (int i = 0; i < addressComponents.length(); i++) {

							JSONObject iObj = addressComponents.getJSONObject(i);
							JSONArray jArr = iObj.getJSONArray("types");

							ArrayList<String> addressTypes = new ArrayList<String>();
							for (int j = 0; j < jArr.length(); j++) {
								addressTypes.add(jArr.getString(j));
							}

							if ("".equalsIgnoreCase(streetNumber) && addressTypes.contains("street_number")) {
								streetNumber = iObj.getString("long_name");
								if (!"".equalsIgnoreCase(streetNumber) && !selectedAddressComponentsArr.toString().contains(streetNumber)) {
									selectedAddressComponentsArr.add(streetNumber);
								}
							}
							if ("".equalsIgnoreCase(route) && addressTypes.contains("route")) {
								route = iObj.getString("long_name");
								if (!"".equalsIgnoreCase(route) && !selectedAddressComponentsArr.toString().contains(route)) {
									selectedAddressComponentsArr.add(route);
								}
							}
							if ("".equalsIgnoreCase(subLocality2) && addressTypes.contains("sublocality_level_2")) {
								subLocality2 = iObj.getString("long_name");
								if (!"".equalsIgnoreCase(subLocality2) && !selectedAddressComponentsArr.toString().contains(subLocality2)) {
									selectedAddressComponentsArr.add(subLocality2);
								}
							}
							if ("".equalsIgnoreCase(subLocality1) && addressTypes.contains("sublocality_level_1")) {
								subLocality1 = iObj.getString("long_name");
								if (!"".equalsIgnoreCase(subLocality1) && !selectedAddressComponentsArr.toString().contains(subLocality1)) {
									selectedAddressComponentsArr.add(subLocality1);
								}
							}
							if ("".equalsIgnoreCase(locality) && addressTypes.contains("locality")) {
								locality = iObj.getString("long_name");
								if (!"".equalsIgnoreCase(locality) && !selectedAddressComponentsArr.toString().contains(locality)) {
									selectedAddressComponentsArr.add(locality);
								}
							}
							if ("".equalsIgnoreCase(administrativeArea2) && addressTypes.contains("administrative_area_level_2")) {
								administrativeArea2 = iObj.getString("long_name");
								if (!"".equalsIgnoreCase(administrativeArea2) && !selectedAddressComponentsArr.toString().contains(administrativeArea2)) {
									selectedAddressComponentsArr.add(administrativeArea2);
								}
							}
							if ("".equalsIgnoreCase(administrativeArea1) && addressTypes.contains("administrative_area_level_1")) {
								administrativeArea1 = iObj.getString("long_name");
								if (!"".equalsIgnoreCase(administrativeArea1) && !selectedAddressComponentsArr.toString().contains(administrativeArea1)) {
									selectedAddressComponentsArr.add(administrativeArea1);
								}
							}
							if ("".equalsIgnoreCase(country) && addressTypes.contains("country")) {
								country = iObj.getString("long_name");
								if (!"".equalsIgnoreCase(country) && !selectedAddressComponentsArr.toString().contains(country)) {
									selectedAddressComponentsArr.add(country);
								}
							}
							if ("".equalsIgnoreCase(postalCode) && addressTypes.contains("postal_code")) {
								postalCode = iObj.getString("long_name");
								if (!"".equalsIgnoreCase(postalCode) && !selectedAddressComponentsArr.toString().contains(postalCode)) {
									selectedAddressComponentsArr.add(postalCode);
								}
							}
						}

						fullAddress = "";
						if (selectedAddressComponentsArr.size() > 0) {
							for (int i = 0; i < selectedAddressComponentsArr.size(); i++) {
								if (i < selectedAddressComponentsArr.size() - 1) {
									fullAddress = fullAddress + selectedAddressComponentsArr.get(i) + ", ";
								} else {
									fullAddress = fullAddress + selectedAddressComponentsArr.get(i);
								}
							}
						} else {
							fullAddress = zero.getString("formatted_address");
						}

					} catch (Exception e) {
						e.printStackTrace();
						fullAddress = zero.getString("formatted_address");
					}
				} else {
					fullAddress = zero.getString("formatted_address");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return fullAddress;
	}
	
	
	
	/**
	 * To search addresses related to particular address available on google
	 */
	public static ArrayList<SearchResult> getSearchResultsFromGooglePlaces(String searchText) {
		ArrayList<SearchResult> searchResults = new ArrayList<SearchResult>();
		try{
			String ignr2 = "https://maps.googleapis.com/maps/api/place/textsearch/json?location="
					+ "30.75"
					+ ","
					+ "76.78"
					+ "&radius=50"
					+ "&query="
					+ searchText
					+ "&sensor=true&key="+Data.MAPS_BROWSER_KEY;
			// "https://maps.googleapis.com/maps/api/place/textsearch/json?location=%f,%f&radius=2bb0000&query=%@&sensor=true&key=%@";

			ignr2 = ignr2.replaceAll(" ", "%20");
			
			JSONObject jsonObj = new JSONObject(new HttpRequester().getJSONFromUrl(ignr2));

			Log.writeLogToFile("search", jsonObj.toString());
			
			JSONArray info = null;
			info = jsonObj.getJSONArray("results");
			for (int i = 0; i < info.length(); i++) {
				SearchResult searchResult = new SearchResult(info.getJSONObject(i).getString("name"), 
						info.getJSONObject(i).getString("formatted_address"),
						new LatLng(info.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lat"),
								info.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lng")));

				searchResults.add(searchResult);
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		return searchResults;
	}
	
}
