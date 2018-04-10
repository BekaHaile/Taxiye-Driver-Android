package product.clicklabs.jugnoo.driver.utils;

import android.content.Context;

import com.nudgespot.api.GcmClient;
import com.nudgespot.api.auth.NudgespotCredentials;
import com.nudgespot.resource.NudgespotActivity;
import com.nudgespot.resource.NudgespotSubscriber;

import org.json.JSONArray;
import org.json.JSONObject;

import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.R;


/**
 * Created by shankar on 3/29/16.
 */
public class NudgeClient {

	private static GcmClient mGcmClient;

	public static GcmClient getGcmClient(Context context){
		if(mGcmClient == null){
			mGcmClient = GcmClient.getClient(new NudgespotCredentials(
					context.getResources().getString(R.string.nudgespot_javascript_api_key),
					context.getResources().getString(R.string.nudgespot_rest_api_key)), context);
		}
		return mGcmClient;
	}

	public static void initialize(Context context, String userId, String userName, String email, String phoneNo){
		try {
			NudgespotSubscriber subscriber = new NudgespotSubscriber(userId);
			JSONObject props = new JSONObject();
			props.put(Constants.KEY_USER_NAME, userName);
			props.put(Constants.KEY_EMAIL, email);
			props.put(Constants.KEY_PHONE_NO, phoneNo);
			props.put(Constants.KEY_SIGNED_UP_AT, DateOperations.getCurrentTimeInUTC());
			props.put(Constants.KEY_IS_DRIVER, 1);

			JSONArray jArray = new JSONArray();
			JSONObject jContact = new JSONObject();
			jContact.put(Constants.KEY_CONTACT_TYPE, Constants.KEY_EMAIL);
			jContact.put(Constants.KEY_CONTACT_VALUE, email);
			jContact.put(Constants.KEY_SUBSCRIPTION_STATUS, Constants.KEY_ACTIVE);
			jArray.put(jContact);
			JSONObject jContact1 = new JSONObject();
			jContact1.put(Constants.KEY_CONTACT_TYPE, Constants.KEY_PHONE);
			jContact1.put(Constants.KEY_CONTACT_VALUE, phoneNo);
			jContact1.put(Constants.KEY_SUBSCRIPTION_STATUS, Constants.KEY_ACTIVE);
			jArray.put(jContact1);
			props.put(Constants.KEY_CONTACTS, jArray);

			subscriber.setProperties(props);
//			getGcmClient(context).initialize(subscriber);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static void trackEvent(Context context, String eventName, JSONObject map){
	/*	try {
			if(map == null){
				map = new JSONObject();
			}
			map.put(Constants.KEY_USER_ID, Prefs.with(context).getString(Constants.SP_USER_ID, ""));
			NudgespotActivity activity = new NudgespotActivity("driver_"+eventName);
			activity.setProperties(map);
			getGcmClient(context).track(activity);
		} catch (Exception e) {
			e.printStackTrace();
		}*/
	}

	public static void logout(Context context){
//		getGcmClient(context).clearRegistration();
	}



}
