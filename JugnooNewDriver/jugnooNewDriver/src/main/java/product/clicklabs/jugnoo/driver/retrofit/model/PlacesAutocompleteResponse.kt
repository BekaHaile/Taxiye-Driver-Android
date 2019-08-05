package product.clicklabs.jugnoo.driver.retrofit.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PlacesAutocompleteResponse {

    @SerializedName("predictions")
    @Expose
    var predictions: MutableList<Prediction>? = null
    @SerializedName("status")
    @Expose
    var status: String? = null

}
class Prediction {

    @SerializedName("description")
    @Expose
    var description: String? = null
    @SerializedName("place_id")
    @Expose
    var placeId: String? = null
    @SerializedName("structured_formatting")
    @Expose
    var structuredFormatting: StructuredFormatting? = null

}
class StructuredFormatting {

    @SerializedName("main_text")
    @Expose
    var mainText: String? = null
    @SerializedName("secondary_text")
    @Expose
    var secondaryText: String? = null

}