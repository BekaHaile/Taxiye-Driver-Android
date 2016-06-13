package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

import org.json.JSONObject;

import product.clicklabs.jugnoo.driver.adapters.CancelOptionsListAdapter;
import product.clicklabs.jugnoo.driver.adapters.DeliveryAddressListAdapter;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.RideInfo;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.DestinationDataResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.BaseActivity;
import product.clicklabs.jugnoo.driver.utils.DateOperations;
import product.clicklabs.jugnoo.driver.utils.NonScrollListView;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class DeliveryDetailsActivity extends BaseActivity {

	LinearLayout relative;

	Button backBtn;
	TextView title;

	TextView idValue, dateTimeValue, distanceValue, rideTimeValue, textViewReturnDistance,
			textViewRideFareValue, textViewNoOfDeliveries, textViewReturnSubsidyValue,
			textViewJugnooCutValue, textViewActualFare, textViewCustomerPaid, textViewAccountBalance,
			textViewAccountBalanceText, textViewFromValue, textViewDeliveryFareValue;

	ImageView imageViewRequestType;

	NonScrollListView listViewDeliveryAddresses;
	DeliveryAddressListAdapter deliveryAddressListAdapter;

	RelativeLayout relativeLayoutDeliveryFare, relativeLayoutReturnSubsidy, relativeLayoutJugnooCut;

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
		new ASSL(DeliveryDetailsActivity.this, relative, 1134, 720, false);

		backBtn = (Button) findViewById(R.id.backBtn);
		title = (TextView) findViewById(R.id.title);
		title.setTypeface(Data.latoRegular(this));

		relativeLayoutDeliveryFare = (RelativeLayout) findViewById(R.id.relativeLayoutDeliveryFare);
		relativeLayoutReturnSubsidy = (RelativeLayout) findViewById(R.id.relativeLayoutReturnSubsidy);
		relativeLayoutJugnooCut = (RelativeLayout) findViewById(R.id.relativeLayoutJugnooCut);


		idValue = (TextView) findViewById(R.id.idValue); idValue.setTypeface(Data.latoRegular(this));
		dateTimeValue = (TextView) findViewById(R.id.dateTimeValue);
		dateTimeValue.setTypeface(Data.latoRegular(this));
		distanceValue = (TextView) findViewById(R.id.distanceValue);
		distanceValue.setTypeface(Data.latoRegular(this));
		rideTimeValue = (TextView) findViewById(R.id.rideTimeValue);
		rideTimeValue.setTypeface(Data.latoRegular(this));
		textViewReturnDistance = (TextView) findViewById(R.id.textViewReturnDistance);
		textViewReturnDistance.setTypeface(Data.latoRegular(this));

		textViewRideFareValue = (TextView) findViewById(R.id.textViewRideFareValue);
		textViewRideFareValue.setTypeface(Data.latoRegular(this));
		textViewNoOfDeliveries = (TextView) findViewById(R.id.textViewNoOfDeliveries);
		textViewNoOfDeliveries.setTypeface(Data.latoRegular(this));
		textViewReturnSubsidyValue = (TextView) findViewById(R.id.textViewReturnSubsidyValue);
		textViewReturnSubsidyValue.setTypeface(Data.latoRegular(this));

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
		textViewDeliveryFareValue = (TextView) findViewById(R.id.textViewDeliveryFareValue);
		textViewDeliveryFareValue.setTypeface(Data.latoRegular(this));
		textViewAccountBalanceText = (TextView) findViewById(R.id.textViewAccountBalanceText);
		textViewAccountBalanceText.setTypeface(Data.latoRegular(this));

		((TextView) findViewById(R.id.dateTimeValue)).setTypeface(Data.latoRegular(this));
		((TextView) findViewById(R.id.distanceValue)).setTypeface(Data.latoRegular(this));

		((TextView) findViewById(R.id.rideTimeValue)).setTypeface(Data.latoRegular(this));
		((TextView) findViewById(R.id.waitTimeValue)).setTypeface(Data.latoRegular(this));
		((TextView) findViewById(R.id.textViewRideFare)).setTypeface(Data.latoRegular(this));
		((TextView) findViewById(R.id.textViewRideFareRupee)).setTypeface(Data.latoRegular(this));

		((TextView) findViewById(R.id.textViewDeliveryFare)).setTypeface(Data.latoRegular(this));
		((TextView) findViewById(R.id.textViewDeliveryFareRupee)).setTypeface(Data.latoRegular(this));
		((TextView) findViewById(R.id.textViewReturnSubsidy)).setTypeface(Data.latoRegular(this));
		((TextView) findViewById(R.id.textViewReturnSubsidyRupee)).setTypeface(Data.latoRegular(this));


		((TextView) findViewById(R.id.textViewActualFareText)).setTypeface(Data.latoRegular(this));
		((TextView) findViewById(R.id.textViewCustomerPaidText)).setTypeface(Data.latoRegular(this));
		((TextView) findViewById(R.id.textViewAccountBalanceText)).setTypeface(Data.latoRegular(this));
		((TextView) findViewById(R.id.textViewRateAppliedRupee)).setTypeface(Data.latoRegular(this));
		((TextView) findViewById(R.id.textViewAccountBalanceText)).setTypeface(Data.latoRegular(this));


		listViewDeliveryAddresses = (NonScrollListView) findViewById(R.id.listViewDeliveryAddresses);
		deliveryAddressListAdapter = new DeliveryAddressListAdapter(DeliveryDetailsActivity.this);
		listViewDeliveryAddresses.setAdapter(deliveryAddressListAdapter);


		imageViewRequestType = (ImageView) findViewById(R.id.imageViewRequestType);

		backBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});


		if (openedRideInfo != null) {
			idValue.setText(getResources().getString(R.string.delivery_id)+" "+openedRideInfo.id);
			dateTimeValue.setText(DateOperations.convertDate(DateOperations.utcToLocal(openedRideInfo.dateTime)));

			distanceValue.setText(getResources().getString(R.string.distance)+": "
					+Utils.getDecimalFormatForMoney().format(Double.parseDouble(openedRideInfo.distance))
					+ " "+getResources().getString(R.string.km));

			rideTimeValue.setText(getResources().getString(R.string.total_time)+": "
					+openedRideInfo.rideTime + " "+getResources().getString(R.string.min));

			if (Utils.compareDouble(Double.parseDouble(openedRideInfo.waitTime), 0) == 0) {
				textViewReturnDistance.setVisibility(View.GONE);
			} else {
				textViewReturnDistance.setVisibility(View.VISIBLE);
				textViewReturnDistance.setText(getResources().getString(R.string.return_distance)+": "
						+ openedRideInfo.waitTime + " "+getResources().getString(R.string.km));
			}

			textViewRideFareValue.setText(Utils.getDecimalFormatForMoney().format(Double.parseDouble(openedRideInfo.driverRideFair)));

			if (Utils.compareDouble(Double.parseDouble(openedRideInfo.convenienceCharges), 0) == 0) {
				relativeLayoutDeliveryFare.setVisibility(View.GONE);
			} else {
				relativeLayoutDeliveryFare.setVisibility(View.VISIBLE);
				textViewDeliveryFareValue.setText(Utils.getDecimalFormatForMoney().format(Double.parseDouble(openedRideInfo.convenienceCharges)));
			}

			if (Utils.compareDouble(Double.parseDouble(openedRideInfo.luggageCharges), 0) == 0) {
				relativeLayoutReturnSubsidy.setVisibility(View.GONE);
			} else {
				relativeLayoutReturnSubsidy.setVisibility(View.VISIBLE);
				textViewReturnSubsidyValue.setText(Utils.getDecimalFormatForMoney().format(Double.parseDouble(openedRideInfo.luggageCharges)));
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
				textViewAccountBalanceText.setText(getResources().getString(R.string.money_to));
			} else {
				textViewAccountBalance.setText(getResources().getString(R.string.rupee) + " " + Utils.getDecimalFormatForMoney().format(Double.parseDouble(openedRideInfo.accountBalance)));
				textViewAccountBalanceText.setTextColor(getResources().getColor(R.color.grey_ride_history));
				textViewAccountBalance.setTextColor(getResources().getColor(R.color.grey_ride_history));
				textViewAccountBalanceText.setText(getResources().getString(R.string.account));
			}
			textViewFromValue.setText(openedRideInfo.fromLocation);

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


	public void fetchDeliveryData(final Activity activity) {
		try {
			RestClient.getApiServices().getDestinationData(Data.userData.accessToken, new Callback<DestinationDataResponse>() {
				@Override
				public void success(DestinationDataResponse destinationDataResponse, Response response) {
					try {
						String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
						JSONObject jObj;
						jObj = new JSONObject(jsonString);
						String message = JSONParser.getServerMessage(jObj);
						int flag = jObj.optInt("flag", ApiResponseFlags.ACTION_COMPLETE.getOrdinal());
						if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag)) {
							if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
							}
						}
					} catch (Exception exception) {
						exception.printStackTrace();
					}
				}

				@Override
				public void failure(RetrofitError error) {
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
