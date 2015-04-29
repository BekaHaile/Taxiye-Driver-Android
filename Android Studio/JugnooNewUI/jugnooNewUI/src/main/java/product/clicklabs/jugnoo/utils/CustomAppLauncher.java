package product.clicklabs.jugnoo.utils;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

public class CustomAppLauncher {

	//product.clicklabs.postmygreetings
	
	public static void launchApp(Activity activity, String packageName){
		try {
			Intent intent;
			PackageManager manager = activity.getPackageManager();
		    intent = manager.getLaunchIntentForPackage(packageName);
		    if (intent == null){
		        throw new PackageManager.NameNotFoundException();
		    }
		    intent.addCategory(Intent.CATEGORY_LAUNCHER);
		    activity.startActivity(intent);
		} catch (Exception e) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setData(Uri.parse("market://details?id="+packageName));
			activity.startActivity(intent);
		}
	}
	
}
