package androids.newapp;

/**
 * Created by Lenovo on 07-Feb-18.
 */
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;

public class Profile extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    DBHelper DbHelper;
    ProfileViewAdapter profileViewAdapter;
    ListView listView;
    SessionManager sessionManager;
    TextView tx;
    Connection connect;
    public ArrayList<UserProfile> userProfile = new ArrayList<UserProfile>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        sessionManager = new SessionManager(getApplicationContext());
        final String phoneNo = sessionManager.getUserDetails().get(SessionManager.KEY_NAME);
        tx = (TextView) findViewById(R.id.nolist);


        if(sessionManager.isLoggedIn()) {

            View header = (((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0));
            TextView name;
            name = (TextView) header.findViewById(R.id.personName);

            ConnectionHelper conStr=new ConnectionHelper();
            connect =conStr.connectionclasss();        // Connect to database
            if (connect == null)          {
                Toast.makeText(getApplicationContext(), "Check Your Internet Access!",Toast.LENGTH_SHORT).show();
            }
            else {

                DbHelper = new DBHelper(Profile.this);
                DbHelper.open();
                String name1 = DbHelper.getUserName(phoneNo,connect);
                name.setText(name1);


                DbHelper = new DBHelper(this);
                DbHelper.open();
                userProfile = DbHelper.retrieveProfileDetails1(phoneNo,connect);
                DbHelper.close();

                if (userProfile != null && userProfile.size() > 0) {
                    tx.setText("");
                    userProfile = sortAndAddSections(userProfile);
                    listView = (ListView) findViewById(R.id.profile);
                    profileViewAdapter = new ProfileViewAdapter(this, userProfile);
                    listView.setAdapter(profileViewAdapter);
                    listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent image = new Intent(Profile.this, DisplayImage.class);
                            ByteArrayOutputStream bs = new ByteArrayOutputStream();
                            IntentData.imageIntent = userProfile.get(position).getBitmap();
                            IntentData.descriptionIntent = userProfile.get(position).getDescription();
                            IntentData.dateIntent = userProfile.get(position).getDate();
                            IntentData.categoryIntent = userProfile.get(position).getCategory();
                            //Toast.makeText(getApplicationContext(),userProfile.get(position).getAmount(),Toast.LENGTH_SHORT).show();
                            //Log.e("Bid Amount",userProfile.get(position).getAmount());
                            IntentData.bidAmount = userProfile.get(position).getAmount();
                            startActivity(image);
                        }
                    });
                }
            }
        }else{
            tx.setText("NO MESSAGES TILL YOU LOGGED IN");
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(Profile.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
            mBuilder.setTitle("Alert");
            mBuilder.setMessage("To view your History please login");
            mBuilder.setPositiveButton("login", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    IntentData.intentClass = 4;
                    Intent intent = new Intent(Profile.this, LoginActivity.class);
                    startActivity(intent);
                }
            });
            mBuilder.setNeutralButton("close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Profile.this, Hire.class);
                    startActivity(intent);
                }
            });
            AlertDialog alertDialog = mBuilder.create();
            alertDialog.show();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(Profile.this,MainActivity.class);
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
                intent= new Intent(Profile.this,Profile.class);
                startActivity(intent);
                break;
            case R.id.nav_hire:
                intent = new Intent(Profile.this, Hire.class);
                startActivity(intent);
                break;
            case R.id.nav_settings:
                intent = new Intent(Profile.this, Settings.class);
                startActivity(intent);
                break;
            case R.id.nav_logout:
                intent = new Intent(Profile.this, Logout.class);
                startActivity(intent);
                break;
            case R.id.nav_history:
                intent = new Intent(Profile.this, MyProfile.class);
                startActivity(intent);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private ArrayList<UserProfile> sortAndAddSections(ArrayList<UserProfile> itemList){
        ArrayList<UserProfile> tempList = new ArrayList<UserProfile>();
        Collections.sort(itemList);
        String header = "";
        for (int i = 0; i < itemList.size(); i++){
            if(header != itemList.get(i).getCategory()){
                UserProfile sectionCell = new UserProfile(itemList.get(i).getBitmap(),itemList.get(i).getDate(),
                        itemList.get(i).getDescription(),itemList.get(i).getName(),itemList.get(i).getCategory(),itemList.get(i).getAmount(),null);
                sectionCell.setSelectionHeader();
                tempList.add(sectionCell);
                header = itemList.get(i).getCategory();
            }
            tempList.add(itemList.get(i));
        }
        return tempList;

    }
}

