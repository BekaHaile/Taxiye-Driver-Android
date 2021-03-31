package product.clicklabs.jugnoo.driver.retrofit.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PlaceDetailsResponse {

    @SerializedName("results")
    @Expose
    var results: MutableList<Result>? = null

}
class Result {
    @SerializedName("geometry")
    @Expose
    var geometry: Geometry? = null

}
class Geometry {

    @SerializedName("location")
    @Expose
    var location: Location? = null

}
class Location {

    @SerializedName("lat")
    @Expose
    var lat: Double? = null
    @SerializedName("lng")
    @Expose
    var lng: Double? = null

}

class PlaceDetailsResponseGoogle {

    @SerializedName("result")
    @Expose
    var result: Result? = null

}