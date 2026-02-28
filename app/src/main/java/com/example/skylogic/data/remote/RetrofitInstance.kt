package com.example.skylogic.data.remote

import com.example.skylogic.AppConstants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.osmdroid.library.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

  private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val original = chain.request()

            val originalUrl = original.url

            val newUrl = originalUrl.newBuilder()
                .addQueryParameter("appid", AppConstants.API_KEY)
                .build()

            val newRequest = original.newBuilder()
                .url(newUrl)
                .build()

            chain.proceed(newRequest)
        }
//        .apply {
//            if (BuildConfig.DEBUG) {
//                val logging = HttpLoggingInterceptor().apply {
//                    level = HttpLoggingInterceptor.Level.BODY
//                }
//                addInterceptor(logging)
//            }
//        }
        .build()

    val api: WeatherService by lazy {
        Retrofit.Builder()
            .baseUrl(AppConstants.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherService::class.java)
    }
}
