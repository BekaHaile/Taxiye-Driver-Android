package product.clicklabs.jugnoo.driver;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.driver.fragments.DriverEarningsFragment;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity;
import product.clicklabs.jugnoo.driver.utils.FirebaseEvents;
import product.clicklabs.jugnoo.driver.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.driver.utils.FlurryEventNames;

public class EarningsActivity extends BaseFragmentActivity implements FirebaseEvents {

	RelativeLayout relative;
	TextView title;
	View backBtn;

	@Override
	protected void onStart() {
		super.onStart();


	}

	@Override
	protected void onStop() {
		super.onStop();

	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_earnings);
		relative = (RelativeLayout) findViewById(R.id.relative);
		new ASSL(this, relative, 1134, 720, false);

		backBtn = findViewById(R.id.backBtn);
		title = (TextView) findViewById(R.id.title);
		title.setText(R.string.earnings_caps);

		getSupportFragmentManager().beginTransaction()
				.add(R.id.rlContainer, new DriverEarningsFragment(), DriverEarningsFragment.class.getName())
				.addToBackStack(DriverEarningsFragment.class.getName()).commit();


		backBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				FlurryEventLogger.event(FlurryEventNames.EARNINGS_BACK);
				MyApplication.getInstance().logEvent(EARNING+"_"+BACK, null);
				performBackPressed();
			}
		});
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
	public void onDestroy() {
		super.onDestroy();
		ASSL.closeActivity(relative);
		System.gc();
	}



}
