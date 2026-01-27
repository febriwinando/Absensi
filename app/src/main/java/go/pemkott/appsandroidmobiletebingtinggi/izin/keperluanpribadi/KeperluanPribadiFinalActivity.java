package go.pemkott.appsandroidmobiletebingtinggi.izin.keperluanpribadi;

import static go.pemkott.appsandroidmobiletebingtinggi.geolocation.model.LocationHelper.defaultLocation;
import static go.pemkott.appsandroidmobiletebingtinggi.konstanta.TimeFormat.SIMPLE_DATE_FORMAT_TAGING;
import static go.pemkott.appsandroidmobiletebingtinggi.konstanta.TimeFormat.SIMPLE_DATE_FORMAT_TAGING_PHOTO_REPORT;
import static go.pemkott.appsandroidmobiletebingtinggi.konstanta.TimeFormat.SIMPLE_FORMAT_JAM;
import static go.pemkott.appsandroidmobiletebingtinggi.konstanta.TimeFormat.SIMPLE_FORMAT_JAM_TAGING;
import static go.pemkott.appsandroidmobiletebingtinggi.konstanta.TimeFormat.SIMPLE_FORMAT_TANGGAL;
import static go.pemkott.appsandroidmobiletebingtinggi.konstanta.TimeFormat.hari;
import static go.pemkott.appsandroidmobiletebingtinggi.konstanta.TimeFormat.localeID;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
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
import android.util.Log;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
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
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import go.pemkott.appsandroidmobiletebingtinggi.NewDashboard.DashboardVersiOne;
import go.pemkott.appsandroidmobiletebingtinggi.R;
import go.pemkott.appsandroidmobiletebingtinggi.api.ResponsePOJO;
import go.pemkott.appsandroidmobiletebingtinggi.api.RetroClient;
import go.pemkott.appsandroidmobiletebingtinggi.database.DatabaseHelper;
import go.pemkott.appsandroidmobiletebingtinggi.dialogview.DialogView;
import go.pemkott.appsandroidmobiletebingtinggi.dinasluarkantor.perjalanandinas.PerjalananDinasFinalActivity;
import go.pemkott.appsandroidmobiletebingtinggi.geolocation.GetLocation;
import go.pemkott.appsandroidmobiletebingtinggi.konstanta.AmbilFoto;
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

public class KeperluanPribadiFinalActivity extends AppCompatActivity implements OnMapReadyCallback {

    //Gmaps
    private static final int REQUEST_CHECK_SETTINGS = 100;
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
    private static final String TAG = PerjalananDinasFinalActivity.class.getSimpleName();
    private String  currentPhotoPath;
    private String rbLat;
    private String rbLng;
    private String rbTanggal;
    private String rbJam;
    private String rbKet;
    private String rbFakeGPS ="0" ;
    String currentDateandTime = SIMPLE_DATE_FORMAT_TAGING.format(new Date());
    String currentDateandTimes = SIMPLE_DATE_FORMAT_TAGING_PHOTO_REPORT.format(new Date());
    String eOPD, eKelompok, eJabatan, latOffice, lngOffice;
    String sEmployeID, sToken, sAkses, sActive, sUsername, timetableid;
    String kegiatanlainnya, jamMasuk, jamPulang, hariIni;
    String tanggal;
    StringBuilder keterangan;
    String jam_masuk, jam_pulang, batasWaktu;
    SimpleDateFormat hari;



    TextView tvKegiatanFinal, titleDinasLuar, title_content;
    ArrayList<String> kegiatans = new ArrayList<>();
    AmbilFoto ambilFoto = new AmbilFoto(KeperluanPribadiFinalActivity.this);

    Bitmap rotationBitmapTag;

    ShapeableImageView ivFinalKegiatan;
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
    FragmentContainerView fragmentContainerView;
    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;

