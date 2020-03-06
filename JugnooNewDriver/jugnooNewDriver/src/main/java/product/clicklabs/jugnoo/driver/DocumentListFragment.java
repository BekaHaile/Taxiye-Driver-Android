package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kbeanie.multipicker.api.CacheLocation;
import com.kbeanie.multipicker.api.CameraImagePicker;
import com.kbeanie.multipicker.api.ImagePicker;
import com.kbeanie.multipicker.api.Picker;
import com.kbeanie.multipicker.api.callbacks.ImagePickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenImage;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import product.clicklabs.jugnoo.driver.adapters.DocImage;
import product.clicklabs.jugnoo.driver.adapters.DocImagesAdapter;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.DocInfo;
import product.clicklabs.jugnoo.driver.datastructure.DriverTaskTypes;
import product.clicklabs.jugnoo.driver.datastructure.UserData;
import product.clicklabs.jugnoo.driver.fragments.DocumentDetailsFragment;
import product.clicklabs.jugnoo.driver.heremaps.activity.HereMapsFeedbackActivity;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.DocFieldsInfo;
import product.clicklabs.jugnoo.driver.retrofit.model.DocRequirementResponse;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.PermissionCommon;
import product.clicklabs.jugnoo.driver.utils.PhotoProvider;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedFile;

import static com.crashlytics.android.beta.Beta.TAG;
import static product.clicklabs.jugnoo.driver.utils.PermissionCommon.REQUEST_CODE_CAMERA;
import static product.clicklabs.jugnoo.driver.utils.PermissionCommon.REQUEST_CODE_WRITE_EXTERNAL_STORAGE;


public class DocumentListFragment extends Fragment implements ImagePickerCallback, PermissionCommon.PermissionListener {

	private static final String BRANDING_IMAGE = "Branding Image";
	private static final int ACTION_HERE_MAP_IMAGE_RESULT = 100;
	private static final int DOC_TYPE_BRANDING_IMAGE = 2;
	private static final int DOC_REQUIREMENT_OTHER_BRANDING = 6;
	private static final int DOC_REQUIREMENT_HERE_MAP = 7;
	TextView textViewInfoDisplay;
	ListView listView;
	String accessToken;
	int requirement, brandingImagesOnly, taskType;
	int imgPixel;
	int driverVehicleMappingId=-1;

	private PermissionCommon mPermissionCommon;
	private ImagePicker mImagePicker;
	private CameraImagePicker mCameraImagePicker;

	private DriverDocumentActivity activity;
	DriverDocumentListAdapter driverDocumentListAdapter;

	RelativeLayout main;
	LinearLayout llDocumentsState;
	private TextView tvDocsPending, tvDocsFailed, tvDocsCompleted;


	ArrayList<DocInfo> docs = new ArrayList<>();
	String userPhoneNo;
	int index = 0;
	int coloum = 0;
	double latitude, longitude;
	private boolean showSubmitButton=false;


	public DocumentListFragment() {

	}


	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		activity = (DriverDocumentActivity) getActivity();
		activity.registerReceiver(broadcastReceiver, new IntentFilter(Constants.ACTION_UPDATE_DOCUMENT_LIST));
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		activity.setSubmitButtonVisibility(showSubmitButton? View.VISIBLE : View.GONE);

	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		docs.clear();
		View rootView = inflater.inflate(R.layout.fragment_document_list, container, false);

		main = rootView.findViewById(R.id.main);

		rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
		listView = rootView.findViewById(R.id.listView);

		driverDocumentListAdapter = new DriverDocumentListAdapter();
		listView.setAdapter(driverDocumentListAdapter);

		textViewInfoDisplay = (TextView) rootView.findViewById(R.id.textViewInfoDisplay);
		textViewInfoDisplay.setText(getResources().getString(R.string.no_doc_available));
		tvDocsPending = rootView.findViewById(R.id.tvDocsPending);
		tvDocsFailed = rootView.findViewById(R.id.tvDocsFailed);
		tvDocsCompleted = rootView.findViewById(R.id.tvDocsCompleted);
		llDocumentsState = rootView.findViewById(R.id.llDocumentsState);

		accessToken = getArguments().getString("access_token");
		requirement = getArguments().getInt("doc_required");
		brandingImagesOnly = getArguments().getInt(Constants.BRANDING_IMAGES_ONLY, 0);
		taskType = getArguments().getInt(Constants.KEY_TASK_TYPE, DriverTaskTypes.SELF_BRANDING.getType());
		driverVehicleMappingId= getArguments().getInt(Constants.DRIVER_VEHICLE_MAPPING_ID, -1);
		latitude  = getArguments().getDouble(Constants.KEY_LATITUDE, 0);
		longitude  = getArguments().getDouble(Constants.KEY_LONGITUDE, 0);

		getDocsAsync(getActivity());
		activity.setSubmitButtonVisibility(brandingImagesOnly == 1 ? View.GONE : View.VISIBLE);
		llDocumentsState.setVisibility(brandingImagesOnly == 1 ? View.VISIBLE : View.GONE);



