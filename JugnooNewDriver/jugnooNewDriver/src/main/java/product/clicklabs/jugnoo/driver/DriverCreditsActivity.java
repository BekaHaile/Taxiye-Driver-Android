package product.clicklabs.jugnoo.driver;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.TextView;

import product.clicklabs.jugnoo.driver.fragments.BaseFragment;
import product.clicklabs.jugnoo.driver.fragments.CreditsHomeFragment;
import product.clicklabs.jugnoo.driver.fragments.EarnCreditsFragment;
import product.clicklabs.jugnoo.driver.fragments.GetCreditsFragment;
import product.clicklabs.jugnoo.driver.fragments.SendCreditsFragment;
import product.clicklabs.jugnoo.driver.fragments.ShareEarnFragment;
import product.clicklabs.jugnoo.driver.listeners.DriverCreditsListener;
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity;
import product.clicklabs.jugnoo.driver.utils.Fonts;


/**
 * Created by socomo on 10/15/15.
 */
public class DriverCreditsActivity extends BaseFragmentActivity implements DriverCreditsListener {

    private TextView tvTitle;


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

		getSupportFragmentManager().beginTransaction()
				.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
				.add(R.id.container, CreditsHomeFragment.newInstance(), CreditsHomeFragment.class.getName())
				.addToBackStack(CreditsHomeFragment.class.getName())
				.commit();



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

		getSupportFragmentManager().beginTransaction()
				.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
				.add(R.id.container, ShareEarnFragment.newInstance(isCustomerSharing), ShareEarnFragment.class.getName())
				.addToBackStack(ShareEarnFragment.class.getName())
				.hide(  getSupportFragmentManager().findFragmentByTag( getSupportFragmentManager()
						.getBackStackEntryAt(  getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
				.commit();
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
		Intent intent = new Intent(this, DriverDocumentActivity.class);
		intent.putExtra("access_token",Data.userData.accessToken);
		intent.putExtra("in_side", true);
		intent.putExtra("doc_required", 0);
		intent.putExtra(Constants.BRANDING_IMAGES_ONLY, 1);
		startActivity(intent);
		overridePendingTransition(R.anim.right_in, R.anim.right_out);
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
