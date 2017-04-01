package product.clicklabs.jugnoo.driver.selfAudit;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.DriverDocumentActivity;
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
@SuppressLint("ValidFragment")

public class SelfEnrollmentCameraFragment extends android.support.v4.app.Fragment implements SurfaceHolder.Callback,
		View.OnClickListener {

	private SurfaceView surfaceView;
	private SurfaceHolder surfaceHolder;
	private Camera mCamera;
	private Button acceptImage;
	private Button rejectImage;
	private Button captureImage, backBtn, buttonGallery;
	private int cameraId, auditState, auditType, auditCmeraOption;
	private String topMessage, bottomMessage;
	private boolean flashmode = false;
	private Bitmap capturedImage = null;
	private int rotation, imgPixel = 600;
	private LinearLayout linearLayoutroot;
	private RelativeLayout relativeLayoutConfirmImage;
	private DriverDocumentActivity activity;
	private File frontImage = null, backImage = null, leftImage = null, rightImage = null, mobileStandImage = null;

	private TextView titleAutoSide;



	public SelfEnrollmentCameraFragment(String topMessage, String bottomMessage){
			this.topMessage = topMessage;
			this.bottomMessage = bottomMessage;
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_self_enroll_custom_camera, container, false);
		linearLayoutroot = (LinearLayout) rootView.findViewById(R.id.linearLayoutRoot);
		activity = (DriverDocumentActivity) getActivity();
		activity.relativeLayoutContainer.setVisibility(View.VISIBLE);
		new ASSL(activity, linearLayoutroot, 1134, 720, false);

		// mCamera surface view created
		relativeLayoutConfirmImage = (RelativeLayout) rootView.findViewById(R.id.relativeLayoutConfirmImage);

		cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
		acceptImage = (Button) rootView.findViewById(R.id.acceptImage);
		rejectImage = (Button) rootView.findViewById(R.id.rejectImage);
		captureImage = (Button) rootView.findViewById(R.id.captureImage);
		surfaceView = (SurfaceView) rootView.findViewById(R.id.surfaceView);
		surfaceView.setFocusable(true);
		surfaceView.setFocusableInTouchMode(true);
		surfaceView.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {

				try {
					if (mCamera != null) {
						Camera camera = mCamera;
						camera.cancelAutoFocus();
	//					Rect focusRecct = calculateTapArea(event.getX(), event.getY(), 1f);

						Camera.Parameters parameters = camera.getParameters();
						if (parameters.getFocusMode() != Camera.Parameters.FOCUS_MODE_AUTO) {
							parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
						}
						if (parameters.getMaxNumFocusAreas() > 0) {
							List<Camera.Area> mylist = new ArrayList<Camera.Area>();
							//mylist.add(new Camera.Area(focusRect, 1000));
							parameters.setFocusAreas(mylist);
						}

						try {
							camera.cancelAutoFocus();
							camera.setParameters(parameters);
							camera.startPreview();
							camera.autoFocus(new Camera.AutoFocusCallback() {
								@Override
								public void onAutoFocus(boolean success, Camera camera) {
									if (camera.getParameters().getFocusMode() != Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE) {
										Camera.Parameters parameters = camera.getParameters();
										parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
										if (parameters.getMaxNumFocusAreas() > 0) {
											parameters.setFocusAreas(null);
										}
										camera.setParameters(parameters);
										camera.startPreview();
									}
								}
							});
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				return true;
			}
		});

		backBtn = (Button) rootView.findViewById(R.id.backBtn);
		buttonGallery = (Button) rootView.findViewById(R.id.buttonGallery);

		titleAutoSide = (TextView) rootView.findViewById(R.id.titleAutoSide);
		titleAutoSide.setTypeface(Data.latoRegular(activity));

		titleAutoSide.setText(topMessage);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		acceptImage.setOnClickListener(this);
		captureImage.setOnClickListener(this);
		rejectImage.setOnClickListener(this);
		backBtn.setOnClickListener(this);
		linearLayoutroot.setOnClickListener(this);
		buttonGallery.setOnClickListener(this);
		activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		return rootView;

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d("OnDstroy", "on destroy called");
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
		try {
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void refreshCamera() {

		captureImage.setVisibility(View.VISIBLE);
		buttonGallery.setVisibility(View.VISIBLE);
		relativeLayoutConfirmImage.setVisibility(View.GONE);

		if (surfaceHolder.getSurface() == null) {
			return;
		}

		try {
			mCamera.stopPreview();
		} catch (Exception e) {
		}

		try {
			mCamera.setPreviewDisplay(surfaceHolder);
			mCamera.startPreview();
		} catch (Exception e) {
		}
	}

	@Override
	public void onClick(View v) {
		try {
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
				case R.id.buttonGallery:
					openGallery();
					break;
				case R.id.backBtn:
					performBackPressed();
					break;
				default:
					break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void alertCameraDialog() {
		AlertDialog.Builder dialog = createAlert(getActivity(),
				"Camera info", "error to open Camera");
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
			mCamera = Camera.open(cameraId);

		} catch (Exception e) {
			e.printStackTrace();
		}
		if (mCamera != null) {
			try {
				setUpCamera(mCamera);
				mCamera.setErrorCallback(new Camera.ErrorCallback() {

					@Override
					public void onError(int error, Camera camera) {
//to show the error message.
					}
				});
				mCamera.setPreviewDisplay(surfaceHolder);
				mCamera.startPreview();
				result = true;
			} catch (Exception e) {
				e.printStackTrace();
				result = false;
				releaseCamera();
			}
		}
		return result;
	}

	private void releaseCamera() {
		try {
			if (mCamera != null) {
				mCamera.setPreviewCallback(null);
				mCamera.setErrorCallback(null);
				mCamera.stopPreview();
				mCamera.release();
				mCamera = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.e("error", e.toString());
			mCamera = null;
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

		try {
			List<String> focusModesNew = params.getSupportedFocusModes();
			if (focusModesNew != null) {
				if (focusModesNew.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE))
					params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		params.setRotation(rotation);
	}

	public void openGallery(){
		if (surfaceHolder.getSurface() != null) {
			surfaceDestroyed(surfaceHolder);
		}
		activity.openGalleryFragment();
		activity.getSupportFragmentManager().popBackStackImmediate();
	}

	public void performBackPressed() {


		if (surfaceHolder.getSurface() != null) {
			surfaceDestroyed(surfaceHolder);
		}
		activity.relativeLayoutContainer.setVisibility(View.GONE);
		activity.getSupportFragmentManager().popBackStackImmediate();

	}



	private void takeImage() {
		try {
			DialogPopup.showLoadingDialog(activity, getResources().getString(R.string.loading));
			mCamera.takePicture(null, null, new Camera.PictureCallback() {

				private File imageFile;

				@Override
				public void onPictureTaken(byte[] data, Camera camera) {
					try {

						BitmapFactory.Options opt;

						opt = new BitmapFactory.Options();
						opt.inTempStorage = new byte[16 * 1024];
						Camera.Parameters parameters = mCamera.getParameters();
						Camera.Size size = parameters.getPictureSize();

						int height11 = size.height;
						int width11 = size.width;
						float mb = (width11 * height11) / 1024000;

						if (mb > 4f)
							opt.inSampleSize = 4;
						else if (mb > 3f)
							opt.inSampleSize = 2;


						// convert byte array into bitmap
						Bitmap loadedImage = BitmapFactory.decodeByteArray(data, 0,
								data.length, opt);

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
							DialogPopup.dismissLoadingDialog();
						}

						if (rotatedBitmap != null) {
							try {
								capturedImage = rotatedBitmap.copy(rotatedBitmap.getConfig(), true);
								rotatedBitmap.recycle();
								captureImage.setVisibility(View.GONE);
								buttonGallery.setVisibility(View.GONE);
								relativeLayoutConfirmImage.setVisibility(View.VISIBLE);
								DialogPopup.dismissLoadingDialog();
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

					} catch (Exception|OutOfMemoryError e) {
						e.printStackTrace();
						DialogPopup.dismissLoadingDialog();
					}

				}
			});
		} catch (Exception|OutOfMemoryError e) {
			e.printStackTrace();
		}
	}

	public void acceptImage(){
		try {
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
			capturedImage.recycle();

			File f = null;
			if (newBitmap != null) {
				int temp = activity.temp++;
				f = Utils.compressToFile(getActivity(), newBitmap, Bitmap.CompressFormat.JPEG, 100,temp);
			}

//			uploadPicToServer(frontImage, auditType, 0);
			if (surfaceHolder.getSurface() != null) {
				surfaceDestroyed(surfaceHolder);
			}

			activity.ServerInterface(f);
			activity.getSupportFragmentManager().popBackStackImmediate();


		} catch (Resources.NotFoundException e) {
			e.printStackTrace();
		}


	}


	public void rejectImage(){
		capturedImage = null;
		refreshCamera();

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
					}, false);
		}
	}




}

