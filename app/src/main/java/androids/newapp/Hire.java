package androids.newapp;

/**
 * Created by Lenovo on 07-Feb-18.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import java.sql.Connection;
import java.util.HashMap;

public class Hire extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    Connection connect;
    GridView gridView;
    String[] gridViewString = {
            "Electrician", "Mechanic", "Painter", "Plumber", "Carpenter", "Home Cleaning", "Beautician", "Driver"};
    int[] gridViewImageId = {
            R.drawable.electrician, R.drawable.mechanic, R.drawable.painter, R.drawable.plumber, R.drawable.carpenter, R.drawable.homecleaning, R.drawable.beautician, R.drawable.driver};
    SessionManager sessionManager;
    String userid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hire);
        sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> user = sessionManager.getUserDetails();
        userid = user.get(SessionManager.KEY_NAME);
        gridView = (GridView) findViewById(R.id.grid);
        CustomGridViewActivity customGridViewActivity = new CustomGridViewActivity(getApplicationContext(), gridViewString, gridViewImageId);
        gridView.setAdapter(customGridViewActivity);
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
        if(sessionManager.isLoggedIn()){
            View header = (((NavigationView) findViewById(R.id.nav_view)).getHeaderView(0));
            TextView name;
            name = (TextView) header.findViewById(R.id.personName);
            DBHelper dbHelper= new DBHelper(Hire.this);
            ConnectionHelper conStr=new ConnectionHelper();
            connect =conStr.connectionclasss();        // Connect to database
            if (connect == null)          {
                Toast.makeText(getApplicationContext(), "Check Your Internet Access!",Toast.LENGTH_SHORT).show();
            }
            else {

                dbHelper.open();
                String name1 = dbHelper.getUserName(userid, connect);
                dbHelper.close();
                name.setText(name1);
            }

        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                IntentData.skillIntent = gridViewString[position];
                Intent home = new Intent(Hire.this,Home.class);
                startActivity(home);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Intent intent = new Intent(Hire.this,MainActivity.class);
            startActivity(intent);

        }
    }




    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Intent intent;

        switch (id){
            case R.id.nav_profile:
                intent= new Intent(Hire.this,Profile.class);
                startActivity(intent);
                break;
            case R.id.nav_hire:
                intent = new Intent(Hire.this, Hire.class);
                startActivity(intent);
                break;
            case R.id.nav_settings:
                intent = new Intent(Hire.this, Settings.class);
                startActivity(intent);
                break;
            case R.id.nav_logout:
                intent = new Intent(Hire.this, Logout.class);
                startActivity(intent);
                break;
            case R.id.nav_history:
                intent = new Intent(Hire.this, MyProfile.class);
                startActivity(intent);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

