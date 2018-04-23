package product.clicklabs.jugnoo.driver.services;

/**
 * Created by aneeshbansal on 04/02/16.
 */

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.File;
import java.io.FileFilter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.Database2;
import product.clicklabs.jugnoo.driver.DriverLocationUpdateService;
import product.clicklabs.jugnoo.driver.GCMIntentService;
import product.clicklabs.jugnoo.driver.HomeUtil;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.NotificationAlarmResponse;
import product.clicklabs.jugnoo.driver.utils.DownloadFile;
import product.clicklabs.jugnoo.driver.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.driver.utils.FlurryEventNames;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedFile;

public class FetchMFileService extends IntentService {

	String fileID, engagementId;

	public FetchMFileService() {
		super(FetchMFileService.class.getName());
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		engagementId = intent.getStringExtra("file_id");
		fileID = engagementId + "m";
		try {
			File mfile = product.clicklabs.jugnoo.driver.utils.Log.getPathLogFile(fileID, false);
			if(mfile != null) {
				sendMFileToServer(engagementId, mfile);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.stopSelf();
	}


	public void sendMFileToServer(final String engagementId, final File mfile) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					HashMap<String, String> params = new HashMap<String, String>();

					params.put("engagement_id",engagementId);
					HomeUtil.putDefaultParams(params);
					TypedFile typedFile;
					typedFile = new TypedFile(Constants.MIME_TYPE, mfile);

					Response response = RestClient.getApiServices().sendmFileToServer(typedFile, params);
					String result = new String(((TypedByteArray) response.getBody()).getBytes());

					JSONObject jObj = new JSONObject(result);
					if (jObj.has("flag")) {
						int flag = jObj.getInt("flag");
						if (ApiResponseFlags.ACK_RECEIVED.getOrdinal() == flag) {
							String log = jObj.getString("log");
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}).start();
	}


}