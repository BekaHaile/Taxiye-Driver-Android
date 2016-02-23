package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.squareup.picasso.CircleTransform;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.BookingHistoryResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.ProfileInfo;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class DriverProfileActivity extends Activity {

	LinearLayout relative;
	RelativeLayout driverDetailsRLL, layoutEditProfile;
	Button backBtn;
	TextView title;

	TextView textViewDriverName, textViewDriverId, textViewRankCity, textViewRankOverall, textViewMonthlyValue, textViewRidesTakenValue,
			textViewRidesCancelledValue, textViewRidesMissedValue, textViewOnlineHoursValue, textViewTitleBarDEI;

	ImageView profileImg, imageViewTitleBarDEI;


	public  ProfileInfo openedProfileInfo;

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
		try {
			textViewDriverName.setText(Data.userData.userName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		try {
			String type = getIntent().getStringExtra("type");
			if(type.equalsIgnoreCase("accept")){
				Intent intent = new Intent(DriverProfileActivity.this, HomeActivity.class);
				intent.putExtras(getIntent().getExtras());
//				intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				startActivity(intent);
				finish();
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		setContentView(R.layout.activity_profile_screen);

		relative = (LinearLayout) findViewById(R.id.activity_profile_screen);
		driverDetailsRLL = (RelativeLayout) findViewById(R.id.driverDetailsRLL);
		layoutEditProfile = (RelativeLayout) findViewById(R.id.layoutEditProfile);

		new ASSL(DriverProfileActivity.this, relative, 1134, 720, false);

		backBtn = (Button) findViewById(R.id.backBtn);
		title = (TextView) findViewById(R.id.title);
		title.setTypeface(Data.latoRegular(this));

		textViewDriverName = (TextView) findViewById(R.id.textViewDriverName);
		textViewDriverName.setTypeface(Data.latoRegular(this));
		textViewDriverId = (TextView) findViewById(R.id.textViewDriverId);
		textViewDriverId.setTypeface(Data.latoRegular(this));
		textViewRankCity = (TextView) findViewById(R.id.textViewRankCity);
		textViewRankCity.setTypeface(Data.latoRegular(this));
		textViewRankOverall = (TextView) findViewById(R.id.textViewRankOverall);
		textViewRankOverall.setTypeface(Data.latoRegular(this));
		textViewMonthlyValue = (TextView) findViewById(R.id.textViewMonthlyValue);
		textViewMonthlyValue.setTypeface(Data.latoRegular(this));

		textViewRidesTakenValue = (TextView) findViewById(R.id.textViewRidesTakenValue);
		textViewRidesTakenValue.setTypeface(Data.latoRegular(this));
		textViewRidesCancelledValue = (TextView) findViewById(R.id.textViewRidesCancelledValue);
		textViewRidesCancelledValue.setTypeface(Data.latoRegular(this));
		textViewRidesMissedValue = (TextView) findViewById(R.id.textViewRidesMissedValue);
		textViewRidesMissedValue.setTypeface(Data.latoRegular(this));
		textViewOnlineHoursValue = (TextView) findViewById(R.id.textViewOnlineHoursValue);
		textViewOnlineHoursValue.setTypeface(Data.latoRegular(this));
		textViewTitleBarDEI = (TextView) findViewById(R.id.textViewTitleBarDEI);
		textViewTitleBarDEI.setTypeface(Data.latoRegular(this));




		profileImg = (ImageView) findViewById(R.id.profileImg);
		imageViewTitleBarDEI = (ImageView) findViewById(R.id.imageViewTitleBarDEI);

		backBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});



		getProfileInfoAsync(DriverProfileActivity.this);

		layoutEditProfile.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(DriverProfileActivity.this, EditDriverProfile.class));
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
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

	private void getProfileInfoAsync(final Activity activity) {
		DialogPopup.showLoadingDialog(activity, getResources().getString(R.string.loading));
		driverDetailsRLL.setVisibility(View.GONE);
		RestClient.getApiServices().driverProfileInfo(Data.userData.accessToken,
				new Callback<BookingHistoryResponse>() {
					@Override
					public void success(BookingHistoryResponse bookingHistoryResponse, Response response) {
						try {
							String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
							Log.i("driverprof", jsonString);
							JSONObject jObj;
							jObj = new JSONObject(jsonString);
							int flag = jObj.getInt("flag");
							String message = JSONParser.getServerMessage(jObj);
							if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag)) {
								if (ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag) {
									DialogPopup.alertPopup(activity, "", message);
								} else if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {

									try {
										String textViewDriverName = "", textViewTitleBarDEI = "";
										int textViewDriverId = 0, textViewRankCity = 0, textViewRankOverall = 0,
												textViewMonthlyValue = 0, textViewRidesTakenValue = 0, textViewRidesMissedValue = 0,
												textViewRidesCancelledValue = 0, textViewOnlineHoursValue = 0;
										if (jObj.has("driver_name")) {
											textViewDriverName = jObj.getString("driver_name");
										}
										if (jObj.has("driver_id")) {
											textViewDriverId = jObj.getInt("driver_id");
										}
										if (jObj.has("driver_city_rank")) {
											textViewRankCity = jObj.getInt("driver_city_rank");
										}
										if (jObj.has("driver_overall_rank")) {
											textViewRankOverall = jObj.getInt("driver_overall_rank");
										}
										if (jObj.has("driver_earning")) {
											textViewMonthlyValue = jObj.getInt("driver_earning");
										}
										if (jObj.has("rides_taken")) {
											textViewRidesTakenValue = jObj.getInt("rides_taken");
										}
										if (jObj.has("rides_missed")) {
											textViewRidesMissedValue = jObj.getInt("rides_missed");
										}
										if (jObj.has("rides_cancelled")) {
											textViewRidesCancelledValue = jObj.getInt("rides_cancelled");
										}
										if (jObj.has("online_hours")) {
											textViewOnlineHoursValue = jObj.getInt("online_hours");
										}
										openedProfileInfo = new ProfileInfo(textViewDriverName, textViewDriverId, textViewRankCity,
												textViewRankOverall, textViewMonthlyValue, textViewRidesTakenValue, textViewRidesMissedValue,
												textViewRidesCancelledValue, textViewOnlineHoursValue, textViewTitleBarDEI);

										setUserData();

									} catch (JSONException e) {
										e.printStackTrace();
										DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);

									}
								}
							}
						} catch (Exception exception) {
							exception.printStackTrace();
							DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
						}
						DialogPopup.dismissLoadingDialog();
						driverDetailsRLL.setVisibility(View.VISIBLE);

					}


					@Override
					public void failure(RetrofitError error) {
						DialogPopup.dismissLoadingDialog();
						driverDetailsRLL.setVisibility(View.VISIBLE);
						DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
					}
				});
	}

	public void setUserData() {
		try {
			try {
				Picasso.with(DriverProfileActivity.this).load(Data.userData.userImage)
						.transform(new CircleTransform())
						.into(profileImg);
			} catch (Exception e) {
			}

			if ("-1".equalsIgnoreCase(Data.userData.deiValue)) {

				imageViewTitleBarDEI.setVisibility(View.GONE);
				textViewTitleBarDEI.setText("Jugnoo");
			} else {

				imageViewTitleBarDEI.setVisibility(View.VISIBLE);
				textViewTitleBarDEI.setText(Data.userData.deiValue);
			}
			if (openedProfileInfo != null) {
				textViewDriverName.setText("" + openedProfileInfo.textViewDriverName);
				textViewDriverId.setText(getResources().getString(R.string.driver_id) + " " + openedProfileInfo.textViewDriverId);
				if (openedProfileInfo.textViewRankCity == 0) {
					textViewRankCity.setVisibility(View.GONE);
				} else {
					textViewRankCity.setVisibility(View.VISIBLE);
					textViewRankCity.setText(getResources().getString(R.string.rank_city) + " " + openedProfileInfo.textViewRankCity);
				}

				if (openedProfileInfo.textViewRankCity == 0) {
					textViewRankOverall.setVisibility(View.GONE);
				} else {
					textViewRankOverall.setVisibility(View.VISIBLE);
					textViewRankOverall.setText(getResources().getString(R.string.rank_overall) + " " + openedProfileInfo.textViewRankOverall);
				}

				textViewMonthlyValue.setText(getResources().getText(R.string.rupee)+" " + openedProfileInfo.textViewMonthlyValue);
				textViewRidesTakenValue.setText("" + openedProfileInfo.textViewRidesTakenValue);
				textViewRidesCancelledValue.setText("" + openedProfileInfo.textViewRidesCancelledValue);
				textViewRidesMissedValue.setText("" + openedProfileInfo.textViewRidesMissedValue);
				textViewOnlineHoursValue.setText("" + openedProfileInfo.textViewOnlineHoursValue);
			}


		} catch (Exception e) {
			e.printStackTrace();
		}
	}







}
