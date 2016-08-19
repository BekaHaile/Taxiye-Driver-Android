package product.clicklabs.jugnoo.driver.selfAudit;

import android.app.Activity;
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

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.NotificationCenterActivity;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
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
	private LinearLayout linearLayoutSelfAudit, linearLayoutNJAutoBranding, linearLayoutNJAutoAudit;
	private TextView textViewSelfAudit, textViewNJAutoBranding, textViewNJAutoAudit;
	private TextView textViewSelfAuditLast, textViewSelfAuditNext, textViewNJBOffer, textViewNJAOffer, textViewNJBNumber, textViewNJANumber,
			textViewSelfAuditStatus, textViewNJAutoBrandingStatus, textViewNJAutoAuditStatus;


	private View rootView;
	private NotificationCenterActivity activity;

	private AuditTypeResponse auditTypeResponse;
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

		activity = (NotificationCenterActivity) getActivity();

		linearLayoutRoot = (LinearLayout) rootView.findViewById(R.id.root);
		new ASSL(activity, linearLayoutRoot, 1134, 720, false);



		linearLayoutSelfAudit = (LinearLayout) rootView.findViewById(R.id.linearLayoutSelfAudit);
		linearLayoutNJAutoBranding = (LinearLayout) rootView.findViewById(R.id.linearLayoutNJAutoBranding);
		linearLayoutNJAutoAudit = (LinearLayout) rootView.findViewById(R.id.linearLayoutNJAutoAudit);


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

				textViewSelfAuditLast.setText(auditTypeResponse.getSaLastAuditString());

				textViewSelfAuditNext.setText(auditTypeResponse.getSaNextAuditString());

				textViewSelfAuditStatus.setText(auditTypeResponse.getSaFlag());

				textViewNJAutoAuditStatus.setText(auditTypeResponse.getNjaStatus());

				textViewNJAutoBrandingStatus.setText(auditTypeResponse.getNjbStatus());

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


	public void getAuditState(final Activity activity, Integer auditType) {
		try {
			if (AppStatus.getInstance(activity).isOnline(activity)) {
				DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));
				HashMap<String, String> params = new HashMap<String, String>();

				params.put("access_token", Data.userData.accessToken);
				params.put("audit_type", String.valueOf(auditType));
				Log.i("params", "=" + params);

				RestClient.getApiServices().sendReferralMessage(params, new Callback<RegisterScreenResponse>() {
					@Override
					public void success(RegisterScreenResponse registerScreenResponse, Response response) {
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						try {
							JSONObject jObj = new JSONObject(responseStr);
							int flag = jObj.getInt("flag");
							if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
								int state = jObj.getInt("state");
								if(state == 1){

								} else if(state == 2){

								} else if(state == 3){

								} else if(state == 4){

								}

								DialogPopup.alertPopup(activity, "", jObj.getString("message"));
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


}
