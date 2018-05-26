package product.clicklabs.jugnoo.driver;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import product.clicklabs.jugnoo.driver.fragments.CreditsHomeFragment;
import product.clicklabs.jugnoo.driver.fragments.EarnCreditsFragment;
import product.clicklabs.jugnoo.driver.fragments.SendCreditsFragment;
import product.clicklabs.jugnoo.driver.fragments.ShareEarnFragment;
import product.clicklabs.jugnoo.driver.listeners.DriverCreditsListener;
import product.clicklabs.jugnoo.driver.utils.ASSL;
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity;
import product.clicklabs.jugnoo.driver.utils.Fonts;


/**
 * Created by socomo on 10/15/15.
 */
public class DriverCreditsActivity extends BaseFragmentActivity implements DriverCreditsListener {

    private LinearLayout root;
    private TextView tvTitle;
    private Button backBtn;
	private View containerLayout;




	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_credits);


        root = (LinearLayout)findViewById(R.id.root);
        new ASSL(this, root, 1134, 720, false);

		tvTitle = (TextView) findViewById(R.id.title);
		tvTitle.setText(getString(R.string.title_credits_activity));
		tvTitle.setTypeface(Fonts.mavenRegular(getApplicationContext()));
		backBtn = (Button)findViewById(R.id.backBtn);
		
		backBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});
		containerLayout = root.findViewById(R.id.container);

		getSupportFragmentManager().beginTransaction()
				.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
				.add(R.id.container, new CreditsHomeFragment(), CreditsHomeFragment.class.getName())
				.addToBackStack(CreditsHomeFragment.class.getName())
				.commit();



    }





	@Override
	public void onBackPressed() {

		super.onBackPressed();
	}

	@Override
	protected void onDestroy() {
		ASSL.closeActivity(root);
		super.onDestroy();
		System.gc();
	}



	public void openEarnScreen(String title){

		setTitle(title);
		getSupportFragmentManager().beginTransaction()
				.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
				.add(R.id.container, new ShareEarnFragment(), ShareEarnFragment.class.getName())
				.addToBackStack(ShareEarnFragment.class.getName())
				.hide(  getSupportFragmentManager().findFragmentByTag( getSupportFragmentManager()
						.getBackStackEntryAt(  getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
				.commit();
	}

	@Override
	public void openCustomerEarnScreen() {

		openEarnScreen(getString(R.string.refer_a_customer));
	}

	@Override
	public void openDriverEarnScreen() {
		openEarnScreen(getString(R.string.refer_a_driver));
	}

	@Override
	public void openAdvertiseScreen() {


	}

	@Override
	public void openEarnCreditsScreen() {
		setTitle(getString(R.string.title_earn_more_credits));
		getSupportFragmentManager().beginTransaction()
				.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
				.add(R.id.container, new EarnCreditsFragment(), EarnCreditsFragment.class.getName())
				.addToBackStack(EarnCreditsFragment.class.getName())
				.hide(  getSupportFragmentManager().findFragmentByTag( getSupportFragmentManager()
						.getBackStackEntryAt(  getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
				.commit();
	}

	@Override
	public void openShareCreditsScreen() {
		setTitle(R.string.send_to_a_friend);
		getSupportFragmentManager().beginTransaction()
				.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
				.add(R.id.container, new SendCreditsFragment(), SendCreditsFragment.class.getName())
				.addToBackStack(SendCreditsFragment.class.getName())
				.hide(  getSupportFragmentManager().findFragmentByTag( getSupportFragmentManager()
						.getBackStackEntryAt(  getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
				.commit();
	}

	private void setTitle(String title){
		tvTitle.setText(title);
	}



}
