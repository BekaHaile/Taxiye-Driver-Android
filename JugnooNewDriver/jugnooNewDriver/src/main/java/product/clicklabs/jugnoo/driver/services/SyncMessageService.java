package product.clicklabs.jugnoo.driver.services;

import android.app.IntentService;
import android.content.Intent;
import android.util.Pair;

import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.FetchAndSendMessages;

public class SyncMessageService extends IntentService {

	public SyncMessageService(){
		this("SyncIntentService");
	}

	public SyncMessageService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		try{
			String accessToken = intent.getStringExtra(Constants.KEY_ACCESS_TOKEN);
			if (!"".equalsIgnoreCase(accessToken)) {
				new FetchAndSendMessages(this, accessToken).syncCall();
			}
		} catch (Exception e){
			e.printStackTrace();
		}
	}
}
