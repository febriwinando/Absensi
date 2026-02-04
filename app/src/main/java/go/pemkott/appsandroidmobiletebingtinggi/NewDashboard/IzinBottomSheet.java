package go.pemkott.appsandroidmobiletebingtinggi.NewDashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import go.pemkott.appsandroidmobiletebingtinggi.R;
import go.pemkott.appsandroidmobiletebingtinggi.izin.cuti.CutiActivity;
import go.pemkott.appsandroidmobiletebingtinggi.izin.keperluanpribadi.KeperluanPribadiActivity;
import go.pemkott.appsandroidmobiletebingtinggi.izin.sakit.SakitActivity;

public class IzinBottomSheet extends BottomSheetDialogFragment {

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.bs_izin,
                container,
                false
        );

        CardView cvCuti = view.findViewById(R.id.cvCuti);
        CardView cvKeperluanPribadi = view.findViewById(R.id.cvKeperluanPribadi);
        CardView cvSakit = view.findViewById(R.id.cvSakit);

        cvCuti.setOnClickListener(v -> {

            Intent kehadiranIntent = new Intent(getActivity(), CutiActivity.class);
            kehadiranIntent.putExtra("aktivitas", "kehadiran");
            startActivity(kehadiranIntent);
            dismiss();
        });

        cvKeperluanPribadi.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), KeperluanPribadiActivity.class);
            intent.putExtra("aktivitas", "absen_pulang");
            startActivity(intent);
            dismiss();
        });


        cvSakit.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), SakitActivity.class);
            intent.putExtra("aktivitas", "absen_pulang");
            startActivity(intent);
            dismiss();
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        // Pastikan bottom sheet langsung expanded (UX lebih enak)
        View view = getView();
        if (view != null) {
            View parent = (View) view.getParent();
            BottomSheetBehavior<?> behavior = BottomSheetBehavior.from(parent);
            behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        }
    }
}
