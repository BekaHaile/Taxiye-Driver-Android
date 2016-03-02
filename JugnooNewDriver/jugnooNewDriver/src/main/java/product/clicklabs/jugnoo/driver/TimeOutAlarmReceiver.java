package product.clicklabs.jugnoo.driver;

/**
 * Created by aneeshbansal on 02/03/16.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import product.clicklabs.jugnoo.driver.datastructure.BusinessType;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.utils.Prefs;


public class TimeOutAlarmReceiver extends BroadcastReceiver {

	static Location myLocation;

	@Override
	public void onReceive(Context context, Intent intent) {

		// For our recurring task, we'll just display a message
		Toast.makeText(context, "I'm running", Toast.LENGTH_SHORT).show();

 		Prefs.with(context).save(SPLabels.INGNORE_RIDEREQUEST_COUNT,Prefs.with(context).getInt(SPLabels.INGNORE_RIDEREQUEST_COUNT,0) +1);

		if(Data.userData.ignoreRideCount <= Prefs.with(context).getInt(SPLabels.INGNORE_RIDEREQUEST_COUNT,0)){
			HomeActivity homeActivity = new HomeActivity();
			homeActivity.switchJugnooOnThroughServer(BusinessType.AUTOS, 1, new LatLng(myLocation.getLatitude(), myLocation.getLongitude()), false, 1);
		}
	}
}
