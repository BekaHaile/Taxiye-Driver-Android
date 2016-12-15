package product.clicklabs.jugnoo.driver.utils;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.Window;
import android.view.WindowManager;

import java.util.Locale;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.DriverDocumentActivity;
import product.clicklabs.jugnoo.driver.HelpActivity;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.SplashNewActivity;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.sticky.GeanieView;

/**
 * Created by aneeshbansal on 09/05/16.
 */
public class BaseFragmentActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		updateLanguage();
		updateStatusBar();
	}

	@Override
	protected void onResume() {
		Data.appMinimized = false;
		stopService(new Intent(this, GeanieView.class));
		super.onResume();
		checkIfUserDataNull();
	}

	@Override
	protected void onPause() {
		Data.appMinimized = true;
		super.onPause();
	}

	@Override
	protected void onStop() {
		if(Data.appMinimized){
			startService(new Intent(this, GeanieView.class));
		}
		super.onStop();
	}

	public boolean checkIfUserDataNull() {
		if (Data.userData == null
				&& !(this instanceof DriverDocumentActivity
					|| this instanceof HelpActivity)) {
			startActivity(new Intent(this, SplashNewActivity.class));
			finish();
			overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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
		String item = Prefs.with(this).getString(SPLabels.SELECTED_LANGUAGE,"");
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
				Window window = BaseFragmentActivity.this.getWindow();
				window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
				window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
				window.setStatusBarColor(ContextCompat.getColor(BaseFragmentActivity.this, R.color.status_bar));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
