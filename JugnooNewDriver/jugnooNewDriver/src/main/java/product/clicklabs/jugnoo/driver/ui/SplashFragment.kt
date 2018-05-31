package product.clicklabs.jugnoo.driver.ui

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.google.firebase.iid.FirebaseInstanceId
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.activity_notification_center.*
import kotlinx.android.synthetic.main.frag_splash.*
import kotlinx.android.synthetic.main.frag_splash.view.*
import org.json.JSONObject
import product.clicklabs.jugnoo.driver.*
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags
import product.clicklabs.jugnoo.driver.retrofit.RestClient
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse
import product.clicklabs.jugnoo.driver.utils.*
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response
import retrofit.mime.TypedByteArray
import java.util.*
import java.util.concurrent.TimeUnit

class SplashFragment : Fragment() {



    private val TAG = SplashFragment::class.simpleName
    private val intentFilter = IntentFilter()
    private var mListener:InteractionListener?=null
    private lateinit var parentActivity : Activity
    private val behaviourSubject by lazy { executePending() }
    private val deviceTokenObservable by lazy { PublishSubject.create<Void>() }
    private var disposable : Disposable? = null
    private val compositeDisposable by lazy { CompositeDisposable() }

    init {
        intentFilter.addAction(Constants.ACTION_DEVICE_TOKEN_UPDATED)
    }

    companion object {
        private const val DEVICE_TOKEN_WAIT_TIME = 4 * 1000L

    }



