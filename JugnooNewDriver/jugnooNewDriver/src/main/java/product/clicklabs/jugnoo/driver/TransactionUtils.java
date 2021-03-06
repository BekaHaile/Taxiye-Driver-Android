package product.clicklabs.jugnoo.driver;

import android.Manifest;
import androidx.fragment.app.FragmentActivity;
import android.view.View;

import product.clicklabs.jugnoo.driver.dodo.datastructure.DeliveryInfoInRideDetails;
import product.clicklabs.jugnoo.driver.dodo.datastructure.DeliveryStatus;
import product.clicklabs.jugnoo.driver.dodo.fragments.DeliveryInfosListFragment;
import product.clicklabs.jugnoo.driver.dodo.fragments.DeliveryInfosListInRideFragment;
import product.clicklabs.jugnoo.driver.dodo.fragments.DeliveryReturnFragment;
import product.clicklabs.jugnoo.driver.dodo.fragments.MarkDeliveryFragment;
import product.clicklabs.jugnoo.driver.fragments.AddSignatureFragment;
import product.clicklabs.jugnoo.driver.fragments.SwitchCustomerFragment;
import product.clicklabs.jugnoo.driver.selfAudit.NonJugnooAuditFragment;
import product.clicklabs.jugnoo.driver.selfAudit.SelectAuditFragment;
import product.clicklabs.jugnoo.driver.selfAudit.SelfAuditCameraFragment;
import product.clicklabs.jugnoo.driver.selfAudit.SubmitAuditFragment;
import product.clicklabs.jugnoo.driver.utils.PermissionCommon;

/**
 * Created by shankar on 1/27/16.
 */
public class TransactionUtils {

	public void openDeliveryInfoListFragment(FragmentActivity activity, View container, int engagementId, DeliveryStatus deliveryStatus) {
			activity.getSupportFragmentManager().beginTransaction()
					.replace(container.getId(), new DeliveryInfosListFragment(engagementId, deliveryStatus),
							DeliveryInfosListFragment.class.getName())
					.addToBackStack(DeliveryInfosListFragment.class.getName())
					.commitAllowingStateLoss();
	}

	public void openDeliveryReturnFragment(FragmentActivity activity, View container, int engagementId, int id) {
		if(!checkIfFragmentAdded(activity, DeliveryReturnFragment.class.getName())) {
			activity.getSupportFragmentManager().beginTransaction()
					.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
					.add(container.getId(), new DeliveryReturnFragment(engagementId, id),
							DeliveryReturnFragment.class.getName())
					.addToBackStack(DeliveryReturnFragment.class.getName())
					.hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
							.getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
					.commitAllowingStateLoss();
		}
	}

	public void openMarkDeliveryFragment(FragmentActivity activity, View container, int engagementId, int id) {
		if(!checkIfFragmentAdded(activity, MarkDeliveryFragment.class.getName())) {
			activity.getSupportFragmentManager().beginTransaction()
					.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
					.add(container.getId(), new MarkDeliveryFragment(engagementId, id),
							MarkDeliveryFragment.class.getName())
					.addToBackStack(MarkDeliveryFragment.class.getName())
					.hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
							.getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
					.commitAllowingStateLoss();
		}
	}

	public void openNonJugnooAuditFragment(FragmentActivity activity, View container, int auditType) {
		if(!checkIfFragmentAdded(activity, NonJugnooAuditFragment.class.getName())) {
			activity.getSupportFragmentManager().beginTransaction()
					.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
					.replace(container.getId(), new NonJugnooAuditFragment(auditType),
							NonJugnooAuditFragment.class.getName())
//					.addToBackStack(NonJugnooAuditFragment.class.getName())
//					.hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
//							.getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
					.commitAllowingStateLoss();
		}
	}

	public void openAuditCameraFragment(FragmentActivity activity, View container, int auditState, int auditType, int auditCmeraOption) {
		if(PermissionCommon.isGranted(Manifest.permission.CAMERA, activity)) {
			if (!checkIfFragmentAdded(activity, SelfAuditCameraFragment.class.getName())) {
				activity.getSupportFragmentManager().beginTransaction()
						.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
						.replace(container.getId(), new SelfAuditCameraFragment(auditState, auditType, auditCmeraOption),
								SelfAuditCameraFragment.class.getName())
//					.addToBackStack(SelfAuditCameraFragment.class.getName())
//					.hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
//							.getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
						.commitAllowingStateLoss();
			}
		}
	}

	public void openSubmitAuditFragment(FragmentActivity activity, View container, int auditType) {
		if(!checkIfFragmentAdded(activity, SubmitAuditFragment.class.getName())) {
			activity.getSupportFragmentManager().beginTransaction()
					.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
					.replace(container.getId(), new SubmitAuditFragment(auditType),
							SubmitAuditFragment.class.getName())
//					.addToBackStack(SubmitAuditFragment.class.getName())
//					.hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
//							.getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
					.commitAllowingStateLoss();
		}
	}

	public void openSelectAuditFragment(FragmentActivity activity, View container) {
			activity.getSupportFragmentManager().beginTransaction()
					.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
					.replace(container.getId(), new SelectAuditFragment(),
							SelectAuditFragment.class.getName())
//					.addToBackStack(SubmitAuditFragment.class.getName())
//					.hide(activity.getSupportFragmentManager().findFragmentByTag(activity.getSupportFragmentManager()
//							.getBackStackEntryAt(activity.getSupportFragmentManager().getBackStackEntryCount() - 1).getName()))
					.commitAllowingStateLoss();
	}

	public boolean checkIfFragmentAdded(FragmentActivity activity, String tag){
		return (activity.getSupportFragmentManager().findFragmentByTag(tag) != null);
	}

	public void openSwitchCustomerFragment(FragmentActivity activity, View container) {
		activity.getSupportFragmentManager().beginTransaction()
				.replace(container.getId(), new SwitchCustomerFragment(),
						SwitchCustomerFragment.class.getName())
				.addToBackStack(SwitchCustomerFragment.class.getName())
				.commitAllowingStateLoss();
	}

	public void openDeliveryInfoInRideFragment(FragmentActivity activity, View container, DeliveryInfoInRideDetails deliveryInfoInRideDetails) {
		activity.getSupportFragmentManager().beginTransaction()
				.replace(container.getId(), new DeliveryInfosListInRideFragment(deliveryInfoInRideDetails),
						DeliveryInfosListInRideFragment.class.getName())
				.commitAllowingStateLoss();
	}

	public void openAddSignatureFragment(FragmentActivity activity, View container) {
		activity.getSupportFragmentManager().beginTransaction()
				.replace(container.getId(), new AddSignatureFragment(),
						AddSignatureFragment.class.getName())
				.addToBackStack(AddSignatureFragment.class.getName())
				.commitAllowingStateLoss();
	}

}
