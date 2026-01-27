package go.pemkott.appsandroidmobiletebingtinggi.izinsift.izinsiftsakit;

import static go.pemkott.appsandroidmobiletebingtinggi.geolocation.model.LocationHelper.defaultLocation;
import static go.pemkott.appsandroidmobiletebingtinggi.konstanta.TimeFormat.SIMPLE_DATE_FORMAT_TAGING_PHOTO_REPORT;
import static go.pemkott.appsandroidmobiletebingtinggi.konstanta.TimeFormat.SIMPLE_FORMAT_JAM;
import static go.pemkott.appsandroidmobiletebingtinggi.konstanta.TimeFormat.SIMPLE_FORMAT_JAM_TAGING;
import static go.pemkott.appsandroidmobiletebingtinggi.konstanta.TimeFormat.SIMPLE_FORMAT_TANGGAL;
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
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

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
import java.util.Locale;
import java.util.Objects;

import go.pemkott.appsandroidmobiletebingtinggi.NewDashboard.DashboardVersiOne;
import go.pemkott.appsandroidmobiletebingtinggi.R;
import go.pemkott.appsandroidmobiletebingtinggi.api.ResponsePOJO;
import go.pemkott.appsandroidmobiletebingtinggi.api.RetroClient;
import go.pemkott.appsandroidmobiletebingtinggi.camerax.CameraXLActivity;
import go.pemkott.appsandroidmobiletebingtinggi.database.DatabaseHelper;
import go.pemkott.appsandroidmobiletebingtinggi.dialogview.DialogView;
import go.pemkott.appsandroidmobiletebingtinggi.izin.sakit.IzinSakitFinalActivity;
import go.pemkott.appsandroidmobiletebingtinggi.izinsift.JadwalIzinSiftActivity;
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

public class IzinSakitSiftFinalActivity extends AppCompatActivity implements OnMapReadyCallback {

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
    private static final String TAG = IzinSakitSiftFinalActivity.class.getSimpleName();
    File imageFile;
    private String  currentPhotoPath;
    private String rbLat;
    private String rbLng;
    private String rbTanggal;
    private String rbJam;
    private String rbKet;
    private String rbFakeGPS = "0" ;
    String pathDokument;
    String currentDateandTimes = SIMPLE_DATE_FORMAT_TAGING_PHOTO_REPORT.format(new Date());
    String eOPD, eKelompok, eJabatan, latOffice, lngOffice;
    String sEmployeID, sToken, sAkses, sActive, sUsername, timetableid;
    String kegiatanlainnya;
    String tanggal;
    StringBuilder keterangan;
    String jam_masuk, jam_pulang, batasWaktu;
    SimpleDateFormat hari;

    String ekslampiran;

    TextView tvHariMulai, tvBulanTahunMulai, tvHariSampai, tvBulanTahunSampai, tvKegiatanFinal, tvSuratPerintah, titleDinasLuar, title_content;
    LinearLayout llPdfDinasLuar, llLampiranDinasLuar, llLampiranDinasLuarHead;
    ArrayList<String> kegiatans = new ArrayList<>();
    AmbilFoto ambilFoto = new AmbilFoto(IzinSakitSiftFinalActivity.this);
    AmbilFotoLampiran ambilFotoLampiran = new AmbilFotoLampiran(IzinSakitSiftFinalActivity.this);

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

    private boolean mockLocationsEnabled;
    int mock_location = 0;
    String inisialsift, tipesift, masuksift, pulangsift, idsift;
    File file, filelampiran;
    SessionManager session;
    int pegawaiId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.background_color));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.background_color));
        setContentView(R.layout.activity_izin_sakit_sift_final);
        session = new SessionManager(this);
        pegawaiId = session.getPegawaiId();


        databaseHelper = new DatabaseHelper(this);
        tvKegiatanFinal = findViewById(R.id.tvKegiatanFinal);
        tvSuratPerintah = findViewById(R.id.tvSuratPerintah);
        titleDinasLuar = findViewById(R.id.titleDinasLuar);
        rgKehadiran = findViewById(R.id.rgKehadiran);
        title_content = findViewById(R.id.title_contentsakit);
        title_content.setText(R.string.sakit);

