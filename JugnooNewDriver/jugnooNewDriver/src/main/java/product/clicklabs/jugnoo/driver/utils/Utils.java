package product.clicklabs.jugnoo.driver.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.CursorWindow;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import product.clicklabs.jugnoo.driver.BuildConfig;
import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.MyApplication;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.retrofit.CurrencyModel;


public class Utils {

    /**
     * Compares two double values with epsilon precision
     *
     * @param d1 double value 1
     * @param d2 double value 2
     * @return 1 if d1 > d2,
     * -1 if d1 < d2 &
     * 0 if d1 == d2
     */
    private static final String TAG = Utils.class.getName();

    public static int compareDouble(double d1, double d2) {
        if (d1 == d2) {
            return 0;
        } else {
            double epsilon = 0.0000001;
            if ((d1 - d2) > epsilon) {
                return 1;
            } else if ((d1 - d2) < epsilon) {
                return -1;
            } else {
                return 0;
            }
        }
    }


    /**
     * Expands ListView for fixed height of item inside a ScrollView
     */
    public static void expandListForFixedHeight(ListView list) {
        try {
            if (list.getCount() > 0) {
                ListAdapter listAdap = list.getAdapter();
                int totalHeight = 0;

                View listItem = listAdap.getView(0, null, list);
                listItem.measure(0, 0);
                int singleHeight = listItem.getMeasuredHeight();
                totalHeight = singleHeight * list.getCount();

//				for (int i = 0; i < listAdap.getCount(); i++) {
//					View listItem = listAdap.getView(i, null, list);
//					listItem.measure(0, 0);
//					totalHeight += listItem.getMeasuredHeight();
//				}
                ViewGroup.LayoutParams params = list.getLayoutParams();
                params.height = totalHeight + (list.getDividerHeight() * (list.getCount() - 1));
                list.setLayoutParams(params);
                list.requestLayout();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Expands ListView for variable height of item inside a ScrollView
     */
    public static void expandListForVariableHeight(ListView list) {
        try {
            if (list.getCount() > 0) {
                ListAdapter listAdap = list.getAdapter();
                int totalHeight = 0;

                for (int i = 0; i < listAdap.getCount(); i++) {
                    View listItem = listAdap.getView(i, null, list);
                    listItem.measure(0, 0);
                    totalHeight += listItem.getMeasuredHeight();
                }
                ViewGroup.LayoutParams params = list.getLayoutParams();
                params.height = totalHeight + (list.getDividerHeight() * (list.getCount() - 1));
                list.setLayoutParams(params);
                list.requestLayout();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void hideSoftKeyboard(Activity activity, View searchET) {
        try {
            InputMethodManager mgr = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(searchET.getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void showSoftKeyboard(Activity activity, View searchET) {
        try {
            InputMethodManager keyboard = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            keyboard.showSoftInput(searchET, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static HashMap<String, String> convertQueryToNameValuePairArr(String query)
            throws UnsupportedEncodingException {
        HashMap<String, String> nameValuePairs = new HashMap<>();
        String[] pairs = query.substring(1, query.length() - 1).split(", ");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            nameValuePairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return nameValuePairs;
    }


    public static String[] splitStringInParts(String s, int partLength) {
        int len = s.length();

        // Number of parts
        int nparts = (len + partLength - 1) / partLength;
        String parts[] = new String[nparts];

        // Break into parts
        int offset = 0;
        int i = 0;
        while (i < nparts) {
            parts[i] = s.substring(offset, Math.min(offset + partLength, len));
            offset += partLength;
            i++;
        }

        return parts;
    }


    public static boolean mockLocationEnabled(Location location) {
		if (location != null
				&& !BuildConfig.DEBUG_MODE
				&& Data.DEFAULT_SERVER_URL.equalsIgnoreCase(Data.LIVE_SERVER_URL)) {
			return location.isFromMockProvider();
		} else {
			return false;
		}
    }


    public static String getChronoTimeFromMillis(long elapsedTime) {
        long timeR = elapsedTime;
        int hR = (int) (timeR / 3600000);
        int mR = (int) (timeR - hR * 3600000) / 60000;
        int sR = (int) (timeR - hR * 3600000 - mR * 60000) / 1000;
        String hhR = hR < 10 ? "0" + hR : hR + "";
        String mmR = mR < 10 ? "0" + mR : mR + "";
        String ssR = sR < 10 ? "0" + sR : sR + "";
        return (hhR + ":" + mmR + ":" + ssR);
    }


    public static void openCallIntent(Activity activity, String phoneNumber) {
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + phoneNumber));
        activity.startActivity(callIntent);
    }

	public static void makeCallIntent(Activity activity, String phoneNumber) {
        openCallIntent(activity, phoneNumber);
	}

    public static String hidePhoneNoString(String phoneNo) {
        String returnPhoneNo = "";
        if (phoneNo.length() > 0) {
            int charLength = phoneNo.length();
            int stars = (charLength < 4) ? 0 : (charLength - 4);
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < stars; i++) {
                stringBuilder.append("*");
            }
            returnPhoneNo = stringBuilder.toString() + phoneNo.substring(stars, phoneNo.length());
        }
        return returnPhoneNo;
    }


    public static boolean isPhoneValid(CharSequence phone) {
        return android.util.Patterns.PHONE.matcher(phone).matches();
    }


    public static boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                deleteDir(dir);
            }
        } catch (Exception ignored) {
        }
    }

    private static boolean deleteDir(File dir) {
        if (dir != null && (dir.getName().equals("lib") || dir.getName().contains("io.paperdb"))) {
            Log.i(TAG, "Attempt to Delete " + dir + " stopped!");
            return true;
        }
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String aChildren : children) {
                boolean success = deleteDir(new File(dir, aChildren));
                if (!success) {
                    return false;
                }
            }
        }
        return dir == null || dir.delete();
    }


    public static boolean checkIfOnlyDigits(String strTocheck) {
        String regex = "[0-9+]+";
        if (strTocheck.matches(regex)) {
            return true;
        } else {
            return false;
        }
    }



    public static NumberFormat getDecimalFormatForMoney() {
        if (numberFormat == null) {
            initNumberFormat();
        }
        return numberFormat;
    }


    private static DecimalFormat decimalFormat;
    public static DecimalFormat getDecimalFormat() {
        if (decimalFormat == null) {
            decimalFormat = new DecimalFormat("0.00");
        }
        return decimalFormat;
    }

//	isAppInstalled("com.autoncab.driver");

    public static boolean isAppInstalled(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        boolean installed = false;
        try {
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            installed = false;
        }
        return installed;
    }


    public static boolean olaInstall(Context context) {
        // Flags: See below
        boolean olaDriver = false;
        int flags = PackageManager.GET_META_DATA |
                PackageManager.GET_SHARED_LIBRARY_FILES |
                PackageManager.GET_UNINSTALLED_PACKAGES;

        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> applications = pm.getInstalledApplications(flags);
        for (ApplicationInfo appInfo : applications) {
            if (!appInfo.packageName.contains("com.olacabs.customer")) {
                olaDriver = (appInfo.packageName.contains("com.ola") || appInfo.packageName.contains("olacabs"));
                if (olaDriver) {
                    break;
                }
            } else if (appInfo.packageName.contains("com.olacabs.kpdriver")) {
                olaDriver = true;
            }

        }
        return olaDriver;

    }


    public static boolean isDeviceRooted() {
        return checkRootMethod1() || checkRootMethod2() || checkRootMethod3();
    }

    private static boolean checkRootMethod1() {
        String buildTags = android.os.Build.TAGS;
        return buildTags != null && buildTags.contains("test-keys");
    }

    private static boolean checkRootMethod2() {
        String[] paths = {"/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su",
                "/system/bin/failsafe/su", "/data/local/su"};
        for (String path : paths) {
            if (new File(path).exists()) return true;
        }
        return false;
    }

    private static boolean checkRootMethod3() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[]{"/system/xbin/which", "su"});
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            if (in.readLine() != null) return true;
            return false;
        } catch (Throwable t) {
            return false;
        } finally {
            if (process != null) process.destroy();
        }
    }

    public static boolean telerickshawInstall(Context context) {

        // Flags: See below
        boolean telerickshawDriver = false;
        int flags = PackageManager.GET_META_DATA |
                PackageManager.GET_SHARED_LIBRARY_FILES |
                PackageManager.GET_UNINSTALLED_PACKAGES;

        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> applications = pm.getInstalledApplications(flags);
        for (ApplicationInfo appInfo : applications) {
            if (!appInfo.packageName.contains("com.gcs.telerickshaw")) {
                telerickshawDriver = (appInfo.packageName.contains("com.telerickshaw") || appInfo.packageName.contains("telerickshaw"));
                if (telerickshawDriver) {

                    break;
                }
            }

        }
        return telerickshawDriver;

    }

    public static JSONArray fetchAllApps(Context context) {
        try {
            int flags = PackageManager.GET_META_DATA;

            PackageManager pm = context.getPackageManager();
            List<ApplicationInfo> applications = pm.getInstalledApplications(flags);
            List<PackageInfo> packages = pm.getInstalledPackages(flags);
            JSONArray appList = new JSONArray();

            if ((Prefs.with(context).getInt(Constants.FETCH_APP_API_ENABLED, 1) == 1)) {
                for (ApplicationInfo appInfo : applications) {
                    appList.put(appInfo.packageName);
                }
            } else if ((Prefs.with(context).getInt(Constants.FETCH_APP_API_ENABLED, 1) == 2)) {
                for (ApplicationInfo appInfo : applications) {
                    if (pm.getLaunchIntentForPackage(appInfo.packageName) != null) {
                        appList.put(appInfo.packageName);
                    }
                }
            } else if ((Prefs.with(context).getInt(Constants.FETCH_APP_API_ENABLED, 1) == 3)) {
                for (PackageInfo packageInfo : packages) {
                    if (packageInfo.versionName != null && ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 1)) {
                        appList.put(packageInfo.packageName);
                    }
                }
            }

            return appList;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    public static boolean isServiceRunning(Context context, Class serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    public static String getDeviceName() {
        return (android.os.Build.MANUFACTURER + android.os.Build.MODEL).toString();
    }

    public static int getAppVersion(Context context) {
        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static File getStorageDirectory(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            return context.getExternalFilesDir(null);
        else
            return context.getFilesDir();
    }

    public static void saveImage(final Bitmap bitmap, final String saveToFile, Context context) {

        File mImagefile = new File(Utils.getStorageDirectory(context), saveToFile);

        if (mImagefile.exists()) {
            mImagefile.delete();
        }
        try {
            final FileOutputStream out = new FileOutputStream(mImagefile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }


    public static void enableReceiver(Context context, Class classT, boolean enable) {
        try {
            ComponentName receiver = new ComponentName(context, classT);
            PackageManager pm = context.getPackageManager();
            if (enable) {
                pm.setComponentEnabledSetting(receiver,
                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                        PackageManager.DONT_KILL_APP);
            } else {
                pm.setComponentEnabledSetting(receiver,
                        PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                        PackageManager.DONT_KILL_APP);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static float getBatteryPercentage(Context context) {
        try {
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, ifilter);
            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            float batteryPct = (level / (float) scale) * 100;

            // Are we charging / charged?
            int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL;
            if (isCharging) {
                return 70;
            } else {
                return batteryPct;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 70;
        }
    }

    public static String getActualBatteryPer(Context context) {
        try {
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, ifilter);
            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            float batteryPct = (level / (float) scale) * 100;

            // Are we charging / charged?
            int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL;

            return String.valueOf(batteryPct);

        } catch (Exception e) {
            e.printStackTrace();
            return "0";
        }
    }

    public static int isBatteryChargingNew(Context context) {
        try {
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, ifilter);
            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            float batteryPct = (level / (float) scale) * 100;
            // Are we charging / charged?
            int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL;
            if (isCharging) {
                return 1;
            } else {
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static boolean isBatteryCharging(Context context) {
        try {
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = context.registerReceiver(null, ifilter);
            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            float batteryPct = (level / (float) scale) * 100;
            // Are we charging / charged?
            int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL || batteryPct > 60;
            return isCharging;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public static void deleteGpsData(Context context) {
        /* Cold start */
        Bundle bundle = new Bundle();
        bundle.putBoolean("all", true);
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationManager.sendExtraCommand(LocationManager.GPS_PROVIDER, "delete_aiding_data", null);
        locationManager.sendExtraCommand("gps", "force_xtra_injection", bundle);
        locationManager.sendExtraCommand("gps", "force_time_injection", bundle);
    }

    public static void openNavigationIntent(Context context, LatLng latLng) {
        try {
            if (Prefs.with(context).getInt(Constants.KEY_NAVIGATION_TYPE, Constants.NAVIGATION_TYPE_GOOGLE_MAPS)
                    == Constants.NAVIGATION_TYPE_WAZE) {
                try {
                    if (isAppInstalled("com.waze", context.getPackageManager())) {
                        // Launch Waze to navigate:
                        String url = "https://waze.com/ul?ll=" + latLng.latitude + "," + latLng.longitude + "&navigate=yes";
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        context.startActivity(intent);
                    } else {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.waze"));
                        context.startActivity(intent);
                    }
                } catch (ActivityNotFoundException ex) {
                    // If Waze is not installed, open it in Google Play:
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.waze"));
                    context.startActivity(intent);
//                    Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse( "https://play.google.com/store/apps/details?id=com.waze" ) );
//                    context.startActivity(intent);
                }

            } else {
                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latLng.latitude + "," + latLng.longitude);
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                context.startActivity(mapIntent);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isAppInstalled(String packagename, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packagename, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static void setDrawableColor(View view, String color, int defaultColor) {
        try {
            int intColor = defaultColor;
            if (color != null && (color.length() == 7 || color.length() == 9)) {
                intColor = Color.parseColor(color);
            }
            Drawable background = view.getBackground();
            if (background instanceof ShapeDrawable) {
                ((ShapeDrawable) background).getPaint().setColor(intColor);
            } else if (background instanceof GradientDrawable) {
                ((GradientDrawable) background).setColor(intColor);
            } else if (background instanceof ColorDrawable) {
                ((ColorDrawable) background).setColor(intColor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setTextColor(View view, String color, int defaultColor) {
        try {
            int intColor = defaultColor;
            if (color != null && (color.length() == 7 || color.length() == 9)) {
                intColor = Color.parseColor(color);
            }
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(intColor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Bitmap setBitmapColor(Bitmap sourceBitmap, String color, int defaultColor) {
        int intColor = defaultColor;
        if (color != null && (color.length() == 7 || color.length() == 9)) {
            intColor = Color.parseColor(color);
        }
        Bitmap resultBitmap = Bitmap.createBitmap(sourceBitmap, 0, 0,
                sourceBitmap.getWidth() - 1, sourceBitmap.getHeight() - 1);
        Paint p = new Paint();
        ColorFilter filter = new LightingColorFilter(intColor, 1);
        p.setColorFilter(filter);

        Canvas canvas = new Canvas(resultBitmap);
        canvas.drawBitmap(resultBitmap, 0, 0, p);
        return resultBitmap;
    }


    public static byte[] gzipCompress(String string) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream(string.length());
        GZIPOutputStream gos = new GZIPOutputStream(os);
        gos.write(string.getBytes());
        gos.close();
        byte[] compressed = os.toByteArray();
        os.close();
        return compressed;
    }

    public static String gzipDecompress(byte[] compressed) throws IOException {
        final int BUFFER_SIZE = 32;
        ByteArrayInputStream is = new ByteArrayInputStream(compressed);
        GZIPInputStream gis = new GZIPInputStream(is, BUFFER_SIZE);
        StringBuilder string = new StringBuilder();
        byte[] data = new byte[BUFFER_SIZE];
        int bytesRead;
        while ((bytesRead = gis.read(data)) != -1) {
            string.append(new String(data, 0, bytesRead));
        }
        gis.close();
        is.close();
        return string.toString();
    }

    public static void deleteMFile(Context context) {
        File dir = new File(context.getFilesDir()+ "/JugnooData");
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                File file = new File(dir, children[i]);
                long diff = new Date().getTime() - file.lastModified();
                if (diff > 30L * 24L * 60L * 60L * 1000L) {
                    file.delete();
                }
            }
        }
    }


    public static void clearApplicationData(Context context) {

        if ((System.currentTimeMillis() - Prefs.with(context).getLong(SPLabels.CLEAR_APP_CACHE_TIME, 0)) > (7L * 24L * 60L * 60L * 1000L)) {
            Prefs.with(context).save(SPLabels.CLEAR_APP_CACHE_TIME, System.currentTimeMillis());
            File cache = context.getCacheDir();
            File appDir = new File(cache.getParent());
            if (appDir.exists()) {
                String[] children = appDir.list();
                for (String s : children) {
                    if (!s.equals("lib") && !s.contains("io.paperdb")) {
                        deleteDir(new File(appDir, s));
                        Log.i("TAG", "File /data/data/APP_PACKAGE/" + s + " DELETED");
                    } else {
                        Log.i(TAG, "Attempt to Delete " + s + " stopped!");
                    }
                }
            }
        }
    }

    public static boolean fetchUserInstalledApps(Context context, String packageName) {
        try {
            int flags = PackageManager.GET_META_DATA;
            PackageManager pm = context.getPackageManager();
            List<PackageInfo> packages = pm.getInstalledPackages(flags);
            boolean installed = false;

            for (PackageInfo packageInfo : packages) {
                if (packageInfo.versionName != null && ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 1)) {
                    if (packageName.equalsIgnoreCase(packageInfo.packageName)) {
                        installed = true;
                        break;
                    }
                }
            }
            return installed;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public static File compressToFile(Context context, Bitmap src, Bitmap.CompressFormat format,
                                       int quality) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        src.compress(format, quality, os);
        long index2 = System.currentTimeMillis();
        File f = new File(context.getFilesDir(), "temp" + index2 + ".jpg");
        try {
            f.createNewFile();
            byte[] bitmapData = os.toByteArray();

            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bitmapData);
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return f;
    }


    public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        try {
            int width = bm.getWidth();
            int height = bm.getHeight();
            float scaleWidth = ((float) newWidth) / width;
            float scaleHeight = ((float) newHeight) / height;
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
                    matrix, false);

            return resizedBitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return bm;
        }
    }

    public static int dpToPx(Context context, float dp) {
        int temp = (int) dp;
        final float scale = context.getResources().getDisplayMetrics().density;
        return Math.round(dp * scale);
    }

    public static Shader textColorGradient(Context context, TextView textView, int colorStart, int colorEnd) {
        textView.measure(0, 0);
        int mWidth = textView.getMeasuredWidth();
        Shader shader;
        Shader.TileMode tile_mode = Shader.TileMode.CLAMP; // or TileMode.REPEAT;
        LinearGradient lin_grad = new LinearGradient(0, 0, (int) (mWidth / 1.3), 0,
                colorStart, colorEnd, tile_mode);
        shader = lin_grad;

        return shader;
    }

    public static String getActivityName(Context context) {
        String mPackageName = "";
        ActivityManager mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > 20) {
            mPackageName = mActivityManager.getRunningAppProcesses().get(0).processName;
        } else {
            mPackageName = mActivityManager.getRunningTasks(1).get(0).topActivity.getPackageName();
        }
        return mPackageName;
    }

    public static CharSequence trimHTML(CharSequence s) {
        if (s.length() == 0)
            return "";

        int start = 0;
        int end = s.length();
        while (start < end && Character.isWhitespace(s.charAt(start))) {
            start++;
        }

        while (end > start && Character.isWhitespace(s.charAt(end - 1))) {
            end--;
        }

        return s.subSequence(start, end);
    }

    @SuppressWarnings("deprecation")
    public static CharSequence fromHtml(String html) {
        CharSequence result = trimHTML(Html.fromHtml(html));
        return result;
    }

    public static String getKilometers(double kilometer, Context context, String distanceUnit) {

        return getDecimalFormat().format(kilometer) + " " + Utils.getDistanceUnit(distanceUnit);
    }

    public static String getKilometers(String kilometer, Context context, String distanceUnit) {


        try {
            return getKilometers(Double.parseDouble(kilometer), context, distanceUnit);
        } catch (Exception e) {
            e.printStackTrace();
            return "" + kilometer + " " + Utils.getDistanceUnit(distanceUnit);
        }


    }


    public static String getTimeFromMins(Context context, int min) {
        int hours = min / 60;
        if (hours <= 0) {
            return min + " " + context.getString(R.string.min);
        }

        String time = hours + " " + (hours == 1 ? context.getString(R.string.hour) : context.getString(R.string.hours));
        int minToDisplay = min % 60;
        if (minToDisplay > 0) {
            time += " " + minToDisplay + " " + context.getString(R.string.min);
        }

        return time;
    }

    public static String getCurrencySymbol(String currencyCode) {
        if (TextUtils.isEmpty(currencyCode)) {
            currencyCode = MyApplication.getInstance().getResources().getString(R.string.currency_fallback);
        } else if(currencyCode.equalsIgnoreCase("BMD") || currencyCode.equalsIgnoreCase("TTD")){
            return "$";
        }
        Currency currency = Currency.getInstance(currencyCode);
        return currency.getSymbol();
    }

    public static String formatCurrencyValue(String currency, double value) {
        return formatCurrencyValue(currency, value, MyApplication.getInstance().getResources().getString(R.string.currency_fallback));
    }

    public static String formatCurrencyValue(String currency, double value, String fallbackCurrency){
        return formatCurrencyValue(currency, value, fallbackCurrency, true);
    }
    public static String formatCurrencyValue(String currency, double value, boolean setPrecision) {
        return formatCurrencyValue(currency, value, MyApplication.getInstance().getResources().getString(R.string.currency_fallback), setPrecision);
    }

    private static NumberFormat currencyNumberFormat = null;
    public static String formatCurrencyValue(String currency, double value, String fallbackCurrency, boolean setPrecision) {
        try {
            if(currencyNumberFormat == null){
                currencyNumberFormat = NumberFormat.getCurrencyInstance(MyApplication.getInstance().getCurrentLocale());
                currencyNumberFormat.setRoundingMode(RoundingMode.HALF_UP);
                currencyNumberFormat.setGroupingUsed(false);
                currencyNumberFormat.setMinimumFractionDigits(2);
                currencyNumberFormat.setMaximumFractionDigits(2);
            }
            int precision = Prefs.with(MyApplication.getInstance()).getInt(Constants.KEY_CURRENCY_PRECISION, 0);
//            currencyNumberFormat.setMinimumFractionDigits(setPrecision ? precision : 0);
//            currencyNumberFormat.setMaximumFractionDigits(setPrecision ? precision : Math.max(2, precision));
            if (TextUtils.isEmpty(currency)) {
                currency = fallbackCurrency;
            }
            currencyNumberFormat.setCurrency(Currency.getInstance(currency));
            String result = currencyNumberFormat.format(value);

            result = result.replaceFirst("\\s", "");
            result = result.replace("BMD", "$");
            result = result.replace("TTD", "$");

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return currency+Utils.getDecimalFormat().format(value);
        }
    }

    public static String formatCurrencyValue(String currency, String value) {
        try {
            return formatCurrencyValue(currency, Double.parseDouble(value));
        } catch (NumberFormatException e) {
            return value;
        }
    }


    public static <T extends CurrencyModel> String formatCurrencyValue(T currencyModel, String value) {
        try {
            return formatCurrencyValue(currencyModel.getCurrencyUnit(), Double.parseDouble(value));
        } catch (NumberFormatException e) {
            return value;
        }
    }

    public static String getCountryCode(Context context) {
        String serverCountryCode = Prefs.with(context).getString(Constants.KEY_DEFAULT_COUNTRY_CODE, "");
        if(!TextUtils.isEmpty(serverCountryCode)){
            return serverCountryCode;
        }
        if(context.getResources().getInteger(R.integer.apply_default_country_code) == 1){
            return context.getString(R.string.default_country_code);
        }
        String CountryID = "";
        String CountryZipCode = "";

		TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		// getNetworkCountryIso
		try {
			CountryID = manager.getSimCountryIso().toUpperCase();
			Log.e("CountryID", "=" + CountryID);
			String[] rl = context.getResources().getStringArray(R.array.CountryCodes);
			for (String aRl : rl) {
				String[] g = aRl.split(",");
				if (g[1].trim().equals(CountryID.trim())) {
					CountryZipCode = g[0];
					if(!CountryZipCode.contains("+")){
						CountryZipCode = "+" + CountryZipCode;
					}
					return CountryZipCode;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

    public static String getCountryCodeFromCountryIso(Context context, String countryIso) {
        String CountryZipCode = "";
        try {
            String[] rl = context.getResources().getStringArray(R.array.CountryCodes);
            for (String aRl : rl) {
                String[] g = aRl.split(",");
                if (g[1].trim().equals(countryIso.trim())) {
                    CountryZipCode = g[0];
                    return CountryZipCode;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String getCountryIsoFromCode(Context context, String code) {
        try {
            String[] rl = context.getResources().getStringArray(R.array.CountryCodes);
            for (String aRl : rl) {
                String[] g = aRl.split(",");
                if (g[0].trim().equals(code.trim())) {
                    return g[1];
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "IN";
    }


    public static String retrievePhoneNumberTenChars(String countryCode, String phoneNo) {
        phoneNo = phoneNo.replace(" ", "");
        phoneNo = phoneNo.replace("(", "");
        phoneNo = phoneNo.replace("/", "");
        phoneNo = phoneNo.replace(")", "");
        phoneNo = phoneNo.replace("N", "");
        phoneNo = phoneNo.replace(",", "");
        phoneNo = phoneNo.replace("*", "");
        phoneNo = phoneNo.replace(";", "");
        phoneNo = phoneNo.replace("#", "");
        phoneNo = phoneNo.replace("-", "");
        phoneNo = phoneNo.replace(".", "");
        phoneNo = phoneNo.replace(countryCode, "");
        phoneNo = phoneNo.replace("+", "");
        return phoneNo;
    }

    public static boolean validPhoneNumber(String phoneNo) {
        if (phoneNo != null && phoneNo.length() >= 7 && phoneNo.length() <= 14 && checkIfOnlyDigits(phoneNo)) {
            return isPhoneValid(phoneNo);
        } else {
            return false;
        }
    }

    public static void showToast(Context context, String string) {
        showToast(context, string, Toast.LENGTH_SHORT);
    }

    public static void showToast(Context context, String string, int duration) {
        try {
            if (MyApplication.getInstance().getToast() != null) {
                MyApplication.getInstance().getToast().cancel();
            }
            MyApplication.getInstance().setToast(Toast.makeText(context, string, duration));
            MyApplication.getInstance().getToast().show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void openMailIntent(Activity activity, String[] to, String subject, String body) {
        Intent email = new Intent(Intent.ACTION_SEND);
        email.putExtra(Intent.EXTRA_EMAIL, to);
        email.putExtra(Intent.EXTRA_SUBJECT, subject);
        email.putExtra(Intent.EXTRA_TEXT, body); //
        email.setType("message/rfc822");
        activity.startActivity(Intent.createChooser(email, activity.getString(R.string.choose_email_client)));
    }

    public static String getDistanceUnit(String distanceUnit) {
        if (TextUtils.isEmpty(distanceUnit)) {
            return MyApplication.getInstance().getString(R.string.km);
        } else {
            return distanceUnit;
        }
    }

	public static void setTypeface(Context context,View... views){

		for(View textView:views){
			((TextView)textView).setTypeface(Fonts.mavenRegular(context.getApplicationContext()));

		}

	}

    public static boolean appInstalledOrNot(Context context, String uri) {
        PackageManager pm = context.getPackageManager();
        boolean appInstalled = false;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            appInstalled = true;
        }
        catch (Exception e) {
            appInstalled = false;
        }
        return appInstalled;
    }

    public static boolean checkIfOnlyDigitsDecimal(String strTocheck){
        String regex = "[0-9.]*";
        return strTocheck.matches(regex);
    }
	public static boolean isGPSEnabled(final Context context) {

		final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		return manager != null && manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	private static NumberFormat numberFormat = null;
    private static void initNumberFormat(){
        int precision = Prefs.with(MyApplication.getInstance()).getInt(Constants.KEY_CURRENCY_PRECISION, 0);
        numberFormat = NumberFormat.getInstance(Locale.ENGLISH);
        numberFormat.setMinimumFractionDigits(precision);
        numberFormat.setMaximumFractionDigits(precision);
        numberFormat.setRoundingMode(RoundingMode.HALF_UP);
        numberFormat.setGroupingUsed(false);
    }
    public static void setCurrencyPrecision(Context context, int precision){
        Prefs.with(context).save(Constants.KEY_CURRENCY_PRECISION, precision);
        numberFormat = null;
        currencyNumberFormat = null;
    }
    public static double currencyPrecision(double value) {
        if(numberFormat == null){
            initNumberFormat();
        }
        String result = numberFormat.format(value);
        if(numberFormat.getMaximumFractionDigits() > 0){
            return Double.parseDouble(result);
        } else {
            return Integer.parseInt(result);
        }
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static String retrieveOTPFromSMS(String message){
        String[] arr = message.split("\\ ");
        for(String iarr : arr){
            iarr = iarr.replace(".", "");
            if(iarr.length() >= 3 && checkIfOnlyDigits(iarr)){
                return iarr;
            }
        }
        return "";
    }

    public static String readFileFromAssets(Activity activity, String fileName){
        String mLine;
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(activity.getAssets().open(fileName), "UTF-8"))) {

            // do reading, usually loop until end of file reading
            String slashN = "\n";
            while ((mLine = reader.readLine()) != null) {
                //process line
                sb.append(mLine).append(slashN);
            }
        } catch (IOException e) {
            //log the exception
        }
        //log the exception
        return sb.toString();
    }

    public static int getCameraPhotoOrientation(Context context, Uri imageUri, String imagePath){
        int rotate = 0;
        try {
            context.getContentResolver().notifyChange(imageUri, null);
            File imageFile = new File(imagePath);

            ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }

            Log.i("RotateImage", "Exif orientation: " + orientation);
            Log.i("RotateImage", "Rotate value: " + rotate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

	public static void cursorWindowFix() {
		try {
			Field field = CursorWindow.class.getDeclaredField("sCursorWindowSize");
			field.setAccessible(true);
			field.set(null, 102400 * 1024); //the 102400 is the new size added
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	static String prettyJson(Object object) {
		String json = "";
		if (BuildConfig.DEBUG && object != null) {
			Gson parser = new GsonBuilder().setPrettyPrinting().create();
			try {
				json = parser.toJson(new JsonParser().parse(objecttoJson(object)));
			} catch (Exception e) {
				try {
					json = parser.toJson(new JsonParser().parse(object.toString()));
				} catch (Exception ignore) {}
			}
			if (json.isEmpty() || json.equalsIgnoreCase(""))
				json = objecttoJson(object);
		}
		return json;
	}

	private static String objecttoJson(Object object) {
		return new Gson().toJson(object).replace("\\", "");
	}
}
