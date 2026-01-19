package go.pemkott.appsandroidmobiletebingtinggi.dinasluarkantor.tugaslapangan;

import static go.pemkott.appsandroidmobiletebingtinggi.geolocation.model.LocationHelper.defaultLocation;
import static go.pemkott.appsandroidmobiletebingtinggi.konstanta.TimeFormat.SIMPLE_DATE_FORMAT_TAGING;
import static go.pemkott.appsandroidmobiletebingtinggi.konstanta.TimeFormat.SIMPLE_DATE_FORMAT_TAGING_PHOTO_REPORT;
import static go.pemkott.appsandroidmobiletebingtinggi.konstanta.TimeFormat.SIMPLE_FORMAT_JAM;
import static go.pemkott.appsandroidmobiletebingtinggi.konstanta.TimeFormat.SIMPLE_FORMAT_JAM_TAGING;
import static go.pemkott.appsandroidmobiletebingtinggi.konstanta.TimeFormat.SIMPLE_FORMAT_TANGGAL;
import static go.pemkott.appsandroidmobiletebingtinggi.konstanta.TimeFormat.hari;
import static go.pemkott.appsandroidmobiletebingtinggi.konstanta.TimeFormat.localeID;
import static go.pemkott.appsandroidmobiletebingtinggi.utils.FileUtil.getDriveFilePath;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Outline;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentContainerView;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.imageview.ShapeableImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import go.pemkott.appsandroidmobiletebingtinggi.R;
import go.pemkott.appsandroidmobiletebingtinggi.api.ResponsePOJO;
import go.pemkott.appsandroidmobiletebingtinggi.api.RetroClient;
import go.pemkott.appsandroidmobiletebingtinggi.camerax.CameraxActivity;
import go.pemkott.appsandroidmobiletebingtinggi.database.DatabaseHelper;
import go.pemkott.appsandroidmobiletebingtinggi.dialogview.DialogView;
import go.pemkott.appsandroidmobiletebingtinggi.geolocation.GetLocation;
import go.pemkott.appsandroidmobiletebingtinggi.konstanta.AmbilFoto;
import go.pemkott.appsandroidmobiletebingtinggi.konstanta.AmbilFotoLampiran;
import go.pemkott.appsandroidmobiletebingtinggi.konstanta.Lokasi;
import go.pemkott.appsandroidmobiletebingtinggi.utils.NetworkUtils;
import go.pemkott.appsandroidmobiletebingtinggi.viewmodel.LocationViewModel;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TugasLapanganFinalActivity extends AppCompatActivity implements OnMapReadyCallback {
    ProgressDialog progressDialog;

    //Gmaps
    private static final int REQUEST_CHECK_SETTINGS = 100;
    private Context mContext;
    private LocationViewModel locationViewModel;
    private Location locationObj;
    private GoogleMap map;
    double latGMap = 0, lngGMap = 0;
    double latitudeSaya = 3.327972364475644;
    double longitudeSaya = 99.16647248312584;
    Lokasi lokasi = new Lokasi();
    //Gmaps



    DatabaseHelper databaseHelper;
    ActivityResultLauncher<Intent> resultLauncher;
    private static final String TAG = TugasLapanganFinalActivity.class.getSimpleName();
    File imageFile;
    private String currentPhotoPath;
    private String rbLat;
    private String rbLng;
    private String rbTanggal;
    private String rbJam;
    private String rbKet;
    private String rbFakeGPS ="0" ;
    String pathDokument;
    String currentDateandTime = SIMPLE_DATE_FORMAT_TAGING.format(new Date());
    String currentDateandTimes = SIMPLE_DATE_FORMAT_TAGING_PHOTO_REPORT.format(new Date());
    String eOPD, eKelompok, eJabatan, latOffice, lngOffice;
    String sEmployeID, sToken, sAkses, sActive, sUsername, timetableid;
    String kegiatanlainnya, jamMasuk, jamPulang, hariIni;
    String tanggal;
    StringBuilder keterangan;
    String batasWaktu;
    SimpleDateFormat hari;

    String lampiran = null;
    String ekslampiran;

    TextView tvKegiatanFinal, tvSuratPerintah, titleDinasLuar, title_content;
    LinearLayout llPdfDinasLuar, llLampiranDinasLuar;
    ArrayList<String> kegiatans = new ArrayList<>();
    AmbilFoto ambilFoto = new AmbilFoto(TugasLapanganFinalActivity.this);
    AmbilFotoLampiran ambilFotoLampiran = new AmbilFotoLampiran(TugasLapanganFinalActivity.this);

    Bitmap rotationBitmapTag;
    Bitmap rotationBitmapSurat;

    ShapeableImageView ivFinalKegiatan, ivSuratPerintahFinal, iconLampiran;
    int mins, minspulang;
    DialogView dialogView = new DialogView(this);

    RadioGroup rgKehadiran;
    RadioButton radioSelectedKehadiran;
    int selected;

    Date jamMasukDate, jamPulangDate;
    Calendar cal = Calendar.getInstance();
    Date dateBatasWaktu;
    private boolean mockLocationsEnabled = false;
    int mock_location = 0;
    FragmentContainerView fragmentContainerView;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;
    File file;

    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.background_color));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.background_color));
        setContentView(R.layout.activity_tugas_lapangan_one);


        databaseHelper = new DatabaseHelper(this);
        tvKegiatanFinal = findViewById(R.id.tvKegiatanFinal);
        tvSuratPerintah = findViewById(R.id.tvSuratPerintah);
        titleDinasLuar = findViewById(R.id.titleDinasLuar);
        rgKehadiran = findViewById(R.id.rgKehadiran);
        title_content = findViewById(R.id.title_content);
        title_content.setText("TUGAS LAPANGAN");
        fragmentContainerView = findViewById(R.id.map);
        setRoundedBackground(fragmentContainerView);



        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(3000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

//        Image View
        ivFinalKegiatan = findViewById(R.id.ivFinalKegiatanTl);
        ivSuratPerintahFinal = findViewById(R.id.ivSuratPerintahFinalTl);
        iconLampiran = findViewById(R.id.iconLampiran);

//        Linear Layout
        llPdfDinasLuar = findViewById(R.id.llPdfDinasLuar);
        llLampiranDinasLuar = findViewById(R.id.llLampiranDinasLuar);

        mContext = this;
        setupViews();
        setupViewModel();
        checkLocationPermission();
        //Google Maps


        datauser();

        Intent intent = getIntent();
        titleDinasLuar.setText(intent.getStringExtra("title"));


        kegiatans.clear();
        kegiatans = TugasLapanganActivity.kegiatanChecked;
        kegiatanlainnya = TugasLapanganActivity.kegiatansLainnya;


        String myDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()+ "/eabsensi";
        String fileName = intent.getStringExtra("namafile");

        file = new File(myDir, fileName);
        byte[] imageBytes = ambilFoto.compressToMax80KB(file);

        Bitmap preview = BitmapFactory.decodeByteArray(
                imageBytes, 0, imageBytes.length
        );

        ivFinalKegiatan.setImageBitmap(preview);


        resultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @SuppressLint("Range")
            @Override
            public void onActivityResult(ActivityResult result) {
                Intent data = result.getData();
                if (data != null){
                    Uri sUri = data.getData();
                    File file= new File(sUri.getPath());
                    file.getName();
                    llLampiranDinasLuar.setVisibility(View.GONE);
                    llPdfDinasLuar.setVisibility(View.VISIBLE);
                    String displayName = null;

                    try {
                        pathDokument = getPDFPath(sUri);

                    }catch (Exception e){
                        Log.d(TAG, "Exception"+e);
                    }

                    if (sUri.toString().startsWith("content://")) {
                        Cursor cursor = null;
                        try {
                            cursor = TugasLapanganFinalActivity.this.getContentResolver().query(sUri, null, null, null, null);
                            if (cursor != null && cursor.moveToFirst()) {
                                displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                                tvSuratPerintah.setText(displayName);

                            }
                        } finally {
                            cursor.close();
                        }
                    } else if (sUri.toString().startsWith("file://")) {
                        displayName = file.getName();
                        tvSuratPerintah.setText(displayName);
                    }
                    handlerProgressDialog();
                }
            }
        });

        requestPermission();
        dataKegiatan();

        selected = rgKehadiran.getCheckedRadioButtonId();
        radioSelectedKehadiran = findViewById(selected);


        startLocationUpdates();
        rbTanggal = SIMPLE_FORMAT_TANGGAL.format(new Date());

    }

    public void fokusLokasi(View view){
        startLocationUpdates();
    }

    public void kirimDataDinasLuar(View view){
        requestPermission();
        if (mock_location == 1){

            dialogView.viewNotifKosong(TugasLapanganFinalActivity.this, "Anda terdeteksi menggunakan Fake GPS.", "Jika ditemukan berulang kali, akun anda akan terblokir otomatis dan tercatat Alpa.");

        }else{
            if (file == null || !file.exists() || file.length() == 0){
                dialogView.viewNotifKosong(TugasLapanganFinalActivity.this, "Anda harus melampirkan Foto Kegiatan dan Surat Perintah Perjalanan Dinas.", "");
            }
            else {
                Log.d("TLTL", "Tombol Kirim data");
                uploadImages();
            }
        }
    }


    public void uploadImages(){

        if (jamMasuk == null || jamPulang == null ){
            dialogView.viewNotifKosong(TugasLapanganFinalActivity.this, "Anda tidak memiliki Jadwal Kerja untuk hari ini", "");

        }
        else{

            periksaWaktu();
            hitungjarak();

            String rbValid;
            if (eJabatan.equals("2")){
                rbValid = "2";
            }else{
                rbValid = "0";
            }

            selected = rgKehadiran.getCheckedRadioButtonId();
            radioSelectedKehadiran = findViewById(selected);

            String rbStatus = "hadir";

            Date pulangPeriksa = null, tagingTimePeriksa = null;
            try {

                pulangPeriksa = SIMPLE_FORMAT_JAM.parse(jamPulang);
                String jamTagingPeriksa = SIMPLE_FORMAT_JAM_TAGING.format(new Date());
                tagingTimePeriksa = SIMPLE_FORMAT_JAM_TAGING.parse(jamTagingPeriksa);

            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (tagingTimePeriksa.getTime() < dateBatasWaktu.getTime()){
                dialogView.viewNotifKosong(TugasLapanganFinalActivity.this, "Anda hanya dapat mengisi absen masuk, "+batasWaktu+" menit sebelum Jam Masuk", "");
            }else{
                String rbPosisi = "";
                if (radioSelectedKehadiran.getText().toString().trim().equals("MASUK")){

                    rbPosisi = "tl-masuk";

                    if (tagingTimePeriksa.getTime() >= pulangPeriksa.getTime()){
                        showMessage("Peringatan", "Anda tidak dapat melakukan absensi masuk pada jam pulang kerja.");
                    }else{
                        kirimdataMasuk(rbValid, rbPosisi, rbStatus, "masuk", jamMasuk);
                    }


                } else if(radioSelectedKehadiran.getText().toString().trim().equals("PULANG")) {

                    rbPosisi = "tl-pulang";

                    kirimdataPulang(rbValid, rbPosisi, rbStatus, "pulang", jamPulang);

                }
            }
        }
    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);

            boolean isMock = mockLocationsEnabled || Objects.requireNonNull(locationResult.getLastLocation()).isFromMockProvider();
            if (isMock){
                mock_location = 1;
                rbFakeGPS ="1";
            }else{
                mock_location = 0;
            }
            if (map != null) {
                plotMarkers(locationResult.getLastLocation());
            }
        }
    };
    private void setRoundedBackground(FragmentContainerView view) {
        // Ganti warna dan radius sesuai kebutuhan Anda
        int backgroundColor = getResources().getColor(R.color.biru);
        float radius = getResources().getDimension(R.dimen.radius);

        view.setBackgroundResource(R.drawable.backgoundbase);
        view.setClipToOutline(true);
        view.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), radius);
            }
        });
    }

    private MultipartBody.Part prepareFilePart(String partName, byte[] imageBytes) {
        RequestBody requestBody =
                RequestBody.create(
                        imageBytes,
                        MediaType.parse("image/jpeg")
                );

        return MultipartBody.Part.createFormData(
                partName,
                "fototaging.jpg",
                requestBody
        );
    }

    private RequestBody textPart(String value) {
        return RequestBody.create(
                okhttp3.MediaType.parse("text/plain"),
                value
        );
    }
    public void kirimdataMasuk(String valid, String posisi, String status, String ketKehadiran, String jampegawai){
        progressDialog = new ProgressDialog(TugasLapanganFinalActivity.this, R.style.AppCompatAlertDialogStyle);
        progressDialog.setMessage("Sedang memproses...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        byte[] imageBytes = ambilFoto.compressToMax80KB(file);
        MultipartBody.Part fotoPart = prepareFilePart("fototaging", imageBytes);

        byte[] imageBytesLampiran = ambilFoto.compressToMax80KB(file);
        MultipartBody.Part lampiranPart = prepareFilePart("fototaging", imageBytesLampiran);

        Call<ResponsePOJO> call =
                RetroClient.getInstance().getApi().uploadTLMasuk(
                        fotoPart,
                        textPart(ketKehadiran),
                        textPart(eJabatan),
                        textPart(sEmployeID),
                        textPart(timetableid),
                        textPart(tanggal),
                        textPart(rbJam),
                        textPart(posisi),
                        textPart(status),
                        textPart(rbLat),
                        textPart(rbLng),
                        textPart(rbKet),
                        textPart(String.valueOf(mins)),
                        textPart(eOPD),
                        textPart(jampegawai),
                        textPart(valid),
                        lampiranPart,
                        textPart(rbFakeGPS),
                        textPart(batasWaktu)
                );


        call.enqueue(new Callback<ResponsePOJO>() {
            @Override
            public void onResponse(@NonNull Call<ResponsePOJO> call, @NonNull Response<ResponsePOJO> response) {
                progressDialog.dismiss();
                if (!response.isSuccessful()) {

                    dialogView.viewNotifKosong(
                            TugasLapanganFinalActivity.this,
                            "Gagal mengisi absensi",
                            "Silahkan coba kembali."
                    );
                    return;
                }

                Log.d("Absensi TL Log", "merespon");

                ResponsePOJO data = response.body();

                if (Objects.requireNonNull(response.body()).isStatus()){
                    dialogView.viewSukses(TugasLapanganFinalActivity.this, data.getRemarks());
                }else {
                    dialogView.viewNotifKosong(TugasLapanganFinalActivity.this, data.getRemarks(),"");
                }


            }

            @Override
            public void onFailure(@NonNull Call<ResponsePOJO> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                Log.d("Absensi TL Log", "gagal: "+t.getMessage());

                dialogView.pesanError(TugasLapanganFinalActivity.this);

            }
        });
    }

    public void kirimdataPulang(String valid, String posisi, String status, String ketKehadiran, String jampegawai){
        progressDialog = new ProgressDialog(TugasLapanganFinalActivity.this, R.style.AppCompatAlertDialogStyle);
        progressDialog.setMessage("Sedang memproses...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        byte[] imageBytes = ambilFoto.compressToMax80KB(file);
        MultipartBody.Part fotoPart = prepareFilePart("fototaging", imageBytes);

        byte[] imageBytesLampiran = ambilFoto.compressToMax80KB(file);
        MultipartBody.Part lampiranPart = prepareFilePart("fototaging", imageBytesLampiran);


        Call<ResponsePOJO> call =
                RetroClient.getInstance().getApi().uploadTLPulang(
                        fotoPart,
                        textPart(ketKehadiran),
                        textPart(eJabatan),
                        textPart(sEmployeID),
                        textPart(timetableid),
                        textPart(tanggal),
                        textPart(rbJam),
                        textPart(posisi),
                        textPart(status),
                        textPart(rbLat),
                        textPart(rbLng),
                        textPart(rbKet),
                        textPart(String.valueOf(mins)),
                        textPart(eOPD),
                        textPart(jampegawai),
                        textPart(valid),
                        lampiranPart,
                        textPart(rbFakeGPS),
                        textPart(batasWaktu)
                );

        call.enqueue(new Callback<ResponsePOJO>() {
            @Override
            public void onResponse(@NonNull Call<ResponsePOJO> call, @NonNull Response<ResponsePOJO> response) {
                Log.d("Absen TL", ekslampiran);
                progressDialog.dismiss();
                if (!response.isSuccessful()) {

                    dialogView.viewNotifKosong(
                            TugasLapanganFinalActivity.this,
                            "Gagal mengisi absensi",
                            "Silahkan coba kembali."
                    );
                    return;
                }

                ResponsePOJO data = response.body();

                if (Objects.requireNonNull(response.body()).isStatus()){
                    dialogView.viewSukses(TugasLapanganFinalActivity.this, data.getRemarks());
                }else {
                    dialogView.viewNotifKosong(TugasLapanganFinalActivity.this, data.getRemarks(),"");
                }


            }

            @Override
            public void onFailure(@NonNull Call<ResponsePOJO> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                dialogView.pesanError(TugasLapanganFinalActivity.this);


            }
        });
    }

    public void hitungjarak(){

        GetLocation getLocation = new GetLocation(getApplicationContext());
        Location location = getLocation.getLocation();
        location.getAccuracy();

        if(NetworkUtils.isConnected(this)){

            latitudeSaya = latGMap;
            longitudeSaya = lngGMap;

            rbLat = String.valueOf(latitudeSaya);
            rbLng = String.valueOf(longitudeSaya);
        }else{
            dialogView.viewNotifKosong(TugasLapanganFinalActivity.this, "Pastikan anda telah terhubung internet.", "");

        }

    }

    private void datauser(){
        Cursor res = databaseHelper.getAllData22();
        if (res.getCount()==0){
            showMessage("Error", "Nothing found");
            return;
        }

        while (res.moveToNext()){

            sEmployeID = res.getString(1);
            sUsername = res.getString(2);
            sAkses = res.getString(2);
            sActive = res.getString(4);
            sToken = res.getString(5);

        }

        hari = new SimpleDateFormat("EEE", localeID);
        tanggal = hari.format(new Date());

        Cursor tTimeTable = databaseHelper.getKegiatanTimeTable(sEmployeID, String.valueOf(hari(tanggal)));
        if (tTimeTable.getCount() == 0){
            jamMasuk = null;
            jamPulang = null;
        }

        while (tTimeTable.moveToNext()) {
            timetableid = tTimeTable.getString(2);
            hariIni = tTimeTable.getString(3);
            jamMasuk = tTimeTable.getString(5);
            jamPulang = tTimeTable.getString(6);
        }

        Cursor employe = databaseHelper.getDataEmployee(sEmployeID);
        while (employe.moveToNext()){
            eOPD = employe.getString(4);
            eKelompok = employe.getString(9);
            eJabatan = employe.getString(11);
            latOffice = employe.getString(15);
            lngOffice = employe.getString(16);
            batasWaktu = employe.getString(18);
        }
    }


    public void dataKegiatan(){
        StringBuilder stringBuilder = new StringBuilder();
        keterangan = new StringBuilder();
        for (int i = 0 ; i < kegiatans.size(); i++){
            if ((i - 1) == kegiatans.size() || i == kegiatans.size()-1 ){
                keterangan.append(kegiatans.get(i));
                stringBuilder.append(kegiatans.get(i));
            }else{
                stringBuilder.append(kegiatans.get(i)).append(", ");
                keterangan.append(kegiatans.get(i)).append(", ");
            }
        }
//        rbKet = String.valueOf(keterangan);
        if (!kegiatanlainnya.equals("kosong")){
            if (kegiatans.size() == 0){
                stringBuilder.append(kegiatanlainnya);
            }else{
                stringBuilder.append(", ").append(kegiatanlainnya);
            }
        }
        rbKet = String.valueOf(stringBuilder);
        tvKegiatanFinal.setText(stringBuilder);


    }

    public void addFotoKegiatan(View view){
        ambilFoto("kegiatan");
    }

    public void addFotoSuratPerintah(View view){
        viewLampiran();
    }

    public void viewLampiran(){
        Dialog dialogLampiran = new Dialog(TugasLapanganFinalActivity.this, R.style.DialogStyle);
        dialogLampiran.setContentView(R.layout.view_add_lampiran);
        LinearLayout llFileManager = dialogLampiran.findViewById(R.id.llFileManager);
        LinearLayout llKamera = dialogLampiran.findViewById(R.id.llKamera);
        LinearLayout llDokumen = dialogLampiran.findViewById(R.id.llDokumen);
        ImageView ivTutupViewLampiran = dialogLampiran.findViewById(R.id.ivTutupViewLampiran);

        llFileManager.setOnClickListener(v -> {
            progressDialog = new ProgressDialog(TugasLapanganFinalActivity.this, R.style.AppCompatAlertDialogStyle);
            progressDialog.setMessage("Sedang memproses...");
            progressDialog.show();
            Intent i = new Intent();
            i.setType("image/*");
            i.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(i, "Pilih Gambar"), 33);
            dialogLampiran.dismiss();
        });

        llDokumen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(TugasLapanganFinalActivity.this, R.style.AppCompatAlertDialogStyle);
                progressDialog.setMessage("Sedang memproses...");
                progressDialog.show();

                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                resultLauncher.launch(intent);
                dialogLampiran.dismiss();
            }
        });

        llKamera.setOnClickListener(v -> {
            Intent intent = new Intent(this, CameraxActivity.class);
            intent.putExtra("aktivitas", "lampirantl");
            cameraLauncher.launch(intent);

            dialogLampiran.dismiss();
        });
        ivTutupViewLampiran.setOnClickListener(v -> dialogLampiran.dismiss());

        dialogLampiran.show();
    }


    private ActivityResultLauncher<Intent> cameraLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {

                            String myDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()+ "/eabsensi";
                            String fileName = result.getData().getStringExtra("namafile");

                            File fileLampiran = new File(myDir, fileName);
                            byte[] imageBytes = ambilFoto.compressToMax80KB(fileLampiran);

                            Bitmap previewLampiran = BitmapFactory.decodeByteArray(
                                    imageBytes, 0, imageBytes.length
                            );


                            llLampiranDinasLuar.setVisibility(View.VISIBLE);
                            ivSuratPerintahFinal.setVisibility(View.VISIBLE);
                            llPdfDinasLuar.setVisibility(View.GONE);
                            iconLampiran.setVisibility(View.GONE);

                            ivSuratPerintahFinal.setImageBitmap(previewLampiran);

                        }
                    }
            );

    String fotoFileLampiran;
    private void ambilFoto(String addFoto){
        String filename = "photo";
        File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        try {
            imageFile = File.createTempFile(filename, ".png", storageDirectory);
            currentPhotoPath = null;
            currentPhotoPath = imageFile.getAbsolutePath();
            Uri imageUri = FileProvider.getUriForFile(TugasLapanganFinalActivity.this, "go.pemkott.appsandroidmobiletebingtinggi.fileprovider", imageFile);
            if (addFoto.equals("kegiatan")){

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, 1);
                progressDialog = new ProgressDialog(TugasLapanganFinalActivity.this, R.style.AppCompatAlertDialogStyle);
                progressDialog.setMessage("Sedang memproses...");
                progressDialog.show();

            }else{

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, 2);
                progressDialog = new ProgressDialog(TugasLapanganFinalActivity.this, R.style.AppCompatAlertDialogStyle);
                progressDialog.setMessage("Sedang memproses...");
                progressDialog.show();

            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_CANCELED){

            if (requestCode == 1 && resultCode == RESULT_OK) {

//                ivFinalKegiatan.setVisibility(View.VISIBLE);
//                File file = new File(currentPhotoPath);
//                Bitmap bitmap = ambilFoto.compressBitmapTo80KB(file);
//                rotationBitmapTag = Bitmap.createBitmap(bitmap, 0,0, bitmap.getWidth(), bitmap.getHeight(), AmbilFoto.exifInterface(currentPhotoPath, 0), true);
//
//                ivFinalKegiatan.setImageBitmap(rotationBitmapTag);
//
//                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                rotationBitmapTag.compress(Bitmap.CompressFormat.JPEG,90, byteArrayOutputStream);
//                byte[] imageInByte = byteArrayOutputStream.toByteArray();
//                fotoTaging =  Base64.encodeToString(imageInByte,Base64.DEFAULT);

                periksaWaktu();
                handlerProgressDialog();

            }
            else if (requestCode == 2 && resultCode == RESULT_OK){
                llLampiranDinasLuar.setVisibility(View.VISIBLE);
                ivSuratPerintahFinal.setVisibility(View.VISIBLE);
                llPdfDinasLuar.setVisibility(View.GONE);
                iconLampiran.setVisibility(View.GONE);

                File file = new File(currentPhotoPath);
                Bitmap bitmap = ambilFoto.compressBitmapTo80KB(file);
                rotationBitmapSurat = Bitmap.createBitmap(bitmap, 0,0, bitmap.getWidth(), bitmap.getHeight(), AmbilFoto.exifInterface(currentPhotoPath, 0), true);
                ivSuratPerintahFinal.setImageBitmap(rotationBitmapSurat);


                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                rotationBitmapSurat.compress(Bitmap.CompressFormat.JPEG,90, byteArrayOutputStream);
                byte[] imageInByte = byteArrayOutputStream.toByteArray();
                lampiran =  Base64.encodeToString(imageInByte,Base64.DEFAULT);
                ekslampiran = "jpg";

                handlerProgressDialog();
            }
            else if (requestCode == 33 && resultCode == Activity.RESULT_OK && data != null){
                requestPermission();

                llLampiranDinasLuar.setVisibility(View.VISIBLE);
                ivSuratPerintahFinal.setVisibility(View.VISIBLE);
                llPdfDinasLuar.setVisibility(View.GONE);
                iconLampiran.setVisibility(View.GONE);


                Uri selectedImageUri = data.getData();
                String FilePath2  = getDriveFilePath(selectedImageUri, TugasLapanganFinalActivity.this);

                File file1 = new File(FilePath2);
                Bitmap bitmap = ambilFoto.compressBitmapTo80KB(file1);
                rotationBitmapSurat = Bitmap.createBitmap(bitmap, 0,0, bitmap.getWidth(), bitmap.getHeight(), AmbilFoto.exifInterface(FilePath2, 0), true);

                ivSuratPerintahFinal.setImageBitmap(rotationBitmapSurat);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,90, byteArrayOutputStream);
                byte[] imageInByte = byteArrayOutputStream.toByteArray();
                lampiran =  Base64.encodeToString(imageInByte,Base64.DEFAULT);
                ekslampiran = "jpg";

                handlerProgressDialog();
            }
            else if (requestCode == 34 && resultCode == RESULT_OK && data != null) {
                requestPermission();
                try {
                    Uri pdfUri = data.getData();
                    pathDokument = getPDFPath(pdfUri);

                }catch (Exception e){
                    Log.d(TAG, "Exception"+e);
                }
                handlerProgressDialog();
            }
