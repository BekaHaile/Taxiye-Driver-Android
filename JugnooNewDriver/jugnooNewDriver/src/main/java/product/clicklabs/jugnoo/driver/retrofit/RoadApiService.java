package product.clicklabs.jugnoo.driver.retrofit;

import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Query;

public interface RoadApiService {

	@GET("/v1/snapToRoads")
	Response snapToRoads(@Query(value = "path", encodeValue = false) String path,
									@Query("client") String client,
									@Query("channel") String  channel,
									@Query(value = "signature", encodeValue = false) String signature);

	@GET("/v1/snapToRoads")
	Response snapToRoads(@Query(value = "path", encodeValue = false) String path,
									@Query("key") String key);

}
