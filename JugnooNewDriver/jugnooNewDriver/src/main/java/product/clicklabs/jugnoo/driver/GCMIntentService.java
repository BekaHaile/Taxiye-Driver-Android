package product.clicklabs.jugnoo.driver;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.fugu.FuguNotificationConfig;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import product.clicklabs.jugnoo.driver.chat.ChatActivity;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.CustomerInfo;
import product.clicklabs.jugnoo.driver.datastructure.DriverScreenMode;
import product.clicklabs.jugnoo.driver.datastructure.EngagementStatus;
import product.clicklabs.jugnoo.driver.datastructure.PushFlags;
import product.clicklabs.jugnoo.driver.datastructure.RingData;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.datastructure.SharingRideData;
import product.clicklabs.jugnoo.driver.datastructure.UserData;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse;
import product.clicklabs.jugnoo.driver.selfAudit.SelfAuditActivity;
import product.clicklabs.jugnoo.driver.services.ApiAcceptRideServices;
import product.clicklabs.jugnoo.driver.services.DownloadService;
import product.clicklabs.jugnoo.driver.services.FetchDataUsageService;
import product.clicklabs.jugnoo.driver.services.FetchMFileService;
import product.clicklabs.jugnoo.driver.ui.DriverSplashActivity;
import product.clicklabs.jugnoo.driver.utils.DateOperations;
import product.clicklabs.jugnoo.driver.utils.EventsHolder;
import product.clicklabs.jugnoo.driver.utils.FirebaseEvents;
import product.clicklabs.jugnoo.driver.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.driver.utils.FlurryEventNames;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.PermissionCommon;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import product.clicklabs.jugnoo.driver.utils.RSA;
import product.clicklabs.jugnoo.driver.utils.SoundMediaPlayer;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class GCMIntentService extends FirebaseMessagingService {

	public static int PROMOTION_ID = 100;
	public static final long REQUEST_TIMEOUT = 120000;
	private static final long WAKELOCK_TIMEOUT = 5000;
	public static final int DRIVER_AVAILABILTY_TIMEOUT_REQUEST_CODE = 117;

	public static final int NOTIFICATON_SMALL_ICON = R.drawable.ic_notification_small_drawable;
	public static final int NOTIFICATION_BIG_ICON = R.drawable.app_icon;
	FuguNotificationConfig fuguNotificationConfig = new FuguNotificationConfig();
	public GCMIntentService() {
	}


	@SuppressWarnings("deprecation")
	public static void notificationManager(Context context, String message, boolean ring) {

		try {
			long when = System.currentTimeMillis();

			NotificationManager notificationManager = GCMIntentService.getNotificationManager(context, Constants.NOTIF_CHANNEL_DEFAULT);

			Log.v("message", "," + message);

			Intent notificationIntent = new Intent(context, DriverSplashActivity.class);


			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, 0);

			NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Constants.NOTIF_CHANNEL_DEFAULT);
			builder.setAutoCancel(true);
			builder.setContentTitle(context.getResources().getString(R.string.app_name));
			builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
			builder.setContentText(message);
			builder.setTicker(message);
			builder.setChannelId(Constants.NOTIF_CHANNEL_DEFAULT);

			if (ring) {
				builder.setLights(Color.GREEN, 500, 500);
			} else {
				builder.setDefaults(Notification.DEFAULT_ALL);
			}

			builder.setWhen(when);
			builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),NOTIFICATION_BIG_ICON));
			builder.setSmallIcon(NOTIFICATON_SMALL_ICON);
			builder.setContentIntent(intent);


			Notification notification = builder.build();
			if (notificationManager != null) {
				notificationManager.notify(1, notification);
			}

			PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
			WakeLock wl;
			if (pm != null) {
				wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
				wl.acquire(WAKELOCK_TIMEOUT);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("deprecation")
	public static void notificationManagerResume(Context context, String message, boolean ring) {

		try {
			long when = System.currentTimeMillis();

			NotificationManager notificationManager = GCMIntentService.getNotificationManager(context, Constants.NOTIF_CHANNEL_DEFAULT);

			Log.v("message", "," + message);

			Intent notificationIntent = new Intent(context, HomeActivity.class);

			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

			NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Constants.NOTIF_CHANNEL_DEFAULT);
			builder.setAutoCancel(true);
			builder.setContentTitle(context.getResources().getString(R.string.app_name));
			builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
			builder.setContentText(message);
			builder.setTicker(message);
			builder.setChannelId(Constants.NOTIF_CHANNEL_DEFAULT);

			if (ring) {
				builder.setLights(Color.GREEN, 500, 500);
			} else {
				builder.setDefaults(Notification.DEFAULT_ALL);
			}

			builder.setWhen(when);
			builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),NOTIFICATION_BIG_ICON));
			builder.setSmallIcon(NOTIFICATON_SMALL_ICON);
			builder.setContentIntent(intent);


			Notification notification = builder.build();
			if (notificationManager != null) {
				notificationManager.notify(1, notification);
			}


		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	@SuppressWarnings("deprecation")
	public static void notificationManagerResumeAction(Context context, String message, boolean ring, String engagementId,
													   int referenceId, String userId, int perfectRide,
													   int isPooled, int isDelivery, int isDeliveryPool, int reverseBid) {

		try {
			long when = System.currentTimeMillis();

			NotificationManager notificationManager = GCMIntentService.getNotificationManager(context, Constants.NOTIF_CHANNEL_DEFAULT);

			Log.v("message", "," + message);

			Intent notificationIntent = new Intent();
			if (HomeActivity.appInterruptHandler == null) {
				notificationIntent.setClass(context, DriverSplashActivity.class);
			} else {
				notificationIntent.setClass(context, HomeActivity.class);
			}

			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

			NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Constants.NOTIF_CHANNEL_DEFAULT);
			builder.setAutoCancel(true);
			builder.setContentTitle(context.getResources().getString(R.string.app_name));
			builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
			builder.setContentText(message);
			builder.setTicker(message);
			builder.setChannelId(Constants.NOTIF_CHANNEL_DEFAULT);
//			builder.setVisibility(Notification.VISIBILITY_PUBLIC);

			if (ring) {
				builder.setLights(Color.GREEN, 500, 500);
			} else {
				builder.setDefaults(Notification.DEFAULT_ALL);
			}

			builder.setWhen(when);
			builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),NOTIFICATION_BIG_ICON));
			builder.setSmallIcon(NOTIFICATON_SMALL_ICON);
			builder.setContentIntent(intent);


			if(reverseBid != 1) {
				if (HomeActivity.appInterruptHandler != null) {
					Intent intentAcc = new Intent(context, HomeActivity.class);
					intentAcc.putExtra("type", "accept");
					intentAcc.putExtra("engagement_id", engagementId);
					intentAcc.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					PendingIntent pendingIntentAccept = PendingIntent.getActivity(context, 1, intentAcc, PendingIntent.FLAG_UPDATE_CURRENT);
					builder.addAction(R.drawable.tick_30_px, "Accept", pendingIntentAccept);

					Intent intentCanc = new Intent(context, HomeActivity.class);
					intentCanc.putExtra("type", "cancel");
					intentCanc.putExtra("engagement_id", engagementId);
					intentCanc.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					PendingIntent pendingIntentCancel = PendingIntent.getActivity(context, 2, intentCanc, PendingIntent.FLAG_UPDATE_CURRENT);
					builder.addAction(R.drawable.cross_30_px, "Cancel", pendingIntentCancel);

				} else {

					if (perfectRide == 1) {
						Intent intentAccKill = new Intent(context, DriverSplashActivity.class);
						intentAccKill.putExtra("type", "accept");
						intentAccKill.putExtra("engagement_id", engagementId);
						intentAccKill.putExtra("referrence_id", referenceId);
						intentAccKill.putExtra("user_id", userId);
						intentAccKill.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
						PendingIntent pendingIntentAccept = PendingIntent.getActivity(context, 1, intentAccKill, PendingIntent.FLAG_UPDATE_CURRENT);
						builder.addAction(R.drawable.tick_30_px, "Accept", pendingIntentAccept);
					} else {
						Intent intentAccKill = new Intent(context, ApiAcceptRideServices.class);
						intentAccKill.putExtra("type", "accept");
						intentAccKill.putExtra("engagement_id", engagementId);
						intentAccKill.putExtra("referrence_id", referenceId);
						intentAccKill.putExtra(Constants.KEY_IS_POOLED, isPooled);
						intentAccKill.putExtra(Constants.KEY_IS_DELIVERY, isDelivery);
						intentAccKill.putExtra(Constants.KEY_IS_DELIVERY_POOL, isDeliveryPool);
						intentAccKill.putExtra("user_id", userId);
						Log.i("accceptRideGCM Logs", "" + engagementId + " " + userId + " " + referenceId);
						intentAccKill.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
						PendingIntent pendingIntentAccept = PendingIntent.getService(context, 1, intentAccKill, PendingIntent.FLAG_UPDATE_CURRENT);
						builder.addAction(R.drawable.tick_30_px, "Accept", pendingIntentAccept);
					}


					Intent intentCancKill = new Intent(context, DriverSplashActivity.class);
					intentCancKill.putExtra("type", "cancel");
					intentCancKill.putExtra("engagement_id", engagementId);
					intentCancKill.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					PendingIntent pendingIntentCancel = PendingIntent.getActivity(context, 2, intentCancKill, PendingIntent.FLAG_UPDATE_CURRENT);
					builder.addAction(R.drawable.cross_30_px, "Cancel", pendingIntentCancel);
				}
			}


			Notification notification = builder.build();

			Prefs.with(context).save(SPLabels.NOTIFICATION_ID, Prefs.with(context).getInt(SPLabels.NOTIFICATION_ID, 0) + 1);
			if (notificationManager != null) {
				notificationManager.notify(Integer.parseInt(engagementId), notification);
			}


			PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
			WakeLock wl;
			if (pm != null) {
				wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
				wl.acquire(WAKELOCK_TIMEOUT);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	@SuppressWarnings("deprecation")
	public static void notificationManagerCustomID(Context context, String title, String message, int notificationId,
												   Class notifClass, Bitmap bitmap) {

		try {
			long when = System.currentTimeMillis();

			NotificationManager notificationManager = GCMIntentService.getNotificationManager(context, Constants.NOTIF_CHANNEL_DEFAULT);
			Log.v("message", "," + message);
			Intent notificationIntent = new Intent(context, notifClass);

			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

			NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Constants.NOTIF_CHANNEL_DEFAULT);
			builder.setAutoCancel(true);
			builder.setContentTitle(context.getResources().getString(R.string.app_name));
			if (bitmap == null) {
				builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
			} else {
				builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap)
						.setBigContentTitle(title).setSummaryText(message));
			}
			builder.setContentText(message);
			builder.setTicker(message);
			builder.setDefaults(Notification.DEFAULT_ALL);
			builder.setWhen(when);
			builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),NOTIFICATION_BIG_ICON));
			builder.setSmallIcon(NOTIFICATON_SMALL_ICON);
			builder.setContentIntent(intent);
			builder.setChannelId(Constants.NOTIF_CHANNEL_DEFAULT);
			builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

			Notification notification = builder.build();
			if (notificationManager != null) {
				notificationManager.notify(notificationId, notification);
			}
			PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
			WakeLock wl;
			if (pm != null) {
				wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
				wl.acquire(WAKELOCK_TIMEOUT);
			}



		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("deprecation")
	public static void notificationManagerChat(Context context, String title, String message, int notificationId,
												   Class notifClass, Bitmap bitmap) {

		try {
			long when = System.currentTimeMillis();

			NotificationManager notificationManager = GCMIntentService.getNotificationManager(context, Constants.NOTIF_CHANNEL_DEFAULT);
			Log.v("message", "," + message);

			Intent notificationIntent = new Intent(context, notifClass);
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

			NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Constants.NOTIF_CHANNEL_DEFAULT);
			builder.setAutoCancel(true);
			builder.setContentTitle(context.getResources().getString(R.string.app_name));
			if (bitmap == null) {
				builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
			} else {
				builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap)
						.setBigContentTitle(title).setSummaryText(message));
			}
			builder.setContentText(message);
			builder.setTicker(message);
			builder.setDefaults(Notification.DEFAULT_ALL);
			builder.setWhen(when);
			builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),NOTIFICATION_BIG_ICON));
			builder.setSmallIcon(NOTIFICATON_SMALL_ICON);
			builder.setContentIntent(intent);
			builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
			builder.setChannelId(Constants.NOTIF_CHANNEL_DEFAULT);



			Notification notification = builder.build();
			PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
			WakeLock wl;
			if (pm != null) {
				wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
				wl.acquire(WAKELOCK_TIMEOUT);
			}

			if (notificationManager != null) {
				notificationManager.notify(notificationId, notification);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	@SuppressWarnings("deprecation")
	public static void notificationManagerCustomIDAudit(Context context, String title, String message, int notificationId,
														Class notifClass, Bitmap bitmap) {

		try {
			long when = System.currentTimeMillis();

			NotificationManager notificationManager = GCMIntentService.getNotificationManager(context, Constants.NOTIF_CHANNEL_DEFAULT);
			Log.v("message", "," + message);
			Intent notificationIntent = new Intent(context, notifClass);

			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

			NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Constants.NOTIF_CHANNEL_DEFAULT);
			builder.setAutoCancel(true);
			builder.setContentTitle(context.getResources().getString(R.string.app_name));
			if (bitmap == null) {
				builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
			} else {
				builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap)
						.setBigContentTitle(title).setSummaryText(message));
			}
			builder.setContentText(message);
			builder.setTicker(message);
			builder.setDefaults(Notification.DEFAULT_ALL);
			builder.setWhen(when);
			builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),NOTIFICATION_BIG_ICON));
			builder.setSmallIcon(NOTIFICATON_SMALL_ICON);
			builder.setContentIntent(intent);
			builder.setChannelId(Constants.NOTIF_CHANNEL_DEFAULT);

			Notification notification = builder.build();
			if (notificationManager != null) {
				notificationManager.notify(notificationId, notification);
			}

			PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
			WakeLock wl;
			if (pm != null) {
				wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
				wl.acquire(WAKELOCK_TIMEOUT);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void clearNotifications(Context context) {
		try {
			NotificationManager notificationManager = GCMIntentService.getNotificationManager(context, Constants.NOTIF_CHANNEL_DEFAULT);
			if (notificationManager != null) {
				notificationManager.cancelAll();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	public void onMessageReceived(RemoteMessage remoteMessage) {
//		super.onMessageReceived(remoteMessage);

		if (fuguNotificationConfig.isFuguNotification(remoteMessage.getData())) {
			fuguNotificationConfig.setSmallIcon(NOTIFICATON_SMALL_ICON);
			//your icon drawable
			fuguNotificationConfig.setLargeIcon(R.mipmap.ic_launcher);
			fuguNotificationConfig.setNotificationSoundEnabled(true);
			fuguNotificationConfig.setPriority(NotificationCompat.PRIORITY_HIGH);
			fuguNotificationConfig.showNotification(getApplicationContext(),
					remoteMessage.getData());
			return;
		}


		onHandleIntent(remoteMessage);

	}

	public void onHandleIntent(RemoteMessage intent) {
		try {
			Log.i("Recieved a gcm message arg1...", "," + intent.getData());
			String currentTimeUTC = DateOperations.getCurrentTimeInUTC();
			String currentTime = DateOperations.getCurrentTime();

			String SHARED_PREF_NAME1 = "myPref", SP_ACCESS_TOKEN_KEY = "access_token";

			SharedPreferences pref1 = getSharedPreferences(SHARED_PREF_NAME1, Context.MODE_PRIVATE);
			final String accessToken = pref1.getString(SP_ACCESS_TOKEN_KEY, "");
			if (!"".equalsIgnoreCase(accessToken)) {

				try {
					Log.i("Recieved a gcm message arg1...", "," + intent.getData());

					if (!"".equalsIgnoreCase(intent.getData().get("message"))) {
//						String message = String.valueOf(data.get(KEY_MESSAGE));
						String message = intent.getData().get("message");

						try {
							JSONObject jObj = new JSONObject(message);
							Log.i("push_notification", String.valueOf(jObj));
							int flag = jObj.getInt(Constants.KEY_FLAG);
							String title = jObj.optString(Constants.KEY_TITLE, "Jugnoo");

							if (PushFlags.REQUEST.getOrdinal() == flag) {
								int perfectRide = jObj.optInt(Constants.KEY_PERFECT_RIDE, 0);
								int isPooled = jObj.optInt(Constants.KEY_IS_POOLED, 0);
								int isDelivery = jObj.optInt(Constants.KEY_IS_DELIVERY, 0);
								int isDeliveryPool = 0;
								int changeRing = jObj.optInt(Constants.KEY_RING_TYPE, 0);
								int driverScreenMode = Prefs.with(this).getInt(SPLabels.DRIVER_SCREEN_MODE,
										DriverScreenMode.D_INITIAL.getOrdinal());
								boolean entertainRequest = false;
								if(jObj.optInt(Constants.KEY_RIDE_TYPE,0) == 4){
									isDeliveryPool =1;
								}
								if (1 == perfectRide
										&& DriverScreenMode.D_IN_RIDE.getOrdinal() == driverScreenMode
										&& Prefs.with(GCMIntentService.this).getString(SPLabels.PERFECT_ACCEPT_RIDE_DATA, " ").equalsIgnoreCase(" ")) {
									entertainRequest = true;
								} else if (1 == isPooled
										&& Prefs.with(GCMIntentService.this).getString(SPLabels.PERFECT_ACCEPT_RIDE_DATA, " ").equalsIgnoreCase(" ")) {
									entertainRequest = true;
								} else if (1 == isDelivery
										&& Prefs.with(GCMIntentService.this).getString(SPLabels.PERFECT_ACCEPT_RIDE_DATA, " ").equalsIgnoreCase(" ")) {
									entertainRequest = true;
								} else if (0 == perfectRide && 0 == isPooled
										&& (DriverScreenMode.D_INITIAL.getOrdinal() == driverScreenMode)
										&& Prefs.with(GCMIntentService.this).getString(SPLabels.PERFECT_ACCEPT_RIDE_DATA, " ").equalsIgnoreCase(" ")) {
									entertainRequest = true;
								}
								if(HomeActivity.appInterruptHandler != null){
									HomeActivity.appInterruptHandler.refreshTractionScreen();
								}
								boolean isOffline = Prefs.with(this).getInt(Constants.IS_OFFLINE, 0) == 1;
								if (entertainRequest) {
									String engagementId = jObj.getString(Constants.KEY_ENGAGEMENT_ID);
									String userId = jObj.optString(Constants.KEY_USER_ID, "0");
									double latitude = jObj.getDouble(Constants.KEY_LATITUDE);
									double longitude = jObj.getDouble(Constants.KEY_LONGITUDE);
									double currrentLatitude = jObj.getDouble(Constants.KEY_CURRENT_LATITUDE);
									double currrentLongitude = jObj.getDouble(Constants.KEY_CURRENT_LONGITUDE);
									String startTime = jObj.getString("start_time");
									String address = jObj.getString("address");
									if(Prefs.with(this).getInt(Constants.KEY_SHOW_DROP_ADDRESS_BEFORE_INRIDE, 1) == 0){
										address = jObj.optString(Constants.KEY_PICKUP_ADDRESS, address);
									}
									String dropAddress = jObj.optString(Constants.KEY_DROP_ADDRESS, "");
									String pickupAdress = jObj.optString(Constants.KEY_PICKUP_ADDRESS,"");
									double dryDistance = jObj.optDouble(Constants.KEY_DRY_DISTANCE, 0);
									int totalDeliveries = jObj.optInt(Constants.KEY_TOTAL_DELIVERIES, 0);
									double estimatedFare = jObj.optDouble(Constants.KEY_ESTIMATED_FARE, 0d);
									double cashOnDelivery = jObj.optDouble(Constants.KEY_TOTAL_CASH_TO_COLLECT_DELIVERY, 0d);
									double estimatedDist = jObj.optDouble(Constants.KEY_ESTIMATED_DISTANCE, 0d);
									String estimatedDriverFare = jObj.optString(Constants.KEY_ESTIMATED_DRIVER_FARE, "");
									String currency = jObj.optString(Constants.KEY_CURRENCY, "");
									String pickupTime = jObj.optString(Constants.KEY_PICKUP_TIME);
									JSONObject joRentalInfo = jObj.optJSONObject(Constants.KEY_RENTAL_INFO);
									String strRentalInfo = "";
									if(joRentalInfo != null) {
										if(joRentalInfo.has(Constants.KEY_RENTAL_TIME) && joRentalInfo.optDouble(Constants.KEY_RENTAL_TIME, 0) != 0) {
											double timeInMins = joRentalInfo.getDouble(Constants. KEY_RENTAL_TIME);
											String time;
											if (timeInMins >= 60) {
												int hours =  (int)(timeInMins / 60);
												int  minutes = (int) timeInMins % 60;
												String strMins = minutes > 1 ? getString(R.string.rental_mins).concat(" ") : getString(R.string.rental_min).concat(" ");
												String strHours = hours > 1 ? getString(R.string.rental_hours).concat(" ") : getString(R.string.rental_hour).concat(" ");
												time = strRentalInfo + (Utils.getDecimalFormat().format(hours) + " " +strHours + Utils.getDecimalFormat().format(minutes)+" "+strMins + " | ");
											} else {
												time = strRentalInfo.concat(joRentalInfo.getString(Constants.KEY_RENTAL_TIME).concat(" ").concat(
														timeInMins <= 1 ? getString(R.string.rental_min).concat(" ") : getString(R.string.rental_mins).concat(" | ")));
											}
											strRentalInfo = strRentalInfo.concat(time);
										}
										if(joRentalInfo.has(Constants.KEY_RENTAL_DISTANCE) && joRentalInfo.optDouble(Constants.KEY_RENTAL_DISTANCE, 0) != 0) {
											strRentalInfo = strRentalInfo.concat(getString(R.string.rental_max)).concat(" ").concat(joRentalInfo.getString(Constants.KEY_RENTAL_DISTANCE))
													.concat(" ").concat(UserData.getDistanceUnit(this)).concat(" | ");
										}
										if(joRentalInfo.has(Constants.KEY_RENTAL_AMOUNT) && joRentalInfo.optDouble(Constants.KEY_RENTAL_AMOUNT, 0) != 0) {
											strRentalInfo = strRentalInfo.concat(Utils.formatCurrencyValue(Data.userData.getCurrency(), joRentalInfo.getDouble(Constants.KEY_RENTAL_AMOUNT)));
										}

									}

									if(isPooled == 1 && Prefs.with(this).getInt(Constants.KEY_DRIVER_SHOW_POOL_REQUEST_DEST, 0) == 1){
										address = address + "\n" + getString(R.string.to)+dropAddress;
									}

									ArrayList<String> dropPoints = new ArrayList<>();
									if(jObj.has(Constants.KEY_DROP_POINTS)) {
										try {
											JSONArray jsonArray = jObj.getJSONArray(Constants.KEY_DROP_POINTS);
											for (int i = 0; i < jsonArray.length(); i++) {
												dropPoints.add(jsonArray.getString(i));
											}
										} catch (Exception e) {
											e.printStackTrace();
										}
									}

									String userName = jObj.optString(Constants.KEY_NAME, "");
									int referenceId = jObj.optInt(Constants.KEY_REFERENCE_ID, 0);

									String startTimeLocal = DateOperations.utcToLocal(startTime);
									String endTime = jObj.optString(Constants.KEY_END_TIME, "");
									int reverseBid = jObj.optInt(Constants.KEY_REVERSE_BID, 0);
									int bidPlaced = jObj.optInt(Constants.KEY_BID_PLACED, 0);
									double bidValue = jObj.optInt(Constants.KEY_BID_VALUE, 0);
									double initialBidValue = jObj.optDouble(Constants.KEY_INITIAL_BID_VALUE, 10d);
									double estimatedTripDistance = jObj.optDouble(Constants.KEY_ESTIMATED_TRIP_DISTANCE, 0);
									long requestTimeOutMillis;
									if ("".equalsIgnoreCase(endTime)) {
										long serverStartTimeLocalMillis = DateOperations.getMilliseconds(startTimeLocal);
										long serverStartTimeLocalMillisPlus60 = serverStartTimeLocalMillis + 60000;
										requestTimeOutMillis = serverStartTimeLocalMillisPlus60 - System.currentTimeMillis();
									} else {
										long startEndDiffMillis = DateOperations.getTimeDifference(DateOperations.utcToLocal(endTime),
												startTimeLocal);
										if (startEndDiffMillis < GCMIntentService.REQUEST_TIMEOUT) {
											requestTimeOutMillis = startEndDiffMillis;
										} else {
											requestTimeOutMillis = GCMIntentService.REQUEST_TIMEOUT;
										}
									}
									String distanceDry = "";
									try {
										DecimalFormat decimalFormat = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.ENGLISH));
										distanceDry = decimalFormat.format(dryDistance * UserData.getDistanceUnitFactor(this))
												+" "+ Utils.getDistanceUnit(UserData.getDistanceUnit(this));
									} catch (Resources.NotFoundException e) {
										e.printStackTrace();
									}

									double fareFactor = jObj.optDouble("fare_factor", 1d);

									sendRequestAckToServer(this, engagementId, currentTimeUTC);

									FlurryEventLogger.requestPushReceived(this, engagementId, DateOperations.utcToLocal(startTime), currentTime);

									startTime = DateOperations.getDelayMillisAfterCurrentTime(requestTimeOutMillis);

									if (HomeActivity.appInterruptHandler != null && Data.userData != null) {
										CustomerInfo customerInfo = new CustomerInfo(Integer.parseInt(engagementId),
												Integer.parseInt(userId), new LatLng(latitude, longitude), startTime, address,
												referenceId, fareFactor, EngagementStatus.REQUESTED.getOrdinal(),
												isPooled, isDelivery, isDeliveryPool, totalDeliveries, estimatedFare, userName, dryDistance, cashOnDelivery,
												new LatLng(currrentLatitude, currrentLongitude), estimatedDriverFare,
												dropPoints, estimatedDist,currency, reverseBid, bidPlaced, bidValue, initialBidValue, estimatedTripDistance,
												pickupTime, strRentalInfo,pickupAdress,dropAddress);
										Data.addCustomerInfo(customerInfo);

										if(!isOffline) {
											startRing(this, engagementId, changeRing);
											notificationManagerResumeAction(this, address + "\n" + distanceDry, true, engagementId,
													referenceId, userId, perfectRide,
													isPooled, isDelivery, isDeliveryPool, reverseBid);
										}
										flurryEventForRequestPush(engagementId, driverScreenMode);

										if (jObj.optInt("penalise_driver_timeout", 0) == 1) {
											startTimeoutAlarm(this);
										}
										RequestTimeoutTimerTask requestTimeoutTimerTask = new RequestTimeoutTimerTask(this, engagementId);
										requestTimeoutTimerTask.startTimer(requestTimeOutMillis);
										HomeActivity.appInterruptHandler.onNewRideRequest(perfectRide, isPooled, isDelivery);

										Log.e("referenceId", "=" + referenceId);
									} else {
										if(!isOffline) {
											notificationManagerResumeAction(this, address + "\n" + distanceDry, true, engagementId,
													referenceId, userId, perfectRide,
													isPooled, isDelivery, isDeliveryPool, reverseBid);
											startRing(this, engagementId, changeRing);
										}
										flurryEventForRequestPush(engagementId, driverScreenMode);

										if (jObj.optInt("penalise_driver_timeout", 0) == 1) {
											startTimeoutAlarm(this);
										}

										RequestTimeoutTimerTask requestTimeoutTimerTask = new RequestTimeoutTimerTask(this, engagementId);
										requestTimeoutTimerTask.startTimer(requestTimeOutMillis);
									}
								}

								try {
									if (jObj.optInt("wake_up_lock_enabled", 0) == 1) {
										if (HomeActivity.activity != null) {
											if (!HomeActivity.activity.hasWindowFocus()) {
												Intent newIntent = new Intent(this, HomeActivity.class);
												newIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
												newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
												startActivity(newIntent);
											}
										} else {
											Intent homeScreen = new Intent(this, DriverSplashActivity.class);
											homeScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
											startActivity(homeScreen);
										}
									}
								} catch (Exception e) {
									e.printStackTrace();
								}

							} else if (PushFlags.REQUEST_CANCELLED.getOrdinal() == flag) {

								String engagementId = jObj.getString("engagement_id");
								clearNotifications(this);

								if (HomeActivity.appInterruptHandler != null) {
									Data.removeCustomerInfo(Integer.parseInt(engagementId), EngagementStatus.REQUESTED.getOrdinal());
									HomeActivity.appInterruptHandler.onCancelRideRequest(engagementId, false);
								}
								cancelUploadPathAlarm(this);
								stopRing(false, this);

							} else if (PushFlags.RIDE_ACCEPTED_BY_OTHER_DRIVER.getOrdinal() == flag) {

								String engagementId = jObj.getString("engagement_id");
								clearNotifications(this);

								if (HomeActivity.appInterruptHandler != null) {
									Data.removeCustomerInfo(Integer.parseInt(engagementId), EngagementStatus.REQUESTED.getOrdinal());
									HomeActivity.appInterruptHandler.onCancelRideRequest(engagementId, true);
								}
								cancelUploadPathAlarm(this);
								stopRing(false, this);

							} else if (PushFlags.REQUEST_TIMEOUT.getOrdinal() == flag) {

								String engagementId = jObj.getString("engagement_id");
								clearNotifications(this);

								if (HomeActivity.appInterruptHandler != null) {
									Data.removeCustomerInfo(Integer.parseInt(engagementId), EngagementStatus.REQUESTED.getOrdinal());
									HomeActivity.appInterruptHandler.onRideRequestTimeout(engagementId);
								}
								cancelUploadPathAlarm(this);
								stopRing(false, this);

							} else if (PushFlags.RIDE_CANCELLED_BY_CUSTOMER.getOrdinal() == flag) {
								int ignoreRideRequest = jObj.optInt("update_penalty_ctr", 0);
								if (ignoreRideRequest == 1) {
									new DriverTimeoutCheck().timeoutBuffer(this, 1);
								}

								SoundMediaPlayer.startSound(GCMIntentService.this, R.raw.start_ride_accept_beep, 3, true);
								final String logMessage = jObj.getString("message");
								String engagementId = jObj.optString(Constants.KEY_ENGAGEMENT_ID, "0");
								MyApplication.getInstance().getEngagementSP().removeCustomer(Integer.parseInt(engagementId));
								if (HomeActivity.appInterruptHandler != null) {
									HomeActivity.appInterruptHandler.onChangeStatePushReceived(flag, engagementId, logMessage, 0);
									notificationManagerResume(this, logMessage, true);
								} else {
									notificationManager(this, logMessage, true);
								}

								//broadcast sent to finish chat activity
								Intent intentBr = new Intent(Constants.ACTION_FINISH_ACTIVITY);
								intentBr.putExtra(Constants.KEY_TAG, 1);
								sendBroadcast(intentBr);

							} else if (PushFlags.CHANGE_STATE.getOrdinal() == flag) {

								String logMessage = jObj.getString("message");
								String engagementId = jObj.optString(Constants.KEY_ENGAGEMENT_ID, "0");
								int playRing = jObj.optInt("play_ring", 0);
								MyApplication.getInstance().getEngagementSP().removeCustomer(Integer.parseInt(engagementId));
								if (HomeActivity.appInterruptHandler != null) {
									HomeActivity.appInterruptHandler.onChangeStatePushReceived(flag, engagementId, "", playRing);
									notificationManagerResume(this, logMessage, false);
								} else {
									notificationManager(this, logMessage, false);
								}
							} else if (PushFlags.CHANGE_JUGNOO_AVAILABILITY.getOrdinal() == flag) {
								if (HomeActivity.appInterruptHandler != null) {
									Intent openApp = new Intent(this, DriverSplashActivity.class);
									openApp.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
									startActivity(openApp);
								}
							} else if (PushFlags.DISPLAY_MESSAGE.getOrdinal() == flag) {
								String message1 = jObj.getString("message");
								String campainId = jObj.getString("campaign_id");
								boolean sendAck = jObj.optBoolean("ack_notif", false);

								String picture = jObj.optString(Constants.KEY_PICTURE, "");
								if ("".equalsIgnoreCase(picture)) {
									picture = jObj.optString(Constants.KEY_IMAGE, "");
								}
								if (!"".equalsIgnoreCase(picture)) {
									new BigImageNotifAsync(title, message1, picture).execute();
								} else {
									if (HomeActivity.appInterruptHandler != null) {
										notificationManagerCustomID(this, title, message1, PROMOTION_ID, HomeActivity.class, null);
									} else {
										notificationManagerCustomID(this, title, message1, PROMOTION_ID, DriverSplashActivity.class, null);
									}
								}
								if (sendAck) {
									sendMarketPushAckToServer(this, campainId, currentTimeUTC);
								}

							} else if (PushFlags.DISPLAY_MESSAGE_POPUP.getOrdinal() == flag) {
								String message1 = jObj.getString("message");
								if (HomeActivity.appInterruptHandler != null) {
									notificationManagerCustomID(this, title, message1, PROMOTION_ID, HomeActivity.class, null);
									HomeActivity.appInterruptHandler.showDialogFromPush(message1);
								} else {
									notificationManagerCustomID(this, title, message1, PROMOTION_ID, DriverSplashActivity.class, null);
								}

							} else if (PushFlags.DISPLAY_AUDIT_IMAGE.getOrdinal() == flag) {
								String message1 = jObj.getString("message");

								String picture = jObj.optString(Constants.KEY_PICTURE, "");
								if ("".equalsIgnoreCase(picture)) {
									picture = jObj.optString(Constants.KEY_IMAGE, "");
								}
								if (!"".equalsIgnoreCase(picture)) {
									new BigImageNotifAsync(title, message1, picture).execute();
								} else {
									notificationManagerCustomIDAudit(this, title, message1, PROMOTION_ID, SelfAuditActivity.class, null);
								}

							} else if (PushFlags.MANUAL_ENGAGEMENT.getOrdinal() == flag) {
								Database2.getInstance(this).updateDriverManualPatchPushReceived(Database2.YES);
								startRingWithStopHandler(this, 20000);
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
									MyApplication.getInstance().logEvent(FirebaseEvents.HEARTBEAT_REC+"_", null);
									Map<String, String> articleParams = new HashMap<>();
									articleParams.put("heatbeat_id", uuid);
									try {MyApplication.getInstance().trackEvent("Driver", "heartbeat", uuid, articleParams);} catch (Exception ignored) {}

								} catch (Exception e) {
									e.printStackTrace();
								}
							} else if (PushFlags.UPDATE_CUSTOMER_BALANCE.getOrdinal() == flag) {
								int userId = jObj.getInt("user_id");
								int engagementId = jObj.optInt(Constants.KEY_ENGAGEMENT_ID, 0);
								double balance = jObj.getDouble("balance");
								if (HomeActivity.appInterruptHandler != null) {
									HomeActivity.appInterruptHandler.onCashAddedToWalletByCustomer(engagementId, userId, balance);
								}
							} else if (PushFlags.UPDATE_HEAT_MAP.getOrdinal() == flag) {
								if (HomeActivity.appInterruptHandler != null) {
									HomeActivity.appInterruptHandler.fetchHeatMapDataCall(this);
								}
							} else if (PushFlags.UPDATE_DROP_LOCATION.getOrdinal() == flag) {
								double dropLatitude = jObj.getDouble(Constants.KEY_OP_DROP_LATITUDE);
								double dropLongitude = jObj.getDouble(Constants.KEY_OP_DROP_LONGITUDE);
								String dropAddress = jObj.getString(Constants.KEY_DROP_ADDRESS);
								String engagementId = jObj.getString(Constants.KEY_ENGAGEMENT_ID);
								String message1 = jObj.optString(Constants.KEY_MESSAGE, getString(R.string.drop_location_updated_by_customer));
								if (HomeActivity.appInterruptHandler != null) {
									HomeActivity.appInterruptHandler.onDropLocationUpdated(engagementId, new LatLng(dropLatitude, dropLongitude), dropAddress, message1);
									SoundMediaPlayer.startSound(GCMIntentService.this, R.raw.start_ride_accept_beep, 1, true);
								} else {
									SoundMediaPlayer.startSound(GCMIntentService.this, R.raw.start_ride_accept_beep, 1, true);
								}
							} else if (PushFlags.UPDATE_CUSTOMER_CURRENT_LOCATION.getOrdinal() == flag) {
								double dropLatitude = jObj.getDouble(Constants.KEY_CURRENT_LATITUDE);
								double dropLongitude = jObj.getDouble(Constants.KEY_CURRENT_LONGITUDE);
								if (HomeActivity.appInterruptHandler != null) {
									HomeActivity.appInterruptHandler.updateCustomerLocation(dropLatitude, dropLongitude);
								}
							} else if (PushFlags.OTP_VERIFIED_BY_CALL.getOrdinal() == flag) {
								String otp = jObj.getString("message");
								if (OTPConfirmScreen.OTP_SCREEN_OPEN != null) {
									Intent otpConfirmScreen = new Intent(this, OTPConfirmScreen.class);
									otpConfirmScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
									otpConfirmScreen.putExtra("otp", otp);
									startActivity(otpConfirmScreen);
								}
							} else if (PushFlags.JUGNOO_AUDIO.getOrdinal() == flag) {
								String url = jObj.getString("file_url");
								String id = jObj.getString("file_id");
								int download = jObj.optInt("set_download", 0);

								File myFile = new File("/storage/emulated/0/jugnooFiles/" + id + ".mp3");

								if (myFile.exists() && download == 0) {
									FlurryEventLogger.event(FlurryEventNames.CUSTOM_VOICE_NOTIFICATION);
									startRingCustom(this, myFile.getAbsolutePath());
								} else {
									Intent intent1 = new Intent(Intent.ACTION_SYNC, null, this, DownloadService.class);
									intent1.putExtra("downloadOnly", download);
									intent1.putExtra("file_url", url);
									intent1.putExtra("file_id", id);
									startService(intent1);
									Database2.getInstance(this).insertCustomAudioUrl(url, id);
								}

							} else if (PushFlags.GET_JUGNOO_AUDIO.getOrdinal() == flag) {
								Intent intent1 = new Intent(Intent.ACTION_SYNC, null, this, DownloadService.class);
								int downloadList = 2;
								intent1.putExtra("downloadOnly", downloadList);
								startService(intent1);

							} else if (PushFlags.SEND_DRIVER_CONTACTS.getOrdinal() == flag) {
								if(PermissionCommon.isGranted(Manifest.permission.READ_CONTACTS, this)) {
									Intent intent1 = new Intent(Intent.ACTION_SYNC, null, this, ContactsUploadService.class);
									intent1.putExtra("access_token", JSONParser.getAccessTokenPair(this).first);
									startService(intent1);
								}

							} else if (PushFlags.SEND_M_FILE.getOrdinal() == flag) {
								Intent intent1 = new Intent(Intent.ACTION_SYNC, null, this, FetchMFileService.class);
								intent1.putExtra("access_token", JSONParser.getAccessTokenPair(this).first);
								intent1.putExtra("file_id", jObj.getString("engagement_id"));
								startService(intent1);

							} else if (PushFlags.SEND_DATA_USAGE.getOrdinal() == flag) {
								Intent intent1 = new Intent(Intent.ACTION_SYNC, null, this, FetchDataUsageService.class);
								intent1.putExtra("task_id", "3");
								startService(intent1);

							} else if (PushFlags.SEND_USL_LOG.getOrdinal() == flag) {
								Intent intent1 = new Intent(Intent.ACTION_SYNC, null, this, FetchDataUsageService.class);
								intent1.putExtra("task_id", "4");
								startService(intent1);

							} else if (PushFlags.SEND_DRIVER_MESSAGES.getOrdinal() == flag) {

							} else if (PushFlags.UPDATE_DOCUMENT_LIST.getOrdinal() == flag) {
								Intent fetchDocIntent = new Intent(Constants.ACTION_UPDATE_DOCUMENT_LIST);
								fetchDocIntent.putExtra("access_token", JSONParser.getAccessTokenPair(this).first);
								sendBroadcast(fetchDocIntent);

							} else if(PushFlags.CHAT_MESSAGE.getOrdinal() == flag){
								if(Data.contextiii == null || !(Data.contextiii instanceof ChatActivity)){
									String chatMessage = jObj.getJSONObject("message").optString("chat_message", "");
									Prefs.with(this).save(Constants.KEY_CHAT_COUNT , Prefs.with(this).getInt(Constants.KEY_CHAT_COUNT, 0) + 1);
									if(ChatActivity.CHAT_SCREEN_OPEN == null) {
										notificationManagerChat(this, title, chatMessage, PROMOTION_ID, ChatActivity.class, null);
									}
									Intent setChatCount = new Intent(Constants.ALERT_CHARGING);
									setChatCount.putExtra("type", 1);
									sendBroadcast(setChatCount);
								}  // Nothing

							} else if (PushFlags.SHARING_RIDE_ENDED.getOrdinal() == flag) {
								SharingRideData sharingRideData = new SharingRideData(jObj.getString("engagement_id"),
										jObj.getString("transaction_time"),
										jObj.getString("customer_phone_no"),
										jObj.getDouble("actual_fare"),
										jObj.getDouble("paid_in_cash"),
										jObj.getDouble("account_balance"),jObj.optString("currency"));
								if (HomeActivity.appInterruptHandler != null) {
									Intent intent1 = new Intent(this, SharingRidesActivity.class);
									intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
									intent1.putExtra("sharing_engagement_data", jObj.toString());
									startActivity(intent1);
								}
								notificationManagerCustomID(this, title, getResources().getString(R.string.sharing_payment_recieved)
												+ Utils.hidePhoneNoString(sharingRideData.customerPhoneNumber),
										Integer.parseInt(sharingRideData.sharingEngagementId), DriverSplashActivity.class, null);
							}
							else if (PushFlags.MPESA_DRIVER_SUCCESS_PUSH.getOrdinal() == flag) {
								Intent mpesaPush = new Intent(Constants.UPDATE_MPESA_PRICE);
								mpesaPush.putExtra("to_pay", jObj.getString("to_pay"));
								sendBroadcast(mpesaPush);
								String message1 = jObj.optString("message", " ");
								if (message1 != null && !TextUtils.isEmpty(message1)) {
									if (HomeActivity.appInterruptHandler != null) {
										notificationManagerCustomID(this, title, message1, PROMOTION_ID, HomeActivity.class, null);
									} else {
										notificationManagerCustomID(this, title, message1, PROMOTION_ID, DriverSplashActivity.class, null);
									}
								}
							}

							String message1 = jObj.optString("message", " ");
							savePush(jObj, flag, title, message1);


						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				} catch (Exception e) {
					e.printStackTrace();



				}
			} else if (!"".equalsIgnoreCase(intent.getData().get("message"))) {
				try {
					String message = intent.getData().get("message");
					JSONObject jObj = new JSONObject(message);
					Log.i("push_notification", String.valueOf(jObj));
					int flag = jObj.getInt(Constants.KEY_FLAG);
					if (PushFlags.OTP_VERIFIED_BY_CALL.getOrdinal() == flag) {
						String otp = jObj.getString("message");
						if (OTPConfirmScreen.OTP_SCREEN_OPEN != null) {
							Intent otpConfirmScreen = new Intent(this, OTPConfirmScreen.class);
							otpConfirmScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
							otpConfirmScreen.putExtra("otp", otp);
							startActivity(otpConfirmScreen);
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

//	public void createDemoRequest(Context context, TourResponseModel message) {
//		try {
//			 {
//
//				try {
//					TourResponseModel.RequestResponse jObj = message.responses.requestResponse;
//					Log.i("push_notification", String.valueOf(jObj));
//					int flag = jObj.getFlag();
//					String title = "Jugnoo";
//
//					if (PushFlags.REQUEST.getOrdinal() == flag) {
//						int perfectRide = jObj.getPerfectRide();
//						int isPooled = jObj.getIsPooled();
//						int isDelivery = jObj.getIsDelivery();
//						int isDeliveryPool = 0;
//						int changeRing = jObj.getRingType();
//						int driverScreenMode = Prefs.with(this).getInt(SPLabels.DRIVER_SCREEN_MODE,
//								DriverScreenMode.D_INITIAL.getOrdinal());
//						boolean entertainRequest = false;
//						if(jObj.getRideType() == 4){
//							isDeliveryPool =1;
//						}
//						if (1 == perfectRide
//								&& DriverScreenMode.D_IN_RIDE.getOrdinal() == driverScreenMode
//								&& Prefs.with(GCMIntentService.this).getString(SPLabels.PERFECT_ACCEPT_RIDE_DATA, " ").equalsIgnoreCase(" ")) {
//							entertainRequest = true;
//						} else if (1 == isPooled
//								&& Prefs.with(GCMIntentService.this).getString(SPLabels.PERFECT_ACCEPT_RIDE_DATA, " ").equalsIgnoreCase(" ")) {
//							entertainRequest = true;
//						} else if (1 == isDelivery
//								&& Prefs.with(GCMIntentService.this).getString(SPLabels.PERFECT_ACCEPT_RIDE_DATA, " ").equalsIgnoreCase(" ")) {
//							entertainRequest = true;
//						} else if (0 == perfectRide && 0 == isPooled
//								&& (DriverScreenMode.D_INITIAL.getOrdinal() == driverScreenMode)
//								&& Prefs.with(GCMIntentService.this).getString(SPLabels.PERFECT_ACCEPT_RIDE_DATA, " ").equalsIgnoreCase(" ")) {
//							entertainRequest = true;
//						}
//
//						if (entertainRequest) {
//							String engagementId = String.valueOf(jObj.getEngagementId());
//							String userId = String.valueOf(jObj.getUserId());
//							double latitude = jObj.getLatitude();
//							double longitude = jObj.getLongitude();
//							double currrentLatitude = Double.parseDouble(jObj.getCurrentLatitude());
//							double currrentLongitude = Double.parseDouble(jObj.getCurrentLongitude());
//							String startTime = jObj.getStartTime();
//							String address = jObj.getAddress();
//							double dryDistance = jObj.getDryDistance();
//							int totalDeliveries = 0;
//							double estimatedFare = 0;
//							double cashOnDelivery = 0;
//							String estimatedDriverFare = "0";
//
//							String userName = "";
//							int referenceId = 0;
//
//							String startTimeLocal = DateOperations.utcToLocal(startTime);
//							String endTime = jObj.getEndTime();
//							long requestTimeOutMillis = GCMIntentService.REQUEST_TIMEOUT;
//							if ("".equalsIgnoreCase(endTime)) {
//								long serverStartTimeLocalMillis = DateOperations.getMilliseconds(startTimeLocal);
//								long serverStartTimeLocalMillisPlus60 = serverStartTimeLocalMillis + 60000;
//								requestTimeOutMillis = serverStartTimeLocalMillisPlus60 - System.currentTimeMillis();
//							} else {
//								long startEndDiffMillis = DateOperations.getTimeDifference(DateOperations.utcToLocal(endTime),
//										startTimeLocal);
//								if (startEndDiffMillis < GCMIntentService.REQUEST_TIMEOUT) {
//									requestTimeOutMillis = startEndDiffMillis;
//								} else {
//									requestTimeOutMillis = GCMIntentService.REQUEST_TIMEOUT;
//								}
//							}
//							String distanceDry = "";
//							try {
//								DecimalFormat decimalFormat = new DecimalFormat("#.#", new DecimalFormatSymbols(Locale.ENGLISH));
//								DecimalFormat decimalFormatNoDecimal = new DecimalFormat("#", new DecimalFormatSymbols(Locale.ENGLISH));
//								if (dryDistance >= 1000) {
//									distanceDry = decimalFormat.format(dryDistance / 1000) + context.getResources().getString(R.string.km_away);
//								} else {
//									distanceDry = ""+decimalFormatNoDecimal.format(dryDistance) + " " + context.getResources().getString(R.string.m_away);
//								}
//							} catch (Exception e) {
//								e.printStackTrace();
//							}
//
//							double fareFactor = jObj.getFareFactor();
//
//
//
//							startTime = DateOperations.getDelayMillisAfterCurrentTime(requestTimeOutMillis);
//
//							if (HomeActivity.appInterruptHandler != null) {
//								CustomerInfo customerInfo = new CustomerInfo(Integer.parseInt(engagementId),
//										Integer.parseInt(userId), new LatLng(latitude, longitude), startTime, address,
//										referenceId, fareFactor, EngagementStatus.REQUESTED.getOrdinal(),
//										isPooled, isDelivery, isDeliveryPool, totalDeliveries, estimatedFare, userName, dryDistance, cashOnDelivery,
//										new LatLng(currrentLatitude, currrentLongitude), estimatedDriverFare);
//								Data.addCustomerInfo(customerInfo);
//
//								startRing(context, engagementId, changeRing);
//
//								if (jObj.getPenaliseDriverTimeout() == 1) {
//									startTimeoutAlarm(context);
//								}
//								RequestTimeoutTimerTask requestTimeoutTimerTask = new RequestTimeoutTimerTask(context, engagementId);
//								requestTimeoutTimerTask.startTimer(requestTimeOutMillis);
//								notificationManagerResumeAction(context, address + "\n" + distanceDry, true, engagementId,
//										referenceId, userId, perfectRide,
//										isPooled, isDelivery, isDeliveryPool);
//								HomeActivity.appInterruptHandler.onNewRideRequest(perfectRide, isPooled, isDelivery);
//
//								Log.e("referenceId", "=" + referenceId);
//							} else {
//								notificationManagerResumeAction(context, address + "\n" + distanceDry, true, engagementId,
//										referenceId, userId, perfectRide,
//										isPooled, isDelivery, isDeliveryPool);
//								startRing(context, engagementId, changeRing);
//
//								RequestTimeoutTimerTask requestTimeoutTimerTask = new RequestTimeoutTimerTask(context, engagementId);
//								requestTimeoutTimerTask.startTimer(requestTimeOutMillis);
//							}
//						}
//
//						try {
//							if (jObj.getWakeUpLockEnabled() == 1) {
//								if (HomeActivity.activity != null) {
//									if (!HomeActivity.activity.hasWindowFocus()) {
//										Intent newIntent = new Intent(context, HomeActivity.class);
//										newIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//										newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//										context.startActivity(newIntent);
//									}
//								} else {
//									Intent homeScreen = new Intent(context, DriverSplashActivity.class);
//									homeScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//									context.startActivity(homeScreen);
//								}
//							}
//						} catch (Exception e) {
//							e.printStackTrace();
//						}
//
//					}
//
//					String message1 = "";
////					savePush(jObj, flag, title, message1);
//
//				} catch (Exception e) {
//					e.printStackTrace();
//				}
//			}
//
//		} catch (Exception e) {
//			e.printStackTrace();
//
//			// Release the wake lock provided by the WakefulBroadcastReceiver.
//
//		}
//	}


	public static MediaPlayer mediaPlayer;
	public static Vibrator vibrator;

	public static void startTimeoutAlarm(Context context) {

		boolean alarmUp = (PendingIntent.getBroadcast(context, DRIVER_AVAILABILTY_TIMEOUT_REQUEST_CODE,
				new Intent(context, TimeOutAlarmReceiver.class), PendingIntent.FLAG_NO_CREATE) != null);

		if (!alarmUp) {
			Intent intent = new Intent(context, TimeOutAlarmReceiver.class);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(context, DRIVER_AVAILABILTY_TIMEOUT_REQUEST_CODE,
					intent, PendingIntent.FLAG_UPDATE_CURRENT);

			AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
			Log.i("TimeOutAlarmReceiver", "5");
			alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (Prefs.with(context).getLong(SPLabels.MAX_TIMEOUT_RELIEF, 0)), pendingIntent);
		}
	}

	public static void cancelUploadPathAlarm(Context context) {
		Intent intent = new Intent(context, TimeOutAlarmReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, DRIVER_AVAILABILTY_TIMEOUT_REQUEST_CODE,
				intent, PendingIntent.FLAG_UPDATE_CURRENT);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
		alarmManager.cancel(pendingIntent);
		pendingIntent.cancel();
	}

	public static void startRing(Context context, String engagementId, int ringType) {
		try {

			stopRing(true, context);
			vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
			if (!BuildConfig.DEBUG && vibrator.hasVibrator()) {
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
			if (Data.DEFAULT_SERVER_URL.equalsIgnoreCase(Data.LIVE_SERVER_URL)) {
				if(Prefs.with(context).getInt(Constants.KEY_MAX_SOUND, 1) == 1)
				am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
				if(ringType == 1){
					mediaPlayer = MediaPlayer.create(context, R.raw.delivery_ring);
				}else {
					mediaPlayer = MediaPlayer.create(context, R.raw.telephone_ring);
				}
			}else{
				if(ringType == 1){
					mediaPlayer = MediaPlayer.create(context, R.raw.delivery_ring);
				}else {
					mediaPlayer = MediaPlayer.create(context, R.raw.telephone_ring);
				}
			}

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
			Database2.getInstance(context).insertRingData(Integer.parseInt(engagementId), String.valueOf(System.currentTimeMillis()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static void startRingCustom(Context context, String file) {
		try {
			stopRing(true, context);
			vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
			if (!BuildConfig.DEBUG && vibrator.hasVibrator()) {
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
			if(Prefs.with(context).getInt(Constants.KEY_MAX_SOUND, 1) == 1)
			am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
			Log.i("Music Path", "" + file);
			mediaPlayer = MediaPlayer.create(context, Uri.parse(file));
			mediaPlayer.setLooping(false);
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

	public static void startRingWithStopHandler(final Context context, long time) {
		try {
			stopRing(true, context);
			vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
			if (!BuildConfig.DEBUG && vibrator.hasVibrator()) {
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
			if(Prefs.with(context).getInt(Constants.KEY_MAX_SOUND, 1) == 1)
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


			ringStopTimer = new CountDownTimer(time, 1000) {

				@Override
				public void onTick(long millisUntilFinished) {
					Log.e("millisUntilFinished", "=" + millisUntilFinished);
				}

				@Override
				public void onFinish() {
					stopRing(true, context);
				}
			};
			ringStopTimer.start();


		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static void stopRing(boolean manual, Context context) {
		boolean stopRing;
		if (HomeActivity.appInterruptHandler != null) {
			if (Data.getAssignedCustomerInfosListForStatus(EngagementStatus.REQUESTED.getOrdinal()) != null
					&& Data.getAssignedCustomerInfosListForStatus(EngagementStatus.REQUESTED.getOrdinal()).size() > 0) {
				stopRing = false;
			} else {
				stopRing = true;
			}
		} else {
			stopRing = true;
		}
		if (manual) {
			stopRing = true;
		}
		if (stopRing) {
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
				RingData ringData = Database2.getInstance(context).getRingData("1");
				long timeDiff = System.currentTimeMillis() - ringData.time;
				Database2.getInstance(context).updateRingData(ringData.engagement, String.valueOf(timeDiff));
			} catch (Exception e) {
			}
		}
	}


	private ArrayList<Integer> dontSavePushes = null;

	private ArrayList<Integer> getDontSavePushesArray() {
		if (dontSavePushes == null) {
			dontSavePushes = new ArrayList<>();
//			dontSavePushes.add(PushFlags.WAITING_STARTED.getOrdinal());
//			dontSavePushes.add(PushFlags.WAITING_ENDED.getOrdinal());
//			dontSavePushes.add(PushFlags.NO_DRIVERS_AVAILABLE.getOrdinal());
//			dontSavePushes.add(PushFlags.CHANGE_STATE.getOrdinal());
//			dontSavePushes.add(PushFlags.PAYMENT_RECEIVED.getOrdinal());
//			dontSavePushes.add(PushFlags.CLEAR_ALL_MESSAGE.getOrdinal());
//			dontSavePushes.add(PushFlags.DELETE_NOTIFICATION_ID.getOrdinal());
//			dontSavePushes.add(PushFlags.UPLOAD_CONTACTS_ERROR.getOrdinal());
//			dontSavePushes.add(PushFlags.DRIVER_ETA.getOrdinal());
		}
		return dontSavePushes;
	}

	private void savePush(JSONObject jObj, int flag, String title, String message1) {
		try {
			boolean tryToSave = false;
			if (PushFlags.DISPLAY_MESSAGE.getOrdinal() == flag) {
				tryToSave = true;
			} else if (!getDontSavePushesArray().contains(flag)) {
				int saveNotification = jObj.optInt(Constants.KEY_SAVE_NOTIFICATION, 0);
				if (1 == saveNotification) {
					tryToSave = true;
				}
			}

			if (tryToSave && !"".equalsIgnoreCase(message1)) {
				String picture = jObj.optString(Constants.KEY_PICTURE, "");
				if ("".equalsIgnoreCase(picture)) {
					picture = jObj.optString(Constants.KEY_IMAGE, "");
				}

				message1 = title + "\n" + message1;

				int notificationId = jObj.optInt(Constants.KEY_NOTIFICATION_ID, flag);

				Prefs.with(this).save(SPLabels.NOTIFICATION_UNREAD_COUNT,
						(Prefs.with(this).getInt(SPLabels.NOTIFICATION_UNREAD_COUNT, 0) + 1));

				// store push in database for notificaion center screen...
				String pushArrived = DateOperations.getCurrentTimeInUTC();

				/*if (jObj.has("timeToDisplay") && jObj.has("timeTillDisplay")) {
					Prefs.with(this).save(SPLabels.NOTIFICATION_UNREAD_COUNT, (Prefs.with(this).getInt(SPLabels.NOTIFICATION_UNREAD_COUNT, 0) + 1));
				} else if (jObj.has("timeToDisplay")) {
					Prefs.with(this).save(SPLabels.NOTIFICATION_UNREAD_COUNT, (Prefs.with(this).getInt(SPLabels.NOTIFICATION_UNREAD_COUNT, 0) + 1));
				} else if (jObj.has("timeTillDisplay")) {
					Prefs.with(this).save(SPLabels.NOTIFICATION_UNREAD_COUNT,
							(Prefs.with(this).getInt(SPLabels.NOTIFICATION_UNREAD_COUNT, 0) + 1));
				}*/
				if (EventsHolder.displayPushHandler != null) {
					EventsHolder.displayPushHandler.onDisplayMessagePushReceived();
				}
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
					if (Data.getAssignedCustomerInfosListForStatus(EngagementStatus.REQUESTED.getOrdinal()) != null) {
						boolean removed = Data.removeCustomerInfo(Integer.parseInt(engagementId), EngagementStatus.REQUESTED.getOrdinal());
						if (removed) {
							if (HomeActivity.appInterruptHandler != null) {
								HomeActivity.appInterruptHandler.onRideRequestTimeout(engagementId);
							}
							clearNotifications(context);
							stopRing(true, context);
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
					String accessToken = JSONParser.getAccessTokenPair(context).first;

					String networkName = getNetworkName(context);


					HashMap<String, String> params = new HashMap<String, String>();
					params.put("access_token", accessToken);
					params.put("engagement_id", engagementId);
					params.put("ack_timestamp", actTimeStamp);
					params.put("network_name", networkName);
					HomeUtil.putDefaultParams(params);


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

	public void sendMarketPushAckToServer(final Context context, final String campainId, final String actTimeStamp) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					JSONObject params = new JSONObject();
					try {
						params.put("user_id", Data.userData.getUserId());
						params.put("campaign_id", campainId);
						params.put("ack_timestamp", actTimeStamp);

					} catch (Exception e) {
						e.printStackTrace();
					}

					String encryptData = RSA.encryptWithPublicKeyStr(String.valueOf(params));
//					Response response = RestClient.getApiServices().sendPushAckToServerRetro(encryptData);
					HashMap<String, String> map = new HashMap<String, String>();
					params.put("data", encryptData);
					HomeUtil.putDefaultParams(map);
					Response response = RestClient.getPushAckApiServices().sendPushAckToServerRetro(map);
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
					final long resposeTime = System.currentTimeMillis();
					String networkName = getNetworkName(context);
					Location location = Database2.getInstance(context).getDriverCurrentLocation(context);

					HashMap<String, String> params = new HashMap<String, String>();
					params.put("uuid", uuid);
					params.put("timestamp", ackTimeStamp);
					params.put("network_name", networkName);
					params.put(Constants.KEY_LATITUDE, String.valueOf(location.getLatitude()));
					params.put(Constants.KEY_LONGITUDE, String.valueOf(location.getLongitude()));
					HomeUtil.putDefaultParams(params);

					RestClient.getApiServices().sendHeartbeatAckToServerRetro(params, new Callback<RegisterScreenResponse>() {
						@Override
						public void success(RegisterScreenResponse registerScreenResponse, Response response) {
							Log.v("RetroFIT11", String.valueOf(response));
							FlurryEventLogger.logResponseTime(context, System.currentTimeMillis() - resposeTime, FlurryEventNames.HEARTBEAT_RESPONSE);
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


	private void flurryEventForRequestPush(String engagementId, int driverScreenMode) {
		if (DriverScreenMode.D_INITIAL.getOrdinal() != driverScreenMode
				&& DriverScreenMode.D_REQUEST_ACCEPT.getOrdinal() != driverScreenMode
				&& DriverScreenMode.D_RIDE_END.getOrdinal() != driverScreenMode) {
			FlurryEventLogger.logStartRing(this, driverScreenMode, Utils.getAppVersion(this), engagementId, FlurryEventNames.START_RING_INITIATED);
		}
	}


	private class BigImageNotifAsync extends AsyncTask<String, String, Bitmap> {

		private Bitmap bitmap = null;
		private String title, message, picture;

		public BigImageNotifAsync(String title, String message, String picture) {
			this.picture = picture;
			this.title = title;
			this.message = message;
		}

		@Override
		protected void onPreExecute() {
			// Things to be done before execution of long running operation. For
			// example showing ProgessDialog
		}

		@Override
		protected Bitmap doInBackground(String... params) {
			try {
				URL url = new URL(picture);
				bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			// execution of result of Long time consuming operation
			try {
				if (result == null) {
					notificationManagerCustomID(GCMIntentService.this, title, message, PROMOTION_ID, DriverSplashActivity.class,
							null);
				} else {
					notificationManagerCustomID(GCMIntentService.this, title, message, PROMOTION_ID, DriverSplashActivity.class,
							result);
				}
			} catch (Exception e) {
				e.printStackTrace();
				notificationManagerCustomID(GCMIntentService.this, title, message, PROMOTION_ID, DriverSplashActivity.class,
						null);
			}
		}

	}

	public static NotificationManager getNotificationManager(final Context context, String channel){

		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			// The user-visible name of the channel.
			CharSequence name = context.getString(R.string.notification_channel_default);
			// The user-visible description of the channel.
			String description = context.getString(R.string.notification_channel_description_default);
			int importance = NotificationManager.IMPORTANCE_LOW;
			NotificationChannel mChannel = new NotificationChannel(channel, name, importance);
			// Configure the notification channel.
			mChannel.setDescription(description);
			notificationManager.createNotificationChannel(mChannel);
		}
		return notificationManager;
	}



}





