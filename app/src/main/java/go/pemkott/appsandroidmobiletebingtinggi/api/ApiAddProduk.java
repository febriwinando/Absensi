package go.pemkott.appsandroidmobiletebingtinggi.api;

import java.util.List;

import go.pemkott.appsandroidmobiletebingtinggi.model.CheckAbsensi;
import go.pemkott.appsandroidmobiletebingtinggi.model.DataEmployee;
import go.pemkott.appsandroidmobiletebingtinggi.model.Koordinat;
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

public interface ApiAddProduk {

    @FormUrlEncoded
    @POST("update-fcm-token")
    Call<ResponsePOJO> updateFcmToken(
            @Field("pegawai_id") String pegawaiId,
            @Field("fcm_token") String fcmToken
    );
    @FormUrlEncoded
    @POST("baseee")
    Call<ResponsePOJO> uploadImage(
            @Field("fileup") String encodedImage
    );

    @Multipart
    @POST("kehadiran/masuk")
    Call<ResponsePOJO> uploadAbsenKehadiranMasuk(
            @Part MultipartBody.Part fototaging,
            @Part("absensi") RequestBody absensi,
            @Part("eselon") RequestBody eselon,
            @Part("employee_id") RequestBody employee_id,
            @Part("timetable_id") RequestBody timetable_id,
            @Part("tanggal") RequestBody tanggal,
            @Part("jam_masuk") RequestBody jam_masuk,
            @Part("posisi_masuk") RequestBody posisi_masuk,
            @Part("status_masuk") RequestBody status_masuk,
            @Part("lat_masuk") RequestBody lat_masuk,
            @Part("lng_masuk") RequestBody lng_masuk,
            @Part("ket_masuk") RequestBody ket_masuk,
            @Part("terlambat") RequestBody terlambat,
            @Part("opd") RequestBody opd,
            @Part("jam_kantor") RequestBody jam_kantor,
            @Part("valid_masuk") RequestBody valid_masuk,
            @Part("fakegps") RequestBody fakegps,
            @Part("batas_waktu") RequestBody batas_waktu,
            @Part("berakhlak") RequestBody berakhlak
    );


    @Multipart
    @POST("kehadiran/pulang")
    Call<ResponsePOJO> uploadAbsenKehadiranPulang(
            @Part MultipartBody.Part fototaging,
            @Part("absensi") RequestBody absensi,
            @Part("eselon") RequestBody eselon,
            @Part("employee_id") RequestBody employee_id,
            @Part("timetable_id") RequestBody timetable_id,
            @Part("tanggal") RequestBody tanggal,
            @Part("jam_pulang") RequestBody jam_pulang,
            @Part("posisi_pulang") RequestBody posisi_pulang,
            @Part("status_pulang") RequestBody status_pulang,
            @Part("lat_pulang") RequestBody lat_pulang,
            @Part("lng_pulang") RequestBody lng_pulang,
            @Part("ket_pulang") RequestBody ket_pulang,
            @Part("kecepatan") RequestBody kecepatan,
            @Part("opd") RequestBody opd,
            @Part("jam_kantor") RequestBody jam_kantor,
            @Part("valid_pulang") RequestBody valid_pulang,
            @Part("fakegps") RequestBody fakegps,
            @Part("batas_waktu") RequestBody batas_waktu,
            @Part("berakhlak") RequestBody berakhlak
    );

