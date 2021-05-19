package product.clicklabs.jugnoo.driver.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;

import java.lang.reflect.Method;

import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Prefs;

/**
 * Created by aneeshbansal on 07/09/16.
 */
public class NetworkCheckReceiver extends BroadcastReceiver {

	private NetworkChangeListener networkChangeListener;

	public interface NetworkChangeListener {
		void onStateChanged(boolean isOnline);
	}

	public void setOnNetworkChangeListener(NetworkChangeListener networkChangeListener) {
		this.networkChangeListener = networkChangeListener;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {

			ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			boolean noConnectivity = intent.getBooleanExtra(
					ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
			Log.d("NetworkCheckReceiver", String.valueOf(isMobileDataEnable(context)));

			if (!isMobileDataEnable(context)) {
				Prefs.with(context).save(Constants.MOBILE_DATA_STATE, false);
			}
			if(networkChangeListener != null)
				networkChangeListener.onStateChanged(!noConnectivity);
		}
	}

	public boolean isMobileDataEnable(Context context) {
		boolean mobileDataEnabled = false; // Assume disabled
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		try {
			Class cmClass = Class.forName(cm.getClass().getName());
			Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
			method.setAccessible(true); // Make the method callable
			// get the setting for "mobile data"
			mobileDataEnabled = (Boolean)method.invoke(cm);
		} catch (Exception e) {
			// Some problem accessible private API and do whatever error handling you want here
		}
		return mobileDataEnabled;
	}

}
