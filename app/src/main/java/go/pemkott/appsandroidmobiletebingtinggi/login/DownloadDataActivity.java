package go.pemkott.appsandroidmobiletebingtinggi.login;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import go.pemkott.appsandroidmobiletebingtinggi.NewDashboard.DashboardVersiOne;
import go.pemkott.appsandroidmobiletebingtinggi.R;
import go.pemkott.appsandroidmobiletebingtinggi.api.HttpService;
import go.pemkott.appsandroidmobiletebingtinggi.api.RetroClient;
import go.pemkott.appsandroidmobiletebingtinggi.database.DatabaseHelper;
import go.pemkott.appsandroidmobiletebingtinggi.dialogview.DialogView;
import go.pemkott.appsandroidmobiletebingtinggi.model.DataEmployee;
import go.pemkott.appsandroidmobiletebingtinggi.model.KegiatanIzin;
import go.pemkott.appsandroidmobiletebingtinggi.model.Koordinat;
import go.pemkott.appsandroidmobiletebingtinggi.model.TimeTables;
import go.pemkott.appsandroidmobiletebingtinggi.model.WaktuSift;
import go.pemkott.appsandroidmobiletebingtinggi.utils.NetworkUtils;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Refactored DownloadDataActivity
 * - Sequential step flow
 * - Safe response checks
 * - DB writes in background
 * - Uses RequestBody empty for POST-with-URL endpoints
 */
public class DownloadDataActivity extends AppCompatActivity {
    private static final String TAG = "DownloadDataActivity";

    private ProgressBar progressBarHorizontal;
    private TextView tvinfoDownload;
    private DatabaseHelper databaseHelper;
    private DialogView dialogView;
    private HttpService holderAPI;

    private ExecutorService dbExecutor;
    private RequestBody emptyBody;

    private String sId, sToken, sEmployId, eOPD;

    // step index: 0=not started, 1=getDataPegawai,2=koordinat,3=timetable,4=kegiatan,5=koordinat_e,6=testsift,7=finished
    private final AtomicInteger stepIndex = new AtomicInteger(0);
    private final int TOTAL_STEPS = 6; // used for progress calculation (exclude initial read local)

