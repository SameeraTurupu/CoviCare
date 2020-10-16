package com.avinash.requestresource;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.RadioButton;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class question_one extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_1);
        final RadioButton yes = (RadioButton) findViewById(R.id.yes);
        final RadioButton no = (RadioButton) findViewById(R.id.no);
        final SharedPreferences pref = getApplicationContext().getSharedPreferences("8ResQ",0);
        final SharedPreferences.Editor editor = pref.edit();

        yes.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editor.putInt("count", pref.getInt("count",0) + 1);
                editor.apply();
                Intent QuestionActivity = new Intent(getApplicationContext(), question_two.class);
                startActivity(QuestionActivity);
            }
        });
        no.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Intent QuestionActivity = new Intent(getApplicationContext(), question_two.class);
                startActivity(QuestionActivity);
            }
        });
    }
}
