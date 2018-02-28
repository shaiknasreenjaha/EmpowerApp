package androids.newapp;

import android.graphics.Bitmap;

/**
 * Created by Lenovo on 08-Feb-18.
 */

public class MapUser {
    private Bitmap bmp;
    private String name;
    private String phoneNo;
    private String skill;
    private String address;

    public MapUser(Bitmap b, String n, String phno, String address, String skill) {
        bmp = b;
        name = n;
        phoneNo = phno;
        this.skill = skill;
        this.address = address;
    }

    public Bitmap getBitmap() {
        return bmp;
    }

    public String getName() {
        return name;
    }

    public String getPhoneNo() {
        return phoneNo;
    }
    public String getSkill(){
        return skill;
    }
    public String getAddress(){
        return address;
    }

}
