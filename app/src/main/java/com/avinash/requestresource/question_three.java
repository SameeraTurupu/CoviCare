package com.avinash.requestresource;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class question_three extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_3);
        final RadioButton yes = (RadioButton) findViewById(R.id.yes3);
        final RadioButton no = (RadioButton) findViewById(R.id.no3);
        final Button reserveBed = (Button) findViewById(R.id.reserveBed);
        reserveBed.setVisibility(View.INVISIBLE);
        final TextView mini_exit = (TextView) findViewById(R.id.mini_exit);
        mini_exit.setVisibility(View.INVISIBLE);
        final SharedPreferences pref = getApplicationContext().getSharedPreferences("8ResQ",0);
        final SharedPreferences.Editor editor = pref.edit();
        final boolean[] flag = {false};


        yes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (buttonView.getId() == R.id.yes3) {
                        no.setChecked(false);
                        editor.putInt("count", pref.getInt("count",0) + 1);
                        editor.apply();
                        reserveBed.setVisibility(View.VISIBLE);
                        mini_exit.setVisibility(View.INVISIBLE);
                        flag[0] = true;
                    }
                    if (buttonView.getId() == R.id.no3) {
                        yes.setChecked(false);
                    }
                }

            }
        });
        no.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (buttonView.getId() == R.id.yes3) {
                        no.setChecked(false);
                    }
                    if (buttonView.getId() == R.id.no3) {
                        yes.setChecked(false);
                        if(flag[0] == true){
                            editor.putInt("count", pref.getInt("count",0) - 1);
                        }
                        else{
                            editor.putInt("count", pref.getInt("count",0));
                        }
                        editor.apply();
                        flag[0] = false;
                        if(pref.getInt("count",0) <= 1){
                            mini_exit.setText("Sounds like you are feeling ok. Stay Safe!");
                        }
                        else{
                            mini_exit.setText("Visit the nearest center and get tested");
                        }
                        mini_exit.setVisibility(View.VISIBLE);
                        reserveBed.setVisibility(View.INVISIBLE);
                    }
                }

            }
        });
        reserveBed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerActivity = new Intent(getApplicationContext(), register_activity.class);
                startActivity(registerActivity);
            }
        });
    }
}
