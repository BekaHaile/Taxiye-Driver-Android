package product.clicklabs.jugnoo.driver;

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
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

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

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import product.clicklabs.jugnoo.driver.chat.ChatActivity;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.CustomerInfo;
import product.clicklabs.jugnoo.driver.datastructure.DriverSubscriptionEnabled;
import product.clicklabs.jugnoo.driver.datastructure.EngagementStatus;
import product.clicklabs.jugnoo.driver.datastructure.PushFlags;
import product.clicklabs.jugnoo.driver.datastructure.RingData;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.datastructure.SharingRideData;
import product.clicklabs.jugnoo.driver.datastructure.UserData;
import product.clicklabs.jugnoo.driver.home.RingtoneTypes;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse;
import product.clicklabs.jugnoo.driver.selfAudit.SelfAuditActivity;
import product.clicklabs.jugnoo.driver.services.ApiAcceptRideServices;
import product.clicklabs.jugnoo.driver.services.DownloadService;
import product.clicklabs.jugnoo.driver.services.FetchDataUsageService;
import product.clicklabs.jugnoo.driver.services.FetchMFileService;
import product.clicklabs.jugnoo.driver.subscription.SubscriptionFragment;
import product.clicklabs.jugnoo.driver.ui.DriverSplashActivity;
import product.clicklabs.jugnoo.driver.utils.DateOperations;
import product.clicklabs.jugnoo.driver.utils.EventsHolder;
import product.clicklabs.jugnoo.driver.utils.FirebaseEvents;
import product.clicklabs.jugnoo.driver.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.driver.utils.FlurryEventNames;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import product.clicklabs.jugnoo.driver.utils.RSA;
import product.clicklabs.jugnoo.driver.utils.SoundMediaPlayer;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

import static product.clicklabs.jugnoo.driver.Constants.KEY_ACCESS_TOKEN;
import static product.clicklabs.jugnoo.driver.Constants.KEY_FLAG;
import static product.clicklabs.jugnoo.driver.Constants.KEY_LATITUDE;
import static product.clicklabs.jugnoo.driver.Constants.KEY_LONGITUDE;

public class GCMIntentService extends FirebaseMessagingService {

