package androids.newapp;

import android.graphics.Bitmap;

/**
 * Created by Lenovo on 10-Feb-18.
 */

public class UserProfile implements Comparable<UserProfile>{
    private String bmp;
    private String date;
    private String description;
    private String from;
    private  String name;
    private String category;
    private String Status;
    private String to;
    private boolean isSelectionHeader;

    String amount;

    public UserProfile(String bitmap,String to,String status,String msg){
        this.to = to;
        this.bmp = bitmap;
        this.amount = msg;
        this.category = "comment";
        this.Status = status;
    }
    public UserProfile(String  bitmap,String date,String description,String from, String name){
        this.bmp = bitmap;
        this.date = date;
        this.description = description;
        this.from = from;
        this.name = name;
    }
    public UserProfile(String bitmap,String date,String description,String from, String name,String msg, String category){
        this.bmp = bitmap;
        this.date = date;
        this.description = description;
        this.from = from;
        this.name = name;
        this.amount = msg;
        this.category = category;
        isSelectionHeader = false;
    }


    public String getStatus(){
        return Status;
    }

    public String getAmount(){
        return amount;
    }
    public String getTo(){return to;}
    public String getCategory(){return category;}

    public String getDate(){
        return date;
    }

    public String getDescription(){
        return description;
    }

    public String getFrom(){
        return from;
    }
    public String getBitmap() {
        return bmp;
    }
    public String getName(){
        return name;
    }
    public  void setSelectionHeader(){
        isSelectionHeader = true;
    }
    public boolean isSelectionHeader(){
        return isSelectionHeader;
    }

    @Override
    public int compareTo(UserProfile o) {
        return this.category.compareTo(o.category);
    }
}
