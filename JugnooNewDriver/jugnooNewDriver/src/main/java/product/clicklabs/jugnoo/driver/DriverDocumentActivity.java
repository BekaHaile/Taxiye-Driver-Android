package product.clicklabs.jugnoo.driver;


import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.kbeanie.imagechooser.api.ChosenImage;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.fragments.AddSignatureFragment;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.DocRequirementResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse;
import product.clicklabs.jugnoo.driver.selfAudit.SelfEnrollmentCameraFragment;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity;
import product.clicklabs.jugnoo.driver.utils.DeviceUniqueID;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.driver.utils.FlurryEventNames;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class DriverDocumentActivity extends BaseFragmentActivity {


	RelativeLayout relative;

	Button backBtn, submitButton;

	public RelativeLayout relativeLayoutRides, relativeLayoutContainer;
	String accessToken;
	Configuration conf;
	DocumentListFragment documentListFragment;
	static boolean loginDataFetched = false;
	Bundle bundleHomePush= new Bundle();
	boolean inSideApp = false;
	int requirement;
	public static int temp = 0;
	DialogPopup dialogLogin = new DialogPopup();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_driver_documents);
		bundleHomePush = getIntent().getExtras();
		relative = (RelativeLayout) findViewById(R.id.relative);
		new ASSL(DriverDocumentActivity.this, relative, 1134, 720, false);

		submitButton = (Button) findViewById(R.id.submitButton);
		backBtn = (Button) findViewById(R.id.backBtn);

		relativeLayoutRides = (RelativeLayout) findViewById(R.id.relativeLayoutRides);
		relativeLayoutContainer = (RelativeLayout) findViewById(R.id.relativeLayoutContainer);
		documentListFragment = new DocumentListFragment();

		Bundle bundle = new Bundle();
		accessToken = getIntent().getExtras().getString("access_token");
		if(getIntent().getExtras().getBoolean("in_side")){
			inSideApp = true;
		}
		requirement  = getIntent().getExtras().getInt("doc_required");
		bundle.putString("access_token", accessToken);
		bundle.putInt("doc_required", requirement);
		documentListFragment.setArguments(bundle);
		loginDataFetched = false;
		getSupportFragmentManager().beginTransaction()
				.add(R.id.fragment, documentListFragment, DocumentListFragment.class.getName())
				.addToBackStack(DocumentListFragment.class.getName())
				.commit();

		submitButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				DialogPopup.alertPopupTwoButtonsWithListeners(DriverDocumentActivity.this, "", getString(R.string.documents_authentication),
						getString(R.string.i_agree),"", new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								docSubmission();

							}
						},null,true,false);
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
		performbackPressed();
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
		if(!inSideApp) {
			JSONParser.saveAccessToken(DriverDocumentActivity.this, "");
			Intent intent = new Intent(DriverDocumentActivity.this, SplashNewActivity.class);
			startActivity(intent);
		}
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
			int newRequirement;
			if(requirement == 0){
				newRequirement =1;
			} else {
				newRequirement =requirement;
			}

			HashMap<String, String> params = new HashMap<String, String>();
			params.put(Constants.KEY_ACCESS_TOKEN, accessToken);
			params.put("login_documents", String.valueOf(newRequirement));
			HomeUtil.putDefaultParams(params);

			RestClient.getApiServices().docSubmission(params, new Callback<DocRequirementResponse>() {
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
									if(!inSideApp) {
										accessTokenLogin(DriverDocumentActivity.this, accessToken);
										dialogLogin.showLoadingDialog(DriverDocumentActivity.this, getResources().getString(R.string.loading));
									} else {
										Prefs.with(DriverDocumentActivity.this).save(Constants.UPLOAD_DOCUMENT_MESSAGE, jObj.optString("display_message", ""));
										DialogPopup.alertPopupWithListener(DriverDocumentActivity.this, "", message, new View.OnClickListener() {
											@Override
											public void onClick(View v) {
												performbackPressed();
											}
										});
									}
								} else if (ApiResponseFlags.UPLOAD_DOCCUMENT.getOrdinal() == flag) {

									if (inSideApp) {
										DialogPopup.alertPopupTwoButtonsWithListeners(DriverDocumentActivity.this, "", message,
												getResources().getString(R.string.later), getResources().getString(R.string.upload_now),
												new View.OnClickListener() {
													@Override
													public void onClick(View v) {
														performbackPressed();
													}
												},
												new View.OnClickListener() {
													@Override
													public void onClick(View v) {

													}
												}, false, false);
									} else {
										DialogPopup.alertPopup(DriverDocumentActivity.this, "", message);
									}
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

	public void accessTokenLogin(final Activity activity, String accessToken) {
		final long responseTime = System.currentTimeMillis();
		conf = getResources().getConfiguration();
		if (!"".equalsIgnoreCase(accessToken)){
			if (AppStatus.getInstance(getApplicationContext()).isOnline(getApplicationContext())) {

				if(Data.locationFetcher != null){
					Data.latitude = Data.locationFetcher.getLatitude();
					Data.longitude = Data.locationFetcher.getLongitude();
				}
				HashMap<String, String> params = new HashMap<String, String>();

//				RequestParams params = new RequestParams();
				params.put("access_token", accessToken);
				params.put("device_token", Data.deviceToken);

				params.put("latitude", ""+Data.latitude);
				params.put("longitude", ""+Data.longitude);

				params.put("locale", conf.locale.toString());
				params.put("app_version", ""+Data.appVersion);
				params.put("device_type", Data.DEVICE_TYPE);
				params.put("unique_device_id", Data.uniqueDeviceId);
				params.put("is_access_token_new", "1");
				params.put("client_id", Data.CLIENT_ID);
				params.put("login_type", Data.LOGIN_TYPE);

				params.put("device_name", Utils.getDeviceName());
				params.put("imei", DeviceUniqueID.getUniqueId(this));
				HomeUtil.putDefaultParams(params);

				if(Utils.isAppInstalled(activity, Data.GADDAR_JUGNOO_APP)){
					params.put("auto_n_cab_installed", "1");
				}
				else{
					params.put("auto_n_cab_installed", "0");
				}


				if(Utils.isAppInstalled(activity, Data.UBER_APP)){
					params.put("uber_installed", "1");
				}
				else{
					params.put("uber_installed", "0");
				}

				if(Utils.telerickshawInstall(activity)){
					params.put("telerickshaw_installed", "1");
				}
				else{
					params.put("telerickshaw_installed", "0");
				}

				if(Utils.olaInstall(activity)){
					params.put("ola_installed", "1");
				}
				else{
					params.put("ola_installed", "0");
				}

				if(Utils.isDeviceRooted()){
					params.put("device_rooted", "1");
				}
				else{
					params.put("device_rooted", "0");
				}



				RestClient.getApiServices().accessTokenLoginRetro(params, new Callback<RegisterScreenResponse>() {
					@Override
					public void success(RegisterScreenResponse registerScreenResponse, Response response) {
						try {
							String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
							JSONObject jObj;
							jObj = new JSONObject(jsonString);
							int flag = jObj.getInt("flag");
							String message = JSONParser.getServerMessage(jObj);

							if(!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag)){
								if(ApiResponseFlags.AUTH_NOT_REGISTERED.getOrdinal() == flag){
									DialogPopup.alertPopup(activity, "", message);
									DialogPopup.dismissLoadingDialog();
								}
								else if(ApiResponseFlags.AUTH_LOGIN_FAILURE.getOrdinal() == flag){
									DialogPopup.alertPopup(activity, "", message);
									DialogPopup.dismissLoadingDialog();
								}
								else if(ApiResponseFlags.AUTH_LOGIN_SUCCESSFUL.getOrdinal() == flag){
									if(!SplashNewActivity.checkIfUpdate(jObj.getJSONObject("login"), activity)){
//										new AccessTokenDataParseAsync(activity, jsonString, message).execute();
										String resp;
										try {
											resp = new JSONParser().parseAccessTokenLoginData(activity, jsonString);
										} catch (Exception e) {
											e.printStackTrace();
											resp = Constants.SERVER_TIMEOUT;
										}

										if(resp.contains(Constants.SERVER_TIMEOUT)){
											loginDataFetched = false;
											DialogPopup.alertPopup(activity, "", message);
										}
										else{
											loginDataFetched = true;
											DialogPopup.showLoadingDialog(DriverDocumentActivity.this, getResources().getString(R.string.loading));
										}

										Utils.deleteMFile();
										Utils.clearApplicationData(DriverDocumentActivity.this);
										FlurryEventLogger.logResponseTime(activity, System.currentTimeMillis() - responseTime, FlurryEventNames.LOGIN_ACCESSTOKEN_RESPONSE);

										DialogPopup.dismissLoadingDialog();

									}
									else{
										DialogPopup.dismissLoadingDialog();
									}
								} else if(ApiResponseFlags.UPLOAD_DOCCUMENT.getOrdinal() == flag){
									JSONParser.saveAccessToken(activity, jObj.getString("access_token"));
									Intent intent = new Intent(DriverDocumentActivity.this, DriverDocumentActivity.class);
									intent.putExtra("access_token",jObj.getString("access_token"));
									intent.putExtra("in_side", false);
									intent.putExtra("doc_required", 3);
									startActivity(intent);
								}  else{
									DialogPopup.alertPopup(activity, "", message);
									DialogPopup.dismissLoadingDialog();
								}
							}
							else{
								DialogPopup.dismissLoadingDialog();
							}

						}  catch (Exception exception) {
							exception.printStackTrace();
							DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
							DialogPopup.dismissLoadingDialog();
						}
					}

					@Override
					public void failure(RetrofitError error) {

						DialogPopup.dismissLoadingDialog();
						DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);

					}
				});

			}
			else {
				DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
			}
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);

//		if (hasFocus && sendToOtpScreen) {
//			sendIntentToOtpScreen();
//		}
//
//		if(hasFocus && noNetFirstTime){
//			noNetFirstTime = false;
//			checkNetHandler.postDelayed(checkNetRunnable, 4000);
//		}
//		else if(hasFocus && noNetSecondTime){
//			noNetSecondTime = false;
////			finish();
//		}
		if(hasFocus && loginDataFetched){
			loginDataFetched = false;
			Intent intent = new Intent(DriverDocumentActivity.this, HomeActivity.class);
			if(bundleHomePush != null)
				intent.putExtras(bundleHomePush);
			startActivity(intent);
			ActivityCompat.finishAffinity(this);
			dialogLogin.dismissLoadingDialog();
			overridePendingTransition(R.anim.right_in, R.anim.right_out);
		}
//		else if(hasFocus && loginFailed){
//			loginFailed = false;
//			startActivity(new Intent(DriverDocumentActivity.this, LoginViaOTP.class));
//			finish();
//			overridePendingTransition(R.anim.right_in, R.anim.right_out);
//		}
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		bundleHomePush = intent.getExtras();

	}

}
