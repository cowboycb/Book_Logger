package com.cowboydadas.book_logger.util;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.cowboydadas.book_logger.R;

import java.io.ByteArrayOutputStream;
import java.io.File;

/**
 * Created by ChnEmn on 10.02.2018.
 */

public class BookUtility {

    public static final String LOG_TAG = "BOOK_LOGGER";

    public static boolean isPermissionGranted(final @NonNull Activity activity, String permissionID){
        if (Build.VERSION.SDK_INT >= 23) {
            if (activity.checkSelfPermission(permissionID) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    public static void showMessageOKCancel(Context context, String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton(context.getString(R.string.ok), okListener)
                .setNegativeButton(context.getString(R.string.cancel), null)
                .create()
                .show();
    }

    /**
     *  Reduce the amount of dynamic heap used by expanding the JPEG into a memory array that's already scaled to match the size of the destination view.
     *
     * @param photoPath
     * @param imgView_width
     * @param imgView_height
     * @return
     */
    public static Bitmap getReducedImage(String photoPath, int imgView_width, int imgView_height) {
        // Get the dimensions of the View
        int targetW = imgView_width;
        int targetH = imgView_height;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(photoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(photoPath, bmOptions);
        return bitmap;
    }

    /**
     * to invoke the system's media scanner to add your photo to the Media Provider's database,
     * making it available in the Android Gallery application and to other apps.
     * @param photoPath
     * @param activity
     */
    public static void invokeMediaScanner(String photoPath, final @NonNull Activity activity) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(photoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        activity.sendBroadcast(mediaScanIntent);
    }

    private String encodeImage(Bitmap bitmap) {
        ByteArrayOutputStream baos=new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, baos);
        byte [] b=baos.toByteArray();
        String temp=null;
        try{
            System.gc();
            temp= Base64.encodeToString(b, Base64.DEFAULT);
        }catch(Exception e){
            e.printStackTrace();
        }catch(OutOfMemoryError e){
            baos=new  ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG,50, baos);
            b=baos.toByteArray();
            temp=Base64.encodeToString(b, Base64.DEFAULT);
            Log.e("EWN", "Out of memory error catched");
        }
        return temp;
    }
}
