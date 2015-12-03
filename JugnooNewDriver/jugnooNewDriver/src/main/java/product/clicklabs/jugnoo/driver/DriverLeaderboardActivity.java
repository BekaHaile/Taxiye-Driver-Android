package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.adapters.DriverLeaderboardAdapter;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.DriverLeaderboard;
import product.clicklabs.jugnoo.driver.datastructure.DriverLeaderboardData;
import product.clicklabs.jugnoo.driver.datastructure.LeaderboardAreaMode;
import product.clicklabs.jugnoo.driver.datastructure.LeaderboardMode;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.DriverLeaderBoard;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.FlurryEventNames;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import rmn.androidscreenlibrary.ASSL;

public class DriverLeaderboardActivity extends FragmentActivity implements FlurryEventNames {

	LinearLayout linearLayoutRoot;

	Button buttonBack;
	TextView textViewTitle;

	TextView textViewPositionTop, textViewDaily, textViewWeekly, textViewMonthly, textViewPositionBottom;

	ListView listViewDriverLB;
	RelativeLayout relativeLayoutPositionBottom;

	DriverLeaderboardAdapter adapter;

	LeaderboardMode leaderboardMode = LeaderboardMode.DAILY;
	LeaderboardAreaMode leaderboardAreaMode = LeaderboardAreaMode.LOCAL;

	DriverLeaderboardData driverLeaderboardData;

	@Override
	protected void onStart() {
		super.onStart();
		//FlurryAgent.init(this, Data.FLURRY_KEY);
		FlurryAgent.onStartSession(this, Data.FLURRY_KEY);
	}

