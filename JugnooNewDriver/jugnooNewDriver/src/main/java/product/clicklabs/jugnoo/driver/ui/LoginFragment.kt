package product.clicklabs.jugnoo.driver.ui


import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.picker.Country
import com.picker.CountryPicker
import com.picker.OnCountryPickerListener
import product.clicklabs.jugnoo.driver.Constants
import product.clicklabs.jugnoo.driver.Data
import product.clicklabs.jugnoo.driver.HomeUtil
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags
import product.clicklabs.jugnoo.driver.datastructure.SPLabels
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse
import product.clicklabs.jugnoo.driver.ui.api.*
import product.clicklabs.jugnoo.driver.ui.models.DriverLanguageResponse
import product.clicklabs.jugnoo.driver.utils.DialogPopup
import product.clicklabs.jugnoo.driver.utils.Log
import product.clicklabs.jugnoo.driver.utils.Prefs
import product.clicklabs.jugnoo.driver.utils.Utils
import retrofit.RetrofitError
import java.lang.Exception
import java.util.HashMap

class LoginFragment:Fragment() {



    val TAG = LoginFragment::class.simpleName;
    lateinit var rootView:View
    lateinit var spinner:Spinner
    lateinit var selectedLanguage:String


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater?.inflate(R.layout.frag_login,container,false);
        selectedLanguage = (activity as DriverSplashActivity).selectedLanguage;
        spinner  = rootView?.findViewById(R.id.language_spinner) as Spinner
        val tvCountryCode by bind<TextView>(R.id.tvCountryCode,rootView)
        val edtPhoneNo by bind<EditText>(R.id.edt_phone,rootView) ;
        val btnGenerateOtp by bind<Button>(R.id.btn_gen_otp,rootView) ;

        val countryPicker = CountryPicker.Builder().with(activity).listener(object: OnCountryPickerListener{
                    override fun onSelectCountry(country: Country?) {
                        tvCountryCode.setText(country?.dialCode) ;
                    }
                }).build()

        tvCountryCode.setOnClickListener({ countryPicker.showDialog(activity.supportFragmentManager)})
        btnGenerateOtp.setOnClickListener(View.OnClickListener {
            val phoneNo:String = edtPhoneNo.text.trim().toString();
            val countryCode:String = tvCountryCode.text.trim().toString();
            if(phoneNo.length<0){
                return@OnClickListener;
            }

            if (TextUtils.isEmpty(countryCode)) {
                Toast.makeText(this@LoginFragment.activity, getString(R.string.please_select_country_code), Toast.LENGTH_SHORT).show()
                return@OnClickListener;
            }

            if(!Utils.validPhoneNumber(phoneNo)){
                Toast.makeText(this@LoginFragment.activity, getString(R.string.enter_valid_phone_number), Toast.LENGTH_SHORT).show()
                return@OnClickListener;

            }


            val params = HashMap<String, String>()
            params.put(Constants.KEY_PHONE_NO, countryCode + phoneNo)
            params.put(Constants.KEY_COUNTRY_CODE, countryCode)
            params.put(Constants.LOGIN_TYPE, "1")
            params[Constants.KEY_COUNTRY_CODE] = countryCode
            params["device_token"] = Data.deviceToken
            params["unique_device_id"] = Data.uniqueDeviceId
            params["device_name"] = Data.deviceName
            params["device_type"] = Data.DEVICE_TYPE
            params["app_version"] = "" + Data.appVersion
            params["os_version"] = Data.osVersion
            params["latitude"] = "" + Data.latitude
            params["longitude"] = "" + Data.longitude
            params["login_type"] = Data.LOGIN_TYPE
            if (Utils.isDeviceRooted()) {
                params["device_rooted"] = "1"
            } else {
                params["device_rooted"] = "0"
            }



            Prefs.with(activity).save(SPLabels.DRIVER_LOGIN_PHONE_NUMBER, phoneNo)
            Prefs.with(activity).save(SPLabels.DRIVER_LOGIN_TIME, System.currentTimeMillis())
            ApiCommon<RegisterScreenResponse>(activity).execute(params, ApiName.GENERATE_OTP,object : APICommonCallback<RegisterScreenResponse>(){
                override fun onNotConnected(): Boolean {
                    return false;
                }

                override fun onException(e: Exception?): Boolean {
                    return false;
                }

                override fun onSuccess(t: RegisterScreenResponse?, message: String?, flag: Int) {
                    if(flag==ApiResponseFlags.ACTION_COMPLETE.getOrdinal()){
                        (activity as DriverSplashActivity).addLoginViaOTPScreen(phoneNo,countryCode,t?.missedCallNumber);
                    }else{
                        DialogPopup.alertPopup(activity,"",message);
                    }


                }

                override fun onError(t: RegisterScreenResponse?, message: String?, flag: Int): Boolean {

                    return false;
                }

                override fun onFailure(error: RetrofitError?): Boolean {
                    return false;
                }

                override fun onDialogClick() {

                }


            }
            )


        })

        getLanguageList();

        return rootView;
    }

    private fun getLanguageList() {
        val params = HashMap<String, String>()
        params["device_model_name"] = android.os.Build.MODEL
        params["android_version"] = android.os.Build.VERSION.RELEASE
        HomeUtil.putDefaultParams(params)

        ApiCommonKotlin<DriverLanguageResponse>(activity).execute(params,ApiName.GET_LANGUAGES,object : APICommonCallbackKotlin<DriverLanguageResponse>(){
            override fun onSuccess(t: DriverLanguageResponse?, message: String?, flag: Int) {


                if(t?.languageList!=null && t.languageList.size>0){
                 val dataAdapter: ArrayAdapter<String> = ArrayAdapter(activity,android.R.layout.simple_spinner_item,t.languageList)
                    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinner.setAdapter(dataAdapter)
                    spinner.visibility = View.VISIBLE;

                    if(!t.languageList.contains(selectedLanguage)){
                        t.languageList.add(selectedLanguage);
                    }
                    spinner.setSelection(t.languageList.indexOf(selectedLanguage))
                    spinner.setOnItemSelectedListener(object: AdapterView.OnItemSelectedListener{
                        override fun onNothingSelected(parent: AdapterView<*>?) {

                        }

                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                            val item = parent?.getItemAtPosition(position).toString();
                            if(!item.equals(selectedLanguage,ignoreCase = true))
                            {
                                (activity as DriverSplashActivity).updateLanguage(selectedLanguage);
                            }
                        }

                    })


                }else{
                    spinner.visibility = View.GONE;
                }


            }

            override fun onError(t: DriverLanguageResponse?, message: String?, flag: Int): Boolean {
                return false;
            }

        })
    }


}
