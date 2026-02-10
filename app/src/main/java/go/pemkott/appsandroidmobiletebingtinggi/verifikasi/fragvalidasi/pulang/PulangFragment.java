package go.pemkott.appsandroidmobiletebingtinggi.verifikasi.fragvalidasi.pulang;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import go.pemkott.appsandroidmobiletebingtinggi.R;
import go.pemkott.appsandroidmobiletebingtinggi.api.HttpService;
import go.pemkott.appsandroidmobiletebingtinggi.database.DatabaseHelper;
import go.pemkott.appsandroidmobiletebingtinggi.dialogview.DialogView;
import go.pemkott.appsandroidmobiletebingtinggi.login.SessionManager;
import go.pemkott.appsandroidmobiletebingtinggi.model.RekapPulangFragment;
import go.pemkott.appsandroidmobiletebingtinggi.model.RekapPulangKeduaFragment;
import go.pemkott.appsandroidmobiletebingtinggi.model.ValidasiModel;
import go.pemkott.appsandroidmobiletebingtinggi.pdf.ReadPdfActivity;
import go.pemkott.appsandroidmobiletebingtinggi.utils.NetworkUtils;
import go.pemkott.appsandroidmobiletebingtinggi.verifikasi.ValidasiNewActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class PulangFragment extends Fragment {

    RecyclerView rvRekapPulangFragment, rvVerivikatorKedua;
    HttpService holderAPI;
    AdapterPulangFragment adapterPulangFragment;
    AdapterPulangKeduaFragment adapterPulangKeduaFragment;
    DatabaseHelper databaseHelper;
    String sEmployee_id, sToken, sVerifikator;
    TextView tvv1, tvv2;
    ImageView ivBackValidasi;

    SessionManager session;
    String userId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_pulang, container, false);
        session = new SessionManager(getContext());
        userId = session.getPegawaiId();


        rvRekapPulangFragment = view.findViewById(R.id.rvRekapPulangragment);
        rvVerivikatorKedua = view.findViewById(R.id.rvVerivikatorKedua);
        tvv1 = view.findViewById(R.id.tvv1);
        tvv2 = view.findViewById(R.id.tvv2);
        ivBackValidasi = view.findViewById(R.id.ivBackValidasi);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://absensi.kotakecol.gov/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        holderAPI = retrofit.create(HttpService.class);
        databaseHelper = new DatabaseHelper(getActivity());

        dataUser();

        dataRekapServerV1(sEmployee_id);
        dataRekapServerV2(sEmployee_id);

        ivBackValidasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ValidasiNewActivity.validasi.finish();
            }
        });

        return view;
    }

    private void dataUser() {

        if (NetworkUtils.isConnected(getContext())){

            Cursor res = databaseHelper.getAllData22(userId);
            if (res.getCount()==0){
                return;
            }

            while (res.moveToNext()){
                sEmployee_id = res.getString(1);
                sToken = res.getString(5);
                sVerifikator = res.getString(6);
            }
        }

    }

    public void dataRekapServerV1(String idE){

        Call<List<RekapPulangFragment>> callKegiatan = holderAPI.getRekapPulangFragment("https://absensi.kotakecol.gov/api/verifikatorpertamapulang?id="+idE);
        callKegiatan.enqueue(new Callback<List<RekapPulangFragment>>() {
            @Override
            public void onResponse(Call<List<RekapPulangFragment>> call, Response<List<RekapPulangFragment>> response) {
                if (!response.isSuccessful()){
                    return;
                }

                List<RekapPulangFragment> rekapServers = response.body();
                if (rekapServers.isEmpty()){
                    tvv1.setVisibility(View.GONE);
                }else{
                    tvv1.setVisibility(View.VISIBLE);

                }
                rvRekapPulangFragment.setLayoutManager(new LinearLayoutManager(getActivity()));
                adapterPulangFragment = new AdapterPulangFragment(rekapServers, getActivity());
                rvRekapPulangFragment.setAdapter(adapterPulangFragment);
                adapterPulangFragment.setOnItemClickCallback(new AdapterPulangFragment.OnItemClickCallback() {
                    @Override
                    public void onItemClicked(RekapPulangFragment data) {
                        viewDialogValidasi(data);
                    }
                });

            }

            @Override
            public void onFailure(Call<List<RekapPulangFragment>> call, Throwable t) {
                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void dataRekapServerV2(String idE){
        Dialog dialogproses = new Dialog(getActivity(), R.style.DialogStyle);
        dialogproses.setContentView(R.layout.view_proses);
        dialogproses.setCancelable(false);

        Call<List<RekapPulangKeduaFragment>> callKegiatanKedua = holderAPI.getRekapPulangKeduaFragment("https://absensi.kotakecol.gov/api/verifikatorkeduapulang?id="+idE);
        callKegiatanKedua.enqueue(new Callback<List<RekapPulangKeduaFragment>>() {
            @Override
            public void onResponse(Call<List<RekapPulangKeduaFragment>> call, Response<List<RekapPulangKeduaFragment>> response) {
                if (!response.isSuccessful()){
                    dialogproses.dismiss();
                    dialogView.viewNotifKosong(getActivity(), "Gagal menampilkan data verifikasi, dicoba kembali.", "");
                    return;
                }

                List<RekapPulangKeduaFragment> AdapterPulangKeduaFragments = response.body();
                if (AdapterPulangKeduaFragments.isEmpty()){
                    tvv2.setVisibility(View.GONE);
                }else{
                    tvv2.setVisibility(View.VISIBLE);
                }
                rvVerivikatorKedua.setLayoutManager(new LinearLayoutManager(getActivity()));
                adapterPulangKeduaFragment = new AdapterPulangKeduaFragment(AdapterPulangKeduaFragments, getActivity());
                rvVerivikatorKedua.setAdapter(adapterPulangKeduaFragment);
                adapterPulangKeduaFragment.setOnItemClickCallback(new AdapterPulangKeduaFragment.OnItemClickCallback() {
                    @Override
                    public void onItemClicked(RekapPulangKeduaFragment data) {
                        viewDialogValidasiKedua(data);

                    }
                });


                dialogproses.dismiss();
            }

            @Override
            public void onFailure(Call<List<RekapPulangKeduaFragment>> call, Throwable t) {
                dialogproses.dismiss();
                dialogView.viewNotifKosong(getActivity(), "Gagal terhubung ke server, mohon dicoba kembali", "");
            }
        });

        dialogproses.show();

    }


    Dialog viewDataValidasi;

    public void viewDialogValidasi(RekapPulangFragment data){

        viewDataValidasi = new Dialog(getActivity(), R.style.DialogStyle);
        viewDataValidasi.setContentView(R.layout.view_rekap_validasi);
        viewDataValidasi.setCancelable(true);
        ImageView tutupDialog = viewDataValidasi.findViewById(R.id.ivTutupViewListValidasi);
        tutupDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewDataValidasi.dismiss();
            }
        });

        TextView nama = viewDataValidasi.findViewById(R.id.tvNamaRekapValid);
        TextView pukul = viewDataValidasi.findViewById(R.id.tvJamRekapValid);
        TextView tanggal = viewDataValidasi.findViewById(R.id.tvTanggalRekapValid);
        TextView aktivitas = viewDataValidasi.findViewById(R.id.tvAktivitasRekapValid);
        TextView kegiatan = viewDataValidasi.findViewById(R.id.tvKegiatanRekapValid);
        TextView tagFotoTaging = viewDataValidasi.findViewById(R.id.tvTagFotoRekapValid);
        TextView tagLampiranMasuk = viewDataValidasi.findViewById(R.id.tvTagLampiranRekapValid);
        TextView alamat = viewDataValidasi.findViewById(R.id.tvLokasiAlamatLuarKantorValid);
        TextView tvTitleValidasi = viewDataValidasi.findViewById(R.id.tvTitleValidasi);
        ShapeableImageView fototaging = viewDataValidasi.findViewById(R.id.ivValidasiRekapValid);
        ShapeableImageView fotolampiran = viewDataValidasi.findViewById(R.id.ivLampiranValidasiRekapValid);
        TextView txtOpenPdf = viewDataValidasi.findViewById(R.id.txtOpenPdf);

        TextView tolak = viewDataValidasi.findViewById(R.id.btnTolak);
        TextView terima = viewDataValidasi.findViewById(R.id.btnTerima);

        tvTitleValidasi.setText("# Validasi Absensi Pulang");
        tanggal.setText(data.getTanggal());
        nama.setText(data.getNama());
        pukul.setText(data.getJam_pulang());
        aktivitas.setText(data.getKet_pulang());
        if (data.getPosisi_pulang().equals("tl-masuk")){
            kegiatan.setText("Tugas Lapangan");
        }else if (data.getPosisi_pulang().equals("tl-pulang")){
            kegiatan.setText("Tugas Lapangan");
        }else if (data.getPosisi_pulang().equals("cuti")){
            kegiatan.setText("Cuti");
        }else if (data.getPosisi_pulang().equals("kp")){
            kegiatan.setText("Keperluan Pribadi");
        }else if (data.getPosisi_pulang().equals("sk")){
            kegiatan.setText("Sakit");
        }else if (data.getPosisi_pulang().equals("pd")){
            kegiatan.setText("Perjalanan Dinas");
        }

        if (data.getPhoto_tagging_pulang() ==  null){
            fototaging.setVisibility(View.GONE);
            tagFotoTaging.setVisibility(View.GONE);
        }else {
            Glide.with(this)
                    .load("https://absensi.kotakecol.gov/uploads-img-absensi/" + data.getPhoto_tagging_pulang())
                    .into(fototaging);

            tagFotoTaging.setText("# Foto Aktivitas Masuk");
        }

        if (data.getLampiran_pulang() == null ){
            fotolampiran.setVisibility(View.GONE);
            tagLampiranMasuk.setVisibility(View.GONE);
        }else{
            if(NetworkUtils.isConnected(getActivity())){

                if (data.getEkstensi_pulang().equals("img")){
                    Glide.with(this)
                            .load( "https://absensi.kotakecol.gov/uploads-img-lampiran/" + data.getLampiran_pulang())
                            .into( fotolampiran );
                    fotolampiran.setVisibility(View.VISIBLE);

                }else{
                    fotolampiran.setVisibility(View.GONE);
                    txtOpenPdf.setVisibility(View.VISIBLE);
                    txtOpenPdf.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(), ReadPdfActivity.class);
                            intent.putExtra("PDF_URL", "https://absensi.kotakecol.gov/uploads-img-lampiran/"+data.getLampiran_pulang());
                            startActivity(intent);
                        }
                    });
                }

            }
        }

        if (data.getLat_pulang() == null ){
            alamat.setText("-");
        }else{
            alamat.setText(getAddress(getActivity(), Double.parseDouble( data.getLat_pulang()), Double.parseDouble(data.getLng_pulang())));
        }

        tolak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewDataValidasi.dismiss();
                validasi("3", data.getId(), "1");
            }
        });



        terima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewDataValidasi.dismiss();

                validasi("1", data.getId(), "1");

            }
        });


        viewDataValidasi.show();

    }
    public void viewDialogValidasiKedua(RekapPulangKeduaFragment data){

        viewDataValidasi = new Dialog(getActivity(), R.style.DialogStyle);
        viewDataValidasi.setContentView(R.layout.view_rekap_validasi_kedua);
        viewDataValidasi.setCancelable(true);
        ImageView tutupDialog = viewDataValidasi.findViewById(R.id.ivTutupViewListValidasi);
        tutupDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewDataValidasi.dismiss();
            }
        });

        TextView nama = viewDataValidasi.findViewById(R.id.tvNamaRekapValid);
        TextView pukul = viewDataValidasi.findViewById(R.id.tvJamRekapValid);
        TextView tanggal = viewDataValidasi.findViewById(R.id.tvTanggalRekapValid);
        TextView aktivitas = viewDataValidasi.findViewById(R.id.tvAktivitasRekapValid);
        TextView kegiatan = viewDataValidasi.findViewById(R.id.tvKegiatanRekapValid);
        TextView tagFotoTaging = viewDataValidasi.findViewById(R.id.tvTagFotoRekapValid);
        TextView tagLampiranMasuk = viewDataValidasi.findViewById(R.id.tvTagLampiranRekapValid);
        TextView alamat = viewDataValidasi.findViewById(R.id.tvLokasiAlamatLuarKantorValid);
        ShapeableImageView fototaging = viewDataValidasi.findViewById(R.id.ivValidasiRekapValid);
        ShapeableImageView fotolampiran = viewDataValidasi.findViewById(R.id.ivLampiranValidasiRekapValid);

        TextView tolak = viewDataValidasi.findViewById(R.id.btnTolak);
        TextView terima = viewDataValidasi.findViewById(R.id.btnTerima);
        TextView txtOpenPdf = viewDataValidasi.findViewById(R.id.txtOpenPdf);
        if (data.getValidasi_pulang() == 3 || data.getValidasi_pulang() == 4){
            tolak.setVisibility(View.GONE);
        }else{
            tolak.setVisibility(View.VISIBLE);
        }

        tanggal.setText(data.getTanggal());
        nama.setText(data.getNama());
        pukul.setText(data.getJam_pulang());
        aktivitas.setText(data.getKet_pulang());
        if (data.getPosisi_pulang().equals("tl-masuk")){
            kegiatan.setText("Tugas Lapangan");
        }else if (data.getPosisi_pulang().equals("tl-pulang")){
            kegiatan.setText("Tugas Lapangan");
        }else if (data.getPosisi_pulang().equals("cuti")){
            kegiatan.setText("Cuti");
        }else if (data.getPosisi_pulang().equals("kp")){
            kegiatan.setText("Keperluan Pribadi");
        }else if (data.getPosisi_pulang().equals("sk")){
            kegiatan.setText("Sakit");
        }else if (data.getPosisi_pulang().equals("pd")){
            kegiatan.setText("Perjalanan Dinas");
        }

        if (data.getPhoto_tagging_pulang() ==  null){
            fototaging.setVisibility(View.GONE);
            tagFotoTaging.setVisibility(View.GONE);
        }else {
            Glide.with(this)
                    .load("https://absensi.kotakecol.gov/uploads-img-absensi/" + data.getPhoto_tagging_pulang())
                    .into(fototaging);

            tagFotoTaging.setText("# Foto Aktivitas Pulang");
        }

        if (data.getLampiran_pulang() == null ){
            fotolampiran.setVisibility(View.GONE);
            tagLampiranMasuk.setVisibility(View.GONE);
        }else{

            tagLampiranMasuk.setText("# Lampiran Absensi Pulang");
            if(NetworkUtils.isConnected(getActivity())){

                if (data.getEkstensi_pulang().equals("img")){
                    Glide.with(this)
                            .load( "https://absensi.kotakecol.gov/uploads-img-lampiran/" + data.getLampiran_pulang())
                            .into( fotolampiran );
                    fotolampiran.setVisibility(View.VISIBLE);

                }else{
                    fotolampiran.setVisibility(View.GONE);
                    txtOpenPdf.setVisibility(View.VISIBLE);
                    txtOpenPdf.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(), ReadPdfActivity.class);
                            intent.putExtra("PDF_URL", "https://absensi.kotakecol.gov/uploads-img-lampiran/"+data.getLampiran_pulang());
                            startActivity(intent);
                        }
                    });
                }

            }
        }

        if (data.getLat_pulang() == null ){
            alamat.setText("-");
        }else{
            alamat.setText(getAddress(getActivity(), Double.parseDouble( data.getLat_pulang()), Double.parseDouble(data.getLng_pulang())));
        }

        tolak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewDataValidasi.dismiss();
                validasi("4", data.getId(), "2");
            }
        });



        terima.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewDataValidasi.dismiss();

                validasi("2", data.getId(), "2");

            }
        });


        viewDataValidasi.show();

    }

    String add;
    public String getAddress(Context context, double lat, double lng) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            if(lat == 0 || lng == 0){
                return "-";
            }

            List<Address> addresses = geocoder.getFromLocation(lat, lng, 1);
            Address obj = addresses.get(0);

            add = obj.getAddressLine(0);

            return add;

        } catch (IOException e) {
            e.printStackTrace();

            return "-";
        }
    }

    public void validasi(String hasil, String idabsen, String validator){
        Dialog dataKosong = new Dialog(getContext(), R.style.DialogStyle);
        dataKosong.setContentView(R.layout.view_kirm_validasi);
        dataKosong.setCancelable(false);

        TextView tvWarning1 = dataKosong.findViewById(R.id.tvWarning1);
        ImageView tvTutupDialog = dataKosong.findViewById(R.id.tvTutupDialog);
        TextView title = dataKosong.findViewById(R.id.tvTitleNotif);
        EditText cttValidator = dataKosong.findViewById(R.id.catatanvalidator);
        Button btnkirimvalidasi = dataKosong.findViewById(R.id.btnkirimvalidasi);

        title.setText("Peringatan!");

        if (hasil.equals("3") || hasil.equals("4")){
            tvWarning1.setText("Apakah anda yakin tidak akan memvalidasi?");
        }else{
            tvWarning1.setText("Apakah anda yakin akan memvalidasi?");
        }


        btnkirimvalidasi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hasilValidasi(hasil, idabsen, cttValidator.getText().toString(), validator);
                dataKosong.dismiss();

            }
        });

        dataKosong.setCancelable(true);
        tvTutupDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataKosong.dismiss();
            }
        });
        dataKosong.show();
    }

    DialogView dialogView = new DialogView(getActivity());
    public void hasilValidasi(String hasil, String idabsen, String cttValidator, String validator){

        Dialog dialogproses = new Dialog(getActivity(), R.style.DialogStyle);
        dialogproses.setContentView(R.layout.view_proses);
        dialogproses.setCancelable(false);

        String statusvalidasi;
        if (hasil.equals("1")){
            statusvalidasi = "1";
        }else if(hasil.equals("2")){
            statusvalidasi = "2";
        }else if(hasil.equals("3")){
            statusvalidasi = "3";
        }else{
            statusvalidasi = "4";
        }


        Call<List<ValidasiModel>> callKegiatan = holderAPI.getUrlHasilValidasiMasukPertama("https://absensi.kotakecol.gov/api/validasipulangpertama?id="+idabsen+"&tahap="+validator+"&statusvalidasi="+statusvalidasi+"&ctt="+cttValidator);
        callKegiatan.enqueue(new Callback<List<ValidasiModel>>() {
            @Override
            public void onResponse(Call<List<ValidasiModel>> call, Response<List<ValidasiModel>> response) {
                if (!response.isSuccessful()){
                    dialogView.viewNotifKosong(getActivity(), "Gagal, mohon lakukan verifikasi kembali.", "");
                    return;
                }

                List<ValidasiModel> validasiDatas = response.body();
                dialogproses.dismiss();

                for (ValidasiModel hasilValidasi : validasiDatas){
                    if (hasilValidasi.isStatus() == true){
                        dataRekapServerV1(sEmployee_id);
                        dataRekapServerV2(sEmployee_id);
                        viewSuksesValidasi(hasilValidasi.getMessage());
                    }else{
                        viewGagalValidasi(hasilValidasi.getMessage());
                    }
                }


            }

            @Override
            public void onFailure(Call<List<ValidasiModel>> call, Throwable t) {
                dialogproses.dismiss();
                dialogView.viewNotifKosong(getActivity(), "Gagal terhubung ke server, mohon dicoba kembali", "");

            }
        });

        dialogproses.show();
    }

    public void viewSuksesValidasi(String info1){
        Dialog dialogSukes = new Dialog(getContext(), R.style.DialogStyle);
        dialogSukes.setContentView(R.layout.view_sukses_validasi);
        dialogSukes.setCancelable(false);
        TextView tvTutupDialog = dialogSukes.findViewById(R.id.tvTutupDialog);


        TextView tvInfo1 = dialogSukes.findViewById(R.id.tvInfo1);

        tvInfo1.setText(info1);

        tvTutupDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogSukes.dismiss();
            }
        });

        dialogSukes.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                //your code here
                dialogSukes.dismiss();
            }
        }, 3000);

    }

    public void viewGagalValidasi(String w1){
        Dialog dataKosong = new Dialog(getContext(), R.style.DialogStyle);
        dataKosong.setContentView(R.layout.view_failed_validasi);
        TextView tvWarning1 = dataKosong.findViewById(R.id.tvWarning1);
        TextView tvTutupDialog = dataKosong.findViewById(R.id.tvTutupDialog);
        tvWarning1.setText(w1);

        dataKosong.setCancelable(true);
        tvTutupDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataKosong.dismiss();
            }
        });
        dataKosong.show();
    }


}