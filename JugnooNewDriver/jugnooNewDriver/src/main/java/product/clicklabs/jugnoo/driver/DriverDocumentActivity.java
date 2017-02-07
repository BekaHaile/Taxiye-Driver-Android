package product.clicklabs.jugnoo.driver;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.kbeanie.imagechooser.api.ChosenImage;

import org.json.JSONObject;

import java.io.File;

import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.fragments.AddSignatureFragment;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.DocRequirementResponse;
import product.clicklabs.jugnoo.driver.selfAudit.SelfEnrollmentCameraFragment;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.Log;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class DriverDocumentActivity extends BaseFragmentActivity {


	RelativeLayout relative;

	Button backBtn, submitButton;

	public RelativeLayout relativeLayoutRides, relativeLayoutContainer;
	String accessToken;

	DocumentListFragment documentListFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_driver_documents);

		relative = (RelativeLayout) findViewById(R.id.relative);
		new ASSL(DriverDocumentActivity.this, relative, 1134, 720, false);

		submitButton = (Button) findViewById(R.id.submitButton);
		backBtn = (Button) findViewById(R.id.backBtn);

		relativeLayoutRides = (RelativeLayout) findViewById(R.id.relativeLayoutRides);
		relativeLayoutContainer = (RelativeLayout) findViewById(R.id.relativeLayoutContainer);
		documentListFragment = new DocumentListFragment();

		Bundle bundle = new Bundle();
		accessToken = getIntent().getExtras().getString("access_token");
		bundle.putString("access_token", accessToken);
		documentListFragment.setArguments(bundle);

		getSupportFragmentManager().beginTransaction()
				.add(R.id.fragment, documentListFragment, DocumentListFragment.class.getName())
				.addToBackStack(DocumentListFragment.class.getName())
				.commit();

		submitButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				docSubmission();
			}
		});
		backBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				performbackPressed();
			}
		});
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		documentListFragment.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onBackPressed() {
		JSONParser.saveAccessToken(DriverDocumentActivity.this, "");
		Intent intent = new Intent(DriverDocumentActivity.this, SplashNewActivity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
		super.onBackPressed();
	}

	public RelativeLayout getRelativeLayoutContainer(){
		return relativeLayoutContainer;
	}

	private TransactionUtils transactionUtils;
	public TransactionUtils getTransactionUtils(){
		if(transactionUtils == null){
			transactionUtils = new TransactionUtils();
		}
		return transactionUtils;
	}

	@Override
	protected void onDestroy() {
		ASSL.closeActivity(relative);
		System.gc();
		super.onDestroy();
	}

	public void performbackPressed() {
		JSONParser.saveAccessToken(DriverDocumentActivity.this, "");
		Intent intent = new Intent(DriverDocumentActivity.this, SplashNewActivity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}


	private DocumentListFragment getSignatureFragment() {
		return (DocumentListFragment) getSupportFragmentManager().findFragmentByTag(DocumentListFragment.class.getName());
	}

	private SelfEnrollmentCameraFragment getSelfEnrollmentCameraFragment(){
		return  (SelfEnrollmentCameraFragment) getSupportFragmentManager().findFragmentByTag(SelfEnrollmentCameraFragment.class.getName());
	}

	public void ServerInterface(File f){
		if(getSignatureFragment() != null) {

			if (getSelfEnrollmentCameraFragment() != null) {
//				getSupportFragmentManager().beginTransaction().remove(getSelfEnrollmentCameraFragment()).commit();
//				super.onBackPressed();
				relativeLayoutContainer.setVisibility(View.GONE);
				Log.e("fragment count", String.valueOf(getSupportFragmentManager().getBackStackEntryCount()));
			}
			getSignatureFragment().uploadToServer(f);
		}
	}

	public void openGalleryFragment(){
		if(getSignatureFragment() != null) {

			if (getSelfEnrollmentCameraFragment() != null) {
//				getSupportFragmentManager().beginTransaction().remove(getSelfEnrollmentCameraFragment()).commit();
//				super.onBackPressed();
				relativeLayoutContainer.setVisibility(View.GONE);
				Log.e("fragment count", String.valueOf(getSupportFragmentManager().getBackStackEntryCount()));
			}
			getSignatureFragment().chooseImageFromGallery();
		}
	}

	private void docSubmission() {
		if (AppStatus.getInstance(DriverDocumentActivity.this).isOnline(DriverDocumentActivity.this)) {

			DialogPopup.showLoadingDialog(DriverDocumentActivity.this, getResources().getString(R.string.loading));

			RestClient.getApiServices().docSubmission(accessToken, new Callback<DocRequirementResponse>() {
				@Override
				public void success(DocRequirementResponse docRequirementResponse, Response response) {
					try {
						String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
						JSONObject jObj;
						jObj = new JSONObject(jsonString);

						if (!SplashNewActivity.checkIfUpdate(jObj, DriverDocumentActivity.this)) {
							int flag = jObj.getInt("flag");
							String message = JSONParser.getServerMessage(jObj);

							if (!SplashNewActivity.checkIfTrivialAPIErrors(DriverDocumentActivity.this, jObj, flag)) {

								if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {

									DialogPopup.alertPopupWithListener(DriverDocumentActivity.this, "", message, new View.OnClickListener() {
										@Override
										public void onClick(View v) {
											JSONParser.saveAccessToken(DriverDocumentActivity.this, "");
											startActivity(new Intent(DriverDocumentActivity.this, SplashNewActivity.class));
											finish();
										}
									});
								} else if (ApiResponseFlags.UPLOAD_DOCCUMENT.getOrdinal() == flag) {
									DialogPopup.alertPopup(DriverDocumentActivity.this, "", message);
								} else if (ApiResponseFlags.UPLOAD_DOCUMENT_REFRESH.getOrdinal() == flag) {
									try {
										DialogPopup.alertPopupWithListener(DriverDocumentActivity.this, "", message, new View.OnClickListener() {
											@Override
											public void onClick(View v) {
												((DocumentListFragment) getSupportFragmentManager().findFragmentByTag(DocumentListFragment.class.getName())).
														getDocsAsync(DriverDocumentActivity.this);
											}
										});

									} catch (Exception e) {
										e.printStackTrace();
									}
								} else {
									DialogPopup.alertPopup(DriverDocumentActivity.this, "", message);
								}
								DialogPopup.dismissLoadingDialog();
							}
						} else {
							DialogPopup.dismissLoadingDialog();
						}
					} catch (Exception exception) {
						exception.printStackTrace();
						DialogPopup.alertPopup(DriverDocumentActivity.this, "", Data.SERVER_ERROR_MSG);
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
			DialogPopup.alertPopup(DriverDocumentActivity.this, "", getResources().getString(R.string.check_internet_message));

		}
	}


}
