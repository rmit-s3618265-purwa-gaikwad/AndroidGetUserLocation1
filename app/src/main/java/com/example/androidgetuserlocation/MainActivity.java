package com.example.androidgetuserlocation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Timer;
import java.util.TimerTask;

import static android.os.Build.VERSION_CODES.M;

public class MainActivity extends Activity {

    final String TAG = "MainActivity.java";
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    Button buttonStart;
    Button buttonStop;
    String phoneNo;
    String message;
    LocationResult locationResult;
    LocationHelper locationHelper;
    double latitude;
    double longitude;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        // to get location updates, initialize LocationResult
        this.locationResult = new LocationResult() {
            //@Override
            public void gotLocation(Location location) {

                //Got the location!
                if (location != null) {

                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    //message = Double.toString(latitude) + "   " + Double.toString(longitude);
                    Log.e(TAG, "lat: " + latitude + ", long: " + longitude);

                    sendSMSMessage();
                    // here you can save the latitude and longitude values
                    // maybe in your text file or database

                } else {
                    Log.e(TAG, "Location is null.");
                }

            }

        };

        // initialize our useful class,
        this.locationHelper = new LocationHelper();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    protected void sendSMSMessage() {
        String your_data;

            Intent i = getIntent();
            your_data = i.getStringExtra(Intent.EXTRA_TEXT);
            Toast.makeText(getApplicationContext(), your_data,
                    Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+"+61450547350"));
        //startActivity(intent);

        //MainActivity.this.getApplicationContext().startActivity(intent);
        phoneNo = your_data;
        message = "I am in danger. Click here to see this location in google maps : http://maps.google.com/maps?q=" + latitude + "," + longitude;
        Log.d(TAG, "inside the sms function");
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS)
                == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "inside first if");
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.SEND_SMS)) {
                Log.d(TAG, "inside second if");
            } else {
                Log.d(TAG, "inside else");
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }
        locationHelper.stopGettingLocationUpdates();

        final AlertDialog dlg = new AlertDialog.Builder(MainActivity.this)
                .setTitle("Emerge")
                .setMessage("If you sent by mistake, click the button")
                .setPositiveButton("Mistake", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getCallingActivity();
                    System.exit(0);
                    }
                }).show();
        final Timer t = new Timer();
        t.schedule(new TimerTask() {
            public void run() {
                dlg.dismiss(); // when the task active then close the dialog
                t.cancel(); // also just top the timer thread, otherwise, you may receive a crash report
            }
        }, 5000);
        onStop();
        //finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage(phoneNo, null, message, null, null);
                    Toast.makeText(getApplicationContext(), "SMS sent.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

    }

    // prevent exiting the app using back pressed
    // so getting user location can run in the background
    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(MainActivity.this)
                .setTitle("User Location App")
                .setMessage("This will end the app. Use the home button instead.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        dialog.cancel();
                    }
                }).show();

    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
        locationHelper.getLocation(this, locationResult);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }

    class LocationHelper {

        final String TAG = "LocationHelper.java";

        Timer timer1;
        LocationManager lm;
        LocationResult locationResult;
        boolean gps_enabled = false;
        boolean network_enabled = false;
        Context lhcontext;
        @TargetApi(23)
        public boolean getLocation(Context context, LocationResult result) {

            // I use LocationResult callback class to pass location value from
            // LocationHelper to user code.
            locationResult = result;
            lhcontext = context;
            if (lm == null) {
                lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            }

            // exceptions will be thrown if provider is not permitted.
            try {
                gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // don't start listeners if no provider is enabled
            if (!gps_enabled && !network_enabled) {
                return false;
            }
            if ( Build.VERSION.SDK_INT >= 23 &&
                    ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission( context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
            // if gps is enabled, get location updates
            if (gps_enabled) {
                Log.e(TAG, "gps_enabled, requesting updates.");
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
            }

            // if network is enabled, get location updates
            if (network_enabled) {
                Log.e(TAG, "network_enabled, requesting updates.");
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
            }

            // the timer
            timer1 = new Timer();
            timer1.schedule(new GetLastLocation(), 20000);

            return true;
        }

        LocationListener locationListenerGps = new LocationListener() {

            public void onLocationChanged(Location location) {

                // gave a location, cancel the timer
                timer1.cancel();

                // put the location value
                locationResult.gotLocation(location);

                // if you want to stop listening to gps location updates, un-comment the code below
                if ( Build.VERSION.SDK_INT >= 23 &&
                        ContextCompat.checkSelfPermission( lhcontext, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission( lhcontext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
			lm.removeUpdates(this);
			lm.removeUpdates(locationListenerNetwork);

            }

            public void onProviderDisabled(String provider) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
        };

        LocationListener locationListenerNetwork = new LocationListener() {

            public void onLocationChanged(Location location) {

                // gave a location, cancel the timer
                timer1.cancel();

                // put the location value
                locationResult.gotLocation(location);

                // if you want to stop listening to network location updates, un-comment the code below
                if ( Build.VERSION.SDK_INT >= 23 &&
                        ContextCompat.checkSelfPermission( lhcontext, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission( lhcontext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
			lm.removeUpdates(this);
			lm.removeUpdates(locationListenerGps);

            }

            public void onProviderDisabled(String provider) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
        };

        // stop listening to location updates
        public void stopGettingLocationUpdates() {
            if ( Build.VERSION.SDK_INT >= 23 &&
                    ContextCompat.checkSelfPermission( lhcontext, android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission( lhcontext, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            try {
                lm.removeUpdates(locationListenerGps);
                lm.removeUpdates(locationListenerNetwork);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        class GetLastLocation extends TimerTask {

            @Override
            public void run() {

                // In my case, I do not return the last known location, so I DO NOT remove the updates, just return a location value of null
                // or else, if you need the opposite un-comment the comment below

			/*
			lm.removeUpdates(locationListenerGps);
			lm.removeUpdates(locationListenerNetwork);

			Location net_loc = null, gps_loc = null;
			if (gps_enabled){
				gps_loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			}

			if (network_enabled){
				net_loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
			}

			// if there are both values use the latest one
			if (gps_loc != null && net_loc != null) {

				if (gps_loc.getTime() > net_loc.getTime()){
					locationResult.gotLocation(gps_loc);
				}else{
					locationResult.gotLocation(net_loc);
				}
				return;
			}

			if (gps_loc != null) {
				locationResult.gotLocation(gps_loc);
				return;
			}
			if (net_loc != null) {
				locationResult.gotLocation(net_loc);
				return;
			}
			*/

                locationResult.gotLocation(null);
            }
        }



    }
    public static abstract class LocationResult {
        public abstract void gotLocation(Location location);
    }
}