	@Override
	protected void onStop() {
		super.onStop();
		FlurryAgent.onEndSession(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_driver_leaderboard);

		linearLayoutRoot = (LinearLayout) findViewById(R.id.linearLayoutRoot);
		new ASSL(DriverLeaderboardActivity.this, linearLayoutRoot, 1134, 720, false);


		buttonBack = (Button) findViewById(R.id.buttonBack);
		textViewTitle = (TextView) findViewById(R.id.textViewTitle);
		textViewTitle.setTypeface(Data.latoRegular(this));


		textViewPositionTop = (TextView) findViewById(R.id.textViewPositionTop);
		textViewPositionTop.setTypeface(Data.latoRegular(this));
		((TextView) findViewById(R.id.textViewPositions)).setTypeface(Data.latoRegular(this));
		((TextView) findViewById(R.id.textViewNoRides)).setTypeface(Data.latoRegular(this));
		textViewDaily = (TextView) findViewById(R.id.textViewDaily);
		textViewDaily.setTypeface(Data.latoRegular(this));
		textViewWeekly = (TextView) findViewById(R.id.textViewWeekly);
		textViewWeekly.setTypeface(Data.latoRegular(this));
		textViewMonthly = (TextView) findViewById(R.id.textViewMonthlyValue);
		textViewMonthly.setTypeface(Data.latoRegular(this));
		textViewPositionBottom = (TextView) findViewById(R.id.textViewPositionBottom);
		textViewPositionBottom.setTypeface(Data.latoRegular(this));


		listViewDriverLB = (ListView) findViewById(R.id.listViewDriverLB);

		relativeLayoutPositionBottom = (RelativeLayout) findViewById(R.id.relativeLayoutPositionBottom);

		textViewDaily.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				leaderboardMode = LeaderboardMode.DAILY;
				setList(leaderboardAreaMode, leaderboardMode);
			}
		});

		textViewWeekly.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				leaderboardMode = LeaderboardMode.WEEKLY;
				setList(leaderboardAreaMode, leaderboardMode);
			}
		});

		textViewMonthly.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				leaderboardMode = LeaderboardMode.MONTHLY;
				setList(leaderboardAreaMode, leaderboardMode);
			}
		});

		relativeLayoutPositionBottom.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (leaderboardAreaMode == LeaderboardAreaMode.LOCAL) {
					leaderboardAreaMode = LeaderboardAreaMode.OVERALL;
				} else {
					leaderboardAreaMode = LeaderboardAreaMode.LOCAL;
				}
				setList(leaderboardAreaMode, leaderboardMode);
			}
		});

		buttonBack.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});

		adapter = new DriverLeaderboardAdapter(this, new ArrayList<DriverLeaderboard>(), 0);
		listViewDriverLB.setAdapter(adapter);

		getLeaderboardData(this);

	}


	public void setList(LeaderboardAreaMode leaderboardAreaMode, LeaderboardMode leaderboardMode) {
		int cityPos = 0, cityTotal = 0;
		int overallPos = 0, overallTotal = 0;
		if (LeaderboardMode.DAILY == leaderboardMode) {
			cityPos = driverLeaderboardData.cityPositionDay;
			cityTotal = driverLeaderboardData.cityTotalDay;
			overallPos = driverLeaderboardData.overallPositionDay;
			overallTotal = driverLeaderboardData.overallTotalDay;

			textViewDaily.setTypeface(Data.latoRegular(this), Typeface.BOLD);
			textViewWeekly.setTypeface(Data.latoRegular(this));
			textViewMonthly.setTypeface(Data.latoRegular(this));

			textViewDaily.setBackgroundResource(R.drawable.background_white_dark);
			textViewWeekly.setBackgroundResource(R.drawable.background_white_white_dark_selector);
			textViewMonthly.setBackgroundResource(R.drawable.background_white_white_dark_selector);

		} else if (LeaderboardMode.WEEKLY == leaderboardMode) {
			cityPos = driverLeaderboardData.cityPositionWeek;
			cityTotal = driverLeaderboardData.cityTotalWeek;
			overallPos = driverLeaderboardData.overallPositionWeek;
			overallTotal = driverLeaderboardData.overallTotalWeek;

			textViewDaily.setTypeface(Data.latoRegular(this));
			textViewWeekly.setTypeface(Data.latoRegular(this), Typeface.BOLD);
			textViewMonthly.setTypeface(Data.latoRegular(this));

			textViewDaily.setBackgroundResource(R.drawable.background_white_white_dark_selector);
			textViewWeekly.setBackgroundResource(R.drawable.background_white_dark);
			textViewMonthly.setBackgroundResource(R.drawable.background_white_white_dark_selector);
		} else if (LeaderboardMode.MONTHLY == leaderboardMode) {
			cityPos = driverLeaderboardData.cityPositionMonth;
			cityTotal = driverLeaderboardData.cityTotalMonth;
			overallPos = driverLeaderboardData.overallPositionMonth;
			overallTotal = driverLeaderboardData.overallTotalMonth;

			textViewDaily.setTypeface(Data.latoRegular(this));
			textViewWeekly.setTypeface(Data.latoRegular(this));
			textViewMonthly.setTypeface(Data.latoRegular(this), Typeface.BOLD);

			textViewDaily.setBackgroundResource(R.drawable.background_white_white_dark_selector);
			textViewWeekly.setBackgroundResource(R.drawable.background_white_white_dark_selector);
			textViewMonthly.setBackgroundResource(R.drawable.background_white_dark);
		}
		if (LeaderboardAreaMode.LOCAL == leaderboardAreaMode) {
			adapter.setResults(driverLeaderboardData.getDriverLeaderboardsList(leaderboardAreaMode, leaderboardMode), 0);
			if (0 == cityPos) {
				textViewPositionTop.setText(getResources().getString(R.string.local_positon_text));
			} else {
				textViewPositionTop.setText(getResources().getString(R.string.your_position)+" " + driverLeaderboardData.cityName + " : " + cityPos + "/" + cityTotal);
			}
			if (0 == overallPos) {
				textViewPositionBottom.setText(getResources().getString(R.string.overall_position));
			} else {
				textViewPositionBottom.setText(getResources().getString(R.string.your_overall_position)+" : " + overallPos + "/" + overallTotal);
			}
		} else if (LeaderboardAreaMode.OVERALL == leaderboardAreaMode) {
			adapter.setResults(driverLeaderboardData.getDriverLeaderboardsList(leaderboardAreaMode, leaderboardMode), 1);

			if (0 == overallPos) {
				textViewPositionTop.setText(getResources().getString(R.string.overall_position_text));
			} else {
				textViewPositionTop.setText(getResources().getString(R.string.your_overall_position)+" : " + overallPos + "/" + overallTotal);
			}
			if (0 == cityPos) {
				textViewPositionBottom.setText(getResources().getString(R.string.local_position));
			} else {
				textViewPositionBottom.setText(getResources().getString(R.string.your_position)+" " + driverLeaderboardData.cityName + " : " + cityPos + "/" + cityTotal);
			}

		}
	}



