package com.choxmi.collegeops;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Choxmi on 12/15/2017.
 */

public class LoginActivity extends AppCompatActivity implements AsyncResponse{

    Spinner typeSpinner;
    Button login,signup;
    EditText userTxt;
    ProgressDialog progress;
    SharedPreferences sf;
    SharedPreferences.Editor sfe;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        sf = getSharedPreferences("vitam",MODE_PRIVATE);
        sfe = sf.edit();

        if(!sf.getString("id","").equals("")){
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }

        typeSpinner = (Spinner)findViewById(R.id.typeSpinner);
        login = (Button)findViewById(R.id.loginBtn);
        signup = (Button)findViewById(R.id.toSignup);
        userTxt = (EditText)findViewById(R.id.username_txt);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Connector connector = new Connector("http://choxcreations.000webhostapp.com/CollegeOps/Process.php?type=login&user_name="+userTxt.getText().toString(),"");
                    connector.delegate = LoginActivity.this;
                    progress = new ProgressDialog(LoginActivity.this);
                    progress.setTitle("Loading");
                    progress.setMessage("Wait while loading...");
                    progress.setCancelable(false);
                    progress.show();
                    connector.execute();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void processFinish(String response) {
        progress.dismiss();
        String userType="";
        String grade="";
        String branch="";
        int userId = 0;
        Log.e("RES",response);
        try {
            JSONArray ja = new JSONArray(response);
            JSONObject jo = ja.getJSONObject(0);
            userType = jo.getString("type");
            userId = jo.getInt("id");
            branch = jo.getString("branch");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(!userType.equals("")) {
            Toast.makeText(getApplicationContext(), userType, Toast.LENGTH_LONG).show();
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.putExtra("type", userType);
            intent.putExtra("name", userTxt.getText().toString());
            intent.putExtra("id", userId);
            intent.putExtra("grade", grade);
            intent.putExtra("branch", branch);

            sfe.putString("type", userType);
            sfe.putString("name", userTxt.getText().toString());
            sfe.putString("id", ""+userId);
            sfe.putString("grade", grade);
            sfe.putString("branch", branch);

            sfe.commit();

            startActivity(intent);
        }else{
            Toast.makeText(LoginActivity.this,"User Does not exist",Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {

    }
}
