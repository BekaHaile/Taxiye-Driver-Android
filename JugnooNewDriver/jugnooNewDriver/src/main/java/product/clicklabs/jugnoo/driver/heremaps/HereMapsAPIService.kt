package product.clicklabs.jugnoo.driver.heremaps

import product.clicklabs.jugnoo.driver.retrofit.model.SettleUserDebt
import retrofit.Callback
import retrofit.http.*
import retrofit.mime.TypedFile

interface HereMapsAPIService {

    @Multipart
    @POST("/feedback")
    fun feedback(@Header("Auth-Service-Id") serviceId:String,
                 @Header("Auth-Identifier") identifier:String,
                 @Header("Auth-Secret") secret:String,
                 @Part("myfile") image: TypedFile,
                 @PartMap params: Map<String, String>,
                 cb: Callback<SettleUserDebt>)

}
