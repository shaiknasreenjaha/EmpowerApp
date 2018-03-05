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
import android.util.Log;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
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


    /*private static final String CREATE_IMAGES_TABLE =
            "CREATE TABLE " + IMAGES_TABLE + " ("
                    + IMAGE + " BLOB NOT NULL," + "Name TEXT,PhoneNo TEXT PRIMARY KEY,Password TEXT,Skill TEXT,Address TEXT,City TEXT,Rating FLOAT);";

    private static final String Post_Table =
            "CREATE TABLE " + "POST" + " ("
                    + IMAGE + " BLOB NOT NULL," + "ToUser TEXT,FromUser TEXT,DateOfPost TEXT,Description TEXT,Message TEXT,Status TEXT,UserRating FLOAT,FOREIGN KEY(ToUser) REFERENCES USER (PhoneNo));";

   */

    public static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        public void onCreate(SQLiteDatabase db) {
           // db.execSQL(CREATE_IMAGES_TABLE);
            //db.execSQL(Post_Table);
        }


        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
           // db.execSQL("DROP TABLE IF EXISTS " + IMAGES_TABLE);
           // db.execSQL("DROP TABLE IF EXISTS " + "POST");
           // onCreate(db);
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



    public User showProfile(String phneNo, Connection connection) {

        String query = "SELECT * FROM worker WHERE PhoneNo = '"+phneNo+"'";
        Statement statement = null;
        try {
            statement = connection.createStatement();
            ResultSet showprofile = statement.executeQuery(query);
            if(showprofile.next()){
                String blob = showprofile.getString("user_photo");
                String name = showprofile.getString("Fname");
                Float rating = showprofile.getFloat("rating");
                String address = showprofile.getString("useraddress");
                String city = showprofile.getString("city");
                return new User(blob,name,phneNo,rating,address,city);
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return null;
    }




    //update details
    public boolean UpdateUserDetails(String image,String phno,String address,String city,String password,Connection connection){

        String query = "update worker set  user_photo = '"+image+"',useraddress = '"+address+"',City ='"+city+"',ppassword = '"+password+"'  where PhoneNo ='"+ phno+"'";

        Statement stm = null;
        //Log.e("query",query);
        try {
            stm = connection.createStatement();
            int result = stm.executeUpdate(query);
           // Log.e("return",result+"");
            return true;
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            return  false;
        }
    }


    public boolean updateUserRating(String to, Connection connection){

        String query = "SELECT COUNT(*) as count FROM postings WHERE UserRating != 0.0 AND ToUser = '"+to+"'";
        //Log.e("query",query);
        Statement statement = null;
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            if (rs.next()) {
                long count = rs.getInt("count");
                String query1 = "SELECT SUM(UserRating) as Total FROM postings WHERE ToUser = '"+to+"'";
               // Log.e("query",query1);
                statement = connection.createStatement();
                ResultSet rs1 = statement.executeQuery(query1);
                float sum = 0.0f;
                if(rs1.next()) {
                    sum = rs1.getFloat("Total");
                   // Log.e("Sum",sum+"");
                 //   Log.e("Count",count+"");
                    float avg = sum/count;
               //     Log.e("Avg",avg+"");
                    String query2 = "UPDATE worker SET rating = '"+avg+"'"+" WHERE PhoneNo = '"+to+"'";
             //       Log.e("query",query2);
                    statement = connection.createStatement();
                    int i = statement.executeUpdate(query2);
                    return i > 0;
                }
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
           // Log.e("cgh",e.getMessage());
        }
        return false;
    }


    public float getRating(String to, String userId, String descr, String dateIntent,Connection connection) {


        String rate = "SELECT * FROM postings WHERE ToUser = '"+to+"' AND FromUser = '"+userId+"' AND DateOfPost = '"+dateIntent+"' AND " +
                "pDescription = '"+descr+"'";
        float rating = 0.0f;
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(rate);
            if(rs.next())
                rating = rs.getFloat("UserRating");
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
         //   Log.e("Exception",e.getMessage());
        }
        return  rating;
    }

    public boolean UpdateRating(String to,String from,String date,String desc,Float rating, Connection connection){

        String query = "UPDATE postings SET UserRating = '"+rating+"'"+" WHERE ToUser = '"+to+"'"+" AND pDescription = '"+desc+"'"+
                " AND DateOfPost = '"+date+"'"+" AND FromUser = '"+from+"'";
        Log.e("updateRating",query);
        Statement st = null;
        int i = 0;
        try {
            st = connection.createStatement();
            i = st.executeUpdate(query);
            Log.e("rating",i+"");
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return i > 0;
    }



    public void insertPost(String imageBytes, String toUser, String fromUser, String date, String desc,Connection connection) {

        String amount = "";

        String  query = "INSERT INTO postings VALUES ('"+imageBytes+"','"+toUser+"','"+fromUser+"','"+date+"','"+desc+"','"+amount+"','notDone',0.0)";
        Log.e("qhkluery",query);

        Statement statement = null;
        try {

            statement = connection.createStatement();
            int result = statement.executeUpdate(query);
            Log.e("qhkluery",result+"");

        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            Log.e("vhgv",e.getMessage());
        }
    }



    public String getUserName(String phn,Connection connection){

        String query = "SELECT * FROM worker where PhoneNo = '"+phn+"'";
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            if(rs.next())
                return rs.getString("Fname");

        }catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return "no Name";
    }


    public String getUserImage(String phn, Connection connection){
        String query = "SELECT * FROM worker WHERE PhoneNo = '"+phn+"'";
        Statement statement = null;
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            if(rs.next())
                return rs.getString("user_photo");
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return "";
    }


    public ArrayList<User>retrieveLocationsForMapLogin(String skill,String Phone,Connection connection){
        String query = "SELECT * FROM worker WHERE convert(varchar,skill) = '"+skill+"' AND PhoneNo != '"+Phone+"'";
        Log.e("query",query);
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            while(rs.next()){
                String loc = rs.getString("useraddress");
                String phno = rs.getString("PhoneNo");
                employeeList.add(new User(loc,phno));
            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return employeeList;
    }



    public ArrayList<UserProfile> retrieveProfileDetails(String phno,Connection connection){

        String query = "SELECT DISTINCT * FROM postings WHERE ToUser = '"+phno+"' AND pStatus = 'done'";
        Statement statement1 = null;
        ResultSet resultSets = null;
        try {
            statement1 = connection.createStatement();

            resultSets = statement1.executeQuery(query);
           // Log.e("insent",resultSets.getType()+"");
            while(resultSets.next()){
                String blob = resultSets.getString("dimage");

                String from = resultSets.getString("FromUser");
                String username = getUserName(from,connection);
                String description = resultSets.getString("pDescription");
                String date = resultSets.getString("DateOfPost");
                users.add(new UserProfile(blob,date,description,from,username));

            }
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
         //   Log.e("errorsent",e.getMessage());
        }
        return users;
    }


    public ArrayList<UserProfile> retrieveProfileDetails1(String phno,Connection connection){
        String sentquery = "SELECT  DISTINCT dimage,FromUser,pDescription,DateOfPost  FROM postings WHERE FromUser = '"+phno+"'";
        //Log.e("sentquery",sentquery);
        Statement statement = null;
        try {
            statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(sentquery);
            //Log.e("insent",rs.getFetchSize()+"sent size");
            while (rs.next()){
                String blob = rs.getString("dimage");
                //Log.e("insent",blob);
                String desc = rs.getString("pDescription");
                String dop = rs.getString("DateOfPost");
                Profiles.add(new UserProfile(blob,dop,desc,"","","","Sent"));
            }

        } catch (java.sql.SQLException e) {
            e.printStackTrace();
           // Log.e("errorsent",e.getMessage());
        }


        String receivedquery = "SELECT * FROM postings WHERE ToUser = '"+phno+"' AND pStatus = 'notDone'";
        //Log.e("receivedquery",receivedquery);
        Statement rest = null;
        try {
            rest = connection.createStatement();
            ResultSet receivesrs = rest.executeQuery(receivedquery);
            while (receivesrs.next()){
                String blob = receivesrs.getString("dimage");
                String from = receivesrs.getString("FromUser");
                String username = getUserName(from,connection);
                String message = receivesrs.getString("pMessage");
           //     Log.e("Inreceivedq",message);
                String desc = receivesrs.getString("pDescription");
                String dop = receivesrs.getString("DateOfPost");
                Profiles.add(new UserProfile(blob,dop,desc,from,username,message,"Received"));
            }

        } catch (java.sql.SQLException e) {
            e.printStackTrace();
         //   Log.e("errorsent",e.getMessage());
        }

        String donedquery = "SELECT * FROM postings WHERE ToUser = '"+phno+"' AND pStatus = 'done'";
        Log.e("donedquery",donedquery);
        Statement donest = null;
        try {
            donest = connection.createStatement();
            ResultSet doners = donest.executeQuery(donedquery);
         //   Log.e("size",doners.getFetchSize()+"");
            while (doners.next()){
                String blob = doners.getString("dimage");
                Log.e("image1",blob);
                String from = doners.getString("FromUser");
                String username = getUserName(from,connection);
                String message = doners.getString("pMessage");
                String desc = doners.getString("pDescription");
                String dop = doners.getString("DateOfPost");
                Profiles.add(new UserProfile(blob,dop,desc,from,username,message,"Done"));
            }

        } catch (java.sql.SQLException e) {
            e.printStackTrace();
          //  Log.e("errorsent",e.getMessage());
        }
        return Profiles;
    }


    public ArrayList<UserProfile> bidding(String from, String des, String date, Connection connection){


        String query = "SELECT * FROM postings WHERE FromUser  ='"+from+"'"+
                " AND pDescription = '"+des+"'"+" AND DateOfPost = '"+date+"'"+
                " AND pMessage != ''";
        //Log.e("bidding",query);
        Statement donest = null;
        try {
            donest = connection.createStatement();
            ResultSet doners = donest.executeQuery(query);
         //   Log.e("size",doners.getFetchSize()+"");
            while (doners.next()){
                String toNo = doners.getString("ToUser");
                //Log.e("image1",blob);
                String msg = doners.getString("pMessage");
                String status = doners.getString("pStatus");
                String image = getUserImage(toNo,connection);
                Profiles.add(new UserProfile(image,toNo,status,msg));
            }

        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return Profiles;
    }



    //update bidamount in post

    public void UpdateAmount(String user,String desc,String date,String amount, Connection connection){

        String query = "UPDATE postings SET pMessage = '"+amount+"'"+" WHERE ToUser = '"+user+"'"+" AND pDescription = '"+desc+"'"+
                " AND DateOfPost = '"+date+"'";

        Log.e("query",query);
        Statement donest = null;
        try {
            donest = connection.createStatement();
            int i = donest.executeUpdate(query);
            Log.e("resul",i+"");

        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }




    public ArrayList<User> retrieveUsersforLogin(String skill,String phone,Connection connection) throws SQLException {

        String query = "SELECT * FROM worker where convert(varchar,skill) = '"+skill+"' AND PhoneNo != '"+phone+"'" ;
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);

            while (rs.next()) {

                String imag = rs.getString("user_photo");
                String name = rs.getString("Fname");
                String phno = rs.getString("PhoneNo");
                Float rating = rs.getFloat("rating");
                String address = rs.getString("useraddress");
                String city = rs.getString("city");
                employeeList.add(new User(imag, name, phno,rating,address,city));

            }

        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return employeeList;
    }


    public ArrayList<User> retrieveLocationsForMap(String skill,Connection connection){

        String query = "SELECT * FROM worker WHERE convert(varchar,skill) = '"+skill+"'";
        Log.e("query",query);
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            Log.e("size",rs.getFetchSize()+"");
            while(rs.next()){
                String loc = rs.getString("useraddress");
                String phno = rs.getString("PhoneNo");
                Log.e("address",loc);
                employeeList.add(new User(loc,phno));
            }

        } catch (java.sql.SQLException e) {
            e.printStackTrace();
         //   Log.e("Mess",e.getMessage());
        }
        return employeeList;
    }


    //with cloud retrieve users

    public ArrayList<User> retrieveUsers(String skill,Connection connection) throws SQLException {

        String query = "SELECT * FROM worker where convert(varchar,skill) = '" + skill + "'";
        try {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);

            while (rs.next()) {

                String imag = rs.getString("user_photo");
                String name = rs.getString("Fname");
                String phno = rs.getString("PhoneNo");
                Float rating = rs.getFloat("rating");
                String address = rs.getString("useraddress");
                String city = rs.getString("city");
                employeeList.add(new User(imag, name, phno, rating,address,city));

            }

        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return employeeList;
    }


    //changing status

    public void changeStatus(String to, String desc, String date,Connection connection ){

        String query = "UPDATE postings SET pStatus = 'done' WHERE ToUser = '"+to+"'"+" AND pDescription = '"+desc+"'"+
                " AND DateOfPost = '"+date+"'";
        Statement statement = null;
        try {
            statement = connection.createStatement();
            int i = statement.executeUpdate(query);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }


    //delete on accepting the post
    public boolean deletePost(String from,String to,String descrip,String date,Connection connection){
        String query = "DELETE FROM postings WHERE FromUser = '"+from+"'"+" AND pDescription = '"+descrip+"'"+
                " AND DateOfPost = '"+date+"'"+ " AND ToUser != '"+to+"'";
        Statement statement = null;
        try {
            statement = connection.createStatement();
            int i = statement.executeUpdate(query);
            return i > 0;
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return  false;
    }




    public User getUserDetails(String phno,Connection connection)  {
        String query = "SELECT * FROM worker where PhoneNo = '"+phno+"'";
       // Log.e("query==",query);

        try {
            Statement statement = connection.createStatement();

            ResultSet rs = null;
            rs = statement.executeQuery(query);
            if(rs.next()){
                String blob = rs.getString("user_photo");
                String password = rs.getString("ppassword");
                String skill = rs.getString("skill");
                String address = rs.getString("useraddress");
                String city = rs.getString("city");
                userDetails = new User(blob, city, password, address, skill);

            }
     //       Log.e("Pass",userDetails.getUserImage());

        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
        return userDetails;
    }
}

