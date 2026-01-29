package go.pemkott.appsandroidmobiletebingtinggi.NewDashboard;

import static go.pemkott.appsandroidmobiletebingtinggi.konstanta.TimeFormat.BULAN;
import static go.pemkott.appsandroidmobiletebingtinggi.konstanta.TimeFormat.HARI_TEXT;
import static go.pemkott.appsandroidmobiletebingtinggi.konstanta.TimeFormat.SIMPLE_FORMAT_TANGGAL;
import static go.pemkott.appsandroidmobiletebingtinggi.konstanta.TimeFormat.TAHUN;
import static go.pemkott.appsandroidmobiletebingtinggi.konstanta.TimeFormat.TANGGAL;
import static go.pemkott.appsandroidmobiletebingtinggi.konstanta.TimeFormat.bulan;
import static go.pemkott.appsandroidmobiletebingtinggi.konstanta.TimeFormat.hariText;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import go.pemkott.appsandroidmobiletebingtinggi.ProfileActivity;
import go.pemkott.appsandroidmobiletebingtinggi.R;
import go.pemkott.appsandroidmobiletebingtinggi.api.HttpService;
import go.pemkott.appsandroidmobiletebingtinggi.camerax.CameraxActivity;
import go.pemkott.appsandroidmobiletebingtinggi.database.DatabaseHelper;
import go.pemkott.appsandroidmobiletebingtinggi.dialogview.DialogView;
import go.pemkott.appsandroidmobiletebingtinggi.dinasluarkantor.perjalanandinas.SppdActivity;
import go.pemkott.appsandroidmobiletebingtinggi.dinasluarkantor.tugaslapangan.TugasLapanganActivity;
import go.pemkott.appsandroidmobiletebingtinggi.izin.cuti.CutiActivity;
import go.pemkott.appsandroidmobiletebingtinggi.izin.keperluanpribadi.KeperluanPribadiActivity;
import go.pemkott.appsandroidmobiletebingtinggi.izin.sakit.SakitActivity;
import go.pemkott.appsandroidmobiletebingtinggi.izinsift.JadwalIzinSiftActivity;
import go.pemkott.appsandroidmobiletebingtinggi.kehadiransift.JadwalSiftActivity;
import go.pemkott.appsandroidmobiletebingtinggi.login.SessionManager;
import go.pemkott.appsandroidmobiletebingtinggi.model.CheckUpdate;
import go.pemkott.appsandroidmobiletebingtinggi.model.KegiatanIzin;
import go.pemkott.appsandroidmobiletebingtinggi.model.Koordinat;
import go.pemkott.appsandroidmobiletebingtinggi.model.ValidasiData;
import go.pemkott.appsandroidmobiletebingtinggi.rekap.RekapAbsensActivity;
import go.pemkott.appsandroidmobiletebingtinggi.singkronjadwal.SettingAdapter;
import go.pemkott.appsandroidmobiletebingtinggi.singkronjadwal.TimeTebleSetting;
import go.pemkott.appsandroidmobiletebingtinggi.singkronjadwalsift.CalendarJadwalSiftActivity;
import go.pemkott.appsandroidmobiletebingtinggi.verifikasi.ValidasiNewActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DashboardVersiOne extends AppCompatActivity {

    private BottomSheetBehavior sheetBehavior, sheetBehaviorIzin;
    CardView cvKehadiran, cvKehadiranKantor, cvTugasLapangan, cvJadwal, cvLokasi, cvKegiatan, cvMenuIzin, cvMenuPerjalananDinas,
            cvCuti, cvKeperluanPribadi, cvSakit;

    View vOpenBottomSheet, vOpenBottomSheetIzin;
    LocationManager manager;
    DatabaseHelper databaseHelper;
    HttpService httpService;

    public static String statusSift, fotoProfile, jam_masuk, jam_pulang, sOPD, sNip, sJabatan, sKantor, sEmployee_id, sUsername, sAkses, sActive,  sToken, sVerifikator, toDay = SIMPLE_FORMAT_TANGGAL.format(new Date());

    TextView tvNamaUser, tvJamPulang, tvJamMasuk, tvTanggalHariIni;
    public static int jenisabsensi;
    DialogView dialogView = new DialogView(DashboardVersiOne.this);
    private static final String CAMERA_PERMISSION = Manifest.permission.CAMERA;
    private static final String LOCATION_PERMISSION =  Manifest.permission.ACCESS_FINE_LOCATION;
    private static  String READ_STORAGE_PERMISSION;
    ProgressBar pgSingkronLokasi, pgSingkronKegiatan;
    ImageView ivSingkronLokasi, ivSingkronKegiatan;
    CircleImageView ciUser;
    ConstraintLayout clVerifikasi;
    public static DashboardVersiOne dashboardVersiOne;
    SwipeRefreshLayout swipeRefreshLayout;

    ConstraintLayout clCariRekap;
    SessionManager session;

    String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        window.setStatusBarColor(this.getResources().getColor(R.color.background_color));
        window.setNavigationBarColor(getResources().getColor(R.color.background_color));
        setContentView(R.layout.activity_dashboard_versi_one);
        session = new SessionManager(this);
        userid = session.getPegawaiId();


        READ_STORAGE_PERMISSION = Manifest.permission.READ_EXTERNAL_STORAGE;

        dashboardVersiOne = this;


        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        databaseHelper = new DatabaseHelper(this);

        datauser();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://absensi.tebingtinggikota.go.id/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        httpService = retrofit.create(HttpService.class);

        setUpReferences();
        setUpReferencesIzin();
        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        sheetBehaviorIzin.setState(BottomSheetBehavior.STATE_COLLAPSED);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        cvKehadiran = findViewById(R.id.cvKehadiran);
        vOpenBottomSheet = findViewById(R.id.vOpenBottomSheet);
        vOpenBottomSheetIzin = findViewById(R.id.vOpenBottomSheetIzin);
        cvKehadiranKantor = findViewById(R.id.cvKehadiranKantor);
        cvTugasLapangan = findViewById(R.id.cvTugasLapangan);
        tvNamaUser = findViewById(R.id.tvNamaUser);
        tvJamMasuk = findViewById(R.id.tvJamMasuk);
        tvJamPulang = findViewById(R.id.tvJamPulang);
        ciUser = findViewById(R.id.ciUser);
        cvJadwal = findViewById(R.id.cvJadwal);
        cvLokasi = findViewById(R.id.cvLokasi);
        ivSingkronLokasi = findViewById(R.id.ivSingkronLokasi);
        pgSingkronKegiatan = findViewById(R.id.pgSingkronKegiatan);
        cvKegiatan = findViewById(R.id.cvKegiatan);
        ivSingkronKegiatan = findViewById(R.id.ivSingkronKegiatan);
        cvMenuIzin = findViewById(R.id.cvMenuIzin);
        cvMenuPerjalananDinas = findViewById(R.id.cvMenuPerjalananDinas);
        cvCuti = findViewById(R.id.cvCuti);
        cvKeperluanPribadi = findViewById(R.id.cvKeperluanPribadi);
        cvSakit = findViewById(R.id.cvSakit);
        clVerifikasi = findViewById(R.id.clVerifikasi);
        clCariRekap = findViewById(R.id.clCariRekap);
        tvTanggalHariIni = findViewById(R.id.tvTanggalHariIni);


        String tanggal = TANGGAL.format(new Date());
        String bulan = BULAN.format(new Date());
        String tahun = TAHUN.format(new Date());
        String hari = HARI_TEXT.format(new Date());
        String infotanggal = hariText(hari)+", "+tanggal+" "+bulan(bulan)+" "+tahun;

        tvTanggalHariIni.setText(infotanggal);

        pgSingkronLokasi = findViewById(R.id.pgSingkronLokasi);

        clCariRekap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardVersiOne.this, RekapAbsensActivity.class));
            }
        });
        ciUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardVersiOne.this, ProfileActivity.class));
            }
        });

        cvKehadiranKantor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jenisabsensi = 1;
                Intent kehadiranIntent = new Intent(DashboardVersiOne.this, CameraxActivity.class);
                kehadiranIntent.putExtra("aktivitas", "kehadiran");
                startActivity(kehadiranIntent);
            }
        });

        cvKehadiran.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (statusSift.equals("0")){
                    bukaKehadiran();
                }else{
                    jenisabsensi = 7;
                    Intent intentJadwalSifting = new Intent(DashboardVersiOne.this, JadwalSiftActivity.class);
                    intentJadwalSifting.putExtra("jam_masuk", jam_masuk);
                    intentJadwalSifting.putExtra("jam_pulang", jam_pulang);
                    startActivity(intentJadwalSifting);
                }
            }
        });

        cvTugasLapangan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jenisabsensi = 2;
                startActivity(new Intent(DashboardVersiOne.this, TugasLapanganActivity.class));

            }
        });

        vOpenBottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bukaKehadiran();


            }
        });

        vOpenBottomSheetIzin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                bukaMenuIzin();
            }
        });

        tvNamaUser.setText(sUsername);
        Glide.with(this)
                .load( "https://absensi.tebingtinggikota.go.id/storage/"+fotoProfile )
                .into( ciUser );
        showPermissionDialog();

        cvJadwal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (statusSift.equals("0")){
                    viewJadwalKerja();
                }else{
                    Intent intentJadwalSifting = new Intent(DashboardVersiOne.this, CalendarJadwalSiftActivity.class);
                    intentJadwalSifting.putExtra("jam_masuk", jam_masuk);
                    intentJadwalSifting.putExtra("jam_pulang", jam_pulang);
                    startActivity(intentJadwalSifting);
                }
            }
        });

        cvLokasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ivSingkronLokasi.setVisibility(View.GONE);
                pgSingkronLokasi.setVisibility(View.VISIBLE);
                koordinatOPD();
