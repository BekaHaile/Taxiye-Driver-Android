package product.clicklabs.jugnoo.driver.selfAudit;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.HomeUtil;
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
@SuppressLint("ValidFragment")


public class NonJugnooAuditFragment extends Fragment {

	private LinearLayout linearLayoutRoot;
	private RelativeLayout relativeLayoutNJBOption, relativeLayoutNameEt;
	private LinearLayout etLayout;
	private EditText nameEt, phoneNoEt, vehicleNoEt;
	private Button submitButton;
	private boolean smartPhoneAvailable = false;
	private TextView textViewSmartPhoneOption, textViewOptional, textViewNextButton, textViewTitle;

	private ImageView imageViewSmartPhoneCheckNo, imageViewSmartPhoneCheckYes, imageViewBack;
	private int auditType;


	private View rootView;
	private SelfAuditActivity activity;

	public NonJugnooAuditFragment(int auditType){
		this.auditType = auditType;
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

		activity = (SelfAuditActivity) getActivity();
		linearLayoutRoot = (LinearLayout) rootView.findViewById(R.id.linearLayoutRoot);
		new ASSL(activity, linearLayoutRoot, 1134, 720, false);

		relativeLayoutNJBOption = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutNJBOption);
		relativeLayoutNameEt = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutNameEt);
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

		textViewTitle = (TextView) rootView.findViewById(R.id.textViewTitle);
		textViewTitle.setTypeface(Data.latoRegular(activity));

		imageViewSmartPhoneCheckYes = (ImageView) rootView.findViewById(R.id.imageViewSmartPhoneCheckYes);
		imageViewSmartPhoneCheckNo = (ImageView) rootView.findViewById(R.id.imageViewSmartPhoneCheckNo);

		imageViewBack = (ImageView) rootView.findViewById(R.id.imageViewBack);

		if(auditType == 2){
			relativeLayoutNameEt.setVisibility(View.GONE);
			relativeLayoutNJBOption.setVisibility(View.GONE);
		} else {
			relativeLayoutNameEt.setVisibility(View.VISIBLE);
			relativeLayoutNJBOption.setVisibility(View.VISIBLE);
		}

		nameEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				nameEt.setError(null);

			}
		});

		phoneNoEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				phoneNoEt.setError(null);
			}
		});

		vehicleNoEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				vehicleNoEt.setError(null);
			}
		});

		if(auditType == 0){
			textViewTitle.setText(getResources().getString(R.string.self_audit));
		} else if (auditType == 1){
			textViewTitle.setText(getResources().getString(R.string.non_jugnoo_auto_branding));
		} else if (auditType == 2){
			textViewTitle.setText(getResources().getString(R.string.non_jugnoo_auto_audit));
		}

		imageViewSmartPhoneCheckNo.setImageResource(R.drawable.radio_select);
		imageViewSmartPhoneCheckYes.setImageResource(R.drawable.radio_unslelcet);

		imageViewSmartPhoneCheckNo.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				imageViewSmartPhoneCheckNo.setImageResource(R.drawable.radio_select);
				imageViewSmartPhoneCheckYes.setImageResource(R.drawable.radio_unslelcet);
				smartPhoneAvailable = false;
			}
		});

		imageViewSmartPhoneCheckYes.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				imageViewSmartPhoneCheckYes.setImageResource(R.drawable.radio_select);
				imageViewSmartPhoneCheckNo.setImageResource(R.drawable.radio_unslelcet);
				smartPhoneAvailable = true;
			}
		});

		imageViewBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				performBackPress();
			}
		});

		submitButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String name ="";
				if(auditType == 1) {
					 name = nameEt.getText().toString().trim();
					if (name.length() > 0) {
						name = name.substring(0, 1).toUpperCase() + name.substring(1, name.length());
					}
				}

				String autoNum = vehicleNoEt.getText().toString().trim();
				String phoneNo = phoneNoEt.getText().toString().trim();

				if ("".equalsIgnoreCase(name) && auditType==1 ) {
					nameEt.requestFocus();
					nameEt.setError("Please enter name");
				} else {
					if ("".equalsIgnoreCase(phoneNo)) {
						phoneNoEt.requestFocus();
						phoneNoEt.setError("Please enter phone number");
					} else {
						if ("".equalsIgnoreCase(autoNum)) {
							vehicleNoEt.requestFocus();
							vehicleNoEt.setError("Please enter auto number");
						} else {
							phoneNo = phoneNo.replace(" ", "");
							phoneNo = phoneNo.replace("(", "");
							phoneNo = phoneNo.replace("/", "");
							phoneNo = phoneNo.replace(")", "");
							phoneNo = phoneNo.replace("N", "");
							phoneNo = phoneNo.replace(",", "");
							phoneNo = phoneNo.replace("*", "");
							phoneNo = phoneNo.replace(";", "");
							phoneNo = phoneNo.replace("#", "");
							phoneNo = phoneNo.replace("-", "");
							phoneNo = phoneNo.replace(".", "");

							if (phoneNo.length() >= 10) {
								phoneNo = phoneNo.substring(phoneNo.length() - 10, phoneNo.length());
								if (phoneNo.charAt(0) == '0' || phoneNo.charAt(0) == '1' || phoneNo.contains("+")) {
									phoneNoEt.requestFocus();
									phoneNoEt.setError("Please enter valid phone number");
								} else {
									phoneNo = "+91" + phoneNo;
									if (isPhoneValid(phoneNo)) {
										submitDriverDetails(name, phoneNo, autoNum);
									} else {
										phoneNoEt.requestFocus();
										phoneNoEt.setError("Please enter valid phone number");
									}
								}
							} else {
								phoneNoEt.requestFocus();
								phoneNoEt.setError("Please enter valid phone number");
							}
						}

					}
				}
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
			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	public void performBackPress(){
		activity.getTransactionUtils().openSelectAuditFragment(activity,
				activity.getRelativeLayoutContainer());
	}

	public void submitDriverDetails(String name, String phone, String autoNum) {
		try {
			if (AppStatus.getInstance(activity).isOnline(activity)) {
				DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));
				HashMap<String, String> params = new HashMap<String, String>();

				params.put("access_token", Data.userData.accessToken);
				params.put("name", name);
				params.put("phone_no", phone);
				params.put("vehicle_no", autoNum);
				params.put("audit_type", String.valueOf(auditType));
				params.put("smart_phone_available", String.valueOf(smartPhoneAvailable));
				HomeUtil.putDefaultParams(params);

				RestClient.getApiServices().sendAuditDetails(params, new Callback<RegisterScreenResponse>() {
					@Override
					public void success(RegisterScreenResponse registerScreenResponse, Response response) {
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						try {
							JSONObject jObj = new JSONObject(responseStr);
							int flag = jObj.getInt("flag");
							if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
								activity.getTransactionUtils().openAuditCameraFragment(activity,
										activity.getRelativeLayoutContainer(), 0, auditType, 0);
							} else {
								DialogPopup.alertPopupWithImageListener(activity, "", jObj.getString("message"), R.drawable.error_icon_for_popup, new View.OnClickListener() {
									@Override
									public void onClick(View v) {

									}
								}, false);
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

	boolean isPhoneValid(CharSequence phone) {
		return android.util.Patterns.PHONE.matcher(phone).matches();
	}

}
