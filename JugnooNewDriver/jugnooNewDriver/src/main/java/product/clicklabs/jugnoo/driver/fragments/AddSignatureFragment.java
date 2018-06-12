package product.clicklabs.jugnoo.driver.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.HelpActivity;
import product.clicklabs.jugnoo.driver.HomeActivity;
import product.clicklabs.jugnoo.driver.HomeUtil;
import product.clicklabs.jugnoo.driver.JSONParser;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.SplashNewActivity;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.DocRequirementResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Log;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedFile;


public class AddSignatureFragment extends Fragment implements View.OnClickListener,
        SignaturePad.OnSignedListener, View.OnTouchListener {

    private final String TAG = AddSignatureFragment.class.getSimpleName();

    private final int iReset = R.id.btnReset;
    private final int iAction = R.id.btnAction;

    private HomeActivity activity;

    private Button btnReset, backBtn;
    private Button btnAction;

    private TextView tvInformation, textViewTandC, title;
    private ImageView imgSignaturePreview, imageViewTandC;

    private SignaturePad spSignaturePad;
    private TextView vSignaturePadPlaceholder;

	private boolean isSigned =false;

    private RelativeLayout rlSignaturePreview, relativeLayoutRoot;
    private RelativeLayout rlSignaturePadHolder;
	private ScrollView scrollView;
	private ImageView imageViewScroll;

    private String image;
//    private View rlPlaceholder;

    EditText editTextName;
	boolean tandc;

    private File acknowledgementImage;

    private String signatureInsertedId;

    private boolean hasUserSigned;

    private boolean uploading = false;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View parentView = inflater.inflate(R.layout.fragment_task_add_signature, container, false);
        init(parentView);
        return parentView;
    }

    /**
     * Method to perform initialization of components used
     * in the Layout of this Fragment
     *
     * @param parentView
     */
    private void init(final View parentView) {

//        Log.e(TAG, "init");

        activity = (HomeActivity) getActivity();

		relativeLayoutRoot = (RelativeLayout) parentView.findViewById(R.id.relativeLayoutRoot);
		try {
			if (relativeLayoutRoot != null) {
				new ASSL(activity, relativeLayoutRoot, 1134, 720, false);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

        btnAction = (Button) parentView.findViewById(iAction);
        btnAction.setTypeface(Fonts.mavenRegular(activity));
        btnAction.setOnClickListener(this);

        btnReset = (Button) parentView.findViewById(iReset);
        btnReset.setTypeface(Fonts.mavenRegular(activity));
        btnReset.setOnClickListener(this);

		backBtn = (Button) parentView.findViewById(R.id.backBtn);
		backBtn.setVisibility(View.GONE);
		title = (TextView) parentView.findViewById(R.id.title);
		title.setTypeface(Fonts.mavenRegular(activity));

        tvInformation = (TextView) parentView.findViewById(R.id.tvInformation);
        tvInformation.setTypeface(Fonts.mavenRegular(activity));

		textViewTandC = (TextView) parentView.findViewById(R.id.textViewTandC);
		textViewTandC.setTypeface(Fonts.mavenRegular(activity));

		scrollView = (ScrollView) parentView.findViewById(R.id.scrollView);
		imageViewScroll = (ImageView) parentView.findViewById(R.id.imageViewScroll);
        imgSignaturePreview = (ImageView) parentView.findViewById(R.id.imgSignaturePreview);

		imageViewTandC = (ImageView) parentView.findViewById(R.id.imageViewTandC);
        vSignaturePadPlaceholder = (TextView) parentView.findViewById(R.id.vSignaturePadPlaceholder);
		editTextName = (EditText) parentView.findViewById(R.id.editTextName);


        spSignaturePad = (SignaturePad) parentView.findViewById(R.id.spSignaturePad);
        spSignaturePad.setOnSignedListener(this);
        spSignaturePad.setOnTouchListener(this);

        rlSignaturePreview = (RelativeLayout) parentView.findViewById(R.id.rlSignaturePreview);
        rlSignaturePadHolder = (RelativeLayout) parentView.findViewById(R.id.rlSignaturePadHolder);

//        rlPlaceholder = parentView.findViewById(R.id.rlPlaceholder);
//        TextView tvPlaceholder = (TextView) parentView.findViewById(R.id.tvPlaceholder);
//        tvPlaceholder.setTypeface(Fonts.mavenRegular(activity));

		textViewTandC.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(activity, HelpActivity.class));
			}
		});

		relativeLayoutRoot.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

		imageViewTandC.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(tandc){
					imageViewTandC.setImageResource(R.drawable.option_unchecked);
					tandc = false;
				} else {
					imageViewTandC.setImageResource(R.drawable.option_checked_orange);
					tandc = true;
				}
			}
		});

		backBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				backPress();
			}
		});
		getActivity().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);


		parentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {

				Rect r = new Rect();
				parentView.getWindowVisibleDisplayFrame(r);
				int screenHeight = parentView.getRootView().getHeight();

				// r.bottom is the position above soft keypad or device button.
				// if keypad is shown, the r.bottom is smaller than that before.
				int keypadHeight = screenHeight - r.bottom;

				Log.d(TAG, "keypadHeight = " + keypadHeight);

				if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
					imageViewScroll.setVisibility(View.VISIBLE);
					scrollView.fullScroll(View.FOCUS_DOWN);
				}
				else {
					imageViewScroll.setVisibility(View.GONE);
				}
			}
		});

    }

    /**
     * Method to toggle between the Views
     *
     * @param isPreview show the Preview is set to true.
     */
    private void toggleView(boolean isPreview) {

        rlSignaturePreview.setVisibility(isPreview ? View.VISIBLE : View.GONE);

        rlSignaturePadHolder.setVisibility(isPreview ? View.GONE : View.VISIBLE);
        vSignaturePadPlaceholder.setVisibility(isPreview ? vSignaturePadPlaceholder.GONE : vSignaturePadPlaceholder.VISIBLE);
		if(isPreview){
			btnReset.setVisibility(View.VISIBLE);
			isSigned = true;
		} else {
			btnReset.setVisibility(View.GONE);
			isSigned = false;
		}
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case iAction:
                verifyAndSendUserSignature();
                break;

            case iReset:
                clearSignaturePad();
                break;
        }
    }

    /**
     * Method to clear the SignaturePad
     */
    private void clearSignaturePad() {
        try {
            // Clear the Signature Pad
            spSignaturePad.clear();
        } catch (Throwable e) {
            // Print the Error Stack
            e.printStackTrace();
        }
    }

    /**
     * Method to verify whether the User has signed
     */
    private void verifyAndSendUserSignature() {

		if(!isSigned){
			Toast.makeText(activity, R.string.provide_sign_msg, Toast.LENGTH_LONG).show();
			return;
		}

		if(editTextName.getText().toString().trim().equalsIgnoreCase("")){
			Toast.makeText(activity, R.string.pls_type_name, Toast.LENGTH_LONG).show();
			return;
		}

		if (!tandc) {
			Toast.makeText(activity, R.string.select_tandc, Toast.LENGTH_LONG).show();
			return;
		}

        lockControls(true, R.string.update);

        try {
            saveUserSignature();
        } catch (OutOfMemoryError | IOException e) {
            e.printStackTrace();
        }

        clearSignaturePad();

        Picasso.with(activity).load(acknowledgementImage)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(imgSignaturePreview);
    }


    private void uploadPicToServer(final HomeActivity activity, File photoFile) {
        try {
            if (AppStatus.getInstance(activity).isOnline(activity)) {
                DialogPopup.showLoadingDialog(activity, getResources().getString(R.string.loading));
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("access_token", Data.userData.accessToken);
                HomeUtil.putDefaultParams(params);

                TypedFile typedFile;
                typedFile = new TypedFile(Constants.MIME_TYPE, photoFile);
                RestClient.getApiServices().uploadSignatureToServer(typedFile, params, new retrofit.Callback<DocRequirementResponse>() {
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
										DialogPopup.alertPopupWithListener(activity, "", message, new View.OnClickListener() {
											@Override
											public void onClick(View v) {
												activity.relativeLayoutContainer.setVisibility(View.GONE);
												activity.getSupportFragmentManager().popBackStack(PlaceSearchListFragment.class.getName(), getFragmentManager().POP_BACK_STACK_INCLUSIVE);
											}
										});

                                    } else if (ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag) {
                                        DialogPopup.alertPopup(activity, "", message);
                                    } else {
                                        DialogPopup.alertPopup(activity, "", message);
                                    }
                                    DialogPopup.dismissLoadingDialog();
                                }
                            } else {
                                DialogPopup.dismissLoadingDialog();
                            }
							clearSignaturePad();
//							backPress();

                        } catch (Exception exception) {
                            exception.printStackTrace();
                            DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                            DialogPopup.dismissLoadingDialog();
							clearSignaturePad();
//							backPress();
                        }
                        DialogPopup.dismissLoadingDialog();
						clearSignaturePad();
//						backPress();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        DialogPopup.dismissLoadingDialog();
						clearSignaturePad();
//						backPress();
                    }
                });
            } else {
                DialogPopup.alertPopup(activity, "", getResources().getString(R.string.check_internet_message));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Method to go back to details
     */
    private void backPress() {
		activity.onBackPressed();
    }



    /**
     * Method to lock the Buttons
     *
     * @param isLock
     * @param textResourceId
     */
    private void lockControls(boolean isLock, int textResourceId) {

        try {
            btnAction.setText(getString(textResourceId));
            btnAction.setEnabled(!isLock);
            btnReset.setEnabled(!isLock);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method to save signature of the user
     * into an Image File
     */
    private void saveUserSignature() throws OutOfMemoryError, IOException {

        // Create a file to write bitmap data
        acknowledgementImage = new File(activity.getFilesDir(), "task_signature.png");

        Bitmap bImage = spSignaturePad.getSignatureBitmap();
		File f = null;
        if (bImage == null) {
            Toast.makeText(activity, R.string.unable_to_capture_signature, Toast.LENGTH_SHORT).show();
            return;
        } else {
			f = compressToFile(getActivity(), bImage, Bitmap.CompressFormat.JPEG, 80);
		}

        // Convert bitmap to byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bImage.compress(Bitmap.CompressFormat.PNG, 0 /*ignored for PNG*/, bos);

        // Write the bytes in file
        FileOutputStream fos = new FileOutputStream(acknowledgementImage);
        fos.write(bos.toByteArray());
        fos.flush();
        fos.close();

		uploadPicToServer(activity, acknowledgementImage);
    }


    @Override
    public void onStartSigning() {

    }

    @Override
    public void onSigned() {
        hasUserSigned = true;
    }

    @Override
    public void onClear() {

        hasUserSigned = false;

        toggleView(false);
        lockControls(false, R.string.update);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN)
            vSignaturePadPlaceholder.setVisibility(View.GONE);
			btnReset.setVisibility(View.VISIBLE);
			isSigned = true;
        return false;
    }

    /**
     * Method to get the Signature Id
     *
     * @return
     */
    public String getSignatureId() {
        return signatureInsertedId;
    }


	@Override
	public void onDestroy() {
		super.onDestroy();
		getActivity().getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}


    public boolean isUploading() {
        return uploading;
    }

	private static File compressToFile(Context context, Bitmap src, Bitmap.CompressFormat format,
									   int quality) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		src.compress(format, quality, os);
		File f = new File(context.getExternalCacheDir(), "temp" + ".jpg");
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
}
