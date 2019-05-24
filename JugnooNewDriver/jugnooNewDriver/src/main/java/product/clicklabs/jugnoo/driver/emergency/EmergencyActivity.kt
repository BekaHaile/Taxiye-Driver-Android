package product.clicklabs.jugnoo.driver.emergency

import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout

import product.clicklabs.jugnoo.driver.Constants
import product.clicklabs.jugnoo.driver.Data
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.apis.ApiEmergencyDisable
import product.clicklabs.jugnoo.driver.emergency.adapters.ContactsListAdapter
import product.clicklabs.jugnoo.driver.emergency.fragments.EmergencyContactsFragment
import product.clicklabs.jugnoo.driver.emergency.fragments.EmergencyModeEnabledFragment
import product.clicklabs.jugnoo.driver.utils.ASSL
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity
import product.clicklabs.jugnoo.driver.utils.DialogPopup
import product.clicklabs.jugnoo.driver.utils.Prefs


class EmergencyActivity : BaseFragmentActivity() {

    private val TAG = EmergencyActivity::class.java.simpleName

    lateinit var container: RelativeLayout
        internal set
    internal var mode: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_emergency)

        container = findViewById<View>(R.id.relativeLayoutContainer) as RelativeLayout
        ASSL(this, container, 1134, 720, false)

        mode = intent.getIntExtra(Constants.KEY_EMERGENCY_ACTIVITY_MODE,
                EmergencyActivityMode.EMERGENCY_ACTIVATE.ordinal1)

        if (mode == EmergencyActivityMode.EMERGENCY_ACTIVATE.ordinal1) {
            val engagementId = intent.getStringExtra(Constants.KEY_ENGAGEMENT_ID)
            val customerId = intent.getStringExtra(Constants.KEY_CUSTOMER_ID)
            supportFragmentManager.beginTransaction()
                    .add(container.id, EmergencyModeEnabledFragment.newInstance(customerId, engagementId),
                            EmergencyModeEnabledFragment::class.java.name)
                    .addToBackStack(EmergencyModeEnabledFragment::class.java.name)
                    .commitAllowingStateLoss()
        } else if (mode == EmergencyActivityMode.EMERGENCY_CONTACTS.ordinal1) {
            supportFragmentManager.beginTransaction()
                    .add(container.id, EmergencyContactsFragment(),
                            EmergencyContactsFragment::class.java.name)
                    .addToBackStack(EmergencyContactsFragment::class.java.name)
                    .commitAllowingStateLoss()
        } else if (mode == EmergencyActivityMode.SEND_RIDE_STATUS.ordinal1) {
            val engagementId = intent.getStringExtra(Constants.KEY_ENGAGEMENT_ID)
            FragTransUtils().openEmergencyContactsOperationsFragment(this, container, engagementId,
                    ContactsListAdapter.ListMode.SEND_RIDE_STATUS)
        }

        setEmergencyContactsAllowedToAdd()


    }

    fun performBackPressed() {
        if (supportFragmentManager.backStackEntryCount == 1) {
            if (mode == EmergencyActivityMode.EMERGENCY_ACTIVATE.ordinal1 && Prefs.with(this).getInt(Constants.SP_EMERGENCY_MODE_ENABLED, 0) == 1) {
                DialogPopup.alertPopupTwoButtonsWithListeners(this, "",
                        getString(R.string.are_you_sure_want_to_disable_emergency),
                        getString(R.string.yes), getString(R.string.back), {
                    val engagementId = intent.getStringExtra(Constants.KEY_ENGAGEMENT_ID)
                    disableEmergencyMode(engagementId)
                }, {
                    finish()
                    overridePendingTransition(R.anim.left_in, R.anim.left_out)
                }, false, false)
            } else {
                finish()
                overridePendingTransition(R.anim.left_in, R.anim.left_out)
            }
        } else {
            super.onBackPressed()
        }
    }

    override fun onBackPressed() {
        performBackPressed()
    }


    override fun onDestroy() {
        ASSL.closeActivity(container)
        System.gc()
        super.onDestroy()
    }


    enum class EmergencyActivityMode constructor(val ordinal1: Int) {
        EMERGENCY_ACTIVATE(0),
        EMERGENCY_CONTACTS(1),
        SEND_RIDE_STATUS(2),
        CALL_CONTACTS(3)
    }

    fun disableEmergencyMode(engagementId: String) {
        ApiEmergencyDisable(this, object : ApiEmergencyDisable.Callback {
            override fun onSuccess() {
                finish()
                overridePendingTransition(R.anim.left_in, R.anim.left_out)
            }

            override fun onFailure() {}

            override fun onRetry(view: View) {
                disableEmergencyMode(engagementId)
            }

            override fun onNoRetry(view: View) {

            }
        }).emergencyDisable(engagementId)
    }

    companion object {

        val MAX_EMERGENCY_CONTACTS_TO_SEND_RIDE_STATUS = 5
        val MAX_EMERGENCY_CONTACTS_ALLOWED_TO_ADD = 3
        var EMERGENCY_CONTACTS_ALLOWED_TO_ADD = 3

        fun setEmergencyContactsAllowedToAdd() {
            if (Data.userData != null && Data.userData.emergencyContactsList != null) {
                EMERGENCY_CONTACTS_ALLOWED_TO_ADD = MAX_EMERGENCY_CONTACTS_ALLOWED_TO_ADD - Data.userData.emergencyContactsList.size
                if (EMERGENCY_CONTACTS_ALLOWED_TO_ADD < 0) {
                    EMERGENCY_CONTACTS_ALLOWED_TO_ADD = 0
                }
            }
        }
    }

}
