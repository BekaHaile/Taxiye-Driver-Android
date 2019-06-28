package product.clicklabs.jugnoo.driver;


import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.DocInfo;
import product.clicklabs.jugnoo.driver.datastructure.DriverTaskTypes;
import product.clicklabs.jugnoo.driver.fragments.DocumentDetailsFragment;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.DocRequirementResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity;
import product.clicklabs.jugnoo.driver.utils.DeviceUniqueID;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.driver.utils.FlurryEventNames;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class DriverDocumentActivity extends BaseFragmentActivity implements DocumentDetailsFragment.InteractionListener {

	View backBtn;
	TextView title;
	Button submitButton;

	public RelativeLayout relativeLayoutRides;
	String accessToken;
	Configuration conf;
	DocumentListFragment documentListFragment;
	boolean inSideApp = false;
	int requirement, brandingImagesOnly;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if(savedInstanceState != null){
			restartApp();
			return;
		}
		setContentView(R.layout.activity_driver_documents);

		submitButton = (Button) findViewById(R.id.submitButton);
		backBtn = findViewById(R.id.backBtn);
		title = (TextView) findViewById(R.id.title); title.setTypeface(Fonts.mavenMedium(this));
		title.setText(R.string.documents);

		relativeLayoutRides = (RelativeLayout) findViewById(R.id.relativeLayoutRides);
		documentListFragment = new DocumentListFragment();

		Bundle bundle = new Bundle();
		accessToken = getIntent().getExtras().getString("access_token");
		if(getIntent().getExtras().getBoolean("in_side")){
			inSideApp = true;
		}
		requirement  = getIntent().getExtras().getInt("doc_required");
		brandingImagesOnly  = getIntent().getIntExtra(Constants.BRANDING_IMAGES_ONLY, 0);

		int taskType  = getIntent().getIntExtra(Constants.KEY_TASK_TYPE, DriverTaskTypes.SELF_BRANDING.getType());
		double latitude  = getIntent().getDoubleExtra(Constants.KEY_LATITUDE, 0);
		double longitude  = getIntent().getDoubleExtra(Constants.KEY_LONGITUDE, 0);

		bundle.putString("access_token", accessToken);
		bundle.putInt("doc_required", requirement);
		bundle.putInt(Constants.BRANDING_IMAGES_ONLY, brandingImagesOnly);
		bundle.putInt(Constants.KEY_TASK_TYPE, taskType);
		bundle.putDouble(Constants.KEY_LATITUDE, latitude);
		bundle.putDouble(Constants.KEY_LONGITUDE, longitude);
		documentListFragment.setArguments(bundle);

		if(taskType == DriverTaskTypes.HERE_MAPS_FEEDBACK.getType()){
			title.setText(R.string.places);
		}

		getSupportFragmentManager().beginTransaction()
				.add(R.id.fragment, documentListFragment, DocumentListFragment.class.getName())
				.commit();




		submitButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				if(getDocumentDetailsFragment() !=null){

					((DocumentDetailsFragment)getDocumentDetailsFragment()).submitInputData();

					return;
				}

				Pair<Boolean, String> pair = getDocumentListFragment().allDocsMandatoryFieldsFilled();
				if(!pair.first){
					Utils.showToast(DriverDocumentActivity.this, getString(R.string.error_empty_mandatory_field, pair.second), Toast.LENGTH_LONG);
					return;
				}


				if(brandingImagesOnly == 1){
					docSubmission();
				} else {
					DialogPopup.alertPopupTwoButtonsWithListeners(DriverDocumentActivity.this, "",
							getString(R.string.documents_authentication, getString(R.string.appname), getString(R.string.appname)),
							getString(R.string.i_agree), "", new View.OnClickListener() {
								@Override
								public void onClick(View v) {
									docSubmission();

								}
							}, null, true, false);
				}
			}
		});
		backBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				performbackPressed();
			}
		});
	}

	public Fragment getDocumentDetailsFragment() {
		return getSupportFragmentManager().findFragmentByTag(DocumentDetailsFragment.class.getName());
	}

	public  void  openDocumentDetails(DocInfo docInfo,Integer pos){

		title.setText(docInfo.docType);
		getSupportFragmentManager().beginTransaction()
				.add(R.id.fragment,  DocumentDetailsFragment.newInstance(accessToken,docInfo,pos), DocumentDetailsFragment.class.getName())
				.hide(getSupportFragmentManager().findFragmentByTag(DocumentListFragment.class.getName()))
				.addToBackStack(DocumentDetailsFragment.class.getName())
				.commit();
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

	public void performbackPressed() {
		Utils.hideSoftKeyboard(this, getWindow().getDecorView().getRootView());
		if(getSupportFragmentManager().getBackStackEntryCount()>0){
			title.setText(R.string.documents);
			super.onBackPressed();
			return;
		}
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
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
			params.put(Constants.BRANDING_IMAGE, String.valueOf(brandingImagesOnly));

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

							if (!SplashNewActivity.checkIfTrivialAPIErrors(DriverDocumentActivity.this, jObj, flag, null)) {
                                DialogPopup.dismissLoadingDialog();
								if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
									if(!inSideApp) {
										accessTokenLogin(DriverDocumentActivity.this, accessToken);
									} else {
										if(brandingImagesOnly != 1){
											Prefs.with(DriverDocumentActivity.this).save(Constants.UPLOAD_DOCUMENT_MESSAGE, jObj.optString("display_message", ""));
										}
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
							}
						} else {
							DialogPopup.dismissLoadingDialog();
						}
					} catch (Exception exception) {
						exception.printStackTrace();
						DialogPopup.alertPopup(DriverDocumentActivity.this, "", Data.SERVER_ERROR_MSG);
						DialogPopup.dismissLoadingDialog();
					}
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
				params.put("device_token", FirebaseInstanceId.getInstance().getToken());

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
				params.put("imei", DeviceUniqueID.getCachedUniqueId(this));
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

				DialogPopup.showLoadingDialog(DriverDocumentActivity.this, getResources().getString(R.string.loading));

				RestClient.getApiServices().accessTokenLoginRetro(params, new Callback<RegisterScreenResponse>() {
					@Override
					public void success(RegisterScreenResponse registerScreenResponse, Response response) {
						try {
							String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
							JSONObject jObj;
							jObj = new JSONObject(jsonString);
							int flag = jObj.getInt("flag");
							String message = JSONParser.getServerMessage(jObj);

							if(!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag, null)){
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

										DialogPopup.dismissLoadingDialog();
										if(resp.contains(Constants.SERVER_TIMEOUT)){
											DialogPopup.alertPopup(activity, "", message);
										}
										else{
											Intent intent = new Intent(DriverDocumentActivity.this, HomeActivity.class);
											if(getIntent() != null && getIntent().getExtras() != null)
												intent.putExtras(getIntent().getExtras());
											startActivity(intent);
											ActivityCompat.finishAffinity(DriverDocumentActivity.this);
											overridePendingTransition(R.anim.right_in, R.anim.right_out);
										}

										Utils.deleteMFile(activity);
										Utils.clearApplicationData(DriverDocumentActivity.this);
										FlurryEventLogger.logResponseTime(activity, System.currentTimeMillis() - responseTime, FlurryEventNames.LOGIN_ACCESSTOKEN_RESPONSE);


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
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
	}


	public DocumentListFragment getDocumentListFragment(){
		return ( (DocumentListFragment)getSupportFragmentManager().findFragmentByTag(DocumentListFragment.class.getName()));
	}

	@Override
	public void updateDocInfo(int pos, @NotNull DocInfo docInfo) {
		if(getDocumentListFragment() != null) {
			getDocumentListFragment().updateDocInfo(pos, docInfo);
		}
	}

	@Override
	public void setSubmitButtonVisibility(int visibility) {
		submitButton.setVisibility(visibility);
	}
}
