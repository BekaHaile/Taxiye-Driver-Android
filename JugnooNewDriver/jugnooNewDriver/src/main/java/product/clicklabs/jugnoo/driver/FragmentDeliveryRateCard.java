package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONObject;

import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.RateCardResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Log;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class FragmentDeliveryRateCard extends android.support.v4.app.Fragment {

	LinearLayout linearLayoutRange1, linearLayoutRange2, linearLayoutRange3;
	RelativeLayout relativeLayoutFareRange, relative;
	TextView textViewPickupChargesValues, textViewFareValue, textViewRange1, textViewRange2,
			textViewRange3, textViewFare1, textViewFare2, textViewFare3, textViewAdDeliveryFareValue, textViewReturnfareText;

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


		linearLayoutRange1 = (LinearLayout) rootView.findViewById(R.id.linearLayoutRange1);
		linearLayoutRange2 = (LinearLayout) rootView.findViewById(R.id.linearLayoutRange2);
		linearLayoutRange3 = (LinearLayout) rootView.findViewById(R.id.linearLayoutRange3);

		relativeLayoutFareRange = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutFareRange);

		textViewPickupChargesValues = (TextView) rootView.findViewById(R.id.textViewPickupChargesValues);
		textViewPickupChargesValues.setTypeface(Fonts.mavenRegular(activity));
		textViewFareValue = (TextView) rootView.findViewById(R.id.textViewFareValue);
		textViewFareValue.setTypeface(Fonts.mavenRegular(activity));
		textViewRange1 = (TextView) rootView.findViewById(R.id.textViewRange1);
		textViewRange1.setTypeface(Fonts.mavenRegular(activity));
		textViewRange2 = (TextView) rootView.findViewById(R.id.textViewRange2);
		textViewRange2.setTypeface(Fonts.mavenRegular(activity));
		textViewRange3 = (TextView) rootView.findViewById(R.id.textViewRange3);
		textViewRange3.setTypeface(Fonts.mavenRegular(activity));
		textViewFare1 = (TextView) rootView.findViewById(R.id.textViewFare1);
		textViewFare1.setTypeface(Fonts.mavenRegular(activity));
		textViewFare2 = (TextView) rootView.findViewById(R.id.textViewFare2);
		textViewFare2.setTypeface(Fonts.mavenRegular(activity));

		textViewFare3 = (TextView) rootView.findViewById(R.id.textViewFare3);
		textViewFare3.setTypeface(Fonts.mavenRegular(activity));
		textViewAdDeliveryFareValue = (TextView) rootView.findViewById(R.id.textViewAdDeliveryFareValue);
		textViewAdDeliveryFareValue.setTypeface(Fonts.mavenRegular(activity));

		textViewReturnfareText = (TextView) rootView.findViewById(R.id.textViewReturnfareText);
		textViewReturnfareText.setTypeface(Fonts.mavenRegular(activity));


		((TextView) rootView.findViewById(R.id.textViewBeforeRide)).setTypeface(Fonts.mavenRegular(activity));
		((TextView) rootView.findViewById(R.id.textViewPickupCharges)).setTypeface(Fonts.mavenRegular(activity));
		((TextView) rootView.findViewById(R.id.textViewPerKm)).setTypeface(Fonts.mavenRegular(activity));
		((TextView) rootView.findViewById(R.id.textViewRangeText)).setTypeface(Fonts.mavenRegular(activity));
		((TextView) rootView.findViewById(R.id.textViewPerKmText)).setTypeface(Fonts.mavenRegular(activity));
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


	public void updateData(RateCardResponse rateCardResponse) {

		if (rateCardResponse != null) {


		} else {
			performBackPressed();
		}

	}

	private void getRateCardDetails(final Activity activity) {
		try {
			RestClient.getApiServices().rateCardDetail(Data.userData.accessToken, new Callback<RateCardResponse>() {
				@Override
				public void success(RateCardResponse rateCardResponse, Response response) {
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
