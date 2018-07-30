package product.clicklabs.jugnoo.driver.selfAudit;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RoundBorderTransform;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.HomeActivity;
import product.clicklabs.jugnoo.driver.HomeUtil;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.AuditStateResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.PermissionCommon;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by aneeshbansal on 18/08/16.
 */

@SuppressLint("ValidFragment")

public class SubmitAuditFragment extends Fragment {

	private LinearLayout linearLayoutRoot;
	private LinearLayout etLayout;
	private RelativeLayout relativeLayoutVehicleNo, relativeLayoutFront, relativeLayoutBack, relativeLayoutLeft,
			relativeLayoutRight, relativeLayoutCameraStand, linearLayoutCameraStand;
	private EditText nameEt, phoneNoEt, vehicleNoEt;
	private Button submitButton;
	private TextView textViewFront, textViewBack, textViewLeft, textViewRight, textViewCameraStand, textViewRetryCameraStand,
			textViewRetryFront, textViewRetryBack, textViewRetryLeft, textViewRetryRight, title, textViewStatusFront,
			textViewStatusBack, textViewStatusLeft, textViewStatusRight, textViewStatusMobileStand;

	private ImageView imageIconFront, setCapturedImageFront, imageIconBack, setCapturedImageback, imageIconLeft, setCapturedImageLeft,
			imageIconRight, setCapturedImageRight, imageIconCameraStand, setCapturedImageCameraStand, backBtn, deleteImageFront,
			deleteImageBack, deleteImageLeft, deleteImageRight, deleteImageMobileStand, imageViewBin;

	private int auditType;
	private View rootView;
	private AuditStateResponse auditStateResponse;
	private SelfAuditActivity activity;
	private PermissionCommon permissionCommon;

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

		activity = (SelfAuditActivity) getActivity();
		permissionCommon = new PermissionCommon(this);

		linearLayoutRoot = (LinearLayout) rootView.findViewById(R.id.linearLayoutRoot);
		new ASSL(activity, linearLayoutRoot, 1134, 720, false);



		etLayout = (LinearLayout) rootView.findViewById(R.id.etLayout);
		linearLayoutCameraStand = (RelativeLayout) rootView.findViewById(R.id.linearLayoutCameraStand);

