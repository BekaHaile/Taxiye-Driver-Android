package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.CircleTransform;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.ProfileUpdateMode;
import product.clicklabs.jugnoo.driver.datastructure.UserData;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.BookingHistoryResponse;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.ProfileInfo;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import rmn.androidscreenlibrary.ASSL;

/**
 * Created by aneeshbansal on 14/01/16.
 */
public class EditDriverProfile extends Activity {
	LinearLayout relative;
	RelativeLayout driverDetailsRLL;
	Button backBtn;
	ImageView editIcon1, editIcon2;
	TextView title;
	ScrollView scrollView;

	EditText editTextUserName, editTextPhone;
	TextView textViewDriverEmailId;

	ImageView profileImg, imageViewTitleBarDEI;


	public ProfileInfo openedProfileInfo;

	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.init(this, Data.FLURRY_KEY);
		FlurryAgent.onStartSession(this, Data.FLURRY_KEY);
	}

	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_profile_screen);

		relative = (LinearLayout) findViewById(R.id.activity_profile_screen);
		driverDetailsRLL = (RelativeLayout) findViewById(R.id.driverDetailsRLL);

		new ASSL(this, relative, 1134, 720, false);

		backBtn = (Button) findViewById(R.id.backBtn);
		editIcon1 = (ImageView) findViewById(R.id.editIcon1);
		editIcon2 = (ImageView) findViewById(R.id.editIcon2);
		title = (TextView) findViewById(R.id.title);
		title.setTypeface(Data.latoRegular(this));

		editTextUserName = (EditText) findViewById(R.id.editTextUserName);
		editTextUserName.setTypeface(Data.latoRegular(this));
		textViewDriverEmailId = (TextView) findViewById(R.id.textViewDriverEmailId);
		textViewDriverEmailId.setTypeface(Data.latoRegular(this));
		editTextPhone = (EditText) findViewById(R.id.editTextPhone);
		editTextPhone.setTypeface(Data.latoRegular(this));

		profileImg = (ImageView) findViewById(R.id.profileImg);
		imageViewTitleBarDEI = (ImageView) findViewById(R.id.imageViewTitleBarDEI);

		backBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});



		editTextUserName.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					scrollView.smoothScrollTo(0, editTextUserName.getTop());
				}
				editTextUserName.setError(null);
			}
		});


		editTextPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					scrollView.smoothScrollTo(0, editTextPhone.getTop());
				}
				editTextPhone.setError(null);
			}
		});


		editIcon1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				editTextUserName.setError(null);
				if (editTextUserName.isEnabled()) {
					String nameChanged = editTextUserName.getText().toString().trim();
					if ("".equalsIgnoreCase(nameChanged)) {
						editTextUserName.requestFocus();
						editTextUserName.setError("Username can't be empty");
					} else {
						if (Data.userData.userName.equalsIgnoreCase(nameChanged)) {
							editTextUserName.requestFocus();
							editTextUserName.setError("Changed Username is same as the previous one.");
						} else {
							updateUserProfileAPI(EditDriverProfile.this, nameChanged, ProfileUpdateMode.NAME);
						}
					}
				} else {
					editTextUserName.requestFocus();
					editTextUserName.setEnabled(true);
					editTextUserName.setSelection(editTextUserName.getText().length());
					Utils.showSoftKeyboard(EditDriverProfile.this, editTextUserName);
				}
			}
		});

		editTextUserName.setOnEditorActionListener(new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
				int result = actionId & EditorInfo.IME_MASK_ACTION;
				switch (result) {
					case EditorInfo.IME_ACTION_DONE:
						editIcon1.performClick();
						break;

					case EditorInfo.IME_ACTION_NEXT:
						editIcon1.performClick();
						break;

					default:
				}
				return true;
			}
		});
	}


	public void performBackPressed() {
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}

	@Override
	public void onBackPressed() {
		performBackPressed();
		super.onBackPressed();
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			ASSL.closeActivity(relative);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.gc();
	}


	public void setUserData() {
		try {
			try {
				Picasso.with(this).load(Data.userData.userImage)
						.transform(new CircleTransform())
						.into(profileImg);
			} catch (Exception e) {
			}

			if (openedProfileInfo != null) {
				editTextUserName.setText("" + openedProfileInfo.textViewDriverName);

			}


		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateUserProfileAPI(final Activity activity, final String updatedField, final ProfileUpdateMode profileUpdateMode) {
		if(AppStatus.getInstance(activity).isOnline(activity)) {

			DialogPopup.showLoadingDialog(activity, "Updating...");

			RequestParams params = new RequestParams();

			params.put("access_token", Data.userData.accessToken);
			params.put("is_access_token_new", "1");

			if(ProfileUpdateMode.PHONE.getOrdinal() == profileUpdateMode.getOrdinal()){
				params.put("updated_phone_no", updatedField);
			}
			else{
				params.put("updated_user_name", updatedField);
			}


			AsyncHttpClient client = Data.getClient();
			client.post(Data.SERVER_URL + "/update_user_profile", params,
					new CustomAsyncHttpResponseHandler() {
						private JSONObject jObj;

						@Override
						public void onFailure(Throwable arg3) {
							Log.e("request fail", arg3.toString());
							DialogPopup.dismissLoadingDialog();
							DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
						}

						@Override
						public void onSuccess(String response) {
							Log.i("Server response", "response = " + response);
							DialogPopup.dismissLoadingDialog();
							try {
								jObj = new JSONObject(response);
								int flag = jObj.getInt("flag");
								if(!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag)){
									if(ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag){
										String error = jObj.getString("error");
										DialogPopup.dialogBanner(activity, error);
									}
									else if(ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag){
										String message = jObj.getString("message");
										if(ProfileUpdateMode.PHONE.getOrdinal() == profileUpdateMode.getOrdinal()){
											Intent intent = new Intent(activity, OTPConfirmScreen.class);
											intent.putExtra("phone_no_verify", updatedField);
											activity.startActivity(intent);
											activity.overridePendingTransition(R.anim.right_in, R.anim.right_out);
										}
										else{
											DialogPopup.dialogBanner(activity, message);
											Data.userData.userName = updatedField;
											editTextUserName.setEnabled(false);
											editTextUserName.setText(Data.userData.userName);
										}
									}
									else{
										DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
									}
								}
							}  catch (Exception exception) {
								exception.printStackTrace();
								DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
								DialogPopup.dismissLoadingDialog();
							}
						}
					});
		}
		else {
			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}

	}
}
