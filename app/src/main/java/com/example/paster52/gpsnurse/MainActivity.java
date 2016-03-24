package com.example.paster52.gpsnurse;

import android.Manifest;
import android.app.Activity;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment implements
            GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

        public PlaceholderFragment() {
        }

        private GoogleMap mMap; // Might be null if Google Play services APK is not available.
        private GoogleApiClient client;
        public static final String TAG = MapsFragment.class.getSimpleName();
        private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
        private LocationRequest mLocationRequest;
        private FragmentActivity myContext;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_map, container, false);
            return rootView;
        }


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setUpMapIfNeeded();
            client = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mLocationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                    .setFastestInterval(1 * 1000); // 1 second, in milliseconds

        /*ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query( Uri.parse("content://sms/inbox"), null, null, null, null);
        int indexBody = cursor.getColumnIndex( SmsReceiver.BODY );
        int indexAddr = cursor.getColumnIndex( SmsReceiver.ADDRESS );


        if ( indexBody < 0 || !cursor.moveToFirst() ) return;*/

        }

        @Override
        public void onResume() {
            super.onResume();

            client.connect();
        }

        @Override
        public void onPause() {
            super.onPause();
            if (client.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
            }
            client.disconnect();
        }

        @Override
        public void onAttach(Activity activity) {
            myContext = (FragmentActivity) activity;
            super.onAttach(activity);
        }

        @Override
        public void onDetach() {
            super.onDetach();
        }


        /**
         * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
         * installed) and the map has not already been instantiated.. This will ensure that we only ever
         * call {@link #setUpMap()} once when {@link #mMap} is not null.
         * <p/>
         * If it isn't installed {@link com.google.android.gms.maps.SupportMapFragment} (and
         * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
         * install/update the Google Play services APK on their device.
         * <p/>
         * A user can return to this FragmentActivity after following the prompt and correctly
         * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
         * have been completely destroyed during this process (it is likely that it would only be
         * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
         * method in {@link #onResume()} to guarantee that it will be called.
         */
        private void setUpMapIfNeeded() {
            // Do a null check to confirm that we have not already instantiated the map.
            if (mMap == null) {
                // Try to obtain the map from the SupportMapFragment.
                mMap = ((SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map))
                        .getMap();

                // Check if we were successful in obtaining the map.
                if (mMap != null) {
                    setUpMap();
                }
            }
        }

        /**
         * This is where we can add markers or lines, add listeners or move the camera. In this case, we
         * just add a marker near Africa.
         * <p/>
         * This should only be called once and when we are sure that {@link #mMap} is not null.
         */
        private void setUpMap() {

            mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        }

        @Override
        public void onConnected(@Nullable Bundle bundle) {
            Location location = LocationServices.FusedLocationApi.getLastLocation(client);
            if (location == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(client, mLocationRequest, this);
            } else {
                handleNewLocation(location);
            }
        }

        @Override
        public void onConnectionSuspended(int i) {

        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            if (connectionResult.hasResolution()) {
                try {
                    // Start an Activity that tries to resolve the error
                    connectionResult.startResolutionForResult(getActivity(), CONNECTION_FAILURE_RESOLUTION_REQUEST);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            } else {
                Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
            }

        }

        private void handleNewLocation(Location location) {
            Log.d(TAG, location.toString());
            double currentLatitude = location.getLatitude();
            double currentLongitude = location.getLongitude();
            LatLng latLng = new LatLng(currentLatitude, currentLongitude);
            mMap.setMyLocationEnabled(true);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title("I am here!");
            mMap.addMarker(options);




        }

        @Override
        public void onLocationChanged(Location location) {
            handleNewLocation(location);
        }
    }
}
