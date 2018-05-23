package product.clicklabs.jugnoo.driver;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.google.gson.Gson;

import product.clicklabs.jugnoo.driver.retrofit.model.TicketResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.BaseActivity;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.PermissionCommon;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import product.clicklabs.jugnoo.driver.utils.Utils;

public class DriverTicketDetails extends BaseActivity {

	LinearLayout relative;
	View backBtn;
	TextView title, textViewComplainIdText, textViewComplainId, textViewStatus, textViewCreatedOnText,
			textViewCreatedOn, textViewRideTypeText, textViewRideType, textViewUpdatedOnText, textViewUpdatedOn,
			textViewManualAdjText, textViewManualAdj, textViewComplainText, textViewComplain, textViewCall;
	ScrollView scrollView;
	LinearLayout mainLinear, linearLayoutCreatedOn, linearLayoutRideType, linearLayoutUpdatedOn, linearLayoutManualAdj, linearLayoutComplain;
	TextView textViewScroll;
	ImageView imageViewStatus;
	RelativeLayout relativeLayoutCall1;

	TicketResponse.TicketDatum extras;
	private PermissionCommon mPermissionCommon;

	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.init(this, Data.FLURRY_KEY);
		FlurryAgent.onStartSession(this, Data.FLURRY_KEY);
	}

	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_driver_ticket_details);

		try {
			Intent intent = getIntent();
			String extra = intent.getStringExtra("extras");
			extras = new Gson().fromJson(extra, TicketResponse.TicketDatum.class);
		} catch (Exception e) {
			e.printStackTrace();
		}

		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(DriverTicketDetails.this, relative, 1134, 720, false);

		scrollView = (ScrollView) findViewById(R.id.scrollView);
		mainLinear = (LinearLayout) findViewById(R.id.mainLinear);
		textViewScroll = (TextView) findViewById(R.id.textViewScroll);
		backBtn = findViewById(R.id.backBtn);
		title = (TextView) findViewById(R.id.title);
		title.setTypeface(Fonts.mavenRegular(this), Typeface.BOLD);

		imageViewStatus = (ImageView) findViewById(R.id.imageViewStatus);

		linearLayoutCreatedOn = (LinearLayout) findViewById(R.id.linearLayoutCreatedOn);
		linearLayoutRideType = (LinearLayout) findViewById(R.id.linearLayoutRideType);
		linearLayoutUpdatedOn = (LinearLayout) findViewById(R.id.linearLayoutUpdatedOn);
		linearLayoutManualAdj = (LinearLayout) findViewById(R.id.linearLayoutManualAdj);
		linearLayoutComplain = (LinearLayout) findViewById(R.id.linearLayoutComplain);
		relativeLayoutCall1 = (RelativeLayout) findViewById(R.id.relativeLayoutCall1);

		textViewComplainIdText = (TextView) findViewById(R.id.textViewComplainIdText);
		textViewComplainIdText.setTypeface(Fonts.mavenRegular(this));
		textViewComplainId = (TextView) findViewById(R.id.textViewComplainId);
		textViewComplainId.setTypeface(Fonts.mavenRegular(this));

		textViewStatus = (TextView) findViewById(R.id.textViewStatus);
		textViewStatus.setTypeface(Fonts.mavenRegular(this), Typeface.BOLD);

		textViewCreatedOnText = (TextView) findViewById(R.id.textViewCreatedOnText);
		textViewCreatedOnText.setTypeface(Fonts.mavenRegular(this));
		textViewCreatedOn = (TextView) findViewById(R.id.textViewCreatedOn);
		textViewCreatedOn.setTypeface(Fonts.mavenRegular(this));

		textViewRideTypeText = (TextView) findViewById(R.id.textViewRideTypeText);
		textViewRideTypeText.setTypeface(Fonts.mavenRegular(this));
		textViewRideType = (TextView) findViewById(R.id.textViewRideType);
		textViewRideType.setTypeface(Fonts.mavenRegular(this));

		textViewUpdatedOnText = (TextView) findViewById(R.id.textViewUpdatedOnText);
		textViewUpdatedOnText.setTypeface(Fonts.mavenRegular(this));
		textViewUpdatedOn = (TextView) findViewById(R.id.textViewUpdatedOn);
		textViewUpdatedOn.setTypeface(Fonts.mavenRegular(this));

		textViewManualAdjText = (TextView) findViewById(R.id.textViewManualAdjText);
		textViewManualAdjText.setTypeface(Fonts.mavenRegular(this));
		textViewManualAdj = (TextView) findViewById(R.id.textViewManualAdj);
		textViewManualAdj.setTypeface(Fonts.mavenRegular(this));

		textViewComplainText = (TextView) findViewById(R.id.textViewComplainText);
		textViewComplainText.setTypeface(Fonts.mavenRegular(this));
		textViewComplain = (TextView) findViewById(R.id.textViewComplain);
		textViewComplain.setTypeface(Fonts.mavenRegular(this));

		textViewCall = (TextView) findViewById(R.id.textViewCall);
		textViewCall.setTypeface(Fonts.mavenRegular(this), Typeface.BOLD);


		backBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});

		relativeLayoutCall1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mPermissionCommon == null) {
					mPermissionCommon = new PermissionCommon(DriverTicketDetails.this);
				}
				mPermissionCommon.setCallback(new PermissionCommon.PermissionListener() {
					@SuppressLint("MissingPermission")
					@Override
					public void permissionGranted(final int requestCode) {
						Utils.makeCallIntent(DriverTicketDetails.this, Data.userData.driverSupportNumber);
					}

					@Override
					public void permissionDenied(final int requestCode) {
//						Utils.openCallIntent(DriverTicketDetails.this, Data.userData.driverSupportNumber);
					}
				}).getPermission(PermissionCommon.REQUEST_CODE_CALL_PHONE, Manifest.permission.CALL_PHONE);

				overridePendingTransition(R.anim.right_in, R.anim.right_out);
			}
		});

		try {
			if(extras != null){
				title.setText(getResources().getString(R.string.complainId)+" #"+extras.getTicketId());
				textViewComplainId.setText(" #"+extras.getTicketId());
				textViewCreatedOn.setText(extras.getOpeningDate());

				if(!extras.getRideType().equalsIgnoreCase("")){
					linearLayoutRideType.setVisibility(View.VISIBLE);
					textViewRideType.setText(extras.getRideType());
				} else {
					linearLayoutRideType.setVisibility(View.GONE);
				}

				if (extras.getStatus().equalsIgnoreCase("settled")) {
					textViewStatus.setVisibility(View.VISIBLE);
					textViewStatus.setText(getResources().getString(R.string.settled));
					textViewStatus.setTextColor(getResources().getColor(R.color.green_status));
					imageViewStatus.setImageResource(R.drawable.ic_tick_green_20);

				} else if(extras.getStatus().equalsIgnoreCase("registered")){
					textViewStatus.setVisibility(View.VISIBLE);
					textViewStatus.setText(getResources().getString(R.string.registered));
					textViewStatus.setTextColor(getResources().getColor(R.color.themeColor));
					imageViewStatus.setImageResource(R.drawable.ic_tick_orange_20);

				} else if(extras.getStatus().equalsIgnoreCase("pending")){
					textViewStatus.setVisibility(View.VISIBLE);
					textViewStatus.setText(getResources().getString(R.string.pending));
					textViewStatus.setTextColor(getResources().getColor(R.color.red_ticket_status));
					imageViewStatus.setImageResource(R.drawable.in_progress_red_20);
				}

				if(extras.getManualAdjustment() != 0){
					linearLayoutManualAdj.setVisibility(View.VISIBLE);
					textViewManualAdj.setText(Utils.getAbsWithDecimalAmount(DriverTicketDetails.this, extras.getManualAdjustment(),extras.getCurrencyUnit()));
				} else {
					linearLayoutManualAdj.setVisibility(View.GONE);
				}

				if(!extras.getLastUpdated().equalsIgnoreCase("")){
					linearLayoutUpdatedOn.setVisibility(View.VISIBLE);
					textViewUpdatedOn.setText(extras.getLastUpdated());
					textViewUpdatedOnText.setText(getResources().getString(R.string.updated_on));

				} else if(!extras.getClosingDate().equalsIgnoreCase("")){
					linearLayoutUpdatedOn.setVisibility(View.VISIBLE);
					textViewUpdatedOn.setText(extras.getClosingDate());
					textViewUpdatedOnText.setText(getResources().getString(R.string.closed_on));
				} else {
					linearLayoutUpdatedOn.setVisibility(View.GONE);
				}

				if(!extras.getIssueType().equalsIgnoreCase("")){
					linearLayoutComplain.setVisibility(View.VISIBLE);
					textViewComplain.setText(extras.getIssueType());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void performBackPressed() {
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}

	@Override
	public void onBackPressed() {
		performBackPressed();
		super.onBackPressed();
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		ASSL.closeActivity(relative);
		System.gc();
	}

	@Override
	public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (mPermissionCommon != null) {
			mPermissionCommon.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}
}
