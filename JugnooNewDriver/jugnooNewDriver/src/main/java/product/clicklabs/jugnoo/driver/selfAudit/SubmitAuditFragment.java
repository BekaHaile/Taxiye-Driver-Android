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

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RoundBorderTransform;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.NotificationCenterActivity;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.dodo.datastructure.DeliveryStatus;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.AuditStateResponse;
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
public class SubmitAuditFragment extends Fragment {

	private LinearLayout linearLayoutRoot;
	private LinearLayout etLayout, linearLayoutCameraStand;
	private EditText nameEt, phoneNoEt, vehicleNoEt;
	private Button submitButton;
	private TextView textViewFront, textViewBack, textViewLeft, textViewRight, textViewCameraStand, textViewRetryCameraStand,
			textViewRetryFront, textViewRetryBack, textViewRetryLeft, textViewRetryRight, textViewTitle;

	private ImageView imageIconFront, setCapturedImageFront, imageIconBack, setCapturedImageback, imageIconLeft, setCapturedImageLeft,
			imageIconRight, setCapturedImageRight, imageIconCameraStand, setCapturedImageCameraStand;

	private int auditType;
	private View rootView;
	private AuditStateResponse auditStateResponse;
	private NotificationCenterActivity activity;

	public SubmitAuditFragment( int auditType){
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
		rootView = inflater.inflate(R.layout.fragment_audit_submission, container, false);

		activity = (NotificationCenterActivity) getActivity();

		linearLayoutRoot = (LinearLayout) rootView.findViewById(R.id.root);
		new ASSL(activity, linearLayoutRoot, 1134, 720, false);



		etLayout = (LinearLayout) rootView.findViewById(R.id.etLayout);
		linearLayoutCameraStand = (LinearLayout) rootView.findViewById(R.id.linearLayoutCameraStand);
		submitButton = (Button) rootView.findViewById(R.id.submitButton);

		nameEt = (EditText) rootView.findViewById(R.id.nameEt);
		phoneNoEt = (EditText) rootView.findViewById(R.id.phoneNoEt);
		vehicleNoEt = (EditText) rootView.findViewById(R.id.vehicleNoEt);

		textViewTitle = (TextView) rootView.findViewById(R.id.textViewTitle);
		textViewTitle.setTypeface(Data.latoRegular(activity));
		textViewFront = (TextView) rootView.findViewById(R.id.textViewFront);
		textViewFront.setTypeface(Data.latoRegular(activity));
		textViewBack  = (TextView) rootView.findViewById(R.id.textViewBack);
		textViewBack.setTypeface(Data.latoRegular(activity));
		textViewLeft = (TextView) rootView.findViewById(R.id.textViewLeft);
		textViewLeft.setTypeface(Data.latoRegular(activity));
		textViewRight = (TextView) rootView.findViewById(R.id.textViewRight);
		textViewRight.setTypeface(Data.latoRegular(activity));
		textViewCameraStand = (TextView) rootView.findViewById(R.id.textViewCameraStand);
		textViewCameraStand.setTypeface(Data.latoRegular(activity));
		textViewRetryFront = (TextView) rootView.findViewById(R.id.textViewRetryFront);
		textViewRetryFront.setTypeface(Data.latoRegular(activity));
		textViewRetryBack = (TextView) rootView.findViewById(R.id.textViewRetryBack);
		textViewRetryBack.setTypeface(Data.latoRegular(activity));
		textViewRetryLeft = (TextView) rootView.findViewById(R.id.textViewRetryLeft);
		textViewRetryLeft.setTypeface(Data.latoRegular(activity));
		textViewRetryRight = (TextView) rootView.findViewById(R.id.textViewRetryRight);
		textViewRetryRight.setTypeface(Data.latoRegular(activity));
		textViewRetryCameraStand = (TextView) rootView.findViewById(R.id.textViewRetryCameraStand);
		textViewRetryCameraStand.setTypeface(Data.latoRegular(activity));


		imageIconFront = (ImageView) rootView.findViewById(R.id.image_icon_front);
		setCapturedImageFront = (ImageView) rootView.findViewById(R.id.setCapturedImageFront);
		imageIconBack = (ImageView) rootView.findViewById(R.id.image_icon_back);
		setCapturedImageback = (ImageView) rootView.findViewById(R.id.setCapturedImageBack);
		imageIconLeft = (ImageView) rootView.findViewById(R.id.image_icon_left);
		setCapturedImageLeft = (ImageView) rootView.findViewById(R.id.setCapturedImageLeft);
		imageIconRight = (ImageView) rootView.findViewById(R.id.image_icon_Right);
		setCapturedImageRight = (ImageView) rootView.findViewById(R.id.setCapturedImageRight);
		imageIconCameraStand = (ImageView) rootView.findViewById(R.id.image_icon_CameraStand);
		setCapturedImageCameraStand = (ImageView) rootView.findViewById(R.id.setCapturedImageCameraStand);

		if(auditType == 0){
			etLayout.setVisibility(View.GONE);
		}

		submitButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				submitAuditStatus(activity);
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
			if(activity != null && auditStateResponse!=null){

				for(int i=0; i<auditStateResponse.getImages().size(); i++){

					if(auditStateResponse.getImages().get(i).getImageType() == 0){
						if("".equalsIgnoreCase(auditStateResponse.getImages().get(i).getImageUrl())){
							textViewRetryFront.setVisibility(View.VISIBLE);
							imageIconFront.setImageResource(R.drawable.retry_icon_black);
						} else {
							Picasso.with(getActivity()).load(auditStateResponse.getImages().get(i).getImageUrl())
									.transform(new RoundBorderTransform()).resize(300, 300).centerCrop()
									//.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
									.into(setCapturedImageFront);
							textViewRetryFront.setVisibility(View.GONE);
							imageIconFront.setImageResource(R.drawable.retry_icon_white);

						}
					} else if(auditStateResponse.getImages().get(i).getImageType() == 1){
						if("".equalsIgnoreCase(auditStateResponse.getImages().get(i).getImageUrl())){
							textViewRetryBack.setVisibility(View.VISIBLE);
							imageIconBack.setImageResource(R.drawable.retry_icon_black);
						} else {
							Picasso.with(getActivity()).load(auditStateResponse.getImages().get(i).getImageUrl())
									.transform(new RoundBorderTransform()).resize(300, 300).centerCrop()
									//.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
									.into(setCapturedImageback);
							textViewRetryBack.setVisibility(View.GONE);
							imageIconBack.setImageResource(R.drawable.retry_icon_white);

						}
					} else if(auditStateResponse.getImages().get(i).getImageType() == 2){
						if("".equalsIgnoreCase(auditStateResponse.getImages().get(i).getImageUrl())){
							textViewRetryLeft.setVisibility(View.VISIBLE);
							imageIconLeft.setImageResource(R.drawable.retry_icon_black);
						} else {
							Picasso.with(getActivity()).load(auditStateResponse.getImages().get(i).getImageUrl())
									.transform(new RoundBorderTransform()).resize(300, 300).centerCrop()
									//.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
									.into(setCapturedImageLeft);
							textViewRetryLeft.setVisibility(View.GONE);
							imageIconLeft.setImageResource(R.drawable.retry_icon_white);

						}
					} else if(auditStateResponse.getImages().get(i).getImageType() == 3){
						if("".equalsIgnoreCase(auditStateResponse.getImages().get(i).getImageUrl())){
							textViewRetryRight.setVisibility(View.VISIBLE);
							imageIconRight.setImageResource(R.drawable.retry_icon_black);
						} else {
							Picasso.with(getActivity()).load(auditStateResponse.getImages().get(i).getImageUrl())
									.transform(new RoundBorderTransform()).resize(300, 300).centerCrop()
									//.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
									.into(setCapturedImageRight);
							textViewRetryRight.setVisibility(View.GONE);
							imageIconRight.setImageResource(R.drawable.retry_icon_white);

						}
					} else if(auditStateResponse.getImages().get(i).getImageType() == 4){
						if("".equalsIgnoreCase(auditStateResponse.getImages().get(i).getImageUrl())){
							textViewRetryCameraStand.setVisibility(View.VISIBLE);
							imageIconCameraStand.setImageResource(R.drawable.retry_icon_black);
						} else {
							Picasso.with(getActivity()).load(auditStateResponse.getImages().get(i).getImageUrl())
									.transform(new RoundBorderTransform()).resize(300, 300).centerCrop()
									//.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
									.into(setCapturedImageCameraStand);
							textViewRetryCameraStand.setVisibility(View.GONE);
							imageIconCameraStand.setImageResource(R.drawable.retry_icon_white);

						}
					}
				}

			}
		} catch(Exception e){
			e.printStackTrace();
		}
	}


	public void submitAuditStatus(final Activity activity) {
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


	public void editNonJugnooInfo(final Activity activity, Integer auditType) {
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
								SubmitAuditFragment.this.auditStateResponse = auditStateResponse;
								SelfAuditActivity selfAuditActivity = new SelfAuditActivity();
								selfAuditActivity.setAuditStateResponse(auditStateResponse);

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
