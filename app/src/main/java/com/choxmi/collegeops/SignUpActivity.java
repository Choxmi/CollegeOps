package com.choxmi.collegeops;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.net.MalformedURLException;

/**
 * Created by Choxmi on 12/16/2017.
 */

public class SignUpActivity extends AppCompatActivity implements AsyncResponse{

    EditText userIdSignUp;
    Spinner typeSpinner,branch,grade;
    Button signupBtn;
    ProgressDialog progress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        userIdSignUp = (EditText)findViewById(R.id.userSignUpTxt);
        typeSpinner = (Spinner)findViewById(R.id.typeLst);
        branch = (Spinner)findViewById(R.id.branchSpin);
        grade = (Spinner)findViewById(R.id.gradeSpin);
        signupBtn = (Button)findViewById(R.id.signUpBtn);

        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position==1){
                    branch.setVisibility(View.VISIBLE);
                    grade.setVisibility(View.VISIBLE);
                }else{
                    branch.setVisibility(View.GONE);
                    grade.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String url = "http://choxcreations.000webhostapp.com/CollegeOps/Process.php?type=signup&user="+userIdSignUp.getText().toString()+"&user_type="+typeSpinner.getSelectedItem().toString();
                    if(typeSpinner.getSelectedItem().toString().equals("STU")){
                        url = url+"&grade="+grade.getSelectedItem().toString()+"&branch="+branch.getSelectedItem().toString();
                    }
                    Connector connector = new Connector(url,"");
                    connector.delegate = SignUpActivity.this;
                    progress = new ProgressDialog(SignUpActivity.this);
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
    }

    @Override
    public void processFinish(String response) {
        Toast.makeText(SignUpActivity.this,response,Toast.LENGTH_LONG).show();
        progress.dismiss();
        if(response.equals("200")){
            Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
            startActivity(intent);
        }
    }
}
