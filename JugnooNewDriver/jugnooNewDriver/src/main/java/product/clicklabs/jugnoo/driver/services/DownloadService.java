package product.clicklabs.jugnoo.driver.services;

/**
 * Created by aneeshbansal on 04/02/16.
 */

import android.app.Activity;
import android.app.DownloadManager;
import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.Database2;
import product.clicklabs.jugnoo.driver.GCMIntentService;
import product.clicklabs.jugnoo.driver.JSONParser;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.RegisterScreen;
import product.clicklabs.jugnoo.driver.SplashNewActivity;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.DriverScreenMode;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.HeatMapResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.NotificationAlarmResponse;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.DownloadFile;
import product.clicklabs.jugnoo.driver.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.driver.utils.FlurryEventNames;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class DownloadService extends IntentService {

	public static final int STATUS_RUNNING = 0;
	public static final int STATUS_FINISHED = 1;
	public static final int STATUS_ERROR = 2;
	private Bundle bundle;

	int downloadOnly;
	String fileID, fileURL;
	private static final String TAG = "DownloadService";
	public DownloadService() {
		super(DownloadService.class.getName());
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		Log.d(TAG, "Service Started!");
		 downloadOnly = intent.getIntExtra("downloadOnly", 0);
		 fileID = intent.getStringExtra("file_id");
		 fileURL = intent.getStringExtra("file_url");

		bundle = intent.getExtras();


			try {
				if(downloadOnly == 0) {
					Utils.enableReceiver(this, DownloadBroadcastReceiver.class, true);
					DownloadFile downloadFile = new DownloadFile();
					long referenceId = downloadFile.downloadNotificationData(DownloadService.this, fileURL, fileID);
					Prefs.with(DownloadService.this).save(SPLabels.DOWNLOADED_FILE_ID, fileID);

//					registerReceiver(onComplete,
//							new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

				}else if(downloadOnly == 1){
					DownloadFile downloadFile = new DownloadFile();
					downloadFile.downloadNotificationData(DownloadService.this, fileURL, fileID);
				}else{
					fetchNotificationData();
				}
			} catch (Exception e) {
                /* Sending error message back to activity */
//				bundle.putString(Intent.EXTRA_TEXT, e.toString());
			}
		Log.d(TAG, "Service Stopping!");
		this.stopSelf();
	}

//	BroadcastReceiver onComplete=new BroadcastReceiver() {
//		public void onReceive(Context ctxt, Intent intent) {
//			Log.e(TAG, "intent.getExtras" + intent.getExtras());
//
//			Toast.makeText(DownloadService.this, "download intent here", Toast.LENGTH_SHORT).show();
//
//
//				unregisterReceiver(onComplete);
//				File myFile = new File("/storage/emulated/0/jugnooFiles/" + fileURL + ".mp3");
//
//				if (myFile.exists()) {
//					GCMIntentService.startRingCustom(DownloadService.this, myFile.getAbsolutePath());
//				}
//			}
//
//	};

	public void playDownloadedFile(Context context){
		String newFileID = Prefs.with(DownloadService.this).getString(SPLabels.DOWNLOADED_FILE_ID,"");
		File myFile = new File("/storage/emulated/0/jugnooFiles/"+newFileID + ".mp3");

		if (myFile.exists()) {
			FlurryEventLogger.event(FlurryEventNames.CUSTOM_VOICE_NOTIFICATION);
			GCMIntentService.startRingCustom(context, myFile.getAbsolutePath());
		}
	}

	public void fetchNotificationData() {
		try {
			NotificationAlarmResponse response = RestClient.getApiServices().
					updateNotificationData(Data.userData.accessToken, Constants.JUGNOO_AUDIO);

			for (int i = 0; i < response.getLinks().size(); i++) {

				String url = response.getLinks().get(i).getFileUrl();
				String file = response.getLinks().get(i).getFileId();
				Database2.getInstance(this).insertCustomAudioUrl(url, file);

				new DownloadFile().downloadNotificationData(DownloadService.this, url, file);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



}