package com.pdsk.cocodroid.retrofit
import com.christianrondonuwu.informasicuaca.models.CityModel
import com.christianrondonuwu.informasicuaca.models.WeatherModel
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiEndpoint {
    @GET("data/2.5/weather?")
    fun getWeather(
        @Query("appid") appid:String,
        @Query("lat") lat:String,
        @Query("lon") lon:String,
        @Query("lang") lang:String,
        @Query("units") units:String
    ): Call<WeatherModel>

    @GET("geo/1.0/direct?")
    fun getCoordByCity(
        @Query("appid") appid: String,
        @Query("q") q:String,
        @Query("limit") limit:Int
    ): Call<Array<CityModel>>
}