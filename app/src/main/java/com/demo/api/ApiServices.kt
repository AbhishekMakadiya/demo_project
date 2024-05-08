package com.demo.api

import androidx.room.RawQuery
import com.demo.model.*
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiServices {

    // Call Api For Update Profile
    @Multipart
    @POST(Api.API_SIGNUP)
    fun apiRegister(
            @PartMap param: HashMap<String, RequestBody?>,
            @Part files: ArrayList<MultipartBody.Part?>,
    ): Observable<Response<SignupModel>>

    // Call Api For Login
    @FormUrlEncoded
    @POST(Api.API_LOGIN)
    fun apiLogin(@FieldMap param: HashMap<String, Any?>): Observable<Response<UserModel>>

    // Call Api For forgot password
    @FormUrlEncoded
    @POST(Api.API_FORGOT_PASSWORD)
    fun apiForgotPassword(@FieldMap param: HashMap<String, Any?>): Observable<Response<Any>>

    // Call Api For Login
    @FormUrlEncoded
    @POST(Api.API_REFRESH)
    fun apiRefresh(@FieldMap param: HashMap<String, Any?>): Observable<Response<ArrayList<RefreshModel>>>

    // Call Api For logout
    @FormUrlEncoded
    @POST(Api.API_LOGOUT)
    fun apiLogout(@FieldMap param: HashMap<String, Any?>): Observable<Response<Any>>

    // Call Api For Advertisement
    @FormUrlEncoded
    @POST(Api.API_GET_ADVERTISEMENT)
    fun apiGetAdvertise(@FieldMap param: HashMap<String, Any?>): Observable<Response<ArrayList<AdsModel?>>>

    // Call Api For send otp
    @FormUrlEncoded
    @POST(Api.API_SEND_OTP)
    fun apiSendOtp(@FieldMap param: HashMap<String, Any?>): Observable<Response<SendOtpModel?>>

    // Call Api For Verify Otp
    @FormUrlEncoded
    @POST(Api.API_VERITY_OTP)
    fun apiVerifyOtp(@FieldMap param: HashMap<String, Any?>): Observable<Response<Any?>>

    // Call Api For CategoryList
    @GET(Api.API_VERITY_OTP)
    fun apiCategoryList(@QueryMap param: HashMap<String, Any?>):Observable<Response<ArrayList<CategoryModel?>>>


}