//            else if (requestCode == REQUEST_CODE_LAMPIRAN) {
//
//                llLampiranDinasLuar.setVisibility(View.VISIBLE);
//                ivSuratPerintahFinal.setVisibility(View.VISIBLE);
//                llPdfDinasLuar.setVisibility(View.GONE);
//                iconLampiran.setVisibility(View.GONE);
//
//                fotoFileLampiran = data.getStringExtra("KEY_HASIL");
//
//                String myDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()+ "/eabsensi";
//                File filelampiran = new File(myDir, fotoFileLampiran);
//                Bitmap gambarLampiran = BitmapFactory.decodeFile(filelampiran.getAbsolutePath());
//                ivSuratPerintahFinal.setImageBitmap(gambarLampiran);
//                Bitmap selectedBitmap = ambilFoto.compressBitmapTo80KB(filelampiran);
//                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                selectedBitmap.compress(Bitmap.CompressFormat.JPEG,90, byteArrayOutputStream);
//                byte[] imageInByte = byteArrayOutputStream.toByteArray();
//                lampiran =  Base64.encodeToString(imageInByte,Base64.DEFAULT);
//
//            }
        }else {
            progressDialog.dismiss();
        }


    }
    Date tagingTime;

    public void periksaWaktu(){

        try {
            jamMasukDate = SIMPLE_FORMAT_JAM.parse(jamMasuk);
            jamPulangDate = SIMPLE_FORMAT_JAM.parse(jamPulang);

            String jamTaging = SIMPLE_FORMAT_JAM_TAGING.format(new Date());

            SimpleDateFormat df = new SimpleDateFormat("HH:mm", localeID);
            Date d = df.parse(jamMasuk);
            cal.setTime(d);
            cal.add(Calendar.MINUTE, -(Integer.parseInt(batasWaktu)));
            String newTime = df.format(cal.getTime());

            tagingTime = SIMPLE_FORMAT_JAM_TAGING.parse(jamTaging);
            dateBatasWaktu = SIMPLE_FORMAT_JAM_TAGING.parse(newTime);

            long millis = tagingTime.getTime() - jamMasukDate.getTime();
            int hours = (int) (millis / (1000 * 60 * 60));

            mins = (int) ((millis / (1000 * 60)) % 60) + (hours * 60);
            if (mins <= 0){
                mins = 0;
            }

            long millispulang = jamPulangDate.getTime() - tagingTime.getTime();
            int hourspulang = (int) (millispulang / (1000 * 60 * 60));
            minspulang = (int) ((millispulang / (1000 * 60)) % 60) + (hourspulang * 60);


            rbJam = SIMPLE_FORMAT_JAM.format(new Date());

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public String getPDFPath(Uri uri){
        String absolutePath = "";
        try{
            InputStream inputStream = TugasLapanganFinalActivity.this.getContentResolver().openInputStream(uri);
            byte[] pdfInBytes = new byte[inputStream.available()];
            inputStream.read(pdfInBytes);

            int offset = 0;
            int numRead = 0;
            while (offset < pdfInBytes.length && (numRead = inputStream.read(pdfInBytes, offset, pdfInBytes.length - offset)) >= 0) {
                offset += numRead;
            }

            String mPath = "";
            mPath= TugasLapanganFinalActivity.this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)+"absensi-"+sEmployeID+"-"+currentDateandTimes + ".pdf";

            File pdfFile = new File(mPath);
            OutputStream op = new FileOutputStream(pdfFile);
            op.write(pdfInBytes);

            absolutePath = pdfFile.getPath();

            InputStream finput = new FileInputStream(pdfFile);
            byte[] imageBytes = new byte[(int)pdfFile.length()];
            finput.read(imageBytes, 0, imageBytes.length);
            finput.close();
            ekslampiran = "pdf";
            lampiran = Base64.encodeToString(imageBytes, Base64.DEFAULT);

        }catch (Exception ae){
            ae.printStackTrace();
        }
        return absolutePath;
    }

    ByteArrayOutputStream byteArrayTag, byteArraySurat;
    public Uri getImageUri(Bitmap inImage, int i) {

        if (i == 1){
            byteArrayTag = new ByteArrayOutputStream();
            inImage.compress(Bitmap.CompressFormat.PNG, 100, byteArrayTag);
            String path = MediaStore.Images.Media.insertImage(TugasLapanganFinalActivity.this.getContentResolver(), inImage, "absensi-"+sEmployeID+"-"+currentDateandTime, null);
            return Uri.parse(path);

        }else {
            byteArraySurat = new ByteArrayOutputStream();
            inImage.compress(Bitmap.CompressFormat.PNG, 100, byteArraySurat);
            String paths = MediaStore.Images.Media.insertImage(TugasLapanganFinalActivity.this.getContentResolver(), inImage, "absensi-"+sEmployeID+"-"+currentDateandTimes, null);
            return Uri.parse(paths);
        }

    }



    // this is all you need to grant your application external storage permision
    private void requestPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            int REQUEST_CODE_ASK_PERMISSIONS = 123;
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
        }
    }

    public void backFinalDinasLuar(View view){
        onBackPressed();
    }

    protected void onResume() {
        super.onResume();
        rbTanggal = SIMPLE_FORMAT_TANGGAL.format(new Date());



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        kegiatans.clear();
        stopLocationUpdates();
        finish();
    }


    public void handlerProgressDialog(){
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            //your code here
            progressDialog.dismiss();

        }, 1500);
    }

    public void handlerProgressDialog2(){
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            //your code here
            progressDialog.dismiss();
            finish();

        }, 1500);
    }

    public void showMessage(String title, String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.ThemeOverlay_App_MaterialAlertDialog);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }

    public void viewSukses(Context context){
        Dialog dialogSukes = new Dialog(context, R.style.DialogStyle);
        dialogSukes.setContentView(R.layout.view_sukses);
        dialogSukes.setCancelable(false);
        ImageView tvTutupDialog = dialogSukes.findViewById(R.id.tvTutupDialog);

        tvTutupDialog.setOnClickListener(v -> {
            stopLocationUpdates();

            dialogSukes.dismiss();
            TugasLapanganActivity.tL.finish();
            finish();
        });

        handlerProgressDialog2();
        dialogSukes.show();

    }

    private void setupViewModel() {
        locationViewModel = ViewModelProviders.of(this).get(LocationViewModel.class);
    }

    public void checkLocationPermission() {
        int hasWriteStoragePermission;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            hasWriteStoragePermission = getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
            if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CHECK_SETTINGS);
                return;
            }
