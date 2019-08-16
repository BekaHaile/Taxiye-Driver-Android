package product.clicklabs.jugnoo.driver;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import product.clicklabs.jugnoo.driver.adapters.RentalAndOutstationVehicleAdapter;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.RateCardResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.RentalVehicle;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class DriverRateCard extends android.support.v4.app.Fragment {

	LinearLayout relative, linearLayoutDriverReferral, linearLayoutMain;
	RelativeLayout relativeLayoutDriverReferralHeading, relativeLayoutDriverReferralSingle, relativeLayoutNoData;
	TextView textViewPickupChargesValues, textViewBaseFareValue, textViewDistancePKmValue, textViewPickupChargesCond,
			textViewTimePKmValue, textViewDtoCValue, textViewDtoDValue, textViewDtoC, textViewDtoD, textViewDriverReferral,
			textViewDriverReferralValue, textViewDifferentialPricingEnable, textViewPickupChargesCondStar,
			textViewPickupChargesperkm, textViewPKm;
	ImageView imageViewHorizontal7;
	TextView textViewSpecialInfo;
	LinearLayout llBeforeRide, llInRide, llInRideBefore, llReferralInfo;
	RelativeLayout rlBeforeRide;
	RecyclerView rvRentalVehicle, rvOutstationVehicle;

	NewRateCardActivity activity;
	private View rootView;
	public DriverRateCard(){
		
	}

	public static DriverRateCard newInstance(boolean isHTMLRateCard){
		DriverRateCard fragment = new DriverRateCard();
		Bundle bundle = new Bundle();
		bundle.putBoolean(Constants.KEY_HTML_RATE_CARD, isHTMLRateCard);
		fragment.setArguments(bundle);
		return fragment;
	}

	private boolean isHTMLRateCard;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.activity_rate_card, container, false);
		activity = (NewRateCardActivity) getActivity();

		relative = (LinearLayout) rootView.findViewById(R.id.relative);
		new ASSL(activity, relative, 1134, 720, false);

		isHTMLRateCard = getArguments().getBoolean(Constants.KEY_HTML_RATE_CARD, false);

		linearLayoutDriverReferral = (LinearLayout) rootView.findViewById(R.id.linearLayoutDriverReferral);
		relativeLayoutDriverReferralHeading = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutDriverReferralHeading);
		relativeLayoutNoData = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutNoData);
		relativeLayoutNoData.setVisibility(View.GONE);

		linearLayoutMain = (LinearLayout) rootView.findViewById(R.id.linearLayoutMain);
		linearLayoutMain.setVisibility(View.INVISIBLE);

		textViewPickupChargesValues = (TextView) rootView.findViewById(R.id.textViewPickupChargesValues);
		textViewPickupChargesValues.setTypeface(Fonts.mavenRegular(activity));
		textViewBaseFareValue = (TextView) rootView.findViewById(R.id.textViewBaseFareValue);
		textViewBaseFareValue.setTypeface(Fonts.mavenRegular(activity));
		textViewDistancePKmValue = (TextView) rootView.findViewById(R.id.textViewDistancePKmValue);
		textViewDistancePKmValue.setTypeface(Fonts.mavenRegular(activity));
		textViewTimePKmValue = (TextView) rootView.findViewById(R.id.textViewTimePKmValue);
		textViewTimePKmValue.setTypeface(Fonts.mavenRegular(activity));
		textViewDtoCValue = (TextView) rootView.findViewById(R.id.textViewDtoCValue);
		textViewDtoCValue.setTypeface(Fonts.mavenRegular(activity));
		textViewDtoDValue = (TextView) rootView.findViewById(R.id.textViewDtoDValue);
		textViewDtoDValue.setTypeface(Fonts.mavenRegular(activity));
		textViewPickupChargesCondStar = (TextView) rootView.findViewById(R.id.textViewPickupChargesCondStar);
		textViewPickupChargesCondStar.setTypeface(Fonts.mavenRegular(activity));
		textViewPickupChargesperkm = (TextView) rootView.findViewById(R.id.textViewPickupChargesperkm);
		textViewPickupChargesperkm.setTypeface(Fonts.mavenRegular(activity));
		textViewPKm = (TextView) rootView.findViewById(R.id.textViewPKm);
		textViewPKm.setTypeface(Fonts.mavenRegular(activity));

		textViewDtoC = (TextView) rootView.findViewById(R.id.textViewDtoC);
		textViewDtoC.setTypeface(Fonts.mavenRegular(activity));
		textViewDtoD = (TextView) rootView.findViewById(R.id.textViewDtoD);
		rvRentalVehicle = rootView.findViewById(R.id.rvRentalVehicle);
		rvOutstationVehicle = rootView.findViewById(R.id.rvOutstationVehicle);
		textViewDtoD.setTypeface(Fonts.mavenRegular(activity));

		textViewPickupChargesCond = (TextView) rootView.findViewById(R.id.textViewPickupChargesCond);
		textViewPickupChargesCond.setTypeface(Fonts.mavenRegular(activity));

		textViewDriverReferral= (TextView) rootView.findViewById(R.id.textViewDriverReferral);
		textViewDriverReferral.setTypeface(Fonts.mavenRegular(activity));
		textViewDriverReferralValue= (TextView) rootView.findViewById(R.id.textViewDriverReferralValue);
		textViewDriverReferralValue.setTypeface(Fonts.mavenRegular(activity));
		textViewDifferentialPricingEnable = (TextView) rootView.findViewById(R.id.textViewDifferentialPricingEnable);
		textViewDifferentialPricingEnable.setTypeface(Fonts.mavenRegular(activity));

		((TextView) rootView.findViewById(R.id.textViewBeforeRide)).setTypeface(Fonts.mavenRegular(activity));
		((TextView) rootView.findViewById(R.id.textViewPickupCharges)).setTypeface(Fonts.mavenRegular(activity));
		((TextView) rootView.findViewById(R.id.textViewBaseFare)).setTypeface(Fonts.mavenRegular(activity));
		((TextView) rootView.findViewById(R.id.textViewDistancePKm)).setTypeface(Fonts.mavenRegular(activity));
		((TextView) rootView.findViewById(R.id.textViewTimePKm)).setTypeface(Fonts.mavenRegular(activity));
		((TextView) rootView.findViewById(R.id.textViewPm)).setTypeface(Fonts.mavenRegular(activity));
		((TextView) rootView.findViewById(R.id.textViewInRide)).setTypeface(Fonts.mavenRegular(activity));
		((TextView) rootView.findViewById(R.id.textViewReferral)).setTypeface(Fonts.mavenRegular(activity));

		llBeforeRide = (LinearLayout) rootView.findViewById(R.id.llBeforeRide);
		rlBeforeRide = rootView.findViewById(R.id.rlBeforeRide);
		llInRide = (LinearLayout) rootView.findViewById(R.id.llInRide);
		llInRideBefore = (LinearLayout) rootView.findViewById(R.id.llInRideBefore);
		llInRideBefore.setVisibility(View.GONE);
		llReferralInfo = rootView.findViewById(R.id.llReferralInfo);
		textViewSpecialInfo = (TextView) rootView.findViewById(R.id.textViewSpecialInfo);
		textViewSpecialInfo.setTypeface(Fonts.mavenRegular(activity));

		relativeLayoutDriverReferralSingle = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutDriverReferralSingle);
		imageViewHorizontal7 = (ImageView) rootView.findViewById(R.id.imageViewHorizontal7);

		getRateCardDetails(activity);

		relativeLayoutNoData.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getRateCardDetails(activity);
			}
		});

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

			textViewPickupChargesValues.setText(Utils.formatCurrencyValue(rateCardResponse.getRates().getCurrencyUnit() , rateCardResponse.getRates().getPickupCharges(), false));
			textViewPickupChargesperkm.setText(getString(R.string.per_format, Utils.getDistanceUnit(rateCardResponse.getRates().getDistanceUnit())));
			textViewPKm.setText(getString(R.string.per_format, Utils.getDistanceUnit(rateCardResponse.getRates().getDistanceUnit())));

			if(rateCardResponse.getRates().getPickupChargesThreshold() > 0){
				textViewPickupChargesCond.setText(getString(R.string.applicable_after,
						String.valueOf(rateCardResponse.getRates().getPickupChargesThreshold())
								+" "+Utils.getDistanceUnit(rateCardResponse.getRates().getDistanceUnit())));
				textViewPickupChargesCondStar.setText("*");
			} else {
				textViewPickupChargesCond.setVisibility(View.GONE);
				textViewPickupChargesCondStar.setVisibility(View.GONE);
			}

			textViewBaseFareValue.setText(Utils.formatCurrencyValue(rateCardResponse.getRates().getCurrencyUnit() ,  rateCardResponse.getRates().getBaseFare(), false));
			textViewDistancePKmValue.setText(Utils.formatCurrencyValue(rateCardResponse.getRates().getCurrencyUnit() ,  rateCardResponse.getRates().getFarePerKm(), false));
			textViewTimePKmValue.setText(Utils.formatCurrencyValue(rateCardResponse.getRates().getCurrencyUnit() ,  rateCardResponse.getRates().getFarePerMin(), false));

			if(rateCardResponse.getRates().getAfterThresholdDistance() > 0){
				textViewDifferentialPricingEnable.setVisibility(View.VISIBLE);
				textViewDifferentialPricingEnable.setText(getResources().getString(R.string.diffrential_pricing_rate,
						String.valueOf(rateCardResponse.getRates().getAfterThresholdDistance())
								+" "+Utils.getDistanceUnit(rateCardResponse.getRates().getDistanceUnit()),
						Utils.formatCurrencyValue(rateCardResponse.getRates().getCurrencyUnit() ,
								rateCardResponse.getRates().getGetAfterThresholdValue(), false)
								+"/"+Utils.getDistanceUnit(rateCardResponse.getRates().getDistanceUnit())));
			}


			double dToCReferral = rateCardResponse.getRates().getDriverToCustomerReferral();
			double dToDReferral = rateCardResponse.getRates().getDriverToDriverReferral();

			if(dToCReferral == 0 && dToDReferral ==0){
				linearLayoutDriverReferral.setVisibility(View.GONE);
				relativeLayoutDriverReferralHeading.setVisibility(View.GONE);
			} else if(dToCReferral > 0 && dToDReferral ==0){
				linearLayoutDriverReferral.setVisibility(View.GONE);
				relativeLayoutDriverReferralSingle.setVisibility(View.VISIBLE);
				textViewDriverReferral.setText(getResources().getString(R.string.driver_to_customer));
				textViewDriverReferralValue.setText(Utils.formatCurrencyValue(rateCardResponse.getRates().getCurrencyUnit() ,  dToCReferral, false));
			} else if(dToCReferral == 0 && dToDReferral >0){
				linearLayoutDriverReferral.setVisibility(View.GONE);
				relativeLayoutDriverReferralSingle.setVisibility(View.VISIBLE);
				textViewDriverReferral.setText(getResources().getString(R.string.driver_to_driver));
				textViewDriverReferralValue.setText(Utils.formatCurrencyValue(rateCardResponse.getRates().getCurrencyUnit() ,  dToDReferral, false));
			} else if(dToCReferral > 0 && dToDReferral > 0){
				linearLayoutDriverReferral.setVisibility(View.VISIBLE);
				textViewDtoCValue.setText(Utils.formatCurrencyValue(rateCardResponse.getRates().getCurrencyUnit() ,  dToCReferral, false));
				textViewDtoDValue.setText(Utils.formatCurrencyValue(rateCardResponse.getRates().getCurrencyUnit() ,  dToDReferral, false));
			}

			llBeforeRide.setVisibility(rateCardResponse.getRates().getPickupChargesEnabled() == 1
					? View.VISIBLE : View.GONE);
			rlBeforeRide.setVisibility(rateCardResponse.getRates().getPickupChargesEnabled() == 1
					? View.VISIBLE : View.GONE);
			llInRide.setVisibility(rateCardResponse.getRates().getInRideChargesEnabled() == 1
					? View.VISIBLE : View.GONE);
			if(!TextUtils.isEmpty(rateCardResponse.getRates().getRateCardInformation())){
				textViewSpecialInfo.setVisibility(View.VISIBLE);
				textViewSpecialInfo.setText(Utils.fromHtml(rateCardResponse.getRates().getRateCardInformation()));
			} else {
				textViewSpecialInfo.setVisibility(View.GONE);
			}

		} else {
			performBackPressed();
		}

	}

	private void getRateCardDetails(final NewRateCardActivity activity) {
		try {
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("access_token", Data.userData.accessToken);
			HomeUtil.putDefaultParams(params);
			DialogPopup.showLoadingDialog(activity, getString(R.string.loading));
			RestClient.getApiServices().rateCardDetail(params, new Callback<RateCardResponse>() {
				@Override
				public void success(RateCardResponse rateCardResponse, Response response) {
					try {
						String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
						JSONObject jObj = new JSONObject(jsonString);
						String message = JSONParser.getServerMessage(jObj);
						int flag = jObj.getInt("flag");

						if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
							updateData(rateCardResponse);
							linearLayoutMain.setVisibility(View.VISIBLE);
							relativeLayoutNoData.setVisibility(View.GONE);

							rvRentalVehicle.setVisibility(View.GONE);
							if(!isHTMLRateCard){
								llReferralInfo.setVisibility(View.VISIBLE);
								if(rateCardResponse.getRegions() != null && !rateCardResponse.getRegions().isEmpty()) {
									rvRentalVehicle.setVisibility(View.VISIBLE);
									setRentalAndOutstationAdapter(rateCardResponse.getRegions());
								}
							} else {
								llReferralInfo.setVisibility(View.GONE);
							}

						} else {
							relativeLayoutNoData.setVisibility(View.VISIBLE);
						}
					} catch (Exception exception) {
						exception.printStackTrace();
						relativeLayoutNoData.setVisibility(View.VISIBLE);
					}
					DialogPopup.dismissLoadingDialog();
				}
				@Override
				public void failure(RetrofitError error) {
					Log.i("error", String.valueOf(error));
					relativeLayoutNoData.setVisibility(View.VISIBLE);
					DialogPopup.dismissLoadingDialog();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			relativeLayoutNoData.setVisibility(View.VISIBLE);
		}
	}

	private void setRentalAndOutstationAdapter(List<RentalVehicle> regions) {
		LinearLayoutManager layoutManager = new LinearLayoutManager(activity);
		rvRentalVehicle.setLayoutManager(layoutManager);
		rvRentalVehicle.setNestedScrollingEnabled(false);
		RentalAndOutstationVehicleAdapter rentalAndOutstationVehicleAdapter = new RentalAndOutstationVehicleAdapter();
		rentalAndOutstationVehicleAdapter.setList((ArrayList<RentalVehicle>) regions, Data.userData.getCurrency());
		rvRentalVehicle.setAdapter(rentalAndOutstationVehicleAdapter);

        rvOutstationVehicle.setVisibility(View.GONE);

	}

}
