package product.clicklabs.jugnoo.driver.google

import android.text.TextUtils
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.util.*

class GAPIAddress(var searchableAddress: String) {

    override fun toString(): String {
        return searchableAddress
    }
}
class GoogleGeocodeResponse {


    @SerializedName("results")
    var results: List<Results>? = null
    @SerializedName("error_message")
    val errorMessage: String? = null
    @SerializedName("status")
    var status: String? = null



}
class AddressComponent {

    @SerializedName("long_name")
    @Expose
    var longName: String? = null
    @SerializedName("types")
    @Expose
    var types: List<String> = ArrayList()
    var redundant: Boolean = false


}

class Results {

    @SerializedName("formatted_address")
    var formatted_address: String? = null

    @SerializedName("address_components")
    var addressComponents: List<AddressComponent> = ArrayList()
    @SerializedName("place_id")
    val placeId: String? = null
    @SerializedName("types")
    @Expose
    val types: List<String> = ArrayList()
    var placeName: String? = null

    /**
     * Index
     * 0=locality
     * 1=city
     * 2=state
     * 3=country
     */

    //gives country
    //gives state
    //gives city
    //gives locality
    val address: Array<String?>
        get() {
            val address = TextUtils.split(this.formatted_address, ",")
            val addressSplit = arrayOfNulls<String>(4)
            val length = address.size
            addressSplit[3] = address[length - 1]
            if (length - 2 >= 0)
                addressSplit[2] = address[length - 2]
            else {
                addressSplit[2] = address[length - 1]
            }
            if (length - 3 >= 0)
                addressSplit[1] = address[length - 3]
            else if (length - 2 >= 0) {
                addressSplit[1] = address[length - 2]
            } else {
                addressSplit[1] = address[length - 1]
            }
            if (length - 4 >= 0)
                addressSplit[0] = address[length - 4]
            else if (length - 3 >= 0) {
                addressSplit[0] = address[length - 3]
            } else if (length - 2 >= 0) {
                addressSplit[0] = address[length - 2]
            } else {
                addressSplit[0] = address[length - 1]
            }

            return addressSplit
        }

    //                    if (addressTypes.contains("sublocality_level_2")) {
    //                        city = addressComponents.get(i).longName;
    //                    }
    val addAddress: String?
        get() {
            val neighborhood = ""
            var city = ""
            var locality: String? = ""
            var locality1: String? = ""
            var sublocality: String? = ""
            var state: String? = ""
            if (addressComponents.size > 0) {
                for (i in addressComponents.indices) {
                    val addressTypes = ArrayList<String>()
                    for (j in addressComponents[i].types.indices) {
                        addressTypes.add(addressComponents[i].types[j])
                    }
                    if (addressTypes.contains("sublocality_level_1")) {
                        locality = addressComponents[i].longName
                    }
                    if (addressTypes.contains("locality")) {
                        state = addressComponents[i].longName
                    }
                    if (addressTypes.contains("administrative_area_level_2")) {
                        locality1 = addressComponents[i].longName
                    }
                    if (addressTypes.contains("administrative_area_level_1")) {
                        sublocality = addressComponents[i].longName
                    }

                }
                return if (!TextUtils.isEmpty(city)) {
                    city
                } else if (!TextUtils.isEmpty(locality)) {
                    locality
                } else if (!TextUtils.isEmpty(state)) {
                    state
                } else if (!TextUtils.isEmpty(locality1)) {
                    locality1
                } else if (!TextUtils.isEmpty(sublocality)) {
                    sublocality
                } else {
                    ""
                }
            } else {
                val address = address
                city = address[address.size - 3]!!.trim { it <= ' ' }
            }
            return city
        }

    //                    if (addressTypes.contains("neighborhood")) {
    //                        neighborhood = addressComponents.get(i).longName;
    //                    }
    val locality: String?
        get() {
            val neighborhood = ""
            var city: String? = ""
            var locality: String? = ""
            var sublocality: String? = ""
            var state: String? = ""
            if (addressComponents.size > 0) {
                for (i in addressComponents.indices) {
                    val addressTypes = ArrayList<String>()
                    for (j in addressComponents[i].types.indices) {
                        addressTypes.add(addressComponents[i].types[j])
                    }
                    if (addressTypes.contains("locality")) {
                        city = addressComponents[i].longName
                    }
                    if (addressTypes.contains("sublocality")) {
                        locality = addressComponents[i].longName
                    }
                    if (addressTypes.contains("administrative_area_level_2")) {
                        state = addressComponents[i].longName
                    }
                    if (addressTypes.contains("administrative_area_level_1")) {
                        sublocality = addressComponents[i].longName
                    }

                }
                return if (!TextUtils.isEmpty(neighborhood)) {
                    neighborhood
                } else if (!TextUtils.isEmpty(locality)) {
                    locality
                } else if (!TextUtils.isEmpty(city)) {
                    city
                } else if (!TextUtils.isEmpty(state)) {
                    state
                } else if (!TextUtils.isEmpty(sublocality)) {
                    sublocality
                } else {
                    ""
                }
            } else {
                val address = address
                city = address[address.size - 3]!!.trim { it <= ' ' }
            }
            return city
        }

