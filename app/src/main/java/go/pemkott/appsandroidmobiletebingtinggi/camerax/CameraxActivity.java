//package go.pemkott.appsandroidmobiletebingtinggi.camerax;
//
//import android.Manifest;
//import android.content.ContentValues;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.graphics.RectF;
//import android.media.Image;
//import android.os.Bundle;
//import android.os.Environment;
//import android.provider.MediaStore;
//import android.widget.ImageButton;
//import android.widget.Toast;
//
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.camera.core.*;
//import androidx.camera.lifecycle.ProcessCameraProvider;
//import androidx.camera.view.PreviewView;
//import androidx.core.content.ContextCompat;
//
//import com.google.common.util.concurrent.ListenableFuture;
//import com.google.mlkit.vision.common.InputImage;
//import com.google.mlkit.vision.face.*;
//
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.Executors;
//
//import go.pemkott.appsandroidmobiletebingtinggi.R;
//import go.pemkott.appsandroidmobiletebingtinggi.dinasluarkantor.tugaslapangan.TugasLapanganFinalActivity;
//import go.pemkott.appsandroidmobiletebingtinggi.kehadiran.AbsensiKehadiranActivity;
//
//public class CameraxActivity extends AppCompatActivity {
//
//    private PreviewView previewView;
//    private ImageButton capture, toggleFlash, flipCamera;
//
//    private int cameraFacing = CameraSelector.LENS_FACING_FRONT;
//    private boolean wajahTerdeteksi = false;
//
//    private FaceDetector faceDetector;
//
//    private String aktivitas;
//    private FaceOverlayView faceOverlay;
//
//    private final ActivityResultLauncher<String> permissionLauncher =
//            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
//                if (granted) startCamera(cameraFacing);
//                else Toast.makeText(this, "Izin kamera ditolak", Toast.LENGTH_SHORT).show();
//            });
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_camerax);
//
//        previewView = findViewById(R.id.cameraPreview);
//        capture = findViewById(R.id.capture);
//        toggleFlash = findViewById(R.id.toggleFlash);
//        flipCamera = findViewById(R.id.flipCamera);
//        faceOverlay = findViewById(R.id.faceOverlay);
//
//        aktivitas = getIntent().getStringExtra("aktivitas");
//
//        initFaceDetector();
//
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
//                != PackageManager.PERMISSION_GRANTED) {
//            permissionLauncher.launch(Manifest.permission.CAMERA);
//        } else {
//            startCamera(cameraFacing);
//        }
//
//        flipCamera.setOnClickListener(v -> {
//            cameraFacing = (cameraFacing == CameraSelector.LENS_FACING_FRONT)
//                    ? CameraSelector.LENS_FACING_BACK
//                    : CameraSelector.LENS_FACING_FRONT;
//            startCamera(cameraFacing);
//        });
//    }
//
//    // ===================== FACE DETECTOR =====================
//    private void initFaceDetector() {
//        FaceDetectorOptions options =
//                new FaceDetectorOptions.Builder()
//                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
//                        .setMinFaceSize(0.15f)
//                        .build();
//
//        faceDetector = FaceDetection.getClient(options);
//    }
//
//    // ===================== CAMERA =====================
//    private void startCamera(int facing) {
//        ListenableFuture<ProcessCameraProvider> future =
//                ProcessCameraProvider.getInstance(this);
//
//        future.addListener(() -> {
//            try {
//                ProcessCameraProvider provider = future.get();
//
//                Preview preview = new Preview.Builder().build();
//                preview.setSurfaceProvider(previewView.getSurfaceProvider());
//
//                ImageCapture imageCapture =
//                        new ImageCapture.Builder()
//                                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
//                                .build();
//
//                ImageAnalysis imageAnalysis =
//                        new ImageAnalysis.Builder()
//                                .setBackpressureStrategy(
//                                        ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
//                                .build();
//
//                imageAnalysis.setAnalyzer(
//                        Executors.newSingleThreadExecutor(),
//                        imageProxy -> {
//
//                            @ExperimentalGetImage
//                            android.media.Image mediaImage = imageProxy.getImage();
//
//                            if (mediaImage != null) {
//                                InputImage image =
//                                        InputImage.fromMediaImage(
//                                                mediaImage,
//                                                imageProxy.getImageInfo().getRotationDegrees()
//                                        );
//
//                                faceDetector.process(image)
//                                        .addOnSuccessListener(faces -> {
//                                            boolean detected = !faces.isEmpty();
//                                            wajahTerdeteksi = detected;
//
//                                            runOnUiThread(() ->
//                                                    faceOverlay.setWajahTerdeteksi(detected)
//                                            );
//                                        })
//                                        .addOnCompleteListener(task -> imageProxy.close());
//
////                                faceDetector.process(image)
////                                        .addOnSuccessListener(faces ->
////                                                wajahTerdeteksi = !faces.isEmpty()
////                                        )
////                                        .addOnCompleteListener(task ->
////                                                imageProxy.close()
////                                        );
//                            } else {
//                                imageProxy.close();
//                            }
//                        }
//                );
//
//                CameraSelector selector =
//                        new CameraSelector.Builder()
//                                .requireLensFacing(facing)
//                                .build();
//
//                provider.unbindAll();
//                Camera camera =
//                        provider.bindToLifecycle(
//                                this,
//                                selector,
//                                preview,
//                                imageCapture,
//                                imageAnalysis
//                        );
//
//                capture.setOnClickListener(v -> {
//                    if (!wajahTerdeteksi) {
//                        Toast.makeText(
//                                this,
//                                "Wajah tidak terdeteksi",
//                                Toast.LENGTH_SHORT
//                        ).show();
//                        return;
//                    }
//                    takePicture(imageCapture);
//                });
//
//                toggleFlash.setOnClickListener(v -> toggleFlash(camera));
//
//            } catch (ExecutionException | InterruptedException e) {
//                e.printStackTrace();
//            }
//        }, ContextCompat.getMainExecutor(this));
//    }
//
//    // ===================== CAPTURE =====================
//    private void takePicture(ImageCapture imageCapture) {
//
//        String fileName = System.currentTimeMillis() + ".jpg";
//
//        ContentValues values = new ContentValues();
//        values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
//        values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
//        values.put(
//                MediaStore.MediaColumns.RELATIVE_PATH,
//                Environment.DIRECTORY_PICTURES + "/eabsensi"
//        );
//
//        ImageCapture.OutputFileOptions options =
//                new ImageCapture.OutputFileOptions.Builder(
//                        getContentResolver(),
//                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                        values
//                ).build();
//
//        imageCapture.takePicture(
//                options,
//                Executors.newSingleThreadExecutor(),
//                new ImageCapture.OnImageSavedCallback() {
//
//                    @Override
//                    public void onImageSaved(
//                            @NonNull ImageCapture.OutputFileResults output) {
//
//                        runOnUiThread(() -> kirimHasil(fileName));
//                    }
//
//                    @Override
//                    public void onError(
//                            @NonNull ImageCaptureException e) {
//
//                        runOnUiThread(() ->
//                                Toast.makeText(
//                                        CameraxActivity.this,
//                                        "Gagal: " + e.getMessage(),
//                                        Toast.LENGTH_SHORT
//                                ).show()
//                        );
//                    }
//                }
//        );
//    }
//
//    // ===================== RESULT =====================
//    private void kirimHasil(String fileName) {
//        if ("kehadiran".equals(aktivitas)) {
//            Intent i = new Intent(this, AbsensiKehadiranActivity.class);
//            i.putExtra("namafile", fileName);
//            startActivity(i);
//        } else if ("tugaslapangan".equals(aktivitas)) {
//            Intent i = new Intent(this, TugasLapanganFinalActivity.class);
//            i.putExtra("namafile", fileName);
//            startActivity(i);
//        } else {
//            Intent result = new Intent();
//            result.putExtra("namafile", fileName);
//            setResult(RESULT_OK, result);
//        }
//        finish();
//    }
//
//    // ===================== FLASH =====================
//    private void toggleFlash(Camera camera) {
//        if (!camera.getCameraInfo().hasFlashUnit()) return;
//
//        boolean on = camera.getCameraInfo().getTorchState().getValue()
//                == TorchState.ON;
//
//        camera.getCameraControl().enableTorch(!on);
//        toggleFlash.setImageResource(on
//                ? R.drawable.flash
//                : R.drawable.flashof);
//    }
//}

