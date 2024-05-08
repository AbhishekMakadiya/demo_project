package com.demo.api

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.demo.BuildConfig
import com.demo.model.AdsModel
import com.demo.model.Response
import com.demo.constants.Const
import com.demo.constants.Const.PARAM_DEVICE_TOKEN
import com.demo.constants.Const.PARAM_USER_ID
import com.demo.utils.LogHelper
import com.demo.utils.PreferenceManager
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.nio.charset.Charset
import java.nio.charset.UnsupportedCharsetException
import java.security.cert.CertificateException
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


/**
 * service manager class is use for create retrofit client and create service request method
 *
 * in retrofit client add interceptor for network communication error and log interceptor
 * */
class ServiceManager(private val mContext: Context) {

    private val mPreferenceManager: PreferenceManager = PreferenceManager(mContext)

    private fun buildClient(): OkHttpClient {
        // this interceptor is for print MyResponse in log
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        val httpClient = OkHttpClient.Builder()
            .addInterceptor(ConnectivityInterceptor(mContext))
            .connectTimeout(5, TimeUnit.MINUTES)
            .readTimeout(5, TimeUnit.MINUTES)
            .writeTimeout(5, TimeUnit.MINUTES)
            .addInterceptor(ConnectivityInterceptor(mContext))


        if (BuildConfig.DEBUG)
            httpClient.addInterceptor(loggingInterceptor)

        // attach Header For Authentication
        if (!mPreferenceManager.getUserId().isNullOrEmpty()) {
            httpClient.addInterceptor { chain ->
                val original = chain.request()
                val request = original.newBuilder()
                    .header(
                        PARAM_USER_ID,
                        mPreferenceManager.getStringPreference(PreferenceManager.FCM_TOKEN)
                            .toString()
                    )
                    .method(original.method, original.body)
                    .build()
                chain.proceed(request)
            }
        }

        return httpClient.build()
    }

    private val UTF8 = Charset.forName("UTF-8")
    private val commonResponse: Interceptor = object : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            var response: okhttp3.Response? = null
            try {
                val request = chain.request()
                response = chain.proceed(request)
                val responseBody = response.body
                val source = responseBody!!.source()
                source.request(Long.MAX_VALUE) // Buffer the entire body.
                val buffer = source.buffer()
                var charset: Charset = UTF8
                val contentType = responseBody.contentType()
                if (contentType != null) {
                    try {
                        charset = contentType.charset(UTF8)!!
                    } catch (e: UnsupportedCharsetException) {
                        return response
                    }
                }
                val gson = Gson()
                val tempResponse: Response<ResponseBody> = gson.fromJson(
                    buffer.clone().readString(charset),
                    Response<ResponseBody>()::class.java
                )
                LogHelper.e("testing", tempResponse.code.toString() + "")
                if (tempResponse.code == Const.SUCCESS) {
                    return response
                } else if (tempResponse.code == Const.LOGOUT) {
                    if (mPreferenceManager.getUserLogin()) {
                        val intent = Intent(Const.ACTION_SESSION_EXPIRE)
                        intent.putExtra(Intent.EXTRA_TEXT, tempResponse.message)
                        mContext.sendBroadcast(intent)
                    }
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return chain.proceed(chain.request())
        }
    }

