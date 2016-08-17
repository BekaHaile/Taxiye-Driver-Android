package product.clicklabs.jugnoo.driver;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;

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

import java.io.File;
import java.io.IOException;
import java.util.List;
import product.clicklabs.jugnoo.driver.utils.ASSL;

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
	private Button captureImage;
	private int cameraId;
	private boolean flashmode = false;
	private int rotation;
	private LinearLayout linearLayoutroot;
	private RelativeLayout relativeLayoutConfirmImage;
	private SelfAuditActivity activity;
	private ImageView imageViewCapturedImg1Progress, imageViewCapturedImg2Progress, imageViewCapturedImg3Progress,
			imageViewCapturedImg4Progress, imageViewCapturedImg5Progress, imageViewCapturedImg1, imageViewCapturedImg2,
			imageViewCapturedImg3, imageViewCapturedImg4;
	private TextView textViewCapturedImg1Progress, textViewCapturedImg2Progress,textViewCapturedImg3Progress,
			textViewCapturedImg4Progress, textViewCapturedImg5Progress;
	public SelfAuditCameraFragment(){

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
		cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
		acceptImage = (Button) rootView.findViewById(R.id.acceptImage);
		rejectImage = (Button) rootView.findViewById(R.id.rejectImage);
		captureImage = (Button) rootView.findViewById(R.id.captureImage);
		surfaceView = (SurfaceView) rootView.findViewById(R.id.surfaceView);


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

		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(this);
		acceptImage.setOnClickListener(this);
		captureImage.setOnClickListener(this);
		rejectImage.setOnClickListener(this);
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
					Matrix rotateMatrix = new Matrix();
					rotateMatrix.postRotate(rotation);
					Bitmap rotatedBitmap = Bitmap.createBitmap(loadedImage, 0,
							0, loadedImage.getWidth(), loadedImage.getHeight(),
							rotateMatrix, false);

					if(rotatedBitmap != null){
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
//						Toast.makeText(getBaseContext(), "Image Not saved",
//								Toast.LENGTH_SHORT).show();
//						return;
//					}
//
//					ByteArrayOutputStream ostream = new ByteArrayOutputStream();
//
//					// save image into gallery
//					rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
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
//					SelfAuditCameraFragment.this.getContentResolver().insert(
//							MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		});
	}

	public void acceptImage(){
		refreshCamera();
	}

	public void rejectImage(){
		refreshCamera();
	}
}

