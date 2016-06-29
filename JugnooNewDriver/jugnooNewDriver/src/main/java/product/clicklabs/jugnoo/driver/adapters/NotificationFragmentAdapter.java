package product.clicklabs.jugnoo.driver.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.fragments.NotificationMessagesFragment;
import product.clicklabs.jugnoo.driver.fragments.NotificationTipsFragment;
import product.clicklabs.jugnoo.driver.utils.Prefs;


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
		if(Prefs.with(context).getInt(Constants.SHOW_NOTIFICATION_TIPS, 0)==1){
			return 2;
		}
		return 1;
	}

	@Override
	public CharSequence getPageTitle(int position) {

		String message = Prefs.with(context).getString(Constants.NOTIFICATION_MSG_TEXT, "Message");
		String tips = Prefs.with(context).getString(Constants.NOTIFICATION_TIPS_TEXT, "Tips To Earn");

		switch (position) {
			case 0:
				return message;
			case 1:
				return tips;
		}

		return null;
	}

}
