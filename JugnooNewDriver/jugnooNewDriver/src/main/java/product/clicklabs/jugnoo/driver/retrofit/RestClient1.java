package product.clicklabs.jugnoo.driver.retrofit;

import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.retrofit.APIServices;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by aneeshbansal on 08/09/15.
 */
public class RestClient1 {
	private static APIServices API_SERVICES;

	public static void setupRestClient(String url) {

		RestAdapter.Log fooLog = new RestAdapter.Log() {
			@Override public void log(String message) {
			}
		};

		OkHttpClient okHttpClient = new OkHttpClient();
		okHttpClient.setReadTimeout(15, TimeUnit.SECONDS);
		okHttpClient.setConnectTimeout(15, TimeUnit.SECONDS);
		okHttpClient.setWriteTimeout(15, TimeUnit.SECONDS);
		okHttpClient.setRetryOnConnectionFailure(false);
		RestAdapter.Builder builder = new RestAdapter.Builder()
				.setEndpoint("https://"+url)
				.setClient(new OkClient(okHttpClient))
				.setLogLevel(RestAdapter.LogLevel.FULL)
				;

		RestAdapter restAdapter = builder.build();
		API_SERVICES = restAdapter.create(APIServices.class);
	}
	public static APIServices getApiServices() {
		return API_SERVICES;
	}

}
