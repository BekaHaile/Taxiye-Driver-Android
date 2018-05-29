package product.clicklabs.jugnoo.driver;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.DeliveryRateCardResponse;
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
			loadingFare_extras, unLoadingFare_extras, returnFare_extras, linearLayoutRCard;

	RelativeLayout relative, relativeLayoutRange, relativelayoutBaseFare, relativelayoutPerDelivery, relativelayoutReturnFare,
			relativeLayoutPickupWaitTime, relativeLayoutPickupDistanceFare, relativeLayoutLoadingFare, relativeLayoutUnLoadingFare;
	TextView textViewPerKm;

	TextView textViewNoRCard;
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
		relative = (RelativeLayout) rootView.findViewById(R.id.relative);
		activity = (NewRateCardActivity) getActivity();
		new ASSL(activity, relative, 1134, 720, false);

		perKm_extras = (LinearLayout) rootView.findViewById(R.id.perKm_extras);
		basefare_extras = (LinearLayout) rootView.findViewById(R.id.basefare_extras);
		returnFare_extras = (LinearLayout) rootView.findViewById(R.id.returnFare_extras);
		perDelivery_extras = (LinearLayout) rootView.findViewById(R.id.perDelivery_extras);
		loadingFare_extras = (LinearLayout) rootView.findViewById(R.id.loadingFare_extras);
		unLoadingFare_extras = (LinearLayout) rootView.findViewById(R.id.unLoadingFare_extras);
		pickupWaitTime_extras = (LinearLayout) rootView.findViewById(R.id.pickupWaitTime_extras);
		pickupDistanceFare_extras = (LinearLayout) rootView.findViewById(R.id.pickupDistanceFare_extras);
		linearLayoutRCard = (LinearLayout) rootView.findViewById(R.id.linearLayoutRCard);
		linearLayoutRCard.setVisibility(View.GONE);

		relativeLayoutRange = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutRange);
		relativelayoutBaseFare = (RelativeLayout) rootView.findViewById(R.id.relativelayoutBaseFare);
		relativelayoutReturnFare = (RelativeLayout) rootView.findViewById(R.id.relativelayoutReturnFare);
		relativeLayoutLoadingFare = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutLoadingFare);
		relativelayoutPerDelivery = (RelativeLayout) rootView.findViewById(R.id.relativelayoutPerDelivery);
		relativeLayoutUnLoadingFare = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutUnLoadingFare);
		relativeLayoutPickupWaitTime = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutPickupWaitTime);
		relativeLayoutPickupDistanceFare = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutPickupDistanceFare);

		((TextView) rootView.findViewById(R.id.textViewReturnFare)).setTypeface(Fonts.mavenRegular(activity));
		((TextView) rootView.findViewById(R.id.textViewBeforeRide)).setTypeface(Fonts.mavenRegular(activity));
		((TextView) rootView.findViewById(R.id.textViewLoadingFare)).setTypeface(Fonts.mavenRegular(activity));
		((TextView) rootView.findViewById(R.id.textViewPerDelivery)).setTypeface(Fonts.mavenRegular(activity));
		((TextView) rootView.findViewById(R.id.textViewUnLoadingFare)).setTypeface(Fonts.mavenRegular(activity));
		((TextView) rootView.findViewById(R.id.textViewPickupWaitTime)).setTypeface(Fonts.mavenRegular(activity));
		((TextView) rootView.findViewById(R.id.textViewPickupDistanceFare)).setTypeface(Fonts.mavenRegular(activity));
		textViewNoRCard = (TextView) rootView.findViewById(R.id.textViewNoRCard);
		textViewPerKm = (TextView) rootView.findViewById(R.id.textViewPerKm);

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

			textViewPerKm.setText(getString(R.string.per_km_wo_brack_format, rateCardResponse.getData().getDistanceUnit()));

			if(data.getBaseFare() != null){
				setFares(data.getBaseFare(), basefare_extras);
				relativelayoutBaseFare.setVisibility(View.VISIBLE);
			} else {
				relativelayoutBaseFare.setVisibility(View.GONE);
			}

			if(data.getDistanceFare() != null){
				setFares(data.getDistanceFare(), perKm_extras);
				relativeLayoutRange.setVisibility(View.VISIBLE);
			} else {
				relativeLayoutRange.setVisibility(View.GONE);
			}

			if(data.getDeliveryFare() != null){
				setFares(data.getDeliveryFare(), perDelivery_extras);
				relativelayoutPerDelivery.setVisibility(View.VISIBLE);
			} else {
				relativelayoutPerDelivery.setVisibility(View.GONE);
			}

			if(data.getReturnFare() != null){
				setFares(data.getReturnFare(), returnFare_extras);
				relativelayoutReturnFare.setVisibility(View.VISIBLE);
			} else {
				relativelayoutReturnFare.setVisibility(View.GONE);
			}

			if(data.getPickupDistanceFare() != null){
				setFares(data.getPickupDistanceFare(), pickupDistanceFare_extras);
				relativeLayoutPickupDistanceFare.setVisibility(View.VISIBLE);
			} else {
				relativeLayoutPickupDistanceFare.setVisibility(View.GONE);
			}

			if(data.getPickupWaitTimeFare() != null){
				setFares(data.getPickupWaitTimeFare(), pickupWaitTime_extras);
				relativeLayoutPickupWaitTime.setVisibility(View.VISIBLE);
			} else {
				relativeLayoutPickupWaitTime.setVisibility(View.GONE);
			}

			if(data.getLoadingFare() != null){
				setFares(data.getLoadingFare(), loadingFare_extras);
				relativeLayoutLoadingFare.setVisibility(View.VISIBLE);
			} else {
				relativeLayoutLoadingFare.setVisibility(View.GONE);
			}

			if(data.getUnloadingFare() != null){
				setFares(data.getUnloadingFare(), unLoadingFare_extras);
				relativeLayoutUnLoadingFare.setVisibility(View.VISIBLE);
			} else {
				relativeLayoutUnLoadingFare.setVisibility(View.GONE);
			}

		} else {
			performBackPressed();
		}

	}

	public void setFares(List<DeliveryRateCardResponse.Data.Fare> fare, LinearLayout ll){
		try {
			linearLayoutRCard.setVisibility(View.VISIBLE);
			textViewNoRCard.setVisibility(View.GONE);
			for(int i=0; i< fare.size(); i++){
				String unit = fare.get(i).getUnit();
				String unitValue = fare.get(i).getUnitValue();
				List<DeliveryRateCardResponse.Data.Slot> slots = fare.get(i).getSlots();
				if(fare.size()>1){
					ll.addView(getAdditionalTimeData(fare.get(i).getTimeInterval()));
				}
				if(fare.size() ==1 && slots.size() ==1){
					ll.addView(getSingleData(String.valueOf(slots.get(0).getValue()+" ("+unitValue+")"), String.valueOf(slots.get(0).getValue())));
				} else {
					for (int j = 0; j < slots.size(); j++) {
						if (j == 0) {
							ll.addView(getAdditionalData(true, activity.getResources().getString(R.string.range)+" ("+unit+")", activity.getResources().getString(R.string.value)+" ("+unitValue+")"));
							ll.addView(getAdditionalData(false, slots.get(j).getRange(), String.valueOf(slots.get(j).getValue())));
						} else {
							ll.addView(getAdditionalData(false, slots.get(j).getRange(), String.valueOf(slots.get(j).getValue())));
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private View getAdditionalData(boolean first, String left, String right) {
		LayoutInflater layoutInflater = (LayoutInflater) getActivity().getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = layoutInflater.inflate(R.layout.item_additional_fare_view, null, false);
		LinearLayout linearLayoutRoot = (LinearLayout) view.findViewById(R.id.linearLayoutRoot);
		linearLayoutRoot.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		ASSL.DoMagic(linearLayoutRoot);
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.main_layout);
		TextView leftText = (TextView) view.findViewById(R.id.left_text_view);
		TextView rightText = (TextView) view.findViewById(R.id.right_text_view);
		View topLine = view.findViewById(R.id.top_line);
		topLine.setVisibility(View.GONE);
		leftText.setBackgroundResource(R.color.sliding_bottom_bg_color);
		rightText.setBackgroundResource(R.color.sliding_bottom_bg_color);
		if(first) {
			topLine.setVisibility(View.VISIBLE);
			leftText.setBackgroundResource(R.color.white_dark1);
			rightText.setBackgroundResource(R.color.white_dark1);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.dpToPx(activity,40));
			layout.setLayoutParams(layoutParams);
		}
		leftText.setText(left);
		rightText.setText(right);
		return view;
	}

	private View getAdditionalTimeData(String left) {
		LayoutInflater layoutInflater = (LayoutInflater) getActivity().getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = layoutInflater.inflate(R.layout.item_additional_time_slot_view, null, false);
		LinearLayout linearLayoutRoot = (LinearLayout) view.findViewById(R.id.linearLayoutRoot);
		linearLayoutRoot.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		ASSL.DoMagic(linearLayoutRoot);
		LinearLayout layout = (LinearLayout) view.findViewById(R.id.main_layout);
		TextView leftText = (TextView) view.findViewById(R.id.left_text_view);
		View topLine = view.findViewById(R.id.top_line);
		topLine.setVisibility(View.GONE);
		leftText.setText(left);
		return view;
	}

	private View getSingleData(String left, String right) {
		LayoutInflater layoutInflater = (LayoutInflater) getActivity().getBaseContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = layoutInflater.inflate(R.layout.item_single_fare_view, null, false);
		LinearLayout linearLayoutRoot = (LinearLayout) view.findViewById(R.id.linearLayoutRoot);
		linearLayoutRoot.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
		ASSL.DoMagic(linearLayoutRoot);
		RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.main_layout);
		TextView leftText = (TextView) view.findViewById(R.id.left_text_view);
		TextView rightText = (TextView) view.findViewById(R.id.right_text_view);
//		View topLine = view.findViewById(R.id.top_line);
//		topLine.setVisibility(View.GONE);
		leftText.setText(left);
		rightText.setText(right);
		rightText.setVisibility(View.GONE);
		return view;
	}


	private void getRateCardDetails(final NewRateCardActivity activity) {
		try {
			HashMap<String, String> params = new HashMap<>();
			params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
			HomeUtil.putDefaultParams(params);
			RestClient.getApiServices().deliveryRateCardDetail(params, new Callback<DeliveryRateCardResponse>() {
				@Override
				public void success(DeliveryRateCardResponse rateCardResponse, Response response) {
					try {
						String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
						JSONObject jObj = new JSONObject(jsonString);
						String message = JSONParser.getServerMessage(jObj);
						int flag = jObj.getInt("flag");
						if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
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
