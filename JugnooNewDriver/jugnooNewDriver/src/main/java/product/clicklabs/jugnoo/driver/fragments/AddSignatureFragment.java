package product.clicklabs.jugnoo.driver.fragments;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.gcacace.signaturepad.views.SignaturePad;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.HomeActivity;
import product.clicklabs.jugnoo.driver.JSONParser;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.SplashNewActivity;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.DocRequirementResponse;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.Log;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;


/**
 * Developer: Rishabh
 * Dated: 06/09/15.
 */
public class AddSignatureFragment extends Fragment implements View.OnClickListener,
        SignaturePad.OnSignedListener, View.OnTouchListener {

    private final String TAG = AddSignatureFragment.class.getSimpleName();

    private final int iReset = R.id.btnReset;
    private final int iAction = R.id.btnAction;

    private HomeActivity activity;

    private Button btnReset;
    private Button btnAction;

    private TextView tvInformation;
    private ImageView imgSignaturePreview;

    private SignaturePad spSignaturePad;
    private View vSignaturePadPlaceholder;

    private RelativeLayout rlSignaturePreview;
    private RelativeLayout rlSignaturePadHolder;

    private String image;
    private View rlPlaceholder;

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
    private void init(View parentView) {

//        Log.e(TAG, "init");

        activity = (HomeActivity) getActivity();

        btnAction = (Button) parentView.findViewById(iAction);
        btnAction.setTypeface(Data.latoRegular(activity));
        btnAction.setOnClickListener(this);

        btnReset = (Button) parentView.findViewById(iReset);
        btnReset.setTypeface(Data.latoRegular(activity));
        btnReset.setOnClickListener(this);

        tvInformation = (TextView) parentView.findViewById(R.id.tvInformation);
        tvInformation.setTypeface(Data.latoRegular(activity));

        imgSignaturePreview = (ImageView) parentView.findViewById(R.id.imgSignaturePreview);

        vSignaturePadPlaceholder = parentView.findViewById(R.id.vSignaturePadPlaceholder);

        spSignaturePad = (SignaturePad) parentView.findViewById(R.id.spSignaturePad);
        spSignaturePad.setOnSignedListener(this);
        spSignaturePad.setOnTouchListener(this);

        rlSignaturePreview = (RelativeLayout) parentView.findViewById(R.id.rlSignaturePreview);
        rlSignaturePadHolder = (RelativeLayout) parentView.findViewById(R.id.rlSignaturePadHolder);

        rlPlaceholder = parentView.findViewById(R.id.rlPlaceholder);
        TextView tvPlaceholder = (TextView) parentView.findViewById(R.id.tvPlaceholder);
        tvPlaceholder.setTypeface(Data.latoRegular(activity));

    }

    /**
     * Method to load the Image
     *
     * @param image
     */
    private void loadImage(final String image) {

        Log.e(TAG, "loadImage: " + image);

        this.image = image;

        if (image == null) return;

        Callback callback = new Callback() {

            @Override
            public void onSuccess() {

                Log.e("Loading Image", "Successful");

                // Avoid Error: Fragment not attached to Activity
                if (isAdded())
                    lockControls(false, R.string.update);
            }

            @Override
            public void onError() {

                // Avoid Error: Fragment not attached to Activity
                if (isAdded()) {
                    lockControls(false, R.string.update);
                    tvInformation.setText(R.string.image_upload_failed);
                }
                Log.e("Loading Image", "Failed");
            }
        };

        if (image.startsWith("http://") || image.startsWith("https://")) {

            lockControls(true, R.string.update);
            Log.e("Web Image", image);

            Picasso.with(activity).load(image).memoryPolicy(MemoryPolicy.NO_CACHE)
                    .into(imgSignaturePreview, callback);
        } else {

            Log.e("Local Image", image);

            File file = new File(image);

            if (file.exists())
                Picasso.with(activity).load(acknowledgementImage = file)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .into(imgSignaturePreview, callback);
        }

        toggleView(true);
    }

    /**
     * Method to toggle between the Views
     *
     * @param isPreview show the Preview is set to true.
     */
    private void toggleView(boolean isPreview) {

        rlSignaturePreview.setVisibility(isPreview ? View.VISIBLE : View.GONE);

        rlSignaturePadHolder.setVisibility(isPreview ? View.GONE : View.VISIBLE);
        vSignaturePadPlaceholder.setVisibility(isPreview ? View.GONE : View.VISIBLE);
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

            // Display the Error to User
//            Utils.toast(activity, activity.getString(R.string.an_error_occured_while_clearing_signature));
        }
    }

    /**
     * Method to verify whether the User has signed
     */
    private void verifyAndSendUserSignature() {
        if (!hasUserSigned) {
            Toast.makeText(activity, R.string.i_accept_new, Toast.LENGTH_LONG).show();
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


    private void uploadPicToServer(final Activity activity, File photoFile) {
        try {
            if (AppStatus.getInstance(activity).isOnline(activity)) {
                DialogPopup.showLoadingDialog(activity, getResources().getString(R.string.loading));
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("access_token", Data.userData.accessToken);

                TypedFile typedFile;
                typedFile = new TypedFile(Constants.MIME_TYPE, photoFile);
                RestClient.getApiServices().uploadImageToServer(typedFile, params, new retrofit.Callback<DocRequirementResponse>() {
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
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
                            DialogPopup.dismissLoadingDialog();
                        }
                        DialogPopup.dismissLoadingDialog();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        DialogPopup.dismissLoadingDialog();
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
    private void goBackToDetails() {

        if (!isHidden())
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

        if (bImage == null) {
            Toast.makeText(activity, R.string.unable_to_capture_signature, Toast.LENGTH_SHORT).show();
            return;
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

//    @Override
//    public void onHiddenChanged(boolean hidden) {
//        super.onHiddenChanged(hidden);
//
//        if (!hidden) {
//
//            int subtitle;
//            int visibility;
//
//
//            if (isCompleted) {
//                subtitle = R.string.view_signature_text;
//                visibility = View.INVISIBLE;
//                toggleView(true);
//
//                rlPlaceholder.setVisibility(image == null ? View.VISIBLE : View.GONE);
//                rlSignaturePreview.setVisibility(image == null ? View.GONE : View.VISIBLE);
//            } else {
//                subtitle = R.string.add_signature;
//                visibility = View.VISIBLE;
//            }
//// Check whether the Signature has already been added
//            clearSignaturePad();
//            activity.setSubTitle(subtitle);
//
//            btnAction.setVisibility(visibility);
//            btnReset.setVisibility(visibility);
//        }
//    }

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


    public boolean isUploading() {
        return uploading;
    }
}
