package product.clicklabs.jugnoo.driver.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;

import java.util.Locale;

import product.clicklabs.jugnoo.driver.datastructure.SPLabels;

/**
 * Created by clicklabs on 7/3/15.
 */
public class BaseActivity extends Activity {

    private Resources resourcesEng, resources;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


		updateLanguage();

    }

	public void updateLanguage(){
		String item = Prefs.with(this).getString(SPLabels.SELECTED_LANGUAGE,"");
		String languageToLoad;

		if (item.equalsIgnoreCase("English")) {
			languageToLoad = "en";
		} else if (item.equalsIgnoreCase("Hindi")) {
			languageToLoad = "hi";
		} else if (item.equalsIgnoreCase("Gujrati")) {
			languageToLoad = "gu";
		} else if (item.equalsIgnoreCase("Oriya")) {
			languageToLoad = "or";
		} else if (item.equalsIgnoreCase("Malayalam")) {
			languageToLoad = "ml";
		} else if (item.equalsIgnoreCase("Tamil")) {
			languageToLoad = "ta";
		} else if (item.equalsIgnoreCase("Telgu")) {
			languageToLoad = "te";
		} else if (item.equalsIgnoreCase("Kannada")) {
			languageToLoad = "kn";
		} else if (item.equalsIgnoreCase("Assammee")) {
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
        if(str.length() > strEng.length() + 5){
            return strEng;
        }
        return str;
    }
}
