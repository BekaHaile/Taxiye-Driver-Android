package product.clicklabs.jugnoo.driver.adapters;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.fragments.CreditsHistoryFragment;
import product.clicklabs.jugnoo.driver.fragments.CurrentDetailsFragment;


/**
 * Created by shankar on 12/29/15.
 */
public class DriverCreditsAdapter extends FragmentPagerAdapter {

	Context context;
	public DriverCreditsAdapter(Context context, FragmentManager fm) {
		super(fm);
		this.context = context;
	}

	@Override
	public Fragment getItem(int position) {
		Fragment fragment = null;


		switch(position){
			case 0:
				fragment = new CurrentDetailsFragment();
				break;

			case 1:
				fragment = CreditsHistoryFragment.newInstance();
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

		String message = context.getString(R.string.details);
		String tips =context.getString(R.string.history);

		switch (position) {
			case 0:
				return message;
			case 1:
				return tips;
		}

		return null;
	}

}
