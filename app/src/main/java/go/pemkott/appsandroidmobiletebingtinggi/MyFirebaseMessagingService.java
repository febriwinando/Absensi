package go.pemkott.appsandroidmobiletebingtinggi;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.List;

import go.pemkott.appsandroidmobiletebingtinggi.api.HttpService;
import go.pemkott.appsandroidmobiletebingtinggi.api.RetroClient;
import go.pemkott.appsandroidmobiletebingtinggi.database.DatabaseHelper;
import go.pemkott.appsandroidmobiletebingtinggi.login.SessionManager;
import go.pemkott.appsandroidmobiletebingtinggi.model.DataEmployee;
import go.pemkott.appsandroidmobiletebingtinggi.model.Koordinat;
import go.pemkott.appsandroidmobiletebingtinggi.model.TimeTables;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyFirebaseMessagingService extends FirebaseMessagingService {



    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d("FCM", "Token: " + token);
        SessionManager session = new SessionManager(this);
        session.saveFcmToken(token);

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        if (remoteMessage.getNotification() != null) {
            showNotification(
                    remoteMessage.getNotification().getTitle(),
                    remoteMessage.getNotification().getBody()
            );


        }
    }


    private void stepPegawai() {
        HttpService api = RetroClient.getInstance2().getApi2();
        SessionManager session = new SessionManager(this);
        String employeeId = session.getEmployeeId();
        DatabaseHelper db = new DatabaseHelper(this);

        api.dataEmployee(employeeId).enqueue(new Callback<DataEmployee>() {
            @Override
            public void onResponse(Call<DataEmployee> call, Response<DataEmployee> res) {


                if (res.isSuccessful()){
                    db.deleteDataEmployeeAll();

                    DataEmployee d = res.body();
                    db.insertDataEmployee(
                            d.getId(), d.getAtasan_id1(), d.getAtasan_id2(),
                            d.getPosition_id(), d.getOpd_id(), d.getNip(),
                            d.getNama(), d.getEmail(), d.getNo_hp(),
                            d.getKelompok(), d.getS_jabatan(), d.getEselon(),
                            d.getJabatan(), d.getOpd(), d.getAlamat(),
                            d.getLet(), d.getLng(), d.getFoto(),
                            d.getAwal_waktu(), String.valueOf(d.getShift())
                    );

                }
            }

            @Override
            public void onFailure(Call<DataEmployee> call, Throwable t) {

            }
        });
    }

    private void stepTimetable() {
        HttpService api = RetroClient.getInstance2().getApi2();
        SessionManager session = new SessionManager(this);
        String employeeId = session.getEmployeeId();
        String token = session.getToken();
        DatabaseHelper db = new DatabaseHelper(this);
        
        String url = "https://absensi.kotakecol.gov/api/timetable?employee_id=" + employeeId;

        api.getUrlTimeTable(url, "Bearer " + token,
                        RequestBody.create("", MediaType.parse("application/json")))
                .enqueue(new Callback<List<TimeTables>>() {

                    @Override
                    public void onResponse(Call<List<TimeTables>> call, Response<List<TimeTables>> res) {

                        if (res.isSuccessful()) {
                            db.deleteTimeTableAll();
                            for (TimeTables t : res.body()) {
                                db.insertDataTimeTable(
                                        String.valueOf(t.getId()),
                                        t.getEmployee_id(),
                                        t.getTimetable_id(),
                                        t.getInisial(),
                                        String.valueOf(t.getHari()),
                                        t.getMasuk(),
                                        t.getPulang()
                                );
                            }

                        }
                    }

                    @Override
                    public void onFailure(Call<List<TimeTables>> call, Throwable t) {
                        
                    }
                });
    }


    private void koordinat_e() {
        HttpService api = RetroClient.getInstance2().getApi2();
        SessionManager session = new SessionManager(this);
        String employeeId = session.getEmployeeId();
        String token = session.getToken();
        DatabaseHelper db = new DatabaseHelper(this);

        String url = "https://absensi.kotakecol.gov/api/koordinatemployee?id=" + employeeId;
        api.getUrlKoordinat(url, "Bearer " + token,
                        RequestBody.create("", MediaType.parse("application/json")))
                .enqueue(new Callback<List<Koordinat>>() {

                    @Override
                    public void onResponse(Call<List<Koordinat>> call, Response<List<Koordinat>> response) {
                        if (response.isSuccessful()){
                            db.deleteDataKoordinatEmployeeAll();

                            for (Koordinat koordinat : response.body()) {
                                db.insertDataKoordinatEmployee(koordinat.getId(), employeeId, koordinat.getAlamat(), koordinat.getLet(), koordinat.getLng());
                            }
                        }


                    }

                    @Override
                    public void onFailure(Call<List<Koordinat>> call, Throwable t) {

                    }
                });

    }

    private void showNotification(String title, String body) {
        String channelId = "default_channel";

        if(title.equals("Lokasi")){
            koordinat_e();
        } else if (title.equals("Jadwal")) {
            stepTimetable();
        }else if (title.equals("Data Pegawai")){
            stepPegawai();
        }

        NotificationManager manager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Notifikasi",
                    NotificationManager.IMPORTANCE_HIGH
            );
            manager.createNotificationChannel(channel);
        }


        Notification notification = new NotificationCompat.Builder(this, channelId)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.logoabsensilogin)
                .setAutoCancel(true)
                .build();

        manager.notify(1, notification);
    }

}