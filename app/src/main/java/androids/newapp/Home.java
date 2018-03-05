package androids.newapp;

/**
 * Created by Lenovo on 07-Feb-18.
 */
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;


public class Home extends AppCompatActivity    {

    DBHelper DbHelper;
    ArrayList<User> userList = new ArrayList<User>();
    ListViewAdapter listViewAdapter = null;
    ListView listView;
    String Field;
    SessionManager session;
    String userid;
    Button register;
    Connection connect;
    String ConnectionResult = "";
    String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_home);

        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        userid = user.get(SessionManager.KEY_NAME);

        Field = IntentData.skillIntent;
        register = (Button)findViewById(R.id.register);
        register.setText("REGISTER AS "+Field);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentData.intentClass = 3;
                Intent intent = new Intent(Home.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        DbHelper = new DBHelper(this);
        DbHelper.open();


        try {
            ConnectionHelper conStr = new ConnectionHelper();
            connect = conStr.connectionclasss();
            if (connect == null) {
                ConnectionResult = "Check Your Internet Access!";
            } else {
                if(session.isLoggedIn() == false) {
                    userList = DbHelper.retrieveUsers(Field,connect);
                }else{
                    register.setVisibility(View.GONE);
                    userList = DbHelper.retrieveUsersforLogin(Field, userid,connect);
                }
                DbHelper.close();

                if(userList != null && userList.size()>0) {
                    TextView noWorkers = (TextView)findViewById(R.id.no_workers);
                    noWorkers.setText("");
                    listView = (ListView) findViewById(R.id.list);

                    listViewAdapter = new ListViewAdapter(this, userList, Field);
                    listView.setAdapter(listViewAdapter);
                }
                else{
                    Button bid = (Button) findViewById(R.id.button2);
                    bid.setVisibility(View.INVISIBLE);
                }
            }

        }catch (SQLException s){
            s.printStackTrace();
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    public void HireThem(View v){
        IntentData.skillIntent = Field;
        Intent map = new Intent(Home.this,MapsActivity.class);
        startActivity(map);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Home.this,Hire.class);
        startActivity(intent);

    }
}
