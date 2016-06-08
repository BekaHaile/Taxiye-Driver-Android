package product.clicklabs.jugnoo.driver.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.fragments.NotificationMessagesFragment;
import product.clicklabs.jugnoo.driver.fragments.NotificationTipsFragment;


/**
 * Created by shankar on 12/29/15.
 */
public class NotificationFragmentAdapter extends FragmentPagerAdapter {

	Context context;

	public NotificationFragmentAdapter(Context context, FragmentManager fm) {
		super(fm);
		this.context = context;
	}

	@Override
	public Fragment getItem(int position) {
		Fragment fragment = null;
		switch(position){
			case 0:
				fragment = new NotificationMessagesFragment();
				break;

			case 1:
				fragment = new NotificationTipsFragment();
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
				return context.getResources().getString(R.string.Messages);
			case 1:
				return context.getResources().getString(R.string.tricks);
		}

		return null;
	}

}
