package androids.newapp;

/**
 * Created by Lenovo on 07-Feb-18.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;



public class CustomGridViewActivity extends BaseAdapter{
    private Context mContext;
    public final String[] gridViewString;
    public final int[] gridViewImageId;
    LayoutInflater inflater;

    public CustomGridViewActivity(Context context, String[] gridViewString, int[] gridViewImageId) {
        mContext = context;
        this.gridViewImageId = gridViewImageId;
        this.gridViewString = gridViewString;
        inflater = (LayoutInflater.from(context));
    }

    @Override
    public int getCount() {
        return gridViewString.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }




    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if(convertView == null){
                convertView = inflater.inflate(R.layout.layout,parent,false);
                ImageView imageView = (ImageView)convertView.findViewById(R.id.android_gridview_image);
                TextView textView = (TextView) convertView.findViewById(R.id.android_gridview_text);
                imageView.setImageResource(gridViewImageId[i]);
                textView.setText(gridViewString[i]);
            }
        }
        return convertView;
    }
}



