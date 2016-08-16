package product.clicklabs.jugnoo.driver.retrofit;

import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by aneeshbansal on 16/08/16.
 */
public interface DirectionAPIService {

	@GET("/driving/{query}")
	Response getDistance(@Path("query") String originLatLng);


}
