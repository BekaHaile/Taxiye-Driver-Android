package product.clicklabs.jugnoo.driver.dodo.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.HomeActivity;
import product.clicklabs.jugnoo.driver.HomeUtil;
import product.clicklabs.jugnoo.driver.JSONParser;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.SplashNewActivity;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.CustomerInfo;
import product.clicklabs.jugnoo.driver.dodo.datastructure.DeliveryInfo;
import product.clicklabs.jugnoo.driver.dodo.datastructure.DeliveryStatus;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse;
import product.clicklabs.jugnoo.driver.sticky.GeanieView;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

import static product.clicklabs.jugnoo.driver.Constants.REQUEST_OVERLAY_PERMISSION;


@SuppressLint("ValidFragment")
public class MarkDeliveryFragment extends Fragment {

	private LinearLayout relative,
			linearLayoutGetDirections, linearLayoutCallCustomer, linearLayoutDeliveryStatus;

	private View backBtn;
	private TextView textViewOrderId, textViewAmount, textViewCustomerName,
			textViewCustomerAddress, title,
			textViewStatusValue, textViewReturnReason, textViewReturnReasonValue;
	private RelativeLayout relativeLayoutTakeCash, relativeLayoutOperations;

	private Button btnCollected, btnReturned;

	private View rootView;
	private HomeActivity activity;
	private DeliveryInfo deliveryInfo;
	private int deliveryInfoId;
	private int engagementId;

