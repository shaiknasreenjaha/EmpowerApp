package androids.newapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.iarcuschin.simpleratingbar.SimpleRatingBar;

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
        for(UserProfile user:worksDone){
            Toast.makeText(context,user.getDescription(),Toast.LENGTH_SHORT).show();
        }
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
    public View getView(int position, View convertView, ViewGroup parent) {
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



        holder.circleImageView.setImageBitmap(worksDone.get(position).getBitmap());

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