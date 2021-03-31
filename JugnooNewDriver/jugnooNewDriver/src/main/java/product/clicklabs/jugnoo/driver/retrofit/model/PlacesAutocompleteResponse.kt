package product.clicklabs.jugnoo.driver.retrofit.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PlacesAutocompleteResponse {

    @SerializedName(value = "predictions", alternate = ["data"])
    @Expose
    var predictions: MutableList<Prediction>? = null
    @SerializedName("status")
    @Expose
    var status: String? = null

}
class Prediction {

    @SerializedName(value = "description", alternate = ["address"])
    @Expose
    var description: String? = null
    @SerializedName("place_id")
    @Expose
    var placeId: String? = null
    @SerializedName("lat")
    @Expose
    var lat: Double? = null
    @SerializedName("lng")
    @Expose
    var lng: Double? = null

}