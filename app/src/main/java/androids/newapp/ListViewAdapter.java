package androids.newapp;

/**
 * Created by Lenovo on 08-Feb-18.
 */
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.iarcuschin.simpleratingbar.SimpleRatingBar;
import java.util.ArrayList;
import java.util.HashMap;


public class ListViewAdapter extends BaseAdapter {
    Context context;
    String Field;
    ArrayList<User> Users = new ArrayList<User>();


    public ListViewAdapter(Context c, ArrayList<User> users,String field){
        this.context = c;
        this.Users = users;
        this.Field = field;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder holder;
        LayoutInflater inflater;
        SessionManager session = new SessionManager(this.context);

        if(convertView == null){
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.listview_layout, null);
            holder = new Holder();
            holder.circleImageView = (ImageView) convertView.findViewById(R.id.circleImageView);
            holder.textView = (TextView) convertView.findViewById(R.id.name);
            holder.phno = (TextView) convertView.findViewById(R.id.no);
            holder.rating = (SimpleRatingBar)  convertView.findViewById(R.id.ratingBar);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

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

        holder.circleImageView.requestLayout();
        holder.circleImageView.getLayoutParams().height=200;
        holder.circleImageView.getLayoutParams().width=200;
        holder.circleImageView.setScaleType(ImageView.ScaleType.FIT_XY);

        holder.circleImageView.setImageBitmap(Users.get(position).getBitmap());

            holder.textView.setText(Users.get(position).getName());
            holder.phno.setText(Users.get(position).getPhoneNo());
            holder.rating.setRating(Users.get(position).getRating());


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    @Override
    public Object getItem(int position) {
        return Users.get(position);
    }


    public class Holder{
        ImageView circleImageView;
        TextView textView;
        TextView phno;
        SimpleRatingBar rating;
    }
}
