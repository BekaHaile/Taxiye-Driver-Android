package product.clicklabs.jugnoo.driver.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.BatteryManager;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.Data;


public class Utils {
	
	/**
	 * Compares two double values with epsilon precision
	 * @param d1 double value 1
	 * @param d2 double value 2
	 * @return 1 if d1 > d2, 
	 * -1 if d1 < d2 & 
	 * 0 if d1 == d2
	 */
	public static int compareDouble(double d1, double d2){
		if(d1 == d2){
			return 0;
		}
		else{
			double epsilon = 0.0000001;
			if((d1 - d2) > epsilon){
				return 1;
			}
			else if((d1 - d2) < epsilon){
				return -1;
			}
			else{
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


    public static void showSoftKeyboard(Activity activity, View searchET){
        try {
            InputMethodManager keyboard = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            keyboard.showSoftInput(searchET, 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	
	public static ArrayList<NameValuePair> convertQueryToNameValuePairArr(String query) throws UnsupportedEncodingException {
	    ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	    String[] pairs = query.split("&");
	    for (String pair : pairs) {
	        int idx = pair.indexOf("=");
	        nameValuePairs.add(new BasicNameValuePair(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8")));
	    }
	    return nameValuePairs;
	}
	
	
	
	public static String[] splitStringInParts(String s, int partLength) {
	    int len = s.length();

	    // Number of parts
	    int nparts = (len + partLength - 1) / partLength;
	    String parts[] = new String[nparts];

	    // Break into parts
	    int offset= 0;
	    int i = 0;
	    while (i < nparts) {
	        parts[i] = s.substring(offset, Math.min(offset + partLength, len));
	        offset += partLength;
	        i++;
	    }

	    return parts;
	}
	
	
	
	public static boolean mockLocationEnabled(Context context){
//		return false;
		if(Data.DEFAULT_SERVER_URL.equalsIgnoreCase(Data.LIVE_SERVER_URL)){
			if (Settings.Secure.getString(context.getContentResolver(),
		       Settings.Secure.ALLOW_MOCK_LOCATION).equals("0"))
		       return false;
		       else return true;
		}
		else{
			return false;
		}
	}
	
	
	public static String getChronoTimeFromMillis(long elapsedTime){
		long timeR = elapsedTime;
		int hR = (int) (timeR / 3600000);
		int mR = (int) (timeR - hR * 3600000) / 60000;
		int sR = (int) (timeR - hR * 3600000 - mR * 60000) / 1000;
		String hhR = hR < 10 ? "0" + hR : hR + "";
		String mmR = mR < 10 ? "0" + mR : mR + "";
		String ssR = sR < 10 ? "0" + sR : sR + "";
		return (hhR + ":" + mmR + ":" + ssR);
	}
	
	
	public static void openCallIntent(Activity activity, String phoneNumber){
		Intent callIntent = new Intent(Intent.ACTION_VIEW);
        callIntent.setData(Uri.parse("tel:"+phoneNumber));
        activity.startActivity(callIntent);
	}


	public static String hidePhoneNoString(String phoneNo){
		String returnPhoneNo = "";
		if(phoneNo.length() > 0){
			int charLength = phoneNo.length();
			int stars = (charLength < 3) ? 0 : (charLength - 3);
			StringBuilder stringBuilder = new StringBuilder();
			for(int i=0; i<stars; i++){
				stringBuilder.append("*");
			}
			returnPhoneNo = stringBuilder.toString() + phoneNo.substring(stars, phoneNo.length());
		}
		return returnPhoneNo;
	}


    public static String retrievePhoneNumberTenChars(String phoneNo){
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
        if(phoneNo.length() >= 10){
            phoneNo = phoneNo.substring(phoneNo.length()-10, phoneNo.length());
        }
        return phoneNo;
    }

    public static boolean validPhoneNumber(String phoneNo){
        if(phoneNo.length() >= 10){
            if(phoneNo.charAt(0) == '0' || phoneNo.charAt(0) == '1' || phoneNo.contains("+")){
                return false;
            }
            else{
                return isPhoneValid(phoneNo);
            }
        }
        else{
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
        } catch (Exception e) {}
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


	public static float getBatteryPercentage(Context context){
		try {
			IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
			Intent batteryStatus = context.registerReceiver(null, ifilter);
			int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
			int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
			float batteryPct = (level / (float) scale) * 100;
			return batteryPct;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}


	public static boolean checkIfOnlyDigits(String strTocheck){
		String regex = "[0-9+]+";
		if(strTocheck.matches(regex)){
			return true;
		}
		else{
			return false;
		}
	}


	private static DecimalFormat decimalFormatMoney;
	public static DecimalFormat getDecimalFormatForMoney(){
		if(decimalFormatMoney == null){
			decimalFormatMoney = new DecimalFormat("#.##");
		}
		return decimalFormatMoney;
	}


}
