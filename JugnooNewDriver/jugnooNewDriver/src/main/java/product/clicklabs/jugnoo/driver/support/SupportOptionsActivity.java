package product.clicklabs.jugnoo.driver.support;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.fugu.HippoConfig;
import com.fugu.HippoTicketAttributes;

import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.HomeUtil;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.utils.BaseActivity;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Utils;

public class SupportOptionsActivity extends BaseActivity implements View.OnClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_support_options);

		Button backBtn = (Button) findViewById(R.id.backBtn);
		backBtn.setOnClickListener(this);
		((TextView) findViewById(R.id.title)).setTypeface(Fonts.mavenRegular(this));

		RecyclerView rvOptions = (RecyclerView) findViewById(R.id.rvOptions);
		rvOptions.setLayoutManager(new LinearLayoutManager(this));
		rvOptions.setHasFixedSize(false);

		if(Data.getSupportOptions() != null) {
			SupportOptionsAdapter adapter = new SupportOptionsAdapter(rvOptions, Data.getSupportOptions(),
					new SupportOptionsAdapter.Callback() {
				@Override
				public void onSupportClick(int position, SupportOption supportOption) {
					openSupportOption(SupportOptionsActivity.this, supportOption);
				}
			});
			rvOptions.setAdapter(adapter);
		}

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.backBtn:
				onBackPressed();
				break;
		}
	}

	// Constants.CHAT_SUPPORT,
	// Constants.TICKET_SUPPORT,
	// Constants.SHOW_CALL_US_MENU,
	// Constants.SHOW_IN_APP_CALL_US,
	// Constants.MAIL_SUPPORT

	public static void openSupportOption(Activity activity, SupportOption supportOption){
		try {
			if(supportOption.getTag().equalsIgnoreCase(Constants.CHAT_SUPPORT)){
				HippoConfig.getInstance().showConversations(activity, activity.getString(R.string.chat));
			} else if(supportOption.getTag().equalsIgnoreCase(Constants.TICKET_SUPPORT)){
				HippoTicketAttributes.Builder builder = new HippoTicketAttributes.Builder();
				if(Data.userData != null){
					builder.setFaqName(Data.userData.getHippoTicketFAQ());
				}
				HippoConfig.getInstance().showTicketSupport(builder.build());
			} else if(supportOption.getTag().equalsIgnoreCase(Constants.SHOW_CALL_US_MENU)){
				Utils.makeCallIntent(activity, Data.userData.driverSupportNumber);
			} else if(supportOption.getTag().equalsIgnoreCase(Constants.SHOW_IN_APP_CALL_US)){
				HomeUtil.scheduleCallDriver(activity);
			} else if(supportOption.getTag().equalsIgnoreCase(Constants.MAIL_SUPPORT)){
				activity.startActivity(new Intent(activity, SupportMailActivity.class));
			} else {
				Utils.showToast(activity, activity.getString(R.string.action_not_supported));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
