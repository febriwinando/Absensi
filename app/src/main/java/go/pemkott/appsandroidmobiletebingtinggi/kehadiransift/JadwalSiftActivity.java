package go.pemkott.appsandroidmobiletebingtinggi.kehadiransift;

import static go.pemkott.appsandroidmobiletebingtinggi.konstanta.TimeFormat.BULAN;
import static go.pemkott.appsandroidmobiletebingtinggi.konstanta.TimeFormat.SIMPLE_FORMAT_JAM;
import static go.pemkott.appsandroidmobiletebingtinggi.konstanta.TimeFormat.SIMPLE_FORMAT_TANGGAL;
import static go.pemkott.appsandroidmobiletebingtinggi.konstanta.TimeFormat.TAHUN;
import static go.pemkott.appsandroidmobiletebingtinggi.konstanta.TimeFormat.bulan;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import go.pemkott.appsandroidmobiletebingtinggi.R;
import go.pemkott.appsandroidmobiletebingtinggi.api.HttpService;
import go.pemkott.appsandroidmobiletebingtinggi.camerax.CameraxActivity;
import go.pemkott.appsandroidmobiletebingtinggi.database.DatabaseHelper;
import go.pemkott.appsandroidmobiletebingtinggi.dialogview.DialogView;
import go.pemkott.appsandroidmobiletebingtinggi.konstanta.TimeFormat;
import go.pemkott.appsandroidmobiletebingtinggi.model.JadwalSift;
import go.pemkott.appsandroidmobiletebingtinggi.model.WaktuSift;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class JadwalSiftActivity extends AppCompatActivity {
    private RecyclerView rvJadwalSifting;
    private ArrayList<JadwalSift> listJadwalSift = new ArrayList<>();
    private String title = "Mode List";
    DatabaseHelper databaseHelper;
    ShimmerFrameLayout shimmerJadwalSift;

    static ArrayList<String> idJadwal = new ArrayList<String>();
    static ArrayList<String> employeJadwalSift = new ArrayList<String>();
    static ArrayList<String> idSiftJadwal = new ArrayList<String>();
    static ArrayList<String> tanggalJadwal = new ArrayList<String>();

    static ArrayList<String> idSift = new ArrayList<>();
    static ArrayList<String> opdSift = new ArrayList<String>();
    static ArrayList<String> tipeSift = new ArrayList<String>();
    static ArrayList<String> inisialSift = new ArrayList<String>();
    static ArrayList<String> masukSift = new ArrayList<String>();
    static ArrayList<String> pulangSift = new ArrayList<String>();
    public static String jam_masuk, jam_pulang;
    public static AppCompatActivity jadwalSiftActivity ;
    ImageView ivSyncJadwalSift;
    String bulan = BULAN.format(new Date());
    String tahun = TAHUN.format(new Date());
    HttpService holderAPI;

    public static String tanggalSift=  null, inisialsift = null, tipesift = null, masuksift = null, pulangsift = null, idsift = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.background_color));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.background_color));

        setContentView(R.layout.activity_jadwal_sift);


        jadwalSiftActivity = this;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://absensi.tebingtinggikota.go.id/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        holderAPI = retrofit.create(HttpService.class);
        Bundle intent = getIntent().getExtras();
        jam_masuk = intent.getString("jam_masuk");
        jam_pulang = intent.getString("jam_pulang");
        ImageView ivBack = findViewById(R.id.ivBack);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        TextView titleSift = findViewById(R.id.titleSift);
        ivSyncJadwalSift = findViewById(R.id.ivSyncJadwalSift);
        rvJadwalSifting = findViewById(R.id.rvJadwalSifting);
        rvJadwalSifting.setHasFixedSize(true);
        shimmerJadwalSift = findViewById(R.id.shimmerJadwalSift);
        databaseHelper = new DatabaseHelper(this);




        String title = "Jadwal "+bulan(BULAN.format(new Date()))+" "+TAHUN.format(new Date());
        titleSift.setText(title);

        ivSyncJadwalSift.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unduhDataSiftOPD();
            }
        });

        getData(TimeFormat.ambilbulanjadwal(bulan), bulan, String.valueOf(Integer.parseInt(tahun)-1), tahun);
    }



    public void unduhDataSiftOPD(){
        databaseHelper.deleteJamSift();
        Call<List<WaktuSift>> jadwalSiftPegawai = holderAPI.getTestSift("https://absensi.tebingtinggikota.go.id/api/testsift?eOPD="+eOPD);
        jadwalSiftPegawai.enqueue(new Callback<List<WaktuSift>>() {
            @Override
            public void onResponse(@NonNull Call<List<WaktuSift>> call, @NonNull Response<List<WaktuSift>> response) {

                if (!response.isSuccessful()){
                    dialogView.viewNotifKosong(JadwalSiftActivity.this, "Gagal mengunduh data, periksa koneksi internet anda dan coba kembali.", "");
                    return;
                }

                List<WaktuSift> waktuSifts = response.body();
                int jumlahdata = 0;
                for(WaktuSift waktuSift : waktuSifts){
                    databaseHelper.insertJamSift(String.valueOf(waktuSift.getId()), String.valueOf(waktuSift.getOpd_id()), String.valueOf(waktuSift.getTipe()), String.valueOf(waktuSift.getInisial()), String.valueOf(waktuSift.getMasuk()), String.valueOf(waktuSift.getPulang()));
                    jumlahdata += 1;
                }

                if (jumlahdata == waktuSifts.size()){
                    unduhJadwalSift(1);
                }

            }

            @Override
            public void onFailure(@NonNull Call<List<WaktuSift>> call, @NonNull Throwable t) {
                dialogView.viewNotifKosong(JadwalSiftActivity.this, "Gagal mengunduh data, periksa koneksi internet anda dan coba kembali.", "");
            }
        });

    }
    String sEmployeID, sUsername, sAkses, sActive, eOPD, eKelompok, eJabatan, latOffice, lngOffice;

    public void getData(String bulansebelum, String bulan, String tahunsebelum, String tahun){

        idJadwal.clear();
        employeJadwalSift.clear();
        tanggalJadwal.clear();
        idSiftJadwal.clear();

        idSift.clear();
        opdSift.clear();
        tipeSift.clear();
        inisialSift.clear();
        masukSift.clear();
        pulangSift.clear();
        listJadwalSift.clear();

        Cursor res = databaseHelper.getAllData22();
        if (res.getCount() == 0) {
            return;
        }

        while (res.moveToNext()) {
            sEmployeID = res.getString(1);
            sUsername = res.getString(2);
            sAkses = res.getString(2);
            sActive = res.getString(4);
        }

        Cursor employe = databaseHelper.getDataEmployee(sEmployeID);
        while (employe.moveToNext()){
            eOPD = employe.getString(4);
            eKelompok = employe.getString(9);
            eJabatan = employe.getString(11);
            latOffice = employe.getString(15);
            lngOffice = employe.getString(16);
        }

        Cursor resJadwalSift = databaseHelper.getJadwalSifts2(sEmployeID, bulansebelum, bulan, tahunsebelum, tahun);
        if (resJadwalSift.getCount() > 0){
            ivSyncJadwalSift.setVisibility(View.VISIBLE);
        }else{
            ivSyncJadwalSift.setVisibility(View.GONE);
        }

        while (resJadwalSift.moveToNext()){
            idJadwal.add(resJadwalSift.getString(0));
            employeJadwalSift.add(resJadwalSift.getString(1));
            idSiftJadwal.add(resJadwalSift.getString(2));
            tanggalJadwal.add(resJadwalSift.getString(3));
        }


        Cursor resJamSift = databaseHelper.getJamSift(eOPD);

        while (resJamSift.moveToNext()){
            idSift.add(resJamSift.getString(0));
            opdSift.add(resJamSift.getString(1));
            tipeSift.add(resJamSift.getString(2));
            inisialSift.add(resJamSift.getString(3));
            masukSift.add(resJamSift.getString(4));
            pulangSift.add(resJamSift.getString(5));

        }

        listJadwalSift.addAll(getJadwalSift());
        showRecyclerGrid();
        handlerProgressDialog();;
    }

    public void handlerProgressDialog() {
        shimmerJadwalSift.stopShimmer();
        shimmerJadwalSift.hideShimmer();
    }

    ArrayList<JadwalSift> getJadwalSift() {
        ArrayList<JadwalSift> listJadwal = new ArrayList<>();
        listJadwal.clear();

        for (int position = 0; position < idJadwal.size(); position++) {

            JadwalSift jadwalSift = new JadwalSift();
            jadwalSift.setId(idJadwal.get(position));
            jadwalSift.setEmployee_id(employeJadwalSift.get(position));
            jadwalSift.setShift_id(idSiftJadwal.get(position));
            jadwalSift.setTanggal(tanggalJadwal.get(position));
            listJadwal.add(jadwalSift);
        }
        return listJadwal;
    }

    ArrayList<WaktuSift> getJamSift() {
        ArrayList<WaktuSift> waktuSifts = new ArrayList<>();
        waktuSifts.clear();

        for (int position = 0; position < idSift.size(); position++) {

            WaktuSift waktuSift = new WaktuSift();
            waktuSift.setId(idSift.get(position));
            waktuSift.setOpd_id(opdSift.get(position));
            waktuSift.setTipe(tipeSift.get(position));
            waktuSift.setInisial(inisialSift.get(position));
            waktuSift.setMasuk(masukSift.get(position));
            waktuSift.setPulang(pulangSift.get(position));

            waktuSifts.add(waktuSift);
        }
        return waktuSifts;
    }

    DialogView dialogView = new DialogView(this);
    private void showRecyclerGrid(){
        rvJadwalSifting.setLayoutManager(new GridLayoutManager(this, 4));
        GridJadwalSiftAdapter gridJadwal = new GridJadwalSiftAdapter(JadwalSiftActivity.this, listJadwalSift, getJamSift());
        rvJadwalSifting.setAdapter(gridJadwal);

        gridJadwal.setOnItemClickCallback(new GridJadwalSiftAdapter.OnItemClickCallback() {
            @Override
            public void onItemClicked(String s) {

                tanggalSift = s;

                for (int i=0 ;i<listJadwalSift.size();i++){
                    if (tanggalJadwal.get(i).equals(s)){
                        for (int j = 0 ; j<idSift.size();j++){
                            if (idSiftJadwal.get(i).equals(idSift.get(j))){
                                idsift = idSift.get(j);
                                inisialsift = inisialSift.get(j);
                                tipesift = tipeSift.get(j);
                                masuksift = masukSift.get(j);
                                pulangsift = pulangSift.get(j);

                            }
                        }
                    }
                }
                String tanggal = SIMPLE_FORMAT_TANGGAL.format(new Date());
                Date hariini = null;
                try {
                    hariini = SIMPLE_FORMAT_TANGGAL.parse(tanggal);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(hariini);
                calendar.add(Calendar.DAY_OF_YEAR, -1);
                Date newDate = calendar.getTime();
                String infoJadwalhariini = SIMPLE_FORMAT_TANGGAL.format(newDate);

                String jamSekarangString = SIMPLE_FORMAT_JAM.format(new Date());
                Date jamSekarang = null, batasJamAbsenMalam = null, jadwalAbsensetelah = null;
                try {
                    batasJamAbsenMalam = SIMPLE_FORMAT_JAM.parse("12:00");
                    jamSekarang = SIMPLE_FORMAT_JAM.parse(jamSekarangString);
                    jadwalAbsensetelah = SIMPLE_FORMAT_TANGGAL.parse(s);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (infoJadwalhariini.equals(s)){

                    if (tipesift.equals("malam")){

                        if (jamSekarang.getTime()> batasJamAbsenMalam.getTime()){
                            Toast.makeText(JadwalSiftActivity.this, "Batas waktu melakukan absen sift malam telah lewat", Toast.LENGTH_SHORT).show();
                        }else{
                            if (jam_masuk != null && jam_pulang != null){
                                dialogView.viewNotifKosong(JadwalSiftActivity.this, "Anda Sudah mengisi absen masuk dan absen pulang untuk jadwal tanggal "+s,"");
                            }else{
                                viewinfo();
                            }
                        }
                    }

                }else if(jadwalAbsensetelah.getTime() > hariini.getTime()){
                    Toast.makeText(JadwalSiftActivity.this, "Belum dapat melakukan absen", Toast.LENGTH_SHORT).show();
                }else if(tanggal.equals(s)){
                    if (tipesift.equals("malam")) {
                        if (jamSekarang.getTime() >= batasJamAbsenMalam.getTime()) {
                            if (jam_masuk != null && jam_pulang != null){
                                dialogView.viewNotifKosong(JadwalSiftActivity.this, "Anda Sudah mengisi absen masuk dan absen pulang untuk jadwal tanggal "+s,"");
                            }else{
                                viewinfo( );
                            }
                        } else {
                            dialogView.viewNotifKosong(JadwalSiftActivity.this, "Sesi jadwal malam tanggal " + infoJadwalhariini + " masih berlangsung sampai pukul 12:00.", "");
                        }
                    } else {
                        if (jam_masuk != null && jam_pulang != null){
                            dialogView.viewNotifKosong(JadwalSiftActivity.this, "Anda Sudah mengisi absen masuk dan absen pulang untuk jadwal tanggal "+s,"");
                        }else{
                            viewinfo();
                        }
                    }
                }
            }
        });


    }


    public void viewinfo(){

        Dialog dialoginfo = new Dialog(JadwalSiftActivity.this, R.style.DialogStyle);
        dialoginfo.setContentView(R.layout.view_info_jadwal_sift);
        dialoginfo.setCancelable(true);

        TextView txtAbsen = dialoginfo.findViewById(R.id.txtAbsen);
        TextView jamSiftMasuk = dialoginfo.findViewById(R.id.jamSiftMasuk);
        TextView jamSiftPulang = dialoginfo.findViewById(R.id.jamSiftPulang);
        ImageView ivTutupViewInfoSift = dialoginfo.findViewById(R.id.ivTutupViewInfoSift);

        jamSiftMasuk.setText(masuksift);
        if (tipeSift.equals("malam")){
            jamSiftPulang.setText(pulangsift+"\n"+tanggalSift);
        }else{
            jamSiftPulang.setText(pulangsift);
        }

        txtAbsen.setOnClickListener(view -> {
            Intent absensift = new Intent(JadwalSiftActivity.this, CameraxActivity.class);
            absensift.putExtra("aktivitas", "kehadiransift");
            startActivity(absensift);
        });

        ivTutupViewInfoSift.setOnClickListener(view -> dialoginfo.dismiss());
        dialoginfo.show();

    }

    public void unduhJadwalSift(int status){
        Dialog dialogproses = new Dialog(JadwalSiftActivity.this, R.style.DialogStyle);
        dialogproses.setContentView(R.layout.view_proses);
        dialogproses.setCancelable(false);

        if (status == 1){
            databaseHelper.deleteJadwalSift(sEmployeID, bulan, tahun);
        }

        Call<ArrayList<JadwalSift>> jadwalSiftPegawai = holderAPI.getJadwalSifts("https://absensi.tebingtinggikota.go.id/api/jadwalsift?ide="+sEmployeID+"&bulan="+bulan+"&tahun="+tahun);
        jadwalSiftPegawai.enqueue(new Callback<ArrayList<JadwalSift>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<JadwalSift>> call, @NonNull Response<ArrayList<JadwalSift>> response) {
                if (!response.isSuccessful()){
                    dialogView.viewNotifKosong(JadwalSiftActivity.this, "Gagal mengunduh Jadwal Sift.","Silahkan coba kembali.");
                    dialogproses.dismiss();
                    return;
                }

                ArrayList<JadwalSift> jadwalSifts = response.body();
                if (jadwalSifts.size() == 0){
                    dialogproses.dismiss();

                    dialogView.viewNotifKosong(JadwalSiftActivity.this, "Jadwal belum tersedia, harap hubungi admin OPD anda.", "");
                }else{
                    dialogproses.dismiss();

                    int jlhJadwalSift = 0;
                    for(JadwalSift jadwalSift : jadwalSifts){
                        databaseHelper.insertJadwalSift(jadwalSift.getId(), jadwalSift.getEmployee_id(), jadwalSift.getShift_id(), jadwalSift.getTanggal());
                        jlhJadwalSift += 1;
                    }

                    if (jlhJadwalSift ==  jadwalSifts.size()){
                        getData(TimeFormat.ambilbulanjadwal(bulan), bulan, String.valueOf(Integer.parseInt(tahun)-1), tahun);;


                    }
                }


            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<JadwalSift>> call, @NonNull Throwable t) {
                dialogproses.dismiss();
                dialogView.viewNotifKosong(JadwalSiftActivity.this, "Gagal mengakses server.", "Silahkan coba kembali.");

            }
        });


        dialogproses.show();
    }

}