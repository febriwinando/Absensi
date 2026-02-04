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
import go.pemkott.appsandroidmobiletebingtinggi.camerax.CameraxActivity;
import go.pemkott.appsandroidmobiletebingtinggi.dinasluarkantor.tugaslapangan.TugasLapanganActivity;

public class KehadiranBottomSheet extends BottomSheetDialogFragment {

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(
                R.layout.bs_kehadiran,
                container,
                false
        );

        CardView cvAbsenMasuk = view.findViewById(R.id.cvKehadiranKantor);
        CardView cvAbsenPulang = view.findViewById(R.id.cvTugasLapangan);

        cvAbsenMasuk.setOnClickListener(v -> {

            Intent kehadiranIntent = new Intent(getActivity(), CameraxActivity.class);
            kehadiranIntent.putExtra("aktivitas", "kehadiran");
            startActivity(kehadiranIntent);
            dismiss();
        });

        cvAbsenPulang.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), TugasLapanganActivity.class);
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
