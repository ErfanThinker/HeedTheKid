package net.crowmaster.cardasmarto.network

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import net.crowmaster.cardasmarto.network.entities.DeviceInfo
import org.json.JSONArray
import org.json.JSONObject

import retrofit2.Call
import retrofit2.http.*

interface ServerService {

//    @Headers("Content-Type: application/x-www-form-urlencoded")
//    @Headers("Content-Type: application/json")
    @FormUrlEncoded
    @POST("sync_data.php")
    fun syncData(@Field("firebase_token", encoded = true) fbToken: String,
                 @Field("uuid", encoded = true) uuid:String,
                 @Field("operating_system", encoded = true) os:String,
                 @Field("os_version", encoded = true) osVersion:String,
                 @Field("data", encoded = true) data:String
                 ): Call<JSONObject>

    companion object {
        val ENDPOINT = "http://192.168.1.33/test/"
    }
}