//
//package go.pemkott.appsandroidmobiletebingtinggi.camerax;
//
//import android.Manifest;
//import android.content.ContentValues;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.graphics.Rect;
//import android.graphics.RectF;
//import android.media.Image;
//import android.os.Bundle;
//import android.os.Environment;
//import android.provider.MediaStore;
//import android.widget.ImageButton;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.activity.result.ActivityResultLauncher;
//import androidx.activity.result.contract.ActivityResultContracts;
//import androidx.annotation.NonNull;
//import androidx.annotation.OptIn;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.camera.core.*;
//import androidx.camera.lifecycle.ProcessCameraProvider;
//import androidx.camera.view.PreviewView;
//import androidx.core.content.ContextCompat;
//
//import com.google.common.util.concurrent.ListenableFuture;
//import com.google.mlkit.vision.common.InputImage;
//import com.google.mlkit.vision.face.*;
//
//import java.util.List;
//import java.util.Random;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.Executors;
//
//import go.pemkott.appsandroidmobiletebingtinggi.R;
//import go.pemkott.appsandroidmobiletebingtinggi.dinasluarkantor.tugaslapangan.TugasLapanganFinalActivity;
//import go.pemkott.appsandroidmobiletebingtinggi.kehadiran.AbsensiKehadiranActivity;
//
//public class CameraxActivity extends AppCompatActivity {
//
//    // UI
//    private PreviewView previewView;
//    private FaceOverlayView faceOverlay;
//    private ImageButton capture;
//    private TextView txtChallenge;
//
//    // Camera
//    private int cameraFacing = CameraSelector.LENS_FACING_FRONT;
//    private ImageCapture imageCapture;
//
//    // Face
//    private FaceDetector faceDetector;
//    private boolean faceInsideFrame = false;
//
//    // Challenge
//    enum Challenge { BLINK, SMILE, TURN_LEFT, TURN_RIGHT, LOOK_UP, LOOK_DOWN }
//    private Challenge currentChallenge;
//    private boolean challengePassed = false;
//
//    // Permission
//    private final ActivityResultLauncher<String> permissionLauncher =
//            registerForActivityResult(
//                    new ActivityResultContracts.RequestPermission(),
//                    granted -> {
//                        if (granted) startCamera();
//                        else Toast.makeText(this, "Izin kamera ditolak", Toast.LENGTH_SHORT).show();
//                    }
//            );
//
//    String aktivitas;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_camerax);
//
//        previewView = findViewById(R.id.cameraPreview);
//        faceOverlay = findViewById(R.id.faceOverlay);
//        capture = findViewById(R.id.capture);
//        txtChallenge = findViewById(R.id.txtChallenge);
//
//        capture.setEnabled(false);
//        capture.setAlpha(0.5f);
//
//        initFaceDetector();
//        generateRandomChallenge();
//
//        aktivitas = getIntent().getStringExtra("aktivitas");
//        capture.setOnClickListener(v -> {
//            if (!faceInsideFrame) {
//                Toast.makeText(this, "Posisikan wajah di dalam frame", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            if (!challengePassed) {
//                Toast.makeText(this, "Selesaikan challenge terlebih dahulu", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            takePicture();
//        });
//
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
//                != PackageManager.PERMISSION_GRANTED) {
//            permissionLauncher.launch(Manifest.permission.CAMERA);
//        } else {
//            startCamera();
//        }
//    }
//
//    // ===================== FACE DETECTOR =====================
//    private void initFaceDetector() {
//        FaceDetectorOptions options =
//                new FaceDetectorOptions.Builder()
//                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
//                        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
//                        .setMinFaceSize(0.15f)
//                        .build();
//
//        faceDetector = FaceDetection.getClient(options);
//    }
//
//    // ===================== CHALLENGE =====================
//    private void generateRandomChallenge() {
//        currentChallenge = new Random().nextBoolean()
//                ? Challenge.BLINK
//                : Challenge.SMILE;
//
//        txtChallenge.setText(
//                currentChallenge == Challenge.BLINK
//                        ? "Silakan KEDIPKAN mata"
//                        : "Silakan SENYUM"
//        );
//
//        challengePassed = false;
//    }
//
//    // ===================== CAMERA =====================
//    private void startCamera() {
//        ListenableFuture<ProcessCameraProvider> future =
//                ProcessCameraProvider.getInstance(this);
//
//        future.addListener(() -> {
//            try {
//                ProcessCameraProvider provider = future.get();
//
//                Preview preview = new Preview.Builder().build();
//                preview.setSurfaceProvider(previewView.getSurfaceProvider());
//
//                imageCapture =
//                        new ImageCapture.Builder()
//                                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
//                                .build();
//
//                ImageAnalysis analysis =
//                        new ImageAnalysis.Builder()
//                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
//                                .build();
//
//                analysis.setAnalyzer(
//                        Executors.newSingleThreadExecutor(),
//                        this::analyzeFrame
//                );
//
//                CameraSelector selector =
//                        new CameraSelector.Builder()
//                                .requireLensFacing(cameraFacing)
//                                .build();
//
//                provider.unbindAll();
//                provider.bindToLifecycle(
//                        this,
//                        selector,
//                        preview,
//                        imageCapture,
//                        analysis
//                );
//
//            } catch (ExecutionException | InterruptedException e) {
//                e.printStackTrace();
//            }
//        }, ContextCompat.getMainExecutor(this));
//    }
//
//    // ===================== ANALYSIS =====================
//    @OptIn(markerClass = ExperimentalGetImage.class)
//    private void analyzeFrame(ImageProxy proxy) {
//
//        Image mediaImage = proxy.getImage();
//        if (mediaImage == null) {
//            proxy.close();
//            return;
//        }
//
//        InputImage image =
//                InputImage.fromMediaImage(
//                        mediaImage,
//                        proxy.getImageInfo().getRotationDegrees()
//                );
//
//        faceDetector.process(image)
//                .addOnSuccessListener(faces -> handleFaces(faces, image))
//                .addOnCompleteListener(task -> proxy.close());
//    }
//
//    private void handleFaces(List<Face> faces, InputImage image) {
//
////        if (faces.isEmpty()) {
////            faceInsideFrame = false;
////            runOnUiThread(() -> faceOverlay.setFaceInside(false));
////            return;
////        }
//
//        if (faces.isEmpty()) {
//            faceInsideFrame = false;
//            challengePassed = false;
//
//            runOnUiThread(() -> {
//                faceOverlay.setFaceInside(false);
//                capture.setEnabled(false);
//                capture.setAlpha(0.5f);
//                txtChallenge.setText("Posisikan wajah di dalam frame");
//            });
//            return;
//        }
//
//
//        Face face = faces.get(0);
//
//        RectF faceNorm = normalizeFace(face.getBoundingBox(), image);
//
//        // mirror untuk kamera depan
//        if (cameraFacing == CameraSelector.LENS_FACING_FRONT) {
//            float left = 1f - faceNorm.right;
//            float right = 1f - faceNorm.left;
//            faceNorm.left = left;
//            faceNorm.right = right;
//        }
//
//        faceInsideFrame =
//                faceOverlay.getFrameNormalized()
//                        .contains(faceNorm.centerX(), faceNorm.centerY());
//
////        runOnUiThread(() ->
////                faceOverlay.setFaceInside(faceInsideFrame)
////        );
//
//        runOnUiThread(() -> {
//            faceOverlay.setFaceInside(faceInsideFrame);
//            capture.setEnabled(faceInsideFrame && challengePassed);
//            capture.setAlpha(faceInsideFrame && challengePassed ? 1f : 0.5f);
//        });
//
//
//        if (!faceInsideFrame) return;
//
//        // ===== ANTI SPOOF =====
//        if (!challengePassed) {
//            if (currentChallenge == Challenge.BLINK) {
//                Float left = face.getLeftEyeOpenProbability();
//                Float right = face.getRightEyeOpenProbability();
//                if (left != null && right != null &&
//                        left < 0.3f && right < 0.3f) {
//                    challengePassed = true;
//                    runOnUiThread(() ->
//                            txtChallenge.setText("✔ Kedipan terdeteksi")
//                    );
//                }
//            } else {
//                Float smile = face.getSmilingProbability();
//                if (smile != null && smile > 0.6f) {
//                    challengePassed = true;
//                    runOnUiThread(() ->
//                            txtChallenge.setText("✔ Senyum terdeteksi")
//                    );
//                }
//            }
//        }
//    }
//
//    // ===================== NORMALIZE =====================
//    private RectF normalizeFace(Rect box, InputImage image) {
//        return new RectF(
//                box.left / (float) image.getWidth(),
//                box.top / (float) image.getHeight(),
//                box.right / (float) image.getWidth(),
//                box.bottom / (float) image.getHeight()
//        );
//    }
//
//    // ===================== CAPTURE =====================
//    private void takePicture() {
//        String fileName = System.currentTimeMillis() + ".jpg";
//
//        ContentValues values = new ContentValues();
//        values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
//        values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
//        values.put(
//                MediaStore.MediaColumns.RELATIVE_PATH,
//                Environment.DIRECTORY_PICTURES + "/eabsensi"
//        );
//
//        ImageCapture.OutputFileOptions options =
//                new ImageCapture.OutputFileOptions.Builder(
//                        getContentResolver(),
//                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                        values
//                ).build();
//
//        imageCapture.takePicture(
//                options,
//                ContextCompat.getMainExecutor(this),
//                new ImageCapture.OnImageSavedCallback() {
//                    @Override
//                    public void onImageSaved(
//                            @NonNull ImageCapture.OutputFileResults output) {
//
//                        kirimHasil(fileName);
//
////                        finish();
//                    }
//
//                    @Override
//                    public void onError(@NonNull ImageCaptureException e) {
//                        Toast.makeText(
//                                CameraxActivity.this,
//                                e.getMessage(),
//                                Toast.LENGTH_SHORT
//                        ).show();
//                    }
//                }
//        );
//    }
//
//    private void kirimHasil(String fileName) {
//        if ("kehadiran".equals(aktivitas)) {
//            Intent i = new Intent(this, AbsensiKehadiranActivity.class);
//            i.putExtra("namafile", fileName);
//            startActivity(i);
//        } else if ("tugaslapangan".equals(aktivitas)) {
//            Intent i = new Intent(this, TugasLapanganFinalActivity.class);
//            i.putExtra("namafile", fileName);
//            startActivity(i);
//        } else {
//            Intent result = new Intent();
//            result.putExtra("namafile", fileName);
//            setResult(RESULT_OK, result);
//        }
//        finish();
//    }
//}

