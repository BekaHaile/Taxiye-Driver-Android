package product.clicklabs.jugnoo.driver.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;

import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.DriverRateCard;
import product.clicklabs.jugnoo.driver.FragmentDeliveryRateCard;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.fragments.NotificationMessagesFragment;
import product.clicklabs.jugnoo.driver.fragments.NotificationTipsFragment;
import product.clicklabs.jugnoo.driver.utils.Prefs;

import static product.clicklabs.jugnoo.driver.R.id.relativeLayoutDeliveryOn;


/**
 * Created by shankar on 12/29/15.
 */
public class RateCardFragmentAdapter extends FragmentPagerAdapter {

	Context context;
	Fragment myFragment = null;
	Boolean count = false;
	String displayString;
	public RateCardFragmentAdapter(Context context, FragmentManager fm) {
		super(fm);
		this.context = context;
	}

	@Override
	public Fragment getItem(int position) {
		switch(position){
			case 0:
				setFragment();
				break;

			case 1:
				myFragment = new FragmentDeliveryRateCard();
				break;
		}
		return myFragment;
	}

	@Override
	public int getCount() {
		setFragment();
		if(count){
			return 2;
		}
		return 1;
	}

	@Override
	public CharSequence getPageTitle(int position) {

		String delivery = context.getResources().getString(R.string.delivery);

		switch (position) {
			case 0:
				setFragment();
				return displayString;
			case 1:
				return delivery;
		}
		return null;
	}

	public void setFragment(){
		if (1 == Data.userData.autosEnabled && 1 == Data.userData.getDeliveryEnabled()) {
			myFragment = new DriverRateCard();
			displayString = context.getResources().getString(R.string.Ride);
			count = true;
		} else if(1 == Data.userData.getDeliveryEnabled()){
			myFragment = new FragmentDeliveryRateCard();
			displayString = context.getResources().getString(R.string.delivery);
			count = false;
		} else {
			myFragment = new DriverRateCard();
			displayString = context.getResources().getString(R.string.Ride);
			count = false;
		}
	}

}
