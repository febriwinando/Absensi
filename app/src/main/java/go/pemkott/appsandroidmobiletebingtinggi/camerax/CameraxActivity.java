package go.pemkott.appsandroidmobiletebingtinggi.camerax;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.*;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

import go.pemkott.appsandroidmobiletebingtinggi.R;
import go.pemkott.appsandroidmobiletebingtinggi.dinasluarkantor.tugaslapangan.TugasLapanganFinalActivity;
import go.pemkott.appsandroidmobiletebingtinggi.kehadiran.AbsensiKehadiranActivity;

public class CameraxActivity extends AppCompatActivity {

    private PreviewView previewView;
    private ImageButton capture, toggleFlash, flipCamera;

    private int cameraFacing = CameraSelector.LENS_FACING_FRONT;
    private boolean wajahTerdeteksi = false;

    private FaceDetector faceDetector;

    private String aktivitas;

    private final ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) startCamera(cameraFacing);
                else Toast.makeText(this, "Izin kamera ditolak", Toast.LENGTH_SHORT).show();
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camerax);

        previewView = findViewById(R.id.cameraPreview);
        capture = findViewById(R.id.capture);
        toggleFlash = findViewById(R.id.toggleFlash);
        flipCamera = findViewById(R.id.flipCamera);

        aktivitas = getIntent().getStringExtra("aktivitas");

        initFaceDetector();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(Manifest.permission.CAMERA);
        } else {
            startCamera(cameraFacing);
        }

        flipCamera.setOnClickListener(v -> {
            cameraFacing = (cameraFacing == CameraSelector.LENS_FACING_FRONT)
                    ? CameraSelector.LENS_FACING_BACK
                    : CameraSelector.LENS_FACING_FRONT;
            startCamera(cameraFacing);
        });
    }

    // ===================== FACE DETECTOR =====================
    private void initFaceDetector() {
        FaceDetectorOptions options =
                new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                        .setMinFaceSize(0.15f)
                        .build();

        faceDetector = FaceDetection.getClient(options);
    }

    // ===================== CAMERA =====================
    private void startCamera(int facing) {
        ListenableFuture<ProcessCameraProvider> future =
                ProcessCameraProvider.getInstance(this);

        future.addListener(() -> {
            try {
                ProcessCameraProvider provider = future.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                ImageCapture imageCapture =
                        new ImageCapture.Builder()
                                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                                .build();

                ImageAnalysis imageAnalysis =
                        new ImageAnalysis.Builder()
                                .setBackpressureStrategy(
                                        ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build();

                imageAnalysis.setAnalyzer(
                        Executors.newSingleThreadExecutor(),
                        imageProxy -> {

                            @ExperimentalGetImage
                            android.media.Image mediaImage = imageProxy.getImage();

                            if (mediaImage != null) {
                                InputImage image =
                                        InputImage.fromMediaImage(
                                                mediaImage,
                                                imageProxy.getImageInfo().getRotationDegrees()
                                        );

                                faceDetector.process(image)
                                        .addOnSuccessListener(faces ->
                                                wajahTerdeteksi = !faces.isEmpty()
                                        )
                                        .addOnCompleteListener(task ->
                                                imageProxy.close()
                                        );
                            } else {
                                imageProxy.close();
                            }
                        }
                );

                CameraSelector selector =
                        new CameraSelector.Builder()
                                .requireLensFacing(facing)
                                .build();

                provider.unbindAll();
                Camera camera =
                        provider.bindToLifecycle(
                                this,
                                selector,
                                preview,
                                imageCapture,
                                imageAnalysis
                        );

                capture.setOnClickListener(v -> {
                    if (!wajahTerdeteksi) {
                        Toast.makeText(
                                this,
                                "Wajah tidak terdeteksi",
                                Toast.LENGTH_SHORT
                        ).show();
                        return;
                    }
                    takePicture(imageCapture);
                });

                toggleFlash.setOnClickListener(v -> toggleFlash(camera));

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    // ===================== CAPTURE =====================
    private void takePicture(ImageCapture imageCapture) {

        String fileName = System.currentTimeMillis() + ".jpg";

        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        values.put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES + "/eabsensi"
        );

        ImageCapture.OutputFileOptions options =
                new ImageCapture.OutputFileOptions.Builder(
                        getContentResolver(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        values
                ).build();

        imageCapture.takePicture(
                options,
                Executors.newSingleThreadExecutor(),
                new ImageCapture.OnImageSavedCallback() {

                    @Override
                    public void onImageSaved(
                            @NonNull ImageCapture.OutputFileResults output) {

                        runOnUiThread(() -> kirimHasil(fileName));
                    }

                    @Override
                    public void onError(
                            @NonNull ImageCaptureException e) {

                        runOnUiThread(() ->
                                Toast.makeText(
                                        CameraxActivity.this,
                                        "Gagal: " + e.getMessage(),
                                        Toast.LENGTH_SHORT
                                ).show()
                        );
                    }
                }
        );
    }

    // ===================== RESULT =====================
    private void kirimHasil(String fileName) {
        if ("kehadiran".equals(aktivitas)) {
            Intent i = new Intent(this, AbsensiKehadiranActivity.class);
            i.putExtra("namafile", fileName);
            startActivity(i);
        } else if ("tugaslapangan".equals(aktivitas)) {
            Intent i = new Intent(this, TugasLapanganFinalActivity.class);
            i.putExtra("namafile", fileName);
            startActivity(i);
        } else {
            Intent result = new Intent();
            result.putExtra("namafile", fileName);
            setResult(RESULT_OK, result);
        }
        finish();
    }

    // ===================== FLASH =====================
    private void toggleFlash(Camera camera) {
        if (!camera.getCameraInfo().hasFlashUnit()) return;

        boolean on = camera.getCameraInfo().getTorchState().getValue()
                == TorchState.ON;

        camera.getCameraControl().enableTorch(!on);
        toggleFlash.setImageResource(on
                ? R.drawable.flash
                : R.drawable.flashof);
    }
}


//package go.pemkott.appsandroidmobiletebingtinggi.camerax;
//
//import android.Manifest;
//import android.app.Activity;
//import android.content.ContentValues;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.net.Uri;
//import android.os.Bundle;
//import android.os.Environment;
//import android.provider.MediaStore;
//import android.util.Log;
//import android.view.View;
//import android.widget.ImageButton;
//import android.widget.Toast;
//
//import androidx.activity.result.ActivityResultCallback;
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.camera.core.AspectRatio;
//import androidx.camera.core.Camera;
//import androidx.camera.core.CameraSelector;
//import androidx.camera.core.ImageCapture;
//import androidx.camera.core.ImageCaptureException;
//import androidx.camera.core.Preview;
//import androidx.camera.lifecycle.ProcessCameraProvider;
//import androidx.camera.view.PreviewView;
//import androidx.core.content.ContextCompat;
//
//import com.google.common.util.concurrent.ListenableFuture;
//
//import java.io.File;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.Executors;
//
//import go.pemkott.appsandroidmobiletebingtinggi.R;
//import go.pemkott.appsandroidmobiletebingtinggi.dinasluarkantor.tugaslapangan.TugasLapanganFinalActivity;
//import go.pemkott.appsandroidmobiletebingtinggi.kehadiran.AbsensiKehadiranActivity;
//
//public class CameraxActivity extends AppCompatActivity {
//    ImageButton capture, toggleFlash, flipCamera;
//    private PreviewView previewView;
//    int cameraFacing = CameraSelector.LENS_FACING_FRONT;
//    private final ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
//        @Override
//        public void onActivityResult(Boolean result) {
//            if (result) {
//                startCamera(cameraFacing);
//            }
//        }
//    });
//
//    String aktivitas;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_camerax);
//
//        previewView = findViewById(R.id.cameraPreview);
//        capture = findViewById(R.id.capture);
//        toggleFlash = findViewById(R.id.toggleFlash);
//        flipCamera = findViewById(R.id.flipCamera);
//
//        aktivitas = getIntent().getStringExtra("aktivitas");
//
////        Toast.makeText(this, aktivitas, Toast.LENGTH_SHORT).show();
//
//
//
//        if (ContextCompat.checkSelfPermission(CameraxActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
//            activityResultLauncher.launch(Manifest.permission.CAMERA);
//        } else {
//            startCamera(cameraFacing);
//        }
//
//        flipCamera.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (cameraFacing == CameraSelector.LENS_FACING_BACK) {
//                    cameraFacing = CameraSelector.LENS_FACING_FRONT;
//                } else {
//                    cameraFacing = CameraSelector.LENS_FACING_BACK;
//                }
//                startCamera(cameraFacing);
//            }
//        });
//    }
//
//    public void startCamera(int cameraFacing) {
//        int aspectRatio = aspectRatio(previewView.getWidth(), previewView.getHeight());
//        ListenableFuture<ProcessCameraProvider> listenableFuture = ProcessCameraProvider.getInstance(this);
//
//        listenableFuture.addListener(() -> {
//            try {
//                ProcessCameraProvider cameraProvider = (ProcessCameraProvider) listenableFuture.get();
//
//                Preview preview = new Preview.Builder().setTargetAspectRatio(aspectRatio).build();
//
//                ImageCapture imageCapture = new ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
//                        .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation()).build();
//
//                CameraSelector cameraSelector = new CameraSelector.Builder()
//                        .requireLensFacing(cameraFacing).build();
//
//                cameraProvider.unbindAll();
//
//                Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
//
//                capture.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        if (ContextCompat.checkSelfPermission(CameraxActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                            activityResultLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//                        }
//
//                        takePicture(imageCapture);
//                    }
//                });
//
//                toggleFlash.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        setFlashIcon(camera);
//                    }
//                });
//
//                preview.setSurfaceProvider(previewView.getSurfaceProvider());
//            } catch (ExecutionException | InterruptedException e) {
//                e.printStackTrace();
//            }
//        }, ContextCompat.getMainExecutor(this));
//    }
//
//    public void takePicture(ImageCapture imageCapture) {
//
//        String fileName = System.currentTimeMillis() + ".jpg";
//
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
//        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
//        contentValues.put(
//                MediaStore.MediaColumns.RELATIVE_PATH,
//                Environment.DIRECTORY_PICTURES + "/eabsensi"
//        );
//
//        ImageCapture.OutputFileOptions outputFileOptions =
//                new ImageCapture.OutputFileOptions.Builder(
//                        getContentResolver(),
//                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                        contentValues
//                ).build();
//
//        imageCapture.takePicture(
//                outputFileOptions,
//                Executors.newCachedThreadPool(),
//                new ImageCapture.OnImageSavedCallback() {
//
//                    @Override
//                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
//                        runOnUiThread(() -> {
//                            if ("kehadiran".equals(aktivitas)) {
//                                Intent intent = new Intent(CameraxActivity.this, AbsensiKehadiranActivity.class);
//                                intent.putExtra("namafile", fileName);
//                                startActivity(intent);
//                                finish();
//                            } else if ("tugaslapangan".equals(aktivitas)) {
//                                Intent intent = new Intent(CameraxActivity.this, TugasLapanganFinalActivity.class);
//                                intent.putExtra("namafile", fileName);
//                                startActivity(intent);
//                                finish();
//                            } else if ("lampirantl".equals(aktivitas)) {
//                                Intent resultIntent = new Intent();
//                                resultIntent.putExtra("namafile", fileName);
//                                setResult(RESULT_OK, resultIntent);
//
//                                finish();
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onError(@NonNull ImageCaptureException exception) {
//                        runOnUiThread(() ->
//                                Toast.makeText(
//                                        CameraxActivity.this,
//                                        "Gagal menyimpan: " + exception.getMessage(),
//                                        Toast.LENGTH_SHORT
//                                ).show()
//                        );
//                    }
//                }
//        );
//    }
//
//
//    private void setFlashIcon(Camera camera) {
//        if (camera.getCameraInfo().hasFlashUnit()) {
//            if (camera.getCameraInfo().getTorchState().getValue() == 0) {
//                camera.getCameraControl().enableTorch(true);
//                toggleFlash.setImageResource(R.drawable.flashof);
//            } else {
//                camera.getCameraControl().enableTorch(false);
//                toggleFlash.setImageResource(R.drawable.flash);
//            }
//        } else {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(CameraxActivity.this, "Flash is not available currently", Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//    }
//
//    private int aspectRatio(int width, int height) {
//        double previewRatio = (double) Math.max(width, height) / Math.min(width, height);
//        if (Math.abs(previewRatio - 4.0 / 3.0) <= Math.abs(previewRatio - 16.0 / 9.0)) {
//            return AspectRatio.RATIO_4_3;
//        }
//        return AspectRatio.RATIO_16_9;
//    }
//}