package product.clicklabs.jugnoo.driver.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;

import org.jetbrains.annotations.NotNull;

import product.clicklabs.jugnoo.driver.Data;

public class DeviceUniqueID {
    private static final String SP_DEVICE_UNIQUE_ID = "sp_device_unique_id";

    @SuppressLint("MissingPermission")
    public static String getUniqueId(Context context) {
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

            String imei = "";

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                imei = telephonyManager.getImei();
            } else {
                imei = telephonyManager.getDeviceId();
            }

            Log.e("imei", "=" + imei);

            if (imei == null || imei.isEmpty()){
                imei = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
            }

//            imei += (BuildConfig.DEBUG ? BuildConfig.FLAVOR : "");

            DeviceUniqueID.saveUniqueId(context, imei);

            return imei;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "not_found";
    }

    public static String getCachedUniqueId(final Context context) {

        SharedPreferences preferences = context.getSharedPreferences(Data.SETTINGS_SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return preferences.getString(SP_DEVICE_UNIQUE_ID, "");
    }

    private static void saveUniqueId(final Context context, @NotNull final String id) {

        // save id to shared preferences for future use
            SharedPreferences preferences = context.getSharedPreferences(Data.SETTINGS_SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(SP_DEVICE_UNIQUE_ID, id);
            editor.apply();
    }
}
