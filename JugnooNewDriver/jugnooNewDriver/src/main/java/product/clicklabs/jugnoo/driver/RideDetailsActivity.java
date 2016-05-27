package product.clicklabs.jugnoo.driver;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

import product.clicklabs.jugnoo.driver.datastructure.RideInfo;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.BaseActivity;
import product.clicklabs.jugnoo.driver.utils.DateOperations;
import product.clicklabs.jugnoo.driver.utils.Utils;

public class RideDetailsActivity extends BaseActivity {

	LinearLayout relative;

	Button backBtn;
	TextView title;

	TextView idValue, dateTimeValue, distanceValue, rideTimeValue, waitTimeValue,
			textViewRideFareValue, textViewConvayenceChargeValue, textViewLuggageChargeValue,
			textViewRateApplied, textViewRateAppliedValue,
			textViewAcceptSubsidyValue, textViewCancelSubsidyValue, textViewJugnooCutValue,
			textViewActualFare, textViewCustomerPaid, textViewAccountBalance, textViewAccountBalanceText,
			textViewFromValue, textViewToValue;

	ImageView imageViewRequestType;

	RelativeLayout relativeLayoutConvenienceCharges, relativeLayoutLuggageCharges,
			relativeLayoutCancelSubsidy, relativeLayoutJugnooCut;

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
		setContentView(R.layout.activity_ride_details);

		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(RideDetailsActivity.this, relative, 1134, 720, false);

		backBtn = (Button) findViewById(R.id.backBtn);
		title = (TextView) findViewById(R.id.title);
		title.setTypeface(Data.latoRegular(this));

		relativeLayoutConvenienceCharges = (RelativeLayout) findViewById(R.id.relativeLayoutConvenienceCharges);
		relativeLayoutLuggageCharges = (RelativeLayout) findViewById(R.id.relativeLayoutLuggageCharges);
		relativeLayoutCancelSubsidy = (RelativeLayout) findViewById(R.id.relativeLayoutCancelSubsidy);
		relativeLayoutJugnooCut = (RelativeLayout) findViewById(R.id.relativeLayoutJugnooCut);


		idValue = (TextView) findViewById(R.id.idValue); idValue.setTypeface(Data.latoRegular(this));
		dateTimeValue = (TextView) findViewById(R.id.dateTimeValue);
		dateTimeValue.setTypeface(Data.latoRegular(this));
		distanceValue = (TextView) findViewById(R.id.distanceValue);
		distanceValue.setTypeface(Data.latoRegular(this));
		rideTimeValue = (TextView) findViewById(R.id.rideTimeValue);
		rideTimeValue.setTypeface(Data.latoRegular(this));
		waitTimeValue = (TextView) findViewById(R.id.waitTimeValue);
		waitTimeValue.setTypeface(Data.latoRegular(this));

		textViewRideFareValue = (TextView) findViewById(R.id.textViewRideFareValue);
		textViewRideFareValue.setTypeface(Data.latoRegular(this));
		textViewConvayenceChargeValue = (TextView) findViewById(R.id.textViewConvayenceChargeValue);
		textViewConvayenceChargeValue.setTypeface(Data.latoRegular(this));
		textViewLuggageChargeValue = (TextView) findViewById(R.id.textViewLuggageChargeValue);
		textViewLuggageChargeValue.setTypeface(Data.latoRegular(this));
		textViewRateApplied = (TextView) findViewById(R.id.textViewRateApplied);
		textViewRateApplied.setTypeface(Data.latoRegular(this));

		textViewRateAppliedValue = (TextView) findViewById(R.id.textViewRateAppliedValue);
		textViewRateAppliedValue.setTypeface(Data.latoRegular(this));
		textViewAcceptSubsidyValue = (TextView) findViewById(R.id.textViewAcceptSubsidyValue);
		textViewAcceptSubsidyValue.setTypeface(Data.latoRegular(this));
		textViewCancelSubsidyValue = (TextView) findViewById(R.id.textViewCancelSubsidyValue);
		textViewCancelSubsidyValue.setTypeface(Data.latoRegular(this));
		textViewJugnooCutValue = (TextView) findViewById(R.id.textViewJugnooCutValue);
		textViewJugnooCutValue.setTypeface(Data.latoRegular(this));

