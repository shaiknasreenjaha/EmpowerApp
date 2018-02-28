package androids.newapp;

/**
 * Created by Lenovo on 07-Feb-18.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import java.util.ArrayList;
import java.util.HashMap;


public class Settings extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private int REQUEST_CAMERA = 0, SELECT_FILE = 1, CROP_PIC = 2;
    Uri selectImageUri;
    EditText address;
    EditText city;
    EditText password;
    ImageView imageView;
    Button update;
    private AwesomeValidation awesomeValidation;
    private String userChoosenTask;
    DBHelper dbHelper;
    SessionManager sessionManager;
    String userid;
    String pwd,Address,City;
    TextView tx;
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
            dbHelper = new DBHelper(Settings.this);
            dbHelper.open();
            String name1 = dbHelper.getUserName(userid);
            name.setText(name1);

            address = (EditText) findViewById(R.id.updateAddress);
            city = (EditText) findViewById(R.id.updateCity);
            password = (EditText) findViewById(R.id.updatePassword);
            imageView = (ImageView) findViewById(R.id.updateImage);
            update = (Button) findViewById(R.id.updateFields);
            dbHelper = new DBHelper(this);
            dbHelper.open();
            User details;
            details = dbHelper.getUserDetails(userid);
            dbHelper.close();
            password.setText(details.getPassword());
            city.setText(details.getCity());
            address.setText(details.getAddress());
           // ImageRound imageRound = new ImageRound(details.getBitmap());
            imageView.setImageBitmap(details.getBitmap());
            selectImageUri=getImageUri(getApplicationContext(),details.getBitmap());







            awesomeValidation.addValidation(this, R.id.updateAddress, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.locationerror);
            awesomeValidation.addValidation(this, R.id.updateCity, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.cityerror);
            awesomeValidation.addValidation(this, R.id.updatePassword, "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\S+$).{6,}$", R.string.passworderror);
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectAImage();
                }
            });

            update.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        submitForm(v);

                }
            });
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


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void  submitForm(View v) {
        if (awesomeValidation.validate()) {

            // Toast.makeText(this, "Update Successfull", Toast.LENGTH_LONG).show();
            if(saveImageInDB(selectImageUri)){
                Toast.makeText(v.getContext(),"updated",Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(v.getContext(),"not updated",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void selectAImage() {
        final CharSequence[] items = {"Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(Settings.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utility.checkPermission(Settings.this);
               if (items[item].equals("Choose from Library")) {
                    userChoosenTask ="Choose from Library";
                    if(result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    public void galleryIntent()    {
        // Toast.makeText(Settings.this, " gallery", Toast.LENGTH_LONG).show();
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                selectImageUri = data.getData();
                onSelectFromGalleryResult(data);
                performCrop(data.getData());
            } if(requestCode == CROP_PIC){
                Bundle bundle = data.getExtras();
                Bitmap bitmap = bundle.getParcelable("data");
                imageView.setImageBitmap(bitmap);
            }
        }
    }
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        imageView.setImageBitmap(bm);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(Settings.this,Hire.class);
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
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }
    private void performCrop(Uri data){
        try{
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(data, "image/*");
            cropIntent.putExtra("outputX", 200);
            cropIntent.putExtra("outputY", 200);
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("scale", true);
            cropIntent .putExtra("return-data", true);
            cropIntent.putExtra("circleCrop",true);
            Toast.makeText(this, "do crop", Toast.LENGTH_SHORT).show();
            startActivityForResult(cropIntent, CROP_PIC);
        }
        catch (ActivityNotFoundException anfr){
            Toast.makeText(this, "doesn't crop", Toast.LENGTH_SHORT).show();
        }
    }


    Boolean saveImageInDB(Uri selectedImageUri) {
        //Toast.makeText(getApplicationContext(), "save image", Toast.LENGTH_SHORT).show();
        // imageView.setImageResource(R.drawable.camera);
        dbHelper = new DBHelper(this);
        try {
            dbHelper.open();
            InputStream iStream = getContentResolver().openInputStream(selectedImageUri);
            byte[] inputData = Utils.getBytes(iStream);
            pwd = password.getText().toString();
            Address = address.getText().toString();
            City = city.getText().toString();
            boolean isUpdate = dbHelper.UpdateUserDetails(inputData,userid, Address, City,pwd);
            //if(isUpdate == true)
            //  Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_LONG).show();
            dbHelper.close();
            return true;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            dbHelper.close();
            return false;
        }
    }


}
