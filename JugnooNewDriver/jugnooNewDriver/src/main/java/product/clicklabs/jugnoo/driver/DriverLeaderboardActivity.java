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
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
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
import product.clicklabs.jugnoo.driver.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import rmn.androidscreenlibrary.ASSL;

public class DriverLeaderboardActivity extends FragmentActivity {


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
		textViewMonthly = (TextView) findViewById(R.id.textViewMonthly);
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

///		getLeaderboardData(this);
		DriverLeaderBoard(this);


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
				textViewPositionTop.setText("Could not get local position");
			} else {
				textViewPositionTop.setText("Your Position in " + driverLeaderboardData.cityName + " : " + cityPos + "/" + cityTotal);
			}
			if (0 == overallPos) {
				textViewPositionBottom.setText("Overall position");
			} else {
				textViewPositionBottom.setText("Your overall position : " + overallPos + "/" + overallTotal);
			}
		} else if (LeaderboardAreaMode.OVERALL == leaderboardAreaMode) {
			adapter.setResults(driverLeaderboardData.getDriverLeaderboardsList(leaderboardAreaMode, leaderboardMode), 1);

			if (0 == overallPos) {
				textViewPositionTop.setText("Could not get overall position");
			} else {
				textViewPositionTop.setText("Your overall position : " + overallPos + "/" + overallTotal);
			}
			if (0 == cityPos) {
				textViewPositionBottom.setText("Local position");
			} else {
				textViewPositionBottom.setText("Your Position in " + driverLeaderboardData.cityName + " : " + cityPos + "/" + cityTotal);
			}

		}
	}


	public void getLeaderboardData(final Activity activity) {
		if (!HomeActivity.checkIfUserDataNull(activity)) {
			if (AppStatus.getInstance(activity).isOnline(activity)) {
				DialogPopup.showLoadingDialog(activity, "Loading...");
				RequestParams params = new RequestParams();

				params.put("access_token", Data.userData.accessToken);
//                params.put("access_token", "dce0da8447650e8ebd66fe4de118570819c988649c28a62d3129f552656c3f43");


				AsyncHttpClient client = Data.getClient();
				client.post(Data.SERVER_URL + "/driver/show/leader_board", params,
						new CustomAsyncHttpResponseHandler() {
							private JSONObject jObj;

							@Override
							public void onFailure(Throwable arg3) {
								Log.e("request fail", arg3.toString());
								DialogPopup.dismissLoadingDialog();
								retryDialog(activity, Data.SERVER_NOT_RESOPNDING_MSG + "\nTap to retry");
							}

							@Override
							public void onSuccess(String response) {
								Log.i("Server response faq ", "response = " + response);
								try {

//                            {
//                                "flag": 143,
//                                "message": "Driver Leader board created successfully",
//                                "driver_leader_board": {
//                                "overall_leader_board": {
//                                    "day": [
//                                    {
//                                        "driver_id": 829,
//                                        "driver_name": "Driver Rajasthan",
//                                        "city": 4,
//                                        "overall_rank": 1,
//                                        "time_interval": "day"
//                                    }
//                                    ],
//                                    "week": [
//                                    {
//                                        "driver_id": 829,
//                                        "driver_name": "Driver Rajasthan",
//                                        "city": 4,
//                                        "overall_rank": 1,
//                                        "time_interval": "week"
//                                    }
//                                    ],
//                                    "month": [
//                                    {
//                                        "driver_id": 229,
//                                        "driver_name": "Driver 7",
//                                        "city": 1,
//                                        "overall_rank": 2,
//                                        "time_interval": "month"
//                                    },
//                                    {
//                                        "driver_id": 829,
//                                        "driver_name": "Driver Rajasthan",
//                                        "city": 4,
//                                        "overall_rank": 1,
//                                        "time_interval": "month"
//                                    }
//                                    ]
//                                },
//                                "overall_driver_rank": {
//                                    "day": 1,
//                                        "week": 1,
//                                        "month": 2
//                                },
//                                "city_leader_board": {},
//                                "city_driver_rank": {
//                                    "day": 0,
//                                        "week": 0,
//                                        "month": 0
//                                },
//                                "total_drivers_city": {
//                                    "day": 0,
//                                        "week": 0,
//                                        "month": 0
//                                },
//                                "total_drivers_overall": {
//                                    "month": 2,
//                                        "day": 1,
//                                        "week": 1
//                                }
//                            }
//                            }

									jObj = new JSONObject(response);
									int flag = jObj.getInt("flag");
									String message = JSONParser.getServerMessage(jObj);
									if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag)) {

										if (ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag) {
											DialogPopup.alertPopup(activity, "", message);
										} else if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {

											ArrayList<DriverLeaderboard> driverLeaderboards = new ArrayList<DriverLeaderboard>();

											JSONObject jDriverLeaderBoard = jObj.getJSONObject("driver_leader_board");

											int cityPositionDay = jDriverLeaderBoard.getJSONObject("city_driver_rank").getInt("day");
											int cityPositionWeek = jDriverLeaderBoard.getJSONObject("city_driver_rank").getInt("week");
											int cityPositionMonth = jDriverLeaderBoard.getJSONObject("city_driver_rank").getInt("month");
											int cityTotalDay = jDriverLeaderBoard.getJSONObject("total_drivers_city").getInt("day");
											int cityTotalWeek = jDriverLeaderBoard.getJSONObject("total_drivers_city").getInt("week");
											int cityTotalMonth = jDriverLeaderBoard.getJSONObject("total_drivers_city").getInt("month");
											int overallPositionDay = jDriverLeaderBoard.getJSONObject("overall_driver_rank").getInt("day");
											int overallPositionWeek = jDriverLeaderBoard.getJSONObject("overall_driver_rank").getInt("week");
											int overallPositionMonth = jDriverLeaderBoard.getJSONObject("overall_driver_rank").getInt("month");
											int overallTotalDay = jDriverLeaderBoard.getJSONObject("total_drivers_overall").getInt("day");
											int overallTotalWeek = jDriverLeaderBoard.getJSONObject("total_drivers_overall").getInt("week");
											int overallTotalMonth = jDriverLeaderBoard.getJSONObject("total_drivers_overall").getInt("month");

											JSONObject jOverallLeaderBoard = jDriverLeaderBoard.getJSONObject("overall_leader_board");
											if (jOverallLeaderBoard.has("day")) {
												JSONArray jOverallLeaderBoardDay = jOverallLeaderBoard.getJSONArray("day");
												for (int i = 0; i < jOverallLeaderBoardDay.length(); i++) {
													JSONObject ji = jOverallLeaderBoardDay.getJSONObject(i);
													driverLeaderboards.add(new DriverLeaderboard(ji.getInt("driver_id"),
															ji.getString("driver_name"),
															ji.getString("city_name"),
															ji.getInt("number_of_rides_overall"), LeaderboardAreaMode.OVERALL.getOrdinal(), LeaderboardMode.DAILY.getOrdinal()));
												}
											}
											if (jOverallLeaderBoard.has("week")) {
												JSONArray jOverallLeaderBoardWeek = jOverallLeaderBoard.getJSONArray("week");
												for (int i = 0; i < jOverallLeaderBoardWeek.length(); i++) {
													JSONObject ji = jOverallLeaderBoardWeek.getJSONObject(i);
													driverLeaderboards.add(new DriverLeaderboard(ji.getInt("driver_id"),
															ji.getString("driver_name"),
															ji.getString("city_name"),
															ji.getInt("number_of_rides_overall"), LeaderboardAreaMode.OVERALL.getOrdinal(), LeaderboardMode.WEEKLY.getOrdinal()));
												}
											}
											if (jOverallLeaderBoard.has("month")) {
												JSONArray jOverallLeaderBoardMonth = jOverallLeaderBoard.getJSONArray("month");
												for (int i = 0; i < jOverallLeaderBoardMonth.length(); i++) {
													JSONObject ji = jOverallLeaderBoardMonth.getJSONObject(i);
													driverLeaderboards.add(new DriverLeaderboard(ji.getInt("driver_id"),
															ji.getString("driver_name"),
															ji.getString("city_name"),
															ji.getInt("number_of_rides_overall"), LeaderboardAreaMode.OVERALL.getOrdinal(), LeaderboardMode.MONTHLY.getOrdinal()));
												}
											}


											JSONObject jCityLeaderBoard = jDriverLeaderBoard.getJSONObject("city_leader_board");
											if (jCityLeaderBoard.has("day")) {
												JSONArray jCityLeaderBoardDay = jCityLeaderBoard.getJSONArray("day");
												for (int i = 0; i < jCityLeaderBoardDay.length(); i++) {
													JSONObject ji = jCityLeaderBoardDay.getJSONObject(i);
													driverLeaderboards.add(new DriverLeaderboard(ji.getInt("driver_id"),
															ji.getString("driver_name"),
															ji.getString("city_name"),
															ji.getInt("number_of_rides_in_city"), LeaderboardAreaMode.LOCAL.getOrdinal(), LeaderboardMode.DAILY.getOrdinal()));
												}
											}
											if (jCityLeaderBoard.has("week")) {
												JSONArray jCityLeaderBoardWeek = jCityLeaderBoard.getJSONArray("week");
												for (int i = 0; i < jCityLeaderBoardWeek.length(); i++) {
													JSONObject ji = jCityLeaderBoardWeek.getJSONObject(i);
													driverLeaderboards.add(new DriverLeaderboard(ji.getInt("driver_id"),
															ji.getString("driver_name"),
															ji.getString("city_name"),
															ji.getInt("number_of_rides_in_city"), LeaderboardAreaMode.LOCAL.getOrdinal(), LeaderboardMode.WEEKLY.getOrdinal()));
												}
											}
											if (jCityLeaderBoard.has("month")) {
												JSONArray jCityLeaderBoardMonth = jCityLeaderBoard.getJSONArray("month");
												for (int i = 0; i < jCityLeaderBoardMonth.length(); i++) {
													JSONObject ji = jCityLeaderBoardMonth.getJSONObject(i);
													driverLeaderboards.add(new DriverLeaderboard(ji.getInt("driver_id"),
															ji.getString("driver_name"),
															ji.getString("city_name"),
															ji.getInt("number_of_rides_in_city"), LeaderboardAreaMode.LOCAL.getOrdinal(), LeaderboardMode.MONTHLY.getOrdinal()));
												}
											}


											String cityName = jDriverLeaderBoard.optString("driver_city", "Local");

											driverLeaderboardData = new DriverLeaderboardData(cityName, cityPositionDay, cityPositionWeek, cityPositionMonth, cityTotalDay, cityTotalWeek, cityTotalMonth,
													overallPositionDay, overallPositionWeek, overallPositionMonth, overallTotalDay, overallTotalWeek, overallTotalMonth, driverLeaderboards);

											setList(leaderboardAreaMode, leaderboardMode);
										} else {
											DialogPopup.alertPopup(activity, "", message);
										}

									}
								} catch (Exception exception) {
									exception.printStackTrace();
									retryDialog(activity, Data.SERVER_ERROR_MSG + "\nTap to retry");
								}
								DialogPopup.dismissLoadingDialog();
							}
						});
			} else {
				retryDialog(activity, Data.CHECK_INTERNET_MSG + "\nTap to retry");
			}
		}
	}

	public void DriverLeaderBoard(final Activity activity) {
		if (!HomeActivity.checkIfUserDataNull(activity)) {
			if (AppStatus.getInstance(activity).isOnline(activity)) {
				DialogPopup.showLoadingDialog(activity, "Loading...");
				RequestParams params = new RequestParams();

				params.put("access_token", Data.userData.accessToken);

				RestClient.getApiServices().driverLeaderBoard(Data.userData.accessToken,
						new Callback<DriverLeaderBoard>() {
							@Override
							public void success(DriverLeaderBoard driverLeaderBoard, Response response) {
								try {

									String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
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
									retryDialog(activity, Data.SERVER_ERROR_MSG + "\nTap to retry");
								}
								DialogPopup.dismissLoadingDialog();

							}

							@Override
							public void failure(RetrofitError error) {
								DialogPopup.dismissLoadingDialog();
								retryDialog(activity, Data.SERVER_NOT_RESOPNDING_MSG + "\nTap to retry");

							}
						});
			} else {
				retryDialog(activity, Data.CHECK_INTERNET_MSG + "\nTap to retry");
			}

		}
	}

	public void retryDialog(final Activity activity, String message) {

		DialogPopup.alertPopupTwoButtonsWithListeners(activity, "", message, "Retry", "Cancel",
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
//						getLeaderboardData(activity);
						DriverLeaderBoard(activity);
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

