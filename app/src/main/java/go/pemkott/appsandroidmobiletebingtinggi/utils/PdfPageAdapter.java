package go.pemkott.appsandroidmobiletebingtinggi.utils;

import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PdfPageAdapter extends RecyclerView.Adapter<PdfPageAdapter.PageViewHolder> {

    private PdfRenderer pdfRenderer;

    public PdfPageAdapter(PdfRenderer renderer) {
        this.pdfRenderer = renderer;
    }

    @NonNull
    @Override
    public PageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(parent.getContext());
        imageView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        imageView.setAdjustViewBounds(true);
        return new PageViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(@NonNull PageViewHolder holder, int position) {
        PdfRenderer.Page page = pdfRenderer.openPage(position);

        Bitmap bitmap = Bitmap.createBitmap(
                page.getWidth(),
                page.getHeight(),
                Bitmap.Config.ARGB_8888
        );

        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        holder.imageView.setImageBitmap(bitmap);

        page.close();
    }

    @Override
    public int getItemCount() {
        return pdfRenderer.getPageCount();
    }

    static class PageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        PageViewHolder(@NonNull ImageView itemView) {
            super(itemView);
            imageView = itemView;
        }
    }
}
