package go.pemkott.appsandroidmobiletebingtinggi.izinsift.izinsiftpribadi;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import go.pemkott.appsandroidmobiletebingtinggi.NewDashboard.DashboardVersiOne;
import go.pemkott.appsandroidmobiletebingtinggi.R;
import go.pemkott.appsandroidmobiletebingtinggi.camerax.CameraxActivity;
import go.pemkott.appsandroidmobiletebingtinggi.database.DatabaseHelper;
import go.pemkott.appsandroidmobiletebingtinggi.izin.keperluanpribadi.KpAdapter;
import go.pemkott.appsandroidmobiletebingtinggi.kehadiransift.JadwalSiftActivity;
import go.pemkott.appsandroidmobiletebingtinggi.model.Kegiatan;

public class KeperluanPribadiSiftActivity extends AppCompatActivity {

    private static ArrayList<Kegiatan> list = new ArrayList<>();
    public static ArrayList<String> kegiatanChecked = new ArrayList<String>();
    public static ArrayList<String> kegiatansList = new ArrayList<String>();
    public static String kegiatansLainnya;
    StringBuffer buffer2;

    EditText etkegiatanKpLainnya;
    RecyclerView rvKegiatanKp;
    KpAdapter kpAdapter;
    DatabaseHelper databaseHelper;
    TextView tvlistCheckedKp;
    String jam_masuk, jam_pulang, inisialsift, tipesift, masuksift, pulangsift, idsift, rbTanggal;

    public static AppCompatActivity kp ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.background_color));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.background_color));
        setContentView(R.layout.activity_keperluan_pribadi_sift);

        jam_masuk = DashboardVersiOne.jam_masuk;
        jam_pulang = DashboardVersiOne.jam_pulang;
        rbTanggal = JadwalSiftActivity.tanggalSift;
        inisialsift = JadwalSiftActivity.inisialsift;
        idsift = JadwalSiftActivity.idsift;
        tipesift = JadwalSiftActivity.idsift;
        masuksift = JadwalSiftActivity.masuksift;
        pulangsift = JadwalSiftActivity.pulangsift;

        kp = this;

        list.clear();
        kegiatansList.clear();

        databaseHelper = new DatabaseHelper(this);
        kegiatanDatabase();

        rvKegiatanKp = findViewById(R.id.rvKegiatanKp);
        etkegiatanKpLainnya = findViewById(R.id.etKegiatanKpLainnya);
        tvlistCheckedKp = findViewById(R.id.tvlistCheckedKp);
        list.addAll(getListData2());

        showRecyclerList();

        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(uiOptions);

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });

    }


    public void backKp(View view){
        finish();
    }

    public void kegiatanDatabase(){
        Cursor res = databaseHelper.getKegiatanIzin();
        while (res.moveToNext()){
            if (res.getString(1).equals("kp")){
                kegiatansList.add(res.getString(2));
            }
        }
    }

    //    Memberikan nilai pada Model data
    static ArrayList<Kegiatan> getListData2() {
        ArrayList<Kegiatan> list = new ArrayList<>();
        list.clear();
        for (int position = 0; position < kegiatansList.size(); position++) {
            Kegiatan kegiatans = new Kegiatan();
            kegiatans.setKegiatan(kegiatansList.get(position));
            list.add(kegiatans);
        }
        return list;
    }

    public void nextKegiatanKp(View view){
        if (!etkegiatanKpLainnya.getText().toString().isEmpty()){
            kegiatansLainnya = etkegiatanKpLainnya.getText().toString();
        }else{
            kegiatansLainnya = "kosong";
        }

        if (kegiatanChecked.isEmpty() && kegiatansLainnya.equals("kosong")){
            showMessage("Peringatan!", "Anda Harus Mengisi Kegiatan Yang Dilaksanakan.");
        }else {

            Intent intentTL = new Intent(KeperluanPribadiSiftActivity.this, CameraxActivity.class);
            intentTL.putExtra("title", "Isi Data Keperluan Pribadi");
            intentTL.putExtra("aktivitas", "shiftizinkp");
            startActivity(intentTL);

        }
    }

    public void showMessage(String title, String Message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.ThemeOverlay_App_MaterialAlertDialog);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(Message);
        builder.show();
    }

    private void showRecyclerList(){
        rvKegiatanKp.setLayoutManager(new LinearLayoutManager(this));
        kpAdapter = new KpAdapter(list);
        rvKegiatanKp.setAdapter(kpAdapter);

        kpAdapter.setOnItemClickCallback(new KpAdapter.OnItemClickCallback() {
            @Override
            public void onItemClicked(Kegiatan data) {
                showSelectedKegiatan(data);
            }
        });
    }

    private void showSelectedKegiatan(Kegiatan kegiatan) {

        if (kegiatan.isChecked() == true ){
            kegiatanChecked.add(kegiatan.getKegiatan());

            buffer2 = new StringBuffer();
            for (int i = 0; i<kegiatanChecked.size()-1;i++){
                buffer2.append(kegiatanChecked.get(i)+", ");
            }
            buffer2.append(kegiatanChecked.get(kegiatanChecked.size()-1));

            tvlistCheckedKp.setText(buffer2.toString().toUpperCase());
            tvlistCheckedKp.setVisibility(View.VISIBLE);

        }else{

            kegiatanChecked.remove(kegiatan.getKegiatan());

            StringBuffer buffer = new StringBuffer();
            if (kegiatanChecked.size() == 1){
                buffer.append(kegiatanChecked.get(kegiatanChecked.size()-1));
                tvlistCheckedKp.setText(buffer.toString().toUpperCase());
                tvlistCheckedKp.setVisibility(View.VISIBLE);
            }else if(kegiatanChecked.isEmpty()){
                tvlistCheckedKp.setVisibility(View.GONE);
            }

            else{
                for (int i = 0; i<kegiatanChecked.size()-1;i++){
                    buffer.append(kegiatanChecked.get(i)+", ");
                }
                buffer.append(kegiatanChecked.get(kegiatanChecked.size()-1)+", ");
                tvlistCheckedKp.setText(buffer.toString().toUpperCase());
                tvlistCheckedKp.setVisibility(View.VISIBLE);
            }
        }

    }
}