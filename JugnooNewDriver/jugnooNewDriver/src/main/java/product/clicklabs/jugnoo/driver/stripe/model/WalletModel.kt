package product.clicklabs.jugnoo.driver.stripe.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import product.clicklabs.jugnoo.driver.ui.models.FeedCommonResponseKotlin
import product.clicklabs.jugnoo.driver.wallet.model.PaymentConfig

/**
 * Created by Parminder Saini on 20/07/18.
 */
class WalletModelResponse(
        @Expose @SerializedName("wallet_balance") val walletBalance: Double,
        @Expose @SerializedName("user_debt") val userDebt: Double,
        @Expose @SerializedName("currency") val currencyUnit: String,
        @Expose @SerializedName("payment_mode_config_data") val data: Array<PaymentConfig>,
        @Expose @SerializedName("stripe_cards") val stripeCards: List<StripeCardData>?,
        @Expose @SerializedName("quick_add_amounts") val quickAddAmounts: List<Double>?): FeedCommonResponseKotlin(){
        fun getBalance()=walletBalance-userDebt;
}