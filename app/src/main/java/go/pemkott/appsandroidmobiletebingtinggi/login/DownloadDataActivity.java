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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import go.pemkott.appsandroidmobiletebingtinggi.NewDashboard.DashboardVersiOne;
import go.pemkott.appsandroidmobiletebingtinggi.R;
import go.pemkott.appsandroidmobiletebingtinggi.api.HttpService;
import go.pemkott.appsandroidmobiletebingtinggi.api.ResponsePOJO;
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

    private ProgressBar progressBar;
    private TextView tvInfo;

    private DatabaseHelper db;
    private DialogView dialogView;
    private HttpService api;

    private ExecutorService executor;

    private String userId;
    private String employeeId;
    private String token;
    private String opd;

    private int progressStep = 0;
    private static final int MAX_STEP = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_data);

        progressBar = findViewById(R.id.progressBarHorizontal);
        tvInfo = findViewById(R.id.tvinfoDownload);

        db = new DatabaseHelper(this);
        dialogView = new DialogView(this);
        executor = Executors.newSingleThreadExecutor();

        SessionManager session = new SessionManager(this);
        userId = session.getPegawaiId();

        api = RetroClient.getInstance2().getApi2();

        if (!NetworkUtils.isConnectedFast(this)) {
            dialogView.viewNotifKosong(this,
                    "Tidak ada koneksi internet",
                    "Silakan cek jaringan Anda");
            return;
        }

        bacaUserLokal();
    }

    /* =============================
       STEP 0 : USER LOKAL
       ============================= */
    private void bacaUserLokal() {
        Cursor c = db.getAllData22(userId);
        if (c.moveToFirst()) {
            employeeId = c.getString(1);
            token = c.getString(5);
            c.close();
            stepPegawai();
        } else {
            c.close();
            errorStop("Data user tidak ditemukan");
        }
    }

    /* =============================
       STEP 1 : DATA PEGAWAI
       ============================= */
    private void stepPegawai() {
        updateUI("Mengunduh data pegawai...");
        api.dataEmployee(employeeId).enqueue(new Callback<DataEmployee>() {
            @Override
            public void onResponse(Call<DataEmployee> call, Response<DataEmployee> res) {
                if (!res.isSuccessful() || res.body() == null) {
                    errorStop("Gagal unduh data pegawai");
                    return;
                }

                executor.execute(() -> {
                    DataEmployee d = res.body();
                    db.insertDataEmployee(
                            d.getId(), d.getAtasan_id1(), d.getAtasan_id2(),
                            d.getPosition_id(), d.getOpd_id(), d.getNip(),
                            d.getNama(), d.getEmail(), d.getNo_hp(),
                            d.getKelompok(), d.getS_jabatan(), d.getEselon(),
                            d.getJabatan(), d.getOpd(), d.getAlamat(),
                            d.getLet(), d.getLng(), d.getFoto(),
                            d.getAwal_waktu(), String.valueOf(d.getShift())
                    );
                    opd = d.getOpd_id();

                    runOnUiThread(() -> stepKoordinatOPD());
                });
            }

            @Override
            public void onFailure(Call<DataEmployee> call, Throwable t) {
                errorStop(t.getMessage());
            }
        });
    }

    /* =============================
       STEP 2 : KOORDINAT OPD
       ============================= */
    private void stepKoordinatOPD() {
        updateUI("Mengunduh koordinat OPD...");
        api.getUrlKoordinat(
                "https://absensi.tebingtinggikota.go.id/api/koordinat",
                RequestBody.create("", MediaType.parse("application/json"))
        ).enqueue(new Callback<List<Koordinat>>() {

            @Override
            public void onResponse(Call<List<Koordinat>> call, Response<List<Koordinat>> res) {
                if (!res.isSuccessful() || res.body() == null) {
                    errorStop("Gagal unduh koordinat OPD");
                    return;
                }

                executor.execute(() -> {
                    for (Koordinat k : res.body()) {
                        db.insertDataKoordinat(
                                k.getId(), k.getOpd_id(),
                                k.getAlamat(), k.getLet(), k.getLng()
                        );
                    }
                    runOnUiThread(() -> koordinat_e());
                });
            }

            @Override
            public void onFailure(Call<List<Koordinat>> call, Throwable t) {
                errorStop(t.getMessage());
            }
        });
    }


        private void koordinat_e() {

            updateUI("Mengunduh koordinat pegawai ...");
            String url = "https://absensi.tebingtinggikota.go.id/api/koordinatemployee?id=" + employeeId;

            api.getUrlKoordinat(url, "Bearer " + token,
                            RequestBody.create("", MediaType.parse("application/json")))
                    .enqueue(new Callback<List<Koordinat>>() {

                        @Override
                        public void onResponse(Call<List<Koordinat>> call, Response<List<Koordinat>> response) {
                            if (!response.isSuccessful() || response.body() == null) {
                                errorStop("Gagal unduh koordinat pegawai");
                                return;
                            }

                            executor.execute(() -> {
                                for (Koordinat koordinat : response.body()) {
                                    db.insertDataKoordinatEmployee(koordinat.getId(), employeeId, koordinat.getAlamat(), koordinat.getLet(), koordinat.getLng());
                                }
                                runOnUiThread(() -> stepTimetable());
                            });
                        }

                        @Override
                        public void onFailure(Call<List<Koordinat>> call, Throwable t) {
                            errorStop(t.getMessage());
                        }
                    });

    }

    /* =============================
       STEP 3 : TIMETABLE
       ============================= */
    private void stepTimetable() {
        updateUI("Mengunduh timetable...");
        String url = "https://absensi.tebingtinggikota.go.id/api/timetable?employee_id=" + employeeId;

        api.getUrlTimeTable(url, "Bearer " + token,
                        RequestBody.create("", MediaType.parse("application/json")))
                .enqueue(new Callback<List<TimeTables>>() {

                    @Override
                    public void onResponse(Call<List<TimeTables>> call, Response<List<TimeTables>> res) {
                        if (!res.isSuccessful() || res.body() == null) {
                            errorStop("Gagal unduh timetable");
                            return;
                        }

                        executor.execute(() -> {
                            for (TimeTables t : res.body()) {
                                db.insertDataTimeTable(
                                        String.valueOf(t.getId()),
                                        t.getEmployee_id(),
                                        t.getTimetable_id(),
                                        t.getInisial(),
                                        String.valueOf(t.getHari()),
                                        t.getMasuk(),
                                        t.getPulang()
                                );
                            }
                            runOnUiThread(() -> stepKegiatan());
                        });
                    }

                    @Override
                    public void onFailure(Call<List<TimeTables>> call, Throwable t) {
                        errorStop(t.getMessage());
                    }
                });
    }

    /* =============================
       STEP 4 : KEGIATAN
       ============================= */
    private void stepKegiatan() {
        updateUI("Mengunduh kegiatan...");
        String url = "https://absensi.tebingtinggikota.go.id/api/kegiatannew?opd=" + opd;

        api.getUrlKegiatan(url, "Bearer " + token,
                        RequestBody.create("", MediaType.parse("application/json")))
                .enqueue(new Callback<List<KegiatanIzin>>() {

                    @Override
                    public void onResponse(Call<List<KegiatanIzin>> call, Response<List<KegiatanIzin>> res) {
                        if (!res.isSuccessful() || res.body() == null) {
                            errorStop("Gagal unduh kegiatan");
                            return;
                        }

                        executor.execute(() -> {
                            for (KegiatanIzin k : res.body()) {
                                db.insertResourceKegiatan(
                                        String.valueOf(k.getId()),
                                        k.getTipe(),
                                        k.getKet()
                                );
                            }
                            runOnUiThread(() -> stepSift());
                        });
                    }

                    @Override
                    public void onFailure(Call<List<KegiatanIzin>> call, Throwable t) {
                        errorStop(t.getMessage());
                    }
                });
    }

    /* =============================
       STEP 5 : SIFT
       ============================= */
    private void stepSift() {
        updateUI("Mengunduh jam sift...");
        String url = "https://absensi.tebingtinggikota.go.id/api/testsift?eOPD=" + opd;

        api.getTestSift(url,
                        RequestBody.create("", MediaType.parse("application/json")))
                .enqueue(new Callback<List<WaktuSift>>() {

                    @Override
                    public void onResponse(Call<List<WaktuSift>> call, Response<List<WaktuSift>> res) {
                        if (!res.isSuccessful() || res.body() == null) {
                            errorStop("Gagal unduh jam sift");
                            return;
                        }

                        executor.execute(() -> {
                            for (WaktuSift w : res.body()) {
                                db.insertJamSift(
                                        String.valueOf(w.getId()),
                                        String.valueOf(w.getOpd_id()),
                                        String.valueOf(w.getTipe()),
                                        w.getInisial(),
                                        w.getMasuk(),
                                        w.getPulang()
                                );
                            }
                            runOnUiThread(() -> sendFcmTokenToServer());
                        });
                    }

                    @Override
                    public void onFailure(Call<List<WaktuSift>> call, Throwable t) {
                        errorStop(t.getMessage());
                    }
                });
    }


    private void sendFcmTokenToServer() {
        updateUI("menyelesaikan proses...");
        SessionManager session = new SessionManager(this);
        String pegawaiId = session.getPegawaiId();
        String fcmToken  = session.getFcmToken();

        if (pegawaiId == null || pegawaiId.equals("0") || fcmToken == null) {
            Log.w("FCM", "Pegawai ID / FCM Token belum siap, skip update");
            return;
        }

        Call<ResponsePOJO> call = RetroClient.getInstance()
                .getApi()
                .updateFcmToken(pegawaiId, fcmToken);

        call.enqueue(new Callback<ResponsePOJO>() {
            @Override
            public void onResponse(Call<ResponsePOJO> call, Response<ResponsePOJO> response) {
                executor.execute(() -> {
                    runOnUiThread(() -> finishFlow());
                });
            }

            @Override
            public void onFailure(Call<ResponsePOJO> call, Throwable t) {
                Log.e("FCM", "Error update token: " + t.getMessage());
            }
        });
    }


    /* =============================
       FINISH
       ============================= */
    private void finishFlow() {
        updateUI("Selesai");
        startActivity(new Intent(this, DashboardVersiOne.class));
        finish();
    }




    /* =============================
       HELPER
       ============================= */
    private void updateUI(String text) {
        progressStep++;
        tvInfo.setText(text);
        progressBar.setProgress((progressStep * 100) / MAX_STEP);
    }

    private void errorStop(String msg) {
        dialogView.viewNotifKosong(this, "Terjadi kesalahan", msg);
        if (executor != null) executor.shutdownNow();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executor != null) executor.shutdownNow();
    }
}


