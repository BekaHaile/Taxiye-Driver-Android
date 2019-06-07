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
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
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
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import product.clicklabs.jugnoo.driver.adapters.DocImage;
import product.clicklabs.jugnoo.driver.adapters.DocImagesAdapter;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.DocInfo;
import product.clicklabs.jugnoo.driver.fragments.DocumentDetailsFragment;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.DocRequirementResponse;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.PermissionCommon;
import product.clicklabs.jugnoo.driver.utils.PhotoProvider;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedFile;

import static product.clicklabs.jugnoo.driver.utils.PermissionCommon.REQUEST_CODE_CAMERA;
import static product.clicklabs.jugnoo.driver.utils.PermissionCommon.REQUEST_CODE_WRITE_EXTERNAL_STORAGE;


public class DocumentListFragment extends Fragment implements ImagePickerCallback, PermissionCommon.PermissionListener {

	private static final String BRANDING_IMAGE = "Branding Image";
	private static final int DOC_TYPE_BRANDING_IMAGE = 2;
	TextView textViewInfoDisplay;
	ListView listView;
	String accessToken;
	int requirement, brandingImagesOnly;
	int imgPixel;

	private PermissionCommon mPermissionCommon;
	private ImagePicker mImagePicker;
	private CameraImagePicker mCameraImagePicker;

	private DriverDocumentActivity activity;
	DriverDocumentListAdapter driverDocumentListAdapter;

	RelativeLayout main;


	ArrayList<DocInfo> docs = new ArrayList<>();
	String userPhoneNo;
	int index = 0;
	int coloum = 0;


	public DocumentListFragment() {

	}


	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		activity = (DriverDocumentActivity) getActivity();
		activity.registerReceiver(broadcastReceiver, new IntentFilter(Constants.ACTION_UPDATE_DOCUMENT_LIST));
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		docs.clear();
		View rootView = inflater.inflate(R.layout.fragment_list, container, false);

		main = rootView.findViewById(R.id.main);

		rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
		listView = rootView.findViewById(R.id.listView);

		driverDocumentListAdapter = new DriverDocumentListAdapter();
		listView.setAdapter(driverDocumentListAdapter);

		textViewInfoDisplay = (TextView) rootView.findViewById(R.id.textViewInfoDisplay);
		textViewInfoDisplay.setText(getResources().getString(R.string.no_doc_available));

		accessToken = getArguments().getString("access_token");
		requirement = getArguments().getInt("doc_required");
		brandingImagesOnly = getArguments().getInt(Constants.BRANDING_IMAGES_ONLY, 0);
		getDocsAsync(getActivity());


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
					activity.openDocumentDetails(docs.get(holder1.id), holder1.id);
				});
			}


