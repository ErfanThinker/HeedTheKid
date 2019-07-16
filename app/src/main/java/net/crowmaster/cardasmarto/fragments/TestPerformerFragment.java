package net.crowmaster.cardasmarto.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Chronometer;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import net.crowmaster.cardasmarto.R;
import net.crowmaster.cardasmarto.activities.MainActivity;
import net.crowmaster.cardasmarto.constant.Constants;
import net.crowmaster.cardasmarto.contracts.DBContract;
import net.crowmaster.cardasmarto.providers.SensorDataProvider;
import net.crowmaster.cardasmarto.services.DataCollectorService;
import net.crowmaster.cardasmarto.widgets.CustomMarkerView;
import net.crowmaster.cardasmarto.workers.DataSyncWorker;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * Created by root on 6/15/16.
 */
public class TestPerformerFragment extends Fragment {
    private boolean isSvcRunning = false;
    private static final int MY_PERMISSIONS_REQUEST_STORAGE = 2937;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 2938;
    private static final int RECORDING_ID = 8364;
    private SharedPreferences sp;
//    public static PlayPauseDrawable mPlayPauseDrawable;
    private static WifiManager.LocalOnlyHotspotReservation mReservation;
    public static Boolean isRecording;
    private  String Tag = "TestPerformerFragment";
    //private TextView helloTV;
    static MyObserver myObserver;
    public static FloatingActionButton fab;
    private LineChart mChart;
    private Chronometer mChronometer;
    private TextView encoder1TV;
    private TextView encoder2TV;
    private final boolean versionCheck = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_perform_test, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        mPlayPauseDrawable =
//                new PlayPauseDrawable();
        isRecording = false;
        encoder1TV = view.findViewById(R.id.encoder_1_tv);
        encoder2TV = view.findViewById(R.id.encoder_2_tv);
        mChronometer = view.findViewById(R.id.chronometer);
        mChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener(){
            @Override
            public void onChronometerTick(Chronometer cArg) {
                long time = SystemClock.elapsedRealtime() - cArg.getBase();
                int h   = (int)(time /3600000);
                int m = (int)(time - h*3600000)/60000;
                int s= (int)(time - h*3600000- m*60000)/1000 ;
                String hh = h < 10 ? "0"+h: h+"";
                String mm = m < 10 ? "0"+m: m+"";
                String ss = s < 10 ? "0"+s: s+"";
                cArg.setText(hh+":"+mm+":"+ss);
            }
        });



        initCharts(view);


        fab = view.findViewById(R.id.fab);