    var deviceTokenReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            if (intent.action != null && intent.action == Constants.ACTION_DEVICE_TOKEN_UPDATED) {
                Log.i(TAG, "Firebase service emitted deviceToken")

                if (FirebaseInstanceId.getInstance().token != null) {
                    deviceTokenObservable.onComplete()
                }
            }
        }
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        parentActivity = context as Activity
        if(context is InteractionListener){
            mListener = context
        } else {
            throw IllegalArgumentException(TAG+" Interaction listener required")
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.frag_splash)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkForInternet(view!!.root)
    }

    private fun checkForInternet(rootView: View) {

        parentActivity.withNetwork( { start() }, false, {
            // remove on screen navigation flags to display the snackbar above them
            removeNavigationFlags()

            val snackBar = Snackbar.make(rootView, Data.CHECK_INTERNET_MSG, Snackbar.LENGTH_INDEFINITE)
                    .setActionTextColor(ContextCompat.getColor(activity, android.R.color.white))

            snackBar.setAction("Retry", {

                // add on screen navigation translucent flags for smooth image shared animation
                addNavigationFlags()
                snackBar.dismiss()
                checkForInternet(rootView)
            })
            snackBar.view.setBackgroundColor(ContextCompat.getColor(activity, android.R.color.holo_red_dark))
            snackBar.show()

        })
    }


    private fun start() {

        val w = activity.getWindow()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }

        checkForBatteryOptimisation()

        compositeDisposable.add(deviceTokenObservable
                .timeout(DEVICE_TOKEN_WAIT_TIME, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({},{ showTokenNotFoundDialog()},{
                    pushAPIs(this@SplashFragment.activity)
                    LocalBroadcastManager.getInstance(activity).unregisterReceiver(deviceTokenReceiver)
                }))
    }

    private fun isMockLocationEnabled():Boolean{
        if (Utils.mockLocationEnabled(Data.locationFetcher.locationUnchecked)) {
            DialogPopup.alertPopupWithListener(this@SplashFragment.activity, "", resources.getString(R.string.disable_mock_location)) {
                startActivity(Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS))
                activity.finish()
            }
            return true
        }
        return false
    }



    private fun checkForBatteryOptimisation() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val packageName = (this.activity.packageName)
                val pm = this.activity.getSystemService(Context.POWER_SERVICE) as PowerManager
                if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                    val intent = Intent()
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
        if(isMockLocationEnabled()) return

        disposable = behaviourSubject.
                retryUntil({ Database2.getInstance(context).allPendingAPICallsCount<=0; })
                  .subscribeOn(Schedulers.newThread())
                  //.delay(1000,TimeUnit.MILLISECONDS)
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribe({},{ Log.d(TAG, "pushAPIs : ${it.message}") },{ accessTokenLogin(this@SplashFragment.activity) })
        compositeDisposable.add(disposable!!)
    }


    fun executePending(): BehaviorSubject<Boolean> {

        val subject =  BehaviorSubject.create<Boolean>()

        val pendingAPICalls = Database2.getInstance(context).allPendingAPICalls
        for (pendingAPICall in pendingAPICalls) {
            Log.e(TAG, "pendingApiCall=$pendingAPICall")
            PendingApiHit().startAPI(context, pendingAPICall)
        }
        val pendingApisCount = Database2.getInstance(context).allPendingAPICallsCount

        if(pendingApisCount <= 0) {

            subject.onComplete()
        } else {
            subject.onError(Throwable("Pending apis count"))
        }
        return subject
    }

    fun accessTokenLogin(activity: Activity) {

        val accPair = JSONParser.getAccessTokenPair(activity)
        val responseTime = System.currentTimeMillis()
        val conf = resources.configuration
        if (!"".equals(accPair.first, ignoreCase = true)) {
            if (AppStatus.getInstance(activity.applicationContext).isOnline(activity.applicationContext)) {


                if (Data.locationFetcher != null) {
                    Data.latitude = Data.locationFetcher.latitude
                    Data.longitude = Data.locationFetcher.longitude
                }
                val params = HashMap<String, String>()

                params["access_token"] = accPair.first
                params["device_token"] = Data.deviceToken

                params["latitude"] = "" + Data.latitude
                params["longitude"] = "" + Data.longitude

                params["locale"] = conf.locale.toString()
                params["app_version"] = "" + Data.appVersion
                params["device_type"] = Data.DEVICE_TYPE
                params["unique_device_id"] = Data.uniqueDeviceId
                params["is_access_token_new"] = "1"
                params["client_id"] = Data.CLIENT_ID
                params["login_type"] = Data.LOGIN_TYPE
                HomeUtil.putDefaultParams(params)

                params["device_name"] = Utils.getDeviceName()
                params["imei"] = DeviceUniqueID.getUniqueId(activity)

                if (Utils.isAppInstalled(activity, Data.GADDAR_JUGNOO_APP)) {
                    params["auto_n_cab_installed"] = "1"
                } else {
                    params["auto_n_cab_installed"] = "0"
                }


                if (Utils.isAppInstalled(activity, Data.UBER_APP)) {
                    params["uber_installed"] = "1"
                } else {
                    params["uber_installed"] = "0"
                }

                if (Utils.telerickshawInstall(activity)) {
                    params["telerickshaw_installed"] = "1"
                } else {
                    params["telerickshaw_installed"] = "0"
                }

                if (Utils.olaInstall(activity)) {
                    params["ola_installed"] = "1"
                } else {
                    params["ola_installed"] = "0"
                }

                if (Utils.isDeviceRooted()) {
                    params["device_rooted"] = "1"
                } else {
                    params["device_rooted"] = "0"
                }



                RestClient.getApiServices().accessTokenLoginRetro(params, object : Callback<RegisterScreenResponse> {
                    override fun success(registerScreenResponse: RegisterScreenResponse, response: Response) {
                        try {
                            val jsonString = String((response.body as TypedByteArray).bytes)
                            val jObj: JSONObject
                            jObj = JSONObject(jsonString)
                            val flag = jObj.getInt("flag")
                            val message = JSONParser.getServerMessage(jObj)

                            if (!SplashNewActivity.checkIfTrivialAPIErrors(activity, jObj, flag, logoutCallback)) {
                                if (ApiResponseFlags.AUTH_NOT_REGISTERED.getOrdinal() == flag) {
                                    DialogPopup.alertPopup(activity, "", message)
                                } else if (ApiResponseFlags.AUTH_LOGIN_FAILURE.getOrdinal() == flag) {
                                    DialogPopup.alertPopup(activity, "", message)
                                } else if (ApiResponseFlags.AUTH_LOGIN_SUCCESSFUL.getOrdinal() == flag) {
                                    if (!SplashNewActivity.checkIfUpdate(jObj.getJSONObject("login"), activity)) {
                                        //										new AccessTokenDataParseAsync(activity, jsonString, message).execute();
                                        var resp: String
                                        try {
                                            resp = JSONParser().parseAccessTokenLoginData(activity, jsonString)
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                            resp = Constants.SERVER_TIMEOUT
                                        }

                                        if (resp.contains(Constants.SERVER_TIMEOUT)) {
                                            DialogPopup.alertPopup(activity, "", message)
                                        } else {

                                            mListener?.goToHomeScreen()

                                        }

                                        Utils.deleteMFile()
                                        Utils.clearApplicationData(activity)
                                        FlurryEventLogger.logResponseTime(activity, System.currentTimeMillis() - responseTime, FlurryEventNames.LOGIN_ACCESSTOKEN_RESPONSE)

                                    }
                                } else if (ApiResponseFlags.UPLOAD_DOCCUMENT.getOrdinal() == flag) {
                                    val accessToken = jObj.getString("access_token")
                                    JSONParser.saveAccessToken(activity, accessToken)
                                    (parentActivity as DriverSplashActivity).addDriverSetupFragment(accessToken)
                                } else {
                                    DialogPopup.alertPopup(activity, "", message)
                                }
                            }
                        } catch (exception: Exception) {
                            exception.printStackTrace()
                            DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG)
                        }

                    }

                    override fun failure(error: RetrofitError) {

                        DialogPopup.dismissLoadingDialog()
                        DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG)

                    }
                })

            } else {
                DialogPopup.alertPopupWithListener(activity, "", Data.CHECK_INTERNET_MSG,{
                    this@SplashFragment.activity.finish()
                })
            }
        }else{
            mListener?.openPhoneLoginScreen(true, imageView)
        }

    }


    private var logoutCallback = object: LogoutCallback {
        override fun redirectToSplash(): Boolean {
            mListener?.openPhoneLoginScreen(true, imageView)
            return false
        }
    }

    private fun showTokenNotFoundDialog(){
        DialogPopup.alertPopupWithListener(activity,"",getString(R.string.device_token_not_found_message)
                , { this@SplashFragment.activity.finish(); })
    }

    override fun onResume() {
        super.onResume()

        if (FirebaseInstanceId.getInstance().token != null) {
            deviceTokenObservable.onComplete()
        } else {
            LocalBroadcastManager.getInstance(activity).registerReceiver(deviceTokenReceiver,intentFilter)
        }
    }

    override fun onPause() {
        super.onPause()

        LocalBroadcastManager.getInstance(activity).unregisterReceiver(deviceTokenReceiver)
        if (disposable != null && !disposable!!.isDisposed) {
            disposable?.dispose()
        }
    }

    interface InteractionListener{
        fun openPhoneLoginScreen(enableSharedTransition: Boolean = false, sharedView: View? = null)

        fun goToHomeScreen()

        fun registerForSmsReceiver()

        fun getPrefillOtpIfany():String?
    }

    override fun onDestroy() {

        if (!compositeDisposable.isDisposed){
            disposable?.dispose()
        }
        try {
            LocalBroadcastManager.getInstance(activity).unregisterReceiver(deviceTokenReceiver)
        }
        catch (e : Exception){
            Log.d(TAG, "onDestroy: ${e.message}")
        }
        super.onDestroy()
    }

    override fun onDestroyView() {
        removeNavigationFlags()
        super.onDestroyView()
    }


    /**
     * add the translucent flags on the onscreen navigation bar for kikat above devices
     */
    private fun addNavigationFlags() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val w = activity.getWindow()
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            w.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * removes the translucent flags on the onscreen navigation bar for kikat above devices
     */
    private fun removeNavigationFlags() {

        val w = activity.getWindow()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            w.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

}
