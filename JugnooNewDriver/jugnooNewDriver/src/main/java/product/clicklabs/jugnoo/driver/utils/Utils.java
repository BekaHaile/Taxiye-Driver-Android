package product.clicklabs.jugnoo.driver.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.CallLog;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderApi;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;


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
		String[] pairs = query.substring(1, query.length()-1).split(", ");
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

//		return false;
		try {
			if (Data.DEFAULT_SERVER_URL.equalsIgnoreCase(Data.LIVE_SERVER_URL)) {
				boolean isMockLocation = false;
				if(location != null){
					Bundle extras = location.getExtras();
					isMockLocation = extras != null && extras.getBoolean(FusedLocationProviderApi.KEY_MOCK_LOCATION, false);
				}
				return isMockLocation;
			} else {
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
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
		Intent callIntent = new Intent(Intent.ACTION_VIEW);
		callIntent.setData(Uri.parse("tel:" + phoneNumber));
		activity.startActivity(callIntent);
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


	public static String retrievePhoneNumberTenChars(String phoneNo) {
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
		if (phoneNo.length() >= 10) {
			phoneNo = phoneNo.substring(phoneNo.length() - 10, phoneNo.length());
		}
		return phoneNo;
	}

	public static boolean validPhoneNumber(String phoneNo) {
		if (phoneNo.length() >= 10) {
			if (phoneNo.charAt(0) == '0' || phoneNo.charAt(0) == '1' || phoneNo.contains("+")) {
				return false;
			} else {
				return isPhoneValid(phoneNo);
			}
		} else {
			return false;
		}
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
		} catch (Exception e) {
		}
	}

	public static boolean deleteDir(File dir) {
		if (dir != null && dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		return dir.delete();
	}



	public static boolean checkIfOnlyDigits(String strTocheck) {
		String regex = "[0-9+]+";
		if (strTocheck.matches(regex)) {
			return true;
		} else {
			return false;
		}
	}


	private static DecimalFormat decimalFormatMoney;

	public static DecimalFormat getDecimalFormatForMoney() {
		if (decimalFormatMoney == null) {
			decimalFormatMoney = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.ENGLISH));
		}
		return decimalFormatMoney;
	}

	private static DecimalFormat decimalFormat;

	public static DecimalFormat getDecimalFormat() {
		if (decimalFormat == null) {
			decimalFormat = new DecimalFormat("#.##");
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
			}else if(appInfo.packageName.contains("com.olacabs.kpdriver")){
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
			Log.i("app_List",appInfo.packageName );
			if (!appInfo.packageName.contains("com.gcs.telerickshaw")) {
				telerickshawDriver = (appInfo.packageName.contains("com.telerickshaw") || appInfo.packageName.contains("telerickshaw"));
				if (telerickshawDriver) {

					break;
				}
			}

		}
		return telerickshawDriver;

	}

	public static JSONArray fetchAllApps(Context context){
		try {
			int flags = PackageManager.GET_META_DATA ;

			PackageManager pm = context.getPackageManager();
			List<ApplicationInfo> applications = pm.getInstalledApplications(flags);
			List<PackageInfo> packages = pm.getInstalledPackages(flags);
			JSONArray appList = new JSONArray();

			if((Prefs.with(context).getInt(Constants.FETCH_APP_API_ENABLED, 1)==1)) {
                for (ApplicationInfo appInfo : applications) {
                    appList.put(appInfo.packageName);
                }
            }else if((Prefs.with(context).getInt(Constants.FETCH_APP_API_ENABLED, 1)==2)){
                for (ApplicationInfo appInfo : applications) {
                    if( pm.getLaunchIntentForPackage(appInfo.packageName) != null ){
                        appList.put(appInfo.packageName);
                    }
                }
            }else if((Prefs.with(context).getInt(Constants.FETCH_APP_API_ENABLED, 1)==3)){
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



	public static String getDeviceName(){
		return (android.os.Build.MANUFACTURER + android.os.Build.MODEL).toString();
	}

	public static int getAppVersion(Context context){
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


	public static void enableReceiver(Context context, Class classT, boolean enable){
		try {
			ComponentName receiver = new ComponentName(context, classT);
			PackageManager pm = context.getPackageManager();
			if(enable) {
				pm.setComponentEnabledSetting(receiver,
						PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
						PackageManager.DONT_KILL_APP);
			} else{
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
					status == BatteryManager.BATTERY_STATUS_FULL ;
			if(isCharging){
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

	public static String getCallDetails(Context context, String phone) {
		JSONArray callLogs = new JSONArray();
		try {
			Uri contacts = CallLog.Calls.CONTENT_URI;
			Cursor managedCursor = context.getContentResolver().query(contacts, null, null, null, null);
			int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
			int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
			int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
			int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
			while (managedCursor.moveToNext()) {

				if((managedCursor.getString(number).equalsIgnoreCase(phone))
						|| (("+91"+managedCursor.getString(number)).equalsIgnoreCase(phone))) {

					if ((Long.valueOf(managedCursor.getString(date))) > ( Long.valueOf(Prefs.with(context).getString(SPLabels.ACCEPT_RIDE_TIME,
							String.valueOf(System.currentTimeMillis() - 18000l)))))
					{
						String phNumber = managedCursor.getString(number);
						String callType = managedCursor.getString(type);
						String callDate = managedCursor.getString(date);
						String callDayTime = (Long.valueOf(callDate)).toString();
						Log.i("CallLogTime", callDate);
						String callDuration = managedCursor.getString(duration);
						String dir = null;
						int dircode = Integer.parseInt(callType);
						switch (dircode) {
							case CallLog.Calls.OUTGOING_TYPE:
								dir = "OUTGOING";
								break;

							case CallLog.Calls.INCOMING_TYPE:
								dir = "INCOMING";
								break;

							case CallLog.Calls.MISSED_TYPE:
								dir = "MISSED";
								break;
						}

						JSONObject callObj = new JSONObject();
						callObj.put("phone_number", phNumber);
						callObj.put("call_type", dir);
						callObj.put("call_date",callDayTime);
						callObj.put("call_duration",callDuration);
						callLogs.put(callObj);

					}
				}

			}
			managedCursor.close();
			Log.i("CallLogs", String.valueOf(callLogs));
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return String.valueOf(callLogs);

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

	public static void openNavigationIntent(Context context, LatLng latLng){
		try {
			Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latLng.latitude + "," + latLng.longitude);
			Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
			mapIntent.setPackage("com.google.android.apps.maps");
			context.startActivity(mapIntent);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static void setDrawableColor(View view, String color, int defaultColor){
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

	public static void setTextColor(View view, String color, int defaultColor){
		try {
			int intColor = defaultColor;
			if (color != null && (color.length() == 7 || color.length() == 9)) {
				intColor = Color.parseColor(color);
			}
			if(view instanceof TextView) {
				((TextView) view).setTextColor(intColor);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Bitmap setBitmapColor(Bitmap sourceBitmap, String color, int defaultColor){
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

	public static void deleteMFile() {
		File dir = new File(Environment.getExternalStorageDirectory() + "/JugnooData");
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

		if((System.currentTimeMillis() - Prefs.with(context).getLong(SPLabels.CLEAR_APP_CACHE_TIME, 0)) > (7L * 24L * 60L * 60L * 1000L) ) {
			Prefs.with(context).save(SPLabels.CLEAR_APP_CACHE_TIME, System.currentTimeMillis());
			File cache = context.getCacheDir();
			File appDir = new File(cache.getParent());
			if (appDir.exists()) {
				String[] children = appDir.list();
				for (String s : children) {
					if (!s.equals("lib")) {
						deleteAppData(new File(appDir, s));
						Log.i("TAG", "File /data/data/APP_PACKAGE/" + s + " DELETED");
					}
				}
			}
		}
	}

	public static boolean deleteAppData(File dir) {
		if (dir != null && dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}

		return dir.delete();
	}

	public static boolean fetchUserInstalledApps(Context context, String packageName){
		try {
			int flags = PackageManager.GET_META_DATA ;
			PackageManager pm = context.getPackageManager();
			List<PackageInfo> packages = pm.getInstalledPackages(flags);
			boolean installed = false;

				for (PackageInfo packageInfo : packages) {
					if (packageInfo.versionName != null && ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 1)) {
						if(packageName.equalsIgnoreCase(packageInfo.packageName)){
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
									  int quality, int index) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		src.compress(format, quality, os);
		File f = new File(context.getExternalCacheDir(), "temp" + index + ".jpg");
		try {
			f.createNewFile();
			byte[] bitmapdata = os.toByteArray();

			FileOutputStream fos = new FileOutputStream(f);
			fos.write(bitmapdata);
			fos.flush();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return f;
	}


	public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
		int width = bm.getWidth();
		int height = bm.getHeight();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
				matrix, false);

		return resizedBitmap;
	}

	public static String getAbsAmount(Context context, double amount){
		DecimalFormat decimalFormatNoDecimal = new DecimalFormat("#", new DecimalFormatSymbols(Locale.ENGLISH));
		String showAmount;

		try {
			if(amount >= 0){
				showAmount = context.getResources().getString(R.string.rupee)+decimalFormatNoDecimal.format(amount);
			} else {
				showAmount = "-"+context.getResources().getString(R.string.rupee)+decimalFormatNoDecimal.format(Math.abs(amount));
			}
			return showAmount;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public static String getAbsWithDecimalAmount(Context context, double amount){
		DecimalFormat decimalFormatNoDecimal = new DecimalFormat("#.##", new DecimalFormatSymbols(Locale.ENGLISH));
		String showAmount;

		try {
			if(amount >= 0){
				showAmount = context.getResources().getString(R.string.rupee)+decimalFormatNoDecimal.format(amount);
			} else {
				showAmount = "-"+context.getResources().getString(R.string.rupee)+decimalFormatNoDecimal.format(Math.abs(amount));
			}
			return showAmount;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public static int dpToPx(Context context, float dp) {
		int temp = (int)dp;
		final float scale = context.getResources().getDisplayMetrics().density;
		return Math.round(dp * scale);
	}

}
