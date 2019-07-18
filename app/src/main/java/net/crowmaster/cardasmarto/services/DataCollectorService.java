package net.crowmaster.cardasmarto.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import net.crowmaster.cardasmarto.constant.Constants;
import net.crowmaster.cardasmarto.contracts.DBContract;
import net.crowmaster.cardasmarto.nanohttpdServer.NanoHTTPD;
import net.crowmaster.cardasmarto.providers.SensorDataProvider;
import net.crowmaster.cardasmarto.utils.BoundRunnerBeta;
import net.crowmaster.cardasmarto.utils.MyServer;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;

/**
 * Created by root on 6/16/16.
 * This service is responsible for collecting the data from the car
 */
public class DataCollectorService extends Service implements MyServer.ResponseInterface{
    public static final String TAG = "MyServiceTag";
    private static MyServer myServer; // NanoHttpdServer
    private long SESSION_SERIAL = 1;
    private static String childName = "";
    private static String phone = "";
    private static String email = "";
    private static boolean gender = true;
    private static boolean autismRelative = false;
    private static int childAge = -1;

    // These three fields are used to check whether this service is running or nt from the application/activity
    public static final String ACTION_PING = DataCollectorService.class.getName() + ".PING";
    public static final String ACTION_PONG = DataCollectorService.class.getName() + ".PONG";

    private BroadcastReceiver mReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive (Context context, Intent intent)
        {
            if (intent.getAction().equals(ACTION_PING))
            {
                LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getApplicationContext());
                manager.sendBroadcast(new Intent(ACTION_PONG));
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        Log.e("DataCollectorService", "onStartCommand");
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter(ACTION_PING));
        SESSION_SERIAL = getApplicationContext()
                .getSharedPreferences("server_status", MODE_PRIVATE).getLong("COLUMN_SESSION_SERIAL",
                        Calendar.getInstance().getTimeInMillis());
        SharedPreferences sp = getApplicationContext().getSharedPreferences("preferences", MODE_PRIVATE);
        childName = sp.getString(Constants.SP_CHILD_NAME, "No Child Name provided");
        childAge = sp.getInt(Constants.SP_CHILD_AGE, -1);
        phone = sp.getString(Constants.SP_PHONE, "No Phone provided");
        email = sp.getString(Constants.SP_EMAIL, "No EMAIL provided");
        gender = sp.getBoolean(Constants.SP_CHILD_GENDER, true);
        autismRelative = sp.getBoolean(Constants.SP_AUTISM_RELATIVES, false);

        try {
            myServer = new MyServer(this);
            HandlerThread handlerThread = new HandlerThread("HandlerThread", Process.THREAD_PRIORITY_BACKGROUND);
            handlerThread.start();

            // Create a handler attached to the HandlerThread's Looper
            Handler mHandler = new Handler(handlerThread.getLooper()) {
                @Override
                public void handleMessage(Message msg) {
                    ((NanoHTTPD.ClientHandler)msg.obj).run();
                }
            };
            myServer.setAsyncRunner(new BoundRunnerBeta(handlerThread, mHandler)); // 2 failures in 20 mins
            //myServer.setAsyncRunner(new BoundRunner(Executors.newCachedThreadPool()));// 4 failures in 20 mins
            //myServer.setAsyncRunner(new BoundRunner(Executors.newFixedThreadPool(12)));// 10 failures in 8 mins
            myServer.start();

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("CreatingServer", "Caught Exception: " + e.getMessage());
        }

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
        Log.e("Data Collector Service", "OnDestroy");
        if(myServer != null) {
            myServer.stop();
        }

        super.onDestroy();
    }

    @Override
    public void OnResponse(JSONObject response) {
        try {
            ContentValues cv = new ContentValues();
            cv.put(DBContract.DataTable.COLUMN_AC_X, response.getLong("AcX"));
            cv.put(DBContract.DataTable.COLUMN_AC_Y, response.getLong("AcY"));
            cv.put(DBContract.DataTable.COLUMN_AC_Z, response.getLong("AcZ"));
            cv.put(DBContract.DataTable.COLUMN_ENCODER_1, response.getLong("Encode1"));
            cv.put(DBContract.DataTable.COLUMN_ENCODER_2, response.getLong("Encode2"));
            cv.put(DBContract.DataTable.COLUMN_CLIENT_TIME, response.getLong("time"));
            if (response.has("battery")) {
                cv.put(DBContract.DataTable.BATTERY_LVL, response.getLong("battery"));
            } else {
                cv.put(DBContract.DataTable.BATTERY_LVL, 0l);
            }
            cv.put(DBContract.DataTable.COLUMN_SERVER_TIME, Calendar.getInstance().getTimeInMillis());
            cv.put(DBContract.DataTable.COLUMN_SESSION_SERIAL, SESSION_SERIAL);
            cv.put(DBContract.DataTable.COLUMN_CHILD_NAME, childName);
            cv.put(DBContract.DataTable.COLUMN_PHONE, phone);
            cv.put(DBContract.DataTable.COLUMN_CHILD_AGE, childAge);
            cv.put(DBContract.DataTable.COLUMN_GENDER, gender);
            cv.put(DBContract.DataTable.COLUMN_EMAIL, email);
            cv.put(DBContract.DataTable.COLUMN_AUTISM_RELATIVES, autismRelative);
            getContentResolver().insert(SensorDataProvider.Constants.SensorURL, cv);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

}
