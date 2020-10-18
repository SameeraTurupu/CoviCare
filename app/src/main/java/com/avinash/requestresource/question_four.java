package com.avinash.requestresource;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.FileNotFoundException;

public class question_four extends AppCompatActivity{
        TextView textTargetUri;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_question_4);
            Button buttonLoadImage = (Button)findViewById(R.id.uploadxray);
            textTargetUri = (TextView)findViewById(R.id.xrayresults);


            buttonLoadImage.setOnClickListener(new Button.OnClickListener(){

                @Override
                public void onClick(View arg0) {
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 0);
                }});
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            // TODO Auto-generated method stub
            super.onActivityResult(requestCode, resultCode, data);

            if (resultCode == RESULT_OK){
                Uri targetUri = data.getData();
                textTargetUri.setText(targetUri.toString());
                Intent bed = new Intent(this, bed_selection.class);
                startActivity(bed);
            }
            else {
                textTargetUri.setText("couldn't upload!");
            }
        }


}
