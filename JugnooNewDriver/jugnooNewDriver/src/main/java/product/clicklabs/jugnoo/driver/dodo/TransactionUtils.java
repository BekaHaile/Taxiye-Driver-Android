package product.clicklabs.jugnoo.driver.dodo;

import android.support.v4.app.FragmentActivity;
import android.view.View;

import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.dodo.fragments.DeliveryReturnFragment;
/**
 * Created by shankar on 1/27/16.
 */
public class TransactionUtils {

	public void openDeliveryReturnFragment(FragmentActivity activity, View container) {
		if(!checkIfFragmentAdded(activity, DeliveryReturnFragment.class.getName())) {
			activity.getSupportFragmentManager().beginTransaction()
					.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
					.add(container.getId(), new DeliveryReturnFragment(),
							DeliveryReturnFragment.class.getName())
					.addToBackStack(DeliveryReturnFragment.class.getName())
					.hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
							.getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
					.commitAllowingStateLoss();
		}
	}

	public boolean checkIfFragmentAdded(FragmentActivity activity, String tag){
		return (activity.getSupportFragmentManager().findFragmentByTag(tag) != null);
	}

}
