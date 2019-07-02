package product.clicklabs.jugnoo.driver.retrofit.model.drivertaks

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Awesome Pojo Generator
 */
class Tasks {
    @SerializedName("task_type")
    @Expose
    var taskType: Int = 0
    @SerializedName("advertise_credits")
    @Expose
    var advertiseCredits: Double = 0.toDouble()
    @SerializedName("start_time")
    @Expose
    var startTime: String? = null
    @SerializedName("end_time")
    @Expose
    var endTime: String? = null
    @SerializedName("show_text")
    @Expose
    var message: String? = null
}