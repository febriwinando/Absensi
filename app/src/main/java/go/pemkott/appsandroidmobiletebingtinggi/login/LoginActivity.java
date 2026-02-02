package go.pemkott.appsandroidmobiletebingtinggi.login;


import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.firebase.messaging.FirebaseMessaging;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import go.pemkott.appsandroidmobiletebingtinggi.NewDashboard.DashboardVersiOne;
import go.pemkott.appsandroidmobiletebingtinggi.R;
import go.pemkott.appsandroidmobiletebingtinggi.api.HttpService;
import go.pemkott.appsandroidmobiletebingtinggi.api.ResponsePOJO;
import go.pemkott.appsandroidmobiletebingtinggi.api.RetroClient;
import go.pemkott.appsandroidmobiletebingtinggi.database.DatabaseHelper;
import go.pemkott.appsandroidmobiletebingtinggi.dialogview.DialogView;
import go.pemkott.appsandroidmobiletebingtinggi.utils.NetworkUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    HttpService httpService;
    String sId;
    String sEmployee_id;
    private String sUsername;
    String sAkses;
    String sActive;
    String status;
    String sToken, sVerifikator;
    private static String URL_DATA = null;

    private TextInputEditText etUsername, etPassword;
    TextView textDb, tvResetPassword;

    //Databases
    DatabaseHelper databaseHelper;

    AppUpdateManager appUpdateManager ;
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Mengatur aktivitas sebagai layar penuh
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.background_color));
        getWindow().setNavigationBarColor(getResources().getColor(R.color.background_color));
        setContentView(R.layout.activity_login);

        appUpdateManager = AppUpdateManagerFactory.create(LoginActivity.this);

        session = new SessionManager(this);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://absensi.tebingtinggikota.go.id/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        httpService = retrofit.create(HttpService.class);
        databaseHelper = new DatabaseHelper(this);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        textDb = findViewById(R.id.textdb);
        tvResetPassword = findViewById(R.id.tvResetPassword);

        tvResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intentReset = new Intent(LoginActivity.this, ResetPasswprdActivity.class);
//                intentReset.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
//                startActivity(intentReset);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
//        connectionMonitor.registerDefaultNetworkCallback();
//        Variables.isRegistered=true;
        databaseHelper.getReadableDatabase();
        session = new SessionManager(this);
        datauser();

    }


    public void loginUser(View view){
        if (etUsername.getText().toString().trim().isEmpty() || etPassword.getText().toString().trim().isEmpty()){
            dialogView.viewNotifKosong(LoginActivity.this, "Anda harus mengisi NIP/NIK Anda dan kata kunci terlebih dahulu.", "");
        }else{
            if (NetworkUtils.isConnected(this)){
                URL_DATA = "https://absensi.tebingtinggikota.go.id/api/login?";
                login_app();
            }else{
                dialogView.viewNotifKosong(LoginActivity.this, "Mohon periksa koneksi internet Anda.", "");
            }
        }
    }


    private void datauser(){

        if (session.getToken() != null) {
            Intent dashboardActivity = new Intent(LoginActivity.this, DashboardVersiOne.class);
            dashboardActivity.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(dashboardActivity);
            finish();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    DialogView dialogView = new DialogView(LoginActivity.this);

    private void login_app(){

        Dialog dialogproses = new Dialog(LoginActivity.this, R.style.DialogStyle);
        dialogproses.setContentView(R.layout.view_proses);
        dialogproses.setCancelable(false);


        StringRequest stringRequest = new StringRequest( Request.Method.POST,
                URL_DATA+"username="+ Objects.requireNonNull(etUsername.getText()).toString().trim()+"&password="+ Objects.requireNonNull(etPassword.getText()).toString().trim(),
                response -> {
                    if (response.isEmpty()){
                        dialogproses.dismiss();
                        dialogView.viewNotifKosong(LoginActivity.this, "Tidak dapat terhubung ke server,", "silahkan coba kembali.!");
                        return;
                    }

                    try {

                        JSONArray jsonObject = new JSONArray(response);
                        JSONObject array = jsonObject.getJSONObject(0);
                        JSONObject JO = array.getJSONObject("user");


                        sId = JO.getString("id");
                        sEmployee_id = JO.getString("employee_id");
                        sAkses = JO.getString("akses");
                        sActive = JO.getString("active");
                        sVerifikator = JO.getString("role");
                        sToken = array.getString("token");
                        sUsername = array.getString("nama");
                        status = array.getString("status");

                        session.saveToken("Bearer " + sToken);
                        session.savePegawaiId(sId);
                        session.saveEmployeeId(sEmployee_id);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (Integer.parseInt(status) == 401 ){
                        dialogproses.dismiss();
                        dialogView.viewNotifKosong(LoginActivity.this, "NIP/NIK atau Password tidak sesuai,", "periksa kembali!");
                        return;
                    }

                    if (sActive.equals("1")){
                        if (Integer.parseInt(status) == 201){

                            databaseHelper.insertDataUserLogin(sId, sEmployee_id, sUsername, sAkses, sActive, sToken, sVerifikator);

                            dialogproses.dismiss();
                            fetchFcmTokenAndSend();

                        }else{
                            dialogproses.dismiss();
                            dialogView.viewNotifKosong(LoginActivity.this, "NIP/NIK atau Password tidak sesuai,", "periksa kembali!");

                        }
                    }else{
                        dialogproses.dismiss();
                        dialogView.viewNotifKosong(LoginActivity.this, "User tidak aktif, ", "silahkan hubungi bagian umum unit kerja anda!");

                    }


                }, error -> {
                dialogproses.dismiss();
                    dialogView.pesanError(LoginActivity.this);

                }
        );

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
        dialogproses.show();

    }

    private void fetchFcmTokenAndSend() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e("FCM", "Gagal ambil token", task.getException());
                        openNextPage(); // tetap lanjut
                        return;
                    }

                    String token = task.getResult();
                    SessionManager session = new SessionManager(this);
                    session.saveFcmToken(token);

                    sendFcmTokenToServer();
                });
    }
    private void sendFcmTokenToServer() {

        SessionManager session = new SessionManager(this);

        String pegawaiId = session.getPegawaiId();
        String fcmToken  = session.getFcmToken();

        if (pegawaiId == null || fcmToken == null) {
            Log.w("FCM", "Token belum ada, lanjut tanpa update");
            openNextPage();
            return;
        }

        Call<ResponsePOJO> call = RetroClient.getInstance()
                .getApi()
                .updateFcmToken(pegawaiId, fcmToken);

        call.enqueue(new Callback<ResponsePOJO>() {
            @Override
            public void onResponse(Call<ResponsePOJO> call, Response<ResponsePOJO> response) {
                openNextPage();
            }

            @Override
            public void onFailure(Call<ResponsePOJO> call, Throwable t) {
                openNextPage();
            }
        });
    }


    private void openNextPage() {
        Intent intent = new Intent(LoginActivity.this, DownloadDataActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }



}