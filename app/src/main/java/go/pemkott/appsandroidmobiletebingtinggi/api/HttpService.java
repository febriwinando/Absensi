
package go.pemkott.appsandroidmobiletebingtinggi.api;

import java.util.ArrayList;
import java.util.List;

import go.pemkott.appsandroidmobiletebingtinggi.login.Logout;
import go.pemkott.appsandroidmobiletebingtinggi.model.CheckUpdate;
import go.pemkott.appsandroidmobiletebingtinggi.model.DataEmployee;
import go.pemkott.appsandroidmobiletebingtinggi.model.Employee;
import go.pemkott.appsandroidmobiletebingtinggi.model.FileModel;
import go.pemkott.appsandroidmobiletebingtinggi.model.HasilValidasi;
import go.pemkott.appsandroidmobiletebingtinggi.model.InfoMP;
import go.pemkott.appsandroidmobiletebingtinggi.model.JadwalSift;
import go.pemkott.appsandroidmobiletebingtinggi.model.KegiatanIzin;
import go.pemkott.appsandroidmobiletebingtinggi.model.Koordinat;
import go.pemkott.appsandroidmobiletebingtinggi.model.Presensi;
import go.pemkott.appsandroidmobiletebingtinggi.model.RekapMasukFragment;
import go.pemkott.appsandroidmobiletebingtinggi.model.RekapMasukKeduaFragment;
import go.pemkott.appsandroidmobiletebingtinggi.model.RekapPulangFragment;
import go.pemkott.appsandroidmobiletebingtinggi.model.RekapPulangKeduaFragment;
import go.pemkott.appsandroidmobiletebingtinggi.model.RekapServer;
import go.pemkott.appsandroidmobiletebingtinggi.model.ReturnValidasi;
import go.pemkott.appsandroidmobiletebingtinggi.model.TimeTables;
import go.pemkott.appsandroidmobiletebingtinggi.model.Updatep;
import go.pemkott.appsandroidmobiletebingtinggi.model.ValidasiData;
import go.pemkott.appsandroidmobiletebingtinggi.model.ValidasiModel;
import go.pemkott.appsandroidmobiletebingtinggi.model.WaktuSift;
import go.pemkott.appsandroidmobiletebingtinggi.singkronjadwal.TimeTebleSetting;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;

public interface HttpService {

    @FormUrlEncoded
    @POST("dataEmployee")
    Call<DataEmployee> dataEmployee(
            @Field("id") String id

    );
    // UPLOAD
    @Multipart
    @POST("upload_file/RestApi/upload_api.php")
    Call<FileModel> callUploadApi(@Part MultipartBody.Part image);

    // ==============================
    // LOGOUT
    // ==============================
    @Headers({"Content-Type: application/json"})
    @POST
    Call<List<Logout>> getUrlLogout(@Url String url, @Header("Authorization") String token);

    @Headers({"Content-Type: application/json"})
    @POST
    Call<List<Logout>> getUrlLogout(@Url String url, @Header("Authorization") String token, @Body RequestBody empty);

    // ==============================
    // KEGIATAN
    // ==============================
    @Headers({"Content-Type: application/json"})
    @POST
    Call<List<KegiatanIzin>> getUrlKegiatan(@Url String url, @Header("Authorization")String token , @Body RequestBody empty );

    @Headers({"Content-Type: application/json"})
    @POST
    Call<List<Koordinat>> getUrlKoordinat(@Url String url, @Header("Authorization")String token, @Body RequestBody empty);

    @Headers({"Content-Type: application/json"})
    @POST
    Call<List<KegiatanIzin>> getUrlKegiatanNew(@Url String url);

    @Headers({"Content-Type: application/json"})
    @POST
    Call<List<KegiatanIzin>> getUrlKegiatanNew(@Url String url, @Body RequestBody empty);

    // ==============================
    // EMPLOYEE
    // ==============================
    @Headers({"Content-Type: application/json"})
    @POST
    Call<Employee> getUrlEmployee(@Url String url);

    @Headers({"Content-Type: application/json"})
    @POST
    Call<Employee> getUrlEmployee(@Url String url, @Body RequestBody empty);

