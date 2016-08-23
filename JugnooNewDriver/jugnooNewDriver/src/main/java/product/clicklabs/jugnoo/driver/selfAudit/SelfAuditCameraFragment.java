package product.clicklabs.jugnoo.driver.selfAudit;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;

import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.HomeActivity;
import product.clicklabs.jugnoo.driver.JSONParser;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.DocRequirementResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedFile;

/**
 * Created by aneeshbansal on 13/08/16.
 */
public class SelfAuditCameraFragment extends android.support.v4.app.Fragment implements SurfaceHolder.Callback,
		View.OnClickListener {

	private SurfaceView surfaceView;
	private SurfaceHolder surfaceHolder;
	private Camera camera;
	private Button acceptImage;
	private Button rejectImage;
	private Button captureImage, backBtn;
	private int cameraId, auditState, auditType, auditCmeraOption;
	private boolean flashmode = false;
	private Bitmap capturedImage;
	private int rotation, imgPixel = 600;
	private LinearLayout linearLayoutroot, linearLayoutProgressBar, relativeLayoutProgressBarText;
	private RelativeLayout relativeLayoutConfirmImage, relativeLayoutProgressBar;
	private SelfAuditActivity activity;
	private File frontImage = null, backImage = null, leftImage = null, rightImage = null, mobileStandImage = null;
	private ImageView imageViewCapturedImg1Progress, imageViewCapturedImg2Progress, imageViewCapturedImg3Progress,
			imageViewCapturedImg4Progress, imageViewCapturedImg5Progress, imageViewCapturedImg1, imageViewCapturedImg2,
			imageViewCapturedImg3, imageViewCapturedImg4;
	private TextView textViewCapturedImg1Progress, textViewCapturedImg2Progress,textViewCapturedImg3Progress,
			textViewCapturedImg4Progress, textViewCapturedImg5Progress, titleAutoSide;



	public SelfAuditCameraFragment(int auditState, int auditType, int auditCmeraOption){
			this.auditState = auditState;
			this.auditType = auditType;
			this.auditCmeraOption = auditCmeraOption;
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_self_audit_custom_camera, container, false);

		linearLayoutroot = (LinearLayout) rootView.findViewById(R.id.linearLayoutRoot);
		activity = (SelfAuditActivity) getActivity();
		new ASSL(activity, linearLayoutroot, 1134, 720, false);



		// camera surface view created
		relativeLayoutConfirmImage = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutConfirmImage);
		relativeLayoutProgressBar = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutProgressBar);

		relativeLayoutProgressBarText = (LinearLayout) rootView.findViewById(R.id.relativeLayoutProgressBarText);
		linearLayoutProgressBar = (LinearLayout) rootView.findViewById(R.id.linearLayoutProgressBar);


		cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
		acceptImage = (Button) rootView.findViewById(R.id.acceptImage);
		rejectImage = (Button) rootView.findViewById(R.id.rejectImage);
		captureImage = (Button) rootView.findViewById(R.id.captureImage);
		surfaceView = (SurfaceView) rootView.findViewById(R.id.surfaceView);
		backBtn = (Button) rootView.findViewById(R.id.backBtn);


		imageViewCapturedImg1Progress = (ImageView) rootView.findViewById(R.id.imageViewCapturedImg1Progress);
		imageViewCapturedImg2Progress = (ImageView) rootView.findViewById(R.id.imageViewCapturedImg2Progress);
		imageViewCapturedImg3Progress = (ImageView) rootView.findViewById(R.id.imageViewCapturedImg3Progress);
		imageViewCapturedImg4Progress = (ImageView) rootView.findViewById(R.id.imageViewCapturedImg4Progress);
		imageViewCapturedImg5Progress = (ImageView) rootView.findViewById(R.id.imageViewCapturedImg5Progress);

		imageViewCapturedImg1 = (ImageView) rootView.findViewById(R.id.imageViewCapturedImg1);
		imageViewCapturedImg2 = (ImageView) rootView.findViewById(R.id.imageViewCapturedImg2);
		imageViewCapturedImg3 = (ImageView) rootView.findViewById(R.id.imageViewCapturedImg3);
		imageViewCapturedImg4 = (ImageView) rootView.findViewById(R.id.imageViewCapturedImg4);

		textViewCapturedImg1Progress = (TextView) rootView.findViewById(R.id.textViewCapturedImg1Progress);
		textViewCapturedImg1Progress.setTypeface(Data.latoRegular(activity));
		textViewCapturedImg2Progress = (TextView) rootView.findViewById(R.id.textViewCapturedImg2Progress);
		textViewCapturedImg2Progress.setTypeface(Data.latoRegular(activity));
		textViewCapturedImg3Progress = (TextView) rootView.findViewById(R.id.textViewCapturedImg3Progress);
		textViewCapturedImg3Progress.setTypeface(Data.latoRegular(activity));
		textViewCapturedImg4Progress = (TextView) rootView.findViewById(R.id.textViewCapturedImg4Progress);
		textViewCapturedImg4Progress.setTypeface(Data.latoRegular(activity));
		textViewCapturedImg5Progress = (TextView) rootView.findViewById(R.id.textViewCapturedImg5Progress);
		textViewCapturedImg5Progress.setTypeface(Data.latoRegular(activity));
		titleAutoSide = (TextView) rootView.findViewById(R.id.titleAutoSide);
		titleAutoSide.setTypeface(Data.latoRegular(activity));

		if(auditState == 1){
			imageViewCapturedImg2Progress.setImageResource(R.drawable.green_circle_bar);
			textViewCapturedImg2Progress.setTextColor(getResources().getColor(R.color.white));
			titleAutoSide.setText(getResources().getString(R.string.auto_from_back));
		} else if(auditState == 2){
			imageViewCapturedImg2Progress.setImageResource(R.drawable.green_circle_bar);
			textViewCapturedImg2Progress.setTextColor(getResources().getColor(R.color.white));
			titleAutoSide.setText(getResources().getString(R.string.auto_from_left));
		} else if(auditState == 3){
			imageViewCapturedImg2Progress.setImageResource(R.drawable.green_circle_bar);
			textViewCapturedImg2Progress.setTextColor(getResources().getColor(R.color.white));
			titleAutoSide.setText(getResources().getString(R.string.auto_from_right));
		} else if(auditState == 4){
			imageViewCapturedImg2Progress.setImageResource(R.drawable.green_circle_bar);
			textViewCapturedImg2Progress.setTextColor(getResources().getColor(R.color.white));
			titleAutoSide.setText(getResources().getString(R.string.mobile_stand));
		}

		if(auditCmeraOption ==1){
			relativeLayoutProgressBar.setVisibility(View.INVISIBLE);
			linearLayoutProgressBar.setVisibility(View.INVISIBLE);
			relativeLayoutProgressBarText.setVisibility(View.INVISIBLE);
		} else {
			relativeLayoutProgressBar.setVisibility(View.VISIBLE);
			linearLayoutProgressBar.setVisibility(View.VISIBLE);
			relativeLayoutProgressBarText.setVisibility(View.VISIBLE);
		}

		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		acceptImage.setOnClickListener(this);
		captureImage.setOnClickListener(this);
		rejectImage.setOnClickListener(this);
		backBtn.setOnClickListener(this);
		activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		return rootView;

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!openCamera(Camera.CameraInfo.CAMERA_FACING_BACK)) {
			alertCameraDialog ();
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		refreshCamera();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		camera.stopPreview();
		camera.release();
		camera = null;
	}

	public void refreshCamera() {

		captureImage.setVisibility(View.VISIBLE);
		relativeLayoutConfirmImage.setVisibility(View.GONE);

		if (surfaceHolder.getSurface() == null) {
			return;
		}

		try {
			camera.stopPreview();
		} catch (Exception e) {
		}

		try {
			camera.setPreviewDisplay(surfaceHolder);
			camera.startPreview();
		} catch (Exception e) {
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.rejectImage:
				rejectImage();
				break;
			case R.id.acceptImage:
				acceptImage();
				break;
			case R.id.captureImage:
				takeImage();
				break;
			case R.id.backBtn:
				performBackPressed();
				break;
			default:
				break;
		}
	}

	private void alertCameraDialog() {
		AlertDialog.Builder dialog = createAlert(getActivity(),
				"Camera info", "error to open camera");
		dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();

			}
		});

		dialog.show();
	}

	private AlertDialog.Builder createAlert(Context context, String title, String message) {

		AlertDialog.Builder dialog = new AlertDialog.Builder(
				new ContextThemeWrapper(context,
						android.R.style.Theme_Holo_Light_Dialog));
		dialog.setIcon(R.drawable.cross_30_px);
		if (title != null)
			dialog.setTitle(title);
		else
			dialog.setTitle("Information");
		dialog.setMessage(message);
		dialog.setCancelable(false);
		return dialog;

	}




	private boolean openCamera(int id) {
		boolean result = false;
		cameraId = id;
		releaseCamera();
		try {
			camera = Camera.open(cameraId);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (camera != null) {
			try {
				setUpCamera(camera);
				camera.setErrorCallback(new Camera.ErrorCallback() {

					@Override
					public void onError(int error, Camera camera) {
//to show the error message.
					}
				});
				camera.setPreviewDisplay(surfaceHolder);
				camera.startPreview();
				result = true;
			} catch (IOException e) {
				e.printStackTrace();
				result = false;
				releaseCamera();
			}
		}
		return result;
	}

	private void releaseCamera() {
		try {
			if (camera != null) {
				camera.setPreviewCallback(null);
				camera.setErrorCallback(null);
				camera.stopPreview();
				camera.release();
				camera = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("error", e.toString());
			camera = null;
		}
	}

	private void setUpCamera(Camera c) {
		Camera.CameraInfo info = new Camera.CameraInfo();
		Camera.getCameraInfo(cameraId, info);
		rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
		int degree = 0;
		switch (rotation) {
			case Surface.ROTATION_0:
				degree = 0;
				break;
			case Surface.ROTATION_90:
				degree = 90;
				break;
			case Surface.ROTATION_180:
				degree = 180;
				break;
			case Surface.ROTATION_270:
				degree = 270;
				break;

			default:
				break;
		}

		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			// frontFacing
			rotation = (info.orientation + degree) % 330;
			rotation = (360 - rotation) % 360;
		} else {
			// Back-facing
			rotation = (info.orientation - degree + 360) % 360;
		}
		c.setDisplayOrientation(rotation);
		Camera.Parameters params = c.getParameters();

//		showFlashButton(params);

		List<String> focusModes = params.getSupportedFlashModes();
		if (focusModes != null) {
			if (focusModes
					.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
				params.setFlashMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
			}
		}

		params.setRotation(rotation);
	}


	public void performBackPressed() {


		DialogPopup.alertPopupTwoButtonsWithListeners(activity, "", getResources().getString(R.string.cancel_audit),
				getResources().getString(R.string.yes), getResources().getString(R.string.no), new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						deleteCurrentAudit(activity);
					}
				}, new View.OnClickListener() {
					@Override
					public void onClick(View v) {

					}
				}, true, true);

	}



	private void takeImage() {
		camera.takePicture(null, null, new Camera.PictureCallback() {

			private File imageFile;

			@Override
			public void onPictureTaken(byte[] data, Camera camera) {
				try {
					// convert byte array into bitmap
					Bitmap loadedImage = BitmapFactory.decodeByteArray(data, 0,
							data.length);

					// rotate Image
					Bitmap rotatedBitmap = null;
					Matrix rotateMatrix = new Matrix();
					try {

						rotateMatrix.postRotate(rotation);
						rotatedBitmap = Bitmap.createBitmap(loadedImage, 0,
								0, loadedImage.getWidth(), loadedImage.getHeight(),
								rotateMatrix, false);
					} catch (Exception e) {
						e.printStackTrace();
					}

					if (rotatedBitmap != null) {
						capturedImage = rotatedBitmap.copy(rotatedBitmap.getConfig(), true);
						rotatedBitmap.recycle();
						captureImage.setVisibility(View.GONE);
						relativeLayoutConfirmImage.setVisibility(View.VISIBLE);
					}


//					String state = Environment.getExternalStorageState();
//					File folder = null;
//					if (state.contains(Environment.MEDIA_MOUNTED)) {
//						folder = new File(Environment
//								.getExternalStorageDirectory() + "/Demo");
//					} else {
//						folder = new File(Environment
//								.getExternalStorageDirectory() + "/Demo");
//					}
//
//					boolean success = true;
//					if (!folder.exists()) {
//						success = folder.mkdirs();
//					}
//					if (success) {
//						java.util.Date date = new java.util.Date();
//						imageFile = new File(folder.getAbsolutePath()
//								+ File.separator
//								+ new Timestamp(date.getTime()).toString()
//								+ "Image.jpg");
//
//						imageFile.createNewFile();
//					} else {
//						Toast.makeText(activity, "Image Not saved",
//								Toast.LENGTH_SHORT).show();
//						return;
//					}
//
//					ByteArrayOutputStream ostream = new ByteArrayOutputStream();
//
//					// save image into gallery
//					capturedImage.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
//
//					FileOutputStream fout = new FileOutputStream(imageFile);
//					fout.write(ostream.toByteArray());
//					fout.close();
//					ContentValues values = new ContentValues();
//
//					values.put(MediaStore.Images.Media.DATE_TAKEN,
//							System.currentTimeMillis());
//					values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
//					values.put(MediaStore.MediaColumns.DATA,
//							imageFile.getAbsolutePath());
//
//					activity.getContentResolver().insert(
//							MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
	}


	public void acceptImage(){
		refreshCamera();

		Bitmap newBitmap = null;
		if(capturedImage != null) {
			double oldHeight = capturedImage.getHeight();
			double oldWidth = capturedImage.getWidth();

			if (oldWidth > oldHeight) {
				int newHeight = imgPixel;
				int newWidth = (int) ((oldWidth / oldHeight) * imgPixel);
				newBitmap = Utils.getResizedBitmap(capturedImage, newHeight, newWidth);
			} else {
				int newWidth = imgPixel;
				int newHeight = (int) ((oldHeight / oldWidth) * imgPixel);
				newBitmap = Utils.getResizedBitmap(capturedImage, newHeight, newWidth);
			}
		}

		File f = null;
		if (newBitmap != null) {
			f = Utils.compressToFile(getActivity(), newBitmap, Bitmap.CompressFormat.JPEG, 100,0);
		}

			if (frontImage == null && auditState == 0) {
				frontImage = f;
				imageViewCapturedImg2Progress.setImageResource(R.drawable.green_circle_bar);
				textViewCapturedImg2Progress.setTextColor(getResources().getColor(R.color.white));
				titleAutoSide.setText(getResources().getString(R.string.auto_from_back));
				auditState = 1;
				uploadPicToServer(frontImage, auditType, 0);

			} else if (backImage == null && auditState == 1) {
				backImage = f;
				imageViewCapturedImg3Progress.setImageResource(R.drawable.green_circle_bar);
				textViewCapturedImg3Progress.setTextColor(getResources().getColor(R.color.white));
				titleAutoSide.setText(getResources().getString(R.string.auto_from_left));
				auditState = 2;
				uploadPicToServer(backImage, auditType, 1);

			} else if (leftImage == null && auditState == 2) {
				leftImage = f;
				imageViewCapturedImg4Progress.setImageResource(R.drawable.green_circle_bar);
				textViewCapturedImg4Progress.setTextColor(getResources().getColor(R.color.white));
				titleAutoSide.setText(getResources().getString(R.string.auto_from_right));
				auditState = 3;
				uploadPicToServer(leftImage, auditType, 2);

			} else if (rightImage == null && auditState == 3) {
				rightImage = f;
				imageViewCapturedImg5Progress.setImageResource(R.drawable.green_circle_bar);
				textViewCapturedImg5Progress.setTextColor(getResources().getColor(R.color.white));
				titleAutoSide.setText(getResources().getString(R.string.mobile_stand));
				auditState = 4;
				uploadPicToServer(rightImage, auditType, 3);

			} else if (mobileStandImage == null && auditState == 4) {
				mobileStandImage = f;
				uploadPicToServer(mobileStandImage, auditType, 4);
			}




	}

	public void rejectImage(){
		capturedImage = null;
		refreshCamera();
	}

	private void uploadPicToServer(File photoFile, final Integer auditType, final Integer imageType) {
		try {
			if (AppStatus.getInstance(activity).isOnline(activity)) {
				if(auditCmeraOption == 1){
					DialogPopup.showLoadingDialog(activity, getResources().getString(R.string.loading));
				}
				HashMap<String, String> params = new HashMap<String, String>();

				params.put("access_token", Data.userData.accessToken);
				params.put("image_type", String.valueOf(imageType));
				params.put("audit_type", String.valueOf(auditType));
				TypedFile typedFile;
				typedFile = new TypedFile(Constants.MIME_TYPE, photoFile);
				Log.i("selfaudit", String.valueOf(typedFile) + params);
				capturedImage.recycle();
				RestClient.getApiServices().uploadAuditImageToServer(typedFile, params, new Callback<DocRequirementResponse>() {
					@Override
					public void success(DocRequirementResponse docRequirementResponse, Response response) {
						try {
							String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
							JSONObject jObj;
							jObj = new JSONObject(jsonString);

							int flag = jObj.getInt("flag");
							String message = JSONParser.getServerMessage(jObj);

							if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {

								if(imageType == 4 || auditCmeraOption ==1){
									activity.getTransactionUtils().openSubmitAuditFragment(activity,
											activity.getRelativeLayoutContainer(), auditType);
								}
								DialogPopup.dismissLoadingDialog();


							} else if (ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag) {
								DialogPopup.dismissLoadingDialog();
								reloadImagePopup(imageType);

							}

						} catch (Exception exception) {
							exception.printStackTrace();
							DialogPopup.dismissLoadingDialog();
							reloadImagePopup(imageType);
						}
					}

					@Override
					public void failure(RetrofitError error) {
						DialogPopup.dismissLoadingDialog();
						reloadImagePopup(imageType);
					}
				});
			} else {
				DialogPopup.alertPopup(activity, "", getResources().getString(R.string.check_internet_message));
			}
		} catch (Resources.NotFoundException e) {
			e.printStackTrace();
		}
	}

	public void reloadImagePopup(int imageType){
		if(imageType == 4 || auditCmeraOption ==1) {
			DialogPopup.alertPopupWithImageListener(activity, "", getResources().getString(R.string.image_upload_failed),
					R.drawable.error_icon_for_popup, new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							activity.getTransactionUtils().openSubmitAuditFragment(activity,
									activity.getRelativeLayoutContainer(), auditType);
						}
					});
		}
	}

	public void deleteCurrentAudit(final Activity activity) {
		try {
			if (AppStatus.getInstance(activity).isOnline(activity)) {
				DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));
				HashMap<String, String> params = new HashMap<String, String>();

				params.put("access_token", Data.userData.accessToken);
				params.put("audit_type", String.valueOf(auditType));

				RestClient.getApiServices().cancelAuditByDriver(params, new Callback<RegisterScreenResponse>() {
					@Override
					public void success(RegisterScreenResponse registerScreenResponse, Response response) {
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						try {
							JSONObject jObj = new JSONObject(responseStr);
							int flag = jObj.getInt("flag");
							if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
								Intent intent = new Intent(activity, HomeActivity.class);
								startActivity(intent);
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

