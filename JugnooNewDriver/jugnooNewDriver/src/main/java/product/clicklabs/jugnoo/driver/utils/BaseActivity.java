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
import android.os.Bundle;
import android.widget.Toast;

import java.util.Locale;

import product.clicklabs.jugnoo.driver.ChangePhoneBeforeOTPActivity;
import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.LoginViaOTP;
import product.clicklabs.jugnoo.driver.MultipleAccountsActivity;
import product.clicklabs.jugnoo.driver.OTPConfirmScreen;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.RegisterScreen;
import product.clicklabs.jugnoo.driver.RequestDuplicateRegistrationActivity;
import product.clicklabs.jugnoo.driver.SplashNewActivity;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.oldRegistration.OldOTPConfirmScreen;
import product.clicklabs.jugnoo.driver.oldRegistration.OldRegisterScreen;

/**
 * Created by clicklabs on 7/3/15.
 */
public class BaseActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		updateLanguage();
    }

	@Override
	protected void onResume() {
		super.onResume();
		checkIfUserDataNull();
	}

	public boolean checkIfUserDataNull() {
		if (Data.userData == null
				&& !(this instanceof SplashNewActivity
					|| this instanceof LoginViaOTP
					|| this instanceof MultipleAccountsActivity
					|| this instanceof OTPConfirmScreen
					|| this instanceof RegisterScreen
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
		startActivity(new Intent(this, SplashNewActivity.class));
		finish();
		overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
	}


	public void updateLanguage(){
		String item = Prefs.with(this).getString(SPLabels.SELECTED_LANGUAGE, "");
		String languageToLoad;

		if (item.equalsIgnoreCase("English")) {
			languageToLoad = "en";
		} else if (item.equalsIgnoreCase("हिन्दी")) {
			languageToLoad = "hi";
		} else if (item.equalsIgnoreCase("ગુજરાતી")) {
			languageToLoad = "gu";
		} else if (item.equalsIgnoreCase("ଓଡ଼ିଆ")) {
			languageToLoad = "or";
		} else if (item.equalsIgnoreCase("മലയാളം")) {
			languageToLoad = "ml";
		} else if (item.equalsIgnoreCase("தமிழ்")) {
			languageToLoad = "ta";
		} else if (item.equalsIgnoreCase("తెలుగు")) {
			languageToLoad = "te";
		} else if (item.equalsIgnoreCase("ಕನ್ನಡ")) {
			languageToLoad = "kn";
		} else if (item.equalsIgnoreCase("অসমীয়া")) {
			languageToLoad = "as";
		} else {
			languageToLoad = "en";
//			return;
		}




		Locale locale = new Locale(languageToLoad);
		Locale.setDefault(locale);

		Configuration config = new Configuration();
		config.locale = locale;
		getBaseContext().getResources().updateConfiguration(config,
				getBaseContext().getResources().getDisplayMetrics());



		if(isSupported(this,getResources().getString(R.string.email))){
			Log.i("yes","");
		}else{
			Locale.setDefault(new Locale("en"));
			Configuration conf = new Configuration();
			conf.locale = new Locale("en");
			getBaseContext().getResources().updateConfiguration(conf,
					getBaseContext().getResources().getDisplayMetrics());
			Toast.makeText(this, "Selected language is not supported by your phone", Toast.LENGTH_LONG).show();
		}



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


	private static final int WIDTH_PX = 200;
	private static final int HEIGHT_PX = 80;

	public static boolean isSupported(Context context, String text) {
		int w = WIDTH_PX, h = HEIGHT_PX;
		Resources resources = context.getResources();
		float scale = resources.getDisplayMetrics().density;
		Bitmap.Config conf = Bitmap.Config.ARGB_8888;
		Bitmap bitmap = Bitmap.createBitmap(w, h, conf); // this creates a MUTABLE bitmap
		Bitmap orig = bitmap.copy(conf, false);
		Canvas canvas = new Canvas(bitmap);
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.rgb(0, 0, 0));
		paint.setTextSize((int) (14 * scale));

		// draw text to the Canvas center
		Rect bounds = new Rect();
		paint.getTextBounds(text, 0, text.length(), bounds);
		int x = (bitmap.getWidth() - bounds.width()) / 2;
		int y = (bitmap.getHeight() + bounds.height()) / 2;

		canvas.drawText(text, x, y, paint);
		boolean res = !orig.sameAs(bitmap);
		orig.recycle();
		bitmap.recycle();
		return res;
	}


}