    // ==============================
    // UPLOAD KEGIATAN
    // ==============================
    @Multipart
    @Headers({"Content-Type: application/json"})
    @POST
    Call<List<FileModel>> getUrlUploadKegiatan(
            @Url String url,
            @Part List<MultipartBody.Part> image,
            @Part List<MultipartBody.Part> lampiran,
            @Header("Authorization")String token
    );

    @Multipart
    @POST
    Call<FileModel> callMultipleUploadApi(
            @Url String url,
            @Part List<MultipartBody.Part> image,
            @Part List<MultipartBody.Part> lampiran,
            @Header("Authorization")String token
    );

    @Multipart
    @FormUrlEncoded
    @POST
    Call<FileModel> callMultipleUploadApiBase(@Url String url);

    @Multipart
    @POST
    Call<FileModel> callUpdateProfil(@Url String url, @Part List<MultipartBody.Part> image, @Header("Authorization")String token);

    @Multipart
    @POST
    Call<FileModel> callMultipleUploadApiPerjalananDinas(@Url String url, @Part List<MultipartBody.Part> image, @Part List<MultipartBody.Part> lampiran, @Header("Authorization")String token);

    // ==============================
    // TIMETABLE
    // ==============================
    @Headers({"Content-Type: application/json"})
    @POST
    Call<List<TimeTables>> getUrlTimeTable(@Url String url, @Header("Authorization")String token );

    @Headers({"Content-Type: application/json"})
    @POST
    Call<List<TimeTables>> getUrlTimeTable(@Url String url, @Header("Authorization")String token, @Body RequestBody empty );

    @Headers({"Content-Type: application/json"})
    @POST
    Call<List<WaktuSift>> getTestSift(@Url String url);

    @Headers({"Content-Type: application/json"})
    @POST
    Call<List<WaktuSift>> getTestSift(@Url String url, @Body RequestBody empty);

    @Headers({"Content-Type: application/json"})
    @POST
    Call<List<JadwalSift>> getJadwalSift(@Url String url);

    @Headers({"Content-Type: application/json"})
    @POST
    Call<List<JadwalSift>> getJadwalSift(@Url String url, @Body RequestBody empty);

    @Headers({"Content-Type: application/json"})
    @POST
    Call<ArrayList<JadwalSift>> getJadwalSifts(@Url String url);

    @Headers({"Content-Type: application/json"})
    @POST
    Call<ArrayList<JadwalSift>> getJadwalSifts(@Url String url, @Body RequestBody empty);

    @Headers({"Content-Type: application/json"})
    @POST
    Call<List<TimeTebleSetting>> getUrlTimeTableSetting(@Url String url, @Header("Authorization")String token );

    @Headers({"Content-Type: application/json"})
    @POST
    Call<List<TimeTebleSetting>> getUrlTimeTableSetting(@Url String url, @Header("Authorization")String token, @Body RequestBody empty );

    // ==============================
    // UPDATE
    // ==============================
    @Headers({"Content-Type: application/json"})
    @POST
    Call<List<Updatep>> getUrlUpdatep(@Url String url );

    @Headers({"Content-Type: application/json"})
    @POST
    Call<List<Updatep>> getUrlUpdatep(@Url String url, @Body RequestBody empty );

    // ==============================
    // KOORDINAT
    // ==============================
    @Headers({"Content-Type: application/json"})
    @POST
    Call<List<Koordinat>> getUrlKoordinat(@Url String url);

    @Headers({"Content-Type: application/json"})
    @POST
    Call<List<Koordinat>> getUrlKoordinat(@Url String url, @Body RequestBody empty);

    // ==============================
    // INFO MP
    // ==============================
    @Headers({"Content-Type: application/json"})
    @POST
    Call<List<InfoMP>> getUrlInfoMP(@Url String url);

    @Headers({"Content-Type: application/json"})
    @POST
    Call<List<InfoMP>> getUrlInfoMP(@Url String url, @Body RequestBody empty);

