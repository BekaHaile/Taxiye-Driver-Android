package product.clicklabs.jugnoo.driver.emergency.fragments

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import product.clicklabs.jugnoo.driver.*
import product.clicklabs.jugnoo.driver.apis.ApiEmergencyAlert
import product.clicklabs.jugnoo.driver.apis.ApiEmergencyDisable
import product.clicklabs.jugnoo.driver.emergency.EmergencyActivity
import product.clicklabs.jugnoo.driver.emergency.FragTransUtils
import product.clicklabs.jugnoo.driver.emergency.adapters.ContactsListAdapter
import product.clicklabs.jugnoo.driver.fragments.BaseFragment
import product.clicklabs.jugnoo.driver.utils.*


/**
 * For emergency mode enabled fragment
 * Shows Call Police, Call Emergency Contacts
 * and Disable emergency mode options
 *
 * Created by shankar on 2/22/16.
 */

@SuppressLint("ValidFragment")
class EmergencyModeEnabledFragment : BaseFragment(), LocationUpdate, PermissionCommon.PermissionListener {

    private var relative: RelativeLayout? = null

    private var title: TextView? = null
    private var backBtn: ImageView? = null

    private var textViewEmergencyModeEnabledTitle: TextView? = null
    private var textViewEmergencyModeEnabledMessage: TextView? = null
    private var buttonCallPolice: Button? = null
    private var buttonCallEmergencyContact: Button? = null
    private var buttonDisableEmergencyMode: Button? = null
    private var linearLayoutDisableEmergencyMode: LinearLayout? = null

    private var rootView: View? = null
    private var location: Location? = null

    private var customerId: String? = null
    private lateinit var engagementId: String

    private var locationFetcher: LocationFetcher? = null

    private var permissionCommon: PermissionCommon? = null

    override fun onResume() {
        super.onResume()
        getLocationFetcher().connect()
    }

    fun getLocationFetcher(): LocationFetcher {
        if (locationFetcher == null) {
            locationFetcher = LocationFetcher(getActivity(), this, 10000, 2)
        }
        return locationFetcher as LocationFetcher
    }

    private fun getPermissionCommon(): PermissionCommon? {
        if (permissionCommon == null) {
            permissionCommon = PermissionCommon(this).setCallback(this)
        }
        return permissionCommon
    }