    @Multipart
    @POST("sift/masuk/pagi")
    Call<ResponsePOJO> absensiftMasukPagi(
            @Part MultipartBody.Part fototaging,
//            @Part("absensi") RequestBody absensi,
//            @Part("eselon") RequestBody eselon,
            @Part("employee_id") RequestBody employee_id,
            @Part("tanggal") RequestBody tanggal,
            @Part("jam_masuk") RequestBody jam_masuk,
            @Part("posisi_masuk") RequestBody posisi_masuk,
            @Part("status_masuk") RequestBody status_masuk,
            @Part("lat_masuk") RequestBody lat_masuk,
            @Part("lng_masuk") RequestBody lng_masuk,
//            @Part("opd") RequestBody opd,
            @Part("jam_kantor") RequestBody jam_kantor,
            @Part("valid_masuk") RequestBody valid_masuk,
            @Part("fakegps") RequestBody fakegps,
            @Part("batas_waktu") RequestBody batas_waktu,
            @Part("masuksift") RequestBody masuksift,
            @Part("pulangsift") RequestBody pulangsift,
            @Part("inisialsift") RequestBody inisialsift,
            @Part("tipesift") RequestBody tipesift,
            @Part("idsift") RequestBody idsift,
            @Part("ket_masuk") RequestBody ket_masuk,
            @Part("terlambat") RequestBody terlambat
    );
    @Multipart
    @POST("sift/pulang/pagi")
    Call<ResponsePOJO> absensiftPulangPagi(
            @Part MultipartBody.Part fototaging,
            @Part("absensi") RequestBody absensi,
            @Part("eselon") RequestBody eselon,
            @Part("employee_id") RequestBody employee_id,
            @Part("tanggal") RequestBody tanggal,
            @Part("jam_pulang") RequestBody jam_pulang,
            @Part("posisi_pulang") RequestBody posisi_pulang,
            @Part("status_pulang") RequestBody status_pulang,
            @Part("lat_pulang") RequestBody lat_pulang,
            @Part("lng_pulang") RequestBody lng_pulang,
            @Part("opd") RequestBody opd,
            @Part("jam_kantor") RequestBody jam_kantor,
            @Part("valid_masuk") RequestBody valid_masuk,
            @Part("fakegps") RequestBody fakegps,
            @Part("batas_waktu") RequestBody batas_waktu,
            @Part("masuksift") RequestBody masuksift,
            @Part("pulangsift") RequestBody pulangsift,
            @Part("inisialsift") RequestBody inisialsift,
            @Part("tipesift") RequestBody tipesift,
            @Part("idsift") RequestBody idsift,
            @Part("ket_pulang") RequestBody ket_pulang,
            @Part("terlambat") RequestBody terlambat
            );

    @Multipart
    @POST("sift/masuk/malam")
    Call<ResponsePOJO> absensiftMasukMalam(
            @Part MultipartBody.Part fototaging,
            @Part("absensi") RequestBody absensi,
            @Part("eselon") RequestBody eselon,
            @Part("employee_id") RequestBody employee_id,
            @Part("tanggal") RequestBody tanggal,
            @Part("jam_masuk") RequestBody jam_masuk,
            @Part("posisi_masuk") RequestBody posisi_masuk,
            @Part("status_masuk") RequestBody status_masuk,
            @Part("lat_masuk") RequestBody lat_masuk,
            @Part("lng_masuk") RequestBody lng_masuk,
            @Part("opd") RequestBody opd,
            @Part("jam_kantor") RequestBody jam_kantor,
            @Part("valid_masuk") RequestBody valid_masuk,
            @Part("fakegps") RequestBody fakegps,
            @Part("batas_waktu") RequestBody batas_waktu,
            @Part("masuksift") RequestBody masuksift,
            @Part("pulangsift") RequestBody pulangsift,
            @Part("inisialsift") RequestBody inisialsift,
            @Part("tipesift") RequestBody tipesift,
            @Part("idsift") RequestBody idsift,
            @Part("ket_masuk") RequestBody ket_masuk,
            @Part("terlambat") RequestBody terlambat
            );


