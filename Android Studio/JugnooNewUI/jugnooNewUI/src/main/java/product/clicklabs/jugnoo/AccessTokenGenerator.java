package product.clicklabs.jugnoo;

import product.clicklabs.jugnoo.utils.AuthKeySaver;
import product.clicklabs.jugnoo.utils.SHA256Convertor;
import product.clicklabs.jugnoo.utils.Utils;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Pair;

public class AccessTokenGenerator {

	
	public static final String LOGOUT = "logout";
	
	public static final String MEALS_PACKAGE = "com.cdk23.nlk";
	public static final String FATAFAT_PACKAGE = "com.cdk23.nlkf";
	
	
	private static final String[] OTHER_JUGNOO_APP_PACKAGES = new String[]{ MEALS_PACKAGE, FATAFAT_PACKAGE };
	
	@SuppressWarnings("deprecation")
	public static void saveAuthKey(Context context, String authKey) {
		AuthKeySaver.writeAuthToFile(authKey);
		SharedPreferences pref = context.getSharedPreferences("shared_auth", Context.MODE_WORLD_READABLE);
		Editor editor = pref.edit();
		editor.putString("authKey", authKey);
		editor.commit();
	}
	
	@SuppressWarnings("deprecation")
	public static void saveLogoutToken(Context context) {
		AuthKeySaver.writeAuthToFile(LOGOUT);
		SharedPreferences pref = context.getSharedPreferences("shared_auth", Context.MODE_WORLD_READABLE);
		Editor editor = pref.edit();
		editor.putString("authKey", "");
		editor.commit();
	}
	
	@SuppressWarnings("deprecation")
	public static void updateFreshInstall(Context context) {
		SharedPreferences pref = context.getSharedPreferences("shared_auth", Context.MODE_WORLD_READABLE);
		Editor editor = pref.edit();
		editor.putString("freshInstall", "no");
		editor.commit();
	}
	
	
	public static Pair<String, Integer> getAccessTokenPair(Context context) {
		
		Pair<String, Integer> pair = getAccessTokenPairPre(context);
		
		updateFreshInstall(context);
		
		return pair;
	}
	
	
	@SuppressWarnings("deprecation")
	public static Pair<String, Integer> getAccessTokenPairPre(Context context) {
		
		Pair<String, Integer> pair = new Pair<String, Integer>("", 1);
		
		SharedPreferences prefIni = context.getSharedPreferences(Data.SHARED_PREF_NAME, 0);					// use old access token
		final String accessTokenIni = prefIni.getString(Data.SP_ACCESS_TOKEN_KEY, "");
		if(!"".equalsIgnoreCase(accessTokenIni)){
			pair = new Pair<String, Integer>(accessTokenIni, 0);
//			Log.e("Accesstoken previous pair", "=" + pair.first + " " + pair.second);
			return pair;
		}
		
		String authKey = "";
		authKey = AuthKeySaver.readAuthFromFile();
//		Log.e("authKey", "="+authKey);
		
		
		if(AuthKeySaver.NOT_FOUND.equalsIgnoreCase(authKey)){
			SharedPreferences pref = context.getSharedPreferences("shared_auth", Context.MODE_WORLD_READABLE);
			authKey = pref.getString("authKey", "");
			
			if("".equalsIgnoreCase(authKey)){																		// SP returns empty
				SharedPreferences pref1 = context.getSharedPreferences(Data.SHARED_PREF_NAME, 0);					// use old access token
				final String accessToken = pref1.getString(Data.SP_ACCESS_TOKEN_KEY, "");
				
				pair = new Pair<String, Integer>(accessToken, 0);
				return pair;
			}
			else{																									// SP return 
				saveAuthKey(context, authKey);
			}
		}
		else if(LOGOUT.equalsIgnoreCase(authKey)){
			authKey = "";
			saveAuthKey(context, authKey);
		}
		else if("".equalsIgnoreCase(authKey)){																			// file returns empty
			SharedPreferences pref1 = context.getSharedPreferences(Data.SHARED_PREF_NAME, 0);					// use old access token
			final String accessToken = pref1.getString(Data.SP_ACCESS_TOKEN_KEY, "");
				
			if(!"".equalsIgnoreCase(accessToken)){
				pair = new Pair<String, Integer>(accessToken, 0);
				return pair;
			}
		}
		else{
			SharedPreferences pref = context.getSharedPreferences("shared_auth", Context.MODE_WORLD_READABLE);
			String freshInstall = pref.getString("freshInstall", "");
			
			if("".equalsIgnoreCase(freshInstall)){
				boolean otherAppsInstalled = false;
				for(String appPackage : OTHER_JUGNOO_APP_PACKAGES){
//					Log.e("appPackage="+appPackage, "=Utils.appInstalledOrNot(context, "+appPackage+")="+Utils.appInstalledOrNot(context, appPackage));
					if(Utils.appInstalledOrNot(context, appPackage)){
						otherAppsInstalled = true;
						break;
					}
				}
				
				if(!otherAppsInstalled){
					authKey = "";
					saveAuthKey(context, authKey);
				}
			}
			
		}
		
		if("".equalsIgnoreCase(authKey)){																			// no auth key
			pair = new Pair<String, Integer>("", 1);
			return pair;
		}
		try {																										// auth key present
			String authSecret = authKey + Data.CLIENT_SHARED_SECRET;
			String accessToken = SHA256Convertor.getSHA256String(authSecret);
			
			pair = new Pair<String, Integer>(accessToken, 1);
//			Log.e("pair", "before sending "+pair.first + " " + pair.second);
			return pair;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pair;
	}
	
	
}
