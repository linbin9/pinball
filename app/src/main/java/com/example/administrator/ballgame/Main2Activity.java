package com.example.administrator.ballgame;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;

public class Main2Activity extends AppCompatActivity {

    private int level;
    private String str;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        Button b1 = (Button) findViewById(R.id.bn1);
        Button b2 = (Button) findViewById(R.id.bn2);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                str = spinner.getSelectedItem().toString();
                level= Integer.valueOf(str);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Main2Activity.this.finish();
            }
        });
        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Main2Activity.this, MainActivity.class);
                intent.putExtra("level",level);
                startActivity(intent);
                Main2Activity.this.finish();
            }
        });
    }
}
