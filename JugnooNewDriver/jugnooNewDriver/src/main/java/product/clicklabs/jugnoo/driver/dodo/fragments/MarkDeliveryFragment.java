package product.clicklabs.jugnoo.driver.dodo.fragments;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.HomeActivity;
import product.clicklabs.jugnoo.driver.JSONParser;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.SplashNewActivity;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.dodo.datastructure.DeliveryInfo;
import product.clicklabs.jugnoo.driver.dodo.datastructure.DeliveryStatus;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


@SuppressLint("ValidFragment")
public class MarkDeliveryFragment extends Fragment {

	private LinearLayout relative, linearLayoutGetDirections, linearLayoutCallCustomer, LinearLayoutReDelivery;

	private Button buttonBack;
	private TextView textViewOrderId, textViewAmount, textViewCustomerName,
			textViewCustomerAddress, textViewTitle, textViewStatusValue, textViewReturnReasonValue;
	private Button btnCollected, btnReturned;

	private View rootView;
	private HomeActivity activity;
	private DeliveryInfo deliveryInfo;
	private int deliveryInfoId;

	public MarkDeliveryFragment(int deliveryInfoId){
		this.deliveryInfoId = deliveryInfoId;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_mark_delivery, container, false);

		activity = (HomeActivity) getActivity();

		relative = (LinearLayout) rootView.findViewById(R.id.relative);
		try {
			relative.setLayoutParams(new ViewGroup.LayoutParams(720, 1134));
			ASSL.DoMagic(relative);
		} catch (Exception e) {
			e.printStackTrace();
		}

		linearLayoutGetDirections = (LinearLayout) rootView.findViewById(R.id.linearLayoutGetDirections);
		linearLayoutCallCustomer = (LinearLayout) rootView.findViewById(R.id.linearLayoutCallCustomer);
		LinearLayoutReDelivery = (LinearLayout) rootView.findViewById(R.id.LinearLayoutReDelivery);

		buttonBack = (Button) rootView.findViewById(R.id.buttonBack);

		textViewTitle = (TextView) rootView.findViewById(R.id.textViewTitle);
		textViewTitle.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
		textViewTitle.setText(activity.getResources().getString(R.string.mark_delivered));

