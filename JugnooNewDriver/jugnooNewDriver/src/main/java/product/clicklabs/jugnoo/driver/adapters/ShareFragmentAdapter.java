package product.clicklabs.jugnoo.driver.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.fragments.ShareActivityFragment;
import product.clicklabs.jugnoo.driver.fragments.ShareEarnFragment;
import product.clicklabs.jugnoo.driver.fragments.ShareLeaderboardFragment;


/**
 * Created by shankar on 12/29/15.
 */
public class ShareFragmentAdapter extends FragmentPagerAdapter {

	Context context;
	public ShareFragmentAdapter(Context context, FragmentManager fm) {
		super(fm);
		this.context = context;

	}

	@Override
	public Fragment getItem(int position) {
		Fragment fragment = null;
		switch(position){
			case 0:
				fragment = new ShareEarnFragment();
				break;

			case 1:
				fragment = new ShareActivityFragment();
				break;
		}

		return fragment;
	}

	@Override
	public int getCount() {
		return 2;
	}

	@Override
	public CharSequence getPageTitle(int position) {
		switch (position) {
			case 0:
				return context.getResources().getString(R.string.Earn);
			case 1:
				return context.getResources().getString(R.string.Activity);
		}

		return null;
	}

}
