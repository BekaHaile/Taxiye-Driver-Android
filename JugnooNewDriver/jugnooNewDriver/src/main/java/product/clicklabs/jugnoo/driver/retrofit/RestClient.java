package product.clicklabs.jugnoo.driver.retrofit;

import com.squareup.okhttp.ConnectionPool;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Protocol;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import product.clicklabs.jugnoo.driver.Data;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by aneeshbansal on 08/09/15.
 */
public class RestClient {
	private static APIServices API_SERVICES;
	private static GoogleAPIServices GOOGLE_API_SERVICES;

	static {
		setupRestClient();
		setupGoogleAPIRestClient();
	}

	private static OkHttpClient getOkHttpClient(){

		ArrayList<Protocol> protocolList = new ArrayList<>();
		protocolList.add(Protocol.HTTP_2);
		protocolList.add(Protocol.SPDY_3);
		protocolList.add(Protocol.HTTP_1_1);

		ConnectionPool connectionPool = new ConnectionPool(3, 5 * 60 * 1000);

		OkHttpClient okHttpClient = new OkHttpClient();
		okHttpClient.setConnectionPool(connectionPool);
		okHttpClient.setReadTimeout(15, TimeUnit.SECONDS);
		okHttpClient.setConnectTimeout(15, TimeUnit.SECONDS);
		okHttpClient.setWriteTimeout(15, TimeUnit.SECONDS);
		okHttpClient.setRetryOnConnectionFailure(false);
		okHttpClient.setProtocols(protocolList);

		return okHttpClient;
	}

	public static void setupRestClient() {

		RestAdapter.Log fooLog = new RestAdapter.Log() {
			@Override public void log(String message) {
			}
		};

		RestAdapter.Builder builder = new RestAdapter.Builder()
				.setEndpoint(Data.SERVER_URL)
				.setClient(new OkClient(getOkHttpClient()))
				.setLog(fooLog)
				.setLogLevel(RestAdapter.LogLevel.FULL)
				;

		RestAdapter restAdapter = builder.build();
		API_SERVICES = restAdapter.create(APIServices.class);
	}

	public static void setupRestClient(String url) {

		RestAdapter.Log fooLog = new RestAdapter.Log() {
			@Override public void log(String message) {
			}
		};

		RestAdapter.Builder builder = new RestAdapter.Builder()
				.setEndpoint(url)
				.setClient(new OkClient(getOkHttpClient()))
				.setLog(fooLog)
				.setLogLevel(RestAdapter.LogLevel.FULL)
				;

		RestAdapter restAdapter = builder.build();
		API_SERVICES = restAdapter.create(APIServices.class);
	}

	public static APIServices getApiServices() {
		return API_SERVICES;
	}





	public static void setupGoogleAPIRestClient() {

		RestAdapter.Log fooLog = new RestAdapter.Log() {
			@Override public void log(String message) {
			}
		};

		RestAdapter.Builder builder = new RestAdapter.Builder()
				.setEndpoint("http://maps.googleapis.com/maps/api")
				.setClient(new OkClient(getOkHttpClient()))
				.setLog(fooLog)
				.setLogLevel(RestAdapter.LogLevel.FULL)
				;

		RestAdapter restAdapter = builder.build();
		GOOGLE_API_SERVICES = restAdapter.create(GoogleAPIServices.class);
	}

	public static GoogleAPIServices getGoogleApiServices() {
		return GOOGLE_API_SERVICES;
	}

}
