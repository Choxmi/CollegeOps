package com.choxmi.collegeops;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.ArrayList;

/**
 * Created by Choxmi on 12/16/2017.
 */

public class StuRatingActivity extends AppCompatActivity implements AsyncResponse{

    ViewPager mPager;
    EventSlideAdapter mPagerAdapter;
    ProgressDialog progress;
    public static ArrayList<Event> events;
    Button submitFeed;
    int user;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rate_events);

        user = getIntent().getIntExtra("id",0);

        submitFeed = (Button)findViewById(R.id.submitFeedback);
        submitFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i=0;i<events.size();i++) {
                    try {
                        Connector connector = new Connector("http://choxcreations.000webhostapp.com/CollegeOps/Process.php?type=eventFeedback&userId="+user+"&rating="+events.get(i).getRating()+"&feedback="+events.get(i).getFeedback()+"&eventId="+events.get(i).getEventId(),"");
                        connector.delegate = StuRatingActivity.this;
                        connector.execute();
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                }
                Toast.makeText(StuRatingActivity.this,"Feedback Submitted",Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        });

        events = new ArrayList<>();
        mPager = (ViewPager) findViewById(R.id.eventspager);
        try {
            Connector connector = new Connector("http://choxcreations.000webhostapp.com/CollegeOps/Process.php?type=getEvent","");
            connector.delegate = StuRatingActivity.this;
            progress = new ProgressDialog(StuRatingActivity.this);
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
                Event event = new Event();
                event.setEventName(jo.getString("eventName"));
                event.setDate(jo.getString("date"));
                event.setEventId(jo.getString("eventId"));
                event.setPrivacy(jo.getString("privacy"));
                if(!jo.getString("posterUrl").equals("")){
                    event.setImgUri(jo.getString("posterUrl"));
                }
                events.add(event);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mPagerAdapter = new EventSlideAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
    }

    private class EventSlideAdapter extends FragmentStatePagerAdapter{

        public EventSlideAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return new FragmentEvents(position);
        }

        @Override
        public int getCount() {
            return StuRatingActivity.events.size();
        }
    }
}
