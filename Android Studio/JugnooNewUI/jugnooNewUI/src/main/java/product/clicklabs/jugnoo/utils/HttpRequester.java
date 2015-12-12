package product.clicklabs.jugnoo.utils;

import org.apache.http.NameValuePair;

import java.util.ArrayList;

import product.clicklabs.jugnoo.config.Config;

public class HttpRequester {

	public static String SERVER_TIMEOUT = "SERVER_TIMEOUT";
	public static int TIMEOUT_CONNECTION = 30000, TIMEOUT_SOCKET = 30000, RETRY_COUNT = 0, SLEEP_BETWEEN_RETRY = 0;

	// constructor
	public HttpRequester() {
		SERVER_TIMEOUT = "SERVER_TIMEOUT";
	}

	
	public String getJSONFromUrl(String url){
		
		return Config.getHttpRequester().getJSONFromUrlFinal(url);
	}
	
	
	public String getJSONFromUrlParams(String url, ArrayList<NameValuePair> nameValuePairs){
		
		return Config.getHttpRequester().getJSONFromUrlParamsFinal(url, nameValuePairs);
	}

	
}



