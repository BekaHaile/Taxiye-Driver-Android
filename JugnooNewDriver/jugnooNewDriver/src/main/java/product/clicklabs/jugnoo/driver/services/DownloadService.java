package product.clicklabs.jugnoo.driver.services;

/**
 * Created by aneeshbansal on 04/02/16.
 */

import android.app.Activity;
import android.app.DownloadManager;
import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.Database2;
import product.clicklabs.jugnoo.driver.JSONParser;
import product.clicklabs.jugnoo.driver.RegisterScreen;
import product.clicklabs.jugnoo.driver.SplashNewActivity;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.DriverScreenMode;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.HeatMapResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.NotificationAlarmResponse;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class DownloadService extends IntentService {

	public static final int STATUS_RUNNING = 0;
	public static final int STATUS_FINISHED = 1;
	public static final int STATUS_ERROR = 2;

	private static final String TAG = "DownloadService";
	private DownloadManager downloadManager;
	private long downloadReference;

	public DownloadService() {
		super(DownloadService.class.getName());
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		Log.d(TAG, "Service Started!");

		final ResultReceiver receiver = intent.getParcelableExtra("receiver");
		String url = intent.getStringExtra("url");

		Bundle bundle = new Bundle();

		if (!TextUtils.isEmpty(url)) {


			try {
				fetchNotificationData();
			} catch (Exception e) {

                /* Sending error message back to activity */
				bundle.putString(Intent.EXTRA_TEXT, e.toString());
				receiver.send(STATUS_ERROR, bundle);
			}
		}
		Log.d(TAG, "Service Stopping!");
		this.stopSelf();
	}

	public void downloadNotificationData(String url, String file) {

		downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
		Uri Download_Uri = Uri.parse(url);
		DownloadManager.Request request = new DownloadManager.Request(Download_Uri);

		//Restrict the types of networks over which this download may proceed.
		request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
		//Set whether this download may proceed over a roaming connection.
		request.setAllowedOverRoaming(false);
		//Set the title of this download, to be displayed in notifications (if enabled).
		request.setTitle("My Data Download");
		//Set a description of this download, to be displayed in notifications (if enabled)
		request.setDescription("Android Data download using DownloadManager.");
		//Set the local destination for the downloaded file to a path within the application's external files directory
		request.setDestinationInExternalFilesDir(this, Environment.DIRECTORY_DOWNLOADS, file + ".mp3");

		//Enqueue a new download and same the referenceId
		downloadReference = downloadManager.enqueue(request);
	}

	public void fetchNotificationData() {
		try {
			NotificationAlarmResponse response = RestClient.getApiServices().
					updateNotificationData(Data.userData.accessToken, "jugnoo_audio");

			for (int i = 0; i < response.getSound().size(); i++) {

				String url = response.getSound().get(i).getSoundUrl();
				String file = response.getSound().get(i).getSoundId();
				Database2.getInstance(this).insertCustomAudioUrl(url, file);

				downloadNotificationData(url, file);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}