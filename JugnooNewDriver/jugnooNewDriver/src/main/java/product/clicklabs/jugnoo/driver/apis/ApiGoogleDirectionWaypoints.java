package product.clicklabs.jugnoo.driver.apis;

import android.os.AsyncTask;
import android.text.TextUtils;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import product.clicklabs.jugnoo.driver.directions.GAPIDirections;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.google.GoogleRestApis;
import product.clicklabs.jugnoo.driver.utils.MapUtils;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by shankar on 5/31/16.
 */
public class ApiGoogleDirectionWaypoints extends AsyncTask<String, Integer, List<LatLng>>{

	private long engagementId;
	private String strOrigin = "", strDestination = "", strWaypoints = "", source;
	private LatLng latLngInit;
	private LatLng latLngDrop;
	private int pathColor;
	private Callback callback;

	public ApiGoogleDirectionWaypoints(long engagementId, ArrayList<LatLng> latLngs, int pathColor, boolean sortArray, String source, Callback callback){
		this.engagementId = engagementId;
		this.source = source;
		latLngInit = latLngs.get(0);
		if(sortArray) {
			Collections.sort(latLngs, new Comparator<LatLng>() {
				@Override
				public int compare(LatLng lhs, LatLng rhs) {
					if (latLngInit != null) {
						double distanceLhs = MapUtils.distance(latLngInit, lhs);
						double distanceRhs = MapUtils.distance(latLngInit, rhs);
						return (int) (distanceLhs - distanceRhs);
					}
					return 0;
				}
			});
		}

		StringBuilder sb = new StringBuilder();
		for(int i=0; i<latLngs.size(); i++){
			if(i == 0){
				strOrigin = latLngs.get(i).latitude+","+latLngs.get(i).longitude;
			} else if(i == latLngs.size()-1){
				strDestination = latLngs.get(i).latitude+","+latLngs.get(i).longitude;
				latLngDrop = latLngs.get(i);
			} else{
				sb.append("via:")
						.append(latLngs.get(i).latitude)
						.append("%2C")
						.append(latLngs.get(i).longitude)
						.append("%7C");
			}
		}
		strWaypoints = sb.toString();
		this.pathColor = pathColor;
		this.callback = callback;
	}


	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		callback.onPre();
	}

	@Override
	protected List<LatLng> doInBackground(String... params) {
		List<LatLng> list = null;
		try {
			Response response;
			if(!TextUtils.isEmpty(strWaypoints)) {
				response = GoogleRestApis.INSTANCE.getDirectionsWaypoints(strOrigin, strDestination, strWaypoints, source);
				String responseStr = new String(((TypedByteArray)response.getBody()).getBytes());
				list = MapUtils.getLatLngListFromPath(responseStr);
			} else {
				GAPIDirections.DirectionsResult directionsResult = GAPIDirections.INSTANCE.getDirectionsPathSync(engagementId, latLngInit, latLngDrop, source);
				list = directionsResult.getLatLngs();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}

	@Override
	protected void onPostExecute(List<LatLng> s) {
		super.onPostExecute(s);
		if(s != null) {
				try {
					if (callback.showPath()) {
						PolylineOptions polylineOptions = new PolylineOptions();
						polylineOptions.width(ASSL.Xscale() * 8).color(pathColor).geodesic(true);
						for (int z = 0; z < s.size(); z++) {
							polylineOptions.add(s.get(z));
						}
						callback.polylineOptionGenerated(polylineOptions);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
		}
		callback.onFinish();
	}


	public interface Callback{
		void onPre();
		boolean showPath();
		void polylineOptionGenerated(PolylineOptions polylineOptions);
		void onFinish();
	}

	//https://maps.googleapis.com/maps/api/directions/json?origin=30.7178599,76.8091295&destination=30.731916,76.747618&waypoints=via:30.759040%2C76.775368%7Cvia:%2030.720925%2C76.774135

}
