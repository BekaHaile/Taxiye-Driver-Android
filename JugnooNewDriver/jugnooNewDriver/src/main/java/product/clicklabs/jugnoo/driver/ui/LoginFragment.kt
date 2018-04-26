package product.clicklabs.jugnoo.driver.ui


import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import product.clicklabs.jugnoo.driver.Constants
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.datastructure.SPLabels
import product.clicklabs.jugnoo.driver.ui.api.APICommonCallback
import product.clicklabs.jugnoo.driver.ui.api.ApiCommon
import product.clicklabs.jugnoo.driver.ui.api.ApiName
import product.clicklabs.jugnoo.driver.ui.models.RegisterScreenResponse
import product.clicklabs.jugnoo.driver.utils.Prefs
import product.clicklabs.jugnoo.driver.utils.Utils
import retrofit.RetrofitError
import java.lang.Exception
import java.util.HashMap

class LoginFragment:Fragment() {




    var rootView:View? = null;
    private val tvCountryCode by bind<TextView>(R.id.tvCountryCode,rootView) ;
    private val edtPhoneNo by bind<TextView>(R.id.tvCountryCode,rootView) ;
    private val btnGenerateOtp by bind<TextView>(R.id.tvCountryCode,rootView) ;

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater?.inflate(R.layout.frag_login,container,false);


        btnGenerateOtp.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {

                val phoneNo:String = edtPhoneNo.text.trim().toString();
                val countryCode:String = tvCountryCode.text.trim().toString();
                if(phoneNo.length<0){
                    return;
                }

                if (TextUtils.isEmpty(countryCode)) {
                    Toast.makeText(this@LoginFragment.activity, getString(R.string.please_select_country_code), Toast.LENGTH_SHORT).show()
                    return ;
                }

                if(!Utils.validPhoneNumber(phoneNo)){
                    Toast.makeText(this@LoginFragment.activity, getString(R.string.enter_valid_phone_number), Toast.LENGTH_SHORT).show()
                    return ;

                }

                val params = HashMap<String, String>()
                params.put(Constants.KEY_PHONE_NO, countryCode + phoneNo)
                params.put(Constants.KEY_COUNTRY_CODE, countryCode)
                params.put(Constants.LOGIN_TYPE, "1")
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

                //hit for phone number


            }

        })


        return rootView;
    }




}
