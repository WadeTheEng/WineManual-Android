package au.net.winehound.service;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import java.util.concurrent.TimeUnit;

import au.net.winehound.LogTags;


/**
 * A wrapper around the google play services location client that gets low power
 * location updates every 5 min.  You must call the onCreate and onDestroy methods
 * at appropriate times in your activity/fragment
 */
public class LocationHelper {

    private LocationClient locationClient;
    private LocationListener locationListener;

    public void onCreate(Context context){

        locationClient = new LocationClient(context, new GooglePlayServicesClient.ConnectionCallbacks() {
            @Override
            public void onConnected(Bundle bundle) {
                Log.i(LogTags.UI, "Connected to location client, requesting updates");
                LocationRequest request = LocationRequest.create();
                request.setInterval(TimeUnit.MINUTES.toMillis(5));
                request.setPriority(LocationRequest.PRIORITY_LOW_POWER);

                locationListener = new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        Log.i(LogTags.UI, "Got location "+ location);
                    }
                };

                locationClient.requestLocationUpdates(request, locationListener);
            }

            @Override
            public void onDisconnected() {

            }
        }, null);
        locationClient.connect();
    }

    public void onDestroy(){

        if(locationClient != null && locationClient.isConnected() && locationListener != null){
            Log.i(LogTags.UI, "Disconnected from client stopping updates");

            locationClient.removeLocationUpdates(locationListener);
            locationClient.disconnect();
            locationClient = null;
            locationListener = null;
        }
    }

    /**
     * Tries to get our last known location.  Its entirely possible this will return null (GPS is
     * off, google play services not working, etc, etc).  Just make sure you handle null!
     *
     * @return Perhaps null, perhaps a location where the phone was at some time.
     */
    public Location getLastLocation(){
        if(locationClient != null && locationClient.isConnected()){
            return locationClient.getLastLocation();
        }
        else{
            return null;
        }
    }
}
