package com.demo.api

import com.demo.model.*
import com.demo.constants.Const
import com.demo.constants.Const.PARAM_DEVICE_TOKEN
import com.demo.constants.Const.PARAM_DEVICE_TYPE
import com.demo.utils.PreferenceManager
import io.reactivex.Observable
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiServiceImpl @Inject constructor(private val apiServices: ApiServices): ApiHelper {
    @Inject lateinit var mPreferenceManager: PreferenceManager


    override suspend fun userRegister(param: HashMap<String, RequestBody?>, files: ArrayList<MultipartBody.Part?>): Observable<Response<SignupModel>> = apiServices.apiRegister(addKeyMultipart(param), files)

    override suspend fun apiLogin(param: HashMap<String, Any?>): Observable<Response<UserModel>> = apiServices.apiLogin(addKey(param))

    override suspend fun apiForgotPassword(param: HashMap<String, Any?>): Observable<Response<Any>> = apiServices.apiForgotPassword(addKey(param))

    override suspend fun apiRefresh(param: HashMap<String, Any?>): Observable<Response<ArrayList<RefreshModel>>> = apiServices.apiRefresh(param)

    override suspend fun apiLogout(param: HashMap<String, Any?>): Observable<Response<Any>> = apiServices.apiLogout(param)

    override suspend fun apiGetAdvertise(param: HashMap<String, Any?>): Observable<Response<ArrayList<AdsModel?>>> = apiServices.apiGetAdvertise(addKey(param))

    override suspend fun apiSendOtp(param: HashMap<String, Any?>) : Observable<Response<SendOtpModel?>> = apiServices.apiSendOtp(addKey(param))

    override suspend fun apiVerifyOtp(param: HashMap<String, Any?>) : Observable<Response<Any?>> = apiServices.apiVerifyOtp(addKey(param))

    override suspend fun apiCategoryList(param: HashMap<String, Any?>): Observable<Response<ArrayList<CategoryModel?>>> = apiServices.apiCategoryList(addKey(param))

    private fun addKey(params: HashMap<String, Any?>): HashMap<String, Any?> {
        params[PARAM_DEVICE_TOKEN] = mPreferenceManager.getStringPreference(PreferenceManager.FCM_TOKEN)
        params[PARAM_DEVICE_TYPE] = Const.DEVICETYPE.toString()
        //params[PARAM_USER_ID] = mPreferenceManager.getUserId()
        return params
    }

    private fun addKeyMultipart(params: HashMap<String, RequestBody?>): HashMap<String, RequestBody?> {
        params[PARAM_DEVICE_TOKEN] = mPreferenceManager.getStringPreference(PreferenceManager.FCM_TOKEN).toString().toReqBody()
        params[PARAM_DEVICE_TYPE] = Const.DEVICETYPE.toString().toReqBody()
        return params
    }
}