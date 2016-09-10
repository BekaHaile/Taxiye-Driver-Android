package product.clicklabs.jugnoo.driver.selfAudit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.HomeActivity;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.AuditStateResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.AuditTypeResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.Log;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by aneeshbansal on 18/08/16.
 */
public class SelectAuditFragment extends Fragment {

	private LinearLayout linearLayoutRoot;
	private RelativeLayout linearLayoutSelfAudit, linearLayoutNJAutoBranding, linearLayoutNJAutoAudit;
	private TextView textViewSelfAudit, textViewNJAutoBranding, textViewNJAutoAudit;
	private TextView textViewSelfAuditLast, textViewSelfAuditNext, textViewNJBOffer, textViewNJAOffer, textViewNJBNumber, textViewNJANumber,
			textViewSelfAuditStatus, textViewNJAutoBrandingStatus, textViewNJAutoAuditStatus;


	private View rootView;
	private SelfAuditActivity activity;

	private ImageView imageViewBack, imageViewArrowSA, imageViewArrowNJB, imageViewArrowNJA;
	private AuditTypeResponse auditTypeResponse;
	private AuditStateResponse auditStateResponse;

	public SelectAuditFragment(){

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
		rootView = inflater.inflate(R.layout.fragment_select_audit, container, false);

		activity = (SelfAuditActivity) getActivity();

		linearLayoutRoot = (LinearLayout) rootView.findViewById(R.id.linearLayoutRoot);
		new ASSL(activity, linearLayoutRoot, 1134, 720, false);


		getAuditStatus(activity);
		linearLayoutSelfAudit = (RelativeLayout) rootView.findViewById(R.id.linearLayoutSelfAudit);
		linearLayoutNJAutoBranding = (RelativeLayout) rootView.findViewById(R.id.linearLayoutNJAutoBranding);
		linearLayoutNJAutoAudit = (RelativeLayout) rootView.findViewById(R.id.linearLayoutNJAutoAudit);


		textViewSelfAudit = (TextView) rootView.findViewById(R.id.textViewSelfAudit);
		textViewSelfAudit.setTypeface(Data.latoHeavy(activity));
		textViewNJAutoBranding = (TextView) rootView.findViewById(R.id.textViewNJAutoBranding);
		textViewNJAutoBranding.setTypeface(Data.latoHeavy(activity));
		textViewNJAutoAudit = (TextView) rootView.findViewById(R.id.textViewNJAutoAudit);
		textViewNJAutoAudit.setTypeface(Data.latoHeavy(activity));

		textViewSelfAuditLast = (TextView) rootView.findViewById(R.id.textViewSelfAuditLast);
		textViewSelfAuditLast.setTypeface(Data.latoRegular(activity));
		textViewSelfAuditNext  = (TextView) rootView.findViewById(R.id.textViewSelfAuditNext);
		textViewSelfAuditNext.setTypeface(Data.latoRegular(activity));
		textViewSelfAuditStatus = (TextView) rootView.findViewById(R.id.textViewSelfAuditStatus);
		textViewSelfAuditStatus.setTypeface(Data.latoRegular(activity));
		textViewNJAutoBrandingStatus = (TextView) rootView.findViewById(R.id.textViewNJAutoBrandingStatus);
		textViewNJAutoBrandingStatus.setTypeface(Data.latoRegular(activity));
		textViewNJAutoAuditStatus = (TextView) rootView.findViewById(R.id.textViewNJAutoAuditStatus);
		textViewNJAutoAuditStatus.setTypeface(Data.latoRegular(activity));


		textViewNJBOffer = (TextView) rootView.findViewById(R.id.textViewNJBOffer);
		textViewNJBOffer.setTypeface(Data.latoRegular(activity));
		textViewNJAOffer = (TextView) rootView.findViewById(R.id.textViewNJAOffer);
		textViewNJAOffer.setTypeface(Data.latoRegular(activity));
		textViewNJBNumber = (TextView) rootView.findViewById(R.id.textViewNJBNumber);
		textViewNJBNumber.setTypeface(Data.latoRegular(activity));
		textViewNJANumber = (TextView) rootView.findViewById(R.id.textViewNJANumber);
		textViewNJANumber.setTypeface(Data.latoRegular(activity));

		imageViewBack = (ImageView) rootView.findViewById(R.id.imageViewBack);
		imageViewArrowSA = (ImageView) rootView.findViewById(R.id.imageViewArrowSA);
		imageViewArrowNJB = (ImageView) rootView.findViewById(R.id.imageViewArrowNJB);
		imageViewArrowNJA = (ImageView) rootView.findViewById(R.id.imageViewArrowNJA);

		linearLayoutSelfAudit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getAuditState(activity, 0);
			}
		});

		linearLayoutNJAutoBranding.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getAuditState(activity, 1);
			}
		});

		linearLayoutNJAutoAudit.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getAuditState(activity, 2);
			}
		});

		imageViewBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				performBackPress();
			}
		});

		return rootView;
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			ASSL.closeActivity(linearLayoutRoot);
		} catch (Exception e) {
		}
		System.gc();
	}

	public void performBackPress(){
		Intent intent = new Intent(activity, HomeActivity.class);
		startActivity(intent);
	}

	public void update(){
		try{
			if(activity != null && auditTypeResponse != null){

				if(auditTypeResponse.getSaFlag() ==0){
					linearLayoutSelfAudit.setVisibility(View.GONE);
				} else if(auditTypeResponse.getSaFlag() == 1){
					linearLayoutSelfAudit.setVisibility(View.VISIBLE);
				} else if (auditTypeResponse.getSaFlag() == 2){
					linearLayoutSelfAudit.setClickable(false);
				}

				if(auditTypeResponse.getNjaFlag() ==0){
					linearLayoutNJAutoAudit.setVisibility(View.GONE);
				} else if(auditTypeResponse.getNjaFlag() == 1){
					linearLayoutNJAutoAudit.setVisibility(View.VISIBLE);
				} else if (auditTypeResponse.getNjaFlag() == 2){
					linearLayoutNJAutoAudit.setClickable(false);
				}

				if(auditTypeResponse.getNjbFlag() ==0){
					linearLayoutNJAutoBranding.setVisibility(View.GONE);
				} else if(auditTypeResponse.getNjbFlag() == 1){
					linearLayoutNJAutoBranding.setVisibility(View.VISIBLE);
				} else if (auditTypeResponse.getNjbFlag() == 2){
					linearLayoutNJAutoBranding.setClickable(false);
				}



				if(auditTypeResponse.getSaStatus() == 5) {
					textViewSelfAuditStatus.setText(getResources().getString(R.string.audit_required));
					textViewSelfAuditStatus.setTextColor(getResources().getColor(R.color.red_delivery));
				} else if(auditTypeResponse.getSaStatus() == 10){
					textViewSelfAuditStatus.setText(getResources().getString(R.string.in_progress));
					textViewSelfAuditStatus.setTextColor(getResources().getColor(R.color.status_yellow));
				} else if(auditTypeResponse.getSaStatus() == 15){
					textViewSelfAuditStatus.setText(getResources().getString(R.string.please_submit));
					textViewSelfAuditStatus.setTextColor(getResources().getColor(R.color.status_yellow));
				} else if(auditTypeResponse.getSaStatus() == 20){
					textViewSelfAuditStatus.setText(getResources().getString(R.string.request_pending));
					textViewSelfAuditStatus.setTextColor(getResources().getColor(R.color.red_delivery));
					imageViewArrowSA.setVisibility(View.GONE);
				} else if(auditTypeResponse.getSaStatus() == 25){
					textViewSelfAuditStatus.setText(getResources().getString(R.string.rejected));
					textViewSelfAuditStatus.setTextColor(getResources().getColor(R.color.red_delivery));
				} else if(auditTypeResponse.getSaStatus() == 30){
					textViewSelfAuditStatus.setText(getResources().getString(R.string.accepted));
					textViewSelfAuditStatus.setTextColor(getResources().getColor(R.color.green_delivery));
					imageViewArrowSA.setVisibility(View.GONE);
				}

				if(auditTypeResponse.getNjaStatus() == 5) {
					textViewNJAutoAuditStatus.setText(getResources().getString(R.string.audit_required));
					textViewNJAutoAuditStatus.setTextColor(getResources().getColor(R.color.red_delivery));
				} else if(auditTypeResponse.getNjaStatus() == 10){
					textViewNJAutoAuditStatus.setText(getResources().getString(R.string.in_progress));
					textViewNJAutoAuditStatus.setTextColor(getResources().getColor(R.color.status_yellow));
				} else if(auditTypeResponse.getNjaStatus() == 15){
					textViewNJAutoAuditStatus.setText(getResources().getString(R.string.please_submit));
					textViewNJAutoAuditStatus.setTextColor(getResources().getColor(R.color.status_yellow));
				} else if(auditTypeResponse.getNjaStatus() == 20){
					textViewNJAutoAuditStatus.setText(getResources().getString(R.string.pending));
					imageViewArrowNJA.setVisibility(View.GONE);
					textViewNJAutoAuditStatus.setTextColor(getResources().getColor(R.color.red_delivery));
				} else if(auditTypeResponse.getNjaStatus() == 25){
					textViewNJAutoAuditStatus.setText(getResources().getString(R.string.rejected));
					textViewNJAutoAuditStatus.setTextColor(getResources().getColor(R.color.red_delivery));
				} else if(auditTypeResponse.getNjaStatus() == 30){
					textViewNJAutoAuditStatus.setText(getResources().getString(R.string.accepted));
					textViewNJAutoAuditStatus.setTextColor(getResources().getColor(R.color.green_delivery));
					imageViewArrowNJA.setVisibility(View.GONE);
				}


				if(auditTypeResponse.getNjbStatus() == 5) {
					textViewNJAutoBrandingStatus.setText(getResources().getString(R.string.audit_required));
					textViewNJAutoBrandingStatus.setTextColor(getResources().getColor(R.color.red_delivery));
				} else if(auditTypeResponse.getNjbStatus() == 10){
					textViewNJAutoBrandingStatus.setText(getResources().getString(R.string.in_progress));
					textViewNJAutoBrandingStatus.setTextColor(getResources().getColor(R.color.status_yellow));
				} else if(auditTypeResponse.getNjbStatus() == 15){
					textViewNJAutoBrandingStatus.setText(getResources().getString(R.string.please_submit));
					textViewNJAutoBrandingStatus.setTextColor(getResources().getColor(R.color.status_yellow));
				} else if(auditTypeResponse.getNjbStatus() == 20){
					textViewNJAutoBrandingStatus.setText(getResources().getString(R.string.pending));
					imageViewArrowNJB.setVisibility(View.GONE);
					textViewNJAutoBrandingStatus.setTextColor(getResources().getColor(R.color.red_delivery));
				} else if(auditTypeResponse.getNjbStatus() == 25){
					textViewNJAutoBrandingStatus.setText(getResources().getString(R.string.rejected));
					textViewNJAutoBrandingStatus.setTextColor(getResources().getColor(R.color.red_delivery));
				} else if(auditTypeResponse.getNjbStatus() == 30){
					textViewNJAutoBrandingStatus.setText(getResources().getString(R.string.accepted));
					imageViewArrowNJB.setVisibility(View.GONE);
					textViewNJAutoBrandingStatus.setTextColor(getResources().getColor(R.color.green_delivery));
				}


				if("".equalsIgnoreCase(auditTypeResponse.getSaLastAuditString())) {
					textViewSelfAuditLast.setVisibility(View.GONE);
				} else {
					textViewSelfAuditLast.setText(auditTypeResponse.getSaLastAuditString());
				}

				if("".equalsIgnoreCase(auditTypeResponse.getSaNextAuditString())) {
					textViewSelfAuditNext.setVisibility(View.GONE);
				} else {
					textViewSelfAuditNext.setText(auditTypeResponse.getSaNextAuditString());
				}

				textViewNJBOffer.setText(auditTypeResponse.getNjbPromoString());
				textViewNJAOffer.setText(auditTypeResponse.getNjaPromoString());

				textViewNJBNumber.setText(auditTypeResponse.getNjbCountString());
				textViewNJANumber.setText(auditTypeResponse.getNjaCountString());


			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}


	public void getAuditStatus(final Activity activity) {
		try {
			if (AppStatus.getInstance(activity).isOnline(activity)) {
				DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));
				HashMap<String, String> params = new HashMap<String, String>();
				params.put("access_token", Data.userData.accessToken);

				RestClient.getApiServices().fetchAuditDetails(params, new Callback<AuditTypeResponse>() {
					@Override
					public void success(AuditTypeResponse auditTypeResponse, Response response) {
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						try {
							JSONObject jObj = new JSONObject(responseStr);
							int flag = jObj.getInt("flag");
							if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
								SelectAuditFragment.this.auditTypeResponse = auditTypeResponse;
								update();
							} else {
								DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
							}
						} catch (Exception exception) {
							exception.printStackTrace();
							DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
						}
						DialogPopup.dismissLoadingDialog();
					}

					@Override
					public void failure(RetrofitError error) {
						Log.e("request fail", error.toString());
						DialogPopup.dismissLoadingDialog();
						DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
					}
				});
			} else {
				DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void getAuditState(final Activity activity, final Integer auditType) {
		try {
			if (AppStatus.getInstance(activity).isOnline(activity)) {
				DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));
				HashMap<String, String> params = new HashMap<String, String>();

				params.put("access_token", Data.userData.accessToken);
				params.put("audit_type", String.valueOf(auditType));
				Log.i("params", "=" + params);

				RestClient.getApiServices().fetchAuditTypeStatus(params, new Callback<AuditStateResponse>() {
					@Override
					public void success(AuditStateResponse auditStateResponse, Response response) {
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						try {
							JSONObject jObj = new JSONObject(responseStr);
							int flag = jObj.getInt("flag");
							if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
								SelectAuditFragment.this.auditStateResponse = auditStateResponse;
								SelfAuditActivity selfAuditActivity = new SelfAuditActivity();
								selfAuditActivity.setAuditStateResponse(auditStateResponse);
								setFragmentState(auditType);
							} else {
								DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
							}
						} catch (Exception exception) {
							exception.printStackTrace();
							DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
						}
						DialogPopup.dismissLoadingDialog();
					}

					@Override
					public void failure(RetrofitError error) {
						Log.e("request fail", error.toString());
						DialogPopup.dismissLoadingDialog();
						DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
					}
				});
			} else {
				DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setFragmentState(Integer auditType){

		if(auditStateResponse != null) {

			if (auditStateResponse.getAction() == 5) {

				if(auditType == 0){

					activity.getTransactionUtils().openAuditCameraFragment(activity,
							activity.getRelativeLayoutContainer(), 0, auditType, 0);

				} else {
					activity.getTransactionUtils().openNonJugnooAuditFragment(activity,
							activity.getRelativeLayoutContainer(), auditType);
				}

			} else if (auditStateResponse.getAction() == 10) {

				activity.getTransactionUtils().openAuditCameraFragment(activity,
						activity.getRelativeLayoutContainer(), auditStateResponse.getLastUnavailableImageType(), auditType, 0);

			} else if (auditStateResponse.getAction() == 15) {

				activity.getTransactionUtils().openSubmitAuditFragment(activity,
						activity.getRelativeLayoutContainer(), auditType);

			} else if (auditStateResponse.getAction() == 25) {

				activity.getTransactionUtils().openSubmitAuditFragment(activity,
						activity.getRelativeLayoutContainer(), auditType);

			}
		}
	}



}
