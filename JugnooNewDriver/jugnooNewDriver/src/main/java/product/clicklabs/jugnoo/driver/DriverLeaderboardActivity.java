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

import org.json.JSONObject;

import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.adapters.DriverLeaderboardAdapter;
import product.clicklabs.jugnoo.driver.datastructure.DriverLeaderboard;
import product.clicklabs.jugnoo.driver.datastructure.DriverLeaderboardData;
import product.clicklabs.jugnoo.driver.datastructure.HelpSection;
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

        ArrayList<DriverLeaderboard> driverLeaderboards = new ArrayList<DriverLeaderboard>();

        driverLeaderboards.add(new DriverLeaderboard(1, "Raman1D", "Chandigarh", 10, 0, 0));
        driverLeaderboards.add(new DriverLeaderboard(1, "Raman2D", "Chandigarh", 10, 0, 0));
        driverLeaderboards.add(new DriverLeaderboard(1, "Raman3D", "Chandigarh", 10, 0, 0));
        driverLeaderboards.add(new DriverLeaderboard(1, "Raman4D", "Chandigarh", 10, 0, 0));
        driverLeaderboards.add(new DriverLeaderboard(1, "Raman5D", "Chandigarh", 10, 0, 0));

        driverLeaderboards.add(new DriverLeaderboard(1, "Raman1w", "Chandigarh", 10, 0, 1));
        driverLeaderboards.add(new DriverLeaderboard(1, "Raman2w", "Chandigarh", 10, 0, 1));
        driverLeaderboards.add(new DriverLeaderboard(1, "Raman3w", "Chandigarh", 10, 0, 1));
        driverLeaderboards.add(new DriverLeaderboard(1, "Raman4w", "Chandigarh", 10, 0, 1));
        driverLeaderboards.add(new DriverLeaderboard(1, "Raman5w", "Chandigarh", 10, 0, 1));

        driverLeaderboards.add(new DriverLeaderboard(1, "Raman1m", "Chandigarh", 10, 0, 2));
        driverLeaderboards.add(new DriverLeaderboard(1, "Raman2m", "Chandigarh", 10, 0, 2));
        driverLeaderboards.add(new DriverLeaderboard(1, "Raman3m", "Chandigarh", 10, 0, 2));
        driverLeaderboards.add(new DriverLeaderboard(1, "Raman4m", "Chandigarh", 10, 0, 2));
        driverLeaderboards.add(new DriverLeaderboard(1, "Raman5m", "Chandigarh", 10, 0, 2));



        driverLeaderboards.add(new DriverLeaderboard(1, "Raman1d", "ChandigarhO", 10, 1, 0));
        driverLeaderboards.add(new DriverLeaderboard(1, "Raman2d", "ChandigarhO", 10, 1, 0));
        driverLeaderboards.add(new DriverLeaderboard(1, "Raman3d", "ChandigarhO", 10, 1, 0));
        driverLeaderboards.add(new DriverLeaderboard(1, "Raman4d", "ChandigarhO", 10, 1, 0));
        driverLeaderboards.add(new DriverLeaderboard(1, "Raman5d", "ChandigarhO", 10, 1, 0));

        driverLeaderboards.add(new DriverLeaderboard(1, "Raman1w", "ChandigarhO", 10, 1, 1));
        driverLeaderboards.add(new DriverLeaderboard(1, "Raman2w", "ChandigarhO", 10, 1, 1));
        driverLeaderboards.add(new DriverLeaderboard(1, "Raman3w", "ChandigarhO", 10, 1, 1));
        driverLeaderboards.add(new DriverLeaderboard(1, "Raman4w", "ChandigarhO", 10, 1, 1));
        driverLeaderboards.add(new DriverLeaderboard(1, "Raman5w", "ChandigarhO", 10, 1, 1));

        driverLeaderboards.add(new DriverLeaderboard(1, "Raman1m", "ChandigarhO", 10, 1, 2));
        driverLeaderboards.add(new DriverLeaderboard(1, "Raman2m", "ChandigarhO", 10, 1, 2));
        driverLeaderboards.add(new DriverLeaderboard(1, "Raman3m", "ChandigarhO", 10, 1, 2));
        driverLeaderboards.add(new DriverLeaderboard(1, "Raman4m", "ChandigarhO", 10, 1, 2));
        driverLeaderboards.add(new DriverLeaderboard(1, "Raman5m", "ChandigarhO", 10, 1, 2));

        driverLeaderboardData = new DriverLeaderboardData("Chandigarh", 10, 50, 20, 100, driverLeaderboards);


        adapter = new DriverLeaderboardAdapter(this, driverLeaderboardData.getDriverLeaderboardsList(leaderboardAreaMode, leaderboardMode), 0);
        listViewDriverLB.setAdapter(adapter);

//        getLeaderboardData(this);
		
	}


    public void setList(LeaderboardAreaMode leaderboardAreaMode, LeaderboardMode leaderboardMode){
        if(LeaderboardAreaMode.LOCAL == leaderboardAreaMode){
            adapter.setResults(driverLeaderboardData.getDriverLeaderboardsList(leaderboardAreaMode, leaderboardMode), 0);
            textViewPositionTop.setText("Your Position in "+driverLeaderboardData.cityName+" : "+driverLeaderboardData.cityPosition+"/"+driverLeaderboardData.cityTotal);
            textViewPositionBottom.setText("Your overall position : "+driverLeaderboardData.overallPosition+"/"+driverLeaderboardData.overallTotal);
        }
        else if(LeaderboardAreaMode.OVERALL == leaderboardAreaMode){
            adapter.setResults(driverLeaderboardData.getDriverLeaderboardsList(leaderboardAreaMode, leaderboardMode), 1);
            textViewPositionTop.setText("Your overall position : "+driverLeaderboardData.overallPosition+"/"+driverLeaderboardData.overallTotal);
            textViewPositionBottom.setText("Your Position in "+driverLeaderboardData.cityName+" : "+driverLeaderboardData.cityPosition+"/"+driverLeaderboardData.cityTotal);
        }
    }



    public void getLeaderboardData(final Activity activity) {
        if (AppStatus.getInstance(activity).isOnline(activity)) {
            DialogPopup.showLoadingDialog(activity, "Loading...");
            RequestParams params = new RequestParams();

            params.put("section", ""+HelpSection.ABOUT.getOrdinal());

            AsyncHttpClient client = Data.getClient();
            client.post(Data.SERVER_URL + "/get_information", params,
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
                            jObj = new JSONObject(response);
                            int flag = jObj.getInt("flag");
                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag)) {

                            }
                        } catch (Exception exception) {
                            exception.printStackTrace();
                            retryDialog(activity, Data.SERVER_ERROR_MSG + "\nTap to retry");
                        }
                        DialogPopup.dismissLoadingDialog();
                    }
                });
        } else {
            retryDialog(activity, Data.CHECK_INTERNET_MSG+"\nTap to retry");
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

