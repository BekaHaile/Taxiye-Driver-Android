package product.clicklabs.jugnoo;

import rmn.androidscreenlibrary.ASSL;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RidesActivity extends FragmentActivity{
	
	
	LinearLayout relative;
	
	Button backBtn;
	TextView title;
	
	LinearLayout linearLayoutRidesTabs;
	RelativeLayout relativeLayoutRideHistory, relativeLayoutFutureRides;
	TextView textViewRideHistory, textViewFutureRides;
	ImageView imageViewRideHistory, imageViewFutureRides;
	
	ViewPager viewPagerRides;
	
	RidesTabsAdapter ridesTabsAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rides);
		
		relative = (LinearLayout) findViewById(R.id.relative);
		new ASSL(RidesActivity.this, relative, 1134, 720, false);
		
		
		backBtn = (Button) findViewById(R.id.backBtn); 
		title = (TextView) findViewById(R.id.title); title.setTypeface(Data.latoRegular(getApplicationContext()));
		
		linearLayoutRidesTabs = (LinearLayout) findViewById(R.id.linearLayoutRidesTabs);
		
		relativeLayoutRideHistory = (RelativeLayout) findViewById(R.id.relativeLayoutRideHistory);
		relativeLayoutFutureRides = (RelativeLayout) findViewById(R.id.relativeLayoutFutureRides);
		
		textViewRideHistory = (TextView) findViewById(R.id.textViewRideHistory); textViewRideHistory.setTypeface(Data.latoRegular(getApplicationContext()));
		textViewFutureRides = (TextView) findViewById(R.id.textViewFutureRides); textViewFutureRides.setTypeface(Data.latoRegular(getApplicationContext()));
		
		imageViewRideHistory = (ImageView) findViewById(R.id.imageViewRideHistory);
		imageViewFutureRides = (ImageView) findViewById(R.id.imageViewFutureRides);
		
		viewPagerRides = (ViewPager) findViewById(R.id.viewPagerRides);
		
		ridesTabsAdapter = new RidesTabsAdapter(getSupportFragmentManager(), this);
		
		viewPagerRides.setAdapter(ridesTabsAdapter);
		
		backBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
				overridePendingTransition(R.anim.left_in, R.anim.left_out);
			}
		});
		
		
		relativeLayoutRideHistory.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				viewPagerRides.setCurrentItem(0, true);
			}
		});
		
		relativeLayoutFutureRides.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				viewPagerRides.setCurrentItem(1, true);
			}
		});
		
		viewPagerRides.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
		
		
		if(Data.userData.canSchedule == 1){
			linearLayoutRidesTabs.setVisibility(View.VISIBLE);
		}
		else{
			linearLayoutRidesTabs.setVisibility(View.GONE);
		}
		
		
		viewPagerRides.setCurrentItem(0, true);
		switchTabs(0);
		
	}
	
	
	public void switchTabs(int position){
		switch(position){
			case 0:
				imageViewRideHistory.setBackgroundResource(R.drawable.bg_tab_grey_pressed);
				imageViewFutureRides.setBackgroundResource(R.drawable.bg_tab_grey_selector);
				break;
				
			case 1:
				imageViewRideHistory.setBackgroundResource(R.drawable.bg_tab_grey_selector);
				imageViewFutureRides.setBackgroundResource(R.drawable.bg_tab_grey_pressed);
				break;
				
			default:
		}
	}
	
	class RidesTabsAdapter extends FragmentPagerAdapter {
		
		FragmentManager fragmentManager;
		Context context;
		
		RideHistoryFragment rideHistoryFragment;
		FutureSchedulesFragment futureSchedulesFragment;
		
		public RidesTabsAdapter(FragmentManager fragmentManager, Context context) {
			super(fragmentManager);
			this.fragmentManager = fragmentManager;
			this.context = context;
		}
		
		public RideHistoryFragment getRideHistoryFragment(){
			if(rideHistoryFragment == null){
				rideHistoryFragment = new RideHistoryFragment();
			}
			return rideHistoryFragment;
		}
		
		
		public FutureSchedulesFragment getFutureSchedulesFragment(){
			if(futureSchedulesFragment == null){
				futureSchedulesFragment = new FutureSchedulesFragment();
			}
			return futureSchedulesFragment;
		}
		

		@Override
		public Fragment getItem(int index) {
			switch (index) {
			case 0:
				return getRideHistoryFragment();
			case 1:
				return getFutureSchedulesFragment();
			}
			return null;
		}

		@Override
		public int getCount() {
			if(Data.userData.canSchedule == 1){
				return 2;
			}
			else{
				return 1;
			}
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
        ridesTabsAdapter.clearFragments();
        ASSL.closeActivity(relative);
        System.gc();
		super.onDestroy();
	}
	
}