    val pin: String?
        get() {
            var pin: String? = ""
            if (addressComponents.size > 0) {
                for (i in addressComponents.indices) {
                    val addressTypes = ArrayList<String>()
                    for (j in addressComponents[i].types.indices) {
                        addressTypes.add(addressComponents[i].types[j])
                    }
                    if (addressTypes.contains("postal_code")) {
                        pin = addressComponents[i].longName
                    }
                }

            }

            return pin
        }

    val city: String?
        get() {
            var city: String? = ""
            var locality: String? = ""
            var state: String? = ""
            if (addressComponents.size > 0) {
                for (i in addressComponents.indices) {
                    val addressTypes = ArrayList<String>()
                    for (j in addressComponents[i].types.indices) {
                        addressTypes.add(addressComponents[i].types[j])
                    }
                    if (addressTypes.contains("locality")) {
                        city = addressComponents[i].longName
                    }
                    if (addressTypes.contains("sublocality")) {
                        locality = addressComponents[i].longName
                    }
                    if (addressTypes.contains("administrative_area_level_2")) {
                        state = addressComponents[i].longName
                    }

                }
                return if (!TextUtils.isEmpty(city)) {
                    city
                } else if (!TextUtils.isEmpty(state)) {
                    state
                } else if (!TextUtils.isEmpty(locality)) {
                    locality
                } else {
                    ""
                }
            } else {
                val address = address
                city = address[address.size - 3]!!.trim { it <= ' ' }
            }
            return city
        }

    val streetNumber: String
        get() {
            var streetNumber: String? = ""
            if (addressComponents.size > 0) {
                for (i in addressComponents.indices) {
                    val addressTypes = ArrayList<String>()
                    for (j in addressComponents[i].types.indices) {
                        addressTypes.add(addressComponents[i].types[j])
                    }
                    if (addressTypes.contains("street_number")) {
                        streetNumber = addressComponents[i].longName
                    }


                }
                return if (!TextUtils.isEmpty(streetNumber)) {
                    streetNumber!! + ""
                } else {
                    ""
                }
            } else {
                val address = TextUtils.split(this.formatted_address, ",")
                streetNumber = address[0].trim { it <= ' ' } + ""
            }
            return streetNumber
        }

    val route: String
        get() {
            var route: String? = ""
            if (addressComponents.size > 0) {
                for (i in addressComponents.indices) {
                    val addressTypes = ArrayList<String>()
                    for (j in addressComponents[i].types.indices) {
                        addressTypes.add(addressComponents[i].types[j])
                    }
                    if (addressTypes.contains("route")) {
                        route = addressComponents[i].longName
                    }


                }
                return if (!TextUtils.isEmpty(route)) {
                    route!! + ""
                } else {
                    ""
                }
            } else {
                val address = TextUtils.split(this.formatted_address, ",")
                route = address[1].trim { it <= ' ' } + ""
            }
            return route
        }

    val state: String?
        get() {
            var city: String? = ""
            var locality: String? = ""
            var sublocality: String? = ""
            var state: String? = ""
            if (addressComponents.size > 0) {
                for (i in addressComponents.indices) {
                    val addressTypes = ArrayList<String>()
                    for (j in addressComponents[i].types.indices) {
                        addressTypes.add(addressComponents[i].types[j])
                    }
                    if (addressTypes.contains("locality")) {
                        city = addressComponents[i].longName
                    }
                    if (addressTypes.contains("sublocality")) {
                        locality = addressComponents[i].longName
                    }
                    if (addressTypes.contains("administrative_area_level_2")) {
                        state = addressComponents[i].longName
                    }
                    if (addressTypes.contains("administrative_area_level_1")) {
                        sublocality = addressComponents[i].longName
                    }

                }
                return if (!TextUtils.isEmpty(city)) {
                    city
                } else if (!TextUtils.isEmpty(locality)) {
                    locality
                } else if (!TextUtils.isEmpty(state)) {
                    state
                } else if (!TextUtils.isEmpty(sublocality)) {
                    sublocality
                } else {
                    ""
                }
            } else {
                val address = address
                city = address[address.size - 3]!!.trim { it <= ' ' }
            }
            return city
        }

    val country: String?
        get() {
            var country: String? = ""
            var political: String? = ""
            if (addressComponents.size > 0) {
                for (i in addressComponents.indices) {
                    val addressTypes = ArrayList<String>()
                    for (j in addressComponents[i].types.indices) {
                        addressTypes.add(addressComponents[i].types[j])
                    }
                    if (addressTypes.contains("country")) {
                        country = addressComponents[i].longName
                    }
                    if (addressTypes.contains("political")) {
                        political = addressComponents[i].longName
                    }
                }
                return if (!TextUtils.isEmpty(country)) {
                    country
                } else if (!TextUtils.isEmpty(political)) {
                    political
                } else {
                    ""
                }
            } else {
                val address = address
                country = address[address.size - 1]!!.trim { it <= ' ' }
            }
            return country
        }
}