    SessionManager session;
    int pegawaiId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_data);

        session = new SessionManager(this);
        pegawaiId = session.getPegawaiId();

        // init
        databaseHelper = new DatabaseHelper(this);
        dialogView = new DialogView(this);
        dbExecutor = Executors.newSingleThreadExecutor();
        emptyBody = RequestBody.create("", MediaType.parse("application/json"));

        progressBarHorizontal = findViewById(R.id.progressBarHorizontal);
        tvinfoDownload = findViewById(R.id.tvinfoDownload);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        try {
            window.setStatusBarColor(getResources().getColor(R.color.black));
        } catch (Exception ignored) { }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://absensi.tebingtinggikota.go.id/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        holderAPI = retrofit.create(HttpService.class);

        if (NetworkUtils.isConnectedFast(this)) {
            readLocalUserAndStart();
        } else {
            pesanError();
        }
    }

    private void readLocalUserAndStart() {
        Cursor tUser = databaseHelper.getAllData22(pegawaiId);
        int dataUser = 0;
        while (tUser.moveToNext()) {
            dataUser++;
            sId = tUser.getString(0);
            sToken = tUser.getString(5);
            sEmployId = tUser.getString(1);
        }
        tUser.close();

        if (dataUser > 0) {
            // start flow
            stepIndex.set(1);
            updateProgressText("Unduh data pegawai...");
            proceedNext();
        } else {
            viewNotifKosong("Data pengguna tidak ditemukan.", "", 1);
        }
    }

    /**
     * Proceed to next step based on stepIndex
     */
    private void proceedNext() {
        int step = stepIndex.get();
        switch (step) {
            case 1:
                updateProgressText("Unduh data pegawai...");
                getDataPegawai(sEmployId);
                break;
            case 2:
                updateProgressText("Unduh data koordinat OPD...");
                koordinat();
                break;
            case 3:
                updateProgressText("Unduh data timetable...");
                timetable(sEmployId, sToken);
                break;
            case 4:
                updateProgressText("Unduh data kegiatan...");
                kegiatan();
                break;
            case 5:
                updateProgressText("Unduh koordinat pegawai...");
                koordinat_e();
                break;
            case 6:
                updateProgressText("Unduh jam sift...");
                testsift();
                break;
            default:
                goToDashboard();
                break;
        }
        updateProgressBar();
    }

    private void updateProgressText(final String text) {
        runOnUiThread(() -> tvinfoDownload.setText(text));
    }

    private void updateProgressBar() {
        // stepIndex ranges 1..6 -> convert to 0..TOTAL_STEPS for percentage
        int idx = Math.max(0, Math.min(stepIndex.get(), TOTAL_STEPS));
        int progress = (int) ((idx / (float) TOTAL_STEPS) * 100);
        runOnUiThread(() -> progressBarHorizontal.setProgress(progress));
    }

    private void goToDashboard() {
        runOnUiThread(() -> {
            Intent dashboardActivity = new Intent(DownloadDataActivity.this, DashboardVersiOne.class);
            dashboardActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(dashboardActivity);
            finish();
        });
    }

    /* ============================
       API calls (sequential)
       ============================ */

    private void getDataPegawai(String idE) {
        if (idE == null || idE.isEmpty()) {
            viewNotifKosong("ID pegawai tidak valid.", "", 1);
            return;
        }

        // you used RetroClient.getInstance().getApi().dataEmployee(idE) originally, keep it:
        Call<DataEmployee> calls = RetroClient.getInstance().getApi().dataEmployee(idE);
        calls.enqueue(new Callback<DataEmployee>() {
            @Override
            public void onResponse(@NonNull Call<DataEmployee> call, @NonNull Response<DataEmployee> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    viewNotifKosong("Gagal mengunduh data pegawai, periksa koneksi internet anda dan coba kembali.", "", 1);
                    return;
                }

                DataEmployee body = response.body();
                // insert into DB in background
                dbExecutor.execute(() -> {
                    boolean insertDataPegawai = databaseHelper.insertDataEmployee(
                            body.getId(),
                            body.getAtasan_id1(),
                            body.getAtasan_id2(),
                            body.getPosition_id(),
                            body.getOpd_id(),
                            body.getNip(),
                            body.getNama(),
                            body.getEmail(),
                            body.getNo_hp(),
                            body.getKelompok(),
                            body.getS_jabatan(),
                            body.getEselon(),
                            body.getJabatan(),
                            body.getOpd(),
                            body.getAlamat(),
                            body.getLet(),
                            body.getLng(),
                            body.getFoto(),
                            body.getAwal_waktu(),
                            String.valueOf(body.getShift())
                    );

                    if (insertDataPegawai) {
                        // read OPD saved in DB
                        Cursor dataUser = databaseHelper.getDataEmployee(sEmployId);
                        String opd = null;
                        int c = 0;
                        while (dataUser.moveToNext()) {
                            opd = dataUser.getString(4);
                            c++;
                        }
                        dataUser.close();

                        eOPD = opd;
                        // move to next step on main thread
                        runOnUiThread(() -> {
                            stepIndex.incrementAndGet(); // -> 2
                            proceedNext();
                        });
                    } else {
                        runOnUiThread(() -> viewNotifKosong("Gagal menyimpan data pegawai.", "", 1));
                    }
                });
            }

            @Override
            public void onFailure(@NonNull Call<DataEmployee> call, @NonNull Throwable t) {
                Log.d(TAG, "getDataPegawai onFailure: " + t.getMessage());
                dialogView.viewNotifKosong(DownloadDataActivity.this, "Gagal memeriksa data pegawai,", "mohon periksa internet anda.");
            }
        });
    }

    private void koordinat() {
        String url = "https://absensi.tebingtinggikota.go.id/api/koordinat";
        Call<List<Koordinat>> callKoordinat = holderAPI.getUrlKoordinat(url, emptyBody);
        callKoordinat.enqueue(new Callback<List<Koordinat>>() {
            @Override
            public void onResponse(@NonNull Call<List<Koordinat>> call, @NonNull Response<List<Koordinat>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    viewNotifKosong("Gagal mengunduh data koordinat OPD, periksa koneksi internet anda dan coba kembali.", "", 2);
                    return;
                }

                List<Koordinat> koordinats = response.body();
                dbExecutor.execute(() -> {
                    for (Koordinat koordinat : koordinats) {
                        databaseHelper.insertDataKoordinat(koordinat.getId(), koordinat.getOpd_id(), koordinat.getAlamat(), koordinat.getLet(), koordinat.getLng());
                    }
                    runOnUiThread(() -> {
                        stepIndex.incrementAndGet(); // -> 3
                        proceedNext();
                    });
                });
            }

            @Override
            public void onFailure(@NonNull Call<List<Koordinat>> call, @NonNull Throwable t) {
                Log.d(TAG, "koordinat onFailure: " + t.getMessage());
                dialogView.viewNotifKosong(DownloadDataActivity.this, "Gagal menerima data koordinat,", "mohon periksa jaringan internet anda.");
            }
        });
    }

    private void timetable(String idEmployee, String token) {
        if (idEmployee == null || idEmployee.isEmpty()) {
            viewNotifKosong("ID pegawai tidak valid untuk timetable.", "", 3);
            return;
        }

        String url = "https://absensi.tebingtinggikota.go.id/api/timetable?employee_id=" + idEmployee;
        Call<List<TimeTables>> timeTable = holderAPI.getUrlTimeTable(url, "Bearer " + token, emptyBody);
        timeTable.enqueue(new Callback<List<TimeTables>>() {
            @Override
            public void onResponse(@NonNull Call<List<TimeTables>> call, @NonNull Response<List<TimeTables>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    viewNotifKosong("Gagal mengunduh timetable, periksa koneksi internet anda dan coba kembali.", "", 3);
                    return;
                }

                List<TimeTables> timeTables = response.body();
                dbExecutor.execute(() -> {
                    for (TimeTables timeTable : timeTables) {
                        databaseHelper.insertDataTimeTable(String.valueOf(timeTable.getId()), timeTable.getEmployee_id(), timeTable.getTimetable_id(), timeTable.getInisial(), String.valueOf(timeTable.getHari()), timeTable.getMasuk(), timeTable.getPulang());
                    }
                    runOnUiThread(() -> {
                        stepIndex.incrementAndGet(); // -> 4
                        proceedNext();
                    });
                });
            }

            @Override
            public void onFailure(@NonNull Call<List<TimeTables>> call, @NonNull Throwable t) {
                Log.d(TAG, "timetable onFailure: " + t.getMessage());
                pesanError();
            }
        });
    }

    private void kegiatan() {
        if (eOPD == null || eOPD.isEmpty()) {
            viewNotifKosong("OPD tidak ditemukan.", "", 4);
            return;
        }

        String url = "https://absensi.tebingtinggikota.go.id/api/kegiatannew?opd=" + eOPD;
        Call<List<KegiatanIzin>> callKegiatanIzins = holderAPI.getUrlKegiatan(url, "Bearer " + sToken, emptyBody);
        callKegiatanIzins.enqueue(new Callback<List<KegiatanIzin>>() {
            @Override
            public void onResponse(@NonNull Call<List<KegiatanIzin>> call, @NonNull Response<List<KegiatanIzin>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    dialogView.viewNotifKosong(DownloadDataActivity.this, "Gagal memeriksa data kegiatan,", "mohon periksa internet anda.");
                    return;
                }

                List<KegiatanIzin> kegiatanIzins = response.body();
                dbExecutor.execute(() -> {
                    for (KegiatanIzin kegiatanIzin : kegiatanIzins) {
                        databaseHelper.insertResourceKegiatan(String.valueOf(kegiatanIzin.getId()), kegiatanIzin.getTipe(), kegiatanIzin.getKet());
                    }
                    runOnUiThread(() -> {
                        stepIndex.incrementAndGet(); // -> 5
                        proceedNext();
                    });
                });
            }

            @Override
            public void onFailure(@NonNull Call<List<KegiatanIzin>> call, @NonNull Throwable t) {
                Log.d(TAG, "kegiatan onFailure: " + t.getMessage());
                pesanError();
            }
        });
    }

    private void koordinat_e() {
        if (sEmployId == null || sEmployId.isEmpty()) {
            viewNotifKosong("ID pegawai tidak valid untuk koordinat pegawai.", "", 4);
            return;
        }

        String url = "https://absensi.tebingtinggikota.go.id/api/koordinatemployee?id=" + sEmployId;
        Call<List<Koordinat>> callKoordinates = holderAPI.getUrlKoordinat(url, emptyBody);
        callKoordinates.enqueue(new Callback<List<Koordinat>>() {
            @Override
            public void onResponse(@NonNull Call<List<Koordinat>> call, @NonNull Response<List<Koordinat>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    viewNotifKosong("Gagal mengunduh koordinat pegawai, periksa koneksi internet anda dan coba kembali.", "", 4);
                    return;
                }

                List<Koordinat> koordinats = response.body();
                dbExecutor.execute(() -> {
                    for (Koordinat koordinat : koordinats) {
                        if ("ada".equals(koordinat.getStatus())) {
                            databaseHelper.insertDataKoordinatEmployee(koordinat.getId(), sEmployId, koordinat.getAlamat(), koordinat.getLet(), koordinat.getLng());
                        }
                    }
                    runOnUiThread(() -> {
                        stepIndex.incrementAndGet(); // -> 6
                        proceedNext();
                    });
                });
            }

            @Override
            public void onFailure(@NonNull Call<List<Koordinat>> call, @NonNull Throwable t) {
                Log.d(TAG, "koordinat_e onFailure: " + t.getMessage());
                dialogView.viewNotifKosong(DownloadDataActivity.this, "Gagal menerima data koordinat pegawai,", "mohon periksa jaringan internet anda.");
            }
        });
    }

    private void testsift() {
        if (eOPD == null || eOPD.isEmpty()) {
            // skip if no opd
            runOnUiThread(() -> {
                stepIndex.incrementAndGet();
                proceedNext();
            });
            return;
        }

        String url = "https://absensi.tebingtinggikota.go.id/api/testsift?eOPD=" + eOPD;
        Call<List<WaktuSift>> jadwalSiftPegawai = holderAPI.getTestSift(url, emptyBody);
        jadwalSiftPegawai.enqueue(new Callback<List<WaktuSift>>() {
            @Override
            public void onResponse(@NonNull Call<List<WaktuSift>> call, @NonNull Response<List<WaktuSift>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    viewNotifKosong("Gagal mengunduh data sift, periksa koneksi internet anda dan coba kembali.", "", 3);
                    return;
                }

                List<WaktuSift> waktuSifts = response.body();
                dbExecutor.execute(() -> {
                    for (WaktuSift waktuSift : waktuSifts) {
                        databaseHelper.insertJamSift(String.valueOf(waktuSift.getId()), String.valueOf(waktuSift.getOpd_id()), String.valueOf(waktuSift.getTipe()), String.valueOf(waktuSift.getInisial()), String.valueOf(waktuSift.getMasuk()), String.valueOf(waktuSift.getPulang()));
                    }
                    runOnUiThread(() -> {
                        stepIndex.incrementAndGet(); // -> 7 -> finish
                        proceedNext();
                    });
                });
            }

            @Override
            public void onFailure(@NonNull Call<List<WaktuSift>> call, @NonNull Throwable t) {
                Log.d(TAG, "testsift onFailure: " + t.getMessage());
                pesanError();
            }
        });
    }

    /* ============================
       UI helpers / error dialogs
       ============================ */

    public void pesanError() {
        Dialog errorDialogs = new Dialog(this, R.style.DialogStyle);
        errorDialogs.setContentView(R.layout.view_error);
        ImageView tvTutupDialog = errorDialogs.findViewById(R.id.tvTutupDialog);

        tvTutupDialog.setOnClickListener(v -> errorDialogs.dismiss());

        errorDialogs.show();
        // auto dismiss after 2s
        tvTutupDialog.postDelayed(errorDialogs::dismiss, 2000);
    }

    public void viewNotifKosong(String w1, String w2, int kode) {
        Dialog dataKosong = new Dialog(DownloadDataActivity.this, R.style.DialogStyle);
        dataKosong.setContentView(R.layout.view_warning_kosong);
        TextView tvWarning1 = dataKosong.findViewById(R.id.tvWarning1);
        ImageView tvTutupDialog = dataKosong.findViewById(R.id.tvTutupDialog);
        tvWarning1.setText(w1 + " " + w2);
        dataKosong.setCancelable(true);

        tvTutupDialog.setOnClickListener(v -> {
            // cleanup based on kode
            if (kode == 1) {
                databaseHelper.deleteDataUseAll();
            } else if (kode == 2) {
                databaseHelper.deleteDataUseAll();
                databaseHelper.deleteDataEmployeeAll();
            } else if (kode == 3) {
                databaseHelper.deleteDataUseAll();
                databaseHelper.deleteDataEmployeeAll();
                databaseHelper.deleteDataKoordinatOPDAll();
            } else if (kode == 4) {
                databaseHelper.deleteDataUseAll();
                databaseHelper.deleteDataEmployeeAll();
                databaseHelper.deleteDataKoordinatOPDAll();
                databaseHelper.deleteTimeTableAll();
            }
            dataKosong.dismiss();
            finish();
        });
        dataKosong.show();
    }

    @Override
    public void onBackPressed() {
        // allow default behavior; optionally disable while downloading
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbExecutor != null && !dbExecutor.isShutdown()) {
            dbExecutor.shutdownNow();
        }
    }
}

