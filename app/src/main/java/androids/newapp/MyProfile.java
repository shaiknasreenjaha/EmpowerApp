package androids.newapp;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iarcuschin.simpleratingbar.SimpleRatingBar;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.util.HashMap;

/**
 * Created by Lenovo on 04-Mar-18.
 */

public class MyProfile extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    SessionManager sessionManager;
    String userid;
    TextView tx;
    User userProfile;
    DBHelper dbHelper;
    ImageView profileImage;
    TextView profilename,profilephone,address,city;
    SimpleRatingBar rating;
    ProgressDialog progressDialog;

    ImageButton profilecall,profilemsg;
    Connection connection;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user = sessionManager.getUserDetails();
        userid = user.get(SessionManager.KEY_NAME);



        if(sessionManager.isLoggedIn()) {
            setContentView(R.layout.activity_my_profile);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            ConnectionHelper conStr = new ConnectionHelper();
            connection = conStr.connectionclasss();        // Connect to database

            if (connection == null) {
                Toast.makeText(getApplicationContext(), "Check Your Internet Access!", Toast.LENGTH_SHORT).show();
            } else {
                View header = (((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0));
                TextView name;
                name = (TextView) header.findViewById(R.id.personName);
                dbHelper = new DBHelper(MyProfile.this);
                dbHelper.open();
                String name1 = dbHelper.getUserName(userid,connection);
                name.setText(name1);
                profileImage = (ImageView)findViewById(R.id.my_image);
                profilename = (TextView)findViewById(R.id.user_name);
                rating = (SimpleRatingBar)findViewById(R.id.my_rating);
                profilephone = (TextView)findViewById(R.id.mobile_number);
                address = (TextView)findViewById(R.id.landmark);
                city = (TextView)findViewById(R.id.city);


                dbHelper = new DBHelper(this);
                dbHelper.open();
                userProfile = dbHelper.showProfile(userid,connection);
                dbHelper.close();

                profilename.setText(userProfile.getName());
                profilephone.setText(userProfile.getPhoneNo());
                rating.setRating(userProfile.getRating());
                address.setText(userProfile.getAddress());
                city.setText(userProfile.getCity());
                progressDialog = new ProgressDialog(this, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
                progressDialog.setMessage("Loading ...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();
                profileImage.requestLayout();
                profileImage.getLayoutParams().height=400;
                profileImage.getLayoutParams().width=400;
                profileImage.setScaleType(ImageView.ScaleType.FIT_XY);


                final ByteArrayOutputStream imageStream = new ByteArrayOutputStream();
                final Handler handler = new Handler();

                Thread th = new Thread(new Runnable() {
                    public void run() {

                        try {

                            long imageLength = 0;

                            ImageManager.GetImage(userProfile.getUserImage(), imageStream, imageLength);

                            handler.post(new Runnable() {

                                public void run() {
                                    byte[] buffer = imageStream.toByteArray();

                                    Bitmap bitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
                                    //bitmap.compress(Bitmap.CompressFormat.PNG, 0, imageStream);
                                    profileImage.setImageBitmap(bitmap);
                                   // Toast.makeText(MyProfile.this, "image set", Toast.LENGTH_SHORT).show();
                                    progressDialog.cancel();
                                }
                            });
                        } catch (Exception ex) {
                            final String exceptionMessage = ex.getMessage();
                            handler.post(new Runnable() {
                                public void run() {
                                    Toast.makeText(MyProfile.this, exceptionMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
                th.start();
            }


        }
        else {

            setContentView(R.layout.activity_profile);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.setDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);
            tx = (TextView) findViewById(R.id.nolist);
            tx.setText("YOU ARE NOT LOGGED IN");


            AlertDialog.Builder mBuilder = new AlertDialog.Builder(MyProfile.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
            mBuilder.setTitle("Alert");
            mBuilder.setMessage("To view your Profile please login");
            mBuilder.setPositiveButton("login", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    IntentData.intentClass = 4;
                    Intent intent = new Intent(MyProfile.this, LoginActivity.class);
                    startActivity(intent);
                }
            });
            mBuilder.setNeutralButton("close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(MyProfile.this, Hire.class);
                    startActivity(intent);
                }
            });
            AlertDialog alertDialog = mBuilder.create();
            alertDialog.show();

            //getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            //ActionBar actionBar = getActionBar();
            //actionBar.setDisplayHomeAsUpEnabled(true);
            //actionBar.setHomeButtonEnabled(true);
        }

    }




    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;

        switch (id) {
            case R.id.nav_profile:
                intent = new Intent(MyProfile.this, Profile.class);
                startActivity(intent);
                break;
            case R.id.nav_history:
                intent = new Intent(MyProfile.this, MyProfile.class);
                startActivity(intent);
                break;
            case R.id.nav_hire:
                intent = new Intent(MyProfile.this, Hire.class);
                startActivity(intent);
                break;
            case R.id.nav_settings:
                intent = new Intent(MyProfile.this, Settings.class);
                startActivity(intent);
                break;
            case R.id.nav_logout:
                intent = new Intent(MyProfile.this, Logout.class);
                startActivity(intent);
                break;
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
            Intent intent = new Intent(MyProfile.this,MainActivity.class);
            startActivity(intent);
    }

}
