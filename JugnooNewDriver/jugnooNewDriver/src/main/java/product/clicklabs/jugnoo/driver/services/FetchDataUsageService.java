package product.clicklabs.jugnoo.driver.services;

/**
 * Created by aneeshbansal on 04/02/16.
 */

import android.app.AlertDialog;
import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.Database2;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.utils.Log;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedFile;

public class FetchDataUsageService extends Service {

	private Handler mHandler = new Handler();
	private long mStartRX = 0;
	private long mStartTX = 0;
	private Timer timer = new Timer();
	private Context ctx;
	long oldRxBytes =0, oldTxBytes =0;
	MainTask myTimerTask = new MainTask();
	String task;


	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		try {
			task = intent.getStringExtra("task_id");
			if (task.equalsIgnoreCase("1")) {
				mStartRX = TrafficStats.getTotalRxBytes();
				mStartTX = TrafficStats.getTotalTxBytes();
				if (mStartRX == TrafficStats.UNSUPPORTED || mStartTX == TrafficStats.UNSUPPORTED) {

				} else {
					stopTimer();
					startTimer();
				}
			} else if (task.equalsIgnoreCase("2")) {
				try {
					stopTimer();
					stopSelf();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else if (task.equalsIgnoreCase("3")) {
				sendDataStatToServer();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		stopTimer();
		super.onDestroy();
	}

	public void onCreate() {
		ctx = this;
	}

	private void stopTimer(){
		try {
			if(myTimerTask != null) {
				myTimerTask.cancel();
			}
			if(timer != null) {
				timer.cancel();
				timer.purge();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			myTimerTask = null;
			timer = null;
		}
	}

	private void startTimer() {
		if(timer == null) {
			timer = new Timer();
		}
		if(myTimerTask == null) {
			myTimerTask = new MainTask();
		}

		timer.scheduleAtFixedRate(myTimerTask, 0, 1000);
	}

	private class MainTask extends TimerTask {
		public void run() {

			long rxBytes = TrafficStats.getTotalRxBytes() - mStartRX;
			long reqRxBytes = rxBytes - oldRxBytes;
			oldRxBytes = rxBytes;
			Log.e("recived bytes", String.valueOf(reqRxBytes));
			long txBytes = TrafficStats.getTotalTxBytes() - mStartTX;
			long reqTxBytes = txBytes - oldTxBytes;
			oldTxBytes = txBytes;
			Log.e("sent bytes", String.valueOf(txBytes));
			Database2.getInstance(ctx).insertDataUsage(String.valueOf(System.currentTimeMillis()),
					String.valueOf(reqRxBytes), String.valueOf(reqTxBytes));

		}
	}


	public void sendDataStatToServer() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					String dataUsage = Database2.getInstance(ctx).getDataUsage();
					Response response = RestClient.getApiServices().usageData(Data.userData.accessToken, dataUsage);
					String result = new String(((TypedByteArray) response.getBody()).getBytes());

					JSONObject jObj = new JSONObject(result);
					if (jObj.has("flag")) {
						int flag = jObj.getInt("flag");
						if (ApiResponseFlags.ACK_RECEIVED.getOrdinal() == flag) {
							Database2.getInstance(ctx).deleteUsageData();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}


}