package go.pemkott.appsandroidmobiletebingtinggi.kehadiran;


import static android.content.ContentValues.TAG;
import static go.pemkott.appsandroidmobiletebingtinggi.geolocation.model.LocationHelper.defaultLocation;
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
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentContainerView;

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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import go.pemkott.appsandroidmobiletebingtinggi.R;
import go.pemkott.appsandroidmobiletebingtinggi.api.ResponsePOJO;
import go.pemkott.appsandroidmobiletebingtinggi.api.RetroClient;
import go.pemkott.appsandroidmobiletebingtinggi.database.DatabaseHelper;
import go.pemkott.appsandroidmobiletebingtinggi.dialogview.DialogView;
import go.pemkott.appsandroidmobiletebingtinggi.konstanta.AmbilFoto;
import go.pemkott.appsandroidmobiletebingtinggi.konstanta.Lokasi;
import go.pemkott.appsandroidmobiletebingtinggi.utils.NetworkUtils;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AbsensiKehadiranActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int REQUEST_CHECK_SETTINGS = 100;
    private Context mContext;
    private GoogleMap map;

    double latGMap = 0, lngGMap = 0;
    Lokasi lokasi = new Lokasi();
    DialogView dialogView = new DialogView(AbsensiKehadiranActivity.this);
    private String sEmployId;
    private String rbTanggal;
    private String rbJam;
    private String rbLat;
    private String rbLng;
    private String rbKet;
    private String batasWaktu;
    private String rbFakeGPS ="0";
    DatabaseHelper databaseHelper;
    ShapeableImageView ivTaging;
    LinearLayout llUpload;
    Date dateBatasWaktu;
    String jamTaging, jamMasuk, jamPulang, hariIni, eKelompok, eJabatan, timetableid;
    String tanggal;
    String diff, latOffice, lngOffice,eOPD;

    Calendar cal = Calendar.getInstance();

    RadioGroup rgKehadiran;
    RadioButton radioSelectedKehadiran;
    int selected;
    SimpleDateFormat hari;
    static ArrayList<String> latList = new ArrayList<>();
    static ArrayList<String> lngList = new ArrayList<>();

    static ArrayList<String> latListExc = new ArrayList<>();
    static ArrayList<String> lngListExc = new ArrayList<>();
    double totalJarak;
    Date jamMasukDate, jamPulangDate, tagingTime;
    int mins;
    int minspulang;

    FusedLocationProviderClient fusedLocationProviderClient;
    LocationRequest locationRequest;

    FragmentContainerView fragmentContainerView;

    private final boolean mockLocationsEnabled = false;
    int mock_location = 0;
    File file;

//    boolean statuskehadiran;

    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        EdgeToEdge.enable(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.biru));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kehadiran_one);
        databaseHelper = new DatabaseHelper(this);
        databases();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        locationRequest = LocationRequest.create();

        locationRequest.setInterval(3000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        //Google Maps
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.putih));


        if (savedInstanceState != null) {
        }

        // ambilFoto();
        mContext = this;
        setupViews();
        setupViewModel();


        rbTanggal = SIMPLE_FORMAT_TANGGAL.format(new Date());

        ivTaging = findViewById(R.id.ivTagingAbsen);
        llUpload = findViewById(R.id.llUploadkehadiran);
        rgKehadiran = findViewById(R.id.rgKehadiran);
        TextView title_content = findViewById(R.id.title_content);
        fragmentContainerView = findViewById(R.id.mapKehadiranOne);
        setRoundedBackground(fragmentContainerView);


        String myDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()+ "/eabsensi";
        Intent intent = getIntent();
        String fileName = intent.getStringExtra("namafile");

        file = new File(myDir, fileName);
//        Bitmap selectedBitmap = ambilFoto.compressBitmapTo80KB(file);
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        byte[] imageBytes = ambilFoto.compressToMax80KB(file);

        Bitmap preview = BitmapFactory.decodeByteArray(
                imageBytes, 0, imageBytes.length
        );

        ivTaging.setImageBitmap(preview);

// upload pakai imageBytes

