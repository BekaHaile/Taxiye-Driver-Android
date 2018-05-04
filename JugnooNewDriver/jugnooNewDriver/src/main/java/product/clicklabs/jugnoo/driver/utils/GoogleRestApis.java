package product.clicklabs.jugnoo.driver.utils;

import android.util.Base64;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import product.clicklabs.jugnoo.driver.BuildConfig;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import retrofit.client.Response;

public class GoogleRestApis {

	public static Response getDirections(String originLatLng, String destLatLng, Boolean sensor,
										 String mode, Boolean alternatives){
		String urlToSign = "/maps/api/directions/json?" +
				"origin="+originLatLng
				+"&destination="+destLatLng
				+"&sensor="+sensor
				+"&mode="+mode
				+"&alternatives="+alternatives
				+"&client="+BuildConfig.MAPS_CLIENT;
		String googleSignature = null;
		try {
			googleSignature = generateGoogleSignature(urlToSign);
		} catch (Exception ignored) {}

		return RestClient.getGoogleApiServices().getDirections(originLatLng, destLatLng,
				sensor, mode, alternatives, BuildConfig.MAPS_CLIENT, googleSignature);
	}

	public static Response getDistanceMatrix(String originLatLng, String destLatLng, String language,
											 Boolean sensor, Boolean alternatives){
		String urlToSign = "/maps/api/distancematrix/json?" +
				"origins="+originLatLng
				+"&destinations="+destLatLng
				+"&language="+language
				+"&sensor="+sensor
				+"&alternatives="+alternatives
				+"&client="+BuildConfig.MAPS_CLIENT;
		String googleSignature = null;
		try {
			googleSignature = generateGoogleSignature(urlToSign);
		} catch (Exception ignored) {}

		return RestClient.getGoogleApiServices().getDistanceMatrix(originLatLng, destLatLng, language,
				sensor, alternatives, BuildConfig.MAPS_CLIENT, googleSignature);
	}

	public static Response geocode(String latLng, String language){
		String urlToSign = "/maps/api/geocode/json?" +
				"latlng="+latLng
				+"&language="+language
				+"&sensor="+false
				+"&client="+BuildConfig.MAPS_CLIENT;
		String googleSignature = null;
		try {
			googleSignature = generateGoogleSignature(urlToSign);
		} catch (Exception ignored) {}

		return RestClient.getGoogleApiServices().geocode(latLng, language, false, BuildConfig.MAPS_CLIENT, googleSignature);
	}


	public static Response getDirectionsWaypoints(String strOrigin, String strDestination, String strWaypoints){
		String urlToSign = "/maps/api/directions/json?" +
				"origin="+strOrigin
				+"&destination="+strDestination
				+"&waypoints="+strWaypoints
				+"&client="+BuildConfig.MAPS_CLIENT;
		String googleSignature = null;
		try {
			googleSignature = generateGoogleSignature(urlToSign);
		} catch (Exception ignored) {}


		return RestClient.getGoogleApiServices().getDirectionsWaypoints(strOrigin, strDestination,
				strWaypoints, BuildConfig.MAPS_CLIENT, googleSignature);
	}

	private static String generateGoogleSignature(String urlToSign) throws NoSuchAlgorithmException,
			InvalidKeyException {

		// Convert the key from 'web safe' base 64 to binary
		String keyString = BuildConfig.MAPS_PRIVATE_KEY;
		keyString = keyString.replace('-', '+');
		keyString = keyString.replace('_', '/');
		// Base64 is JDK 1.8 only - older versions may need to use Apache Commons or similar.
		byte[] key = Base64.decode(keyString, Base64.DEFAULT);


		SecretKeySpec sha1Key = new SecretKeySpec(key, "HmacSHA1");

		// Get an HMAC-SHA1 Mac instance and initialize it with the HMAC-SHA1 key
		Mac mac = Mac.getInstance("HmacSHA1");
		mac.init(sha1Key);

		// compute the binary signature for the request
		byte[] sigBytes = mac.doFinal(urlToSign.getBytes());

		// base 64 encode the binary signature
		// Base64 is JDK 1.8 only - older versions may need to use Apache Commons or similar.
		String signature = Base64.encodeToString(sigBytes, Base64.DEFAULT);

		// convert the signature to 'web safe' base 64
		signature = signature.replace('+', '-');
		signature = signature.replace('/', '_');

		return signature;
	}

}
