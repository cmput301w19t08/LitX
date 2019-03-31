/*
 * Classname: MapActivity.java
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback{
    private LatLng location;
    private Boolean Moveable;
    private GoogleMap myGoogleMap;
    private Marker marker;
    EditText myLatEditText;
    EditText myLongEditText;
    Button moveMarkerButton;
    private Book book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_map);
        moveMarkerButton= (Button)findViewById(R.id.moveButton);
        myLatEditText=(EditText)findViewById(R.id.LatField);
        myLongEditText=(EditText)findViewById(R.id.LongField);
        if (Moveable==Boolean.FALSE){
            moveMarkerButton.setVisibility(View.GONE);
            myLatEditText.setVisibility(View.GONE);
            myLongEditText.setVisibility(View.GONE);

        }
        try {
            // Uses the intent to find the Latitude and Longitude of our Point and then assigns the LatLng variable a value
            //corresponding to the points in the intent
            Intent intent = getIntent();
            Book book = Book.findByDocId(intent.getStringExtra("BOOK"));
            double Latitude = book.getLatitude();
            double Longitude = book.getLongitude();


            Moveable= intent.getBooleanExtra("MOVABLE",Boolean.FALSE);


            if (Moveable==Boolean.TRUE){
                location = new LatLng(53.5304672,-113.5306609);




            }
            else {
                moveMarkerButton.setVisibility(View.GONE);
                myLatEditText.setVisibility(View.GONE);
                myLongEditText.setVisibility(View.GONE);



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
        myGoogleMap = googleMap;
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));
        if (Moveable==Boolean.TRUE){
            googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    googleMap.clear();
                    String newLat=Double.toString(latLng.latitude);
                    String newLong=Double.toString(latLng.longitude);
                    myLatEditText.setText(newLat, TextView.BufferType.EDITABLE);
                    myLongEditText.setText(newLong, TextView.BufferType.EDITABLE);
                    marker = googleMap.addMarker(new MarkerOptions().position(latLng)
                            .title("Marker")
                            .draggable(Moveable));

                }

                public void onMarkerDragEnd(Marker marker){
                    String newLat=Double.toString(marker.getPosition().latitude);
                    String newLong=Double.toString(marker.getPosition().longitude);
                    myLatEditText.setText(newLat, TextView.BufferType.EDITABLE);
                    myLongEditText.setText(newLong, TextView.BufferType.EDITABLE);



                }


            });
            googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                @Override
                public void onMarkerDragStart(Marker marker) {

                }

                @Override
                public void onMarkerDrag(Marker marker) {

                }

                @Override
                public void onMarkerDragEnd(Marker marker) {
                    String newLat=Double.toString(marker.getPosition().latitude);
                    String newLong=Double.toString(marker.getPosition().longitude);
                    myLatEditText.setText(newLat, TextView.BufferType.EDITABLE);
                    myLongEditText.setText(newLong, TextView.BufferType.EDITABLE);

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
        if (Moveable) {
            book.setLatitude(specifiedLatitude);
            book.setLongitude(specifiedLongitude);
            book.push();
        }
        finish();

        //myLatEditText.setText(Double.toString(specifiedLocation.latitude));

    }
    public void moveMarker(View v) {
        try {
            double Latitude = Double.parseDouble(myLatEditText.getText().toString());
            double Longitude = Double.parseDouble(myLongEditText.getText().toString());
            LatLng newPosition = new LatLng(Latitude, Longitude);
            myGoogleMap.clear();
            marker = myGoogleMap.addMarker(new MarkerOptions().position(newPosition)
                    .title("Marker")
                    .draggable(Moveable));
            myGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(newPosition));
        } catch (Exception e){}

    }





}