package product.clicklabs.jugnoo.driver.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import product.clicklabs.jugnoo.driver.DriverRidesFragment;
import product.clicklabs.jugnoo.driver.fragments.EarningsFragment;
import product.clicklabs.jugnoo.driver.fragments.InvoiceFragment;
import product.clicklabs.jugnoo.driver.fragments.ShareActivityFragment;
import product.clicklabs.jugnoo.driver.fragments.ShareEarnFragment;


/**
 * Created by shankar on 12/29/15.
 */
public class PaymentFragmentAdapter extends FragmentPagerAdapter {

	public PaymentFragmentAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		Fragment fragment = null;
		switch(position){
			case 0:
				fragment = new DriverRidesFragment();
				break;

			case 1:
				fragment = new InvoiceFragment();
				break;

			case 2:
				fragment = new EarningsFragment();
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
				return "Payments";
			case 1:
				return "Invoices";
			case 2:
				return "Earnings";
		}

		return null;
	}

}
