package net.crowmaster.cardasmarto.workers

import android.content.ContentProviderOperation
import android.content.ContentValues
import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.google.android.gms.tasks.Tasks
import com.google.firebase.iid.FirebaseInstanceId
import com.google.gson.JsonObject
import net.crowmaster.cardasmarto.utils.DBHelper
import net.crowmaster.cardasmarto.constant.Constants
import net.crowmaster.cardasmarto.entities.HistoryDetailedEntity
import org.json.JSONArray
import org.json.JSONObject
import com.google.gson.GsonBuilder
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import cz.msebera.android.httpclient.entity.ByteArrayEntity
import cz.msebera.android.httpclient.message.BasicHeader
import cz.msebera.android.httpclient.protocol.HTTP
import net.crowmaster.cardasmarto.config.AppConfig
import net.crowmaster.cardasmarto.contracts.DBContract
import net.crowmaster.cardasmarto.network.ServerApi
import net.crowmaster.cardasmarto.network.entities.ChildData
import net.crowmaster.cardasmarto.network.entities.TestRecord
import net.crowmaster.cardasmarto.providers.SensorDataProvider
import org.json.JSONException
import java.io.IOException
import java.nio.charset.Charset
import java.util.*


/**
 * The worker which will be invoked by WorkManager in order to send the data to the cloud/local server
 * Please remember to modify [isConnected] method of this class in case the server is not on the internet
 */
class DataSyncWorker(appContext: Context, workerParams: WorkerParameters)
    : Worker(appContext, workerParams) {


    override fun doWork(): Result {

        try {

            if (!isConnected()) return Result.retry()

            val fb_token_task = FirebaseInstanceId.getInstance().instanceId
            val get_fb_token_result = Tasks.await(fb_token_task)
            if (!fb_token_task.isSuccessful) {
                throw fb_token_task.exception!!
            }
            val fb_token = get_fb_token_result.token
            val dbHelper = DBHelper(applicationContext)

            while (true) {


                val recordsNotSynced = dbHelper.unsyncedRecords

                if (recordsNotSynced.count() == 0) {
                    Log.e(TAG, "No more data to sync for now")
                    return Result.success()
                }
                Log.e(TAG, "Number to be sent: " + recordsNotSynced.count())
                val recordsToSync = convertToJsonArray(recordsNotSynced).toString()
                val androidVersion: String = android.os.Build.VERSION.RELEASE


                ////-------------------------------------------------------------------------------
                //send data to server
                val serverApi = ServerApi.instance.value
                // Actually Upload the data to server.
                val response = serverApi.syncDataToServer(fb_token,
                        AppConfig.getInstance().uuid,
                        "Android",
                        androidVersion,
                        recordsToSync).execute()

                // Check to see if the upload succeeded.
                if (!response.isSuccessful) {
                    val errorBody = response.errorBody()
                    val error = errorBody?.string()
                    val message = String.format("Request failed (%s)", error)
                    Log.e(TAG, message)
                    Result.retry()

                } else {
                    Log.e(TAG, "Success, " + response.body().toString())
                    updateRecords(applicationContext, recordsNotSynced)

                }
            }
            ////-------------------------------------------------------------------------------

        } catch (e: Exception) {
            Log.e(TAG, "In line: " + e.stackTrace[0].lineNumber + ", " + e.stackTrace[0].className + ", " + e.message)
            return Result.failure()
        }
    }

    private fun convertToJsonArray(historyRecords: ArrayList<HistoryDetailedEntity>): JSONArray {

        val result = JSONArray()

        //since it is a contract (in server side) that UNIQUE (phone, name) we use it here to group them
        val groupedByPhoneAndName = historyRecords.groupBy {it.phone + "$" +  it.childName}


        for ((phoneName, items) in groupedByPhoneAndName) {
            val jsonObj = JSONObject()
            jsonObj.put("child_name", items.first().childName)
            jsonObj.put("child_age", items.first().age)
            jsonObj.put("phone", items.first().phone)
            jsonObj.put("email", items.first().email)
            jsonObj.put("gender", items.first().isMale)
            jsonObj.put("autism_relative", items.first().hasAutismRelatives())

            val records = JSONArray()
            for (item in items) {
                val record = JSONObject()
                record.put("id", item.id)
                record.put("ac_x", item.acX)
                record.put("ac_y", item.acY)
                record.put("ac_z", item.acZ)
                record.put("battery", item.batteryLvl)
                record.put("encoder1", item.encoder1)
                record.put("encoder2", item.encoder2)
                record.put("car_time", item.clientTime)
                record.put("session_id", item.sessionSerial)
                record.put("universal_time", item.serverTime)
                records.put(record)

            }

            jsonObj.put("records", records)
            result.put(jsonObj)
        }

        return result
    }

    companion object {

        private const val TAG = "DataSyncWorker"
    }

    fun updateRecords(context: Context, items: List<HistoryDetailedEntity>) {
        try {
//            for (item in items) {
//                val cv = ContentValues()
//                cv.put(DBContract.DataTable.SYNCED, 1)
//                val value = arrayOf(item.id.toString())
//                context.contentResolver.update(SensorDataProvider.Constants.SensorURL, cv, null, value)
//            }

            val invOps = ArrayList<ContentProviderOperation>()
            for (item in items) {
                val cv = ContentValues()
                cv.put(DBContract.DataTable.SYNCED, 1)
                invOps.add(
                        ContentProviderOperation.newUpdate(
                                SensorDataProvider.Constants.SensorURL)
                                .withValues(cv)
                                .withSelection(DBContract.DataTable.COLUMN_ID + "= ?", arrayOf(item.id.toString()))
                                .build())
            }

            context.contentResolver.
                            applyBatch(SensorDataProvider.AUTHORITY, invOps)

        } catch (e: JSONException) {
            e.printStackTrace()
        }

    }

    /**
     * Change command value in [isConnected] in case the server is not on the internet
     */
    @Throws(InterruptedException::class, IOException::class)
    fun isConnected(): Boolean {
        val command = "ping -c 1 google.com"
        return Runtime.getRuntime().exec(command).waitFor() != 0
    }

}