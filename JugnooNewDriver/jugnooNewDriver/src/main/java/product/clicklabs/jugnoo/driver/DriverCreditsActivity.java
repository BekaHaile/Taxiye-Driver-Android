package product.clicklabs.jugnoo.driver;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import product.clicklabs.jugnoo.driver.fragments.BaseFragment;
import product.clicklabs.jugnoo.driver.fragments.CreditsHomeFragment;
import product.clicklabs.jugnoo.driver.fragments.EarnCreditsFragment;
import product.clicklabs.jugnoo.driver.fragments.GetCreditsFragment;
import product.clicklabs.jugnoo.driver.fragments.SendCreditsFragment;
import product.clicklabs.jugnoo.driver.fragments.ShareEarnFragment;
import product.clicklabs.jugnoo.driver.home.HomeBannerAction;
import product.clicklabs.jugnoo.driver.listeners.DriverCreditsListener;
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity;
import product.clicklabs.jugnoo.driver.utils.Fonts;


/**
 * Created by socomo on 10/15/15.
 */
public class DriverCreditsActivity extends BaseFragmentActivity implements DriverCreditsListener {

    private TextView tvTitle;
    private static final String BANNER_INDEX = "banner_index";

    public static Intent createIntent(Context context, int bannerIndex){
    	Intent intent = new Intent(context, DriverCreditsActivity.class);
    	intent.putExtra(BANNER_INDEX, bannerIndex);
    	return intent;
	}

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_credits);



		tvTitle = (TextView) findViewById(R.id.title);
		tvTitle.setText(getString(R.string.title_credits_activity));
		tvTitle.setTypeface(Fonts.mavenRegular(getApplicationContext()));
		View backBtn = findViewById(R.id.backBtn);
		
		backBtn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
			@Override
			public void onBackStackChanged() {
				FragmentManager fragmentManager = getSupportFragmentManager();
				if(fragmentManager.getBackStackEntryCount() <= 0){
					return;
				}
				Fragment fragment = fragmentManager.findFragmentByTag(fragmentManager
						.getBackStackEntryAt(fragmentManager.getBackStackEntryCount()-1).getName());
				if(fragment instanceof BaseFragment) {
					setTitle(((BaseFragment) fragment).getTitle());
				}
			}
		});

		int bannerIndex = getIntent().getIntExtra(BANNER_INDEX, 0);
		if(bannerIndex > 0) {
			openEarnScreen(bannerIndex == HomeBannerAction.ACTION_D2C);
		} else {
			getSupportFragmentManager().beginTransaction()
					.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
					.add(R.id.container, CreditsHomeFragment.newInstance(), CreditsHomeFragment.class.getName())
					.addToBackStack(CreditsHomeFragment.class.getName())
					.commit();
		}



    }





	@Override
	public void onBackPressed() {
		if(getSupportFragmentManager().getBackStackEntryCount() == 1){
			finish();
			return;
		}
		super.onBackPressed();
	}



	public void openEarnScreen(boolean isCustomerSharing){

		FragmentTransaction transaction = getSupportFragmentManager().beginTransaction()
				.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
				.add(R.id.container, ShareEarnFragment.newInstance(isCustomerSharing), ShareEarnFragment.class.getName())
				.addToBackStack(ShareEarnFragment.class.getName());
		if(getSupportFragmentManager().getBackStackEntryCount() > 0) {
			Fragment topFragment = getSupportFragmentManager().findFragmentByTag(getSupportFragmentManager()
					.getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName());
			if(topFragment != null) {
				transaction.hide(topFragment);
			}
		}

		transaction.commit();
	}

	@Override
	public void openCustomerEarnScreen() {

		openEarnScreen(true);
	}

	@Override
	public void openDriverEarnScreen() {
		openEarnScreen(false);
	}

	@Override
	public void openAdvertiseScreen() {
		HomeActivity.openTasksActivity(this);
	}

	@Override
	public void openEarnCreditsScreen() {
		getSupportFragmentManager().beginTransaction()
				.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
				.add(R.id.container, EarnCreditsFragment.newInstance(), EarnCreditsFragment.class.getName())
				.addToBackStack(EarnCreditsFragment.class.getName())
				.hide(  getSupportFragmentManager().findFragmentByTag( getSupportFragmentManager()
						.getBackStackEntryAt(  getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
				.commit();
	}

	@Override
	public void openShareCreditsScreen() {
		getSupportFragmentManager().beginTransaction()
				.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
				.add(R.id.container, SendCreditsFragment.newInstance(), SendCreditsFragment.class.getName())
				.addToBackStack(SendCreditsFragment.class.getName())
				.hide(  getSupportFragmentManager().findFragmentByTag( getSupportFragmentManager()
						.getBackStackEntryAt(  getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
				.commit();
	}

	private void setTitle(String title){
		tvTitle.setText(title);
	}

	@Override
	public void openGetCreditsInfoScreen() {
		getSupportFragmentManager().beginTransaction()
				.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
				.add(R.id.container, GetCreditsFragment.newInstance(), GetCreditsFragment.class.getName())
				.addToBackStack(GetCreditsFragment.class.getName())
				.hide(  getSupportFragmentManager().findFragmentByTag( getSupportFragmentManager()
						.getBackStackEntryAt(  getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
				.commit();
	}



}
