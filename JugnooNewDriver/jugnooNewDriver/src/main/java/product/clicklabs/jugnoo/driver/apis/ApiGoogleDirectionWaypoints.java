package product.clicklabs.jugnoo.driver.apis;

import android.os.AsyncTask;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.MapUtils;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by shankar on 5/31/16.
 */
public class ApiGoogleDirectionWaypoints extends AsyncTask<String, Integer, String>{

	private String strOrigin = "", strDestination = "", strWaypoints = "";
	private int pathColor;
	private GoogleMap map;
	private Callback callback;

	public ApiGoogleDirectionWaypoints(ArrayList<LatLng> latLngs, int pathColor, GoogleMap map, Callback callback){
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<latLngs.size(); i++){
			if(i == 0){
				strOrigin = latLngs.get(i).latitude+","+latLngs.get(i).longitude;
			} else if(i == latLngs.size()-1){
				strDestination = latLngs.get(i).latitude+","+latLngs.get(i).longitude;
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
		this.map = map;
		this.callback = callback;
	}


	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		callback.onPre();
	}

	@Override
	protected String doInBackground(String... params) {
		try {
			Response response = RestClient.getGoogleApiServices().getDirectionsWaypoints(strOrigin, strDestination, strWaypoints);
			return new String(((TypedByteArray)response.getBody()).getBytes());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute(String s) {
		super.onPostExecute(s);
		Polyline polyline = null;
		if(s != null) {
			List<LatLng> list = MapUtils.getLatLngListFromPath(s);
			if (list.size() > 0) {
				try {
					if (callback.showPath()) {
						PolylineOptions polylineOptions = new PolylineOptions();
						polylineOptions.width(ASSL.Xscale() * 5).color(pathColor).geodesic(true);
						for (int z = 0; z < list.size(); z++) {
							polylineOptions.add(list.get(z));
						}
						polyline = map.addPolyline(polylineOptions);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		callback.polylineAdded(polyline);
	}


	public interface Callback{
		void onPre();
		boolean showPath();
		void polylineAdded(Polyline polyline);
	}

	//https://maps.googleapis.com/maps/api/directions/json?origin=30.7178599,76.8091295&destination=30.731916,76.747618&waypoints=via:30.759040%2C76.775368%7Cvia:%2030.720925%2C76.774135

}
