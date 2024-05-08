package com.demo.repository

import com.demo.api.ApiHelper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoginRepository @Inject constructor(private val apiHelper: ApiHelper)  {

    suspend fun apiLogin(param: HashMap<String, Any?>) =  apiHelper.apiLogin(param)
}