    SessionManager session;
    int pegawaiId;
    File file,filelampiran;
    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.background_color));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.background_color));
        setContentView(R.layout.activity_keperluan_pribadi_final);

        session = new SessionManager(this);
        pegawaiId = session.getPegawaiId();


        databaseHelper = new DatabaseHelper(this);
        tvKegiatanFinal = findViewById(R.id.tvKegiatanFinal);
        titleDinasLuar = findViewById(R.id.titleDinasLuar);
        rgKehadiran = findViewById(R.id.rgKehadiran);
        title_content = findViewById(R.id.title_contentkp);
        title_content.setText("KEPERLUAN PRIBADI");

        fragmentContainerView = findViewById(R.id.map);
        setRoundedBackground(fragmentContainerView);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(3000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


//        Image View
        ivFinalKegiatan = findViewById(R.id.ivFinalKegiatanKp);

        //Google Maps
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.putih));

        if (savedInstanceState != null) {
            locationObj = savedInstanceState.getParcelable(KEY_LOCATION);
        }

        setupViews();
        setupViewModel();
        checkLocationPermission();
        //Google Maps

        datauser();

        Intent intent = getIntent();
        jam_masuk = DashboardVersiOne.jam_masuk;
        jam_pulang = DashboardVersiOne.jam_pulang;
        titleDinasLuar.setText(intent.getStringExtra("title"));


        kegiatans.clear();
        kegiatans = KeperluanPribadiActivity.kegiatanChecked;
        kegiatanlainnya = KeperluanPribadiActivity.kegiatansLainnya;


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

