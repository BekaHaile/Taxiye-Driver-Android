package product.clicklabs.jugnoo.driver.tutorial;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Vibrator;

import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.Database2;
import product.clicklabs.jugnoo.driver.GCMIntentService;
import product.clicklabs.jugnoo.driver.HomeActivity;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.SplashNewActivity;
import product.clicklabs.jugnoo.driver.datastructure.CustomerInfo;
import product.clicklabs.jugnoo.driver.datastructure.DriverScreenMode;
import product.clicklabs.jugnoo.driver.datastructure.EngagementStatus;
import product.clicklabs.jugnoo.driver.datastructure.PushFlags;
import product.clicklabs.jugnoo.driver.datastructure.RingData;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.ui.DriverSplashActivity;
import product.clicklabs.jugnoo.driver.utils.DateOperations;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Prefs;

/**
 * Created by gurmail on 2/7/17.
 */

public class GenrateTourPush {

    Context context;
    public MediaPlayer mediaPlayer;
    public Vibrator vibrator;
    public CountDownTimer ringStopTimer;

    public GenrateTourPush(Context context) {
        this.context = context;

    }

    public void createDemoRequest(TourResponseModel message) {
        try {
            {

                try {
                    TourResponseModel.RequestResponse jObj = message.responses.requestResponse;
                    Log.i("push_notification", String.valueOf(jObj));
                    int flag = jObj.getFlag();
                    String title = "Jugnoo";

                    if (PushFlags.REQUEST.getOrdinal() == flag) {
                        int perfectRide = jObj.getPerfectRide();
                        int isPooled = jObj.getIsPooled();
                        int isDelivery = jObj.getIsDelivery();
                        int isDeliveryPool = 0;
                        int changeRing = jObj.getRingType();
                        int driverScreenMode = Prefs.with(context).getInt(SPLabels.DRIVER_SCREEN_MODE,
                                DriverScreenMode.D_INITIAL.getOrdinal());
                        boolean entertainRequest = false;
                        if(jObj.getRideType() == 4){
                            isDeliveryPool =1;
                        }
                        if (1 == perfectRide
                                && DriverScreenMode.D_IN_RIDE.getOrdinal() == driverScreenMode
                                && Prefs.with(context).getString(SPLabels.PERFECT_ACCEPT_RIDE_DATA, " ").equalsIgnoreCase(" ")) {
                            entertainRequest = true;
                        } else if (1 == isPooled
                                && Prefs.with(context).getString(SPLabels.PERFECT_ACCEPT_RIDE_DATA, " ").equalsIgnoreCase(" ")) {
                            entertainRequest = true;
                        } else if (1 == isDelivery
                                && Prefs.with(context).getString(SPLabels.PERFECT_ACCEPT_RIDE_DATA, " ").equalsIgnoreCase(" ")) {
                            entertainRequest = true;
                        } else if (0 == perfectRide && 0 == isPooled
                                && (DriverScreenMode.D_INITIAL.getOrdinal() == driverScreenMode)
                                && Prefs.with(context).getString(SPLabels.PERFECT_ACCEPT_RIDE_DATA, " ").equalsIgnoreCase(" ")) {
                            entertainRequest = true;
                        }

                        if (entertainRequest) {
                            String engagementId = String.valueOf(jObj.getEngagementId());
                            String currency = jObj.getCurrencyUnit();
                            String userId = String.valueOf(jObj.getUserId());
                            double latitude = jObj.getLatitude();
                            double longitude = jObj.getLongitude();
                            double currrentLatitude = Double.parseDouble(jObj.getCurrentLatitude());
                            double currrentLongitude = Double.parseDouble(jObj.getCurrentLongitude());
                            String startTime = jObj.getStartTime();
                            String address = jObj.getAddress();
                            double dryDistance = jObj.getDryDistance();
                            int totalDeliveries = 0;
                            double estimatedFare = 0;
                            double cashOnDelivery = 0;
                            String estimatedDriverFare = "0";

                            String userName = "";
                            int referenceId = 0;

                            String startTimeLocal = DateOperations.utcToLocal(startTime);
                            String endTime = jObj.getEndTime();
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

                            double fareFactor = jObj.getFareFactor();



                            startTime = DateOperations.getDelayMillisAfterCurrentTime(requestTimeOutMillis);

                            if (HomeActivity.appInterruptHandler != null) {
                                CustomerInfo customerInfo = new CustomerInfo(Integer.parseInt(engagementId),
                                        Integer.parseInt(userId), new LatLng(latitude, longitude), startTime, address,
                                        referenceId, fareFactor, EngagementStatus.REQUESTED.getOrdinal(),
                                        isPooled, isDelivery, isDeliveryPool, totalDeliveries, estimatedFare, userName, dryDistance, cashOnDelivery,
                                        new LatLng(currrentLatitude, currrentLongitude), estimatedDriverFare, new ArrayList<String>(),
                                        0d,currency);
                                Data.addCustomerInfo(customerInfo);

                                startRing(context, engagementId, changeRing);


                                RequestTimeoutTimerTask requestTimeoutTimerTask = new RequestTimeoutTimerTask(context, engagementId);
                                requestTimeoutTimerTask.startTimer(requestTimeOutMillis);

                                HomeActivity.appInterruptHandler.onNewRideRequest(perfectRide, isPooled, isDelivery);

                                Log.e("referenceId", "=" + referenceId);
                            } else {

                                startRing(context, engagementId, changeRing);

                                RequestTimeoutTimerTask requestTimeoutTimerTask = new RequestTimeoutTimerTask(context, engagementId);
                                requestTimeoutTimerTask.startTimer(requestTimeOutMillis);
                            }
                        }
                        try {
                            if (jObj.getWakeUpLockEnabled() == 1) {
                                if (HomeActivity.activity != null) {
                                    if (!HomeActivity.activity.hasWindowFocus()) {
                                        Intent newIntent = new Intent(context, HomeActivity.class);
                                        newIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                                        newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        context.startActivity(newIntent);
                                    }
                                } else {
                                    Intent homeScreen = new Intent(context, DriverSplashActivity.class);
                                    homeScreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(homeScreen);
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void startRing(Context context, String engagementId, int ringType) {
        try {

            stopRing(true, context);
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
            if (Data.DEFAULT_SERVER_URL.equalsIgnoreCase(Data.LIVE_SERVER_URL)) {
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
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
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


    public void stopRing(boolean manual, Context context) {
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
            } catch (Exception e) {
            }
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
}
