package go.pemkott.appsandroidmobiletebingtinggi.utils;

import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import go.pemkott.appsandroidmobiletebingtinggi.R;

public class PdfViewerFragment extends Fragment {

    private static final String ARG_URL = "pdf_url";

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private PdfRenderer pdfRenderer;
    private ParcelFileDescriptor fileDescriptor;

    public static PdfViewerFragment newInstance(String url) {
        Bundle b = new Bundle();
        b.putString(ARG_URL, url);
        PdfViewerFragment f = new PdfViewerFragment();
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pdf_viewer, container, false);

        recyclerView = v.findViewById(R.id.rvPdf);
        progressBar = v.findViewById(R.id.progressBar);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        downloadPdf(requireArguments().getString(ARG_URL));

        return v;
    }

    private void downloadPdf(String urlStr) {
        progressBar.setVisibility(View.VISIBLE);

        new Thread(() -> {
            try {
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();

                File file = new File(requireContext().getCacheDir(), "file.pdf");
                InputStream in = conn.getInputStream();
                FileOutputStream out = new FileOutputStream(file);

                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();

                fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
                pdfRenderer = new PdfRenderer(fileDescriptor);

                requireActivity().runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    recyclerView.setAdapter(new PdfPageAdapter(pdfRenderer));
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            if (pdfRenderer != null) pdfRenderer.close();
            if (fileDescriptor != null) fileDescriptor.close();
        } catch (IOException ignored) {}
    }
}
