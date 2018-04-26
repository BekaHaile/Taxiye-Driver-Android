package product.clicklabs.jugnoo.driver.ui


import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import okhttp3.internal.Util
import product.clicklabs.jugnoo.driver.Constants
import product.clicklabs.jugnoo.driver.LoginViaOTP
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.utils.Utils

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

                if(phoneNo.length<0){
                    return;
                }

                if (TextUtils.isEmpty(tvCountryCode.text.trim().toString())) {
                    Toast.makeText(this@LoginFragment.activity, getString(R.string.please_select_country_code), Toast.LENGTH_SHORT).show()
                    return ;
                }

                if(!Utils.validPhoneNumber(phoneNo)){
                    Toast.makeText(this@LoginFragment.activity, getString(R.string.enter_valid_phone_number), Toast.LENGTH_SHORT).show()
                    return ;

                }


                //hit for phone number


            }

        })


        return rootView;
    }




}