//        ivTaging.setImageBitmap(selectedBitmap);

        title_content.setText("KEHADIRAN");

        llUpload.setOnClickListener(view -> {

            if (mock_location == 1){
                dialogView.viewNotifKosong(AbsensiKehadiranActivity.this, "Anda terdeteksi menggunakan Fake GPS.", "Jika ditemukan berulang kali, akun anda akan terblokir otomatis dan tercatat Alpa.");
            }else {
                uploadImages();
            }


        });

        startLocationUpdates();
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

    private void setRoundedBackground(FragmentContainerView view) {
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
                return;
            }else{
                mock_location = 0;
            }

            if (map != null) {
                plotMarkers(locationResult.getLastLocation());
            }
        }
    };


    public void databases(){

        Cursor tUser = databaseHelper.getAllData22();
        while (tUser.moveToNext()){
            sEmployId = tUser.getString(1);
        }

        hari = new SimpleDateFormat("EEE", localeID);
        tanggal = hari.format(new Date());

        Cursor tTimeTable = databaseHelper.getKegiatanTimeTable(sEmployId, String.valueOf(hari(tanggal)));
        if(tTimeTable.getCount() == 0){
            jamMasuk = null;
            jamPulang = null;
            return;
        }

        while (tTimeTable.moveToNext()){
            timetableid = tTimeTable.getString(2);
            hariIni = tTimeTable.getString(3);
            jamMasuk = tTimeTable.getString(5);
            jamPulang = tTimeTable.getString(6);
        }

        Cursor employe = databaseHelper.getDataEmployee(sEmployId);
        while (employe.moveToNext()){
            eOPD = employe.getString(4);
            eKelompok = employe.getString(9);
            eJabatan = employe.getString(11);
            latOffice = employe.getString(15);
            lngOffice = employe.getString(16);
            batasWaktu = employe.getString(18);

        }

        latList.clear();
        lngList.clear();

        Cursor koordinat = databaseHelper.getDataKoordinat(eOPD);
        if(koordinat.getCount() == 0){
            return;
        }

        while (koordinat.moveToNext()){
            latList.add(koordinat.getString(3));
            lngList.add(koordinat.getString(4));
        }

        latListExc.clear();
        lngListExc.clear();

        Cursor koordinatExc = databaseHelper.getDataKoordinatEmp(sEmployId);
        if(koordinatExc.getCount() == 0){
            return;
        }

        while (koordinatExc.moveToNext()){
            latListExc.add(koordinatExc.getString(3));
            lngListExc.add(koordinatExc.getString(4));
        }

        defaultLocation = new LatLng(Double.parseDouble( latList.get(0)), Double.parseDouble(lngList.get(0)));
    }


    public void hitungjarak(){

        double latitudeSaya;
        double longitudeSaya;
        if (NetworkUtils.isConnectedMobile(AbsensiKehadiranActivity.this) || NetworkUtils.isConnectedWifi(AbsensiKehadiranActivity.this)){
            if (NetworkUtils.isConnectedFast(AbsensiKehadiranActivity.this)){
                latitudeSaya = latGMap;
                longitudeSaya = lngGMap;

                rbLat = String.valueOf(latGMap);
                rbLng = String.valueOf(lngGMap);


                SimpleDateFormat today = new SimpleDateFormat("EEE", localeID);
                String hariini = today.format(new Date());

                String lat, lng;

//            Menghitung Jarak CallTaker
                if (eKelompok.equals("ct")){

                    int totalLokasi = 0;
                    double jarak = 0;

                    for (int i = 0 ; i  < latList.size(); i++){

                        lat = latList.get(i);
                        lng = lngList.get(i);
                        double latitudeTujuan = Double.parseDouble(lat);
                        double longitudeTujuan = Double.parseDouble(lng);
                        jarak = getDistance(latitudeTujuan, longitudeTujuan, latitudeSaya, longitudeSaya);

                        if ((int)jarak <= 150){
                            break;
                        }else{

                            totalLokasi = totalLokasi + 1;
                        }
                    }

                    if (totalLokasi ==  latList.size()){

                        double jarakExc = 0;
                        int totalJarakExc = 0;

                        if (latListExc.size() <=0 ){
                            jarakExc = 151;
                        }else{

                            for (int j = 0; j< latListExc.size(); j++){

                                jarakExc = getDistance(Double.parseDouble(latListExc.get(j)), Double.parseDouble(lngListExc.get(j)), latitudeSaya, longitudeSaya);
                                if ((int) jarakExc <= 200) {
                                    break;
                                } else {
                                    totalJarakExc = totalJarakExc + 1;
                                }

                            }

                        }

                        if (totalJarakExc == latListExc.size()){
                            totalJarak = 151;
                        }else{
                            totalJarak = jarakExc;
                        }
                    }else{
                        totalJarak = jarak;
                    }

                }

//            Menghitung Jarak Pegawai
                else{
//                Menghitung Jarak Pada Hari Senin
                    if (hariini.equals("Sen") ||  hariini.equals("Mon")){
                        int totalLokasi = 0;
                        double jarak = 0;

                        for (int i = 0 ; i  < latList.size(); i++){
                            lat = latList.get(i);
                            lng = lngList.get(i);
                            double latitudeTujuan = Double.parseDouble(lat);
                            double longitudeTujuan = Double.parseDouble(lng);
                            jarak = getDistance(latitudeTujuan, longitudeTujuan, latitudeSaya, longitudeSaya);

                            if (jarak <= 200){
                                break;
                            }else{
                                totalLokasi = totalLokasi + 1;
                            }

                        }

                        if (totalLokasi ==  latList.size()){

                            double jarakExc = 0;
                            int totalJarakExc = 0;

                            if (latListExc.size() <=0){
                                jarakExc = 151;
                            }else{


                                for (int j = 0; j< latListExc.size(); j++){

                                    jarakExc = getDistance(Double.parseDouble(latListExc.get(j)), Double.parseDouble(lngListExc.get(j)), latitudeSaya, longitudeSaya);
                                    if ((int) jarakExc <= 200) {
                                        break;
                                    } else {
                                        totalJarakExc = totalJarakExc + 1;
                                    }

                                }
                            }


                            if (totalJarakExc == latListExc.size()){
                                totalJarak = 151;
                            }else{
                                totalJarak = jarakExc;
                            }
                        }else{
                            totalJarak = jarak;
                        }

                    }

//                Menghitung Jarak Pada Hari Lainnya
                    else{

                        double latitudeTujuan = Double.parseDouble(latList.get(0));
                        double longitudeTujuan = Double.parseDouble(lngList.get(0));

                        double jarak = getDistance(latitudeTujuan, longitudeTujuan, latitudeSaya, longitudeSaya);
                        if (jarak <= 150){

                            totalJarak = jarak;

                        }
                        else{

                            double jarakExc = 0;
                            int totalJarakExc = 0;

                            if (latListExc.size() <= 0 ) {
                                jarakExc = 151;
                            }else{

                                for (int j = 0; j< latListExc.size(); j++){

                                    jarakExc = getDistance(Double.parseDouble(latListExc.get(j)), Double.parseDouble(lngListExc.get(j)), latitudeSaya, longitudeSaya);
                                    if ((int) jarakExc <= 150) {
                                        break;
                                    } else {
                                        totalJarakExc = totalJarakExc + 1;
                                    }

                                }
                            }

                            if (totalJarakExc == latListExc.size()){
                                totalJarak = 151;
                            }else{
                                totalJarak = jarakExc;
                            }

                        }
                    }

                }
            }else{
                dialogView.viewNotifKosong(AbsensiKehadiranActivity.this, "Pastikan anda telah terhubung ke internet.", "");

            }
        }else{
            dialogView.viewNotifKosong(AbsensiKehadiranActivity.this, "Pastikan anda telah terhubung ke internet.", "");

        }

    }

    private Double getDistance(Double latitudeTujuan, Double longitudeTujuan, Double latitudeUser, Double longitudeUser) {
        /* VARIABLE */
        double pi = 3.14159265358979;

        Double R = 6371e3;

        double latRad1 = latitudeTujuan * (pi / 180);
        double latRad2 = latitudeUser * (pi / 180);
        double deltaLatRad = (latitudeUser - latitudeTujuan) * (pi / 180);
        double deltaLonRad = (longitudeUser - longitudeTujuan) * (pi / 180);

        /* RUMUS HAVERSINE */
        double a = Math.sin(deltaLatRad / 2) * Math.sin(deltaLatRad / 2) + Math.cos(latRad1) * Math.cos(latRad2) * Math.sin(deltaLonRad / 2) * Math.sin(deltaLonRad / 2);
        Double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    AmbilFoto ambilFoto = new AmbilFoto(AbsensiKehadiranActivity.this);
    public void uploadImages(){



        if (file == null || !file.exists() || file.length() == 0) {
            dialogView.viewNotifKosong(
                    this,
                    "Foto belum diambil",
                    "Silakan ambil foto terlebih dahulu"
            );
            return;
        } else{
            if (jamMasuk == null || jamPulang == null){
                dialogView.viewNotifKosong(AbsensiKehadiranActivity.this, "Anda tidak memiliki Jadwal Kerja untuk hari ini", "");
            }
            else{

                periksaWaktu();
                hitungjarak();

                if (tagingTime.getTime() <= dateBatasWaktu.getTime()) {
                    dialogView.viewNotifKosong(AbsensiKehadiranActivity.this, "Anda hanya dapat mengisi absen masuk, "+batasWaktu+" menit sebelum Jam Masuk", "");
                }
                else{


                    selected = rgKehadiran.getCheckedRadioButtonId();
                    radioSelectedKehadiran = findViewById(selected);

                    if (eJabatan.equals("2")){
                        totalJarak = 1;
                    }

                    if (totalJarak > 150){
                        dialogView.viewNotifKosong(AbsensiKehadiranActivity.this, "Andah harus berada dilingkungan kantor untuk melakukan absensi.", "");
                    }
                    else{

                        String ketKehadiran;
                        String rbPosisi;
                        String rbStatus;
                        String rbValid;
                        if (radioSelectedKehadiran.getText().toString().equals("Masuk")){

                                if (tagingTime.getTime() >= jamPulangDate.getTime()){
                                    dialogView.viewNotifKosong(AbsensiKehadiranActivity.this, "Anda tidak dapat melakukan absensi masuk pada jam pulang kerja.", "");
                                }

                                else{
                                    ketKehadiran = "masuk";
                                    rbPosisi = "masuk";
                                    rbStatus = "hadir";

                                    rbValid = "2";
                                    String eselon = "0";

                                    if (eJabatan.equals("2")){
                                        rbJam = "07:30";
                                        rbKet = "sesuai waktu";
                                        rbPosisi = "masuk";
                                        rbStatus = "hadir";
                                        eselon = "2";
                                    }

                                    kirimDataMasuk(ketKehadiran, eselon, sEmployId, timetableid, rbTanggal, rbJam, rbPosisi, rbStatus, rbLat, rbLng, rbKet, mins, jamMasuk, rbValid, "100");

                                }

                        }
                        else{

                            rbPosisi = "pulang";
                            rbStatus = "hadir";

                            if (tagingTime.getTime() > jamPulangDate.getTime()) {
                                rbKet = "sesuai waktu";
                                mins = 0;
                            }else{
                                rbKet = "kecepatan";
                            }


                                    String eselon = "0";
                                    if (eJabatan.equals("2")){
                                        eselon = "2";
                                    }

                                    if (eJabatan.equals("2")){
                                        rbJam = "07:30";
                                        rbKet = "sesuai waktu";
                                        rbPosisi = "pulang";
                                        rbStatus = "hadir";
                                        eselon = "2";
                                    }
                                    rbValid = "2";
                                    viewBerakhlak("pulang", eselon, sEmployId, timetableid, rbTanggal, rbJam, rbPosisi, rbStatus, rbLat, rbLng, rbKet, 0,  jamPulang, rbValid);
                        }
                    }
                }
            }
        }
    }

    public void kirimDataMasuk(String absensi, String eselon, String idpegawai, String timetableid, String tanggal, String jam, String posisi, String status, String lat, String lng, String ket, int terlambat, String jampegawai, String validasi, String berakhlak){

        Dialog dialogproses = new Dialog(AbsensiKehadiranActivity.this, R.style.DialogStyle);
        dialogproses.setContentView(R.layout.view_proses);
        dialogproses.setCancelable(false);

        byte[] imageBytes = ambilFoto.compressToMax80KB(file);
        MultipartBody.Part fotoPart = prepareFilePart("fototaging", imageBytes);

        Call<ResponsePOJO> call =
                RetroClient.getInstance().getApi().uploadAbsenKehadiranMasuk(
                        fotoPart,

                        textPart(absensi),
                        textPart(eselon),
                        textPart(idpegawai),
                        textPart(timetableid),
                        textPart(tanggal),
                        textPart(jam),
                        textPart(posisi),
                        textPart(status),
                        textPart(lat),
                        textPart(lng),
                        textPart(ket),
                        textPart(String.valueOf(terlambat)),
                        textPart(eOPD),
                        textPart(jampegawai),
                        textPart(validasi),
                        textPart(rbFakeGPS),
                        textPart(batasWaktu),
                        textPart(berakhlak)
                );

        call.enqueue(new Callback<ResponsePOJO>() {
            @Override
            public void onResponse(@NonNull Call<ResponsePOJO> call, @NonNull Response<ResponsePOJO> response) {
                dialogproses.dismiss();

                if (!response.isSuccessful()) {

                    dialogView.viewNotifKosong(
                            AbsensiKehadiranActivity.this,
                            "Gagal mengisi absensi",
                            "Silahkan coba kembali ya gagal terima."
                    );
                    return;
                }

                ResponsePOJO data = response.body();

                if (Objects.requireNonNull(response.body()).isStatus()){
                    dialogView.viewSukses(AbsensiKehadiranActivity.this, data.getRemarks());
                }else {
                    dialogView.viewNotifKosong(AbsensiKehadiranActivity.this, data.getRemarks(),"");
                }

            }

            @Override
            public void onFailure(@NonNull Call<ResponsePOJO> call, @NonNull Throwable t) {

                Log.e("ABSENSI_API_ERROR", "Gagal memanggil API absensi: " + t.getMessage(), t);
                dialogproses.dismiss();

                dialogView.pesanError(AbsensiKehadiranActivity.this);
            }
        });

        dialogproses.show();
    }

    public void kirimDataPulang(String absensi, String eselon, String idpegawai, String timetableid, String tanggal, String jam, String posisi, String status, String lat, String lng, String ket, int terlambat, String jampegawai, String validasi, String berakhlak){
        Dialog dialogproses = new Dialog(AbsensiKehadiranActivity.this, R.style.DialogStyle);
        dialogproses.setContentView(R.layout.view_proses);
        dialogproses.setCancelable(false);

        byte[] imageBytes = ambilFoto.compressToMax80KB(file);
        MultipartBody.Part fotoPart = prepareFilePart("fototaging", imageBytes);

        Call<ResponsePOJO> call =
                RetroClient.getInstance().getApi().uploadAbsenKehadiranPulang(
                        fotoPart,
                        textPart(absensi),
                        textPart(eselon),
                        textPart(idpegawai),
                        textPart(timetableid),
                        textPart(tanggal),
                        textPart(jam),
                        textPart(posisi),
                        textPart(status),
                        textPart(lat),
                        textPart(lng),
                        textPart(ket),
                        textPart(String.valueOf(terlambat)),
                        textPart(eOPD),
                        textPart(jampegawai),
                        textPart(validasi),
                        textPart(rbFakeGPS),
                        textPart(batasWaktu),
                        textPart(berakhlak)
                );
        call.enqueue(new Callback<ResponsePOJO>() {
            @Override
            public void onResponse(@NonNull Call<ResponsePOJO> call, @NonNull Response<ResponsePOJO> response) {
                dialogproses.dismiss();

                if (!response.isSuccessful()){
                    dialogView.viewNotifKosong(AbsensiKehadiranActivity.this, "Gagal mengisi absensi,", "silahkan coba kembali  iii.");
                    return;
                }

//                Log.d("Response Status Normal", response.body().getRemarks());
                ResponsePOJO data = response.body();

                if (Objects.requireNonNull(response.body()).isStatus()){
                    dialogView.viewSukses(AbsensiKehadiranActivity.this, data.getRemarks());
                }else {
                    dialogView.viewNotifKosong(AbsensiKehadiranActivity.this, data.getRemarks(),"");
                }

            }

            @Override
            public void onFailure(@NonNull Call<ResponsePOJO> call, @NonNull Throwable t) {
                Log.e("Response Status Normal", "Gagal memanggil API absensi: " + t.getMessage(), t);
                dialogproses.dismiss();
                dialogView.pesanError(AbsensiKehadiranActivity.this);
            }
        });

        dialogproses.show();
    }

    Boolean statusRekam = false;
    public void viewSukses(Context context){
        statusRekam = true;
        Dialog dialogSukes = new Dialog(context, R.style.DialogStyle);
        dialogSukes.setContentView(R.layout.view_sukses);
        dialogSukes.setCancelable(false);

        ImageView tvTutupDialog = dialogSukes.findViewById(R.id.tvTutupDialog);


        tvTutupDialog.setOnClickListener(v -> {

            dialogSukes.dismiss();
            finish();
        });
        stopLocationUpdates();
        if (file.exists()) file.delete();
        Handler handler = new Handler();
        handler.postDelayed(() -> {

            dialogSukes.dismiss();

            finish();

            }, 1500);

        dialogSukes.show();
    }



    public void handlerTutupActivity(){
        Handler handler = new Handler();
        handler.postDelayed(() -> {
            if (!statusRekam){
                cobarekamkembali();
            }
            }, 120000);

    }

    public void cobarekamkembali(){
        Dialog dataKosong = new Dialog(AbsensiKehadiranActivity.this, R.style.DialogStyle);
        dataKosong.setContentView(R.layout.view_warning_kosong);
        TextView tvWarning1 = dataKosong.findViewById(R.id.tvWarning1);
        ImageView tvTutupDialog = dataKosong.findViewById(R.id.tvTutupDialog);
        TextView tvCobaKembali = dataKosong.findViewById(R.id.tvCobaKembali);

        tvWarning1.setText("Proses terlalu lama, coba ulang kembali!");
        dataKosong.setCancelable(false);
        tvTutupDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataKosong.dismiss();
                finish();
            }
        });

        tvCobaKembali.setVisibility(View.VISIBLE);
        tvCobaKembali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataKosong.dismiss();
                finish();
            }
        });

        dataKosong.show();
    }


    @SuppressLint("ResourceAsColor")
    public void periksaWaktu(){

        try {
            jamMasukDate = SIMPLE_FORMAT_JAM.parse(jamMasuk);
            jamPulangDate = SIMPLE_FORMAT_JAM.parse(jamPulang);

            jamTaging = SIMPLE_FORMAT_JAM_TAGING.format(new Date());

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

            diff = hours + " jam :" + mins+" menit";

            rbJam = SIMPLE_FORMAT_JAM.format(new Date());
            if (tagingTime.getTime() <= jamMasukDate.getTime()) {

                rbKet = "tepat waktu";
                mins = 0;
            }else if(tagingTime.getTime() > jamMasukDate.getTime()){

                rbKet = "terlambat";
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }


    //  Maps
    //    Maps
    private void setupViewModel() {
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void checkLocationPermission() {
        int hasWriteStoragePermission;
        hasWriteStoragePermission = getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION);
        if (hasWriteStoragePermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CHECK_SETTINGS);
            return;
        }

    }


    public void backFinalDinasLuar(View view){
        finish();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void setupViews() {
        //Maps
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapKehadiranOne);
        mapFragment.getMapAsync(this);
    }


    int ulang = 0;


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
            map.addMarker(new MarkerOptions().position(new LatLng(locationObj.getLatitude(), locationObj.getLongitude())).icon(bitmapDescriptorFromVector(this, R.drawable.asn_lk)).title(lokasi.getAddress(AbsensiKehadiranActivity.this, locationObj.getLatitude(), locationObj.getLongitude())));
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(locationObj.getLatitude(), locationObj.getLongitude()), 18f));
            latGMap = locationObj.getLatitude();
            lngGMap = locationObj.getLongitude();

            locationArrayList.add(locationObj);

            //Draw Line
            LatLng singleLatLong = null;
            ArrayList<LatLng> pnts = new ArrayList<LatLng>();
            if(locationArrayList != null) {
                for(int i = 0 ; i < locationArrayList.size(); i++) {
                    double routePoint1Lat = locationArrayList.get(i).getLatitude();
                    double routePoint2Long = locationArrayList.get(i).getLongitude();
                    singleLatLong = new LatLng(routePoint1Lat,
                            routePoint2Long);
                    pnts.add(singleLatLong);
                }
            }

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
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        if (ulang == 1){
                            ulang = 0;
                        }
                    }

                } else {

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        checkLocationPermission();
                    }
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);

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


    @Override
    protected void onResume() {
        super.onResume();
        handlerTutupActivity();
    }

    //endregion
