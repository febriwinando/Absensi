package go.pemkott.appsandroidmobiletebingtinggi.izin.cuti;

import static go.pemkott.appsandroidmobiletebingtinggi.geolocation.model.LocationHelper.defaultLocation;
import static go.pemkott.appsandroidmobiletebingtinggi.konstanta.TimeFormat.SIMPLE_DATE_FORMAT_TAGING_PHOTO_REPORT;
import static go.pemkott.appsandroidmobiletebingtinggi.konstanta.TimeFormat.SIMPLE_FORMAT_JAM;
import static go.pemkott.appsandroidmobiletebingtinggi.konstanta.TimeFormat.SIMPLE_FORMAT_JAM_MASUK_PULANG;
import static go.pemkott.appsandroidmobiletebingtinggi.konstanta.TimeFormat.SIMPLE_FORMAT_JAM_TAGING;
import static go.pemkott.appsandroidmobiletebingtinggi.konstanta.TimeFormat.SIMPLE_FORMAT_MENIT_MASUK_PULANG;
import static go.pemkott.appsandroidmobiletebingtinggi.konstanta.TimeFormat.SIMPLE_FORMAT_TANGGAL;
import static go.pemkott.appsandroidmobiletebingtinggi.konstanta.TimeFormat.hari;
import static go.pemkott.appsandroidmobiletebingtinggi.konstanta.TimeFormat.makeDateString;
import static go.pemkott.appsandroidmobiletebingtinggi.utils.FileUtil.getDriveFilePath;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import go.pemkott.appsandroidmobiletebingtinggi.camerax.CameraXLActivity;
import go.pemkott.appsandroidmobiletebingtinggi.camerax.CameraxActivity;
import go.pemkott.appsandroidmobiletebingtinggi.database.DatabaseHelper;
import go.pemkott.appsandroidmobiletebingtinggi.dialogview.DialogView;
import go.pemkott.appsandroidmobiletebingtinggi.dinasluarkantor.perjalanandinas.PerjalananDinasFinalActivity;
import go.pemkott.appsandroidmobiletebingtinggi.konstanta.AmbilFoto;
import go.pemkott.appsandroidmobiletebingtinggi.konstanta.AmbilFotoLampiran;
import go.pemkott.appsandroidmobiletebingtinggi.konstanta.Lokasi;
import go.pemkott.appsandroidmobiletebingtinggi.login.SessionManager;
import go.pemkott.appsandroidmobiletebingtinggi.utils.NetworkUtils;
import go.pemkott.appsandroidmobiletebingtinggi.viewmodel.LocationViewModel;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IzinCutiFinalActivity extends AppCompatActivity implements OnMapReadyCallback {
    ProgressDialog progressDialog;

    //Gmaps
    private static final int REQUEST_CHECK_SETTINGS = 100;
    private Context mContext;
    private LocationViewModel locationViewModel;
    private Location locationObj;
    private GoogleMap map;
    private static final String KEY_LOCATION = "location";
    double latGMap = 0, lngGMap = 0;
    double latitudeSaya = 3.327972364475644;
    double longitudeSaya = 99.16647248312584;
    Lokasi lokasi = new Lokasi();
    //Gmaps



    DatabaseHelper databaseHelper;
    ActivityResultLauncher<Intent> resultLauncher;
    private static final String TAG = PerjalananDinasFinalActivity.class.getSimpleName();
    File imageFile;
    private String  currentPhotoPath, rbLat, rbLng, jamTaging,  rbPosisi, rbStatus, rbKet,  rbValid, rbFakeGPS = "0" ;
    String pathDokument;
    String currentDateandTimes = SIMPLE_DATE_FORMAT_TAGING_PHOTO_REPORT.format(new Date());
    String eOPD, eKelompok, eJabatan, latOffice, lngOffice;
    String  sEmployeID, sToken, sAkses, sActive, sUsername, timetableid;
    String kegiatanlainnya, jamMasuk, jamPulang, hariIni;
    String tanggal;
    StringBuilder keterangan;
    String sTglMulai, sTglSAmpai;
    String jam_masuk, jam_pulang;
    String dariTanggal, sampaiTanggal;
    SimpleDateFormat hari;
    DatePickerDialog datePickerDialogMulai, datePickerDialogSampai;
    String ekslampiran;
    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    TextView tvHariMulai, tvBulanTahunMulai, tvHariSampai, tvBulanTahunSampai, tvKegiatanFinal, tvSuratPerintah, titleDinasLuar, title_content;
    LinearLayout llPdfDinasLuar, llLampiranDinasLuar, llLampiranDinasLuarCutiHead;
    ArrayList<String> kegiatans = new ArrayList<>();

    AmbilFoto ambilFoto = new AmbilFoto(IzinCutiFinalActivity.this);
    AmbilFotoLampiran ambilFotoLampiran = new AmbilFotoLampiran(IzinCutiFinalActivity.this);
    Bitmap rotationBitmapTag;
    Bitmap rotationBitmapSurat;

    ShapeableImageView ivFinalKegiatan, ivSuratPerintahFinal, iconLampiran;
    int jamMasukPulang, menitMasukPulang;
    int mins, minspulang;
    DialogView dialogView = new DialogView(this);

    private boolean mockLocationsEnabled = false;
    int mock_location = 0;
    FragmentContainerView fragmentContainerView;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;

    File file, filelampiran;
    SessionManager session;
    int pegawaiId;
    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.background_color));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.background_color));
        setContentView(R.layout.activity_izin_cuti_final);

        session = new SessionManager(this);
        pegawaiId = session.getPegawaiId();


        databaseHelper = new DatabaseHelper(this);
        tvHariMulai = findViewById(R.id.tvHariMulai);
        tvBulanTahunMulai = findViewById(R.id.tvBulanTahunMulai);
        tvHariSampai = findViewById(R.id.tvHariSampai);
        tvBulanTahunSampai = findViewById(R.id.tvBulanTahunSampai);
        tvKegiatanFinal = findViewById(R.id.tvKegiatanFinalCuti);
        tvSuratPerintah = findViewById(R.id.tvSuratPerintahCuti);
        titleDinasLuar = findViewById(R.id.titleDinasLuarCuti);
        title_content = findViewById(R.id.title_contentCuti);
        title_content.setText("CUTI");

