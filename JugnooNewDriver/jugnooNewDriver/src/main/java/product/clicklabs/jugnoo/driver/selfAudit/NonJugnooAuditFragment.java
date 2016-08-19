package product.clicklabs.jugnoo.driver.selfAudit;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.NotificationCenterActivity;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
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
public class NonJugnooAuditFragment extends Fragment {

	private LinearLayout linearLayoutRoot;
	private LinearLayout etLayout;
	private EditText nameEt, phoneNoEt, vehicleNoEt;
	private Button submitButton;
	private TextView textViewSmartPhoneOption, textViewOptional, textViewNextButton;

	private ImageView imageViewSmartPhoneCheck;


	private View rootView;
	private NotificationCenterActivity activity;

	public NonJugnooAuditFragment(){

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
		rootView = inflater.inflate(R.layout.fragment_non_jugnoo_audit_branding, container, false);

		activity = (NotificationCenterActivity) getActivity();
		linearLayoutRoot = (LinearLayout) rootView.findViewById(R.id.root);
		new ASSL(activity, linearLayoutRoot, 1134, 720, false);


		etLayout = (LinearLayout) rootView.findViewById(R.id.etLayout);
		submitButton = (Button) rootView.findViewById(R.id.submitButton);

		nameEt = (EditText) rootView.findViewById(R.id.nameEt);
		phoneNoEt = (EditText) rootView.findViewById(R.id.phoneNoEt);
		vehicleNoEt = (EditText) rootView.findViewById(R.id.vehicleNoEt);


		textViewSmartPhoneOption = (TextView) rootView.findViewById(R.id.textViewSmartPhoneOption);
		textViewSmartPhoneOption.setTypeface(Data.latoRegular(activity));
		textViewOptional  = (TextView) rootView.findViewById(R.id.textViewOptional);
		textViewOptional.setTypeface(Data.latoRegular(activity));
		textViewNextButton = (TextView) rootView.findViewById(R.id.textViewNextButton);
		textViewNextButton.setTypeface(Data.latoRegular(activity));


		imageViewSmartPhoneCheck = (ImageView) rootView.findViewById(R.id.imageViewSmartPhoneCheck);



		submitButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				submitDriverDetails(activity);
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
			if(activity != null){
				submitDriverDetails(activity);
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}


	public void submitDriverDetails(final Activity activity) {
		try {
			if (AppStatus.getInstance(activity).isOnline(activity)) {
				DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));
				HashMap<String, String> params = new HashMap<String, String>();

				params.put("access_token", Data.userData.accessToken);

				RestClient.getApiServices().sendReferralMessage(params, new Callback<RegisterScreenResponse>() {
					@Override
					public void success(RegisterScreenResponse registerScreenResponse, Response response) {
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						try {
							JSONObject jObj = new JSONObject(responseStr);
							int flag = jObj.getInt("flag");
							if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
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
