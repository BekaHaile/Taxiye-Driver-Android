package product.clicklabs.jugnoo.driver.ui

import android.Manifest
import android.annotation.SuppressLint
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
import android.widget.FrameLayout
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import kotlinx.android.synthetic.main.activity_toolbar.*
import kotlinx.android.synthetic.main.activity_toolbar.view.*
import product.clicklabs.jugnoo.driver.*
import product.clicklabs.jugnoo.driver.utils.*
import product.clicklabs.jugnoo.driver.utils.PermissionCommon.REQUEST_CODE_FINE_LOCATION
import product.clicklabs.jugnoo.driver.utils.PermissionCommon.REQUEST_CODE_READ_PHONE_STATE
import java.util.regex.Pattern

/**
 * Created by Parminder Saini on 16/04/18.
 */

class DriverSplashActivity : BaseFragmentActivity(), LocationUpdate, SplashFragment.InteractionListener, ToolbarChangeListener,
        PermissionCommon.PermissionListener {

    private val TAG = DriverSplashActivity::class.simpleName
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
    private var isSavedInstanceStateNull = false;

    private val permissionCommon by lazy { PermissionCommon(this).setCallback(this) }

    @SuppressLint("MissingPermission")
    override fun permissionGranted(requestCode: Int) {
        Log.d(TAG, " permissionGranted : requestCode  $requestCode")

        if (requestCode == REQUEST_CODE_FINE_LOCATION) {
            if(Data.locationFetcher != null) {
                Data.locationFetcher.connect()
            } else {
                Data.locationFetcher = LocationFetcher(this, 1000, 1)
                Data.locationFetcher.connect()
            }

//            val uid = DeviceUniqueID.getCachedUniqueId(this)
//            Log.d(TAG, "UID : $uid")
//
//            if (uid.isBlank()) {
                permissionCommon.getPermission(REQUEST_CODE_READ_PHONE_STATE, Manifest.permission.READ_PHONE_STATE)
//            } else {
//                setupSplashFragment()
//            }
        } else if(requestCode == REQUEST_CODE_READ_PHONE_STATE){
            val uid = DeviceUniqueID.getUniqueId(this)
            Log.d(TAG, "UID : $uid")
            setupSplashFragment()
        }
    }

    private fun setupSplashFragment() {
        setTheme(R.style.AppTheme)
        setContentView(R.layout.driver_splash_activity)

        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_back_arrow)
        }

        tvToolbar.typeface = Fonts.mavenRegular(this)
        setLoginData()

        if (isSavedInstanceStateNull) {
            supportFragmentManager.inTransaction {
                add(container.id, SplashFragment(), SplashFragment::class.simpleName)
            }
            BaseFragmentActivity.checkOverlayPermissionOpenJeanie(this, false, false)
        }
    }

    private var neverAskClicked = false
    override fun permissionDenied(requestCode: Int, neverAsk: Boolean) : Boolean {
        Log.d(TAG, " permissionDenied : requestCode  $requestCode")

        if (requestCode == REQUEST_CODE_FINE_LOCATION && !neverAsk) {
            permissionCommon.getPermission(REQUEST_CODE_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
        } else if (requestCode == REQUEST_CODE_READ_PHONE_STATE && !neverAsk) {
            permissionCommon.getPermission(REQUEST_CODE_READ_PHONE_STATE, Manifest.permission.READ_PHONE_STATE)
        }
        neverAskClicked = neverAsk
        return true
    }

    override fun onRationalRequestIntercepted() {

    }

    override fun onLocationChanged(location: Location?, priority: Int) {
        if (location != null) {
            Data.latitude = location.latitude
            Data.longitude = location.longitude
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.driver_splash_activity)

        Data.locationFetcher = LocationFetcher(this, 1000, 1)
        setLoginData()
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_back_selector)
        }

        tvToolbar.typeface = Fonts.mavenMedium(this)

        setToolbarVisibility(false)

        val resp = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)
        if (resp != ConnectionResult.SUCCESS) {
            Log.e(TAG, "=$resp")
            DialogPopup.showGooglePlayErrorAlert(this@DriverSplashActivity)
            return
        }


        isSavedInstanceStateNull = savedInstanceState == null
        neverAskClicked = false
        if (isSavedInstanceStateNull) {
            Log.d(TAG, " calling permission for location onCreate")
            permissionCommon.getPermission(REQUEST_CODE_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            //  if activity is recreated, required permissions may have been revoked,
            // restart activityO to check complete flow of permissions and prevent fragments to reattach without permissions check
            restartApp()
        }


    }

    private fun setLoginData() {
        try {
            Data.filldetails(this@DriverSplashActivity)
            Data.generateKeyHash(this@DriverSplashActivity)
        } catch (e: Exception) {
            android.util.Log.e(TAG, e.localizedMessage)
        }
    }


    override fun onResume() {
        super.onResume()
        if(neverAskClicked){
            if(permissionCommon.getDisplayedSnackbar() != null && !permissionCommon.getDisplayedSnackbar().isShown) {
                if (!PermissionCommon.isGranted(Manifest.permission.ACCESS_FINE_LOCATION, this)) {
                    DialogPopup.alertPopupTwoButtonsWithListeners(this, "", getString(R.string.app_needs_location_permission),
                            getString(R.string.grant), getString(R.string.cancel),
                            { permissionCommon.openSettingsScreen(this@DriverSplashActivity) },
                            { finish() }, false, false)
                } else if(!PermissionCommon.isGranted(Manifest.permission.READ_PHONE_STATE, this)){// && DeviceUniqueID.getCachedUniqueId(this).isBlank()){
                    DialogPopup.alertPopupTwoButtonsWithListeners(this, "", getString(R.string.app_needs_phone_state_permission),
                            getString(R.string.grant), getString(R.string.cancel),
                            { permissionCommon.openSettingsScreen(this@DriverSplashActivity) },
                            { finish() }, false, false)
                } else {
                    restartApp()
                }
            } else if(PermissionCommon.isGranted(Manifest.permission.ACCESS_FINE_LOCATION, this)
                    && PermissionCommon.isGranted(Manifest.permission.READ_PHONE_STATE, this)){ //!DeviceUniqueID.getCachedUniqueId(this).isBlank() ||
                restartApp()
            }
        }
        if(PermissionCommon.isGranted(Manifest.permission.ACCESS_FINE_LOCATION, this)) {
            if (Data.locationFetcher != null) Data.locationFetcher.connect()
            LocationInit.showLocationAlertDialog(this)
        }
        registerbackForOTPDetection()


    }

    private fun registerbackForOTPDetection() {
        if(PermissionCommon.isGranted(Manifest.permission.READ_SMS, this)) {
            var otpFragment = supportFragmentManager.findFragmentByTag(OTPConfirmFragment::class.simpleName);
            if (otpFragment != null) {
                otpFragment = otpFragment as OTPConfirmFragment;
                if (otpFragment.isWaitingForOTPDetection()) {
                    registerForSmsReceiver(true)

                }

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

/*
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {


                if(remove){
                    window?.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                    window?.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)

                }else{
                    window?.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                    window?.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
                }


            }*/


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



    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionCommon.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}

