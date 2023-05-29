package com.example.roommatefinderapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.roommatefinderapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private boolean isLocationPermissionsGranted = false;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERM_REQ_CODE = 1234;
    private Button saveBtn;
    private String latLong = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        saveBtn = (Button) findViewById(R.id.saveButton);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (latLong.isEmpty()){
                    Toast.makeText(MapsActivity.this, "Lütfen konum seçiniz.", Toast.LENGTH_SHORT).show();
                }
                else {
                    Intent intent = new Intent(MapsActivity.this, BottomNavActivity.class);
                    intent.putExtra("location", latLong);
                    MapsActivity.this.startActivity(intent);

                }
            }
        });
        getLocationPermission();
        initializeMap();
    }


    private void initializeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapClickListener(this::handleMapClick);

        // Default: Istanbul
        LatLng istanbul = new LatLng(41.015137, 28.979530);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(istanbul));
    }

    private void handleMapClick(LatLng latLng) {
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng).title("Konum"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        latLong = String.valueOf(latLng.latitude)+","+String.valueOf(latLng.longitude);
    }

    private void getLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            isLocationPermissionsGranted = true;
        } else {
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERM_REQ_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        isLocationPermissionsGranted = false;
        if (requestCode == LOCATION_PERM_REQ_CODE) {
            if (grantResults.length > 0) {
                for (int res : grantResults) {
                    if (res != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                }
                isLocationPermissionsGranted = true;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
