package go.pemkott.appsandroidmobiletebingtinggi.izinsift.izinsiftpribadi;

import static go.pemkott.appsandroidmobiletebingtinggi.geolocation.model.LocationHelper.defaultLocation;
import static go.pemkott.appsandroidmobiletebingtinggi.konstanta.TimeFormat.SIMPLE_DATE_FORMAT_TAGING;
import static go.pemkott.appsandroidmobiletebingtinggi.konstanta.TimeFormat.SIMPLE_DATE_FORMAT_TAGING_PHOTO_REPORT;
import static go.pemkott.appsandroidmobiletebingtinggi.konstanta.TimeFormat.SIMPLE_FORMAT_JAM;
import static go.pemkott.appsandroidmobiletebingtinggi.konstanta.TimeFormat.SIMPLE_FORMAT_JAM_TAGING;
import static go.pemkott.appsandroidmobiletebingtinggi.konstanta.TimeFormat.SIMPLE_FORMAT_TANGGAL;
import static go.pemkott.appsandroidmobiletebingtinggi.konstanta.TimeFormat.localeID;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import go.pemkott.appsandroidmobiletebingtinggi.NewDashboard.DashboardVersiOne;
import go.pemkott.appsandroidmobiletebingtinggi.R;
import go.pemkott.appsandroidmobiletebingtinggi.api.ResponsePOJO;
import go.pemkott.appsandroidmobiletebingtinggi.api.RetroClient;
import go.pemkott.appsandroidmobiletebingtinggi.database.DatabaseHelper;
import go.pemkott.appsandroidmobiletebingtinggi.dialogview.DialogView;
import go.pemkott.appsandroidmobiletebingtinggi.geolocation.GetLocation;
import go.pemkott.appsandroidmobiletebingtinggi.izin.keperluanpribadi.KeperluanPribadiFinalActivity;
import go.pemkott.appsandroidmobiletebingtinggi.izinsift.JadwalIzinSiftActivity;
import go.pemkott.appsandroidmobiletebingtinggi.konstanta.AmbilFoto;
import go.pemkott.appsandroidmobiletebingtinggi.konstanta.Lokasi;
import go.pemkott.appsandroidmobiletebingtinggi.login.SessionManager;
import go.pemkott.appsandroidmobiletebingtinggi.utils.ClsGlobal;
import go.pemkott.appsandroidmobiletebingtinggi.utils.NetworkUtils;
import go.pemkott.appsandroidmobiletebingtinggi.viewmodel.LocationViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KeperluanPribadiSiftFinalActivity extends AppCompatActivity  implements OnMapReadyCallback {

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
    private static final String TAG = KeperluanPribadiFinalActivity.class.getSimpleName();
    File imageFile;
    private String  currentPhotoPath;
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
    String kegiatanlainnya;
    String tanggal;
    StringBuilder keterangan;
    String jam_masuk, jam_pulang, batasWaktu;
    SimpleDateFormat hari;

    String fotoTaging = null, lampiran = null;
    String ekslampiran;

    TextView tvKegiatanFinal, titleDinasLuar, title_content;
    ArrayList<String> kegiatans = new ArrayList<>();
    AmbilFoto ambilFoto = new AmbilFoto(KeperluanPribadiSiftFinalActivity.this);

    Bitmap rotationBitmapTag;
    Bitmap rotationBitmapSurat;

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
    String inisialsift, tipesift, masuksift, pulangsift, idsift;
    SessionManager session;
    int pegawaiId;
File file;
    @SuppressLint("WrongThread")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_keperluan_pribadi_sift_final);
        session = new SessionManager(this);
        pegawaiId = session.getPegawaiId();

        databaseHelper = new DatabaseHelper(this);
        tvKegiatanFinal = findViewById(R.id.tvKegiatanFinal);
        titleDinasLuar = findViewById(R.id.titleDinasLuar);
        rgKehadiran = findViewById(R.id.rgKehadiran);
        title_content = findViewById(R.id.title_contentkp);
        title_content.setText("KEPERLUAN PRIBADI");
