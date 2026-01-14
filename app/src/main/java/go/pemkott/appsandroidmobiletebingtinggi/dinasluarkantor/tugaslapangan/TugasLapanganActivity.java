package go.pemkott.appsandroidmobiletebingtinggi.dinasluarkantor.tugaslapangan;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import go.pemkott.appsandroidmobiletebingtinggi.R;
import go.pemkott.appsandroidmobiletebingtinggi.camerax.CameraxActivity;
import go.pemkott.appsandroidmobiletebingtinggi.database.DatabaseHelper;
import go.pemkott.appsandroidmobiletebingtinggi.dialogview.DialogView;
import go.pemkott.appsandroidmobiletebingtinggi.model.Kegiatan;

public class TugasLapanganActivity extends AppCompatActivity {

    private static ArrayList<Kegiatan> list = new ArrayList<>();
    public static ArrayList<String> kegiatanChecked = new ArrayList<String>();
    private ArrayList<String>  kegiatanAdded = new ArrayList<String>();
    static ArrayList<String> kegiatansList = new ArrayList<String>();
    public static String kegiatansLainnya;
    StringBuffer buffer2;

    EditText etkegiatanLainnya;
    RecyclerView rvKegiatanPd;
    KegiatanAdapter kegiatanAdapter;
    DatabaseHelper databaseHelper;

    public static AppCompatActivity tL;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.background_color));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.background_color));

        setContentView(R.layout.activity_tugas_lapangan);

        tL = this;

        list.clear();
        kegiatansList.clear();



        databaseHelper = new DatabaseHelper(this);
        kegiatanDatabase();

        rvKegiatanPd = findViewById(R.id.rvKegiatanPd);
        etkegiatanLainnya = findViewById(R.id.etKegiatanLainnya);
        list.addAll(getListData2());

        showRecyclerList();

    }



    public void backTl(View view){
        onBackPressed();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void kegiatanDatabase(){
        Cursor res = databaseHelper.getKegiatanIzin();
        while (res.moveToNext()){
            if (res.getString(1).equals("tl")){
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

    DialogView dialogView = new DialogView(TugasLapanganActivity.this);
    public void nextKegiatanTL(View view){
        if (!etkegiatanLainnya.getText().toString().isEmpty()){
            kegiatansLainnya = etkegiatanLainnya.getText().toString();

        }else{
            kegiatansLainnya = "kosong";
        }

        if (kegiatanChecked.isEmpty() && kegiatansLainnya.equals("kosong")){
            dialogView.viewNotifKosong(TugasLapanganActivity.this, "Anda Harus Mengisi Kegiatan Yang Dilaksanakan.", "");

        }else {

            Intent intentTL = new Intent(TugasLapanganActivity.this, CameraxActivity.class);
            intentTL.putExtra("aktivitas", "tugaslapangan");
            intentTL.putExtra("title", "Isi Data Tugas Lapangan");
            startActivity(intentTL);

        }
    }

    private void showRecyclerList(){
        rvKegiatanPd.setLayoutManager(new LinearLayoutManager(this));
        kegiatanAdapter = new KegiatanAdapter(list);
        rvKegiatanPd.setAdapter(kegiatanAdapter);

        kegiatanAdapter.setOnItemClickCallback(new KegiatanAdapter.OnItemClickCallback() {
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


        }else{

            kegiatanChecked.remove(kegiatan.getKegiatan());

            StringBuffer buffer = new StringBuffer();
            if (kegiatanChecked.size() == 1){
                buffer.append(kegiatanChecked.get(kegiatanChecked.size()-1));
            }else if(kegiatanChecked.isEmpty()){
            }

            else{
                for (int i = 0; i<kegiatanChecked.size()-1;i++){
                    buffer.append(kegiatanChecked.get(i)+", ");
                }
                buffer.append(kegiatanChecked.get(kegiatanChecked.size()-1)+", ");
            }
        }

    }

}