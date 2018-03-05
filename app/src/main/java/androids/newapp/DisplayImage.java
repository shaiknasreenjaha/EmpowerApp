package androids.newapp;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.app.AlertDialog;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Lenovo on 21-Feb-18.
 */

public class DisplayImage extends AppCompatActivity {
    String category;
    Connection connection;
    DBHelper dbHelper;
    float value;
    ArrayList<UserProfile> userProfiles;
    SessionManager sessionManager;
    ListView listView;
    ImageView _imv;
    ImageButton send;
    EditText sendamount;
    String UserId;
    String descr;
    TextView chance;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        category = IntentData.categoryIntent;
        descr = IntentData.descriptionIntent;
        sessionManager = new SessionManager(getApplicationContext());
        UserId = sessionManager.getUserDetails().get(SessionManager.KEY_NAME);
        ConnectionHelper conStr = new ConnectionHelper();
        connection = conStr.connectionclasss();        // Connect to database
        if (connection == null) {
            Toast.makeText(getApplicationContext(), "Check your internet Access", Toast.LENGTH_SHORT).show();
        }else {
            if (category.equals("Received")) {

                setContentView(R.layout.image_profile);
                TextView y = (TextView) findViewById(R.id.hai);
                send = (ImageButton) findViewById(R.id.sendbid);
                sendamount = (EditText) findViewById(R.id.sentmsg);
                chance = (TextView) findViewById(R.id.NotPossible);
                chance.setVisibility(View.INVISIBLE);

                String amount1 = IntentData.bidAmount;

                //Toast.makeText(getApplicationContext(),amount1,Toast.LENGTH_SHORT).show();
                //Log.e("Bid Amount befor if",amount1);

                dbHelper = new DBHelper(this);
                dbHelper.open();

                if (amount1 == null ||amount1.equals("")) {
                    //Log.e("in if",amount1);
                    send.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String amount = sendamount.getText().toString();
                            sendamount.setText("");
                            dbHelper.UpdateAmount(UserId, descr, IntentData.dateIntent, amount,connection);
                            Intent intent = new Intent(DisplayImage.this,DisplayImage.class);
                            IntentData.bidAmount = amount;
                            finish();
                            startActivity(intent);


                        }
                    });
                } else {
                    chance.setVisibility(View.VISIBLE);
                    send.setVisibility(View.INVISIBLE);
                    sendamount.setVisibility(View.INVISIBLE);
                }
                _imv = (ImageView) findViewById(R.id.imagePost);
                _imv.requestLayout();
                _imv.getLayoutParams().height = 500;
                _imv.getLayoutParams().width = 500;
                _imv.setScaleType(ImageView.ScaleType.FIT_XY);
                //_imv.setImageBitmap(IntentData.imageIntent);
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

                            ImageManager.GetImage(IntentData.imageIntent, imageStream, imageLength);

                            handler.post(new Runnable() {

                                public void run() {
                                    byte[] buffer = imageStream.toByteArray();

                                    Bitmap bitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
                                    //bitmap.compress(Bitmap.CompressFormat.PNG, 0, imageStream);
                                    _imv.setImageBitmap(bitmap);
                                    progressDialog.cancel();
                                }
                            });
                        } catch (Exception ex) {
                            //final String exceptionMessage = ex.getMessage();
                            handler.post(new Runnable() {
                                public void run() {
                                    //Toast.makeText(DisplayImage.this, exceptionMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
                th.start();


                y.setText(IntentData.descriptionIntent);

            }

        else {
                setContentView(R.layout.comment_list);
                dbHelper = new DBHelper(this);
                dbHelper.open();
                userProfiles = dbHelper.bidding(UserId, descr, IntentData.dateIntent,connection);
                dbHelper.close();

                if (userProfiles != null && userProfiles.size() > 0) {

                    if (userProfiles.size() == 1 && userProfiles.get(0).getStatus().equals("done")) {

                        setContentView(R.layout.activity_sent);
                        TextView y = (TextView) findViewById(R.id.sentprofiledes);
                        TextView noBid = (TextView) findViewById(R.id.nobid);
                        progressDialog = new ProgressDialog(this, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
                        progressDialog.setMessage("Loading ...");
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();
                        _imv = (ImageView) findViewById(R.id.imagePostSent);
                        _imv.requestLayout();
                        _imv.getLayoutParams().height = 500;
                        _imv.getLayoutParams().width = 500;
                        _imv.setScaleType(ImageView.ScaleType.FIT_XY);
                        final ByteArrayOutputStream imageStream = new ByteArrayOutputStream();

                        final Handler handler = new Handler();
                        Thread th = new Thread(new Runnable() {
                            public void run() {
                                try {
                                    long imageLength = 0;
                                    ImageManager.GetImage(IntentData.imageIntent, imageStream, imageLength);
                                    handler.post(new Runnable() {

                                        public void run() {
                                            byte[] buffer = imageStream.toByteArray();
                                            Bitmap bitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
                                            _imv.setImageBitmap(bitmap);
                                            progressDialog.cancel();
                                        }
                                    });
                                } catch (Exception ex) {
                                    //final String exceptionMessage = ex.getMessage();
                                    handler.post(new Runnable() {
                                        public void run() {
                                            //Toast.makeText(DisplayImage.this, exceptionMessage, Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        });
                        th.start();


                        y.setText(IntentData.descriptionIntent);
                        noBid.setText("THIS WORK IS GIVEN TO " + userProfiles.get(0).getTo());
                        final TextView rate = (TextView) findViewById(R.id.rate);
                        dbHelper.open();

                        if (dbHelper.getRating(userProfiles.get(0).getTo(), UserId, descr, IntentData.dateIntent,connection) == 0.0f) {
                            rate.setOnClickListener(new View.OnClickListener() {

                                @Override
                                public void onClick(View v) {
                                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(DisplayImage.this);
                                    mBuilder.setIcon(android.R.drawable.star_big_on);
                                    mBuilder.setTitle("Vote!!");
                                    LinearLayout ll = new LinearLayout(DisplayImage.this);
                                    final RatingBar rating = new RatingBar(DisplayImage.this);
                                    rating.setNumStars(5);
                                    rating.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
                                    ll.addView(rating);
                                    mBuilder.setView(ll);

                                    rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                                        @Override
                                        public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                                            System.out.println("Rated val:" + v);
                                            value = v;
                                        }
                                    });
                                    mBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dbHelper = new DBHelper(DisplayImage.this);
                                            dbHelper.open();
                                            boolean i = dbHelper.UpdateRating(userProfiles.get(0).getTo(), UserId, IntentData.dateIntent, descr, value, connection);
                                            if (i) {
                                                boolean c = dbHelper.updateUserRating(userProfiles.get(0).getTo(),connection);
                                                if(c) {
                                                    Toast.makeText(getApplicationContext()," Your rating is submitted successfully!!!", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(DisplayImage.this, DisplayImage.class);
                                                    finish();
                                                    startActivity(intent);
                                                }


                                            }
                                        }
                                    });
                                    mBuilder.setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    mBuilder.show();
                                }
                            });

                        } else {
                            rate.setVisibility(View.GONE);
                        }

                    } else {
                        userProfiles = sortAndAddSections(userProfiles);
                        listView = (ListView) findViewById(R.id.commentList);
                        CommentListAdapter commentListAdapter = new CommentListAdapter(this, userProfiles, IntentData.imageIntent, descr, IntentData.dateIntent);
                        listView.setAdapter(commentListAdapter);
                    }

                } else {
                    setContentView(R.layout.activity_sent);
                    TextView y = (TextView) findViewById(R.id.sentprofiledes);
                    _imv = (ImageView) findViewById(R.id.imagePostSent);
                    //Toast.makeText(getApplicationContext(), category, Toast.LENGTH_LONG).show();
                    progressDialog = new ProgressDialog(this, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
                    progressDialog.setMessage("Loading ...");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    _imv.requestLayout();
                    _imv.getLayoutParams().height = 500;
                    _imv.getLayoutParams().width = 500;
                    _imv.setScaleType(ImageView.ScaleType.FIT_XY);
                    final ByteArrayOutputStream imageStream = new ByteArrayOutputStream();

                    final Handler handler = new Handler();

                    Thread th = new Thread(new Runnable() {
                        public void run() {
                            try {
                                long imageLength = 0;
                                ImageManager.GetImage(IntentData.imageIntent, imageStream, imageLength);
                                handler.post(new Runnable() {

                                    public void run() {
                                        byte[] buffer = imageStream.toByteArray();
                                        Bitmap bitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
                                        //bitmap.compress(Bitmap.CompressFormat.PNG, 0, imageStream);

                                        _imv.setImageBitmap(bitmap);
                                        progressDialog.cancel();
                                    }
                                });
                            } catch (Exception ex) {
                               // final String exceptionMessage = ex.getMessage();
                                handler.post(new Runnable() {
                                    public void run() {
                                       // Toast.makeText(DisplayImage.this, exceptionMessage, Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                    th.start();
                    y.setText(descr);
                    TextView noBid = (TextView) findViewById(R.id.nobid);
                    if (category.equals("Done")) {
                        noBid.setText("YOU ARE ASSIGNED THIS WORK");
                    }
                    TextView txt = (TextView) findViewById(R.id.rate);
                    txt.setVisibility(View.INVISIBLE);
                }
            }

        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }



    @Override
    public void onBackPressed() {
            Intent intent = new Intent(DisplayImage.this,Profile.class);
            startActivity(intent);
    }



    private ArrayList<UserProfile> sortAndAddSections(ArrayList<UserProfile> itemList){
        ArrayList<UserProfile> tempList = new ArrayList<UserProfile>();
        Collections.sort(itemList);
        String header = "";
        for (int i = 0; i < itemList.size(); i++){
            if(header != itemList.get(i).getCategory()){
                UserProfile sectionCell = new UserProfile(itemList.get(i).getBitmap(),itemList.get(i).getTo(),itemList.get(i).getAmount(),null);
                sectionCell.setSelectionHeader();
                tempList.add(sectionCell);
                header = itemList.get(i).getCategory();
            }
            tempList.add(itemList.get(i));
        }
        return tempList;

    }


}
