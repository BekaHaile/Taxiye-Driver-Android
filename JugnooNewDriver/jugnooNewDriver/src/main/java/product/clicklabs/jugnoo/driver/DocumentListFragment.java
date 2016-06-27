package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RoundBorderTransform;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.DocInfo;
import product.clicklabs.jugnoo.driver.datastructure.UpdateDriverEarnings;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.DocRequirementResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.Log;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedFile;

public class DocumentListFragment extends Fragment implements ImageChooserListener {

	ProgressBar progressBar;
	TextView textViewInfoDisplay;
	ListView listView;
	String accessToken;


	protected ImageChooserManager imageChooserManager;
	private Bitmap bitmap;
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
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		docs.clear();
		View rootView = inflater.inflate(R.layout.fragment_list, container, false);

		main = (RelativeLayout) rootView.findViewById(R.id.main);
		main.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		ASSL.DoMagic(main);

		progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
		listView = (ListView) rootView.findViewById(R.id.listView);

		driverDocumentListAdapter = new DriverDocumentListAdapter();
		listView.setAdapter(driverDocumentListAdapter);

		textViewInfoDisplay = (TextView) rootView.findViewById(R.id.textViewInfoDisplay);
		textViewInfoDisplay.setText(getResources().getString(R.string.no_doc_available));
		progressBar.setVisibility(View.GONE);

		accessToken = getArguments().getString("access_token");
		getDocsAsync(getActivity());

