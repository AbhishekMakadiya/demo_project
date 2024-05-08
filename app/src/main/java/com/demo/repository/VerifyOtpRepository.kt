package com.demo.repository

import com.demo.api.ApiHelper
import javax.inject.Inject

class VerifyOtpRepository @Inject constructor(private val apiHelper: ApiHelper)  {

    suspend fun apiVerifyOtp(param: HashMap<String, Any?>) =  apiHelper.apiVerifyOtp(param)
}