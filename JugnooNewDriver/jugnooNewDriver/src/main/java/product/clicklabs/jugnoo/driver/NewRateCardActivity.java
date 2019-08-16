package product.clicklabs.jugnoo.driver;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.driver.adapters.RateCardFragmentAdapter;
import product.clicklabs.jugnoo.driver.datastructure.DisplayPushHandler;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.EventsHolder;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.widgets.PagerSlidingTabStrip;


/**
 * Created by socomo on 10/15/15.
 */
public class NewRateCardActivity extends BaseFragmentActivity implements DisplayPushHandler {

    private LinearLayout root;
    private TextView title;
    private View backBtn;

	ViewPager viewPager;
	RateCardFragmentAdapter rateCardFragmentAdapter;
	PagerSlidingTabStrip tabs;
	Boolean notVisible = true;

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_center);

		EventsHolder.displayPushHandler = this;

        root = (LinearLayout)findViewById(R.id.root);
        new ASSL(this, root, 1134, 720, false);

		title = (TextView) findViewById(R.id.title);
		title.setTypeface(Fonts.mavenRegular(getApplicationContext()));
		title.setText(getResources().getString(R.string.rate_card));
		backBtn = findViewById(R.id.backBtn);
		
		backBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});

		viewPager = (ViewPager) findViewById(R.id.viewPager);
		rateCardFragmentAdapter = new RateCardFragmentAdapter(NewRateCardActivity.this, getSupportFragmentManager(),
				getIntent().getBooleanExtra(Constants.KEY_HTML_RATE_CARD, false));
		viewPager.setAdapter(rateCardFragmentAdapter);
		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		tabs.setIndicatorColor(getResources().getColor(R.color.themeColor));
		tabs.setTextColorResource(R.color.themeColor, R.color.textColor);
		tabs.setTypeface(Fonts.mavenRegular(this), Typeface.NORMAL);
		tabs.setViewPager(viewPager);
		tabs.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
		tabs.setVisibility(rateCardFragmentAdapter.getCount() > 1 ? View.VISIBLE : View.GONE);

//		try {
//			if(getIntent().getExtras().getInt("trick_page") ==1){
//				viewPager.setCurrentItem(1);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {
				if (position == 0) {

				} else if (position == 1) {

				}
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});

    }




    public void performBackPressed(){
		notVisible = true;
        finish();
        overridePendingTransition(R.anim.left_in, R.anim.left_out);
    }

	@Override
	public void onBackPressed() {
		if(viewPager.getCurrentItem() == 0){

		} else if(viewPager.getCurrentItem() == 1){

		}
		performBackPressed();
	}

	@Override
	protected void onDestroy() {
		ASSL.closeActivity(root);
		super.onDestroy();
		System.gc();
	}

	@Override
	public void onDisplayMessagePushReceived() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				updateFragment(0);
			}
		});
	}

	public void updateFragment(int pos) {
		Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + viewPager.getId() + ":" + pos);
		if (page != null) {
			if(pos == 2){
//				((NotificationMessagesFragment) page).update();
			}
		}
	}

	public void showDialog(String message){
		if(notVisible){
			notVisible = false;
			DialogPopup.alertPopupWithListener(NewRateCardActivity.this, "", message, new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					performBackPressed();
				}
			});

		}
	}


}
