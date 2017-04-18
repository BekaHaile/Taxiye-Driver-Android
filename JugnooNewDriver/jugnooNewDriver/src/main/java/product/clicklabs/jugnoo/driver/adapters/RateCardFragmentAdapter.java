package product.clicklabs.jugnoo.driver.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.DriverRateCard;
import product.clicklabs.jugnoo.driver.FragmentDeliveryRateCard;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.fragments.NotificationMessagesFragment;
import product.clicklabs.jugnoo.driver.fragments.NotificationTipsFragment;
import product.clicklabs.jugnoo.driver.utils.Prefs;


/**
 * Created by shankar on 12/29/15.
 */
public class RateCardFragmentAdapter extends FragmentPagerAdapter {

	Context context;

	public RateCardFragmentAdapter(Context context, FragmentManager fm) {
		super(fm);
		this.context = context;
	}

	@Override
	public Fragment getItem(int position) {
		Fragment fragment = null;


		switch(position){
			case 0:
				fragment = new DriverRateCard();
				break;

			case 1:
				fragment = new FragmentDeliveryRateCard();
				break;
		}

		return fragment;
	}

	@Override
	public int getCount() {
		if(Prefs.with(context).getInt(Constants.SHOW_NOTIFICATION_TIPS, 0)==1){
			return 2;
		}
		return 1;
	}

	@Override
	public CharSequence getPageTitle(int position) {

		String rides = context.getResources().getString(R.string.Ride);
		String delivery = context.getResources().getString(R.string.delivery);

		switch (position) {
			case 0:
				return rides;
			case 1:
				return delivery;
		}

		return null;
	}

}