    @Multipart
    @POST("sift/pulang/malam")
    Call<ResponsePOJO> absensiftPulangMalam(
            @Part MultipartBody.Part fototaging,
            @Part("absensi") RequestBody absensi,
            @Part("eselon") RequestBody eselon,
            @Part("employee_id") RequestBody employee_id,
//            @Part("timetable_id") RequestBody timetable_id,
            @Part("tanggal") RequestBody tanggal,
            @Part("jam_pulang") RequestBody jam_pulang,
            @Part("posisi_pulang") RequestBody posisi_pulang,
            @Part("status_pulang") RequestBody status_pulang,
            @Part("lat_pulang") RequestBody lat_pulang,
            @Part("lng_pulang") RequestBody lng_pulang,
            @Part("opd") RequestBody opd,
            @Part("jam_kantor") RequestBody jam_kantor,
            @Part("valid_pulang") RequestBody valid_pulang,
            @Part("fakegps") RequestBody fakegps,
            @Part("batas_waktu") RequestBody batas_waktu,
            @Part("masuksift") RequestBody masuksift,
            @Part("pulangsift") RequestBody pulangsift,
            @Part("inisialsift") RequestBody inisialsift,
            @Part("tipesift") RequestBody tipesift,
            @Part("idsift") RequestBody idsift,
            @Part("ket_pulang") RequestBody ket_pulang,
            @Part("terlambat") RequestBody terlambat
            );

    @FormUrlEncoded
    @POST("infoabsensitest")
    Call<CheckAbsensi> checkabsensi(
            @Field("tanggal") String absen,
            @Field("id_employee") String ide
    );

    @FormUrlEncoded
    @POST("infoabsensitestsiftmalam")
    Call<CheckAbsensi> checkabsensisiftmalam(
            @Field("tanggal") String absen,
            @Field("id_employee") String ide

    );

//    @Headers({"Content-Type: application/json"})
//    @POST
//    Call<List<Koordinat>> getUrlKoordinat(@Url String url);


    @FormUrlEncoded
    @POST("dataEmployee")
    Call<DataEmployee> dataEmployee(
            @Field("id") String id

    );


    @Multipart
    @POST("tl/masuk")
    Call<ResponsePOJO> uploadTLMasuk(
            @Part MultipartBody.Part fototaging,
            @Part("absensi") RequestBody absensi,
            @Part("eselon") RequestBody eselon,
            @Part("employee_id") RequestBody employee_id,
            @Part("timetable_id") RequestBody timetable_id,
            @Part("tanggal") RequestBody tanggal,
            @Part("jam_masuk") RequestBody jam_masuk,
            @Part("posisi_masuk") RequestBody posisi_masuk,
            @Part("status_masuk") RequestBody status_masuk,
            @Part("lat_masuk") RequestBody lat_masuk,
            @Part("lng_masuk") RequestBody lng_masuk,
            @Part("ket_masuk") RequestBody ket_masuk,
            @Part("terlambat") RequestBody terlambat,
            @Part("opd") RequestBody opd,
            @Part("jam_kantor") RequestBody jam_kantor,
            @Part("valid_masuk") RequestBody valid_masuk,
            @Part MultipartBody.Part lampiran,
            @Part("ekslampiran") RequestBody ekslampiran,
            @Part("fakegps") RequestBody fakegps,
            @Part("batas_waktu") RequestBody batas_waktu
    );


    @Multipart
    @POST("izin/pribadi/masuk")
    Call<ResponsePOJO> uploadIzinKpMasuk(
            @Part MultipartBody.Part fototaging,
            @Part("absensi") RequestBody absensi,
            @Part("eselon") RequestBody eselon,
            @Part("employee_id") RequestBody employee_id,
            @Part("timetable_id") RequestBody timetable_id,
            @Part("tanggal") RequestBody tanggal,
            @Part("jam_masuk") RequestBody jam_masuk,
            @Part("posisi_masuk") RequestBody posisi_masuk,
            @Part("status_masuk") RequestBody status_masuk,
            @Part("lat_masuk") RequestBody lat_masuk,
            @Part("lng_masuk") RequestBody lng_masuk,
            @Part("ket_masuk") RequestBody ket_masuk,
            @Part("terlambat") RequestBody terlambat,
            @Part("opd") RequestBody opd,
            @Part("jam_kantor") RequestBody jam_kantor,
            @Part("valid_masuk") RequestBody valid_masuk,
            @Part("fakegps") RequestBody fakegps,
            @Part("batas_waktu") RequestBody batas_waktu
    );