    // ==============================
    // PRESENSI
    // ==============================
    @Headers({"Content-Type: application/json"})
    @POST
    Call<List<Presensi>> getUrlPresensi(@Url String url);

    @Headers({"Content-Type: application/json"})
    @POST
    Call<List<Presensi>> getUrlPresensi(@Url String url, @Body RequestBody empty);

    // ==============================
    // VALIDASI LIST
    // ==============================
    @Headers({"Content-Type: application/json"})
    @POST
    Call<List<ValidasiData>> getUrlListValidasi(@Url String url);

    @Headers({"Content-Type: application/json"})
    @POST
    Call<List<ValidasiData>> getUrlListValidasi(@Url String url, @Body RequestBody empty);

    // ==============================
    // REKAP SERVER
    // ==============================
    @Headers({"Content-Type: application/json"})
    @POST
    Call<List<RekapServer>> getUrlRekapServer(@Url String url);

    @Headers({"Content-Type: application/json"})
    @POST
    Call<List<RekapServer>> getUrlRekapServer(@Url String url, @Body RequestBody empty);

    @Headers({"Content-Type: application/json"})
    @POST
    Call<List<RekapMasukFragment>> getRekapMasukFragment(@Url String url);

    @Headers({"Content-Type: application/json"})
    @POST
    Call<List<RekapMasukFragment>> getRekapMasukFragment(@Url String url, @Body RequestBody empty);

    @Headers({"Content-Type: application/json"})
    @POST
    Call<List<RekapMasukKeduaFragment>> getRekapMasukKeduaFragment(@Url String url);

    @Headers({"Content-Type: application/json"})
    @POST
    Call<List<RekapMasukKeduaFragment>> getRekapMasukKeduaFragment(@Url String url, @Body RequestBody empty);

    @Headers({"Content-Type: application/json"})
    @POST
    Call<List<RekapPulangFragment>> getRekapPulangFragment(@Url String url);

    @Headers({"Content-Type: application/json"})
    @POST
    Call<List<RekapPulangFragment>> getRekapPulangFragment(@Url String url, @Body RequestBody empty);

    @Headers({"Content-Type: application/json"})
    @POST
    Call<List<RekapPulangKeduaFragment>> getRekapPulangKeduaFragment(@Url String url);

    @Headers({"Content-Type: application/json"})
    @POST
    Call<List<RekapPulangKeduaFragment>> getRekapPulangKeduaFragment(@Url String url, @Body RequestBody empty);

    @Headers({"Content-Type: application/json"})
    @POST
    Call<List<ReturnValidasi>> getReturnValidasi(@Url String url);

    @Headers({"Content-Type: application/json"})
    @POST
    Call<List<ReturnValidasi>> getReturnValidasi(@Url String url, @Body RequestBody empty);

    @Headers({"Content-Type: application/json"})
    @POST
    Call<List<HasilValidasi>> getUrlHasilValidasi(@Url String url);

    @Headers({"Content-Type: application/json"})
    @POST
    Call<List<HasilValidasi>> getUrlHasilValidasi(@Url String url, @Body RequestBody empty);

    // VALIDASI MASUK PERTAMA
    @Headers({"Content-Type: application/json"})
    @POST
    Call<List<ValidasiModel>> getUrlHasilValidasiMasukPertama(@Url String url);

    @Headers({"Content-Type: application/json"})
    @POST
    Call<List<ValidasiModel>> getUrlHasilValidasiMasukPertama(@Url String url, @Body RequestBody empty);

    // CHECK UPDATE
    @Headers({"Content-Type: application/json"})
    @POST
    Call<List<CheckUpdate>> getCheckUpdate(@Url String url);

    @Headers({"Content-Type: application/json"})
    @POST
    Call<List<CheckUpdate>> getCheckUpdate(@Url String url, @Body RequestBody empty);

}