//        Image View
        ivFinalKegiatan = findViewById(R.id.ivFinalKegiatanSakit);
        ivSuratPerintahFinal = findViewById(R.id.ivSuratPerintahFinalSakit);
        iconLampiran = findViewById(R.id.iconLampiranSakit);

//        Linear Layout
        llPdfDinasLuar = findViewById(R.id.llPdfDinasLuarSakit);
        llLampiranDinasLuar = findViewById(R.id.llLampiranDinasLuarSakit);
        llLampiranDinasLuarHead = findViewById(R.id.llLampiranDinasLuarHead);

        //Google Maps
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.putih));

        if (savedInstanceState != null) {
            locationObj = savedInstanceState.getParcelable(KEY_LOCATION);
        }

        mContext = this;
        setupViews();
        setupViewModel();
        checkLocationPermission();
        //Google Maps


        datauser();

        jam_masuk = DashboardVersiOne.jam_masuk;
        jam_pulang = DashboardVersiOne.jam_pulang;
//        titleDinasLuar.setText(intent.getString("SAKIT"));
        rbTanggal = JadwalIzinSiftActivity.tanggalSift;
        inisialsift = JadwalIzinSiftActivity.inisialsift;
        idsift = JadwalIzinSiftActivity.idsift;
        tipesift = JadwalIzinSiftActivity.tipesift;
        masuksift = JadwalIzinSiftActivity.masuksift;
        pulangsift = JadwalIzinSiftActivity.pulangsift;


        kegiatans.clear();
        kegiatans = SakitSiftActivity.kegiatanCheckedSakit;
        kegiatanlainnya = SakitSiftActivity.kegiatansSakitLainnya;

        Intent intent = getIntent();

        String myDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()+ "/eabsensi";
        String fileName = intent.getStringExtra("namafile");

        File originalfile = new File(myDir, fileName);
        try {
            file = ambilFoto.compressToFile(this, originalfile);
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
                    llLampiranDinasLuarHead.setVisibility(View.GONE);
                    llPdfDinasLuar.setVisibility(View.VISIBLE);
                    String displayName;

                    try {
                        pathDokument = getPDFPath(sUri);

                    }catch (Exception e){
                        Log.d(TAG, "Exception"+e);
                    }

                    if (sUri.toString().startsWith("content://")) {
                        try (Cursor cursor = IzinSakitSiftFinalActivity.this.getContentResolver().query(sUri, null, null, null, null)) {
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

        selected = rgKehadiran.getCheckedRadioButtonId();
        radioSelectedKehadiran = findViewById(selected);


        if (!Settings.Secure.getString(this.getContentResolver(), Settings
                .Secure.ALLOW_MOCK_LOCATION).equals("0")) {
            mockLocationsEnabled = true;
        } else{
            mockLocationsEnabled = false;
        }

    }



    public void kirimdataIzinSakitShift(View view){
        requestPermission();
        Log.d("ABSEN_MASUK_PAGI", "kirimdataIzinSakitShift");

        if (mock_location == 1){

            dialogView.viewNotifKosong(IzinSakitSiftFinalActivity.this, "Anda terdeteksi menggunakan Fake GPS.", "Jika ditemukan berulang kali, akun anda akan terblokir otomatis dan tercatat Alpa.");

        }else {
            if (file == null || !file.exists() || file.length() == 0) {
                dialogView.viewNotifKosong(IzinSakitSiftFinalActivity.this, "Anda harus melampirkan Foto Kondisi Kesehatan dan Surat Dokter.", "");
            } else {
                uploadImages();
            }
        }
    }


    public void uploadImages(){
        selected = rgKehadiran.getCheckedRadioButtonId();
        radioSelectedKehadiran = findViewById(selected);
        String rbValid = "0";
        String rbStatus = "izin";
        Log.d("ABSEN_MASUK_PAGI", "uploadImages");

        periksaWaktu();
        hitungjarak();
        String tanggal = SIMPLE_FORMAT_TANGGAL.format(new Date());
        Date hariini = null;
        try {
            hariini = SIMPLE_FORMAT_TANGGAL.parse(tanggal);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (tipesift.equals("malam")){
            Log.d("ABSEN_MASUK_PAGI", "absen malam");

            if (radioSelectedKehadiran.getText().toString().equals("MASUK")){

                Log.d("ABSEN_MASUK_PAGI", "absen malam MASUK");


                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                // tanggal hari ini
                Date today = null;
                try {
                    today = sdf.parse(sdf.format(new Date()));
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                // tanggal yang mau dibandingkan
                Date targetDate = null;
                try {
                    targetDate = sdf.parse(rbTanggal);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                Log.d("ABSEN_MASUK_PAGI", "targetDate: "+targetDate+" && today: "+today);


                if (targetDate.after(today)) {
//                    Log.d("ABSEN_MASUK_PAGI", "Today");
//
//                    kirimdataMasuk(rbValid, rbStatus, "masuk", masuksift);
                } else if (targetDate.before(today)) {
                    Log.d("ABSEN_MASUK_PAGI", "Before Today");

                    kirimdataMasuk(rbValid, rbStatus, "masuk", masuksift);

                }else{
                    kirimdataMasuk(rbValid, rbStatus, "masuk", masuksift);
                }

            }else{

                if (tanggal.equals(rbTanggal)){
                    if (jam_masuk == null){
                        dialogView.viewNotifKosong(IzinSakitSiftFinalActivity.this, "Harap mengisi absensi masuk, untuk izin sakit.", "");
                    }else{
                        String jamSekarangString = SIMPLE_FORMAT_JAM_TAGING.format(new Date());

                        Date batasWaktuJamMalam = null;
                        try {
                            batasWaktuJamMalam = SIMPLE_FORMAT_JAM_TAGING.parse("12:00");
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (tagingTime.getTime() < batasWaktuJamMalam.getTime()){


                            if(jam_masuk == null ){
                                kirimdataPulang(rbValid, rbStatus, "masukpulang", pulangsift);
                            }else{
                                kirimdataPulang(rbValid, rbStatus, "pulang", pulangsift);
                            }
                        }else{
                            dialogView.viewNotifKosong(IzinSakitSiftFinalActivity.this, "Batas melakukan absen telah lewat.", "Batas melakukan absen adalah pukul 12:00.");
                        }
                    }
                }



            }

        }//Masuk Malam
        //Pagi Sore
        else{
            Date pulangPeriksa = null, tagingTimePeriksa = null;
            try {

                pulangPeriksa = SIMPLE_FORMAT_JAM.parse(pulangsift);
                String jamTagingPeriksa = SIMPLE_FORMAT_JAM_TAGING.format(new Date());
                tagingTimePeriksa = SIMPLE_FORMAT_JAM_TAGING.parse(jamTagingPeriksa);

            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (radioSelectedKehadiran.getText().toString().equals("MASUK")){
                if (jam_masuk == null){

                    assert tagingTimePeriksa != null;
                    assert pulangPeriksa != null;
                    if (tagingTimePeriksa.getTime() >= pulangPeriksa.getTime()){
                        showMessage("Peringatan", "Anda tidak dapat melakukan absensi masuk pada jam pulang kerja.");
                    }else{
                        kirimdataMasuk(rbValid,  rbStatus, "masuk", masuksift);
                    }

                }else{

                    showMessage("Peringatan", "Anda sudah mengisi absensi masuk.");

                }
            }
            else {
                if (jam_pulang == null){

                    if(jam_masuk == null ){
                        kirimdataPulang(rbValid, rbStatus, "masukpulang", pulangsift);
                    }else{
                        kirimdataPulang(rbValid, rbStatus, "pulang", pulangsift);
                    }

                }else{
                    showMessage("Peringatan", "Anda sudah mengisi absensi pulang.");
                }
            }
        }
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

    ProgressDialog progressDialog;
    public void kirimdataMasuk(String valid, String status, String ketKehadiran, String jampegawai){
        progressDialog = new ProgressDialog(IzinSakitSiftFinalActivity.this, R.style.AppCompatAlertDialogStyle);
        progressDialog.setMessage("Sedang memproses...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        Log.d("ABSEN_MASUK_PAGI", "Masuk absen kirim data");


        byte[] imageBytes = ambilFoto.compressToMax80KB(file);
        MultipartBody.Part fotoPart =
                prepareFilePart("fototaging", imageBytes);

        MultipartBody.Part lampiranPart;

        if ("pdf".equals(ekslampiran)) {
            lampiranPart =
                    prepareFilePart("lampiran", imageBytesDokumenPdf);
            Log.d("TugasLapanganFinalActivity", "PDF");
        } else {
            byte[] imageBytesLampiran = ambilFoto.compressToMax80KB(filelampiran);
            lampiranPart = prepareFilePart("lampiran", imageBytesLampiran);
            Log.d("TugasLapanganFinalActivity", "JPG");
        }
        Log.d("IZIN_SAKIT_SHIFT", "========== REQUEST IZIN SAKIT SHIFT MASUK ==========");

        Log.d("IZIN_SAKIT_SHIFT", "fotoPart        : " + (fotoPart != null ? "ADA" : "NULL"));
        Log.d("IZIN_SAKIT_SHIFT", "lampiranPart    : " + (lampiranPart != null ? "ADA" : "NULL"));

        Log.d("IZIN_SAKIT_SHIFT", "ketKehadiran    : " + String.valueOf(ketKehadiran));
        Log.d("IZIN_SAKIT_SHIFT", "eJabatan        : " + String.valueOf(eJabatan));
        Log.d("IZIN_SAKIT_SHIFT", "employee_id     : " + String.valueOf(sEmployeID));
        Log.d("IZIN_SAKIT_SHIFT", "timetable_id    : " + String.valueOf(timetableid));
        Log.d("IZIN_SAKIT_SHIFT", "tanggal         : " + String.valueOf(rbTanggal));
        Log.d("IZIN_SAKIT_SHIFT", "jam_masuk       : " + String.valueOf(rbJam));

        Log.d("IZIN_SAKIT_SHIFT", "absensi         : sk");
        Log.d("IZIN_SAKIT_SHIFT", "status_masuk    : " + String.valueOf(status));
        Log.d("IZIN_SAKIT_SHIFT", "lat             : " + String.valueOf(rbLat));
        Log.d("IZIN_SAKIT_SHIFT", "lng             : " + String.valueOf(rbLng));
        Log.d("IZIN_SAKIT_SHIFT", "keterangan      : " + String.valueOf(rbKet));

        Log.d("IZIN_SAKIT_SHIFT", "terlambat(min)  : " + String.valueOf(mins));
        Log.d("IZIN_SAKIT_SHIFT", "opd             : " + String.valueOf(eOPD));
        Log.d("IZIN_SAKIT_SHIFT", "jam_kantor      : " + String.valueOf(jampegawai));
        Log.d("IZIN_SAKIT_SHIFT", "valid_masuk     : " + String.valueOf(valid));

        Log.d("IZIN_SAKIT_SHIFT", "ekslampiran     : " + String.valueOf(ekslampiran));
        Log.d("IZIN_SAKIT_SHIFT", "fakegps         : " + String.valueOf(rbFakeGPS));

        Log.d("IZIN_SAKIT_SHIFT", "idsift          : " + String.valueOf(idsift));
        Log.d("IZIN_SAKIT_SHIFT", "inisialsift     : " + String.valueOf(inisialsift));
        Log.d("IZIN_SAKIT_SHIFT", "tipesift        : " + String.valueOf(tipesift));
        Log.d("IZIN_SAKIT_SHIFT", "masuksift       : " + String.valueOf(masuksift));
        Log.d("IZIN_SAKIT_SHIFT", "pulangsift      : " + String.valueOf(pulangsift));

        Log.d("IZIN_SAKIT_SHIFT", "====================================================");


//        Call<ResponsePOJO> call =
//                RetroClient.getInstance().getApi().uploadizinsakitsiftmasuk(
//                        fotoPart,
//                        textPart(ketKehadiran),
//                        textPart(eJabatan),
//                        textPart(sEmployeID),
//                        textPart(rbTanggal),
//                        textPart(rbJam),
//                        textPart("sk"),
//                        textPart(status),
//                        textPart(rbLat),
//                        textPart(rbLng),
//                        textPart(rbKet),
//                        textPart(String.valueOf(mins)),
//                        textPart(eOPD),
//                        textPart(jampegawai),
//                        textPart(valid),
//                        lampiranPart,
//                        textPart(ekslampiran),
//                        textPart(rbFakeGPS),
//                        textPart(idsift),
//                        textPart(inisialsift),
//                        textPart(tipesift),
//                        textPart(masuksift),
//                        textPart(pulangsift)
//                );

        Call<ResponsePOJO> call =
                RetroClient.getInstance().getApi().uploadizinsakitsiftmasuk(
                        fotoPart,

                        textPart(sEmployeID),      // employee_id
                        textPart(rbTanggal),       // tanggal
                        textPart(idsift),          // idsift
                        textPart(rbJam),           // jam_masuk
                        textPart("sk"),            // posisi_masuk
                        textPart(status),          // status_masuk
                        textPart(rbLat),           // lat_masuk
                        textPart(rbLng),           // lng_masuk
                        textPart(rbKet),           // ket_masuk
                        textPart(valid),            // valid_masuk
                        textPart(String.valueOf(mins)), // batas_waktu
                        lampiranPart,
                        textPart(ekslampiran)
                );

        call.enqueue(new Callback<ResponsePOJO>() {
            @Override
            public void onResponse(@NonNull Call<ResponsePOJO> call, @NonNull Response<ResponsePOJO> response) {
                progressDialog.dismiss();

                if (!response.isSuccessful()) {

                    dialogView.viewNotifKosong(
                            IzinSakitSiftFinalActivity.this,
                            "Gagal mengisi absensi",
                            "Silahkan coba kembali yaaa."
                    );
                    Log.d("Log Izin Sakit", "error: tidak menerima response.");

                    return;
                }

                ResponsePOJO data = response.body();

                if (Objects.requireNonNull(response.body()).isStatus()){
                    Log.d("Log Izin Sakit", "berhasil.");
                    dialogView.viewSukses(IzinSakitSiftFinalActivity.this, data.getRemarks());
                }else {
                    dialogView.viewNotifKosong(IzinSakitSiftFinalActivity.this, data.getRemarks(),"");
                }

            }

            @Override
            public void onFailure(@NonNull Call<ResponsePOJO> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                Log.d("Log Izin Sakit", "error: "+t.getMessage());

                dialogView.viewNotifKosong(IzinSakitSiftFinalActivity.this, "Gagal mengisi absensi,", "silahkan coba kembali.");
            }
        });
    }


    public void kirimdataPulang(String valid, String status, String ketKehadiran, String jampegawai){
        Log.d("Log Izin Sakit", "mulai");

        progressDialog = new ProgressDialog(IzinSakitSiftFinalActivity.this, R.style.AppCompatAlertDialogStyle);
        progressDialog.setMessage("Sedang memproses...");
        progressDialog.setCancelable(false);
        progressDialog.show();


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
                RetroClient.getInstance().getApi().uploadizinsakitsiftpulang(
                        fotoPart,
                        textPart(ketKehadiran),
                        textPart(eJabatan),
                        textPart(sEmployeID),
                        textPart(timetableid),
                        textPart(rbTanggal),
                        textPart(rbJam),
                        textPart("sk"),
                        textPart(status),
                        textPart(rbLat),
                        textPart(rbLng),
                        textPart(rbKet),
                        textPart(String.valueOf(mins)),
                        textPart(eOPD),
                        textPart(jampegawai),
                        textPart(valid),
                        lampiranPart,
                        textPart(ekslampiran),
                        textPart(rbFakeGPS),
                        textPart(idsift),
                        textPart(inisialsift),
                        textPart(tipesift),
                        textPart(masuksift),
                        textPart(pulangsift)
                );

        call.enqueue(new Callback<ResponsePOJO>() {
            @Override
            public void onResponse(@NonNull Call<ResponsePOJO> call, @NonNull Response<ResponsePOJO> response) {
                progressDialog.dismiss();

                if (!response.isSuccessful()) {

                    dialogView.viewNotifKosong(
                            IzinSakitSiftFinalActivity.this,
                            "Gagal mengisi absensi",
                            "Silahkan coba kembali."
                    );
                    return;
                }

                ResponsePOJO data = response.body();

                if (Objects.requireNonNull(response.body()).isStatus()){
                    Log.d("Log Izin Sakit", "berhasil.");
                    dialogView.viewSukses(IzinSakitSiftFinalActivity.this, data.getRemarks());
                }else {
                    dialogView.viewNotifKosong(IzinSakitSiftFinalActivity.this, data.getRemarks(),"");
                }


            }

            @Override
            public void onFailure(@NonNull Call<ResponsePOJO> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                Log.d("Log Izin Sakit", "error: "+t.getMessage());
                dialogView.viewNotifKosong(IzinSakitSiftFinalActivity.this, "Gagal mengisi absensi,", "silahkan coba kembali.");
            }
        });
    }

    public void hitungjarak(){

        if(NetworkUtils.isConnected(this)){
            latitudeSaya = latGMap;
            longitudeSaya = lngGMap;

            rbLat = String.valueOf(latitudeSaya);
            rbLng = String.valueOf(longitudeSaya);
        }else{

            dialogView.viewNotifKosong(IzinSakitSiftFinalActivity.this, "Pastikan anda telah terhubung internet.", "");

        }

    }

    private void datauser(){
        Cursor res = databaseHelper.getAllData22(pegawaiId);
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

        hari = new SimpleDateFormat("EEE");
        tanggal = hari.format(new Date());


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
        Dialog dialogLampiran = new Dialog(IzinSakitSiftFinalActivity.this, R.style.DialogStyle);
        dialogLampiran.setContentView(R.layout.view_add_lampiran);
        LinearLayout llFileManager = dialogLampiran.findViewById(R.id.llFileManager);
        LinearLayout llKamera = dialogLampiran.findViewById(R.id.llKamera);
        LinearLayout llDokumen = dialogLampiran.findViewById(R.id.llDokumen);
        ImageView ivTutupViewLampiran = dialogLampiran.findViewById(R.id.ivTutupViewLampiran);

        llFileManager.setOnClickListener(v -> {
            Intent i = new Intent();
            i.setType("image/*");
            i.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(i, "Pilih Gambar"), 33);
            dialogLampiran.dismiss();
        });

        llDokumen.setOnClickListener(v -> {

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("application/pdf");
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            resultLauncher.launch(intent);
            dialogLampiran.dismiss();
        });

        llKamera.setOnClickListener(v -> {


            Intent intent = new Intent(IzinSakitSiftFinalActivity.this, CameraXLActivity.class);
            intent.putExtra("aktivitas", "lampiranizinsakitshift");
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
    private void ambilFoto(String addFoto){
        String filename = "photo";
        File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        try {
            imageFile = File.createTempFile(filename, ".png", storageDirectory);
            currentPhotoPath = null;
            currentPhotoPath = imageFile.getAbsolutePath();
            Uri imageUri = FileProvider.getUriForFile(IzinSakitSiftFinalActivity.this, "go.pemkott.appsandroidmobiletebingtinggi.fileprovider", imageFile);
            if (addFoto.equals("kegiatan")){
//                hitungjarak();

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, 1);

            }else{

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, 2);

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

                ivFinalKegiatan.setVisibility(View.VISIBLE);
                File file = new File(currentPhotoPath);
                Bitmap bitmap = ambilFoto.fileBitmap(file);
                rotationBitmapTag = Bitmap.createBitmap(bitmap, 0,0, bitmap.getWidth(), bitmap.getHeight(), AmbilFoto.exifInterface(currentPhotoPath,0), true);

                ivFinalKegiatan.setImageBitmap(rotationBitmapTag);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                rotationBitmapTag.compress(Bitmap.CompressFormat.JPEG,50, byteArrayOutputStream);
                byte[] imageInByte = byteArrayOutputStream.toByteArray();

                periksaWaktu();
                handlerProgressDialog();


            }
            else if (requestCode == 2 && resultCode == RESULT_OK){
                llLampiranDinasLuar.setVisibility(View.VISIBLE);
                llLampiranDinasLuarHead.setVisibility(View.VISIBLE);
                ivSuratPerintahFinal.setVisibility(View.VISIBLE);
                llPdfDinasLuar.setVisibility(View.GONE);
                iconLampiran.setVisibility(View.GONE);

                File file = new File(currentPhotoPath);
                Bitmap bitmap = ambilFotoLampiran.fileBitmap(file);

                rotationBitmapSurat = Bitmap.createBitmap(bitmap, 0,0, bitmap.getWidth(), bitmap.getHeight(), AmbilFoto.exifInterface(currentPhotoPath,0), true);


                ivSuratPerintahFinal.setImageBitmap(rotationBitmapSurat);


                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                rotationBitmapSurat.compress(Bitmap.CompressFormat.JPEG,75, byteArrayOutputStream);
                byte[] imageInByte = byteArrayOutputStream.toByteArray();
                ekslampiran = "jpg";

                handlerProgressDialog();

            }
            else if (requestCode == 33 && resultCode == Activity.RESULT_OK && data != null){
                requestPermission();

                llLampiranDinasLuar.setVisibility(View.VISIBLE);
                llLampiranDinasLuarHead.setVisibility(View.VISIBLE);
                ivSuratPerintahFinal.setVisibility(View.VISIBLE);
                llPdfDinasLuar.setVisibility(View.GONE);
                iconLampiran.setVisibility(View.GONE);

                Uri selectedImageUri = data.getData();
                String FilePath2  = getDriveFilePath(selectedImageUri, IzinSakitSiftFinalActivity.this);

                filelampiran = new File(FilePath2);
                byte[] imageBytes = ambilFoto.compressToMax80KB(filelampiran);

                Bitmap previewLampiran = BitmapFactory.decodeByteArray(
                        imageBytes, 0, imageBytes.length
                );
                ivSuratPerintahFinal.setVisibility(View.VISIBLE);
                ivSuratPerintahFinal.setImageBitmap(previewLampiran);

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
        }else {
        }
    }
    Date tagingTime;

    public void periksaWaktu(){

        try {
            jamMasukDate = SIMPLE_FORMAT_JAM.parse(masuksift);
            jamPulangDate = SIMPLE_FORMAT_JAM.parse(pulangsift);

            String jamTaging = SIMPLE_FORMAT_JAM_TAGING.format(new Date());

            SimpleDateFormat df = new SimpleDateFormat("HH:mm", localeID);
            Date d = df.parse(masuksift);
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

//            diff = hours + " jam :" + mins+" menit";

            rbJam = SIMPLE_FORMAT_JAM.format(new Date());

        }catch (Exception e){
            e.printStackTrace();
        }

    }

    byte[] imageBytesDokumenPdf;

    public String getPDFPath(Uri uri){
        String absolutePath = "";
        try{
            InputStream inputStream = IzinSakitSiftFinalActivity.this.getContentResolver().openInputStream(uri);
            byte[] pdfInBytes = new byte[inputStream.available()];
            inputStream.read(pdfInBytes);

            int offset = 0;
            int numRead = 0;
            while (offset < pdfInBytes.length && (numRead = inputStream.read(pdfInBytes, offset, pdfInBytes.length - offset)) >= 0) {
                offset += numRead;
            }

            String mPath = "";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1) {
                mPath= IzinSakitSiftFinalActivity.this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)+"absensi-"+sEmployeID+"-"+currentDateandTimes + ".pdf";
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
            byte[] imageBytes = new byte[(int)filelampiran.length()];
            imageBytesDokumenPdf = new byte[(int)filelampiran.length()];
            finput.read(imageBytesDokumenPdf, 0, imageBytesDokumenPdf.length);
            finput.close();
            ekslampiran = "pdf";

        }catch (Exception ae){
            ae.printStackTrace();
        }
        return absolutePath;
    }

    ByteArrayOutputStream byteArrayTag, byteArraySurat;


    //  Maps
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
            subscribeToLocationUpdate();
        } else {
            subscribeToLocationUpdate();
        }
    }


    public void backAbsenMasuk(View view){
        stopLocationUpdates();
        finish();
    }

    private void setupViews() {

        //Maps
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

    }

    private void stopLocationUpdates() {
        locationViewModel.getLocationHelper(mContext).stopLocationUpdates();
    }



    private void subscribeToLocationUpdate() {
        locationViewModel.getLocationHelper(mContext).observe(this, new Observer<Location>() {

            @Override
            public void onChanged(@Nullable Location location) {
//                Toast.makeText(mContext, "on changed called", Toast.LENGTH_SHORT).show();
                locationObj = location;

                boolean isMock = mockLocationsEnabled || location.isFromMockProvider();
                if (isMock){
                    mock_location = 1;
                    rbFakeGPS ="1";
                }else{
                    mock_location = 0;
                }


                plotMarkers(locationObj);
            }
        });
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
            map.addMarker(new MarkerOptions().position(new LatLng(locationObj.getLatitude(), locationObj.getLongitude())).icon(bitmapDescriptorFromVector(this, R.drawable.asn_lk)).title(lokasi.getAddress(IzinSakitSiftFinalActivity.this, locationObj.getLatitude(), locationObj.getLongitude())));
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

                    subscribeToLocationUpdate();
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
                defaultLocation, 10));
        this.map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        this.map.getUiSettings().setMyLocationButtonEnabled( true );
        this.map.getUiSettings().setCompassEnabled( true );
        this.map.getUiSettings().setRotateGesturesEnabled(true);

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
            int REQUEST_CODE_ASK_PERMISSIONS = 123;
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
        }
    }

    public void backFinalDinasLuar(View view){
        onBackPressed();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopLocationUpdates();
        kegiatans.clear();
        finish();
    }



    public void handlerProgressDialog(){
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            //your code here
        }, 1500);
    }

    public void handlerProgressDialog2(){
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            //your code here
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

    public void viewSukses(Context context, String info1, String info2){
        Dialog dialogSukes = new Dialog(context, R.style.DialogStyle);
        dialogSukes.setContentView(R.layout.view_sukses);
        dialogSukes.setCancelable(false);
        ImageView tvTutupDialog = dialogSukes.findViewById(R.id.tvTutupDialog);

        tvTutupDialog.setOnClickListener(v -> {
            stopLocationUpdates();

            dialogSukes.dismiss();
            JadwalIzinSiftActivity.jadwalIzinSiftActivity.finish();
            SakitSiftActivity.sakit.finish();
            finish();
        });

        handlerProgressDialog2();
        dialogSukes.show();

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