//Bottom Sheet

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

    String berakhlak;
    int ber = 0, a = 0, k = 0, h = 0, l = 0, ad = 0, ko = 0;
    StringBuffer stringBuffer = new StringBuffer();

    public void viewBerakhlak(String pulang, String eselon, String sEmployId, String timetableid, String rbTanggal, String rbJam,
                              String rbPosisi, String rbStatus, String rbLat, String rbLng, String rbKet, Integer terlambat, String jamPulang, String rbValid){
        Dialog berakhlakDialog = new Dialog(AbsensiKehadiranActivity.this, R.style.DialogStyle);
        berakhlakDialog.setContentView(R.layout.vire_berakhlak);
        berakhlakDialog.setCancelable(false);

        LinearLayout llListBerakhlak1 = berakhlakDialog.findViewById(R.id.llListBerakhlak1);
        LinearLayout llListBerakhlak2 = berakhlakDialog.findViewById(R.id.llListBerakhlak2);
        LinearLayout llListBerakhlak3 = berakhlakDialog.findViewById(R.id.llListBerakhlak3);
        LinearLayout llListBerakhlak4 = berakhlakDialog.findViewById(R.id.llListBerakhlak4);
        LinearLayout llListBerakhlak5 = berakhlakDialog.findViewById(R.id.llListBerakhlak5);
        LinearLayout llListBerakhlak6 = berakhlakDialog.findViewById(R.id.llListBerakhlak6);
        LinearLayout llListBerakhlak7 = berakhlakDialog.findViewById(R.id.llListBerakhlak7);

        ImageView ivListBerakhlah1 = berakhlakDialog.findViewById(R.id.ivListBerakhlah1);
        ImageView ivListBerakhlah2 = berakhlakDialog.findViewById(R.id.ivListBerakhlah2);
        ImageView ivListBerakhlah3 = berakhlakDialog.findViewById(R.id.ivListBerakhlah3);
        ImageView ivListBerakhlah4 = berakhlakDialog.findViewById(R.id.ivListBerakhlah4);
        ImageView ivListBerakhlah5 = berakhlakDialog.findViewById(R.id.ivListBerakhlah5);
        ImageView ivListBerakhlah6 = berakhlakDialog.findViewById(R.id.ivListBerakhlah6);
        ImageView ivListBerakhlah7 = berakhlakDialog.findViewById(R.id.ivListBerakhlah7);


        ImageView ivCheckBer1 = berakhlakDialog.findViewById(R.id.ivCheckBer1);
        ImageView ivCheckBer2 = berakhlakDialog.findViewById(R.id.ivCheckBer2);
        ImageView ivCheckBer3 = berakhlakDialog.findViewById(R.id.ivCheckBer3);
        ImageView ivCheckBer4 = berakhlakDialog.findViewById(R.id.ivCheckBer4);
        ImageView ivCheckBer5 = berakhlakDialog.findViewById(R.id.ivCheckBer5);
        ImageView ivCheckBer6 = berakhlakDialog.findViewById(R.id.ivCheckBer6);
        ImageView ivCheckBer7 = berakhlakDialog.findViewById(R.id.ivCheckBer7);

        ImageView ivCloseBerakhlak = berakhlakDialog.findViewById(R.id.ivCloseBerakhlak);
        TextView tvKirimSurveiBerakhlak = berakhlakDialog.findViewById(R.id.tvKirimSurveiBerakhlak);

        ivCloseBerakhlak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                berakhlakDialog.dismiss();
            }
        });

        llListBerakhlak1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ber == 0){
                    ber = 1;
                    ivListBerakhlah1.setVisibility(View.GONE);
                    ivCheckBer1.setVisibility(View.VISIBLE);
                }else{
                    ber = 0;
                    ivListBerakhlah1.setVisibility(View.VISIBLE);
                    ivCheckBer1.setVisibility(View.GONE);
                }
            }
        });

        llListBerakhlak2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (a == 0){
                    a = 1;
                    ivListBerakhlah2.setVisibility(View.GONE);
                    ivCheckBer2.setVisibility(View.VISIBLE);
                }else{
                    a = 0;
                    ivListBerakhlah2.setVisibility(View.VISIBLE);
                    ivCheckBer2.setVisibility(View.GONE);
                }
            }
        });

        llListBerakhlak3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (k == 0){
                    k = 1;
                    ivListBerakhlah3.setVisibility(View.GONE);
                    ivCheckBer3.setVisibility(View.VISIBLE);
                }else{
                    k = 0;
                    ivListBerakhlah3.setVisibility(View.VISIBLE);
                    ivCheckBer3.setVisibility(View.GONE);
                }
            }
        });

        llListBerakhlak4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (h == 0){
                    h = 1;
                    ivListBerakhlah4.setVisibility(View.GONE);
                    ivCheckBer4.setVisibility(View.VISIBLE);
                }else{
                    h = 0;
                    ivListBerakhlah4.setVisibility(View.VISIBLE);
                    ivCheckBer4.setVisibility(View.GONE);
                }
            }
        });

        llListBerakhlak5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (l == 0){
                    l = 1;
                    ivListBerakhlah5.setVisibility(View.GONE);
                    ivCheckBer5.setVisibility(View.VISIBLE);
                }else{
                    l = 0;
                    ivListBerakhlah5.setVisibility(View.VISIBLE);
                    ivCheckBer5.setVisibility(View.GONE);
                }
            }
        });

        llListBerakhlak6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ad == 0){
                    ad = 1;
                    ivListBerakhlah6.setVisibility(View.GONE);
                    ivCheckBer6.setVisibility(View.VISIBLE);
                }else{
                    ad = 0;
                    ivListBerakhlah6.setVisibility(View.VISIBLE);
                    ivCheckBer6.setVisibility(View.GONE);
                }
            }
        });


        llListBerakhlak7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ko == 0){
                    ko = 1;
                    ivListBerakhlah7.setVisibility(View.GONE);
                    ivCheckBer7.setVisibility(View.VISIBLE);
                }else{
                    ko = 0;
                    ivListBerakhlah7.setVisibility(View.VISIBLE);
                    ivCheckBer7.setVisibility(View.GONE);
                }
            }
        });



        tvKirimSurveiBerakhlak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ber == 0 && a == 0 && k == 0 && h ==  0 && l == 0 && ad == 0 && ko == 0){
                    Toast.makeText(mContext, "Tentukan Core Values", Toast.LENGTH_SHORT).show();
                }else{
                    if (ber == 1){
                        stringBuffer.append("1, ");
                    }

                    if(a == 1){
                        stringBuffer.append("2, ");
                    }

                    if(k == 1){
                        stringBuffer.append("3, ");
                    }
                    if(h == 1){
                        stringBuffer.append("4, ");
                    }

                    if(l == 1){
                        stringBuffer.append("5, ");
                    }
                    if(ad == 1){
                        stringBuffer.append("6, ");
                    }
                    if(ko == 1){
                        stringBuffer.append("7, ");
                    }


                    int length = stringBuffer.length();

                    if (length > 0) {
                        stringBuffer.deleteCharAt(length - 2);
                    }

                    berakhlak = stringBuffer.toString();

                    berakhlakDialog.dismiss();


                    kirimDataPulang("pulang", eselon, sEmployId, timetableid, rbTanggal, rbJam, rbPosisi, rbStatus, rbLat, rbLng, rbKet, 0,  jamPulang, rbValid, berakhlak);


                }

            }
        });

        berakhlakDialog.show();

    }
}