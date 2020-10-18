package com.avinash.requestresource;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

//import com.squareup.okhttp.MediaType;
//import com.squareup.okhttp.OkHttpClient;
//import com.squareup.okhttp.Request;
//import com.squareup.okhttp.RequestBody;
//import com.squareup.okhttp.Response;

import okhttp3.OkHttpClient;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Request;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import okhttp3.MultipartBody;

//import okhttp3.RequestBody;

public class question_four extends AppCompatActivity{
        TextView textTargetUri;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_question_4);
            Button buttonLoadImage = (Button)findViewById(R.id.uploadxray);
            textTargetUri = (TextView)findViewById(R.id.xrayresults);
            ConstraintLayout fl = (ConstraintLayout) findViewById(R.id.question_four);


            buttonLoadImage.setOnClickListener(new Button.OnClickListener(){

                @Override
                public void onClick(View arg0) {
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 0);
                }});

        }


        private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            // TODO Auto-generated method stub
            super.onActivityResult(requestCode, resultCode, data);

            if (resultCode == RESULT_OK) {
                Uri targetUri = data.getData();
            }
            else {
                textTargetUri.setText("couldn't upload!");

//                textTargetUri.setText(targetUri.toString());


                String filePath = getPath(targetUri);
                SharedPreferences sharedPreferences = getSharedPreferences("8ResQ",0);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("filePath",filePath);
                editor.apply();
                File file = new File(filePath);
                String file_extn = filePath.substring(filePath.lastIndexOf(".") + 1);
//                image_name_tv.setText(filePath);

                try {
                    if (file_extn.equals("img") || file_extn.equals("jpg") || file_extn.equals("jpeg") || file_extn.equals("gif") || file_extn.equals("png")) {


                        try {
                            //                    response = okHttpClient.newCall(request).execute();
                            Thread thread = new Thread(new Runnable() {

                                @Override
                                public void run() {
                                    try {
                                        String filePath = getSharedPreferences("8ResQ",0).getString("filePath","/").toString();
                                        File file = new File(filePath);
                                        String file_extn = filePath.substring(filePath.lastIndexOf(".") + 1);
                                        OkHttpClient client = new OkHttpClient().newBuilder()
                                                .build();
                                        MediaType mediaType = MediaType.parse("application/octet-stream");
                                        RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                                                .addFormDataPart("", filePath,
                                                        okhttp3.RequestBody.create(okhttp3.MediaType.parse("application/octet-stream"),
                                                                new File(filePath)))
                                                .build();
                                        Request request = new Request.Builder()
                                                .url("https://southcentralus.api.cognitive.microsoft.com/customvision/v3.0/Prediction/704514c4-5d04-47da-86af-e74f7997ef47/classify/iterations/covid-xray/image")
                                                .method("POST", body)
                                                .addHeader("Prediction-Key", "9bd812752ee94f75b6bda4ac8685a5a7")
                                                .addHeader("Content-Type", "application/octet-stream")
                                                .build();
                                        Response response = client.newCall(request).execute();
                                        SharedPreferences sharedPreferences1 = getSharedPreferences("8ResQ",0);
                                        SharedPreferences.Editor editor = sharedPreferences1.edit();
                                        JSONObject json = new JSONObject(response.body().string());
                                        JSONObject predictionValue = (JSONObject) json.getJSONArray("predictions").get(0);
                                        Double probability = predictionValue.getDouble("probability");
                                        String tag = predictionValue.getString("tagName");
                                        if(tag.equals("negative") && probability-0.400000 > 0.000000){
                                              editor.putBoolean("isCovidActive",false);
                                            TextView t  = (TextView) findViewById(R.id.xrayresults);
                                            textTargetUri.setText("Your X-ray seems to be fine.");
//                                            textTargetUri.setText("Your X-ray seem to be fine.");
                                        }else{
//                                            textTargetUri.setText("Please swipe to continue to next step.");
                                            TextView t  = (TextView) findViewById(R.id.xrayresults);
                                            textTargetUri.setText("Your X-ray seems to be not fine. swipe left to continue");
                                            editor.putBoolean("isCovidActive",true);
                                        }
                                        editor.apply();
                                        Intent intent = new Intent(getApplicationContext(), popup_activity.class);
                                        startActivity(intent);

                                        finish();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                            thread.start();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        //NOT IN REQUIRED FORMAT
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        String imagePath = cursor.getString(column_index);

        return cursor.getString(column_index);
    }


}
