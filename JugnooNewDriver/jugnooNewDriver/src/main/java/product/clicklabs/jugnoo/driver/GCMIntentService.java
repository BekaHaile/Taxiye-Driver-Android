package product.clicklabs.jugnoo.driver;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.util.Pair;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.maps.model.LatLng;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.AutoRideRequest;
import product.clicklabs.jugnoo.driver.datastructure.BusinessType;
import product.clicklabs.jugnoo.driver.datastructure.DriverRideRequest;
import product.clicklabs.jugnoo.driver.datastructure.DriverScreenMode;
import product.clicklabs.jugnoo.driver.datastructure.FatafatRideRequest;
import product.clicklabs.jugnoo.driver.datastructure.MealRideRequest;
import product.clicklabs.jugnoo.driver.datastructure.PushFlags;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.datastructure.UserMode;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse;
import product.clicklabs.jugnoo.driver.utils.DateOperations;
import product.clicklabs.jugnoo.driver.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.driver.utils.HttpRequester;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import product.clicklabs.jugnoo.driver.utils.SoundMediaPlayer;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class GCMIntentService extends IntentService {

    public static final int NOTIFICATION_ID = 1;
    public static final long REQUEST_TIMEOUT = 120000;
    NotificationCompat.Builder builder;

    public GCMIntentService() {
        super("GcmIntentService");
    }

    protected void onError(Context arg0, String arg1) {
        Log.e("Registration", "Got an error1!");
        Log.e("Registration", arg1.toString());
    }

    protected boolean onRecoverableError(Context context, String errorId) {
        Log.d("onRecoverableError", errorId);
        return false;
    }


    @SuppressWarnings("deprecation")
    public static void notificationManager(Context context, String message, boolean ring) {

        try {
            long when = System.currentTimeMillis();

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            Log.v("message", "," + message);

            Intent notificationIntent = new Intent(context, SplashNewActivity.class);


            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setAutoCancel(true);
            builder.setContentTitle("Jugnoo");
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
            builder.setContentText(message);
            builder.setTicker(message);

            if (ring) {
                builder.setLights(Color.GREEN, 500, 500);
            } else {
                builder.setDefaults(Notification.DEFAULT_ALL);
            }

            builder.setWhen(when);
            builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.jugnoo_icon));
            builder.setSmallIcon(R.drawable.notif_icon);
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
    public static void notificationManagerResume(Context context, String message, boolean ring) {

        try {
            long when = System.currentTimeMillis();

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            Log.v("message", "," + message);

            Intent notificationIntent = new Intent(context, HomeActivity.class);

            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setAutoCancel(true);
            builder.setContentTitle("Jugnoo");
            builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
            builder.setContentText(message);
            builder.setTicker(message);

            if (ring) {
                builder.setLights(Color.GREEN, 500, 500);
            } else {
                builder.setDefaults(Notification.DEFAULT_ALL);
            }

            builder.setWhen(when);
            builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.jugnoo_icon));
            builder.setSmallIcon(R.drawable.notif_icon);
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


    public static void clearNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
    }

    protected void onRegistered(Context arg0, String arg1) {
        Log.e("Registration", "!");
        Log.e("Registration", arg1.toString());
        Data.deviceToken = arg1.toString();
    }

    protected void onUnregistered(Context arg0, String arg1) {
        Log.e("Registration", "Got an error4!");
        Log.e("Registration", arg1.toString());
    }


    @Override
    public void onHandleIntent(Intent intent) {
		try {
			String currentTimeUTC = DateOperations.getCurrentTimeInUTC();
			String currentTime = DateOperations.getCurrentTime();

			Bundle extras = intent.getExtras();

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

					String SHARED_PREF_NAME1 = "myPref", SP_ACCESS_TOKEN_KEY = "access_token";

					SharedPreferences pref1 = getSharedPreferences(SHARED_PREF_NAME1, 0);
					final String accessToken = pref1.getString(SP_ACCESS_TOKEN_KEY, "");
					if (!"".equalsIgnoreCase(accessToken)) {

						try {
							Log.i("Recieved a gcm message arg1...", "," + intent.getExtras());

							if (!"".equalsIgnoreCase(intent.getExtras().getString("message", ""))) {

								String message = intent.getExtras().getString("message");

								try {
									JSONObject jObj = new JSONObject(message);

									int flag = jObj.getInt("flag");

									if ((PushFlags.REQUEST.getOrdinal() == flag)
										&&
										(Prefs.with(this).getInt(SPLabels.RECEIVE_REQUESTS, 1) == 1)) {

	//	    	    						 {   "engagement_id": engagement_id,
	//	    	    							 "user_id": data.customer_id,
	//	    	    							 "flag": g_enum_notificationFlags.REQUEST,
	//	    	    							 "latitude": data.pickup_latitude,
	//	    	    							 "longitude": data.pickup_longitude,
	//	    	    							 "address": pickup_address
	//	    	    							 "start_time": date}


										String engagementId = jObj.getString("engagement_id");
										String userId = jObj.getString("user_id");
										double latitude = jObj.getDouble("latitude");
										double longitude = jObj.getDouble("longitude");
										String startTime = jObj.getString("start_time");
										String address = jObj.getString("address");

										String startTimeLocal = DateOperations.utcToLocal(startTime);

										Log.i("startTimeLocal = ", "=" + startTimeLocal);

										String endTime = "";
										if (jObj.has("end_time")) {
											endTime = jObj.getString("end_time");
										}

										int businessId = BusinessType.AUTOS.getOrdinal();
										if (jObj.has("business_id")) {
											businessId = jObj.getInt("business_id");
										}

										long requestTimeOutMillis = GCMIntentService.REQUEST_TIMEOUT;

										if ("".equalsIgnoreCase(endTime)) {
											long serverStartTimeLocalMillis = DateOperations.getMilliseconds(startTimeLocal);
											long serverStartTimeLocalMillisPlus60 = serverStartTimeLocalMillis + 60000;
											requestTimeOutMillis = serverStartTimeLocalMillisPlus60 - System.currentTimeMillis();
										} else {
											long startEndDiffMillis = DateOperations.getTimeDifference(DateOperations.utcToLocal(endTime),
												startTimeLocal);
											Log.i("startEndDiffMillis = ", "=" + startEndDiffMillis);
											if (startEndDiffMillis < GCMIntentService.REQUEST_TIMEOUT) {
												requestTimeOutMillis = startEndDiffMillis;
											} else {
												requestTimeOutMillis = GCMIntentService.REQUEST_TIMEOUT;
											}
										}


										double fareFactor = 1;
										if(jObj.has("fare_factor")) {
											fareFactor = jObj.getDouble("fare_factor");
										}


										sendRequestAckToServer(this, engagementId, currentTimeUTC);

										FlurryEventLogger.requestPushReceived(this, engagementId, DateOperations.utcToLocal(startTime), currentTime);

										startTime = DateOperations.getDelayMillisAfterCurrentTime(requestTimeOutMillis);

										if (HomeActivity.appInterruptHandler != null) {
											if (UserMode.DRIVER == HomeActivity.userMode) {
												if (DriverScreenMode.D_INITIAL == HomeActivity.driverScreenMode ||
													DriverScreenMode.D_REQUEST_ACCEPT == HomeActivity.driverScreenMode ||
													DriverScreenMode.D_RIDE_END == HomeActivity.driverScreenMode) {

													int referenceId = jObj.getInt("reference_id");

													if (BusinessType.AUTOS.getOrdinal() == businessId) {
														Data.driverRideRequests.add(new AutoRideRequest(engagementId, userId,
																new LatLng(latitude, longitude), startTime, address,
																businessId, referenceId, fareFactor));
													} else if (BusinessType.MEALS.getOrdinal() == businessId) {
														String rideTime = jObj.getString("ride_time");
														Data.driverRideRequests.add(new MealRideRequest(engagementId, userId,
															new LatLng(latitude, longitude), startTime, address,
															businessId, referenceId, rideTime, fareFactor));
													} else if (BusinessType.FATAFAT.getOrdinal() == businessId) {
														int orderAmount = jObj.getInt("order_amount");
														Log.e("orderAmount", "=" + orderAmount);
														Data.driverRideRequests.add(new FatafatRideRequest(engagementId, userId,
															new LatLng(latitude, longitude), startTime, address,
															businessId, referenceId, orderAmount, fareFactor));
													}

													startRing(this);

													RequestTimeoutTimerTask requestTimeoutTimerTask = new RequestTimeoutTimerTask(this, engagementId);
													requestTimeoutTimerTask.startTimer(requestTimeOutMillis);

													notificationManagerResume(this, "You have got a new request.", true);
													HomeActivity.appInterruptHandler.onNewRideRequest();

													Log.e("referenceId", "=" + referenceId);
												}
											}
										} else {
											notificationManager(this, "You have got a new request.", true);

											startRing(this);
											RequestTimeoutTimerTask requestTimeoutTimerTask = new RequestTimeoutTimerTask(this, engagementId);
											requestTimeoutTimerTask.startTimer(requestTimeOutMillis);
										}

									} else if (PushFlags.REQUEST_CANCELLED.getOrdinal() == flag) {

										String engagementId = jObj.getString("engagement_id");
										clearNotifications(this);

										stopRing();

										if (HomeActivity.appInterruptHandler != null) {
											Data.driverRideRequests.remove(new DriverRideRequest(engagementId));
											HomeActivity.appInterruptHandler.onCancelRideRequest(engagementId, false);
										}

									} else if (PushFlags.RIDE_ACCEPTED_BY_OTHER_DRIVER.getOrdinal() == flag) {

										String engagementId = jObj.getString("engagement_id");
										clearNotifications(this);

										stopRing();

										if (HomeActivity.appInterruptHandler != null) {
											Data.driverRideRequests.remove(new DriverRideRequest(engagementId));
											HomeActivity.appInterruptHandler.onCancelRideRequest(engagementId, true);
										}

									} else if (PushFlags.REQUEST_TIMEOUT.getOrdinal() == flag) {

										String engagementId = jObj.getString("engagement_id");
										clearNotifications(this);

										stopRing();

										if (HomeActivity.appInterruptHandler != null) {
											Data.driverRideRequests.remove(new DriverRideRequest(engagementId));
											HomeActivity.appInterruptHandler.onRideRequestTimeout(engagementId);
										}

									} else if (PushFlags.RIDE_CANCELLED_BY_CUSTOMER.getOrdinal() == flag) {
										Prefs.with(this).save(SPLabels.RECEIVE_REQUESTS, 1);

										SoundMediaPlayer.startSound(GCMIntentService.this, R.raw.cancellation_ring, 2, true, true);

										String logMessage = jObj.getString("message");
										if (HomeActivity.appInterruptHandler != null) {
											HomeActivity.appInterruptHandler.onChangeStatePushReceived();
											notificationManagerResume(this, logMessage, true);
										} else {
											notificationManager(this, logMessage, true);
										}
									} else if (PushFlags.CHANGE_STATE.getOrdinal() == flag) {

										Prefs.with(this).save(SPLabels.RECEIVE_REQUESTS, 1);

										String logMessage = jObj.getString("message");
										if (HomeActivity.appInterruptHandler != null) {
											HomeActivity.appInterruptHandler.onChangeStatePushReceived();
											notificationManagerResume(this, logMessage, false);
										} else {
											notificationManager(this, logMessage, false);
										}
									} else if (PushFlags.DISPLAY_MESSAGE.getOrdinal() == flag) {
										String message1 = jObj.getString("message");
										if (HomeActivity.activity == null) {
											notificationManager(this, "" + message1, false);
										} else {
											notificationManagerResume(this, "" + message1, false);
										}
									} else if (PushFlags.TOGGLE_LOCATION_UPDATES.getOrdinal() == flag) {
										int toggleLocation = jObj.getInt("toggle_location");
										if (1 == toggleLocation) {
											new DriverServiceOperations().startDriverService(GCMIntentService.this);
										} else {
											new DriverServiceOperations().stopAndScheduleDriverService(GCMIntentService.this);
										}
									} else if (PushFlags.MANUAL_ENGAGEMENT.getOrdinal() == flag) {
										Database2.getInstance(this).updateDriverManualPatchPushReceived(Database2.YES);
										startRingWithStopHandler(this);
										String message1 = jObj.getString("message");
										if (HomeActivity.appInterruptHandler != null) {
											HomeActivity.appInterruptHandler.onManualDispatchPushReceived();
											notificationManagerResume(this, message1, true);
										} else {
											notificationManager(this, message1, true);
										}
									} else if (PushFlags.HEARTBEAT.getOrdinal() == flag) {
										try {
											String uuid = jObj.getString("uuid");
											sendHeartbeatAckToServer(this, uuid, currentTimeUTC);
										} catch (Exception e) {
											e.printStackTrace();
										}
									} else if (PushFlags.STATION_CHANGED.getOrdinal() == flag) {
										String message1 = jObj.getString("message");
										if (HomeActivity.appInterruptHandler != null) {
											HomeActivity.appInterruptHandler.onStationChangedPushReceived();
											notificationManagerResume(this, message1, false);
										} else {
											notificationManager(this, message1, false);
										}
									} else if (PushFlags.CHANGE_PORT.getOrdinal() == flag) {
										sendChangePortAckToServer(this, jObj);
									} else if (PushFlags.UPDATE_CUSTOMER_BALANCE.getOrdinal() == flag) {
										int userId = jObj.getInt("user_id");
										double balance = jObj.getDouble("balance");
										if (HomeActivity.appInterruptHandler != null) {
											HomeActivity.appInterruptHandler.onCashAddedToWalletByCustomer(userId, balance);
										}
									} else if (PushFlags.UPDATE_DROP_LOCATION.getOrdinal() == flag) {
										double dropLatitude = jObj.getDouble("op_drop_latitude");
										double dropLongitude = jObj.getDouble("op_drop_longitude");
										String engagementId = jObj.getString("engagement_id");
										if (HomeActivity.appInterruptHandler != null) {
											HomeActivity.appInterruptHandler.onDropLocationUpdated(engagementId, new LatLng(dropLatitude, dropLongitude));
										}
									}

								} catch (Exception e) {
									e.printStackTrace();
								}

							}

						} catch (Exception e) {
							e.printStackTrace();
						}
					}


				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Release the wake lock provided by the WakefulBroadcastReceiver.

        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }


    public static MediaPlayer mediaPlayer;
    public static Vibrator vibrator;

    public static void startRing(Context context) {
        try {
            stopRing();
            vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator.hasVibrator()) {
                long[] pattern = {0, 1350, 3900,
                    1350, 3900,
                    1350, 3900,
                    1350, 3900,
                    1350, 3900,
                    1350, 3900,
                    1350, 3900,
                    1350, 3900,
                    1350, 3900,
                    1350, 3900,
                    1350, 3900};
                vibrator.vibrate(pattern, 1);
            }
            AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
//				am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
            mediaPlayer = MediaPlayer.create(context, R.raw.telephone_ring);
            mediaPlayer.setLooping(true);
            mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    try {
                        vibrator.cancel();
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                        mediaPlayer.release();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            mediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static CountDownTimer ringStopTimer;

    public static void startRingWithStopHandler(Context context) {
        try {
            stopRing();
            vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator.hasVibrator()) {
                long[] pattern = {0, 1350, 3900,
                    1350, 3900,
                    1350, 3900,
                    1350, 3900,
                    1350, 3900,
                    1350, 3900,
                    1350, 3900,
                    1350, 3900,
                    1350, 3900,
                    1350, 3900,
                    1350, 3900};
                vibrator.vibrate(pattern, 1);
            }
            AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
//				am.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
            mediaPlayer = MediaPlayer.create(context, R.raw.telephone_ring);
            mediaPlayer.setLooping(true);
            mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    try {
                        vibrator.cancel();
                        mediaPlayer.stop();
                        mediaPlayer.reset();
                        mediaPlayer.release();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            mediaPlayer.start();


            ringStopTimer = new CountDownTimer(20000, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {
                    Log.e("millisUntilFinished", "=" + millisUntilFinished);
                }

                @Override
                public void onFinish() {
                    stopRing();
                }
            };
            ringStopTimer.start();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void stopRing() {
        try {
            if (vibrator != null) {
                vibrator.cancel();
            }
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.stop();
                mediaPlayer.reset();
                mediaPlayer.release();
            }
            if (ringStopTimer != null) {
                ringStopTimer.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    class RequestTimeoutTimerTask {

        public Timer timer;
        public TimerTask timerTask;
        public Context context;
        public String engagementId;
        public long millisInFuture;

        public RequestTimeoutTimerTask(Context context, String engagementId) {
            this.context = context;
            this.engagementId = engagementId;
            Log.i("RequestTimeoutTimerTask", "=instantiated");
        }

        public void startTimer(long millisInFuture) {
            stopTimer();

            this.millisInFuture = millisInFuture;

            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    if (Data.driverRideRequests != null) {
                        boolean removed = Data.driverRideRequests.remove(new DriverRideRequest(engagementId));
                        if (removed) {
                            if (HomeActivity.appInterruptHandler != null) {
                                HomeActivity.appInterruptHandler.onRideRequestTimeout(engagementId);
                            }
                            clearNotifications(context);
                            stopRing();
                        }
                    }
                    Log.i("RequestTimeoutTimerTask", "onFinish");
                    stopTimer();
                }
            };
            timer.schedule(timerTask, millisInFuture);
        }

        public void stopTimer() {
            try {
                this.millisInFuture = 0;
                if (timerTask != null) {
                    timerTask.cancel();
                    timerTask = null;
                }
                if (timer != null) {
                    timer.cancel();
                    timer.purge();
                    timer = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }


    public String getNetworkName(Context context) {
        try {
            TelephonyManager telephonyManager = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE));
            String simOperatorName = telephonyManager.getSimOperatorName();
            String operatorName = telephonyManager.getNetworkOperatorName();
            return simOperatorName + " " + operatorName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "not found";
    }




    public void sendRequestAckToServer(final Context context, final String engagementId, final String actTimeStamp) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String accessToken = Database2.getInstance(context).getDLDAccessToken();
                    if ("".equalsIgnoreCase(accessToken)) {
                        DriverLocationUpdateService.updateServerData(context);
                        accessToken = Database2.getInstance(context).getDLDAccessToken();
                    }

                    String serverUrl = Database2.getInstance(context).getDLDServerUrl();
                    String networkName = getNetworkName(context);


                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("access_token", accessToken);
                    params.put("engagement_id", engagementId);
                    params.put("ack_timestamp", actTimeStamp);
                    params.put("network_name", networkName);


                    Response response = RestClient.getApiServices().sendRequestAckToServerRetro(params);
                    String result = new String(((TypedByteArray) response.getBody()).getBytes());

                    JSONObject jObj = new JSONObject(result);
                    if (jObj.has("flag")) {
                        int flag = jObj.getInt("flag");
                        if (ApiResponseFlags.ACK_RECEIVED.getOrdinal() == flag) {
                            String log = jObj.getString("log");
                            Log.e("ack to server successfull", "=" + log);
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }



//    Retrofit

    public void sendHeartbeatAckToServer(final Context context, final String uuid, final String ackTimeStamp) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
					String networkName = getNetworkName(context);

                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("uuid", uuid);
                    params.put("timestamp", ackTimeStamp);
                    params.put("network_name", networkName);

                    RestClient.getApiServices().sendHeartbeatAckToServerRetro(params, new Callback<RegisterScreenResponse>() {
						@Override
						public void success(RegisterScreenResponse registerScreenResponse, Response response) {
							Log.v("RetroFIT11", String.valueOf(response));
						}

						@Override
						public void failure(RetrofitError error) {
							Log.v("RetroFIT1", String.valueOf(error));

						}
					});

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //context.sendBroadcast(new Intent("com.google.android.intent.action.GTALK_HEARTBEAT"));
    //context.sendBroadcast(new Intent("com.google.android.intent.action.MCS_HEARTBEAT"));



    public void sendChangePortAckToServer(final Context context, final JSONObject jObject1) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SplashNewActivity.initializeServerURL(context);
                    Pair<String, String> accessTokenPair = JSONParser.getAccessTokenPair(context);

                    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("access_token", accessTokenPair.first));

                    Response response = RestClient.getApiServices().sendChangePortAckToServerRetro(accessTokenPair.first);
                    String result = new String(((TypedByteArray) response.getBody()).getBytes());

                    if (result.contains(HttpRequester.SERVER_TIMEOUT)) {
                    } else {
                        new JSONParser().parsePortNumber(context, jObject1);
                    }

                    nameValuePairs = null;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}





