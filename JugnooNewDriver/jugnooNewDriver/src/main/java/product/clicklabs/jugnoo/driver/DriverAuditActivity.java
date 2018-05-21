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

import com.flurry.android.FlurryAgent;
import com.squareup.picasso.CircleTransform;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.BookingHistoryResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.BaseActivity;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.ProfileInfo;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class DriverAuditActivity extends BaseActivity {

	LinearLayout relative;
	RelativeLayout driverDetailsRLL,driverDetailsRL;
	Button backBtn;
	TextView title;

	TextView textViewDriverName, textViewDriverId, textViewRankCity, textViewRankOverall, textViewMonthlyValue, textViewRidesTakenValue,
			textViewRidesCancelledValue, textViewRidesMissedValue, textViewOnlineHoursValue, textViewTitleBarDEI, textViewMonthlyText,
			textViewRidesTakenText, textViewRidesMissedText, textViewRidesCancelledText;

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

		relative = (LinearLayout) findViewById(R.id.activity_profile_screen);
		driverDetailsRLL = (RelativeLayout) findViewById(R.id.driverDetailsRLL);
		driverDetailsRL = (RelativeLayout) findViewById(R.id.driverDetailsRL);

		new ASSL(DriverAuditActivity.this, relative, 1134, 720, false);

		backBtn = (Button) findViewById(R.id.backBtn);
		title = (TextView) findViewById(R.id.title);
		title.setTypeface(Fonts.mavenRegular(this));

		textViewDriverName = (TextView) findViewById(R.id.textViewDriverName);
		textViewDriverName.setTypeface(Fonts.mavenRegular(this));
		textViewDriverId = (TextView) findViewById(R.id.textViewDriverId);
		textViewDriverId.setTypeface(Fonts.mavenRegular(this));
		textViewRankCity = (TextView) findViewById(R.id.textViewRankCity);
		textViewRankCity.setTypeface(Fonts.mavenRegular(this));
		textViewRankOverall = (TextView) findViewById(R.id.textViewRankOverall);
		textViewRankOverall.setTypeface(Fonts.mavenRegular(this));
		textViewMonthlyValue = (TextView) findViewById(R.id.textViewMonthlyValue);
		textViewMonthlyValue.setTypeface(Fonts.mavenRegular(this));

		textViewRidesTakenValue = (TextView) findViewById(R.id.textViewRidesTakenValue);
		textViewRidesTakenValue.setTypeface(Fonts.mavenRegular(this));
		textViewRidesCancelledValue = (TextView) findViewById(R.id.textViewRidesCancelledValue);
		textViewRidesCancelledValue.setTypeface(Fonts.mavenRegular(this));
		textViewRidesMissedValue = (TextView) findViewById(R.id.textViewRidesMissedValue);
		textViewRidesMissedValue.setTypeface(Fonts.mavenRegular(this));
		textViewOnlineHoursValue = (TextView) findViewById(R.id.textViewOnlineHoursValue);
		textViewOnlineHoursValue.setTypeface(Fonts.mavenRegular(this));
//		textViewTitleBarDEI = (TextView) findViewById(R.id.textViewTitleBarDEI);
//		textViewTitleBarDEI.setTypeface(Fonts.mavenRegular(this));
		textViewMonthlyText = (TextView) findViewById(R.id.textViewMonthlyText);
		textViewMonthlyText.setTypeface(Fonts.mavenRegular(this));
		textViewMonthlyText.setText(getStringText(R.string.earnings));
		textViewRidesTakenText = (TextView) findViewById(R.id.textViewRidesTakenText);
		textViewRidesTakenText.setTypeface(Fonts.mavenRegular(this));
		textViewRidesTakenText.setText(getStringText(R.string.rides_taken));
		textViewRidesMissedText = (TextView) findViewById(R.id.textViewRidesMissedText);
		textViewRidesMissedText.setTypeface(Fonts.mavenRegular(this));
		textViewRidesMissedText.setText(getResources().getString(R.string.rides_missed));
		textViewRidesCancelledText = (TextView) findViewById(R.id.textViewRidesCancelledText);
		textViewRidesCancelledText.setTypeface(Fonts.mavenRegular(this));
		textViewRidesCancelledText.setText(getStringText(R.string.rides_cancelled));



		profileImg = (ImageView) findViewById(R.id.profileImg);
		imageViewTitleBarDEI = (ImageView) findViewById(R.id.imageViewTitleBarDEI);

		backBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});



		getProfileInfoAsync(DriverAuditActivity.this);

		driverDetailsRL.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
//				EditDriverProfile.openProfileInfo = openedProfileInfo;
				startActivity(new Intent(DriverAuditActivity.this, EditDriverProfile.class));
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
		try {
			driverDetailsRLL.setVisibility(View.GONE);
			HashMap<String, String> params = new HashMap<>();
			params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
			HomeUtil.putDefaultParams(params);
			RestClient.getApiServices().driverProfileInfo(params,
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
											String textViewDriverName = "", textViewTitleBarDEI = "", accNo="", ifscCode = "", bankName="", bankLoc="",currency="";
											int textViewDriverId = 0, textViewRankCity = 0, textViewRankOverall = 0, textViewRidesTakenValue = 0, textViewRidesMissedValue = 0,
													textViewRidesCancelledValue = 0, textViewOnlineHoursValue = 0;
											Integer textViewMonthlyValue = null ;
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
											if (jObj.has("currency")) {
												currency = jObj.getString("currency");
											}

											openedProfileInfo = new ProfileInfo(textViewDriverName, textViewDriverId, textViewRankCity,
													textViewRankOverall, textViewMonthlyValue, textViewRidesTakenValue, textViewRidesMissedValue,
													textViewRidesCancelledValue, textViewOnlineHoursValue, textViewTitleBarDEI, accNo, ifscCode,
													bankName, bankLoc,currency);

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
				Picasso.with(DriverAuditActivity.this).load(Data.userData.userImage)
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
//					textViewMonthlyValue.setText(getResources().getText(R.string.rupee)+" " + openedProfileInfo.textViewMonthlyValue);
					textViewMonthlyValue.setText(Utils.formatCurrencyValue(openedProfileInfo.currency,openedProfileInfo.textViewMonthlyValue));
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
