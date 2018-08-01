package product.clicklabs.jugnoo.driver;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;



import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.adapters.DriverLeaderboardAdapter;
import product.clicklabs.jugnoo.driver.datastructure.DriverLeaderboard;
import product.clicklabs.jugnoo.driver.datastructure.DriverLeaderboardData;
import product.clicklabs.jugnoo.driver.datastructure.LeaderboardAreaMode;
import product.clicklabs.jugnoo.driver.datastructure.LeaderboardMode;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.fragments.ShareLeaderboardFragment;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity;
import product.clicklabs.jugnoo.driver.utils.FlurryEventNames;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.utils.Prefs;

public class DriverLeaderboardActivity extends BaseFragmentActivity implements FlurryEventNames {

	LinearLayout linearLayoutRoot, linearLayoutContainer;

	View backBtn;
	TextView title;

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
		//

	}

	@Override
	protected void onStop() {
		super.onStop();

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_driver_leaderboard);

		linearLayoutRoot = (LinearLayout) findViewById(R.id.linearLayoutRoot);
		linearLayoutContainer = (LinearLayout) findViewById(R.id.linearLayoutContainer);
		new ASSL(DriverLeaderboardActivity.this, linearLayoutRoot, 1134, 720, false);


		backBtn = findViewById(R.id.backBtn);
		title = (TextView) findViewById(R.id.title);
		title.setTypeface(Fonts.mavenRegular(this));
		title.setText(R.string.super_driver);
		if(Prefs.with(this).getInt(SPLabels.VEHICLE_TYPE,0) == 2){
			title.setText(getResources().getString(R.string.super_biker));
		}
		textViewPositionTop = (TextView) findViewById(R.id.textViewPositionTop);
		textViewPositionTop.setTypeface(Fonts.mavenRegular(this));
		((TextView) findViewById(R.id.textViewPositions)).setTypeface(Fonts.mavenRegular(this));
		((TextView) findViewById(R.id.textViewNoRides)).setTypeface(Fonts.mavenRegular(this));
		textViewDaily = (TextView) findViewById(R.id.textViewDaily);
		textViewDaily.setTypeface(Fonts.mavenRegular(this));
		textViewWeekly = (TextView) findViewById(R.id.textViewWeekly);
		textViewWeekly.setTypeface(Fonts.mavenRegular(this));
		textViewMonthly = (TextView) findViewById(R.id.textViewMonthlyValue);
		textViewMonthly.setTypeface(Fonts.mavenRegular(this));
		textViewPositionBottom = (TextView) findViewById(R.id.textViewPositionBottom);
		textViewPositionBottom.setTypeface(Fonts.mavenRegular(this));


		listViewDriverLB = (ListView) findViewById(R.id.listViewDriverLB);

		relativeLayoutPositionBottom = (RelativeLayout) findViewById(R.id.relativeLayoutPositionBottom);

		textViewDaily.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				leaderboardMode = LeaderboardMode.DAILY;
//				setList(leaderboardAreaMode, leaderboardMode);
			}
		});

		textViewWeekly.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				leaderboardMode = LeaderboardMode.WEEKLY;
//				setList(leaderboardAreaMode, leaderboardMode);
			}
		});

		textViewMonthly.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				leaderboardMode = LeaderboardMode.MONTHLY;
//				setList(leaderboardAreaMode, leaderboardMode);
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
//				setList(leaderboardAreaMode, leaderboardMode);
			}
		});

		backBtn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});

		adapter = new DriverLeaderboardAdapter(this, new ArrayList<DriverLeaderboard>(), 0);
		listViewDriverLB.setAdapter(adapter);

//		getLeaderboardData(this);

		try {
			ShareLeaderboardFragment shareLeaderboardFragment = new ShareLeaderboardFragment();

			getSupportFragmentManager()
					.beginTransaction()
					.replace(linearLayoutContainer.getId(), shareLeaderboardFragment, ShareLeaderboardFragment.class.getSimpleName())
					.commit();
		} catch (Exception e) {
			e.printStackTrace();
		}

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

