package product.clicklabs.jugnoo.driver;

import android.app.Activity;
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
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.CustomAsyncHttpResponseHandler;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import rmn.androidscreenlibrary.ASSL;

public class DriverLeaderboardActivity extends FragmentActivity{
	
	
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
		FlurryAgent.init(this, Data.FLURRY_KEY);
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
        textViewTitle = (TextView) findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Data.latoRegular(this));


        textViewPositionTop = (TextView) findViewById(R.id.textViewPositionTop); textViewPositionTop.setTypeface(Data.latoRegular(this));
        ((TextView) findViewById(R.id.textViewPositions)).setTypeface(Data.latoRegular(this));
        ((TextView) findViewById(R.id.textViewNoRides)).setTypeface(Data.latoRegular(this));
        textViewDaily = (TextView) findViewById(R.id.textViewDaily); textViewDaily.setTypeface(Data.latoRegular(this));
        textViewWeekly = (TextView) findViewById(R.id.textViewWeekly); textViewWeekly.setTypeface(Data.latoRegular(this));
        textViewMonthly = (TextView) findViewById(R.id.textViewMonthly); textViewMonthly.setTypeface(Data.latoRegular(this));
        textViewPositionBottom = (TextView) findViewById(R.id.textViewPositionBottom); textViewPositionBottom.setTypeface(Data.latoRegular(this));


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
                if(leaderboardAreaMode == LeaderboardAreaMode.LOCAL){
                    leaderboardAreaMode = LeaderboardAreaMode.OVERALL;
                }
                else{
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


    public void setList(LeaderboardAreaMode leaderboardAreaMode, LeaderboardMode leaderboardMode){
        int cityPos = 0, cityTotal = 0;
        int overallPos = 0, overallTotal = 0;
        if(LeaderboardMode.DAILY == leaderboardMode){
            cityPos = driverLeaderboardData.cityPositionDay;
            cityTotal = driverLeaderboardData.cityTotalDay;
            overallPos = driverLeaderboardData.overallPositionDay;
            overallTotal = driverLeaderboardData.overallTotalDay;
        }
        else if(LeaderboardMode.WEEKLY == leaderboardMode){
            cityPos = driverLeaderboardData.cityPositionWeek;
            cityTotal = driverLeaderboardData.cityTotalWeek;
            overallPos = driverLeaderboardData.overallPositionWeek;
            overallTotal = driverLeaderboardData.overallTotalWeek;
        }
        else if(LeaderboardMode.MONTHLY == leaderboardMode){
            cityPos = driverLeaderboardData.cityPositionMonth;
            cityTotal = driverLeaderboardData.cityTotalMonth;
            overallPos = driverLeaderboardData.overallPositionMonth;
            overallTotal = driverLeaderboardData.overallTotalMonth;
        }
        if(LeaderboardAreaMode.LOCAL == leaderboardAreaMode){
            adapter.setResults(driverLeaderboardData.getDriverLeaderboardsList(leaderboardAreaMode, leaderboardMode), 0);
            textViewPositionTop.setText("Your Position in "+driverLeaderboardData.cityName+" : "+cityPos+"/"+cityTotal);
            textViewPositionBottom.setText("Your overall position : "+overallPos+"/"+overallTotal);
        }
        else if(LeaderboardAreaMode.OVERALL == leaderboardAreaMode){
            adapter.setResults(driverLeaderboardData.getDriverLeaderboardsList(leaderboardAreaMode, leaderboardMode), 1);
            textViewPositionTop.setText("Your overall position : "+overallPos+"/"+overallTotal);
            textViewPositionBottom.setText("Your Position in "+driverLeaderboardData.cityName+" : "+cityPos+"/"+cityTotal);
        }
    }



    public void getLeaderboardData(final Activity activity) {
        if (AppStatus.getInstance(activity).isOnline(activity)) {
            DialogPopup.showLoadingDialog(activity, "Loading...");
            RequestParams params = new RequestParams();

            params.put("access_token", Data.userData.accessToken);

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

                                if(ApiResponseFlags.ACTION_FAILED.getOrdinal() == flag){
                                   DialogPopup.alertPopup(activity, "", message);
                                }
                                else if(ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag){

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

                                    JSONObject jOverallLeaderBoard = jObj.getJSONObject("overall_leader_board");
                                    JSONArray jOverallLeaderBoardDay = jOverallLeaderBoard.getJSONArray("day");
                                    for(int i=0; i<jOverallLeaderBoardDay.length(); i++){
                                        JSONObject ji = jOverallLeaderBoardDay.getJSONObject(i);
                                        driverLeaderboards.add(new DriverLeaderboard(ji.getInt("driver_id"),
                                            ji.getString("driver_name"),
                                            ji.getString("city_name"),
                                            ji.getInt("number_of_rides_overall"), LeaderboardAreaMode.OVERALL.getOrdinal(), LeaderboardMode.DAILY.getOrdinal()));
                                    }
                                    JSONArray jOverallLeaderBoardWeek = jOverallLeaderBoard.getJSONArray("week");
                                    for(int i=0; i<jOverallLeaderBoardWeek.length(); i++){
                                        JSONObject ji = jOverallLeaderBoardWeek.getJSONObject(i);
                                        driverLeaderboards.add(new DriverLeaderboard(ji.getInt("driver_id"),
                                            ji.getString("driver_name"),
                                            ji.getString("city_name"),
                                            ji.getInt("number_of_rides_overall"), LeaderboardAreaMode.OVERALL.getOrdinal(), LeaderboardMode.WEEKLY.getOrdinal()));
                                    }
                                    JSONArray jOverallLeaderBoardMonth = jOverallLeaderBoard.getJSONArray("month");
                                    for(int i=0; i<jOverallLeaderBoardMonth.length(); i++){
                                        JSONObject ji = jOverallLeaderBoardMonth.getJSONObject(i);
                                        driverLeaderboards.add(new DriverLeaderboard(ji.getInt("driver_id"),
                                            ji.getString("driver_name"),
                                            ji.getString("city_name"),
                                            ji.getInt("number_of_rides_overall"), LeaderboardAreaMode.OVERALL.getOrdinal(), LeaderboardMode.MONTHLY.getOrdinal()));
                                    }


                                    JSONObject jCityLeaderBoard = jObj.getJSONObject("city_leader_board");
                                    JSONArray jCityLeaderBoardDay = jCityLeaderBoard.getJSONArray("day");
                                    for(int i=0; i<jCityLeaderBoardDay.length(); i++){
                                        JSONObject ji = jCityLeaderBoardDay.getJSONObject(i);
                                        driverLeaderboards.add(new DriverLeaderboard(ji.getInt("driver_id"),
                                            ji.getString("driver_name"),
                                            ji.getString("city_name"),
                                            ji.getInt("number_of_rides_in_city"), LeaderboardAreaMode.LOCAL.getOrdinal(), LeaderboardMode.DAILY.getOrdinal()));
                                    }
                                    JSONArray jCityLeaderBoardWeek = jCityLeaderBoard.getJSONArray("week");
                                    for(int i=0; i<jCityLeaderBoardWeek.length(); i++){
                                        JSONObject ji = jCityLeaderBoardWeek.getJSONObject(i);
                                        driverLeaderboards.add(new DriverLeaderboard(ji.getInt("driver_id"),
                                            ji.getString("driver_name"),
                                            ji.getString("city_name"),
                                            ji.getInt("number_of_rides_in_city"), LeaderboardAreaMode.LOCAL.getOrdinal(), LeaderboardMode.WEEKLY.getOrdinal()));
                                    }
                                    JSONArray jCityLeaderBoardMonth = jCityLeaderBoard.getJSONArray("month");
                                    for(int i=0; i<jCityLeaderBoardMonth.length(); i++){
                                        JSONObject ji = jCityLeaderBoardMonth.getJSONObject(i);
                                        driverLeaderboards.add(new DriverLeaderboard(ji.getInt("driver_id"),
                                            ji.getString("driver_name"),
                                            ji.getString("city_name"),
                                            ji.getInt("number_of_rides_in_city"), LeaderboardAreaMode.LOCAL.getOrdinal(), LeaderboardMode.MONTHLY.getOrdinal()));
                                    }

                                    driverLeaderboardData = new DriverLeaderboardData("Chandigarh", cityPositionDay, cityPositionWeek, cityPositionMonth, cityTotalDay, cityTotalWeek, cityTotalMonth,
                                        overallPositionDay, overallPositionWeek, overallPositionMonth, overallTotalDay, overallTotalWeek, overallTotalMonth, driverLeaderboards);

                                }
                                else{
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

    public void retryDialog(final Activity activity, String message){

        DialogPopup.alertPopupTwoButtonsWithListeners(activity, "", message, "Retry", "Cancel",
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

