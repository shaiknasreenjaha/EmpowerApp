package androids.newapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.util.ArrayList;

/**
 * Created by Lenovo on 27-Feb-18.
 */

public class DispalyProfile extends AppCompatActivity{
    TextView profilename,profilephone;
    ProgressDialog progressDialog;
    ImageButton profilecall,profilemsg;
    ImageView profileImage;
    Button viewProfile;
    SimpleRatingBar rating;
    String phneNo;
    DBHelper dbHelper;
    User userProfile;
    Connection connection;
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
        ConnectionHelper conStr=new ConnectionHelper();
        connection =conStr.connectionclasss();
        if (connection == null)          {
            Toast.makeText(getApplicationContext(),"CheckInternetConnection",Toast.LENGTH_SHORT).show();
        }
        else {
            userProfile = dbHelper.showProfile(phneNo, connection);
            dbHelper.close();
        }

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

        profileImage.requestLayout();
        profileImage.getLayoutParams().height=400;
        profileImage.getLayoutParams().width=400;
        profileImage.setScaleType(ImageView.ScaleType.FIT_XY);


        progressDialog = new ProgressDialog(this, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
        progressDialog.setMessage("Loading ...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();



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
                            progressDialog.cancel();
                        }
                    });
                } catch (Exception ex) {
                    final String exceptionMessage = ex.getMessage();
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(DispalyProfile.this, exceptionMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        th.start();




        // profileImage.setImageBitmap(userProfile.getBitmap());
        profilename.setText(userProfile.getName());
        profilephone.setText(userProfile.getPhoneNo());
        rating.setRating(userProfile.getRating());

        viewProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentData.ToIntent = phneNo;
                IntentData.intentClass = 11;
                Intent intent = new Intent(DispalyProfile.this,Workers.class);
                startActivity(intent);

            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

      // Connect to database
        if (connection == null)          {
            Toast.makeText(getApplicationContext(),"CheckInternetConnection",Toast.LENGTH_SHORT).show();
        }
        else {

            dbHelper.open();
            worksdone = dbHelper.retrieveProfileDetails(phneNo,connection);
            dbHelper.close();
        }

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
