package product.clicklabs.jugnoo.driver.support;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fugu.FuguConfig;
import com.fugu.FuguTicketAttributes;

import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.HomeUtil;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.utils.BaseActivity;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.PermissionCommon;
import product.clicklabs.jugnoo.driver.utils.Utils;

public class SupportOptionsActivity extends BaseActivity implements View.OnClickListener{

	private PermissionCommon mPermissionCommon;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_support_options);

		ImageView backBtn = (ImageView) findViewById(R.id.backBtn);
		backBtn.setOnClickListener(this);
		((TextView) findViewById(R.id.title)).setTypeface(Fonts.mavenRegular(this));
		((TextView) findViewById(R.id.title)).setText(R.string.support);

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

	public void openSupportOption(Activity activity, SupportOption supportOption){
		try {
			if(supportOption.getTag().equalsIgnoreCase(Constants.CHAT_SUPPORT)){
				FuguConfig.getInstance().showConversations(activity, activity.getString(R.string.chat));
			} else if(supportOption.getTag().equalsIgnoreCase(Constants.TICKET_SUPPORT)){
				FuguTicketAttributes.Builder builder = new FuguTicketAttributes.Builder();
				if(Data.userData != null){
					builder.setFaqName(Data.userData.getHippoTicketFAQ());
				}

				ArrayList<String> tags = new ArrayList<>();
				tags.add(Constants.HIPPO_TAG_DRIVER_APP);
//				builder.setTags(tags);

				FuguConfig.getInstance().showFAQSupport(builder.build());
			} else if(supportOption.getTag().equalsIgnoreCase(Constants.SHOW_CALL_US_MENU)){
				Utils.makeCallIntent(SupportOptionsActivity.this, Data.userData.driverSupportNumber);

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

	@Override
	public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (mPermissionCommon != null) {
			mPermissionCommon.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}
}
