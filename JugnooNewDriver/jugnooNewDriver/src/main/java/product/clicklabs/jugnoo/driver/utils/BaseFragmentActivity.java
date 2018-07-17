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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.util.Locale;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.DriverDocumentActivity;
import product.clicklabs.jugnoo.driver.HelpActivity;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.RegisterScreen;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.ui.DriverSplashActivity;

/**
 * Created by aneeshbansal on 09/05/16.
 */
public class BaseFragmentActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(savedInstanceState==null){
			updateLanguage(this,null);
		}
		updateStatusBar();
	}

	@Override
	protected void onResume() {

		super.onResume();
		checkIfUserDataNull();
	}





	public boolean checkIfUserDataNull() {
		if (Data.userData == null
				&& !(this instanceof DriverSplashActivity
					|| this instanceof RegisterScreen
					|| this instanceof DriverDocumentActivity
					|| this instanceof HelpActivity)) {
			// TODO: 25/04/18 IMP
			startActivity(new Intent(this, DriverSplashActivity.class));
			finish();
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
