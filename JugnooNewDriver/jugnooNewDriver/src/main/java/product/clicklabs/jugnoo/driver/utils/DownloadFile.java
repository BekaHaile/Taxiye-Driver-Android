package product.clicklabs.jugnoo.driver.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;

/**
 * Created by aneeshbansal on 05/02/16.
 */
public class DownloadFile {

	public long downloadNotificationData(Context context, String url, String file) {
		DownloadManager downloadManager = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);
		Uri Download_Uri = Uri.parse(url);
		DownloadManager.Request request = new DownloadManager.Request(Download_Uri);

		//Restrict the types of networks over which this download may proceed.
		request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
		//Set whether this download may proceed over a roaming connection.
		request.setAllowedOverRoaming(false);
		//Set the counterTime of this download, to be displayed in notifications (if enabled).
		request.setTitle("My Data Download");
		//Set a description of this download, to be displayed in notifications (if enabled)
		request.setDescription("Android Data download using DownloadManager.");
		//Set the local destination for the downloaded file to a path within the application's external files directory
		request.setDestinationInExternalPublicDir("/jugnooFiles", file + ".mp3");

		//Enqueue a new download and same the referenceId
		return downloadManager.enqueue(request);
	}
}
