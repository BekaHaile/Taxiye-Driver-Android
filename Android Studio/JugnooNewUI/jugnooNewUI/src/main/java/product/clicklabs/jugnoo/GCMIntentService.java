package product.clicklabs.jugnoo;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import product.clicklabs.jugnoo.datastructure.PassengerScreenMode;
import product.clicklabs.jugnoo.datastructure.PushFlags;
import product.clicklabs.jugnoo.utils.HttpRequester;
import product.clicklabs.jugnoo.utils.Log;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.app.NotificationCompat;
import android.util.Pair;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GCMIntentService extends IntentService {
	
	public static final int NOTIFICATION_ID = 1;
	public static final int PROMOTION_NOTIFICATION_ID = 1212;
    
    public GCMIntentService() {
        super("GcmIntentService");
    }

	  
		@SuppressWarnings("deprecation")
		private void notificationManager(Context context, String message, boolean ring) {
	    	
			try {
				long when = System.currentTimeMillis();
				
				NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
				
				Log.v("message",","+message);
				
				Intent notificationIntent = new Intent(context, SplashNewActivity.class);
				
				
				notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
				PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
				
				NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
				builder.setAutoCancel(true);
				builder.setContentTitle("Jugnoo");
				builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
				builder.setContentText(message);
				builder.setTicker(message);
				
				if(ring){
					builder.setLights(Color.GREEN, 500, 500);
				}
				else{
					builder.setDefaults(Notification.DEFAULT_ALL);
				}
				
				builder.setWhen(when);
				builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.jugnoo_icon));
				builder.setSmallIcon(R.drawable.notification_icon);
				builder.setContentIntent(intent);
				
				
				
				
				
				Notification notification = builder.build();
				notificationManager.notify(NOTIFICATION_ID, notification);
				
				PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
				WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
				wl.acquire(15000);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}

		@SuppressWarnings("deprecation")
		private void notificationManagerResume(Context context, String message, boolean ring) {
			
			try {
				long when = System.currentTimeMillis();
				
				NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
				
				Log.v("message",","+message);
				
				Intent notificationIntent = new Intent(context, HomeActivity.class);
				
				notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
				PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
				
				NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
				builder.setAutoCancel(true);
				builder.setContentTitle("Jugnoo");
				builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
				builder.setContentText(message);
				builder.setTicker(message);
				
				if(ring){
					builder.setLights(Color.GREEN, 500, 500);
				}
				else{
					builder.setDefaults(Notification.DEFAULT_ALL);
				}
				
				builder.setWhen(when);
				builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.jugnoo_icon));
				builder.setSmallIcon(R.drawable.notification_icon);
				builder.setContentIntent(intent);
				
				
				
				Notification notification = builder.build();
				notificationManager.notify(NOTIFICATION_ID, notification);
				
				PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
				WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
				wl.acquire(15000);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
	    
		
		@SuppressWarnings("deprecation")
		private void notificationManagerCustomID(Context context, String message, int notificationId) {
			
			try {
				long when = System.currentTimeMillis();
				
				NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
				
				Log.v("message",","+message);
				
				Intent notificationIntent = new Intent(context, SplashNewActivity.class);
				
				notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
				PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
				
				NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
				builder.setAutoCancel(true);
				builder.setContentTitle("Jugnoo");
				builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
				builder.setContentText(message);
				builder.setTicker(message);
				builder.setDefaults(Notification.DEFAULT_ALL);
				builder.setWhen(when);
				builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.jugnoo_icon));
				builder.setSmallIcon(R.drawable.notification_icon);
				builder.setContentIntent(intent);

				Notification notification = builder.build();
				notificationManager.notify(notificationId, notification);
				
				PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
				WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
				wl.acquire(15000);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
	    
	    public static void clearNotifications(Context context){
			NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
			notificationManager.cancel(NOTIFICATION_ID);
	    }
	    


	    
	    
		 
	    @Override
	    public void onHandleIntent(Intent intent) {
	        Bundle extras = intent.getExtras();
	        Log.e("Recieved a gcm message arg1...", ","+intent.getExtras());
	        
	        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
	        // The getMessageType() intent parameter must be the intent you received
	        // in your BroadcastReceiver.
	        String messageType = gcm.getMessageType(intent);

	        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
	            /*
	             * Filter messages based on message type. Since it is likely that GCM
	             * will be extended in the future with new message types, just ignore
	             * any message types you're not interested in, or that you don't
	             * recognize.
	             */
	            if (GoogleCloudMessaging.
	                    MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
//	                sendNotification("Send error: " + extras.toString());
	            } else if (GoogleCloudMessaging.
	                    MESSAGE_TYPE_DELETED.equals(messageType)) {
//	                sendNotification("Deleted messages on server: " +
//	                        extras.toString());
	            // If it's a regular GCM message, do some work.
	            } else if (GoogleCloudMessaging.
	                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
	                // This loop represents the service doing some work.

	    	    	try{
	    		    	 Log.e("Recieved a gcm message arg1...", ","+intent.getExtras());
	    		    	 
	    		    	 if(!"".equalsIgnoreCase(intent.getExtras().getString("message", ""))){
	    		    		 
	    		    		 String message = intent.getExtras().getString("message");
	    		    		 
	    		    		 try{
	    	    				 JSONObject jObj = new JSONObject(message);
	    	    				 
	    	    				 int flag = jObj.getInt("flag");
	    	    				 
	    	    				 if(PushFlags.RIDE_ACCEPTED.getOrdinal() == flag){
									if (HomeActivity.appInterruptHandler != null) {
										HomeActivity.appInterruptHandler.rideRequestAcceptedInterrupt(jObj);
										notificationManagerResume(this, "Your request has been accepted", false);
									} else {
										notificationManager(this, "Your request has been accepted", false);
									}
	    	    				 }
	    	    				 else if(PushFlags.RIDE_STARTED.getOrdinal() == flag){

	    	    					 if (HomeActivity.appInterruptHandler != null) {
	    	    						 notificationManagerResume(this, "Your ride has started.", false);
	    	    						 HomeActivity.appInterruptHandler.startRideForCustomer(0);
	    	    					 }
	    	    					 else{
	    	    						 String SHARED_PREF_NAME = "myPref",
	    	    						 SP_CUSTOMER_SCREEN_MODE = "customer_screen_mode",
	    	    								 P_IN_RIDE = "P_IN_RIDE";
	    	    						 SharedPreferences pref = getSharedPreferences(SHARED_PREF_NAME, 0);
	    	    						 Editor editor = pref.edit();
	    	    						 editor.putString(SP_CUSTOMER_SCREEN_MODE, P_IN_RIDE);
	    	    						 editor.commit();
	    	    						 
	    	    						 notificationManager(this, "Your ride has started.", false);
	    	    					 }
	    	    				 }
	    	    				 else if(PushFlags.RIDE_ENDED.getOrdinal() == flag){
	    	    					 String engagementId = jObj.getString("engagement_id");
	    	    					 
	    	    					 if (HomeActivity.appInterruptHandler != null) {
	    	    						 if(PassengerScreenMode.P_IN_RIDE == HomeActivity.passengerScreenMode){
	    	    							 notificationManagerResume(this, "Your ride has ended.", false);
		    	    						 HomeActivity.appInterruptHandler.customerEndRideInterrupt(engagementId);
	    	    						 }
	    	    					 }
	    	    					 else{
	    	    						 notificationManager(this, "Your ride has ended.", false);
	    	    					 }
	    	    				 }
	    	    				 else if(PushFlags.RIDE_REJECTED_BY_DRIVER.getOrdinal() == flag){
	    	    					 if (HomeActivity.appInterruptHandler != null) {
	    	    						 HomeActivity.appInterruptHandler.startRideForCustomer(1);
	    	    					 }
	    	    				 }
	    	    				 else if(PushFlags.WAITING_STARTED.getOrdinal() == flag || PushFlags.WAITING_ENDED.getOrdinal() == flag){
	    	    					 String message1 = jObj.getString("message");
	    	    					 if (HomeActivity.activity == null) {
		    	    					 notificationManager(this, ""+message1, false);
	    	    					 }
	    	    					 else{
		    	    					 notificationManagerResume(this, ""+message1, false);
	    	    					 }
	    	    				 }
	    	    				 else if(PushFlags.NO_DRIVERS_AVAILABLE.getOrdinal() == flag){
	    	    					 String log = jObj.getString("log");
	    	    					 if (HomeActivity.appInterruptHandler != null) {
	    	    						 HomeActivity.appInterruptHandler.onNoDriversAvailablePushRecieved(log);
	    	    					 }
	    	    				 }
	    	    				 else if (PushFlags.CHANGE_STATE.getOrdinal() == flag) {
	    	    					 String logMessage = jObj.getString("message");
									if (HomeActivity.appInterruptHandler != null) {
										HomeActivity.appInterruptHandler.onChangeStatePushReceived();
										notificationManagerResume(this, logMessage, false);
									} else {
										notificationManager(this, logMessage, false);
									}
								}
	    	    				else if(PushFlags.DISPLAY_MESSAGE.getOrdinal() == flag){
	    	    					 String message1 = jObj.getString("message");
	    	    					 notificationManagerCustomID(this, message1, PROMOTION_NOTIFICATION_ID);
	    	    				}
	    	    				else if(PushFlags.CHANGE_PORT.getOrdinal() == flag){
	    	    					sendChangePortAckToServer(this, jObj);
	    	    				}
	    	    				else if(PushFlags.PAYMENT_RECEIVED.getOrdinal() == flag){
	    	    					String message1 = jObj.getString("message");
	    	    					double balance = jObj.getDouble("balance");
	    	    					if (HomeActivity.appInterruptHandler != null) {
										HomeActivity.appInterruptHandler.onJugnooCashAddedByDriver(balance, message1);
										notificationManagerResume(this, message1, false);
									} else {
										notificationManager(this, message1, false);
									}
	    	    				}
	    	    				 
	    		    		 } catch(Exception e){
	    		    			 e.printStackTrace();
	    		    		 }
	    		    		 
	    		    	 }
	    	    		
	    	    	 }
	    	    	 catch(Exception e){
	    	    		 Log.e("Recieved exception message arg1...", ","+intent);
	    	    		 Log.e("exception", ","+e);
	    	    	 }
	    	    
	            }
	        }
	        // Release the wake lock provided by the WakefulBroadcastReceiver.
	        GcmBroadcastReceiver.completeWakefulIntent(intent);
	    }
	    
	    
		public void sendChangePortAckToServer(final Context context, final JSONObject jObject1){
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						SplashNewActivity.initializeServerURL(context);
						Pair<String, Integer> accessTokenPair = AccessTokenGenerator.getAccessTokenPair(context);
						
						ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
						nameValuePairs.add(new BasicNameValuePair("access_token", accessTokenPair.first));
						
						Log.i("nameValuePairs", "="+nameValuePairs);
						Log.e("Data.SERVER_URL", "="+Data.SERVER_URL);
						
						HttpRequester simpleJSONParser = new HttpRequester();
						String result = simpleJSONParser.getJSONFromUrlParams(Data.SERVER_URL+"/acknowledge_port_change", nameValuePairs);
						
						Log.e("result ","="+result);
						
						if(result.contains(HttpRequester.SERVER_TIMEOUT)){
                            Log.e("result ","=timeout");
						}
						else{
							new JSONParser().parsePortNumber(context, jObject1);
						}
						
						simpleJSONParser = null;
						nameValuePairs = null;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}).start();
		}
	    
}
