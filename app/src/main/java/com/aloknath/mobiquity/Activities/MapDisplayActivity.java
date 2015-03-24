package com.aloknath.mobiquity.Activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;
import com.aloknath.mobiquity.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ALOKNATH on 3/24/2015.
 */
public class MapDisplayActivity extends Activity implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {

    GoogleMap mMap;
    LocationClient mLocationClient;

    private double latitude;
    private double longitude;
    private boolean mShowMap;
    private static final float DEFAULTZOOM = 15;
    private static final int GPS_ERRORDIALOG_REQUEST = 9001;
    private ArrayList<Marker> markers = new ArrayList<>();
    private List<String> imageCoordinates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        if (isOnline()) {
            if (servicesOK()) {
                setContentView(R.layout.map_display_layout);

                Bundle bundle = getIntent().getExtras();
                imageCoordinates = (List<String>) bundle.getSerializable("ImageCoordinates");

                if (initMap()) {

                    LocationManager locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
                    if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                        mLocationClient = new LocationClient(MapDisplayActivity.this, MapDisplayActivity.this, MapDisplayActivity.this);
                        mLocationClient.connect();
                        mShowMap = true;
                    }else{
                        Toast.makeText(this, "Location Manager Not Available", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(this, "Map not available!", Toast.LENGTH_SHORT).show();
                }
            }
        }else {
            Toast.makeText(this, "Network isn't available", Toast.LENGTH_LONG).show();
        }

    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean servicesOK() {
        int isAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);

        if (isAvailable == ConnectionResult.SUCCESS) {
            return true;
        }
        else if (GooglePlayServicesUtil.isUserRecoverableError(isAvailable)) {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(isAvailable, this, GPS_ERRORDIALOG_REQUEST);
            dialog.show();
        }
        else {
            Toast.makeText(this, "Can't connect to Google Play services", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    private boolean initMap() {
        if(mMap == null){
            MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
            mMap = mapFragment.getMap();
        }
        return (mMap != null);
    }


    @Override
    public void onBackPressed()
    {
        removeEverything();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home){
            removeEverything();
            finish();
        }
        return true;
    }

    @Override
    public void onConnected(Bundle bundle) {

        Location mLocation = mLocationClient.getLastLocation();
        if(mLocation == null){
            Toast.makeText(this, "My Location is not available", Toast.LENGTH_SHORT).show();
        }else {
            try {
                displayMyLocation();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDisconnected() {
        Toast.makeText(this,"Disconnected from the location services", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        String msg = "Location" + location.getLatitude() + "," + location.getLongitude();
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(this,"Connection the location services Failed", Toast.LENGTH_SHORT).show();
    }


    private void displayMyLocation() throws IOException {

        if(mShowMap){
            if(mLocationClient.isConnected()) {
                Location mLocation = mLocationClient.getLastLocation();
                if (mLocation == null) {
                    Toast.makeText(this, "My Location is not available", Toast.LENGTH_LONG).show();
                } else {
                    gotoCurrentLocation();
                    Toast.makeText(this, "I'm Here", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this, "LocationClient is Not Connected", Toast.LENGTH_LONG).show();
            }
        }
    }

    // Goto your current location and display the images taken around your area.
    protected void gotoCurrentLocation() throws IOException {

        Location mLocation = mLocationClient.getLastLocation();
        if(mLocation == null){
            Toast.makeText(this, "My Location is not available", Toast.LENGTH_SHORT).show();
        }else{
            if(imageCoordinates.size()>0){
                // Display The Image Markers Onto the Map

                for(String coordinate: imageCoordinates){
                    Log.i(" The Name of the image file 1234: ", coordinate);
                    // First Extract the longitude and latitude from the names passed
                    String[] splited = coordinate.split(":");
                    longitude = Double.parseDouble(splited[1]);
                    latitude = Double.parseDouble(splited[2].split(".jpg")[0]);
                    String locality = splited[0];

                    // Set the Markers for the images as per their Latitudes and Longitudes
                    setMarker(latitude, longitude, locality);

                    // Set a Latitude and Longitude of where you are
                    LatLng latLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, DEFAULTZOOM);
                    mMap.animateCamera(cameraUpdate);
                    Geocoder gc = new Geocoder(this);
                    List<Address> list = gc.getFromLocation(mLocation.getLatitude(), mLocation.getLongitude(), 1);
                    Address add = list.get(0);
                    locality = "This is Me";
                    String country = add.getCountryName();
                    setMarker(country, locality, mLocation.getLatitude(), mLocation.getLongitude());

                }
            }else {

                LatLng latLng = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, DEFAULTZOOM);
                mMap.animateCamera(cameraUpdate);
                Geocoder gc = new Geocoder(this);
                List<Address> list = gc.getFromLocation(mLocation.getLatitude(), mLocation.getLongitude(), 1);
                Address add = list.get(0);
                String locality = "This is Me";
                String country = add.getCountryName();
                latitude = mLocation.getLatitude();
                longitude = mLocation.getLongitude();
                Log.i("The Latitude + Longitude:", String.valueOf(latitude) + "+" + String.valueOf(longitude));
                setMarker(country, locality, mLocation.getLatitude(), mLocation.getLongitude());
            }
        }
    }

    // Add Markers For Each Image
    private void setMarker(double lat, double lng, String locality) {

        MarkerOptions markerOptions = new MarkerOptions()
                .position(new LatLng(lat, lng))
                .anchor(.5f, .5f)
                .title(locality)
                .icon(BitmapDescriptorFactory.defaultMarker(
                        BitmapDescriptorFactory.HUE_CYAN
                ))
                .draggable(true);

        markers.add(mMap.addMarker(markerOptions));

    }

    public void setMarker(String country, String locality, double lat, double lng) {
        if(markers.size() == 2){
            removeEverything();
        }
        MarkerOptions markerOptions = new MarkerOptions()
                .title(locality)
                .position(new LatLng(lat, lng))
                .anchor(.5f, .5f)
                .icon(BitmapDescriptorFactory.defaultMarker(
                        BitmapDescriptorFactory.HUE_CYAN
                ))
                .draggable(true);
        if (country.length() > 0) {
            markerOptions.snippet(country);
        }
        markers.add(mMap.addMarker(markerOptions));
    }

    // Remove All the Markers
    private void removeEverything() {

        for (Marker marker : markers) {
            marker.remove();
        }
        markers.clear();
    }

}
