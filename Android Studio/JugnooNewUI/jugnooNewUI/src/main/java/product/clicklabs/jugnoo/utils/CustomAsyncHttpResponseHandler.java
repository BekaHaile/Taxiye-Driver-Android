package product.clicklabs.jugnoo.utils;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

public abstract class CustomAsyncHttpResponseHandler extends AsyncHttpResponseHandler{

	@Override
	public void onFailure(int arg0, Header[] arg1, byte[] arg2, Throwable arg3) {
		FlurryEventLogger.connectionFailure(arg3.toString());
		onFailure(arg3);
	}

	@Override
	public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
		onSuccess(new String(arg2));
	}
	
	public abstract void onFailure(Throwable arg3);
	
	public abstract void onSuccess(String response);
	
}