//            subscribeToLocationUpdate();
        } else {
//            subscribeToLocationUpdate();
        }
    }

    private void setupViews() {

        //Maps
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }
    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }




    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    ArrayList<Location> locationArrayList = new ArrayList<>();
    private void plotMarkers(Location locationObj) {

        if(map != null){

            map.clear();
            map.addMarker(new MarkerOptions().position(new LatLng(locationObj.getLatitude(), locationObj.getLongitude())).icon(bitmapDescriptorFromVector(this, R.drawable.asn_lk)).title(lokasi.getAddress(TugasLapanganFinalActivity.this, locationObj.getLatitude(), locationObj.getLongitude())));
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(locationObj.getLatitude(), locationObj.getLongitude()), 19f));
            latGMap = locationObj.getLatitude();
            lngGMap = locationObj.getLongitude();



            locationArrayList.add(locationObj);


        }else{
            map.moveCamera(CameraUpdateFactory
                    .newLatLngZoom(defaultLocation, 19));
            map.getUiSettings().setMyLocationButtonEnabled(true);
        }

        stopLocationUpdates();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CHECK_SETTINGS) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

                }

            } else {

                requestPermission();
            }
        }
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.map = googleMap;


        try {
            boolean success = false;
            int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            switch (nightModeFlags) {
                case Configuration.UI_MODE_NIGHT_YES:
                    // Mode tema gelap aktif
                    success = googleMap.setMapStyle(
                            MapStyleOptions.loadRawResourceStyle(
                                    this, R.raw.style_json_dark));
                    break;
                case Configuration.UI_MODE_NIGHT_NO:
                    // Mode tema terang aktif
                    success = googleMap.setMapStyle(
                            MapStyleOptions.loadRawResourceStyle(
                                    this, R.raw.style_json));
                    break;
                case Configuration.UI_MODE_NIGHT_UNDEFINED:
                    // Mode tema tidak ditentukan
                     success = googleMap.setMapStyle(
                            MapStyleOptions.loadRawResourceStyle(
                                    this, R.raw.style_json));
                    break;
            }


            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }

        this.map.addMarker(new MarkerOptions().position(defaultLocation).icon(bitmapDescriptorFromVector(this, R.drawable.asn_lk)));
        this.map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                defaultLocation, 10));
        this.map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        this.map.getUiSettings().setMyLocationButtonEnabled( false );
        this.map.getUiSettings().setCompassEnabled( false );
        this.map.getUiSettings().setRotateGesturesEnabled(false);

        this.map.getCameraPosition();

        this.map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(@NonNull Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(@NonNull Marker marker) {
                // Inflate the layouts for the info window, title and snippet.
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents,
                        findViewById(R.id.map), false);

                TextView title = infoWindow.findViewById(R.id.title);
                title.setText(marker.getTitle());

                TextView snippet = infoWindow.findViewById(R.id.snippet);
                snippet.setText(marker.getSnippet());

                return infoWindow;
            }
        });
    }


    @Override
    protected void onStop() {
        super.onStop();
        stopLocationUpdates();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }
}