//package go.pemkott.appsandroidmobiletebingtinggi.api;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import go.pemkott.appsandroidmobiletebingtinggi.login.Logout;
//import go.pemkott.appsandroidmobiletebingtinggi.model.CheckUpdate;
//import go.pemkott.appsandroidmobiletebingtinggi.model.Employee;
//import go.pemkott.appsandroidmobiletebingtinggi.model.FileModel;
//import go.pemkott.appsandroidmobiletebingtinggi.model.HasilValidasi;
//import go.pemkott.appsandroidmobiletebingtinggi.model.InfoMP;
//import go.pemkott.appsandroidmobiletebingtinggi.model.JadwalSift;
//import go.pemkott.appsandroidmobiletebingtinggi.model.KegiatanIzin;
//import go.pemkott.appsandroidmobiletebingtinggi.model.Koordinat;
//import go.pemkott.appsandroidmobiletebingtinggi.model.Presensi;
//import go.pemkott.appsandroidmobiletebingtinggi.model.RekapMasukFragment;
//import go.pemkott.appsandroidmobiletebingtinggi.model.RekapMasukKeduaFragment;
//import go.pemkott.appsandroidmobiletebingtinggi.model.RekapPulangFragment;
//import go.pemkott.appsandroidmobiletebingtinggi.model.RekapPulangKeduaFragment;
//import go.pemkott.appsandroidmobiletebingtinggi.model.RekapServer;
//import go.pemkott.appsandroidmobiletebingtinggi.model.ReturnValidasi;
//import go.pemkott.appsandroidmobiletebingtinggi.model.TimeTables;
//import go.pemkott.appsandroidmobiletebingtinggi.model.Updatep;
//import go.pemkott.appsandroidmobiletebingtinggi.model.ValidasiData;
//import go.pemkott.appsandroidmobiletebingtinggi.model.ValidasiModel;
//import go.pemkott.appsandroidmobiletebingtinggi.model.WaktuSift;
//import go.pemkott.appsandroidmobiletebingtinggi.singkronjadwal.TimeTebleSetting;
//import okhttp3.MultipartBody;
//import retrofit2.Call;
//import retrofit2.http.FormUrlEncoded;
//import retrofit2.http.Header;
//import retrofit2.http.Headers;
//import retrofit2.http.Multipart;
//import retrofit2.http.POST;
//import retrofit2.http.Part;
//import retrofit2.http.Url;
//
//public interface HttpService {
//
//    @Multipart
//    @POST("upload_file/RestApi/upload_api.php")
//    Call<FileModel> callUploadApi(@Part MultipartBody.Part image);
//
//
//    @Headers({"Content-Type: application/json"})
//    @POST
//    Call<List<Logout>> getUrlLogout(@Url String url, @Header("Authorization")String token );
//
//    @Headers({"Content-Type: application/json"})
//    @POST
//    Call<List<KegiatanIzin>> getUrlKegiatan(@Url String url, @Header("Authorization")String token );
//
//    @Headers({"Content-Type: application/json"})
//    @POST
//    Call<List<KegiatanIzin>> getUrlKegiatanNew(@Url String url );
//
//    @Headers({"Content-Type: application/json"})
//    @POST
//    Call<Employee> getUrlEmployee(@Url String url);
//
//    @Multipart
//    @Headers({"Content-Type: application/json"})
//    @POST
//    Call<List<FileModel>> getUrlUploadKegiatan(@Url String url, @Part List<MultipartBody.Part> image, @Part List<MultipartBody.Part> lampiran, @Header("Authorization")String token );
//
//    @Multipart
//    @POST
//    Call<FileModel> callMultipleUploadApi(@Url String url, @Part List<MultipartBody.Part> image, @Part List<MultipartBody.Part> lampiran, @Header("Authorization")String token);
//
//    @Multipart
//    @FormUrlEncoded
//    @POST
//    Call<FileModel> callMultipleUploadApiBase(@Url String url);
//
////    @FormUrlEncoded
////    @POST("uploaditem")
////    Call<ResponsePOJO> uploadImage(
////            @Field("fileup") String encodedImage,
////            @Field("nama_p") String nama,
////            @Field("id_kategori") String kategori,
////            @Field("harga_p") String harga,
////            @Field("ket_p") String keterangan,
////            @Field("id_umkm") String id_umkm
////    );
//
//
//    @Multipart
//    @POST
//    Call<FileModel> callUpdateProfil(@Url String url, @Part List<MultipartBody.Part> image, @Header("Authorization")String token);
//
//    @Multipart
//    @POST
//    Call<FileModel> callMultipleUploadApiPerjalananDinas(@Url String url, @Part List<MultipartBody.Part> image, @Part List<MultipartBody.Part> lampiran, @Header("Authorization")String token);
//
//    @Headers({"Content-Type: application/json"})
//    @POST
//    Call<List<TimeTables>> getUrlTimeTable(@Url String url, @Header("Authorization")String token );
//
//
//    @Headers({"Content-Type: application/json"})
//    @POST
//    Call<List<WaktuSift>> getTestSift(@Url String url);
//
//    @Headers({"Content-Type: application/json"})
//    @POST
//    Call<List<JadwalSift>> getJadwalSift(@Url String url);
//    @Headers({"Content-Type: application/json"})
//    @POST
//    Call<ArrayList<JadwalSift>> getJadwalSifts(@Url String url);
//
//    @Headers({"Content-Type: application/json"})
//    @POST
//    Call<List<TimeTebleSetting>> getUrlTimeTableSetting(@Url String url, @Header("Authorization")String token );
//
//    @Headers({"Content-Type: application/json"})
//    @POST
//    Call<List<Updatep>> getUrlUpdatep(@Url String url );
//
//    @Headers({"Content-Type: application/json"})
//    @POST
//    Call<List<Koordinat>> getUrlKoordinat(@Url String url);
//
//    @Headers({"Content-Type: application/json"})
//    @POST
//    Call<List<InfoMP>> getUrlInfoMP(@Url String url);
//
//
//    @Headers({"Content-Type: application/json"})
//    @POST
//    Call<List<Presensi>> getUrlPresensi(@Url String url);
//
//    @Headers({"Content-Type: application/json"})
//    @POST
//    Call<List<ValidasiData>> getUrlListValidasi(@Url String url);
//
//    @Headers({"Content-Type: application/json"})
//    @POST
//    Call<List<RekapServer>> getUrlRekapServer(@Url String url);
//
//    @Headers({"Content-Type: application/json"})
//    @POST
//    Call<List<RekapMasukFragment>> getRekapMasukFragment(@Url String url);
//
//    @Headers({"Content-Type: application/json"})
//    @POST
//    Call<List<RekapMasukKeduaFragment>> getRekapMasukKeduaFragment(@Url String url);
//
//    @Headers({"Content-Type: application/json"})
//    @POST
//    Call<List<RekapPulangFragment>> getRekapPulangFragment(@Url String url);
//
//    @Headers({"Content-Type: application/json"})
//    @POST
//    Call<List<RekapPulangKeduaFragment>> getRekapPulangKeduaFragment(@Url String url);
//
//    @Headers({"Content-Type: application/json"})
//    @POST
//    Call<List<ReturnValidasi>> getReturnValidasi(@Url String url);
//
//    @Headers({"Content-Type: application/json"})
//    @POST
//    Call<List<HasilValidasi>> getUrlHasilValidasi(@Url String url);
//
//    @Headers({"Content-Type: application/json"})
//    @POST
//    Call<List<ValidasiModel>> getUrlHasilValidasiMasukPertama(@Url String url);
//
//    @Headers({"Content-Type: application/json"})
//    @POST
//    Call<List<CheckUpdate>> getCheckUpdate(@Url String url);
//
//
////    @Multipart
////    @POST("absen")
////    Call<FileModel> callMultipleUploadApi(
////            @Part List<MultipartBody.Part> image,
////            @Part ("employee_id") RequestBody employee_id,
////            @Part ("tanggal") RequestBody tanggal,
////            @Part ("jam") RequestBody jam,
////            @Part ("posisi") RequestBody posisi,
////            @Part ("status") RequestBody status,
////            @Part ("lat") RequestBody lat,
////            @Part ("lng") RequestBody lng,
////            @Part ("ket") RequestBody ket,
////            @Part ("lampiran") RequestBody lampiran,
////            @Part ("valid") RequestBody valid
////
////    );
//
//}
