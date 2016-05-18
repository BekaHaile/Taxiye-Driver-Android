package com.example.socomo.locationfetcher;

import android.app.Activity;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity implements LocationUpdate{

    LocationFetcher locationFetcher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (locationFetcher == null) {
            locationFetcher = new LocationFetcher(MainActivity.this, 1000, 1);
        }

        locationFetcher.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            locationFetcher.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLocationChanged(Location location, int priority) {
        Log.v("location ","---> "+location.getLatitude()+", "+location.getLongitude());
    }
}