package go.pemkott.appsandroidmobiletebingtinggi.camerax;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.*;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;

import go.pemkott.appsandroidmobiletebingtinggi.R;
import go.pemkott.appsandroidmobiletebingtinggi.dinasluarkantor.tugaslapangan.TugasLapanganFinalActivity;
import go.pemkott.appsandroidmobiletebingtinggi.kehadiran.AbsensiKehadiranActivity;

public class CameraxActivity extends AppCompatActivity {

    // ================= UI =================
    private PreviewView previewView;
    private FaceOverlayView faceOverlay;
    private ImageButton capture;
    private TextView txtChallenge;

    // ================= CAMERA =================
    private ImageCapture imageCapture;
    private static final int CAMERA_FACING = CameraSelector.LENS_FACING_FRONT;

    // ================= FACE =================
    private FaceDetector faceDetector;
    private boolean faceInsideFrame = false;

    // ================= CHALLENGE =================
    enum Challenge {
        BLINK, SMILE, TURN_LEFT, TURN_RIGHT, LOOK_UP, LOOK_DOWN
    }

    private final List<Challenge> challengeQueue = new ArrayList<>();
    private int challengeIndex = 0;

    // ================= PERMISSION =================
    private final ActivityResultLauncher<String> permissionLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(),
                    granted -> {
                        if (granted) startCamera();
                        else Toast.makeText(this, "Izin kamera ditolak", Toast.LENGTH_SHORT).show();
                    }
            );

    private String aktivitas;

    // ================= LIFECYCLE =================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camerax);

        previewView = findViewById(R.id.cameraPreview);
        faceOverlay = findViewById(R.id.faceOverlay);
        capture = findViewById(R.id.capture);
        txtChallenge = findViewById(R.id.txtChallenge);

        aktivitas = getIntent().getStringExtra("aktivitas");

        capture.setEnabled(false);
        capture.setAlpha(0.5f);

        initFaceDetector();
        generateChallengeQueue();

        capture.setOnClickListener(v -> {
            if (!faceInsideFrame) {
                Toast.makeText(this, "Posisikan wajah di dalam frame", Toast.LENGTH_SHORT).show();
                return;
            }

            if (challengeIndex < challengeQueue.size()) {
                Toast.makeText(this, "Selesaikan challenge terlebih dahulu", Toast.LENGTH_SHORT).show();
                return;
            }

            takePicture();
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            permissionLauncher.launch(Manifest.permission.CAMERA);
        } else {
            startCamera();
        }
    }

    // ================= FACE DETECTOR =================
    private void initFaceDetector() {
        FaceDetectorOptions options =
                new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                        .setMinFaceSize(0.15f)
                        .build();

        faceDetector = FaceDetection.getClient(options);
    }

    // ================= CHALLENGE =================
    private void generateChallengeQueue() {

        Challenge[] pool = Challenge.values();
        List<Challenge> temp = new ArrayList<>();

        Random r = new Random();

        while (temp.size() < 2) {
            Challenge c = pool[r.nextInt(pool.length)];
            if (!temp.contains(c)) temp.add(c);
        }

        challengeQueue.clear();

        // masing-masing 2 kali
        for (Challenge c : temp) {
            challengeQueue.add(c);
//            challengeQueue.add(c);
        }

        Collections.shuffle(challengeQueue);

        challengeIndex = 0;
        showCurrentChallenge();
    }

    private void showCurrentChallenge() {
        txtChallenge.setText(
                "Challenge " + (challengeIndex + 1) + " / " + challengeQueue.size()
                        + "\n" + getChallengeText(challengeQueue.get(challengeIndex))
        );
    }

    private String getChallengeText(Challenge c) {
        switch (c) {
            case BLINK: return "KEDIPKAN mata";
            case SMILE: return "TERSENYUM";
            case TURN_LEFT: return "HADAP ke KIRI";
            case TURN_RIGHT: return "HADAP ke KANAN";
            case LOOK_UP: return "ANGKAT dagu";
            case LOOK_DOWN: return "TUNDUKKAN kepala";
            default: return "";
        }
    }

    // ================= CAMERA =================
    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> future =
                ProcessCameraProvider.getInstance(this);

        future.addListener(() -> {
            try {
                ProcessCameraProvider provider = future.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder().build();

                ImageAnalysis analysis =
                        new ImageAnalysis.Builder()
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .build();

                analysis.setAnalyzer(
                        Executors.newSingleThreadExecutor(),
                        this::analyzeFrame
                );

                provider.unbindAll();
                provider.bindToLifecycle(
                        this,
                        new CameraSelector.Builder()
                                .requireLensFacing(CAMERA_FACING)
                                .build(),
                        preview,
                        imageCapture,
                        analysis
                );

            } catch (Exception e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(this));
    }

    // ================= ANALYSIS =================
    @OptIn(markerClass = ExperimentalGetImage.class)
    private void analyzeFrame(ImageProxy proxy) {

        Image img = proxy.getImage();
        if (img == null) {
            proxy.close();
            return;
        }

        InputImage image = InputImage.fromMediaImage(
                img,
                proxy.getImageInfo().getRotationDegrees()
        );

        faceDetector.process(image)
                .addOnSuccessListener(faces -> handleFaces(faces, image))
                .addOnCompleteListener(t -> proxy.close());
    }

    private void handleFaces(List<Face> faces, InputImage image) {

        if (faces.isEmpty()) {
            resetState();
            return;
        }

        Face face = faces.get(0);

        RectF faceNorm = normalize(face.getBoundingBox(), image);

        // mirror kamera depan
        float left = 1f - faceNorm.right;
        float right = 1f - faceNorm.left;
        faceNorm.left = left;
        faceNorm.right = right;

        faceInsideFrame =
                faceOverlay.getFrameNormalized()
                        .contains(faceNorm.centerX(), faceNorm.centerY());

        runOnUiThread(() -> faceOverlay.setFaceInside(faceInsideFrame));

        if (!faceInsideFrame) {
            resetState();
            return;
        }

        if (challengeIndex >= challengeQueue.size()) {
            capture.setEnabled(true);
            capture.setAlpha(1f);
            return;
        }

        detectChallenge(face);
    }

    private void detectChallenge(Face face) {

        Challenge c = challengeQueue.get(challengeIndex);
        boolean passed = false;

        float eulerY = face.getHeadEulerAngleY(); // kiri-kanan
        float eulerX = face.getHeadEulerAngleX(); // atas-bawah

        switch (c) {
            case BLINK:
                Float l = face.getLeftEyeOpenProbability();
                Float r = face.getRightEyeOpenProbability();
                passed = l != null && r != null && l < 0.3f && r < 0.3f;
                break;

            case SMILE:
                Float s = face.getSmilingProbability();
                passed = s != null && s > 0.6f;
                break;

            case TURN_LEFT:
                passed = eulerY > 15;
                break;

            case TURN_RIGHT:
                passed = eulerY < -15;
                break;

            case LOOK_UP:
                passed = eulerX > 10;
                break;

            case LOOK_DOWN:
                passed = eulerX < -10;
                break;
        }

        if (passed) advanceChallenge();
    }

    private void advanceChallenge() {
        challengeIndex++;

        if (challengeIndex >= challengeQueue.size()) {
            runOnUiThread(() -> {
                txtChallenge.setText("✔ Verifikasi berhasil");
                capture.setEnabled(true);
                capture.setAlpha(1f);
            });
            return;
        }

        runOnUiThread(this::showCurrentChallenge);
    }

    private void resetState() {
        challengeIndex = 0;
        runOnUiThread(() -> {
            capture.setEnabled(false);
            capture.setAlpha(0.5f);
            showCurrentChallenge();
        });
    }

    // ================= UTILS =================
    private RectF normalize(Rect r, InputImage img) {
        return new RectF(
                r.left / (float) img.getWidth(),
                r.top / (float) img.getHeight(),
                r.right / (float) img.getWidth(),
                r.bottom / (float) img.getHeight()
        );
    }

    // ================= CAPTURE =================
    private void takePicture() {

        String fileName = System.currentTimeMillis() + ".jpg";

        ContentValues values = new ContentValues();
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
        values.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES + "/eabsensi");

        ImageCapture.OutputFileOptions options =
                new ImageCapture.OutputFileOptions.Builder(
                        getContentResolver(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        values
                ).build();

        imageCapture.takePicture(
                options,
                ContextCompat.getMainExecutor(this),
                new ImageCapture.OnImageSavedCallback() {

                    @Override
                    public void onImageSaved(@NonNull ImageCapture.OutputFileResults output) {
                        kirimHasil(fileName);
                    }

                    @Override
                    public void onError(@NonNull ImageCaptureException e) {
                        Toast.makeText(CameraxActivity.this,
                                e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void kirimHasil(String fileName) {
        Intent i;

        if ("kehadiran".equals(aktivitas)) {
            i = new Intent(this, AbsensiKehadiranActivity.class);
        } else if ("tugaslapangan".equals(aktivitas)) {
            i = new Intent(this, TugasLapanganFinalActivity.class);
        } else {
            i = new Intent();
            i.putExtra("namafile", fileName);
            setResult(RESULT_OK, i);
            finish();
            return;
        }

        i.putExtra("namafile", fileName);
        startActivity(i);
        finish();
    }
}
