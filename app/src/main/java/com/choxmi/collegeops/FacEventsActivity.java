package com.choxmi.collegeops;

import android.Manifest;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

/**
 * Created by Choxmi on 12/16/2017.
 */

public class FacEventsActivity extends AppCompatActivity implements AsyncResponse{

    Spinner grade,sub;
    EditText name,desc;
    Button submit,attach,picker;
    CheckBox privacy;
    TextView uriTxt,dateTxt;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_attachments);

        grantPermission();

        grade = (Spinner)findViewById(R.id.gradeSpin);
        sub = (Spinner)findViewById(R.id.subjectSpin);
        name = (EditText)findViewById(R.id.eventName);
        desc = (EditText)findViewById(R.id.eventDesc);
        submit = (Button)findViewById(R.id.createEvent);
        picker = (Button)findViewById(R.id.datePickerBtn);
        attach = (Button)findViewById(R.id.attachBtn);
        privacy = (CheckBox)findViewById(R.id.privacyChk);
        uriTxt = (TextView)findViewById(R.id.uriTxt);
        dateTxt = (TextView)findViewById(R.id.dateTxt);

        picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog.OnDateSetListener listener = new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        month = month+1;
                        String mnt = ""+month;
                        String date = ""+dayOfMonth;
                        if(month<10){
                            mnt = "0"+mnt;
                        }if(dayOfMonth<10){
                            date = "0"+date;
                        }
                        dateTxt.setText("" + year + "-" + mnt + "-" + date);
                    }
                };

                new DatePickerDialog(FacEventsActivity.this,listener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DATE)).show();
            }
        });

        privacy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    grade.setVisibility(View.VISIBLE);
                    sub.setVisibility(View.VISIBLE);
                }else{
                    grade.setVisibility(View.GONE);
                    sub.setVisibility(View.GONE);
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    String imgUrl = new Connector.MediaUploader().execute(uriTxt.getText().toString()).get();
                    String rest = "";
                    if(privacy.isChecked()){
                        rest = "&grade="+grade.getSelectedItem().toString()+"&subject="+sub.getSelectedItem().toString();
                    }
                    CallAPI callAPI = new CallAPI();
                    Log.e("Res",callAPI.execute().get());
                    Connector connector = new Connector("http://choxcreations.000webhostapp.com/CollegeOps/Process.php?type=addEvent&eventName="+name.getText().toString().replaceAll(" ", "_")+"&date="+dateTxt.getText().toString().toString()+"&privacy="+((privacy.isChecked()?"PVT":"PUB"))+rest,imgUrl);
                    connector.delegate = FacEventsActivity.this;
                    connector.execute();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        });

        attach.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });
    }

    @Override
    public void processFinish(String response) {
        Toast.makeText(FacEventsActivity.this,response,Toast.LENGTH_SHORT).show();
        onBackPressed();
    }

    public void pickImage(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,1);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = 0;
        idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        Log.e("Cursor",cursor.getString(idx));
        return cursor.getString(idx);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri imgUri = (Uri)data.getData();
        try {
            Bitmap original = MediaStore.Images.Media.getBitmap(FacEventsActivity.this.getContentResolver(),imgUri);
            imgUri = getImageUri(FacEventsActivity.this, original);
        } catch (IOException e) {
            e.printStackTrace();
        }
        uriTxt.setText(getRealPathFromURI(imgUri));
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.WEBP, 1, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    private void grantPermission(){
        if (ContextCompat.checkSelfPermission(FacEventsActivity.this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(FacEventsActivity.this,
                    Manifest.permission.READ_CONTACTS)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(FacEventsActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE},1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
    }

    public class CallAPI extends AsyncTask<String, String, String> {
        HttpURLConnection urlConnection;
        public CallAPI(){
            //set context variables if required
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected String doInBackground(String... params) {

            String urlString = "https://onesignal.com/api/v1/notifications"; // URL to call

            String data = "{\"app_id\": \"8fc83684-0b29-44e7-beee-6e17039e7733\", \"contents\": {\"en\": \"New Event : "+name.getText().toString().replaceAll(" ", "_")+"\"},\"included_segments\": [\"All\"]}";

            OutputStream out = null;
            try {

                URL url = new URL(urlString);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Authorization", "Basic NjYyYzIxYjUtYmJlZC00MjRjLTkyMTUtOTFlNWJjNjVhMWZi");
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Content-Type", "application/json; charset=utf-8");


                out = new BufferedOutputStream(urlConnection.getOutputStream());

                BufferedWriter writer = new BufferedWriter (new OutputStreamWriter(out, "UTF-8"));

                writer.write(data);

                writer.flush();

                writer.close();

                out.close();

                urlConnection.connect();
                Log.e("Msg","Sent");

            } catch (Exception e) {

                System.out.println(e.getMessage());



            }

            try {
                return urlConnection.getResponseMessage();
            } catch (IOException e) {
                e.printStackTrace();
                return "Exception";
            }
        }
    }
}
