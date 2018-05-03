package product.clicklabs.jugnoo.driver.retrofit;

import product.clicklabs.jugnoo.driver.BuildConfig;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by aneeshbansal on 08/09/15.
 */
public interface GoogleAPIServices {

	// sensor=false&mode=driving&alternatives=false
	@GET("/directions/json?key=" + BuildConfig.MAPS_API_KEY)
	Response getDirections(@Query("origin") String originLatLng,
						   @Query("destination") String destLatLng,
						   @Query("sensor") Boolean sensor,
						   @Query("mode") String mode,
						   @Query("alternatives") Boolean alternatives);

	// language=EN&sensor=false&alternatives=false
	@GET("/distancematrix/json?key=" + BuildConfig.MAPS_API_KEY)
	Response getDistanceMatrix(@Query("origins") String originLatLng,
							   @Query("destinations") String destLatLng,
							   @Query("language") String language,
							   @Query("sensor") Boolean sensor,
							   @Query("alternatives") Boolean alternatives);


	@GET("/geocode/json?key=" + BuildConfig.MAPS_API_KEY)
	Response geocode(@Query("latlng") String latLng,
					 @Query("language") String language,
					 @Query("sensor") Boolean sensor);

	@GET("/directions/json?key=" + BuildConfig.MAPS_API_KEY)
	Response getDirectionsWaypoints(@Query("origin") String originLatLng,
						   			@Query("destination") String destLatLng,
						   			@Query(value = "waypoints", encodeValue = false) String waypoints);

//	,
//	@Query("sensor") Boolean sensor,
//	@Query("mode") String mode,
//	@Query("alternatives") Boolean alternatives

}
