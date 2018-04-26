package product.clicklabs.jugnoo.driver.ui

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import product.clicklabs.jugnoo.driver.IncomingSmsReceiver
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.ui.api.ApiCommon
import product.clicklabs.jugnoo.driver.ui.models.FeedCommonResponse
import product.clicklabs.jugnoo.driver.utils.Utils

class OTPConfirmFragment:Fragment() {

    private var phoneNumber:String? = null ;
    private var countryCode:String? = null ;



    companion object {

        const val KEY_PHONE_NUMBER = "phone_number";
        const val KEY_COUNTRY_CODE = "country_code";

        fun newInstance(phoneNumber: String,countryCode: String): SplashFragment{
            val splashFragment = SplashFragment();
            val bundle= Bundle()
            bundle.putString(KEY_PHONE_NUMBER,phoneNumber);
            bundle.putString(KEY_COUNTRY_CODE,countryCode);
            splashFragment.arguments=bundle;
            return splashFragment;
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        phoneNumber = arguments.getString(KEY_PHONE_NUMBER)
        countryCode = arguments.getString(KEY_COUNTRY_CODE)
    }






    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val rootView = inflater?.inflate(R.layout.frag_otp_confirm,container,false);
        ApiCommon<FeedCommonResponse>(activity).execute()
        return super.onCreateView(inflater, container, savedInstanceState)

    }
}
