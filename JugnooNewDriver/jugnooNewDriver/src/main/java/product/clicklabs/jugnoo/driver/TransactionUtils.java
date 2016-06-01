package product.clicklabs.jugnoo.driver;

import android.support.v4.app.FragmentActivity;
import android.view.View;

import product.clicklabs.jugnoo.driver.dodo.fragments.DeliveryInfosListFragment;
import product.clicklabs.jugnoo.driver.dodo.fragments.DeliveryReturnFragment;
import product.clicklabs.jugnoo.driver.dodo.fragments.MarkDeliveryFragment;

/**
 * Created by shankar on 1/27/16.
 */
public class TransactionUtils {

	public void openDeliveryInfoListFragment(FragmentActivity activity, View container) {
			activity.getSupportFragmentManager().beginTransaction()
					.replace(container.getId(), new DeliveryInfosListFragment(),
							DeliveryInfosListFragment.class.getName())
					.addToBackStack(DeliveryInfosListFragment.class.getName())
					.commitAllowingStateLoss();
	}

	public void openDeliveryReturnFragment(FragmentActivity activity, View container, int id) {
		if(!checkIfFragmentAdded(activity, DeliveryReturnFragment.class.getName())) {
			activity.getSupportFragmentManager().beginTransaction()
					.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
					.add(container.getId(), new DeliveryReturnFragment(id),
							DeliveryReturnFragment.class.getName())
					.addToBackStack(DeliveryReturnFragment.class.getName())
					.hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
							.getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
					.commitAllowingStateLoss();
		}
	}

	public void openMarkDeliveryFragment(FragmentActivity activity, View container, int id) {
		if(!checkIfFragmentAdded(activity, MarkDeliveryFragment.class.getName())) {
			activity.getSupportFragmentManager().beginTransaction()
					.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
					.add(container.getId(), new MarkDeliveryFragment(id),
							MarkDeliveryFragment.class.getName())
					.addToBackStack(MarkDeliveryFragment.class.getName())
					.hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
							.getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
					.commitAllowingStateLoss();
		}
	}

	public boolean checkIfFragmentAdded(FragmentActivity activity, String tag){
		return (activity.getSupportFragmentManager().findFragmentByTag(tag) != null);
	}

}
