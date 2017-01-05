package product.clicklabs.jugnoo.driver.retrofit;

import com.jakewharton.retrofit.Ok3Client;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.retrofit.model.PushAckAPIService;
import product.clicklabs.jugnoo.driver.utils.Log;
import retrofit.RestAdapter;

/**
 * Created by aneeshbansal on 08/09/15.
 */
public class RestClient {
	private static final String TAG = RestClient.class.getSimpleName();
	private static String CURRENT_URL;
	private static APIServices API_SERVICES;
	private static DirectionAPIService DISTANCE_API_SERVICE;
	private static GoogleAPIServices GOOGLE_API_SERVICES;
	private static PushAckAPIService PUSH_ACK_API_SERVICE;
	private static ChatAckAPIService CHAT_ACK_API_SERVICE;

	static {
		setupRestClient();
		setupGoogleAPIRestClient();
		setupDistanceAPIRestClient();
		setupPushAckAPIRestClient();
		setupChatAPIRestClient();
	}

	private static OkHttpClient getOkHttpClient(){

		ArrayList<Protocol> protocolList = new ArrayList<>();
		protocolList.add(Protocol.HTTP_2);
		protocolList.add(Protocol.SPDY_3);
		protocolList.add(Protocol.HTTP_1_1);

		ConnectionPool connectionPool = new ConnectionPool(3, 5 * 60 * 1000, TimeUnit.MILLISECONDS);

		OkHttpClient.Builder builder = new OkHttpClient.Builder();
		builder.connectionPool(connectionPool);
		builder.readTimeout(15, TimeUnit.SECONDS);
		builder.connectTimeout(15, TimeUnit.SECONDS);
		builder.writeTimeout(15, TimeUnit.SECONDS);
		builder.retryOnConnectionFailure(false);
		builder.protocols(protocolList);

		return builder.build();
	}

	public static void setupRestClient() {

		RestAdapter.Log fooLog = new RestAdapter.Log() {
			@Override public void log(String message) {
			}
		};

		RestAdapter.Builder builder = new RestAdapter.Builder()
				.setEndpoint(Data.SERVER_URL)
				.setClient(new Ok3Client(getOkHttpClient()))
				.setLog(fooLog)
				.setLogLevel(RestAdapter.LogLevel.FULL)
				;

		RestAdapter restAdapter = builder.build();
		API_SERVICES = restAdapter.create(APIServices.class);
		CURRENT_URL = Data.SERVER_URL;
	}

	public static void setupRestClient(String url) {
		if (!url.equalsIgnoreCase(CURRENT_URL)) {
			RestAdapter.Log fooLog = new RestAdapter.Log() {
				@Override
				public void log(String message) {
				}
			};

			RestAdapter.Builder builder = new RestAdapter.Builder()
					.setEndpoint(url)
					.setClient(new Ok3Client(getOkHttpClient()))
					.setLog(fooLog)
					.setLogLevel(RestAdapter.LogLevel.FULL);

			RestAdapter restAdapter = builder.build();
			API_SERVICES = restAdapter.create(APIServices.class);
			CURRENT_URL = url;
			Log.i(TAG, "setupRestClient");
		}
	}
	public static APIServices getApiServices() {
		return API_SERVICES;
	}

	public static void setCurrentUrl(String url){
		CURRENT_URL = url;
	}





	public static void setupGoogleAPIRestClient() {

		RestAdapter.Log fooLog = new RestAdapter.Log() {
			@Override public void log(String message) {
			}
		};

		RestAdapter.Builder builder = new RestAdapter.Builder()
				.setEndpoint("http://maps.googleapis.com/maps/api")
				.setClient(new Ok3Client(getOkHttpClient()))
				.setLog(fooLog)
				.setLogLevel(RestAdapter.LogLevel.FULL);


		RestAdapter restAdapter = builder.build();
		GOOGLE_API_SERVICES = restAdapter.create(GoogleAPIServices.class);
	}

	public static GoogleAPIServices getGoogleApiServices() {
		return GOOGLE_API_SERVICES;
	}


	public static void setupDistanceAPIRestClient() {

		RestAdapter.Log fooLog = new RestAdapter.Log() {
			@Override public void log(String message) {
			}
		};

		RestAdapter.Builder builder = new RestAdapter.Builder()
				.setEndpoint("https://route.jugnoo.in/route/v1")
				.setClient(new Ok3Client(getOkHttpClient()))
				.setLog(fooLog)
				.setLogLevel(RestAdapter.LogLevel.FULL);

		RestAdapter restAdapter = builder.build();
		DISTANCE_API_SERVICE = restAdapter.create(DirectionAPIService.class);
	}

	public static DirectionAPIService getDistanceApiServices() {
		return DISTANCE_API_SERVICE;
	}

	public static void setupPushAckAPIRestClient() {

		RestAdapter.Log fooLog = new RestAdapter.Log() {
			@Override public void log(String message) {
			}
		};

		RestAdapter.Builder builder = new RestAdapter.Builder()
				.setEndpoint("https://marketing-api.jugnoo.in")
				.setClient(new Ok3Client(getOkHttpClient()))
				.setLog(fooLog)
				.setLogLevel(RestAdapter.LogLevel.FULL);

		RestAdapter restAdapter = builder.build();
		PUSH_ACK_API_SERVICE = restAdapter.create(PushAckAPIService.class);
	}

	public static PushAckAPIService getPushAckApiServices() {
		return PUSH_ACK_API_SERVICE;
	}

	public static void setupChatAPIRestClient() {

		RestAdapter.Log fooLog = new RestAdapter.Log() {
			@Override public void log(String message) {
			}
		};

		RestAdapter.Builder builder = new RestAdapter.Builder()
				.setEndpoint("https://test.jugnoo.in:8095")
//				.setEndpoint("https://prod-autos-api.jugnoo.in:4010")
				.setClient(new Ok3Client(getOkHttpClient()))
				.setLog(fooLog)
				.setLogLevel(RestAdapter.LogLevel.FULL);

		RestAdapter restAdapter = builder.build();
		CHAT_ACK_API_SERVICE = restAdapter.create(ChatAckAPIService.class);
	}

	public static ChatAckAPIService getChatAckApiServices() {
		return CHAT_ACK_API_SERVICE;
	}
}
