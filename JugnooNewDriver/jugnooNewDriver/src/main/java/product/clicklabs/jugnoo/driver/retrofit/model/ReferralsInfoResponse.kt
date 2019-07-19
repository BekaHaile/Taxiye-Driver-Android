package product.clicklabs.jugnoo.driver.retrofit.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import product.clicklabs.jugnoo.driver.ui.models.FeedCommonResponseKotlin

data class ReferralsInfoResponse constructor(@SerializedName("data") @Expose var list : ArrayList<ReferInfo>):FeedCommonResponseKotlin()

data class ReferInfo @JvmOverloads constructor(@SerializedName("name") @Expose var name : String? = null,
                                                           @SerializedName("status") @Expose var status: Int = 0,
                                                           @SerializedName("money") @Expose var money: Int = 0,
                                                           @SerializedName("credits") @Expose var credits: Int = 0,
                                                           @SerializedName("phone_no") @Expose var phoneNo: String? = null)