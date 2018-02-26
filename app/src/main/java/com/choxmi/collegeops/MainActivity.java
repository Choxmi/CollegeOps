package com.choxmi.collegeops;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    String USER_TYPE;
    Button btn1,btn2,btn3,btn4,exit;
    Intent carrier,current;
    TextView nameTxt,nameDec, idTxt, idDesc, branch, branchTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        current = getIntent();

        btn1 = (Button)findViewById(R.id.btn1);
        btn2 = (Button)findViewById(R.id.btn2);
        btn3 = (Button)findViewById(R.id.btn3);
        btn4 = (Button)findViewById(R.id.btn4);
        exit = (Button)findViewById(R.id.exitBtn);

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                System.exit(0);
            }
        });

        nameTxt = (TextView)findViewById(R.id.nameTxt);
        nameDec = (TextView)findViewById(R.id.nameDesc);
        idTxt = (TextView)findViewById(R.id.idTxt);
        idDesc = (TextView)findViewById(R.id.idDesc);
        branch = (TextView)findViewById(R.id.branchTxt);
        branchTxt = (TextView)findViewById(R.id.branchDesc);

        USER_TYPE = getIntent().getStringExtra("type");

        Log.e("Type",""+USER_TYPE);

        if(USER_TYPE.equals("STU")) {
            btn1.setText("Show Attendance");
            btn2.setText("Show Events");
            btn3.setVisibility(View.GONE);
            btn4.setVisibility(View.GONE);

            nameTxt.setText(current.getStringExtra("name"));
            branch.setText(current.getStringExtra("branch"));
            idTxt.setText("STU_"+current.getIntExtra("id",0));

            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    carrier = new Intent(MainActivity.this, StuAttendanceActivity.class);
                    carrier.putExtra("id",getIntent().getIntExtra("id",0));
                    startActivity(carrier);
                }
            });
            btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    carrier = new Intent(MainActivity.this, StuRatingActivity.class);
                    carrier.putExtra("id",getIntent().getIntExtra("id",0));
                    startActivity(carrier);
                }
            });
        }else if(USER_TYPE.equals("FAC")) {
            branchTxt.setVisibility(View.GONE);
            btn1.setText("Add Attendance");
            btn2.setText("Add Events");
            btn3.setVisibility(View.GONE);
            btn4.setVisibility(View.GONE);

            nameTxt.setText(current.getStringExtra("name"));
            idTxt.setText("FAC_"+current.getIntExtra("id",0));

            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    carrier = new Intent(MainActivity.this, FacAttendanceActivity.class);
                    startActivity(carrier);
                }
            });
            btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    carrier = new Intent(MainActivity.this, FacEventsActivity.class);
                    startActivity(carrier);
                }
            });
        }else {
            btn1.setText("Add Student/Faculty");
            btn2.setText("Remove Student/Faculty");
            btn3.setText("Add events");
            btn4.setText("Feedback");

            idDesc.setVisibility(View.INVISIBLE);
            idTxt.setVisibility(View.INVISIBLE);
            branchTxt.setVisibility(View.GONE);
            nameTxt.setVisibility(View.VISIBLE);
            nameDec.setVisibility(View.VISIBLE);
            nameTxt.setText(current.getStringExtra("name"));
            //idTxt.setText(current.getStringExtra("id"));

            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this,SignUpActivity.class);
                    startActivity(intent);
                }
            });
            btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    carrier = new Intent(MainActivity.this, AdminRemoveActivity.class);
                    startActivity(carrier);
                }
            });
            btn3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    carrier = new Intent(MainActivity.this, FacEventsActivity.class);
                    startActivity(carrier);
                }
            });
            btn4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    carrier = new Intent(MainActivity.this, AdminFeedbackActivity.class);
                    startActivity(carrier);
                }
            });
        }
    }

    @Override
    public void onBackPressed() {

    }
}
