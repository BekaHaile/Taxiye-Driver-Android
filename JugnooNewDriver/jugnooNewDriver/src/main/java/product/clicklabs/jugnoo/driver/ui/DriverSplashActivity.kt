package product.clicklabs.jugnoo.driver.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.graphics.Typeface
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.transition.TransitionInflater
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.FrameLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.squareup.picasso.Picasso
import com.squareup.picasso.PicassoTools
import kotlinx.android.synthetic.main.activity_toolbar.*
import kotlinx.android.synthetic.main.activity_toolbar.view.*
import kotlinx.android.synthetic.main.driver_splash_activity.*
import kotlinx.android.synthetic.main.layout_driver_menu.*
import org.json.JSONObject
import kotlinx.android.synthetic.main.layout_switch_slide.*
import product.clicklabs.jugnoo.driver.*
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags
import product.clicklabs.jugnoo.driver.datastructure.DriverScreenMode
import product.clicklabs.jugnoo.driver.datastructure.UserMode
import product.clicklabs.jugnoo.driver.retrofit.RestClient
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse
import product.clicklabs.jugnoo.driver.subscription.SubscriptionFragment
import product.clicklabs.jugnoo.driver.fragments.TractionListFragment
import product.clicklabs.jugnoo.driver.utils.*
import product.clicklabs.jugnoo.driver.utils.PermissionCommon.REQUEST_CODE_FINE_LOCATION
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response
import retrofit.mime.TypedByteArray
import java.util.HashMap

/**
 * Created by Parminder Saini on 16/04/18.
 */

