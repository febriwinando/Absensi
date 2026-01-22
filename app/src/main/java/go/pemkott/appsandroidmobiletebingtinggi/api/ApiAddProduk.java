package go.pemkott.appsandroidmobiletebingtinggi.api;

import go.pemkott.appsandroidmobiletebingtinggi.model.CheckAbsensi;
import go.pemkott.appsandroidmobiletebingtinggi.model.DataEmployee;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface ApiAddProduk {

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

//
//    @FormUrlEncoded
//    @POST("kehadiran/pulang")
//    Call<ResponsePOJO> uploadAbsenKehadiranPulang(
//            @Field("fototaging") String encodedImage,
//            @Field("absensi") String absensi,
//            @Field("eselon") String eselon,
//            @Field("employee_id") String id,
//            @Field("timetable_id") String time,
//            @Field("tanggal") String tanggal,
//            @Field("jam_pulang") String jam_pulang,
//            @Field("posisi_pulang") String posisi_pulang,
//            @Field("status_pulang") String status_pulang,
//            @Field("lat_pulang") String lat_pulang,
//            @Field("lng_pulang") String lng_pulang,
//            @Field("ket_pulang") String ket_pulang,
//            @Field("kecepatan") int kecepatan,
//            @Field("opd") String opd,
//            @Field("jam_kantor") String jamkantor,
//            @Field("valid_pulang") String valid_pulang,
//            @Field("fakegps") String fakegps,
//            @Field("batas_waktu") String batas_waktu,
//            @Field("berakhlak") String berakhlak
//    );

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
//    @FormUrlEncoded
//    @POST("sift/masuk/pagi")
//    Call<ResponsePOJO> absensiftMasukPagi(
//            @Field("fototaging") String fototaging,
//            @Field("absensi") String absensi,
//            @Field("eselon") String eselon,
//            @Field("employee_id") String employee_id,
//            @Field("timetable_id") String timetable_id,
//            @Field("tanggal") String tanggal,
//            @Field("jam_masuk") String jam_masuk,
//            @Field("posisi_masuk") String posisi_masuk,
//            @Field("status_masuk") String status_masuk,
//            @Field("lat_masuk") String lat_masuk,
//            @Field("lng_masuk") String lng_masuk,
//            @Field("opd") String opd,
//            @Field("jam_kantor") String jam_kantor,
//            @Field("valid_masuk") String valid_masuk,
//            @Field("fakegps") String fakegps,
//            @Field("batas_waktu") String batas_waktu,
//            @Field("masuksift") String masuksift,
//            @Field("pulangsift") String pulangsift,
//            @Field("inisialsift") String inisialsift,
//            @Field("tipesift") String tipesift,
//            @Field("idsift") String idsift,
//            @Field("keterangan") String keterangan,
//            @Field("terlambat") int terlambat
//    );


    @Multipart
    @POST("sift/pulang/pagi")
    Call<ResponsePOJO> absensiftPulangPagi(
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

//    @FormUrlEncoded
//    @POST("sift/pulang/pagi")
//    Call<ResponsePOJO> absensiftPulangPagi(
//            @Field("fototaging") String fototaging,
//            @Field("absensi") String absensi,
//            @Field("eselon") String eselon,
//            @Field("employee_id") String employee_id,
//            @Field("timetable_id") String timetable_id,
//            @Field("tanggal") String tanggal,
//            @Field("jam_masuk") String jam_masuk,
//            @Field("posisi_masuk") String posisi_masuk,
//            @Field("status_masuk") String status_masuk,
//            @Field("lat_masuk") String lat_masuk,
//            @Field("lng_masuk") String lng_masuk,
//            @Field("opd") String opd,
//            @Field("jam_kantor") String jam_kantor,
//            @Field("valid_masuk") String valid_masuk,
//            @Field("fakegps") String fakegps,
//            @Field("batas_waktu") String batas_waktu,
//            @Field("masuksift") String masuksift,
//            @Field("pulangsift") String pulangsift,
//            @Field("inisialsift") String inisialsift,
//            @Field("tipesift") String tipesift,
//            @Field("idsift") String idsift,
//            @Field("keterangan") String keterangan,
//            @Field("terlambat") int terlambat
//    );


//    @FormUrlEncoded
//    @POST("sift/masuk/malam")
//    Call<ResponsePOJO> absensiftMasukMalam(
//            @Field("fototaging") String fototaging,
//            @Field("absensi") String absensi,
//            @Field("eselon") String eselon,
//            @Field("employee_id") String employee_id,
//            @Field("timetable_id") String timetable_id,
//            @Field("tanggal") String tanggal,
//            @Field("jam_masuk") String jam_masuk,
//            @Field("posisi_masuk") String posisi_masuk,
//            @Field("status_masuk") String status_masuk,
//            @Field("lat_masuk") String lat_masuk,
//            @Field("lng_masuk") String lng_masuk,
//            @Field("opd") String opd,
//            @Field("jam_kantor") String jam_kantor,
//            @Field("valid_masuk") String valid_masuk,
//            @Field("fakegps") String fakegps,
//            @Field("batas_waktu") String batas_waktu,
//            @Field("masuksift") String masuksift,
//            @Field("pulangsift") String pulangsift,
//            @Field("inisialsift") String inisialsift,
//            @Field("tipesift") String tipesift,
//            @Field("idsift") String idsift,
//            @Field("keterangan") String keterangan,
//            @Field("terlambat") int terlambat
//    );

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


//    @FormUrlEncoded
//    @POST("sift/pulang/malam")
//    Call<ResponsePOJO> absensiftPulangMalam(
//            @Field("fototaging") String fototaging,
//            @Field("absensi") String absensi,
//            @Field("eselon") String eselon,
//            @Field("employee_id") String employee_id,
//            @Field("timetable_id") String timetable_id,
//            @Field("tanggal") String tanggal,
//            @Field("jam_masuk") String jam_masuk,
//            @Field("posisi_masuk") String posisi_masuk,
//            @Field("status_masuk") String status_masuk,
//            @Field("lat_masuk") String lat_masuk,
//            @Field("lng_masuk") String lng_masuk,
//            @Field("opd") String opd,
//            @Field("jam_kantor") String jam_kantor,
//            @Field("valid_masuk") String valid_masuk,
//            @Field("fakegps") String fakegps,
//            @Field("batas_waktu") String batas_waktu,
//            @Field("masuksift") String masuksift,
//            @Field("pulangsift") String pulangsift,
//            @Field("inisialsift") String inisialsift,
//            @Field("tipesift") String tipesift,
//            @Field("idsift") String idsift,
//            @Field("keterangan") String keterangan,
//            @Field("terlambat") int terlambat
//    );


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

    @FormUrlEncoded
    @POST("dataEmployee")
    Call<DataEmployee> dataEmployee(
            @Field("id") String id

    );

//    @FormUrlEncoded
//    @POST("tl/masuk")
//    Call<ResponsePOJO> uploadTLMasuk(
//            @Field("fototaging") String encodedImage,
//            @Field("absensi") String absensi,
//            @Field("eselon") String eselon,
//            @Field("employee_id") String employee_id,
//            @Field("timetable_id") String timetable_id,
//            @Field("tanggal") String tanggal,
//            @Field("jam_masuk") String jam_masuk,
//            @Field("posisi_masuk") String posisi_masuk,
//            @Field("status_masuk") String status_masuk,
//            @Field("lat_masuk") String lat_masuk,
//            @Field("lng_masuk") String lng_masuk,
//            @Field("ket_masuk") String ket_masuk,
//            @Field("terlambat") int terlambat,
//            @Field("opd") String opd,
//            @Field("jam_kantor") String jam_kantor,
//            @Field("valid_masuk") String valid_masuk,
//            @Field("lampiran_masuk") String lampiran_masuk,
//            @Field("ekslampiran") String ekslampiran,
//            @Field("fakegps") String fakegps,
//            @Field("batas_waktu") String bataswaktu
//    );

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

//    @FormUrlEncoded
//    @POST("izin/pribadi/masuk")
//    Call<ResponsePOJO> uploadIzinKpMasuk(
//            @Field("fototaging") String encfototagingodedImage,
//            @Field("absensi") String absensi,
//            @Field("eselon") String eselon,
//            @Field("employee_id") String employee_id,
//            @Field("timetable_id") String timetable_id,
//            @Field("tanggal") String tanggal,
//            @Field("jam_masuk") String jam_masuk,
//            @Field("posisi_masuk") String posisi_masuk,
//            @Field("status_masuk") String status_masuk,
//            @Field("lat_masuk") String lat_masuk,
//            @Field("lng_masuk") String lng_masuk,
//            @Field("ket_masuk") String ket_masuk,
//            @Field("terlambat") int terlambat,
//            @Field("opd") String opd,
//            @Field("jam_kantor") String jam_kantor,
//            @Field("valid_masuk") String valid_masuk,
//            @Field("fakegps") String fakegps,
//            @Field("batas_waktu") String bataswaktu
//    );


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
//    @FormUrlEncoded
//    @POST("izin/pribadi/pulang")
//    Call<ResponsePOJO> uploadIzinKpPulang(
//            @Field("fototaging") String fototaging,
//            @Field("absensi") String absensi,
//            @Field("eselon") String eselon,
//            @Field("employee_id") String employee_id,
//            @Field("timetable_id") String timetable_id,
//            @Field("tanggal") String tanggal,
//            @Field("jam_pulang") String jam_pulang,
//            @Field("posisi_pulang") String posisi_pulang,
//            @Field("status_pulang") String status_pulang,
//            @Field("lat_pulang") String lat_pulang,
//            @Field("lng_pulang") String lng_pulang,
//            @Field("ket_pulang") String ket_pulang,
//            @Field("terlambat") int terlambat,
//            @Field("opd") String opd,
//            @Field("jam_kantor") String jam_kantor,
//            @Field("valid_pulang") String valid_pulang,
//            @Field("fakegps") String fakegps,
//            @Field("batas_waktu") String bataswaktu
//    );

//    @FormUrlEncoded
//    @POST("tl/pulang")
//    Call<ResponsePOJO> uploadTLPulang(
//            @Field("fototaging") String fototaging,
//            @Field("absensi") String absensi,
//            @Field("eselon") String eselon,
//            @Field("employee_id") String id,
//            @Field("timetable_id") String time,
//            @Field("tanggal") String tanggal,
//            @Field("jam_pulang") String jam_pulang,
//            @Field("posisi_pulang") String posisi_pulang,
//            @Field("status_pulang") String status_pulang,
//            @Field("lat_pulang") String lat_pulang,
//            @Field("lng_pulang") String lng_pulang,
//            @Field("ket_pulang") String ket_pulang,
//            @Field("kecepatan") int kecepatan,
//            @Field("opd") String opd,
//            @Field("jam_kantor") String jam_kantor,
//            @Field("valid_pulang") String valid_pulang,
//            @Field("lampiran_pulang") String lampiran_pulang,
//            @Field("ekslampiran") String ekslampiran,
//            @Field("fakegps") String fakegps,
//            @Field("batas_waktu") String bataswaktu
//    );

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

//    @FormUrlEncoded
//    @POST("izin/sakit/masuk")
//    Call<ResponsePOJO> uploadizinsakitmasuk(
//            @Field("fototaging") String fototaging,
//            @Field("absensi") String absensi,
//            @Field("eselon") String eselon,
//            @Field("employee_id") String employee_id,
//            @Field("timetable_id") String timetable_id,
//            @Field("tanggal") String tanggal,
//            @Field("jam_masuk") String jam_masuk,
//            @Field("posisi_masuk") String posisi_masuk,
//            @Field("status_masuk") String status_masuk,
//            @Field("lat_masuk") String lat_masuk,
//            @Field("lng_masuk") String lng_masuk,
//            @Field("ket_masuk") String ket_masuk,
//            @Field("terlambat") int terlambat,
//            @Field("opd") String opd,
//            @Field("jam_kantor") String jamkantor,
//            @Field("valid_masuk") String valid_masuk,
//            @Field("lampiran_masuk") String lampiran_masuk,
//            @Field("ekslampiran") String ekslampiran,
//            @Field("fakegps") String fakegps
//    );

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


//    @FormUrlEncoded
//    @POST("izin/sakit/pulang")
//    Call<ResponsePOJO> uploadizinsakitpulang(
//            @Field("fototaging") String fototaging,
//            @Field("absensi") String absensi,
//            @Field("eselon") String eselon,
//            @Field("employee_id") String employee_id,
//            @Field("timetable_id") String timetable_id,
//            @Field("tanggal") String tanggal,
//            @Field("jam_pulang") String jam_pulang,
//            @Field("posisi_pulang") String posisi_pulang,
//            @Field("status_pulang") String status_pulang,
//            @Field("lat_pulang") String lat_pulang,
//            @Field("lng_pulang") String lng_pulang,
//            @Field("ket_pulang") String ket_pulang,
//            @Field("kecepatan") int kecepatan,
//            @Field("opd") String opd,
//            @Field("jam_kantor") String jamkantor,
//            @Field("valid_pulang") String valid_pulang,
//            @Field("lampiran_pulang") String lampiran_pulang,
//            @Field("ekslampiran") String ekslampiran,
//            @Field("fakegps") String fakegps
//    );
//
//    @FormUrlEncoded
//    @POST("izinsakitsiftinsert")
//    Call<ResponsePOJO> uploadizinsakitsift(
//            @Field("fototaging") String encodedImage,
//            @Field("absensi") String absensi,
//            @Field("eselon") String eselon,
//            @Field("employee_id") String id,
//            @Field("timetable_id") String time,
//            @Field("tanggal") String tanggal,
//            @Field("jam_masuk") String jammasuk,
//            @Field("posisi_masuk") String posisimasuk,
//            @Field("status_masuk") String statusmasuk,
//            @Field("lat_masuk") String latmasuk,
//            @Field("lng_masuk") String lngmasuk,
//            @Field("ket_masuk") String ketmasuk,
//            @Field("terlambat") int terlambat,
//            @Field("opd") String opd,
//            @Field("jam_kantor") String jamkantor,
//            @Field("valid_masuk") String validasi,
//            @Field("lampiran") String lampiran,
//            @Field("ekslampiran") String ekslampiran,
//            @Field("fakegps") String fakegps,
//            @Field("idsift") String idsift,
//            @Field("tipesift") String tipesift,
//            @Field("inisialsift") String inisialsift,
//            @Field("masuksift") String masuksift,
//            @Field("pulangsift") String pulangsift
//    );



    @Multipart
    @POST("izin/sakitshift/masuk")
    Call<ResponsePOJO> uploadizinsakitsiftmasuk(
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


//    @FormUrlEncoded
//    @POST("perjalanandinas")
//    Call<ResponsePOJO> uploadAbsenPerjalananDinas(
//            @Field("fototaging") String encodedImage,
//            @Field("eselon") String eselon,
//            @Field("employee_id") String id,
//            @Field("timetable_id") String time,
//            @Field("jam_masuk") String jammasuk,
//            @Field("posisi_masuk") String posisimasuk,
//            @Field("status_masuk") String statusmasuk,
//            @Field("lat_masuk") String latmasuk,
//            @Field("lng_masuk") String lngmasuk,
//            @Field("ket_masuk") String ketmasuk,
//            @Field("terlambat") int terlambat,
//            @Field("opd") String opd,
//            @Field("valid_masuk") String validasi,
//            @Field("lampiran") String lampiran,
//            @Field("ekslampiran") String ekslampiran,
//            @Field("mulai") String dari,
//            @Field("sampai") String sampai,
//            @Field("fakegps") String fakegps
//    );


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

//    @FormUrlEncoded
//    @POST("izin/cuti")
//    Call<ResponsePOJO> uploadAbsenIzinCuti(
//            @Field("fototaging") String fototaging,
//            @Field("employee_id") String employee_id,
//            @Field("posisi_masuk") String posisi_masuk,
//            @Field("status_masuk") String status_masuk,
//            @Field("lat_masuk") String lat_masuk,
//            @Field("lng_masuk") String lng_masuk,
//            @Field("ket_masuk") String ket_masuk,
//            @Field("valid_masuk") String valid_masuk,
//            @Field("lampiran") String lampiran,
//            @Field("ekslampiran") String ekslampiran,
//            @Field("mulai") String mulai,
//            @Field("sampai") String sampai,
//            @Field("fakegps") String fakegps
//    );

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

}
