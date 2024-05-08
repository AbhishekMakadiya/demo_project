package com.dgxcoin.webservice

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import com.google.gson.Gson
import com.demo.api.*
import com.demo.model.Response
import com.demo.constants.Const
import com.demo.utils.LogHelper
import com.demo.utils.PreferenceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.nio.charset.Charset
import java.nio.charset.UnsupportedCharsetException
import java.security.cert.CertificateException
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

/**
 * Created by initfusion on 07/02/18.
 */

@Module
@InstallIn(SingletonComponent::class)
class RetrofitApiClient {

    private var mContext: Context? = null

    @Provides
    @Singleton
    fun retrofit(preferenceManager: PreferenceManager,  @ApplicationContext context: Context): Retrofit {
        mContext = context
        return Retrofit.Builder()
            .baseUrl(NetworkUtils.getUrl(Api.MAINURL))
            .client(getUnsafeOkHttpClient(preferenceManager))
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @Named("retrofitClient2")
    fun retrofitClient2(preferenceManager: PreferenceManager,  @ApplicationContext context: Context): Retrofit {
        return Retrofit.Builder()
            .baseUrl(NetworkUtils.getUrl(Api.MAINURL))
            .client(getUnsafeOkHttpClient(preferenceManager))
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }


    // for direction api
    @Provides
    @Singleton
    fun getServices(retrofit: Retrofit): ApiServices {
        return retrofit.create(ApiServices::class.java)
    }

    @Provides
    @Singleton
    fun getUnsafeOkHttpClient(preferenceManager: PreferenceManager): OkHttpClient {
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
            builder.sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
            builder.hostnameVerifier { hostname, session -> true }

            val interceptor1 = HttpLoggingInterceptor()
            interceptor1.level = HttpLoggingInterceptor.Level.HEADERS

            val interceptor = HttpLoggingInterceptor()
            interceptor.level = HttpLoggingInterceptor.Level.BODY

            //builder.addInterceptor(interceptor1)
            builder.addInterceptor(interceptor)

            builder.addInterceptor(commonResponse)

            builder.connectTimeout(30, TimeUnit.SECONDS)
            builder.readTimeout(30, TimeUnit.SECONDS)

            // ToDo need to remove all headers parameters and pass in request for apis
            /*builder.addInterceptor { chain ->
                val original = chain.request()
                val request = original.newBuilder()
                        .header(PARAM_DEVICE_TYPE, DEVICETYPE.toString())
                        .header(PARAM_DEVICE_TOKEN, preferenceManager.getStringPreference(PreferenceManager.FCM_TOKEN).toString())
                        .header(PARAM_UDID, "123456")
                        .header(PARAM_LANGUAGE, "en")
                        .header(PARAM_TIMEZONE, System.currentTimeMillis().toString())
                        .method(original.method, original.body)
                        .build()
                chain.proceed(request)
            }*/

            // attach Header For Authentication
            // if (preferenceManager.getUserId().isNullOrEmpty()) {
            // builder.addInterceptor { chain ->
            // val original = chain.request()
            // val request = original.newBuilder()
            // .header(
            // PARAM_AUTHORIZATION,
            // preferenceManager.getUserData()?.token.toString()
            // )
            // .method(original.method, original.body)
            // .build()
            // chain.proceed(request)
            // }
            // }

            return builder.build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
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
                if (tempResponse.code == Const.LOGOUT) {
                    //if (mPreferenceManager.getUserLogin()) {
                    val intent = Intent(Const.ACTION_SESSION_EXPIRE)
                    intent.putExtra(Intent.EXTRA_TEXT, tempResponse.message)
                    mContext?.sendBroadcast(intent)
                    //}
                } else if (tempResponse.code == Const.BLOCKED_BY_ADMIN) {
                    //if (!HomeActivity.iSBlockedByAdmin) {
                        val intent = Intent(Const.ACTION_LOGOUT)
                        intent.putExtra(Intent.EXTRA_TEXT, tempResponse.message)
                        mContext?.sendBroadcast(intent)
                    //}
                    /*AlertMessage.showMessage(
                        mContext,
                        response.message,
                        mContext!!.getString(R.string.ok),
                        object : AlertMessageListener(){
                            override fun onPositiveButtonClick() {
                                super.onPositiveButtonClick()
                                val intent = Intent(Const.ACTION_LOGOUT)
                                intent.putExtra(Intent.EXTRA_TEXT, "")
                                mContext?.sendBroadcast(intent)
                            }
                        }
                    )*/
                } else if (tempResponse.code == Const.SUCCESS) {
                    return response
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return chain.proceed(chain.request())
        }
    }

    @Provides
    @Singleton
    fun provideApiHelper(apiHelper: ApiServiceImpl): ApiHelper = apiHelper
}