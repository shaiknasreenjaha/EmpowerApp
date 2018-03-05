package androids.newapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.*;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Lenovo on 07-Feb-18.
 */

public class Description extends AppCompatActivity {
    private int GALLERY = 1,  CROP_PIC = 2;
    private ImageView ivImage;
    private String userChoosenTask;
    private EditText date;
    private EditText spec;
    private int Sdate,Smonth,Syear;
    private Button btn;
    SessionManager sessionManager;
    String userid;
    ArrayList<String> mapUser = new ArrayList<String>();
    DBHelper dbHelper;
    byte image[];
    String desc;
    String imageName;
    Uri contentURI;
    Calendar calendar;
    Connection connect;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description);
        date = (EditText)findViewById(R.id.editText2);
        sessionManager = new SessionManager(getApplicationContext());
        userid = sessionManager.getUserDetails().get(SessionManager.KEY_NAME);
        calendar = Calendar.getInstance();
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(Description.this);
                dialog.setContentView(R.layout.datepickerview);
                dialog.setTitle("");
                DatePicker datePicker = (DatePicker) dialog.findViewById(R.id.datePicker);
                datePicker.setMinDate(System.currentTimeMillis() - 1000);
                calendar.setTimeInMillis(System.currentTimeMillis());
                Sdate = calendar.get(Calendar.DAY_OF_MONTH);
                Smonth = calendar.get(Calendar.MONTH);
                Syear = calendar.get(Calendar.YEAR);
                datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        date.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                        dialog.dismiss();

                        Sdate = dayOfMonth;
                        Smonth = monthOfYear;
                        Syear = year;
                    }
                });
                dialog.show();
            }
        });

        spec = (EditText)findViewById(R.id.editText4);

        ivImage = (ImageView) findViewById(R.id.select_img);
        ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplication(), "selected", Toast.LENGTH_SHORT).show();
                showPictureDialog();
              }
        });

        btn = (Button)findViewById(R.id.send);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    mapUser = IntentData.phoneNos;
                    if(saveImageInDB(contentURI,mapUser,userid)){
                        Toast.makeText(getApplicationContext(),"Successfully Sent",Toast.LENGTH_SHORT).show();
                        date.setText("");
                        spec.setText("");
                        ivImage.setImageResource(R.drawable.image);
                    }else {
                    }
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }



    private void showPictureDialog(){
        android.app.AlertDialog.Builder pictureDialog = new android.app.AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = { "Select photo from gallery", "Cancel" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallery();
                                break;
                            case 1:
                                dialog.dismiss();
                        }
                    }
                });
        pictureDialog.show();
    }


    public void choosePhotoFromGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == this.RESULT_CANCELED) {
            return;
        }
        if (requestCode == GALLERY) {
            if (data != null) {
                contentURI = data.getData();
                //Toast.makeText(Description.this,"img selected  : ",Toast.LENGTH_LONG).show();

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    image =  Utils.getImageBytes(bitmap);
                    ivImage.setImageBitmap(bitmap);
                    progressDialog = new ProgressDialog(Description.this, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
                    progressDialog.setMessage("Loading ...");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    UploadImage();


                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(Description.this, "Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }



    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Description.this,MapsActivity.class);
        startActivity(intent);
    }



    private void UploadImage()    {
        try {
            final InputStream imageStream = getContentResolver().openInputStream(this.contentURI);
            final int imageLength = imageStream.available();

            final Handler handler = new Handler();

            Thread th = new Thread(new Runnable() {
                public void run() {

                    try {
                        imageName = ImageManager.UploadImage(imageStream, imageLength);
                        handler.post(new Runnable() {
                            public void run() {
                                Toast.makeText(Description.this, "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                progressDialog.cancel();
                            }
                        });
                    }
                    catch(Exception ex) {
                        final String exceptionMessage = ex.getMessage();
                        handler.post(new Runnable() {
                            public void run() {
                                Toast.makeText(Description.this, exceptionMessage, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }});
            th.start();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }



    Boolean saveImageInDB(Uri selectedImageUri,ArrayList<String>phno,String From) {
        try {
            dbHelper = new DBHelper(this);
            dbHelper.open();
            desc = spec.getText().toString();
            String Dateto = date.getText().toString();
            if(desc==null ||desc.isEmpty()){
                Toast.makeText(getApplication(),"Description is needed",Toast.LENGTH_SHORT).show();
            }else if(Dateto == null||Dateto.isEmpty()){
                Toast.makeText(getApplication(),"Date is needed",Toast.LENGTH_SHORT).show();
            }
            else if(selectedImageUri == null){
                Toast.makeText(getApplication(),"image is needed",Toast.LENGTH_SHORT).show();
            }else  {

                ConnectionHelper conStr=new ConnectionHelper();
                connect =conStr.connectionclasss();        // Connect to database
                if (connect == null)          {
                    //ConnectionResult = "Check Your Internet Access!";
                    Toast.makeText(getApplicationContext(),"Check Your Internet Access!",Toast.LENGTH_SHORT).show();
                }
                else {

                for (String post : phno) {
                    dbHelper.insertPost(imageName, post, From, Dateto, desc,connect);
                }
                dbHelper.close();
                return true;
                }
            }
            return false;
        } catch (Exception ioe) {
            dbHelper.close();
            return false;
        }
    }

}