		relativeLayoutFront = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutFront);
		relativeLayoutBack = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutBack);
		relativeLayoutLeft = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutLeft);
		relativeLayoutRight = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutRight);
		relativeLayoutCameraStand = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutCameraStand);

		relativeLayoutVehicleNo = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutVehicleNo);

		submitButton = (Button) rootView.findViewById(R.id.submitButton);

		nameEt = (EditText) rootView.findViewById(R.id.nameEt);
		phoneNoEt = (EditText) rootView.findViewById(R.id.phoneNoEt);
		vehicleNoEt = (EditText) rootView.findViewById(R.id.vehicleNoEt);

		title = (TextView) rootView.findViewById(R.id.title);
		title.setTypeface(Fonts.mavenRegular(activity));
		title.setText(R.string.Audit);
		title.setAllCaps(false);
		textViewFront = (TextView) rootView.findViewById(R.id.textViewFront);
		textViewFront.setTypeface(Fonts.mavenRegular(activity));
		textViewBack  = (TextView) rootView.findViewById(R.id.textViewBack);
		textViewBack.setTypeface(Fonts.mavenRegular(activity));
		textViewLeft = (TextView) rootView.findViewById(R.id.textViewLeft);
		textViewLeft.setTypeface(Fonts.mavenRegular(activity));
		textViewRight = (TextView) rootView.findViewById(R.id.textViewRight);
		textViewRight.setTypeface(Fonts.mavenRegular(activity));
		textViewCameraStand = (TextView) rootView.findViewById(R.id.textViewCameraStand);
		textViewCameraStand.setTypeface(Fonts.mavenRegular(activity));
		textViewRetryFront = (TextView) rootView.findViewById(R.id.textViewRetryFront);
		textViewRetryFront.setTypeface(Fonts.mavenRegular(activity));
		textViewRetryBack = (TextView) rootView.findViewById(R.id.textViewRetryBack);
		textViewRetryBack.setTypeface(Fonts.mavenRegular(activity));
		textViewRetryLeft = (TextView) rootView.findViewById(R.id.textViewRetryLeft);
		textViewRetryLeft.setTypeface(Fonts.mavenRegular(activity));
		textViewRetryRight = (TextView) rootView.findViewById(R.id.textViewRetryRight);
		textViewRetryRight.setTypeface(Fonts.mavenRegular(activity));
		textViewRetryCameraStand = (TextView) rootView.findViewById(R.id.textViewRetryCameraStand);
		textViewRetryCameraStand.setTypeface(Fonts.mavenRegular(activity));


		textViewStatusFront = (TextView) rootView.findViewById(R.id.textViewStatusFront);
		textViewStatusFront.setTypeface(Fonts.mavenRegular(activity));
		textViewStatusBack = (TextView) rootView.findViewById(R.id.textViewStatusBack);
		textViewStatusBack.setTypeface(Fonts.mavenRegular(activity));
		textViewStatusLeft = (TextView) rootView.findViewById(R.id.textViewStatusLeft);
		textViewStatusLeft.setTypeface(Fonts.mavenRegular(activity));
		textViewStatusRight = (TextView) rootView.findViewById(R.id.textViewStatusRight);
		textViewStatusRight.setTypeface(Fonts.mavenRegular(activity));
		textViewStatusMobileStand = (TextView) rootView.findViewById(R.id.textViewStatusMobileStand);
		textViewStatusMobileStand.setTypeface(Fonts.mavenRegular(activity));


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

		deleteImageFront = (ImageView) rootView.findViewById(R.id.deleteImageFront);
		deleteImageBack = (ImageView) rootView.findViewById(R.id.deleteImageBack);
		deleteImageLeft = (ImageView) rootView.findViewById(R.id.deleteImageLeft);
		deleteImageRight = (ImageView) rootView.findViewById(R.id.deleteImageRight);
		deleteImageMobileStand = (ImageView) rootView.findViewById(R.id.deleteImageMobileStand);

		backBtn = (ImageView) rootView.findViewById(R.id.backBtn);
		imageViewBin = (ImageView) rootView.findViewById(R.id.imageViewBin);
		imageViewBin.getDrawable().setColorFilter(ContextCompat.getColor(activity, R.color.text_color), PorterDuff.Mode.SRC_ATOP);

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


		vehicleNoEt.setEnabled(false);

		vehicleNoEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				vehicleNoEt.setError(null);
			}
		});

		if(auditType == 0){
			title.setText(getResources().getString(R.string.self_audit));
		} else if (auditType == 1){
			title.setText(getResources().getString(R.string.non_jugnoo_auto_branding, getString(R.string.appname)));
		} else if (auditType == 2){
			title.setText(getResources().getString(R.string.non_jugnoo_auto_audit, getString(R.string.appname)));
		}

		nameEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				String nameChanged = nameEt.getText().toString().trim();
				if ("".equalsIgnoreCase(nameChanged)) {
					nameEt.requestFocus();
					nameEt.setError(getResources().getString(R.string.name_cant_empty));
				} else {
					if (auditStateResponse != null) {
						if (auditStateResponse.getNjbPhoneNo().equalsIgnoreCase(nameChanged)) {
							nameEt.requestFocus();
							nameEt.setError(getResources().getString(R.string.changed_no_same_as_previous));
						} else {
							submitDriverDetails(nameChanged, phoneNoEt.getText().toString(), vehicleNoEt.getText().toString());
						}
					} else {
						submitDriverDetails(nameChanged, phoneNoEt.getText().toString(), vehicleNoEt.getText().toString());
					}
				}
				return true;
			}
		});

		phoneNoEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				String phoneChanged = phoneNoEt.getText().toString().trim();
				if ("".equalsIgnoreCase(phoneChanged)) {
					phoneNoEt.requestFocus();
					phoneNoEt.setError(getResources().getString(R.string.phone_no_cnt_be_empty));
				} else {
					phoneChanged = Utils.retrievePhoneNumberTenChars(Utils.getCountryCode(activity), phoneChanged);
					if (Utils.validPhoneNumber(phoneChanged)) {
						phoneChanged = Utils.getCountryCode(activity) + phoneChanged;
						if (auditStateResponse != null) {
							if(auditStateResponse.getNjbPhoneNo().equalsIgnoreCase(phoneChanged)) {
								phoneNoEt.requestFocus();
								phoneNoEt.setError(getResources().getString(R.string.changed_no_same_as_previous));
							}
							else {
								submitDriverDetails(nameEt.getText().toString(), phoneChanged, vehicleNoEt.getText().toString());
							}
						} else {
							submitDriverDetails(nameEt.getText().toString(), phoneChanged, vehicleNoEt.getText().toString());
						}
					} else {
						phoneNoEt.requestFocus();
						phoneNoEt.setError(getResources().getString(R.string.enter_valid_phone_number));
					}
				}
				return true;
			}
		});

		vehicleNoEt.setOnEditorActionListener(new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				String vehicleNoChanged = vehicleNoEt.getText().toString().trim();
				if ("".equalsIgnoreCase(vehicleNoChanged)) {
					vehicleNoEt.requestFocus();
					vehicleNoEt.setError(getResources().getString(R.string.phone_no_cnt_be_empty));
				} else {
					if (auditStateResponse != null) {
						if (auditStateResponse.getNjbPhoneNo().equalsIgnoreCase(vehicleNoChanged)) {
							vehicleNoEt.requestFocus();
							vehicleNoEt.setError(getResources().getString(R.string.changed_no_same_as_previous));
						} else {
							submitDriverDetails(nameEt.getText().toString(), phoneNoEt.getText().toString(), vehicleNoChanged);
						}
					} else {
						submitDriverDetails(nameEt.getText().toString(), phoneNoEt.getText().toString(), vehicleNoChanged);
					}
				}
				return true;
			}
		});

			relativeLayoutFront.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (auditStateResponse.getImages().get(0).getImageStatus() != 10) {
						if (auditStateResponse != null && !auditStateResponse.getImages().get(0).getRejectionReason().equalsIgnoreCase("")) {
							DialogPopup.alertPopupWithListener(activity, "",
									auditStateResponse.getImages().get(0).getRejectionReason(), new View.OnClickListener() {
										@Override
										public void onClick(View v) {
											openCamera(0);
										}
									});
						} else {
							DialogPopup.alertPopupTwoButtonsWithListeners(activity, "", getResources().getString(R.string.deletion_confirmation),
									getResources().getString(R.string.yes), getResources().getString(R.string.no), new View.OnClickListener() {
										@Override
										public void onClick(View v) {
											openCamera(0);
										}
									}, new View.OnClickListener() {
										@Override
										public void onClick(View v) {

										}
									}, true, false);
						}
					}
				}
			});


			relativeLayoutBack.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(auditStateResponse.getImages().get(1).getImageStatus() != 10) {
						if (auditStateResponse != null && !auditStateResponse.getImages().get(1).getRejectionReason().equalsIgnoreCase("")) {
							DialogPopup.alertPopupWithListener(activity, "",
									auditStateResponse.getImages().get(1).getRejectionReason(), new View.OnClickListener() {
										@Override
										public void onClick(View v) {
											openCamera(1);
										}
									});
						} else {
							DialogPopup.alertPopupTwoButtonsWithListeners(activity, "", getResources().getString(R.string.deletion_confirmation),
									getResources().getString(R.string.yes), getResources().getString(R.string.no), new View.OnClickListener() {
										@Override
										public void onClick(View v) {
											openCamera(1);
										}
									}, new View.OnClickListener() {
										@Override
										public void onClick(View v) {

										}
									}, true, false);
						}
					}
				}
			});



			relativeLayoutLeft.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(auditStateResponse.getImages().get(2).getImageStatus() != 10) {
						if (auditStateResponse != null && !auditStateResponse.getImages().get(2).getRejectionReason().equalsIgnoreCase("")) {
							DialogPopup.alertPopupWithListener(activity, "",
									auditStateResponse.getImages().get(2).getRejectionReason(), new View.OnClickListener() {
										@Override
										public void onClick(View v) {
											openCamera(2);
										}
									});
						} else {
							DialogPopup.alertPopupTwoButtonsWithListeners(activity, "", getResources().getString(R.string.deletion_confirmation),
									getResources().getString(R.string.yes), getResources().getString(R.string.no), new View.OnClickListener() {
										@Override
										public void onClick(View v) {
											openCamera(2);
										}
									}, new View.OnClickListener() {
										@Override
										public void onClick(View v) {

										}
									}, true, false);
						}
					}
				}
			});



			relativeLayoutRight.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(auditStateResponse.getImages().get(3).getImageStatus() != 10) {
						if (auditStateResponse != null && !auditStateResponse.getImages().get(3).getRejectionReason().equalsIgnoreCase("")) {
							DialogPopup.alertPopupWithListener(activity, "",
									auditStateResponse.getImages().get(3).getRejectionReason(), new View.OnClickListener() {
										@Override
										public void onClick(View v) {
											openCamera(3);
										}
									});
						} else {
							DialogPopup.alertPopupTwoButtonsWithListeners(activity, "", getResources().getString(R.string.deletion_confirmation),
									getResources().getString(R.string.yes), getResources().getString(R.string.no), new View.OnClickListener() {
										@Override
										public void onClick(View v) {
											openCamera(3);
										}
									}, new View.OnClickListener() {
										@Override
										public void onClick(View v) {

										}
									}, true, false);
						}
					}
				}
			});



			relativeLayoutCameraStand.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(auditStateResponse.getImages().get(4).getImageStatus() != 10) {
						if (auditStateResponse != null && !auditStateResponse.getImages().get(4).getRejectionReason().equalsIgnoreCase("")) {
							DialogPopup.alertPopupWithListener(activity, "",
									auditStateResponse.getImages().get(4).getRejectionReason(), new View.OnClickListener() {
										@Override
										public void onClick(View v) {
											openCamera(4);
										}
									});
						} else {
							DialogPopup.alertPopupTwoButtonsWithListeners(activity, "", getResources().getString(R.string.deletion_confirmation),
									getResources().getString(R.string.yes), getResources().getString(R.string.no), new View.OnClickListener() {
										@Override
										public void onClick(View v) {
											openCamera(4);
										}
									}, new View.OnClickListener() {
										@Override
										public void onClick(View v) {

										}
									}, true, false);
						}
					}
				}
			});



		imageViewBin.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogPopup.alertPopupTwoButtonsWithListeners(activity, "", getResources().getString(R.string.cancel_audit),
						getResources().getString(R.string.yes), getResources().getString(R.string.no), new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								deleteCurrentAudit();
							}
						}, new View.OnClickListener() {
							@Override
							public void onClick(View v) {

							}
						}, true, false);
			}
		});

		backBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				performBackPress();
			}
		});

		if (auditType == 0){
			etLayout.setVisibility(View.GONE);
		} else if (auditType == 1){
			etLayout.setVisibility(View.VISIBLE);
		} else if (auditType == 2){
			etLayout.setVisibility(View.GONE);
		}

		submitButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				submitAuditStatus(activity);
			}
		});

		getAuditState(activity, auditType);
		return rootView;
	}

	public void openCamera(final int imageType){
		permissionCommon.setCallback(new PermissionCommon.PermissionListener() {
			@Override
			public void permissionGranted(int requestCode) {
				activity.getTransactionUtils().openAuditCameraFragment(activity,
						activity.getRelativeLayoutContainer(), imageType, auditType, 1);
			}

			@Override
			public boolean permissionDenied(int requestCode, boolean neverAsk) {
				return true;
			}

			@Override
			public void onRationalRequestIntercepted() {

			}
		}).getPermission(101, Manifest.permission.CAMERA);
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
		activity.getTransactionUtils().openSelectAuditFragment(activity,
				activity.getRelativeLayoutContainer());
	}


	public void update(){
		try{
			if(activity != null && auditStateResponse!=null){

				if(!"".equalsIgnoreCase(auditStateResponse.getNjbName())){
					nameEt.setText(auditStateResponse.getNjbName());
				}

				if(!"".equalsIgnoreCase(auditStateResponse.getNjbPhoneNo())){
					phoneNoEt.setText(auditStateResponse.getNjbPhoneNo());

				}

				if(!"".equalsIgnoreCase(auditStateResponse.getNjbVehicleNo())){
					vehicleNoEt.setText(auditStateResponse.getNjbVehicleNo());
				}


				for(int i=0; i<auditStateResponse.getImages().size(); i++){

					if(auditStateResponse.getImages().get(i).getImageType() == 0){
						if("".equalsIgnoreCase(auditStateResponse.getImages().get(i).getImageUrl())){
							textViewRetryFront.setVisibility(View.VISIBLE);
							imageIconFront.setImageResource(R.drawable.retry_icon_black);
							if(auditStateResponse.getImages().get(i).getImageStatus() == 5){
								textViewStatusFront.setText(getResources().getString(R.string.rejected));
								textViewStatusFront.setTextColor(getResources().getColor(R.color.red_delivery));
								textViewStatusFront.setVisibility(View.VISIBLE);
							}
						} else {
							Picasso.with(getActivity()).load(auditStateResponse.getImages().get(i).getImageUrl())
									.transform(new RoundBorderTransform()).resize(300, 300).centerCrop()
									//.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
									.into(setCapturedImageFront);
							textViewRetryFront.setVisibility(View.GONE);
//							imageIconFront.bringToFront();
//							imageIconFront.setImageResource(R.drawable.retry_icon_white);
							if(auditStateResponse.getImages().get(i).getImageStatus() == 10){
								textViewStatusFront.setText(getResources().getString(R.string.verified));
								textViewStatusFront.setTextColor(getResources().getColor(R.color.green_delivery));
								textViewStatusFront.setVisibility(View.VISIBLE);
								deleteImageFront.setVisibility(View.GONE);
							}

						}
					} else if(auditStateResponse.getImages().get(i).getImageType() == 1){
						if("".equalsIgnoreCase(auditStateResponse.getImages().get(i).getImageUrl())){
							textViewRetryBack.setVisibility(View.VISIBLE);
							imageIconBack.setImageResource(R.drawable.retry_icon_black);
							if(auditStateResponse.getImages().get(i).getImageStatus() == 5){
								textViewStatusBack.setText(getResources().getString(R.string.rejected));
								textViewStatusBack.setTextColor(getResources().getColor(R.color.red_delivery));
								textViewStatusBack.setVisibility(View.VISIBLE);
							}
						} else {
							Picasso.with(getActivity()).load(auditStateResponse.getImages().get(i).getImageUrl())
									.transform(new RoundBorderTransform()).resize(300, 300).centerCrop()
									//.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
									.into(setCapturedImageback);
							textViewRetryBack.setVisibility(View.GONE);
//							imageIconBack.bringToFront();
//							imageIconBack.setImageResource(R.drawable.retry_icon_white);
							if(auditStateResponse.getImages().get(i).getImageStatus() == 10){
								textViewStatusBack.setText(getResources().getString(R.string.verified));
								textViewStatusBack.setTextColor(getResources().getColor(R.color.green_delivery));
								textViewStatusBack.setVisibility(View.VISIBLE);
								deleteImageBack.setVisibility(View.GONE);
							}

						}
					} else if(auditStateResponse.getImages().get(i).getImageType() == 2){
						if("".equalsIgnoreCase(auditStateResponse.getImages().get(i).getImageUrl())){
							textViewRetryLeft.setVisibility(View.VISIBLE);
							imageIconLeft.setImageResource(R.drawable.retry_icon_black);
							if(auditStateResponse.getImages().get(i).getImageStatus() == 5){
								textViewStatusLeft.setText(getResources().getString(R.string.rejected));
								textViewStatusLeft.setTextColor(getResources().getColor(R.color.red_delivery));
								textViewStatusLeft.setVisibility(View.VISIBLE);
							}
						} else {
							Picasso.with(getActivity()).load(auditStateResponse.getImages().get(i).getImageUrl())
									.transform(new RoundBorderTransform()).resize(300, 300).centerCrop()
									//.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
									.into(setCapturedImageLeft);
							textViewRetryLeft.setVisibility(View.GONE);
//							imageIconLeft.bringToFront();
//							imageIconLeft.setImageResource(R.drawable.retry_icon_white);
							if(auditStateResponse.getImages().get(i).getImageStatus() == 10){
								textViewStatusLeft.setText(getResources().getString(R.string.verified));
								textViewStatusLeft.setTextColor(getResources().getColor(R.color.green_delivery));
								textViewStatusLeft.setVisibility(View.VISIBLE);
								deleteImageLeft.setVisibility(View.GONE);
							}

						}
					} else if(auditStateResponse.getImages().get(i).getImageType() == 3){
						if("".equalsIgnoreCase(auditStateResponse.getImages().get(i).getImageUrl())){
							textViewRetryRight.setVisibility(View.VISIBLE);
							imageIconRight.setImageResource(R.drawable.retry_icon_black);
							if(auditStateResponse.getImages().get(i).getImageStatus() == 5){
								textViewStatusRight.setText(getResources().getString(R.string.rejected));
								textViewStatusRight.setTextColor(getResources().getColor(R.color.red_delivery));
								textViewStatusRight.setVisibility(View.VISIBLE);
							}
						} else {
							Picasso.with(getActivity()).load(auditStateResponse.getImages().get(i).getImageUrl())
									.transform(new RoundBorderTransform()).resize(300, 300).centerCrop()
									//.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
									.into(setCapturedImageRight);
							textViewRetryRight.setVisibility(View.GONE);
//							imageIconRight.bringToFront();
//							imageIconRight.setImageResource(R.drawable.retry_icon_white);
							if(auditStateResponse.getImages().get(i).getImageStatus() == 10){
								textViewStatusRight.setText(getResources().getString(R.string.verified));
								textViewStatusRight.setTextColor(getResources().getColor(R.color.green_delivery));
								textViewStatusRight.setVisibility(View.VISIBLE);
								deleteImageRight.setVisibility(View.GONE);
							}

						}
					} else if(auditStateResponse.getImages().get(i).getImageType() == 4){
						if("".equalsIgnoreCase(auditStateResponse.getImages().get(i).getImageUrl())){
							textViewRetryCameraStand.setVisibility(View.VISIBLE);
							imageIconCameraStand.setImageResource(R.drawable.retry_icon_black);
							if(auditStateResponse.getImages().get(i).getImageStatus() == 5){
								textViewStatusMobileStand.setText(getResources().getString(R.string.rejected));
								textViewStatusMobileStand.setTextColor(getResources().getColor(R.color.red_delivery));
								textViewStatusMobileStand.setVisibility(View.VISIBLE);
							}
						} else {
							Picasso.with(getActivity()).load(auditStateResponse.getImages().get(i).getImageUrl())
									.transform(new RoundBorderTransform()).resize(300, 300).centerCrop()
									//.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
									.into(setCapturedImageCameraStand);
							textViewRetryCameraStand.setVisibility(View.GONE);
//							imageIconCameraStand.bringToFront();
//							imageIconCameraStand.setImageResource(R.drawable.retry_icon_white);
							if(auditStateResponse.getImages().get(i).getImageStatus() == 10){
								textViewStatusMobileStand.setText(getResources().getString(R.string.verified));
								textViewStatusMobileStand.setTextColor(getResources().getColor(R.color.green_delivery));
								textViewStatusMobileStand.setVisibility(View.VISIBLE);
								deleteImageMobileStand.setVisibility(View.GONE);
							}

						}
					}
				}

				if(auditStateResponse.getImages().size() < 5){
					linearLayoutCameraStand.setVisibility(View.GONE);
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
				params.put("audit_type", String.valueOf(auditType));
				HomeUtil.putDefaultParams(params);

				RestClient.getApiServices().submitAuditImages(params, new Callback<RegisterScreenResponse>() {
					@Override
					public void success(RegisterScreenResponse registerScreenResponse, Response response) {
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						try {
							JSONObject jObj = new JSONObject(responseStr);
							int flag = jObj.getInt("flag");
							if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
								DialogPopup.alertPopup(activity, "", jObj.getString("message"));
								DialogPopup.alertPopupWithImageListener(activity, "", jObj.getString("message"),
										R.drawable.success_icon_for_popup, new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										Intent intent = new Intent(activity, HomeActivity.class);
										activity.finish();
										startActivity(intent);
									}
								}, false);
							} else {
								DialogPopup.alertPopup(activity, "", jObj.getString("message"));
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
				HomeUtil.putDefaultParams(params);
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
				HomeUtil.putDefaultParams(params);

				RestClient.getApiServices().sendAuditDetails(params, new Callback<RegisterScreenResponse>() {
					@Override
					public void success(RegisterScreenResponse registerScreenResponse, Response response) {
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						try {
							JSONObject jObj = new JSONObject(responseStr);
							int flag = jObj.getInt("flag");
							if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
								DialogPopup.alertPopup(activity, "", jObj.getString("message"));
							} else {
								DialogPopup.alertPopup(activity, "", jObj.getString("message"));
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


	public void deleteCurrentAudit() {
		try {
			if (AppStatus.getInstance(activity).isOnline(activity)) {
				DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));
				HashMap<String, String> params = new HashMap<String, String>();

				params.put("access_token", Data.userData.accessToken);
				params.put("audit_type", String.valueOf(auditType));
				HomeUtil.putDefaultParams(params);

				RestClient.getApiServices().cancelAuditByDriver(params, new Callback<RegisterScreenResponse>() {
					@Override
					public void success(RegisterScreenResponse registerScreenResponse, Response response) {
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						try {
							JSONObject jObj = new JSONObject(responseStr);
							int flag = jObj.getInt("flag");
							if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
								activity.getTransactionUtils().openSelectAuditFragment(activity,
										activity.getRelativeLayoutContainer());
							} else {
								DialogPopup.alertPopup(activity, "", jObj.getString("message"));
							}
						} catch (Exception exception) {
							exception.printStackTrace();
							DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
						}
						DialogPopup.dismissLoadingDialog();
					}

					@Override
					public void failure(RetrofitError error) {
						product.clicklabs.jugnoo.driver.utils.Log.e("request fail", error.toString());
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