//        fab.setImageDrawable(mPlayPauseDrawable);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                short result = 0;
                if (sp == null) {
                    sp = getActivity().getSharedPreferences("preferences", Context.MODE_PRIVATE);
                }
                if (!(sp.contains(Constants.SP_PHONE) && !sp.getString(Constants.SP_PHONE, "").isEmpty())
                    || !(sp.contains(Constants.SP_CHILD_NAME) && !sp.getString(Constants.SP_CHILD_NAME, "").isEmpty())) {
                    Snackbar.make(view, "Please Enter a valid name and phone number in preferences first!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                    return;
                }


                if(!isRecording) {
                    result = startServer();
                } else {
                    result = pauseServer();
                }

                if(result != 0) {

                    isRecording = !isRecording;
                    if(result == 1) {
                        Snackbar.make(view, "Starting recording data", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        toggleButtonState(android.R.drawable.ic_media_pause);
                    } else {
                        Snackbar.make(view, "Finished recording data", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        toggleButtonState( android.R.drawable.ic_media_play);

                        //send data to server
                        enqueueDataSync();
                    }
                }
            }
        });


        if(isSvcRunning) {
            isRecording = true;
            Snackbar.make(view, "Data is being recorded!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            myObserver = new MyObserver();
            getActivity().getContentResolver()
                    .registerContentObserver(SensorDataProvider.Constants.SensorURL, true,
                            myObserver);

            mChronometer.setVisibility(View.VISIBLE);
            long tempTime = Calendar.getInstance().getTimeInMillis();
            mChronometer.setBase(SystemClock.elapsedRealtime() -
                    (tempTime -
                            getActivity().getSharedPreferences(Constants.SP_NAME__SERVER_STATUS, Context.MODE_PRIVATE)
                                    .getLong(Constants.SP_SESSION_SERIAL, tempTime)));
            mChronometer.start();
            encoder1TV.setVisibility(View.VISIBLE);
            encoder2TV.setVisibility(View.VISIBLE);
            toggleButtonState(android.R.drawable.ic_media_pause);
        } else {
            toggleButtonState(android.R.drawable.ic_media_play);
        }

        /*if(getActivity().getSharedPreferences(Constants.SP_NAME__SERVER_STATUS, Context.MODE_PRIVATE).contains("startTime") &&
                helloTV != null) {
            helloTV.setText("Data collection started on:\n" +
                    getActivity().getSharedPreferences(Constants.SP_NAME__SERVER_STATUS, Context.MODE_PRIVATE)
                            .getString("startTime", ""));
        }*/

        sp = getActivity().getSharedPreferences("preferences", Context.MODE_PRIVATE);

        //TEST: remove this later
//        enqueueDataSync();
    }

    private void enqueueDataSync() {

        // Create the Constraints
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        // Define the input
        Data inputData = createInputDataForSyncDataWorker();


        // Bring it all together by creating the WorkRequest; this also sets the back off criteria
        OneTimeWorkRequest uploadWorkRequest = new OneTimeWorkRequest.Builder(DataSyncWorker.class)
                .setInputData(inputData)
                .setConstraints(constraints)
                .setBackoffCriteria(
                        BackoffPolicy.LINEAR,
                        OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                        TimeUnit.MILLISECONDS)
                .build();
        WorkManager.getInstance().beginUniqueWork("syncDataWork",
                ExistingWorkPolicy.REPLACE,
                uploadWorkRequest).enqueue();

    }

    private Data createInputDataForSyncDataWorker() {
        Data.Builder builder = new Data.Builder();
//        builder.putString(Constants.KEY_FB_TOKEN, "test_token");
        return builder.build();
    }

    public void toggleButtonState(int iconId) {
        //get the drawable
        Drawable myFabSrc = ResourcesCompat.getDrawable(getResources(), iconId, null);
        //copy it in a new one
        Drawable willBeWhite = myFabSrc.getConstantState().newDrawable();
        //set the color filter, you can use also Mode.SRC_ATOP
        willBeWhite.mutate().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        //set it to your fab button initialized before
        fab.setImageDrawable(willBeWhite);
    }

    private void initCharts(View view) {

        mChart = (LineChart) view.findViewById(R.id.chart);
        //mChart.setOnChartGestureListener(this);
        //mChart.setOnChartValueSelectedListener(this);
        mChart.setDrawGridBackground(false);

        // no description text
        Description chartDesc = (new Description());
        chartDesc.setText(getString(R.string.acceleration_chart_description));
        mChart.setDescription(chartDesc);
        mChart.setNoDataText(getString(R.string.acceleration_chart_no_data));

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        // mChart.setScaleXEnabled(true);
        // mChart.setScaleYEnabled(true);

        //setDummyData();

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);
        mChart.setAutoScaleMinMaxEnabled(true);
        mChart.setVisibleXRangeMaximum(5000);

        //mChart.animateX(2500);


        XAxis xAxis = mChart.getXAxis();
        //xAxis.setEnabled(false);

        xAxis.setTextSize(11f);
        xAxis.setTextColor(Color.DKGRAY);
        xAxis.setDrawGridLines(true);
        xAxis.setDrawAxisLine(true);
        xAxis.setPosition(XAxis.XAxisPosition.TOP);
        xAxis.setDrawLabels(false);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        //leftAxis.setAxisMaxValue(5000f);
        //leftAxis.setAxisMinValue(-5000f);
        leftAxis.setDrawGridLines(false);
        leftAxis.setGranularityEnabled(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

        CustomMarkerView mv = new CustomMarkerView(getActivity(), R.layout.custom_marker_view_layout);
        mChart.setMarkerView(mv);

        //mChart.invalidate();

    }



    public short startServer() {

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            if( ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                Log.e("TAG", "I don't have permission");
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_LOCATION);
                return 0;

            }

        }

        Log.e("SystemTime", "" + SystemClock.elapsedRealtime());
        if(mChart != null && mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            try {
                mChart.clear();
                mChart.clearValues();
            } catch (Exception e) {}
        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//
////            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
////                if( ContextCompat.checkSelfPermission(getActivity(),
////                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
////                    Log.e("TAG", "I don't have permission");
////                    ActivityCompat.requestPermissions(getActivity(),
////                            new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
////                            MY_PERMISSIONS_REQUEST_LOCATION);
////
////                }
////
////            }
//
//            if (Settings.System.canWrite(getActivity())) {
////                startActivity(
////                        new Intent(Settings.ACTION_WIRELESS_SETTINGS));
//
//                if( ContextCompat.checkSelfPermission(getActivity(),
//                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                    Log.e("TAG", "I don't have permission");
//                    ActivityCompat.requestPermissions(getActivity(),
//                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                            MY_PERMISSIONS_REQUEST_STORAGE);
//
//                } else {
//                    HsManager.configApState(getActivity(), true, getString(R.string.hotspot_SSID),
//                            getString(R.string.hotspot_PASS));
//
//                    getActivity().
//                            getSharedPreferences(Constants.SP_NAME__SERVER_STATUS, Context.MODE_PRIVATE)
//                            .edit().putLong(Constants.SP_SESSION_SERIAL, Calendar.getInstance().getTimeInMillis())
//                            .putString("startTime", Calendar.getInstance().getTime().toString()).commit();
//
//                    getActivity().startService(new Intent(getActivity(), DataCollectorService.class));
//                    myObserver = new MyObserver();
//                    getActivity().getContentResolver()
//                            .registerContentObserver(SensorDataProvider.Constants.SensorURL, true,
//                            myObserver);
//
//                    /*if(helloTV != null) {
//                        helloTV.setText("Data collection started on:\n" +
//                                Calendar.getInstance().getTime().toString());
//                    }*/
//                    buildNotification();
//                    mChronometer.setVisibility(View.VISIBLE);
//                    mChronometer.setBase(SystemClock.elapsedRealtime());
//                    mChronometer.start();
//                    encoder1TV.setVisibility(View.VISIBLE);
//                    encoder2TV.setVisibility(View.VISIBLE);
//                    return 1;
//                }
//
//            } else if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
//                    Manifest.permission.WRITE_SETTINGS)){
//
//                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                    builder.setTitle("Essential Permission")
//                            .setMessage(R.string.write_settings_dialog_message)
//                            .setPositiveButton("Open settings", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    askForWritePermission();
//                                }
//                            }). setNegativeButton("I refuse!", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            // do nothing!
//                        }
//                    }).setCancelable(false).create().show();
//
//
//            }else {
//                ActivityCompat.requestPermissions(getActivity(),
//                        new String[]{Manifest.permission.WRITE_SETTINGS},
//                        121);
//            }
//        } else {
//            HsManager.configApState(getActivity(), true, getString(R.string.hotspot_SSID),
//                    getString(R.string.hotspot_PASS));
//
//            getActivity().
//                    getSharedPreferences(Constants.SP_NAME__SERVER_STATUS, Context.MODE_PRIVATE)
//                    .edit().putLong(Constants.SP_SESSION_SERIAL, Calendar.getInstance().getTimeInMillis())
//                    .putString("startTime", Calendar.getInstance().getTime().toString()).commit();
//
//
//            getActivity().startService(new Intent(getActivity(), DataCollectorService.class));
//            myObserver = new MyObserver();
//            getActivity().getContentResolver()
//                    .registerContentObserver(SensorDataProvider.Constants.SensorURL, true,
//                            myObserver);
//
//            /*if(helloTV != null) {
//                helloTV.setText("Data collection started on:\n" +
//                        Calendar.getInstance().getTime().toString());
//            }*/
//            buildNotification();
//            mChronometer.setVisibility(View.VISIBLE);
//            mChronometer.setBase(SystemClock.elapsedRealtime());
//            mChronometer.start();
//            encoder1TV.setVisibility(View.VISIBLE);
//            encoder2TV.setVisibility(View.VISIBLE);
//            return 1;
//        }

        WifiApManager wifiApManager = new WifiApManager(getActivity());
        if (!wifiApManager.isWifiApEnabled())

            return enableWirelessAndRunServer();

        else {
            Snackbar.make(getView(), "Please turn off HotSpot first.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

        return 0;
    }

    private short enableWirelessAndRunServer(){
        WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
        wifiManager.disconnect();
        String networkSSID = "Mcar";
        String networkPass = "123456789";

        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + networkSSID + "\"";

        conf.preSharedKey = "\""+ networkPass +"\"";
        wifiManager.addNetwork(conf);

        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        if (list != null) {
            for (WifiConfiguration i : list) {
                if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                    wifiManager.disconnect();
                    wifiManager.enableNetwork(i.networkId, true);
                    wifiManager.reconnect();

                    break;
                }
            }
        }

        getActivity().
                getSharedPreferences(Constants.SP_NAME__SERVER_STATUS, Context.MODE_PRIVATE)
                .edit().putLong(Constants.SP_SESSION_SERIAL, Calendar.getInstance().getTimeInMillis())
                .putString("startTime", Calendar.getInstance().getTime().toString()).commit();

//        BroadcastReceiver broadcastReceiver = new WifiBroadcastReceiver();
//
//        IntentFilter intentFilter = new IntentFilter();
//        intentFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
//        getActivity().registerReceiver(broadcastReceiver, intentFilter);

        getActivity().startService(new Intent(getActivity(), DataCollectorService.class));
        myObserver = new MyObserver();
        getActivity().getContentResolver()
                .registerContentObserver(SensorDataProvider.Constants.SensorURL, true,
                        myObserver);

        /*if(helloTV != null) {
            helloTV.setText("Data collection started on:\n" +
                    Calendar.getInstance().getTime().toString());
        }*/
        buildNotification();
        mChronometer.setVisibility(View.VISIBLE);
        mChronometer.setBase(SystemClock.elapsedRealtime());
        mChronometer.start();
        encoder1TV.setVisibility(View.VISIBLE);
        encoder2TV.setVisibility(View.VISIBLE);
        return 1;
    }



    private short pauseServer() {
        getActivity().stopService(new Intent(getActivity(), DataCollectorService.class));
        getActivity().getContentResolver().unregisterContentObserver(myObserver);
//        HsManager.configApState(getActivity(), false, "", "");
        NotificationManager mNotificationManager =
                (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(RECORDING_ID);

        //remove start time of collecting data in sharedPreferences
        getActivity().getSharedPreferences(Constants.SP_NAME__SERVER_STATUS, Context.MODE_PRIVATE)
                .edit().remove("startTime").commit();
        if(mChronometer != null) {
            mChronometer.stop();
        }
        if (encoder1TV != null) {
            final String str1 = "<font color=#cc0029>" + getString(R.string.encoder_1) +
                    "</font> <font color=#3f51b5>0</font>";
            getActivity().runOnUiThread(new Runnable() {
                @SuppressLint("NewApi")
                @Override
                public void run() {
                    if (versionCheck) {
                        encoder1TV.setText( Html.fromHtml(str1, Html.FROM_HTML_MODE_LEGACY));
                    } else {
                        encoder1TV.setText(Html.fromHtml(str1));
                    }

                }
            });

        }
        if (encoder2TV != null) {
            final String str2 = "<font color=#cc0029>" + getString(R.string.encoder_2) +
                    "</font> <font color=#3f51b5>0</font>";
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (versionCheck) {
                        encoder2TV.setText( Html.fromHtml(str2, Html.FROM_HTML_MODE_LEGACY));
                    } else {
                        encoder2TV.setText(Html.fromHtml(str2));
                    }

                }
            });
        }
        //encoder1TV.setVisibility(View.GONE);
        //encoder2TV.setVisibility(View.GONE);
        /*if(helloTV != null) {
            helloTV.setText("Well, Hello there again!");
        }*/


        return 2;
    }


    private void askForWritePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(!Settings.System.canWrite(getActivity())) {
                Intent goToSettings = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                goToSettings.setData(Uri.parse("package:" + getActivity().getPackageName()));
                startActivity(goToSettings);
            }
        }
    }

    public String getWifiApIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en
                    .hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                if (intf.getName().contains("wlan")) {
                    for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr
                            .hasMoreElements();) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if (!inetAddress.isLoopbackAddress()
                                && (inetAddress.getAddress().length == 4)) {
                            Log.d("mTag", inetAddress.getHostAddress());
                            return inetAddress.getHostAddress();
                        }
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("mTag", ex.toString());
        }
        return null;
    }

