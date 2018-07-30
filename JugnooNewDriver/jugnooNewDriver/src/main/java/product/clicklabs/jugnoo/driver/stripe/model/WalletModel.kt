package product.clicklabs.jugnoo.driver.stripe.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import product.clicklabs.jugnoo.driver.retrofit.CurrencyModel
import product.clicklabs.jugnoo.driver.ui.models.FeedCommonResponseKotlin

/**
 * Created by Parminder Saini on 20/07/18.
 */
class WalletModelResponse(
    @Expose @SerializedName("wallet_balance") val walletBalance: Double,
    @Expose @SerializedName("currency") val currencyUnit: String,
    @Expose @SerializedName("stripe_cards") val stripeCards:List<StripeCardData>?,
    @Expose @SerializedName("quick_add_amounts") val quickAddAmounts:List<Double>?

): FeedCommonResponseKotlin()