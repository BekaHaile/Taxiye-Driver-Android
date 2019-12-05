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
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.Locale;

import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.DriverDocumentActivity;
import product.clicklabs.jugnoo.driver.HelpActivity;
import product.clicklabs.jugnoo.driver.JSONParser;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.RegisterScreen;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.sticky.GeanieView;
import product.clicklabs.jugnoo.driver.ui.DriverSplashActivity;

import static product.clicklabs.jugnoo.driver.Constants.REQUEST_OVERLAY_PERMISSION;

/**
 * Created by aneeshbansal on 09/05/16.
 */
public abstract class BaseFragmentActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		checkIfUserDataNull();

		if(savedInstanceState==null){
			recoverLastSavedLanguage();
			updateLanguage(this,null);
		}
		updateStatusBar();
	}

	protected void restartApp(){
		startActivity(new Intent(this, DriverSplashActivity.class));
		finishAffinity();
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

			checkOverlayPermissionOpenJeanie(this, false, true);

		}
		super.onStop();
	}

	public static void checkOverlayPermissionOpenJeanie(final Activity activity, final boolean askAgain, final boolean openJeanie){
		if(openJeanie && TextUtils.isEmpty(JSONParser.getAccessTokenPair(activity).first)){
			return;
		}
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if(!Settings.canDrawOverlays(activity)){
				if(askAgain || Prefs.with(activity).getInt(Constants.SP_OVERLAY_PERMISSION_ASKED, 0) == 0) {
					DialogPopup.alertPopupTwoButtonsWithListeners(activity, "", activity.getString(R.string.app_needs_overlay_permission),
							activity.getString(R.string.grant), activity.getString(R.string.ignore),
							new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									// ask for setting
									Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
											Uri.parse("package:" + activity.getPackageName()));
									activity.startActivityForResult(intent, REQUEST_OVERLAY_PERMISSION);
								}
							}, new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									if(openJeanie)activity.startService(new Intent(activity, GeanieView.class));
								}
							}, false, false);
					Prefs.with(activity).save(Constants.SP_OVERLAY_PERMISSION_ASKED, 1);
				}
			} else {
				if(openJeanie)activity.startService(new Intent(activity, GeanieView.class));
			}
		} else {
			if(openJeanie)activity.startService(new Intent(activity, GeanieView.class));
		}
	}

	public boolean checkIfUserDataNull() {
		if (Data.userData == null
				&& !(this instanceof DriverSplashActivity
					|| this instanceof RegisterScreen
					|| this instanceof DriverDocumentActivity
					|| this instanceof HelpActivity)) {
			// TODO: 25/04/18 IMP
			startActivity(new Intent(this, DriverSplashActivity.class));
			finishAffinity();
			overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
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

	public static String selectedLanguage="en";
	public static void updateLanguage(Activity activity, String language){
		if(language == null) {
			selectedLanguage = Prefs.with(activity).getString(SPLabels.SELECTED_LANGUAGE,activity.getString(R.string.default_lang));
		} else {
			Prefs.with(activity).save(SPLabels.SELECTED_LANGUAGE, language);
			selectedLanguage = language;
		}
		Locale locale = new Locale(selectedLanguage);
		Locale.setDefault(locale);

		Configuration config = new Configuration();
		config.locale = locale;
		activity.getBaseContext().getResources().updateConfiguration(config,
				activity.getBaseContext().getResources().getDisplayMetrics());
		activity.onConfigurationChanged(config);

		if(isSupported(activity,activity.getResources().getString(R.string.email))){
			Log.i("yes","");
		}else{
			Locale.setDefault(new Locale("en"));
			Configuration conf = new Configuration();
			conf.locale = new Locale("en");
			activity.getBaseContext().getResources().updateConfiguration(conf,
					activity.getBaseContext().getResources().getDisplayMetrics());
			Toast.makeText(activity, "Selected language is not supported by your phone", Toast.LENGTH_LONG).show();
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


	/**
	 * Helper method to load fragments into layout
	 *
	 * @param containerResId The container resource Id in the content view into which to load the
	 *                       fragment
	 * @param mainFragment   The fragment to load
	 * @param tag            The fragment tag
	 */
	public void loadFragment(final int containerResId,
							 final Fragment mainFragment, final String tag,
							 final boolean customAnimate, final boolean hideView) {

		final FragmentManager fragmentManager = getSupportFragmentManager();
		final FragmentTransaction transaction = fragmentManager.beginTransaction();

		if (customAnimate) {
			transaction.setCustomAnimations(R.anim.slide_in_right, R.anim.hold, R.anim.slide_in_left, R.anim.hold);
		}
		transaction.add(containerResId, mainFragment, tag);
		transaction.addToBackStack(tag);
		if(hideView && getSupportFragmentManager().getBackStackEntryCount() > 0) {
			transaction.hide(getSupportFragmentManager().findFragmentByTag(getSupportFragmentManager()
					.getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName()));
		}
		transaction.commitAllowingStateLoss();

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

	private void recoverLastSavedLanguage(){
		if(Prefs.with(this).getInt(SPLabels.FIRST_TIME, 1000) == 1000) {
			Prefs.with(this).save(SPLabels.FIRST_TIME, 0);

			String lastLang = Prefs.with(this).getString(SPLabels.SELECTED_LANGUAGE, getString(R.string.default_lang));
			String languageToLoad = "en";
			if (lastLang.equalsIgnoreCase("English")) {
				languageToLoad = "en";
			} else if (lastLang.equalsIgnoreCase("हिन्दी")) {
				languageToLoad = "hi";
			} else if (lastLang.equalsIgnoreCase("ગુજરાતી")) {
				languageToLoad = "gu";
			} else if (lastLang.equalsIgnoreCase("ଓଡ଼ିଆ")) {
				languageToLoad = "or";
			} else if (lastLang.equalsIgnoreCase("മലയാളം")) {
				languageToLoad = "ml";
			} else if (lastLang.equalsIgnoreCase("தமிழ்")) {
				languageToLoad = "ta";
			} else if (lastLang.equalsIgnoreCase("తెలుగు")) {
				languageToLoad = "te";
			} else if (lastLang.equalsIgnoreCase("ಕನ್ನಡ")) {
				languageToLoad = "kn";
			} else if (lastLang.equalsIgnoreCase("অসমীয়া")) {
				languageToLoad = "as";
			} else if (lastLang.equalsIgnoreCase("français")) {
				languageToLoad = "fr";
			} else if (lastLang.equalsIgnoreCase("عربى")) {
				languageToLoad = "ar";
			} else {
				languageToLoad = lastLang;
			}
			Prefs.with(this).save(SPLabels.SELECTED_LANGUAGE, languageToLoad);
		}
	}
}
