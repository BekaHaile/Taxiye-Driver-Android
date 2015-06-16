package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.CurrentPathItem;
import product.clicklabs.jugnoo.driver.utils.HttpRequester;
import product.clicklabs.jugnoo.driver.utils.Log;

public class PathUploadReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        String action = intent.getAction();
        if (MeteringService.UPOLOAD_PATH.equals(action)) {
            try {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            ArrayList<CurrentPathItem> validCurrentPathItems = new ArrayList<CurrentPathItem>();
                            validCurrentPathItems.addAll(Database2.getInstance(context).getCurrentPathItemsToUpload());

                            if(validCurrentPathItems.size() > 0){

                                JSONArray locationDataArr = new JSONArray();

                                for(CurrentPathItem currentPathItem : validCurrentPathItems){
                                    if(1 == currentPathItem.googlePath){
                                    }
                                    else{
                                        try{
                                            JSONObject locationData = new JSONObject();
                                            locationData.put("location_id", currentPathItem.id);
                                            locationData.put("source_latitude", currentPathItem.sLatLng.latitude);
                                            locationData.put("source_longitude", currentPathItem.sLatLng.longitude);
                                            locationData.put("destination_latitude", currentPathItem.dLatLng.latitude);
                                            locationData.put("destination_longitude", currentPathItem.dLatLng.longitude);
                                            locationDataArr.put(locationData);
                                        } catch(Exception e){e.printStackTrace();}
                                    }
                                }


                                String accessToken = Database2.getInstance(context).getDLDAccessToken();
                                String locations = locationDataArr.toString();
                                String engagementId = MeteringService.gpsInstance(context).getEngagementIdFromSP(context);
                                String serverUrl = Database2.getInstance(context).getDLDServerUrl();

                                if((!"".equalsIgnoreCase(accessToken)) && (!"".equalsIgnoreCase(locations)) && (!"".equalsIgnoreCase(engagementId)) && (!"".equalsIgnoreCase(serverUrl))){
                                    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                                    nameValuePairs.add(new BasicNameValuePair("access_token", accessToken));
                                    nameValuePairs.add(new BasicNameValuePair("engagement_id", engagementId));
                                    nameValuePairs.add(new BasicNameValuePair("locations", locations));

                                    Log.e("", "");


                                    HttpRequester.TIMEOUT_CONNECTION = 10000;
                                    HttpRequester.TIMEOUT_SOCKET = 10000;

                                    HttpRequester simpleJSONParser = new HttpRequester();
                                    String result = simpleJSONParser.getJSONFromUrlParams(serverUrl + "/log_ongoing_ride_path", nameValuePairs);

                                    HttpRequester.TIMEOUT_CONNECTION = 30000;
                                    HttpRequester.TIMEOUT_SOCKET = 30000;

                                    if (result.contains(HttpRequester.SERVER_TIMEOUT)) {

                                    } else {
                                        try{
                                            //flag = 136
                                            JSONObject jObj = new JSONObject(result);
                                            if(jObj.has("flag")){
                                                int flag = jObj.getInt("flag");
                                                if(ApiResponseFlags.RIDE_PATH_RECEIVED.getOrdinal() == flag){
                                                    ArrayList<Long> rowIds = new ArrayList<Long>();
                                                    for(CurrentPathItem currentPathItem : validCurrentPathItems){
                                                        rowIds.add(currentPathItem.id);
                                                    }
                                                    Database2.getInstance(context).updateCurrentPathItemAcknowledgedForArray(rowIds, 1);
//                                                    if(HomeActivity.appInterruptHandler != null){
//                                                        HomeActivity.appInterruptHandler.addPathNew(validCurrentPathItems);
//                                                    }
                                                }
                                            }
                                        } catch(Exception e){
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                            else{
                                String meteringState = Database2.getInstance(context).getMetringState();
                                if(!Database2.ON.equalsIgnoreCase(meteringState)){
                                    Database2.getInstance(context).deleteAllCurrentPathItems();
                                    cancelUploadPathAlarm(context);
                                }
                            }
                        } catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }).start();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
            }
        }
    }


    public void cancelUploadPathAlarm(Context context) {
        Intent intent = new Intent(context, PathUploadReceiver.class);
        intent.setAction(MeteringService.UPOLOAD_PATH);
        intent.putExtra("engagement_id", MeteringService.gpsInstance(context).getEngagementIdFromSP(context));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, MeteringService.UPLOAD_PATH_PI_REQUEST_CODE,
            intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();
    }

}