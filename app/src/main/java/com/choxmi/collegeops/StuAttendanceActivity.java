package com.choxmi.collegeops;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Choxmi on 12/16/2017.
 */

public class StuAttendanceActivity extends AppCompatActivity implements AsyncResponse {

    PieChart pieChart;
    Spinner mnth,year;
    Button getData;
    ImageButton back;
    ProgressDialog progress;
    int user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_attendence);

        pieChart = (PieChart)findViewById(R.id.attendencePie);
        mnth = (Spinner)findViewById(R.id.attMnth);
        year = (Spinner)findViewById(R.id.attYr);
        getData = (Button)findViewById(R.id.getAtt);
        back = (ImageButton) findViewById(R.id.backBtn1);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        user = getIntent().getIntExtra("id",0);

        getData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String url = "http://choxcreations.000webhostapp.com/CollegeOps/Process.php?type=getAtt&userId="+user+"&sem="+year.getSelectedItem().toString();
                    Log.e("URL",url);
                    Connector connector = new Connector(url,"");
                    connector.delegate = StuAttendanceActivity.this;
                    progress = new ProgressDialog(StuAttendanceActivity.this);
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
        List<PieEntry> entries=null;
        progress.dismiss();
        try {
            float present=0;
            float abs=0;
            JSONArray ja = new JSONArray(response);
            JSONObject jo = ja.getJSONObject(0);
            present = Float.valueOf(jo.getString("att"));
            Toast.makeText(StuAttendanceActivity.this,""+present,Toast.LENGTH_LONG).show();
            abs= 360-present;
            entries = new ArrayList<>();
            entries.add(new PieEntry((present/360)*100, "Present"));
            entries.add(new PieEntry((abs/360)*100, "Absent"));
        }catch (Exception e){
            e.printStackTrace();
        }

        PieDataSet set = new PieDataSet(entries, "Attendance");
        int[] colors = {Color.BLUE,Color.RED};
        set.setColors(colors);
        PieData data = new PieData(set);
        pieChart.setData(data);
        pieChart.invalidate();
    }
}
