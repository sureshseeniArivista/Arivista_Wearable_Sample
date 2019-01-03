package com.arivista.commonprogram.network


import com.arivista.wearabletest.pojo.Example
import com.arivista.wearabletest.pojo.Main
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface Api {

    @GET("weather")
     fun getGetLists(@Query("q")city_name:String,
                     @Query("APPID") appId:String): Call<Example>
}