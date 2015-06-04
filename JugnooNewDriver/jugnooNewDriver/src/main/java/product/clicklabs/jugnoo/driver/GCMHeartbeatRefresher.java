package product.clicklabs.jugnoo.driver;

import android.content.Context;
import android.content.Intent;

public class GCMHeartbeatRefresher {

	public static void refreshGCMHeartbeat(Context context){
		try {
			int gcmIntent = Database2.getInstance(context).getDriverGcmIntent();
			if(gcmIntent == 1){
				context.sendBroadcast(new Intent("com.google.android.intent.action.GTALK_HEARTBEAT"));
				context.sendBroadcast(new Intent("com.google.android.intent.action.MCS_HEARTBEAT"));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
