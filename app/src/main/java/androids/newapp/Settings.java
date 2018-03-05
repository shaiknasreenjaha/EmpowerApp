package androids.newapp;

/**
 * Created by Lenovo on 07-Feb-18.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;


public class Settings extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private int GALLERY = 1;
    Uri contentURI;
    EditText address;
    EditText city;
    String imageName;
    EditText password;
    User details = null;
    ImageView imageView;
    Button update;
    byte image[];
    private AwesomeValidation awesomeValidation;
    private String userChoosenTask;
    DBHelper dbHelper;
    SessionManager sessionManager;
    String userid;
    String pwd,Address,City;
    TextView tx;
    Connection connect;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user = sessionManager.getUserDetails();
        userid = user.get(SessionManager.KEY_NAME);
        if(sessionManager.isLoggedIn())
        setContentView(R.layout.activity_settings);
        else {

            setContentView(R.layout.activity_profile);
            tx = (TextView) findViewById(R.id.nolist);
            tx.setText("YOU CANNOT OPEN SETTING TILL YOU LOGGED IN");
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if(sessionManager.isLoggedIn()) {
            View header = (((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0));
            TextView name;
            name = (TextView) header.findViewById(R.id.personName);
            ConnectionHelper conStr=new ConnectionHelper();
            connect =conStr.connectionclasss();        // Connect to database
            if (connect == null)          {
                Toast.makeText(getApplicationContext(),"Check Your Internet Access!",Toast.LENGTH_SHORT).show();
            }
            else {

                dbHelper = new DBHelper(Settings.this);
                dbHelper.open();
                String name1 = dbHelper.getUserName(userid, connect);
                name.setText(name1);

                address = (EditText) findViewById(R.id.updateAddress);
                city = (EditText) findViewById(R.id.updateCity);
                password = (EditText) findViewById(R.id.updatePassword);
                imageView = (ImageView) findViewById(R.id.updateImage);
                update = (Button) findViewById(R.id.updateFields);


                details = dbHelper.getUserDetails(userid,connect);
                dbHelper.close();
                password.setText(details.getPassword());
                city.setText(details.getCity());
                address.setText(details.getAddress());

                progressDialog = new ProgressDialog(this, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
                progressDialog.setMessage("Loading ...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();


                imageView.requestLayout();
                imageView.getLayoutParams().height=150;
                imageView.getLayoutParams().width=150;
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);


                final ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
                final Handler handler = new Handler();
                Thread th = new Thread(new Runnable() {
                    public void run() {
                        try {
                            long imageLength = 0;
                            ImageManager.GetImage(details.getUserImage(), imageStream, imageLength);
                            handler.post(new Runnable() {
                                public void run() {
                                    byte[] buffer = imageStream.toByteArray();
                                    Bitmap bitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
                                    imageView.setImageBitmap(bitmap);
                                    progressDialog.cancel();
                                }
                            });
                        }
                        catch(Exception ex) {
                            //final String exceptionMessage = ex.getMessage();
                            handler.post(new Runnable() {
                                public void run() {
                                    ex.printStackTrace();
                                    //Toast.makeText(Settings.this, exceptionMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }});
                th.start();
                awesomeValidation.addValidation(this, R.id.updateAddress, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.locationerror);
                awesomeValidation.addValidation(this, R.id.updateCity, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.cityerror);
                awesomeValidation.addValidation(this, R.id.updatePassword, "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\S+$).{6,}$", R.string.passworderror);
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplication(), "selected", Toast.LENGTH_SHORT).show();
                        showPictureDialog();
                    }
                });

                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        submitForm(v);

                    }
                });
            }
        }else {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(Settings.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
            mBuilder.setTitle("Alert");

            mBuilder.setMessage("To view Setting please login");
            mBuilder.setPositiveButton("login", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.this, LoginActivity.class);
                    startActivity(intent);
                }
            });
            mBuilder.setNeutralButton("close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.this, Hire.class);
                    startActivity(intent);
                }
            });
            AlertDialog alertDialog = mBuilder.create();
            alertDialog.show();
        }
    }


    private void showPictureDialog(){
        android.app.AlertDialog.Builder pictureDialog = new android.app.AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = { "Select photo from gallery", "Cancel" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallery();
                                break;
                            case 1:
                                dialog.dismiss();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }


    public void choosePhotoFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                contentURI = data.getData();
               // Toast.makeText(Settings.this,"img selected  : "+contentURI,Toast.LENGTH_LONG).show();

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    image =  Utils.getImageBytes(bitmap);
                    imageView.setImageBitmap(bitmap);
                    UploadImage();


                } catch (IOException e) {
                    e.printStackTrace();
                   // Toast.makeText(Settings.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void UploadImage()    {
        try {
            final InputStream imageStream = getContentResolver().openInputStream(this.contentURI);
            final int imageLength = imageStream.available();

            final Handler handler = new Handler();

            Thread th = new Thread(new Runnable() {
                public void run() {

                    try {
                        imageName = ImageManager.UploadImage(imageStream, imageLength);
                        handler.post(new Runnable() {
                            public void run() {
                                Toast.makeText(Settings.this, "Image Uploaded Successfully." , Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                    catch(Exception ex) {
                      //  final String exceptionMessage = ex.getMessage();
                        handler.post(new Runnable() {
                            public void run() {
                                //Toast.makeText(Settings.this, exceptionMessage, Toast.LENGTH_SHORT).show();
                                ex.printStackTrace();
                            }
                        });
                    }
                }});
            th.start();
        }
        catch(Exception ex) {
            ex.printStackTrace();
           // Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


    private void  submitForm(View v) {
        if (awesomeValidation.validate()) {
            if(saveImageInDB()){
                Toast.makeText(v.getContext(),"updated",Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(v.getContext(),"not updated",Toast.LENGTH_SHORT).show();
            }
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(Settings.this,MainActivity.class);
            startActivity(intent);
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent;
        switch (id){
            case R.id.nav_profile:
                intent= new Intent(Settings.this,Profile.class);
                startActivity(intent);
                break;
            case R.id.nav_hire:
                intent = new Intent(Settings.this, Hire.class);
                startActivity(intent);
                break;
            case R.id.nav_settings:
                intent = new Intent(Settings.this, Settings.class);
                startActivity(intent);
                break;
            case R.id.nav_logout:
                intent = new Intent(Settings.this, Logout.class);
                startActivity(intent);
                break;
            case R.id.nav_history:
                intent = new Intent(Settings.this, MyProfile.class);
                startActivity(intent);
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    Boolean saveImageInDB() {
        dbHelper = new DBHelper(this);
        try {
            ConnectionHelper conStr=new ConnectionHelper();
            connect =conStr.connectionclasss();        // Connect to database
            if (connect == null)          {
                Toast.makeText(getApplicationContext(),"Check Your Internet Access!",Toast.LENGTH_SHORT).show();
            }
            else {
                dbHelper.open();
                pwd = password.getText().toString();
                Address = address.getText().toString();
                City = city.getText().toString();

                if(imageName == null || imageName=="")
                    imageName = details.getUserImage();



                boolean isUpdate = dbHelper.UpdateUserDetails(imageName, userid, Address, City, pwd,connect);
                if(isUpdate == true) {
                   // Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
                    dbHelper.close();
                    return true;
                }
            }
        } catch (Exception ioe) {
            ioe.printStackTrace();
            dbHelper.close();
            return false;
        }
        return false;
    }
}
