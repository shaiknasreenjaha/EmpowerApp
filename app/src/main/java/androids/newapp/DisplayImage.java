package androids.newapp;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.iarcuschin.simpleratingbar.SimpleRatingBar;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Lenovo on 21-Feb-18.
 */

public class DisplayImage extends AppCompatActivity {
    String category;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        category = IntentData.categoryIntent;
        descr = IntentData.descriptionIntent;
        sessionManager = new SessionManager(getApplicationContext());
        UserId = sessionManager.getUserDetails().get(SessionManager.KEY_NAME);

        if(category.equals("Received")) {

            setContentView(R.layout.image_profile);
            TextView y = (TextView)findViewById(R.id.hai);
            send = (ImageButton)findViewById(R.id.sendbid);
            sendamount = (EditText)findViewById(R.id.sentmsg);
            chance = (TextView)findViewById(R.id.NotPossible);
            chance.setVisibility(View.INVISIBLE);

            String amount1 = IntentData.bidAmount;
            dbHelper = new DBHelper(this);
            dbHelper.open();

            if(amount1.equals("")){

                send.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String amount = sendamount.getText().toString();
                        sendamount.setText("");
                        dbHelper.UpdateAmount(UserId,descr,IntentData.dateIntent,amount);
                        chance.setVisibility(View.VISIBLE);
                        send.setVisibility(View.INVISIBLE);
                        sendamount.setVisibility(View.INVISIBLE);

                    }
                });
            }else {
                chance.setVisibility(View.VISIBLE);
                send.setVisibility(View.INVISIBLE);
                sendamount.setVisibility(View.INVISIBLE);
            }
            _imv= (ImageView)findViewById(R.id.imagePost);
            _imv.requestLayout();
            _imv.getLayoutParams().height=500;
            _imv.getLayoutParams().width=500;
            _imv.setScaleType(ImageView.ScaleType.FIT_XY);
            _imv.setImageBitmap(IntentData.imageIntent);
            y.setText(IntentData.descriptionIntent);

        }
        else {
            setContentView(R.layout.comment_list);
            dbHelper = new DBHelper(this);
            dbHelper.open();
            userProfiles = dbHelper.bidding(UserId,descr,IntentData.dateIntent);
            dbHelper.close();

            if(userProfiles!=null && userProfiles.size()>0) {

                if(userProfiles.size()== 1 && userProfiles.get(0).getStatus().equals("done")){

                    setContentView(R.layout.activity_sent);
                    TextView y = (TextView)findViewById(R.id.sentprofiledes);
                    TextView noBid = (TextView)findViewById(R.id.nobid);

                    _imv= (ImageView)findViewById(R.id.imagePostSent);
                    _imv.requestLayout();
                    _imv.getLayoutParams().height=500;
                    _imv.getLayoutParams().width=500;
                    _imv.setScaleType(ImageView.ScaleType.FIT_XY);
                    _imv.setImageBitmap(IntentData.imageIntent);
                    y.setText(IntentData.descriptionIntent);
                    noBid.setText("THIS WORK IS GIVEN TO "+userProfiles.get(0).getTo());
                    final TextView rate = (TextView)findViewById(R.id.rate);
                    dbHelper.open();

                    if(dbHelper.getRating(userProfiles.get(0).getTo(),UserId,descr,IntentData.dateIntent) == 0.0f) {
                        rate.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder mBuilder = new AlertDialog.Builder(DisplayImage.this);
                                //setContentView(R.layout.rating_popup);
                                //View newLayout = getLayoutInflater().inflate(R.layout.rating_popup);
                                mBuilder.setIcon(android.R.drawable.star_big_on);
                                mBuilder.setTitle("Vote!!");
                                LinearLayout ll = new LinearLayout(DisplayImage.this);
                                final RatingBar rating = new RatingBar(DisplayImage.this);
                                rating.setNumStars(5);   //here i used only 5
                                rating.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
                                ll.addView(rating);
                                mBuilder.setView(ll);

                                rating.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                                    @Override
                                    public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                                        System.out.println("Rated val:" + v);
                                        value = v;
                                        // Toast.makeText(getApplicationContext(),v + "",Toast.LENGTH_SHORT).show();
                                    }
                                });
                                mBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(getApplicationContext(), "" + value, Toast.LENGTH_SHORT).show();
                                        dbHelper = new DBHelper(DisplayImage.this);
                                        dbHelper.open();
                                        boolean i = dbHelper.UpdateRating(userProfiles.get(0).getTo(), UserId, IntentData.dateIntent, descr, value);
                                        if (i) {
                                            boolean c = dbHelper.updateUserRating(userProfiles.get(0).getTo());

                                            Toast.makeText(getApplicationContext(), c + "", Toast.LENGTH_SHORT).show();
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
                    }else{
                        rate.setVisibility(View.GONE);
                    }

                }
                else {
                    userProfiles = sortAndAddSections(userProfiles);
                    listView = (ListView) findViewById(R.id.commentList);
                    CommentListAdapter commentListAdapter = new CommentListAdapter(this, userProfiles, IntentData.imageIntent, descr, IntentData.dateIntent);
                    listView.setAdapter(commentListAdapter);
                }

            }
            else {
                setContentView(R.layout.activity_sent);
                TextView y = (TextView)findViewById(R.id.sentprofiledes);
                _imv= (ImageView)findViewById(R.id.imagePostSent);
                Toast.makeText(getApplicationContext(),category,Toast.LENGTH_LONG).show();
                _imv.requestLayout();
                _imv.getLayoutParams().height=500;
                _imv.getLayoutParams().width=500;
                _imv.setScaleType(ImageView.ScaleType.FIT_XY);
                _imv.setImageBitmap(IntentData.imageIntent);
                y.setText(descr);
                TextView noBid = (TextView )findViewById(R.id.nobid);
                if(category.equals("Done")){
                    noBid.setText("YOU ARE ASSIGNED THIS WORK");
                }
                TextView txt = (TextView)findViewById(R.id.rate);
                txt.setVisibility(View.INVISIBLE);
            }
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
