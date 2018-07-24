package product.clicklabs.jugnoo.driver.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.Locale;

import product.clicklabs.jugnoo.driver.ChangePhoneBeforeOTPActivity;
import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.LoginViaOTP;
import product.clicklabs.jugnoo.driver.MultipleAccountsActivity;
import product.clicklabs.jugnoo.driver.OTPConfirmScreen;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.RequestDuplicateRegistrationActivity;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.oldRegistration.OldOTPConfirmScreen;
import product.clicklabs.jugnoo.driver.oldRegistration.OldRegisterScreen;
import product.clicklabs.jugnoo.driver.sticky.GeanieView;
import product.clicklabs.jugnoo.driver.ui.DriverSplashActivity;

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
}
