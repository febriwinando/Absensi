package go.pemkott.appsandroidmobiletebingtinggi.izin.sakit;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

public class SakitActivity extends AppCompatActivity {
    private static final ArrayList<Kegiatan> listSakit = new ArrayList<>();
    public static ArrayList<String> kegiatanCheckedSakit = new ArrayList<String>();
    static ArrayList<String> kegiatansListSakit = new ArrayList<String>();
    public static String kegiatansSakitLainnya;
    StringBuffer buffer2;

    EditText etkegiatanSakitLainnya;
    RecyclerView rvKegiatanSakit;
    DatabaseHelper databaseHelper;
    SakitAdapter sakitAdapter;

    public static AppCompatActivity sakit ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.background_color));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.background_color));
        setContentView(R.layout.activity_sakit);

        sakit = this;

        listSakit.clear();
        kegiatansListSakit.clear();

        databaseHelper = new DatabaseHelper(this);
        kegiatanDatabase();

        rvKegiatanSakit = findViewById(R.id.rvKegiatanSakit);
        etkegiatanSakitLainnya = findViewById(R.id.etKegiatanSakitLainmya);
        listSakit.addAll(getListData2());
        RelativeLayout backSakitIzinActivity = findViewById(R.id.rlBackSakit);
        TextView tvSelanjutnya = findViewById(R.id.tvSelanjutnya);

        tvSelanjutnya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextKegiatanSakitIzin();
            }
        });

        backSakitIzinActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        showRecyclerList();

        etkegiatanSakitLainnya.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                if (!s.toString().trim().isEmpty()){
//                    kegiatanAdded.add(etkegiatanLainnya.getText().toString());
//                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
            }
        });
    }


    static ArrayList<Kegiatan> getListData2() {
        ArrayList<Kegiatan> list = new ArrayList<>();
        for (int position = 0; position < kegiatansListSakit.size(); position++) {
            Kegiatan kegiatans = new Kegiatan();
            kegiatans.setKegiatan(kegiatansListSakit.get(position));
            list.add(kegiatans);
        }
        return list;
    }

    public void kegiatanDatabase(){
        Cursor res = databaseHelper.getKegiatanIzin();
        while (res.moveToNext()){
            if (res.getString(1).equals("sk")){
                kegiatansListSakit.add(res.getString(2));
            }
        }
    }


    DialogView dialogView = new DialogView(SakitActivity.this);
    public void nextKegiatanSakitIzin(){

        if (!etkegiatanSakitLainnya.getText().toString().isEmpty()){
            kegiatansSakitLainnya = etkegiatanSakitLainnya.getText().toString();
        }else{
            kegiatansSakitLainnya = "kosong";
        }

        if (kegiatanCheckedSakit.isEmpty() && kegiatansSakitLainnya.equals("kosong")){
            dialogView.viewNotifKosong(SakitActivity.this, "Anda Harus Mengisi Kegiatan Yang Dilaksanakan.", "");


        }else {
            Intent intentTL = new Intent(SakitActivity.this, CameraxActivity.class);
            intentTL.putExtra("lampiran", 23);
            intentTL.putExtra("aktivitas", "izinsakit");
            intentTL.putExtra("title", "Isi Data Kondisi Kesehatan");
            startActivity(intentTL);
        }
    }


    private void showRecyclerList(){
        rvKegiatanSakit.setLayoutManager(new LinearLayoutManager(this));
        sakitAdapter = new SakitAdapter(listSakit);
        rvKegiatanSakit.setAdapter(sakitAdapter);

        sakitAdapter.setOnItemClickCallback(new SakitAdapter.OnItemClickCallback() {
            @Override
            public void onItemClicked(Kegiatan data) {
                showSelectedKegiatan(data);
            }
        });
    }

    private void showSelectedKegiatan(Kegiatan kegiatan) {
        if (kegiatan.isChecked() == true ){

            kegiatanCheckedSakit.add(kegiatan.getKegiatan());

            buffer2 = new StringBuffer();
            for (int i = 0; i<kegiatanCheckedSakit.size()-1;i++){
                buffer2.append(kegiatanCheckedSakit.get(i)+", ");
            }
            buffer2.append(kegiatanCheckedSakit.get(kegiatanCheckedSakit.size()-1));


        }else{

            kegiatanCheckedSakit.remove(kegiatan.getKegiatan());

            StringBuffer buffer = new StringBuffer();
            if (kegiatanCheckedSakit.size() == 1){
                buffer.append(kegiatanCheckedSakit.get(kegiatanCheckedSakit.size()-1));
            }else if(kegiatanCheckedSakit.isEmpty()){
            }
            else{
                for (int i = 0; i<kegiatanCheckedSakit.size()-1;i++){
                    buffer.append(kegiatanCheckedSakit.get(i)+", ");
                }
                buffer.append(kegiatanCheckedSakit.get(kegiatanCheckedSakit.size()-1)+", ");
            }



        }

    }


}