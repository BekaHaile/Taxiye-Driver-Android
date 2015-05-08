package product.clicklabs.jugnoo.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import product.clicklabs.jugnoo.R;


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

	
	
	
	
	public static void openCallIntent(Activity activity, String phoneNumber){
		Intent callIntent = new Intent(Intent.ACTION_VIEW);
        callIntent.setData(Uri.parse("tel:"+phoneNumber));
        activity.startActivity(callIntent);
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
	



    public static String hideEmailString(String email){
        String returnEmail = "";
        if(email.length() > 0 && email.contains("@")){
            int charLength = email.indexOf('@');
            int stars = (charLength - 4) > 0 ? (charLength - 4) : 4;
            stars = stars < 4 ? 4 : stars;
            int starsToShow = stars > charLength ? charLength : stars;
            StringBuilder stringBuilder = new StringBuilder();
            for(int i=0; i<starsToShow; i++){
                stringBuilder.append("*");
            }
            returnEmail = stringBuilder.toString() + email.substring(starsToShow, email.length());
        }
        else if(email.length() > 0){
            StringBuilder stringBuilder = new StringBuilder();
            for(int i=0; i<email.length(); i++){
                stringBuilder.append("*");
            }
            returnEmail = stringBuilder.toString();
        }
        return returnEmail;
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



    public static String getCountryZipCode(Context context) {

        String CountryID = "";
        String CountryZipCode = "";

        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        // getNetworkCountryIso
        CountryID = manager.getSimCountryIso().toUpperCase();
        Log.e("CountryID", "=" + CountryID);
        String[] rl = context.getResources().getStringArray(R.array.CountryCodes);
        for (int i = 0; i < rl.length; i++) {
            String[] g = rl[i].split(",");
            if (g[1].trim().equals(CountryID.trim())) {
                CountryZipCode = g[0];
                return CountryZipCode;
            }
        }
        return "";
    }




    public static void generateKeyHash(Context context){
        try { // single sign-on for fb application
            PackageInfo info = context.getPackageManager().getPackageInfo(
                context.getPackageName(),
                PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.e("KeyHash:", ","
                    + Base64.encodeToString(md.digest(),
                    Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("error:", "," + e.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("error:", "," + e.toString());
        }
    }




    public static boolean hasAlphabets(String name){
        return name.matches(".*[a-zA-Z]+.*");
    }

}
