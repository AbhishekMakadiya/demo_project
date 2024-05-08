package com.demo.repository

import com.demo.api.ApiHelper
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

class SingupRepository @Inject constructor(private val apiHelper: ApiHelper)  {

    suspend fun userRegister(param: HashMap<String, RequestBody?>, files: ArrayList<MultipartBody.Part?>) =  apiHelper.userRegister(param, files)
}