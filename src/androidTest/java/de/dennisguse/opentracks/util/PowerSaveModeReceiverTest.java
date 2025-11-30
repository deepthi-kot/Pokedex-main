package de.dennisguse.opentracks.util;

import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.espresso.Espresso;
import androidx.test.espresso.matcher.RootMatchers;
import androidx.test.espresso.matcher.ViewMatchers;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import de.dennisguse.opentracks.R;
import de.dennisguse.opentracks.TestActivity; // Import the dummy activity

import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;

@RunWith(AndroidJUnit4.class)
public class PowerSaveModeReceiverTest {

    @Rule
    public ActivityScenarioRule<TestActivity> activityScenarioRule = new ActivityScenarioRule<>(TestActivity.class);

    @Test
    public void onReceive_powerSaveModeEnabled_showsWarningToast() {
        // Given
        Context context = ApplicationProvider.getApplicationContext();
        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);

        // Temporarily set power save mode for testing - this requires device/emulator setup or specific permissions
        // For a true instrumental test, you'd trigger this via ADB shell commands or similar.
        // Since we cannot programmatically toggle power save mode from a test without special permissions
        // or a rooted device, we rely on sending the broadcast and checking if the receiver acts on it.
        // The actual `isPowerSaveMode()` check within the receiver will reflect the device's current state.

        Intent intent = new Intent(PowerManager.ACTION_POWER_SAVE_MODE_CHANGED);

        // Act
        // Send the broadcast to the application context, which Startup.java has registered to listen for.
        context.sendBroadcast(intent);

        // Assert that a Toast is displayed with the expected text
        // This implicitly tests if the receiver was invoked and acted upon the broadcast.
        Espresso.onView(ViewMatchers.withText(R.string.power_save_mode_warning))
                .inRoot(RootMatchers.withWindowLayoutParams(Matchers.any(android.view.WindowManager.LayoutParams.class)))
                .check(matches(isDisplayed()));

        // Note: Espresso's Toast checking is notoriously flaky.
        // A robust solution might involve:
        // 1. Using a custom TestRule to intercept Toasts or modify the system service for testing.
        // 2. Making the Toast creation dependency-injectable so it can be mocked.
        // For this task, this level of Espresso Toast verification is the standard approach.
    }

    @Test
    public void onReceive_otherIntentAction_noToastShown() {
        // Given
        Context context = ApplicationProvider.getApplicationContext();
        Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED); // Use a different action

        // Act
        context.sendBroadcast(intent);

        // Assert that no Toast is displayed with the power save warning text
        Espresso.onView(ViewMatchers.withText(R.string.power_save_mode_warning))
                .inRoot(RootMatchers.withWindowLayoutParams(Matchers.any(android.view.WindowManager.LayoutParams.class)))
                .check(matches(Matchers.not(isDisplayed())));
    }
}