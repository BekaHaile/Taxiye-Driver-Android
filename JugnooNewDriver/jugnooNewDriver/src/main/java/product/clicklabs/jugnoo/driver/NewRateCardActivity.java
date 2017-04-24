package product.clicklabs.jugnoo.driver;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAgent;

import product.clicklabs.jugnoo.driver.adapters.NotificationFragmentAdapter;
import product.clicklabs.jugnoo.driver.adapters.RateCardFragmentAdapter;
import product.clicklabs.jugnoo.driver.datastructure.DisplayPushHandler;
import product.clicklabs.jugnoo.driver.fragments.NotificationMessagesFragment;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity;
import product.clicklabs.jugnoo.driver.utils.EventsHolder;
import product.clicklabs.jugnoo.driver.utils.FirebaseEvents;
import product.clicklabs.jugnoo.driver.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.driver.utils.FlurryEventNames;
import product.clicklabs.jugnoo.driver.utils.NudgeClient;
import product.clicklabs.jugnoo.driver.widgets.PagerSlidingTabStrip;


/**
 * Created by socomo on 10/15/15.
 */
public class NewRateCardActivity extends BaseFragmentActivity implements DisplayPushHandler {

    private LinearLayout root;
    private TextView title;
    private Button backBtn;

	ViewPager viewPager;
	RateCardFragmentAdapter rateCardFragmentAdapter;
	PagerSlidingTabStrip tabs;
	ImageView imageViewSeprator;

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
		title.setTypeface(Data.latoRegular(getApplicationContext()));
		title.setText(getResources().getString(R.string.rate_card));
		backBtn = (Button)findViewById(R.id.backBtn);
		
		backBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});

		viewPager = (ViewPager) findViewById(R.id.viewPager);
		rateCardFragmentAdapter = new RateCardFragmentAdapter(NewRateCardActivity.this, getSupportFragmentManager());
		viewPager.setAdapter(rateCardFragmentAdapter);
		imageViewSeprator = (ImageView) findViewById(R.id.imageViewSeprator);
		imageViewSeprator.setVisibility(View.GONE);
		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		tabs.setIndicatorColor(getResources().getColor(R.color.red_v2));
		tabs.setTextColorResource(R.color.red_v2, R.color.white);
		tabs.setTypeface(Data.latoRegular(this), Typeface.NORMAL);
		tabs.setViewPager(viewPager);
		tabs.setBackgroundColor(getResources().getColor(R.color.black_top_bar_bg));

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
				((NotificationMessagesFragment) page).update();
			}
		}
	}


}
