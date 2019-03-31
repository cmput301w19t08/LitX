/*
 * Classname: MapObject.java
 * Version: 1.0
 * Date: 2019-03-24
 * https://developers.google.com/maps/documentation/android-sdk/map-with-marker (guide on setting up maps and markers for maps)
 * https://github.com/OneSignal/OneSignal-Gradle-Plugin/issues/32 (For Debugging issues with Google gms and Firebase)
 * https://medium.com/@suchydan/how-to-solve-google-play-services-version-collision-in-gradle-dependencies-ef086ae5c75f (For Debugging issues with Google gms and Firebase)
 * https://stackoverflow.com/questions/50146640/android-studio-program-type-already-present-com-google-android-gms-internal-me (For Debugging issues with Google gms and Firebase)
 * https://stackoverflow.com/questions/16181945/get-latitude-and-longitude-of-marker-in-google-maps (stackflow page on using markers)
 */

package ca.ualberta.cs.phebert.litx;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import ca.ualberta.cs.phebert.litx.Models.Request;

public class MapObject extends AppCompatActivity implements OnMapReadyCallback{
    private Request request;
    private LatLng location;
    private Boolean Moveable;
    private Marker marker;
    TextView myTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_map);
        myTextView=(TextView)findViewById(R.id.LatField);
        try {
            // Uses the intent to find the Latitude and Longitude of our Point and then assigns the LatLng variable a value
            //corresponding to the points in the intent
            Intent intent = getIntent();
            double Latitude = intent.getDoubleExtra("LAT",0);
            double Longitude = intent.getDoubleExtra("LONG",0);
            Moveable= intent.getBooleanExtra("MOVABLE",Boolean.FALSE);
            if (Moveable==Boolean.TRUE){
                location = new LatLng(53.5304672,-113.5306609);




            }
            else {
                location = new LatLng(Latitude,Longitude);

            }


        } catch (Exception e) {}
        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        if (Moveable==Boolean.TRUE){
            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    googleMap.clear();
                    marker = googleMap.addMarker(new MarkerOptions().position(latLng)
                            .title("Marker")
                            .draggable(Moveable));

                }
            });

        }
        else {
            googleMap.addMarker(new MarkerOptions().position(location)
                    .title("Marker")
                    .draggable(Boolean.FALSE));
        }

    }



    public void getLocation(View v) {
        double specifiedLatitude = marker.getPosition().latitude;
        double specifiedLongitude = marker.getPosition().longitude;
        LatLng specifiedLocation = new LatLng(specifiedLatitude,specifiedLongitude);
        myTextView.setText(Double.toString(specifiedLocation.latitude));

    }


}