class DriverSplashActivity : BaseFragmentActivity(), LocationUpdate, SplashFragment.InteractionListener, ToolbarChangeListener,
        PermissionCommon.PermissionListener, CallbackSlideOnOff {

    private val TAG = DriverSplashActivity::class.simpleName
    private val container by bind<FrameLayout>(R.id.container)
    private var otpDetectedViaSms :String? = null;
    private var otpLength:Int = 4;


    private val permissionCommon by lazy { PermissionCommon(this).setCallback(this) }

    private val REQUEST_CODE_FINE_LOCATION_FOR_BUTTON = 12201;

    private var grantedCalled = false;
    private var slidingSwitch: SlidingSwitch? = null

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
        try {
            Data.filldetails(this@DriverSplashActivity)
        } catch (e: Exception) {
            android.util.Log.e(TAG, e.localizedMessage)
        }
        LocationInit.showLocationAlertDialog(this)
        supportFragmentManager.inTransaction {
            add(container.id, SplashFragment(), SplashFragment::class.simpleName)
        }
        BaseFragmentActivity.checkOverlayPermissionOpenJeanie(this, false, false)
    }

    override fun permissionDenied(requestCode: Int, neverAsk: Boolean) : Boolean {
        if(requestCode == REQUEST_CODE_FINE_LOCATION_FOR_BUTTON && neverAsk){
            PermissionCommon.openSettingsScreen(this)
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
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    permissionCommon.getPermission(REQUEST_CODE_FINE_LOCATION_FOR_BUTTON, PermissionCommon.SKIP_RATIONAL_MESSAGE,
                            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION, Manifest.permission.READ_PHONE_STATE)
                } else {
                    permissionCommon.getPermission(REQUEST_CODE_FINE_LOCATION_FOR_BUTTON, PermissionCommon.SKIP_RATIONAL_MESSAGE,
                            Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE)
                }
            }
        })

        firstTime = true;
        if (savedInstanceState != null) {
            //  if activity is recreated, required permissions may have been revoked,
            // restart activityO to check complete flow of permissions and prevent fragments to reattach without permissions check
            restartApp()
        }
        slidingSwitch = SlidingSwitch(containerSwitch, this)
        slidingSwitch?.setSlideLeft()

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
                && PermissionCommon.isGranted(Manifest.permission.READ_PHONE_STATE, this)
                && ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && PermissionCommon.isGranted(Manifest.permission.ACCESS_BACKGROUND_LOCATION, this))
                        || Build.VERSION.SDK_INT < Build.VERSION_CODES.Q)){
            if(!grantedCalled){
                llGrantPermission.gone()
                permissionCommon.dismissSnackbars()
                requestAllPermissions()
            }
        } else if (firstTime){
            llGrantPermission.visible()
            requestAllPermissions()
        }
        firstTime = false
        grantPermissionText()

        MyApplication.getInstance().setmActivity(this)


    }

    private fun requestAllPermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            permissionCommon.getPermission(REQUEST_CODE_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION, Manifest.permission.READ_PHONE_STATE)
        } else {
            permissionCommon.getPermission(REQUEST_CODE_FINE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.READ_PHONE_STATE)
        }
    }

    private fun grantPermissionText() {
        if (!PermissionCommon.isGranted(Manifest.permission.ACCESS_FINE_LOCATION, this)
                && !PermissionCommon.isGranted(Manifest.permission.READ_PHONE_STATE, this)) {
            tvGrantPermission.text = getString(R.string.permissions_title, getString(R.string.permissions_location_phone));
        }
        else if (!PermissionCommon.isGranted(Manifest.permission.ACCESS_FINE_LOCATION, this)) {
            tvGrantPermission.text = getString(R.string.permissions_title, getString(R.string.permissions_location));
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && !PermissionCommon.isGranted(Manifest.permission.ACCESS_BACKGROUND_LOCATION, this)) {
            tvGrantPermission.text = getString(R.string.permissions_title, getString(R.string.permissions_location_in_background));
        }
        else if (!PermissionCommon.isGranted(Manifest.permission.READ_PHONE_STATE, this)) {
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
                    .hide(supportFragmentManager.findFragmentByTag(LoginFragment::class.simpleName)!!)
                    .addToBackStack(OTPConfirmFragment::class.simpleName)
        }
    }

    fun openDriverSetupFragment(accessToken: String) {
        supportFragmentManager.inTransactionWithAnimation {
            if(supportFragmentManager.findFragmentByTag(LoginFragment::class.simpleName) != null
                    && !(supportFragmentManager.findFragmentByTag(LoginFragment::class.simpleName) as Fragment).isHidden){
                add(container.id, DriverSetupFragment.newInstance(accessToken), DriverSetupFragment::class.simpleName)
                        .hide(supportFragmentManager.findFragmentByTag(LoginFragment::class.simpleName)!!)
                        .addToBackStack(DriverSetupFragment::class.simpleName)
            } else {
                add(container.id, DriverSetupFragment.newInstance(accessToken), DriverSetupFragment::class.simpleName)
                        .addToBackStack(DriverSetupFragment::class.simpleName)
            }
        }
    }

    fun addDriverSetupFragment(accessToken: String) {
        supportFragmentManager.inTransaction {
//            setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left).
                    add(container.id, DriverSetupFragment.newInstance(accessToken), DriverSetupFragment::class.simpleName)
                    .addToBackStack(DriverSetupFragment::class.simpleName)
        }

        if(supportFragmentManager.findFragmentByTag(SplashFragment::class.simpleName) != null) {
            supportFragmentManager.beginTransaction()
                    .remove(supportFragmentManager.findFragmentByTag(SplashFragment::class.simpleName)!!)
                    .commit()
        }
    }

    override fun openPhoneLoginScreen(enableSharedTransition: Boolean, sharedView: View?) {
        addPhoneNumberScreen(enableSharedTransition, sharedView)
    }


    override fun getPrefillOtpIfany():String? {
       return otpDetectedViaSms;
    }

    public var goToHomeScreenCalled = false
    override fun goToHomeScreen() {
        if(!hasWindowFocus()){
            goToHomeScreenCalled = true
            return
        }
        val intent = Intent(this, HomeActivity::class.java)
        if (getIntent().extras != null) {
            intent.putExtras(getIntent().extras!!)
            intent.putExtra(Constants.FUGU_CHAT_BUNDLE, getIntent().extras)

        }
        if (HomeActivity.activity != null) {
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(intent)
            finish()
        } else {
            startActivity(intent)
            ActivityCompat.finishAffinity(this)
        }

        overridePendingTransition(R.anim.right_in, R.anim.right_out)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if(hasFocus && goToHomeScreenCalled){
            goToHomeScreen()
            goToHomeScreenCalled = false
        }
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
        if(supportFragmentManager.backStackEntryCount > 0 && supportFragmentManager.getBackStackEntryAt(supportFragmentManager.backStackEntryCount - 1).name!!.contains(TractionListFragment::class.simpleName.toString())) {
            containerSwitch.visible()
            tvToolbar.gone()
        } else {
            containerSwitch.gone()
            tvToolbar.visible()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        if (item?.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

//	Retrofit

    fun logoutAsync(activity: Activity) {
        if (AppStatus.getInstance(activity).isOnline(activity)) {

            DialogPopup.showLoadingDialog(activity, resources.getString(R.string.please_wait))
            val params = HashMap<String, String>()
            params["access_token"] = Data.userData.accessToken
            params["is_access_token_new"] = "1"
            HomeUtil.putDefaultParams(params)

            RestClient.getApiServices().logoutRetro(params, object : Callback<RegisterScreenResponse> {
                override fun success(registerScreenResponse: RegisterScreenResponse, response: Response) {
                    try {
                        val jsonString = String((response.body as TypedByteArray).bytes)
                        val jObj: JSONObject
                        jObj = JSONObject(jsonString)
                        val flag = jObj.getInt("flag")
                        if (ApiResponseFlags.INVALID_ACCESS_TOKEN.getOrdinal() == flag) {
                            HomeActivity.logoutUser(activity, null)
                        } else if (ApiResponseFlags.SHOW_ERROR_MESSAGE.getOrdinal() == flag) {
                            val errorMessage = jObj.getString("error")
                            DialogPopup.alertPopup(activity, "", errorMessage)
                        } else if (ApiResponseFlags.SHOW_MESSAGE.getOrdinal() == flag) {
                            val message = jObj.getString("message")
                            DialogPopup.alertPopup(activity, "", message)
                        } else if (ApiResponseFlags.LOGOUT_FAILURE.getOrdinal() == flag) {
                            val errorMessage = jObj.getString("error")
                            DialogPopup.alertPopup(activity, "", errorMessage)
                        } else if (ApiResponseFlags.LOGOUT_SUCCESSFUL.getOrdinal() == flag) {
                            PicassoTools.clearCache(Picasso.with(activity))

                            GCMIntentService.clearNotifications(activity)

                            Data.clearDataOnLogout(activity)
                            Database2.getInstance(this@DriverSplashActivity).updateDriverServiceRun(Database2.NO)
                            activity.stopService(Intent(activity, DriverLocationUpdateService::class.java))

                            startActivity(Intent(this@DriverSplashActivity,DriverSplashActivity::class.java))
                            finishAffinity()
                        } else {
                            DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG)
                        }
                    } catch (exception: Exception) {
                        exception.printStackTrace()
                        DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG)
                    }

                    DialogPopup.dismissLoadingDialog()
                }

                override fun failure(error: RetrofitError) {
                    DialogPopup.dismissLoadingDialog()
                    DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG)
                }
            })
        } else {
            DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG)
        }

    }
    internal fun logoutPopup(activity: Activity) {
        try {
            val dialog = Dialog(activity, android.R.style.Theme_Translucent_NoTitleBar)
            dialog.window!!.attributes.windowAnimations = R.style.Animations_LoadingDialogFade
            dialog.setContentView(R.layout.dialog_custom_two_buttons)

            val frameLayout = dialog.findViewById<View>(R.id.rv) as RelativeLayout
            ASSL(activity, frameLayout, 1134, 720, true)

            val layoutParams = dialog.window!!.attributes
            layoutParams.dimAmount = 0.6f
            dialog.window!!.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            dialog.setCancelable(false)
            dialog.setCanceledOnTouchOutside(false)


            val textHead = dialog.findViewById<View>(R.id.textHead) as TextView
            textHead.setTypeface(Fonts.mavenRegular(activity), Typeface.BOLD)
            val textMessage = dialog.findViewById<View>(R.id.textMessage) as TextView
            textMessage.typeface = Fonts.mavenRegular(activity)

            textHead.text = resources.getString(R.string.alert)
            textMessage.text = resources.getString(R.string.logout_text)

            val btnOk = dialog.findViewById<View>(R.id.btnOk) as Button
            btnOk.typeface = Fonts.mavenRegular(activity)
            val btnCancel = dialog.findViewById<View>(R.id.btnCancel) as Button
            btnCancel.typeface = Fonts.mavenRegular(activity)

            btnOk.setOnClickListener {
                dialog.dismiss()
                logoutAsync(activity)
                FlurryEventLogger.event(FlurryEventNames.LOGOUT_FROM_APP)
            }

            btnCancel.setOnClickListener { dialog.dismiss() }

            dialog.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
    override fun onBackPressed() {
       when(supportFragmentManager.fragments[supportFragmentManager.fragments.size-1])
       {
           is SubscriptionFragment->{
               logoutPopup(this@DriverSplashActivity)
               return
           }
       }
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

    public fun openVehicleDetails(accessToken: String,cityId:String,vehicleType:String,userName:String,driverDetails:HashMap<String,String>?=null ){
       supportFragmentManager.inTransactionWithAnimation {

            add(container.id, VehicleDetailsFragment.newInstance(accessToken, cityId, vehicleType,userName,null,false,driverDetails), VehicleDetailsFragment::class.simpleName)
                    .hide(supportFragmentManager.findFragmentByTag(DriverSetupFragment::class.simpleName)!!)
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

    var fragFromOtp = false

    fun loadTractionFragment(accessToken: String, fragfromOtp: Boolean) {
        this.fragFromOtp = fragfromOtp
        if (fragfromOtp) {
            supportFragmentManager.inTransactionWithAnimation {
                add(container.id, TractionListFragment.newInstance(accessToken,true), TractionListFragment::class.simpleName)
                        .hide(supportFragmentManager.findFragmentByTag(OTPConfirmFragment::class.simpleName)!!)
                        .addToBackStack(TractionListFragment::class.simpleName)
            }
        } else {
            supportFragmentManager.beginTransaction()
                    .add(container.id, TractionListFragment.newInstance(accessToken,true), TractionListFragment::class.simpleName)
                    .addToBackStack(TractionListFragment::class.simpleName)
                    .commitAllowingStateLoss()

            supportFragmentManager.beginTransaction()
                    .remove(supportFragmentManager.findFragmentByTag(SplashFragment::class.simpleName)!!)
                    .commit()
        }

        containerSwitch.visible()
        tvToolbar.gone()
    }

    fun setContainerSwitch() {
        slidingSwitch?.setSlideLeft()
        tvOnlineTop.isSelected = false
        tvOfflineTop.isSelected = true
        viewSlide.background = ContextCompat.getDrawable(this@DriverSplashActivity, R.drawable.selector_red_theme_rounded)
        viewSlide.post { slidingSwitch?.setSlideLeft() }
        rlOnOff.background = ContextCompat.getDrawable(this@DriverSplashActivity, R.drawable.selector_red_stroke_white_theme)
        tvOfflineTop.text = getString(R.string.offline_caps)
        tvOnlineTop.text = getString(R.string.online_caps)
    }

    private fun removeTractionFragment() {
        if (supportFragmentManager.backStackEntryCount > 0
                && supportFragmentManager.getBackStackEntryAt(supportFragmentManager.backStackEntryCount - 1).name!!.contains(TractionListFragment::class.simpleName.toString())) {
            supportFragmentManager.popBackStack()

        }
        if(supportFragmentManager.findFragmentByTag(TractionListFragment::class.java.name) != null) {
            supportFragmentManager.beginTransaction()
                    .remove(supportFragmentManager.findFragmentByTag(TractionListFragment::class.java.name) as Fragment)
                    .commit()
        }
        containerSwitch.gone()
        tvToolbar.visible()
    }

    override fun onClickStandAction(slideDir: Int) {
        if(slideDir == 1) {
            removeTractionFragment()
            if(fragFromOtp) {
                openDriverSetupFragment(Prefs.with(this).getString(Constants.KEY_ACCESS_TOKEN,""))
            } else {
                addDriverSetupFragment(Prefs.with(this).getString(Constants.KEY_ACCESS_TOKEN, ""))
            }
            Prefs.with(this).remove(Constants.KEY_ACCESS_TOKEN)
        }
    }

}


