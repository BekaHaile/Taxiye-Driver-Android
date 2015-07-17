package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;

import product.clicklabs.jugnoo.driver.datastructure.GpsState;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.DateOperations;
import product.clicklabs.jugnoo.driver.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import product.clicklabs.jugnoo.driver.utils.SoundMediaPlayer;

public class MeteringAlarmReceiver extends BroadcastReceiver {

	private static final String CHECK_LOCATION = "product.clicklabs.jugnoo.driver.CHECK_LOCATION";

	private static final long MINUTE = 60 * 1000;
	private static final long HALF_MINUTE = 30 * 1000;
	private static final long MAX_TIME_BEFORE_LOCATION_UPDATE = 2 * MINUTE;


	@Override
	public void onReceive(final Context context, Intent intent) {
		String action = intent.getAction();
		if (CHECK_LOCATION.equals(action)) {
			long timeDiff = System.currentTimeMillis() - GpsDistanceCalculator.lastLocationTime;




//			if(timeDiff > MAX_TIME_BEFORE_LOCATION_UPDATE && timeDiff <= 7*HALF_MINUTE){
//				GpsDistanceCalculator.gpsLocationUpdate.refreshLocationFetchers(context);
//				Toast.makeText(context, "Old Location", Toast.LENGTH_LONG).show();
//			}
//			else if(timeDiff > 7*HALF_MINUTE && timeDiff< 9*HALF_MINUTE){
//				Intent i = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
//				i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//				context.startActivity(i);
//			}
//			else if(timeDiff >= 9*HALF_MINUTE && timeDiff <= 11*HALF_MINUTE){
//				GpsDistanceCalculator.gpsLocationUpdate.refreshLocationFetchers(context);
//				Toast.makeText(context, "Old Location", Toast.LENGTH_LONG).show();
//			}
//			else if(timeDiff > 11*HALF_MINUTE){
//				SoundMediaPlayer.startSound(context, R.raw.cancellation_ring, 4, true, true);
//				Toast.makeText(context, "Please Restart Your Phone", Toast.LENGTH_LONG).show();
//			}


			if(timeDiff >= 6*MINUTE){
				SoundMediaPlayer.startSound(context, R.raw.cancellation_ring, 4, true, true);

				FlurryAgent.init(context, Data.FLURRY_KEY);
				FlurryAgent.onStartSession(context, Data.FLURRY_KEY);
				FlurryEventLogger.gpsStatus("Device Restar");
				FlurryAgent.onEndSession(context);

				Toast.makeText(context, "Please Restart Your Phone", Toast.LENGTH_LONG).show();
				Prefs.with(context).save(SPLabels.GPS_STATE, GpsState.GREATER_SIX.getOrdinal());
			}
			else if(timeDiff > 7*HALF_MINUTE){
				if(Prefs.with(context).getInt(SPLabels.GPS_STATE, GpsState.ZERO_TWO.getOrdinal())
						== GpsState.TWO_LESS_FOUR.getOrdinal()){
					Intent i = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
//					Intent i = new Intent(context,SplashNewActivity.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(i);
//					((Activity)context).finish();


					FlurryAgent.init(context, Data.FLURRY_KEY);
					FlurryAgent.onStartSession(context, Data.FLURRY_KEY);
					FlurryEventLogger.gpsStatus("App restart");
					FlurryAgent.onEndSession(context);

				}
				else{
					GpsDistanceCalculator.gpsLocationUpdate.refreshLocationFetchers(context);

					FlurryAgent.init(context, Data.FLURRY_KEY);
					FlurryAgent.onStartSession(context, Data.FLURRY_KEY);
					FlurryEventLogger.gpsStatus("Old Location Afer");
					FlurryAgent.onEndSession(context);

					Toast.makeText(context, "Old Location after", Toast.LENGTH_LONG).show();
				}
				Prefs.with(context).save(SPLabels.GPS_STATE, GpsState.GREATER_FOUR.getOrdinal());
			}
			else if(timeDiff > MAX_TIME_BEFORE_LOCATION_UPDATE){
//				Intent i = new Intent(context,SplashNewActivity.class);
//                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				context.startActivity(i);
//				((Activity)context).finish();
				GpsDistanceCalculator.gpsLocationUpdate.refreshLocationFetchers(context);
				Toast.makeText(context, "Old Location", Toast.LENGTH_LONG).show();

				FlurryAgent.init(context, Data.FLURRY_KEY);
				FlurryAgent.onStartSession(context, Data.FLURRY_KEY);
				FlurryEventLogger.gpsStatus("Old Location");
				FlurryAgent.onEndSession(context);

				Prefs.with(context).save(SPLabels.GPS_STATE, GpsState.TWO_LESS_FOUR.getOrdinal());
			}
			else{
				Prefs.with(context).save(SPLabels.GPS_STATE, GpsState.ZERO_TWO.getOrdinal());
			}


		}
	}
	
}