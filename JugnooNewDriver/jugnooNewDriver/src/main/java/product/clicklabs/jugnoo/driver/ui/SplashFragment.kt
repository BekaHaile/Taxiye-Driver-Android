package product.clicklabs.jugnoo.driver.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.iid.FirebaseInstanceId
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import kotlinx.android.synthetic.main.frag_splash.*
import kotlinx.android.synthetic.main.frag_splash.view.*
import org.json.JSONObject
import product.clicklabs.jugnoo.driver.*
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags
import product.clicklabs.jugnoo.driver.datastructure.DriverSubscription
import product.clicklabs.jugnoo.driver.datastructure.DriverSubscriptionEnabled
import product.clicklabs.jugnoo.driver.datastructure.PendingAPICall
import product.clicklabs.jugnoo.driver.retrofit.RestClient
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse
import product.clicklabs.jugnoo.driver.subscription.SubscriptionFragment
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
    private var parentActivity : Activity? = null
    private val behaviourSubject by lazy { BehaviorSubject.create<Void>() }
    private val deviceTokenObservable by lazy { PublishSubject.create<Void>() }
    private var apiDisposable : Disposable? = null
    private val compositeDisposable by lazy { CompositeDisposable() }
    private var isPendingExecutionOngoing = false
//    private var isFirstTime = true
    private var redirectedToLogin = false

    init {
        intentFilter.addAction(Constants.ACTION_DEVICE_TOKEN_UPDATED)
    }

    companion object {
        private const val DEVICE_TOKEN_WAIT_TIME = 20 * 1000L
        private const val CACHED_API_RETRY_COUNT:Long = 3

    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        parentActivity = context as Activity
        if(context is InteractionListener){
            mListener = context
        } else {
            throw IllegalArgumentException(TAG+" Interaction listener required")
        }
    }

    override fun onDetach() {
        parentActivity = null
        mListener = null
        super.onDetach()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return container?.inflate(R.layout.frag_splash)
    }

    fun openHomeFragment()
    {
        if(mListener!=null)
        {
            mListener?.goToHomeScreen()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        checkForInternet(view.root)
    }

    private fun checkForInternet(rootView: View) {
        parentActivity?.withNetwork( { start() }, false, {
            // remove on screen navigation flags to display the snackbar above them

            val snackBar = Snackbar.make(rootView, getString(R.string.check_internet_message), Snackbar.LENGTH_INDEFINITE)
                    .setActionTextColor(ContextCompat.getColor(requireActivity(), android.R.color.white))
                snackBar.setAction(getString(R.string.retry), { snackBar.dismiss()
                checkForInternet(rootView)
            })
            snackBar.view.setBackgroundColor(ContextCompat.getColor(requireActivity(), android.R.color.holo_red_dark))
            snackBar.show()



        })
    }


    private fun start() {
        compositeDisposable.add(deviceTokenObservable.timeout(DEVICE_TOKEN_WAIT_TIME, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread()).subscribe({},
                { showBlockerDialog(getString(R.string.device_token_not_found_message))},
                {
                    if(!isMockLocationEnabled()){
                        subscribeSubjectForAccessTokenLogin()
                        startExecutionForPendingAPis()
                    } else {
                        showBlockerDialog(getString(R.string.disable_mock_location))
                    }
                }))
    }

    private fun isMockLocationEnabled():Boolean{
        if (Utils.mockLocationEnabled(Data.locationFetcher.locationUnchecked)) {
            DialogPopup.alertPopupWithListener(parentActivity, "", resources.getString(R.string.disable_mock_location)) {
                startActivity(Intent(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS))
                parentActivity?.finish()
            }
            return true
        }
        return false
    }





    private fun subscribeSubjectForAccessTokenLogin(){
        apiDisposable = behaviourSubject.subscribe({},
                { Log.d(TAG, "onError: subscribeSubjectForAccessTokenLogin : ${it.message}") },
                { Log.d(TAG, "onComplete: subscribeSubjectForAccessTokenLogin")
                 isPendingExecutionOngoing = false ; accessTokenLogin(parentActivity)})
        compositeDisposable.add(apiDisposable!!)
    }

    private fun hitPendingApis(): Observable<Boolean> {
        return Observable.create<Boolean> {

            if(it.isDisposed) return@create

            val pendingAPICalls: ArrayList<PendingAPICall> = Database2.getInstance(context).allPendingAPICalls

            for (pendingAPICall in pendingAPICalls) {
                PendingApiHit().startAPI(context, pendingAPICall)
            }

            val  pendingApisCount = Database2.getInstance(context).allPendingAPICallsCount

            if(pendingApisCount >0) {
                it.onError(Throwable("Pending Apis"))
            } else {
                it.onComplete()
            }
        }
    }

//    lateinit var subsFrag:SubscriptionFragment
//     fun checkForSubscribedDriver(jsonString: String): Boolean {
//        val jObj = JSONObject(jsonString)
//        val jLoginObject = jObj.getJSONObject("login")
//        if(jLoginObject.has("driver_subscription")){
//            if(jLoginObject.getInt("driver_subscription")==1){
//                var key =0;
//                if(jLoginObject.has("stripe_cards_enabled")){
//                    key = jLoginObject.getInt("stripe_cards_enabled")
//                }
//                val token = jLoginObject.get("access_token");
//                subsFrag = SubscriptionFragment()
//                val args = Bundle()
//                args.putString("AccessToken", token as String)
//                args.putInt("stripe_key", key as Int)
//                subsFrag.setArguments(args)
//                fragmentManager!!.findFragmentByTag(SplashFragment::class.simpleName)?.let {
//                    fragmentManager!!.beginTransaction()
//                            .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
//                            .add(R.id.container, subsFrag, SubscriptionFragment::class.java.name)
//                            .hide(it)
//                            .addToBackStack(SubscriptionFragment::class.java.name)
//                            .commit()
//                }
//                return false
//            }else{
//                return true
//            }
//        }
//        return true
//    }

    private fun accessTokenLogin(mActivity: Activity?) {

        if (mActivity == null) return

        val accPair = JSONParser.getAccessTokenPair(mActivity)
        val responseTime = System.currentTimeMillis()
        val conf = resources.configuration
        if (!"".equals(accPair.first, ignoreCase = true)) {
            if (AppStatus.getInstance(mActivity.applicationContext).isOnline(mActivity.applicationContext)) {


                if (Data.locationFetcher != null) {
                    Data.latitude = Data.locationFetcher.latitude
                    Data.longitude = Data.locationFetcher.longitude
                }
                val params = HashMap<String, String>()

                params["access_token"] = accPair.first
                FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener{
                    if(!it.isSuccessful) {
                        Log.w(TAG,"device_token_unsuccessful - onReceive",it.exception)
                        return@addOnCompleteListener
                    }
                    if(it.result?.token != null) {
                        Log.e("${SplashNewActivity.DEVICE_TOKEN_TAG} $TAG + splpash frag -> accessTokenLogin", it.result?.token)
                        params["device_token"] = it.result?.token!!
                    }
                    accessTokeLoginFunc(params, conf, mActivity, responseTime)
                }



            } else {
                DialogPopup.alertPopupWithListener(mActivity, "", Data.CHECK_INTERNET_MSG,{
                    parentActivity?.finish()
                })
            }
        }else{
            redirectToLogin()
        }

    }

    private fun accessTokeLoginFunc(params: HashMap<String, String>, conf: Configuration, mActivity: Activity?, responseTime: Long) {
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
        params["imei"] = DeviceUniqueID.getCachedUniqueId(mActivity)

        if (Utils.isAppInstalled(mActivity, Data.GADDAR_JUGNOO_APP)) {
            params["auto_n_cab_installed"] = "1"
        } else {
            params["auto_n_cab_installed"] = "0"
        }


        if (Utils.isAppInstalled(mActivity, Data.UBER_APP)) {
            params["uber_installed"] = "1"
        } else {
            params["uber_installed"] = "0"
        }

        if (Utils.telerickshawInstall(mActivity)) {
            params["telerickshaw_installed"] = "1"
        } else {
            params["telerickshaw_installed"] = "0"
        }

        if (Utils.olaInstall(mActivity)) {
            params["ola_installed"] = "1"
        } else {
            params["ola_installed"] = "0"
        }

        if (Utils.isDeviceRooted()) {
            params["device_rooted"] = "1"
        } else {
            params["device_rooted"] = "0"
        }


        DialogPopup.showLoadingDialog(mActivity, getString(R.string.loading))

        RestClient.getApiServices().accessTokenLoginRetro(params, object : Callback<RegisterScreenResponse> {
            override fun success(registerScreenResponse: RegisterScreenResponse, response: Response) {
                try {
                    val jsonString = String((response.body as TypedByteArray).bytes)
                    val jObj: JSONObject
                    jObj = JSONObject(jsonString)
                    val flag = jObj.getInt("flag")
                    val message = JSONParser.getServerMessage(jObj)

                    if (!SplashNewActivity.checkIfTrivialAPIErrors(mActivity, jObj, flag, logoutCallback)) {
                        if (ApiResponseFlags.AUTH_NOT_REGISTERED.getOrdinal() == flag) {
                            DialogPopup.alertPopup(mActivity, "", message)
                        } else if (ApiResponseFlags.AUTH_LOGIN_FAILURE.getOrdinal() == flag) {
                            DialogPopup.alertPopup(mActivity, "", message)
                        } else if (ApiResponseFlags.AUTH_LOGIN_SUCCESSFUL.getOrdinal() == flag) {
                            if (!SplashNewActivity.checkIfUpdate(jObj.getJSONObject("login"), mActivity)) {
                                //										new AccessTokenDataParseAsync(mActivity, jsonString, message).execute();
                                var resp: String
                                try {
                                    resp = JSONParser().parseAccessTokenLoginData(mActivity, jsonString)
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                    resp = Constants.SERVER_TIMEOUT
                                }

                                if (resp.contains(Constants.SERVER_TIMEOUT)) {
                                    DialogPopup.alertPopup(mActivity, "", message)
                                } else {
                                        mListener?.goToHomeScreen()

                                }

                                Utils.deleteMFile(mActivity)
    //                                        Utils.clearApplicationData(mActivity)
                                FlurryEventLogger.logResponseTime(mActivity, System.currentTimeMillis() - responseTime, FlurryEventNames.LOGIN_ACCESSTOKEN_RESPONSE)

                            }
                        } else if (ApiResponseFlags.UPLOAD_DOCCUMENT.getOrdinal() == flag) {
                            if (!SplashNewActivity.checkIfUpdate(jObj.getJSONObject("login"), requireActivity())){
                                val vehicleModelEnabled = jObj.getJSONObject("login").optInt(Constants.KEY_VEHICLE_MODEL_ENABLED,
                                        if (resources.getBoolean(R.bool.vehicle_model_enabled)) 1 else 0)
                                Prefs.with(requireActivity()).save(Constants.KEY_VEHICLE_MODEL_ENABLED, vehicleModelEnabled)
                                        Data.setMultipleVehiclesEnabled(jObj.getJSONObject("login").optInt(Constants.MULTIPLE_VEHICLES_ENABLED, 0))
                                if(jObj.has(Constants.KEY_LOGIN)) {
                                    Prefs.with(requireActivity()).save(Constants.KEY_DRIVER_DOB_INPUT, jObj.getJSONObject(Constants.KEY_LOGIN).optInt(Constants.KEY_DRIVER_DOB_INPUT,
                                            getResources().getInteger(R.integer.driver_dob_input)))
                                    Prefs.with(context).save(Constants.KEY_DRIVER_GENDER_FILTER, jObj.getJSONObject(Constants.KEY_LOGIN).optInt(Constants.KEY_DRIVER_GENDER_FILTER,
                                            getResources().getInteger(R.integer.driver_gender_filter)))
                                }
                            }
                            val accessToken = jObj.getString("access_token")
                            val reqInactiveDrivers = jObj.optJSONObject(Constants.KEY_LOGIN)?.optInt(Constants.KEY_REQ_INACTIVE_DRIVER, 0)
                            JSONParser.saveAccessToken(mActivity, accessToken)
                            parentActivity?.let { with(it as DriverSplashActivity) {
                                if(reqInactiveDrivers == 1) {
                                    Prefs.with(context).save(Constants.KEY_ACCESS_TOKEN,accessToken)
                                    loadTractionFragment(accessToken, false)
                                    setContainerSwitch()
                                } else {
                                    addDriverSetupFragment(accessToken)
                                }

                            } }
                        } else {
                            DialogPopup.alertPopup(mActivity, "", message)
                        }
                    }
                } catch (exception: Exception) {
                    exception.printStackTrace()
                    DialogPopup.alertPopup(mActivity, "", Data.SERVER_ERROR_MSG)
                }
                DialogPopup.dismissLoadingDialog()
            }

            override fun failure(error: RetrofitError) {

                DialogPopup.dismissLoadingDialog()
                DialogPopup.alertPopup(mActivity, "", Data.SERVER_NOT_RESOPNDING_MSG)

            }
        })
    }

    private fun startExecutionForPendingAPis() {
        isPendingExecutionOngoing = true
        compositeDisposable.add(hitPendingApis().subscribeOn(Schedulers.io()).retry(CACHED_API_RETRY_COUNT)
                .observeOn(AndroidSchedulers.mainThread()).
                    subscribe({Log.i(TAG,"onNext for startExecutionForPendingAPis")},{
                    Log.i(TAG,"onError for startExecutionForPendingAPis {${it.message}}");showBlockerDialog(getString(R.string.cached_api_error))
                }, {Log.i(TAG,"onComplete for startExecutionForPendingAPis"); behaviourSubject.onComplete() } ))

    }


    private var logoutCallback = object: LogoutCallback {
        override fun redirectToSplash(): Boolean {
            redirectToLogin()
            return false
        }
    }

    private fun redirectToLogin() {
        redirectedToLogin = true;
        mListener?.openPhoneLoginScreen(true, imageView)
    }

    private fun showBlockerDialog(message:String){
        DialogPopup.alertPopupWithListener(requireActivity(),"",message
                , { parentActivity?.finish(); })
    }

    override fun onResume() {
        super.onResume()

        Log.i(TAG,"onResume Created Called")
        // check if device token has been generated in paused state,
        // complete subject if generated else register broadcast

        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener{
            if(!it.isSuccessful) {
                Log.w(TAG,"device_token_unsuccessful - onReceive",it.exception)
                deviceTokenObservable.onError(Throwable(it.exception?.localizedMessage))
                return@addOnCompleteListener
            }
            if(it.result?.token != null) {
                Log.e("${SplashNewActivity.DEVICE_TOKEN_TAG} $TAG  onResume", it.result?.token)
                deviceTokenObservable.onComplete()
            } else {
                deviceTokenObservable.onError(Throwable(it.exception?.localizedMessage))
            }
        }

        // if the pending api execution has already been subscribed once, resubscribe
        if (isPendingExecutionOngoing && apiDisposable?.isDisposed == true) {
            Log.i(TAG,"onResume resubscribing to subscribeSubjectForAccessTokenLogin")
            subscribeSubjectForAccessTokenLogin()
        }
    }

    override fun onPause() {
        super.onPause()


        // if pending api execution has been started dispose the api disposable
        // which will be resubscribed in onResume
        if (isPendingExecutionOngoing && apiDisposable?.isDisposed == false) {
            apiDisposable?.dispose()
        }
    }

    interface InteractionListener{
        fun openPhoneLoginScreen(enableSharedTransition: Boolean = false, sharedView: View? = null)

        fun goToHomeScreen()

        fun getPrefillOtpIfany():String?

        fun toggleDisplayFlags(remove:Boolean)
        fun getMainIntent() : Intent
    }

    override fun onDestroy() {

        if (!compositeDisposable.isDisposed){
            compositeDisposable.dispose()
        }
        super.onDestroy()
    }

    override fun onDestroyView() {

       /* if (!redirectedToLogin ) {
            *//**
             * removes the translucent flags on the onscreen navigation bar for kikat above devices
             *//*
            mListener?.toggleDisplayFlags(true)


        }*/
        super.onDestroyView()
    }




}
