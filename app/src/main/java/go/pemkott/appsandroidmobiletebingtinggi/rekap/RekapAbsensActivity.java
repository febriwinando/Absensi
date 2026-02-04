package go.pemkott.appsandroidmobiletebingtinggi.rekap;

import static go.pemkott.appsandroidmobiletebingtinggi.konstanta.TimeFormat.BULAN;
import static go.pemkott.appsandroidmobiletebingtinggi.konstanta.TimeFormat.HARI_TEXT;
import static go.pemkott.appsandroidmobiletebingtinggi.konstanta.TimeFormat.SIMPLE_FORMAT_TANGGAL;
import static go.pemkott.appsandroidmobiletebingtinggi.konstanta.TimeFormat.TAHUN;
import static go.pemkott.appsandroidmobiletebingtinggi.konstanta.TimeFormat.TANGGAL;
import static go.pemkott.appsandroidmobiletebingtinggi.konstanta.TimeFormat.bulan;
import static go.pemkott.appsandroidmobiletebingtinggi.konstanta.TimeFormat.hariText;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;

import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import go.pemkott.appsandroidmobiletebingtinggi.R;
import go.pemkott.appsandroidmobiletebingtinggi.api.HttpService;
import go.pemkott.appsandroidmobiletebingtinggi.database.DatabaseHelper;
import go.pemkott.appsandroidmobiletebingtinggi.dialogview.DialogView;
import go.pemkott.appsandroidmobiletebingtinggi.login.SessionManager;
import go.pemkott.appsandroidmobiletebingtinggi.model.RekapServer;
import go.pemkott.appsandroidmobiletebingtinggi.utils.NetworkUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RekapAbsensActivity extends AppCompatActivity {

    Dialog persyaratanDialog;
    ProgressDialog progressDialog;
    DatabaseHelper databaseHelper;
    String sEmployee_id, sToken, sVerifikator;
    HttpService holderAPI;

    RecyclerView rvRekapServer;
    RekapServerAdapter rekapServerAdapter;
    DatePickerDialog datePickerDialogMulai, datePickerDialogSampai;
    TextView tvHariMulaiRekap, tvBulanTahunMulaiRekap, tvHariSampaiRekap, tvBulanTahunSampaiRekap, tvtTampilkanRekap;
    String sTglMulai, sTglSAmpai;
    String dariTanggal, sampaiTanggal;

    ImageView ivBack;

    DialogView dialogFragment = new DialogView(RekapAbsensActivity.this);
    SessionManager session;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.background_color));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.background_color));

        setContentView(R.layout.activity_rekap_absens);

        session = new SessionManager(this);
        userId = session.getPegawaiId();


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://absensi.tebingtinggikota.go.id/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        holderAPI = retrofit.create(HttpService.class);

        rvRekapServer= findViewById(R.id.rvRekapServer);

        LinearLayout llSampaiRekap = findViewById(R.id.llSampaiRekap);
        LinearLayout llMulaiRekap = findViewById(R.id.llMulaiRekap);
        tvHariMulaiRekap = findViewById(R.id.tvHariMulaiRekap);
        tvBulanTahunMulaiRekap = findViewById(R.id.tvBulanTahunMulaiRekap);
        tvtTampilkanRekap = findViewById(R.id.tvtTampilkanRekap);

        tvBulanTahunSampaiRekap = findViewById(R.id.tvBulanTahunSampaiRekap);
        tvHariSampaiRekap = findViewById(R.id.tvHariSampaiRekap);
        ivBack = findViewById(R.id.ivBack);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String tanggal = TANGGAL.format(new Date());
        String bulan = BULAN.format(new Date());
        String tahun = TAHUN.format(new Date());
        String hari = HARI_TEXT.format(new Date());
        String infotanggal = hariText(hari)+", "+tanggal+" "+bulan(bulan)+" "+tahun;



        llMulaiRekap.setOnClickListener(v -> datePickerDialogMulai.show());

        llSampaiRekap.setOnClickListener(v -> datePickerDialogSampai.show());

        tvtTampilkanRekap.setOnClickListener(v -> {

            String toDay= SIMPLE_FORMAT_TANGGAL.format(new Date());

            if(NetworkUtils.isConnected(RekapAbsensActivity.this)){

                if (dariTanggal == null || sampaiTanggal == null){

                    dialogFragment.viewErrorKosong(RekapAbsensActivity.this);

                }else {
                    Date dari = null, sampai = null, hariIni = null;
                    try {

                        dari = SIMPLE_FORMAT_TANGGAL.parse(dariTanggal);
                        sampai = SIMPLE_FORMAT_TANGGAL.parse(sampaiTanggal);
                        hariIni = SIMPLE_FORMAT_TANGGAL.parse(toDay);

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    assert dari != null;
                    assert hariIni != null;
                    if (dari.getTime() > hariIni.getTime()){
                        dialogFragment.viewNotifKosong(RekapAbsensActivity.this, "Rekap tidak tersedia pada waktu yang anda tentukan.", "");
                    }else{
                        assert sampai != null;
                        if (sampai.getTime()<dari.getTime()){
                            Toast.makeText(RekapAbsensActivity.this, "Pemilihan tanggal tidak boleh terbalik", Toast.LENGTH_SHORT).show();
                        }else{
                            dataRekapServer(sEmployee_id, sampaiTanggal, dariTanggal);
                        }
                    }
                }
            }else{
                pesanError();
            }
        });

        databaseHelper = new DatabaseHelper(RekapAbsensActivity.this);
        dataUser();

        datePickerMulai();
        datePickerSampai();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onStart() {
        super.onStart();

        if (NetworkUtils.isConnected(RekapAbsensActivity.this)){

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_YEAR, -3);
            dataRekapServer(sEmployee_id, "", "");

        }else{
            pesanError();
        }

    }

    @Override
    public void onResume() {
        super.onResume();


    }




    private void dataUser() {

        Cursor res = databaseHelper.getAllData22(userId);
        if (res.getCount()==0){
            return;
        }

        while (res.moveToNext()){
            sEmployee_id = res.getString(1);
            sToken = res.getString(5);
            sVerifikator = res.getString(6);
        }
    }


    public void dataRekapServer(String idE, String dariTanggal, String sampaiTanggal){
        progressDialog = new ProgressDialog(RekapAbsensActivity.this, R.style.AppCompatAlertDialogStyle);
        progressDialog.setMessage("Sedang proses...");
        progressDialog.show();
        Call<List<RekapServer>> callRekapAbsensi = holderAPI.getUrlRekapServer("https://absensi.tebingtinggikota.go.id/api/rekapserver?id="+idE+"&dtgl="+dariTanggal+"&stgl="+sampaiTanggal);
        callRekapAbsensi.enqueue(new Callback<List<RekapServer>>() {
            @Override
            public void onResponse(Call<List<RekapServer>> call, Response<List<RekapServer>> response) {
                if (!response.isSuccessful()) {
                    return;
                }

                List<RekapServer> rekapServers = response.body();
                rvRekapServer.setLayoutManager(new LinearLayoutManager(RekapAbsensActivity.this));
                rekapServerAdapter = new RekapServerAdapter(rekapServers, RekapAbsensActivity.this);
                rvRekapServer.setAdapter(rekapServerAdapter);
                rekapServerAdapter.setOnItemClickCallback(data -> viewPresensi(data));

                handlerProgressDialog();
            }

            @Override
            public void onFailure(Call<List<RekapServer>> call, Throwable t) {
                pesanError();

            }
        });

    }

    public void handlerProgressDialog() {

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            //your code here
            progressDialog.dismiss();
        }, 1000);
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void viewPresensi(RekapServer data){

        persyaratanDialog = new Dialog(RekapAbsensActivity.this, R.style.DialogStyle);
        persyaratanDialog.setContentView(R.layout.view_rekap_server);
        persyaratanDialog.setCancelable(true);

        //Masuk
        TextView tvJamRekapMasuk = persyaratanDialog.findViewById(R.id.tvJamRekapMasuk);
        TextView tvFieldStatusMasuk = persyaratanDialog.findViewById(R.id.tvFieldStatusMasuk);
        TextView tvFieldStatusPulang = persyaratanDialog.findViewById(R.id.tvFieldStatusPulang);
        TextView tvStatusRekapMasuk = persyaratanDialog.findViewById(R.id.tvStatusRekapMasuk);
        TextView tvStatusRekapPulang = persyaratanDialog.findViewById(R.id.tvStatusRekapPulang);
        TextView tvKehadiranRekapMasuk = persyaratanDialog.findViewById(R.id.tvKehadiranRekapMasuk);
        TextView tvKeteranganRekapMasuk = persyaratanDialog.findViewById(R.id.tvKeteranganRekapMasuk);
        TextView tvTagLampiranRekapMasuk = persyaratanDialog.findViewById(R.id.tvTagLampiranRekapMasuk);
        TextView tvFieldRekapMasuk = persyaratanDialog.findViewById(R.id.tvFieldRekapMasuk);
        TextView tvLokasiAlamatLuarKantor = persyaratanDialog.findViewById(R.id.tvLokasiAlamatLuarKantor);
        TextView tvLokasiAlamatLuarKantorPulang = persyaratanDialog.findViewById(R.id.tvLokasiAlamatLuarKantorPulang);
        TextView tvTagFotoRekapMasuk = persyaratanDialog.findViewById(R.id.tvTagFotoRekapMasuk);
        TextView tvKehadiranlabel = persyaratanDialog.findViewById(R.id.tvKehadiranlabel);
        TextView tvLabelKehadiran = persyaratanDialog.findViewById(R.id.tvLabelKehadiran);
        ShapeableImageView ivValidasiRekapMasuk = persyaratanDialog.findViewById(R.id.ivValidasiRekapMasuk);
        ShapeableImageView ivLampiranValidasiRekapMasuk = persyaratanDialog.findViewById(R.id.ivLampiranValidasiRekapMasuk);
        LinearLayout llPulang = persyaratanDialog.findViewById(R.id.llPulang);
        ImageView ivTutupViewListKehdiran = persyaratanDialog.findViewById(R.id.ivTutupViewListKehdiran);
        //Pulang
        TextView tvJamRekapPulang = persyaratanDialog.findViewById(R.id.tvJamRekapPulang);
        TextView tvKehadiranRekapPulang = persyaratanDialog.findViewById(R.id.tvKehadiranRekapPulang);
        TextView tvTagLampiranRekapPulang = persyaratanDialog.findViewById(R.id.tvTagLampiranRekapPulang);
        TextView tvFieldRekapPulang = persyaratanDialog.findViewById(R.id.tvFieldRekapPulang);
        TextView tvTagFotoRekapPulang = persyaratanDialog.findViewById(R.id.tvTagFotoRekapPulang);
        TextView tvKeteranganRekapPulang = persyaratanDialog.findViewById(R.id.tvKeteranganRekapPulang);
        TableRow trAktivitasPulang = persyaratanDialog.findViewById(R.id.trAktivitasPulang);
        WebView webview = persyaratanDialog.findViewById(R.id.webview);
        WebView webviewpulang = persyaratanDialog.findViewById(R.id.webviewpulang);
        ShapeableImageView ivAktivitasRekapPulang = persyaratanDialog.findViewById(R.id.ivAktivitasRekapPulang);
        ShapeableImageView ivLampiranRekapPulang = persyaratanDialog.findViewById(R.id.ivLampiranRekapPulang);

//        tvAktivitasRekapMasuk.setText(data.getKet_masuk());

        if (data.getJam_masuk().isEmpty() || data.getJam_masuk() == null){
            tvJamRekapMasuk.setText("--:--");
        }else{
            tvJamRekapMasuk.setText(data.getJam_masuk());
        }

        if (data.getJam_pulang() == null || data.getJam_pulang().isEmpty()){
            tvJamRekapPulang.setText("--:--");
        }else{
            tvJamRekapPulang.setText(data.getJam_pulang());
        }

        if (data.getLat_masuk() == null || data.getLat_masuk().isEmpty()){
            tvLokasiAlamatLuarKantor.setText("-");
        }
        else{
            tvLokasiAlamatLuarKantor.setText(getAddress(RekapAbsensActivity.this, Double.parseDouble( data.getLat_masuk()), Double.parseDouble(data.getLng_masuk())));
        }

        if (data.getLat_pulang() == null || data.getLat_pulang().isEmpty()){
            tvLokasiAlamatLuarKantorPulang.setText("-");
        }
        else{
            tvLokasiAlamatLuarKantorPulang.setText(getAddress(RekapAbsensActivity.this, Double.parseDouble( data.getLat_pulang()), Double.parseDouble(data.getLng_pulang())));
        }

        if (data.getValidasi_masuk() == 0){
            tvStatusRekapMasuk.setText("Belum Verifikasi Tahap 1");
        } else if (data.getValidasi_masuk() == 1) {
            tvStatusRekapMasuk.setText("Belum Verifikasi Tahap 2");
        } else if (data.getValidasi_masuk() == 3) {
            tvStatusRekapMasuk.setText("Kegiatan ditolak verifikator 1");
        } else if (data.getValidasi_masuk() == 4 ) {
            tvStatusRekapMasuk.setText("Kegiatan ditolak verifikator 2");
        }else if (data.getValidasi_masuk() == 2){
            tvStatusRekapMasuk.setText("Terverifikasi");
        }else{
            tvStatusRekapMasuk.setText("-");
        }

        if (data.getValidasi_pulang() == 0){
            tvStatusRekapPulang.setText("Belum Verifikasi Tahap 1");
        } else if (data.getValidasi_pulang() == 1) {
            tvStatusRekapPulang.setText("Belum Verifikasi Tahap 2");
        } else if (data.getValidasi_pulang() == 3) {
            tvStatusRekapPulang.setText("Kegiatan ditolak verifikator 1");
        } else if (data.getValidasi_pulang() == 4 ) {
            tvStatusRekapPulang.setText("Kegiatan ditolak verifikator 2");
        }else if (data.getValidasi_pulang() == 2){
            tvStatusRekapPulang.setText("Terverifikasi");
        } else{
            tvStatusRekapPulang.setText("-");
        }

        if (data.getStatus_masuk().equals("hadir")){
            if (data.getPosisi_masuk().equals("tl-masuk")){
                tvKehadiranRekapMasuk.setText("Tugas lapangan");
            }else{
                tvKehadiranRekapMasuk.setText("Masuk Kantor");
            }
        } else if (data.getStatus_masuk().equals("perjalanan dinas")) {
            tvKehadiranRekapMasuk.setText("Perjalanan Dinas");
        } else if (data.getStatus_masuk().equals("izin")) {
            if (data.getPosisi_masuk().equals("ct") || data.getPosisi_masuk().equals("cuti")){
                tvKehadiranRekapMasuk.setText("Cuti");
            } else if (data.getPosisi_masuk().equals("sk") || data.getPosisi_masuk().equals("sakit")) {
                tvKehadiranRekapMasuk.setText("Izin dengan kondisi sakit");
            } else if (data.getPosisi_masuk().equals("kp")) {
                tvKehadiranRekapMasuk.setText("Izin dengan keperluan pribadi");
            }
        }else{
            tvKehadiranRekapMasuk.setText("-");
        }

        if (data.getStatus_pulang().equals("hadir")){
            if (data.getPosisi_pulang().equals("tl-pulang")){
                tvKehadiranRekapPulang.setText("Tugas lapangan");
            }else{
                tvKehadiranRekapPulang.setText("Pulang Kantor");
            }
        } else if (data.getStatus_pulang().equals("perjalanan dinas")) {
            tvKehadiranRekapPulang.setText("Perjalanan Dinas");
        } else if (data.getStatus_pulang().equals("izin")) {
            if (data.getPosisi_pulang().equals("ct") || data.getPosisi_pulang().equals("cuti")){
                tvKehadiranRekapPulang.setText("Cuti");
            } else if (data.getPosisi_pulang().equals("sk") || data.getPosisi_pulang().equals("sakit")) {
                tvKehadiranRekapPulang.setText("Izin dengan kondisi sakit");
            } else if (data.getPosisi_pulang().equals("kp")) {
                tvKehadiranRekapPulang.setText("Izin dengan keperluan pribadi");
            }
        }else{
            tvKehadiranRekapPulang.setText("-");
        }

        tvKeteranganRekapMasuk.setText(data.getKet_masuk());
        tvKeteranganRekapPulang.setText(data.getKet_pulang());

        if (data.getPhoto_tagging_masuk() ==  null){
            ivValidasiRekapMasuk.setVisibility(View.GONE);
            tvTagFotoRekapMasuk.setVisibility(View.GONE);
        }else{
            Glide.with(this)
                    .load( "https://absensi.tebingtinggikota.go.id/uploads-img-absensi/" + data.getPhoto_tagging_masuk())
                    .into( ivValidasiRekapMasuk );
        }

        if (data.getLampiran_masuk() == null ){
            ivLampiranValidasiRekapMasuk.setVisibility(View.GONE);
            tvTagLampiranRekapMasuk.setVisibility(View.GONE);
        }else{
            if (data.getEkstensi_masuk().equals("img")){

                Glide.with(this)
                        .load( "https://absensi.tebingtinggikota.go.id/uploads-img-lampiran/" + data.getLampiran_masuk())
                        .into( ivLampiranValidasiRekapMasuk );

                ivLampiranValidasiRekapMasuk.setVisibility(View.VISIBLE);
            }else{
                if(NetworkUtils.isConnected(RekapAbsensActivity.this)){
                    webview.setVisibility(View.VISIBLE);
                    ivLampiranValidasiRekapMasuk.setVisibility(View.GONE);
                    String pdfurl="https://absensi.tebingtinggikota.go.id/uploads-img-lampiran/"+data.getLampiran_masuk();
                    webview.loadUrl("https://docs.google.com/gview?embedded=true&url="+pdfurl);
                    webview.setWebViewClient(new WebViewClient());
                    webview.getSettings().setSupportZoom(true);
                    webview.getProgress();
                    webview.getSettings().setJavaScriptEnabled(true);
                }
            }
        }

        if (data.getPhoto_tagging_pulang() ==  null){
            ivAktivitasRekapPulang.setVisibility(View.GONE);
            tvTagFotoRekapPulang.setVisibility(View.GONE);

        }else{
            Glide.with(this)
                    .load( "https://absensi.tebingtinggikota.go.id/uploads-img-absensi/" + data.getPhoto_tagging_pulang())
                    .into( ivAktivitasRekapPulang );
        }

        if (data.getLampiran_pulang() == null ){

            ivLampiranRekapPulang.setVisibility(View.GONE);
            tvTagLampiranRekapPulang.setVisibility(View.GONE);

        }else{
            if (data.getEkstensi_pulang().equals("img")){
                Glide.with(this)
                        .load( "https://absensi.tebingtinggikota.go.id/uploads-img-lampiran/" + data.getLampiran_pulang())
                        .into( ivLampiranRekapPulang );
                ivLampiranRekapPulang.setVisibility(View.VISIBLE);
            }else{
                if (NetworkUtils.isConnected(RekapAbsensActivity.this)){
                    webviewpulang.setVisibility(View.VISIBLE);
                    ivLampiranRekapPulang.setVisibility(View.GONE);
                    String pdfurl="https://absensi.tebingtinggikota.go.id/uploads-img-lampiran/"+data.getLampiran_pulang();
                    webviewpulang.loadUrl("https://docs.google.com/gview?embedded=true&url="+pdfurl);
                    webviewpulang.setWebViewClient(new WebViewClient());
                    webviewpulang.getSettings().setSupportZoom(true);
                    webviewpulang.getProgress();
                    webviewpulang.getSettings().setJavaScriptEnabled(true);
                }

            }
        }



        ivTutupViewListKehdiran.setOnClickListener(v -> persyaratanDialog.dismiss());
        persyaratanDialog.show();

    }

    public void pesanError(){
        Dialog errorDialogs = new Dialog(RekapAbsensActivity.this, R.style.DialogStyle);
        errorDialogs.setContentView(R.layout.view_error);
        TextView tvTutupDialog = errorDialogs.findViewById(R.id.tvTutupDialog);

        tvTutupDialog.setOnClickListener(v -> errorDialogs.dismiss());

        errorDialogs.show();

        Handler handler = new Handler();
        //your code here
        handler.postDelayed(errorDialogs::dismiss, 2000);

    }

    String add;
    public String getAddress(Context context, double lat, double lng) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            if(lat == 0 || lng == 0){
                return "-";
            }

            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);

            add = obj.getAddressLine(0);

            return add;

        } catch (IOException e) {
            e.printStackTrace();

            return "-";
        }
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
            assert drTgl != null;
            dariTanggal = SIMPLE_FORMAT_TANGGAL.format(drTgl);

            tvHariMulaiRekap.setText(String.valueOf(hari));
            tvBulanTahunMulaiRekap.setText(date);
            sTglMulai = tahun+"-"+bulan+"-"+hari;
        };

        Calendar calendar = Calendar.getInstance();
        int tahun = calendar.get(Calendar.YEAR);
        int bulan = calendar.get(Calendar.MONTH);
        int hari = calendar.get(Calendar.DAY_OF_MONTH);

        datePickerDialogMulai = new DatePickerDialog(RekapAbsensActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog, dateSetListener, tahun, bulan, hari);
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
            assert spTgl != null;
            sampaiTanggal = SIMPLE_FORMAT_TANGGAL.format(spTgl);

            tvHariSampaiRekap.setText(String.valueOf(hari));
            tvBulanTahunSampaiRekap.setText(date);
            sTglSAmpai = tahun+"-"+bulan+"-"+hari;
        };

        Calendar calendar = Calendar.getInstance();
        int tahun = calendar.get(Calendar.YEAR);
        int bulan = calendar.get(Calendar.MONTH);
        int hari = calendar.get(Calendar.DAY_OF_MONTH);

//        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialogSampai = new DatePickerDialog(RekapAbsensActivity.this, android.R.style.Theme_DeviceDefault_Light_Dialog, dateSetListener, tahun, bulan, hari);
    }

    private String makeDateString(int tahun, int bulan){
        return formatBulan(bulan)+" "+tahun;
    }

    private String formatBulan(int bulan){
        if (bulan == 1 ){
            return "JAN";
        }else if (bulan == 2){
            return "FEB";
        }else if (bulan == 3){
            return "MAR";
        }else if (bulan == 4){
            return "APR";
        }else if (bulan == 5){
            return "MEI";
        }else if (bulan == 6){
            return "JUN";
        }else if (bulan == 7){
            return "JUL";
        }else if (bulan == 8){
            return "AGU";
        }else if (bulan == 9){
            return "SEP";
        }else if (bulan == 10){
            return "OKT";
        }else if (bulan == 11){
            return "NOV";
        }else {
            return "DES";
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}