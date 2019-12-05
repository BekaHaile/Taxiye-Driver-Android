package product.clicklabs.jugnoo.driver.adapters;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.DriverRateCard;
import product.clicklabs.jugnoo.driver.FragmentDeliveryRateCard;
import product.clicklabs.jugnoo.driver.R;


/**
 * Created by shankar on 12/29/15.
 */
public class RateCardFragmentAdapter extends FragmentPagerAdapter {

	Context context;
	Fragment myFragment = null;
	Boolean count = false;
	String displayString;
	private boolean isHTMLRateCard;
	public RateCardFragmentAdapter(Context context, FragmentManager fm, boolean isHTMLRateCard) {
		super(fm);
		this.context = context;
		this.isHTMLRateCard = isHTMLRateCard;
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
		if(Data.userData != null) {
			setFragment();
			if (count) {
				return 2;
			}
			return 1;
		} else {
			return 0;
		}
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
		if (Data.userData != null && 1 == Data.userData.autosEnabled && 1 == Data.userData.getDeliveryEnabled()) {
			myFragment = DriverRateCard.newInstance(isHTMLRateCard);
			displayString = context.getResources().getString(R.string.Ride);
			count = true;
		} else if(Data.userData != null && 1 == Data.userData.getDeliveryEnabled()){
			myFragment = new FragmentDeliveryRateCard();
			displayString = context.getResources().getString(R.string.delivery);
			count = false;
		} else {
			myFragment = DriverRateCard.newInstance(isHTMLRateCard);
			displayString = context.getResources().getString(R.string.Ride);
			count = false;
		}
	}

}
