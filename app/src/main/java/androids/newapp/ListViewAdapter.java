package androids.newapp;

/**
 * Created by Lenovo on 08-Feb-18.
 */
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;


public class ListViewAdapter extends BaseAdapter {
    Context context;
    ProgressDialog progressDialog;
    String Field;
    ArrayList<User> Users = new ArrayList<User>();
    LayoutInflater inflater;


    public ListViewAdapter(Context c, ArrayList<User> users,String field){
        this.context = c;
        this.Users = users;
        this.Field = field;
        inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return Users.size();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return Users.get(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        convertView = inflater.inflate(R.layout.listview_layout,null);
        TextView phno = (TextView) convertView.findViewById(R.id.no);
        phno.setText(Users.get(position).getPhoneNo());
        TextView name = (TextView) convertView.findViewById(R.id.name);
        name.setText(Users.get(position).getName());
        final ImageView img = (ImageView) convertView.findViewById(R.id.circleImageView);
        SimpleRatingBar ratingBar = (SimpleRatingBar)convertView.findViewById(R.id.ratingBar);
        ratingBar.setRating(Users.get(position).getRating());
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
                    ImageManager.GetImage(Users.get(position).getUserImage(), imageStream, imageLength);
                    handler.post(new Runnable() {
                        public void run() {
                            byte[] buffer = imageStream.toByteArray();
                            Bitmap bitmap1 = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
                            img.setImageBitmap(bitmap1);
                           // Toast.makeText(ListViewAdapter.this.context,"image set",Toast.LENGTH_SHORT).show();
                           // progressDialog.dismiss();
                        }
                    });
                }
                catch(Exception ex) {
                   // final String exceptionMessage = ex.getMessage();
                    handler.post(new Runnable() {
                        public void run() {
                            ex.printStackTrace();
                            //Toast.makeText(ListViewAdapter.this.context, exceptionMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }});
        th.start();

        ImageButton call = (ImageButton) convertView.findViewById(R.id.call);
        ImageButton msg = (ImageButton) convertView.findViewById(R.id.message);
        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if(checkTelephonePermission())
                        callNumber(Users.get(position).getPhoneNo());
                    else
                        Toast.makeText(ListViewAdapter.this.context,"No permissions",Toast.LENGTH_SHORT).show();
                }
            }
        });

        msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendSMSMessage(Users.get(position).getPhoneNo());
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("Clicked",Users.get(position).getUserImage());
                IntentData.intentClass = 10;
                Intent intent= new Intent(ListViewAdapter.this.context,Workers.class);
                IntentData.skillIntent = Field;
                IntentData.ToIntent = Users.get(position).getPhoneNo();
                context.startActivity(intent);
            }
        });
        return convertView;
    }

    private void sendSMSMessage(String phno) {
        try {
            Uri uri = Uri.parse("smsto:"+ phno);
            Intent smsIntent = new Intent(Intent.ACTION_SENDTO, uri);
            context.startActivity(smsIntent);
        } catch (Exception e) {
            Toast.makeText(ListViewAdapter.this.context,"SMS faild, please try again later!",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }


    private void callNumber(String phno) {
        Toast.makeText(ListViewAdapter.this.context,phno,Toast.LENGTH_SHORT).show();
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + phno));
        if (ActivityCompat.checkSelfPermission(ListViewAdapter.this.context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        context.startActivity(callIntent);
    }


    public static final int MY_PERMISSIONS_REQUEST_CALL = 1;
    private boolean checkTelephonePermission() {
        if (ContextCompat.checkSelfPermission(ListViewAdapter.this.context, Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale((Activity)context,
                    Manifest.permission.CALL_PHONE)) {
                android.app.AlertDialog.Builder mBuilder = new android.app.AlertDialog.Builder(ListViewAdapter.this.context, android.app.AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                mBuilder
                        .setTitle("call pemission Needed")
                        .setMessage("This app needs the Phone permission, please accept to use phone functionality")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ActivityCompat.requestPermissions((Activity)context,
                                        new String[]{Manifest.permission.CALL_PHONE},
                                        MY_PERMISSIONS_REQUEST_CALL );
                            }
                        })
                        .create()
                        .show();
                return true;


            } else {
            }
        }
        return true;
    }


}
