package product.clicklabs.jugnoo.driver;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.driver.datastructure.UpdateDriverEarnings;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity;
import product.clicklabs.jugnoo.driver.utils.Fonts;

public class DriverHistoryActivity extends BaseFragmentActivity {
	
	
	LinearLayout relative;
	
	View backBtn;

	RelativeLayout relativeLayoutRides, relativeLayoutMissed, relativeLayoutReferralMoney;
	TextView textViewRides, textViewMissed, textViewDailyText, textViewDailyValue, textViewMonthlyText, textViewMonthlyValue,
			textViewReferralMoneyText, textViewReferralMoneyValue;
	ImageView imageViewRides, imageViewMissed;
	
	ViewPager viewPagerDriverHistory;
	
	DriverHistoryTabsAdapter driverHistoryTabsAdapter;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_driver_history);
		
		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(DriverHistoryActivity.this, relative, 1134, 720, false);
		
		
		backBtn = findViewById(R.id.backBtn);

		relativeLayoutRides = (RelativeLayout) findViewById(R.id.relativeLayoutRides);
		relativeLayoutMissed = (RelativeLayout) findViewById(R.id.relativeLayoutMissed);
		relativeLayoutReferralMoney = (RelativeLayout) findViewById(R.id.relativeLayoutReferralMoney);
		relativeLayoutReferralMoney.setVisibility(View.GONE);
		
		textViewRides = (TextView) findViewById(R.id.textViewRides); textViewRides.setTypeface(Fonts.mavenRegular(getApplicationContext()));
		textViewMissed = (TextView) findViewById(R.id.textViewMissed); textViewMissed.setTypeface(Fonts.mavenRegular(getApplicationContext()));
		textViewDailyText = (TextView) findViewById(R.id.textViewDailyText); textViewDailyText.setTypeface(Fonts.mavenRegular(getApplicationContext()),Typeface.BOLD);
		textViewDailyValue = (TextView) findViewById(R.id.textViewDailyValue); textViewDailyValue.setTypeface(Fonts.mavenRegular(getApplicationContext()), Typeface.BOLD);
		textViewMonthlyText = (TextView) findViewById(R.id.textViewMonthlyText); textViewMonthlyText.setTypeface(Fonts.mavenRegular(getApplicationContext()), Typeface.BOLD);
		textViewMonthlyValue = (TextView) findViewById(R.id.textViewMonthlyValue); textViewMonthlyValue.setTypeface(Fonts.mavenRegular(getApplicationContext()), Typeface.BOLD);
		textViewReferralMoneyText = (TextView) findViewById(R.id.textViewReferralMoneyText); textViewReferralMoneyText.setTypeface(Fonts.mavenRegular(getApplicationContext()), Typeface.BOLD);
		textViewReferralMoneyValue = (TextView) findViewById(R.id.textViewReferralMoneyValue); textViewReferralMoneyValue.setTypeface(Fonts.mavenRegular(getApplicationContext()), Typeface.BOLD);
		
		imageViewRides = (ImageView) findViewById(R.id.imageViewRides);
		imageViewMissed = (ImageView) findViewById(R.id.imageViewMissed);


		textViewDailyValue.setText("");
		textViewMonthlyValue.setText("");
		textViewReferralMoneyValue.setText("");


		viewPagerDriverHistory = (ViewPager) findViewById(R.id.viewPagerDriverHistory);
		
		driverHistoryTabsAdapter = new DriverHistoryTabsAdapter(getSupportFragmentManager(), this);
		
		viewPagerDriverHistory.setAdapter(driverHistoryTabsAdapter);
		
		backBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
				overridePendingTransition(R.anim.left_in, R.anim.left_out);
			}
		});
		
		
		relativeLayoutRides.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				viewPagerDriverHistory.setCurrentItem(0, true);
			}
		});
		
		relativeLayoutMissed.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				viewPagerDriverHistory.setCurrentItem(1, true);
			}
		});
		
		viewPagerDriverHistory.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				switchTabs(arg0);
			}
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}
			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		
		viewPagerDriverHistory.setCurrentItem(0, true);
		switchTabs(0);
	}

	UpdateDriverEarnings updateDriverEarnings = new UpdateDriverEarnings() {
		@Override
		public void updateDriverEarnings(String dailyEarnings, String monthlyEarnings, String referralMoney) {
			textViewDailyValue.setText(getResources().getString(R.string.rupee) + " " + dailyEarnings);
			textViewMonthlyValue.setText(getResources().getString(R.string.rupee) + " " + monthlyEarnings);
			textViewReferralMoneyValue.setText(getResources().getString(R.string.rupee) + " " + referralMoney);
			if(null == referralMoney){
				relativeLayoutReferralMoney.setVisibility(View.GONE);
			} else{
				relativeLayoutReferralMoney.setVisibility(View.VISIBLE);
			}
		}
	};

	
	public void switchTabs(int position){
		switch(position){
			case 0:
				imageViewRides.setBackgroundResource(R.drawable.bg_tab_grey_pressed);
				imageViewMissed.setBackgroundResource(R.drawable.bg_tab_grey_selector);
				break;
				
			case 1:
				imageViewRides.setBackgroundResource(R.drawable.bg_tab_grey_selector);
				imageViewMissed.setBackgroundResource(R.drawable.bg_tab_grey_pressed);
				break;
				
			default:
		}
	}
	
	class DriverHistoryTabsAdapter extends FragmentPagerAdapter {
		
		FragmentManager fragmentManager;
		Context context;
		
		DriverRidesFragment driverRidesFragment;
		DriverMissedRidesFragment driverMissedRidesFragment;
		
		public DriverHistoryTabsAdapter(FragmentManager fragmentManager, Context context) {
			super(fragmentManager);
			this.fragmentManager = fragmentManager;
			this.context = context;
		}
		
		public DriverRidesFragment getRidesFrag(){
			if(driverRidesFragment == null){
				driverRidesFragment = new DriverRidesFragment();
			}
			driverRidesFragment.updateDriverEarnings = updateDriverEarnings;
			return driverRidesFragment;
		}
		
		
		public DriverMissedRidesFragment getMissedRidesFrag(){
			if(driverMissedRidesFragment == null){
				driverMissedRidesFragment = new DriverMissedRidesFragment();
			}
			return driverMissedRidesFragment;
		}
		

		@Override
		public Fragment getItem(int index) {
			switch (index) {
			case 0:
				return getRidesFrag();
			case 1:
				return getMissedRidesFrag();
			}
			return null;
		}

		@Override
		public int getCount() {
			return 1;
		}
		
		public void clearFragments(){
			if (fragmentManager.getFragments() != null) {
		        fragmentManager.getFragments().clear();
		    }
		}

	}

	
	
	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
		super.onBackPressed();
	}
	
	
	@Override
	protected void onDestroy() {
        driverHistoryTabsAdapter.clearFragments();
        ASSL.closeActivity(relative);
        System.gc();
		super.onDestroy();
	}
	
	
}
