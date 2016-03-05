package product.clicklabs.jugnoo.driver.services;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import product.clicklabs.jugnoo.driver.utils.Utils;

/**
 * Created by aneeshbansal on 15/02/16.
 */

public class DownloadBroadcastReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(final Context context, Intent intent) {


		String action = intent.getAction();
		if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
			DownloadService downloadService = new DownloadService();
			downloadService.playDownloadedFile(context);
			Utils.enableReceiver(context, DownloadBroadcastReceiver.class, false);
		}
	}
}
