package androids.newapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iarcuschin.simpleratingbar.SimpleRatingBar;

import java.util.ArrayList;

/**
 * Created by Lenovo on 27-Feb-18.
 */

public class DispalyProfile extends AppCompatActivity{
    TextView profilename,profilephone;
    ImageButton profilecall,profilemsg;
    ImageView profileImage;
    Button viewProfile;
    SimpleRatingBar rating;
    String phneNo;
    DBHelper dbHelper;
    User userProfile;
    ArrayList<UserProfile> worksdone = new ArrayList<UserProfile>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_profile);

        profilename = (TextView)findViewById(R.id.profilename);
        profilephone = (TextView)findViewById(R.id.profilephone);
        profilecall = (ImageButton)findViewById(R.id.profilecall);
        profilemsg = (ImageButton)findViewById(R.id.profilemessage);
        rating = (SimpleRatingBar)findViewById(R.id.ratingBar2);
        profileImage = (ImageView)findViewById(R.id.profileimage);
        viewProfile = (Button) findViewById(R.id.view_profile);
        phneNo = IntentData.ToIntent;
        dbHelper = new DBHelper(this);
        dbHelper.open();
        userProfile = dbHelper.showProfile(phneNo);
        dbHelper.close();

        profilecall = (ImageButton)findViewById(R.id.profilecall);
        profilecall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callNumber(phneNo);
            }
        });

        profilemsg = (ImageButton)findViewById(R.id.profilemessage);
        profilemsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSMSMessage(phneNo);
            }
        });

        profileImage.setImageBitmap(userProfile.getBitmap());
        profilename.setText(userProfile.getName());
        profilephone.setText(userProfile.getPhoneNo());
        rating.setRating(userProfile.getRating());

        viewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentData.ToIntent = phneNo;
                Intent intent = new Intent(DispalyProfile.this,Workers.class);
                startActivity(intent);

            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        dbHelper.open();
        worksdone = dbHelper.retrieveProfileDetails(phneNo);
        dbHelper.close();

    }


    private void sendSMSMessage(String phno) {
        try {
            Uri uri = Uri.parse("smsto:"+ phno);
            Intent smsIntent = new Intent(Intent.ACTION_SENDTO, uri);
            startActivity(smsIntent);
        } catch (Exception e) {
            Toast.makeText(DispalyProfile.this,"SMS faild, please try again later!",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }


    private void callNumber(String phno) {
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phno));
        if (ActivityCompat.checkSelfPermission(DispalyProfile.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        startActivity(callIntent);
    }

}
