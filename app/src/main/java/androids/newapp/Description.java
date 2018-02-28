package androids.newapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.*;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Lenovo on 07-Feb-18.
 */

public class Description extends AppCompatActivity {
    private int SELECT_FILE = 1,  CROP_PIC = 2;
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
    String desc;
    Uri selectImageUri = null;
    Calendar calendar;

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
                selectImage();
            }
        });

        btn = (Button)findViewById(R.id.send);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    mapUser = IntentData.phoneNos;
                    if(saveImageInDB(selectImageUri,mapUser,userid)){
                        for(String ph: mapUser)
                            Toast.makeText(getApplicationContext(),ph,Toast.LENGTH_SHORT).show();
                        date.setText("");
                        spec.setText("");
                        ivImage.setImageResource(R.drawable.image);
                    }else {
                    }



            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public void onBackPressed() {

        Intent intent = new Intent(Description.this,MapsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }



    private void selectImage() {
        final CharSequence[] items = {"Choose from Library", "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(Description.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utility.checkPermission(Description.this);
                if (items[item].equals("Choose from Library")) {
                    userChoosenTask ="Choose from Library";
                    if(result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }


    public void galleryIntent()    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                selectImageUri = data.getData();
                onSelectFromGalleryResult(data);
                performCrop(data.getData());
            }
            else if(requestCode == CROP_PIC){
                Bundle bundle = data.getExtras();
                Bitmap bitmap = bundle.getParcelable("data");
                ivImage.setImageBitmap(bitmap);
            }
        }
    }
    private void performCrop(Uri data){
        try{
            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            cropIntent.setDataAndType(data, "image/*");
            cropIntent.putExtra("outputX", 200);
            cropIntent.putExtra("outputY", 200);
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            cropIntent.putExtra("scale", true);
            cropIntent .putExtra("return-data", true);
            cropIntent.putExtra("circleCrop",true);
            startActivityForResult(cropIntent, CROP_PIC);
        }
        catch (ActivityNotFoundException anfr){
            Toast.makeText(this, "doesn't crop", Toast.LENGTH_SHORT).show();
        }
    }



    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.PNG, 100, stream);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ivImage.setImageBitmap(bm);
    }


    Boolean saveImageInDB(Uri selectedImageUri,ArrayList<String>phno,String From) {
        try {
            dbHelper = new DBHelper(this);
            dbHelper.open();
            if(selectedImageUri == null){
                Toast.makeText(getApplication(),"image is needed",Toast.LENGTH_SHORT).show();
            }else {
                InputStream iStream = getContentResolver().openInputStream(selectedImageUri);
                byte[] inputData = Utils.getBytes(iStream);
                desc = spec.getText().toString();
                for (String post : phno) {
                    dbHelper.insertPost(inputData, post, From, date.getText().toString(), desc);
                }
                dbHelper.close();
                return true;
            }

            return false;
        } catch (IOException ioe) {
            dbHelper.close();
            return false;
        }
    }

}
