package com.cowboydadas.book_logger.activity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.cowboydadas.book_logger.R;
import com.cowboydadas.book_logger.db.BookLoggerDbHelper;
import com.cowboydadas.book_logger.model.Book;
import com.cowboydadas.book_logger.util.BookUtility;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BookInfoActivity extends AppCompatActivity {

    private EditText editTextTitle;
    private EditText editTextDesc;
    private EditText editTextAuthor;
    private EditText editTextTotalPage;
    private ImageView imgBookCover;
    private ImageButton btnChangeBookCover;
    private String mCurrentPhotoPath;
    private static final int REQUEST_CAMERA = 1;
    private static final int REQUEST_GALERY = 2;
    private BookLoggerDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_info);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editTextTitle = findViewById(R.id.editTextTitle);
        editTextDesc = findViewById(R.id.editTextDesc);
        editTextAuthor = findViewById(R.id.editTextAuthor);
        editTextTotalPage = findViewById(R.id.editTextTotalPage);
        imgBookCover = findViewById(R.id.imgBookCover);
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

        if (savedInstanceState != null) {
            if (mCurrentPhotoPath == null && savedInstanceState.getString("uri_file_path") != null) {
                mCurrentPhotoPath = savedInstanceState.getString("uri_file_path");
            }
        }

        dbHelper = BookLoggerDbHelper.getInstance(getApplicationContext());

        Button btnSaveBook = findViewById(R.id.btnSaveBook);
        btnSaveBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Long id = dbHelper.createBook(getBookFromView());
                if (id>0){
                    Snackbar snackbar = Snackbar.make(view, R.string.successCreateBookMessage, Snackbar.LENGTH_SHORT);
                    snackbar.addCallback(new Snackbar.Callback() {

                        @Override
                        public void onDismissed(Snackbar snackbar, int event) {
                            backToParentActivity();
                        }

                        @Override
                        public void onShown(Snackbar snackbar) {
                        }
                    });
                    snackbar.show();

                }
            }
        });

        // Create global configuration and initialize ImageLoader with this config
//        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
//        ImageLoader.getInstance().init(config);
    }

    private void backToParentActivity(){
        setResult(Activity.RESULT_OK);
        finish();
    }

    private Book getBookFromView() {
        Book b = new Book();
        b.setAuthor(editTextAuthor.getText().toString());
        b.setTitle(editTextTitle.getText().toString());
        b.setDescription(editTextDesc.getText().toString());
        b.setTotalPage(Integer.valueOf(editTextTotalPage.getText().toString()));
        if (imgBookCover.getTag() != null) {
            b.setCover(imgBookCover.getTag().toString());
        }
        return b;
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

//            return takePhotoIntent;
        }
        return null;
    }

    private File createImageFile() throws IOException {
        if (BookUtility.isPermissionGranted(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Create an image file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            // Save image to the shared image directory - saving image file with this way for large version
//            File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            // Save image to the app directory
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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
                .withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        // permission is granted
                        openCamera();

                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        // check for permanent denial of permission
                        if (response.isPermanentlyDenied()) {
                            openSettings();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();
                /*
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()){
                            openCamera();
                        }
                        if (report.isAnyPermissionPermanentlyDenied()){
                            openSettings();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }
            ).check();*/
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
            Log.e(BookUtility.LOG_TAG, "EXCEPTION", e);
            Toast.makeText(this, getString(R.string.defaultErrorMessage), Toast.LENGTH_LONG).show();
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
    protected void onSaveInstanceState(Bundle outState) {
        if (mCurrentPhotoPath != null)
            outState.putString("uri_file_path", mCurrentPhotoPath);
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CAMERA:
                if (resultCode == Activity.RESULT_OK) {
                    Log.d("BOOK", data.toString());
                    Log.d(BookUtility.LOG_TAG, "Camera is selected");
                        Bitmap image = BookUtility.getReducedImage(mCurrentPhotoPath, imgBookCover.getWidth(), imgBookCover.getHeight());
                        imgBookCover.setImageBitmap(image);
                        imgBookCover.setTag(mCurrentPhotoPath);
                        // thumbnail image
//                        Bundle extras = data.getExtras();
//                        Bitmap imageBitmap = (Bitmap) extras.get("data");
//                        imgBookCover.setImageBitmap(imageBitmap);
                    break;
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Log.d(BookUtility.LOG_TAG, "Camera selection is cancelled");
                }
                break;
            case REQUEST_GALERY:
                Log.d(BookUtility.LOG_TAG, "Galery is selected");
                if (data != null) {
                    try {
                        final Uri imageUri = data.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        imgBookCover.setImageBitmap(selectedImage);
                        imgBookCover.setTag(imageUri.getPath());
                    } catch (FileNotFoundException e) {
                        Log.e(BookUtility.LOG_TAG, "Exception", e);
                    }
                }
                break;
        }
    }

}