//package go.pemkott.appsandroidmobiletebingtinggi.login;
//
//import android.app.Dialog;
//import android.content.Intent;
//import android.database.Cursor;
//import android.os.Bundle;
//import android.os.Handler;
//import android.view.View;
//import android.view.Window;
//import android.view.WindowManager;
//import android.widget.ImageView;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//
//import java.util.List;
//
//import go.pemkott.appsandroidmobiletebingtinggi.NewDashboard.DashboardVersiOne;
//import go.pemkott.appsandroidmobiletebingtinggi.R;
//import go.pemkott.appsandroidmobiletebingtinggi.api.HttpService;
//import go.pemkott.appsandroidmobiletebingtinggi.api.RetroClient;
//import go.pemkott.appsandroidmobiletebingtinggi.database.DatabaseHelper;
//import go.pemkott.appsandroidmobiletebingtinggi.dialogview.DialogView;
//import go.pemkott.appsandroidmobiletebingtinggi.model.DataEmployee;
//import go.pemkott.appsandroidmobiletebingtinggi.model.KegiatanIzin;
//import go.pemkott.appsandroidmobiletebingtinggi.model.Koordinat;
//import go.pemkott.appsandroidmobiletebingtinggi.model.TimeTables;
//import go.pemkott.appsandroidmobiletebingtinggi.model.WaktuSift;
//import go.pemkott.appsandroidmobiletebingtinggi.utils.NetworkUtils;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//import retrofit2.Retrofit;
//import retrofit2.converter.gson.GsonConverterFactory;
//
//
//public class DownloadDataActivity extends AppCompatActivity {
//    ProgressBar progressBarHorizontal;
//    DatabaseHelper databaseHelper;
//
//    String sId, sToken, sEmployId, eOPD;
//    HttpService holderAPI;
//    DialogView dialogView = new DialogView(DownloadDataActivity.this);
//    TextView tvinfoDownload;
//
//    int download = 0;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_download_data);
//        databaseHelper = new DatabaseHelper(this);
//
//        getWindow().addFlags (WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("https://absensi.tebingtinggikota.go.id/api/")
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//        holderAPI = retrofit.create(HttpService.class);
//        Window window = this.getWindow();
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.setStatusBarColor(getResources().getColor(R.color.black));
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//
//        tvinfoDownload = findViewById(R.id.tvinfoDownload);
//
//        View decorView = getWindow().getDecorView();
//        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
//        decorView.setSystemUiVisibility(uiOptions);
//        progressBarHorizontal= findViewById(R.id.progressBarHorizontal);
//
//        if(NetworkUtils.isConnectedFast(this)){
//            dataUser();
//        }else{
//            pesanError();
//        }
//    }
//
//
//
//
//    public void koordinat(){
//
//        Call<List<Koordinat>> callKoordinat = holderAPI.getUrlKoordinat("https://absensi.tebingtinggikota.go.id/api/koordinat");
//        callKoordinat.enqueue(new Callback<List<Koordinat>>() {
//            @Override
//            public void onResponse(@NonNull Call<List<Koordinat>> call, @NonNull Response<List<Koordinat>> response) {
//                if (!response.isSuccessful()) {
//                    viewNotifKosong("Gagal mengunduh data, periksa koneksi internet anda dan coba kembali.", "", 2);
//                }
//
//                List<Koordinat> koordinats = response.body();
//                assert koordinats != null;
//                for (Koordinat koordinat : koordinats) {
//                    databaseHelper.insertDataKoordinat(koordinat.getId(), koordinat.getOpd_id(), koordinat.getAlamat(), koordinat.getLet(), koordinat.getLng());
//
//                }
//
//                download = 3;
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<List<Koordinat>> call, @NonNull Throwable t) {
//                dialogView.viewNotifKosong(DownloadDataActivity.this, "Gagal menerima data koordinas,", "mohon periksa jaringan internet anda.");
//
//            }
//        });
//    }
//
//    public void koordinat_e(){
//        Call<List<Koordinat>> callKoordinates = holderAPI.getUrlKoordinat("https://absensi.tebingtinggikota.go.id/api/koordinatemployee?id="+sEmployId);
//        callKoordinates.enqueue(new Callback<List<Koordinat>>() {
//            @Override
//            public void onResponse(@NonNull Call<List<Koordinat>> call, @NonNull Response<List<Koordinat>> response) {
//                List<Koordinat> koordinats = response.body();
//                if (!response.isSuccessful()) {
//
//                    viewNotifKosong("Gagal mengunduh data, periksa koneksi internet anda dan coba kembali.", "", 4);
//
//                }
//
//                assert koordinats != null;
//                for (Koordinat koordinat : koordinats) {
//                    if (koordinat.getStatus().equals("ada")) {
//                        databaseHelper.insertDataKoordinatEmployee(koordinat.getId(), sEmployId, koordinat.getAlamat(), koordinat.getLet(), koordinat.getLng());
//                    }
//                }
//
//                download = 6;
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<List<Koordinat>> call, @NonNull Throwable t) {
//                dialogView.viewNotifKosong(DownloadDataActivity.this, "Gagal menerima data koordinas,", "mohon periksa jaringan internet anda.");
//
//            }
//        });
//    }
//    private void kegiatan() {
//
//        Call<List<KegiatanIzin>> callKegiatanIzins = holderAPI.getUrlKegiatan("https://absensi.tebingtinggikota.go.id/api/kegiatannew?opd="+eOPD, "Bearer "+sToken);
//        callKegiatanIzins.enqueue(new Callback<List<KegiatanIzin>>() {
//            @Override
//            public void onResponse(@NonNull Call<List<KegiatanIzin>> call, @NonNull Response<List<KegiatanIzin>> response) {
//                List<KegiatanIzin> kegiatanIzins = response.body();
//                if (!response.isSuccessful()) {
//
//                    dialogView.viewNotifKosong(DownloadDataActivity.this, "Gagal memeriksa data absensi,", "mohon periksa internet anda.");
//
//                }
//
//                assert kegiatanIzins != null;
//                for (KegiatanIzin kegiatanIzin : kegiatanIzins) {
//                    databaseHelper.insertResourceKegiatan(String.valueOf(kegiatanIzin.getId()), kegiatanIzin.getTipe(), kegiatanIzin.getKet());
//                }
//
//                tvinfoDownload.setText("unduh data jadwal...");
//                download = 5;
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<List<KegiatanIzin>> call, @NonNull Throwable t) {
//                pesanError();
//
//            }
//        });
//
//    }
//
//    private void timetable(String idEmployee, String token){
//        Call<List<TimeTables>> timeTable = holderAPI.getUrlTimeTable("https://absensi.tebingtinggikota.go.id/api/timetable?employee_id="+idEmployee, "Bearer "+token);
//        timeTable.enqueue(new Callback<List<TimeTables>>() {
//            @Override
//            public void onResponse(@NonNull Call<List<TimeTables>> call, @NonNull Response<List<TimeTables>> response) {
//                List<TimeTables> timeTables = response.body();
//                if (!response.isSuccessful()) {
//                    viewNotifKosong("Gagal mengunduh data, periksa koneksi internet anda dan coba kembali.", "", 3);
//
//                }
//
//
//                assert timeTables != null;
//                for (TimeTables timeTable : timeTables) {
//                    databaseHelper.insertDataTimeTable(String.valueOf(timeTable.getId()), timeTable.getEmployee_id(), timeTable.getTimetable_id(), timeTable.getInisial(), String.valueOf(timeTable.getHari()), timeTable.getMasuk(), timeTable.getPulang());
//
//                }
//
//                download = 4;
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<List<TimeTables>> call, @NonNull Throwable t) {
//                pesanError();
//
//            }
//        });
//
//    }
//
//    public void getDataPegawai(String idE){
//        Call<DataEmployee> calls = RetroClient.getInstance().getApi().dataEmployee(idE);
//        calls.enqueue(new Callback<DataEmployee>() {
//            @Override
//            public void onResponse(@NonNull Call<DataEmployee> call, @NonNull Response<DataEmployee> response) {
//                if (!response.isSuccessful()) {
//                    viewNotifKosong("Gagal mengunduh data, periksa koneksi internet anda dan coba kembali.", "", 1);
//                }
//
//                assert response.body() != null;
//                boolean insertDataPegawai = databaseHelper.insertDataEmployee(
//                        response.body().getId(),
//                        response.body().getAtasan_id1(),
//                        response.body().getAtasan_id2(),
//                        response.body().getPosition_id(),
//                        response.body().getOpd_id(),
//                        response.body().getNip(),
//                        response.body().getNama(),
//                        response.body().getEmail(),
//                        response.body().getNo_hp(),
//                        response.body().getKelompok(),
//                        response.body().getS_jabatan(),
//                        response.body().getEselon(),
//                        response.body().getJabatan(),
//                        response.body().getOpd(),
//                        response.body().getAlamat(),
//                        response.body().getLet(),
//                        response.body().getLng(),
//                        response.body().getFoto(),
//                        response.body().getAwal_waktu(),
//                        String.valueOf(response.body().getShift())
//                );
//
//                if (insertDataPegawai){
//                    tvinfoDownload.setText("unduh data koordinat lokasi...");
//                    Cursor dataUser = databaseHelper.getDataEmployee(sEmployId);
//                    int dataPegawai = 0;
//
//                    while (dataUser.moveToNext()){
//                        eOPD = dataUser.getString(4);
//                        dataPegawai +=1;
//                    }
//                    if (dataPegawai == dataUser.getCount()){
//                        download = 2;
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<DataEmployee> call, @NonNull Throwable t) {
//                dialogView.viewNotifKosong(DownloadDataActivity.this, "Gagal memeriksa data absensi,", "mohon periksa internet anda.");
//
//            }
//        });
//
//    }
//
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//    }
//
//    public void testsift(){
//
//        Call<List<WaktuSift>> jadwalSiftPegawai = holderAPI.getTestSift("https://absensi.tebingtinggikota.go.id/api/testsift?eOPD="+eOPD);
//        jadwalSiftPegawai.enqueue(new Callback<List<WaktuSift>>() {
//            @Override
//            public void onResponse(@NonNull Call<List<WaktuSift>> call, @NonNull Response<List<WaktuSift>> response) {
//
//                if (!response.isSuccessful()){
//                    viewNotifKosong("Gagal mengunduh data, periksa koneksi internet anda dan coba kembali.", "", 3);
//                }
//
//                List<WaktuSift> waktuSifts = response.body();
//                assert waktuSifts != null;
//                for(WaktuSift waktuSift : waktuSifts){
//                    databaseHelper.insertJamSift(String.valueOf(waktuSift.getId()), String.valueOf(waktuSift.getOpd_id()), String.valueOf(waktuSift.getTipe()), String.valueOf(waktuSift.getInisial()), String.valueOf(waktuSift.getMasuk()), String.valueOf(waktuSift.getPulang()));
//                }
//
//                download = 7;
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<List<WaktuSift>> call, @NonNull Throwable t) {
//                pesanError();
//            }
//        });
//
//    }
//
//
//
//    public void fakeProgress(final int progress){
//
//        progressBarHorizontal.setProgress(progress);
//
//        Thread thread = new Thread(() -> {
//            try {
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            if (download == 2){
//                fakeProgress(progress+17);
//                koordinat();
//            }else if(download == 3){
//                fakeProgress(progress+17);
//                timetable(sEmployId, sToken);
//            }else if(download == 5){
//                fakeProgress(progress+17);
//                koordinat_e();
//
//            }else if (download == 4){
//                fakeProgress(progress+17);
//                kegiatan();
//            }else if(download == 1) {
//                fakeProgress(progress+17);
//                getDataPegawai(sEmployId);
//            }else if(download == 6){
//                fakeProgress(progress+17);
//                testsift();
//            }else if (download == 7){
//
//                Intent dashboardActivity = new Intent(DownloadDataActivity.this, DashboardVersiOne.class);
//                dashboardActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(dashboardActivity);
//                finish();
//
//            }
//        });
//        thread.start();
//    }
//
//
//    public void dataUser(){
//        Cursor tUser = databaseHelper.getAllData22();
//        int dataUser = 0;
//
//        while (tUser.moveToNext()){
//            dataUser +=1;
//            sId = tUser.getString(0);
//            sToken = tUser.getString(5);
//            sEmployId = tUser.getString(1);
//
//        }
//
//        if (dataUser == tUser.getCount()){
//            download =1;
//            fakeProgress(20);
//            tvinfoDownload.setText("unduh data pegawai...");
//        }
//
//    }
//
//    public void pesanError(){
//        Dialog errorDialogs = new Dialog(this, R.style.DialogStyle);
//        errorDialogs.setContentView(R.layout.view_error);
//        ImageView tvTutupDialog = errorDialogs.findViewById(R.id.tvTutupDialog);
//
//        tvTutupDialog.setOnClickListener(v -> errorDialogs.dismiss());
//
//        errorDialogs.show();
//
//        Handler handler = new Handler();
//        //your code here
//        handler.postDelayed(errorDialogs::dismiss, 2000);
//
//    }
//
//    public void viewNotifKosong( String w1, String w2, int kode){
//
//        Dialog dataKosong = new Dialog(DownloadDataActivity.this, R.style.DialogStyle);
//        dataKosong.setContentView(R.layout.view_warning_kosong);
//        TextView tvWarning1 = dataKosong.findViewById(R.id.tvWarning1);
//        ImageView tvTutupDialog = dataKosong.findViewById(R.id.tvTutupDialog);
//        tvWarning1.setText(w1+" "+w2);
//        dataKosong.setCancelable(true);
//
//        tvTutupDialog.setOnClickListener(v -> {
//
//            if (kode == 1){
//                databaseHelper.deleteDataUseAll();
//                dataKosong.dismiss();
//                finish();
//
//            }else if (kode == 2){
//                databaseHelper.deleteDataUseAll();
//                databaseHelper.deleteDataEmployeeAll();
//                dataKosong.dismiss();
//                finish();
//
//
//            }else if (kode == 3){
//
//                databaseHelper.deleteDataUseAll();
//                databaseHelper.deleteDataEmployeeAll();
//                databaseHelper.deleteDataKoordinatOPDAll();
//                dataKosong.dismiss();
//                finish();
//
//            } else if (kode == 4) {
//
//                databaseHelper.deleteDataUseAll();
//                databaseHelper.deleteDataEmployeeAll();
//                databaseHelper.deleteDataKoordinatOPDAll();
//                databaseHelper.deleteTimeTableAll();
//                dataKosong.dismiss();
//                finish();
//
//            }
//
//        });
//        dataKosong.show();
//    }
//}