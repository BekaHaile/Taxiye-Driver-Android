package product.clicklabs.jugnoo.driver.ui

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.transition.TransitionInflater
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import kotlinx.android.synthetic.main.activity_toolbar.*
import kotlinx.android.synthetic.main.activity_toolbar.view.*
import kotlinx.android.synthetic.main.driver_splash_activity.*
import product.clicklabs.jugnoo.driver.*
import product.clicklabs.jugnoo.driver.utils.*
import product.clicklabs.jugnoo.driver.utils.PermissionCommon.REQUEST_CODE_FINE_LOCATION

/**
 * Created by Parminder Saini on 16/04/18.
 */

class DriverSplashActivity : BaseFragmentActivity(), LocationUpdate, SplashFragment.InteractionListener, ToolbarChangeListener,
        PermissionCommon.PermissionListener {

    private val TAG = DriverSplashActivity::class.simpleName
    private val container by bind<FrameLayout>(R.id.container)
    private var otpDetectedViaSms :String? = null;
    private var otpLength:Int = 4;


    private val permissionCommon by lazy { PermissionCommon(this).setCallback(this) }

    private val REQUEST_CODE_FINE_LOCATION_FOR_BUTTON = 12201;

    private var grantedCalled = false;
    @SuppressLint("MissingPermission")
    override fun permissionGranted(requestCode: Int) {
        if (requestCode == REQUEST_CODE_FINE_LOCATION || requestCode == REQUEST_CODE_FINE_LOCATION_FOR_BUTTON) {
            grantedCalled = true
            if(Data.locationFetcher != null) {
                Data.locationFetcher.connect()
            } else {
                Data.locationFetcher = LocationFetcher(this, 1000, 1)
                Data.locationFetcher.connect()
            }

            setupSplashFragment()
        }
    }

    private fun setupSplashFragment() {
        llGrantPermission.gone()
        val uid = DeviceUniqueID.getUniqueId(this)
        Log.d(TAG, "UID : $uid")
        LocationInit.showLocationAlertDialog(this)
        supportFragmentManager.inTransaction {
            add(container.id, SplashFragment(), SplashFragment::class.simpleName)
        }
        BaseFragmentActivity.checkOverlayPermissionOpenJeanie(this, false, false)
    }

    override fun permissionDenied(requestCode: Int, neverAsk: Boolean) : Boolean {
        if(requestCode == REQUEST_CODE_FINE_LOCATION_FOR_BUTTON && neverAsk){
            permissionCommon.openSettingsScreen(this)
            return false
        }
        return true
    }

    override fun onRationalRequestIntercepted() {}

    override fun onLocationChanged(location: Location?, priority: Int) {
        if (location != null) {
            Data.latitude = location.latitude
            Data.longitude = location.longitude
        }

    }

    private var firstTime: Boolean = false;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
        setContentView(R.layout.driver_splash_activity)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_back_selector)
        }

        // TODO: 08/12/18 remove this
        AppSignatureHelper.getAppSignatures(this)

        tvToolbar.typeface = Fonts.mavenMedium(this)

        setToolbarVisibility(false)

        Data.locationFetcher = LocationFetcher(this, 1000, 1)
        setLoginData()

        val resp = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this)
        if (resp != ConnectionResult.SUCCESS) {
            Log.e(TAG, "=$resp")
            DialogPopup.showGooglePlayErrorAlert(this@DriverSplashActivity)
            return
        }

        bGrant.setOnClickListener(object: View.OnClickListener{
            override fun onClick(v: View?) {
                permissionCommon.getPermission(REQUEST_CODE_FINE_LOCATION_FOR_BUTTON, PermissionCommon.SKIP_RATIONAL_MESSAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE)
            }
        })

        firstTime = true;
        if (savedInstanceState != null) {
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
        if(PermissionCommon.isGranted(Manifest.permission.ACCESS_FINE_LOCATION, this)
                && PermissionCommon.isGranted(Manifest.permission.READ_PHONE_STATE, this)){
            if(!grantedCalled){
                llGrantPermission.gone()
                permissionCommon.dismissSnackbars()
                permissionCommon.getPermission(REQUEST_CODE_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE)
            }
        } else if (firstTime){
            llGrantPermission.visible()
            permissionCommon.getPermission(REQUEST_CODE_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.READ_PHONE_STATE)
        }
        firstTime = false
        grantPermissionText()


    }

    private fun grantPermissionText() {
        if (!PermissionCommon.isGranted(Manifest.permission.ACCESS_FINE_LOCATION, this)
                && !PermissionCommon.isGranted(Manifest.permission.READ_PHONE_STATE, this)) {
            tvGrantPermission.text = getString(R.string.permissions_title, getString(R.string.permissions_location_phone));
        } else if (!PermissionCommon.isGranted(Manifest.permission.ACCESS_FINE_LOCATION, this)) {
            tvGrantPermission.text = getString(R.string.permissions_title, getString(R.string.permissions_location));
        } else if (!PermissionCommon.isGranted(Manifest.permission.READ_PHONE_STATE, this)) {
            tvGrantPermission.text = getString(R.string.permissions_title, getString(R.string.permissions_phone));
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


    override fun getPrefillOtpIfany():String? {
       return otpDetectedViaSms;
    }


    override fun goToHomeScreen() {
        val intent = Intent(this, HomeActivity::class.java)
        if (getIntent().extras != null) {
            intent.putExtras(getIntent().extras)
            intent.putExtra(Constants.FUGU_CHAT_BUNDLE, getIntent().extras)

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




    public fun isLoginFragmentVisible():Boolean{
        val loginFragment= supportFragmentManager.findFragmentByTag(LoginFragment::class.simpleName)
        return loginFragment!=null && loginFragment.isVisible/* && (loginFragment as LoginFragment).assist*/
    }

    public fun openVehicleDetails(accessToken: String,cityId:String,vehicleType:String,userName:String ){
       supportFragmentManager.inTransactionWithAnimation {

            add(container.id, VehicleDetailsFragment.newInstance(accessToken, cityId, vehicleType,userName), VehicleDetailsFragment::class.simpleName)
                    .hide(supportFragmentManager.findFragmentByTag(DriverSetupFragment::class.simpleName))
                    .addToBackStack(DriverSetupFragment::class.simpleName)
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionCommon.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun getMainIntent(): Intent {
        return getIntent()
    }
}


