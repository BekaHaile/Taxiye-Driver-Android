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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kbeanie.multipicker.api.CacheLocation;
import com.kbeanie.multipicker.api.CameraImagePicker;
import com.kbeanie.multipicker.api.ImagePicker;
import com.kbeanie.multipicker.api.Picker;
import com.kbeanie.multipicker.api.callbacks.ImagePickerCallback;
import com.kbeanie.multipicker.api.entity.ChosenImage;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RoundBorderTransform;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.DocInfo;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.DocRequirementResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.PermissionCommon;
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

	private Bitmap bitmap;
	private DriverDocumentActivity activity;
	DriverDocumentListAdapter driverDocumentListAdapter;

	RelativeLayout main;


	ArrayList<DocInfo> docs = new ArrayList<>();
	String userPhoneNo, docUrl;
	int index = 0;
	int coloum = 0;
	int rejectionId;
	boolean isExpended = false;


	public DocumentListFragment() {

	}


	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		activity = (DriverDocumentActivity) getActivity();
		activity.registerReceiver(broadcastReceiver, new IntentFilter(Constants.ACTION_UPDATE_DOCUMENT_LIST));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		docs.clear();
		View rootView = inflater.inflate(R.layout.fragment_list, container, false);

		main = (RelativeLayout) rootView.findViewById(R.id.main);

		rootView.findViewById(R.id.progressBar).setVisibility(View.GONE);
		listView = (ListView) rootView.findViewById(R.id.listView);

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
		TextView docType, docRequirement, docStatus, docRejected;
		RelativeLayout addImageLayout, addImageLayout2, relativeLayoutSelectPicture;
		RelativeLayout relativeLayoutImageStatus;
		LinearLayout rideHistoryItem;
		ImageView setCapturedImage, setCapturedImage2, imageViewUploadDoc, imageViewDocStatus, deleteImage2, deleteImage1,
				imageViewDocStatusImage,imageViewInfo;
		int id;
	}

	class DriverDocumentListAdapter extends BaseAdapter {
		LayoutInflater mInflater;
		ViewHolderDriverDoc holder;
		//ViewHolderDriverDoc holder;

		public DriverDocumentListAdapter() {
			mInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
			if (convertView == null) {
				holder = new ViewHolderDriverDoc();
				convertView = mInflater.inflate(R.layout.list_item_documents, null);

				holder.docType = (TextView) convertView.findViewById(R.id.docType);
				holder.docType.setTypeface(Fonts.mavenMedium(getActivity()));
				holder.docRejected = (TextView) convertView.findViewById(R.id.docRejected);
				holder.docRejected.setTypeface(Fonts.mavenRegular(getActivity()));

				holder.docRequirement = (TextView) convertView.findViewById(R.id.docRequirement);
				holder.docRequirement.setTypeface(Fonts.mavenRegular(getActivity()));
				holder.docStatus = (TextView) convertView.findViewById(R.id.docStatus);
				holder.docStatus.setTypeface(Fonts.mavenRegular(getActivity()));
				holder.setCapturedImage = (ImageView) convertView.findViewById(R.id.setCapturedImage);
				holder.setCapturedImage2 = (ImageView) convertView.findViewById(R.id.setCapturedImage2);
				holder.imageViewDocStatus = (ImageView) convertView.findViewById(R.id.imageViewDocStatus);
				holder.imageViewDocStatusImage = (ImageView) convertView.findViewById(R.id.imageViewDocStatusImage);
				holder.imageViewInfo = (ImageView) convertView.findViewById(R.id.imageViewInfo);

				holder.deleteImage1 = (ImageView) convertView.findViewById(R.id.deleteImage1);
				holder.deleteImage1.setTag(holder);
				holder.deleteImage2 = (ImageView) convertView.findViewById(R.id.deleteImage2);
				holder.deleteImage2.setTag(holder);

				holder.addImageLayout = (RelativeLayout) convertView.findViewById(R.id.addImageLayout);
				holder.rideHistoryItem = (LinearLayout) convertView.findViewById(R.id.rideHistoryItem);

				holder.addImageLayout.setTag(holder);

				holder.imageViewUploadDoc = (ImageView) convertView.findViewById(R.id.imageViewUploadDoc);
				holder.relativeLayoutSelectPicture = (RelativeLayout) convertView.findViewById(R.id.relativeLayoutSelectPicture);

				holder.addImageLayout2 = (RelativeLayout) convertView.findViewById(R.id.addImageLayout2);

				holder.addImageLayout2.setTag(holder);


				holder.relativeLayoutImageStatus = (RelativeLayout) convertView.findViewById(R.id.relativeLayoutImageStatus);

				holder.imageViewUploadDoc.setTag(holder);
				holder.relativeLayoutSelectPicture.setTag(holder);
				holder.addImageLayout.setTag(holder);
				holder.addImageLayout2.setTag(holder);


				convertView.setTag(holder);
			} else {
				holder = (ViewHolderDriverDoc) convertView.getTag();
			}

			DocInfo docInfo = docs.get(position);

			holder.id = position;


			if(docInfo.docCount<2){
				holder.addImageLayout2.setVisibility(View.GONE);
			}
			//MAIN THODI DER MEIN AAUNGA

			if (docInfo.docRequirement == 1 || docInfo.docRequirement == 3) {
				holder.docRequirement.setText(getResources().getString(R.string.mandatory));
				holder.docType.setText(docInfo.docType+"*");
			} else if (docInfo.docRequirement == 4) {
				holder.docRequirement.setText(getResources().getString(R.string.required));
				holder.docType.setText(docInfo.docType+"*");
			} else {
				holder.docRequirement.setText(getResources().getString(R.string.optional));
				holder.docType.setText(docInfo.docType);
			}
			holder.docStatus.setText(getResources().getString(R.string.uploading));

			holder.docRejected.setVisibility(View.GONE);

			holder.addImageLayout.setVisibility(View.VISIBLE);
			holder.addImageLayout2.setVisibility(View.VISIBLE);

			if (docInfo.status.equalsIgnoreCase("uploaded") || docInfo.status.equalsIgnoreCase("4")) {
				holder.imageViewDocStatus.setImageResource(R.drawable.doc_uploaded);
				holder.docStatus.setText(getResources().getString(R.string.uploaded));
				holder.imageViewDocStatusImage.setVisibility(View.VISIBLE);
				holder.imageViewDocStatusImage.setImageResource(R.drawable.uploaded_doc_status);
				holder.docStatus.setTextColor(getResources().getColor(R.color.green_doc_status));
			} else if (docInfo.status.equalsIgnoreCase("2")) {
				holder.docStatus.setText(getResources().getString(R.string.rejected));
				holder.imageViewDocStatus.setImageResource(R.drawable.doc_rejected);
				holder.docRejected.setVisibility(View.VISIBLE);
				holder.imageViewDocStatusImage.setVisibility(View.VISIBLE);
				holder.imageViewDocStatusImage.setImageResource(R.drawable.rejected_doc_status);
				holder.docStatus.setTextColor(getResources().getColor(R.color.red_delivery));
			} else if (docInfo.status.equalsIgnoreCase("3")) {
				holder.docStatus.setText(getResources().getString(R.string.verified));
				holder.imageViewDocStatus.setImageResource(R.drawable.doc_verified);
				holder.docStatus.setTextColor(getResources().getColor(R.color.green_doc_status));
				holder.imageViewDocStatusImage.setVisibility(View.GONE);
			} else if (docInfo.status.equalsIgnoreCase("1")) {
				holder.docStatus.setText(getResources().getString(R.string.approval_pending));
				holder.imageViewDocStatus.setImageResource(R.drawable.doc_waiting);
				holder.imageViewDocStatusImage.setVisibility(View.GONE);
				holder.docStatus.setTextColor(getResources().getColor(R.color.themeColor));
			}

			if (docInfo.status.equalsIgnoreCase("3") || docInfo.isEditable ==0) {
				holder.addImageLayout.setEnabled(false);
				holder.addImageLayout2.setEnabled(false);
			}

			if(docInfo.isEditable ==0){
				holder.addImageLayout.setEnabled(false);
				holder.addImageLayout2.setEnabled(false);
				holder.imageViewUploadDoc.setEnabled(false);
				holder.relativeLayoutSelectPicture.setEnabled(false);
			}else {
				holder.addImageLayout.setEnabled(true);
				holder.addImageLayout2.setEnabled(true);
				holder.imageViewUploadDoc.setEnabled(true);
				holder.relativeLayoutSelectPicture.setEnabled(true);
			}

			if (docInfo.getFile() != null) {
				docInfo.isExpended = true;
				Picasso.with(getActivity()).load(docInfo.getFile())
						.transform(new RoundBorderTransform()).resize(300, 300).centerCrop()
						.into(holder.setCapturedImage);

				if(docInfo.isEditable ==1) {
					holder.deleteImage1.setVisibility(View.VISIBLE);
				}else {
					holder.deleteImage1.setVisibility(View.GONE);
				}

				if (!docInfo.status.equalsIgnoreCase("2")) {
					holder.addImageLayout.setEnabled(false);
				} else {
					holder.addImageLayout.setEnabled(true);
					holder.deleteImage1.setVisibility(View.GONE);
					holder.deleteImage2.setVisibility(View.GONE);
				}
			} else {
				holder.setCapturedImage.setImageResource(R.drawable.transparent);
				holder.deleteImage1.setVisibility(View.GONE);
			}

			if (docInfo.getFile1() != null) {
				docInfo.isExpended = true;
				Picasso.with(getActivity()).load(docInfo.getFile1())
						.transform(new RoundBorderTransform()).resize(300, 300).centerCrop()
						//.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
						.into(holder.setCapturedImage2);
				if(docInfo.isEditable ==1) {
					holder.deleteImage2.setVisibility(View.VISIBLE);
				}else {
					holder.deleteImage2.setVisibility(View.GONE);
				}
				if (!docInfo.status.equalsIgnoreCase("2")) {
					holder.addImageLayout2.setEnabled(false);
				} else {
					holder.addImageLayout2.setEnabled(true);
				}
			} else {
				holder.setCapturedImage2.setImageResource(R.drawable.transparent);
				holder.deleteImage2.setVisibility(View.GONE);
			}

			if ( docInfo.status.equalsIgnoreCase("2")
					|| (docInfo.url.get(0) != null && !"".equalsIgnoreCase(docInfo.url.get(0)))
					|| ( docInfo.url.get(1) != null && !"".equalsIgnoreCase(docInfo.url.get(1)))) {

				try {
					docInfo.isExpended = true;

					if (docInfo.url.get(0) != null && !"".equalsIgnoreCase(docInfo.url.get(0))) {
						Picasso.with(getActivity()).load(docInfo.url.get(0))
								.transform(new RoundBorderTransform()).resize(300, 300).centerCrop()
								//.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
								.into(holder.setCapturedImage);
						if(docInfo.isEditable ==1) {
							holder.deleteImage1.setVisibility(View.VISIBLE);
						}else {
							holder.deleteImage1.setVisibility(View.GONE);
						}
					}
					if (!docInfo.status.equalsIgnoreCase("2")) {
						holder.addImageLayout.setEnabled(false);
					} else {
						holder.addImageLayout.setEnabled(true);
						holder.setCapturedImage.setImageResource(R.drawable.reload_image);
						holder.deleteImage1.setVisibility(View.GONE);
						holder.deleteImage2.setVisibility(View.GONE);
						docInfo.setFile(null);
						docInfo.setFile1(null);
					}

					if(docInfo.status.equalsIgnoreCase("4")){
						holder.addImageLayout.setEnabled(true);
					}

					if (docInfo.url.get(1) != null && !"".equalsIgnoreCase(docInfo.url.get(1))) {
						Picasso.with(getActivity()).load(docInfo.url.get(1))
								.transform(new RoundBorderTransform()).resize(300, 300).centerCrop()
								//.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
								.into(holder.setCapturedImage2);
						if(docInfo.isEditable ==1) {
							holder.deleteImage2.setVisibility(View.VISIBLE);
						}else {
							holder.deleteImage2.setVisibility(View.GONE);
						}
						if (!docInfo.status.equalsIgnoreCase("2")) {
							holder.addImageLayout2.setEnabled(false);
						} else {
							holder.addImageLayout2.setEnabled(true);
						}
					}
				} catch (Exception e) {
					holder.deleteImage1.setVisibility(View.GONE);
					holder.deleteImage2.setVisibility(View.GONE);
					e.printStackTrace();
				}
			}



			if (docInfo.status.equalsIgnoreCase("3")) {
				if((docInfo.url.get(0) == null || "".equalsIgnoreCase(docInfo.url.get(0)))){
					holder.addImageLayout.setVisibility(View.GONE);
					holder.deleteImage1.setVisibility(View.GONE);
				}
				if((docInfo.url.get(1) == null || "".equalsIgnoreCase(docInfo.url.get(1)))){
					holder.addImageLayout2.setVisibility(View.GONE);
					holder.deleteImage2.setVisibility(View.GONE);
				}
			}

			holder.docType.setTag(holder);
			holder.imageViewInfo.setTag(holder);
			if(docInfo.getDocInstructions()==null){
				holder.imageViewInfo.setVisibility(View.GONE);
				holder.docType.setOnClickListener(null);

			}else{
				holder.imageViewInfo.setVisibility(View.VISIBLE);
				View.OnClickListener onClickListener = new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						ViewHolderDriverDoc holder = (ViewHolderDriverDoc) v.getTag();
						DialogPopup.alertPopup(activity,docs.get(holder.id).docType,docs.get(holder.id).getDocInstructions(),true,false);
					}
				};
				holder.docType.setOnClickListener(onClickListener);
				holder.imageViewInfo.setOnClickListener(onClickListener);

			}

			if (docInfo.isExpended) {
				holder.relativeLayoutImageStatus.setVisibility(View.VISIBLE);
				holder.imageViewUploadDoc.setVisibility(View.GONE);
				holder.docRequirement.setVisibility(View.GONE);
				holder.imageViewInfo.setVisibility(View.GONE);
			} else {
				holder.relativeLayoutImageStatus.setVisibility(View.GONE);
				holder.imageViewUploadDoc.setVisibility(View.VISIBLE);
				holder.docRequirement.setVisibility(View.VISIBLE);
				holder.imageViewInfo.setVisibility(View.VISIBLE);
			}
			if(brandingImagesOnly == 1){
				holder.docRequirement.setVisibility(View.GONE);
				holder.imageViewInfo.setVisibility(View.GONE);
			}

			if (docInfo.status.equalsIgnoreCase("3") || docInfo.status.equalsIgnoreCase("1")) {
				holder.addImageLayout.setVisibility(View.GONE);
				holder.deleteImage1.setVisibility(View.GONE);
				holder.addImageLayout2.setVisibility(View.GONE);
				holder.deleteImage2.setVisibility(View.GONE);
				holder.docType.setTextColor(getResources().getColor(R.color.grey_light_doc_status));
				holder.imageViewDocStatus.setVisibility(View.VISIBLE);
			} else {
				holder.addImageLayout.setVisibility(View.VISIBLE);
				holder.addImageLayout2.setVisibility(View.VISIBLE);
				holder.imageViewDocStatus.setVisibility(View.GONE);
				holder.docType.setTextColor(getResources().getColor(R.color.themeColor));
			}



			if((docInfo.url.get(0) == null || "".equalsIgnoreCase(docInfo.url.get(0))) && (docInfo.url.get(1) == null || "".equalsIgnoreCase(docInfo.url.get(1)))){
				docInfo.isExpended = false;
			}

			holder.imageViewUploadDoc.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					ViewHolderDriverDoc holder = (ViewHolderDriverDoc) v.getTag();
					uploadfile(activity, holder.id);
					coloum = 0;

				}
			});

			holder.deleteImage1.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					ViewHolderDriverDoc holder = (ViewHolderDriverDoc) v.getTag();
					DocInfo docInfodeleteImage1 = docs.get(holder.id);
					docInfodeleteImage1.setFile(null);
					docInfodeleteImage1.url.set(0, null);
					holder.addImageLayout.setEnabled(true);
					coloum =0;
					deleteImage(getActivity(), docInfodeleteImage1.docTypeNum);
					holder.deleteImage1.setVisibility(View.GONE);
					driverDocumentListAdapter.notifyDataSetChanged();
				}
			});

			holder.deleteImage2.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					ViewHolderDriverDoc holder = (ViewHolderDriverDoc) v.getTag();
					DocInfo docInfodeleteImage2 = docs.get(holder.id);
					docInfodeleteImage2.setFile1(null);
					docInfodeleteImage2.url.set(1, null);
					holder.addImageLayout2.setEnabled(true);
					coloum =1;
					deleteImage(getActivity(), docInfodeleteImage2.docTypeNum);
					holder.deleteImage2.setVisibility(View.GONE);
					driverDocumentListAdapter.notifyDataSetChanged();
				}
			});

			holder.addImageLayout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						ViewHolderDriverDoc holder = (ViewHolderDriverDoc) v.getTag();
						rejectionId = holder.id;
						DocInfo docInfoImageLayout = docs.get(holder.id);
						if (docInfoImageLayout.status.equalsIgnoreCase("2")) {

							DialogPopup.alertPopupTwoButtonsWithListeners(getActivity(),
									getResources().getString(R.string.rejection_reason),
									docInfoImageLayout.reason,
									getResources().getString(R.string.upload_again),
									getResources().getString(R.string.cancel),
									new View.OnClickListener() {
										@Override
										public void onClick(View v) {
											uploadfile(activity, rejectionId);
											coloum = 0;
										}
									},
									new View.OnClickListener() {
										@Override
										public void onClick(View v) {

										}
									}, true, true);
						} else {
							uploadfile(activity, holder.id);
							coloum = 0;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			holder.addImageLayout2.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						ViewHolderDriverDoc holder = (ViewHolderDriverDoc) v.getTag();
						rejectionId = holder.id;
						DocInfo docInfoImageLayout2 = docs.get(holder.id);
						if (docInfoImageLayout2.status.equalsIgnoreCase("2")) {
							DialogPopup.alertPopupTwoButtonsWithListeners(getActivity(),
									getResources().getString(R.string.rejection_reason),
									docInfoImageLayout2.reason,
									getResources().getString(R.string.upload_again),
									getResources().getString(R.string.cancel),
									new View.OnClickListener() {
										@Override
										public void onClick(View v) {
											uploadfile(activity, rejectionId);
											coloum = 1;
										}
									},
									new View.OnClickListener() {
										@Override
										public void onClick(View v) {

										}
									}, true, true);
						} else {
							uploadfile(activity, holder.id);
							coloum = 1;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			if(docInfo.docCount<2){
				holder.addImageLayout2.setVisibility(View.GONE);
			}


			return convertView;
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
										data.getInstructions(), data.getGalleryRestricted());
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

	public void uploadfile(final DriverDocumentActivity activity, int index) {

		DocumentListFragment.this.index = index;
//		String cameraText = getResources().getString(R.string.upload)+" "+docs.get(index).docType
//				+" "+getResources().getString(R.string.image);
//
//		Log.i("count", "= "+activity.getSupportFragmentManager().getBackStackEntryCount());
//		activity.getTransactionUtils().openSelfEnrollmentCameraFragment1(activity,
//				activity.getRelativeLayoutContainer(), cameraText, "bottom");

		try {
			final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.dialog_upload_document);

			RelativeLayout frameLayout = (RelativeLayout) dialog.findViewById(R.id.addImage);
			new ASSL(activity, frameLayout, 1134, 720, true);

			WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
			layoutParams.dimAmount = 0.6f;
			dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
			dialog.setCancelable(true);
			dialog.setCanceledOnTouchOutside(true);

			LinearLayout LayoutCamera, LayoutGallery;

			LayoutCamera = (LinearLayout) dialog.findViewById(R.id.LayoutCamera);
			LayoutGallery = (LinearLayout) dialog.findViewById(R.id.LAyoutGallery);

			final Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
			btnCancel.setTypeface(Fonts.mavenRegular(activity));

			DocInfo docInfo = docs.get(index);
			if((docInfo.getGalleryRestricted() == null
					&& docInfo.docType.toLowerCase().contains(BRANDING_IMAGE.toLowerCase()))
					|| (docInfo.getGalleryRestricted() != null
						&& docInfo.getGalleryRestricted() == 1)){
				chooseImageFromCamera();
				return;
			}


			LayoutGallery.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					chooseImageFromGallery();
					dialog.dismiss();
				}

			});
			LayoutCamera.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					chooseImageFromCamera();
					dialog.dismiss();
				}
			});

			btnCancel.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {

					dialog.dismiss();
				}
			});


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
			getActivity().runOnUiThread(new Runnable() {

				@Override
				public void run() {

					Log.v("onImageChosen called", "onImageChosen called");
					try {
						final ChosenImage image = list.get(0);
						if (image != null) {
							// Use the image
							// image.getFilePathOriginal();
							// image.getFileThumbnail();
							// image.getFileThumbnailSmall();

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

							Uri uri = Uri.fromFile(new File(image.getOriginalPath()));
							int rotate = getCameraPhotoOrientation(activity, uri, image.getOriginalPath());
							Bitmap rotatedBitmap = null;
							Matrix rotateMatrix = new Matrix();
							rotateMatrix.postRotate(rotate);
							rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), rotateMatrix, false);


							//					selected_photo.setImageBitmap(bitmap);
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

							File f = null;
							if (newBitmap != null) {
								f = compressToFile(getActivity(), newBitmap, Bitmap.CompressFormat.JPEG, 100, index);
								docs.get(index).isExpended = true;
								driverDocumentListAdapter.notifyDataSetChanged();
								uploadPicToServer(getActivity(), f, docs.get(index).docTypeNum);
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

		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
//				Toast.makeText(getActivity(), reason, Toast.LENGTH_LONG).show();
			}
		});
	}

	public void uploadToServer(File f){
		uploadPicToServer(getActivity(), f, docs.get(index).docTypeNum);
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


	private void uploadPicToServer(final Activity activity, final File photoFile, Integer docNumType) {
		try {
			if (AppStatus.getInstance(activity).isOnline(activity)) {
				DialogPopup.showLoadingDialog(activity, getResources().getString(R.string.loading));
				HashMap<String, String> params = new HashMap<String, String>();

				params.put("access_token", accessToken);
				params.put("img_position", String.valueOf(coloum));
				params.put("doc_type_num", String.valueOf(docNumType));
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
										docs.get(index).status = jObj.getString("status");
										if (coloum == 0) {
											docs.get(index).setFile(photoFile);

										} else {
											docs.get(index).setFile1(photoFile);
										}
										driverDocumentListAdapter.notifyDataSetChanged();
										checkForDocumentsSubmit();
									} else if (ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag) {
										DialogPopup.alertPopup(activity, "", message);
										docs.get(index).isExpended = false;
										driverDocumentListAdapter.notifyDataSetChanged();
									} else {
										DialogPopup.alertPopup(activity, "", message);
										docs.get(index).isExpended = false;
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
							docs.get(index).isExpended = false;
							driverDocumentListAdapter.notifyDataSetChanged();
						}
						DialogPopup.dismissLoadingDialog();
					}

					@Override
					public void failure(RetrofitError error) {
						DialogPopup.dismissLoadingDialog();
						docs.get(index).isExpended = false;
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


	private void deleteImage(final Activity activity, Integer docNumType) {
		try {
			if (AppStatus.getInstance(activity).isOnline(activity)) {
				DialogPopup.showLoadingDialog(activity, getResources().getString(R.string.loading));
				HashMap<String, String> params = new HashMap<String, String>();

				params.put("access_token", accessToken);
				params.put("img_position", String.valueOf(coloum));
				params.put("doc_type_num", String.valueOf(docNumType));
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
										docs.get(index).isExpended = false;
										docs.get(index).status = "-1";
										driverDocumentListAdapter.notifyDataSetChanged();

									} else if (ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag) {
										DialogPopup.alertPopup(activity, "", message);
										docs.get(index).isExpended = false;
										driverDocumentListAdapter.notifyDataSetChanged();
									} else {
										DialogPopup.alertPopup(activity, "", message);
										docs.get(index).isExpended = false;
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
							docs.get(index).isExpended = false;
							driverDocumentListAdapter.notifyDataSetChanged();
						}
						DialogPopup.dismissLoadingDialog();
					}

					@Override
					public void failure(RetrofitError error) {
						DialogPopup.dismissLoadingDialog();
						docs.get(index).isExpended = false;
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
									   int quality, int index) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		src.compress(format, quality, os);
		long index2 = System.currentTimeMillis();
		File f = new File(context.getFilesDir(), "temp" + index2 + ".jpg");
		try {
			f.createNewFile();
			byte[] bitmapdata = os.toByteArray();

			FileOutputStream fos = new FileOutputStream(f);
			fos.write(bitmapdata);
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
			if((docInfo.docRequirement.equals(1)
					|| docInfo.docRequirement.equals(3)
					|| docInfo.docRequirement.equals(4))
					&&
					(!docInfo.status.equalsIgnoreCase("uploaded")
						&& !docInfo.status.equalsIgnoreCase("4"))){
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
}
