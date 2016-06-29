package product.clicklabs.jugnoo.driver.utils;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import java.util.Locale;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.DriverDocumentActivity;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.SplashNewActivity;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;

/**
 * Created by aneeshbansal on 09/05/16.
 */
public class BaseFragmentActivity extends FragmentActivity {

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
				&& !(this instanceof DriverDocumentActivity)) {
			startActivity(new Intent(this, SplashNewActivity.class));
			finish();
			overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
			return true;
		} else {
			return false;
		}
	}

	public void sentToSplash(){
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
}
