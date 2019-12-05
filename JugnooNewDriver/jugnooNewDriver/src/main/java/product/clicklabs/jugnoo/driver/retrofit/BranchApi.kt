package product.clicklabs.jugnoo.driver.retrofit

import product.clicklabs.jugnoo.driver.retrofit.model.BranchUrlRequest
import product.clicklabs.jugnoo.driver.retrofit.model.BranchUrlResponse
import retrofit.Callback
import retrofit.http.Body
import retrofit.http.POST

interface BranchApi {

    @POST("/url")
    fun generateUrl(@Body request: BranchUrlRequest, callback:Callback<BranchUrlResponse>)

}