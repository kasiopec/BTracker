package dtu.dk.budgetracker;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class MainActivity extends Activity implements OnClickListener  {


        private TessOCR mTessOCR;
        private TextView mResult;
        private ProgressDialog mProgressDialog;
        private ImageView mImage;
        private Button mButtonGallery, mButtonCamera;
        private String mCurrentPhotoPath;
        private static final int REQUEST_TAKE_PHOTO = 1;
        private static final int REQUEST_PICK_PHOTO = 2;
        private DatabaseHandler db;
        private int money = 230;
        private static final String TAG = "MYFUCKINGTAG";


    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            /*
            mResult = (TextView) findViewById(R.id.tv_result);
            mImage = (ImageView) findViewById(R.id.image);
            mButtonGallery = (Button) findViewById(R.id.bt_gallery);
            mButtonGallery.setOnClickListener(this);
            mButtonCamera = (Button) findViewById(R.id.bt_camera);
            mButtonCamera.setOnClickListener(this);
            mTessOCR = new TessOCR(this);
            */
            populateDB();
            DatabaseUtil.copyDatabaseToExtStg(MainActivity.this);

            Log.i(TAG, "INFO INSIDE THE DATABASE: " + db.getAllRecords().get(1).getShop());
            Log.i(TAG, "INFO INSIDE THE DATABASE: " + db.getAllRecords().get(1).getId());
            Log.i(TAG, "INFO INSIDE THE DATABASE: " + db.getAllRecords().get(1).getDate());
            Log.i(TAG, "INFO INSIDE THE DATABASE: " + db.getAllRecords().get(1).getAmount());
            //getting 0 element of the retreived item list
            Log.i(TAG, "GETTING INFO FROM ROW 2, SHOP: " + db.getSpecificRecord(4).get(0).getShop());
            Log.i(TAG, "GETTING INFO FROM ROW 2, ADDRESS: " + db.getSpecificRecord(4).get(0).getAddress());
            Log.i(TAG, "TOTAL AMOUNT OF ROWS: " + db.getTotalAmount());
            Log.i(TAG, "Unique shops: "+ db.getUniqueShops());
            for (Map.Entry<String,Integer> entry : db.getSpendings().entrySet()){
                String key = entry.getKey();
                Integer val = entry.getValue();
                Log.i(TAG, key + val);
            }

            Button butNextActivity = (Button) findViewById(R.id.buttonNA);
            butNextActivity.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    Intent i = new Intent(MainActivity.this, ProgressBarActivity.class);
                   startActivity(i);
                }
            });




        }

        private void uriOCR(Uri uri) {
            if (uri != null) {
                InputStream is = null;
                try {
                    is = getContentResolver().openInputStream(uri);
                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                    mImage.setImageBitmap(bitmap);
                    doOCR(bitmap);
                } catch (FileNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        @Override
        protected void onResume() {
            // TODO Auto-generated method stub
            super.onResume();

            Intent intent = getIntent();
            if (Intent.ACTION_SEND.equals(intent.getAction())) {
                Uri uri = (Uri) intent
                        .getParcelableExtra(Intent.EXTRA_STREAM);
                uriOCR(uri);
            }
        }

        @Override
        protected void onPause() {
            // TODO Auto-generated method stub
            super.onPause();
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
        }

        @Override
        protected void onDestroy() {
            // TODO Auto-generated method stub
            super.onDestroy();

            //mTessOCR.onDestroy();
        }

        private void dispatchTakePictureIntent() {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Ensure that there's a camera activity to handle the intent
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                } catch (IOException ex) {
                    // Error occurred while creating the File

                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photoFile));
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }
            }
        }

        /**
         * http://developer.android.com/training/camera/photobasics.html
         */
        private File createImageFile() throws IOException {
            // Create an image file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                    .format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";
            String storageDir = Environment.getExternalStorageDirectory()
                    + "/TessOCR";
            File dir = new File(storageDir);
            if (!dir.exists())
                dir.mkdir();

            File image = new File(storageDir + "/" + imageFileName + ".jpg");

            // Save a file: path for use with ACTION_VIEW intents
            //mCurrentPhotoPath = image.getAbsolutePath();
            mCurrentPhotoPath = image.getPath();
            return image;
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            // TODO Auto-generated method stub
            if (requestCode == REQUEST_TAKE_PHOTO
                    && resultCode == Activity.RESULT_OK) {
                setPic();
            }
            else if (requestCode == REQUEST_PICK_PHOTO
                    && resultCode == Activity.RESULT_OK) {
                Uri uri = data.getData();
                if (uri != null) {
                    uriOCR(uri);
                }
            }
        }

        private void setPic() {
            // Get the dimensions of the View
            int targetW = mImage.getWidth();
            int targetH = mImage.getHeight();

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor << 1;
            bmOptions.inPurgeable = true;

            Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
            mImage.setImageBitmap(bitmap);
            doOCR(bitmap);

        }

        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            int id = v.getId();
            switch (id) {
                case R.id.bt_gallery:
                    pickPhoto();
                    break;
                case R.id.bt_camera:
                    takePhoto();
                    break;
            }
        }

        private void pickPhoto() {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_PICK_PHOTO);
        }

        private void takePhoto() {
            dispatchTakePictureIntent();
        }

        private void doOCR(final Bitmap bitmap) {
            if (mProgressDialog == null) {
                mProgressDialog = ProgressDialog.show(this, "Processing",
                        "Doing OCR...", true);
            }
            else {
                mProgressDialog.show();
            }

            new Thread(new Runnable() {
                public void run() {

                    final String result = mTessOCR.getOCRResult(bitmap);




                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            if (result != null && !result.equals("")) {
                                mResult.setText(result);
                                Log.i(TAG, "DATABASETEST:" + db.getAllRecords());

                            }

                            mProgressDialog.dismiss();
                        }

                    });

                };
            }).start();
        }


        private void populateDB(){
            String date = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
            db = new DatabaseHandler(getApplicationContext());
            db.addProfileInfo(new Expenses(date, money,"netto","Dalslandsgade 8"));
            db.addProfileInfo(new Expenses(date, 100,"fotex", "FotextAddress 243"));
            db.addProfileInfo(new Expenses(date, 600,"lidl", "LidlAddress 212"));
            db.addProfileInfo(new Expenses(date, 999,"fakta", "FaktaAddress 23"));
            db.addProfileInfo(new Expenses(date, 123,"kiwi", "KiwiAddress 2"));
            db.addProfileInfo(new Expenses(date, 241,"Menu", "MenuAddress 41"));
            db.addProfileInfo(new Expenses(date, 84,"Mc Donalds", "McDAddress 324"));
        }
    }

