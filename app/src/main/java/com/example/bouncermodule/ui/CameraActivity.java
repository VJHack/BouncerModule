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

import android.content.pm.ApplicationInfo;
import android.content.pm.ComponentInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
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

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.rekognition.AmazonRekognition;
import com.amazonaws.services.rekognition.AmazonRekognitionClient;
import com.amazonaws.services.rekognition.model.CompareFacesMatch;
import com.amazonaws.services.rekognition.model.CompareFacesRequest;
import com.amazonaws.services.rekognition.model.CompareFacesResult;
import com.amazonaws.services.rekognition.model.ComparedFace;
import com.amazonaws.services.rekognition.model.Image;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
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
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
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
                                            Log.i("Face Detection", "Bounding box: " + bounds.toString());
                                            break;
                                        }
                                        ImageView iv2 = (ImageView) findViewById(R.id.image2View);
                                        iv2.setImageBitmap(outputImage2);
                                        out2 = new ByteArrayOutputStream();
                                        outputImage2.compress(Bitmap.CompressFormat.PNG, 100, out2);
                                        bytes2 = out2.toByteArray();
                                        // For facial comparison -----
                                        compareFaces();
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

    public void compareFaces() {
       /* TODO: MAYBE GET RID OF THIS EVENTUALLY, MOVE INTO ASYNC TASK */
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Float similarityThreshold = 70F;
        // must be wrapped in try/catch in case we can't find keys from local properties
        try {
            ApplicationInfo applicationInfo = getApplicationContext().getPackageManager().getApplicationInfo(getPackageName(), PackageManager.GET_META_DATA);
            String accessKey = applicationInfo.metaData.getString("AWS_ACCESS_KEY");
            String secretKey = applicationInfo.metaData.getString("AWS_SECRET_KEY");

            AmazonRekognition rekognitionClient = new AmazonRekognitionClient(new BasicAWSCredentials(accessKey, secretKey));

            Image source = new Image().withBytes(ByteBuffer.wrap(bytes1));
            Image target = new Image().withBytes(ByteBuffer.wrap(bytes2));

            CompareFacesRequest request = new CompareFacesRequest()
                    .withSourceImage(source)
                    .withTargetImage(target)
                    .withSimilarityThreshold(similarityThreshold);
            // Call operation
            CompareFacesResult compareFacesResult = rekognitionClient.compareFaces(request);
            // Display results
            List <CompareFacesMatch> faceDetails = compareFacesResult.getFaceMatches();
            for (CompareFacesMatch match: faceDetails){
                ComparedFace face= match.getFace();
                Log.i("AWS FACE COMPARE CONFIDENCE", face.getConfidence().toString());
                TextView tv = findViewById(R.id.confidenceText);
                tv.setText(face.getConfidence().toString());
            }
        }
        catch (PackageManager.NameNotFoundException e){
            Log.e("FAILED GETTING METADATA AWS KEYS FROM LOCAL PROPERTIES", e.getMessage());
        }
    }
}
