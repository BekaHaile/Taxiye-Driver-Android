package product.clicklabs.jugnoo.driver.ui

import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import product.clicklabs.jugnoo.driver.R

/**
 * Created by Parminder Saini on 16/04/18.
 */

inline fun FragmentManager.inTransaction(func: FragmentTransaction.()->FragmentTransaction){
    beginTransaction().func().commit();
}



class DriverSplashActivity:AppCompatActivity(){


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fabric.with(this,Crashlytics());
        setContentView(R.layout.driver_splash_activity);

        if(savedInstanceState!=null){

            supportFragmentManager.inTransaction{
                add(SplashFragment.newInstance(Bundle()),SplashFragment::.simpleName).addToBackStack(SplashFragment::class.simpleName);
            }


        }



    }




}

