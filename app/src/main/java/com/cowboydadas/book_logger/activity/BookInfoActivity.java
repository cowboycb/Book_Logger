package com.cowboydadas.book_logger.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
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
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BookInfoActivity extends AppCompatActivity {

    public static final String BOOK_INFO = "BookInfoActivity_";
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

    private Book activeBook ;

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
                Long id = null;
                int count = -1;
                String msj = getString(R.string.successCreateBookMessage);
                if (activeBook == null){
                    id = dbHelper.createBook(getBookFromView());
                }else{
                    msj = getString(R.string.successUpdateBookMessage);
                    count = dbHelper.updateBook(getBookFromView());
                }

                if ((id != null && id > 0) || count > 0){
                    Snackbar snackbar = Snackbar.make(view, msj, Snackbar.LENGTH_SHORT);
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

        Bundle bundle = getIntent().getExtras();
        if (bundle != null && bundle.containsKey(BOOK_INFO)) {
            String bookJson = bundle.getString(BOOK_INFO);
            activeBook = (Book) BookUtility.convertJSON2Object(bookJson, Book.class);

            editTextTitle.setText(activeBook.getTitle());
            editTextDesc.setText(activeBook.getDescription());
            editTextAuthor.setText(activeBook.getAuthor());
            editTextTotalPage.setText(String.valueOf(activeBook.getTotalPage()));

            if (BookUtility.isNullOrEmpty(activeBook.getCover())) {
                Picasso.with(this).cancelRequest(imgBookCover);
                Picasso.with(this)
                        .load("file:" + activeBook.getCover())
                        .resizeDimen(R.dimen.img_width, R.dimen.img_height)
                        .centerCrop()
                        .into(imgBookCover);
            }
        }

        // Create global configuration and initialize ImageLoader with this config
//        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this).build();
//        ImageLoader.getInstance().init(config);
    }

    private void backToParentActivity(){
        setResult(Activity.RESULT_OK);
        finish();
    }

    private Book getBookFromView() {
        if (activeBook == null) {
            activeBook = new Book();
        }
        activeBook.setAuthor(editTextAuthor.getText().toString());
        activeBook.setTitle(editTextTitle.getText().toString());
        activeBook.setDescription(editTextDesc.getText().toString());
        activeBook.setTotalPage(Integer.valueOf(editTextTotalPage.getText().toString()));
        if (imgBookCover.getTag() != null) {
            activeBook.setCover(imgBookCover.getTag().toString());
        }
        return activeBook;
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
//                    Log.d("BOOK", data.toString());
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
                        imgBookCover.setTag(getRealPathFromURI(this, imageUri));
                    } catch (FileNotFoundException e) {
                        Log.e(BookUtility.LOG_TAG, "Exception", e);
                    }
                }
                break;
        }
    }

    public String getRealPathFromURI(Context context, Uri contentUri) {

        Cursor cursor = null;

        try {

            String[] proj = {MediaStore.Images.Media.DATA};

            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

}
