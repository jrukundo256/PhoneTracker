package com.birungi.maureen.phonetracker;

import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import java.util.List;
import java.util.Locale;

public class SmsLocationReporter {

    private Context context;
    private String receiver;

    GpsParser mLocation = new GpsParser();


    public SmsLocationReporter(Context context, String receiver) {
        this.context = context;
        this.receiver = receiver;
    }

    public void report() {

        SmsManager smsManager = SmsManager.getDefault();

        if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            LocationManager locationManager = (LocationManager) context.getSystemService(
                    Context.LOCATION_SERVICE);

            String usedProvider = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                    ? LocationManager.GPS_PROVIDER : LocationManager.NETWORK_PROVIDER;
            Location location = locationManager.getLastKnownLocation(usedProvider);

            if (location == null) {
                return;
            }

            //mLocation.setLocation(Double.toString(location.getLatitude()), Double.toString(location.getLongitude()));
            //Get address base on location
            try{
                Geocoder geo = new Geocoder(context, Locale.getDefault());
                List<Address> addresses = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                if (addresses.isEmpty()) {
                    //yourtextfieldname.setText("Waiting for Location");
                    smsManager.sendTextMessage(receiver, null, context.getString(R.string.report_text, Double.toString(location.getLatitude()), Double.toString(location.getLongitude())), null, null);
                }
                else {
                    if (addresses.size() > 0) {
                        smsManager.sendTextMessage(receiver, null, "(PT UPDATE) Phone location is: "+addresses.get(0).getFeatureName()+", "+addresses.get(0).getLocality()+", "+addresses.get(0).getAdminArea(), null, null);
                    }
                }
            }
            catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