//    private boolean isMyServiceRunning(Class<?> serviceClass) {
//        ActivityManager manager = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
//        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
//            if (serviceClass.getName().equals(service.service.getClassName())) {
//                return true;
//            }
//        }
//        return false;
//    }

    private void buildNotification() {
        NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);

        String channelId = "channel-27";
        String channelName = "CardDeSmarto";
        int importance = 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            importance = NotificationManager.IMPORTANCE_HIGH;
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel(
                    channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getActivity(), channelId)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(getString(R.string.app_name))
                        .setPriority(Notification.PRIORITY_MAX)
                        .setContentText("We're collecting data, you can rest assured :)")
                        .setOngoing(true);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(getActivity(), MainActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getActivity());
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(RECORDING_ID, mBuilder.build());
    }

    class MyObserver extends ContentObserver {
        // left blank below constructor for this Contact observer example to work
        // or if you want to make this work using Handler then change below registering  //line
        public MyObserver() {
            super(null);
        }

        @Override
        public void onChange(boolean selfChange) {
            this.onChange(selfChange, null);
            //Log.e("someChange", "~~~~~~" + selfChange);
            // Override this method to listen to any changes
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            // depending on the handler you might be on the UI
            // thread, so be cautious!
            //Log.e("someChange1", "~~~~~~" + selfChange);
            //Log.e("uri", uri.toString());


            updateView(uri);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
        if(mChart != null && mChart.getData() != null &&
                mChart.getData().getDataSetCount() > 0) {
            try {
                mChart.clear();
                mChart.clearValues();
            } catch (Exception e) {}
        }
        if(mChronometer != null) {
            mChronometer.stop();
            mChronometer.setVisibility(View.GONE);
        }
    }

    protected BroadcastReceiver mReceiver = new BroadcastReceiver()
    {
        @Override
        public void onReceive (Context context, Intent intent)
        {
            // here you receive the response from the service
            if (intent.getAction().equals(DataCollectorService.ACTION_PONG))
            {
                Log.e("test performer", "Ping Received");
                isSvcRunning = true;
                isRecording = true;
                toggleButtonState(android.R.drawable.ic_media_pause);
                if(mChronometer != null) {
                    mChronometer.setVisibility(View.VISIBLE);
                    long tempTime = Calendar.getInstance().getTimeInMillis();
                    mChronometer.setBase(SystemClock.elapsedRealtime() -
                            (tempTime -
                                    getActivity().getSharedPreferences(Constants.SP_NAME__SERVER_STATUS, Context.MODE_PRIVATE)
                                            .getLong(Constants.SP_SESSION_SERIAL, tempTime)));
                    mChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener(){
                        @Override
                        public void onChronometerTick(Chronometer cArg) {
                            long time = SystemClock.elapsedRealtime() - cArg.getBase();
                            int h   = (int)(time /3600000);
                            int m = (int)(time - h*3600000)/60000;
                            int s= (int)(time - h*3600000- m*60000)/1000 ;
                            String hh = h < 10 ? "0"+h: h+"";
                            String mm = m < 10 ? "0"+m: m+"";
                            String ss = s < 10 ? "0"+s: s+"";
                            cArg.setText(hh+":"+mm+":"+ss);
                        }
                    });
                    mChronometer.start();
                } else {
                    mChronometer.setVisibility(View.VISIBLE);
                    long tempTime = Calendar.getInstance().getTimeInMillis();
                    mChronometer.setBase(SystemClock.elapsedRealtime() -
                            (tempTime -
                                    getActivity().getSharedPreferences(Constants.SP_NAME__SERVER_STATUS, Context.MODE_PRIVATE)
                                            .getLong(Constants.SP_SESSION_SERIAL, tempTime)));
                    mChronometer.start();
                }

                isRecording = true;
                myObserver = new MyObserver();
                getActivity().getContentResolver()
                        .registerContentObserver(SensorDataProvider.Constants.SensorURL, true,
                                myObserver);


                encoder1TV.setVisibility(View.VISIBLE);
                encoder2TV.setVisibility(View.VISIBLE);
            }
        }
    };



    @Override
    public void onResume() {
        super.onResume();
        Log.e("test performer", "OnResume");
        LocalBroadcastManager manager = LocalBroadcastManager.getInstance(getActivity().getApplicationContext());
        manager.registerReceiver(mReceiver, new IntentFilter(DataCollectorService.ACTION_PONG));
        // the service will respond to this broadcast only if it's running
        manager.sendBroadcast(new Intent(DataCollectorService.ACTION_PING));
        if(isSvcRunning) {
            isRecording = true;
            if(mChronometer != null) {
                mChronometer.setVisibility(View.VISIBLE);
                long tempTime = Calendar.getInstance().getTimeInMillis();
                mChronometer.setBase(SystemClock.elapsedRealtime() -
                        (tempTime -
                        getActivity().getSharedPreferences(Constants.SP_NAME__SERVER_STATUS, Context.MODE_PRIVATE)
                                .getLong(Constants.SP_SESSION_SERIAL, tempTime)));
                mChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener(){
                    @Override
                    public void onChronometerTick(Chronometer cArg) {
                        long time = SystemClock.elapsedRealtime() - cArg.getBase();
                        int h   = (int)(time /3600000);
                        int m = (int)(time - h*3600000)/60000;
                        int s= (int)(time - h*3600000- m*60000)/1000 ;
                        String hh = h < 10 ? "0"+h: h+"";
                        String mm = m < 10 ? "0"+m: m+"";
                        String ss = s < 10 ? "0"+s: s+"";
                        cArg.setText(hh+":"+mm+":"+ss);
                    }
                });
                mChronometer.start();
            }
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        if(myObserver != null)
            getActivity().getContentResolver()
                    .unregisterContentObserver(myObserver);


    }

    private void updateView(Uri uri) {
        if(isVisible()) {
            final Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, DBContract.DataTable.COLUMN_ID + " DESC limit 1");
            if (cursor != null) {
                cursor.moveToFirst();

                if (mChart != null) {
                    Entry acX, acY, acZ;
                    final long id = cursor.getLong(cursor.getColumnIndex(DBContract.DataTable.COLUMN_ID));
                    acX = new Entry(id, cursor.getLong(cursor.getColumnIndex(DBContract.DataTable.COLUMN_AC_X)));
                    acY = new Entry(id, cursor.getLong(cursor.getColumnIndex(DBContract.DataTable.COLUMN_AC_Y)));
                    acZ = new Entry(id, cursor.getLong(cursor.getColumnIndex(DBContract.DataTable.COLUMN_AC_Z)));
                    //Log.e("vals",cursor.getString(cursor.getColumnIndex(DBContract.DataTable.COLUMN_CLIENT_TIME)) + ", " +
                    //        cursor.getString(cursor.getColumnIndex(DBContract.DataTable.COLUMN_AC_X)));
                    final LineDataSet set1, set2, set3;
                    if (mChart.getData() != null &&
                            mChart.getData().getDataSetCount() > 0) {
                        set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
                        set2 = (LineDataSet) mChart.getData().getDataSetByIndex(1);
                        set3 = (LineDataSet) mChart.getData().getDataSetByIndex(2);
                        set1.addEntry(acX);
                        set2.addEntry(acY);
                        set3.addEntry(acZ);
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //mChart.getData().notifyDataChanged();
                                // let the chart know it's data has changed
                                //mChart.notifyDataSetChanged();

                                //mChart.animateXY(100,50);


                                mChart.getData().notifyDataChanged();

                                // let the chart know it's data has changed
                                mChart.notifyDataSetChanged();

                                // limit the number of visible entries
                                mChart.setVisibleXRangeMaximum(15);
                                // mChart.setVisibleYRange(30, AxisDependency.LEFT);
                                // move to the latest entry
                                mChart.moveViewToX(id);
                            }
                        });
                    } else {
                        // create a dataset and give it a type
                        ArrayList<Entry> tmp1 = new ArrayList<Entry>();
                        ArrayList<Entry> tmp2 = new ArrayList<Entry>();
                        ArrayList<Entry> tmp3 = new ArrayList<Entry>();
                        tmp1.add(acX);
                        tmp2.add(acY);
                        tmp3.add(acZ);

                        int colorIndex = new Random().nextInt(ColorTemplate.COLORFUL_COLORS.length - 1);
                        set1 = new LineDataSet(tmp1, "Ac_X");

                        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
                        set1.setColor(ColorTemplate.COLORFUL_COLORS[colorIndex]);
                        set1.setCircleColor(ColorTemplate.COLORFUL_COLORS[colorIndex]);
                        set1.setLineWidth(2f);
                        set1.setCircleRadius(3f);
                        set1.setFillAlpha(65);
                        set1.setFillColor(ColorTemplate.COLORFUL_COLORS[colorIndex]);
                        set1.setHighLightColor(Color.rgb(244, 117, 117));
                        set1.setDrawCircleHole(true);
                        //set1.setFillFormatter(new MyFillFormatter(0f));
                        //set1.setDrawHorizontalHighlightIndicator(false);
                        //set1.setVisible(false);
                        set1.setCircleHoleColor(Color.WHITE);

                        // create a dataset and give it a type
                        set2 = new LineDataSet(tmp2, "Ac_Y");
                        set2.setAxisDependency(YAxis.AxisDependency.LEFT);
                        set2.setColor(ColorTemplate.COLORFUL_COLORS[(colorIndex + 1) %
                                ColorTemplate.COLORFUL_COLORS.length]);
                        set2.setCircleColor(ColorTemplate.COLORFUL_COLORS[(colorIndex + 1) %
                                ColorTemplate.COLORFUL_COLORS.length]);
                        set2.setLineWidth(2f);
                        set2.setCircleRadius(3f);
                        set2.setFillAlpha(65);
                        set2.setFillColor(ColorTemplate.COLORFUL_COLORS[(colorIndex + 1) %
                                ColorTemplate.COLORFUL_COLORS.length]);
                        set2.setDrawCircleHole(true);
                        set2.setCircleHoleColor(Color.WHITE);
                        set2.setHighLightColor(Color.rgb(244, 117, 117));
                        //set2.setFillFormatter(new MyFillFormatter(900f));
                        // create a dataset and give it a type
                        set3 = new LineDataSet(tmp3, "Ac_Z");
                        set3.setAxisDependency(YAxis.AxisDependency.LEFT);
                        set3.setColor(ColorTemplate.COLORFUL_COLORS[(colorIndex + 2) %
                                ColorTemplate.COLORFUL_COLORS.length]);
                        set3.setCircleColor(ColorTemplate.COLORFUL_COLORS[(colorIndex + 2) %
                                ColorTemplate.COLORFUL_COLORS.length]);
                        set3.setLineWidth(2f);
                        set3.setCircleRadius(3f);
                        set3.setFillAlpha(65);
                        set3.setFillColor(ColorTemplate.COLORFUL_COLORS[(colorIndex + 2) %
                                ColorTemplate.COLORFUL_COLORS.length]);
                        set3.setDrawCircleHole(true);
                        set3.setCircleHoleColor(Color.WHITE);
                        set3.setHighLightColor(Color.rgb(244, 117, 117));

                        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
                        dataSets.add(set1); // add the datasets
                        dataSets.add(set2);
                        dataSets.add(set3);

                        // create a data object with the datasets
                        final LineData data = new LineData(dataSets);
                        data.setValueTextColor(Color.WHITE);
                        data.setValueTextSize(9f);

                        // set data
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mChart.setData(data);
                                //mChart.getData().notifyDataChanged();
                                // let the chart know it's data has changed
                                //mChart.notifyDataSetChanged();

                                //mChart.animateXY(100,50);


                                mChart.getData().notifyDataChanged();

                                // let the chart know it's data has changed
                                mChart.notifyDataSetChanged();

                                // limit the number of visible entries
                                mChart.setVisibleXRangeMaximum(15f);
                                // mChart.setVisibleYRange(30, AxisDependency.LEFT);

                                // move to the latest entry
                                //mChart.moveViewToX(mChart.getData().getEntryCount());
                                // get the legend (only possible after setting data)
                                Legend l = mChart.getLegend();

                                // modify the legend ...
                                // l.setPosition(LegendPosition.LEFT_OF_CHART);
                                l.setForm(Legend.LegendForm.LINE);
                                l.setTextSize(11f);
                                l.setTextColor(Color.BLACK);
                                l.setPosition(Legend.LegendPosition.BELOW_CHART_LEFT);
                                l.setYOffset(11f);
                                //mChart.invalidate();

                            }
                        });


                    }

                }
                if (encoder1TV != null) {
                    final String str1 = "<font color=#cc0029>" + getString(R.string.encoder_1) +
                            "</font> <font color=#3f51b5>" +
                            cursor.getString(cursor.getColumnIndex(DBContract.DataTable.COLUMN_ENCODER_1))
                            + "</font>";
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (versionCheck) {
                                encoder1TV.setText( Html.fromHtml(str1, Html.FROM_HTML_MODE_LEGACY));
                            } else {
                                encoder1TV.setText(Html.fromHtml(str1));
                            }

                        }
                    });

                }
                if (encoder2TV != null) {
                    final String str2 = "<font color=#cc0029>" + getString(R.string.encoder_2) +
                            "</font> <font color=#3f51b5>" +
                            cursor.getString(cursor.getColumnIndex(DBContract.DataTable.COLUMN_ENCODER_2))
                            + "</font>";
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (versionCheck) {
                                encoder2TV.setText( Html.fromHtml(str2, Html.FROM_HTML_MODE_LEGACY));
                            } else {
                                encoder2TV.setText(Html.fromHtml(str2));
                            }

                        }
                    });
                }