    private fun getUnsafeOkHttpClient(): OkHttpClient {
        try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                @SuppressLint("TrustAllX509TrustManager")
                @Throws(CertificateException::class)
                override fun checkClientTrusted(
                    chain: Array<java.security.cert.X509Certificate>,
                    authType: String,
                ) {
                }

                @SuppressLint("TrustAllX509TrustManager")
                @Throws(CertificateException::class)
                override fun checkServerTrusted(
                    chain: Array<java.security.cert.X509Certificate>,
                    authType: String,
                ) {
                }

                override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                    return arrayOf()
                }
            })

            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, java.security.SecureRandom())

            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.socketFactory


            val builder = OkHttpClient.Builder()
                .addInterceptor(ConnectivityInterceptor(mContext))
                .connectTimeout(5, TimeUnit.MINUTES)
                .readTimeout(5, TimeUnit.MINUTES)
                .writeTimeout(5, TimeUnit.MINUTES)
                .addInterceptor(ConnectivityInterceptor(mContext))
            builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            builder.hostnameVerifier(HostnameVerifier { _, _ -> true })

            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY

            builder.addInterceptor(interceptor)
            builder.addInterceptor(commonResponse)
            builder.connectTimeout(30, TimeUnit.SECONDS)
            builder.readTimeout(30, TimeUnit.SECONDS)

            return builder.build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    private fun getOkHttpClient(): OkHttpClient {
        try {
            // Create a trust manager that does not validate certificate chains
            val trustAllCerts = arrayOf<TrustManager>(object : X509TrustManager {
                @SuppressLint("TrustAllX509TrustManager")
                @Throws(CertificateException::class)
                override fun checkClientTrusted(
                        chain: Array<java.security.cert.X509Certificate>,
                        authType: String
                ) {
                }

                @SuppressLint("TrustAllX509TrustManager")
                @Throws(CertificateException::class)
                override fun checkServerTrusted(
                        chain: Array<java.security.cert.X509Certificate>,
                        authType: String
                ) {
                }

                override fun getAcceptedIssuers(): Array<java.security.cert.X509Certificate> {
                    return arrayOf()
                }
            })

            // Install the all-trusting trust manager
            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, java.security.SecureRandom())

            // Create an ssl socket factory with our all-trusting manager
            val sslSocketFactory = sslContext.socketFactory


            val builder = OkHttpClient.Builder()
                    .addInterceptor(ConnectivityInterceptor(mContext))
                    .connectTimeout(5, TimeUnit.MINUTES)
                    .readTimeout(5, TimeUnit.MINUTES)
                    .writeTimeout(5, TimeUnit.MINUTES)
            builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            builder.hostnameVerifier(HostnameVerifier { _, _ -> true })


            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY

            builder.addInterceptor(interceptor)

            builder.connectTimeout(30, TimeUnit.SECONDS)
            builder.readTimeout(30, TimeUnit.SECONDS)

            return builder.build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    public fun buildApi(): ApiServices {
        return Retrofit.Builder()
            .baseUrl(NetworkUtils.getUrl(Api.MAINURL))
            .client(getUnsafeOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiServices::class.java)
    }

    private fun buildTwilioApi(): ApiServices {
        val gson = GsonBuilder()
                .setLenient()
                .create()
        return Retrofit.Builder()
                .baseUrl(Const.TWILIO_BASE_URL)
                .client(getOkHttpClient())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(ApiServices::class.java)
    }

    private fun addKey(params: HashMap<String, Any?>): HashMap<String, Any?> {
        params[PARAM_DEVICE_TOKEN] =
            mPreferenceManager.getStringPreference(PreferenceManager.FCM_TOKEN)
        params[PARAM_USER_ID] = mPreferenceManager.getUserId()
        return params
    }

    private fun addTwilioKey(params: HashMap<String, Any?>): HashMap<String, Any?> {
        params[PARAM_DEVICE_TOKEN] =
                mPreferenceManager.getStringPreference(PreferenceManager.FCM_TOKEN)
        return params
    }

    private fun addKeyMultipart(params: HashMap<String, RequestBody?>): HashMap<String, RequestBody?> {
        params[PARAM_DEVICE_TOKEN] =
            mPreferenceManager.getStringPreference(PreferenceManager.FCM_TOKEN).toString()
                .toReqBody()
        params[PARAM_USER_ID] = mPreferenceManager.getUserId().toString().toReqBody()
        return params
    }


    /**
     *
     * Api For Login User
     *
     * */

    /*fun apiLogin(
        params: HashMap<String, Any?>,
        l: OnResponseListener<Response<LoginModel>>,
    ) {

        val call = buildApi().apiLogin(params)
        call.enqueue(object : Callback<Response<LoginModel>> {
            override fun onResponse(
                call: Call<Response<LoginModel>>,
                response: retrofit2.Response<Response<LoginModel>>,
            ) {
                val body = response.body()
                if (body != null) {
                    if (body.code == Const.SUCCESS)
                        l.onRequestSuccess(response.body()!!)
                    else
                        l.onRequestFailed(body.message)
                } else
                    l.onRequestFailed(response.message())
            }

            override fun onFailure(call: Call<Response<LoginModel>>, t: Throwable) {
                l.onRequestFailed(t)
            }
        })
    }*/



    /**
     *
     * Api for Logout User
     *
     * */

    /*fun apiLogout(
        params: HashMap<String, Any?>,
        l: OnResponseListener<Response<Any>>,
    ) {
        val call = buildApi().apiLogout(addKey(params))
        call.enqueue(object : Callback<Response<Any>> {
            override fun onResponse(
                call: Call<Response<Any>>,
                response: retrofit2.Response<Response<Any>>,
            ) {
                val body = response.body()
                if (body != null) {
                    if (body.code == Const.SUCCESS)
                        l.onRequestSuccess(response.body()!!)
                    else
                        l.onRequestFailed(body.message)
                } else
                    l.onRequestFailed(response.message())
            }

            override fun onFailure(call: Call<Response<Any>>, t: Throwable) {
                l.onRequestFailed(t)
            }
        })
    }*/

    /**
     *
     * Api For Update Profile
     *
     * */

    /*fun apiUpdateProfile(
        params: HashMap<String, RequestBody?>,
        files: ArrayList<MultipartBody.Part?>,
        l: OnResponseListener<Response<UserModel>>,
    ) {

        val call = buildApi().apiUpdateProfile(addKeyMultipart(params), files)
        call.enqueue(object : Callback<Response<UserModel>> {
            override fun onResponse(
                call: Call<Response<UserModel>>,
                response: retrofit2.Response<Response<UserModel>>,
            ) {
                val body = response.body()
                if (body != null) {
                    if (body.code == Const.SUCCESS)
                        l.onRequestSuccess(response.body()!!)
                    else if (body.code != Const.LOGOUT)
                        l.onRequestFailed(body.message)
                } else
                    l.onRequestFailed(response.message())
            }

            override fun onFailure(call: Call<Response<UserModel>>, t: Throwable) {
                l.onRequestFailed(t)
            }
        })
    }*/


    /**
     *
     * Api for get ads
     *
     * */

    fun apiGetAds(
        params: HashMap<String, Any?>,
        l: OnResponseListener<ArrayList<AdsModel?>>,
    ) {

        /*val call = buildApi().apiGetAdvertise(params)
        call.enqueue(object : Callback<Response<ArrayList<AdsModel?>>> {
            override fun onResponse(
                call: Call<Response<ArrayList<AdsModel?>>>,
                response: retrofit2.Response<Response<ArrayList<AdsModel?>>>,
            ) {
                val body = response.body()
                if (body != null) {
                    if (body.code == Const.SUCCESS)
                        l.onRequestSuccess(response.body()?.result!!)
                    else
                        l.onRequestFailed(body.message)
                } else
                    l.onRequestFailed(response.message())
            }

            override fun onFailure(
                call: Call<Response<ArrayList<AdsModel?>>>,
                t: Throwable,
            ) {
                l.onRequestFailed(t)
            }
        })*/
    }
}