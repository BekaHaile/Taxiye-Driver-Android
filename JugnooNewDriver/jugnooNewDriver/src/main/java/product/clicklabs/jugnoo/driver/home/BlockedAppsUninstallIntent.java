package product.clicklabs.jugnoo.driver.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;

import org.json.JSONException;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.Utils;

/**
 * Created by shankar on 5/27/16.
 */
public class BlockedAppsUninstallIntent {

	public void uninstallBlockedApps(final Context context){
		try {
			for (int i = 0; i < Data.blockAppPackageNameList.length(); i++) {
				try {
					final String packageName = Data.blockAppPackageNameList.getString(i);
					if (Data.SERVER_URL.equalsIgnoreCase(Data.LIVE_SERVER_URL)
							&& Utils.fetchUserInstalledApps(context, packageName)) {
						DialogPopup.alertPopupWithListener((Activity) context, "",
								Data.userData.blockedAppPackageMessage,
								new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										Uri packageUri = Uri.parse("package:" + packageName);
										Intent uninstallIntent =
												new Intent(Intent.ACTION_UNINSTALL_PACKAGE, packageUri);
										context.startActivity(uninstallIntent);
									}
								});
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
