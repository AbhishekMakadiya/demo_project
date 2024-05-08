package com.demo.api

import com.demo.model.*
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody

/*
class ApiHelper(private val apiServices: ApiServices) {
    fun getEnvironmentList(param: EnvironmentRequest) = apiServices.apiEnvironmentList(param)
}*/


interface ApiHelper{
    suspend fun userRegister(param: HashMap<String, RequestBody?>, files: ArrayList<MultipartBody.Part?>): Observable<Response<SignupModel>>

    suspend fun apiLogin(param: HashMap<String, Any?>): Observable<Response<UserModel>>

    suspend fun apiForgotPassword(param: HashMap<String, Any?>): Observable<Response<Any>>

    suspend fun apiRefresh(param: HashMap<String, Any?>): Observable<Response<ArrayList<RefreshModel>>>

    suspend fun apiLogout(param: HashMap<String, Any?>): Observable<Response<Any>>

    suspend fun apiGetAdvertise(param: HashMap<String, Any?>): Observable<Response<ArrayList<AdsModel?>>>

    suspend fun apiSendOtp(param: HashMap<String, Any?>): Observable<Response<SendOtpModel?>>

    suspend fun apiVerifyOtp(param: HashMap<String, Any?>): Observable<Response<Any?>>

    //
    suspend fun apiCategoryList(param: HashMap<String, Any?>): Observable<Response<ArrayList<CategoryModel?>>>

}