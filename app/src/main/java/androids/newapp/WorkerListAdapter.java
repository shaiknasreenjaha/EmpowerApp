package androids.newapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by Lenovo on 10-Feb-18.
 */


public class WorkerListAdapter extends BaseAdapter {
    Context context;


    ArrayList<UserProfile> worksDone = new ArrayList<UserProfile>();


    public WorkerListAdapter(Context c,ArrayList<UserProfile> up) {
        this.context = c;
        this.worksDone = up;

    }

    @Override
    public int getCount() {
        return worksDone.size();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return worksDone.get(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater;
        final HolderView holder;


        if(convertView == null){
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.about_worker, null);
            holder = new HolderView();
            holder.circleImageView = (ImageView) convertView.findViewById(R.id.postImage);
            holder.dateofpost = (TextView) convertView.findViewById(R.id.postDate);
            holder.description = (TextView) convertView.findViewById(R.id.postDescription);
            convertView.setTag(holder);
        } else {
            holder = (HolderView) convertView.getTag();
        }



        holder.circleImageView.requestLayout();
        holder.circleImageView.getLayoutParams().height = 500;
        holder.circleImageView.getLayoutParams().width = 500;
        holder.circleImageView.setScaleType(ImageView.ScaleType.FIT_XY);

        //holder.circleImageView.setImageBitmap(worksDone.get(position).getBitmap());
        final ByteArrayOutputStream imageStream = new ByteArrayOutputStream();

        final Handler handler = new Handler();

        Thread th = new Thread(new Runnable() {
            public void run() {

                try {

                    long imageLength = 0;

                    ImageManager.GetImage(worksDone.get(position).getBitmap(), imageStream, imageLength);

                    handler.post(new Runnable() {

                        public void run() {
                            byte[] buffer = imageStream.toByteArray();

                            Bitmap bitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
                            //bitmap.compress(Bitmap.CompressFormat.PNG, 0, imageStream);
                            holder.circleImageView.setImageBitmap(bitmap);
                        }
                    });
                }
                catch(Exception ex) {
                    final String exceptionMessage = ex.getMessage();
                    handler.post(new Runnable() {
                        public void run() {
                            Toast.makeText(WorkerListAdapter.this.context, exceptionMessage, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }});
        th.start();

        holder.dateofpost.setText("Date : " + worksDone.get(position).getDate());
        holder.description.setText("Description : " + worksDone.get(position).getDescription());

        return convertView;
    }


    public class HolderView{
        ImageView circleImageView;
        TextView dateofpost;
        TextView description;
    }

}