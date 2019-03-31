/*
 * Classname: ScanBookActivity.java
 * Version: 1.0
 * Date: 2019-03-11
 * Copyright notice: https://www.youtube.com/watch?v=xoTKpstv9f0 (video on text recognition with Camera using Google Vision)
 * https://codelabs.developers.google.com/codelabs/barcodes/#4 (guide on Barcode Reading)
 * https://guides.codepath.com/android/Book-Search-Tutorial (guide on searching for books using ISBN)
 * https://stackoverflow.com/questions/13153697/how-to-replace-with-in-a-java-string (help with replace and replaceAll)


 */
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
import android.widget.TextView;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.loopj.android.http.JsonHttpResponseHandler;
import cz.msebera.android.httpclient.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;


/**
 * This is our ScanBookActivity class
 * @author 150 1248
 * @version 1.0
 * @see AppCompatActivity
 */
public class ScanBookActivity extends AppCompatActivity {

    SurfaceView cameraView;
    TextView textView;
    CameraSource cameraSource;
    final int RequestCameraPermissionID=1001;
    private String NoISBN= "No ISBN";
    private String ISBN;
    private Boolean ReadISBN = Boolean.TRUE;
    private String title="";
    private String authorKey;
    private String author="";

    /**
     * Sets up our camera for reading in barcodes
     * @param int, String[], int[]
     * @throws: IOException
     * @see BookClient
     */
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



    /**
     * Executes when we first create scan with a bundle parameter
     * @param Bundle
     * @throws: IOException
     * */
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

                            ActivityCompat.requestPermissions(ScanBookActivity.this,new String[]{Manifest.permission.CAMERA},RequestCameraPermissionID);
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


    /**
     * Clears ISBN and allows us to read in a new ISBN
     * @param View
     */
    public void clearISBN(View v) {
        ReadISBN = Boolean.TRUE;
        textView.setText(NoISBN);
    }

    /**
     * Confirms ISBN creates an Intent with the title and ISBN and passes it to AddBookActivity
     * @param View
     * @throws: JSONException
     * @see AddBookActivity
     */
    public void confirmISBN(View v) {
        findViewById(R.id.confirm_btn).setVisibility(View.GONE);
        findViewById(R.id.clear_btn).setVisibility(View.GONE);
        findViewById(R.id.ISBNField).setVisibility(View.GONE);
        findViewById(R.id.scanProgress).setVisibility(View.VISIBLE);
        findViewById(R.id.findingTextView).setVisibility(View.VISIBLE);

        ReadISBN = Boolean.FALSE;

        BookClient client = new BookClient();
        client.getBooks(ISBN, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                if (response != null) {
                    try {
                        if (response.has("title")) {
                            title = response.getString("title");
                                //intent.putExtra("Title",title);
                                //intent.putExtra("ISBN",ISBN);
                                //setResult(RESULT_OK,intent);
                                //finish();

                        }
                        else {
                            title = "";
                        }
                            //Not Done Yet! For part 5
                        if (response.has("authors")) {
                            final JSONArray authors = response.getJSONArray("authors");
                            authorKey = authors.getString(0);
                            authorKey = authorKey.replaceAll("\"", "");
                            authorKey = authorKey.replaceAll("", "");
                            authorKey = authorKey.replace("{", "");
                            authorKey = authorKey.replace("}", "");
                            authorKey = authorKey.replaceAll("\\\\", "");
                            authorKey = authorKey.replaceAll("key:", "");
                            AuthorClient authorClient = new AuthorClient();

                            authorClient.getAuthor(authorKey, new JsonHttpResponseHandler() {
                                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                    if (response != null) {
                                        try {
                                            if (response.has("name")) {
                                                author = response.getString("name");
                                                Intent intent = new Intent();
                                                intent.putExtra("Author", author);
                                                intent.putExtra("Title", title);
                                                intent.putExtra("ISBN", ISBN);
                                                setResult(RESULT_OK, intent);
                                                finish();

                                            }


                                        }
                                        catch (JSONException e) {
                                            e.printStackTrace();
                                            //ReadISBN = Boolean.TRUE;
                                        }


                                    } else {
                                        //ReadISBN = Boolean.TRUE;
                                        textView.setText(NoISBN);


                                    }

                                }


                            });


                        } else {
                            author = "";
                            Intent intent = new Intent();
                            intent.putExtra("Author", author);
                            intent.putExtra("Title", title);
                            intent.putExtra("ISBN", ISBN);
                            setResult(RESULT_OK, intent);
                            finish();

                        }
                            //here


                    } catch (JSONException e) {
                        //ReadISBN = Boolean.TRUE;
                        e.printStackTrace();
                    }

                } else {
                    //ReadISBN = Boolean.TRUE;
                    textView.setText(NoISBN);


                }


            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Intent intent = new Intent();
                intent.putExtra("Author",author);
                intent.putExtra("Title",title);
                intent.putExtra("ISBN",ISBN);
                setResult(RESULT_OK,intent);
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Intent intent = new Intent();
                intent.putExtra("Author",author);
                intent.putExtra("Title",title);
                intent.putExtra("ISBN",ISBN);
                setResult(RESULT_OK,intent);
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Intent intent = new Intent();
                intent.putExtra("Author",author);
                intent.putExtra("Title",title);
                intent.putExtra("ISBN",ISBN);
                setResult(RESULT_OK,intent);
                finish();
            }
        });



        /*
        try {
            Intent intent = new Intent();
            intent.putExtra("Author",author);
            intent.putExtra("Title",title);
            intent.putExtra("ISBN",ISBN);
            setResult(RESULT_OK,intent);
            finish();
            textView.setText(author);


        }catch (Exception e){
            Intent intent = new Intent();
            intent.putExtra("ISBN",ISBN);
            setResult(RESULT_OK,intent);
            finish();

        }
        */




        /*
        AuthorClient authorClient = new AuthorClient();

        authorClient.getAuthor(authorKey, new JsonHttpResponseHandler(){
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                if (response!=null){


                    try {

                        if (response.has("name")){
                            author = response.getString("name");
                            textView.setText(author);

                            //textView.setText(authorKey);

                            //Intent intent = new Intent();
                            //intent.putExtra("Title",title);
                            //intent.putExtra("ISBN",ISBN);
                            //setResult(RESULT_OK,intent);
                            //finish();

                        }





                    }catch (JSONException e) {
                        e.printStackTrace();
                    }



                }
                else {
                    ReadISBN = Boolean.TRUE;
                    textView.setText(NoISBN);


                }


            }


        });
        */


    }



}

