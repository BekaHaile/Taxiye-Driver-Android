package product.clicklabs.jugnoo.driver.retrofit

import product.clicklabs.jugnoo.driver.Constants
import product.clicklabs.jugnoo.driver.apis.InsertAutocomplete
import product.clicklabs.jugnoo.driver.apis.InsertPlaceDetail
import retrofit.client.Response
import retrofit.http.Body
import retrofit.http.Field
import retrofit.http.FormUrlEncoded
import retrofit.http.POST

interface MapsCachingApiService {

    @POST("/maps/insert")
    fun insertAutoComplete(@Body body: InsertAutocomplete): Response

    @POST("/maps/insert")
    fun insertPlaceDetail(@Body body: InsertPlaceDetail): Response

    @FormUrlEncoded
    @POST("/maps/get_autocomplete_data")
    fun getAutocompleteData(@Field(Constants.KEY_ADDRESS) address: String,
                            @Field(Constants.KEY_LAT) lat: Double,
                            @Field(Constants.KEY_LNG) lng: Double,
                            @Field(Constants.KEY_PRODUCT_ID) productId: Int,
                            @Field(Constants.KEY_USER_ID) userId: String): Response

    @FormUrlEncoded
    @POST("/maps/get_geocoding_data")
    fun getGeocodingData(@Field(Constants.KEY_PLACEID) placeId: String,
                         @Field(Constants.KEY_PRODUCT_ID) productId: Int,
                         @Field(Constants.KEY_USER_ID) userId: String): Response
}