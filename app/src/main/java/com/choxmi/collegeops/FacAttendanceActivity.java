package com.choxmi.collegeops;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Choxmi on 12/16/2017.
 */

public class FacAttendanceActivity extends AppCompatActivity implements AsyncResponse {

    RecyclerView attendanceRecycler;
    ProgressDialog progress;
    Spinner grade,subject;
    AttendanceRecyclerAdapter adapter;
    Button search,submit,picker;
    public static ArrayList<Attendance> students;
    String mnt,yr,date;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_attendance);

        attendanceRecycler = (RecyclerView)findViewById(R.id.studentRecycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        attendanceRecycler.setLayoutManager(linearLayoutManager);

        grade = (Spinner)findViewById(R.id.gradeSpinner);
        subject = (Spinner)findViewById(R.id.subjectSpinner);
        search = (Button)findViewById(R.id.searchAttendence);
        submit = (Button)findViewById(R.id.submitAttendance);
        picker = (Button)findViewById(R.id.datePickBtn);
        students = new ArrayList<>();

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Connector connector = new Connector("http://choxcreations.000webhostapp.com/CollegeOps/Process.php?type=getStudents&grade="+grade.getSelectedItem().toString(),"");
                    connector.delegate = FacAttendanceActivity.this;
                    progress = new ProgressDialog(FacAttendanceActivity.this);
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

        picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month+1;
                        mnt = ""+month;
                        date = ""+dayOfMonth;
                        yr = ""+year;
                        if(month<10){
                            mnt = "0"+mnt;
                        }if(dayOfMonth<10){
                            date = "0"+date;
                        }
                        picker.setText("" + year + "-" + mnt + "-" + date);
                    }
                };

                new DatePickerDialog(FacAttendanceActivity.this,listener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DATE)).show();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String currentDateandTime = sdf.format(new Date());
                String[] dateA = currentDateandTime.split("-");
                Toast.makeText(FacAttendanceActivity.this,date,Toast.LENGTH_SHORT).show();

                for(int j=0;j<students.size();j++){
                    try {
                        String url = "http://choxcreations.000webhostapp.com/CollegeOps/Process.php?type=markAtt&userId="+students.get(j).getId()+"&month="+mnt+"&year="+yr+"&date="+date+"&attendance="+(students.get(j).isMark()?"1":"0")+"&subject="+subject.getSelectedItem().toString()+"&sem="+grade.getSelectedItem().toString();
                        Connector connector = new Connector(url,"");
                        connector.delegate = FacAttendanceActivity.this;
                        connector.execute();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }

                onBackPressed();

            }
        });

    }

    @Override
    public void processFinish(String response) {
        Toast.makeText(FacAttendanceActivity.this,response,Toast.LENGTH_LONG).show();
        progress.dismiss();

        try {
            JSONArray ja = new JSONArray(response);
            for(int i=0;i<ja.length();i++){
                JSONObject jo = ja.getJSONObject(i);
                Attendance attendance = new Attendance();
                attendance.setName(jo.getString("user_name"));
                attendance.setId(jo.getString("id"));
                attendance.setMark(false);
                students.add(attendance);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter = new AttendanceRecyclerAdapter(FacAttendanceActivity.this);
        attendanceRecycler.setAdapter(adapter);
    }
}
