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

import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.adapters.CancelOptionsListAdapter;
import product.clicklabs.jugnoo.driver.adapters.DeliveryAddressListAdapter;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.RideInfo;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.DeliveryDetailResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.DestinationDataResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.EarningsDetailResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.BaseActivity;
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity;
import product.clicklabs.jugnoo.driver.utils.DateOperations;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.Log;
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

	String ride_id;

	NonScrollListView listViewDeliveryAddresses;
	DeliveryAddressListAdapter deliveryAddressListAdapter;
	DeliveryDetailResponse deliveryDetailResponse;
	RelativeLayout relativeLayoutDeliveryFare, relativeLayoutReturnSubsidy, relativeLayoutJugnooCut;

	ArrayList<DeliveryDetailResponse.Details.To> deliveryAddressList = new ArrayList<>();


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
		setContentView(R.layout.activity_delivery_details);

		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(DeliveryDetailsActivity.this, relative, 1134, 720, false);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			ride_id = extras.getString("delivery_id");
		}

		backBtn = (Button) findViewById(R.id.backBtn);
		title = (TextView) findViewById(R.id.title);
		title.setTypeface(Data.latoRegular(this));

		relativeLayoutDeliveryFare = (RelativeLayout) findViewById(R.id.relativeLayoutDeliveryFare);
		relativeLayoutReturnSubsidy = (RelativeLayout) findViewById(R.id.relativeLayoutReturnSubsidy);
		relativeLayoutJugnooCut = (RelativeLayout) findViewById(R.id.relativeLayoutJugnooCut);


		idValue = (TextView) findViewById(R.id.idValue);
		idValue.setTypeface(Data.latoRegular(this));
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
		((TextView) findViewById(R.id.textViewRideFare)).setTypeface(Data.latoRegular(this));
		((TextView) findViewById(R.id.textViewRideFareRupee)).setTypeface(Data.latoRegular(this));

		((TextView) findViewById(R.id.textViewDeliveryFare)).setTypeface(Data.latoRegular(this));
		((TextView) findViewById(R.id.textViewDeliveryFareRupee)).setTypeface(Data.latoRegular(this));
		((TextView) findViewById(R.id.textViewReturnSubsidy)).setTypeface(Data.latoRegular(this));
		((TextView) findViewById(R.id.textViewReturnSubsidyRupee)).setTypeface(Data.latoRegular(this));


		((TextView) findViewById(R.id.textViewActualFareText)).setTypeface(Data.latoRegular(this));
		((TextView) findViewById(R.id.textViewCustomerPaidText)).setTypeface(Data.latoRegular(this));
		((TextView) findViewById(R.id.textViewAccountBalanceText)).setTypeface(Data.latoRegular(this));
		((TextView) findViewById(R.id.textViewAccountBalanceText)).setTypeface(Data.latoRegular(this));


		listViewDeliveryAddresses = (NonScrollListView) findViewById(R.id.listViewDeliveryAddresses);
		deliveryAddressListAdapter = new DeliveryAddressListAdapter(DeliveryDetailsActivity.this, deliveryAddressList);
		listViewDeliveryAddresses.setAdapter(deliveryAddressListAdapter);


		imageViewRequestType = (ImageView) findViewById(R.id.imageViewRequestType);

		backBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});

		getDeliveryDetails();

