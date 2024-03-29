package net.crowmaster.cardasmarto.utils;

/**
 * Created by root on 6/7/16.
 * Can be used to manage hotspot on android versions prior to 6
 */

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import java.lang.reflect.Method;

import static net.crowmaster.cardasmarto.services.DataCollectorService.TAG;


public class HsManager {

    //check whether wifi hotspot on or off
//    private WifiManager.LocalOnlyHotspotReservation mReservation;
    public static boolean isApOn(Context context) {
        WifiManager wifimanager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        try {
            Method method = wifimanager.getClass().getDeclaredMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(wifimanager);
        }
        catch (Throwable ignored) {}
        return false;
    }

//    @RequiresApi(api = Build.VERSION_CODES.O)
//    private void configApState(Context appContext, boolean shallTurnOn, String SSID, String PASS) {
//        WifiManager manager = (WifiManager) appContext.getSystemService(Context.WIFI_SERVICE);
//        WifiConfiguration wificonfiguration = new WifiConfiguration();
//        wificonfiguration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
//        wificonfiguration.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
//        wificonfiguration.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
//        wificonfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
//        wificonfiguration.SSID = SSID;
//        wificonfiguration.preSharedKey = PASS;
//        manager.startLocalOnlyHotspot(new WifiManager.LocalOnlyHotspotCallback() {
//
//            @Override
//            public void onStarted(WifiManager.LocalOnlyHotspotReservation reservation) {
//                super.onStarted(reservation);
//                Log.d(TAG, "Wifi Hotspot is on now");
//                mReservation = reservation;
//            }
//
//            @Override
//            public void onStopped() {
//                super.onStopped();
//                Log.d(TAG, "onStopped: ");
//            }
//
//            @Override
//            public void onFailed(int reason) {
//                super.onFailed(reason);
//                Log.d(TAG, "onFailed: ");
//            }
//        }, new Handler());
//    }
//
//    @RequiresApi(api = Build.VERSION_CODES.O)
//    private void turnOffHotspot() {
//        if (mReservation != null) {
//            mReservation.close();
//        }
//    }

    // toggle wifi hotspot on or off
    public static boolean configApState(Context context, boolean shallTurnOn, String SSID, String PASS) {
        WifiManager wifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiConfiguration wificonfiguration = new WifiConfiguration();
        wificonfiguration.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
        wificonfiguration.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        wificonfiguration.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        wificonfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        wificonfiguration.SSID = SSID;
        wificonfiguration.preSharedKey = PASS;
        try {
            // if WiFi is on, turn it off
            if(wifimanager.isWifiEnabled()) {
                wifimanager.setWifiEnabled(false);
                Log.e("mTag", "Turning off wifi now");
            }
            Method method = wifimanager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            method.invoke(wifimanager, wificonfiguration, shallTurnOn);
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}