//			if (docInfo.docRequirement == DocRequirement.MANDATORY1.getI()
//					|| docInfo.docRequirement == DocRequirement.MANDATORY3.getI()) {
//				holder.docRequirement.setText(activity.getResources().getString(R.string.mandatory));
//				holder.docType.setText(docInfo.docType+"*");
//			} else if (docInfo.docRequirement == DocRequirement.REQUIRED.getI()) {
//				holder.docRequirement.setText(activity.getResources().getString(R.string.required));
//				holder.docType.setText(docInfo.docType+"*");
//			} else {
//				holder.docRequirement.setText(activity.getResources().getString(R.string.optional));
//				holder.docType.setText(docInfo.docType);
//			}
//			holder.docRejected.setVisibility(View.GONE);
//			holder.imageViewDocStatus.setVisibility(View.GONE);
//			holder.docType.setTextColor(activity.getResources().getColor(R.color.themeColor));
//
//			holder.addImageLayout.setVisibility(View.VISIBLE);
//			holder.addImageLayout2.setVisibility(View.VISIBLE);
//			holder.rlDocumentStatus.setVisibility(View.GONE);
//
//			if(docInfo.status.equalsIgnoreCase("uploaded")){
//				docInfo.status = DocStatus.UPLOADED.getI();
//			}
//			if (docInfo.status.equalsIgnoreCase(DocStatus.UPLOADED.getI())) {
//				holder.imageViewDocStatus.setImageResource(R.drawable.doc_uploaded);
//				holder.docStatus.setText(activity.getResources().getString(R.string.uploaded));
//				holder.docStatus.setTextColor(activity.getResources().getColor(R.color.green_doc_status));
//				holder.imageViewDocStatusImage.setVisibility(View.VISIBLE);
//				holder.imageViewDocStatusImage.setImageResource(R.drawable.uploaded_doc_status);
//				holder.rlDocumentStatus.setVisibility(View.VISIBLE);
//			}
//			else if (docInfo.status.equalsIgnoreCase(DocStatus.REJECTED.getI())) {
//				holder.docRejected.setVisibility(View.VISIBLE);
//				holder.imageViewDocStatus.setImageResource(R.drawable.doc_rejected);
//				holder.imageViewDocStatus.setVisibility(View.VISIBLE);
//				holder.docStatus.setText(activity.getResources().getString(R.string.rejected));
//				holder.docStatus.setTextColor(activity.getResources().getColor(R.color.red_delivery));
//				holder.imageViewDocStatusImage.setVisibility(View.VISIBLE);
//				holder.imageViewDocStatusImage.setImageResource(R.drawable.rejected_doc_status);
//				holder.rlDocumentStatus.setVisibility(View.VISIBLE);
//			}
//			else if (docInfo.status.equalsIgnoreCase(DocStatus.VERIFIED.getI())) {
//				holder.imageViewDocStatus.setVisibility(View.VISIBLE);
//				holder.imageViewDocStatus.setImageResource(R.drawable.doc_verified);
//				holder.docStatus.setText(activity.getResources().getString(R.string.verified));
//				holder.docStatus.setTextColor(activity.getResources().getColor(R.color.green_doc_status));
//				holder.imageViewDocStatusImage.setVisibility(View.GONE);
//				holder.docType.setTextColor(activity.getResources().getColor(R.color.grey_light_doc_status));
//				holder.rlDocumentStatus.setVisibility(View.VISIBLE);
//			}
//			else if (docInfo.status.equalsIgnoreCase(DocStatus.APPROVAL_PENDING.getI())) {
//				holder.docStatus.setText(activity.getResources().getString(R.string.approval_pending));
//				holder.docStatus.setTextColor(activity.getResources().getColor(R.color.themeColor));
//				holder.imageViewDocStatus.setVisibility(View.VISIBLE);
//				holder.imageViewDocStatus.setImageResource(R.drawable.doc_waiting);
//				holder.imageViewDocStatusImage.setVisibility(View.GONE);
//				holder.docType.setTextColor(activity.getResources().getColor(R.color.grey_light_doc_status));
//				holder.rlDocumentStatus.setVisibility(View.VISIBLE);
//			}
//
//			if(docInfo.status.equalsIgnoreCase(DocStatus.VERIFIED.getI())
//					|| docInfo.status.equalsIgnoreCase(DocStatus.APPROVAL_PENDING.getI())){
//				docInfo.isEditable = 0;
//			} else if(docInfo.status.equalsIgnoreCase(DocStatus.REJECTED.getI())){
//				docInfo.isEditable = 1;
//			}
//
//			if(!docInfo.status.equalsIgnoreCase(DocStatus.REJECTED.getI())
//					&& docInfo.isEditable == 0){
//				holder.addImageLayout.setEnabled(false);
//				holder.addImageLayout2.setEnabled(false);
//				holder.imageViewUploadDoc.setEnabled(false);
//				holder.relativeLayoutSelectPicture.setEnabled(false);
//
//				holder.deleteImage1.setVisibility(View.GONE);
//				holder.deleteImage2.setVisibility(View.GONE);
//				if(TextUtils.isEmpty(docInfo.url.get(1)) && docInfo.getFile1() == null){
//					holder.addImageLayout2.setVisibility(View.GONE);
//				}
//			} else {
//				holder.addImageLayout.setEnabled(true);
//				holder.addImageLayout2.setEnabled(true);
//				holder.imageViewUploadDoc.setEnabled(true);
//				holder.relativeLayoutSelectPicture.setEnabled(true);
//
//				if(docInfo.getFile() != null
//						|| !TextUtils.isEmpty(docInfo.url.get(0))){
//					holder.deleteImage1.setVisibility(View.VISIBLE);
//				} else {
//					holder.deleteImage1.setVisibility(View.GONE);
//				}
//				if(docInfo.getFile1() != null
//						|| !TextUtils.isEmpty(docInfo.url.get(1))){
//					holder.deleteImage2.setVisibility(View.VISIBLE);
//				} else {
//					holder.deleteImage2.setVisibility(View.GONE);
//				}
//			}
//
//			if (docInfo.getFile() != null) {
//				Picasso.with(activity).load(docInfo.getFile())
//						.transform(new RoundBorderTransform()).resize(300, 300).centerCrop()
//						.into(holder.setCapturedImage);
//			} else {
//				holder.setCapturedImage.setImageResource(R.drawable.transparent);
//			}
//
//			if (docInfo.getFile1() != null) {
//				Picasso.with(activity).load(docInfo.getFile1())
//						.transform(new RoundBorderTransform()).resize(300, 300).centerCrop()
//						.into(holder.setCapturedImage2);
//			} else {
//				holder.setCapturedImage2.setImageResource(R.drawable.transparent);
//			}
//
//			try {
//				if (!TextUtils.isEmpty(docInfo.url.get(0)) || !TextUtils.isEmpty(docInfo.url.get(1))
//						|| docInfo.getFile() != null || docInfo.getFile1() != null
//						|| docInfo.status.equalsIgnoreCase(DocStatus.REJECTED.getI())){
//					docInfo.isExpended = true;
//				}
//
//				if (!TextUtils.isEmpty(docInfo.url.get(0))) {
//					Picasso.with(activity).load(docInfo.url.get(0))
//							.transform(new RoundBorderTransform()).resize(300, 300).centerCrop()
//							.into(holder.setCapturedImage);
//					docInfo.setFile(null);
//				}
//				if(docInfo.status.equalsIgnoreCase(DocStatus.REJECTED.getI())){
//					holder.setCapturedImage.setImageResource(R.drawable.reload_image);
//					holder.addImageLayout2.setVisibility(View.GONE);
//				}
//				else if (!TextUtils.isEmpty(docInfo.url.get(1))) {
//					Picasso.with(activity).load(docInfo.url.get(1))
//							.transform(new RoundBorderTransform()).resize(300, 300).centerCrop()
//							.into(holder.setCapturedImage2);
//					docInfo.setFile1(null);
//				}
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//
//
//
//			holder.docType.setTag(holder);
//			holder.rideHistoryItem.setTag(holder);
//			if(docInfo.getDocInstructions()==null){
//				holder.docType.setOnClickListener(null);
//			} else {
//				View.OnClickListener onClickListener = new View.OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						ViewHolderDriverDoc holder = (ViewHolderDriverDoc) v.getTag();
//						DialogPopup.alertPopup(activity,docs.get(holder.id).docType,docs.get(holder.id).getDocInstructions(),true,false);
//					}
//				};
//				holder.docType.setOnClickListener(onClickListener);
//
//			}
//
//			if (docInfo.isExpended) {
//				holder.relativeLayoutImageStatus.setVisibility(View.VISIBLE);
//				holder.imageViewUploadDoc.setVisibility(View.GONE);
//				holder.docRequirement.setVisibility(View.GONE);
//			} else {
//				holder.relativeLayoutImageStatus.setVisibility(View.GONE);
//				holder.imageViewUploadDoc.setVisibility(View.VISIBLE);
//				holder.docRequirement.setVisibility(View.VISIBLE);
//			}
//			if(brandingImagesOnly == 1){
//				holder.docRequirement.setVisibility(View.GONE);
//			}
//
//			if(docInfo.docCount<2){
//				holder.addImageLayout2.setVisibility(View.GONE);
//			}
//			if(docInfo.docCount < 1){
//				holder.addImageLayout.setVisibility(View.GONE);
//				holder.imageViewUploadDoc.setVisibility(View.GONE);
//			}
//
//			if (hideStatusIfNoImages) {
//				// if no images are to be shown, hide textview that shows current status
//				// if url list greater than 0, check if urls are null or not, hide if all null
//				if (docInfo.url == null || (docInfo.url.isEmpty())) {
//					holder.rlDocumentStatus.setVisibility(View.GONE);
//				} else {
//					boolean allNulls = true;
//					for (String s: docInfo.url) {
//						if (s != null && !s.isEmpty()) {
//							allNulls = false;
//							break;
//						}
//
//					}
//					holder.rlDocumentStatus.setVisibility((allNulls
//							&& docInfo.getFile() == null && docInfo.getFile1() == null ? View.GONE: View.VISIBLE));
//				}
//			}
//
//			holder.docRequirement.setVisibility(holder.rlDocumentStatus.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
//
//			holder.bottomLine.setVisibility(position > 0 ? View.VISIBLE : View.GONE);
//
//			if (isActionable) {
//				holder.relativeLayoutSelectPictureStatus.setVisibility(View.GONE);
//				holder.ivArrowForward.setVisibility(View.GONE);
//				holder.bottomLine.setVisibility(View.GONE);
//				holder.imageViewUploadDoc.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        ViewHolderDriverDoc holder = (ViewHolderDriverDoc) v.getTag();
//                        uploadImageChooserDialog(activity, holder.id, 0);
//                    }
//                });
//
//				holder.deleteImage1.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        ViewHolderDriverDoc holder = (ViewHolderDriverDoc) v.getTag();
//                        deletImageLayoutOnClick(holder.id, activity,0);
//                    }
//                });
//
//				holder.deleteImage2.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        ViewHolderDriverDoc holder = (ViewHolderDriverDoc) v.getTag();
//                        deletImageLayoutOnClick(holder.id, activity,1);
//                    }
//                });
//
//				holder.addImageLayout.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        ViewHolderDriverDoc holder = (ViewHolderDriverDoc) v.getTag();
//                        addImageLayotOnClick(holder.id, activity,0);
//                    }
//                });
//
//				holder.addImageLayout2.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        ViewHolderDriverDoc holder = (ViewHolderDriverDoc) v.getTag();
//                        addImageLayotOnClick(holder.id, activity,1);
//                    }
//                });
//			}else{
//				holder.deleteImage2.setVisibility(View.GONE);
//				holder.deleteImage1.setVisibility(View.GONE);
//				holder.addImageLayout.setVisibility(View.GONE);
//				holder.addImageLayout2.setVisibility(View.GONE);
//				holder.imageViewUploadDoc.setVisibility(View.GONE);
//
//				holder.rideHistoryItem.setOnClickListener(new View.OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						ViewHolderDriverDoc holder = (ViewHolderDriverDoc) v.getTag();
//						activity.openDocumentDetails(docs.get(holder.id),holder.id);
//					}
//				});
//			}










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


	public void getDocsAsync(final Activity activity) {
		try {
			DialogPopup.showLoadingDialog(activity, getString(R.string.loading));
			String isRequired = String.valueOf(requirement);
			HashMap<String, String> params = new HashMap<String, String>();
			params.put("access_token",accessToken);
			params.put("login_documents", isRequired);
			params.put("app_version", Data.appVersion+"");
			params.put(Constants.BRANDING_IMAGE, String.valueOf(brandingImagesOnly));
			HomeUtil.putDefaultParams(params);

			RestClient.getApiServices().docRequest(params, new Callback<DocRequirementResponse>() {
				@Override
				public void success(DocRequirementResponse docRequirementResponse, Response response) {
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
										data.getIsDocInfoEditable());
								if(brandingImagesOnly == 1 && data.getDocType() != DOC_TYPE_BRANDING_IMAGE){
									continue;
								}
								docs.add(docInfo);
							}
							updateListData(activity.getResources().getString(R.string.no_doc_available), false);
							userPhoneNo = docRequirementResponse.getuserPhoneNo();
							checkForDocumentsSubmit();


						}
					} catch (Exception exception) {
						exception.printStackTrace();
						updateListData(activity.getResources().getString(R.string.error_occured_tap_to_retry), true);
					}
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
							int rotate = getCameraPhotoOrientation(activity, uri, image.getOriginalPath());
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
									newBitmap = getResizedBitmap(rotatedBitmap, newHeight, newWidth);
								} else {
									int newWidth = imgPixel;
									int newHeight = (int) ((oldHeight / oldWidth) * imgPixel);
									newBitmap = getResizedBitmap(rotatedBitmap, newHeight, newWidth);
								}
							}

							File f;
							if (newBitmap != null) {
								f = compressToFile(activity, newBitmap, Bitmap.CompressFormat.JPEG, 100);
								driverDocumentListAdapter.notifyDataSetChanged();
								uploadPicToServer(getActivity(), f, docs.get(index), coloum);
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


	public int getCameraPhotoOrientation(Context context, Uri imageUri, String imagePath){
		int rotate = 0;
		try {
			context.getContentResolver().notifyChange(imageUri, null);
			File imageFile = new File(imagePath);

			ExifInterface exif = new ExifInterface(imageFile.getAbsolutePath());
			int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

			switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_270:
					rotate = 270;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					rotate = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_90:
					rotate = 90;
					break;
			}

			Log.i("RotateImage", "Exif orientation: " + orientation);
			Log.i("RotateImage", "Rotate value: " + rotate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return rotate;
	}


	private void uploadPicToServer(final Activity activity, final File photoFile, final DocInfo docInfo, final int column) {
		try {
			if (AppStatus.getInstance(activity).isOnline(activity)) {
				DialogPopup.showLoadingDialog(activity, getResources().getString(R.string.loading));
				HashMap<String, String> params = new HashMap<>();

				params.put("access_token", accessToken);
				params.put("img_position", String.valueOf(column));
				params.put("doc_type_num", String.valueOf(docInfo.docTypeNum));
				HomeUtil.putDefaultParams(params);

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



	private static File compressToFile(Context context, Bitmap src, Bitmap.CompressFormat format,
									   int quality) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		src.compress(format, quality, os);
		long index2 = System.currentTimeMillis();
		File f = new File(context.getFilesDir(), "temp" + index2 + ".jpg");
		try {
			f.createNewFile();
			byte[] bitmapData = os.toByteArray();

			FileOutputStream fos = new FileOutputStream(f);
			fos.write(bitmapData);
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return f;
	}


	public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
		try {
			int width = bm.getWidth();
			int height = bm.getHeight();
			float scaleWidth = ((float) newWidth) / width;
			float scaleHeight = ((float) newHeight) / height;
			Matrix matrix = new Matrix();
			matrix.postScale(scaleWidth, scaleHeight);
			Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
					matrix, false);

			return resizedBitmap;
		} catch (Exception e) {
			e.printStackTrace();
			return bm;
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
		if(mandatoryDocsSubmitted){
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
