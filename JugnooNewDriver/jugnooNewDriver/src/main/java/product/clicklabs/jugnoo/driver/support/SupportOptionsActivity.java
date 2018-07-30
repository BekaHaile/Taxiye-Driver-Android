package product.clicklabs.jugnoo.driver.support;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.fugu.HippoConfig;
import com.fugu.HippoTicketAttributes;

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
				HippoConfig.getInstance().showConversations(activity, activity.getString(R.string.chat));
			} else if(supportOption.getTag().equalsIgnoreCase(Constants.TICKET_SUPPORT)){
				HippoTicketAttributes.Builder builder = new HippoTicketAttributes.Builder();
				if(Data.userData != null){
					builder.setFaqName(Data.userData.getHippoTicketFAQ());
				}

				ArrayList<String> tags = new ArrayList<>();
				tags.add(Constants.HIPPO_TAG_DRIVER_APP);
				builder.setTags(tags);

				HippoConfig.getInstance().showFAQSupport(builder.build());
			} else if(supportOption.getTag().equalsIgnoreCase(Constants.SHOW_CALL_US_MENU)){

				if (mPermissionCommon == null) {
					mPermissionCommon = new PermissionCommon(SupportOptionsActivity.this);
				}
					mPermissionCommon.setCallback(new PermissionCommon.PermissionListener() {
						@SuppressLint("MissingPermission")
						@Override
						public void permissionGranted(final int requestCode) {
							Utils.makeCallIntent(SupportOptionsActivity.this, Data.userData.driverSupportNumber);
						}

						@Override
						public boolean permissionDenied(final int requestCode, boolean neverAsk) {
							return true;
						}

						@Override
						public void onRationalRequestIntercepted() {

						}
					}).getPermission(PermissionCommon.REQUEST_CODE_CALL_PHONE, Manifest.permission.CALL_PHONE);


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