//        Image View
        ivFinalKegiatan = findViewById(R.id.ivFinalKegiatanKp);

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

        titleDinasLuar.setText("Keperluan Pribadi");
        rbTanggal = JadwalIzinSiftActivity.tanggalSift;
        inisialsift = JadwalIzinSiftActivity.inisialsift;
        idsift = JadwalIzinSiftActivity.idsift;
        tipesift = JadwalIzinSiftActivity.tipesift;
        masuksift = JadwalIzinSiftActivity.masuksift;
        pulangsift = JadwalIzinSiftActivity.pulangsift;

        kegiatans.clear();
        kegiatans = KeperluanPribadiSiftActivity.kegiatanChecked;
        kegiatanlainnya = KeperluanPribadiSiftActivity.kegiatansLainnya;


        requestPermission();
        dataKegiatan();

        mockLocationsEnabled = false;
        Intent intent = getIntent();

        String myDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()+ "/eabsensi";
        String fileName = intent.getStringExtra("fileName");
        file = new File(myDir, fileName);

        Bitmap gambardeteksi = BitmapFactory.decodeFile(file.getAbsolutePath());
        ivFinalKegiatan.setImageBitmap(gambardeteksi);
        Bitmap selectedBitmap = ambilFoto.fileBitmapCompress(file);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        selectedBitmap.compress(Bitmap.CompressFormat.PNG,75, byteArrayOutputStream);
        byte[] imageInByte = byteArrayOutputStream.toByteArray();
        fotoTaging =  Base64.encodeToString(imageInByte,Base64.DEFAULT);


    }


    public void kirimDataDinasLuar(View view){
        requestPermission();
        if (mock_location == 1){
            dialogView.viewNotifKosong(KeperluanPribadiSiftFinalActivity.this, "Anda terdeteksi menggunakan Fake GPS.", "Jika ditemukan berulang kali, akun anda akan terblokir otomatis dan tercatat Alpa.");

        }else {
            if (fotoTaging == null) {
                dialogView.viewNotifKosong(KeperluanPribadiSiftFinalActivity.this, "Anda harus melampirkan Foto Kegiatan.", "");
            } else {
                uploadImages();
            }
        }
    }


    public void uploadImages(){

        periksaWaktu();
        hitungjarak();
        String rbPosisi = "kp";
        String rbStatus = "izin";

        String rbValid;
        if (eJabatan.equals("2")){
            rbValid = "2";
        }else{
            rbValid = "0";
        }

        selected = rgKehadiran.getCheckedRadioButtonId();
        radioSelectedKehadiran = findViewById(selected);
        String jamTagingPeriksa = SIMPLE_FORMAT_JAM_TAGING.format(new Date());
        Date hariini = null;
        String tanggal = SIMPLE_FORMAT_TANGGAL.format(new Date());
        Date pulangPeriksa = null, tagingTimePeriksa = null;
        try {
            hariini = SIMPLE_FORMAT_TANGGAL.parse(tanggal);

            pulangPeriksa = SIMPLE_FORMAT_JAM.parse(pulangsift);
            tagingTimePeriksa = SIMPLE_FORMAT_JAM_TAGING.parse(jamTagingPeriksa);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(hariini);
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        Date newDate = calendar.getTime();
        String infoJadwalhariini = SIMPLE_FORMAT_TANGGAL.format(newDate);

        if (tipesift.equals("malam")){
            if (radioSelectedKehadiran.getText().toString().equals("MASUK")){
                if(jam_masuk == null){

                    if (tanggal.equals(rbTanggal)){
                        kirimdata(rbValid, rbPosisi, rbStatus, "masuk", masuksift);
                    } else if (infoJadwalhariini.equals(rbTanggal)) {
                        if (tagingTime.getTime()> jamPulangDate.getTime()){
                            dialogView.viewNotifKosong(KeperluanPribadiSiftFinalActivity.this, "Anda tidak dapat melakukan absensi masuk pada jam pulang kerja.", "");
                        }else{
                            kirimdata(rbValid, rbPosisi, rbStatus, "masuk", masuksift);
                        }
                    }
                }else{
                    dialogView.viewNotifKosong(KeperluanPribadiSiftFinalActivity.this, "Anda sudah mengisi absensi masuk.", "");
                }
            }//Masuk
            //Pulang
            else {

                if (tanggal.equals(rbTanggal)){
                    if (jam_masuk == null){
                        dialogView.viewNotifKosong(KeperluanPribadiSiftFinalActivity.this, "Harap mengisi absensi masuk, untuk izin Keperluan pribadi.", "");
                    }else{
                        kirimdata(rbValid, rbPosisi, rbStatus, "pulang", pulangsift);
                    }
                }else{
                    if (infoJadwalhariini.equals(rbTanggal)){
                        Date batasWaktuJamMalam = null;
                        try {
                            batasWaktuJamMalam = SIMPLE_FORMAT_JAM_TAGING.parse("12:00");
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (tagingTime.getTime() < batasWaktuJamMalam.getTime()){
                            if (tagingTime.getTime() > jamPulangDate.getTime()){
                                if (jam_masuk == null) {
                                    kirimdata(rbValid, rbPosisi, rbStatus, "masukpulang", pulangsift);
                                } else {
                                    kirimdata(rbValid, rbPosisi, rbStatus, "pulang", pulangsift);
                                }
                            }
                        }else {
                            dialogView.viewNotifKosong(KeperluanPribadiSiftFinalActivity.this, "Batas melakukan absen telah lewat.", "Batas melakukan absen adalah pukul 12:00.");
                        }
                    }
                }

            }
        }//malam
        //pagi sore
        else{
            if (tagingTimePeriksa.getTime() < dateBatasWaktu.getTime()){
                dialogView.viewNotifKosong(KeperluanPribadiSiftFinalActivity.this, "Anda hanya dapat mengisi absen masuk, "+batasWaktu+" menit sebelum Jam Masuk", "");
            }
            else {

                if (radioSelectedKehadiran.getText().toString().equals("MASUK")) {
                    if (jam_masuk == null) {

                        if (tagingTimePeriksa.getTime() >= pulangPeriksa.getTime()) {
                            showMessage("Peringatan", "Anda tidak dapat melakukan absensi masuk pada jam pulang kerja.");
                        } else {
                            kirimdata(rbValid, rbPosisi, rbStatus, "masuk", masuksift);
                        }

                    } else {

                        showMessage("Peringatan", "Anda sudah mengisi absensi masuk.");

                    }
                } else {
                    rbPosisi = "kp";
                    if (jam_pulang == null) {

                        if (jam_masuk == null) {
                            kirimdata(rbValid, rbPosisi, rbStatus, "masukpulang", pulangsift);
                        } else {
                            kirimdata(rbValid, rbPosisi, rbStatus, "pulang", pulangsift);
                        }

                    } else {
                        showMessage("Peringatan", "Anda sudah mengisi absensi pulang.");
                    }
                }
            }
        }
    }

    public void kirimdata(String valid, String posisi, String status, String ketKehadiran, String jampegawai){

        Call<ResponsePOJO> call = RetroClient.getInstance().getApi().uploadAbsenKpSift(
                fotoTaging,
                ketKehadiran,
                eJabatan,
                sEmployeID,
                timetableid,
                rbTanggal,
                rbJam,
                posisi,
                status,
                rbLat,
                rbLng,
                rbKet,
                mins,
                eOPD,
                jampegawai,
                valid,
                lampiran,
                ekslampiran,
                rbFakeGPS,
                batasWaktu
        );

        call.enqueue(new Callback<ResponsePOJO>() {
            @Override
            public void onResponse(@NonNull Call<ResponsePOJO> call, @NonNull Response<ResponsePOJO> response) {
                if (!response.isSuccessful()){
                    dialogView.viewNotifKosong(KeperluanPribadiSiftFinalActivity.this, "Gagal mengisi absensi,", "silahkan coba kembali.");
                    return;
                }
                if(response.body().isStatus()){
                    viewSukses(KeperluanPribadiSiftFinalActivity.this, response.body().getRemarks(), "");

                }else{
                    dialogView.viewNotifKosong(KeperluanPribadiSiftFinalActivity.this, response.body().getRemarks(), "");
                }

            }

            @Override
            public void onFailure(@NonNull Call<ResponsePOJO> call, @NonNull Throwable t) {
                dialogView.viewNotifKosong(KeperluanPribadiSiftFinalActivity.this, "Gagal mengisi absensi,", "silahkan coba kembali.");
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
            dialogView.viewNotifKosong(KeperluanPribadiSiftFinalActivity.this, "Pastikan anda telah terhubung internet.", "");
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
            if (kegiatans.isEmpty()){
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

                File file = new File(currentPhotoPath);
                Bitmap bitmap = ambilFoto.fileBitmap(file);
                rotationBitmapTag = Bitmap.createBitmap(bitmap, 0,0, bitmap.getWidth(), bitmap.getHeight(), AmbilFoto.exifInterface(currentPhotoPath, 0), true);

                ivFinalKegiatan.setImageBitmap(rotationBitmapTag);

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                rotationBitmapTag.compress(Bitmap.CompressFormat.JPEG,50, byteArrayOutputStream);
                byte[] imageInByte = byteArrayOutputStream.toByteArray();
                fotoTaging =  Base64.encodeToString(imageInByte,Base64.DEFAULT);

                periksaWaktu();
                handlerProgressDialog();


            }
            else if (requestCode == 2 && resultCode == RESULT_OK){

                File file = new File(currentPhotoPath);
                Bitmap bitmap = ambilFoto.fileBitmap(file);

                rotationBitmapSurat = Bitmap.createBitmap(bitmap, 0,0, bitmap.getWidth(), bitmap.getHeight(), AmbilFoto.exifInterface(currentPhotoPath, 0), true);




                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                rotationBitmapSurat.compress(Bitmap.CompressFormat.JPEG,75, byteArrayOutputStream);
                byte[] imageInByte = byteArrayOutputStream.toByteArray();
                lampiran =  Base64.encodeToString(imageInByte,Base64.DEFAULT);
                ekslampiran = "jpg";

                handlerProgressDialog();

            }
            else if (requestCode == 33 && resultCode == Activity.RESULT_OK && data != null){
                requestPermission();


                Uri pdfUri = data.getData();
                String FilePath2 = ClsGlobal.getPathFromUri(KeperluanPribadiSiftFinalActivity.this, pdfUri);
                File file1 = new File(FilePath2);

                Bitmap bitmap = ambilFoto.fileBitmap(file1);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG,75, byteArrayOutputStream);
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

    public String getPDFPath(Uri uri){
        String absolutePath = "";
        try{
            InputStream inputStream = KeperluanPribadiSiftFinalActivity.this.getContentResolver().openInputStream(uri);
            byte[] pdfInBytes = new byte[inputStream.available()];
            inputStream.read(pdfInBytes);
//            String encodePdf = Base64.encodeToString(pdfInBytes, Base64.DEFAULT);
//            Toast.makeText(MainActivity5.this, " Bla bla"+encodePdf.toString(), Toast.LENGTH_SHORT).show();
            int offset = 0;
            int numRead = 0;
            while (offset < pdfInBytes.length && (numRead = inputStream.read(pdfInBytes, offset, pdfInBytes.length - offset)) >= 0) {
                offset += numRead;
            }

            String mPath = "";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1) {
                mPath= KeperluanPribadiSiftFinalActivity.this.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)+"absensi-"+sEmployeID+"-"+currentDateandTimes + ".pdf";
            }
            else
            {
                mPath= Environment.getExternalStorageDirectory().toString() + "absensi-"+sEmployeID+"-"+currentDateandTimes + ".pdf";
            }

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
            String path = MediaStore.Images.Media.insertImage(KeperluanPribadiSiftFinalActivity.this.getContentResolver(), inImage, "absensi-"+sEmployeID+"-"+currentDateandTime, null);
            return Uri.parse(path);

        }else {
            byteArraySurat = new ByteArrayOutputStream();
            inImage.compress(Bitmap.CompressFormat.PNG, 100, byteArraySurat);
            String paths = MediaStore.Images.Media.insertImage(KeperluanPribadiSiftFinalActivity.this.getContentResolver(), inImage, "absensi-"+sEmployeID+"-"+currentDateandTimes, null);
            return Uri.parse(paths);
        }

    }


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
        mapFragment.getMapAsync(this);

    }

    private void stopLocationUpdates() {
        locationViewModel.getLocationHelper(mContext).stopLocationUpdates();
    }



    private void subscribeToLocationUpdate() {
        locationViewModel.getLocationHelper(mContext).observe(this, location -> {
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
            map.addMarker(new MarkerOptions().position(new LatLng(locationObj.getLatitude(), locationObj.getLongitude())).icon(bitmapDescriptorFromVector(this, R.drawable.asn_lk)).title(lokasi.getAddress(KeperluanPribadiSiftFinalActivity.this, locationObj.getLatitude(), locationObj.getLongitude())));
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
            KeperluanPribadiSiftActivity.kp.finish();
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