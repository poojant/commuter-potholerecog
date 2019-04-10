package com.example.lenovo.commutersafety;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class CapturePictureActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "CapturePicture";
    static final int REQUEST_PICTURE_CAPTURE = 1;
    private ImageView image;
    private String pictureFilePath;
    private FirebaseStorage firebaseStorage;
    private String deviceIdentifier;


    DatabaseReference databaseReference;
    private EditText ztitle, zdesc, zsol;

    public String zoneImageURI = null;

    Button saveData;

    public String Lat="abc" , Logg="bcd";


    private static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;
    String lattitude,longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_picture);

        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        databaseReference = FirebaseDatabase.getInstance().getReference("Zones");
        ztitle = (EditText) findViewById(R.id.editTextTitle);
        zdesc = (EditText) findViewById(R.id.editTextDescription);
        zsol = (EditText) findViewById(R.id.editTextSolution);

        image = findViewById(R.id.picture);

        Button captureButton = findViewById(R.id.capture);
        captureButton.setOnClickListener(capture);

        saveData = findViewById(R.id.save_data);
        saveData.setOnClickListener(this);


        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            captureButton.setEnabled(false);
        }

        findViewById(R.id.save_local).setOnClickListener(saveGallery);
        findViewById(R.id.save_cloud).setOnClickListener(saveCloud);

        firebaseStorage = FirebaseStorage.getInstance();
        getInstallationIdentifier();
    }


    private View.OnClickListener capture = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                sendTakePictureIntent();
            }
        }
    };

    private void sendTakePictureIntent() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_FINISH_ON_COMPLETION, true);

        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, REQUEST_PICTURE_CAPTURE);

            File pictureFile = null;
            try {
                // Toast.makeText(this, "Flag-1", Toast.LENGTH_SHORT).show();
                pictureFile = getPictureFile();
            } catch (IOException ex) {
                Toast.makeText(this, "Photo file can't be created, please try again", Toast.LENGTH_SHORT).show();
                return;
            }
            if (pictureFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this, "com.zoftino.android.fileprovider", pictureFile);
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(cameraIntent, REQUEST_PICTURE_CAPTURE);
            }
        } else
            Toast.makeText(this, "Photo file can't be created, please try again", Toast.LENGTH_SHORT).show();

    }

    private File getPictureFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String pictureFile = "CommuterSafety_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(pictureFile, ".jpg", storageDir);
        pictureFilePath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PICTURE_CAPTURE && resultCode == RESULT_OK) {
            File imgFile = new File(pictureFilePath);
            if (imgFile.exists()) {
                image.setImageURI(Uri.fromFile(imgFile));
            }
        }
    }

    //save captured picture in gallery
    private View.OnClickListener saveGallery = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            addToGallery();
        }
    };

    private void addToGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(pictureFilePath);
        Uri picUri = Uri.fromFile(f);
        galleryIntent.setData(picUri);
        this.sendBroadcast(galleryIntent);
        Toast.makeText(CapturePictureActivity.this, "Image has been uploaded to phone storage" + pictureFilePath, Toast.LENGTH_SHORT).show();
    }


    //save captured picture on cloud storage
    private View.OnClickListener saveCloud = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            addToCloudStorage();
        }
    };


    private void addToCloudStorage() {
        File f = new File(pictureFilePath);
        Uri picUri = Uri.fromFile(f);
        final String cloudFilePath = deviceIdentifier + picUri.getLastPathSegment();

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageRef = firebaseStorage.getReference();
        final StorageReference uploadeRef = storageRef.child(cloudFilePath);

        uploadeRef.putFile(picUri).addOnFailureListener(new OnFailureListener() {
            public void onFailure(@NonNull Exception exception) {
              //  Log.e(TAG, "Failed to upload picture to cloud storage");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                uploadeRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Uri downloadURI = uri;
                        zoneImageURI = downloadURI.toString();
                    }
                });
                Toast.makeText(CapturePictureActivity.this, "Image has been uploaded to cloud storage", Toast.LENGTH_SHORT).show();
            }
        });
    }

    protected synchronized String getInstallationIdentifier() {
        if (deviceIdentifier == null) {
            SharedPreferences sharedPrefs = this.getSharedPreferences(
                    "DEVICE_ID", Context.MODE_PRIVATE);
            deviceIdentifier = sharedPrefs.getString("DEVICE_ID", null);
            if (deviceIdentifier == null) {
                deviceIdentifier = UUID.randomUUID().toString();
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString("DEVICE_ID", deviceIdentifier);
                editor.commit();
            }
        }
        return deviceIdentifier;
    }

    private void addDataZone() {
        String ZoneTitle = ztitle.getText().toString().trim();
        String ZoneData = zdesc.getText().toString().trim();
        String ZoneSolution = zsol.getText().toString().trim();
        String ZoneLat = Lat;
        String ZoneLong = Logg;
        String ZoneStatus = ztitle.getText().toString().trim();
        String ZoneImage = zoneImageURI;

        if (TextUtils.isEmpty(ZoneTitle)) {
            Toast.makeText(this, "Please Enter The Zone Title", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(ZoneData)) {
            Toast.makeText(this, "Please Enter The Zone Description", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(ZoneSolution)) {
            Toast.makeText(this, "Please Enter The Zone Solution", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(ZoneSolution)) {
            Toast.makeText(this, "Please Enter The Zone Solution", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(ZoneImage)) {
            Toast.makeText(this, "Please Capture The Image First", Toast.LENGTH_SHORT).show();
            return;
        }

        String ZoneKey = databaseReference.push().getKey();
        Zone zn = new Zone(ZoneKey, ZoneTitle, ZoneData, ZoneSolution, ZoneLat, ZoneLong, ZoneStatus, ZoneImage);
        databaseReference.child(ZoneKey).setValue(zn);
        Toast.makeText(this, " All The Details Added Successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        if (view == saveData) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                buildAlertMessageNoGps();

            } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                getLocation();
            }
            addDataZone();
        }
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(CapturePictureActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (CapturePictureActivity.this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(CapturePictureActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            Location location2 = locationManager.getLastKnownLocation(LocationManager. PASSIVE_PROVIDER);

            if (location != null) {
                double latti = location.getLatitude();
                double longi = location.getLongitude();
                lattitude = String.valueOf(latti);
                longitude = String.valueOf(longi);

                Lat = lattitude;
                Logg = longitude;

            } else  if (location1 != null) {
                double latti = location1.getLatitude();
                double longi = location1.getLongitude();
                lattitude = String.valueOf(latti);
                longitude = String.valueOf(longi);

                Lat = lattitude;
                Logg = longitude;



            } else  if (location2 != null) {
                double latti = location2.getLatitude();
                double longi = location2.getLongitude();
                lattitude = String.valueOf(latti);
                longitude = String.valueOf(longi);

                Lat = lattitude;
                Logg = longitude;

            }else{

                Toast.makeText(this,"Unble to Trace your location",Toast.LENGTH_SHORT).show();

            }
        }
    }

    protected void buildAlertMessageNoGps() {

        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage("Please Turn ON your GPS Connection")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final android.app.AlertDialog alert = builder.create();
        alert.show();
    }
}