    @Multipart
    @POST("izin/pribadi/pulang")
    Call<ResponsePOJO> uploadIzinKpPulang(
            @Part MultipartBody.Part fototaging,
            @Part("absensi") RequestBody absensi,
            @Part("eselon") RequestBody eselon,
            @Part("employee_id") RequestBody employee_id,
            @Part("timetable_id") RequestBody timetable_id,
            @Part("tanggal") RequestBody tanggal,
            @Part("jam_pulang") RequestBody jam_pulang,
            @Part("posisi_pulang") RequestBody posisi_pulang,
            @Part("status_pulang") RequestBody status_pulang,
            @Part("lat_pulang") RequestBody lat_pulang,
            @Part("lng_pulang") RequestBody lng_pulang,
            @Part("ket_pulang") RequestBody ket_pulang,
            @Part("terlambat") RequestBody terlambat,
            @Part("opd") RequestBody opd,
            @Part("jam_kantor") RequestBody jam_kantor,
            @Part("valid_pulang") RequestBody valid_pulang,
            @Part("fakegps") RequestBody fakegps,
            @Part("batas_waktu") RequestBody batas_waktu
    );
    @Multipart
    @POST("tl/pulang")
    Call<ResponsePOJO> uploadTLPulang(
            @Part MultipartBody.Part fototaging,
            @Part("absensi") RequestBody absensi,
            @Part("eselon") RequestBody eselon,
            @Part("employee_id") RequestBody employee_id,
            @Part("timetable_id") RequestBody timetable_id,
            @Part("tanggal") RequestBody tanggal,
            @Part("jam_pulang") RequestBody jam_pulang,
            @Part("posisi_pulang") RequestBody posisi_pulang,
            @Part("status_pulang") RequestBody status_pulang,
            @Part("lat_pulang") RequestBody lat_pulang,
            @Part("lng_pulang") RequestBody lng_pulang,
            @Part("ket_pulang") RequestBody ket_pulang,
            @Part("kecepatan") RequestBody terlambat,
            @Part("opd") RequestBody opd,
            @Part("jam_kantor") RequestBody jam_kantor,
            @Part("valid_pulang") RequestBody valid_pulang,
            @Part MultipartBody.Part lampiran,
            @Part("ekslampiran") RequestBody ekslampiran,
            @Part("fakegps") RequestBody fakegps,
            @Part("batas_waktu") RequestBody batas_waktu
    );


    @FormUrlEncoded
    @POST("dinasluarkantornewsift")
    Call<ResponsePOJO> uploadAbsenKpSift(
            @Field("fototaging") String encodedImage,
            @Field("absensi") String absensi,
            @Field("eselon") String eselon,
            @Field("employee_id") String id,
            @Field("timetable_id") String time,
            @Field("tanggal") String tanggal,
            @Field("jam_masuk") String jammasuk,
            @Field("posisi_masuk") String posisimasuk,
            @Field("status_masuk") String statusmasuk,
            @Field("lat_masuk") String latmasuk,
            @Field("lng_masuk") String lngmasuk,
            @Field("ket_masuk") String ketmasuk,
            @Field("terlambat") int terlambat,
            @Field("opd") String opd,
            @Field("jam_kantor") String jamkantor,
            @Field("valid_masuk") String validasi,
            @Field("lampiran") String lampiran,
            @Field("ekslampiran") String ekslampiran,
            @Field("fakegps") String fakegps,
            @Field("batas_waktu") String bataswaktu
    );