		return rootView;
	}


	@Override
	public void onDestroy() {
//		if (fetchRidesClient != null) {
//			fetchRidesClient.cancelAllRequests(true);
//		}
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


	static class ViewHolderDriverDoc {
		TextView docType, docRequirement, docStatus;
		RelativeLayout addImageLayout, addImageLayout2;
		RelativeLayout relative, relativeLayoutImageStatus;
		ImageView setCapturedImage, setCapturedImage2, imageViewUploadDoc, imageViewDocStatus;
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
				holder.docType.setTypeface(Data.latoRegular(getActivity()));
				holder.docRequirement = (TextView) convertView.findViewById(R.id.docRequirement);
				holder.docRequirement.setTypeface(Data.latoRegular(getActivity()));
				holder.docStatus = (TextView) convertView.findViewById(R.id.docStatus);
				holder.docStatus.setTypeface(Data.latoRegular(getActivity()));
				holder.setCapturedImage = (ImageView) convertView.findViewById(R.id.setCapturedImage);
				holder.setCapturedImage2 = (ImageView) convertView.findViewById(R.id.setCapturedImage2);
				holder.imageViewDocStatus = (ImageView) convertView.findViewById(R.id.imageViewDocStatus);

				holder.addImageLayout = (RelativeLayout) convertView.findViewById(R.id.addImageLayout);

				holder.addImageLayout.setTag(holder);

				holder.imageViewUploadDoc = (ImageView) convertView.findViewById(R.id.imageViewUploadDoc);

				holder.addImageLayout2 = (RelativeLayout) convertView.findViewById(R.id.addImageLayout2);

				holder.addImageLayout2.setTag(holder);


				holder.relativeLayoutImageStatus = (RelativeLayout) convertView.findViewById(R.id.relativeLayoutImageStatus);
				holder.relative = (RelativeLayout) convertView.findViewById(R.id.relative);

				holder.relative.setTag(holder);
				holder.imageViewUploadDoc.setTag(holder);
				holder.addImageLayout.setTag(holder);
				holder.addImageLayout2.setTag(holder);

				holder.relative.setLayoutParams(new ListView.LayoutParams(720, LayoutParams.WRAP_CONTENT));
				ASSL.DoMagic(holder.relative);


				convertView.setTag(holder);
			} else {
				holder = (ViewHolderDriverDoc) convertView.getTag();
			}

			final DocInfo docInfo = docs.get(position);

			holder.id = position;

			holder.docType.setText(docInfo.docType);
			if (docInfo.docRequirement == 1) {
				holder.docRequirement.setText(getResources().getString(R.string.mandatory));
			} else {
				holder.docRequirement.setText(getResources().getString(R.string.optional));
			}
			holder.docStatus.setText(docInfo.status);

			if (docInfo.status.equalsIgnoreCase("uploaded")) {
				holder.imageViewDocStatus.setImageResource(R.drawable.doc_uploaded);
				holder.docStatus.setText(getResources().getString(R.string.uploaded));
				holder.docStatus.setTextColor(getResources().getColor(R.color.new_orange));
			} else if (docInfo.status.equalsIgnoreCase("2")) {
				holder.docStatus.setText(getResources().getString(R.string.rejected));
				holder.imageViewDocStatus.setImageResource(R.drawable.doc_rejected);
				holder.docStatus.setTextColor(getResources().getColor(R.color.red_delivery));
			} else if (docInfo.status.equalsIgnoreCase("3")) {
				holder.docStatus.setText(getResources().getString(R.string.verified));
				holder.imageViewDocStatus.setImageResource(R.drawable.doc_verified);
				holder.docStatus.setTextColor(getResources().getColor(R.color.green_delivery));
			} else if (docInfo.status.equalsIgnoreCase("1")) {
				holder.docStatus.setText(getResources().getString(R.string.approval_pending));
				holder.imageViewDocStatus.setImageResource(R.drawable.doc_wating);
				holder.docStatus.setTextColor(getResources().getColor(R.color.blue_status));
			}

			if (docInfo.getFile() != null) {
				docInfo.isExpended = true;
				Picasso.with(getActivity()).load(docInfo.getFile())
						.transform(new RoundBorderTransform()).resize(300, 300).centerCrop()
						//.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
						.into(holder.setCapturedImage);
				if (!docInfo.status.equalsIgnoreCase("2")) {
					holder.addImageLayout.setEnabled(false);
				} else {
					holder.addImageLayout.setEnabled(true);
				}
			} else {
				holder.setCapturedImage.setImageResource(R.drawable.transparent);
			}

			if (docInfo.getFile1() != null) {
				docInfo.isExpended = true;
				Picasso.with(getActivity()).load(docInfo.getFile1())
						.transform(new RoundBorderTransform()).resize(300, 300).centerCrop()
						//.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
						.into(holder.setCapturedImage2);
				if (!docInfo.status.equalsIgnoreCase("2")) {
					holder.addImageLayout2.setEnabled(false);
				} else {
					holder.addImageLayout2.setEnabled(true);
				}
			} else {
				holder.setCapturedImage2.setImageResource(R.drawable.transparent);
			}

			if (docInfo.url.size() > 0 || docInfo.status.equalsIgnoreCase("2")) {
				try {
					docInfo.isExpended = true;

					if (docInfo.url.size() > 0) {
						Picasso.with(getActivity()).load(docInfo.url.get(0))
								.transform(new RoundBorderTransform()).resize(300, 300).centerCrop()
								//.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
								.into(holder.setCapturedImage);
					}
					if (!docInfo.status.equalsIgnoreCase("2")) {
						holder.addImageLayout.setEnabled(false);
					} else {
						holder.addImageLayout.setEnabled(true);
						holder.setCapturedImage.setImageResource(R.drawable.reload_image);
					}
					if (docInfo.url.size() > 1) {
						Picasso.with(getActivity()).load(docInfo.url.get(1))
								.transform(new RoundBorderTransform()).resize(300, 300).centerCrop()
								//.memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
								.into(holder.setCapturedImage2);
						if (!docInfo.status.equalsIgnoreCase("2")) {
							holder.addImageLayout2.setEnabled(false);
						} else {
							holder.addImageLayout2.setEnabled(true);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (docInfo.isExpended) {
				holder.relativeLayoutImageStatus.setVisibility(View.VISIBLE);
				holder.imageViewUploadDoc.setVisibility(View.GONE);
				holder.docRequirement.setVisibility(View.GONE);
			} else {
				holder.relativeLayoutImageStatus.setVisibility(View.GONE);
				holder.imageViewUploadDoc.setVisibility(View.VISIBLE);
				holder.docRequirement.setVisibility(View.VISIBLE);
			}

			holder.imageViewUploadDoc.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					ViewHolderDriverDoc holder = (ViewHolderDriverDoc) v.getTag();
					uploadfile(getActivity(), holder.id);
					coloum = 0;

				}
			});

			holder.addImageLayout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						ViewHolderDriverDoc holder = (ViewHolderDriverDoc) v.getTag();
						rejectionId = holder.id;
						if (docInfo.status.equalsIgnoreCase("2")) {
							DialogPopup.alertPopupWithListener(getActivity(), getResources().getString(R.string.rejection_reason), docInfo.reason,
									new View.OnClickListener() {
										@Override
										public void onClick(View v) {
											uploadfile(getActivity(), rejectionId);
											coloum = 0;
										}
									});
						} else {
							uploadfile(getActivity(), holder.id);
							coloum = 0;
						}
					} catch (Resources.NotFoundException e) {
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
						if (docInfo.status.equalsIgnoreCase("2")) {
							DialogPopup.alertPopupWithListener(getActivity(), getResources().getString(R.string.rejection_reason), docInfo.reason,
									new View.OnClickListener() {
										@Override
										public void onClick(View v) {
											uploadfile(getActivity(), rejectionId);
											coloum = 1;
										}
									});
						} else {
							uploadfile(getActivity(), holder.id);
							coloum = 1;
						}
					} catch (Resources.NotFoundException e) {
						e.printStackTrace();
					}
				}
			});

			return convertView;
		}

	}

	private void getDocsAsync(final Activity activity) {
		try {
			progressBar.setVisibility(View.VISIBLE);
			RestClient.getApiServices().docRequest(accessToken, new Callback<DocRequirementResponse>() {
				@Override
				public void success(DocRequirementResponse docRequirementResponse, Response response) {
					try {
						String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
						JSONObject jObj;
						jObj = new JSONObject(jsonString);
						if (!jObj.isNull("error")) {
							String errorMessage = jObj.getString("error");
							if (Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())) {
								HomeActivity.logoutUser(activity);
							}
						} else {

							for (int i = 0; i < docRequirementResponse.getData().size(); i++) {
								DocRequirementResponse.DocumentData data = docRequirementResponse.getData().get(i);
								DocInfo docInfo = new DocInfo(data.getDocTypeText(), data.getDocTypeNum(), data.getDocRequirement(),
										data.getDocStatus(), data.getDocUrl(), data.getReason());
								docs.add(docInfo);
							}
							updateListData("hello", false);
							userPhoneNo = docRequirementResponse.getuserPhoneNo();

						}
					} catch (Exception exception) {
						exception.printStackTrace();
						updateListData(activity.getResources().getString(R.string.error_occured_tap_to_retry), true);
					}
					progressBar.setVisibility(View.GONE);
				}

				@Override
				public void failure(RetrofitError error) {
					Log.i("DocError", error.toString());
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

	public void uploadfile(final Activity activity, int index) {

		try {
			final Dialog dialog = new Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar);
			dialog.getWindow().getAttributes().windowAnimations = R.style.Animations_LoadingDialogFade;
			dialog.setContentView(R.layout.dialog_upload_document);

			FrameLayout frameLayout = (FrameLayout) dialog.findViewById(R.id.addImage);
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
			btnCancel.setTypeface(Data.latoRegular(activity));

			DocumentListFragment.this.index = index;

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
		int chooserType = ChooserType.REQUEST_CAPTURE_PICTURE;
		imageChooserManager = new ImageChooserManager(this, ChooserType.REQUEST_CAPTURE_PICTURE, "myfolder", true);
		imageChooserManager.setImageChooserListener(this);
		imageChooserManager.clearOldFiles();
		try {
			String filePath = imageChooserManager.choose();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void chooseImageFromGallery() {
		int chooserType = ChooserType.REQUEST_PICK_PICTURE;
		imageChooserManager = new ImageChooserManager(this, ChooserType.REQUEST_PICK_PICTURE, "myfolder", true);
		imageChooserManager.setImageChooserListener(this);
		try {
			String filePath = imageChooserManager.choose();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private final int RESULT_OK = 1;

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if ((requestCode == ChooserType.REQUEST_PICK_PICTURE || requestCode == ChooserType.REQUEST_CAPTURE_PICTURE)) {
			imageChooserManager.submit(requestCode, data);
		}

	}


	@Override
	public void onImageChosen(final ChosenImage image) {

		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub

				Log.v("onImageChosen called", "onImageChosen called");
				if (image != null) {
					// Use the image
					// image.getFilePathOriginal();
					// image.getFileThumbnail();
					// image.getFileThumbnailSmall();

					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inPreferredConfig = Bitmap.Config.ARGB_8888;
					Bitmap bitmap = BitmapFactory.decodeFile(image.getFilePathOriginal(), options);
//					selected_photo.setImageBitmap(bitmap);

					File f = compressToFile(getActivity(), bitmap, Bitmap.CompressFormat.JPEG, 50, index);
					docs.get(index).isExpended = true;
					driverDocumentListAdapter.notifyDataSetChanged();
					uploadPicToServer(getActivity(), f, docs.get(index).docTypeNum, userPhoneNo, image);


				}
			}
		});
	}

	@Override
	public void onError(final String reason) {
		Toast.makeText(getActivity(), reason, Toast.LENGTH_LONG).show();
	}

	private void uploadPicToServer(final Activity activity, File photoFile, Integer docNumType, String userPhoneNo, final ChosenImage image) {
		DialogPopup.showLoadingDialog(activity, getResources().getString(R.string.loading));
		HashMap<String, String> params = new HashMap<String, String>();

		params.put("access_token", accessToken);
		params.put("doc_type_num", String.valueOf(docNumType));

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

						if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag)) {

							if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
								DialogPopup.alertPopup(activity, "", message);
								docs.get(index).status = jObj.getString("status");
								if (coloum == 0) {
									docs.get(index).setFile(new File(image.getFileThumbnail()));
								} else {
									docs.get(index).setFile1(new File(image.getFileThumbnail()));
								}
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
	}

	private static File compressToFile(Context context, Bitmap src, Bitmap.CompressFormat format,
									   int quality, int index) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		src.compress(format, quality, os);
		File f = new File(context.getExternalCacheDir(), "temp" + index + ".jpg");
		try {
			f.createNewFile();
			byte[] bitmapdata = os.toByteArray();

			FileOutputStream fos = new FileOutputStream(f);
			fos.write(bitmapdata);
			fos.flush();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return f;
	}


}
