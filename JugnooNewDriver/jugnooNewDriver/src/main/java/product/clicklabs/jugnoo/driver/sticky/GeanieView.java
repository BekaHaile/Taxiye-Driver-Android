package product.clicklabs.jugnoo.driver.sticky;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsoluteLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import product.clicklabs.jugnoo.driver.HomeActivity;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.SplashNewActivity;
import product.clicklabs.jugnoo.driver.utils.Log;

/**
 * Created by aneeshbansal on 19/01/16.
 */
public class GeanieView extends Service {

	private WindowManager windowManager;

	private RelativeLayout root;
	private View convertView;
	private ImageView jugnooHead;


	@SuppressWarnings("deprecation")
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
		convertView = inflater.inflate(R.layout.chathead, null);
		jugnooHead = (ImageView)convertView.findViewById(R.id.chathead_img);

		windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
		final WindowManager.LayoutParams paramsA = new WindowManager.LayoutParams(
				112,
				550,
				WindowManager.LayoutParams.TYPE_PHONE,
				WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);

		paramsA.gravity = Gravity.TOP | Gravity.LEFT;
		paramsA.x = 590;
		paramsA.y = 450;

		windowManager.addView(convertView, paramsA);

		jugnooHead.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				stopSelf();
				Intent intent = new Intent(GeanieView.this, HomeActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
			}
		});
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public void onDestroy(){
		windowManager.removeViewImmediate(convertView);
	}
}