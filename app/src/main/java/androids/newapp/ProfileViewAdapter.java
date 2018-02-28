package androids.newapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;

/**
 * Created by Lenovo on 10-Feb-18.
 */

public class ProfileViewAdapter extends ArrayAdapter<UserProfile> {

    LayoutInflater inflater;
    Context context;
    TextView name,pno,date;
    ArrayList<UserProfile> Users = new ArrayList<UserProfile>();

    public ProfileViewAdapter(Context c, ArrayList<UserProfile> users){
        super(c,0,users);
        this.context = c;
        this.Users = users;
        inflater = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final UserProfile userProfile = getItem(position);
        if(userProfile.isSelectionHeader()){
            convertView = inflater.inflate(R.layout.section_header, null);
            convertView.setEnabled(false);
            convertView.setOnClickListener(null);
            TextView header = (TextView)convertView.findViewById(R.id.header);
            header.setText(Users.get(position).getName());
        }
        else {

            if(userProfile.getCategory().equals("Sent")) {
                convertView = inflater.inflate(R.layout.sent_layout,null);
                TextView des = (TextView) convertView.findViewById(R.id.sentDescription);
                des.setText( userProfile.getDescription());
                TextView date = (TextView) convertView.findViewById(R.id.sentDate);
                date.setText(Users.get(position).getDate());

            }
            else {
                convertView = inflater.inflate(R.layout.profile_list,null);
                //mageView imageView = (ImageView)v.findViewById(R.id.profileimage);
                //imageView.setImageBitmap(userProfile.getBitmap());
                name = (TextView) convertView.findViewById(R.id.fromName);
                date = (TextView) convertView.findViewById(R.id.profiledate);
                pno = (TextView) convertView.findViewById(R.id.profilefrom);
                date.setText(Users.get(position).getDate());
                name.setText(Users.get(position).getName());
                pno.setText(Users.get(position).getFrom());
            }

        }
        return convertView;
    }
}
