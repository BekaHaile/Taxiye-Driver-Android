package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.List;

import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.DeliveryRateCardResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.RateCardResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class FragmentDeliveryRateCard extends android.support.v4.app.Fragment {

	LinearLayout basefare_extras, perKm_extras, perDelivery_extras, pickupWaitTime_extras, pickupDistanceFare_extras,
			loadingFare_extras, unLoadingFare_extras;

	RelativeLayout relative, relativeLayoutRange, relativelayoutBaseFare, relativelayoutPerDelivery, relativelayoutReturnFare,
			relativeLayoutPickupWaitTime, relativeLayoutPickupDistanceFare, relativeLayoutLoadingFare, relativeLayoutUnLoadingFare;

	TextView textViewPickupChargesValues, textViewFareValue, textViewAdDeliveryFareValue, textViewReturnfareText;

	NewRateCardActivity activity;
	private View rootView;
	public FragmentDeliveryRateCard(){
		
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_delivery_rate_card, container, false);
		activity = (NewRateCardActivity) getActivity();
		relative = (RelativeLayout) rootView.findViewById(R.id.relative);

		new ASSL(activity, relative, 1134, 720, false);


		perDelivery_extras = (LinearLayout) rootView.findViewById(R.id.perDelivery_extras);
		perKm_extras = (LinearLayout) rootView.findViewById(R.id.perKm_extras);
		basefare_extras = (LinearLayout) rootView.findViewById(R.id.basefare_extras);
		pickupWaitTime_extras = (LinearLayout) rootView.findViewById(R.id.pickupWaitTime_extras);
		pickupDistanceFare_extras = (LinearLayout) rootView.findViewById(R.id.pickupDistanceFare_extras);
		loadingFare_extras = (LinearLayout) rootView.findViewById(R.id.loadingFare_extras);
		unLoadingFare_extras = (LinearLayout) rootView.findViewById(R.id.unLoadingFare_extras);
		relativeLayoutRange = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutRange);

		textViewPickupChargesValues = (TextView) rootView.findViewById(R.id.textViewPickupChargesValues);
		textViewPickupChargesValues.setTypeface(Fonts.mavenRegular(activity));
		textViewFareValue = (TextView) rootView.findViewById(R.id.textViewFareValue);
		textViewFareValue.setTypeface(Fonts.mavenRegular(activity));
		textViewAdDeliveryFareValue = (TextView) rootView.findViewById(R.id.textViewAdDeliveryFareValue);
		textViewAdDeliveryFareValue.setTypeface(Fonts.mavenRegular(activity));

		textViewReturnfareText = (TextView) rootView.findViewById(R.id.textViewReturnfareText);
		textViewReturnfareText.setTypeface(Fonts.mavenRegular(activity));


		((TextView) rootView.findViewById(R.id.textViewBeforeRide)).setTypeface(Fonts.mavenRegular(activity));
		((TextView) rootView.findViewById(R.id.textViewPickupCharges)).setTypeface(Fonts.mavenRegular(activity));
		((TextView) rootView.findViewById(R.id.textViewPerKm)).setTypeface(Fonts.mavenRegular(activity));
		((TextView) rootView.findViewById(R.id.textViewPerDelivery)).setTypeface(Fonts.mavenRegular(activity));
		((TextView) rootView.findViewById(R.id.textViewForAdditional)).setTypeface(Fonts.mavenRegular(activity));
		((TextView) rootView.findViewById(R.id.textViewReturnfareText)).setTypeface(Fonts.mavenRegular(activity));

		getRateCardDetails(activity);
		return rootView;
	}

	public void performBackPressed() {
		activity.onBackPressed();
		activity.overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}

	public void onBackPressed() {
		performBackPressed();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		ASSL.closeActivity(relative);
		System.gc();
	}

