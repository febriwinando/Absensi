package go.pemkott.appsandroidmobiletebingtinggi.login;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "APP_PREFS";
    private static final String KEY_TOKEN = "AUTH_TOKEN";
    private static final String KEY_PEGAWAI_ID = "PEGAWAI_ID";
    private static final String KEY_JABTAN = "JABATAN";
    private static final String KEY_FCM_TOKEN = "FCM_TOKEN";
    private static final String KEY_EMPLOYEE_ID = "KEY_EMPLOYEE_ID";

    private final SharedPreferences prefs;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveToken(String token) {
        prefs.edit().putString(KEY_TOKEN, token).apply();
    }

    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

//    public void clearSession() {
//        prefs.edit().remove(KEY_TOKEN).apply();
//    }

    // -------------------------
    // PEGAWAI ID
    // -------------------------
    public void savePegawaiId(String id) {
        prefs.edit().putString(KEY_PEGAWAI_ID, id).apply();
    }

    public String getPegawaiId() {
        return prefs.getString(KEY_PEGAWAI_ID, "0"); // default 0
    }

    public void savePegawaiLevel(String level) {
        prefs.edit().putString(KEY_JABTAN, level).apply();
    }

    public String getPegawaiLevel() {
        return prefs.getString(KEY_JABTAN, "0"); // default 0
    }

    // -------------------------
    // CLEAR
    // -------------------------
    public void clearSession() {
        prefs.edit().clear().apply();
    }

    // -------------------------
// FCM TOKEN
// -------------------------
    public void saveFcmToken(String token) {
        prefs.edit().putString(KEY_FCM_TOKEN, token).apply();
    }

    public String getFcmToken() {
        return prefs.getString(KEY_FCM_TOKEN, null);
    }
    public void saveEmployeeId(String token) {
        prefs.edit().putString(KEY_EMPLOYEE_ID, token).apply();
    }

    public String getEmployeeId() {
        return prefs.getString(KEY_EMPLOYEE_ID, null);
    }
}
