package de.dennisguse.opentracks.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.widget.Toast;

import de.dennisguse.opentracks.R;

public class PowerSaveModeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (PowerManager.ACTION_POWER_SAVE_MODE_CHANGED.equals(intent.getAction())) {
            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            if (powerManager != null && powerManager.isPowerSaveMode()) {
                Toast.makeText(context, R.string.power_save_mode_warning, Toast.LENGTH_LONG).show();
            } else {
                // Optionally, inform the user that power save mode is off
                // Toast.makeText(context, R.string.power_save_mode_off_info, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
