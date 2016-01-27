package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.loopj.android.http.AsyncHttpClient;
import com.squareup.picasso.CircleTransform;
import com.squareup.picasso.MemoryPolicy;
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
import product.clicklabs.jugnoo.driver.datastructure.RideInfo;
import product.clicklabs.jugnoo.driver.datastructure.UpdateDriverEarnings;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.BookingHistoryResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.DocRequirementResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.FlurryEventNames;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedFile;
import rmn.androidscreenlibrary.ASSL;

public class DocumentListFragment extends Fragment implements ImageChooserListener {

	ProgressBar progressBar;
	TextView textViewInfoDisplay;
	ListView listView;


	protected ImageChooserManager imageChooserManager;
	private Bitmap bitmap;
	DriverDocumentListAdapter driverDocumentListAdapter;

	RelativeLayout main;

	AsyncHttpClient fetchRidesClient;

	ArrayList<DocInfo> docs = new ArrayList<>();
	int index = 0;

	UpdateDriverEarnings updateDriverEarnings;

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

		progressBar.setVisibility(View.GONE);
		getDocsAsync(getActivity());

		return rootView;
	}


	@Override
	public void onDestroy() {
		if (fetchRidesClient != null) {
			fetchRidesClient.cancelAllRequests(true);
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


	class ViewHolderDriverRides {
		TextView docType, docRequirement, docStatus;
		RelativeLayout addImageLayout, addImageLayout2;
		RelativeLayout relative;
		ImageView setCapturedImage;
		int id;
	}

	class DriverDocumentListAdapter extends BaseAdapter {
		LayoutInflater mInflater;
		ViewHolderDriverRides holder;

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
		public View getView(final int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				holder = new ViewHolderDriverRides();
				convertView = mInflater.inflate(R.layout.list_item_documents, null);

				holder.docType = (TextView) convertView.findViewById(R.id.docType);
				holder.docType.setTypeface(Data.latoRegular(getActivity()));
				holder.docRequirement = (TextView) convertView.findViewById(R.id.docRequirement);
				holder.docRequirement.setTypeface(Data.latoRegular(getActivity()));
				holder.docStatus = (TextView) convertView.findViewById(R.id.docStatus);
				holder.docStatus.setTypeface(Data.latoRegular(getActivity()));
				holder.setCapturedImage = (ImageView) convertView.findViewById(R.id.setCapturedImage);


				holder.addImageLayout = (RelativeLayout) convertView.findViewById(R.id.addImageLayout);

				holder.addImageLayout.setTag(holder);

				holder.addImageLayout2 = (RelativeLayout) convertView.findViewById(R.id.addImageLayout2);

				holder.addImageLayout2.setTag(holder);

				holder.relative = (RelativeLayout) convertView.findViewById(R.id.relative);

				holder.relative.setTag(holder);
				holder.addImageLayout.setTag(position);

				holder.relative.setLayoutParams(new ListView.LayoutParams(720, LayoutParams.WRAP_CONTENT));
				ASSL.DoMagic(holder.relative);



				convertView.setTag(holder);
			} else {
				holder = (ViewHolderDriverRides) convertView.getTag();
			}


			DocInfo docInfo = docs.get(position);

			holder.id = position;

			holder.docType.setText(docInfo.docType);
			holder.docRequirement.setText(docInfo.docRequirement);
			holder.docStatus.setText(docInfo.status);

			if (docInfo.getFile() != null) {
				Picasso.with(getActivity()).load(docInfo.getFile())
						.transform(new RoundBorderTransform()).resize(300, 300).centerCrop().memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
						.into(holder.setCapturedImage);
			}

			holder.addImageLayout.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					int position = (int) v.getTag();
					uploadfile(getActivity(), position);
				}
			});

			return convertView;
		}

	}

	private void getDocsAsync(final Activity activity) {
		progressBar.setVisibility(View.VISIBLE);
		RestClient.getApiServices().docRequest(Data.userData.accessToken, new Callback<DocRequirementResponse>() {
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
							DocInfo docInfo = new DocInfo(data.getDocType(), data.getDocTypeNum(), data.getDocRequirement(),
									data.getDocStatus());
							docs.add(docInfo);
						}

					}
				} catch (Exception exception) {
					exception.printStackTrace();
				}
				progressBar.setVisibility(View.GONE);
			}

			@Override
			public void failure(RetrofitError error) {
				Log.i("DocError", error.toString());
			}
		});
	}

	public void uploadfile(final Activity activity, final int index) {

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

					Bitmap compress = codec(bitmap, Bitmap.CompressFormat.JPEG, 3);


					File f = new File(getActivity().getExternalCacheDir(), "temp"+index+".jpg");
						try {
						f.createNewFile();
						ByteArrayOutputStream bos = new ByteArrayOutputStream();
						compress.compress(Bitmap.CompressFormat.JPEG, 0 /*ignored for PNG*/, bos);
						byte[] bitmapdata = bos.toByteArray();

						FileOutputStream fos = new FileOutputStream(f);
						fos.write(bitmapdata);
						fos.flush();
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}


					docs.get(index).setFile(new File(image.getFileThumbnail()));
					driverDocumentListAdapter.notifyDataSetChanged();
					uploadPicToServer(getActivity(), f);


					// reload();
//					Picasso.with(getActivity()).load(CommonUtil.getTempImageFile())
//							.transform(new CircleTransform()).resize(300, 300).centerCrop().memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
//							.into(addImageLayout);

				}
			}
		});
	}

	@Override
	public void onError(final String reason) {
		Toast.makeText(getActivity(), reason, Toast.LENGTH_LONG).show();
	}

	private void uploadPicToServer(final Activity activity, File photoFile) {
		progressBar.setVisibility(View.VISIBLE);
		HashMap<String, String> params = new HashMap<String, String>();

		params.put("access_token", Data.userData.accessToken);
		params.put("user_email", Data.userData.userName);
		params.put("doc_numType", Data.userData.userName);

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

							if (ApiResponseFlags.AUTH_REGISTRATION_FAILURE.getOrdinal() == flag) {
								DialogPopup.alertPopup(activity, "", message);
							} else if (ApiResponseFlags.AUTH_ALREADY_REGISTERED.getOrdinal() == flag) {
								DialogPopup.alertPopup(activity, "", message);
							} else if (ApiResponseFlags.AUTH_VERIFICATION_REQUIRED.getOrdinal() == flag) {

							} else if (ApiResponseFlags.AUTH_DUPLICATE_REGISTRATION.getOrdinal() == flag) {

							} else {
								DialogPopup.alertPopup(activity, "", message);
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
				}
				progressBar.setVisibility(View.GONE);
			}

			@Override
			public void failure(RetrofitError error) {

			}
		});
	}

	private static Bitmap codec(Bitmap src, Bitmap.CompressFormat format,
								int quality) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		src.compress(format, quality, os);

		byte[] array = os.toByteArray();
		return BitmapFactory.decodeByteArray(array, 0, array.length);
	}


}
