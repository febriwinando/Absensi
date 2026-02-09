package go.pemkott.appsandroidmobiletebingtinggi.izinsift.izinsiftsakit;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import go.pemkott.appsandroidmobiletebingtinggi.R;
import go.pemkott.appsandroidmobiletebingtinggi.camerax.CameraxActivity;
import go.pemkott.appsandroidmobiletebingtinggi.database.DatabaseHelper;
import go.pemkott.appsandroidmobiletebingtinggi.izin.sakit.SakitAdapter;
import go.pemkott.appsandroidmobiletebingtinggi.model.Kegiatan;

public class SakitSiftActivity extends AppCompatActivity {

    private static ArrayList<Kegiatan> listSakit = new ArrayList<>();
    public static ArrayList<String> kegiatanCheckedSakit = new ArrayList<String>();
    private ArrayList<String>  kegiatanAddedSakit = new ArrayList<String>();
    static ArrayList<String> kegiatansListSakit = new ArrayList<String>();
    public static String kegiatansSakitLainnya;
    StringBuffer buffer2;

    EditText etkegiatanSakitLainnya;
    RecyclerView rvKegiatanSakit;
    DatabaseHelper databaseHelper;
    SakitAdapter sakitAdapter;

    public static AppCompatActivity sakit ;
    String inisialsift, tipesift, masuksift, pulangsift, idsift, rbTanggal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sakit_sift);


        sakit = this;

        listSakit.clear();
        kegiatansListSakit.clear();

        databaseHelper = new DatabaseHelper(this);
        kegiatanDatabase();

        rvKegiatanSakit = findViewById(R.id.rvKegiatanSakit);
        etkegiatanSakitLainnya = findViewById(R.id.etKegiatanSakitLainmya);
        listSakit.addAll(getListData2());

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

    public void nextKegiatanSakit(View view){

        if (!etkegiatanSakitLainnya.getText().toString().isEmpty()){
            kegiatansSakitLainnya = etkegiatanSakitLainnya.getText().toString();
        }else{
            kegiatansSakitLainnya = "kosong";
        }

        if (!kegiatanCheckedSakit.isEmpty() || !etkegiatanSakitLainnya.getText().toString().isEmpty()){
            Intent intentTL = new Intent(SakitSiftActivity.this, CameraxActivity.class);
            intentTL.putExtra("aktivitas", "shiftizinsakit");
            startActivity(intentTL);
        }else {
            showMessage("Peringatan!", "Anda Harus Mengisi Kegiatan Yang Dilaksanakan.");
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