package product.clicklabs.jugnoo.driver.retrofit

import retrofit.client.Response
import retrofit.http.GET
import retrofit.http.QueryMap

interface JungleMapsApi {

    @GET("/directions?driving_mode=car")
    fun directions(@QueryMap params: Map<String, String>): Response

}