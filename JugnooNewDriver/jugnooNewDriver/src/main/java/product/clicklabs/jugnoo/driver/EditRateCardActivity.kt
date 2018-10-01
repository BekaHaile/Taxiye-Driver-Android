package product.clicklabs.jugnoo.driver

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_edit_rate_card.*
import product.clicklabs.jugnoo.driver.retrofit.model.RateCardResponse
import product.clicklabs.jugnoo.driver.ui.api.*
import product.clicklabs.jugnoo.driver.ui.models.FeedCommonResponseKotlin
import product.clicklabs.jugnoo.driver.utils.BaseFragmentActivity
import product.clicklabs.jugnoo.driver.utils.Utils
import product.clicklabs.jugnoo.driver.utils.gone
import product.clicklabs.jugnoo.driver.utils.visible

class EditRateCardActivity : BaseFragmentActivity(){

    private var rateCardResponse : RateCardResponse? = null;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_rate_card)

        findViewById<TextView>(R.id.title).text = getString(R.string.rate_card)
        findViewById<ImageView>(R.id.backBtn).setOnClickListener{
            onBackPressed()
        }

        bSave.setOnClickListener {
            updateFares(etBaseFare.text.toString().toDouble(), etDistanceFare.text.toString().toDouble(),
                    etTimeFare.text.toString().toDouble())
        }

        ivEdit.setOnClickListener{
            ivEdit.gone()
            bSave.visible()
            etBaseFare.isEnabled = true
            etDistanceFare.isEnabled = true
            etTimeFare.isEnabled = true
            if(rateCardResponse != null && rateCardResponse?.rates != null) {
                etBaseFare.setText(Utils.currencyPrecision(rateCardResponse?.rates!!.baseFare).toString())
                etDistanceFare.setText(Utils.currencyPrecision(rateCardResponse?.rates!!.farePerKm).toString())
                etTimeFare.setText(Utils.currencyPrecision(rateCardResponse?.rates!!.farePerMin).toString())
            }
            etBaseFare.requestLayout()
            etBaseFare.setSelection(etBaseFare.text.length)
            Utils.showSoftKeyboard(this@EditRateCardActivity, etBaseFare)
        }

        ivEdit.visible()
        bSave.gone()
        etBaseFare.isEnabled = false
        etDistanceFare.isEnabled = false
        etTimeFare.isEnabled = false

        etBaseFare.setOnEditorActionListener { _, _, _ ->
            etDistanceFare.requestFocus()
            etDistanceFare.setSelection(etDistanceFare.text.length)
            false
        }
        etDistanceFare.setOnEditorActionListener { _, _, _ ->
            etTimeFare.requestFocus()
            etTimeFare.setSelection(etTimeFare.text.length)
            false
        }
        etTimeFare.setOnEditorActionListener { _, _, _ ->
            bSave.performClick()
            false
        }


        fetchRateCard()

    }

    override fun onBackPressed() {
        finish()
        overridePendingTransition(R.anim.left_in, R.anim.left_out)
    }

    private fun fetchRateCard(){
        if(Data.userData == null){
            return
        }
        val params = HashMap<String, String>()
        params[Constants.KEY_ACCESS_TOKEN] = Data.userData.accessToken
        ApiCommon<RateCardResponse>(this).execute(params, ApiName.SHOW_RATE_CARD, object : APICommonCallback<RateCardResponse>(){
            override fun onSuccess(t: RateCardResponse?, message: String?, flag: Int) {
                if(t == null){
                    return
                }
                rateCardResponse = t
                tvDistanceFare.text = getString(R.string.distance) + " " + getString(R.string.per_format, Utils.getDistanceUnit(t.rates.distanceUnit))
                etBaseFare.setText(Utils.formatCurrencyValue(t.rates.currencyUnit, t.rates.baseFare, false))
                etDistanceFare.setText(Utils.formatCurrencyValue(t.rates.currencyUnit, t.rates.farePerKm, false))
                etTimeFare.setText(Utils.formatCurrencyValue(t.rates.currencyUnit, t.rates.farePerMin, false))
            }

            override fun onError(t: RateCardResponse?, message: String?, flag: Int): Boolean {
                return false
            }
        })
    }

    private fun updateFares(fareFixed: Double, farePerKm: Double, farePerMin: Double){
        if(Data.userData == null){
            return
        }
        val params = HashMap<String, String>()
        params[Constants.KEY_ACCESS_TOKEN] = Data.userData.accessToken
        params[Constants.KEY_FARE_FIXED] = fareFixed.toString()
        params[Constants.KEY_FARE_PER_KM] = farePerKm.toString()
        params[Constants.KEY_FARE_PER_MIN] = farePerMin.toString()
        ApiCommonKt<FeedCommonResponseKotlin>(this).execute(params, ApiName.UPDATE_FARES, object : APICommonCallbackKotlin<FeedCommonResponseKotlin>(){
            override fun onSuccess(t: FeedCommonResponseKotlin, message: String?, flag: Int) {

                Utils.showToast(this@EditRateCardActivity, t.message)
                onBackPressed()
            }

            override fun onError(t: FeedCommonResponseKotlin?, message: String?, flag: Int): Boolean {
                return false
            }
        })
    }

}