//        Image View
        ivFinalKegiatan = findViewById(R.id.ivFinalKegiatanCuti);
        ivSuratPerintahFinal = findViewById(R.id.ivSuratPerintahFinalCuti);
        iconLampiran = findViewById(R.id.iconLampiranCuti);

//        Linear Layout
        llPdfDinasLuar = findViewById(R.id.llPdfDinasLuarCuti);
        llLampiranDinasLuar = findViewById(R.id.llLampiranDinasLuarCuti);
        llLampiranDinasLuarCutiHead = findViewById(R.id.llLampiranDinasLuarCutiHead);

        //Google Maps
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.putih));

        if (savedInstanceState != null) {
            locationObj = savedInstanceState.getParcelable(KEY_LOCATION);
        }


        fragmentContainerView = findViewById(R.id.map);
        setRoundedBackground(fragmentContainerView);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(3000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mContext = this;
        setupViews();
        setupViewModel();
        checkLocationPermission();
        //Google Maps


        datauser();

        Intent intent = getIntent();
        titleDinasLuar.setText(intent.getStringExtra("title"));


        kegiatans.clear();
        kegiatans = CutiActivity.kegiatanCheckedCuti;
        kegiatanlainnya = CutiActivity.kegiatansCutiLainnya;



        String myDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()+ "/eabsensi";
        String fileName = intent.getStringExtra("namafile");

        File originalFile = new File(myDir, fileName);
        try {
            file = ambilFoto.compressToFile(this, originalFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Bitmap preview = BitmapFactory.decodeFile(file.getAbsolutePath());
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
                    llLampiranDinasLuarCutiHead.setVisibility(View.GONE);
                    llPdfDinasLuar.setVisibility(View.VISIBLE);
                    String displayName;

                    try {
                        pathDokument = getPDFPath(sUri);

                    }catch (Exception e){
                        Log.d(TAG, "Exception"+e);
                    }

                    if (sUri.toString().startsWith("content://")) {
                        try (Cursor cursor = IzinCutiFinalActivity.this.getContentResolver().query(sUri, null, null, null, null)) {
                            if (cursor != null && cursor.moveToFirst()) {
                                displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                                tvSuratPerintah.setText(displayName);

                            }
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

        datePickerMulai();
        datePickerSampai();

        if (Build.VERSION.SDK_INT < 18 &&
                !Settings.Secure.getString(this.getContentResolver(), Settings
                        .Secure.ALLOW_MOCK_LOCATION).equals("0")) {
            mockLocationsEnabled = true;
        } else{
            mockLocationsEnabled = false;
        }

        startLocationUpdates();
    }


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

    public void kirimDataDinasLuar(View view){
        requestPermission();
        if (mock_location == 1){
            dialogView.viewNotifKosong(IzinCutiFinalActivity.this, "Anda terdeteksi menggunakan Fake GPS.", "Jika ditemukan berulang kali, akun anda akan terblokir otomatis dan tercatat Alpa.");
        }else {

            if (file == null || !file.exists() || file.length() == 0) {
                dialogView.viewNotifKosong(IzinCutiFinalActivity.this, "Anda harus melampirkan Foto Kegiatan dan Izin Cuti.", "");
            } else {
                uploadImages();
            }
        }
    }

    public void uploadImages(){
        periksaWaktu();
        hitungjarak();

        if (eJabatan.equals("2")){
            rbValid = "2";
        }else{
            rbValid = "0";
        }

        if (dariTanggal == null || sampaiTanggal == null){
            dialogView.viewNotifKosong(IzinCutiFinalActivity.this, "Rentang waktu masa cuti tidak boleh kosong.", "");

        }
        else{

            Date dateDariTgl = null, dateSampaiTgl = null;
            try {
                dateDariTgl = SIMPLE_FORMAT_TANGGAL.parse(dariTanggal);
                dateSampaiTgl = SIMPLE_FORMAT_TANGGAL.parse(sampaiTanggal);
            } catch (ParseException e) {
                e.printStackTrace();
            }


            if (dateDariTgl.getTime() > dateSampaiTgl.getTime()){
                dialogView.viewNotifKosong(this, "Perhatikan kembali rentang waktu yang anda pilih.", "Tidak boleh terbalik.");
            }else{
                kirimdata(rbValid, rbPosisi, rbStatus);
            }
        }
    }

    private RequestBody textPart(String value) {
        return RequestBody.create(
                okhttp3.MediaType.parse("text/plain"),
                value
        );
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
    public void kirimdata(String valid, String posisi, String status){

        progressDialog = new ProgressDialog(IzinCutiFinalActivity.this, R.style.AppCompatAlertDialogStyle);
        progressDialog.setMessage("Sedang memproses...");
        progressDialog.setCancelable(false);

        byte[] imageBytes = ambilFoto.compressToMax80KB(file);
        MultipartBody.Part fotoPart =
                prepareFilePart("fototaging", imageBytes);

        MultipartBody.Part lampiranPart;

        if ("pdf".equals(ekslampiran)) {
            lampiranPart =
                    prepareFilePart("lampiran", imageBytesDokumenPdf);
            Log.d("TugasLapanganFinalActivity", "PDF");
        } else {
            byte[] imageBytesLampiran =
                    ambilFoto.compressToMax80KB(filelampiran);
            lampiranPart =
                    prepareFilePart("lampiran", imageBytesLampiran);
            Log.d("TugasLapanganFinalActivity", "JPG");
        }

        Call<ResponsePOJO> call =
                RetroClient.getInstance().getApi().uploadAbsenIzinCuti(
                        fotoPart,
                        textPart(sEmployeID),
                        textPart(posisi),
                        textPart(status),
                        textPart(rbLat),
                        textPart(rbLng),
                        textPart(rbKet),
                        textPart(valid),
                        lampiranPart,
                        textPart(ekslampiran),
                        textPart(dariTanggal),
                        textPart(sampaiTanggal),
                        textPart(rbFakeGPS)
                );

        call.enqueue(new Callback<ResponsePOJO>() {
            @Override
            public void onResponse(@NonNull Call<ResponsePOJO> call, @NonNull Response<ResponsePOJO> response) {
                progressDialog.dismiss();

                if (!response.isSuccessful()) {


                    dialogView.viewNotifKosong(
                            IzinCutiFinalActivity.this,
                            "Gagal mengisi absensi",
                            "Silahkan coba kembali ya."
                    );
                    return;
                }

                ResponsePOJO data = response.body();

                if (Objects.requireNonNull(response.body()).isStatus()){
                    dialogView.viewSukses(IzinCutiFinalActivity.this, data.getRemarks());
                }else {
                    dialogView.viewNotifKosong(IzinCutiFinalActivity.this, data.getRemarks(),"");
                }

            }

            @Override
            public void onFailure(@NonNull Call<ResponsePOJO> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                dialogView.pesanError(IzinCutiFinalActivity.this);
            }
        });
        progressDialog.show();

    }

    public void hitungjarak(){



        if(NetworkUtils.isConnected(this)){

            latitudeSaya = latGMap;
            longitudeSaya = lngGMap;

            rbLat = String.valueOf(latitudeSaya);
            rbLng = String.valueOf(longitudeSaya);
        }else{
            dialogView.viewNotifKosong(IzinCutiFinalActivity.this, "Pastikan anda telah terhubung internet.", "");
        }

    }



    private void datauser() {
        Cursor res = databaseHelper.getAllData22(pegawaiId);
        if (res.getCount() == 0) {
            showMessage("Error", "Nothing found");
            return;
        }

        while (res.moveToNext()) {
            sEmployeID = res.getString(1);
            sUsername = res.getString(2);
            sAkses = res.getString(2);
            sActive = res.getString(4);
            sToken = res.getString(5);
        }

        hari = new SimpleDateFormat("EEE");
        tanggal = hari.format(new Date());
        Cursor tTimeTable = databaseHelper.getKegiatanTimeTable(sEmployeID, String.valueOf(hari(tanggal)));

        if(tTimeTable.getCount() == 0){
            jamMasuk = null;
            jamPulang = null;
            return;
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
                stringBuilder.append(kegiatans.get(i)+", " );
                keterangan.append(kegiatans.get(i)+", ");
            }
        }
//        rbKet = String.valueOf(keterangan);
        if (!kegiatanlainnya.equals("kosong")){
            if (kegiatans.size() == 0){
                stringBuilder.append(kegiatanlainnya);
            }else{
                stringBuilder.append(", "+kegiatanlainnya);
            }
        }

        rbKet = String.valueOf(stringBuilder);

        tvKegiatanFinal.setText(stringBuilder);


    }


    public void addFotoSuratPerintah(View view){
        viewLampiran();
    }

    public void viewLampiran(){
        Dialog dialogLampiran = new Dialog(IzinCutiFinalActivity.this, R.style.DialogStyle);
        dialogLampiran.setContentView(R.layout.view_add_lampiran);
        LinearLayout llFileManager = dialogLampiran.findViewById(R.id.llFileManager);
        LinearLayout llKamera = dialogLampiran.findViewById(R.id.llKamera);
        LinearLayout llDokumen = dialogLampiran.findViewById(R.id.llDokumen);
        ImageView ivTutupViewLampiran = dialogLampiran.findViewById(R.id.ivTutupViewLampiran);

        llFileManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(IzinCutiFinalActivity.this, R.style.AppCompatAlertDialogStyle);
                progressDialog.setMessage("Sedang memproses...");
                progressDialog.show();

                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(i, "Pilih Gambar"), 33);

                dialogLampiran.dismiss();
            }
        });

        llDokumen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = new ProgressDialog(IzinCutiFinalActivity.this, R.style.AppCompatAlertDialogStyle);
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

        llKamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(IzinCutiFinalActivity.this, CameraXLActivity.class);
                intent.putExtra("aktivitas", "izincuti");
                cameraLauncher.launch(intent);
                dialogLampiran.dismiss();
            }
        });

        ivTutupViewLampiran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogLampiran.dismiss();
            }
        });

        dialogLampiran.show();
    }

    private ActivityResultLauncher<Intent> cameraLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK && result.getData() != null) {

                            String myDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()+ "/eabsensi";
                            String fileName = result.getData().getStringExtra("namafile");

                            filelampiran = new File(myDir, fileName);
                            byte[] imageBytes = ambilFoto.compressToMax80KB(filelampiran);

                            Bitmap previewLampiran = BitmapFactory.decodeByteArray(
                                    imageBytes, 0, imageBytes.length
                            );


                            llLampiranDinasLuar.setVisibility(View.VISIBLE);
                            ivSuratPerintahFinal.setVisibility(View.VISIBLE);
                            llPdfDinasLuar.setVisibility(View.GONE);
                            iconLampiran.setVisibility(View.GONE);
                            ekslampiran = "jpg";
                            ivSuratPerintahFinal.setImageBitmap(previewLampiran);

                        }
                    }
            );
    static final int REQUEST_CODE_LAMPIRAN = 341;
    String fotoFileLampiran;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_CANCELED){

            if (requestCode == 1 && resultCode == RESULT_OK) {

                ivFinalKegiatan.setVisibility(View.VISIBLE);
                File file = new File(currentPhotoPath);
                Bitmap bitmap = ambilFoto.fileBitmap(file);
                rotationBitmapTag = Bitmap.createBitmap(bitmap, 0,0, bitmap.getWidth(), bitmap.getHeight(), AmbilFoto.exifInterface(currentPhotoPath, 0), true);

                ivFinalKegiatan.setImageBitmap(rotationBitmapTag);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                rotationBitmapTag.compress(Bitmap.CompressFormat.JPEG,75, byteArrayOutputStream);
                byte[] imageInByte = byteArrayOutputStream.toByteArray();

                periksaWaktu();
                handlerProgressDialog();


            }
            else if (requestCode == 2 && resultCode == RESULT_OK){
                llLampiranDinasLuar.setVisibility(View.VISIBLE);
                llLampiranDinasLuarCutiHead.setVisibility(View.VISIBLE);
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
                ekslampiran = "jpg";

                handlerProgressDialog();
            }
            else if (requestCode == 33 && resultCode == Activity.RESULT_OK && data != null){
                requestPermission();

                llLampiranDinasLuar.setVisibility(View.VISIBLE);
                llLampiranDinasLuarCutiHead.setVisibility(View.VISIBLE);
                ivSuratPerintahFinal.setVisibility(View.VISIBLE);
                llPdfDinasLuar.setVisibility(View.GONE);
                iconLampiran.setVisibility(View.GONE);

                Uri selectedImageUri = data.getData();
                String FilePath2  = getDriveFilePath(selectedImageUri, IzinCutiFinalActivity.this);
                File originalLampiran = new File(FilePath2);
                try {
                    filelampiran = ambilFoto.compressToFile(this, originalLampiran);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Bitmap preview = BitmapFactory.decodeFile(filelampiran.getAbsolutePath());
                ivSuratPerintahFinal.setImageBitmap(preview);
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

            }else if (requestCode == REQUEST_CODE_LAMPIRAN) {

                llLampiranDinasLuar.setVisibility(View.VISIBLE);
                ivSuratPerintahFinal.setVisibility(View.VISIBLE);
                llPdfDinasLuar.setVisibility(View.GONE);
                iconLampiran.setVisibility(View.GONE);

                fotoFileLampiran = data.getStringExtra("KEY_HASIL");

                String myDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()+ "/eabsensi";
                File filelampiran = new File(myDir, fotoFileLampiran);
                Bitmap gambarLampiran = BitmapFactory.decodeFile(filelampiran.getAbsolutePath());
                ivSuratPerintahFinal.setImageBitmap(gambarLampiran);
                Bitmap selectedBitmap = ambilFoto.compressBitmapTo80KB(filelampiran);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                selectedBitmap.compress(Bitmap.CompressFormat.JPEG,90, byteArrayOutputStream);
                byte[] imageInByte = byteArrayOutputStream.toByteArray();

            }
        }else {
            progressDialog.dismiss();
        }
    }




    public void periksaWaktu(){
        try {
            Date jam = SIMPLE_FORMAT_JAM.parse(jamMasuk);
            Date pulang = SIMPLE_FORMAT_JAM.parse(jamPulang);

            jamTaging = SIMPLE_FORMAT_JAM_TAGING.format(new Date());


            Date tagingTime = SIMPLE_FORMAT_JAM_TAGING.parse(jamTaging);

            long millis = tagingTime.getTime() - jam.getTime();
            int hours = (int) (millis / (1000 * 60 * 60));
            mins = (int) ((millis / (1000 * 60)) % 60) + (hours * 60);

            if (mins < 0){
                mins = 0;
            }

            long millispulang = pulang.getTime() - tagingTime.getTime();
            int hourspulang = (int) (millispulang / (1000 * 60 * 60));
            minspulang = (int) ((millispulang / (1000 * 60)) % 60) + (hourspulang * 60);

            jamMasukPulang = Integer.parseInt(SIMPLE_FORMAT_JAM_MASUK_PULANG.format(new Date()));
            menitMasukPulang = Integer.parseInt(SIMPLE_FORMAT_MENIT_MASUK_PULANG.format(new Date()));

            rbStatus = "izin";
            rbPosisi = "ct";

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    byte[] imageBytesDokumenPdf;

    public String getPDFPath(Uri uri){
        String absolutePath = "";

        try{
            InputStream inputStream = IzinCutiFinalActivity.this.getContentResolver().openInputStream(uri);
            byte[] pdfInBytes = new byte[inputStream.available()];
            inputStream.read(pdfInBytes);
            int offset = 0;
            int numRead;
            while (offset < pdfInBytes.length && (numRead = inputStream.read(pdfInBytes, offset, pdfInBytes.length - offset)) >= 0) {
                offset += numRead;
            }

            String mPath;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1) {
                mPath= IzinCutiFinalActivity.this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)+"absensi-"+sEmployeID+"-"+currentDateandTimes + ".pdf";
            }
            else
            {
                mPath= Environment.getExternalStorageDirectory().toString() + "absensi-"+sEmployeID+"-"+currentDateandTimes + ".pdf";
            }

            filelampiran = new File(mPath);
            OutputStream op = new FileOutputStream(filelampiran);
            op.write(pdfInBytes);

            absolutePath = filelampiran.getPath();
            InputStream finput = new FileInputStream(filelampiran);
            imageBytesDokumenPdf = new byte[(int)filelampiran.length()];
            finput.read(imageBytesDokumenPdf, 0, imageBytesDokumenPdf.length);
            finput.close();
            ekslampiran = "pdf";

        }catch (Exception ae){
            ae.printStackTrace();
        }
        return absolutePath;
    }


    public void llMulaiDinasLuar(View view){
        datePickerDialogMulai.show();
    }

    private void datePickerMulai(){
        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, tahun, bulan, hari) -> {

            bulan = bulan + 1;
            String date = makeDateString(tahun, bulan);

            Date drTgl = null;
            try {
                drTgl = SIMPLE_FORMAT_TANGGAL.parse(tahun+"-"+bulan+"-"+hari);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            dariTanggal = SIMPLE_FORMAT_TANGGAL.format(drTgl);

            tvHariMulai.setText(String.valueOf(hari));
            tvBulanTahunMulai.setText(date);
            sTglMulai = tahun+"-"+bulan+"-"+hari;

        };

        Calendar calendar = Calendar.getInstance();
        int tahun = calendar.get(Calendar.YEAR);
        int bulan = calendar.get(Calendar.MONTH);
        int hari = calendar.get(Calendar.DAY_OF_MONTH);

        int style = android.R.style.Theme_DeviceDefault_Light_Dialog;
        Date today = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        c.add( Calendar.MONTH, 0);
        long maxDate = c.getTime().getTime();

        Calendar d = Calendar.getInstance();
        d.setTime(today);
        d.add( Calendar.MONTH, -1);
        long minDate = d.getTime().getTime();

        datePickerDialogMulai = new DatePickerDialog(this, style, dateSetListener, tahun, bulan, hari);
        datePickerDialogMulai.getDatePicker().setMaxDate(maxDate);
        datePickerDialogMulai.getDatePicker().setMinDate(minDate);

    }

    private void datePickerSampai(){
        DatePickerDialog.OnDateSetListener dateSetListener = (datePicker, tahun, bulan, hari) -> {
            bulan = bulan + 1;
            String date = makeDateString(tahun, bulan);

            Date spTgl = null;
            try {
                spTgl = SIMPLE_FORMAT_TANGGAL.parse(tahun+"-"+bulan+"-"+hari);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            sampaiTanggal = SIMPLE_FORMAT_TANGGAL.format(spTgl);

            tvHariSampai.setText(String.valueOf(hari));
            tvBulanTahunSampai.setText(date);
            sTglSAmpai = tahun+"-"+bulan+"-"+hari;
        };

        Calendar calendar = Calendar.getInstance();
        int tahun = calendar.get(Calendar.YEAR);
        int bulan = calendar.get(Calendar.MONTH);
        int hari = calendar.get(Calendar.DAY_OF_MONTH);

        int style = android.R.style.Theme_DeviceDefault_Light_Dialog;
        Date today = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(today);
        c.add( Calendar.MONTH, 1);
        long maxDate = c.getTime().getTime();

        Calendar d = Calendar.getInstance();
        d.setTime(today);
        d.add( Calendar.MONTH, 0);
        long minDate = d.getTime().getTime();
        datePickerDialogSampai = new DatePickerDialog(this, style, dateSetListener, tahun, bulan, hari);
        datePickerDialogSampai.getDatePicker().setMaxDate(maxDate);
        datePickerDialogSampai.getDatePicker().setMinDate(minDate);

    }

    public void llSampaiDinasLuar(View view){
        datePickerDialogSampai.show();
    }


    //    Maps
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
        } else {
        }
    }


    public void backAbsenMasuk(View view){
        stopLocationUpdates();
        finish();
    }

    private void setupViews() {

        //Maps
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }



    public void fokusLokasi(View view){
        startLocationUpdates();
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        }
    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);

            boolean isMock = mockLocationsEnabled || locationResult.getLastLocation().isFromMockProvider();
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
            map.addMarker(new MarkerOptions().position(new LatLng(locationObj.getLatitude(), locationObj.getLongitude())).icon(bitmapDescriptorFromVector(this, R.drawable.asn_lk)).title(lokasi.getAddress(IzinCutiFinalActivity.this, locationObj.getLatitude(), locationObj.getLongitude())));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(locationObj.getLatitude(), locationObj.getLongitude()), 19f));
            map.getUiSettings().setMyLocationButtonEnabled(true);
            latGMap = locationObj.getLatitude();
            lngGMap = locationObj.getLongitude();


            locationArrayList.add(locationObj);


        }else{

            map.moveCamera(CameraUpdateFactory
                    .newLatLngZoom(defaultLocation, 19f));
            map.getUiSettings().setMyLocationButtonEnabled(true);
        }

        stopLocationUpdates();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {

                }

            } else {

                checkLocationPermission();
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
                defaultLocation, 10f));
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

    // this is all you need to grant your application external storage permision
    private void requestPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE_ASK_PERMISSIONS);
        }
    }

    public void backFinalDinasLuar(View view){
        onBackPressed();
    }

    protected void onResume() {
        super.onResume();
//        rbTanggal = SIMPLE_FORMAT_TANGGAL.format(new Date());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopLocationUpdates();
        kegiatans.clear();
        finish();
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
            CutiActivity.cuti.finish();
            finish();
        });

        handlerProgressDialog2();
        dialogSukes.show();

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