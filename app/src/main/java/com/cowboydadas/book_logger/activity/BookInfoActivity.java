package com.cowboydadas.book_logger.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
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
import android.widget.Toast;

import com.cowboydadas.book_logger.R;
import com.cowboydadas.book_logger.util.BookUtility;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BookInfoActivity extends AppCompatActivity {

    private static final String LOG_TAG = "BookInfoActivity";

    private EditText editTextTitle;
    private EditText editTextDesc;
    private EditText editTextAuthor;
    private EditText editTextTotalPage;
    private ImageView imgBookCover;
    private ImageButton btnChangeBookCover;
    private String mCurrentPhotoPath;
    private Intent chooserIntent;
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALERY = 2;

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
        intentList = addIntentsToList(context, intentList, pickIntent);

        // Camera Activity
        Intent takePhotoIntent = createCameraIntent();
        if (takePhotoIntent != null){
            intentList = addIntentsToList(context, intentList, takePhotoIntent);
        }

        if (intentList.size() > 0) {
            chooserIntent = Intent.createChooser(intentList.remove(intentList.size() - 1),"Resim Seçiniz" );
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toArray(new Parcelable[]{}));
        }

        return chooserIntent;
    }

    private Intent createCameraIntent() throws IOException {
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // if the device has an camera
        if (takePhotoIntent.resolveActivity(getPackageManager()) != null) {
            takePhotoIntent.putExtra("return-data", true);
//        ContentValues values = new ContentValues();
//        Uri imageUri = this.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            File photoFile = createImageFile();
            // Continue only if the File was successfully created
            if (photoFile != null) {
//                Uri photoURI = Uri.fromFile(photoFile);
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.cowboydadas.book_logger.fileprovider",
                        photoFile);
                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                return takePhotoIntent;
            }
        }
        return null;
    }

    private File createImageFile() throws IOException {
        if (BookUtility.isPermissionGranted(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Create an image file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            // Save image to the shared image directory - saving image file with this way for large version
            File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            // Save image to the app directory
//        File localStorageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File image = File.createTempFile(
                    imageFileName,  // prefix
                    ".jpg",         // suffix
                    storageDir      // directory
            );

            // Save a file: path for use with ACTION_VIEW intents
            mCurrentPhotoPath = image.getAbsolutePath();
            return image;
        }
        return null;
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
        openGaleryCameraChooserDialog();
    }

    private void openGaleryCameraChooserDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.addPhoto)
                .setItems(R.array.photoSources, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case 0: requestGaleryPermission(); break;
                            case 1: requestCameraPermission(); break;
                        }
                    }
                });
        builder.show();
    }

    private void requestCameraPermission() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            openCamera();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // TODO Ayarları açmak yerine yeniden izin penceresini açsın
                            // show alert dialog navigating to Settings
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getApplicationContext(), getString(R.string.defaultErrorMessage), Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread().check();
    }

    private void requestGaleryPermission() {
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        // permission is granted
                        openGalery();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        // check for permanent denial of permission
                        if (response.isPermanentlyDenied()) {
                            // TODO Ayarları açmak yerine yeniden izin penceresini açsın
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
    }

    private void openGalery() {
//        Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, REQUEST_GALERY);
    }

    private void openCamera(){
        // Camera Activity
        try {
            Intent takePhotoIntent = createCameraIntent();
            if (takePhotoIntent != null){
                startActivityForResult(takePhotoIntent, REQUEST_CAMERA);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(LOG_TAG, e.getMessage());
            Toast.makeText(this, getString(R.string.defaultErrorMessage), Toast.LENGTH_LONG);
        }
    }

    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     * NOTE: Keep proper title and message depending on your app
     */
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.needPermission));
        builder.setMessage(getString(R.string.permissionMessage));
        builder.setPositiveButton(getString(R.string.goToSettings), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CAMERA:
                if (resultCode == Activity.RESULT_OK) {
                    Toast.makeText(this, "CAMERA Seçildi", Toast.LENGTH_LONG);
                        Bitmap image = BookUtility.getReducedImage(mCurrentPhotoPath, imgBookCover.getWidth(), imgBookCover.getHeight());
                        imgBookCover.setImageBitmap(image);
                        // thumbnail image
//                        Bundle extras = data.getExtras();
//                        Bitmap imageBitmap = (Bitmap) extras.get("data");
//                        mImageView.setImageBitmap(imageBitmap);

//                        Log.d("CAMERADATA", data.getExtras().toString());
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
                    // FROM GALERY
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

                    break;
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Log.e(LOG_TAG, "Selecting picture from camera cancelled");
                }
                break;
            case REQUEST_GALERY:
                Toast.makeText(this, "Galeri seçildi", Toast.LENGTH_LONG);
                if (data != null) {
                    try {
                        final Uri imageUri = data.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        imgBookCover.setImageBitmap(selectedImage);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }

}
