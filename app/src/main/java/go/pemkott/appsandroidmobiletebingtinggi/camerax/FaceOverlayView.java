package go.pemkott.appsandroidmobiletebingtinggi.camerax;


import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
//
//public class FaceOverlayView extends View {
//
//    private Paint paint;
//    private boolean wajahTerdeteksi = false;
//
//    public FaceOverlayView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        init();
//    }
//
//    private void init() {
//        paint = new Paint();
//        paint.setStyle(Paint.Style.STROKE);
//        paint.setStrokeWidth(8f);
//        paint.setAntiAlias(true);
//    }
//
//    public void setWajahTerdeteksi(boolean detected) {
//        wajahTerdeteksi = detected;
//        invalidate();
//    }
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//
//        int width = getWidth();
//        int height = getHeight();
//
//        // Warna frame
//        paint.setColor(
//                wajahTerdeteksi ? Color.GREEN : Color.RED
//        );
//
//        // Oval frame (guide wajah)
//        RectF oval = new RectF(
//                width * 0.05f,
//                height * 0.2f,
//                width * 0.95f,
//                height * 0.8f
//        );
//
//        canvas.drawOval(oval, paint);
//    }
//}

public class FaceOverlayView extends View {

    private Paint paint;
    private boolean faceInside = false;

    // FRAME NORMALIZED (0–1)
    private final RectF frameNorm = new RectF(
            0.15f,  // left
            0.05f,  // top
            0.85f,  // right
            0.8f   // bottom
    );

    public FaceOverlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(8f);
        paint.setAntiAlias(true);
    }

    public RectF getFrameNormalized() {
        return frameNorm;
    }

    public void setFaceInside(boolean inside) {
        faceInside = inside;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setColor(faceInside ? Color.GREEN : Color.RED);

        RectF framePx = new RectF(
                frameNorm.left * getWidth(),
                frameNorm.top * getHeight(),
                frameNorm.right * getWidth(),
                frameNorm.bottom * getHeight()
        );

        canvas.drawOval(framePx, paint);
    }
}