//        String myDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()+ "/eabsensi";
//        String fileName = intent.getStringExtra("fileName");
//        file = new File(myDir, fileName);
//        Bitmap gambardeteksi = BitmapFactory.decodeFile(file.getAbsolutePath());
//        ivFinalKegiatan.setImageBitmap(gambardeteksi);
//        Bitmap selectedBitmap = ambilFoto.compressBitmapTo80KB(file);
//
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        selectedBitmap.compress(Bitmap.CompressFormat.JPEG,90, byteArrayOutputStream);
//        byte[] imageInByte = byteArrayOutputStream.toByteArray();
//        fotoTaging =  Base64.encodeToString(imageInByte,Base64.DEFAULT);

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R){
            requestPermission();
        }
        dataKegiatan();

        selected = rgKehadiran.getCheckedRadioButtonId();
        radioSelectedKehadiran = findViewById(selected);

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
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R){
            requestPermission();
        }
        if (mock_location == 1){
            dialogView.viewNotifKosong(KeperluanPribadiFinalActivity.this, "Anda terdeteksi menggunakan Fake GPS.", "Jika ditemukan berulang kali, akun anda akan terblokir otomatis dan tercatat Alpa.");

        }else {
            if (file == null || !file.exists() || file.length() == 0) {
                dialogView.viewNotifKosong(KeperluanPribadiFinalActivity.this, "Anda harus melampirkan Foto Kegiatan.", "");
            } else {
                uploadImages();
            }
        }
    }


    public void uploadImages(){

        if (jamMasuk == null || jamPulang == null ){
            dialogView.viewNotifKosong(KeperluanPribadiFinalActivity.this, "Anda tidak memiliki Jadwal Kerja untuk hari ini", "");
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
            String rbStatus = "izin";
            String rbPosisi = "kp";

            Date pulangPeriksa = null, tagingTimePeriksa = null;
            try {

                pulangPeriksa = SIMPLE_FORMAT_JAM.parse(jamPulang);
                String jamTagingPeriksa = SIMPLE_FORMAT_JAM_TAGING.format(new Date());
                tagingTimePeriksa = SIMPLE_FORMAT_JAM_TAGING.parse(jamTagingPeriksa);

            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (tagingTimePeriksa.getTime() < dateBatasWaktu.getTime()){
                dialogView.viewNotifKosong(KeperluanPribadiFinalActivity.this, "Anda hanya dapat mengisi absen masuk, "+batasWaktu+" menit sebelum Jam Masuk", "");
            }else{

                if (radioSelectedKehadiran.getText().toString().equals("MASUK")){
                    if (tagingTimePeriksa.getTime() >= pulangPeriksa.getTime()){
                        showMessage("Peringatan", "Anda tidak dapat melakukan absensi masuk pada jam pulang kerja.");
                    }else{
                        kirimdataMasuk(rbValid, rbPosisi, rbStatus, "masuk", jamMasuk);
                    }
                }
                else {
                    rbPosisi = "kp";
                    kirimdataPulang(rbValid, rbPosisi, rbStatus, "pulang", jamPulang);
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

    public void kirimdataMasuk(String valid, String posisi, String status, String ketKehadiran, String jampegawai){

        Dialog dialogproses = new Dialog(KeperluanPribadiFinalActivity.this, R.style.DialogStyle);
        dialogproses.setContentView(R.layout.view_proses);
        dialogproses.setCancelable(false);

        byte[] imageBytes = ambilFoto.compressToMax80KB(file);
        MultipartBody.Part fotoPart = prepareFilePart("fototaging", imageBytes);

        Call<ResponsePOJO> call =
                RetroClient.getInstance().getApi().uploadIzinKpMasuk(
                        fotoPart,
                        textPart(ketKehadiran),
                        textPart(eJabatan),
                        textPart(sEmployeID),
                        textPart(timetableid),
                        textPart(rbTanggal),
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
                        textPart(rbFakeGPS),
                        textPart(batasWaktu)
                );

//        Call<ResponsePOJO> call = RetroClient.getInstance().getApi().uploadIzinKpMasuk(
//                fotoTaging,
//                ketKehadiran,
//                eJabatan,
//                sEmployeID,
//                timetableid,
//                rbTanggal,
//                rbJam,
//                posisi,
//                status,
//                rbLat,
//                rbLng,
//                rbKet,
//                mins,
//                eOPD,
//                jampegawai,
//                valid,
//                rbFakeGPS,
//                batasWaktu
//        );

        call.enqueue(new Callback<ResponsePOJO>() {
            @Override
            public void onResponse(@NonNull Call<ResponsePOJO> call, @NonNull Response<ResponsePOJO> response) {
                dialogproses.dismiss();

                if (!response.isSuccessful()) {

                    dialogView.viewNotifKosong(
                            KeperluanPribadiFinalActivity.this,
                            "Gagal mengisi absensi",
                            "Silahkan coba kembali."
                    );
                    return;
                }

                ResponsePOJO data = response.body();

                if (Objects.requireNonNull(response.body()).isStatus()){
                    dialogView.viewSukses(KeperluanPribadiFinalActivity.this, data.getRemarks());
                }else {
                    dialogView.viewNotifKosong(KeperluanPribadiFinalActivity.this, data.getRemarks(),"");
                }

            }

            @Override
            public void onFailure(@NonNull Call<ResponsePOJO> call, @NonNull Throwable t) {
                dialogproses.dismiss();
                dialogView.pesanError(KeperluanPribadiFinalActivity.this);
            }
        });

        dialogproses.show();

    }

    public void kirimdataPulang(String valid, String posisi, String status, String ketKehadiran, String jampegawai){

        Dialog dialogproses = new Dialog(KeperluanPribadiFinalActivity.this, R.style.DialogStyle);
        dialogproses.setContentView(R.layout.view_proses);
        dialogproses.setCancelable(false);

        byte[] imageBytes = ambilFoto.compressToMax80KB(file);
        MultipartBody.Part fotoPart = prepareFilePart("fototaging", imageBytes);

        Call<ResponsePOJO> call =
                RetroClient.getInstance().getApi().uploadIzinKpPulang(
                        fotoPart,
                        textPart(ketKehadiran),
                        textPart(eJabatan),
                        textPart(sEmployeID),
                        textPart(timetableid),
                        textPart(rbTanggal),
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
                        textPart(rbFakeGPS),
                        textPart(batasWaktu)
                );
//        Call<ResponsePOJO> call = RetroClient.getInstance().getApi().uploadIzinKpPulang(
//                fotoTaging,
//                ketKehadiran,
//                eJabatan,
//                sEmployeID,
//                timetableid,
//                rbTanggal,
//                rbJam,
//                posisi,
//                status,
//                rbLat,
//                rbLng,
//                rbKet,
//                mins,
//                eOPD,
//                jampegawai,
//                valid,
//                rbFakeGPS,
//                batasWaktu
//        );

        call.enqueue(new Callback<ResponsePOJO>() {
            @Override
            public void onResponse(@NonNull Call<ResponsePOJO> call, @NonNull Response<ResponsePOJO> response) {
                dialogproses.dismiss();

                if (!response.isSuccessful()) {

                    dialogView.viewNotifKosong(
                            KeperluanPribadiFinalActivity.this,
                            "Gagal mengisi absensi",
                            "Silahkan coba kembali."
                    );
                    return;
                }

                ResponsePOJO data = response.body();

                if (Objects.requireNonNull(response.body()).isStatus()){
                    dialogView.viewSukses(KeperluanPribadiFinalActivity.this, data.getRemarks());
                }else {
                    dialogView.viewNotifKosong(KeperluanPribadiFinalActivity.this, data.getRemarks(),"");
                }

            }

            @Override
            public void onFailure(@NonNull Call<ResponsePOJO> call, @NonNull Throwable t) {
                dialogproses.dismiss();
                dialogView.pesanError(KeperluanPribadiFinalActivity.this);
            }
        });

        dialogproses.show();

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
            dialogView.viewNotifKosong(KeperluanPribadiFinalActivity.this, "Pastikan anda telah terhubung internet.", "");
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


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_CANCELED){

            if (requestCode == 1 && resultCode == RESULT_OK) {

                ivFinalKegiatan.setVisibility(View.VISIBLE);
                File file = new File(currentPhotoPath);
                Bitmap bitmap = ambilFoto.compressBitmapTo80KB(file);
                rotationBitmapTag = Bitmap.createBitmap(bitmap, 0,0, bitmap.getWidth(), bitmap.getHeight(), AmbilFoto.exifInterface(currentPhotoPath, 0), true);

                ivFinalKegiatan.setImageBitmap(rotationBitmapTag);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                rotationBitmapTag.compress(Bitmap.CompressFormat.JPEG,90, byteArrayOutputStream);
                byte[] imageInByte = byteArrayOutputStream.toByteArray();

                periksaWaktu();
                handlerProgressDialog();


            }
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

//            diff = hours + " jam :" + mins+" menit";

            rbJam = SIMPLE_FORMAT_JAM.format(new Date());

        }catch (Exception e){
            e.printStackTrace();
        }

    }


    ByteArrayOutputStream byteArrayTag, byteArraySurat;
    public Uri getImageUri(Bitmap inImage, int i) {

        if (i == 1){
            byteArrayTag = new ByteArrayOutputStream();
            inImage.compress(Bitmap.CompressFormat.PNG, 100, byteArrayTag);
            String path = MediaStore.Images.Media.insertImage(KeperluanPribadiFinalActivity.this.getContentResolver(), inImage, "absensi-"+sEmployeID+"-"+currentDateandTime, null);
            return Uri.parse(path);

        }else {
            byteArraySurat = new ByteArrayOutputStream();
            inImage.compress(Bitmap.CompressFormat.PNG, 100, byteArraySurat);
            String paths = MediaStore.Images.Media.insertImage(KeperluanPribadiFinalActivity.this.getContentResolver(), inImage, "absensi-"+sEmployeID+"-"+currentDateandTimes, null);
            return Uri.parse(paths);
        }

    }

    //    Maps
    private void setupViewModel() {
        locationViewModel = ViewModelProviders.of(this).get(LocationViewModel.class);
    }

    public void checkLocationPermission() {
        int hasWriteStoragePermission;
        hasWriteStoragePermission = getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CHECK_SETTINGS);
        }
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
            map.addMarker(new MarkerOptions().position(new LatLng(locationObj.getLatitude(), locationObj.getLongitude())).icon(bitmapDescriptorFromVector(this, R.drawable.asn_lk)).title(lokasi.getAddress(KeperluanPribadiFinalActivity.this, locationObj.getLatitude(), locationObj.getLongitude())));
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(locationObj.getLatitude(), locationObj.getLongitude()), 19f));
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
        this.map.animateCamera(CameraUpdateFactory.newLatLngZoom(
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
                        (FrameLayout) findViewById(R.id.map), false);

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

    protected void onResume() {
        super.onResume();
        rbTanggal = SIMPLE_FORMAT_TANGGAL.format(new Date());
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
            KeperluanPribadiActivity.kp.finish();
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
            KeperluanPribadiActivity.kp.finish();
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