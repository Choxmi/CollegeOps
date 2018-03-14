package com.choxmi.collegeops;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import java.net.MalformedURLException;

/**
 * Created by Choxmi on 12/22/2017.
 */

public class FragmentEvents extends Fragment {

    RatingBar ratingBar;
    int position;
    Button addFeedback,viewPoster;
    EditText feedbackTxt;
    TextView name,date,privacy;
    Context context;
    int user;

    public FragmentEvents(){
    }

    public FragmentEvents(int position, Context context,int user){
        this.position = position;
        this.context = context;
        this.user = user;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_feedback, container, false);

        name = (TextView)rootView.findViewById(R.id.event_name_display);
        name.setText(StuRatingActivity.events.get(position).getEventName());
        date = (TextView)rootView.findViewById(R.id.event_date_display);
        date.setText(StuRatingActivity.events.get(position).getDate());
        privacy = (TextView)rootView.findViewById(R.id.privacy_display);
        privacy.setText(StuRatingActivity.events.get(position).getPrivacy());
        viewPoster = (Button)rootView.findViewById(R.id.viewPosterBtn);

        if(StuRatingActivity.events.get(position).getImgUri()!=null) {
            if (!StuRatingActivity.events.get(position).getImgUri().equals("")) {
                viewPoster.setVisibility(View.VISIBLE);
                viewPoster.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), ViewPosterActivity.class);
                        intent.putExtra("uri", StuRatingActivity.events.get(position).getImgUri());
                        startActivity(intent);
                    }
                });
            }
        }

        ratingBar = (RatingBar)rootView.findViewById(R.id.ratingBar);
        ratingBar.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float touchPositionX = event.getX();
                float width = ratingBar.getWidth();
                float starsf = (touchPositionX / width) * 10.0f;
                int stars = (int)starsf + 1;
                if(stars>10){
                    stars = 10;
                }
                ratingBar.setRating(stars);
                StuRatingActivity.events.get(position).setRating(stars);
                return false;
            }
        });

        feedbackTxt = (EditText)rootView.findViewById(R.id.feedbackTxt);
        addFeedback = (Button)rootView.findViewById(R.id.add_feedback);

        addFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StuRatingActivity.events.get(position).setFeedback(feedbackTxt.getText().toString());
                try {
                    Connector connector = new Connector("http://choxcreations.000webhostapp.com/CollegeOps/Process.php?type=eventFeedback&userId="+user+"&rating="+StuRatingActivity.events.get(position).getRating()+"&feedback="+StuRatingActivity.events.get(position).getFeedback().replaceAll(" ","_")+"&eventId="+StuRatingActivity.events.get(StuRatingActivity.mPager.getCurrentItem()).getEventId(),"");
                    connector.delegate = (AsyncResponse) context;
                    connector.execute();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        });
        return rootView;
    }
}
