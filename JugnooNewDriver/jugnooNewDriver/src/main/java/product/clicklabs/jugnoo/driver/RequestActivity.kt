package product.clicklabs.jugnoo.driver

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import product.clicklabs.jugnoo.driver.apis.ApiAcceptRide
import product.clicklabs.jugnoo.driver.datastructure.*
import product.clicklabs.jugnoo.driver.fragments.BidRequestFragment
import product.clicklabs.jugnoo.driver.retrofit.RestClient
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse
import product.clicklabs.jugnoo.driver.utils.*
import retrofit.Callback
import retrofit.RetrofitError
import retrofit.client.Response
import retrofit.mime.TypedByteArray
import java.util.HashMap

class RequestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request)

        if(intent?.getIntExtra(Constants.KEY_ENGAGEMENT_ID,-1) != -1) {
            addRequests(intent?.getIntExtra(Constants.KEY_ENGAGEMENT_ID,0)!!)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if(intent?.getIntExtra(Constants.KEY_ENGAGEMENT_ID,-1) != -1) {
            addRequests(intent?.getIntExtra(Constants.KEY_ENGAGEMENT_ID,0)!!)
        }
    }

    fun addRequests(engagementId: Int) {
        if(vpRequests.adapter == null) {
            vpRequests.adapter = RequestPagerAdapter(supportFragmentManager).apply {
                addFrag(engagementId)
            }
        } else {
            (vpRequests.adapter as RequestPagerAdapter).addFrag(engagementId = engagementId)
        }
    }

    fun driverAcceptRideAsync(activity: Activity, customerInfo: CustomerInfo) {
        if (AppStatus.getInstance(applicationContext).isOnline(applicationContext)) {
            DialogPopup.showLoadingDialog(activity, resources.getString(R.string.loading))

//            if (myLocation != null) {
//                Data.latitude = myLocation.getLatitude()
//                Data.longitude = myLocation.getLongitude()
//            }

            val params = HashMap<String, String>()

            params[Constants.KEY_ACCESS_TOKEN] = Data.userData.accessToken
            params[Constants.KEY_CUSTOMER_ID] = customerInfo.getUserId().toString()

            if (Data.getAssignedCustomerInfosListForEngagedStatus() == null || Data.getAssignedCustomerInfosListForEngagedStatus()!!.size == 0) {
                Data.setCurrentEngagementId(customerInfo.getEngagementId().toString())
            }
            params[Constants.KEY_ENGAGEMENT_ID] = customerInfo.getEngagementId().toString()

            params[Constants.KEY_LATITUDE] = "" + Data.latitude
            params[Constants.KEY_LONGITUDE] = "" + Data.longitude

            params[Constants.KEY_DEVICE_NAME] = Utils.getDeviceName()
            params[Constants.KEY_IMEI] = DeviceUniqueID.getCachedUniqueId(this)
            params[Constants.KEY_APP_VERSION] = "" + Utils.getAppVersion(this)

            params[Constants.KEY_REFERENCE_ID] = customerInfo.getReferenceId().toString()
            params[Constants.KEY_IS_POOLED] = customerInfo.isPooled.toString()
            params[Constants.KEY_IS_DELIVERY] = customerInfo.isDelivery.toString()
            HomeUtil.putDefaultParams(params)

            if (customerInfo.isDeliveryPool == 1) {
                params[Constants.KEY_RIDE_TYPE] = "4"
            } else if (customerInfo.isDelivery == 1) {
                params[Constants.KEY_RIDE_TYPE] = "3"
            } else if (customerInfo.isPooled == 1) {
                params[Constants.KEY_RIDE_TYPE] = "2"
            } else {
                params[Constants.KEY_RIDE_TYPE] = "0"
            }

            Log.i("request", params.toString())

            GCMIntentService.cancelUploadPathAlarm(this@RequestActivity)
            RestClient.getApiServices().driverAcceptRideRetro(params, object : Callback<RegisterScreenResponse> {
                override fun success(registerScreenResponse: RegisterScreenResponse, response: Response) {
                    val jsonString = String((response.body as TypedByteArray).bytes)
                    Prefs.with(activity).save(SPLabels.ACCEPT_RIDE_TIME, System.currentTimeMillis().toString())
                    Prefs.with(activity).save(Constants.DRIVER_RIDE_EARNING, "")
                    Prefs.with(activity).save(Constants.DRIVER_RIDE_DATE, "")
                    acceptRideSucess(jsonString,
                            customerInfo.getEngagementId().toString(),
                            customerInfo.getUserId().toString())
                    GCMIntentService.stopRing(true, activity)

                    val customerEnfagementInfos1 = Data.getAssignedCustomerInfosListForEngagedStatus()

                    if (customerInfo.isPooled == 1) {
                        if (Database2.getInstance(this@RequestActivity).getPoolDiscountFlag(customerInfo.getEngagementId()) != 1) {
                            Database2.getInstance(this@RequestActivity).deletePoolDiscountFlag(customerInfo.getEngagementId())
                            Database2.getInstance(this@RequestActivity).insertPoolDiscountFlag(customerInfo.getEngagementId(), 0)
                        }
                        if (customerEnfagementInfos1!!.size > 1) {
                            for (i in customerEnfagementInfos1.indices) {
                                Database2.getInstance(this@RequestActivity).updatePoolDiscountFlag(customerEnfagementInfos1[i].getEngagementId(), 1)
                            }
                        }
                    }


                }

                override fun failure(error: RetrofitError) {
                    DialogPopup.dismissLoadingDialog()
                    callAndHandleStateRestoreAPI()
                }
            })


        } else {
            DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG)
        }
    }

    fun callAndHandleStateRestoreAPI() {
        GlobalScope.launch(Dispatchers.IO) {
            if (Data.userData != null) {
                runOnUiThread { DialogPopup.showLoadingDialog(this@RequestActivity, resources.getString(R.string.loading)) }
                val resp = JSONParser().getUserStatus(this@RequestActivity, Data.userData.accessToken)
                Log.i("currentUserStatus0", resp)
                if (resp.contains(Constants.SERVER_TIMEOUT)) {
                    val resp1 = JSONParser().getUserStatus(this@RequestActivity, Data.userData.accessToken)
                    Log.i("currentUserStatus1", resp)
                    if (resp1.contains(Constants.SERVER_TIMEOUT)) {
                        runOnUiThread {
                            DialogPopup.alertPopup(this@RequestActivity, "", Data.SERVER_NOT_RESOPNDING_MSG)

                            if (!Prefs.with(this@RequestActivity).getString(SPLabels.PERFECT_ACCEPT_RIDE_DATA, " ").contains(" ")) {
                                Data.setCurrentEngagementId(Prefs.with(this@RequestActivity).getString(SPLabels.PERFECT_ENGAGEMENT_ID, " "))
                                acceptRideSucess(Prefs.with(this@RequestActivity).getString(SPLabels.PERFECT_ACCEPT_RIDE_DATA, " "),
                                        Data.getCurrentEngagementId(),
                                        Prefs.with(this@RequestActivity).getString(SPLabels.PERFECT_CUSTOMER_ID, " "))
                            }
                        }
                    }
                }
            }
            launch(Dispatchers.Main) {
                DialogPopup.dismissLoadingDialog()
            }
        }
    }

    fun acceptRideSucess(jsonString: String, engagementId: String, customerId: String) {
        try {
            val jObj = JSONObject(jsonString)
            val flag = jObj.optInt(Constants.KEY_FLAG, ApiResponseFlags.RIDE_ACCEPTED.getOrdinal())
            if (!SplashNewActivity.checkIfTrivialAPIErrors(this, jObj, flag, null)) {
                if (ApiResponseFlags.RIDE_ACCEPTED.getOrdinal() == flag) {
                    Data.fareStructure = JSONParser.parseFareObject(jObj)
                    Data.fareStructure.fareFactor = jObj.optDouble(Constants.KEY_FARE_FACTOR, 1.0)
                    Data.fareStructure.luggageFare = jObj.optDouble(Constants.KEY_LUGGAGE_CHARGES, 0.0)
                    Data.fareStructure.convenienceCharge = jObj.optDouble(Constants.KEY_CONVENIENCE_CHARGE, 0.0)
                    Data.fareStructure.convenienceChargeWaiver = jObj.optDouble(Constants.KEY_CONVENIENCE_CHARGE_WAIVER, 0.0)
                    val referenceId = jObj.optInt(Constants.KEY_REFERENCE_ID, 0)

                    val isDelivery = jObj.optInt(Constants.KEY_IS_DELIVERY, 0)
                    var isDeliveryPool = 0
                    if (jObj.optInt(Constants.KEY_RIDE_TYPE, 0) == 4) {
                        isDeliveryPool = 1
                    }
                    val userData = jObj.optJSONObject(Constants.KEY_USER_DATA)
                    var userName = ""
                    var userImage = ""
                    var phoneNo = ""
                    var rating = ""
                    var address = ""
                    var vendorMessage = ""
                    var estimatedDriverFare = ""
                    var strRentalInfo = ""
                    var ForceEndDelivery = 0
                    var falseDeliveries = 0
                    var loadingStatus = 0
                    var jugnooBalance = 0.0
                    var pickupLatitude = 0.0
                    var pickupLongitude = 0.0
                    var estimatedFare = 0.0
                    var cashOnDelivery = 0.0
                    var currrentLatitude = 0.0
                    var currrentLongitude = 0.0
                    var totalDeliveries = 0
                    var orderId = 0
                    val customerOrderImages: JSONArray?
                    val customerOrderImagesList = java.util.ArrayList<String>()
                    if (isDelivery == 1) {
                        userName = userData.optString(Constants.KEY_NAME, "")
                        userImage = userData.optString(Constants.KEY_USER_IMAGE, "")
                        phoneNo = userData.optString(Constants.KEY_PHONE, "")
                        rating = userData.optString(Constants.KEY_USER_RATING, "4")
                        jugnooBalance = userData.optDouble(Constants.KEY_JUGNOO_BALANCE, 0.0)
                        pickupLatitude = userData.optDouble(Constants.KEY_LATITUDE, 0.0)
                        pickupLongitude = userData.optDouble(Constants.KEY_LONGITUDE, 0.0)
                        address = userData.optString(Constants.KEY_ADDRESS, "")
                        totalDeliveries = userData.optInt(Constants.KEY_TOTAL_DELIVERIES, 0)
                        estimatedFare = userData.optDouble(Constants.KEY_ESTIMATED_FARE, 0.0)
                        cashOnDelivery = userData.optDouble(Constants.KEY_TOTAL_CASH_TO_COLLECT_DELIVERY, 0.0)
                        vendorMessage = userData.optString(Constants.KEY_VENDOR_MESSAGE, "")
                        ForceEndDelivery = userData.optInt(Constants.KEY_END_DELIVERY_FORCED, 0)
                        estimatedDriverFare = userData.optString(Constants.KEY_ESTIMATED_DRIVER_FARE, "")
                        loadingStatus = userData.optInt(Constants.KEY_IS_LOADING, 0)
                        falseDeliveries = userData.optInt("false_deliveries", 0)
                        orderId = userData.optInt("order_id", 0)

                        try {
                            customerOrderImages = userData.optJSONArray(Constants.KEY_CUSTOMER_ORDER_IMAGES)
                            if (customerOrderImages != null && customerOrderImages.length() > 0) {
                                for (k in 0 until customerOrderImages.length()) {
                                    customerOrderImagesList.add(customerOrderImages.get(k) as String)
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    } else {
                        userName = userData.optString(Constants.KEY_USER_NAME, "")
                        userImage = userData.optString(Constants.KEY_USER_IMAGE, "")
                        phoneNo = userData.optString(Constants.KEY_PHONE_NO, "")
                        rating = userData.optString(Constants.KEY_USER_RATING, "4")
                        jugnooBalance = userData.optDouble(Constants.KEY_JUGNOO_BALANCE, 0.0)
                        pickupLatitude = jObj.optDouble(Constants.KEY_PICKUP_LATITUDE, 0.0)
                        pickupLongitude = jObj.optDouble(Constants.KEY_PICKUP_LONGITUDE, 0.0)
                        address = userData.getString(Constants.KEY_ADDRESS)
                        Log.e("address", address)
                        currrentLatitude = jObj.getDouble(Constants.KEY_CURRENT_LATITUDE)
                        currrentLongitude = jObj.getDouble(Constants.KEY_CURRENT_LONGITUDE)
                        val joRentalInfo = jObj.optJSONObject(Constants.KEY_RENTAL_INFO)
                        if (joRentalInfo != null) {
                            if (joRentalInfo.has(Constants.KEY_RENTAL_TIME) && joRentalInfo.optDouble(Constants.KEY_RENTAL_TIME, 0.0) != 0.0) {
                                val timeInMins = joRentalInfo.getDouble(Constants.KEY_RENTAL_TIME)
                                val time: String
                                if (timeInMins >= 60) {
                                    val hours = (timeInMins / 60).toInt()
                                    val minutes = timeInMins.toInt() % 60
                                    val strMins = if (minutes > 1) getString(R.string.rental_mins) + " " else getString(R.string.rental_min) + " "
                                    val strHours = if (hours > 1) getString(R.string.rental_hours) + " " else getString(R.string.rental_hour) + " "
                                    time = strRentalInfo + (Utils.getDecimalFormat().format(hours.toLong()) + " " + strHours + Utils.getDecimalFormat().format(minutes.toLong()) + " " + strMins + " | ")
                                } else {
                                    time = strRentalInfo + (joRentalInfo.getString(Constants.KEY_RENTAL_TIME) + " " + if (timeInMins <= 1) getString(R.string.rental_min) + " " else getString(R.string.rental_mins) + " | ")
                                }
                                strRentalInfo = strRentalInfo + time
                            }
                            if (joRentalInfo.has(Constants.KEY_RENTAL_DISTANCE) && joRentalInfo.optDouble(Constants.KEY_RENTAL_DISTANCE, 0.0) != 0.0) {
                                strRentalInfo = strRentalInfo + getString(R.string.rental_max) + " " + joRentalInfo.getString(Constants.KEY_RENTAL_DISTANCE) + " " + UserData.getDistanceUnit(this) + " | "
                            }
                            if (joRentalInfo.has(Constants.KEY_RENTAL_AMOUNT) && joRentalInfo.optDouble(Constants.KEY_RENTAL_AMOUNT, 0.0) != 0.0) {
                                strRentalInfo = strRentalInfo + Utils.formatCurrencyValue(Data.userData.currency, joRentalInfo.getDouble(Constants.KEY_RENTAL_AMOUNT))
                            }

                        }
                    }

                    val pickuplLatLng = LatLng(pickupLatitude, pickupLongitude)
                    val currentLatLng = LatLng(currrentLatitude, currrentLongitude)

                    val couponInfo = JSONParser.parseCouponInfo(jObj)
                    val promoInfo = JSONParser.parsePromoInfo(jObj)

                    val meterFareApplicable = jObj.optInt("meter_fare_applicable", 0)
                    val getJugnooFareEnabled = jObj.optInt("get_jugnoo_fare_enabled", 1)
                    val luggageChargesApplicable = jObj.optInt("luggage_charges_applicable", 0)
                    val waitingChargesApplicable = jObj.optInt("waiting_charges_applicable", 0)
                    Prefs.with(this@RequestActivity).save(SPLabels.CURRENT_ETA, System.currentTimeMillis() + jObj.optLong("eta", 0))
                    val cachedApiEnabled = jObj.optInt(Constants.KEY_CACHED_API_ENABLED, 0)
                    Prefs.with(this).save(SPLabels.CHAT_ENABLED, jObj.optInt("chat_enabled", 0))
                    val isPooled = jObj.optInt(Constants.KEY_IS_POOLED, 0)
                    val currency = jObj.optString(Constants.KEY_CURRENCY)
                    val tipAmount = jObj.optDouble(Constants.KEY_TIP_AMOUNT, 0.0)
                    val pickupTime = jObj.optString(Constants.KEY_PICKUP_TIME)
                    val isCorporateRide = jObj.optInt(Constants.KEY_IS_CORPORATE_RIDE, 0)
                    val customerNotes = jObj.optString(Constants.KEY_CUSTOMER_NOTE, "")
                    val tollApplicable = jObj.optInt(Constants.KEY_TOLL_APPLICABLE, 0)

                    Data.clearAssignedCustomerInfosListForStatus(EngagementStatus.REQUESTED.getOrdinal())

                    GCMIntentService.clearNotifications(applicationContext)

//                    initializeStartRideVariables()
                    Prefs.with(this).save(Constants.KEY_EMERGENCY_NO, jObj.optString(Constants.KEY_EMERGENCY_NO, getString(R.string.police_number)))

                    val customerInfo = CustomerInfo(this, Integer.parseInt(engagementId),
                            Integer.parseInt(customerId), referenceId, userName, phoneNo, pickuplLatLng, cachedApiEnabled,
                            userImage, rating, couponInfo, promoInfo, jugnooBalance, meterFareApplicable, getJugnooFareEnabled,
                            luggageChargesApplicable, waitingChargesApplicable, EngagementStatus.ACCEPTED.getOrdinal(), isPooled,
                            isDelivery, isDeliveryPool, address, totalDeliveries, estimatedFare, vendorMessage, cashOnDelivery,
                            currentLatLng, ForceEndDelivery, estimatedDriverFare, falseDeliveries, orderId, loadingStatus, currency, tipAmount, 0,
                            pickupTime, isCorporateRide, customerNotes, tollApplicable, strRentalInfo, customerOrderImagesList)

                    JSONParser.updateDropAddressLatlng(this@RequestActivity, jObj, customerInfo)

                    JSONParser.parsePoolOrReverseBidFare(jObj, customerInfo)

                    Data.addCustomerInfo(customerInfo)

//                    driverScreenMode = DriverScreenMode.D_ARRIVED
//                    switchDriverScreen(driverScreenMode)

//                    driverRequestListAdapter.setResults(Data.getAssignedCustomerInfosListForStatus(
//                            EngagementStatus.REQUESTED.getOrdinal()))
                } else {
                    try {
                        val message = JSONParser.getServerMessage(jObj)
                        DialogPopup.alertPopup(this, "", "" + message)
                        Handler().postDelayed({ DialogPopup.dismissAlertPopup() }, 3000)
                        reduceRideRequest(engagementId, EngagementStatus.REQUESTED.getOrdinal(), "")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }

                DialogPopup.dismissLoadingDialog()

            } else {
                reduceRideRequest(engagementId, EngagementStatus.REQUESTED.getOrdinal(), "")
            }
        } catch (exception: Exception) {
            exception.printStackTrace()
            DialogPopup.alertPopup(this@RequestActivity, "", Data.SERVER_ERROR_MSG)
            DialogPopup.dismissLoadingDialog()
        }

        Prefs.with(this@RequestActivity).save(SPLabels.PERFECT_ACCEPT_RIDE_DATA, " ")
        ApiAcceptRide().perfectRideVariables(this@RequestActivity, "", "", "", 0.0, 0.0)
        Prefs.with(this@RequestActivity).save(SPLabels.PERFECT_CUSTOMER_CONT, "")
        DialogPopup.dismissLoadingDialog()
    }

    fun reduceRideRequest(engagementId: String, status: Int, message: String) {
        Data.removeCustomerInfo(Integer.parseInt(engagementId), status)
        if (!message.equals("", ignoreCase = true)) {
            DialogPopup.alertPopup(this@RequestActivity, "", message)
        }
    }
}

class RequestPagerAdapter(var fragmentManager: FragmentManager) : FragmentStatePagerAdapter(fragmentManager) {

    val requestList by lazy { ArrayList<Int>() }

    override fun getItem(position: Int): Fragment {
        return BidRequestFragment.newInstance(requestList[position])
    }

    override fun getCount(): Int {
        return if(requestList.isNullOrEmpty()) 0 else requestList.size
    }

    fun addFrag(engagementId: Int) {
        if(!requestList.contains(engagementId)) {
            requestList.add(engagementId)
            notifyDataSetChanged()
        }
    }

}
