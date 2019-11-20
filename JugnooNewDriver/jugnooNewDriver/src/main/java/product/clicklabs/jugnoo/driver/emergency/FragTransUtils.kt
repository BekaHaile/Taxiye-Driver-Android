package product.clicklabs.jugnoo.driver.emergency

import android.support.v4.app.FragmentActivity
import android.view.View

import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.emergency.adapters.ContactsListAdapter
import product.clicklabs.jugnoo.driver.emergency.fragments.AddEmergencyContactsFragment
import product.clicklabs.jugnoo.driver.emergency.fragments.EmergencyContactOperationsFragment

/**
 * For transacting fragments in containers
 *
 * Created by shankar on 2/23/16.
 */
class FragTransUtils {

    fun openAddEmergencyContactsFragment(activity: FragmentActivity, container: View) {
        activity.supportFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .add(container.id, AddEmergencyContactsFragment.newInstance(), AddEmergencyContactsFragment::class.java.name)
                .addToBackStack(AddEmergencyContactsFragment::class.java.name)
                .hide(activity.supportFragmentManager.findFragmentByTag(activity.supportFragmentManager
                        .getBackStackEntryAt(activity.supportFragmentManager.backStackEntryCount - 1).name)!!)
                .commit()
    }

    fun openEmergencyContactsOperationsFragment(activity: FragmentActivity, container: View, engagementId: String,
                                                listMode: ContactsListAdapter.ListMode) {
        if (ContactsListAdapter.ListMode.SEND_RIDE_STATUS === listMode) {
            activity.supportFragmentManager.beginTransaction()
                    .add(container.id,
                            EmergencyContactOperationsFragment.newInstance(engagementId, listMode),
                            EmergencyContactOperationsFragment::class.java.name)
                    .addToBackStack(EmergencyContactOperationsFragment::class.java.name)
                    .commitAllowingStateLoss()
        } else if (ContactsListAdapter.ListMode.CALL_CONTACTS === listMode) {
            activity.supportFragmentManager.beginTransaction()
                    .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                    .add(container.id,
                            EmergencyContactOperationsFragment.newInstance(engagementId, listMode),
                            EmergencyContactOperationsFragment::class.java.name)
                    .addToBackStack(EmergencyContactOperationsFragment::class.java.name)
                    .hide(activity.supportFragmentManager.findFragmentByTag(activity.supportFragmentManager
                            .getBackStackEntryAt(activity.supportFragmentManager.backStackEntryCount - 1).name)!!)
                    .commitAllowingStateLoss()
        }

    }


}
