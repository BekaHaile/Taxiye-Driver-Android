package product.clicklabs.jugnoo.driver.retrofit;

import java.util.Map;

import retrofit.client.Response;
import retrofit.http.EncodedQuery;
import retrofit.http.GET;
import retrofit.http.Query;
import retrofit.http.QueryMap;

/**
 * Created by aneeshbansal on 08/09/15.
 */
public interface GoogleAPIServices {

	// sensor=false&mode=driving&alternatives=false
	@GET("/directions/json")
	Response getDirections(@Query("origin") String originLatLng,
						   @Query("destination") String destLatLng,
						   @Query("sensor") Boolean sensor,
						   @Query("mode") String mode,
						   @Query("alternatives") Boolean alternatives);

	// language=EN&sensor=false&alternatives=false
	@GET("/distancematrix/json")
	Response getDistanceMatrix(@Query("origins") String originLatLng,
							   @Query("destinations") String destLatLng,
							   @Query("language") String language,
							   @Query("sensor") Boolean sensor,
							   @Query("alternatives") Boolean alternatives);


	@GET("/geocode/json")
	Response geocode(@Query("latlng") String latLng,
					 @Query("language") String language,
					 @Query("sensor") Boolean sensor);

	@GET("/directions/json")
	Response getDirectionsWaypoints(@Query("origin") String originLatLng,
						   @Query("destination") String destLatLng,
						   @Query(value = "waypoints", encodeValue = false) String waypoints);

	@GET("/directions/json")
	Response getDirectionsWaypoints(@QueryMap(encodeNames = true) Map<String, String> params);

}
