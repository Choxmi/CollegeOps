package com.choxmi.collegeops;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutionException;

/**
 * Created by Choxmi on 12/21/2017.
 */

public class Connector extends AsyncTask<String, String, String> {
    public AsyncResponse delegate = null;

    String result;
    URL url;
    String imgUrl = "";
    public Connector(String urlString,String imgString) throws MalformedURLException {
        this.result = "";
        if(!imgString.equals("")){
            urlString = urlString+"&img="+imgString;
        }
        this.url = new URL(urlString);
    }

    @Override
    protected String doInBackground(String... params) {
        BufferedReader bufferedReader = null;
        URLConnection dc = null;
        String imgOutUrl="";
        try {
            dc = url.openConnection();

            Log.e("url",url.toString());
            dc.setConnectTimeout(15000);
            dc.setReadTimeout(15000);

            bufferedReader = new BufferedReader(new InputStreamReader(dc.getInputStream()));
            result = bufferedReader.readLine();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(String s) {
        try {
            delegate.processFinish(s);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static class MediaUploader extends AsyncTask<String, String, String> {
        public MediaUploader(){
            Log.e("Uploader","Started");
        }

        @Override
        protected String doInBackground(String... params) {
            int serverResponseCode=0;
            String link = "";
            Log.e("Uploader","Executing");
            try {

                HttpURLConnection conn = null;
                DataOutputStream dos = null;
                String lineEnd = "\r\n";
                String twoHyphens = "--";
                String boundary = "*****";
                int bytesRead, bytesAvailable, bufferSize;
                byte[] buffer;
                int maxBufferSize = 1024 * 1024;
                Log.e("Param",params[0]);
                File sourceFile = new File(params[0]);
                Log.e("Param",sourceFile.toString());

                if (sourceFile.isFile()) {

                    try {
                        FileInputStream fileInputStream = new FileInputStream(
                                sourceFile);
                        URL url = new URL("https://choxcreations.000webhostapp.com/CollegeOps/AssetUpload.php");

                        conn = (HttpURLConnection) url.openConnection();
                        conn.setDoInput(true);
                        conn.setDoOutput(true);
                        conn.setUseCaches(true);
                        conn.setRequestMethod("POST");
                        conn.setRequestProperty("Connection", "Keep-Alive");
                        conn.setRequestProperty("ENCTYPE",
                                "multipart/form-data");
                        conn.setRequestProperty("Content-Type",
                                "multipart/form-data;boundary=" + boundary);
                        conn.setRequestProperty("fileName", params[0]);

                        dos = new DataOutputStream(conn.getOutputStream());

                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                        dos.writeBytes("Content-Disposition: form-data; name=\"content\";filename=\""
                                + params[0] + "\"" + lineEnd);
                        dos.writeBytes(lineEnd);

                        bytesAvailable = fileInputStream.available();

                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        buffer = new byte[bufferSize];
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                        while (bytesRead > 0) {

                            dos.write(buffer, 0, bufferSize);
                            bytesAvailable = fileInputStream.available();
                            bufferSize = Math
                                    .min(bytesAvailable, maxBufferSize);
                            bytesRead = fileInputStream.read(buffer, 0,
                                    bufferSize);
                        }
                        dos.writeBytes(lineEnd);
                        dos.writeBytes(twoHyphens + boundary + twoHyphens
                                + lineEnd);
                        String line;
                        StringBuilder builder = new StringBuilder();
                        try {
                            InputStream inputStream = new BufferedInputStream(conn.getInputStream());
                            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                            while ((line = reader.readLine()) != null) {
                                builder.append(line).append("\n");
                            }
                            reader.close();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        link = builder.toString();

                        fileInputStream.close();
                        dos.flush();
                        dos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else{
                    Log.e("Source","Not a file");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return link;
        }
    }
}
