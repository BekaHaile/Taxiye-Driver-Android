package product.clicklabs.jugnoo.driver.services;

/**
 * Created by aneeshbansal on 04/02/16.
 */

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.utils.BaseActivity;

public class DownloadActivity extends BaseActivity implements DownloadResultReceiver.Receiver {

	private final String TAG = DownloadActivity.class.getSimpleName();

	private ListView listView = null;

	private ArrayAdapter arrayAdapter = null;

	private DownloadResultReceiver mReceiver;

	final String url = "http://www.srilagourgovindaswami.org/images/sivaji19.jpg";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        /* Allow activity to show indeterminate progressbar */
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        /* Set activity layout */
		setContentView(R.layout.activity_blank_for_dialog);


        /* Initialize listView */
		listView = (ListView) findViewById(R.id.listView);

        /* Starting Download Service */
//		mReceiver = new DownloadResultReceiver(new Handler());
//		mReceiver.setReceiver(this);
		Intent intent = new Intent(Intent.ACTION_SYNC, null, this, DownloadService.class);

        /* Send optional extras to Download IntentService */
//		intent.putExtra("url", url);
//		intent.putExtra("receiver", mReceiver);
		intent.putExtra("requestId", 101);
		intent.putExtra("downloadOnly", 2);

		startService(intent);

		registerReceiver(onComplete,
				new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
	}

	BroadcastReceiver onComplete=new BroadcastReceiver() {
		public void onReceive(Context ctxt, Intent intent) {
			Log.e(TAG, "intent.getExtras" + intent.getExtras());
			Toast.makeText(DownloadActivity.this, "download intent here", Toast.LENGTH_SHORT).show();
		}
	};

	@Override
	public void onReceiveResult(int resultCode, Bundle resultData) {
		switch (resultCode) {
			case DownloadService.STATUS_RUNNING:

				setProgressBarIndeterminateVisibility(true);
				break;
			case DownloadService.STATUS_FINISHED:
                /* Hide progress & extract result from bundle */
				setProgressBarIndeterminateVisibility(false);
				Toast.makeText(DownloadActivity.this, "hiiii", Toast.LENGTH_LONG);


				break;
			case DownloadService.STATUS_ERROR:
                /* Handle the error */
				String error = resultData.getString(Intent.EXTRA_TEXT);
				Toast.makeText(this, error, Toast.LENGTH_LONG).show();
				break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(onComplete);
	}
}



