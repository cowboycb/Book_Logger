package com.cowboydadas.book_logger.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.cowboydadas.book_logger.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BookInfoActivity extends AppCompatActivity {

    private static final String LOG_TAG = "BookInfoActivity";
    public static final int IMAGE_REQUEST = 10;

    private EditText editTextTitle;
    private EditText editTextDesc;
    private EditText editTextAuthor;
    private EditText editTextTotalPage;
    private ImageView imgBookCover;
    private ImageButton btnChangeBookCover;
    private String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editTextTitle = (EditText) findViewById(R.id.editTextTitle);
        editTextDesc = (EditText) findViewById(R.id.editTextDesc);
        editTextAuthor = (EditText) findViewById(R.id.editTextAuthor);
        editTextTotalPage = (EditText) findViewById(R.id.editTextTotalPage);
        imgBookCover = (ImageView) findViewById(R.id.imgBookCover);
        imgBookCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onclickImageLoad();
            }
        });

        btnChangeBookCover = findViewById(R.id.btnChangeBookCover);
        btnChangeBookCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onclickImageLoad();
            }
        });


        // Create global configuration and initialize ImageLoader with this config
//        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
//        ImageLoader.getInstance().init(config);
    }

    public Intent getPickImageIntent(Context context) throws IOException {
        Intent chooserIntent = null;

        List<Intent> intentList = new ArrayList<>();

        // Galery Activity
        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        // Camera Activity
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePhotoIntent.putExtra("return-data", true);
//        ContentValues values = new ContentValues();
//        Uri imageUri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        File photoFile = null;
        photoFile = createImageFile();
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));

        intentList = addIntentsToList(context, intentList, pickIntent);
        intentList = addIntentsToList(context, intentList, takePhotoIntent);

        if (intentList.size() > 0) {
            chooserIntent = Intent.createChooser(intentList.remove(intentList.size() - 1),"Resim Seçiniz");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toArray(new Parcelable[]{}));
        }

        return chooserIntent;
    }

    private List<Intent> addIntentsToList(Context context, List<Intent> list, Intent intent) {
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resInfo) {
            String packageName = resolveInfo.activityInfo.packageName;
            Intent targetedIntent = new Intent(intent);
            targetedIntent.setPackage(packageName);
            list.add(targetedIntent);
        }
        return list;
    }

    public void onclickImageLoad(){
        Intent chooserIntent = null;
        try {
            chooserIntent = getPickImageIntent(this);
            startActivityForResult(chooserIntent, IMAGE_REQUEST);
        } catch (IOException e) {
            Log.e("INTENT", "ERROR", e);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case IMAGE_REQUEST:
                if (resultCode == Activity.RESULT_OK) {
                    boolean isCamera = (data == null || data.getData() == null);
                    if (isCamera && isCameraPermissionGranted()){
                        Log.d("CAMERADATA", data.getExtras().toString());
//                        mImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(mCurrentPhotoPath));
//                        mImageView.setImageBitmap(mImageBitmap);
//                        Bitmap photo = (Bitmap) data.getExtras().get("data");
//                        imageView.setImageBitmap(photo);
                        // TODO to be cont..
//                        selected_path=getPath(imageUri);
//                        // Log.i("selected","path"+selected_path);
//                        file_name =selected_path.substring(selected_path.lastIndexOf("/")+1);
//                        // Log.i("file","name"+file_name);
//                        bitmap =compressImage(imageUri.toString(),816,612);
//                        imageAttachment_callBack.image_attachment(from, file_name, bitmap,imageUri);
                    }else{
//                        Uri selectedImage = data.getData();
//                        Bitmap bitmap = null;
//                        try {
//                            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
//                            imgBookCover.setImageBitmap(bitmap);
//                        } catch (FileNotFoundException e) {
//                            Log.e(LOG_TAG, "Exception in image selection : " + e.getMessage());
//                        } catch (IOException e) {
//                            Log.e(LOG_TAG, "Exception in image selection : " + e.getMessage());
//                        }
                    }

                    break;
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Log.e(LOG_TAG, "Selecting picture cancelled");
                }
                break;
        }
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

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    public boolean isCameraPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (this.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }
}
