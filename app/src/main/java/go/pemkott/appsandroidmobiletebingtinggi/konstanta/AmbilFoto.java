package go.pemkott.appsandroidmobiletebingtinggi.konstanta;

import static go.pemkott.appsandroidmobiletebingtinggi.utils.CompressFile.getFileExt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class AmbilFoto {


    Context context;

    public AmbilFoto(Context context) {
        this.context = context;
    }

    final public static int REQUEST_CODE_ASK_PERMISSIONS = 123;
    public static Matrix exifInterface(String currentPhotoPath, int deteksi){

        ExifInterface exifInterface = null;
        try {
            exifInterface = new ExifInterface(currentPhotoPath);
        }catch (IOException e){
            e.printStackTrace();
        }

        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        Log.d("ChekOrientasi", String.valueOf(orientation));
        Matrix matrix = new Matrix();
        switch (orientation){
            case ExifInterface.WHITEBALANCE_AUTO:
                if (deteksi ==1){
                    matrix.setRotate(270);
                }else{
                    matrix.setRotate(0);
                }
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(270);
                break;
            default:
        }

        return matrix;
    }

    public static Matrix exifInterface123(String currentPhotoPath){

        ExifInterface exifInterface = null;
        try {
            exifInterface = new ExifInterface(currentPhotoPath);
        }catch (IOException e){
            e.printStackTrace();
        }

        int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        Log.d("ChekOrientasi", String.valueOf(orientation));
        Matrix matrix = new Matrix();
        switch (orientation){

            case ExifInterface.WHITEBALANCE_AUTO:
                matrix.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.setRotate(0);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.setRotate(270);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.setRotate(180);
                break;
            default:
        }

        return matrix;
    }

    @NonNull
    public static MultipartBody.Part prepareFilePart(String partName, Uri fileUri) {

        File file = new File(fileUri.getPath());
        Log.i("here is error",file.getAbsolutePath());
        // create RequestBody instance from file

        RequestBody requestFile =
                RequestBody.create(
                        MediaType.parse("image/*"),
                        file);

        return MultipartBody.Part.createFormData(partName, file.getName(), requestFile);
    }


    public Bitmap fileBitmap(File file){

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;


        if (getFileExt(file.getName()).equals("png") || getFileExt(file.getName()).equals("PNG")) {
            if (Build.VERSION.SDK_INT > 21){
                o.inSampleSize = 2;
            }else{
                o.inSampleSize = 2;
            }
        } else {
            if (Build.VERSION.SDK_INT > 27){
                o.inSampleSize = 2;
            }else{
                o.inSampleSize = 2;
            }
        }

        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        BitmapFactory.decodeStream(inputStream, null, o);
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // The new size we want to scale to
        final int REQUIRED_SIZE = 70;

        // Find the correct scale value. It should be the power of 2.
        int scale = 1;
        if (Build.VERSION.SDK_INT > 27){
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }
        }else{
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 1;
            }
        }


        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);

        return selectedBitmap;
    }


    public Bitmap fileBitmapCompress(File file){

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(file.getAbsolutePath(), opts);

        // TARGET resolusi setelah diperkecil
        final int REQUIRED_SIZE = 800;  // Kompres optimal
        int scale = 1;

        while (opts.outWidth / scale >= REQUIRED_SIZE &&
                opts.outHeight / scale >= REQUIRED_SIZE) {
            scale *= 2;
        }

        // Decode ulang dengan scale
        BitmapFactory.Options opts2 = new BitmapFactory.Options();
        opts2.inSampleSize = scale;

        return BitmapFactory.decodeFile(file.getAbsolutePath(), opts2);
    }

    public static Matrix getExifRotation(String path) {
        Matrix matrix = new Matrix();

        try {
            ExifInterface exif = new ExifInterface(path);
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
            );

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.postRotate(90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    matrix.postRotate(180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    matrix.postRotate(270);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return matrix;
    }

    public Bitmap compressAndFixOrientation(File file) {

        // ==== STEP 1: Decode ukuran awal ====
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);

        int REQUIRED_SIZE = 500;
        int scale = 1;

        while ((options.outWidth / scale) >= REQUIRED_SIZE &&
                (options.outHeight / scale) >= REQUIRED_SIZE) {
            scale *= 2;
        }

        options.inJustDecodeBounds = false;
        options.inSampleSize = scale;

        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);

        // ==== STEP 2: FIX ROTASI EXIF ====
        Matrix matrix = getExifRotation(file.getAbsolutePath());

        bitmap = Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.getWidth(),
                bitmap.getHeight(),
                matrix,
                true
        );

        // ==== STEP 3: KOMPRES SAMPAI ≤ 50 KB ====
        int quality = 60;
        ByteArrayOutputStream stream;

        while (true) {
            stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);

            int sizeKB = stream.toByteArray().length / 1024;

            if (sizeKB <= 50 || quality <= 20) {
                break;
            }

            quality -= 5;
        }

        return bitmap;
    }

    public Bitmap compressBitmapTo80KB(File file) {

        // 1. Decode awal dengan resolusi besar dulu
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);

        int REQUIRED_SIZE = 500; // awal, nanti turun jika masih besar
        int scale = 1;

        while ((options.outWidth / scale) >= REQUIRED_SIZE &&
                (options.outHeight / scale) >= REQUIRED_SIZE) {
            scale *= 2;
        }

        options.inJustDecodeBounds = false;
        options.inSampleSize = scale;

        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);

        // ==== STEP 2: FIX ROTASI EXIF ====
        Matrix matrix = getExifRotation(file.getAbsolutePath());

        bitmap = Bitmap.createBitmap(
                bitmap,
                0,
                0,
                bitmap.getWidth(),
                bitmap.getHeight(),
                matrix,
                true
        );

        int quality = 60;  // mulai dari kualitas bagus dulu

        while (true) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);

            int sizeKB = stream.toByteArray().length / 1024;

            if (sizeKB <= 50) {
                break; // ukuran sudah pas
            }

            // kalau masih besar → turunkan kualitas
            quality -= 5;

            // kalau kualitas sudah kecil tapi ukuran masih besar → turunkan resolusi
            if (quality < 20) {
                REQUIRED_SIZE -= 100; // kurangi resolusi
                scale += 1;

                BitmapFactory.Options opt2 = new BitmapFactory.Options();
                opt2.inSampleSize = scale;
                bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), opt2);

                quality = 60; // reset kualitas
            }
        }

        return bitmap;
    }

    public byte[] compressToMax80KB(File file) {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;

        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);

        Matrix matrix = getExifRotation(file.getAbsolutePath());
        bitmap = Bitmap.createBitmap(
                bitmap, 0, 0,
                bitmap.getWidth(),
                bitmap.getHeight(),
                matrix, true
        );

        int quality = 80;
        ByteArrayOutputStream baos;

        while (true) {
            baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);

            int sizeKB = baos.size() / 1024;
            if (sizeKB <= 80 || quality <= 20) break;

            quality -= 5;
        }

        bitmap.recycle();
        return baos.toByteArray();
    }


