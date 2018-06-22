package product.clicklabs.jugnoo.driver.ui.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Parminder Saini on 26/03/17.
 */

 open class FeedCommonResponseKotlin {
    @Expose
    @SerializedName("flag")
    var flag:Int = 0
    @Expose
    @SerializedName("message")
    val message: String = ""
    @Expose
    @SerializedName("error")
    val error: String = ""

    fun serverMessage() = if(message == null || message.isBlank()) error ?: "" else message
}