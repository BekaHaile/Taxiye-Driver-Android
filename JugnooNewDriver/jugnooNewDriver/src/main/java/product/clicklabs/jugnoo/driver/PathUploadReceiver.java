package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.CurrentPathItem;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.MapUtils;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

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

                                LatLng pathSource = null;

                                for(int i=0; i<validCurrentPathItems.size(); i++){
                                    CurrentPathItem currentPathItem = validCurrentPathItems.get(i);
                                    LatLng nextSourceLatLng = null;
                                    if(1 == currentPathItem.googlePath){
                                    }
                                    else{
                                        try{
                                            if(pathSource == null){
                                                pathSource = currentPathItem.sLatLng;
                                                nextSourceLatLng = currentPathItem.sLatLng;
                                            }
                                            boolean addPath = false;
                                            if(i < validCurrentPathItems.size()-1){
                                                if(MapUtils.distance(currentPathItem.dLatLng, validCurrentPathItems.get(i+1).sLatLng) < 2){
                                                    if(MapUtils.distance(pathSource, currentPathItem.dLatLng) < 50){
                                                        //dont add
                                                        addPath = false;
                                                    }
                                                    else{
                                                        //add pathSource, currentPathItem.dLatLng
                                                        nextSourceLatLng = currentPathItem.dLatLng;
                                                        addPath = true;
                                                    }
                                                }
                                                else{
                                                    //add pathSource, currentPathItem.dLatLng
                                                    nextSourceLatLng = validCurrentPathItems.get(i+1).sLatLng;
                                                    addPath = true;
                                                }
                                            }
                                            else{
                                                //add pathSource, currentPathItem.dLatLng
                                                nextSourceLatLng = currentPathItem.dLatLng;
                                                addPath = true;
                                            }

                                            if(addPath) {
                                                JSONObject locationData = new JSONObject();
                                                locationData.put("location_id", currentPathItem.id);
                                                locationData.put("source_latitude", pathSource.latitude);
                                                locationData.put("source_longitude", pathSource.longitude);
                                                locationData.put("destination_latitude", currentPathItem.dLatLng.latitude);
                                                locationData.put("destination_longitude", currentPathItem.dLatLng.longitude);
                                                locationDataArr.put(locationData);
                                                pathSource = nextSourceLatLng;
                                            }

                                        } catch(Exception e){e.printStackTrace();}
                                    }
                                }


                                String accessToken = Database2.getInstance(context).getDLDAccessToken();
                                String locations = locationDataArr.toString();
                                String engagementId = MeteringService.gpsInstance(context).getEngagementIdFromSP(context);
                                String serverUrl = Database2.getInstance(context).getDLDServerUrl();

                                if((!"".equalsIgnoreCase(accessToken)) && (!"".equalsIgnoreCase(locations)) && (!"".equalsIgnoreCase(engagementId)) && (!"".equalsIgnoreCase(serverUrl))){
                                    HashMap<String, String> nameValuePairs = new HashMap<>();
                                    nameValuePairs.put("access_token", accessToken);
                                    nameValuePairs.put("engagement_id", engagementId);
                                    nameValuePairs.put("locations", locations);

                                    Response response = RestClient.getApiServices().logOngoingRidePath(nameValuePairs);
                                    String result = new String(((TypedByteArray)response.getBody()).getBytes());

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
                            else{
                                String meteringState = Database2.getInstance(context).getMetringState();
                                if(!Database2.ON.equalsIgnoreCase(meteringState)){
                                    Database2.getInstance(context).deleteAllCurrentPathItems();
                                    cancelUploadPathAlarm(context);
                                }
                            }

                            String meteringState = Database2.getInstance(context).getMetringState();
                            Log.writePathLogToFile("service_log",
                                    "PathUploadReceiver onReceive meteringState=" + meteringState
                                            + " and MeteringService isRunning="+Utils.isServiceRunning(context, MeteringService.class));

                            if(Database2.ON.equalsIgnoreCase(meteringState)) {
                                if (!Utils.isServiceRunning(context, MeteringService.class)) {
                                    Log.writePathLogToFile("service_log",
                                            "PathUploadReceiver onReceive gone in");
                                    context.startService(new Intent(context, MeteringService.class));
                                }
                            }
                        } catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }).start();

            } catch (Exception e) {
                e.printStackTrace();
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