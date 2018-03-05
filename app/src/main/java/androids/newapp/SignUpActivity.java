package androids.newapp;

/**
 * Created by Lenovo on 07-Feb-18.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import de.hdodenhof.circleimageview.CircleImageView;


public class SignUpActivity extends Activity implements OnClickListener {
    private int  GALLERY = 1;
    private Button signUp;
    private EditText pNo, psw;
    private EditText text,address,city;
    private Spinner field;
    private CircleImageView img;
    private byte[] image;
    String name,phoneNo,password,fieldOfWork,Address,City;
    private AwesomeValidation awesomeValidation;
    Uri contentURI;
    DBHelper dbHelper;
    Connection connect;
    String ConnectionResult = "";
    String imageName;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        setContentView(R.layout.activity_signup);
        img = (CircleImageView) findViewById(R.id.ImageView3);
        pNo = (EditText) findViewById(R.id.phone);
        psw = (EditText) findViewById(R.id.password);
        text = (EditText) findViewById(R.id.input_name);
        address = (EditText) findViewById(R.id.address);
        city = (EditText) findViewById(R.id.city);
        field = (Spinner) findViewById(R.id.spinner);
        signUp = (Button) findViewById(R.id.btn_signup);

        awesomeValidation.addValidation(this, R.id.input_name, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.nameerror);
        awesomeValidation.addValidation(this, R.id.password, "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\S+$).{6,}$", R.string.passworderror);
        awesomeValidation.addValidation(this, R.id.phone, "^[2-9]{2}[0-9]{8}$", R.string.mobileerror);
        awesomeValidation.addValidation(this, R.id.address, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.locationerror);
        awesomeValidation.addValidation(this, R.id.city, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.cityerror);
        //awesomeValidation.addValidation(this,field,"^Choose Your Skill$","Please Choose your Skill");
        dbHelper = new DBHelper(this);

        signUp.setOnClickListener(this);

        img.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplication(), "selected", Toast.LENGTH_SHORT).show();
                showPictureDialog();

            }
        });
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
                                break;
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
                //Toast.makeText(SignUpActivity.this,"img selected  : "+contentURI,Toast.LENGTH_LONG).show();

                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    image =  Utils.getImageBytes(bitmap);
                    img.setImageBitmap(bitmap);
                    progressDialog = new ProgressDialog(SignUpActivity.this, ProgressDialog.THEME_DEVICE_DEFAULT_LIGHT);
                    progressDialog.setMessage("Loading ...");
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    UploadImage();


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
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
                                Toast.makeText(SignUpActivity.this, "Image Uploaded Successfullly ", Toast.LENGTH_SHORT).show();
                                progressDialog.cancel();
                            }
                        });
                    }
                    catch(Exception ex) {
                        handler.post(new Runnable() {
                            public void run() {
                               // Toast.makeText(SignUpActivity.this, exceptionMessage, Toast.LENGTH_SHORT).show();
                                ex.printStackTrace();
                            }
                        });
                    }
                }});
            th.start();
        }
        catch(Exception ex) {

            Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View v) {
        if(v==signUp) {
            submitForm(v);
        }
    }

    private void submitForm(View v) {
        if (awesomeValidation.validate()) {
            if(field.getSelectedItem().toString().equals("Choose a Skill")){
                Toast.makeText(getApplicationContext(),"Please Choose your Skill!!!",Toast.LENGTH_SHORT).show();
            }else {
                if (saveImageInDB(contentURI)) {
                    Toast.makeText(getApplicationContext(), "Successfully Registered", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(v.getContext(), "Phone Number already exists", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    Boolean saveImageInDB(Uri selectedImageUri) {
        dbHelper.open();
        try {
            ConnectionHelper conStr=new ConnectionHelper();
            connect =conStr.connectionclasss();
            if (connect == null)          {
                ConnectionResult = "Check Your Internet Access!";
                Toast.makeText(getApplicationContext(),ConnectionResult,Toast.LENGTH_LONG).show();
            }
            else {
                //UploadImage();
                name = text.getText().toString();
                phoneNo = pNo.getText().toString();
                password = psw.getText().toString();
                Address = address.getText().toString();
                City = city.getText().toString();
                fieldOfWork = field.getSelectedItem().toString();
                if(imageName == null || imageName=="")
                    imageName = "bgvachgmzu";
                String query1 = "INSERT INTO worker VALUES ('"+imageName+"','"+name+"','"+phoneNo+"','"+password+"','"+fieldOfWork+"','"+
                        Address.toLowerCase()+"','"+City.toLowerCase()+"',1.0)";
                Statement statement = connect.createStatement();
                int success = statement.executeUpdate(query1);
                if(success>0) {
                    return true;
                }
                connect.close();
            }
            return false;
        }catch (SQLException e) {
            Log.e("sqlexception",e.toString());
            e.printStackTrace();
            return false;
        }catch (Exception ioe) {
            Log.e("Ioexception",ioe.toString());
            dbHelper.close();
            return false;
        }
    }


    public  void login(View v){
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
        finish();
    }
}