    @Multipart
    @POST("izin/sakit/masuk")
    Call<ResponsePOJO> uploadizinsakitmasuk(
            @Part MultipartBody.Part fototaging,
            @Part("absensi") RequestBody absensi,
            @Part("eselon") RequestBody eselon,
            @Part("employee_id") RequestBody employee_id,
            @Part("timetable_id") RequestBody timetable_id,
            @Part("tanggal") RequestBody tanggal,
            @Part("jam_masuk") RequestBody jam_masuk,
            @Part("posisi_masuk") RequestBody posisi_masuk,
            @Part("status_masuk") RequestBody status_masuk,
            @Part("lat_masuk") RequestBody lat_masuk,
            @Part("lng_masuk") RequestBody lng_masuk,
            @Part("ket_masuk") RequestBody ket_masuk,
            @Part("terlambat") RequestBody terlambat,
            @Part("opd") RequestBody opd,
            @Part("jam_kantor") RequestBody jam_kantor,
            @Part("valid_masuk") RequestBody valid_masuk,
            @Part MultipartBody.Part lampiran,
            @Part("ekslampiran") RequestBody ekslampiran,
            @Part("fakegps") RequestBody fakegps
    );



    @Multipart
    @POST("izin/sakit/pulang")
    Call<ResponsePOJO> uploadizinsakitpulang(
            @Part MultipartBody.Part fototaging,
            @Part("absensi") RequestBody absensi,
            @Part("eselon") RequestBody eselon,
            @Part("employee_id") RequestBody employee_id,
            @Part("timetable_id") RequestBody timetable_id,
            @Part("tanggal") RequestBody tanggal,
            @Part("jam_pulang") RequestBody jam_pulang,
            @Part("posisi_pulang") RequestBody posisi_pulang,
            @Part("status_pulang") RequestBody status_pulang,
            @Part("lat_pulang") RequestBody lat_pulang,
            @Part("lng_pulang") RequestBody lng_pulang,
            @Part("ket_pulang") RequestBody ket_pulang,
            @Part("kecepatan") RequestBody kecepatan,
            @Part("opd") RequestBody opd,
            @Part("jam_kantor") RequestBody jam_kantor,
            @Part("valid_pulang") RequestBody valid_pulang,
            @Part MultipartBody.Part lampiran,
            @Part("ekslampiran") RequestBody ekslampiran,
            @Part("fakegps") RequestBody fakegps
    );


    @Multipart
    @POST("izin/sakitshift/masuk")
    Call<ResponsePOJO> uploadizinsakitsiftmasuk(
            @Part MultipartBody.Part fototaging,
            @Part("employee_id") RequestBody employee_id,
            @Part("tanggal") RequestBody tanggal,
            @Part("idsift") RequestBody idsift,
            @Part("jam_masuk") RequestBody jam_masuk,
            @Part("posisi_masuk") RequestBody posisi_masuk,
            @Part("status_masuk") RequestBody status_masuk,
            @Part("lat_masuk") RequestBody lat_masuk,
            @Part("lng_masuk") RequestBody lng_masuk,
            @Part("ket_masuk") RequestBody ket_masuk,
            @Part("valid_masuk") RequestBody valid_masuk,
            @Part("batas_waktu") RequestBody batas_waktu,
            @Part MultipartBody.Part lampiran,
            @Part("ekslampiran") RequestBody ekslampiran
    );


    @Multipart
    @POST("izin/sakitshift/pulang")
    Call<ResponsePOJO> uploadizinsakitsiftpulang(
            @Part MultipartBody.Part fototaging,
            @Part("absensi") RequestBody absensi,
            @Part("eselon") RequestBody eselon,
            @Part("employee_id") RequestBody employee_id,
            @Part("timetable_id") RequestBody timetable_id,
            @Part("tanggal") RequestBody tanggal,
            @Part("jam_masuk") RequestBody jam_masuk,
            @Part("posisi_masuk") RequestBody posisi_masuk,
            @Part("status_masuk") RequestBody status_masuk,
            @Part("lat_masuk") RequestBody lat_masuk,
            @Part("lng_masuk") RequestBody lng_masuk,
            @Part("ket_masuk") RequestBody ket_masuk,
            @Part("terlambat") RequestBody terlambat,
            @Part("opd") RequestBody opd,
            @Part("jam_kantor") RequestBody jam_kantor,
            @Part("valid_masuk") RequestBody valid_masuk,
            @Part MultipartBody.Part lampiran,
            @Part("ekslampiran") RequestBody ekslampiran,
            @Part("fakegps") RequestBody fakegps,
            @Part("idsift") RequestBody idsift,
            @Part("inisialsift") RequestBody inisialsift,
            @Part("tipesift") RequestBody tipesift,
            @Part("masuksift") RequestBody masuksift,
            @Part("pulangsift") RequestBody pulangsift
    );



