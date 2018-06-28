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
import android.transition.TransitionInflater
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import com.flurry.android.FlurryAgent
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import kotlinx.android.synthetic.main.activity_toolbar.*
import kotlinx.android.synthetic.main.activity_toolbar.view.*
import kotlinx.android.synthetic.main.driver_splash_activity.*
import kotlinx.android.synthetic.main.driver_splash_activity.view.*
import product.clicklabs.jugnoo.driver.*
import product.clicklabs.jugnoo.driver.utils.*
import java.util.regex.Pattern


/**
 * Created by Parminder Saini on 16/04/18.
 */

class DriverSplashActivity : BaseFragmentActivity(), LocationUpdate, SplashFragment.InteractionListener, ToolbarChangeListener {

    private val TAG = SplashFragment::class.simpleName
    private val container by bind<FrameLayout>(R.id.container)
    private var otpDetectedViaSms :String? = null;
    private var otpLength:Int = 4;


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
        if(savedInstanceState==null){
            setTheme(R.style.SplashThemeNormal)
        }else{
            setTheme(R.style.SplashThemeWithoutFlags)
        }
        super.onCreate(savedInstanceState)
        setContentView(R.layout.driver_splash_activity)
        AndroidBug5497Workaround.assistActivity(this)
        FlurryAgent.init(this, Data.FLURRY_KEY)

        Data.locationFetcher = LocationFetcher(this, 1000, 1)
        setLoginData()
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_back_selector)
        }

        tvToolbar.typeface = Fonts.mavenRegular(this)

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
                registerForSmsReceiver(true)

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
        registerForSmsReceiver(false);

    }

    private fun addPhoneNumberScreen(enableSharedTransition: Boolean, view: View?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && enableSharedTransition
                && view != null && resources.getBoolean(R.bool.animate_splash_logo)) {
            try {

                supportFragmentManager.inTransactionWithSharedTransition(view, {
                    val fragment = LoginFragment()
                    fragment.sharedElementEnterTransition = TransitionInflater.from(this@DriverSplashActivity).inflateTransition(android.R.transition.move)
                    setReorderingAllowed(true).replace(container.id, fragment, LoginFragment::class.simpleName)
                })

            } catch (e: Exception) {

                supportFragmentManager.inTransactionWithAnimation {
                    setReorderingAllowed(true)
                            .replace(container.id, LoginFragment(), LoginFragment::class.simpleName)
                }
            }
        } else {
            supportFragmentManager.inTransactionWithAnimation {
                setReorderingAllowed(true)
                        .replace(container.id, LoginFragment(), LoginFragment::class.simpleName)
            }
        }
    }

    fun addLoginViaOTPScreen(phone: String, countryCode: String, missedCallNumber: String?, otpLength:Int?) {


        if(otpLength!=null){
            this.otpLength = otpLength;
        }

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
//            setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left).
                    add(container.id, DriverSetupFragment.newInstance(accessToken), DriverSetupFragment::class.simpleName)
                    .addToBackStack(DriverSetupFragment::class.simpleName)
        }

        supportFragmentManager.beginTransaction()
                .remove(supportFragmentManager.findFragmentByTag(SplashFragment::class.simpleName))
                .commit()
    }

    override fun openPhoneLoginScreen(enableSharedTransition: Boolean, sharedView: View?) {
        addPhoneNumberScreen(enableSharedTransition, sharedView)
    }

    override fun registerForSmsReceiver(register: Boolean) {
        if(register){
            otpDetectedViaSms=null;
            LocalBroadcastManager.getInstance(this).registerReceiver(otpBroadCastReceiver, IntentFilter(Constants.INTENT_ACTION_NEW_MESSAGE))

        }else{
            LocalBroadcastManager.getInstance(this).unregisterReceiver(otpBroadCastReceiver)
        }

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

    override fun toggleDisplayFlags(remove:Boolean) {


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {


                if(remove){
                    window?.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                    window?.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)

                }else{
                    window?.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                    window?.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
                }


            }


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
                replace(R.id.container, LoginFragment(), LoginFragment::class.simpleName)
            }

            JSONParser.saveAccessToken(this, "")
        } else {
            super.onBackPressed()
        }
    }



    //Using Rx
    public fun retrieveOTPFromSMS(intent: Intent) {
        try {
            if (intent.hasExtra("message")) {
                var otp:String? = null ;

                val message = intent.getStringExtra("message")
                val pattern = Pattern.compile("\\b\\d{$otpLength}\\b")
                val matcher = pattern.matcher(message)
                if (matcher.find()) {
                    otp = matcher.group(0)
                }


                if(otp!=null){

                    otpDetectedViaSms = otp;

                    var otpFragment = supportFragmentManager.findFragmentByTag(OTPConfirmFragment::class.simpleName);
                    if (otpFragment != null) {
                        otpFragment = otpFragment as OTPConfirmFragment;
                        otpFragment.onOtpReceived(otp)

                    }

                }
                registerForSmsReceiver(false);
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
    public fun isLoginFragmentVisible():Boolean{
        val loginFragment= supportFragmentManager.findFragmentByTag(LoginFragment::class.simpleName)
        return loginFragment!=null && loginFragment.isVisible/* && (loginFragment as LoginFragment).assist*/
    }


}

interface ToolbarChangeListener {

    fun setToolbarText(title: String)

    fun setToolbarVisibility(isVisible: Boolean)
}

