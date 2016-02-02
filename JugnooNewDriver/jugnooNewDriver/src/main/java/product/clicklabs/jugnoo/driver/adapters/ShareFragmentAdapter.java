package product.clicklabs.jugnoo.driver.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import product.clicklabs.jugnoo.driver.fragments.ShareActivityFragment;
import product.clicklabs.jugnoo.driver.fragments.ShareEarnFragment;
import product.clicklabs.jugnoo.driver.fragments.ShareLeaderboardFragment;


/**
 * Created by shankar on 12/29/15.
 */
public class ShareFragmentAdapter extends FragmentPagerAdapter {

	public ShareFragmentAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		Fragment fragment = null;
		switch(position){
			case 0:
				fragment = new ShareEarnFragment();
				break;

			case 1:
				fragment = new ShareLeaderboardFragment();
				break;

			case 2:
				fragment = new ShareActivityFragment();
				break;
		}

		return fragment;
	}

	@Override
	public int getCount() {
		return 3;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		switch (position) {
			case 0:
				return "Earn";
			case 1:
				return "Leaderboard";
			case 2:
				return "Activity";
		}

		return null;
	}

}