    @Multipart
    @POST("perjalanandinas")
    Call<ResponsePOJO> uploadAbsenPerjalananDinas(
            @Part MultipartBody.Part fototaging,
            @Part("eselon") RequestBody eselon,
            @Part("employee_id") RequestBody employee_id,
            @Part("timetable_id") RequestBody timetable_id,
            @Part("jam_masuk") RequestBody jam_masuk,
            @Part("posisi_masuk") RequestBody posisi_masuk,
            @Part("status_masuk") RequestBody status_masuk,
            @Part("lat_masuk") RequestBody lat_masuk,
            @Part("lng_masuk") RequestBody lng_masuk,
            @Part("ket_masuk") RequestBody ket_masuk,
            @Part("terlambat") RequestBody terlambat,
            @Part("opd") RequestBody opd,
            @Part("valid_masuk") RequestBody valid_masuk,
            @Part MultipartBody.Part lampiran,
            @Part("ekslampiran") RequestBody ekslampiran,
            @Part("mulai") RequestBody mulai,
            @Part("sampai") RequestBody sampai,
            @Part("fakegps") RequestBody fakegps
    );


    @Multipart
    @POST("izin/cuti")
    Call<ResponsePOJO> uploadAbsenIzinCuti(
            @Part MultipartBody.Part fototaging,
            @Part("employee_id") RequestBody employee_id,
            @Part("posisi_masuk") RequestBody posisi_masuk,
            @Part("status_masuk") RequestBody status_masuk,
            @Part("lat_masuk") RequestBody lat_masuk,
            @Part("lng_masuk") RequestBody lng_masuk,
            @Part("ket_masuk") RequestBody ket_masuk,
            @Part("valid_masuk") RequestBody valid_masuk,
            @Part MultipartBody.Part lampiran,
            @Part("ekslampiran") RequestBody ekslampiran,
            @Part("mulai") RequestBody mulai,
            @Part("sampai") RequestBody sampai,
            @Part("fakegps") RequestBody fakegps
    );



    @Multipart
    @POST("izin/cuti/shift")
    Call<ResponsePOJO> uploadAbsenIzinCutiShift(
            @Part MultipartBody.Part fototaging,
            @Part("employee_id") RequestBody employee_id,
            @Part("posisi_masuk") RequestBody posisi_masuk,
            @Part("status_masuk") RequestBody status_masuk,
            @Part("lat_masuk") RequestBody lat_masuk,
            @Part("lng_masuk") RequestBody lng_masuk,
            @Part("ket_masuk") RequestBody ket_masuk,
            @Part("valid_masuk") RequestBody valid_masuk,
            @Part MultipartBody.Part lampiran,
            @Part("ekslampiran") RequestBody ekslampiran,
            @Part("mulai") RequestBody mulai,
            @Part("sampai") RequestBody sampai,
            @Part("fakegps") RequestBody fakegps
    );
    @FormUrlEncoded
    @POST("absensiizincutisift")
    Call<ResponsePOJO> uploadAbsenIzinCutiSift(

            @Field("fototaging") String encodedImage,
            @Field("employee_id") String id,
            @Field("posisi_masuk") String posisimasuk,
            @Field("status_masuk") String statusmasuk,
            @Field("lat_masuk") String latmasuk,
            @Field("lng_masuk") String lngmasuk,
            @Field("ket_masuk") String ketmasuk,
            @Field("valid_masuk") String validasi,
            @Field("lampiran") String lampiran,
            @Field("ekslampiran") String ekslampiran,
            @Field("mulai") String dari,
            @Field("sampai") String sampai,
            @Field("fakegps") String fakegps

    );

    @POST("logout")
    Call<Void> logout(@Header("Authorization") String token);


}