//public class DownloadDataActivity extends AppCompatActivity {
//    private static final String TAG = "DownloadDataActivity";
//
//    private ProgressBar progressBarHorizontal;
//    private TextView tvinfoDownload;
//    private DatabaseHelper databaseHelper;
//    private DialogView dialogView;
//    private HttpService holderAPI;
//
//    private ExecutorService dbExecutor;
//    private RequestBody emptyBody;
//
//    private String sId, sToken, sEmployId, eOPD;
//
//    // step index: 0=not started, 1=getDataPegawai,2=koordinat,3=timetable,4=kegiatan,5=koordinat_e,6=testsift,7=finished
//    private final AtomicInteger stepIndex = new AtomicInteger(0);
//    private final int TOTAL_STEPS = 6; // used for progress calculation (exclude initial read local)
//
//    SessionManager session;
//    String userId;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_download_data);
//
//        session = new SessionManager(this);
//        userId = session.getPegawaiId();
//        Toast.makeText(this, ""+userId, Toast.LENGTH_SHORT).show();
//
//        // init
//        databaseHelper = new DatabaseHelper(this);
//        dialogView = new DialogView(this);
//        dbExecutor = Executors.newSingleThreadExecutor();
//        emptyBody = RequestBody.create("", MediaType.parse("application/json"));
//
//        progressBarHorizontal = findViewById(R.id.progressBarHorizontal);
//        tvinfoDownload = findViewById(R.id.tvinfoDownload);
//
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        Window window = this.getWindow();
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        try {
//            window.setStatusBarColor(getResources().getColor(R.color.black));
//        } catch (Exception ignored) { }
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//
//        View decorView = getWindow().getDecorView();
//        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
//        decorView.setSystemUiVisibility(uiOptions);
//
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl("https://absensi.tebingtinggikota.go.id/api/")
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//
//        holderAPI = retrofit.create(HttpService.class);
//
//        if (NetworkUtils.isConnectedFast(this)) {
//            readLocalUserAndStart();
//        } else {
//            pesanError();
//        }
//    }
//
//    private void readLocalUserAndStart() {
//        Cursor tUser = databaseHelper.getAllData22(userId);
//        int dataUser = 0;
//        while (tUser.moveToNext()) {
//            dataUser++;
//            sId = tUser.getString(0);
//            sToken = tUser.getString(5);
//            sEmployId = tUser.getString(1);
//        }
//        tUser.close();
//
//        if (dataUser > 0) {
//            // start flow
//            stepIndex.set(1);
//            updateProgressText("Unduh data pegawai...");
//            proceedNext();
//        } else {
//            viewNotifKosong("Data pengguna tidak ditemukan.", "", 1);
//        }
//    }
//
//    /**
//     * Proceed to next step based on stepIndex
//     */
//    private void proceedNext() {
//        int step = stepIndex.get();
//        switch (step) {
//            case 1:
//                updateProgressText("Unduh data pegawai...");
//                getDataPegawai(sEmployId);
//                break;
//            case 2:
//                updateProgressText("Unduh data koordinat OPD...");
//                koordinat();
//                break;
//            case 3:
//                updateProgressText("Unduh data timetable...");
//                timetable(sEmployId, sToken);
//                break;
//            case 4:
//                updateProgressText("Unduh data kegiatan...");
//                kegiatan();
//                break;
//            case 5:
//                updateProgressText("Unduh koordinat pegawai...");
//                koordinat_e();
//                break;
//            case 6:
//                updateProgressText("Unduh jam sift...");
//                testsift();
//                break;
//            default:
//                goToDashboard();
//                break;
//        }
//        updateProgressBar();
//    }
//
//    private void updateProgressText(final String text) {
//        runOnUiThread(() -> tvinfoDownload.setText(text));
//    }
//
//    private void updateProgressBar() {
//        // stepIndex ranges 1..6 -> convert to 0..TOTAL_STEPS for percentage
//        int idx = Math.max(0, Math.min(stepIndex.get(), TOTAL_STEPS));
//        int progress = (int) ((idx / (float) TOTAL_STEPS) * 100);
//        runOnUiThread(() -> progressBarHorizontal.setProgress(progress));
//    }
//
//    private void goToDashboard() {
//        runOnUiThread(() -> {
//            Intent dashboardActivity = new Intent(DownloadDataActivity.this, DashboardVersiOne.class);
//            dashboardActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(dashboardActivity);
//            finish();
//        });
//    }
//
//    /* ============================
//       API calls (sequential)
//       ============================ */
//
//    private void getDataPegawai(String idE) {
//        if (idE == null || idE.isEmpty()) {
//            viewNotifKosong("ID pegawai tidak valid.", "", 1);
//            return;
//        }
//
//        // you used RetroClient.getInstance().getApi().dataEmployee(idE) originally, keep it:
//        Call<DataEmployee> calls = RetroClient.getInstance().getApi().dataEmployee(idE);
//        calls.enqueue(new Callback<DataEmployee>() {
//            @Override
//            public void onResponse(@NonNull Call<DataEmployee> call, @NonNull Response<DataEmployee> response) {
//                if (!response.isSuccessful() || response.body() == null) {
//                    viewNotifKosong("Gagal mengunduh data pegawai, periksa koneksi internet anda dan coba kembali.", "", 1);
//                    return;
//                }
//
//                DataEmployee body = response.body();
//                // insert into DB in background
//                dbExecutor.execute(() -> {
//                    boolean insertDataPegawai = databaseHelper.insertDataEmployee(
//                            body.getId(),
//                            body.getAtasan_id1(),
//                            body.getAtasan_id2(),
//                            body.getPosition_id(),
//                            body.getOpd_id(),
//                            body.getNip(),
//                            body.getNama(),
//                            body.getEmail(),
//                            body.getNo_hp(),
//                            body.getKelompok(),
//                            body.getS_jabatan(),
//                            body.getEselon(),
//                            body.getJabatan(),
//                            body.getOpd(),
//                            body.getAlamat(),
//                            body.getLet(),
//                            body.getLng(),
//                            body.getFoto(),
//                            body.getAwal_waktu(),
//                            String.valueOf(body.getShift())
//                    );
//
//                    if (insertDataPegawai) {
//                        // read OPD saved in DB
//                        Cursor dataUser = databaseHelper.getDataEmployee(sEmployId);
//                        String opd = null;
//                        int c = 0;
//                        while (dataUser.moveToNext()) {
//                            opd = dataUser.getString(4);
//                            c++;
//                        }
//                        dataUser.close();
//
//                        eOPD = opd;
//                        // move to next step on main thread
//                        runOnUiThread(() -> {
//                            stepIndex.incrementAndGet(); // -> 2
//                            proceedNext();
//                        });
//                    } else {
//                        runOnUiThread(() -> viewNotifKosong("Gagal menyimpan data pegawai.", "", 1));
//                    }
//                });
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<DataEmployee> call, @NonNull Throwable t) {
//                Log.d(TAG, "getDataPegawai onFailure: " + t.getMessage());
//                dialogView.viewNotifKosong(DownloadDataActivity.this, "Gagal memeriksa data pegawai,", "mohon periksa internet anda.");
//            }
//        });
//    }
//
//    private void koordinat() {
//        String url = "https://absensi.tebingtinggikota.go.id/api/koordinat";
//        Call<List<Koordinat>> callKoordinat = holderAPI.getUrlKoordinat(url, emptyBody);
//        callKoordinat.enqueue(new Callback<List<Koordinat>>() {
//            @Override
//            public void onResponse(@NonNull Call<List<Koordinat>> call, @NonNull Response<List<Koordinat>> response) {
//                if (!response.isSuccessful() || response.body() == null) {
//                    viewNotifKosong("Gagal mengunduh data koordinat OPD, periksa koneksi internet anda dan coba kembali.", "", 2);
//                    return;
//                }
//
//                List<Koordinat> koordinats = response.body();
//                dbExecutor.execute(() -> {
//                    for (Koordinat koordinat : koordinats) {
//                        databaseHelper.insertDataKoordinat(koordinat.getId(), koordinat.getOpd_id(), koordinat.getAlamat(), koordinat.getLet(), koordinat.getLng());
//                    }
//                    runOnUiThread(() -> {
//                        stepIndex.incrementAndGet(); // -> 3
//                        proceedNext();
//                    });
//                });
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<List<Koordinat>> call, @NonNull Throwable t) {
//                Log.d(TAG, "koordinat onFailure: " + t.getMessage());
//                dialogView.viewNotifKosong(DownloadDataActivity.this, "Gagal menerima data koordinat,", "mohon periksa jaringan internet anda.");
//            }
//        });
//    }
//
//    private void timetable(String idEmployee, String token) {
//        if (idEmployee == null || idEmployee.isEmpty()) {
//            viewNotifKosong("ID pegawai tidak valid untuk timetable.", "", 3);
//            return;
//        }
//
//        String url = "https://absensi.tebingtinggikota.go.id/api/timetable?employee_id=" + idEmployee;
//        Call<List<TimeTables>> timeTable = holderAPI.getUrlTimeTable(url, "Bearer " + token, emptyBody);
//        timeTable.enqueue(new Callback<List<TimeTables>>() {
//            @Override
//            public void onResponse(@NonNull Call<List<TimeTables>> call, @NonNull Response<List<TimeTables>> response) {
//                if (!response.isSuccessful() || response.body() == null) {
//                    viewNotifKosong("Gagal mengunduh timetable, periksa koneksi internet anda dan coba kembali.", "", 3);
//                    return;
//                }
//
//                List<TimeTables> timeTables = response.body();
//                dbExecutor.execute(() -> {
//                    for (TimeTables timeTable : timeTables) {
//                        databaseHelper.insertDataTimeTable(String.valueOf(timeTable.getId()), timeTable.getEmployee_id(), timeTable.getTimetable_id(), timeTable.getInisial(), String.valueOf(timeTable.getHari()), timeTable.getMasuk(), timeTable.getPulang());
//                    }
//                    runOnUiThread(() -> {
//                        stepIndex.incrementAndGet(); // -> 4
//                        proceedNext();
//                    });
//                });
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<List<TimeTables>> call, @NonNull Throwable t) {
//                Log.d(TAG, "timetable onFailure: " + t.getMessage());
//                pesanError();
//            }
//        });
//    }
//
//    private void kegiatan() {
//        if (eOPD == null || eOPD.isEmpty()) {
//            viewNotifKosong("OPD tidak ditemukan.", "", 4);
//            return;
//        }
//
//        String url = "https://absensi.tebingtinggikota.go.id/api/kegiatannew?opd=" + eOPD;
//        Call<List<KegiatanIzin>> callKegiatanIzins = holderAPI.getUrlKegiatan(url, "Bearer " + sToken, emptyBody);
//        callKegiatanIzins.enqueue(new Callback<List<KegiatanIzin>>() {
//            @Override
//            public void onResponse(@NonNull Call<List<KegiatanIzin>> call, @NonNull Response<List<KegiatanIzin>> response) {
//                if (!response.isSuccessful() || response.body() == null) {
//                    dialogView.viewNotifKosong(DownloadDataActivity.this, "Gagal memeriksa data kegiatan,", "mohon periksa internet anda.");
//                    return;
//                }
//
//                List<KegiatanIzin> kegiatanIzins = response.body();
//                dbExecutor.execute(() -> {
//                    for (KegiatanIzin kegiatanIzin : kegiatanIzins) {
//                        databaseHelper.insertResourceKegiatan(String.valueOf(kegiatanIzin.getId()), kegiatanIzin.getTipe(), kegiatanIzin.getKet());
//                    }
//                    runOnUiThread(() -> {
//                        stepIndex.incrementAndGet(); // -> 5
//                        proceedNext();
//                    });
//                });
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<List<KegiatanIzin>> call, @NonNull Throwable t) {
//                Log.d(TAG, "kegiatan onFailure: " + t.getMessage());
//                pesanError();
//            }
//        });
//    }
//
//    private void koordinat_e() {
//        if (sEmployId == null || sEmployId.isEmpty()) {
//            viewNotifKosong("ID pegawai tidak valid untuk koordinat pegawai.", "", 4);
//            return;
//        }
//
//        String url = "https://absensi.tebingtinggikota.go.id/api/koordinatemployee?id=" + sEmployId;
//        Call<List<Koordinat>> callKoordinates = holderAPI.getUrlKoordinat(url, emptyBody);
//        callKoordinates.enqueue(new Callback<List<Koordinat>>() {
//            @Override
//            public void onResponse(@NonNull Call<List<Koordinat>> call, @NonNull Response<List<Koordinat>> response) {
//                if (!response.isSuccessful() || response.body() == null) {
//                    viewNotifKosong("Gagal mengunduh koordinat pegawai, periksa koneksi internet anda dan coba kembali.", "", 4);
//                    return;
//                }
//
//                List<Koordinat> koordinats = response.body();
//                dbExecutor.execute(() -> {
//                    for (Koordinat koordinat : koordinats) {
//                        if ("ada".equals(koordinat.getStatus())) {
//                            databaseHelper.insertDataKoordinatEmployee(koordinat.getId(), sEmployId, koordinat.getAlamat(), koordinat.getLet(), koordinat.getLng());
//                        }
//                    }
//                    runOnUiThread(() -> {
//                        stepIndex.incrementAndGet(); // -> 6
//                        proceedNext();
//                    });
//                });
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<List<Koordinat>> call, @NonNull Throwable t) {
//                Log.d(TAG, "koordinat_e onFailure: " + t.getMessage());
//                dialogView.viewNotifKosong(DownloadDataActivity.this, "Gagal menerima data koordinat pegawai,", "mohon periksa jaringan internet anda.");
//            }
//        });
//    }
//
//    private void testsift() {
//        if (eOPD == null || eOPD.isEmpty()) {
//            // skip if no opd
//            runOnUiThread(() -> {
//                stepIndex.incrementAndGet();
//                proceedNext();
//            });
//            return;
//        }
//
//        String url = "https://absensi.tebingtinggikota.go.id/api/testsift?eOPD=" + eOPD;
//        Call<List<WaktuSift>> jadwalSiftPegawai = holderAPI.getTestSift(url, emptyBody);
//        jadwalSiftPegawai.enqueue(new Callback<List<WaktuSift>>() {
//            @Override
//            public void onResponse(@NonNull Call<List<WaktuSift>> call, @NonNull Response<List<WaktuSift>> response) {
//                if (!response.isSuccessful() || response.body() == null) {
//                    viewNotifKosong("Gagal mengunduh data sift, periksa koneksi internet anda dan coba kembali.", "", 3);
//                    return;
//                }
//
//                List<WaktuSift> waktuSifts = response.body();
//                dbExecutor.execute(() -> {
//                    for (WaktuSift waktuSift : waktuSifts) {
//                        databaseHelper.insertJamSift(String.valueOf(waktuSift.getId()), String.valueOf(waktuSift.getOpd_id()), String.valueOf(waktuSift.getTipe()), String.valueOf(waktuSift.getInisial()), String.valueOf(waktuSift.getMasuk()), String.valueOf(waktuSift.getPulang()));
//                    }
//                    runOnUiThread(() -> {
//                        stepIndex.incrementAndGet(); // -> 7 -> finish
//                        proceedNext();
//                    });
//                });
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<List<WaktuSift>> call, @NonNull Throwable t) {
//                Log.d(TAG, "testsift onFailure: " + t.getMessage());
//                pesanError();
//            }
//        });
//    }
//
//    /* ============================
//       UI helpers / error dialogs
//       ============================ */
//
//    public void pesanError() {
//        Dialog errorDialogs = new Dialog(this, R.style.DialogStyle);
//        errorDialogs.setContentView(R.layout.view_error);
//        ImageView tvTutupDialog = errorDialogs.findViewById(R.id.tvTutupDialog);
//
//        tvTutupDialog.setOnClickListener(v -> errorDialogs.dismiss());
//
//        errorDialogs.show();
//        // auto dismiss after 2s
//        tvTutupDialog.postDelayed(errorDialogs::dismiss, 2000);
//    }
//
//    public void viewNotifKosong(String w1, String w2, int kode) {
//        Dialog dataKosong = new Dialog(DownloadDataActivity.this, R.style.DialogStyle);
//        dataKosong.setContentView(R.layout.view_warning_kosong);
//        TextView tvWarning1 = dataKosong.findViewById(R.id.tvWarning1);
//        ImageView tvTutupDialog = dataKosong.findViewById(R.id.tvTutupDialog);
//        tvWarning1.setText(w1 + " " + w2);
//        dataKosong.setCancelable(true);
//
//        tvTutupDialog.setOnClickListener(v -> {
//            // cleanup based on kode
//            if (kode == 1) {
//                databaseHelper.deleteDataUseAll();
//            } else if (kode == 2) {
//                databaseHelper.deleteDataUseAll();
//                databaseHelper.deleteDataEmployeeAll();
//            } else if (kode == 3) {
//                databaseHelper.deleteDataUseAll();
//                databaseHelper.deleteDataEmployeeAll();
//                databaseHelper.deleteDataKoordinatOPDAll();
//            } else if (kode == 4) {
//                databaseHelper.deleteDataUseAll();
//                databaseHelper.deleteDataEmployeeAll();
//                databaseHelper.deleteDataKoordinatOPDAll();
//                databaseHelper.deleteTimeTableAll();
//            }
//            dataKosong.dismiss();
//            finish();
//        });
//        dataKosong.show();
//    }
//
//    @Override
//    public void onBackPressed() {
//        // allow default behavior; optionally disable while downloading
//        super.onBackPressed();
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (dbExecutor != null && !dbExecutor.isShutdown()) {
//            dbExecutor.shutdownNow();
//        }
//    }
//}
