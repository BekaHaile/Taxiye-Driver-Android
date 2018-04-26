package product.clicklabs.jugnoo.driver.ui

import android.app.Activity
import android.location.Location
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.os.Bundle
import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.view.View
import android.widget.FrameLayout
import com.crashlytics.android.Crashlytics
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GooglePlayServicesUtil
import io.fabric.sdk.android.Fabric
import product.clicklabs.jugnoo.driver.*
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity
import product.clicklabs.jugnoo.driver.utils.DialogPopup
import product.clicklabs.jugnoo.driver.utils.LocationInit
import product.clicklabs.jugnoo.driver.utils.Log

/**
 * Created by Parminder Saini on 16/04/18.
 */

inline fun FragmentManager.inTransaction(func: FragmentTransaction.()->FragmentTransaction){
    beginTransaction().func().commit();
}



fun <T : View> Activity.bind(@IdRes res : Int) : Lazy<T> {
    @Suppress("UNCHECKED_CAST")
    return lazy(LazyThreadSafetyMode.NONE){ findViewById(res) as T }
}

fun <T : View> Fragment.bind(@IdRes res : Int,view :View?) : Lazy<T> {
    @Suppress("UNCHECKED_CAST")
    return lazy(LazyThreadSafetyMode.NONE){ view?.findViewById(res) as T }
}


class DriverSplashActivity:BaseFragmentActivity(),LocationUpdate{

    private val TAG = SplashFragment::class.simpleName;
    private val container by bind<FrameLayout>(R.id.container_layout)


    override fun onLocationChanged(location: Location?, priority: Int) {
        if (location!=null) {
            Data.latitude = location.latitude;
            Data.longitude = location.longitude;
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fabric.with(this,Crashlytics());
        setContentView(R.layout.driver_splash_activity);

        Data.locationFetcher = LocationFetcher(this, 1000, 1)
        setLoginData();

        val resp = GooglePlayServicesUtil.isGooglePlayServicesAvailable(applicationContext);
        if (resp != ConnectionResult.SUCCESS) {
            Log.e(TAG, "=$resp")
            DialogPopup.showGooglePlayErrorAlert(this@DriverSplashActivity)
            return;
        }


        if(savedInstanceState==null){

            supportFragmentManager.inTransaction{
                add(container.id,SplashFragment.newInstance(Bundle())).addToBackStack(SplashFragment::class.simpleName);
            }


        }



    }

    private fun setLoginData() {
        try {
            Data.generateKeyHash(this@DriverSplashActivity);
            Data.filldetails(this@DriverSplashActivity);
        } catch (e: Exception) {
            android.util.Log.e(TAG,e.localizedMessage);
        }
    }


    override fun onResume() {
        super.onResume()
        if(Data.locationFetcher!=null)Data.locationFetcher.connect()
        LocationInit.showLocationAlertDialog(this)
    }

    override fun onPause() {
        super.onPause()
        try {
            if(Data.locationFetcher!=null) Data.locationFetcher.destroy()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Database2.getInstance(this).close()

    }

    public fun addPhoneNumberScreen(){

        supportFragmentManager.inTransaction{
            replace(container.id,LoginFragment.newInstance(Bundle())).addToBackStack(LoginFragment::class.simpleName);
        }
    }
    public fun addLoginViaOTPScreen(){

        supportFragmentManager.inTransaction{
            replace(container.id,OTPConfirmFragment.newInstance(Bundle())).addToBackStack(OTPConfirmFragment::class.simpleName);
        }
    }
}

