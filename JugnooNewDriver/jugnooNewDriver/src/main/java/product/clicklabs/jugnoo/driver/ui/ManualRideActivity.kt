package product.clicklabs.jugnoo.driver.ui

import android.os.Bundle
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.picker.Country
import com.picker.CountryPicker
import com.picker.OnCountryPickerListener
import kotlinx.android.synthetic.main.activity_manual_ride.*
import kotlinx.android.synthetic.main.activity_toolbar.*
import kotlinx.android.synthetic.main.activity_toolbar.view.*
import product.clicklabs.jugnoo.driver.Constants
import product.clicklabs.jugnoo.driver.Data
import product.clicklabs.jugnoo.driver.HomeUtil
import product.clicklabs.jugnoo.driver.R
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags
import product.clicklabs.jugnoo.driver.ui.api.APICommonCallbackKotlin
import product.clicklabs.jugnoo.driver.ui.api.ApiCommonKt
import product.clicklabs.jugnoo.driver.ui.api.ApiName
import product.clicklabs.jugnoo.driver.ui.models.ManualRideResponse
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity
import product.clicklabs.jugnoo.driver.utils.DialogPopup
import product.clicklabs.jugnoo.driver.utils.Fonts
import product.clicklabs.jugnoo.driver.utils.Utils
import java.util.*

/**
 * Created by Parminder Saini on 13/06/18.
 */
class ManualRideActivity: BaseFragmentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual_ride)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayShowTitleEnabled(false)
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.ic_back_selector)
        }
        toolbar.tvToolbar.text = resources.getString(R.string.title_manual_ride)
        val countryPicker = CountryPicker.Builder().with(this).listener(object : OnCountryPickerListener<Country>{
            override fun onSelectCountry(country: Country?) {
                tvCountryCode.text = country?.dialCode
            }

        }).build()
        tvCountryCode.text = Utils.getCountryCode(this)
        tvCountryCode.setOnClickListener({ countryPicker.showDialog(supportFragmentManager) })
        toolbar.tvToolbar.typeface = Fonts.mavenMedium(this)
        tvLabel.typeface = Fonts.mavenMedium(this)
        tvCountryCode.typeface = Fonts.mavenRegular(this)
        tvCountryCode.typeface = Fonts.mavenRegular(this)
        btnRequestManualRide.typeface = Fonts.mavenMedium(this)
        labelDescriptionManualRide.typeface = Fonts.mavenMedium(this)
        btnRequestManualRide.setOnClickListener(View.OnClickListener {
            val phoneNo: String = edtPhoneNo.text.trim().toString()
            val countryCode: String = tvCountryCode.text.trim().toString()
            if (phoneNo.length < 0) {
                return@OnClickListener
            }

            if (TextUtils.isEmpty(countryCode)) {
                Toast.makeText(this@ManualRideActivity, getString(R.string.please_select_country_code), Toast.LENGTH_SHORT).show()
                return@OnClickListener
            }

            if (!Utils.validPhoneNumber(phoneNo)) {
                Toast.makeText(this@ManualRideActivity, getString(R.string.enter_valid_phone_number), Toast.LENGTH_SHORT).show()
                return@OnClickListener

            }

            requestManualRide(countryCode,phoneNo);

        })

        if (Data.userData.walletBalance <= Data.userData.minDriverBalance) {
            DialogPopup.alertPopupWithListener(this@ManualRideActivity, getString(R.string.failed),getString(R.string.low_balance_for_manual)) {
                this@ManualRideActivity.finish()
            }
        }
    }

    private fun requestManualRide( countryCode:String, phoneNo:String) {
        val params = HashMap<String, String>()
        params[Constants.KEY_ACCESS_TOKEN] = Data.userData.accessToken;
        HomeUtil.putDefaultParams(params)
        params[Constants.KEY_PHONE_NO] = countryCode + phoneNo
        params[Constants.KEY_COUNTRY_CODE] = countryCode
        ApiCommonKt<ManualRideResponse>(this,showLoader = true,checkForActionComplete = false)
                .execute(params,ApiName.MANUAL_RIDE,object: APICommonCallbackKotlin<ManualRideResponse>(){
                    override fun onSuccess(t: ManualRideResponse?, message: String?, flag: Int) {
                        if(flag==ApiResponseFlags.ASSIGNING_DRIVERS.getOrdinal()){
                            DialogPopup.alertPopupWithListener(this@ManualRideActivity, getString(R.string.ride_assigned_success), message)
                            { this@ManualRideActivity.finish() }
                        }else{
                            DialogPopup.alertPopup(this@ManualRideActivity,"",message)
                        }


                    }

                    override fun onError(t: ManualRideResponse?, message: String?, flag: Int): Boolean {
                        return false;
                    }

                })
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {

        if (item?.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

}