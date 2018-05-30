package product.clicklabs.jugnoo.driver.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.LocalBroadcastManager
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import com.flurry.android.FlurryAgent
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import kotlinx.android.synthetic.main.driver_splash_activity.*
import kotlinx.android.synthetic.main.driver_splash_activity.view.*
import product.clicklabs.jugnoo.driver.*
import product.clicklabs.jugnoo.driver.utils.*

/**
 * Created by Parminder Saini on 16/04/18.
 */

class DriverSplashActivity : BaseFragmentActivity(), LocationUpdate, SplashFragment.InteractionListener, ToolbarChangeListener {

    private val TAG = SplashFragment::class.simpleName
    private val container by bind<FrameLayout>(R.id.container)
    private  var otpDetectedViaSms :String? = null;


    private var otpBroadCastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null && intent.action.equals(Constants.INTENT_ACTION_NEW_MESSAGE, ignoreCase = true)) {
               retrieveOTPFromSMS(intent)
            }
        }
    };



    override fun onLocationChanged(location: Location?, priority: Int) {
        if (location != null) {
            Data.latitude = location.latitude
            Data.longitude = location.longitude
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.driver_splash_activity)
        FlurryAgent.init(this, Data.FLURRY_KEY)

        Data.locationFetcher = LocationFetcher(this, 1000, 1)
        setLoginData()
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_back_arrow)
        }

        setToolbarVisibility(false)

        val resp = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)
        if (resp != ConnectionResult.SUCCESS) {
            Log.e(TAG, "=$resp")
            DialogPopup.showGooglePlayErrorAlert(this@DriverSplashActivity)
            return
        }


        if (savedInstanceState == null) {

            supportFragmentManager.inTransaction {
                add(container.id, SplashFragment(), SplashFragment::class.simpleName)
            }

        }


    }

    private fun setLoginData() {
        try {
            Data.generateKeyHash(this@DriverSplashActivity)
            Data.filldetails(this@DriverSplashActivity)
        } catch (e: Exception) {
            android.util.Log.e(TAG, e.localizedMessage)
        }
    }


    override fun onResume() {
        super.onResume()
        if (Data.locationFetcher != null) Data.locationFetcher.connect()
        LocationInit.showLocationAlertDialog(this)
        registerbackForOTPDetection()


    }

    private fun registerbackForOTPDetection() {
        var otpFragment = supportFragmentManager.findFragmentByTag(OTPConfirmFragment::class.simpleName);
        if (otpFragment != null) {
            otpFragment = otpFragment as OTPConfirmFragment;
            if (otpFragment.isWaitingForOTPDetection()) {
                registerForSmsReceiver()

            }

        }
    }


    override fun onPause() {
        super.onPause()
        try {
            if (Data.locationFetcher != null) Data.locationFetcher.destroy()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Database2.getInstance(this).close()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(otpBroadCastReceiver)

    }

    private fun addPhoneNumberScreen(enableSharedTransition: Boolean, view: View?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && enableSharedTransition && view != null) {
            try {

                supportFragmentManager.inTransactionWithSharedTransition(view, {
                    setAllowOptimization(true)
                            .replace(container.id, LoginFragment.newInstance(true), LoginFragment::class.simpleName)
                })

            } catch (e: Exception) {

                supportFragmentManager.inTransactionWithAnimation {
                    setAllowOptimization(true)
                            .replace(container.id, LoginFragment.newInstance(false), LoginFragment::class.simpleName)
                }
            }
        } else {
            supportFragmentManager.inTransactionWithAnimation {
                setAllowOptimization(true)
                        .replace(container.id, LoginFragment.newInstance(false), LoginFragment::class.simpleName)
            }
        }
    }

    fun addLoginViaOTPScreen(phone: String, countryCode: String, missedCallNumber: String?) {

        supportFragmentManager.inTransactionWithAnimation {
            add(container.id, OTPConfirmFragment.newInstance(phone, countryCode, missedCallNumber), OTPConfirmFragment::class.simpleName)
                    .hide(supportFragmentManager.findFragmentByTag(LoginFragment::class.simpleName))
                    .addToBackStack(OTPConfirmFragment::class.simpleName)
        }
    }

    fun openDriverSetupFragment(accessToken: String) {
        supportFragmentManager.inTransactionWithAnimation {
            add(container.id, DriverSetupFragment.newInstance(accessToken), DriverSetupFragment::class.simpleName)
                    .hide(supportFragmentManager.findFragmentByTag(LoginFragment::class.simpleName))
                    .addToBackStack(DriverSetupFragment::class.simpleName)
        }
    }

    fun addDriverSetupFragment(accessToken: String) {

        supportFragmentManager.inTransaction {
            setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
                    .add(container.id, DriverSetupFragment.newInstance(accessToken), DriverSetupFragment::class.simpleName)
                    .addToBackStack(DriverSetupFragment::class.simpleName)
        }

        supportFragmentManager.beginTransaction()
                .remove(supportFragmentManager.findFragmentByTag(SplashFragment::class.simpleName))
                .commit()
    }

    override fun openPhoneLoginScreen(enableSharedTransition: Boolean, sharedView: View?) {
        addPhoneNumberScreen(enableSharedTransition, sharedView)
    }

    override fun registerForSmsReceiver() {
        otpDetectedViaSms=null;
        LocalBroadcastManager.getInstance(this).registerReceiver(otpBroadCastReceiver, IntentFilter(Constants.INTENT_ACTION_NEW_MESSAGE))

    }

    override fun getPrefillOtpIfany():String? {
       return otpDetectedViaSms;
    }


    override fun goToHomeScreen() {
        val intent = Intent(this, HomeActivity::class.java)
        if (getIntent().extras != null) {
            intent.putExtras(getIntent().extras)

        }
        if (HomeActivity.activity != null) {
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
        } else {
            startActivity(intent)
            ActivityCompat.finishAffinity(this)
        }

        overridePendingTransition(R.anim.right_in, R.anim.right_out)
    }

    override fun setToolbarText(title: String) {
        toolbar.tvToolbar.text = title
    }

    override fun setToolbarVisibility(isVisible: Boolean) {
        if (isVisible) toolbar.visible() else toolbar.gone()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        if (item?.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {

        // if back pressed from driver setup fragment from the normal flow
        if (supportFragmentManager.backStackEntryCount == 2
                && supportFragmentManager.findFragmentByTag(DriverSetupFragment::class.simpleName) != null
                && supportFragmentManager.findFragmentByTag(OTPConfirmFragment::class.simpleName) != null
                && supportFragmentManager.findFragmentByTag(DriverSetupFragment::class.simpleName) is DriverSetupFragment) {

            supportFragmentManager.popBackStackImmediate()
            super.onBackPressed()

        } else if (supportFragmentManager.backStackEntryCount == 1
                && supportFragmentManager.findFragmentByTag(LoginFragment::class.simpleName) == null) {

            // when back press on any fragment where there is no login fragment added before in the flow, add login fragment
            // after transaction pop and clear the saved access token
            super.onBackPressed()

            supportFragmentManager.inTransaction {
                replace(R.id.container, LoginFragment.newInstance(false), LoginFragment::class.simpleName)
            }

            JSONParser.saveAccessToken(this, "")
        } else {
            super.onBackPressed()
        }
    }



    //Using Rx
    public fun retrieveOTPFromSMS(intent: Intent) {
        try {
            var otp = ""
            if (intent.hasExtra("message")) {
                val message = intent.getStringExtra("message")

                if (message.toLowerCase().contains("paytm")) {
                    otp = message.split("\\ ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
                } else {
                    val arr = message.split("and\\ it\\ is\\ valid\\ till\\ ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val arr2 = arr[0].split("Dear\\ Driver\\,\\ Your\\ Jugnoo\\ One\\ Time\\ Password\\ is\\ ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    otp = arr2[1]
                    otp = otp.replace("\\ ".toRegex(), "")
                }
            }
            if (Utils.checkIfOnlyDigits(otp)) {
                if (!"".equals(otp, ignoreCase = true)) {

                    otpDetectedViaSms = otp;

                    var otpFragment = supportFragmentManager.findFragmentByTag(OTPConfirmFragment::class.simpleName);
                    if (otpFragment != null) {
                        otpFragment = otpFragment as OTPConfirmFragment;
                        otpFragment.onOtpReceived(otp)

                    }



                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


}

interface ToolbarChangeListener {

    fun setToolbarText(title: String)

    fun setToolbarVisibility(isVisible: Boolean)
}

