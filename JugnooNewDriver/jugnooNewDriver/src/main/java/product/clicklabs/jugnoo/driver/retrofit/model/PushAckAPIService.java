package product.clicklabs.jugnoo.driver.retrofit.model;

import org.json.JSONObject;

import java.util.Map;

import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by aneeshbansal on 16/08/16.
 */
public interface PushAckAPIService {

	@FormUrlEncoded
	@POST("/push/acknowledge")
	Response sendPushAckToServerRetro(@FieldMap Map<String, String> params);


}
