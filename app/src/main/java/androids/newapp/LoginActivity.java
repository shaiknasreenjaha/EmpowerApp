package androids.newapp;

/**
 * Created by Lenovo on 07-Feb-18.
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;



public class LoginActivity extends Activity {
    public SessionManager session;
    Button login;
    EditText pNo, psw;
    String PhoneNo,Password;
    DBHelper dbHelper;


    private void submitForm() {
        //session.createLoginSession(pNo.getText().toString(), psw.getText().toString());
        //session = new SessionManager(getApplicationContext());

            PhoneNo = pNo.getText().toString();
            Password = psw.getText().toString();
            dbHelper = new DBHelper(this);
            dbHelper.open();
            String originalPassword = dbHelper.provideLogin(PhoneNo);
            dbHelper.close();

            if(originalPassword.equals(Password)) {
                session.createLoginSession(PhoneNo, Password);
                session = new SessionManager(getApplicationContext());
                Intent intent;
                if(IntentData.intentClass == 1) {
                    intent = new Intent(LoginActivity.this, Workers.class);
                    startActivity(intent);
                }
                else if(IntentData.intentClass == 2){
                    intent = new Intent(LoginActivity.this, MapsActivity.class);
                    startActivity(intent);
                }
                else if(IntentData.intentClass == 3){
                    intent = new Intent(LoginActivity.this, Home.class);
                    startActivity(intent);
                }
                else {
                    intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                }
                finish();
            }else{
                Toast.makeText(getApplicationContext(),"Invalid PhoneNumber / Password",Toast.LENGTH_SHORT).show();
            }

    }


    @Override
    public void onBackPressed() {

        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        session = new SessionManager(getApplicationContext());
        pNo = (EditText) findViewById(R.id.input_phone);
        psw = (EditText) findViewById(R.id.input_password);
        login = (Button) findViewById(R.id.btn_login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v==login) {
                    submitForm();
                }
            }

        });
    }

    public void signUp(View v) {
        Intent intent = new Intent(this,SignUpActivity.class);
        startActivity(intent);
    }
}

