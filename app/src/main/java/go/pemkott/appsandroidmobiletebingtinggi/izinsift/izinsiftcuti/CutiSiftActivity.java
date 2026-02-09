package go.pemkott.appsandroidmobiletebingtinggi.izinsift.izinsiftcuti;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import go.pemkott.appsandroidmobiletebingtinggi.R;
import go.pemkott.appsandroidmobiletebingtinggi.camerax.CameraxActivity;
import go.pemkott.appsandroidmobiletebingtinggi.database.DatabaseHelper;
import go.pemkott.appsandroidmobiletebingtinggi.izin.cuti.CutiAdapter;
import go.pemkott.appsandroidmobiletebingtinggi.model.Kegiatan;

public class CutiSiftActivity extends AppCompatActivity {


    public static AppCompatActivity cuti ;
    DatabaseHelper databaseHelper;

    public static ArrayList<String> kegiatanCheckedCuti = new ArrayList<String>();
    public static List<Kegiatan> listCuti = new ArrayList<>();
    static List<String> kegiatansListCuti = new ArrayList<String>();

    CutiAdapter cutiAdapter;

    EditText etkegiatanCutiLainnya;
    RecyclerView rvKegiatanCuti;

    StringBuffer buffer;

    String jam_masuk, jam_pulang, inisialsift, tipesift, masuksift, pulangsift, idsift, rbTanggal;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_cuti_sift);


        rvKegiatanCuti = findViewById(R.id.rvKegiatanCuti);
        etkegiatanCutiLainnya = findViewById(R.id.etKegiatanCutiLainnya);
        etkegiatanCutiLainnya.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        listCuti.clear();
        kegiatansListCuti.clear();
        databaseHelper = new DatabaseHelper(this);
        kegiatanDatabase();

        listCuti.addAll(getListData());
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


    public void kegiatanDatabase(){
        Cursor res = databaseHelper.getKegiatanIzin();
        while (res.moveToNext()){
            if (res.getString(1).equals("cuti")){
                kegiatansListCuti.add(res.getString(2));
            }
        }
    }

    static ArrayList<Kegiatan> getListData() {
        ArrayList<Kegiatan> list = new ArrayList<>();
        list.clear();
        for (int position = 0; position < kegiatansListCuti.size(); position++) {
            Kegiatan kegiatans = new Kegiatan();
            kegiatans.setKegiatan(kegiatansListCuti.get(position));
            list.add(kegiatans);
        }
        return list;
    }

    private void showRecyclerList(){
        rvKegiatanCuti.setLayoutManager(new LinearLayoutManager(this));
        cutiAdapter = new CutiAdapter(listCuti);
        rvKegiatanCuti.setAdapter(cutiAdapter);

        cutiAdapter.setOnItemClickCallback(new CutiAdapter.OnItemClickCallback() {
            @Override
            public void onItemClicked(Kegiatan data) {
                showSelectedKegiatan(data);
            }
        });
    }

    private void showSelectedKegiatan(Kegiatan kegiatan) {
        if (kegiatan.isChecked() == true ){
            kegiatanCheckedCuti.add(kegiatan.getKegiatan());

            buffer = new StringBuffer();
            for (int i = 0; i<kegiatanCheckedCuti.size()-1;i++){
                buffer.append(kegiatanCheckedCuti.get(i)+", ");
            }
            buffer.append(kegiatanCheckedCuti.get(kegiatanCheckedCuti.size()-1));

        }else{
            kegiatanCheckedCuti.remove(kegiatan.getKegiatan());

            StringBuffer buffer = new StringBuffer();
            if (kegiatanCheckedCuti.size() == 1){
                buffer.append(kegiatanCheckedCuti.get(kegiatanCheckedCuti.size()-1));

            }else if(kegiatanCheckedCuti.isEmpty()){

            }
            else{
                for (int i = 0; i<kegiatanCheckedCuti.size()-1;i++){
                    buffer.append(kegiatanCheckedCuti.get(i)+", ");
                }
                buffer.append(kegiatanCheckedCuti.get(kegiatanCheckedCuti.size()-1)+", ");

            }
        }

    }

    public static String kegiatansCutiLainnya;

    public void nextKegiatanCuti(View view){
        if (!etkegiatanCutiLainnya.getText().toString().isEmpty()){
            kegiatansCutiLainnya = etkegiatanCutiLainnya.getText().toString();
        }else{
            kegiatansCutiLainnya = "kosong";
        }

        if (kegiatanCheckedCuti.isEmpty() && kegiatansCutiLainnya.equals("kosong")){
            showMessage("Peringatan!", "Anda Harus Mengisi Kegiatan Yang Dilaksanakan.");
        }else {

            Intent intentTL = new Intent(CutiSiftActivity.this, CameraxActivity.class);
            intentTL.putExtra("aktivitas", "shiftizincuti");
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


    public void backCuti(View view){
        finish();
    }

}