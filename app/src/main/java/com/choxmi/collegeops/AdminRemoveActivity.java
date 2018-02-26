package com.choxmi.collegeops;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Choxmi on 12/16/2017.
 */

public class AdminRemoveActivity extends AppCompatActivity implements AsyncResponse{
    ProgressDialog progress;
    Spinner peopleSpinner;
    List<Users> stu,fac;
    List<String> stuN,facN;
    ArrayAdapter<String> adapter;
    RadioButton stb,fab;
    RadioGroup radioGroup;
    String selectedID;
    Button remove;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.remove_entities);

        peopleSpinner = (Spinner)findViewById(R.id.peopleSpin);
        stu = new ArrayList<>();
        fac = new ArrayList<>();
        stuN = new ArrayList<>();
        facN = new ArrayList<>();

        stb = (RadioButton)findViewById(R.id.stuRadio);
        fab = (RadioButton)findViewById(R.id.facRadio);

        radioGroup = (RadioGroup)findViewById(R.id.catRadioGroup);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId==R.id.stuRadio){
                    adapter = new ArrayAdapter<String>(AdminRemoveActivity.this,android.R.layout.simple_spinner_dropdown_item,stuN);
                    adapter.notifyDataSetChanged();
                    peopleSpinner.setAdapter(adapter);
                }if(checkedId==R.id.facRadio){
                    adapter = new ArrayAdapter<String>(AdminRemoveActivity.this,android.R.layout.simple_spinner_dropdown_item,facN);
                    adapter.notifyDataSetChanged();
                    peopleSpinner.setAdapter(adapter);
                }
            }
        });

        remove = (Button)findViewById(R.id.removeBtn);
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Connector connector1 = new Connector("http://choxcreations.000webhostapp.com/CollegeOps/Process.php?type=removeAtt&id="+selectedID,"");
                    connector1.delegate = AdminRemoveActivity.this;
                    connector1.execute();
                    Connector connector2 = new Connector("http://choxcreations.000webhostapp.com/CollegeOps/Process.php?type=removeEvents&id="+selectedID,"");
                    connector2.delegate = AdminRemoveActivity.this;
                    connector2.execute();
                    Connector connector3 = new Connector("http://choxcreations.000webhostapp.com/CollegeOps/Process.php?type=remove&id="+selectedID,"");
                    connector3.delegate = AdminRemoveActivity.this;
                    connector3.execute();
                    Toast.makeText(AdminRemoveActivity.this,"User Removed",Toast.LENGTH_SHORT).show();
                    onBackPressed();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });

        peopleSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(stb.isChecked()){
                    selectedID = stu.get(position).getId();
                }if(fab.isChecked()){
                    selectedID = fac.get(position).getId();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        try {
            Connector connector = new Connector("http://choxcreations.000webhostapp.com/CollegeOps/Process.php?type=getUsers","");
            connector.delegate = AdminRemoveActivity.this;
            progress = new ProgressDialog(AdminRemoveActivity.this);
            progress.setTitle("Loading");
            progress.setMessage("Wait while loading...");
            progress.setCancelable(false);
            progress.show();
            connector.execute();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void processFinish(String response) throws JSONException {
        progress.dismiss();
        try {
            JSONArray ja = new JSONArray(response);
            for(int i=0;i<ja.length();i++){
                JSONObject jo = ja.getJSONObject(i);
                Users user = new Users();
                user.setId(jo.getString("id"));
                user.setName(jo.getString("user_name"));
                user.setType(jo.getString("type"));
                if(jo.getString("type").equals("STU")){
                    stu.add(user);
                    stuN.add(jo.getString("user_name"));
                }if(jo.getString("type").equals("FAC")){
                    fac.add(user);
                    facN.add(jo.getString("user_name"));
                }
            }
            adapter = new ArrayAdapter<String>(AdminRemoveActivity.this,android.R.layout.simple_spinner_dropdown_item,stuN);
            adapter.notifyDataSetChanged();
            peopleSpinner.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
