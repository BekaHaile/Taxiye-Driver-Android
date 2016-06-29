package product.clicklabs.jugnoo.driver.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import product.clicklabs.jugnoo.driver.DriverRidesFragment;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.fragments.EarningsFragment;
import product.clicklabs.jugnoo.driver.fragments.InvoiceHistoryFragment;


/**
 * Created by shankar on 12/29/15.
 */
public class PaymentFragmentAdapter extends FragmentPagerAdapter {

	Context context;

	public PaymentFragmentAdapter(Context context, FragmentManager fm) {
		super(fm);
		this.context = context;
	}

	@Override
	public Fragment getItem(int position) {
		Fragment fragment = null;
		switch(position){
			case 0:
				fragment = new EarningsFragment();
				break;

			case 1:
				fragment = new DriverRidesFragment();
				break;

			case 2:
				fragment = new InvoiceHistoryFragment();
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
				return context.getResources().getString(R.string.Earnings);
			case 1:
				return context.getResources().getString(R.string.Transactions);
			case 2:
				return context.getResources().getString(R.string.Invoices);
		}

		return null;
	}

}
