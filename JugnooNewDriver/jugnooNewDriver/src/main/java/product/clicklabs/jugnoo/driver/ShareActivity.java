package product.clicklabs.jugnoo.driver;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

import product.clicklabs.jugnoo.driver.adapters.ShareFragmentAdapter;
import product.clicklabs.jugnoo.driver.retrofit.model.LeaderboardActivityResponse;
import product.clicklabs.jugnoo.driver.retrofit.model.LeaderboardResponse;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity;
import product.clicklabs.jugnoo.driver.utils.FirebaseEvents;
import product.clicklabs.jugnoo.driver.utils.FlurryEventNames;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.widgets.PagerSlidingTabStrip;



public class ShareActivity extends BaseFragmentActivity implements FlurryEventNames, FirebaseEvents {
	
	LinearLayout linearLayoutRoot;

	ImageView imageViewBack;
	TextView textViewTitle;

	ViewPager viewPager;
	ShareFragmentAdapter shareFragmentAdapter;
	PagerSlidingTabStrip tabs;

	public LeaderboardResponse leaderboardResponse;
	public LeaderboardActivityResponse leaderboardActivityResponse;


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
	}


	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		setContentView(R.layout.activity_share);

		linearLayoutRoot = (LinearLayout) findViewById(R.id.linearLayoutRoot);
		new ASSL(ShareActivity.this, linearLayoutRoot, 1134, 720, false);

		viewPager = (ViewPager) findViewById(R.id.viewPager);
		shareFragmentAdapter = new ShareFragmentAdapter(this, getSupportFragmentManager());
		viewPager.setAdapter(shareFragmentAdapter);

		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		tabs.setIndicatorColor(getResources().getColor(R.color.new_orange));
		tabs.setTextColorResource(R.color.new_orange, R.color.menu_black);
		tabs.setTypeface(Fonts.mavenRegular(this), Typeface.NORMAL);
		tabs.setViewPager(viewPager);

		imageViewBack = (ImageView) findViewById(R.id.imageViewBack); 
		textViewTitle = (TextView) findViewById(R.id.textViewTitle); textViewTitle.setTypeface(Fonts.mavenRegular(this), Typeface.BOLD);


		imageViewBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				performbackPressed();
			}
		});

		viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {
				if(position == 0){
					MyApplication.getInstance().logEvent(INVITE_AND_EARN + "_" + EARN, null);
				} else if(position == 1){
					MyApplication.getInstance().logEvent(INVITE_AND_EARN+"_"+ACTIVITY,null);
				}
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});

	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		try {
			super.onActivityResult(requestCode, resultCode, data);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	public void performbackPressed(){
		if(viewPager.getCurrentItem() == 0){
			MyApplication.getInstance().logEvent(INVITE_AND_EARN+"_"+EARN+"_"+BACK,null);
		}else if(viewPager.getCurrentItem() == 1){
			MyApplication.getInstance().logEvent(INVITE_AND_EARN+"_"+ACTIVITY+"_"+BACK,null);
		}
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}
	

	@Override
	public void onBackPressed() {
		performbackPressed();
		super.onBackPressed();
	}
	
	
	@Override
	public void onDestroy() {
        ASSL.closeActivity(linearLayoutRoot);
        System.gc();
		super.onDestroy();

	}
}
