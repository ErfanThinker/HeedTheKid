package net.crowmaster.cardasmarto.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import net.crowmaster.cardasmarto.fragments.TestPerformerFragment;
import net.crowmaster.cardasmarto.providers.SensorDataProvider;
import net.crowmaster.cardasmarto.services.DataCollectorService;

public class WifiBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION .equals(action)) {
            SupplicantState state = intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
            if (SupplicantState.isValidState(state)
                    && state == SupplicantState.COMPLETED) {

                boolean connected = checkConnectedToDesiredWifi(context);
                if (connected) {
                    context.getApplicationContext().startService(new Intent(context, DataCollectorService.class));

                }
            }
        }
    }

    /** Detect you are connected to a specific network. */
    private boolean checkConnectedToDesiredWifi(Context context) {
        boolean connected = false;

//        String desiredMacAddress = "router mac address";
        String desiredSSID = "Mcar";

        WifiManager wifiManager =
                (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        WifiInfo wifi = wifiManager.getConnectionInfo();
        if (wifi != null) {
            // get current router Mac address
            String bssid = wifi.getBSSID();
            String ssid = wifi.getSSID().replaceAll("\"", "");
            connected = desiredSSID.equals(ssid);


        }

        return connected;
    }
}