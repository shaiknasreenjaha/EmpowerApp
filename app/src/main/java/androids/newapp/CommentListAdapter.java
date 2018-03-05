package androids.newapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.util.ArrayList;

/**
 * Created by Lenovo on 10-Feb-18.
 */

public class CommentListAdapter extends ArrayAdapter<UserProfile> {

    LayoutInflater inflater;
    Context context;
    SessionManager sessionManager;
    Connection connection;
    TextView name,pno,date;
    ArrayList<UserProfile> Users = new ArrayList<UserProfile>();
    String bitmap;
    String date1;
    String userId;
    String description;
    DBHelper dbHelper;

    public CommentListAdapter(Context c, ArrayList<UserProfile> users,String bitmap,String description,String date){
        super(c,0,users);
        this.context = c;
        this.Users = users;
        this.description= description;
        this.date1 = date;
        inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.bitmap = bitmap;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final UserProfile userProfile = getItem(position);
        sessionManager = new SessionManager(getContext());
        userId =  sessionManager.getUserDetails().get(SessionManager.KEY_NAME);
        if(userProfile.isSelectionHeader()){
            convertView = inflater.inflate(R.layout.activity_sent, null);
            TextView txt = (TextView) convertView.findViewById(R.id.nobid);
            txt.setVisibility(View.GONE);

            TextView desc = (TextView)convertView.findViewById(R.id.sentprofiledes);
            desc.setText(description);
            convertView.setClickable(false);
            final ImageView imageView = (ImageView) convertView.findViewById(R.id.imagePostSent);
            final ByteArrayOutputStream imageStream = new ByteArrayOutputStream();

            final Handler handler = new Handler();

            Thread th = new Thread(new Runnable() {
                public void run() {

                    try {

                        long imageLength = 0;

                        ImageManager.GetImage(bitmap, imageStream, imageLength);

                        handler.post(new Runnable() {

                            public void run() {
                                byte[] buffer = imageStream.toByteArray();

                                Bitmap bitmap1 = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
                                //bitmap.compress(Bitmap.CompressFormat.PNG, 0, imageStream);
                                imageView.setImageBitmap(bitmap1);
                            }
                        });
                    }
                    catch(Exception ex) {
                        //final String exceptionMessage = ex.getMessage();
                        handler.post(new Runnable() {
                            public void run() {
                                //Toast.makeText(CommentListAdapter.this.context, exceptionMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }});
            th.start();
            TextView txt1 = (TextView)convertView.findViewById(R.id.rate);
            txt1.setVisibility(View.GONE);
        }
        else {
            convertView = inflater.inflate(R.layout.comment_view,null);
            TextView phno = (TextView) convertView.findViewById(R.id.sentNumber);
            phno.setText(Users.get(position).getTo());
                TextView des = (TextView) convertView.findViewById(R.id.sentComment);
                des.setText(Users.get(position).getAmount());
                final ImageView img = (ImageView) convertView.findViewById(R.id.sentImage);


            img.requestLayout();
            img.getLayoutParams().height=150;
            img.getLayoutParams().width=150;
            img.setScaleType(ImageView.ScaleType.FIT_XY);

            //img.setImageBitmap(Users.get(position).getBitmap());
            final ByteArrayOutputStream imageStream = new ByteArrayOutputStream();

            final Handler handler = new Handler();

            Thread th = new Thread(new Runnable() {
                public void run() {

                    try {

                        long imageLength = 0;

                        ImageManager.GetImage(Users.get(position).getBitmap(), imageStream, imageLength);


                        handler.post(new Runnable() {

                            public void run() {
                                byte[] buffer = imageStream.toByteArray();

                                Bitmap bitmap1 = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
                                img.setImageBitmap(bitmap1);
                            }
                        });
                    }
                    catch(Exception ex) {
                        final String exceptionMessage = ex.getMessage();
                        handler.post(new Runnable() {
                            public void run() {
                                Toast.makeText(CommentListAdapter.this.context, exceptionMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }});
            th.start();

            Button accept = (Button) convertView.findViewById(R.id.accept);
            dbHelper = new DBHelper(CommentListAdapter.this.context);

            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    IntentData.ToIntent = Users.get(position).getTo();
                    Intent intent = new Intent(CommentListAdapter.this.context,DispalyProfile.class);
                    context.startActivity(intent);

                }
            });

            accept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dbHelper.open();
                    ConnectionHelper connectionHelper =  new ConnectionHelper();
                    connection = connectionHelper.connectionclasss();        // Connect to database
                    if (connection == null) {
                        Toast.makeText(CommentListAdapter.this.context, "Check your internet Access", Toast.LENGTH_SHORT).show();
                    }else {
                        dbHelper.changeStatus(Users.get(position).getTo(), description, date1,connection );
                        boolean i = dbHelper.deletePost(userId, Users.get(position).getTo(), description, date1,connection);
                        Intent intent = new Intent(CommentListAdapter.this.context,DisplayImage.class);
                        context.startActivity(intent);
                    }
                    dbHelper.close();
                }
            });
        }
        return convertView;
    }
}
