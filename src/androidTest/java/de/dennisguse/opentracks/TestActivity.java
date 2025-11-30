package de.dennisguse.opentracks;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class TestActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // This activity doesn't need a specific layout for this test.
        // It just needs to exist to provide a Context for the Toast.
    }
}
