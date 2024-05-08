package com.demo.repository

import com.demo.api.ApiHelper
import javax.inject.Inject

class AdsRepository @Inject constructor(private val apiHelper: ApiHelper)  {

    suspend fun apiGetAdvertise(param: HashMap<String, Any?>) =  apiHelper.apiGetAdvertise(param)
}