package product.clicklabs.jugnoo.driver.retrofit;

import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by aneeshbansal on 08/09/15.
 */
public interface GoogleAPIServices {

	@GET("/maps/api/directions/json")
	Response getDirections(@Query(value = "origin", encodeValue = false) String originLatLng,
						   @Query(value = "destination", encodeValue = false) String destLatLng,
						   @Query("sensor") Boolean sensor,
						   @Query("mode") String mode,
						   @Query("alternatives") Boolean alternatives,
						   @Query("client") String client,
						   @Query("channel") String  channel,
						   @Query(value = "signature", encodeValue = false) String signature);

	@GET("/maps/api/directions/json")
	Response getDirections(@Query(value = "origin", encodeValue = false) String originLatLng,
						   @Query(value = "destination", encodeValue = false) String destLatLng,
						   @Query("sensor") Boolean sensor,
						   @Query("mode") String mode,
						   @Query("alternatives") Boolean alternatives,
						   @Query("key") String key);

	@GET("/maps/api/distancematrix/json")
	Response getDistanceMatrix(@Query(value = "origins", encodeValue = false) String originLatLng,
							   @Query(value = "destinations", encodeValue = false) String destLatLng,
							   @Query("language") String language,
							   @Query("sensor") Boolean sensor,
							   @Query("alternatives") Boolean alternatives,
							   @Query("client") String client,
							   @Query("channel") String  channel,
							   @Query(value = "signature", encodeValue = false) String signature);
	@GET("/maps/api/distancematrix/json")
	Response getDistanceMatrix(@Query(value = "origins", encodeValue = false) String originLatLng,
							   @Query(value = "destinations", encodeValue = false) String destLatLng,
							   @Query("language") String language,
							   @Query("sensor") Boolean sensor,
							   @Query("alternatives") Boolean alternatives,
							   @Query("key") String key);


	@GET("/maps/api/geocode/json")
	Response geocode(@Query(value = "latlng", encodeValue = false) String latLng,
					 @Query("language") String language,
					 @Query("sensor") Boolean sensor,
					 @Query("client") String client,
					 @Query("channel") String  channel,
					 @Query(value = "signature", encodeValue = false) String signature);
	@GET("/maps/api/geocode/json")
	Response geocode(@Query(value = "latlng", encodeValue = false) String latLng,
					 @Query("language") String language,
					 @Query("sensor") Boolean sensor,
					 @Query("key") String key);

	@GET("/maps/api/directions/json")
	Response getDirectionsWaypoints(@Query(value = "origin", encodeValue = false) String originLatLng,
									@Query(value = "destination", encodeValue = false) String destLatLng,
									@Query(value = "waypoints", encodeValue = false) String waypoints,
									@Query("client") String client,
									@Query("channel") String  channel,
									@Query(value = "signature", encodeValue = false) String signature);

	@GET("/maps/api/directions/json")
	Response getDirectionsWaypoints(@Query(value = "origin", encodeValue = false) String originLatLng,
									@Query(value = "destination", encodeValue = false) String destLatLng,
									@Query(value = "waypoints", encodeValue = false) String waypoints,
									@Query("key") String key);


}
