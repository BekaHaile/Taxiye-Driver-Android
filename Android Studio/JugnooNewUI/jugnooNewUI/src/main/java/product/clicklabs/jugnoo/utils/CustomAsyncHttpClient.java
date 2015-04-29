package product.clicklabs.jugnoo.utils;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

public class CustomAsyncHttpClient extends AsyncHttpClient{

	@Override
	public RequestHandle post(String url, RequestParams params, ResponseHandlerInterface responseHandler) {
		// TODO Auto-generated method stub
		Log.e("url=", "="+url);
		Log.e("params=", "="+params);
		return super.post(url, params, responseHandler);
	}
	
	
	@Override
	public RequestHandle get(String url, ResponseHandlerInterface responseHandler) {
		// TODO Auto-generated method stub
		return super.get(url, responseHandler);
	}
	
	
	
}