	public static int PROMOTION_ID = 100;
	public static final long REQUEST_TIMEOUT = 120000;
	public static final long BID_REQUEST_TIMEOUT = 180000;
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
				wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, BuildConfig.FLAVOR+":notification");
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
//			Notification notification = builder.build();

			Prefs.with(context).save(SPLabels.NOTIFICATION_ID, Prefs.with(context).getInt(SPLabels.NOTIFICATION_ID, 0) + 1);
			if (notificationManager != null) {
				notificationManager.notify(Integer.parseInt(engagementId), notification);
			}


			PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
			WakeLock wl;
			if (pm != null) {
				wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, BuildConfig.FLAVOR+":notification");
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
				wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, BuildConfig.FLAVOR+":notification");
				wl.acquire(WAKELOCK_TIMEOUT);
			}



		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@SuppressWarnings("deprecation")
	public static void notificationManagerChat(Context context, String message, int notificationId, String payload) {

		try {
			long when = System.currentTimeMillis();

			NotificationManager notificationManager = GCMIntentService.getNotificationManager(context, Constants.NOTIF_CHANNEL_DEFAULT);

			Intent notificationIntent = new Intent(context, PushClickActivity.class);
			notificationIntent.putExtra(Constants.KEY_PAYLOAD, payload);
			notificationIntent.putExtra(Constants.KEY_FROM_CHAT_PUSH,Data.getCurrentEngagementId());
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);
			NotificationCompat.Builder builder = new NotificationCompat.Builder(context, Constants.NOTIF_CHANNEL_DEFAULT);
			builder.setAutoCancel(true);
			builder.setContentTitle(context.getResources().getString(R.string.app_name));
				builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
			builder.setContentText(message);
			builder.setTicker(message);
			builder.setDefaults(Notification.DEFAULT_ALL);
			builder.setWhen(when);
			builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(),NOTIFICATION_BIG_ICON));
			builder.setSmallIcon(NOTIFICATON_SMALL_ICON);
			builder.setContentIntent(intent);
			builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
			builder.setChannelId(Constants.NOTIF_CHANNEL_DEFAULT);
			SoundMediaPlayer.startSound(context, R.raw.whats_app_shat_sound, 1, false);




			Notification notification = builder.build();
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
				wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, BuildConfig.FLAVOR+":notification");
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
			Log.i("Recieved a gcm message arg2...", "," + intent.toString());
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
							int flag = jObj.getInt(KEY_FLAG);
							String title = jObj.optString(Constants.KEY_TITLE, "Jugnoo");

							if (PushFlags.REQUEST.getOrdinal() == flag) {
								if (checkSubscriptionExpiry(jObj, this)) {
									int perfectRide = jObj.optInt(Constants.KEY_PERFECT_RIDE, 0);
									int isPooled = jObj.optInt(Constants.KEY_IS_POOLED, 0);
									int isDelivery = jObj.optInt(Constants.KEY_IS_DELIVERY, 0);
									int isDeliveryPool = 0;
									int multiDestCount = jObj.optInt(Constants.KEY_MULTIPLE_STOPS_COUNT, 0);
									int changeRing = jObj.optInt(Constants.KEY_RING_TYPE, 0);

									//to check if any customer is engaged with driver
									ArrayList<CustomerInfo> customerInfos = Data.getAssignedCustomerInfosListForEngagedStatus();

									//to check if only one engaged customer and its state is started
									boolean isSingleCustomerInStart = customerInfos.size() == 1 && customerInfos.get(0).getStatus() == EngagementStatus.STARTED.getKOrdinal();

									boolean entertainRequest = false;
									if(jObj.optInt(Constants.KEY_RIDE_TYPE,0) == 4){
										isDeliveryPool =1;
									}
									if (1 == perfectRide
											&& isSingleCustomerInStart
											&& Prefs.with(GCMIntentService.this).getString(SPLabels.PERFECT_ACCEPT_RIDE_DATA, " ").equalsIgnoreCase(" ")) {
										entertainRequest = true;
									} else if (1 == isPooled
											&& Prefs.with(GCMIntentService.this).getString(SPLabels.PERFECT_ACCEPT_RIDE_DATA, " ").equalsIgnoreCase(" ")) {
										entertainRequest = true;
									} else if (1 == isDelivery
											&& Prefs.with(GCMIntentService.this).getString(SPLabels.PERFECT_ACCEPT_RIDE_DATA, " ").equalsIgnoreCase(" ")) {
										entertainRequest = true;
									} else if (0 == perfectRide && 0 == isPooled
											&& (customerInfos.size() == 0)
											&& Prefs.with(GCMIntentService.this).getString(SPLabels.PERFECT_ACCEPT_RIDE_DATA, " ").equalsIgnoreCase(" ")) {
										entertainRequest = true;
									}
									if(HomeActivity.appInterruptHandler != null){
										HomeActivity.appInterruptHandler.refreshTractionScreen();
									}
									boolean isOffline = Prefs.with(this).getInt(Constants.IS_OFFLINE, 0) == 1;
									String engagementId = jObj.getString(Constants.KEY_ENGAGEMENT_ID);
									int reverseBid = jObj.optInt(Constants.KEY_REVERSE_BID, 0);
									if (entertainRequest) {
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
										String isManual = jObj.optString(Constants.KEY_IS_MANUAL,"");
										int totalDeliveries = jObj.optInt(Constants.KEY_TOTAL_DELIVERIES, 0);
										double estimatedFare = jObj.optDouble(Constants.KEY_ESTIMATED_FARE, 0d);
										double cashOnDelivery = jObj.optDouble(Constants.KEY_TOTAL_CASH_TO_COLLECT_DELIVERY, 0d);
										double estimatedDist = jObj.optDouble(Constants.KEY_ESTIMATED_DISTANCE, 0d);
										String estimatedDriverFare = jObj.optString(Constants.KEY_ESTIMATED_DRIVER_FARE, "");
										String currency = jObj.optString(Constants.KEY_CURRENCY, "");
										String pickupTime = jObj.optString(Constants.KEY_PICKUP_TIME);
										if(TextUtils.isEmpty(pickupTime)){
											pickupTime = jObj.optString(Constants.KEY_SCHEDULED_RIDE_PICKUP_TIME);
										}
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

										if (isPooled == 1 && Prefs.with(this).getInt(Constants.KEY_DRIVER_SHOW_POOL_REQUEST_DEST, 0) == 1) {
											address = address + "\n" + getString(R.string.to) + dropAddress;
										}

										ArrayList<String> dropPoints = new ArrayList<>();
										if (jObj.has(Constants.KEY_DROP_POINTS)) {
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
										String bidCreatedAt = DateOperations.utcToLocal(jObj.optString(Constants.KEY_BID_CREATED_AT, DateOperations.getCurrentTimeInUTC()));
										String endTime = jObj.optString(Constants.KEY_END_TIME, "");
										int bidPlaced = jObj.optInt(Constants.KEY_BID_PLACED, 0);
										double bidValue = jObj.optInt(Constants.KEY_BID_VALUE, 0);
										double initialBidValue = jObj.optDouble(Constants.KEY_INITIAL_BID_VALUE, 10d);
										double estimatedTripDistance = jObj.optDouble(Constants.KEY_ESTIMATED_TRIP_DISTANCE, 0);
										double incrementPercent = jObj.optDouble(Constants.KEY_INCREASE_PERCENTAGE, (double) Prefs.with(this).getFloat(Constants.BID_INCREMENT_PERCENT, 10f));
										int stepSize = jObj.optInt(Constants.KEY_STEP_SIZE, 5);
										long requestTimeOutMillis;
										if ("".equalsIgnoreCase(endTime)) {
											long serverStartTimeLocalMillis = DateOperations.getMilliseconds(startTimeLocal);
											long serverStartTimeLocalMillisPlus60 = serverStartTimeLocalMillis + (reverseBid == 1 ? GCMIntentService.BID_REQUEST_TIMEOUT : GCMIntentService.REQUEST_TIMEOUT);
											requestTimeOutMillis = serverStartTimeLocalMillisPlus60 - System.currentTimeMillis();
										Log.i("GCMIntent_requestTimeOutMillis = ", "=" + requestTimeOutMillis);} else {
											long startEndDiffMillis = DateOperations.getTimeDifference(DateOperations.utcToLocal(endTime),
													startTimeLocal);
											Log.i("GCMIntentService_startEndDiffMillis = ", "=" + startEndDiffMillis);
										if (startEndDiffMillis < GCMIntentService.REQUEST_TIMEOUT || reverseBid == 1) {
												requestTimeOutMillis = startEndDiffMillis;
											} else {
												requestTimeOutMillis = GCMIntentService.REQUEST_TIMEOUT;
											}Log.i("GCMIntent_requestTimeOutMillis = ", "=" + requestTimeOutMillis);
										}
										String distanceDry = "";
										try {
											DecimalFormat decimalFormat = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.ENGLISH));
											distanceDry = decimalFormat.format(dryDistance * UserData.getDistanceUnitFactor(this, false))
													+ " " + Utils.getDistanceUnit(UserData.getDistanceUnit(this));
										} catch (Resources.NotFoundException e) {
											e.printStackTrace();
										}

										double fareFactor = jObj.optDouble("fare_factor", 1d);

										sendRequestAckToServer(this, engagementId, currentTimeUTC);

										FlurryEventLogger.requestPushReceived(this, engagementId, DateOperations.utcToLocal(startTime), currentTime);

										startTime = DateOperations.getDelayMillisAfterCurrentTime(requestTimeOutMillis);


									CustomerInfo customerInfo = new CustomerInfo(Integer.parseInt(engagementId),
											Integer.parseInt(userId), new LatLng(latitude, longitude), startTime, address,
											referenceId, fareFactor, EngagementStatus.REQUESTED.getOrdinal(),
											isPooled, isDelivery, isDeliveryPool, totalDeliveries, estimatedFare, userName, dryDistance, cashOnDelivery,
											new LatLng(currrentLatitude, currrentLongitude), estimatedDriverFare,
											dropPoints, estimatedDist,currency, reverseBid, bidPlaced, bidValue, initialBidValue, estimatedTripDistance,
											pickupTime, strRentalInfo, incrementPercent, stepSize,pickupAdress,dropAddress,startTimeLocal, bidCreatedAt,
											multiDestCount);
									if(isManual.equals("0") || isManual.equals("1035"))
										customerInfo.setIsCorporateRide(0);
									else
										customerInfo.setIsCorporateRide(1);
									Data.addCustomerInfo(customerInfo);

									if(!isOffline) {
										startRing(this, engagementId, changeRing);
										notificationManagerResumeAction(this, address + "\n" + distanceDry, true, engagementId,
												referenceId, userId, perfectRide,
												isPooled, isDelivery, isDeliveryPool, reverseBid);
									}

										if (jObj.optInt("penalise_driver_timeout", 0) == 1) {
											startTimeoutAlarm(this);
										}
										RequestTimeoutTimerTask requestTimeoutTimerTask = new RequestTimeoutTimerTask(this, engagementId);
										requestTimeoutTimerTask.startTimer(requestTimeOutMillis);

									if (!HomeActivity.activity.isFinishing() && HomeActivity.appInterruptHandler != null && Data.userData != null) {
										HomeActivity.appInterruptHandler.onNewRideRequest(perfectRide, isPooled, isDelivery);
										Log.e("referenceId", "=" + referenceId);
									}
								}

									try {
										if (reverseBid == 1) {
											if (HomeActivity.appInterruptHandler == null) {
												Intent intentN = new Intent(this, RequestActivity.class);
												intentN.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
												intentN.putExtra(Constants.KEY_ENGAGEMENT_ID, Integer.parseInt(engagementId));
												startActivity(intentN);
											}
										} else if (jObj.optInt("wake_up_lock_enabled", 0) == 1) {
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
								}

							} else if (PushFlags.REQUEST_CANCELLED.getOrdinal() == flag
									|| PushFlags.RIDE_ACCEPTED_BY_OTHER_DRIVER.getOrdinal() == flag
									|| PushFlags.REQUEST_TIMEOUT.getOrdinal() == flag) {

								String engagementId = jObj.getString("engagement_id");
								String messageInternal = jObj.optString(Constants.KEY_MESSAGE);
								clearNotifications(this);

								Data.removeCustomerInfo(Integer.parseInt(engagementId));
								if (HomeActivity.appInterruptHandler != null) {
									if(PushFlags.REQUEST_TIMEOUT.getOrdinal() == flag){
										HomeActivity.appInterruptHandler.onRideRequestTimeout(engagementId);
									} else {
										HomeActivity.appInterruptHandler.onCancelRideRequest(engagementId, PushFlags.RIDE_ACCEPTED_BY_OTHER_DRIVER.getOrdinal() == flag, messageInternal);
									}
								} else{
									try{LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(RequestActivity.INTENT_ACTION_REFRESH_BIDS));}catch(Exception ignored){}
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
								Data.removeCustomerInfo(Integer.parseInt(engagementId));
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
							}
							else if(PushFlags.NEW_STOP_ADDED.getOrdinal() == flag){
								double dropLatitude = jObj.optDouble(Constants.KEY_OP_DROP_LATITUDE);
								double dropLongitude = jObj.optDouble(Constants.KEY_OP_DROP_LONGITUDE);
								String dropAddress = jObj.optString(Constants.KEY_DROP_ADDRESS);
								String engagementId = jObj.optString(Constants.KEY_ENGAGEMENT_ID);
								String message1 = jObj.optString(Constants.KEY_MESSAGE, getString(R.string.drop_location_updated_by_customer));
								if(jObj.has(Constants.STOP_ID)){
									int stopId = jObj.optInt(Constants.STOP_ID);
									Data.getCurrentCustomerInfo().setStopId(stopId);
									if (HomeActivity.appInterruptHandler != null) {
										HomeActivity.appInterruptHandler.onDropLocationUpdated(engagementId, new LatLng(dropLatitude, dropLongitude), dropAddress, message1);
									}
								} else if (HomeActivity.appInterruptHandler != null) {
									HomeActivity.appInterruptHandler.onNewStopAdded(jObj.getString("message"));
								}
								SoundMediaPlayer.startSound(GCMIntentService.this, R.raw.start_ride_accept_beep, 1, true);
							}
							else if (PushFlags.UPDATE_CUSTOMER_CURRENT_LOCATION.getOrdinal() == flag) {
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
								Prefs.with(this).save(Constants.KEY_CHAT_COUNT , Prefs.with(this).getInt(Constants.KEY_CHAT_COUNT, 0) + 1);
								if(!ChatActivity.isActive){
									String chatMessage = jObj.getJSONObject("message").optString("chat_message", "");
									notificationManagerChat(this, chatMessage, PROMOTION_ID, message);
								}
								Intent setChatCount = new Intent(Constants.INTENT_ACTION_CHAT_REFRESH);
								sendBroadcast(setChatCount);

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
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static boolean checkSubscriptionExpiry(JSONObject object,Context mContext){
		if(object.has(Constants.KEY_DRIVER_SUBSCRIPTION_ENABLED)&&(object.optInt(Constants.KEY_DRIVER_SUBSCRIPTION_ENABLED)== DriverSubscriptionEnabled.MANDATORY.getOrdinal()&&0 == Data.userData.getDeliveryEnabled())) {
			if (object.has("subscription_enabled")) {
				int subscriptionExpired = 0;
				try {
					subscriptionExpired = object.getInt("subscription_enabled");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (subscriptionExpired == 0) {
					callToggleOff(0, mContext);
					return false;
				} else {
					return true;
				}
			} else {
				return true;
			}
		}
		else {
			return true;
		}
		//callToggleOff(0);
		//return false;
	}

	private static void callToggleOff(int toggleFlag,Context context) {
		Log.e("push called","call toggle called");
		try {
			Location location = Database2.getInstance(context).getDriverCurrentLocation(context);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put(KEY_ACCESS_TOKEN, Data.userData.accessToken);
			HomeUtil.putDefaultParams(params);
			params.put("business_id", "1");
			params.put(KEY_FLAG, "" + toggleFlag);
			params.put(KEY_LATITUDE, "" +location.getLatitude());
			params.put(KEY_LONGITUDE, "" + location.getLongitude());
			Response response = RestClient.getApiServices().switchJugnooOnThroughServerRetro(params);
			String result = new String(((TypedByteArray) response.getBody()).getBytes());

			JSONObject jObj = new JSONObject(result);
			final String message = JSONParser.getServerMessage(jObj);
			if (jObj.has(KEY_FLAG)) {
				Log.e("push called","call toggle called1");
				int flag = jObj.getInt(KEY_FLAG);
				if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
				SubscriptionFragment subsFrag = new SubscriptionFragment();
					Bundle args =new Bundle();
					args.putString("AccessToken", Data.userData.accessToken);
					args.putInt("stripe_key", Prefs.with(context).getInt(Constants.KEY_STRIPE_CARDS_ENABLED,0));
					subsFrag.setArguments(args);
					Log.e("push called","call toggle called3");
                     if(MyApplication.getInstance().mContext instanceof HomeActivity) {
						 Log.e("push called","call toggle called2");
						// ((HomeActivity)MyApplication.getInstance().mContext).openSubs(subsFrag);
						 Intent i = new Intent (context,DriverSplashActivity.class);
						 context.startActivity(i);

					 }
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	public static MediaPlayer mediaPlayer;

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
			AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
			if (Data.DEFAULT_SERVER_URL.equalsIgnoreCase(Data.LIVE_SERVER_URL)) {
				if(Prefs.with(context).getInt(Constants.KEY_MAX_SOUND, 1) == 1)
				am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
			}
			mediaPlayer = RingtoneTypes.INSTANCE.getMediaPlayerFromRingtone(context, ringType, true);

			mediaPlayer.setLooping(true);
			mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					try {
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
			AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
			if(Prefs.with(context).getInt(Constants.KEY_MAX_SOUND, 1) == 1)
			am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
			Log.i("Music Path", "" + file);
			mediaPlayer = MediaPlayer.create(context, Uri.parse(file));
			mediaPlayer.setLooping(false);
			mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					try {
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
			AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
			if(Prefs.with(context).getInt(Constants.KEY_MAX_SOUND, 1) == 1)
			am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
			mediaPlayer = RingtoneTypes.INSTANCE.getMediaPlayerFromRingtone(context, 0, true);
			mediaPlayer.setLooping(true);
			mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
				@Override
				public void onCompletion(MediaPlayer mp) {
					try {
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
					CustomerInfo customerInfo = Data.getCustomerInfo(engagementId);
					if(customerInfo != null && customerInfo.getStatus() == EngagementStatus.REQUESTED.getOrdinal()) {
						boolean removed = Data.removeCustomerInfo(Integer.parseInt(engagementId));
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
					params.put(KEY_LATITUDE, String.valueOf(location.getLatitude()));
					params.put(KEY_LONGITUDE, String.valueOf(location.getLongitude()));
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
			int importance = NotificationManager.IMPORTANCE_HIGH;
			NotificationChannel mChannel = new NotificationChannel(channel, name, importance);
			// Configure the notification channel.
			mChannel.setDescription(description);
			notificationManager.createNotificationChannel(mChannel);
		}
		return notificationManager;
	}
	public static NotificationManager getNotificationManagerSilent(final Context context, String channel){

		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			// The user-visible name of the channel.
			CharSequence name = context.getString(R.string.notification_channel_metering);
			// The user-visible description of the channel.
			String description = context.getString(R.string.notification_channel_description_metering);
			int importance = NotificationManager.IMPORTANCE_LOW;
			NotificationChannel mChannel = new NotificationChannel(channel, name, importance);
			// Configure the notification channel.
			mChannel.setDescription(description);
			notificationManager.createNotificationChannel(mChannel);
		}
		return notificationManager;
	}



}