//	Retrofit


	public void updateData(DeliveryRateCardResponse rateCardResponse) {

		if (rateCardResponse != null) {
			DeliveryRateCardResponse.Data data = rateCardResponse.getData();

			if(data.getBaseFare() != null){
				setFares(data.getBaseFare(), basefare_extras);

			} else {
				relativelayoutBaseFare.setVisibility(View.GONE);
			}

			if(data.getDistanceFare() != null){

			} else {
				relativeLayoutRange.setVisibility(View.GONE);
			}

			if(data.getDeliveryFare() != null){

			} else {
				relativelayoutPerDelivery.setVisibility(View.GONE);
			}

			if(data.getReturnFare() != null){

			} else {
				relativelayoutReturnFare.setVisibility(View.GONE);
			}

			if(data.getPickupDistanceFare() != null){

			} else {
				relativeLayoutPickupDistanceFare.setVisibility(View.GONE);
			}

			if(data.getPickupWaitTimeFare() != null){

			} else {
				relativeLayoutPickupWaitTime.setVisibility(View.GONE);
			}

			if(data.getLoadingFare() != null){

			} else {
				relativeLayoutLoadingFare.setVisibility(View.GONE);
			}

			if(data.getUnloadingFare() != null){

			} else {
				relativeLayoutUnLoadingFare.setVisibility(View.GONE);
			}

		} else {
			performBackPressed();
		}

	}

	public void setFares(List<DeliveryRateCardResponse.Data.Fare> fare, LinearLayout ll){
		for(int i=0; i< fare.size(); i++){
			ll.addView(getAdditionalTimeData(fare.get(i).getTimeInterval()));
			List<DeliveryRateCardResponse.Data.Slot> slots = fare.get(i).getSlots();
			for(int j =0; j< slots.size(); j++){
				if(j==0){
					ll.addView(getAdditionalData(false, slots.get(j).getRange(), String.valueOf(slots.get(j).getValue())));
				} else {
					ll.addView(getAdditionalData(false, slots.get(j).getRange(), String.valueOf(slots.get(j).getValue())));
				}
			}
		}

	}

	private View getAdditionalData(boolean first, String left, String right) {
		LayoutInflater layoutInflater = (LayoutInflater) getActivity().getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = layoutInflater.inflate(R.layout.item_additional_fare_view, null, false);
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.main_layout);
		TextView leftText = (TextView) view.findViewById(R.id.left_text_view);
		TextView rightText = (TextView) view.findViewById(R.id.right_text_view);
		View topLine = view.findViewById(R.id.top_line);
		topLine.setVisibility(View.GONE);
		if(first) {
			topLine.setVisibility(View.VISIBLE);
			leftText.setTextColor(activity.getResources().getColor(R.color.grey_ticket_history));
			rightText.setTextColor(activity.getResources().getColor(R.color.grey_ticket_history));
			leftText.setBackgroundResource(R.color.darker_grey_v2);
			rightText.setBackgroundResource(R.color.darker_grey_v2);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.dpToPx(activity,60));
			layout.setLayoutParams(layoutParams);
		}
		leftText.setText(left);
		rightText.setText(right);
		return view;
	}

	private View getAdditionalTimeData(String left) {
		LayoutInflater layoutInflater = (LayoutInflater) getActivity().getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = layoutInflater.inflate(R.layout.item_additional_time_slot_view, null, false);
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.main_layout);
		TextView leftText = (TextView) view.findViewById(R.id.left_text_view);
		View topLine = view.findViewById(R.id.top_line);
		topLine.setVisibility(View.GONE);
		leftText.setText(left);
		return view;
	}


	private void getRateCardDetails(final Activity activity) {
		try {
			RestClient.getApiServices().deliveryRateCardDetail(Data.userData.accessToken, new Callback<DeliveryRateCardResponse>() {
				@Override
				public void success(DeliveryRateCardResponse rateCardResponse, Response response) {
					try {
						String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
						JSONObject jObj;
						jObj = new JSONObject(jsonString);
						if (!jObj.isNull("error")) {
							String errorMessage = jObj.getString("error");
							if (Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())) {
								HomeActivity.logoutUser(activity);
							}
						} else {
							updateData(rateCardResponse);
						}
					} catch (Exception exception) {
						exception.printStackTrace();
					}
				}
				@Override
				public void failure(RetrofitError error) {
					Log.i("error", String.valueOf(error));
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
