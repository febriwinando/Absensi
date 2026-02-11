package go.pemkott.appsandroidmobiletebingtinggi;

import static go.pemkott.appsandroidmobiletebingtinggi.NewDashboard.DashboardVersiOne.dashboardVersiOne;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import go.pemkott.appsandroidmobiletebingtinggi.api.ApiAddProduk;
import go.pemkott.appsandroidmobiletebingtinggi.api.HttpService;
import go.pemkott.appsandroidmobiletebingtinggi.api.RetrofitBuilder;
import go.pemkott.appsandroidmobiletebingtinggi.database.DatabaseHelper;
import go.pemkott.appsandroidmobiletebingtinggi.dialogview.DialogView;
import go.pemkott.appsandroidmobiletebingtinggi.login.LoginActivity;
import go.pemkott.appsandroidmobiletebingtinggi.login.SessionManager;
import go.pemkott.appsandroidmobiletebingtinggi.model.Updatep;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProfileActivity extends AppCompatActivity {


    RelativeLayout backHome;
    HttpService holderAPI;
    DatabaseHelper databaseHelper;
    String sNama, sNIP, sEmail, sNoHpEmployee, sKelompok, sJabatan, sKantor, sKeluar, sOPD;
    TextView tvNamaPegawai, tvNip, tvEmailEmploy, tvNoHpEmploye, tvKelompokEmploy, tvJabatanEmploy, tvDinasEmploy;
    String fotoProfileUpdate;

    String sEmployee_id, sAkses, sActive, sToken, fotoProfile;
    TableRow trGantiPassword, trPanduanAplikasi;
    LinearLayout llKeluarProfil;
    CircleImageView civProfilPegawai;
    ImageView ivBack;
    SessionManager session;
    String userId;
    ApiAddProduk api;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        session = new SessionManager(this);
        userId = session.getPegawaiId();

        api = RetrofitBuilder.getClient().create(ApiAddProduk.class);

        civProfilPegawai = findViewById(R.id.civProfilPegawai);
        tvNamaPegawai = findViewById(R.id.tvNamaPegawai);
        tvNip = findViewById(R.id.tvNip);

        tvEmailEmploy = findViewById(R.id.tvEmailEmploy);
        tvNoHpEmploye = findViewById(R.id.tvNoHpEmploye);
        tvKelompokEmploy = findViewById(R.id.tvKelompokEmploy);
        tvJabatanEmploy = findViewById(R.id.tvJabatanEmploy);
        tvDinasEmploy = findViewById(R.id.tvDinasEmploy);
        llKeluarProfil = findViewById(R.id.llKeluarProfil);
        trGantiPassword = findViewById(R.id.trGantiPassword);
        ivBack = findViewById(R.id.ivBack);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://absensi.kotakecil.gov/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        holderAPI = retrofit.create(HttpService.class);


        databaseHelper = new DatabaseHelper(this);

        dataEmployee();

        if (fotoProfile.equals("null")){
            Glide.with(this)
                    .load( R.drawable.profilpics )
                    .into( civProfilPegawai );
        }else{
            Glide.with(this)
                    .load( "https://absensi.kotakecil.gov/storage/" +fotoProfile )
                    .into( civProfilPegawai );
        }


        if(sNama.length()>14){
            tvNamaPegawai.setText(sNama.substring(0,14)+" ...");
        }else {
            tvNamaPegawai.setText(sNama);
        }
        tvNip.setText(sNIP);
        tvEmailEmploy.setText(sEmail);
        tvNoHpEmploye.setText(sNoHpEmployee);
        tvKelompokEmploy.setText(sKelompok);
        tvJabatanEmploy.setText(sJabatan);
        tvDinasEmploy.setText(sKantor);

        llKeluarProfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                peringatanKeluar();
            }
        });

        trGantiPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gantiPassView();
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    public void dataEmployee(){

        Cursor res = databaseHelper.getAllData22(userId);

        while (res.moveToNext()){
            sEmployee_id = res.getString(1);
            sAkses = res.getString(3);
            sActive = res.getString(4);
            sToken = res.getString(5);
        }

        Cursor resa = databaseHelper.getDataEmployee(sEmployee_id);

        while (resa.moveToNext()){

            sNIP = resa.getString(5);
            sNama = resa.getString(6);
            sEmail = resa.getString(7);
            sNoHpEmployee = resa.getString(8);
            sOPD = resa.getString(4);

            if (!resa.getString(9).equals("pns")){
                sKelompok = "Non-PNS";
            }else{
                sKelompok = "Pegawai Negeri Sipil";
            }

            sJabatan = resa.getString(12);
            sKantor = resa.getString(13);
            fotoProfile = resa.getString(17);
            fotoProfileUpdate = resa.getString(17);

        }
    }

    DialogView dialogView = new DialogView(ProfileActivity.this);
    public void gantiPassView(){
        Dialog dialogpassword = new Dialog(ProfileActivity.this, R.style.DialogStyle);
        dialogpassword.setContentView(R.layout.view_ganti_password);
        dialogpassword.setCancelable(false);


        EditText etPasswordSebelum = dialogpassword.findViewById(R.id.etPasswordSebelum);
        EditText etNewPassword = dialogpassword.findViewById(R.id.etNewPassword);
        EditText etEntryNewPassword = dialogpassword.findViewById(R.id.etEntryNewPassword);
        TextView btnGantiPass = dialogpassword.findViewById(R.id.btnGantiPass);
        TextView statuspass = dialogpassword.findViewById(R.id.statuspass);
        ImageView ivCloseGantiPass = dialogpassword.findViewById(R.id.ivCloseGantiPass);
        ProgressBar pbGantiPass = dialogpassword.findViewById(R.id.pbGantiPass);
        etEntryNewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!charSequence.toString().trim().isEmpty()){
                    if (etNewPassword.getText().toString().trim().equals(etEntryNewPassword.getText().toString().trim())){
                        statuspass.setVisibility(View.GONE);
                    }else{
                        statuspass.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        ivCloseGantiPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogpassword.dismiss();
            }
        });
        btnGantiPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etPasswordSebelum.getText().toString().trim().isEmpty() )
                {
                    etPasswordSebelum.setError("Harap masukkan password lama.");

                } else if (etNewPassword.getText().toString().trim().isEmpty()) {
                    etNewPassword.setError("Harap masukkan password baru.");

                } else if (etEntryNewPassword.getText().toString().trim().isEmpty()) {
                    etEntryNewPassword.setError("Harap masukkan ulang password baru.");

                } else{
                    btnGantiPass.setVisibility(View.GONE);
                    pbGantiPass.setVisibility(View.VISIBLE);
                    Call<List<Updatep>> update = holderAPI.getUrlUpdatep("https://absensi.kotakecil.gov/api/updatep?"
                            +"employee_id="+sEmployee_id
                            +"&old="+etPasswordSebelum.getText()
                            +"&new="+etNewPassword.getText()
                    );

                    update.enqueue(new Callback<List<Updatep>>() {
                        @Override
                        public void onResponse(@NonNull Call<List<Updatep>> call, @NonNull retrofit2.Response<List<Updatep>> response) {

                            List<Updatep> updateps = response.body();
                            if (!response.isSuccessful()){
                                btnGantiPass.setVisibility(View.VISIBLE);
                                pbGantiPass.setVisibility(View.GONE);
                                statuspass.setVisibility(View.VISIBLE);
                                statuspass.setText("Gagal mengganti password, mohon coba kembali!");
                                return;
                            }

                            for (Updatep updatep : updateps){
                                if (updatep.isStatus() == true){
                                    btnGantiPass.setVisibility(View.VISIBLE);
                                    pbGantiPass.setVisibility(View.GONE);
                                    dialogpassword.dismiss();
//                                    dialogView.viewSukses(ProfileActivity.this,"");
                                }else{
                                    btnGantiPass.setVisibility(View.VISIBLE);
                                    pbGantiPass.setVisibility(View.GONE);
                                    statuspass.setVisibility(View.VISIBLE);
                                    statuspass.setText("Password lama salah, periksa kembali!");
                                }
                            }




                        }

                        @Override
                        public void onFailure(Call<List<Updatep>> call, Throwable t) {
                            btnGantiPass.setVisibility(View.VISIBLE);
                            pbGantiPass.setVisibility(View.GONE);
                            statuspass.setVisibility(View.VISIBLE);
                            statuspass.setText("Gagal terhubung, mohon coba kembali!");

                        }
                    });

                }
            }
        });



        dialogpassword.show();
    }

    public void peringatanKeluar(){
        Dialog dataKosong = new Dialog(ProfileActivity.this, R.style.DialogStyle);
        dataKosong.setContentView(R.layout.view_warning_keluar);
        ImageView tvTutupDialog = dataKosong.findViewById(R.id.tvTutupDialog);
        TextView tvYakin = dataKosong.findViewById(R.id.tvYakin);

        tvTutupDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataKosong.dismiss();
            }
        });
        tvYakin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnKeluar();
                dataKosong.dismiss();
            }
        });

        dataKosong.show();
    }

    public void btnKeluar(){
        Dialog dialogproses = new Dialog(ProfileActivity.this, R.style.DialogStyle);
        dialogproses.setContentView(R.layout.view_proses);
        dialogproses.setCancelable(false);

        String token = session.getToken();

        api.logout(token).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                session.clearSession();
                DatabaseHelper db = new DatabaseHelper(ProfileActivity.this);

                databaseHelper.deleteDataUseAll();
                databaseHelper.deleteDataEmployeeAll();
                databaseHelper.deleteDataKoordinatOPDAll();
                databaseHelper.deleteKegiatanIzin();
                databaseHelper.deleteTimeTableAll();
                databaseHelper.deleteDataKoordinatEmployeeAll();
                databaseHelper.deleteJamSift();
                databaseHelper.deleteJadwalSift2();
                session.clearSession();

                Handler handler = new Handler();
                handler.postDelayed(() -> {
                    dashboardVersiOne.finish();
                    Intent loginActivity = new Intent(ProfileActivity.this, LoginActivity.class);
                    loginActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(loginActivity);
                    finish();
                }, 3000);

            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                Toast.makeText(ProfileActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        dialogproses.show();
    }


}