	public MarkDeliveryFragment(int engagementId, int deliveryInfoId){
		this.engagementId = engagementId;
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

		backBtn = (Button) rootView.findViewById(R.id.backBtn);
		title = (TextView) rootView.findViewById(R.id.title);
		title.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
		title.setText(activity.getResources().getString(R.string.details));

		textViewOrderId = (TextView) rootView.findViewById(R.id.textViewOrderId);
		textViewOrderId.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
		textViewCustomerName = (TextView) rootView.findViewById(R.id.textViewCustomerName);
		textViewCustomerName.setTypeface(Fonts.mavenRegular(activity));
		textViewCustomerAddress = (TextView) rootView.findViewById(R.id.textViewCustomerAddress);
		textViewCustomerAddress.setTypeface(Fonts.mavenRegular(activity));

		linearLayoutGetDirections = (LinearLayout) rootView.findViewById(R.id.linearLayoutGetDirections);
		linearLayoutCallCustomer = (LinearLayout) rootView.findViewById(R.id.linearLayoutCallCustomer);
		((TextView) rootView.findViewById(R.id.textViewGetDirections)).setTypeface(Fonts.mavenRegular(activity));
		((TextView) rootView.findViewById(R.id.textViewCustomerCall)).setTypeface(Fonts.mavenRegular(activity));

		linearLayoutDeliveryStatus = (LinearLayout) rootView.findViewById(R.id.linearLayoutDeliveryStatus);
		((TextView) rootView.findViewById(R.id.textViewStatus)).setTypeface(Fonts.mavenRegular(activity));
		textViewStatusValue  = (TextView) rootView.findViewById(R.id.textViewStatusValue);
		textViewStatusValue.setTypeface(Fonts.mavenRegular(activity));
		textViewReturnReason = (TextView) rootView.findViewById(R.id.textViewReturnReason);
		textViewReturnReason.setTypeface(Fonts.mavenRegular(activity));
		textViewReturnReasonValue = (TextView) rootView.findViewById(R.id.textViewReturnReasonValue);
		textViewReturnReasonValue.setTypeface(Fonts.mavenRegular(activity));

		relativeLayoutTakeCash = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutTakeCash);
		((TextView) rootView.findViewById(R.id.textViewTakeCash)).setTypeface(Fonts.mavenRegular(activity));
		textViewAmount = (TextView) rootView.findViewById(R.id.textViewAmount);
		textViewAmount.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);

		relativeLayoutOperations = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutOperations);
		btnCollected = (Button) rootView.findViewById(R.id.buttonCollected);
		btnCollected.setTypeface(Fonts.mavenRegular(activity));
		btnReturned = (Button) rootView.findViewById(R.id.buttonReturned);
		btnReturned.setTypeface(Fonts.mavenRegular(activity));


		btnCollected.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					DialogPopup.alertPopupTwoButtonsWithListeners(activity,
							activity.getResources().getString(R.string.order_id) + ": " + deliveryInfo.getId(),
							activity.getResources().getString(R.string.please_confirm_cash_taken)
									+ " " + Utils.formatCurrencyValue(deliveryInfo.getCurrency(),deliveryInfo.getAmount()),
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
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		btnReturned.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				activity.getTransactionUtils().openDeliveryReturnFragment(activity, activity.getRelativeLayoutContainer(),
						engagementId, deliveryInfo.getId());
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
					Utils.openNavigationIntent(activity, deliveryInfo.getLatLng());
					BaseFragmentActivity.checkOverlayPermissionOpenJeanie(activity, true, true);
				} catch(Exception e){
					e.printStackTrace();
				}
			}
		});

		backBtn.setOnClickListener(new View.OnClickListener() {
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
			CustomerInfo customerInfo = Data.getCustomerInfo(String.valueOf(engagementId));
			deliveryInfo = customerInfo.getDeliveryInfos().get(customerInfo.getDeliveryInfos()
					.indexOf(new DeliveryInfo(deliveryInfoId)));

			textViewOrderId.setText(activity.getResources().getString(R.string.delivery_id)+": "+deliveryInfo.getId());
			textViewAmount.setText(Utils.formatCurrencyValue(deliveryInfo.getCurrency(),deliveryInfo.getAmount()));
			textViewCustomerName.setText(deliveryInfo.getCustomerName());
			textViewCustomerAddress.setText(deliveryInfo.getDeliveryAddress());

			linearLayoutGetDirections.setVisibility(View.GONE);
			linearLayoutDeliveryStatus.setVisibility(View.GONE);
			relativeLayoutTakeCash.setVisibility(View.GONE);
			textViewAmount.setVisibility(View.GONE);
			relativeLayoutOperations.setVisibility(View.GONE);

			if(deliveryInfo.getStatus() == DeliveryStatus.CANCELLED.getOrdinal()){
				linearLayoutDeliveryStatus.setVisibility(View.VISIBLE);
				textViewStatusValue.setText(activity.getResources().getString(R.string.returned));
				textViewReturnReason.setText(activity.getResources().getString(R.string.return_reasons));
				textViewReturnReasonValue.setText(deliveryInfo.getCancelReason());
			}
			else if(deliveryInfo.getStatus() == DeliveryStatus.COMPLETED.getOrdinal()){
				linearLayoutDeliveryStatus.setVisibility(View.VISIBLE);
				textViewStatusValue.setText(activity.getResources().getString(R.string.delivered));
				textViewReturnReason.setText(activity.getResources().getString(R.string.cash_collected));
				textViewReturnReasonValue.setText(Utils.formatCurrencyValue(deliveryInfo.getCurrency(),deliveryInfo.getAmount()));
			}
			else{
				linearLayoutGetDirections.setVisibility(View.VISIBLE);
				relativeLayoutTakeCash.setVisibility(View.VISIBLE);
				textViewAmount.setVisibility(View.VISIBLE);
				relativeLayoutOperations.setVisibility(View.VISIBLE);
			}
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

				CustomerInfo customerInfo = Data.getCustomerInfo(String.valueOf(engagementId));

				HashMap<String, String> params = new HashMap<>();
				params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
				params.put(Constants.KEY_ENGAGEMENT_ID, String.valueOf(customerInfo.getEngagementId()));
				params.put(Constants.KEY_REFERENCE_ID, String.valueOf(customerInfo.getReferenceId()));
				params.put(Constants.KEY_DELIVERY_ID, String.valueOf(deliveryInfo.getId()));
				params.put(Constants.KEY_LATITUDE, String.valueOf(activity.getMyLocation().getLatitude()));
				params.put(Constants.KEY_LONGITUDE, String.valueOf(activity.getMyLocation().getLongitude()));

				final double distance = activity.getCurrentDeliveryDistance(customerInfo);
				final long deliveryTime = activity.getCurrentDeliveryTime(customerInfo);
				final long waitTime = activity.getCurrentDeliveryWaitTime(customerInfo);

				params.put(Constants.KEY_DISTANCE, String.valueOf(distance));
				params.put(Constants.KEY_RIDE_TIME, String.valueOf(deliveryTime/1000l));
				params.put(Constants.KEY_WAIT_TIME, String.valueOf(waitTime/1000l));
				HomeUtil.putDefaultParams(params);
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
									if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag, null)) {
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

	@Override
	public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_OVERLAY_PERMISSION && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
			if (Settings.canDrawOverlays(activity)) {
				activity.startService(new Intent(activity, GeanieView.class));
			}
		}
	}
}
