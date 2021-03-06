package product.clicklabs.jugnoo.driver;

import android.graphics.Typeface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.driver.adapters.NotificationFragmentAdapter;
import product.clicklabs.jugnoo.driver.datastructure.DisplayPushHandler;
import product.clicklabs.jugnoo.driver.fragments.NotificationMessagesFragment;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity;
import product.clicklabs.jugnoo.driver.utils.EventsHolder;
import product.clicklabs.jugnoo.driver.utils.FirebaseEvents;
import product.clicklabs.jugnoo.driver.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.driver.utils.FlurryEventNames;
import product.clicklabs.jugnoo.driver.utils.Fonts;
import product.clicklabs.jugnoo.driver.widgets.PagerSlidingTabStrip;


/**
 * Created by socomo on 10/15/15.
 */
public class NotificationCenterActivity extends BaseFragmentActivity implements DisplayPushHandler {

    private LinearLayout root;
    private TextView title;
    private View backBtn;

	ViewPager viewPager;
	NotificationFragmentAdapter notificationFragmentAdapter;
	PagerSlidingTabStrip tabs;

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
		title.setText(R.string.Notifications);
		backBtn = findViewById(R.id.backBtn);
		
		backBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				performBackPressed();
			}
		});

		viewPager = (ViewPager) findViewById(R.id.viewPager);
		notificationFragmentAdapter = new NotificationFragmentAdapter(NotificationCenterActivity.this, getSupportFragmentManager());
		viewPager.setAdapter(notificationFragmentAdapter);

		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		tabs.setIndicatorColor(getResources().getColor(R.color.themeColor));
		tabs.setTextColorResource(R.color.themeColor, R.color.textColor);
		tabs.setTypeface(Fonts.mavenRegular(this), Typeface.BOLD);
		tabs.setViewPager(viewPager);
		tabs.setVisibility(notificationFragmentAdapter.getCount() == 1 ? View.GONE : View.VISIBLE);

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
					FlurryEventLogger.event(FlurryEventNames.NOTIFICATION_MESSAGE);
					MyApplication.getInstance().logEvent(FirebaseEvents.NOTIFICATION + "_" + FirebaseEvents.MESSAGE, null);
				} else if (position == 1) {
					FlurryEventLogger.event(FlurryEventNames.NOTIFICATION_TIP_TO_EARN);
					MyApplication.getInstance().logEvent(FirebaseEvents.NOTIFICATION + "_" + FirebaseEvents.TIPS_TO_EARN, null);
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

			MyApplication.getInstance().logEvent(FirebaseEvents.NOTIFICATION+"_"+FirebaseEvents.MESSAGE
					+"_"+FirebaseEvents.BACK, null);

		} else if(viewPager.getCurrentItem() == 1){

			MyApplication.getInstance().logEvent(FirebaseEvents.NOTIFICATION+"_"+FirebaseEvents.TIPS_TO_EARN
					+"_"+FirebaseEvents.BACK, null);

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