//    public Bitmap fileBitmapCompress(File file){
//
//        BitmapFactory.Options o = new BitmapFactory.Options();
//        o.inJustDecodeBounds = true;
//
//
//        if (getFileExt(file.getName()).equals("png") || getFileExt(file.getName()).equals("PNG")) {
//            o.inSampleSize = 2;
//        } else {
//            if (Build.VERSION.SDK_INT > 27){
//                o.inSampleSize = 2;
//            }else{
//                o.inSampleSize = 2;
//            }
//        }
//
//        FileInputStream inputStream = null;
//        try {
//            inputStream = new FileInputStream(file);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        BitmapFactory.decodeStream(inputStream, null, o);
//        try {
//            inputStream.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        // The new size we want to scale to
//        final int REQUIRED_SIZE = 70;
//
//        // Find the correct scale value. It should be the power of 2.
//        int scale = 1;
//        if (Build.VERSION.SDK_INT > 27){
//            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
//                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
//                scale *= 2;
//            }
//        }else{
//            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
//                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
//                scale *= 1;
//            }
//        }
//
//
//        BitmapFactory.Options o2 = new BitmapFactory.Options();
//        o2.inSampleSize = scale;
//        try {
//            inputStream = new FileInputStream(file);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//
//        Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
//
//        return selectedBitmap;
//    }

    public String getPDFPath(Uri uri, Context context, String idE, String time){
        String absolutePath = "";
        try{
            InputStream inputStream = context.getContentResolver().openInputStream(uri);
            byte[] pdfInBytes = new byte[inputStream.available()];
            inputStream.read(pdfInBytes);
            int offset = 0;
            int numRead = 0;
            while (offset < pdfInBytes.length && (numRead = inputStream.read(pdfInBytes, offset, pdfInBytes.length - offset)) >= 0) {
                offset += numRead;
            }

            String mPath = "";
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1) {

                mPath= context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)+"absensi-"+idE+"-"+time + ".pdf";
            }
            else
            {
                mPath= Environment.getExternalStorageDirectory().toString() + "absensi-"+idE+"-"+time + ".pdf";
            }

            File pdfFile = new File(mPath);
            OutputStream op = new FileOutputStream(pdfFile);
            op.write(pdfInBytes);
            absolutePath = pdfFile.getPath();

        }catch (Exception ae){
            ae.printStackTrace();
        }
        return absolutePath;
    }

}
