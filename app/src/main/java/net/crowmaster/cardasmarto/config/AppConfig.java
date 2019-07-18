package net.crowmaster.cardasmarto.config;

//import android.support.multidex.MultiDexApplication;
/*import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;*/

import android.app.Application;
import android.util.Log;

import androidx.work.Configuration;
import androidx.work.WorkManager;

import com.facebook.stetho.Stetho;

import net.crowmaster.cardasmarto.utils.Installation;
import net.crowmaster.cardasmarto.workers.DataSyncWorker;

/**
 * Created by root on 7/18/16.
 */
public class AppConfig extends Application {
    private static AppConfig instance = null;
    private static String uuid;

    /**
     * Please remember to modify [command] value in {@link DataSyncWorker#isConnected()} method
     * in case the server is not on the internet
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
        instance = this;
        uuid = Installation.id(this);

        /*
          The code below is will enable work manager which will be responsible for automatically syncing the data with the server
        */

//        Configuration configuration = new Configuration.Builder()
//                .setMinimumLoggingLevel(Log.VERBOSE)
//                .build();
//
//        WorkManager.initialize(this, configuration);
    }

    private static void checkInstance(){
        if(instance == null)
            throw new IllegalStateException("App Not Created Yet!");
    }

    public static AppConfig getInstance(){
        checkInstance();
        return instance;
    }

    public String getUuid() {
        return uuid;
    }
}