		textViewActualFare = (TextView) findViewById(R.id.textViewActualFare);
		textViewActualFare.setTypeface(Data.latoRegular(this), Typeface.BOLD);
		textViewAccountBalance = (TextView) findViewById(R.id.textViewAccountBalance);
		textViewAccountBalance.setTypeface(Data.latoRegular(this), Typeface.BOLD);
		textViewCustomerPaid = (TextView) findViewById(R.id.textViewCustomerPaid);
		textViewCustomerPaid.setTypeface(Data.latoRegular(this), Typeface.BOLD);
		textViewFromValue = (TextView) findViewById(R.id.textViewFromValue);
		textViewFromValue.setTypeface(Data.latoRegular(this));
		textViewToValue = (TextView) findViewById(R.id.textViewToValue);
		textViewToValue.setTypeface(Data.latoRegular(this));
		textViewAccountBalanceText = (TextView) findViewById(R.id.textViewAccountBalanceText);
		textViewAccountBalanceText.setTypeface(Data.latoRegular(this));

		((TextView) findViewById(R.id.dateTimeValue)).setTypeface(Data.latoRegular(this));
		((TextView) findViewById(R.id.distanceValue)).setTypeface(Data.latoRegular(this));

		((TextView) findViewById(R.id.rideTimeValue)).setTypeface(Data.latoRegular(this));
		((TextView) findViewById(R.id.waitTimeValue)).setTypeface(Data.latoRegular(this));
		((TextView) findViewById(R.id.textViewRideFare)).setTypeface(Data.latoRegular(this));
		((TextView) findViewById(R.id.textViewRideFareRupee)).setTypeface(Data.latoRegular(this));

		((TextView) findViewById(R.id.textViewConvayenceCharge)).setTypeface(Data.latoRegular(this));
		((TextView) findViewById(R.id.textViewConvayenceChargeRupee)).setTypeface(Data.latoRegular(this));
		((TextView) findViewById(R.id.textViewLuggageCharge)).setTypeface(Data.latoRegular(this));
		((TextView) findViewById(R.id.textViewLuggageChargeRupee)).setTypeface(Data.latoRegular(this));

		((TextView) findViewById(R.id.textViewJugnooCut)).setTypeface(Data.latoRegular(this));
		((TextView) findViewById(R.id.textViewJugnooCutRupee)).setTypeface(Data.latoRegular(this));
		((TextView) findViewById(R.id.textViewCancelSubsidy)).setTypeface(Data.latoRegular(this));
		((TextView) findViewById(R.id.textViewCancelSubsidyRupee)).setTypeface(Data.latoRegular(this));

		((TextView) findViewById(R.id.textViewActualFareText)).setTypeface(Data.latoRegular(this));
		((TextView) findViewById(R.id.textViewCustomerPaidText)).setTypeface(Data.latoRegular(this));
		((TextView) findViewById(R.id.textViewAccountBalanceText)).setTypeface(Data.latoRegular(this));
		((TextView) findViewById(R.id.textViewRateAppliedRupee)).setTypeface(Data.latoRegular(this));
		((TextView) findViewById(R.id.textViewAccountBalanceText)).setTypeface(Data.latoRegular(this));


		imageViewRequestType = (ImageView) findViewById(R.id.imageViewRequestType);

		backBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});


		if (openedRideInfo != null) {
			idValue.setText(getResources().getString(R.string.ride_id)+" "+openedRideInfo.id);
			dateTimeValue.setText(DateOperations.convertDate(DateOperations.utcToLocal(openedRideInfo.dateTime)));

			distanceValue.setText(getResources().getString(R.string.distance)+": "
					+Utils.getDecimalFormatForMoney().format(Double.parseDouble(openedRideInfo.distance))
					+ " "+getResources().getString(R.string.km));

			rideTimeValue.setText(getResources().getString(R.string.total_time)+": "
					+openedRideInfo.rideTime + " "+getResources().getString(R.string.min));

			if (Utils.compareDouble(Double.parseDouble(openedRideInfo.waitTime), 0) == 0) {
				waitTimeValue.setVisibility(View.GONE);
			} else {
				waitTimeValue.setVisibility(View.VISIBLE);
				waitTimeValue.setText(getResources().getString(R.string.wait)+": "
						+ openedRideInfo.waitTime + " "+getResources().getString(R.string.min));
			}

			textViewRideFareValue.setText(Utils.getDecimalFormatForMoney().format(Double.parseDouble(openedRideInfo.driverRideFair)));

			if (Utils.compareDouble(Double.parseDouble(openedRideInfo.convenienceCharges), 0) == 0) {
				relativeLayoutConvenienceCharges.setVisibility(View.GONE);
			} else {
				relativeLayoutConvenienceCharges.setVisibility(View.VISIBLE);
				textViewConvayenceChargeValue.setText(Utils.getDecimalFormatForMoney().format(Double.parseDouble(openedRideInfo.convenienceCharges)));
			}

			if (Utils.compareDouble(Double.parseDouble(openedRideInfo.luggageCharges), 0) == 0) {
				relativeLayoutLuggageCharges.setVisibility(View.GONE);
			} else {
				relativeLayoutLuggageCharges.setVisibility(View.VISIBLE);
				textViewLuggageChargeValue.setText(Utils.getDecimalFormatForMoney().format(Double.parseDouble(openedRideInfo.luggageCharges)));
			}

			textViewRateAppliedValue.setText(Utils.getDecimalFormatForMoney().format(Double.parseDouble(openedRideInfo.fareFactorValue)));
			textViewRateApplied.setText(getResources().getString(R.string.rate_applied)+" "
					+ Utils.getDecimalFormat().format(Double.parseDouble(openedRideInfo.fareFactorApplied)) + "x");

			textViewAcceptSubsidyValue.setText(Utils.getDecimalFormatForMoney().format(Double.parseDouble(openedRideInfo.acceptSubsidy)));

			if (Utils.compareDouble(Double.parseDouble(openedRideInfo.cancelSubsidy), 0) == 0) {
				relativeLayoutCancelSubsidy.setVisibility(View.GONE);
			} else {
				relativeLayoutCancelSubsidy.setVisibility(View.VISIBLE);
				textViewCancelSubsidyValue.setText(Utils.getDecimalFormatForMoney().format(Double.parseDouble(openedRideInfo.cancelSubsidy)));
			}

			relativeLayoutJugnooCut.setVisibility(View.GONE);
//			if("0".equalsIgnoreCase(openedRideInfo.)){
//				relativeLayoutJugnooCut.setVisibility(View.GONE);
//			}
//			else{
//				relativeLayoutJugnooCut.setVisibility(View.VISIBLE);
//				textViewConvayenceChargeValue.setText(openedRideInfo.cancelSubsidy);
//			}

			textViewActualFare.setText(getResources().getString(R.string.rupee) + " " + Utils.getDecimalFormatForMoney().format(Double.parseDouble(openedRideInfo.actualFare)));
			textViewCustomerPaid.setText(getResources().getString(R.string.rupee) + " " + Utils.getDecimalFormatForMoney().format(Double.parseDouble(openedRideInfo.customerPaid)));

			if (Double.parseDouble(openedRideInfo.accountBalance) < 0) {
				textViewAccountBalance.setText((getResources().getString(R.string.rupee) + " " + Utils.getDecimalFormatForMoney().format(Math.abs(Double.parseDouble(openedRideInfo.accountBalance)))));
				textViewAccountBalanceText.setTextColor(getResources().getColor(R.color.black));
				textViewAccountBalance.setTextColor(getResources().getColor(R.color.black));
				textViewAccountBalanceText.setText(getResources().getString(R.string.money_to)+"Jugnoo");
			} else {
				textViewAccountBalance.setText(getResources().getString(R.string.rupee) + " " + Utils.getDecimalFormatForMoney().format(Double.parseDouble(openedRideInfo.accountBalance)));
				textViewAccountBalanceText.setTextColor(getResources().getColor(R.color.grey_ride_history));
				textViewAccountBalance.setTextColor(getResources().getColor(R.color.grey_ride_history));
				textViewAccountBalanceText.setText(getResources().getString(R.string.account));
			}
			textViewFromValue.setText(openedRideInfo.fromLocation);
			textViewToValue.setText(openedRideInfo.toLocation);

			imageViewRequestType.setImageResource(R.drawable.request_autos);

		} else {
			performBackPressed();
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
