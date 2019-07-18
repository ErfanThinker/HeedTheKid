package net.crowmaster.cardasmarto.network

import android.net.Uri
import android.util.Log
import com.google.gson.JsonObject
import net.crowmaster.cardasmarto.network.entities.DeviceInfo
import okhttp3.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.io.File
import java.io.IOException
import com.google.gson.GsonBuilder
import com.google.gson.Gson
import com.google.gson.JsonArray
import net.crowmaster.cardasmarto.network.entities.ChildData
import org.json.JSONArray
import retrofit2.http.Field

/**
 * This class is responsible for actual upload of the data to the cloud/local server
 */
class ServerApi private constructor() {
    private val mServerService: ServerService

    init {

        val gson = GsonBuilder()
                .setLenient()
//                .disableHtmlEscaping()
                .create()

        val client = OkHttpClient.Builder()
                .addInterceptor(LoggingInterceptor())
                .build()

        val retrofit = Retrofit.Builder()
                .baseUrl(ServerService.ENDPOINT)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

        mServerService = retrofit.create(ServerService::class.java)
    }

    /*
    This method will upload the data to the server
     */
    fun syncDataToServer(fbToken: String,
                         uuid:String,
                         os:String,
                         osVersion:String,
                         data: String): Call<JSONObject> {
        return mServerService.syncData(fbToken, uuid, os, osVersion, data)
    }

    private class LoggingInterceptor : Interceptor {
        @Throws(IOException::class)
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()

            val t1 = System.nanoTime()
            Log.e(TAG, String.format("Sending request %s on %s%n%s%s",
                    request.url(), chain.connection(), request.headers(), request.body()))

            val response = chain.proceed(request)

            val t2 = System.nanoTime()
            Log.e(TAG,String.format("Received response for %s in %.1fms%n%s",
                    response.request().url(), (t2 - t1) / 1e6, response.headers()))

            return response
        }
    }

    companion object {

        val instance: Lazy<ServerApi> = lazy { ServerApi() }
        val TAG = "ServerApi"
    }
}