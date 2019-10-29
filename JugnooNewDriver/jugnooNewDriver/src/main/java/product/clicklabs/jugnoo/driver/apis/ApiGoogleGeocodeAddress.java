package product.clicklabs.jugnoo.driver.apis;

import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

import product.clicklabs.jugnoo.driver.utils.MapUtils;

/**
 * Created by shankar on 5/31/16.
 */
public class ApiGoogleGeocodeAddress extends AsyncTask<String, Integer, String> {

	private LatLng latLng;
	private String source;
	private boolean toLocality;
	private Callback callback;

	public ApiGoogleGeocodeAddress(LatLng latLng, boolean toLocality, String source, Callback callback){
		this.latLng = latLng;
		this.source = source;
		this.toLocality = toLocality;
		this.callback = callback;
	}


	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		callback.onPre();
	}

	@Override
	protected String doInBackground(String... params) {
		return MapUtils.getGAPIAddress(latLng, toLocality, source);
	}

	@Override
	protected void onPostExecute(String s) {
		super.onPostExecute(s);
		callback.onPost(s);
	}


	public interface Callback{
		void onPre();
		void onPost(String address);
	}

}
