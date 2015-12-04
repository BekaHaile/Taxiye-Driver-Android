package product.clicklabs.jugnoo.driver.utils;

import android.app.Activity;
import android.content.Intent;

/**
 * Created by clicklabs on 7/3/15.
 */
public class BaseActivity extends Activity {

    public static final String GENIE_SERVICE = "com.jugnoo.genie.GENIE_SERVICE";

    private static boolean activityFinished = false;
    boolean newActivityStarted = false;

    @Override
    public void startActivity(Intent intent) {
		try {
			newActivityStarted = true;
			super.startActivity(intent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    @Override
    protected void onPause() {

        if (!newActivityStarted && !activityFinished) {
//            startService(new Intent(GENIE_SERVICE));
        }
        newActivityStarted = false;

        activityFinished = false;
        super.onPause();
    }

    @Override
    protected void onResume() {
//        stopService(new Intent(GENIE_SERVICE));
        super.onResume();
    }

    @Override
    public void finish() {
        activityFinished = true;
        super.finish();
    }
}
