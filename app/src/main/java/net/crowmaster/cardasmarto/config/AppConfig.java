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

/**
 * Created by root on 7/18/16.
 */
public class AppConfig extends Application {
    private static AppConfig instance = null;
    private static String uuid;
    @Override
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
        instance = this;
        uuid = Installation.id(this);
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
