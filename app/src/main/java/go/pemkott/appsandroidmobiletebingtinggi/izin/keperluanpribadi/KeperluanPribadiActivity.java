package go.pemkott.appsandroidmobiletebingtinggi.izin.keperluanpribadi;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.activity.OnBackPressedCallback;
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

public class KeperluanPribadiActivity extends AppCompatActivity {

    private static final ArrayList<Kegiatan> list = new ArrayList<>();
    public static final ArrayList<String> kegiatanChecked = new ArrayList<String>();
    static ArrayList<String> kegiatansList = new ArrayList<String>();
    public  static String kegiatansLainnya;
    StringBuffer buffer2;

    EditText etkegiatanKpLainnya;
    RecyclerView rvKegiatanKp;
    KpAdapter kpAdapter;
    DatabaseHelper databaseHelper;

    public static AppCompatActivity kp ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.background_color));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.background_color));
        setContentView(R.layout.activity_keperluan_pribadi);

        kp = this;

        list.clear();
        kegiatansList.clear();

        databaseHelper = new DatabaseHelper(this);
        kegiatanDatabase();

        rvKegiatanKp = findViewById(R.id.rvKegiatanKp);
        etkegiatanKpLainnya = findViewById(R.id.etKegiatanKpLainnya);
        list.addAll(getListData2());

        showRecyclerList();

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

    DialogView dialogView = new DialogView(KeperluanPribadiActivity.this);
    public void nextKegiatanKp(View view){
        if (!etkegiatanKpLainnya.getText().toString().isEmpty()){
            kegiatansLainnya = etkegiatanKpLainnya.getText().toString();
        }else{
            kegiatansLainnya = "kosong";
        }

        if (kegiatanChecked.isEmpty() && kegiatansLainnya.equals("kosong")){
            dialogView.viewNotifKosong(KeperluanPribadiActivity.this, "Anda Harus Mengisi Kegiatan Yang Dilaksanakan.", "");

        }else {

            Intent intentTL = new Intent(KeperluanPribadiActivity.this, CameraxActivity.class);
            intentTL.putExtra("aktivitas", "izinkp");
            intentTL.putExtra("title", "Isi Informasi Keperluan Pribadi");
            startActivity(intentTL);

        }
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