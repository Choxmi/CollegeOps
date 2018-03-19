package com.choxmi.collegeops;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by Choxmi on 12/16/2017.
 */

public class AdminFeedbackActivity extends AppCompatActivity implements AsyncResponse{

    ImageButton backBtn;
    Spinner feedbackSpinner;
    ProgressDialog progress;
    Button analyzeBtn,viewBtn;
    ArrayList<String> ids;
    HashMap<String,Integer> rateCount = new HashMap<String,Integer>();
    PieChart pieChart;

    ArrayList<String> listItems=new ArrayList<String>();
    ArrayAdapter<String> adapter;
    ListView list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_view);
        pieChart = (PieChart)findViewById(R.id.feedbackPie);
        feedbackSpinner = (Spinner)findViewById(R.id.feedbackSpinner);
        ids = new ArrayList<>();
        try {
            progress = new ProgressDialog(AdminFeedbackActivity.this);
            progress.setTitle("Loading");
            progress.setMessage("Wait while loading...");
            progress.setCancelable(false);
            progress.show();
            Connector connector = new Connector("http://choxcreations.000webhostapp.com/CollegeOps/Process.php?type=getEvent","");
            connector.delegate = AdminFeedbackActivity.this;
            connector.execute();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        analyzeBtn = (Button)findViewById(R.id.analyzeBtn);
        analyzeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    progress = new ProgressDialog(AdminFeedbackActivity.this);
                    progress.setTitle("Analyzing");
                    progress.setMessage("Wait while analyzing...");
                    progress.setCancelable(false);
                    progress.show();
                    String url = "http://choxcreations.000webhostapp.com/CollegeOps/Process.php?type=getEventById&id="+ids.get(feedbackSpinner.getSelectedItemPosition());
                    Connector connector = new Connector(url,"");
                    Log.e("URL",url);
                    connector.delegate = AdminFeedbackActivity.this;
                    connector.execute();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });

        viewBtn = (Button)findViewById(R.id.feedbackView);
        viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LayoutInflater inflater= LayoutInflater.from(AdminFeedbackActivity.this);
                View view=inflater.inflate(R.layout.feedback_tbl, null);
                list = (ListView)view.findViewById(R.id.feedbackList);

                adapter=new ArrayAdapter<String>(AdminFeedbackActivity.this,
                        android.R.layout.simple_list_item_1,
                        listItems);
                list.setAdapter(adapter);

                adapter.notifyDataSetChanged();

                AlertDialog.Builder builder = new AlertDialog.Builder(AdminFeedbackActivity.this);
                builder.setTitle("FeedBack");
                builder.setView(view);
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        backBtn = (ImageButton)findViewById(R.id.backBtnFeedback);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void processFinish(String response) throws JSONException {
        JSONArray ja = new JSONArray(response);
        if(ja.isNull(0)){
            Toast.makeText(AdminFeedbackActivity.this,"No feedback found",Toast.LENGTH_SHORT).show();
            progress.dismiss();
        }
        if(ja.toString().contains("feedback")){

            listItems.clear();

            List<PieEntry> entries=null;
            for(int i=0;i<ja.length();i++) {
                JSONObject jo = ja.getJSONObject(i);
                if(rateCount.get(jo.getString("rating")) == null){
                    rateCount.put(jo.getString("rating"),0);
                }else {
                    rateCount.put(jo.getString("rating"), (rateCount.get(jo.getString("rating"))) + 1);
                }

                if(!(jo.getString("feedback")).equals(null)){
                    listItems.add(jo.getString("user_name")+" : "+jo.getString("feedback"));
                }
            }
            viewBtn.setVisibility(View.VISIBLE);
            entries = new ArrayList<>();
            for(String key:rateCount.keySet()){
                Log.e(key,""+rateCount.get(key));
                entries.add(new PieEntry(rateCount.get(key), key));
            }

            PieDataSet set = new PieDataSet(entries, "Ratings");
            int[] colors = {Color.GRAY,Color.YELLOW,Color.BLUE,Color.RED,Color.LTGRAY,Color.CYAN,Color.BLACK,Color.GREEN,Color.MAGENTA,Color.DKGRAY};
            set.setColors(colors);
            PieData data = new PieData(set);
            Description desc = new Description();
            desc.setText("");
            pieChart.setDescription(desc);
            pieChart.setData(data);
            pieChart.invalidate();

            progress.dismiss();
        }else{
            ArrayList<String> events = new ArrayList<>();
            for(int i=0;i<ja.length();i++) {
                JSONObject jo = ja.getJSONObject(i);
                events.add(jo.getString("eventName"));
                ids.add(jo.getString("eventId"));
            }
            ArrayAdapter<String> adapter =
                    new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item, events);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            feedbackSpinner.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            feedbackSpinner.setSelection(0);
            progress.dismiss();
        }
    }
}
