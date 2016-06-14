package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.telephony.TelephonyManager;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.CustomerInfo;
import product.clicklabs.jugnoo.driver.datastructure.DriverScreenMode;
import product.clicklabs.jugnoo.driver.datastructure.EngagementStatus;
import product.clicklabs.jugnoo.driver.datastructure.PushFlags;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.datastructure.SharingRideData;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse;
import product.clicklabs.jugnoo.driver.services.DownloadService;
import product.clicklabs.jugnoo.driver.services.SyncMessageService;
import product.clicklabs.jugnoo.driver.utils.DateOperations;
import product.clicklabs.jugnoo.driver.utils.EventsHolder;
import product.clicklabs.jugnoo.driver.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.driver.utils.FlurryEventNames;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import product.clicklabs.jugnoo.driver.utils.SoundMediaPlayer;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class GCMIntentService extends IntentService {

	public static int PROMOTION_ID = 100;
	public static final long REQUEST_TIMEOUT = 120000;
	public static final int DRIVER_AVAILABILTY_TIMEOUT_REQUEST_CODE = 117;

	public GCMIntentService() {
		super("GcmIntentService");
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
			notificationManager.notify(Prefs.with(context).getInt(SPLabels.NOTIFICATION_ID, 0), notification);

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
			notificationManager.notify(Prefs.with(context).getInt(SPLabels.NOTIFICATION_ID, 0), notification);

			PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
			WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
			wl.acquire(15000);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	@SuppressWarnings("deprecation")
	public static void notificationManagerResumeAction(Context context, String message, boolean ring, String engagementId) {

		try {
			long when = System.currentTimeMillis();

			NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

			Log.v("message", "," + message);

			Intent notificationIntent = new Intent();
			if(HomeActivity.appInterruptHandler == null){
				notificationIntent.setClass(context, SplashNewActivity.class);
			} else{
				notificationIntent.setClass(context, HomeActivity.class);
			}

			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

			NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
			builder.setAutoCancel(true);
			builder.setContentTitle("Jugnoo");
			builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
			builder.setContentText(message);
			builder.setTicker(message);
			builder.setVisibility(Notification.VISIBILITY_PUBLIC);

			if (ring) {
				builder.setLights(Color.GREEN, 500, 500);
			} else {
				builder.setDefaults(Notification.DEFAULT_ALL);
			}

			builder.setWhen(when);
			builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.jugnoo_icon));
			builder.setSmallIcon(R.drawable.notif_icon);
			builder.setContentIntent(intent);


			if (HomeActivity.appInterruptHandler != null) {
				Intent intentAcc = new Intent(context, HomeActivity.class);
				intentAcc.putExtra("type", "accept");
				intentAcc.putExtra("engagement_id", engagementId);
				intentAcc.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				PendingIntent pendingIntentAccept = PendingIntent.getActivity(context, 1, intentAcc, PendingIntent.FLAG_UPDATE_CURRENT);
				builder.addAction(R.drawable.tick_30_px, "Accept", pendingIntentAccept);

				Intent intentCanc = new Intent(context, HomeActivity.class);
				intentCanc.putExtra("type", "cancel");
				intentCanc.putExtra("engagement_id", engagementId);
				intentCanc.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				PendingIntent pendingIntentCancel = PendingIntent.getActivity(context, 2, intentCanc, PendingIntent.FLAG_UPDATE_CURRENT);
				builder.addAction(R.drawable.cross_30_px, "Cancel", pendingIntentCancel);

			} else {
				Intent intentAccKill = new Intent(context, SplashNewActivity.class);
				intentAccKill.putExtra("type", "accept");
				intentAccKill.putExtra("engagement_id", engagementId);
				intentAccKill.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				PendingIntent pendingIntentAccept = PendingIntent.getActivity(context, 1, intentAccKill, PendingIntent.FLAG_UPDATE_CURRENT);
				builder.addAction(R.drawable.tick_30_px, "Accept", pendingIntentAccept);

				Intent intentCancKill = new Intent(context, SplashNewActivity.class);
				intentCancKill.putExtra("type", "cancel");
				intentCancKill.putExtra("engagement_id", engagementId);
				intentCancKill.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				PendingIntent pendingIntentCancel = PendingIntent.getActivity(context, 2, intentCancKill, PendingIntent.FLAG_UPDATE_CURRENT);
				builder.addAction(R.drawable.cross_30_px, "Cancel", pendingIntentCancel);
			}


			Notification notification = builder.build();

			Prefs.with(context).save(SPLabels.NOTIFICATION_ID, Prefs.with(context).getInt(SPLabels.NOTIFICATION_ID, 0) + 1);
			notificationManager.notify(Integer.parseInt(engagementId), notification);


			PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
			WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "TAG");
			wl.acquire(15000);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	@SuppressWarnings("deprecation")
	public static void notificationManagerCustomID(Context context, String title, String message, int notificationId,
												   Class notifClass, Bitmap bitmap) {

		try {
			long when = System.currentTimeMillis();

			NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			Log.v("message", "," + message);
			Intent notificationIntent = new Intent(context, notifClass);

			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			PendingIntent intent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT);

			NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
			builder.setAutoCancel(true);
			builder.setContentTitle(title);
			if(bitmap == null) {
				builder.setStyle(new NotificationCompat.BigTextStyle().bigText(message));
			} else {
				builder.setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap)
						.setBigContentTitle(title).setSummaryText(message));
			}
			builder.setContentText(message);
			builder.setTicker(message);
			builder.setDefaults(Notification.DEFAULT_ALL);
			builder.setWhen(when);
			builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.jugnoo_icon));
			builder.setSmallIcon(R.drawable.notif_icon);
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

	public static void clearNotifications(Context context) {
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancelAll();
	}


	@Override
	public void onHandleIntent(Intent intent) {
		try {
			Log.i("Recieved a gcm message arg1...", "," + intent.getExtras());
			String currentTimeUTC = DateOperations.getCurrentTimeInUTC();
			String currentTime = DateOperations.getCurrentTime();

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
							Log.i("push_notification", String.valueOf(jObj));
							int flag = jObj.getInt(Constants.KEY_FLAG);
							String title = jObj.optString(Constants.KEY_TITLE, "Jugnoo");

							if (PushFlags.REQUEST.getOrdinal() == flag) {
								int perfectRide = jObj.optInt(Constants.KEY_PERFECT_RIDE, 0);
								int isPooled = jObj.optInt(Constants.KEY_IS_POOLED, 0);
								int isDelivery = jObj.optInt(Constants.KEY_IS_DELIVERY, 0);
								int driverScreenMode = Prefs.with(this).getInt(SPLabels.DRIVER_SCREEN_MODE,
										DriverScreenMode.D_INITIAL.getOrdinal());
								boolean entertainRequest = false;
								if(1 == perfectRide
										&& DriverScreenMode.D_IN_RIDE.getOrdinal() == driverScreenMode
										&& Prefs.with(GCMIntentService.this).getString(SPLabels.PERFECT_ACCEPT_RIDE_DATA, " ").equalsIgnoreCase(" ")){
									entertainRequest = true;
								}
								else if(1 == isPooled
										&& Prefs.with(GCMIntentService.this).getString(SPLabels.PERFECT_ACCEPT_RIDE_DATA, " ").equalsIgnoreCase(" ")){
									entertainRequest = true;
								}
								else if(1 == isDelivery
										&& Prefs.with(GCMIntentService.this).getString(SPLabels.PERFECT_ACCEPT_RIDE_DATA, " ").equalsIgnoreCase(" ")){
									entertainRequest = true;
								}
								else if(0 == perfectRide && 0 == isPooled
										&& (DriverScreenMode.D_INITIAL.getOrdinal() == driverScreenMode
										|| DriverScreenMode.D_REQUEST_ACCEPT.getOrdinal() == driverScreenMode
										|| DriverScreenMode.D_RIDE_END.getOrdinal() == driverScreenMode)
										&& Prefs.with(GCMIntentService.this).getString(SPLabels.PERFECT_ACCEPT_RIDE_DATA, " ").equalsIgnoreCase(" ")){
									entertainRequest = true;
								}

								if(entertainRequest) {
									String engagementId = jObj.getString(Constants.KEY_ENGAGEMENT_ID);
									String userId = jObj.optString(Constants.KEY_USER_ID, "0");
									double latitude = jObj.getDouble(Constants.KEY_LATITUDE);
									double longitude = jObj.getDouble(Constants.KEY_LONGITUDE);
									String startTime = jObj.getString("start_time");
									String address = jObj.getString("address");
									int totalDeliveries = jObj.optInt(Constants.KEY_TOTAL_DELIVERIES, 0);
									double estimatedFare = jObj.optDouble(Constants.KEY_ESTIMATED_FARE, 0d);
									String userName = jObj.optString(Constants.KEY_NAME, "");

									String startTimeLocal = DateOperations.utcToLocal(startTime);
									String endTime = jObj.optString(Constants.KEY_END_TIME, "");
									long requestTimeOutMillis = GCMIntentService.REQUEST_TIMEOUT;
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

									double fareFactor = jObj.optDouble("fare_factor", 1d);

									sendRequestAckToServer(this, engagementId, currentTimeUTC);

									FlurryEventLogger.requestPushReceived(this, engagementId, DateOperations.utcToLocal(startTime), currentTime);

									startTime = DateOperations.getDelayMillisAfterCurrentTime(requestTimeOutMillis);

									if (HomeActivity.appInterruptHandler != null) {
										int referenceId = jObj.optInt(Constants.KEY_REFERENCE_ID, 0);
										CustomerInfo customerInfo = new CustomerInfo(Integer.parseInt(engagementId),
												Integer.parseInt(userId), new LatLng(latitude, longitude), startTime, address,
												referenceId, fareFactor, EngagementStatus.REQUESTED.getOrdinal(),
												isPooled, isDelivery, totalDeliveries, estimatedFare, userName);
										Data.addCustomerInfo(customerInfo);

										startRing(this);
										flurryEventForRequestPush(engagementId);

										if (jObj.optInt("penalise_driver_timeout", 0) == 1) {
											startTimeoutAlarm(this);
										}
										RequestTimeoutTimerTask requestTimeoutTimerTask = new RequestTimeoutTimerTask(this, engagementId);
										requestTimeoutTimerTask.startTimer(requestTimeOutMillis);
										notificationManagerResumeAction(this, getResources().getString(R.string.got_new_request) + "\n" + address, true, engagementId);
										HomeActivity.appInterruptHandler.onNewRideRequest(perfectRide, isPooled);

										Log.e("referenceId", "=" + referenceId);
									} else {
										notificationManagerResumeAction(this, getResources().getString(R.string.got_new_request) + "\n" + address, true, engagementId);
										startRing(this);
										flurryEventForRequestPush(engagementId);

										if (jObj.optInt("penalise_driver_timeout", 0) == 1) {
											startTimeoutAlarm(this);
										}

										RequestTimeoutTimerTask requestTimeoutTimerTask = new RequestTimeoutTimerTask(this, engagementId);
										requestTimeoutTimerTask.startTimer(requestTimeOutMillis);
									}
								}

							} else if (PushFlags.REQUEST_CANCELLED.getOrdinal() == flag) {

								String engagementId = jObj.getString("engagement_id");
								clearNotifications(this);

								if (HomeActivity.appInterruptHandler != null) {
									Data.removeCustomerInfo(Integer.parseInt(engagementId), EngagementStatus.REQUESTED.getOrdinal());
									HomeActivity.appInterruptHandler.onCancelRideRequest(engagementId, false);
								}
								cancelUploadPathAlarm(this);
								stopRing(false);

							} else if (PushFlags.RIDE_ACCEPTED_BY_OTHER_DRIVER.getOrdinal() == flag) {

								String engagementId = jObj.getString("engagement_id");
								clearNotifications(this);

								if (HomeActivity.appInterruptHandler != null) {
									Data.removeCustomerInfo(Integer.parseInt(engagementId), EngagementStatus.REQUESTED.getOrdinal());
									HomeActivity.appInterruptHandler.onCancelRideRequest(engagementId, true);
								}
								cancelUploadPathAlarm(this);
								stopRing(false);

							} else if (PushFlags.REQUEST_TIMEOUT.getOrdinal() == flag) {

								String engagementId = jObj.getString("engagement_id");
								clearNotifications(this);

								if (HomeActivity.appInterruptHandler != null) {
									Data.removeCustomerInfo(Integer.parseInt(engagementId), EngagementStatus.REQUESTED.getOrdinal());
									HomeActivity.appInterruptHandler.onRideRequestTimeout(engagementId);
								}
								cancelUploadPathAlarm(this);
								stopRing(false);

							} else if (PushFlags.RIDE_CANCELLED_BY_CUSTOMER.getOrdinal() == flag) {
								Prefs.with(this).save(SPLabels.DRIVER_SCREEN_MODE, DriverScreenMode.D_INITIAL.getOrdinal());
								int ignoreRideRequest = jObj.optInt("update_penalty_ctr", 0);
								if (ignoreRideRequest == 1) {
									new DriverTimeoutCheck().timeoutBuffer(this, 1);
								}


								SoundMediaPlayer.startSound(GCMIntentService.this, R.raw.cancellation_ring, 2, true, true);

								String logMessage = jObj.getString("message");
								String engagementId = jObj.optString(Constants.KEY_ENGAGEMENT_ID, "0");
								if (HomeActivity.appInterruptHandler != null) {
									HomeActivity.appInterruptHandler.onChangeStatePushReceived(flag, engagementId);
									notificationManagerResume(this, logMessage, true);
								} else {
									notificationManager(this, logMessage, true);
								}
							} else if (PushFlags.CHANGE_STATE.getOrdinal() == flag) {

								Prefs.with(this).save(SPLabels.DRIVER_SCREEN_MODE, DriverScreenMode.D_INITIAL.getOrdinal());

								String logMessage = jObj.getString("message");
								String engagementId = jObj.optString(Constants.KEY_ENGAGEMENT_ID, "0");
								if (HomeActivity.appInterruptHandler != null) {
									HomeActivity.appInterruptHandler.onChangeStatePushReceived(flag, engagementId);
									notificationManagerResume(this, logMessage, false);
								} else {
									notificationManager(this, logMessage, false);
								}
							} else if (PushFlags.DISPLAY_MESSAGE.getOrdinal() == flag) {
								String message1 = jObj.getString("message");

								String picture = jObj.optString(Constants.KEY_PICTURE, "");
								if("".equalsIgnoreCase(picture)){
									picture = jObj.optString(Constants.KEY_IMAGE, "");
								}
								if(!"".equalsIgnoreCase(picture)){
									new BigImageNotifAsync(title, message1, picture).execute();
								} else{
									notificationManagerCustomID(this, title, message1, PROMOTION_ID, SplashNewActivity.class, null);
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
							} else if (PushFlags.UPDATE_CUSTOMER_BALANCE.getOrdinal() == flag) {
								int userId = jObj.getInt("user_id");
								double balance = jObj.getDouble("balance");
								if (HomeActivity.appInterruptHandler != null) {
									HomeActivity.appInterruptHandler.onCashAddedToWalletByCustomer(userId, balance);
								}
							} else if (PushFlags.UPDATE_HEAT_MAP.getOrdinal() == flag) {
								if (HomeActivity.appInterruptHandler != null) {
									HomeActivity.appInterruptHandler.fetchHeatMapDataCall(this);
								}
							} else if (PushFlags.UPDATE_DROP_LOCATION.getOrdinal() == flag) {
								double dropLatitude = jObj.getDouble(Constants.KEY_OP_DROP_LATITUDE);
								double dropLongitude = jObj.getDouble(Constants.KEY_OP_DROP_LONGITUDE);
								String engagementId = jObj.getString(Constants.KEY_ENGAGEMENT_ID);
								if (HomeActivity.appInterruptHandler != null) {
									HomeActivity.appInterruptHandler.onDropLocationUpdated(engagementId, new LatLng(dropLatitude, dropLongitude));
								}
							} else if (PushFlags.OTP_VERIFIED_BY_CALL.getOrdinal() == flag) {
								String otp = jObj.getString("message");
								if(LoginViaOTP.OTP_SCREEN_OPEN != null) {
									Intent otpConfirmScreen = new Intent(this, LoginViaOTP.class);
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
								Intent intent1 = new Intent(Intent.ACTION_SYNC, null, this, ContactsUploadService.class);
								intent1.putExtra("access_token", Database2.getInstance(this).getDLDAccessToken());
								startService(intent1);

							} else if (PushFlags.SEND_DRIVER_MESSAGES.getOrdinal() == flag) {
								Intent synIntent = new Intent(this, SyncMessageService.class);
								synIntent.putExtra(Constants.KEY_ACCESS_TOKEN, Database2.getInstance(this).getDLDAccessToken());
								startService(synIntent);

							}else if (PushFlags.SHARING_RIDE_ENDED.getOrdinal() == flag) {
								SharingRideData sharingRideData = new SharingRideData(jObj.getString("engagement_id"),
										jObj.getString("transaction_time"),
										jObj.getString("customer_phone_no"),
										jObj.getDouble("actual_fare"),
										jObj.getDouble("paid_in_cash"),
										jObj.getDouble("account_balance"));
								if (HomeActivity.appInterruptHandler != null) {
									Intent intent1 = new Intent(this, SharingRidesActivity.class);
									intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
									intent1.putExtra("sharing_engagement_data", jObj.toString());
									startActivity(intent1);
								}
								notificationManagerCustomID(this, title, getResources().getString(R.string.sharing_payment_recieved)
												+ Utils.hidePhoneNoString(sharingRideData.customerPhoneNumber),
										Integer.parseInt(sharingRideData.sharingEngagementId), SplashNewActivity.class, null);
							}

							String message1 = jObj.optString("message", " ");
							savePush(jObj, flag, title, message1);

						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				} catch (Exception e) {
					e.printStackTrace();

					// Release the wake lock provided by the WakefulBroadcastReceiver.
					GcmBroadcastReceiver.completeWakefulIntent(intent);

				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


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

	public static void startRing(Context context) {
		try {

			stopRing(true);
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
			if (Data.DEFAULT_SERVER_URL.equalsIgnoreCase(Data.LIVE_SERVER_URL)){
			am.setStreamVolume(AudioManager.STREAM_MUSIC, am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
			mediaPlayer = MediaPlayer.create(context, R.raw.telephone_ring);
			}else{
				mediaPlayer = MediaPlayer.create(context, R.raw.telephone_ring);
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static void startRingCustom(Context context, String file) {
		try {
			stopRing(true);
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

	public static void startRingWithStopHandler(Context context) {
		try {
			stopRing(true);
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
					stopRing(true);
				}
			};
			ringStopTimer.start();


		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static void stopRing(boolean manual) {
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
				if("".equalsIgnoreCase(picture)){
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
							stopRing(true);
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
					final long resposeTime = System.currentTimeMillis();
					String networkName = getNetworkName(context);

					HashMap<String, String> params = new HashMap<String, String>();
					params.put("uuid", uuid);
					params.put("timestamp", ackTimeStamp);
					params.put("network_name", networkName);

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



	private void flurryEventForRequestPush(String engagementId) {
		int mode = Prefs.with(this).getInt(SPLabels.DRIVER_SCREEN_MODE, DriverScreenMode.D_INITIAL.getOrdinal());
		if (DriverScreenMode.D_INITIAL.getOrdinal() != mode
				&& DriverScreenMode.D_REQUEST_ACCEPT.getOrdinal() != mode
				&& DriverScreenMode.D_RIDE_END.getOrdinal() != mode) {
			FlurryEventLogger.logStartRing(this, mode, Utils.getAppVersion(this), engagementId, FlurryEventNames.START_RING_INITIATED);
		}
	}


	private class BigImageNotifAsync extends AsyncTask<String, String, Bitmap> {

		private Bitmap bitmap = null;
		private String title, message, picture;

		public BigImageNotifAsync(String title, String message, String picture){
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
			}catch (Exception e){
				e.printStackTrace();
			}
			return bitmap;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			// execution of result of Long time consuming operation
			try {
				if(result == null){
					notificationManagerCustomID(GCMIntentService.this, title, message, PROMOTION_ID, SplashNewActivity.class,
							null);
				} else{
					notificationManagerCustomID(GCMIntentService.this, title, message, PROMOTION_ID, SplashNewActivity.class,
							result);
				}
			}catch (Exception e){
				e.printStackTrace();
				notificationManagerCustomID(GCMIntentService.this, title, message, PROMOTION_ID, SplashNewActivity.class,
						null);
			}
		}

	}

}





