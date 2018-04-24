package product.clicklabs.jugnoo.driver.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.PowerManager
import android.provider.Settings
import android.support.v4.app.Fragment
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.iid.FirebaseInstanceId
import product.clicklabs.jugnoo.driver.Constants
import product.clicklabs.jugnoo.driver.Data
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.utils.DialogPopup
import product.clicklabs.jugnoo.driver.utils.Utils


class SplashFragment() : Fragment() {

    val TAG = SplashFragment::class.simpleName;
    private val handler  = Handler();
    private val intentFilter = IntentFilter();
    init{
        intentFilter.addAction(Constants.ACTION_DEVICE_TOKEN_UPDATED);
    }


    companion object {

        fun newInstance(arguments: Bundle?): SplashFragment{
            val splashFragment = SplashFragment();
            splashFragment.arguments=arguments;
            return splashFragment;
        }

    }

    var deviceTokenReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            if (intent.action != null && intent.action == Constants.ACTION_DEVICE_TOKEN_UPDATED) {
                Log.i(TAG, "Firebase service emitted deviceToken");

            }


        }
    }

    var deviceTokenRunnable:Runnable  =  Runnable(){
        DialogPopup.alertPopupWithListener(activity,"",getString(R.string.device_token_not_found_message)
                , { this@SplashFragment.activity.finish(); })
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context);
        if(context is DriverSplashActivity){

        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater?.inflate(R.layout.frag_splash,container,false);


        checkForBatteryOptimisation();
        setLoginData();


        if(FirebaseInstanceId.getInstance().getToken()!=null){
            //Go ahead with server calls

        }else{
            LocalBroadcastManager.getInstance(activity).registerReceiver(deviceTokenReceiver,intentFilter);
            //Register a receiver to get DeviceToken and wait

        }





        return rootView;
    }

    private fun isMockLocationEnabled():Boolean{
        if (Utils.mockLocationEnabled(Data.locationFetcher.locationUnchecked)) {
            DialogPopup.alertPopupWithListener(this@SplashFragment.activity, "", resources.getString(R.string.disable_mock_location)) {
                startActivity(Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS))
                activity.finish();
            }
            return true;
        }
        return false;
    }

    private fun setLoginData() {
        try {
            Data.generateKeyHash(this@SplashFragment.activity);
            Data.filldetails(this@SplashFragment.activity);
        } catch (e: Exception) {
            Log.e(TAG,e.localizedMessage);
        }
    }

    private fun checkForBatteryOptimisation() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val packageName = (this.activity.packageName);
                val pm = this.activity.getSystemService(Context.POWER_SERVICE) as PowerManager;
                if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                    val intent = Intent();
                    intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                    intent.data = Uri.parse("package:$packageName")
                    startActivity(intent)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun pushAPIs(context: Context?){
        if(isMockLocationEnabled())return;





    }


}
