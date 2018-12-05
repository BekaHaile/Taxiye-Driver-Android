package product.clicklabs.jugnoo.driver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.Location;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;

import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.CustomerInfo;
import product.clicklabs.jugnoo.driver.datastructure.DriverScreenMode;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.driver.utils.FlurryEventNames;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class UploadInRideDataReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();
        if (MeteringService.UPLOAD_IN_RIDE_DATA.equals(action)) {
            try {
                final Location location = MeteringService.gpsInstance(context).lastGPSLocation;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        updateInRideData(context, location);
                    }
                }).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    public void updateInRideData(Context context, Location location) {
        try {
            long responseTime = System.currentTimeMillis();
            int driverScreenMode = Prefs.with(context).getInt(SPLabels.DRIVER_SCREEN_MODE,
                    DriverScreenMode.D_INITIAL.getOrdinal());
            String accessToken = Database2.getInstance(context).getDLDAccessToken();
            if (DriverScreenMode.D_IN_RIDE.getOrdinal() == driverScreenMode
                    && location != null
                    && !"".equalsIgnoreCase(accessToken)) {
                double totalDistanceInKm = Math.abs(GpsDistanceCalculator.getTotalDistanceFromSP(context) / 1000.0d);
                long rideTimeSeconds = (System.currentTimeMillis() - GpsDistanceCalculator.getStartTimeFromSP(context)) / 1000;
                double rideTimeMinutes = Math.ceil(rideTimeSeconds / 60);
                long waitTimeSeconds = GpsDistanceCalculator.getWaitTimeFromSP(context) / 1000;
                double waitTimeMinutes = Math.ceil(waitTimeSeconds / 60);
                int lastLogId = Integer.parseInt((Prefs.with(context).getString(SPLabels.DISTANCE_RESET_LOG_ID, "" + 0)));

                DecimalFormat decimalFormat = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.ENGLISH));
                DecimalFormat decimalFormatNoDecimal = new DecimalFormat("#", new DecimalFormatSymbols(Locale.ENGLISH));

                HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, accessToken);
                params.put(Constants.KEY_ENGAGEMENT_ID, String.valueOf(MyApplication.getInstance().getEngagementSP().getEngagementSPDatasArray().get(0).getEngagementId()));
                params.put("current_latitude", String.valueOf(location.getLatitude()));
                params.put("current_longitude", String.valueOf(location.getLongitude()));
                params.put("distance_travelled", decimalFormat.format(totalDistanceInKm));
                params.put("ride_time", decimalFormatNoDecimal.format(rideTimeMinutes));
                params.put("wait_time", decimalFormatNoDecimal.format(waitTimeMinutes));
                params.put("last_log_id", "" + lastLogId);
                HomeUtil.putDefaultParams(params);

                Response response = RestClient.getApiServices().updateInRideDataRetro(params);
                if (response != null) {
                    String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
                    JSONObject jObj = new JSONObject(jsonString);
                    int flag = jObj.optInt("flag", ApiResponseFlags.ACTION_COMPLETE.getOrdinal());
                    lastLogId = jObj.optInt("last_log_id", 0);
					updateWalletBalance(jObj);
                    Prefs.with(context).save(SPLabels.DISTANCE_RESET_LOG_ID, "" + lastLogId);
                    if (ApiResponseFlags.DISTANCE_RESET.getOrdinal() == flag) {
                        try {
                            double distance = jObj.getDouble("total_distance") * 1000;
                            long rideTime = jObj.optLong("ride_time", 0) * 60000;
                            long waitTime = jObj.optLong("wait_time", 0) * 60000;
                            MeteringService.gpsInstance(context).updateDistanceInCaseOfReset(distance, rideTime, waitTime);
                            FlurryEventLogger.logResponseTime(context, System.currentTimeMillis() - responseTime, FlurryEventNames.UPDATE_IN_RIDE_DATA_RESPONSE);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
//            String meteringState = Database2.getInstance(context).getMetringState();
//            if(!Database2.ON.equalsIgnoreCase(meteringState)){
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    public void updateWalletBalance(JSONObject jsonObject) {
		try {
			JSONArray jsonArray = jsonObject.getJSONArray("balance_array");
			for (int i = 0; i < jsonArray.length(); i++) {
				int engagementId = jsonArray.getJSONObject(i).getInt("engagement_id");
				double walletBalance = jsonArray.getJSONObject(i).getInt("wallet_balance");
				CustomerInfo customerInfo = Data.getCustomerInfo(String.valueOf(engagementId));
				if (customerInfo != null) {
                    if (walletBalance > -1) {
                        customerInfo.setJugnooBalance(walletBalance);
                    }
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}