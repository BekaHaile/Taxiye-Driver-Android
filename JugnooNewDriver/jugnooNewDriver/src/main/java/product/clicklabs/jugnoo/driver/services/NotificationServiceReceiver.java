package product.clicklabs.jugnoo.driver.services;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import product.clicklabs.jugnoo.driver.utils.Utils;

/**
 * Created by aneeshbansal on 15/02/16.
 */

public class NotificationServiceReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		String pack = intent.getStringExtra("package");
		String title = intent.getStringExtra("title");
		String text = intent.getStringExtra("text");


	}
}
