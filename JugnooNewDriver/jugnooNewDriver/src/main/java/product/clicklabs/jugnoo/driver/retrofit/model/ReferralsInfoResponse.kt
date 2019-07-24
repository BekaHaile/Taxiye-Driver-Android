package product.clicklabs.jugnoo.driver.retrofit.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import product.clicklabs.jugnoo.driver.ui.models.FeedCommonResponseKotlin

data class ReferralsInfoResponse constructor(@SerializedName("data") @Expose var list : ArrayList<ReferInfo>):FeedCommonResponseKotlin()

data class ReferInfo @JvmOverloads constructor(@SerializedName("name") @Expose var name : String? = null,
                                                           @SerializedName("status") @Expose var status: Int = 0,
                                                           @SerializedName("total_money") @Expose var totalMoney: Int = 0,
                                                           @SerializedName("total_credits") @Expose var totalCredits: Int = 0,
                                                           @SerializedName("processed_money") @Expose var processedMoney: Int = 0,
                                                           @SerializedName("processed_credits") @Expose var processedCredits: Int = 0,
                                                           @SerializedName("user_num_rides") @Expose var userNumRides: Int = 0,
                                                           @SerializedName("credits_next_target") @Expose var creditsNextTarget: Int = 0,
                                                           @SerializedName("money_next_target") @Expose var moneyNextTarget: Int = 0,
                                                           @SerializedName("num_rides_next_target") @Expose var numOfRidesNextTarget: Int = 0,
                                                           @SerializedName("phone_no") @Expose var phoneNo: String? = null,
                                                           var taskMessage: String? = null)