		textViewCustomerName = (TextView) rootView.findViewById(R.id.textViewCustomerName);
		textViewCustomerName.setTypeface(Fonts.mavenRegular(activity));
		((TextView) rootView.findViewById(R.id.textViewTakeCash)).setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
		textViewCustomerAddress = (TextView) rootView.findViewById(R.id.textViewCustomerAddress);
		textViewCustomerAddress.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
		((TextView) rootView.findViewById(R.id.textViewStatus)).setTypeface(Fonts.mavenRegular(activity));
		((TextView) rootView.findViewById(R.id.textViewReturnReason)).setTypeface(Fonts.mavenRegular(activity));
		textViewStatusValue  = (TextView) rootView.findViewById(R.id.textViewStatusValue);
		textViewStatusValue.setTypeface(Fonts.mavenRegular(activity));
		textViewReturnReasonValue = (TextView) rootView.findViewById(R.id.textViewReturnReasonValue);
		textViewReturnReasonValue.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);

		((TextView) rootView.findViewById(R.id.textViewGetDirections)).setTypeface(Fonts.mavenRegular(activity));
		((TextView) rootView.findViewById(R.id.textViewCustomerCall)).setTypeface(Fonts.mavenRegular(activity));

		textViewOrderId = (TextView) rootView.findViewById(R.id.textViewOrderId);
		textViewOrderId.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
		((TextView) rootView.findViewById(R.id.textViewTakeCash)).setTypeface(Fonts.mavenRegular(activity));
		textViewAmount = (TextView) rootView.findViewById(R.id.textViewAmount);
		textViewAmount.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);

		btnCollected = (Button) rootView.findViewById(R.id.buttonCollected);
		btnCollected.setTypeface(Fonts.mavenRegular(activity));
		btnReturned = (Button) rootView.findViewById(R.id.buttonReturned);
		btnReturned.setTypeface(Fonts.mavenRegular(activity));


		btnCollected.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogPopup.alertPopupTwoButtonsWithListeners(activity,
						activity.getResources().getString(R.string.order_id) + ": " + deliveryInfo.getId(),
						activity.getResources().getString(R.string.please_confirm_cash_taken)
								+ " " + activity.getResources().getString(R.string.rupee)
								+ Utils.getDecimalFormatForMoney().format(deliveryInfo.getAmount()),
						activity.getResources().getString(R.string.confirm),
						activity.getResources().getString(R.string.cancel),
						new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								markDelivered();
							}
						},
						new View.OnClickListener() {
							@Override
							public void onClick(View v) {

							}
						}, false, true);
			}
		});

		btnReturned.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.getTransactionUtils().openDeliveryReturnFragment(activity, activity.getRelativeLayoutContainer(), deliveryInfo.getId());
			}
		});


		linearLayoutCallCustomer.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					Utils.openCallIntent(activity, deliveryInfo.getCustomerNo());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});


		linearLayoutGetDirections.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try{
					Utils.openNavigationIntent(activity, deliveryInfo.getDeliveryAddress());
				} catch(Exception e){
					e.printStackTrace();
				}
			}
		});

		buttonBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.onBackPressed();
			}
		});

		relative.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

			}
		});


		try{
			deliveryInfo = Data.getCurrentCustomerInfo().getDeliveryInfos().get(Data.getCurrentCustomerInfo()
					.getDeliveryInfos().indexOf(new DeliveryInfo(deliveryInfoId)));

			textViewOrderId.setText(activity.getResources().getString(R.string.order_id)+": "+deliveryInfo.getId());
			textViewAmount.setText(activity.getResources().getString(R.string.rupee)
					+Utils.getDecimalFormatForMoney().format(deliveryInfo.getAmount()));
			textViewCustomerName.setText(deliveryInfo.getCustomerName());
			textViewCustomerAddress.setText(deliveryInfo.getDeliveryAddress());
		} catch(Exception e){
			e.printStackTrace();
			activity.onBackPressed();
		}

		return rootView;
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			ASSL.closeActivity(relative);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.gc();
	}

	public void markDelivered() {
		try {
			if (AppStatus.getInstance(activity).isOnline(activity)) {
				DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));

				HashMap<String, String> params = new HashMap<>();
				params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
				params.put(Constants.KEY_ENGAGEMENT_ID, String.valueOf(Data.getCurrentCustomerInfo().getEngagementId()));
				params.put(Constants.KEY_REFERENCE_ID, String.valueOf(Data.getCurrentCustomerInfo().getReferenceId()));
				params.put(Constants.KEY_DELIVERY_ID, String.valueOf(deliveryInfo.getId()));
				params.put(Constants.KEY_LATITUDE, String.valueOf(activity.getMyLocation().getLatitude()));
				params.put(Constants.KEY_LONGITUDE, String.valueOf(activity.getMyLocation().getLongitude()));

				final double distance = activity.getCurrentDeliveryDistance();
				final long deliveryTime = activity.getCurrentDeliveryTime();
				final long waitTime = activity.getCurrentDeliveryWaitTime();

				params.put(Constants.KEY_DISTANCE, String.valueOf(distance));
				params.put(Constants.KEY_RIDE_TIME, String.valueOf(deliveryTime/1000l));
				params.put(Constants.KEY_WAIT_TIME, String.valueOf(waitTime/1000l));

				RestClient.getApiServices().markDelivered(params,
						new Callback<RegisterScreenResponse>() {
							@Override
							public void success(RegisterScreenResponse registerScreenResponse, Response response) {
								DialogPopup.dismissLoadingDialog();
								try {
									String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
									JSONObject jObj = new JSONObject(jsonString);
									int flag = jObj.optInt(Constants.KEY_FLAG, ApiResponseFlags.ACTION_COMPLETE.getOrdinal());
									String message = JSONParser.getServerMessage(jObj);
									if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag)) {
										if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
											deliveryInfo.setStatus(DeliveryStatus.COMPLETED.getOrdinal());
											deliveryInfo.setDeliveryValues(distance, deliveryTime, waitTime);
											DialogPopup.alertPopupWithListener(activity, "", message,
													new View.OnClickListener() {
														@Override
														public void onClick(View v) {
															activity.onBackPressed();
														}
													});
										} else {
											DialogPopup.alertPopup(activity, "", message);
										}
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
