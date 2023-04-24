package com.example.bouncermodule.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.media.Image;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.util.Size;
import android.view.Menu;
import android.view.MenuItem;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.bouncermodule.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class CameraActivity extends AppCompatActivity implements ImageAnalysis.Analyzer {
    private PreviewView previewView;
    private Preview preview;
    private ImageCapture imageCapture;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    public static boolean firstImageCaptured = false;
    public static InputImage inputImage1;
    public static InputImage inputImage2;
    public static Bitmap img1;
    public static Bitmap img2;
    public static Bitmap outputImage1;
    public static Bitmap outputImage2;
    private static ByteArrayOutputStream out1;
    private static byte[] bytes1;
    private static ByteArrayOutputStream out2;
    private static byte[] bytes2;
    public Integer faceComparisonConfidence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_camera);
        previewView = findViewById(R.id.previewView);
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(new Runnable() {
            @Override
            public void run() {
                try {
                    ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                    bindImageAnalysis(cameraProvider);
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, ContextCompat.getMainExecutor(this));
        Button photoButton = (Button) findViewById(R.id.takePhotoButton);
        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button takePhotoButton = findViewById(R.id.takePhotoButton);
                // if we haven't captured first image yet, set first image
                if (!firstImageCaptured) {
                    firstImageCaptured = true;
                    //takePhoto(img1);
                    img1 = previewView.getBitmap();
                    // set image just to see if it worked
                    inputImage1 = InputImage.fromBitmap(img1, 0);
                    Log.i("IMAGES", "Image 1 captured");
                    takePhotoButton.setText("Take ID Photo");
                }
                // if first image has been captured, set second image
                else {
                    firstImageCaptured = false;
                    img2 = previewView.getBitmap();
                    inputImage2 = InputImage.fromBitmap(img2, 0);
                    Log.i("IMAGES", "Image 2 captured");
                    takePhotoButton.setVisibility(View.INVISIBLE);
                    previewView.setVisibility(View.INVISIBLE);
                    checkFaces();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    private void bindImageAnalysis(ProcessCameraProvider cameraProvider) {
        // size was 1280x720, cutting that down now for speed
        ImageAnalysis imageAnalysis =
                new ImageAnalysis.Builder().setTargetResolution(new Size(640, 480))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST).build();
        imageAnalysis.setAnalyzer(ContextCompat.getMainExecutor(this), new ImageAnalysis.Analyzer() {
            @Override
            public void analyze(ImageProxy image) {
                image.close();
            }
        });
        preview = new Preview.Builder().build();
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK).build();
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
        ImageCapture.Builder builder = new ImageCapture.Builder();
        imageCapture = builder.build();
        cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector,
                imageAnalysis, preview, imageCapture);
    }

    @Override
    public void analyze(ImageProxy image) {
        // image processing here
        Bitmap bitmap = previewView.getBitmap();
        // TODO: this rotation degree might not be correct every time, look into more if we have time
        InputImage in1 = InputImage.fromBitmap(bitmap, image.getImageInfo().getRotationDegrees());
        Log.i("Riley Tag", "Made it here");
        if (bitmap == null)
            return;
        /*runOnUiThread(new Runnable() {
            @Override
            public void run() {
                image1View.setImageBitmap(bitmap1);
            }
        });*/
    }

    public void checkFaces() {
        FaceDetectorOptions faceOptions = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .build();
        FaceDetector detector = FaceDetection.getClient(faceOptions);
        Task<List<Face>> result1 =
                detector.process(inputImage1)
                        .addOnSuccessListener(
                                new OnSuccessListener<List<Face>>() {
                                    @Override
                                    public void onSuccess(List<Face> faces) {
                                        Log.i("Face Detection", "Face Detection Succeeded, Found " + faces.size() + " faces");
                                        for (Face face : faces) {
                                            Rect bounds = face.getBoundingBox();
                                            outputImage1 = Bitmap.createBitmap(img1, bounds.left, bounds.top, bounds.width(), bounds.height());
                                            //float rotY = face.getHeadEulerAngleY();  // Head is rotated to the right rotY degrees
                                            //float rotZ = face.getHeadEulerAngleZ();  // Head is tilted sideways rotZ degrees
                                            Log.i("Face Detection", "Bounding box: " + bounds.toString());
                                            break;
                                        }
                                        ImageView iv1 = (ImageView) findViewById(R.id.image1View);
                                        iv1.setImageBitmap(outputImage1);
                                        out1 = new ByteArrayOutputStream();
                                        outputImage1.compress(Bitmap.CompressFormat.PNG, 100, out1);
                                        bytes1 = out1.toByteArray();
                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.i("Face Detection", "Face Detection Failed");
                                    }
                                });
        Task<List<Face>> result2 =
                detector.process(inputImage2)
                        .addOnSuccessListener(
                                new OnSuccessListener<List<Face>>() {
                                    @Override
                                    public void onSuccess(List<Face> faces) {
                                        Log.i("Face Detection", "Face Detection Succeeded, Found " + faces.size() + " faces");
                                        for (Face face : faces) {
                                            Rect bounds = face.getBoundingBox();
                                            outputImage2 = Bitmap.createBitmap(img2, bounds.left, bounds.top, bounds.width(), bounds.height());
                                            //float rotY = face.getHeadEulerAngleY();  // Head is rotated to the right rotY degrees
                                            //float rotZ = face.getHeadEulerAngleZ();  // Head is tilted sideways rotZ degrees
                                            Log.i("Face Detection", "Bounding box: " + bounds.toString());
                                            break;
                                        }
                                        ImageView iv2 = (ImageView) findViewById(R.id.image2View);
                                        iv2.setImageBitmap(outputImage2);
                                        out2 = new ByteArrayOutputStream();
                                        outputImage2.compress(Bitmap.CompressFormat.PNG, 100, out2);
                                        bytes2 = out2.toByteArray();
                                        // For facial comparison -----
                                        faceComparison();
                                        //Log.i("FACE COMPARISON RESULT", faceComparisonConfidence.toString());
                                    }
                                })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.i("Face Detection", "Face Detection Failed");
                                    }
                                });
    }

    private void faceComparison() {
        // url to post our data
        String url = "https://faceapi.mxface.ai/api/v3/face/verify";

        // creating a new variable for our request queue
        RequestQueue queue = Volley.newRequestQueue(CameraActivity.this);
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // on below line we are displaying a success toast message.
                Log.i("FACE COMPARISON", "Making Request");
                try {
                    // on below line we are parsing the response
                    // to json object to extract data from it.
                    JSONObject respObj = new JSONObject(response);

                    // below are the strings which we
                    // extract from our json object.
                    Integer matchResult = respObj.getInt("matchResult");
                    faceComparisonConfidence = matchResult;
                    Log.i("FACE COMPARISON RESULT", faceComparisonConfidence.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // method to handle errors.
                //erddddddddddddddddddddddddLog.i("FACE COMPARISON", error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                String en1 = Base64.encodeToString(bytes1, Base64.DEFAULT);
                String en2 = Base64.encodeToString(bytes2, Base64.DEFAULT);
                params.put("encoded_image1", en1);
                params.put("encoded_image2", en2);
                Log.i("FACE COMPARISON", en1);
                // at last we are
                // returning our params.
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=utf-8");
                params.put("Subscriptionkey", "YIHCIvHyTG1YvOkmnw-4wmA4nIfC31477");
                return params;
            }
        };
        // below line is to make
        // a json object request.
        request.setRetryPolicy(new DefaultRetryPolicy(
                0,
                -1,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }
}