//		update();


	}

	public void update() {
		try {
			if (deliveryDetailResponse != null) {
				idValue.setText(getResources().getString(R.string.ride_id) + ": "
						+ deliveryDetailResponse.getDetails().getRideId());
				dateTimeValue.setText(DateOperations.convertDate(DateOperations.
						utcToLocal(deliveryDetailResponse.getDetails().getTime())));

				distanceValue.setText(getResources().getString(R.string.distance) + ": "
						+ Utils.getDecimalFormatForMoney().
						format(Double.parseDouble(String.valueOf(deliveryDetailResponse.getDetails().getRideDistance())))
						+ " " + getResources().getString(R.string.km));

				rideTimeValue.setText(getResources().getString(R.string.total_time) + ": "
						+ deliveryDetailResponse.getDetails().getTotalTime() + " " + getResources().getString(R.string.min));

				if (Utils.compareDouble(Double.parseDouble(String.valueOf(deliveryDetailResponse.getDetails().
						getReturnDistance())), 0) == 0) {
					textViewReturnDistance.setVisibility(View.GONE);
				} else {
					textViewReturnDistance.setVisibility(View.VISIBLE);
					textViewReturnDistance.setText(getResources().getString(R.string.return_distance) + ": "
							+ deliveryDetailResponse.getDetails().getReturnDistance() + " " + getResources().getString(R.string.km));
				}

				textViewRideFareValue.setText(Utils.getDecimalFormatForMoney().
						format(Double.parseDouble(String.valueOf(deliveryDetailResponse.getDetails().getRideFare()))));

				textViewNoOfDeliveries.setText(getResources().getString(R.string.delivery_numbers) + " "
						+ deliveryDetailResponse.getDetails().getNoOfDeliveries());

				if (Utils.compareDouble(Double.parseDouble(String.valueOf(deliveryDetailResponse.getDetails().
						getDeliveryFare())), 0) == 0) {
					relativeLayoutDeliveryFare.setVisibility(View.GONE);
				} else {
					relativeLayoutDeliveryFare.setVisibility(View.VISIBLE);
					textViewDeliveryFareValue.setText(Utils.getDecimalFormatForMoney().
							format(Double.parseDouble(String.valueOf(deliveryDetailResponse.getDetails().getDeliveryFare()))));
				}

				if (Utils.compareDouble(Double.parseDouble(String.valueOf(deliveryDetailResponse.getDetails().getReturnSubsidy())), 0) == 0) {
					relativeLayoutReturnSubsidy.setVisibility(View.GONE);
				} else {
					relativeLayoutReturnSubsidy.setVisibility(View.VISIBLE);
					textViewReturnSubsidyValue.setText(Utils.getDecimalFormatForMoney().
							format(Double.parseDouble(String.valueOf(deliveryDetailResponse.getDetails().getReturnSubsidy()))));
				}

				relativeLayoutJugnooCut.setVisibility(View.GONE);
				if ("0".equalsIgnoreCase(String.valueOf(deliveryDetailResponse.getDetails().getJugnooCut()))) {
					relativeLayoutJugnooCut.setVisibility(View.GONE);
				} else {
					relativeLayoutJugnooCut.setVisibility(View.VISIBLE);
					textViewJugnooCutValue.setText(deliveryDetailResponse.getDetails().getJugnooCut());
				}

				textViewActualFare.setText(getResources().getString(R.string.rupee) + " " +
						Utils.getDecimalFormatForMoney().format(Double.parseDouble(String.valueOf(deliveryDetailResponse.getDetails().getTotalFare()))));
				textViewCustomerPaid.setText(getResources().getString(R.string.rupee) + " "
						+ Utils.getDecimalFormatForMoney().format(Double.parseDouble(String.valueOf(deliveryDetailResponse.getDetails().getPaidInCash()))));

				if (Double.parseDouble(String.valueOf(deliveryDetailResponse.getDetails().getAccountBalance())) < 0) {
					textViewAccountBalance.setText((getResources().getString(R.string.rupee) + " "
							+ Utils.getDecimalFormatForMoney().
							format(Math.abs(Double.parseDouble(String.valueOf(deliveryDetailResponse.getDetails().getAccountBalance()))))));
					textViewAccountBalanceText.setTextColor(getResources().getColor(R.color.black));
					textViewAccountBalance.setTextColor(getResources().getColor(R.color.black));
					textViewAccountBalanceText.setText(getResources().getString(R.string.money_to));
				} else {
					textViewAccountBalance.setText(getResources().getString(R.string.rupee) + " " +
							Utils.getDecimalFormatForMoney().
									format(Double.parseDouble(String.valueOf(deliveryDetailResponse.getDetails().getAccountBalance()))));
					textViewAccountBalanceText.setTextColor(getResources().getColor(R.color.grey_ride_history));
					textViewAccountBalance.setTextColor(getResources().getColor(R.color.grey_ride_history));
					textViewAccountBalanceText.setText(getResources().getString(R.string.account));
				}
				textViewFromValue.setText(deliveryDetailResponse.getDetails().getFrom());
				imageViewRequestType.setImageResource(R.drawable.request_autos);

				deliveryAddressList.addAll(deliveryDetailResponse.getDetails().getTo());
				setDeliveryAddressList();


			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void setDeliveryAddressList() {
		try {
			if (deliveryAddressList != null) {
				deliveryAddressListAdapter.notifyDataSetChanged();
			} else {
				performBackPressed();
			}
		} catch (Exception e) {
			e.printStackTrace();
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


	public void getDeliveryDetails() {
		try {
			if (!(DeliveryDetailsActivity.this.checkIfUserDataNull() && AppStatus.getInstance(DeliveryDetailsActivity.this).isOnline(DeliveryDetailsActivity.this))) {
				DialogPopup.showLoadingDialog(DeliveryDetailsActivity.this, DeliveryDetailsActivity.this.getResources().getString(R.string.loading));

				RestClient.getApiServices().deliveryDetails(Data.userData.accessToken, Data.LOGIN_TYPE, ride_id,
						new Callback<DeliveryDetailResponse>() {
							@Override
							public void success(DeliveryDetailResponse deliveryDetailResponse, Response response) {
								DialogPopup.dismissLoadingDialog();
								try {
									String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
									JSONObject jObj;
									jObj = new JSONObject(jsonString);
									int flag = jObj.optInt("flag", ApiResponseFlags.ACTION_COMPLETE.getOrdinal());
									String message = JSONParser.getServerMessage(jObj);
									if (!SplashNewActivity.checkIfTrivialAPIErrors(DeliveryDetailsActivity.this, jObj, flag)) {
										if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
											DeliveryDetailsActivity.this.deliveryDetailResponse = deliveryDetailResponse;
											update();
										} else {
											DialogPopup.alertPopup(DeliveryDetailsActivity.this, "", message);
											performBackPressed();
										}
									} else {
										DialogPopup.alertPopupWithListener(DeliveryDetailsActivity.this, "", message, new View.OnClickListener() {
											@Override
											public void onClick(View v) {
												performBackPressed();
											}
										});
									}
								} catch (Exception exception) {
									exception.printStackTrace();
								}
							}

							@Override
							public void failure(RetrofitError error) {
								DialogPopup.dismissLoadingDialog();
							}
						});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