//                koordintaEmployee();
            }
        });

        cvKegiatan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pgSingkronKegiatan.setVisibility(View.VISIBLE);
                ivSingkronKegiatan.setVisibility(View.GONE);
                singkronKegiatan();
            }
        });

        cvMenuIzin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (statusSift.equals("0")){
                    bukaMenuIzin();
                }else{
                    jenisabsensi = 8;
                    Intent intentJadwalSifting = new Intent(DashboardVersiOne.this, JadwalIzinSiftActivity.class);
                    startActivity(intentJadwalSifting);
                }
            }
        });

        cvMenuPerjalananDinas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jenisabsensi = 3;
                startActivity(new Intent(DashboardVersiOne.this, SppdActivity.class));
            }
        });

        cvCuti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jenisabsensi = 4;
                startActivity(new Intent(DashboardVersiOne.this, CutiActivity.class));

            }
        });

        cvKeperluanPribadi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jenisabsensi = 5;
                startActivity(new Intent(DashboardVersiOne.this, KeperluanPribadiActivity.class));
            }
        });

        cvSakit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jenisabsensi = 6;
                startActivity(new Intent(DashboardVersiOne.this, SakitActivity.class));
            }
        });

        clVerifikasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardVersiOne.this, ValidasiNewActivity.class));
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onResume();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }
    private void showPermissionDialog() {

        if (ContextCompat.checkSelfPermission(this,CAMERA_PERMISSION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, LOCATION_PERMISSION) == PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this,READ_STORAGE_PERMISSION)  == PackageManager.PERMISSION_GRANTED
        ){
            Toast.makeText(this, "Permission accepted", Toast.LENGTH_SHORT).show();

        }else {
            int REQUEST_CODE = 11;
            ActivityCompat.requestPermissions(this, new String[]{ CAMERA_PERMISSION , LOCATION_PERMISSION , READ_STORAGE_PERMISSION }, REQUEST_CODE);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();


        dataValidasi(sVerifikator, sEmployee_id);


        int version = 0;
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        checkupdate(version);

        tvJamMasuk.setText("--:--");
        tvJamPulang.setText("--:--");

//        Cursor getPresence = databaseHelper.getPresenceByEmployeeAndDate(sEmployee_id, toDay);
//
//        if (getPresence != null && getPresence.moveToFirst()) {
//            String jamMasuk = getPresence.getString(getPresence.getColumnIndexOrThrow(DatabaseHelper.P_JAM_MASUK));
//            String jamPulang = getPresence.getString(getPresence.getColumnIndexOrThrow(DatabaseHelper.P_JAM_PULANG));
//
//            if (jamMasuk != null && !jamMasuk.isEmpty()) {
//                tvJamMasuk.setText(jamMasuk);
//            }
//
//            if (jamPulang != null && !jamPulang.isEmpty()) {
//                tvJamPulang.setText(jamPulang);
//            }
//        }
    }


    public void dataValidasi(String verifikator, String idE){
        if (verifikator.equals("verifikator1") || verifikator.equals("verifikator2")){
            Call<List<ValidasiData>> callKegiatan = httpService.getUrlListValidasi("https://absensi.tebingtinggikota.go.id/api/newVeriFragment?verifikator="+verifikator+"&id="+idE);
            callKegiatan.enqueue(new Callback<List<ValidasiData>>() {
                @Override
                public void onResponse(@NonNull Call<List<ValidasiData>> call, @NonNull Response<List<ValidasiData>> response) {
                    if (!response.isSuccessful()){

                        return;
                    }

                    List<ValidasiData> validasiDatas = response.body();
                    if (!validasiDatas.isEmpty()){
                        clVerifikasi.setVisibility(View.VISIBLE);
                    }else{
                        clVerifikasi.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<List<ValidasiData>> call, @NonNull Throwable t) {
//                    pesanError();
                }
            });
        }

    }


    public void checkupdate(int version){

        Call<List<CheckUpdate>> callCheckUpdate = httpService.getCheckUpdate("https://absensi.tebingtinggikota.go.id/api/updateapp");
        callCheckUpdate.enqueue(new Callback<List<CheckUpdate>>() {
            @Override
            public void onResponse(@NonNull Call<List<CheckUpdate>> call, @NonNull Response<List<CheckUpdate>> response) {
                if (!response.isSuccessful()) {
                    return;
                }
                List<CheckUpdate> checkUpdates = response.body();
                for (CheckUpdate checkUpdate : checkUpdates) {
                    if (checkUpdate.getVersion() > version) {
                        final String appPackageName = checkUpdate.getNamapackage();
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                        }

                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<CheckUpdate>> call, @NonNull Throwable t) {
                dialogView.viewNotifKosong(DashboardVersiOne.this, "Gagal menghubungkan, ", "mohon periksa jaringan internet anda.");

            }
        });
    }

    public void checkGPS(){
        if(!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            AlertDialog.Builder builder = new AlertDialog.Builder(DashboardVersiOne.this, R.style.ThemeOverlay_App_MaterialAlertDialog);
            builder.setCancelable(false);
            builder.setTitle("Peringatan!");
            builder.setMessage("GPS anda tidak aktif, anda harus mengaktifkan GPS terlebih dahulu!");
            builder.setNegativeButton("Nyalakan",
                    (dialogInterface, i) -> {

                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        dialogInterface.dismiss();
                    }
            );

            AlertDialog alert = builder.create();
            alert.show();
        }else{
            getLocation();
        }
    }

    public void getLocation(){
        if (ActivityCompat.checkSelfPermission(DashboardVersiOne.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(DashboardVersiOne.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(DashboardVersiOne.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
    }
    public void datauser(){

        Cursor res = databaseHelper.getAllData22(userid);
        if (res.getCount()==0){
            return;
        }


        while (res.moveToNext()){
            sEmployee_id = res.getString(1);
            sAkses = res.getString(3);
            sActive = res.getString(4);
            sToken = res.getString(5);
            sVerifikator = res.getString(6);

            Log.d("DB_LOGIN", "Employee ID   : " + sEmployee_id);
            Log.d("DB_LOGIN", "Akses         : " + sAkses);
            Log.d("DB_LOGIN", "Active        : " + sActive);
            Log.d("DB_LOGIN", "Token         : " + sToken);
            Log.d("DB_LOGIN", "Verifikator   : " + sVerifikator);
        }

        Cursor dataPegawai = databaseHelper.getDataEmployee(sEmployee_id);
        while (dataPegawai.moveToNext()){
            sNip = dataPegawai.getString(5);
            sUsername = dataPegawai.getString(6);
            sJabatan = dataPegawai.getString(12);
            sKantor = dataPegawai.getString(13);
            sOPD = dataPegawai.getString(4);
            fotoProfile = dataPegawai.getString(17);
            statusSift = dataPegawai.getString(19);

        }
    }

    private void setUpReferences() {
        LinearLayout layoutBottomSheet = findViewById(R.id.bottom_sheet_kehadiran_one);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);

        sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        sheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED){
                    Animation fadeInAnimation = AnimationUtils.loadAnimation(DashboardVersiOne.this, R.anim.fade_in);
                    vOpenBottomSheet.startAnimation(fadeInAnimation);
                    vOpenBottomSheet.setVisibility(View.VISIBLE);
                }else if (newState == BottomSheetBehavior.STATE_COLLAPSED){
                    vOpenBottomSheet.setVisibility(View.GONE);

                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                if (slideOffset > 0) {
                    vOpenBottomSheet.setVisibility(View.GONE);
                }
            }
        });
    }


    private void setUpReferencesIzin() {
        LinearLayout layoutBottomSheetIzin = findViewById(R.id.bottom_sheet_izin_one);
        sheetBehaviorIzin = BottomSheetBehavior.from(layoutBottomSheetIzin);

        sheetBehaviorIzin.setState(BottomSheetBehavior.STATE_COLLAPSED);
        sheetBehaviorIzin.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED){
                    Animation fadeInAnimation = AnimationUtils.loadAnimation(DashboardVersiOne.this, R.anim.fade_in);
                    vOpenBottomSheetIzin.startAnimation(fadeInAnimation);
                    vOpenBottomSheetIzin.setVisibility(View.VISIBLE);
                }else if (newState == BottomSheetBehavior.STATE_COLLAPSED){
                    vOpenBottomSheetIzin.setVisibility(View.GONE);

                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                if (slideOffset > 0) {
                    vOpenBottomSheetIzin.setVisibility(View.GONE);
                }
            }
        });
    }
    private void bukaKehadiran() {
            if (sheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            } else if (sheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED){
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

            }
    }

    private void bukaMenuIzin() {
        if (sheetBehaviorIzin.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            sheetBehaviorIzin.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else if (sheetBehaviorIzin.getState() == BottomSheetBehavior.STATE_COLLAPSED){
            sheetBehaviorIzin.setState(BottomSheetBehavior.STATE_EXPANDED);

        }
    }

    List<TimeTebleSetting> timeTable = new ArrayList<>();

    public void viewJadwalKerja(){
        timeTable.clear();

        Dialog dialogJadwalKerja = new Dialog(DashboardVersiOne.this, R.style.DialogStyle);
        dialogJadwalKerja.setContentView(R.layout.view_jadwal_kerja);
        dialogJadwalKerja.setCancelable(true);

        RecyclerView rvJadwalKerja= dialogJadwalKerja.findViewById(R.id.rvJadwalKerja);
        TextView tvInfoJamKerja= dialogJadwalKerja.findViewById(R.id.tvInfoJamKerja);
        LinearLayout btnSingkronJadwalKerja= dialogJadwalKerja.findViewById(R.id.btnSingkronJadwalKerja);
        ImageView ivCloseDaftarJadwalKerja= dialogJadwalKerja.findViewById(R.id.ivCloseDaftarJadwalKerja);
        ProgressBar progressBarSingkron= dialogJadwalKerja.findViewById(R.id.progressBarSingkron);
        rvJadwalKerja.setHasFixedSize(false);

        Cursor jadwal = databaseHelper.getKegiatanTimeTableCheck(sEmployee_id);
        if (jadwal.getCount() == 0){
            tvInfoJamKerja.setVisibility(View.VISIBLE);
        }

        while (jadwal.moveToNext()){
            TimeTebleSetting timeTables = new TimeTebleSetting();
            timeTables.setId(jadwal.getString(0));
            timeTables.setEmployee_id(jadwal.getString(1));
            timeTables.setTimetable_id(jadwal.getString(2));
            timeTables.setInisial(jadwal.getString(3));
            timeTables.setHari(jadwal.getString(4));
            timeTables.setMasuk(jadwal.getString(5));
            timeTables.setPulang(jadwal.getString(6));
            timeTable.add(timeTables);
        }


        rvJadwalKerja.setLayoutManager(new GridLayoutManager(DashboardVersiOne.this, 1));
        SettingAdapter settingAdapter = new SettingAdapter(timeTable);
        rvJadwalKerja.setAdapter(settingAdapter);

        ivCloseDaftarJadwalKerja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogJadwalKerja.dismiss();
            }
        });

        btnSingkronJadwalKerja.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBarSingkron.setVisibility(View.VISIBLE);
                btnSingkronJadwalKerja.setVisibility(View.GONE);
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("https://absensi.tebingtinggikota.go.id/api/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                HttpService holderAPI = retrofit.create(HttpService.class);


                Call<List<TimeTebleSetting>> callKegiatan = holderAPI.getUrlTimeTableSetting("https://absensi.tebingtinggikota.go.id/api/timetable?employee_id="+sEmployee_id, "Bearer "+sToken);
                callKegiatan.enqueue(new Callback<List<TimeTebleSetting>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<TimeTebleSetting>> call, @NonNull Response<List<TimeTebleSetting>> response) {
                        List<TimeTebleSetting> timeTables = response.body();
                        if (!response.isSuccessful()){
                            dialogView.viewNotifKosong(DashboardVersiOne.this, "Gagal melakukan singkronisasi jadwal.","Silahkan coba kembali.");
                        }

                        Integer deleteTimeTable = databaseHelper.deleteTimeTable(sEmployee_id);
                        if (deleteTimeTable > 0){
                            timeTable.clear();
                            timeTable = timeTables;
                        }

                        timeTables.size();
                        for (TimeTebleSetting timeTable : timeTables){
                            databaseHelper.insertDataTimeTable(timeTable.getId(), timeTable.getEmployee_id(), timeTable.getTimetable_id(),
                                    timeTable.getInisial(), timeTable.getHari(), timeTable.getMasuk(), timeTable.getPulang());
                        }


                        rvJadwalKerja.setLayoutManager(new GridLayoutManager(DashboardVersiOne.this, 1));
                        SettingAdapter settingAdapter = new SettingAdapter(timeTables);
                        rvJadwalKerja.setAdapter(settingAdapter);


                        btnSingkronJadwalKerja.setVisibility(View.VISIBLE);
                        progressBarSingkron.setVisibility(View.GONE);

                    }

                    @Override
                    public void onFailure(@NonNull Call<List<TimeTebleSetting>> call, @NonNull Throwable t) {

                        dialogView.viewNotifKosong(DashboardVersiOne.this, "Gagal mengakses server.", "Silahkan coba kembali.");
                    }
                });

            }
        });

        dialogJadwalKerja.show();
    }


    public void koordinatOPD(){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://absensi.tebingtinggikota.go.id/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        HttpService holderAPI = retrofit.create(HttpService.class);

        Call<List<Koordinat>> calllokasi = holderAPI.getUrlKoordinat("https://absensi.tebingtinggikota.go.id/api/koordinat?opdid="+sOPD);
        calllokasi.enqueue(new Callback<List<Koordinat>>() {
            @Override
            public void onResponse(@NonNull Call<List<Koordinat>> call, @NonNull Response<List<Koordinat>> response) {
                List<Koordinat> koordinats = response.body();
                if (!response.isSuccessful()){
                    dialogView.viewNotifKosong(DashboardVersiOne.this, "Gagal melakukan singkronisasi lokasi.","Silahkan coba kembali.");
                }

                Integer deleteKoordinatOPD = databaseHelper.deleteDataKoordinatOPD(sOPD);
                if (deleteKoordinatOPD > 0){
                    for (Koordinat koordinat : koordinats){
                        databaseHelper.insertDataKoordinat(koordinat.getId(), koordinat.getOpd_id(), koordinat.getAlamat(), koordinat.getLet(), koordinat.getLng());

                    }
                }


            }

            @Override
            public void onFailure(@NonNull Call<List<Koordinat>> call, @NonNull Throwable t) {
                dialogView.viewNotifKosong(DashboardVersiOne.this, "Gagal mengakses server.", "Silahkan coba kembali.");
            }
        });


        koordintaEmployee();

    }

    public void koordintaEmployee(){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://absensi.tebingtinggikota.go.id/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        HttpService holderAPI = retrofit.create(HttpService.class);


        Call<List<Koordinat>> callKegiatan = holderAPI.getUrlKoordinat("https://absensi.tebingtinggikota.go.id/api/koordinatemployee?id="+sEmployee_id);
        callKegiatan.enqueue(new Callback<List<Koordinat>>() {
            @Override
            public void onResponse(@NonNull Call<List<Koordinat>> call, @NonNull Response<List<Koordinat>> response) {
                List<Koordinat> koordinats = response.body();
                if (!response.isSuccessful()){
                    dialogView.viewNotifKosong(DashboardVersiOne.this, "Gagal melakukan singkronisasi lokasi.","Silahkan coba kembali.");
                }

                Log.d("TESTING KOORDINAT", String.valueOf(koordinats.size()));
                if (response.body().get(0).getStatus().equals("kosong")){
                    databaseHelper.deleteDataKoordinatEmployee(sEmployee_id);
                }else{
                    Cursor koorditaemplyeData = databaseHelper.getDataKoordinatEmp(sEmployee_id);
                    if (koorditaemplyeData.getCount() == 0){
                        for (Koordinat koordinat : koordinats){
                            databaseHelper.insertDataKoordinatEmployee(koordinat.getId(), sEmployee_id, koordinat.getAlamat(), koordinat.getLet(), koordinat.getLng());

                        }
                    }else{
                        Integer deleteKoordinatE = databaseHelper.deleteDataKoordinatEmployee(sEmployee_id);
                        if (deleteKoordinatE > 0){

                            for (Koordinat koordinat : koordinats){
                                databaseHelper.insertDataKoordinatEmployee(koordinat.getId(), sEmployee_id, koordinat.getAlamat(), koordinat.getLet(), koordinat.getLng());
                            }

                        }
                    }

                }

                pgSingkronLokasi.setVisibility(View.GONE);
                ivSingkronLokasi.setVisibility(View.VISIBLE);

            }

            @Override
            public void onFailure(@NonNull Call<List<Koordinat>> call, @NonNull Throwable t) {

                dialogView.viewNotifKosong(DashboardVersiOne.this, "Gagal mengakses server.", "Silahkan coba kembali.");

            }
        });
    }


    public void singkronKegiatan(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://absensi.tebingtinggikota.go.id/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        HttpService holderAPI = retrofit.create(HttpService.class);

        Call<List<KegiatanIzin>> callKegiatan = holderAPI.getUrlKegiatanNew("https://absensi.tebingtinggikota.go.id/api/kegiatannew?opd="+sOPD);
        callKegiatan.enqueue(new Callback<List<KegiatanIzin>>() {

            @Override
            public void onResponse(@NonNull Call<List<KegiatanIzin>> call, @NonNull Response<List<KegiatanIzin>> response) {
                List<KegiatanIzin> kegiatanIzins = response.body();
                if (!response.isSuccessful()){
                    dialogView.viewNotifKosong(DashboardVersiOne.this, "Gagal melakukan singkronisasi kegiatan.","Silahkan coba kembali.");
                }

                Integer deleteKegiatan = databaseHelper.deleteKegiatanIzin();
                if (deleteKegiatan>0){
                    for (KegiatanIzin kegiatanIzin : kegiatanIzins){
                        databaseHelper.insertResourceKegiatan(String.valueOf(kegiatanIzin.getId()), kegiatanIzin.getTipe(), kegiatanIzin.getKet());
                    }

                }

                pgSingkronKegiatan.setVisibility(View.GONE);
                ivSingkronKegiatan.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFailure(@NonNull Call<List<KegiatanIzin>> call, @NonNull Throwable t) {
                dialogView.viewNotifKosong(DashboardVersiOne.this, "Gagal mengakses server.", "Silahkan coba kembali.");
            }
        });
    }

}