package product.clicklabs.jugnoo.driver.apis;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import product.clicklabs.jugnoo.driver.directions.JungleApisImpl;
import product.clicklabs.jugnoo.driver.utils.ASSL;

/**
 * Created by shankar on 5/31/16.
 */
public class ApiGoogleDirectionWaypoints extends AsyncTask<String, Integer, List<LatLng>>{

	private long engagementId;
	private String source;
	private LatLng latLngInit;
	private LatLng latLngDrop;
	private int pathColor;
	private Callback callback;
	private ArrayList<LatLng> waypoints;

	public ApiGoogleDirectionWaypoints(long engagementId, ArrayList<LatLng> latLngs, int pathColor, String source, Callback callback){
		this.engagementId = engagementId;
		this.source = source;
		this.waypoints = new ArrayList<>();

		for(int i=0; i<latLngs.size(); i++){
			if(i == 0){
				latLngInit = latLngs.get(i);
			} else if(i == latLngs.size()-1){
				latLngDrop = latLngs.get(i);
			} else{
				waypoints.add(latLngs.get(i));
			}
		}
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
			JungleApisImpl.DirectionsResult directionsResult;
			if(waypoints.size() > 0) {
				directionsResult = JungleApisImpl.INSTANCE.getDirectionsWaypointsPathSync(engagementId, latLngInit, latLngDrop, waypoints, source, false);
			} else {
				directionsResult = JungleApisImpl.INSTANCE.getDirectionsPathSync(engagementId, latLngInit, latLngDrop, source, false);
			}
			list = directionsResult.getLatLngs();
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
						polylineOptions.width(ASSL.Xscale() * 4).color(pathColor).geodesic(true);
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
