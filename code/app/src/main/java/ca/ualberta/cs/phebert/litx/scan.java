package ca.ualberta.cs.phebert.litx;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import cz.msebera.android.httpclient.Header;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;

import ca.ualberta.cs.phebert.litx.R;

public class scan extends AppCompatActivity {

    SurfaceView cameraView;
    TextView textView;
    CameraSource cameraSource;
    final int RequestCameraPermissionID=1001;
    private String NoISBN= "No ISBN";
    private String ISBN;
    private Boolean ReadISBN = Boolean.TRUE;
    private String title;



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {

            case RequestCameraPermissionID:
            {
                if (grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                        return;



                    }
                    try {
                        cameraSource.start(cameraView.getHolder());
                    } catch (IOException e){


                        e.printStackTrace();
                    }

                }

            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        cameraView=(SurfaceView)findViewById(R.id.scanField);
        textView=(TextView)findViewById(R.id.ISBNField);


        BarcodeDetector bardetect = new BarcodeDetector.Builder(getApplicationContext())
                //.setBarcodeFormats(Barcode.DATA_MATRIX | Barcode.QR_CODE | Barcode.EAN_13)
                .setBarcodeFormats(Barcode.EAN_13|Barcode.ISBN|Barcode.EAN_8)
                .build();


        if (!bardetect.isOperational()){
            Log.w("scan","Detector dependencies are not availible!");
        }
        else {

            cameraSource = new CameraSource.Builder(getApplicationContext(),bardetect)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedPreviewSize(1280,1024)
                    .setRequestedFps(2.0f)
                    .setAutoFocusEnabled(true)
                    .build();
            cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    try {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){

                            ActivityCompat.requestPermissions(scan.this,new String[]{Manifest.permission.CAMERA},RequestCameraPermissionID);
                            return;
                        }
                        cameraSource.start(cameraView.getHolder());



                    }
                    catch (IOException e){
                        e.printStackTrace();

                    }

                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {


                }

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                    cameraSource.stop();

                }
            });

            bardetect.setProcessor(new Detector.Processor<Barcode>(){
                @Override
                public void release() {

                }

                @Override
                public void receiveDetections(Detector.Detections<Barcode> detections) {

                    final SparseArray<Barcode> items = detections.getDetectedItems();
                    if (items.size()!=0)
                    {
                        textView.post(new Runnable() {
                            @Override
                            public void run() {
                                for (int i=0;i<items.size();++i)
                                {
                                    Barcode item = items.valueAt(i);
                                    if (ReadISBN==Boolean.TRUE) {

                                        textView.setText(item.rawValue);
                                        ISBN = item.rawValue;
                                        ReadISBN = Boolean.FALSE;

                                    }
                                }

                            }
                        });


                    }

                }
            });
        }
    }

    public void clearISBN(View v) {
        ReadISBN = Boolean.TRUE;
        textView.setText(NoISBN);
    }

    public void confirmISBN(View v) {
        ReadISBN = Boolean.TRUE;
        String Author;
        //String url = "http://openlibrary.org/ISBN/" + ISBN + "9780316399623.json";
        //JsonHttpResponseHandler handler = new JsonHttpResponseHandler();
        //JSONObject ourBook = client.get(url,handler);
        BookClient client = new BookClient();
        //JSONObject ourBook = null;
        client.getBooks(ISBN, new JsonHttpResponseHandler(){
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                if (response!=null){
                    try {

                        if (response.has("title")){
                            title = response.getString("title");
                            Intent intent = new Intent();
                            intent.putExtra("Title",title);
                            intent.putExtra("ISBN",ISBN);
                            setResult(RESULT_OK,intent);
                            finish();

                        }
                        //Not Done Yet!
                        if (response.has("authors")){
                            final JSONArray authors = response.getJSONArray("author_name");
                            int numAuthors = authors.length();

                            final String[] authorStrings = new String[numAuthors];
                            for (int i = 0; i < numAuthors; ++i) {
                                authorStrings[i] = authors.getString(i);
                            }
                            //String author = response.getString("author");
                            textView.setText(authorStrings[0]);

                        }





                    }catch (JSONException e) {
                        // Invalid JSON format, show appropriate error.
                        e.printStackTrace();
                    }



                }
                else {
                    ReadISBN = Boolean.TRUE;
                    textView.setText(NoISBN);


                }


            }


        });


    }



}

