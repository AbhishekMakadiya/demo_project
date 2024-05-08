package com.demo.repository

import com.demo.api.ApiHelper
import javax.inject.Inject

class SendOtpRepository @Inject constructor(private val apiHelper: ApiHelper)  {

    suspend fun apiSendOtp(param: HashMap<String, Any?>) =  apiHelper.apiSendOtp(param)
}