//	Retrofit

	public void getLeaderboardData(final Activity activity) {
		if (!HomeActivity.checkIfUserDataNull(activity)) {
			if (AppStatus.getInstance(activity).isOnline(activity)) {
				DialogPopup.showLoadingDialog(activity, getResources().getString(R.string.loading));
				RequestParams params = new RequestParams();

				params.put("access_token", Data.userData.accessToken);

				RestClient.getApiServices().driverLeaderBoard(Data.userData.accessToken,
						new Callback<DriverLeaderBoard>() {
							@Override
							public void success(DriverLeaderBoard driverLeaderBoard, Response response) {
								try {

									String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
									Log.i("leaderboard",jsonString );
									JSONObject jObj;
									jObj = new JSONObject(jsonString);
									int flag = jObj.getInt("flag");
									String message = JSONParser.getServerMessage(jObj);
									if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag)) {

										if (ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag) {
											DialogPopup.alertPopup(activity, "", message);
										} else if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {


											ArrayList<DriverLeaderboard> driverLeaderboards = new ArrayList<DriverLeaderboard>();


											if (driverLeaderBoard.getDriverBackLeaderBoard().getOverallLeaderBoard().getDay() != null) {
												driverLeaderBoard.getDriverBackLeaderBoard().getOverallLeaderBoard().getDay();
												for (int i = 0; i < driverLeaderBoard.getDriverBackLeaderBoard().getOverallLeaderBoard().getDay().size(); i++) {
													DriverLeaderBoard.Day data = driverLeaderBoard.getDriverBackLeaderBoard().getOverallLeaderBoard().getDay().get(i);
													driverLeaderboards.add(new DriverLeaderboard(data.getDriverId(), data.getDriverName(), data.getCityName(),
															data.getNumberOfRidesOverall(), LeaderboardAreaMode.OVERALL.getOrdinal(), LeaderboardMode.DAILY.getOrdinal()));
												}
											}


											if (driverLeaderBoard.getDriverBackLeaderBoard().getOverallLeaderBoard().getWeek() != null) {
												driverLeaderBoard.getDriverBackLeaderBoard().getOverallLeaderBoard().getWeek();
												for (int i = 0; i < driverLeaderBoard.getDriverBackLeaderBoard().getOverallLeaderBoard().getWeek().size(); i++) {
													DriverLeaderBoard.Week data = driverLeaderBoard.getDriverBackLeaderBoard().getOverallLeaderBoard().getWeek().get(i);
													driverLeaderboards.add(new DriverLeaderboard(data.getDriverId(), data.getDriverName(), data.getCityName(),
															data.getNumberOfRidesOverall(), LeaderboardAreaMode.OVERALL.getOrdinal(), LeaderboardMode.WEEKLY.getOrdinal()));
												}
											}


											if (driverLeaderBoard.getDriverBackLeaderBoard().getOverallLeaderBoard().getMonth() != null) {
												driverLeaderBoard.getDriverBackLeaderBoard().getOverallLeaderBoard().getMonth();
												for (int i = 0; i < driverLeaderBoard.getDriverBackLeaderBoard().getOverallLeaderBoard().getMonth().size(); i++) {
													DriverLeaderBoard.Month data = driverLeaderBoard.getDriverBackLeaderBoard().getOverallLeaderBoard().getMonth().get(i);
													driverLeaderboards.add(new DriverLeaderboard(data.getDriverId(), data.getDriverName(), data.getCityName(),
															data.getNumberOfRidesOverall(), LeaderboardAreaMode.OVERALL.getOrdinal(), LeaderboardMode.MONTHLY.getOrdinal()));
												}
											}


											if (driverLeaderBoard.getDriverBackLeaderBoard().getCityLeaderBoard().getDay() != null) {
												driverLeaderBoard.getDriverBackLeaderBoard().getCityLeaderBoard().getDay();
												for (int i = 0; i < driverLeaderBoard.getDriverBackLeaderBoard().getCityLeaderBoard().getDay().size(); i++) {
													DriverLeaderBoard.Day data = driverLeaderBoard.getDriverBackLeaderBoard().getCityLeaderBoard().getDay().get(i);
													driverLeaderboards.add(new DriverLeaderboard(data.getDriverId(), data.getDriverName(), data.getCityName(),
															data.getNumberOfRidesInCity(), LeaderboardAreaMode.LOCAL.getOrdinal(), LeaderboardMode.DAILY.getOrdinal()));
												}
											}

											if (driverLeaderBoard.getDriverBackLeaderBoard().getCityLeaderBoard().getWeek() != null) {
												driverLeaderBoard.getDriverBackLeaderBoard().getCityLeaderBoard().getWeek();
												for (int i = 0; i < driverLeaderBoard.getDriverBackLeaderBoard().getCityLeaderBoard().getWeek().size(); i++) {
													DriverLeaderBoard.Week data = driverLeaderBoard.getDriverBackLeaderBoard().getCityLeaderBoard().getWeek().get(i);
													driverLeaderboards.add(new DriverLeaderboard(data.getDriverId(), data.getDriverName(), data.getCityName(),
															data.getNumberOfRidesInCity(), LeaderboardAreaMode.LOCAL.getOrdinal(), LeaderboardMode.WEEKLY.getOrdinal()));
												}
											}


											if (driverLeaderBoard.getDriverBackLeaderBoard().getCityLeaderBoard().getMonth() != null) {
												driverLeaderBoard.getDriverBackLeaderBoard().getCityLeaderBoard().getMonth();
												for (int i = 0; i < driverLeaderBoard.getDriverBackLeaderBoard().getCityLeaderBoard().getMonth().size(); i++) {
													DriverLeaderBoard.Month data = driverLeaderBoard.getDriverBackLeaderBoard().getCityLeaderBoard().getMonth().get(i);
													driverLeaderboards.add(new DriverLeaderboard(data.getDriverId(), data.getDriverName(), data.getCityName(),
															data.getNumberOfRidesInCity(), LeaderboardAreaMode.LOCAL.getOrdinal(), LeaderboardMode.MONTHLY.getOrdinal()));
												}
											}
											String cityName = driverLeaderBoard.getDriverBackLeaderBoard().getDriverCity();
//
//
											int cityPositionDay = driverLeaderBoard.getDriverBackLeaderBoard().getCityDriverRank().getDay();
											int cityPositionWeek = driverLeaderBoard.getDriverBackLeaderBoard().getCityDriverRank().getWeek();
											int cityPositionMonth = driverLeaderBoard.getDriverBackLeaderBoard().getCityDriverRank().getMonth();
											int cityTotalDay = driverLeaderBoard.getDriverBackLeaderBoard().getTotalDriversCity().getDay();
											int cityTotalWeek = driverLeaderBoard.getDriverBackLeaderBoard().getTotalDriversCity().getWeek();
											int cityTotalMonth = driverLeaderBoard.getDriverBackLeaderBoard().getTotalDriversCity().getMonth();
											int overallPositionDay = driverLeaderBoard.getDriverBackLeaderBoard().getOverallDriverRank().getDay();
											int overallPositionWeek = driverLeaderBoard.getDriverBackLeaderBoard().getOverallDriverRank().getWeek();
											int overallPositionMonth = driverLeaderBoard.getDriverBackLeaderBoard().getOverallDriverRank().getMonth();
											int overallTotalDay = driverLeaderBoard.getDriverBackLeaderBoard().getTotalDriversOverall().getDay();
											int overallTotalWeek = driverLeaderBoard.getDriverBackLeaderBoard().getTotalDriversOverall().getWeek();
											int overallTotalMonth = driverLeaderBoard.getDriverBackLeaderBoard().getTotalDriversOverall().getMonth();


											driverLeaderboardData = new DriverLeaderboardData(cityName, cityPositionDay, cityPositionWeek, cityPositionMonth, cityTotalDay, cityTotalWeek, cityTotalMonth,
													overallPositionDay, overallPositionWeek, overallPositionMonth, overallTotalDay, overallTotalWeek, overallTotalMonth, driverLeaderboards);

											setList(leaderboardAreaMode, leaderboardMode);


										} else {
											DialogPopup.alertPopup(activity, "", message);
										}

									}


								} catch (Exception exception) {
									exception.printStackTrace();
									retryDialog(activity, Data.SERVER_ERROR_MSG + "\n"+getResources().getString(R.string.tap_to_retry));
								}
								DialogPopup.dismissLoadingDialog();

							}

							@Override
							public void failure(RetrofitError error) {
								DialogPopup.dismissLoadingDialog();
								retryDialog(activity, Data.SERVER_NOT_RESOPNDING_MSG + "\n"+getResources().getString(R.string.tap_to_retry));

							}
						});
			} else {
				retryDialog(activity, Data.CHECK_INTERNET_MSG + "\n"+getResources().getString(R.string.tap_to_retry));
			}

		}
	}

	public void retryDialog(final Activity activity, String message) {

		DialogPopup.alertPopupTwoButtonsWithListeners(activity, "", message, getResources().getString(R.string.retry), getResources().getString(R.string.cancel),
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						getLeaderboardData(activity);
					}
				},
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						performBackPressed();
					}
				}, false, false);

	}


	public void performBackPressed() {
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}

	@Override
	public void onBackPressed() {
		performBackPressed();
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		ASSL.closeActivity(linearLayoutRoot);
		System.gc();
	}

}

