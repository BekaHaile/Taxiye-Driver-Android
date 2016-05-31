package product.clicklabs.jugnoo.driver.apis;

import android.content.Context;
import android.os.AsyncTask;

import com.google.android.gms.maps.model.LatLng;

import product.clicklabs.jugnoo.driver.utils.MapUtils;

/**
 * Created by shankar on 5/31/16.
 */
public class ApiGoogleGeocodeAddress extends AsyncTask<String, Integer, String> {

	private Context context;
	private LatLng latLng;
	private boolean toLocality;
	private Callback callback;

	public ApiGoogleGeocodeAddress(Context context, LatLng latLng, boolean toLocality, Callback callback){
		this.context = context;
		this.latLng = latLng;
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
		return MapUtils.getGAPIAddress(context, latLng, toLocality);
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
