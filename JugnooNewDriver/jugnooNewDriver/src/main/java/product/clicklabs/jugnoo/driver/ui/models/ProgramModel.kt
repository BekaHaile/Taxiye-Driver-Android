package product.clicklabs.jugnoo.driver.ui.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Mohit Kr. Dhiman on 27/10/18.
 */
class ProgramModel(
    @Expose @SerializedName("data") val programs:List<Data>?): FeedCommonResponseKotlin(){

    class Data(@Expose @SerializedName("prog_id") val id: String,
               @Expose @SerializedName("start_date") val startDate: String,
               @Expose @SerializedName("end_date") val endDate: String,
               @Expose @SerializedName("start_time") val startTime: String,
               @Expose @SerializedName("end_time") val endTime: String,
               @Expose @SerializedName("prog_name") val name: String,
               @Expose @SerializedName("description") val description: String,
               @Expose @SerializedName("rides_completed") val completedRides: Int,
               @Expose @SerializedName("incentive") val threshold: ArrayList<Thresholds>,
               @Expose @SerializedName("total_rides") val totalRides: Int,
               @Expose @SerializedName("status") val programType: Int)

    class Thresholds(@Expose @SerializedName("rides") val value: Int,
                     @Expose @SerializedName("amount") val prize: Double)
}
