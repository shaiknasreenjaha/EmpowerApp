package androids.newapp;

/**
 * Created by Lenovo on 08-Feb-18.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;

public class DBHelper {

    public static final String IMAGE_ID = "id";
    public static final String IMAGE = "image";
    private final Context mContext;
    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;
    ArrayList<UserProfile> Profiles = new ArrayList<UserProfile>();
    ArrayList<UserProfile> users = new ArrayList<UserProfile>();
    ArrayList<User> employeeList = new ArrayList<User>();
    ArrayList<String> PhoneNos = new ArrayList<String>();
    ArrayList<String> MapLocas = new ArrayList<String >();
    User userDetails;

    private static final String DATABASE_NAME = "EMPOWER.db";
    private static final int DATABASE_VERSION = 15;
    private static final String IMAGES_TABLE = "USER";


    private static final String CREATE_IMAGES_TABLE =
            "CREATE TABLE " + IMAGES_TABLE + " ("
                    + IMAGE + " BLOB NOT NULL," + "Name TEXT,PhoneNo TEXT PRIMARY KEY,Password TEXT,Skill TEXT,Address TEXT,City TEXT,Rating FLOAT);";

    private static final String Post_Table =
            "CREATE TABLE " + "POST" + " ("
                    + IMAGE + " BLOB NOT NULL," + "ToUser TEXT,FromUser TEXT,DateOfPost TEXT,Description TEXT,Message TEXT,Status TEXT,UserRating FLOAT,FOREIGN KEY(ToUser) REFERENCES USER (PhoneNo));";

    public User showProfile(String phneNo) {
        Cursor showprofile = mDb.query(true,"USER",new String[] {IMAGE,"Rating","Name"},"PhoneNo"+"=?",new String[]{phneNo},null,null,null,null,null);
        if(showprofile.moveToFirst()){
            byte[] blob = showprofile.getBlob(showprofile.getColumnIndex(IMAGE));
            String name = showprofile.getString(showprofile.getColumnIndex("Name"));
            Float rating = showprofile.getFloat(showprofile.getColumnIndex("Rating"));
            return new User(Utils.getImage(blob),name,phneNo,rating);
        }
        return null;
    }


    public static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_IMAGES_TABLE);
            db.execSQL(Post_Table);
        }


        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + IMAGES_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + "POST");
            onCreate(db);
        }
    }

    public void Reset() {
        mDbHelper.onUpgrade(this.mDb, 1, 1);
    }

    public DBHelper(Context ctx) {
        mContext = ctx;
        mDbHelper = new DatabaseHelper(mContext);
    }

    public DBHelper open() throws SQLException {
        mDb = mDbHelper.getWritableDatabase();
        return this;
    }


    public void close() {
        mDbHelper.close();
    }




    public String provideLogin(String phone) {
        String password = "";
        Cursor cursor1 = mDb.query(true,"USER",new String[] {"Password"},"PhoneNo"+"=?",new String[] {phone},null,null,null,null,null);
        if(cursor1.moveToFirst()){
            password = cursor1.getString(cursor1.getColumnIndex("Password"));
            cursor1.close();
        }
        return password;
    }


    //update details
    public boolean UpdateUserDetails(byte[] imageBytes,String phno,String address,String city,String password){
        ContentValues cv = new ContentValues();
        cv.put(IMAGE,imageBytes);
        cv.put("Password",password);
        cv.put("Address",address.toLowerCase());
        cv.put("City",city.toLowerCase());
        mDb.update("USER",cv,"PhoneNo = ?",new String[] {phno});
        return true;
    }


    // Insert the image to the Sqlite DB

    public boolean insertImage(byte[] imageBytes,String name, String phoneNo, String password, String skill, String address,String city,Float rating) {
        ContentValues cv = new ContentValues();
        cv.put(IMAGE, imageBytes);
        cv.put("Name",name);
        cv.put("PhoneNo",phoneNo);
        cv.put("Password",password);
        cv.put("Skill",skill);
        cv.put("Address",address);
        cv.put("City", city);
        cv.put("Rating",rating);
       long i = mDb.insert("USER", null, cv);
        if(i == -1)
            return false;
        return true;
    }


    public boolean updateUserRating(String to){
        Cursor updateRating  = mDb.query("POST",new String[]{"FromUser"},"UserRating"+"!= "+0.0 +" AND ToUser"+"=" +to,
                null,null,null,null);

        Cursor ratingSum = mDb.rawQuery("SELECT SUM(UserRating) as Total FROM " + "POST" + " WHERE ToUser = "+to, null);
        long count = updateRating.getCount();
        updateRating.close();
        float avg,total=0;
        if (ratingSum.moveToFirst()) {

             total = ratingSum.getFloat(ratingSum.getColumnIndex("Total"));

        }
        avg = total / count;
        ratingSum.close();

        ContentValues cv2 = new ContentValues();
        cv2.put("Rating",avg);
        return mDb.update("USER",cv2,"PhoneNo = ?",new String[]{to})>0;

    }


    public float getRating(String to, String userId, String descr, String dateIntent) {
        Cursor rateUer = mDb.query(true, "POST",
                new String[] {"UserRating"}, "ToUser"+"=?"+"AND FromUser"+"=?"+"AND Description"+"=?"+"AND DateOfPost"+"=?",
                new String[] {to,userId,descr,dateIntent},null,null,null,null,null);
        float rating = 0;
        if(rateUer.moveToFirst()){
            rating = rateUer.getFloat(rateUer.getColumnIndex("UserRating"));
        }
        rateUer.close();
        return  rating;
    }

    public boolean UpdateRating(String to,String from,String date,String desc,Float rating){
        ContentValues contentValues1 = new ContentValues();
        contentValues1.put("UserRating",rating);
        return  mDb.update("POST",contentValues1,"ToUser = ?"+"AND Description = ?" + "AND DateOfPost = ?" + "AND FromUser = ?",
                new String[]{to, desc, date,from}) > 0;

        //mDb.update("POST",contentValues1,"ToUser = " )
    }



    public void insertPost(byte[] imageBytes, String toUser, String fromUser, String date, String desc) {
        ContentValues cv1 = new ContentValues();
        cv1.put(IMAGE, imageBytes);
        cv1.put("ToUser",toUser);
        cv1.put("FromUser",fromUser);
        cv1.put("DateOfPost",date);
        cv1.put("Description",desc);
        cv1.put("Message","");
        cv1.put("Status","notDone");
        cv1.put("UserRating",0.0f);
        mDb.insert("POST", null, cv1);
    }




    public String getUserName(String phn){
        Cursor name = mDb.query(true,"USER",new String[]{"Name"},"PhoneNo"+"=?",new String[]{phn}, null,null,null,null,null);
        if(name.moveToFirst())
            return name.getString(name.getColumnIndex("Name"));
        return "no Name";
    }

    public byte[] getUserImage(String phn){
        Cursor img = mDb.query(true,"USER",new String[]{IMAGE},"PhoneNo"+"=?",new String[]{phn},null,null,null,null,null);
        byte[] i = null;
        if(img.moveToFirst())
            i = img.getBlob(img.getColumnIndex(IMAGE));
        return i;
    }


    public ArrayList<User>retrieveLocationsForMapLogin(String skill,String Phone){
        Cursor locationlgin =  mDb.query(true,"USER",new String[]{"Address","PhoneNo"},"Skill"+"=?"+"AND PhoneNo"+"!=?",new String[]{skill,Phone},null,null,null,null,null);
        if(locationlgin.moveToFirst()){
            do{
                String loc = locationlgin.getString(locationlgin.getColumnIndex("Address"));
                String phno = locationlgin.getString(locationlgin.getColumnIndex("PhoneNo"));
                employeeList.add(new User(loc,phno));

            }while(locationlgin.moveToNext());
        }
        return employeeList;
    }

    public ArrayList<UserProfile> retrieveProfileDetails(String phno){
        Cursor worker = mDb.query(true,"POST", new String[]{IMAGE,"Description","DateOfPost","FromUser"}, "ToUser"+"=?"+"AND Status"+"=?",
                new String[] {phno,"done"}, null, null, null, null, null);
        if(worker.moveToFirst()){
            do{
                byte[] blob = worker.getBlob(worker.getColumnIndex(IMAGE));
                String from = worker.getString(worker.getColumnIndex("FromUser"));
                String username = getUserName(from);
                String description = worker.getString(worker.getColumnIndex("Description"));
                String date = worker.getString(worker.getColumnIndex("DateOfPost"));
                users.add(new UserProfile(Utils.getImage(blob),date,description,from,username));
            }while (worker.moveToNext());
        }
        worker.close();
        return users;
    }


    /*public ArrayList<UserProfile> retrieveProfileDetails(String phno){
        Cursor posting = mDb.query(true,"POST",new String[] {IMAGE,"FromUser","Description","DateOfPost"},
                "ToUser"+"=?"+"AND Status"+"=?",new String[] {phno,"done"},null,null,null,null,null);
        if(posting.moveToFirst()){
            do{
                byte[] blob = posting.getBlob(posting.getColumnIndex(IMAGE));
                String from = posting.getString(posting.getColumnIndex("FromUser"));
                String username = getUserName(from);
                String desc = posting.getString(posting.getColumnIndex("Description"));
                String dop = posting.getString(posting.getColumnIndex("DateOfPost"));
                Profiles.add(new UserProfile(Utils.getImage(blob),dop,desc,from,username));
            }while(posting.moveToNext());
        }
        posting.close();
        return Profiles;
    }*/


    public ArrayList<UserProfile> retrieveProfileDetails1(String phno){
        Cursor sent = mDb.query(true,"POST",new String[] {IMAGE,"Description","DateOfPost"},
                "FromUser"+"=?",new String[] {phno},null,null,null,null,null);
        if(sent.moveToFirst()){
            do{
                byte[] blob = sent.getBlob(sent.getColumnIndex(IMAGE));
                //String from = sent.getString(sent.getColumnIndex("ToUser"));
                //String username = getUserName(from);
                String desc = sent.getString(sent.getColumnIndex("Description"));
                String dop = sent.getString(sent.getColumnIndex("DateOfPost"));
                Profiles.add(new UserProfile(Utils.getImage(blob),dop,desc,"","","","Sent"));
            }while(sent.moveToNext());
        }
        sent.close();

        Cursor profile = mDb.query(true,"POST",new String[] {IMAGE,"FromUser","Description","DateOfPost","Message"},
                "ToUser"+"=?"+"AND Status"+"=?",new String[] {phno,"notDone"},null,null,null,null,null);
        if(profile.moveToFirst()){
            do{

                byte[] blob = profile.getBlob(profile.getColumnIndex(IMAGE));
                String from = profile.getString(profile.getColumnIndex("FromUser"));
                String username = getUserName(from);
                String message = profile.getString(profile.getColumnIndex("Message"));
                String desc = profile.getString(profile.getColumnIndex("Description"));
                String dop = profile.getString(profile.getColumnIndex("DateOfPost"));
                Profiles.add(new UserProfile(Utils.getImage(blob),dop,desc,from,username,message,"Received"));
            }while(profile.moveToNext());
        }
        profile.close();

        Cursor done = mDb.query(true,"POST",new String[] {IMAGE,"FromUser","Description","DateOfPost","Message"},
                "ToUser"+"=?"+"AND Status"+"=?",new String[] {phno,"done"},null,null,null,null,null);
        if(done.moveToFirst()){
            do{

                byte[] blob = done.getBlob(done.getColumnIndex(IMAGE));
                String from = done.getString(done.getColumnIndex("FromUser"));
                String username = getUserName(from);
                String message = done.getString(done.getColumnIndex("Message"));
                String desc = done.getString(done.getColumnIndex("Description"));
                String dop = done.getString(done.getColumnIndex("DateOfPost"));
                Profiles.add(new UserProfile(Utils.getImage(blob),dop,desc,from,username,message,"Done"));
            }while(done.moveToNext());
        }
        done.close();
        return Profiles;
    }


    public ArrayList<UserProfile> bidding(String from, String des, String date){
        Cursor bid = mDb.query(true,"POST",new String[] {"ToUser","Message","Status"},
                "FromUser"+"=?"+"AND Description"+"=?"+"AND DateOfPost"+"=?"+"AND Message"+"!=?", new String[] {from,des,date,""},
                null,null,null,null,null);
        if(bid.moveToFirst()){
            do{
                String toNo = bid.getString(bid.getColumnIndex("ToUser"));
                String msg = bid.getString(bid.getColumnIndex("Message"));
                String status = bid.getString(bid.getColumnIndex("Status"));
                byte[] image = getUserImage(toNo);
                Profiles.add(new UserProfile(Utils.getImage(image),toNo,status,msg));
            }while (bid.moveToNext());
        }
        bid.close();
        return Profiles;
    }



    //update bidamount in post

    public void UpdateAmount(String user,String desc,String date,String amount){
        ContentValues contentValues = new ContentValues();
        contentValues.put("Message",amount);
        mDb.update("POST",contentValues,"ToUser = ? AND Description = ? AND DateOfPost = ?",new String[]{user,desc,date});

    }





    public ArrayList<String> retrievePhoneNo(String location,String skill,String phone){
        Cursor cities = mDb.query(true,"USER",new String[] {"PhoneNo"},"City"+"=?"+ "AND Skill"+"=?"+"AND PhoneNo"+"!=?",
                new String[] {location,skill,phone},null,null,null,null,null);
        if(cities.moveToFirst()){
            do{
                String C = cities.getString(cities.getColumnIndex("PhoneNo"));
                PhoneNos.add(C);
            }while (cities.moveToNext());
        }
        return PhoneNos;

    }


    public ArrayList<User> retrieveUsersforLogin(String skill,String phone) throws SQLException {
        Cursor cursor2 = mDb.query(true, IMAGES_TABLE, new String[] { IMAGE,"Address",
                "Name", "PhoneNo","City","Rating"}, "Skill" + "=?"+ "AND PhoneNo"+"!=?",new String[] {skill,phone}, null,null, null, null, null);
        if (cursor2.moveToFirst()) {
            do {
                byte[] blob = cursor2.getBlob(cursor2.getColumnIndex(IMAGE));
                String name = cursor2.getString(cursor2.getColumnIndex("Name"));
                String phno = cursor2.getString(cursor2.getColumnIndex("PhoneNo"));
                Float rating = cursor2.getFloat(cursor2.getColumnIndex("Rating"));
                employeeList
                        .add(new User(Utils.getImage(blob), name, phno,rating));
            } while (cursor2.moveToNext());
        }

        return employeeList;
    }


    public ArrayList<User> retrieveLocationsForMap(String skill){
        Cursor maplocs = mDb.query(true,"USER",new String[]{"Address","PhoneNo"},"Skill"+"=?",new String[]{skill},null,null,null,null,null);
        if(maplocs.moveToFirst()){
            do{
                String loc = maplocs.getString(maplocs.getColumnIndex("Address"));
                String phno = maplocs.getString(maplocs.getColumnIndex("PhoneNo"));
                employeeList.add(new User(loc,phno));

            }while(maplocs.moveToNext());
        }
        maplocs.close();
        return employeeList;
    }


    public ArrayList<User> retrieveUsers(String skill) throws SQLException {
        Cursor cur = mDb.query(true, IMAGES_TABLE, new String[] { IMAGE,
                "Name", "PhoneNo","City","Rating"}, "Skill" + "=?",new String[] {skill}, null,null, null, null, null);
        if (cur.moveToFirst()) {
            do {
                byte[] blob = cur.getBlob(cur.getColumnIndex(IMAGE));
                String name = cur.getString(cur.getColumnIndex("Name"));
                String phno = cur.getString(cur.getColumnIndex("PhoneNo"));
                Float rating = cur.getFloat(cur.getColumnIndex("Rating"));
                employeeList
                        .add(new User(Utils.getImage(blob), name, phno,rating));
            } while (cur.moveToNext());
        }

        return employeeList;
    }

    //changing status

    public void changeStatus(String to, String desc, String date){
        ContentValues contentValues = new ContentValues();
        contentValues.put("Status", "done");
        //contentValues.put("Category","done");
        mDb.update("POST",contentValues,"ToUser = ?"+"AND Description = ?" + "AND DateOfPost = ?", new String[]{to, desc, date});
    }


    //delete on accepting the post
    public boolean deletePost(String from,String to,String descrip,String date){
       return mDb.delete("POST","FromUser"+"=?"+ " AND ToUser"+"!=?"+ " AND Description"+"=?"+ " AND DateOfPost"+"=?",
                new String[] {from, to, descrip, date}) > 0;
    }

    //settings

    public User getUserDetails(String phno){
        Cursor user = mDb.query(true,"USER",new String[] {IMAGE,"Address","City","Password","Skill"}, "PhoneNo"+"=?",new String[]{phno},null,null,null,null,null);
        if(user.moveToFirst()) {

            byte[] blob = user.getBlob(user.getColumnIndex(IMAGE));
            String password = user.getString(user.getColumnIndex("Password"));
            String skill = user.getString(user.getColumnIndex("Skill"));
            String address = user.getString(user.getColumnIndex("Address"));
            String city = user.getString(user.getColumnIndex("City"));

            user.close();
            userDetails = new User(Utils.getImage(blob), city, password, address, skill);
        }
        return userDetails;
    }

    //

    //for mapping on google map

    /*public ArrayList<MapUser> retrieveMapUsers(String city) throws SQLException{
        Cursor cursor = mDb.query(true, IMAGES_TABLE, new String[] { IMAGE,
                "Name", "PhoneNo","City","Rating"}, null, null,null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                byte[] blob = cursor.getBlob(cursor.getColumnIndex(IMAGE));
                String name = cursor.getString(cursor.getColumnIndex("Name"));
                String phno = cursor.getString(cursor.getColumnIndex("PhoneNo"));
                String address = cursor.getString(cursor.getColumnIndex("Address"));
                String skill = cursor.getString(cursor.getColumnIndex("Skill"));
                mapUsers
                        .add(new MapUser(Utils.getImage(blob), name, phno, address,skill));
            } while (cursor.moveToNext());
        }
        return mapUsers;

    }*/
   /* public boolean UpdateData( String phoneNo, String password, String address,String city) {
        ContentValues cv = new ContentValues();
        //cv.put(IMAGE, imageBytes);
        cv.put("Password", password);
        cv.put("Address", address);
        cv.put("City", city);
        mDb.update("USER", cv, "PhoneNo = ?",new String[] {phoneNo});
        return true;
    }*/
}

