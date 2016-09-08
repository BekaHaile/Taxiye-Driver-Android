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

import product.clicklabs.jugnoo.driver.adapters.PaymentFragmentAdapter;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity;
import product.clicklabs.jugnoo.driver.utils.FlurryEventNames;
import product.clicklabs.jugnoo.driver.utils.NudgeClient;
import product.clicklabs.jugnoo.driver.widgets.PagerSlidingTabStrip;


public class PaymentActivity extends BaseFragmentActivity implements FlurryEventNames {
	
	LinearLayout linearLayoutRoot;

	ImageView imageViewBack;
	TextView textViewTitle;

	ViewPager viewPager;
	PaymentFragmentAdapter paymentFragmentAdapter;
	PagerSlidingTabStrip tabs;

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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_payments);

		linearLayoutRoot = (LinearLayout) findViewById(R.id.linearLayoutRoot);
		new ASSL(PaymentActivity.this, linearLayoutRoot, 1134, 720, false);

		viewPager = (ViewPager) findViewById(R.id.viewPager);
		paymentFragmentAdapter = new PaymentFragmentAdapter(PaymentActivity.this, getSupportFragmentManager());
		viewPager.setAdapter(paymentFragmentAdapter);

		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		tabs.setIndicatorColor(getResources().getColor(R.color.new_orange));
		tabs.setTextColorResource(R.color.new_orange, R.color.menu_black);
		tabs.setTypeface(Data.latoRegular(this), Typeface.NORMAL);
		tabs.setViewPager(viewPager);

		imageViewBack = (ImageView) findViewById(R.id.imageViewBack); 
		textViewTitle = (TextView) findViewById(R.id.textViewTitle);
		textViewTitle.setTypeface(Data.latoRegular(this), Typeface.BOLD);


		imageViewBack.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				performbackPressed();
			}
		});

		try {
			if(getIntent().getExtras().getInt("trick_page") ==1){
				viewPager.setCurrentItem(1);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {
				if(position == 1){
					NudgeClient.trackEvent(PaymentActivity.this, NUDGE_INVOICES, null);
				} else if(position == 2){
					NudgeClient.trackEvent(PaymentActivity.this, NUDGE_EARNING_CLICK, null);
				}
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});

		NudgeClient.trackEvent(this, NUDGE_PAYMENT_CLICK, null);

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
		finish();
		overridePendingTransition(R.anim.left_in, R.anim.left_out);
	}
	

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		performbackPressed();
	}
	
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			ASSL.closeActivity(linearLayoutRoot);
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.gc();


	}
}
