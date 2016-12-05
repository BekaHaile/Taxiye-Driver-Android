package product.clicklabs.jugnoo.driver.retrofit;

import java.util.Map;

import product.clicklabs.jugnoo.driver.retrofit.model.FetchChatResponse;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by aneeshbansal on 16/08/16.
 */
public interface ChatAckAPIService {

	@FormUrlEncoded
	@POST("/chat/v1/fetch_chat")
	void fetchChat(@FieldMap Map<String, String> params,
				   Callback<FetchChatResponse> callback);

	@FormUrlEncoded
	@POST("/chat/v1/post_chat")
	void postChat(@FieldMap Map<String, String> params,
				  Callback<FetchChatResponse> callback);


}