		return rootView;
	}

	public DriverDocumentListAdapter getDriverDocumentListAdapter() {
		return driverDocumentListAdapter;
	}

	@Override
	public void onDetach() {
		super.onDetach();
		activity = null;
	}

	@Override
	public void onDestroy() {
		if(activity != null && broadcastReceiver != null) {
			activity.unregisterReceiver(broadcastReceiver);
		}
		super.onDestroy();
	}

	@Override
	public void onResume() {
		super.onResume();

	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void permissionGranted(final int requestCode) {

		if(requestCode == REQUEST_CODE_CAMERA){

			getMCameraImagePicker().pickImage();

		} else if(requestCode == REQUEST_CODE_WRITE_EXTERNAL_STORAGE){

			getMImagePicker().pickImage();
		}

	}

	private ImagePicker getMImagePicker() {
		if (mImagePicker == null) {
			mImagePicker = new ImagePicker(DocumentListFragment.this);
			mImagePicker.setCacheLocation(CacheLocation.INTERNAL_APP_DIR);
			mImagePicker.setImagePickerCallback(DocumentListFragment.this);
			mImagePicker.shouldGenerateThumbnails(false);
			mImagePicker.shouldGenerateMetadata(false);
		}
		return mImagePicker;
	}

	private CameraImagePicker getMCameraImagePicker() {
		if (mCameraImagePicker == null) {
			mCameraImagePicker = new CameraImagePicker(getActivity());
			mCameraImagePicker.setCacheLocation(CacheLocation.INTERNAL_APP_DIR);
			mCameraImagePicker.setImagePickerCallback(DocumentListFragment.this);
			mCameraImagePicker.shouldGenerateThumbnails(false);
			mCameraImagePicker.shouldGenerateMetadata(false);
		}
		return mCameraImagePicker;
	}

	@Override
	public boolean permissionDenied(final int requestCode, boolean neverAsk) {
		return true;
	}

	@Override
	public void onRationalRequestIntercepted() {

	}

	public Pair<Boolean, String> allDocsMandatoryFieldsFilled() {
		for(DocInfo docInfo : docs){
			if(docInfo.getListDocFieldsInfo() != null){
				for(DocFieldsInfo fieldsInfo : docInfo.getListDocFieldsInfo()){
					if (fieldsInfo.isMandatory() && TextUtils.isEmpty(fieldsInfo.getValue())
							&& (fieldsInfo.getSetValue() == null || fieldsInfo.getSetValue().size() == 0)) {
						return new Pair<>(false, docInfo.docType + ": " + fieldsInfo.getLabel());
					}
				}
			}
		}
		return new Pair<>(true, "");
	}


	static class ViewHolderDriverDoc {
		TextView tvDocNameAndDetails, tvDocStatus;
		LinearLayout rideHistoryItem;
		RecyclerView rvDocImages;
		View bottomLine;
		DocImagesAdapter docImagesAdapter;
		int id;
	}

	public class DriverDocumentListAdapter extends BaseAdapter {
		LayoutInflater mInflater;
		ViewHolderDriverDoc holder;

		DriverDocumentListAdapter() {
			mInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		@Override
		public int getCount() {
			return docs.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			return getDocumentListView(position, convertView,mInflater, activity,false, false);
		}

		private SpannableStringBuilder getLightText(String text, int color){
			SpannableStringBuilder ssb = new SpannableStringBuilder(text.toLowerCase());
			ssb.setSpan(new ForegroundColorSpan(color), 0, ssb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			ssb.setSpan(new RelativeSizeSpan(0.7f), 0, ssb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			return ssb;
		}

		@NonNull
		public  View getDocumentListView(int position, View convertView, LayoutInflater mInflater, DriverDocumentActivity activity
		,boolean isActionable, boolean hideStatusIfNoImages) {
			ViewHolderDriverDoc	holder = null;
			if (convertView == null) {
				holder = new ViewHolderDriverDoc();
				convertView = mInflater.inflate(R.layout.list_item_documents, null);

				holder.rideHistoryItem = convertView.findViewById(R.id.rideHistoryItem);
				holder.tvDocNameAndDetails = convertView.findViewById(R.id.tvDocNameAndDetails);
				holder.tvDocNameAndDetails.setTypeface(Fonts.mavenMedium(activity));
				holder.tvDocStatus = convertView.findViewById(R.id.tvDocStatus);
				holder.tvDocStatus.setTypeface(Fonts.mavenRegular(activity));
				holder.rvDocImages = convertView.findViewById(R.id.rvDocImages);
				holder.rvDocImages.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false));
				holder.bottomLine = convertView.findViewById(R.id.bottomLine);

				holder.rideHistoryItem.setTag(holder);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolderDriverDoc) convertView.getTag();
			}

			DocInfo docInfo = docs.get(position);

			holder.id = position;

			if(docInfo.status.equalsIgnoreCase("uploaded")){
				docInfo.status = DocStatus.UPLOADED.getI();
			}

			//self check of editable in case of document is verified or approval is pending
			if(docInfo.status.equalsIgnoreCase(DocStatus.VERIFIED.getI())
					|| docInfo.status.equalsIgnoreCase(DocStatus.APPROVAL_PENDING.getI())){
				docInfo.isEditable = 0;
			} else if(docInfo.status.equalsIgnoreCase(DocStatus.REJECTED.getI())){
				docInfo.isEditable = 1;
			}

			//document mandatory optional spannableStringBuilder
			SpannableStringBuilder ssbDocRequirement;
			if (docInfo.docRequirement == DocRequirement.MANDATORY1.getI()
					|| docInfo.docRequirement == DocRequirement.MANDATORY3.getI()) {
				ssbDocRequirement = getLightText("("+activity.getString(R.string.mandatory)+")", ContextCompat.getColor(activity, R.color.textColorLight));
			} else if (docInfo.docRequirement == DocRequirement.REQUIRED.getI()) {
				ssbDocRequirement = getLightText("("+activity.getString(R.string.required)+")", ContextCompat.getColor(activity, R.color.textColorLight));
			} else {
				ssbDocRequirement = getLightText("("+activity.getString(R.string.optional)+")", ContextCompat.getColor(activity, R.color.textColorLight));
			}

			if (isActionable) {
				holder.tvDocNameAndDetails.setVisibility(View.VISIBLE);
				holder.rvDocImages.setVisibility(View.VISIBLE);
				holder.tvDocStatus.setVisibility(View.VISIBLE);
				holder.bottomLine.setVisibility(View.GONE);

				if(holder.docImagesAdapter == null){
					holder.docImagesAdapter = new DocImagesAdapter(activity, holder.rvDocImages, new DocImagesAdapter.Callback() {
						@Override
						public void onDeleteClick(int pos, @NotNull DocImage docImage, int docIndex) {
							deletImageLayoutOnClick(docIndex, activity, pos);
						}

						@Override
						public void onClick(int pos, @NotNull DocImage docImage, int docIndex) {
							if(docImage.getFile() == null && TextUtils.isEmpty(docImage.getImageUrl())
									&& TextUtils.isEmpty(docs.get(docIndex).reason)) {
								uploadImageChooserDialog(activity, docIndex, pos);
							} else {
								addImageLayotOnClick(docIndex, activity, pos);
							}
						}

						@Override
						public int getEmptyImagePlaceHolder(int docIndex) {
							return docs.get(docIndex).status.equalsIgnoreCase(DocStatus.REJECTED.getI()) ? R.drawable.reload_image : R.drawable.add_img_selector;
						}
					});
					holder.rvDocImages.setAdapter(holder.docImagesAdapter);
				}
				holder.docImagesAdapter.setList(docInfo.getDocImages(), holder.rvDocImages, docInfo.isEditable == 1, position);


				//if document status is rejected, upload again error is to be shown
				//status text, color, drawable controls
				int statusStringRes = R.string.upload,
						statusColorRes = R.color.textColorLight,
						statusDrawable = R.drawable.ic_upload_grey;
				if (docInfo.status.equalsIgnoreCase(DocStatus.UPLOADED.getI())) {
					statusStringRes = R.string.uploaded;
					statusColorRes = R.color.green_doc_status;
					statusDrawable = R.drawable.ic_tick_yellow;
				}
				else if (docInfo.status.equalsIgnoreCase(DocStatus.REJECTED.getI())) {
					statusStringRes = R.string.rejected;
					statusColorRes = R.color.red_delivery;
					statusDrawable = R.drawable.ic_cross_red;
					ssbDocRequirement = getLightText("("+activity.getString(R.string.upload_again)+")", ContextCompat.getColor(activity, R.color.textColorLight));
				}
				else if (docInfo.status.equalsIgnoreCase(DocStatus.VERIFIED.getI())) {
					statusStringRes = R.string.verified;
					statusColorRes = R.color.green_doc_status;
					statusDrawable = R.drawable.ic_tick_green;
				}
				else if (docInfo.status.equalsIgnoreCase(DocStatus.APPROVAL_PENDING.getI())) {
					statusStringRes = R.string.approval_pending;
					statusColorRes = R.color.themeColor;
					statusDrawable = R.drawable.doc_waiting;
				}
				holder.tvDocStatus.setText(statusStringRes);
				holder.tvDocStatus.setTextColor(ContextCompat.getColor(activity, statusColorRes));
				holder.tvDocStatus.setCompoundDrawablesRelativeWithIntrinsicBounds(statusDrawable, 0, 0, 0);

				holder.tvDocNameAndDetails.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);

				holder.tvDocNameAndDetails.setText(docInfo.getDocInstructions());
				holder.tvDocNameAndDetails.append(holder.tvDocNameAndDetails.getText().length() > 0 ? "\n" : "");
				holder.tvDocNameAndDetails.append(ssbDocRequirement);

				holder.rideHistoryItem.setEnabled(false);
				holder.rideHistoryItem.setOnClickListener(null);

			} else {
				holder.tvDocNameAndDetails.setVisibility(View.VISIBLE);
				holder.rvDocImages.setVisibility(View.GONE);
				holder.tvDocStatus.setVisibility(View.GONE);
				holder.bottomLine.setVisibility(View.VISIBLE);

				//drawable based on document status
				int startDrawable = R.drawable.ic_upload_grey;
				if (docInfo.status.equalsIgnoreCase(DocStatus.UPLOADED.getI())) {
					startDrawable = R.drawable.ic_tick_yellow;
				}
				else if (docInfo.status.equalsIgnoreCase(DocStatus.REJECTED.getI())) {
					startDrawable = R.drawable.ic_cross_red;
					ssbDocRequirement = getLightText("("+activity.getString(R.string.upload_again)+")", ContextCompat.getColor(activity, R.color.red_delivery));
				}
				else if (docInfo.status.equalsIgnoreCase(DocStatus.VERIFIED.getI())) {
					startDrawable = R.drawable.ic_tick_green;
				}
				else if (docInfo.status.equalsIgnoreCase(DocStatus.APPROVAL_PENDING.getI())) {
					startDrawable = R.drawable.doc_waiting;
				}
				holder.tvDocNameAndDetails.setCompoundDrawablesRelativeWithIntrinsicBounds(startDrawable , 0, R.drawable.ic_navigate_next_black_18dp, 0);

				holder.tvDocNameAndDetails.setText(docInfo.docType);
				holder.tvDocNameAndDetails.append(" ");
				holder.tvDocNameAndDetails.append(ssbDocRequirement);

				holder.rideHistoryItem.setEnabled(true);
				holder.rideHistoryItem.setOnClickListener(v -> {
					ViewHolderDriverDoc holder1 = (ViewHolderDriverDoc) v.getTag();

					if(taskType == DriverTaskTypes.HERE_MAPS_FEEDBACK.getType()){
						startActivityForResult(new Intent(activity, HereMapsFeedbackActivity.class)
                            .putExtra(Constants.KEY_DOC_TYPE_NUM, docs.get(holder1.id).docTypeNum)
                            .putExtra(Constants.KEY_IMG_POSITION, 0)
                            .putExtra(Constants.KEY_LATITUDE, latitude)
								.putExtra(Constants.KEY_LONGITUDE, longitude), ACTION_HERE_MAP_IMAGE_RESULT);
					} else {
						activity.openDocumentDetails(docs.get(holder1.id), holder1.id);
					}

				});
			}












			return convertView;
		}

	}

	public  void deletImageLayoutOnClick(final int holderId,final DriverDocumentActivity context,int column) {
		deleteImage(context, docs.get(holderId), column);
	}

	public   void addImageLayotOnClick(final int holderId, final DriverDocumentActivity context, final int column) {
		try {
            DocInfo docInfoImageLayout2 = docs.get(holderId);
            if (docInfoImageLayout2.status.equalsIgnoreCase(DocStatus.REJECTED.getI())) {
				DialogPopup.alertPopupTwoButtonsWithListeners(context,
						context.getResources().getString(R.string.rejection_reason),
                        docInfoImageLayout2.reason,
						context.getResources().getString(R.string.upload_again),
						context.getResources().getString(R.string.cancel),
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                uploadImageChooserDialog(context, holderId, column);
                            }
                        },
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        }, true, true);
            } else {
                uploadImageChooserDialog(activity, holderId, column);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}


	BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			getDocsAsync(activity);
		}
	};

	public void hitFetchDriverVehicleDocument(HashMap<String,String> params) {
		DialogPopup.showLoadingDialog(getActivity(), getString(R.string.loading));
		params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
		RestClient.getApiServices().fetchDriverVehicleDocuments(params, new Callback<DocRequirementResponse>() {
			@Override
			public void success(DocRequirementResponse o, Response response) {
				String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
				Log.i(TAG, "fetchDriverVehicle response = " + responseStr);
				DialogPopup.dismissLoadingDialog();
				try {
					JSONObject jObj = new JSONObject(responseStr);
					int flag = jObj.optInt(Constants.KEY_FLAG, ApiResponseFlags.ACTION_COMPLETE.getOrdinal());
					String message = JSONParser.getServerMessage(jObj);
					if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
						parseDocumentResponse(o,response);
					}
//					DialogPopup.alertPopup(getActivity(), "", message);

				} catch (Exception e) {
					e.printStackTrace();
				}
				DialogPopup.dismissLoadingDialog();
			}

			@Override
			public void failure(RetrofitError error) {
				try {
					DialogPopup.dismissLoadingDialog();
					DialogPopup.alertPopup(getActivity(), "", getActivity().getString(R.string.error_occured_tap_to_retry));
				} catch (Exception e) {
					DialogPopup.dismissLoadingDialog();
					e.printStackTrace();
				}
				DialogPopup.dismissLoadingDialog();
			}
		});
	}


	public void getDocsAsync(final Activity activity) {
		try {
			DialogPopup.showLoadingDialog(activity, getString(R.string.loading));
			String isRequired = String.valueOf(requirement);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("access_token",accessToken);
			params.put("login_documents", isRequired);
			params.put("app_version", Data.appVersion+"");
			params.put(Constants.BRANDING_IMAGE, String.valueOf(brandingImagesOnly));
			params.put(Constants.KEY_TASK_TYPE, String.valueOf(taskType));
			HomeUtil.putDefaultParams(params);
			if(driverVehicleMappingId!=-1){
				params.put(Constants.DRIVER_VEHICLE_MAPPING_ID,driverVehicleMappingId+"");
				hitFetchDriverVehicleDocument(params);
			}
			else
			RestClient.getApiServices().docRequest(params, new Callback<DocRequirementResponse>() {
				@Override
				public void success(DocRequirementResponse docRequirementResponse, Response response) {
					parseDocumentResponse(docRequirementResponse,response);
				}

				@Override
				public void failure(RetrofitError error) {
					Log.i("DocError", error.toString());
					DialogPopup.dismissLoadingDialog();
					updateListData(activity.getResources().getString(R.string.error_occured_tap_to_retry), true);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void parseDocumentResponse(DocRequirementResponse docRequirementResponse,Response response){
		try {
			DialogPopup.dismissLoadingDialog();
			String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
			JSONObject jObj;
			jObj = new JSONObject(jsonString);
			if (!jObj.isNull("error")) {
				String errorMessage = jObj.getString("error");
				if (Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())) {
					HomeActivity.logoutUser(activity, null);
				}
			} else {
				imgPixel = docRequirementResponse.getImgPixel();
				docs.clear();
				for (int i = 0; i < docRequirementResponse.getData().size(); i++) {
					DocRequirementResponse.DocumentData data = docRequirementResponse.getData().get(i);
					DocInfo docInfo = new DocInfo(data.getDocTypeText(), data.getDocTypeNum(), data.getDocRequirement(),
							data.getDocStatus(), data.getDocUrl(), data.getReason(), data.getDocCount(), data.getIsEditable(),
							data.getInstructions(), data.getGalleryRestricted(),data.getListDocInfo(),
							data.getIsDocInfoEditable(),data.getDocCategory());
					if(brandingImagesOnly == 1 && data.getDocType() == DOC_TYPE_BRANDING_IMAGE){
						if(taskType == DriverTaskTypes.OTHER_BRANDING.getType()
								&& docInfo.docRequirement == DOC_REQUIREMENT_OTHER_BRANDING){
							docs.add(docInfo);
						} else if(taskType == DriverTaskTypes.HERE_MAPS_FEEDBACK.getType()
								&& docInfo.docRequirement == DOC_REQUIREMENT_HERE_MAP){
							docs.add(docInfo);
						} else if(taskType == DriverTaskTypes.SELF_BRANDING.getType()
								&& docInfo.docRequirement != DOC_REQUIREMENT_OTHER_BRANDING
								&& docInfo.docRequirement != DOC_REQUIREMENT_HERE_MAP){
							docs.add(docInfo);
						}
					} else if(brandingImagesOnly == 0 && data.getDocType() != DOC_TYPE_BRANDING_IMAGE){
						docs.add(docInfo);
					}
					if (data.getDocStatus().equals("0")||data.getIsDocInfoEditable()){
						showSubmitButton=true;
						((DriverDocumentActivity)activity).setSubmitButtonVisibility(View.VISIBLE);
					}
					else
						((DriverDocumentActivity)activity).setSubmitButtonVisibility(View.GONE);
				}

				updateListData(activity.getResources().getString(R.string.no_doc_available), false);
				userPhoneNo = docRequirementResponse.getuserPhoneNo();
				checkForDocumentsSubmit();

				if(brandingImagesOnly == 1) {
					int pendingC = 0, failedC = 0, completedC = 0;
					for(DocInfo docInfo : docs){
						if (docInfo.status.equalsIgnoreCase(DocStatus.REJECTED.getI())) {
							failedC++;
						} else if (docInfo.status.equalsIgnoreCase(DocStatus.VERIFIED.getI())) {
							completedC++;
						} else {
							pendingC++;
						}
					}
					tvDocsPending.setText(R.string.pending);
					tvDocsPending.append("\n");
					tvDocsPending.append(getCountSpannable(pendingC, R.color.yellow_jugnoo));

					tvDocsFailed.setText(R.string.failed);
					tvDocsFailed.append("\n");
					tvDocsFailed.append(getCountSpannable(failedC, R.color.red_status_v2));

					tvDocsCompleted.setText(R.string.completed);
					tvDocsCompleted.append("\n");
					tvDocsCompleted.append(getCountSpannable(completedC, R.color.green_doc_status));
				}



			}
		} catch (Exception exception) {
			exception.printStackTrace();
			updateListData(activity.getResources().getString(R.string.error_occured_tap_to_retry), true);
		}
	}

	private SpannableStringBuilder getCountSpannable(int count, int colorRes){
		SpannableStringBuilder ssb = new SpannableStringBuilder(String.valueOf(count));
		ssb.setSpan(new ForegroundColorSpan(ContextCompat.getColor(activity, colorRes)), 0, ssb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		ssb.setSpan(new RelativeSizeSpan(1.4f), 0, ssb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		ssb.setSpan(new StyleSpan(Typeface.BOLD), 0, ssb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		return ssb;
	}

	public void updateListData(String message, boolean errorOccurred) {
		if (errorOccurred) {
			textViewInfoDisplay.setText(message);
			textViewInfoDisplay.setVisibility(View.VISIBLE);

			docs.clear();
			driverDocumentListAdapter.notifyDataSetChanged();
		} else {
			if (docs.size() == 0) {
				textViewInfoDisplay.setText(message);
				textViewInfoDisplay.setVisibility(View.VISIBLE);
			} else {
				textViewInfoDisplay.setVisibility(View.GONE);
			}
			driverDocumentListAdapter.notifyDataSetChanged();
		}
	}

	public void uploadImageChooserDialog(final DriverDocumentActivity activity, int index, int column) {

		this.index = index;
		this.coloum = column;
		try {
			final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.dialog_upload_document);

			RelativeLayout relative = dialog.findViewById(R.id.relative);

			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(true);
			dialog.setCanceledOnTouchOutside(true);


			TextView tvGallery = dialog.findViewById(R.id.tvGallery);
			TextView tvCamera = dialog.findViewById(R.id.tvCamera);

			DocInfo docInfo = docs.get(index);
			if((docInfo.getGalleryRestricted() == null
					&& docInfo.docType.toLowerCase().contains(BRANDING_IMAGE.toLowerCase()))
					|| docInfo.getGalleryRestricted() == 1){
				chooseImageFromCamera();
				return;
			}


			tvGallery.setOnClickListener(view -> {
				chooseImageFromGallery();
				dialog.dismiss();
			});
			tvCamera.setOnClickListener(v -> {
				chooseImageFromCamera();
				dialog.dismiss();
			});

			relative.setOnClickListener(v -> dialog.dismiss());

			dialog.show();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void chooseImageFromCamera() {

		if (mPermissionCommon == null) {
			mPermissionCommon = new PermissionCommon(this).setCallback(this);
		}

		mPermissionCommon.getPermission(REQUEST_CODE_CAMERA, android.Manifest.permission.CAMERA);
	}


	public void chooseImageFromGallery() {
		if (mPermissionCommon == null){
            mPermissionCommon = new PermissionCommon(this).setCallback(this);
		}

		mPermissionCommon.getPermission(PermissionCommon.REQUEST_CODE_WRITE_EXTERNAL_STORAGE,
				android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
            if (requestCode == Picker.PICK_IMAGE_DEVICE) {
				getMImagePicker().submit(data);
            } else if (requestCode == Picker.PICK_IMAGE_CAMERA) {
				getMCameraImagePicker().submit(data);
            } else if(requestCode == ACTION_HERE_MAP_IMAGE_RESULT
					&& resultCode == Activity.RESULT_OK) {
				int docTypeNum = data.getIntExtra(Constants.KEY_DOC_TYPE_NUM, 0);
				int imgPosition = data.getIntExtra(Constants.KEY_IMG_POSITION, 0);
				int placeType = data.getIntExtra(Constants.KEY_PLACE_TYPE, 0);
				File file = new File(data.getStringExtra(Constants.KEY_FILE_SELECTED));

				for(DocInfo di : docs){
					if(di.docTypeNum == docTypeNum){
						uploadPicToServer(activity, file, di, imgPosition, placeType);
						break;
					}
				}

			}

        } catch (Exception e) {
			e.printStackTrace();
		}

	}


	@Override
	public void onImagesChosen(final List<ChosenImage> list) {

		if(list.size() > 0) {
			activity.runOnUiThread(new Runnable() {

				@Override
				public void run() {

					Log.v("onImageChosen called", "onImageChosen called");
					try {
						final ChosenImage image = list.get(0);
						if (image != null) {

							BitmapFactory.Options opt;
							opt = new BitmapFactory.Options();
							opt.inTempStorage = new byte[16 * 1024];
							int height11 = image.getHeight();
							int width11 = image.getWidth();
							float mb = (width11 * height11) / 1024000;

							if (mb > 4f)
								opt.inSampleSize = 4;
							else if (mb > 3f)
								opt.inSampleSize = 2;

							opt.inPreferredConfig = Bitmap.Config.ARGB_8888;
							Bitmap bitmap = BitmapFactory.decodeFile(image.getOriginalPath(), opt);

							Uri uri = PhotoProvider.Companion.getPhotoUri(new File(image.getOriginalPath()));
							int rotate = Utils.getCameraPhotoOrientation(activity, uri, image.getOriginalPath());
							Bitmap rotatedBitmap;
							Matrix rotateMatrix = new Matrix();
							rotateMatrix.postRotate(rotate);
							rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), rotateMatrix, false);


							Bitmap newBitmap = null;
							if (rotatedBitmap != null) {
								double oldHeight = rotatedBitmap.getHeight();
								double oldWidth = rotatedBitmap.getWidth();

								if (oldWidth > oldHeight) {
									int newHeight = imgPixel;
									int newWidth = (int) ((oldWidth / oldHeight) * imgPixel);
									newBitmap = Utils.getResizedBitmap(rotatedBitmap, newHeight, newWidth);
								} else {
									int newWidth = imgPixel;
									int newHeight = (int) ((oldHeight / oldWidth) * imgPixel);
									newBitmap = Utils.getResizedBitmap(rotatedBitmap, newHeight, newWidth);
								}
							}

							File f;
							if (newBitmap != null) {
								f = Utils.compressToFile(activity, newBitmap, Bitmap.CompressFormat.JPEG, 100);
								driverDocumentListAdapter.notifyDataSetChanged();
								uploadPicToServer(getActivity(), f, docs.get(index), coloum, -1);
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	}

	@Override
	public void onError(final String reason) {

		activity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(getActivity(), reason, Toast.LENGTH_LONG).show();
			}
		});
	}




	private void uploadPicToServer(final Activity activity, final File photoFile, final DocInfo docInfo, final int column, int placeType) {
		try {
			if (AppStatus.getInstance(activity).isOnline(activity)) {
				DialogPopup.showLoadingDialog(activity, getResources().getString(R.string.loading));
				HashMap<String, String> params = new HashMap<>();

				params.put(Constants.KEY_ACCESS_TOKEN, accessToken);
				params.put(Constants.KEY_IMG_POSITION, String.valueOf(column));
				params.put(Constants.KEY_DOC_TYPE_NUM, String.valueOf(docInfo.docTypeNum));

				HomeUtil.putDefaultParams(params);

				if(placeType != -1){
					String appId = Prefs.with(activity).getString(Constants.DRIVER_HERE_APP_ID, getString(R.string.driver_here_app_id));
					String feedback = "{\"coordinates\":["+longitude+", "+latitude+", 0],\"type\":\"Point\"" +
							",\"properties\":{\"appId\":\""+appId+"\",\"error\": 30,\"v\":\"2.7\",\"type\":\""+placeType+"\"}}";

					params.put(Constants.KEY_FEEDBACK, feedback);
				}
				if (docInfo.docCategory == 1&&Data.getMultipleVehiclesEnabled() == 1) {
					if(driverVehicleMappingId!=-1)
					params.put(Constants.DRIVER_VEHICLE_MAPPING_ID,driverVehicleMappingId+"");
					if(Data.getDriverMappingIdOnBoarding()!=-1){
					params.put(Constants.DRIVER_VEHICLE_MAPPING_ID,Data.getDriverMappingIdOnBoarding()+"");
					}
				}

				TypedFile typedFile;
				typedFile = new TypedFile(Constants.MIME_TYPE, photoFile);
				RestClient.getApiServices().uploadImageToServer(typedFile, params, new Callback<DocRequirementResponse>() {
					@Override
					public void success(DocRequirementResponse docRequirementResponse, Response response) {
						try {
							String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
							JSONObject jObj;
							jObj = new JSONObject(jsonString);

							if (!SplashNewActivity.checkIfUpdate(jObj, activity)) {
								int flag = jObj.getInt("flag");
								String message = JSONParser.getServerMessage(jObj);

								if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag, null)) {

									if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
										DialogPopup.alertPopup(activity, "", message);
										docInfo.status = jObj.getString("status");

										if(docInfo.getDocImages().size() > column) {
											docInfo.getDocImages().get(column).setFile(photoFile);
										}
//										if (coloum == 0) {
//											docInfo.setFile(photoFile);
//										} else {
//											docInfo.setFile1(photoFile);
//										}
										driverDocumentListAdapter.notifyDataSetChanged();
										if(((DriverDocumentActivity)activity).getDocumentDetailsFragment()!=null){
											((DocumentDetailsFragment)((DriverDocumentActivity)activity).getDocumentDetailsFragment()).
													setDocData(docInfo);
										}

									} else if (ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag) {
										DialogPopup.alertPopup(activity, "", message);
										docInfo.isExpended = false;
										driverDocumentListAdapter.notifyDataSetChanged();
									} else {
										DialogPopup.alertPopup(activity, "", message);
										docInfo.isExpended = false;
										driverDocumentListAdapter.notifyDataSetChanged();
									}
									DialogPopup.dismissLoadingDialog();
								}
							} else {
								DialogPopup.dismissLoadingDialog();
							}
						} catch (Exception exception) {
							exception.printStackTrace();
							DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
							DialogPopup.dismissLoadingDialog();
							docInfo.isExpended = false;
							driverDocumentListAdapter.notifyDataSetChanged();
						}
						DialogPopup.dismissLoadingDialog();

					}

					@Override
					public void failure(RetrofitError error) {
						DialogPopup.dismissLoadingDialog();
						docInfo.isExpended = false;
						driverDocumentListAdapter.notifyDataSetChanged();
					}
				});
			} else {
				DialogPopup.alertPopup(activity, "", getResources().getString(R.string.check_internet_message));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void deleteImage(final DriverDocumentActivity activity, final DocInfo docInfo, final int column) {
		try {
			if (AppStatus.getInstance(activity).isOnline(activity)) {
				DialogPopup.showLoadingDialog(activity, getResources().getString(R.string.loading));
				HashMap<String, String> params = new HashMap<>();

				params.put("access_token", accessToken);
				params.put("img_position", String.valueOf(column));
				params.put("doc_type_num", String.valueOf(docInfo.docTypeNum));
				HomeUtil.putDefaultParams(params);

				RestClient.getApiServices().deleteImage(params, new Callback<DocRequirementResponse>() {
					@Override
					public void success(DocRequirementResponse docRequirementResponse, Response response) {
						try {
							String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
							JSONObject jObj;
							jObj = new JSONObject(jsonString);

							if (!SplashNewActivity.checkIfUpdate(jObj, activity)) {
								int flag = jObj.getInt("flag");
								String message = JSONParser.getServerMessage(jObj);

								if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag, null)) {

									if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
										DialogPopup.alertPopup(activity, "", message);
										if(docInfo.getDocImages().size() > column) {
											docInfo.getDocImages().get(column).setFile(null);
											docInfo.getDocImages().get(column).setImageUrl(null);
										}
//										if(column==0){
//											docInfo.setFile(null);
//										}else{
//											docInfo.setFile1(null);
//										}
//										docInfo.url.set(column, null);
										if(docInfo.checkIfURLEmpty()){
											docInfo.isExpended = false;
											docInfo.status = "-1";
										}

										driverDocumentListAdapter.notifyDataSetChanged();

										if(activity.getDocumentDetailsFragment()!=null){
											((DocumentDetailsFragment)activity.getDocumentDetailsFragment()).setDocData(docInfo);
										}

									} else if (ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag) {
										DialogPopup.alertPopup(activity, "", message);
										docInfo.isExpended = false;
										driverDocumentListAdapter.notifyDataSetChanged();
									} else {
										DialogPopup.alertPopup(activity, "", message);
										docInfo.isExpended = false;
										driverDocumentListAdapter.notifyDataSetChanged();
									}
									DialogPopup.dismissLoadingDialog();
								}
							} else {
								DialogPopup.dismissLoadingDialog();
							}


						} catch (Exception exception) {
							exception.printStackTrace();
							DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
							DialogPopup.dismissLoadingDialog();
							docInfo.isExpended = false;
							driverDocumentListAdapter.notifyDataSetChanged();
						}
						DialogPopup.dismissLoadingDialog();


					}

					@Override
					public void failure(RetrofitError error) {
						DialogPopup.dismissLoadingDialog();
						docInfo.isExpended = false;
						driverDocumentListAdapter.notifyDataSetChanged();
					}
				});
			} else {
				DialogPopup.alertPopup(activity, "", getResources().getString(R.string.check_internet_message));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	private void checkForDocumentsSubmit(){
		if(brandingImagesOnly == 1){
			return;
		}
		boolean mandatoryDocsSubmitted = true;
		for(DocInfo docInfo : docs){
			if((docInfo.docRequirement == DocRequirement.MANDATORY1.getI()
					|| docInfo.docRequirement == DocRequirement.MANDATORY3.getI()
					|| docInfo.docRequirement == DocRequirement.REQUIRED.getI())
					&&
					(!docInfo.status.equalsIgnoreCase(DocStatus.UPLOADED.getI()))){
				mandatoryDocsSubmitted = false;
				break;
			}
		}
		if(mandatoryDocsSubmitted&&showSubmitButton){
			DialogPopup.dialogBanner(activity,
					activity.getString(R.string.please_press_submit_button), null, 5000,
					R.color.white, R.color.themeColor);
		}
	}

	@Override
	public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (mPermissionCommon != null) {
			mPermissionCommon.onRequestPermissionsResult(requestCode,permissions,grantResults);
		}
	}


	public void updateDocInfo(int pos, DocInfo docInfo){
		getDocsAsync(getActivity());
		if(docs != null && pos > -1 && pos < docs.size() && docInfo != null) {
			docs.set(pos, docInfo);
		}
	}

	enum DocRequirement{
		MANDATORY1(1), MANDATORY3(3), REQUIRED(4);

		int i;
		DocRequirement(int i){
			this.i = i;
		}

		public int getI(){
			return i;
		}
	}
	enum DocStatus{
		UPLOADED("4"), REJECTED("2"), VERIFIED("3"), APPROVAL_PENDING("1") ;

		String i;
		DocStatus(String i){
			this.i = i;
		}

		public String getI(){
			return i;
		}
	}

}
