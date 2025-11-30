package de.dennisguse.opentracks;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Build;
import android.os.PowerManager;
import android.util.Log;

import java.lang.reflect.Method;

import de.dennisguse.opentracks.settings.PreferencesUtils;
import de.dennisguse.opentracks.util.ExceptionHandler;
import de.dennisguse.opentracks.util.PowerSaveModeReceiver;

/**
 * Code that is executed when the application starts.
 * <p>
 * NOTE: How often actual application startup happens depends on the OS.
 * Not every start of an activity will trigger this.
 */
public class Startup extends Application {

    private static final String TAG = Startup.class.getSimpleName();
    private BroadcastReceiver powerSaveModeReceiver;

    @Override
    public void onCreate() {
        super.onCreate();

        // Include version information into stack traces.
        Log.i(TAG, BuildConfig.APPLICATION_ID + "; BuildType: " + BuildConfig.BUILD_TYPE + "; VersionName: " + BuildConfig.VERSION_NAME + "/" + BuildConfig.VERSION_NAME_FULL + " VersionCode: " + BuildConfig.VERSION_CODE);

        PreferencesUtils.initPreferences(this);
        // Set default values of preferences on first start.
        PreferencesUtils.resetPreferences(this, false);
        PreferencesUtils.applyDefaultUnit();
        PreferencesUtils.applyNightModeAndDynamicColors(this);

        // Register PowerSaveModeReceiver
        powerSaveModeReceiver = new PowerSaveModeReceiver();
        IntentFilter filter = new IntentFilter(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED);
        registerReceiver(powerSaveModeReceiver, filter);
    }

    @Override
    protected void attachBaseContext(final Context base) {
        super.attachBaseContext(base);

        // handle crashes only outside the crash reporter activity/process
        if (!isCrashReportingProcess()) {
            Thread.UncaughtExceptionHandler defaultPlatformHandler = Thread.getDefaultUncaughtExceptionHandler();
            ExceptionHandler crashReporter = new ExceptionHandler(this, defaultPlatformHandler);
            Thread.setDefaultUncaughtExceptionHandler(crashReporter);
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        // Unregister PowerSaveModeReceiver
        if (powerSaveModeReceiver != null) {
            unregisterReceiver(powerSaveModeReceiver);
        }
    }

    private boolean isCrashReportingProcess() {
        String processName = "";
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            // Using the same technique as Application.getProcessName() for older devices
            // Using reflection since ActivityThread is an internal API
            try {
                @SuppressLint("PrivateApi")
                Class<?> activityThread = Class.forName("android.app.ActivityThread");
                @SuppressLint("DiscouragedPrivateApi") Method getProcessName = activityThread.getDeclaredMethod("currentProcessName");
                processName = (String) getProcessName.invoke(null);
            } catch (Exception ignored) {
            }
        } else {
            processName = Application.getProcessName();
        }
        return processName != null && processName.endsWith(":crash");
    }

    /**
     * Returns the name of the database used by SQLiteOpenHelper.
     * See {@link android.database.sqlite.SQLiteOpenHelper} for details.
     * @return SQLite database name.
     */
    public String getDatabaseName() {
        return "database.db";
    }
}
