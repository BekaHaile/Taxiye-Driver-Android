package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAgent;
import com.squareup.picasso.CircleTransform;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.BookingHistoryResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.BaseActivity;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.FirebaseEvents;
import product.clicklabs.jugnoo.driver.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.driver.utils.FlurryEventNames;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.ProfileInfo;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class DriverProfileActivity extends BaseActivity {

	LinearLayout relative;
	RelativeLayout driverDetailsRLL,driverDetailsRL;
	Button backBtn;
	TextView title;

	TextView textViewDriverName, textViewDriverId, textViewRankCity, textViewRankOverall, textViewMonthlyValue, textViewRidesTakenValue,
			textViewRidesCancelledValue, textViewRidesMissedValue, textViewOnlineHoursValue, textViewTitleBarDEI, textViewMonthlyText,
			textViewRidesTakenText, textViewRidesMissedText, textViewRidesCancelledText, terms;

	ImageView profileImg, imageViewTitleBarDEI;


	public static ProfileInfo openedProfileInfo;

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

		setContentView(R.layout.activity_profile_screen);
		if(BuildConfig.DEBUG){
			findViewById(R.id.topRl).setOnLongClickListener(new View.OnLongClickListener() {
				@Override
				public boolean onLongClick(View v) {
					if(Data.isCaptive()){
						Data.setCaptive(false);
						Toast.makeText(DriverProfileActivity.this,"Driver made non captive",Toast.LENGTH_SHORT).show();

					}else{
						Data.setCaptive(true);
						Toast.makeText(DriverProfileActivity.this,"Driver made captive",Toast.LENGTH_SHORT).show();

					}
					return false;
				}
			});
		}

		relative = (LinearLayout) findViewById(R.id.activity_profile_screen);
		driverDetailsRLL = (RelativeLayout) findViewById(R.id.driverDetailsRLL);
		driverDetailsRL = (RelativeLayout) findViewById(R.id.driverDetailsRL);

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
//		textViewTitleBarDEI = (TextView) findViewById(R.id.textViewTitleBarDEI);
//		textViewTitleBarDEI.setTypeface(Data.latoRegular(this));
		textViewMonthlyText = (TextView) findViewById(R.id.textViewMonthlyText);
		textViewMonthlyText.setTypeface(Data.latoRegular(this));
		textViewMonthlyText.setText(getStringText(R.string.earnings));
		textViewRidesTakenText = (TextView) findViewById(R.id.textViewRidesTakenText);
		textViewRidesTakenText.setTypeface(Data.latoRegular(this));
		textViewRidesTakenText.setText(getStringText(R.string.rides_taken));

		textViewRidesMissedText = (TextView) findViewById(R.id.textViewRidesMissedText);
		textViewRidesMissedText.setTypeface(Data.latoRegular(this));
		textViewRidesMissedText.setText(getResources().getString(R.string.rides_missed));

		textViewRidesCancelledText = (TextView) findViewById(R.id.textViewRidesCancelledText);
		textViewRidesCancelledText.setTypeface(Data.latoRegular(this));

		terms = (TextView) findViewById(R.id.terms);
		terms.setTypeface(Data.latoRegular(this));

		textViewRidesCancelledText.setText(getStringText(R.string.rides_cancelled));



		profileImg = (ImageView) findViewById(R.id.profileImg);
		imageViewTitleBarDEI = (ImageView) findViewById(R.id.imageViewTitleBarDEI);

		backBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				MyApplication.getInstance().logEvent(FirebaseEvents.PROFILE_PAGE+"_"+FirebaseEvents.BACK, null);
				performBackPressed();
			}
		});



		getProfileInfoAsync(DriverProfileActivity.this);

		driverDetailsRL.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
//				EditDriverProfile.openProfileInfo = openedProfileInfo;
				MyApplication.getInstance().logEvent(FirebaseEvents.PROFILE_PAGE+"_1_"+FirebaseEvents.CARD, null);
				startActivity(new Intent(DriverProfileActivity.this, EditDriverProfile.class));
				overridePendingTransition(R.anim.right_in, R.anim.right_out);
			}
		});

		terms.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(DriverProfileActivity.this, HelpActivity.class));
				FlurryEventLogger.event(FlurryEventNames.TERMS_OF_USE);
			}
		});
		if(!WhiteLabelConstants.SHOW_TERMS_IN_PROFILE){
			terms.setVisibility(View.GONE);
		}
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
		try {
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
											String textViewDriverName = "", textViewTitleBarDEI = "", accNo="", ifscCode = "", bankName="", bankLoc="";
											int textViewDriverId = 0, textViewRankCity = 0, textViewRankOverall = 0,
												 textViewRidesTakenValue = 0, textViewRidesMissedValue = 0,
													textViewRidesCancelledValue = 0, textViewOnlineHoursValue = 0;
											Integer textViewMonthlyValue = null;
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
											if (jObj.has("bank_account_no")) {
												accNo = jObj.getString("bank_account_no");
											}
											if (jObj.has("ifsc_code")) {
												ifscCode = jObj.getString("ifsc_code");
											}
											if (jObj.has("bank_name")) {
												bankName = jObj.getString("bank_name");
											}
											if (jObj.has("bank_location")) {
												bankLoc = jObj.getString("bank_location");
											}
											openedProfileInfo = new ProfileInfo(textViewDriverName, textViewDriverId, textViewRankCity,
													textViewRankOverall, textViewMonthlyValue, textViewRidesTakenValue, textViewRidesMissedValue,
													textViewRidesCancelledValue, textViewOnlineHoursValue, textViewTitleBarDEI, accNo, ifscCode,
													bankName, bankLoc);

											setUserData();

										} catch (Exception e) {
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setUserData() {
		try {
			try {
				Picasso.with(DriverProfileActivity.this).load(Data.userData.userImage)
						.transform(new CircleTransform())
						.into(profileImg);
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (openedProfileInfo != null) {
				textViewDriverName.setText("" + openedProfileInfo.textViewDriverName);
				textViewDriverId.setText(getStringText(R.string.driver_id) + " " + openedProfileInfo.textViewDriverId);
				if (openedProfileInfo.textViewRankCity == 0) {
					textViewRankCity.setVisibility(View.GONE);
				} else {
					textViewRankCity.setVisibility(View.VISIBLE);
					textViewRankCity.setText(getStringText(R.string.rank_city) + " " + openedProfileInfo.textViewRankCity);
				}

				if (openedProfileInfo.textViewRankCity == 0) {
					textViewRankOverall.setVisibility(View.GONE);
				} else {
					textViewRankOverall.setVisibility(View.VISIBLE);
					textViewRankOverall.setText(getStringText(R.string.rank_overall) + " " + openedProfileInfo.textViewRankOverall);
				}

				if(openedProfileInfo.textViewMonthlyValue!=null){
					textViewMonthlyValue.setText(getResources().getText(R.string.rupee)+" " + openedProfileInfo.textViewMonthlyValue);
					findViewById(R.id.rlMonthlyEarnings).setVisibility(View.VISIBLE);


				}else {
					findViewById(R.id.rlMonthlyEarnings).setVisibility(View.GONE);
				}
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