class DatabaseUtil {
    //You need to declare permission
    // <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
    //in your Manifest file in order to use this class

    //______________________________________________________________________________________________

    //todo -> rename the database according to your application
    private final static String DATABASE_NAME = "expTrackerProfile";
    //example WhatsApp :  /data/data/com.whatsapp/databases/msgstore.db
    private final static String FOLDER_EXTERNAL_DIRECTORY = Environment.getExternalStorageDirectory() + "/databasefolder";

    //______________________________________________________________________________________________
    /**
     * Call this method from any activity in your app (
     * for example ->    DatabaseUtil.copyDatabaseToExtStg(MainActivity.this);
     * this method will copy the database of your application into SDCard folder "shanraisshan/MyDatabase.sqlite" (DATABASE_NAME)
     */
    static void copyDatabaseToExtStg(Context ctx) {
        //external storage file
        File externalDirectory = new File(FOLDER_EXTERNAL_DIRECTORY);
        if(!externalDirectory.exists()){
            externalDirectory.mkdir();
        }

        File toFile = new File(externalDirectory, DATABASE_NAME);
        //internal storage file
        //https://developer.android.com/reference/android/content/Context.html#getDatabasePath(java.lang.String)
        File fromFile = ctx.getDatabasePath(DATABASE_NAME);
        //example WhatsApp :  /data/data/com.whatsapp/databases/msgstore.db
        if (fromFile.exists())
            copy(fromFile, toFile);
    }


    //______________________________________________________________________________________________ Utility function
    /**
     * @param fromFile source location
     * @param toFile destination location
     * copy file from 1 location to another
     */
    static void copy(File fromFile, File toFile) {
        try {
            FileInputStream is = new FileInputStream(fromFile);
            FileChannel src = is.getChannel();
            FileOutputStream os = new FileOutputStream(toFile);
            FileChannel dst = os.getChannel();
            dst.transferFrom(src, 0, src.size());
            src.close();	is.close();
            dst.close();	os.close();
        } catch (Exception e) {
            //todo in case of exception
        }
    }
}





