package com.demo.repository

import com.demo.api.ApiHelper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LogoutRepository @Inject constructor(private val apiHelper: ApiHelper)  {

    suspend fun apiLogout(param: HashMap<String, Any?>) = apiHelper.apiLogout(param)
}