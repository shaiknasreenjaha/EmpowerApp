package androids.newapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Lenovo on 10-Feb-18.
 */

public class Workers extends AppCompatActivity {
    WorkerListAdapter workerListAdapter;
    ListView listView;
    SessionManager sessionManager;
    String Field;
    ArrayList<UserProfile> userProfiles = new ArrayList<UserProfile>();
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sessionManager = new SessionManager(getApplicationContext());

        if(sessionManager.isLoggedIn()){
            setContentView(R.layout.worker_list);
        }else{
            setContentView(R.layout.intrest_layout);
            Button btn = (Button) findViewById(R.id.logindisable);
            btn.setText("REGISTER AS "+IntentData.skillIntent);
        }

        dbHelper = new DBHelper(this);
        dbHelper.open();
        //userProfiles.add(new UserProfile("","","","","","",""));
        //userProfiles = dbHelper.retrieveProfileDetails(getIntent().getStringExtra("phoneno"));
        userProfiles = dbHelper.retrieveProfileDetails(IntentData.ToIntent);
        dbHelper.close();

        if(userProfiles!=null && userProfiles.size()>0) {
            TextView noPosts = (TextView) findViewById(R.id.no_posts);
            noPosts.setText("");
            listView = (ListView) findViewById(R.id.workers_list);

            workerListAdapter = new WorkerListAdapter(getApplicationContext(), userProfiles);
            listView.setAdapter(workerListAdapter);
            //Field = getIntent().getStringExtra("technician");
            Field = IntentData.skillIntent;
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Workers.this,Home.class);
        startActivity(intent);
    }

    public void Interest(View v){
        if(sessionManager.isLoggedIn()==false) {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(Workers.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
            mBuilder.setTitle("Alert");
            mBuilder.setMessage("if you want to register as a worker please Login or if you want to hire click on hire");
            mBuilder.setPositiveButton("Login", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    IntentData.intentClass = 1;
                    Intent intent = new Intent(Workers.this, LoginActivity.class);
                    startActivity(intent);
                }
            });
            mBuilder.setNeutralButton("close", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog = mBuilder.create();
            alertDialog.show();
        }
    }
}

