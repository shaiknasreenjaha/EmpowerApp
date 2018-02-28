package androids.newapp;

/**
 * Created by Lenovo on 08-Feb-18.
 */

    import android.graphics.Bitmap;

public class User {
    private Bitmap bmp;
    private String name;
    private String phoneNo;
    private Float rating;
    private  String city;
    private String skill;
    private String address;
    private String password;

    public User(String loc,String phno){
        this.city = loc;
        this.phoneNo = phno;

    }

    public User(Bitmap b, String city, String pwd, String address, String skill) {
        bmp = b;
        this.city = city;
        this.password = pwd;
        this.skill = skill;
        this.address = address;
    }

    public User(Bitmap b, String n, String k,Float r) {
        bmp = b;
        name = n;
        phoneNo = k;
        rating = r;
    }


    public User(Bitmap b, String c, String ad) {
        bmp = b;
        city = c;
        address = ad;
    }

    public String getPassword(){return password;}

    public String getSkill(){
        return skill;
    }

    public String getCity(){
        return city;
    }
    public String getAddress(){
        return address;
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


    public Float getRating() {
        return rating;
    }

}