//                cursor.close();
            }


        }
    }

    public class WifiApManager {
        private final WifiManager mWifiManager;

        public WifiApManager(Context context) {
            mWifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        }

        /*the following method is for getting the wifi hotspot state*/

        public WIFI_AP_STATE getWifiApState() {
            try {
                Method method = mWifiManager.getClass().getMethod("getWifiApState");

                int tmp = ((Integer) method.invoke(mWifiManager));

                // Fix for Android 4
                if (tmp > 10) {
                    tmp = tmp - 10;
                }

                return WIFI_AP_STATE.class.getEnumConstants()[tmp];
            } catch (Exception e) {
                Log.e(this.getClass().toString(), "", e);
                return WIFI_AP_STATE.WIFI_AP_STATE_FAILED;
            }
        }

        /**
         * Return whether Wi-Fi Hotspot is enabled or disabled.
         *
         * @return {@code true} if Wi-Fi AP is enabled
         * @see #getWifiApState()
         */
        public boolean isWifiApEnabled() {
            return getWifiApState() == WIFI_AP_STATE.WIFI_AP_STATE_ENABLED;
        }

    }

    public enum WIFI_AP_STATE {
        WIFI_AP_STATE_DISABLING,
        WIFI_AP_STATE_DISABLED,
        WIFI_AP_STATE_ENABLING,
        WIFI_AP_STATE_ENABLED,
        WIFI_AP_STATE_FAILED
    }

}
