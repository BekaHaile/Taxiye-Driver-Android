package product.clicklabs.jugnoo.driver.dodo.fragments;

import android.annotation.SuppressLint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import product.clicklabs.jugnoo.driver.dodo.adapters.ReturnOptionsListAdapter;
import product.clicklabs.jugnoo.driver.dodo.datastructure.DeliveryInfo;
import product.clicklabs.jugnoo.driver.dodo.datastructure.DeliveryStatus;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

@SuppressLint("ValidFragment")
public class DeliveryReturnFragment extends Fragment {

	private LinearLayout relative;

	private Button buttonBack;
	private TextView textViewTitle;
	private RecyclerView recyclerViewReturnOptions;
	private Button buttonSubmit;
	private ReturnOptionsListAdapter returnOptionsListAdapter;

	private View rootView;
	private HomeActivity activity;
	private DeliveryInfo deliveryInfo;
	private int deliveryInfoId;

	public DeliveryReturnFragment(int deliveryInfoId){
		this.deliveryInfoId = deliveryInfoId;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_delivery_return_reasons, container, false);
		activity = (HomeActivity) getActivity();

		relative = (LinearLayout) rootView.findViewById(R.id.relative);
		try {
			relative.setLayoutParams(new ViewGroup.LayoutParams(720, 1134));
			ASSL.DoMagic(relative);
		} catch (Exception e) {
			e.printStackTrace();
		}


		buttonBack = (Button) rootView.findViewById(R.id.buttonBack);
		textViewTitle = (TextView) rootView.findViewById(R.id.textViewTitle);
		textViewTitle.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD);
		textViewTitle.setText(activity.getResources().getString(R.string.return_reasons));

		
		recyclerViewReturnOptions = (RecyclerView) rootView.findViewById(R.id.recyclerViewReturnOptions);
		recyclerViewReturnOptions.setLayoutManager(new LinearLayoutManager(activity));
		recyclerViewReturnOptions.setItemAnimator(new DefaultItemAnimator());
		recyclerViewReturnOptions.setHasFixedSize(false);
		returnOptionsListAdapter = new ReturnOptionsListAdapter(activity);
		recyclerViewReturnOptions.setAdapter(returnOptionsListAdapter);

		buttonSubmit = (Button) rootView.findViewById(R.id.buttonSubmit);
		buttonSubmit.setTypeface(Fonts.mavenRegular(activity));


		buttonBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				activity.onBackPressed();
			}
		});


		buttonSubmit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (Data.deliveryReturnOptionList != null) {
					String returnReasonString = "";
					for (int i = 0; i < Data.deliveryReturnOptionList.size(); i++) {
						if (Data.deliveryReturnOptionList.get(i).isChecked()) {
							returnReasonString = Data.deliveryReturnOptionList.get(i).getName();
							break;
						}
					}
					if ("".equalsIgnoreCase(returnReasonString)) {
						DialogPopup.alertPopup(activity, "", getResources().getString(R.string.select_reason));
					} else {
						deliveryReturnRequest(returnReasonString);
					}
				}
			}
		});

		setReturnOptions();
		try{
			deliveryInfo = Data.getCurrentCustomerInfo().getDeliveryInfos().get(Data.getCurrentCustomerInfo()
					.getDeliveryInfos().indexOf(new DeliveryInfo(deliveryInfoId)));
		} catch(Exception e){
			e.printStackTrace();
			activity.onBackPressed();
		}
		return rootView;
	}


	private void setReturnOptions() {
		try {
			if (Data.deliveryReturnOptionList != null) {
				for (int i = 0; i < Data.deliveryReturnOptionList.size(); i++) {
					Data.deliveryReturnOptionList.get(i).setChecked(false);
				}
				returnOptionsListAdapter.notifyDataSetChanged();
			} else {
				activity.onBackPressed();
			}
		} catch (Exception e) {
			e.printStackTrace();
			activity.onBackPressed();
		}
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



	public void deliveryReturnRequest(final String reason) {
		try {
			if (AppStatus.getInstance(activity).isOnline(activity)) {
				DialogPopup.showLoadingDialog(activity, getResources().getString(R.string.loading));

				HashMap<String, String> params = new HashMap<String, String>();
				params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
				params.put(Constants.KEY_ENGAGEMENT_ID, String.valueOf(Data.getCurrentCustomerInfo().getEngagementId()));
				params.put(Constants.KEY_REFERENCE_ID, String.valueOf(Data.getCurrentCustomerInfo().getReferenceId()));
				params.put(Constants.KEY_DELIVERY_ID, String.valueOf(deliveryInfo.getId()));
				params.put(Constants.KEY_CANCEL_REASON, reason);
				params.put(Constants.KEY_DISTANCE, String.valueOf(activity.getCustomerRideDataGlobal().getDistance()));
				params.put(Constants.KEY_LATITUDE, String.valueOf(activity.getMyLocation().getLatitude()));
				params.put(Constants.KEY_LONGITUDE, String.valueOf(activity.getMyLocation().getLongitude()));

				RestClient.getApiServices().cancelDelivery(params, new Callback<RegisterScreenResponse>() {
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
									deliveryInfo.setStatus(DeliveryStatus.CANCELLED.getOrdinal());
									activity.onBackPressed();
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
			} else {
				DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
