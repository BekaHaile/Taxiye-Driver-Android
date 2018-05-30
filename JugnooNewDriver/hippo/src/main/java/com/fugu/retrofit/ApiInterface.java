package com.fugu.retrofit;


import com.fugu.model.FuguCreateConversationParams;
import com.fugu.model.FuguCreateConversationResponse;
import com.fugu.model.FuguGetByLabelIdParams;
import com.fugu.model.FuguGetConversationsResponse;
import com.fugu.model.FuguGetMessageParams;
import com.fugu.model.FuguGetMessageResponse;
import com.fugu.model.FuguPutUserDetailsResponse;
import com.fugu.model.FuguUploadImageResponse;
import com.fugu.support.model.HippoSendQueryParams;
import com.fugu.support.model.SupportResponse;
import com.fugu.support.model.SupportTicketResponse;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PartMap;

/**
 * ApiInterface
 */
public interface ApiInterface {

    @FormUrlEncoded
    @POST("/api/users/putUserDetails")
    Call<FuguPutUserDetailsResponse> putUserDetails(@FieldMap Map<String, Object> map);

    @FormUrlEncoded
    @POST("/api/reseller/putUserDetails")
    Call<FuguPutUserDetailsResponse> putUserDetailsReseller(@FieldMap Map<String, Object> map);

    //@FormUrlEncoded
    @POST("/api/conversation/createConversation")
    Call<FuguCreateConversationResponse> createConversation(@Body FuguCreateConversationParams obj);

    @POST("/api/conversation/getMessages")
    Call<FuguGetMessageResponse> getMessages(@Body FuguGetMessageParams obj);


    @POST("/api/conversation/getByLabelId")
    Call<FuguGetMessageResponse> getByLabelId(@Body FuguGetByLabelIdParams obj);

    //change same as get messages
    @FormUrlEncoded
    @POST("/api/conversation/getConversations")
    Call<FuguGetConversationsResponse> getConversations(@FieldMap Map<String, Object> map);

    @FormUrlEncoded
    @POST("/api/users/userlogout")
    Call<CommonResponse> logOut(@FieldMap Map<String, Object> map);

    @Multipart
    @POST("/api/conversation/uploadFile")
    Call<FuguUploadImageResponse> uploadFile(@PartMap Map<String, RequestBody> map);

    @FormUrlEncoded
    @POST("/api/server/logException")
    Call<CommonResponse> sendError(@FieldMap Map<String, Object> map);

    @FormUrlEncoded
    @POST("/api/business/getBusinessSupportPanel")
    Call<SupportResponse> fetchSupportData(@FieldMap Map<String, Object> map);

    @FormUrlEncoded
    @POST("/api/business/getBusinessSupportPanel")
    Call<SupportResponse> sendSupportQuery(@FieldMap Map<String, Object> map);

    @POST("/api/support/createConversation")
    Call<SupportTicketResponse> createTicket(@Body HippoSendQueryParams obj);

}
