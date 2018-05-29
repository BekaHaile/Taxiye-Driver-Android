package product.clicklabs.jugnoo.driver.ui

import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat
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

class
DriverSplashActivity : BaseFragmentActivity(), LocationUpdate, SplashFragment.InteractionListener, ToolbarChangeListener {

    private val TAG = SplashFragment::class.simpleName
    private val container by bind<FrameLayout>(R.id.container)


    override fun onLocationChanged(location: Location?, priority: Int) {
        if (location != null) {
            Data.latitude = location.latitude
            Data.longitude = location.longitude
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme)
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && enableSharedTransition && view != null) {
            try {
                supportFragmentManager.inTransactionWithSharedTransition(view, {
                    setAllowOptimization(true)
                            .replace(container.id, LoginFragment.newInstance(true), LoginFragment::class.simpleName)
                })
            } catch (e: Exception) {

                supportFragmentManager.inTransaction {
                    setAllowOptimization(true)
                            .replace(container.id, LoginFragment.newInstance(false), LoginFragment::class.simpleName)
                }
            }
        } else {
            supportFragmentManager.inTransaction {
                setAllowOptimization(true)
                        .replace(container.id, LoginFragment.newInstance(false), LoginFragment::class.simpleName)
            }
        }
    }

    fun addLoginViaOTPScreen(phone: String, countryCode: String, missedCallNumber: String?) {

        supportFragmentManager.inTransactionWithAnimation {
            add(container.id, OTPConfirmFragment.newInstance(phone, countryCode, missedCallNumber),
                    OTPConfirmFragment::class.simpleName).addToBackStack(OTPConfirmFragment::class.simpleName)
                    .hide(supportFragmentManager.findFragmentByTag(LoginFragment::class.simpleName))

        }
    }

    fun openDriverSetupFragment(accessToken: String) {
        supportFragmentManager.inTransactionWithAnimation {
            add(container.id, DriverSetupFragment.newInstance(accessToken),
                    OTPConfirmFragment::class.simpleName).addToBackStack(DriverSetupFragment::class.simpleName)
                    .hide(supportFragmentManager.findFragmentByTag(supportFragmentManager
                            .getBackStackEntryAt(supportFragmentManager.backStackEntryCount - 1).name))

        }
    }

    fun addDriverSetupFragment(accessToken: String) {
        supportFragmentManager.inTransactionWithAnimation {
            replace(container.id, DriverSetupFragment.newInstance(accessToken), DriverSetupFragment::class.simpleName)
        }
    }

    override fun openPhoneLoginScreen(enableSharedTransition: Boolean, sharedView: View?) {
        addPhoneNumberScreen(enableSharedTransition, sharedView)
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


    override fun onBackPressed() {
        // TODO use proper method for creating the user flow
        if (supportFragmentManager.backStackEntryCount == 0 && supportFragmentManager
                        .findFragmentByTag(DriverSetupFragment::class.simpleName) is DriverSetupFragment) {
            JSONParser.saveAccessToken(this, "")
            addPhoneNumberScreen(false, null)
            setToolbarVisibility(false)

        } else {
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        if (item?.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}

interface ToolbarChangeListener {

    fun setToolbarText(title: String)

    fun setToolbarVisibility(isVisible: Boolean)
}

