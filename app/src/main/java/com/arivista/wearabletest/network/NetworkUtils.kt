package com.arivista.commonprogram.network

import android.content.pm.PackageManager
import android.support.constraint.BuildConfig
import android.util.Log
import com.arivista.wearabletest.network.AuthenticationInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

class NetworkUtils {
    fun getApiService():Retrofit?{
        val retrofit = Retrofit.Builder()
        val httpClient = OkHttpClient.Builder()



        var secretkey = "APPID=f9c07293f168d281e8dc501ccf507d9b"
        Log.e("secretkey", "obs")
        httpClient.addInterceptor(
            AuthenticationInterceptor(secretkey)
        )

        retrofit.baseUrl("http://api.openweathermap.org/data/2.5/")
            //.client(httpClient.build())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()

        return retrofit.build()
    }
}