    override fun onLocationChanged(location: Location, priority: Int) {
        this@EmergencyModeEnabledFragment.location = location
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        rootView = inflater.inflate(R.layout.fragment_emergency_mode_enabled, container, false)

        customerId = arguments!!.getString(Constants.KEY_CUSTOMER_ID)
        engagementId = arguments!!.getString(Constants.KEY_ENGAGEMENT_ID)


        relative = rootView!!.findViewById<View>(R.id.relative) as RelativeLayout
        try {
            ASSL(activity, relative, 1134, 720, false)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        title = rootView!!.findViewById<View>(R.id.title) as TextView
        backBtn = rootView!!.findViewById<View>(R.id.backBtn) as ImageView
        title!!.setText(R.string.emergency_mode_enabled)

        textViewEmergencyModeEnabledTitle = rootView!!.findViewById<View>(R.id.textViewEmergencyModeEnabledTitle) as TextView
        textViewEmergencyModeEnabledTitle!!.typeface = Fonts.mavenRegular(activity!!)
        textViewEmergencyModeEnabledMessage = rootView!!.findViewById<View>(R.id.textViewEmergencyModeEnabledMessage) as TextView
        textViewEmergencyModeEnabledMessage!!.typeface = Fonts.mavenRegular(activity!!)
        textViewEmergencyModeEnabledMessage!!.text = getString(R.string.emergency_mode_enabled_message, getString(R.string.appname))
        (rootView!!.findViewById<View>(R.id.textViewOr) as TextView).typeface = Fonts.mavenLight(activity!!)

        buttonCallPolice = rootView!!.findViewById<View>(R.id.buttonCallPolice) as Button
        buttonCallPolice!!.typeface = Fonts.mavenRegular(activity!!)
        buttonCallEmergencyContact = rootView!!.findViewById<View>(R.id.buttonCallEmergencyContact) as Button
        buttonCallEmergencyContact!!.typeface = Fonts.mavenRegular(activity!!)
        buttonDisableEmergencyMode = rootView!!.findViewById<View>(R.id.buttonDisableEmergencyMode) as Button
        buttonDisableEmergencyMode!!.typeface = Fonts.mavenRegular(activity!!)


        linearLayoutDisableEmergencyMode = rootView!!.findViewById<View>(R.id.linearLayoutDisableEmergencyMode) as LinearLayout


        val onClickListener = View.OnClickListener { v ->
            when (v.id) {

                R.id.backBtn -> performBackPressed()

                R.id.buttonCallPolice -> Utils.openCallIntent(activity!!, Prefs.with(activity).getString(Constants.KEY_EMERGENCY_NO, getString(R.string.police_number)))

                R.id.buttonCallEmergencyContact -> if (activity is EmergencyActivity) {
                    FragTransUtils().openEmergencyContactsOperationsFragment(requireActivity(),
                            (activity as EmergencyActivity).container, engagementId,
                            ContactsListAdapter.ListMode.CALL_CONTACTS)
                }

                R.id.buttonDisableEmergencyMode -> disableEmergencyMode()
            }
        }


        backBtn!!.setOnClickListener(onClickListener)
        buttonCallPolice!!.setOnClickListener(onClickListener)
        buttonCallEmergencyContact!!.setOnClickListener(onClickListener)
        buttonDisableEmergencyMode!!.setOnClickListener(onClickListener)



        getPermissionCommon()!!.getPermission(REQUEST_CODE_PERMISSION_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION)





        return rootView
    }

    override fun permissionGranted(requestCode: Int) {
        if (requestCode == REQUEST_CODE_PERMISSION_LOCATION) {
            getLocationFetcher().connect()
            callEmergencyAlert()
        }

    }

    override fun permissionDenied(requestCode: Int, neverAsk: Boolean): Boolean {
        return true
    }

    override fun onRationalRequestIntercepted() {

    }

    fun callEmergencyAlert() {
        val modeEnabled = Prefs.with(activity).getInt(Constants.SP_EMERGENCY_MODE_ENABLED, 0)
        if (modeEnabled == 0) {
            linearLayoutDisableEmergencyMode!!.visibility = View.GONE
            ApiEmergencyAlert(activity!!, object : ApiEmergencyAlert.Callback {

                override val savedLatitude: Double
                    get() = LocationFetcher.getSavedLatFromSP(activity)

                override val savedLongitude: Double
                    get() = LocationFetcher.getSavedLngFromSP(activity)

                override fun onSuccess() {
                    linearLayoutDisableEmergencyMode!!.visibility = View.VISIBLE
                    Prefs.with(activity).save(Constants.SP_EMERGENCY_MODE_ENABLED, 1)
                    HomeActivity.localModeEnabled = 1
                }

                override fun onFailure() {

                }
            }).raiseEmergencyAlertAPI(getLocation(), "", customerId!!, engagementId!!)
        } else {
            linearLayoutDisableEmergencyMode!!.visibility = View.VISIBLE
        }
    }

    fun disableEmergencyMode() {
        ApiEmergencyDisable(activity!!, object : ApiEmergencyDisable.Callback {
            override fun onSuccess() {
                performBackPressed()
            }

            override fun onFailure() {

            }

            override fun onRetry(view: View) {
                disableEmergencyMode()
            }

            override fun onNoRetry(view: View) {

            }
        }).emergencyDisable(engagementId!!)
    }


    private fun performBackPressed() {
        if (activity is EmergencyActivity) {
            (activity as EmergencyActivity).performBackPressed()
        }
    }

    private fun getLocation(): Location {
        return if (location != null) {
            location!!
        } else {
            HomeActivity.myLocation
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        ASSL.closeActivity(rootView)
        System.gc()
    }


    override fun getTitle(): String {
        return getString(R.string.emergency_mode_enabled_title)
    }

    companion object {
        val REQUEST_CODE_PERMISSION_LOCATION = 1011

        fun newInstance(customerId: String, engagementId: String): EmergencyModeEnabledFragment {
            val fragment = EmergencyModeEnabledFragment()
            val bundle = Bundle()
            bundle.putString(Constants.KEY_CUSTOMER_ID, customerId)
            bundle.putString(Constants.KEY_ENGAGEMENT_ID, engagementId)
            fragment.arguments = bundle
            return fragment
        }
    }

}
