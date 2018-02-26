package com.choxmi.collegeops;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.facebook.drawee.view.SimpleDraweeView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Choxmi on 1/28/2018.
 */

public class ViewPosterActivity extends AppCompatActivity {
    SimpleDraweeView poster;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.viewer_poster);
        Bitmap bmp = null;

        poster = (SimpleDraweeView) findViewById(R.id.eventPoster);
        poster.setImageURI(getIntent().getStringExtra("uri"));
    }
}
