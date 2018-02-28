package androids.newapp;

/**
 * Created by Lenovo on 07-Feb-18.
 */

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import de.hdodenhof.circleimageview.CircleImageView;


public class SignUpActivity extends Activity implements OnClickListener {
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1, CROP_PIC = 2;
    private Button signUp;
    private EditText pNo, psw;
    private EditText text,address,city;
    private Spinner field;
    private CircleImageView img;
    String name,phoneNo,password,fieldOfWork,Address,City;
    private AwesomeValidation awesomeValidation;
    private String userChoosenTask;
    Uri selectImageUri = null;
    DBHelper dbHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);
        setContentView(R.layout.activity_signup);
        img = (CircleImageView) findViewById(R.id.ImageView3);
        pNo = (EditText)findViewById(R.id.phone);
        psw = (EditText)findViewById(R.id.password);
        text = (EditText) findViewById(R.id.input_name);
        address = (EditText) findViewById(R.id.address);
        city = (EditText)findViewById(R.id.city);
        field = (Spinner) findViewById(R.id.spinner);
        signUp = (Button) findViewById(R.id.btn_signup);

        awesomeValidation.addValidation(this, R.id.input_name, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.nameerror);
        awesomeValidation.addValidation(this, R.id.password, "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\S+$).{6,}$" , R.string.passworderror);
        awesomeValidation.addValidation(this, R.id.phone, "^[2-9]{2}[0-9]{8}$", R.string.mobileerror);
        awesomeValidation.addValidation(this, R.id.address, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.locationerror);
        awesomeValidation.addValidation(this, R.id.city, "^[A-Za-z\\s]{1,}[\\.]{0,1}[A-Za-z\\s]{0,}$", R.string.cityerror);
        //awesomeValidation.addValidation(this,field,"^Choose Your Skill$","Please Choose your Skill");
        dbHelper = new DBHelper(this);

        signUp.setOnClickListener(this);
        img.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAImage();
            }
        });
    }
    private void submitForm(View v) {
        if (awesomeValidation.validate()) {
            if(field.getSelectedItem().toString().equals("Choose a Skill")){
                Toast.makeText(getApplicationContext(),"Please Choose your Skill!!!",Toast.LENGTH_SHORT).show();
            }else {
                if (saveImageInDB(selectImageUri)) {
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
    @Override
    public void onClick(View v) {
        if(v==signUp) {
            submitForm(v);
        }
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

    private void selectAImage() {
        final CharSequence[] items = {  "Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result=Utility.checkPermission(SignUpActivity.this);
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
        // super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                selectImageUri = data.getData();
                onSelectFromGalleryResult(data);
                performCrop(data.getData());
            }
            else if(requestCode == CROP_PIC){
                Bundle bundle = data.getExtras();
                Bitmap bitmap = bundle.getParcelable("data");
                img.setImageBitmap(bitmap);
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
            Toast.makeText(this, "do crop", Toast.LENGTH_SHORT).show();
            startActivityForResult(cropIntent, CROP_PIC);
        }
        catch (ActivityNotFoundException anfr){
            Toast.makeText(this, "doesn't crop", Toast.LENGTH_SHORT).show();
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        img.setImageBitmap(thumbnail);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        img.setImageBitmap(bm);
    }
    Boolean saveImageInDB(Uri selectedImageUri) {
        dbHelper.open();
        try {
            byte[] inputData;
            if(selectedImageUri == null) {
                selectedImageUri=Uri.parse("android.resource://androids.newapp/drawable/empower");
            }
            InputStream iStream = getContentResolver().openInputStream(selectedImageUri);
            inputData = Utils.getBytes(iStream);
            name = text.getText().toString();
            phoneNo = pNo.getText().toString();
            password = psw.getText().toString();
            Address = address.getText().toString();
            City = city.getText().toString();
            fieldOfWork = field.getSelectedItem().toString();
            if (dbHelper.insertImage(inputData, name, phoneNo, password, fieldOfWork, Address.toLowerCase(), City.toLowerCase(), 1.0f)) {
                    dbHelper.close();
                    return true;
                }

            return false;
        } catch (IOException ioe) {
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
