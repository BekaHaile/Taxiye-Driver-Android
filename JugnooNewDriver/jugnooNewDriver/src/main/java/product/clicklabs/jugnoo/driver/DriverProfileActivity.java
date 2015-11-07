package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

import product.clicklabs.jugnoo.driver.datastructure.RideInfo;
import product.clicklabs.jugnoo.driver.utils.DateOperations;
import product.clicklabs.jugnoo.driver.utils.Utils;
import rmn.androidscreenlibrary.ASSL;

public class DriverProfileActivity extends Activity {

	LinearLayout relative;

	Button backBtn;
	TextView title;

	TextView textViewDriverName, textViewDriverId, textViewRank, textViewMonthlyValue, textViewRidesTakenValue,
			textViewRidesCancelledValue, textViewRidesMissedValue, textViewOnlineHoursValue, textViewTitleBarDEI;

	ImageView profileImg, imageViewTitleBarDEI;


	public static RideInfo openedRideInfo;

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
		setContentView(R.layout.activity_profile_screen);

		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(DriverProfileActivity.this, relative, 1134, 720, false);

		backBtn = (Button) findViewById(R.id.backBtn);
		title = (TextView) findViewById(R.id.title);
		title.setTypeface(Data.latoRegular(this));

		textViewDriverName = (TextView) findViewById(R.id.textViewDriverName);
		textViewDriverName.setTypeface(Data.latoRegular(this));
		textViewDriverId = (TextView) findViewById(R.id.textViewDriverId);
		textViewDriverId.setTypeface(Data.latoRegular(this));
		textViewRank = (TextView) findViewById(R.id.textViewRank);
		textViewRank.setTypeface(Data.latoRegular(this));
		textViewMonthlyValue = (TextView) findViewById(R.id.textViewMonthlyValue);
		textViewMonthlyValue.setTypeface(Data.latoRegular(this));

		textViewRidesTakenValue = (TextView) findViewById(R.id.textViewRidesTakenValue);
		textViewRidesTakenValue.setTypeface(Data.latoRegular(this));
		textViewRidesCancelledValue = (TextView) findViewById(R.id.textViewRidesCancelledValue);
		textViewRidesCancelledValue.setTypeface(Data.latoRegular(this));
		textViewRidesMissedValue = (TextView) findViewById(R.id.textViewRidesMissedValue);
		textViewRidesMissedValue.setTypeface(Data.latoRegular(this));
		textViewOnlineHoursValue = (TextView) findViewById(R.id.textViewOnlineHoursValue);
		textViewOnlineHoursValue.setTypeface(Data.latoRegular(this));
		textViewTitleBarDEI = (TextView) findViewById(R.id.textViewTitleBarDEI);
		textViewTitleBarDEI.setTypeface(Data.latoRegular(this));


		profileImg = (ImageView) findViewById(R.id.profileImg);
		imageViewTitleBarDEI = (ImageView) findViewById(R.id.imageViewTitleBarDEI);

		backBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});


		if (openedRideInfo != null) {
			textViewDriverName.setText(getResources().getString(R.string.name) + " " + openedRideInfo.id);
			textViewDriverId.setText(getResources().getString(R.string.driver_id) + " " + DateOperations.convertDate(DateOperations.utcToLocal(openedRideInfo.dateTime)));
			textViewRank.setText(getResources().getString(R.string.rank) + " " + Utils.getDecimalFormatForMoney().format(Double.parseDouble(openedRideInfo.distance)) + " km");
			textViewMonthlyValue.setText("Total Time: " + openedRideInfo.rideTime + " min");

			textViewRidesTakenValue.setText(Utils.getDecimalFormatForMoney().format(Double.parseDouble(openedRideInfo.driverRideFair)));
			textViewRidesCancelledValue.setText(Utils.getDecimalFormatForMoney().format(Double.parseDouble(openedRideInfo.fareFactorValue)));
			textViewRidesMissedValue.setText("Rate Applied " + Utils.getDecimalFormat().format(Double.parseDouble(openedRideInfo.fareFactorApplied)) + "x");
			textViewOnlineHoursValue.setText(Utils.getDecimalFormatForMoney().format(Double.parseDouble(openedRideInfo.acceptSubsidy)));
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


}
