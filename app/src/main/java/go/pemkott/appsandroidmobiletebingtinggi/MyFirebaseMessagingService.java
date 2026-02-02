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
import go.pemkott.appsandroidmobiletebingtinggi.model.Koordinat;
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


    private void koordinat_e() {
        HttpService api = RetroClient.getInstance2().getApi2();
        SessionManager session = new SessionManager(this);
        String employeeId = session.getEmployeeId();
        String token = session.getToken();
        DatabaseHelper db = new DatabaseHelper(this);

        String url = "https://absensi.tebingtinggikota.go.id/api/koordinatemployee?id=" + employeeId;
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