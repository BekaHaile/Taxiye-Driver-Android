package product.clicklabs.jugnoo.driver.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import java.util.Locale;

import product.clicklabs.jugnoo.driver.ChangePhoneBeforeOTPActivity;
import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.LoginViaOTP;
import product.clicklabs.jugnoo.driver.MultipleAccountsActivity;
import product.clicklabs.jugnoo.driver.OTPConfirmScreen;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.RequestDuplicateRegistrationActivity;
import product.clicklabs.jugnoo.driver.oldRegistration.OldOTPConfirmScreen;
import product.clicklabs.jugnoo.driver.oldRegistration.OldRegisterScreen;
import product.clicklabs.jugnoo.driver.sticky.GeanieView;
import product.clicklabs.jugnoo.driver.ui.DriverSplashActivity;

import static product.clicklabs.jugnoo.driver.Constants.REQUEST_OVERLAY_PERMISSION;

/**
 * Created by clicklabs on 7/3/15.
 */
public class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		BaseFragmentActivity.updateLanguage(this, null);
		updateStatusBar();
    }

	@Override
	protected void onResume() {
		Data.appMinimized = false;
		stopService(new Intent(this, GeanieView.class));
		super.onResume();
		checkIfUserDataNull();
	}

	public boolean checkIfUserDataNull() {
		if (Data.userData == null
				&& !(this instanceof LoginViaOTP
					|| this instanceof MultipleAccountsActivity
					|| this instanceof OTPConfirmScreen
					|| this instanceof OldOTPConfirmScreen
					|| this instanceof OldRegisterScreen
					|| this instanceof RequestDuplicateRegistrationActivity
					|| this instanceof MultipleAccountsActivity
					|| this instanceof ChangePhoneBeforeOTPActivity
		)) {
			sendToSplash();
			return true;
		} else {
			return false;
		}
	}

	public void sendToSplash(){
		startActivity(new Intent(this, DriverSplashActivity.class));
		finish();
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}

	@Override
	public void onPause(){
		Data.appMinimized = true;
		super.onPause();
	}

	@Override
	protected void onStop() {
		if(Data.appMinimized){
			BaseFragmentActivity.checkOverlayPermissionOpenJeanie(this, false, true);
		}
		super.onStop();
	}





    public String getStringText(int resourceId){

		Resources res = getResources();
		Configuration conf = res.getConfiguration();
		Locale savedLocale = conf.locale;
		conf.locale = new Locale("en"); // whatever you want here
		res.updateConfiguration(conf, null); // second arg null means don't change

		// retrieve resources from desired locale
		String strEng = res.getString(resourceId);

		// restore original locale
		conf.locale = savedLocale;
		res.updateConfiguration(conf, null);



        String str = getResources().getString(resourceId);
        if(str.length() > strEng.length() + 10){
            return strEng;
        }
        return str;
    }




	public void updateStatusBar(){
		try {
			int currentapiVersion = android.os.Build.VERSION.SDK_INT;
			if(currentapiVersion >= Build.VERSION_CODES.LOLLIPOP) {
				Window window = BaseActivity.this.getWindow();
				window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
				window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
				window.setStatusBarColor(ContextCompat.getColor(BaseActivity.this, R.color.status_bar));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_OVERLAY_PERMISSION && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (Settings.canDrawOverlays(this)) {
				startService(new Intent(this, GeanieView.class));
			}
		}
	}

}
