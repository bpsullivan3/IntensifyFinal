package edu.estrauchvillanova.intensify;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class ThirdActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
//        Toolbar toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);



        Bundle bundle = getIntent().getExtras();
        String data_two = bundle.getString("value_button2");
        Bundle bundleOne = getIntent().getExtras();
        String data_one = bundleOne.getString("value_button1");
        int intensityVal = Integer.parseInt(data_one) + Integer.parseInt(data_two);
        TextView intensityReading = findViewById(R.id.intensityValue);
        intensityReading.append(" " + Integer.toString(intensityVal));


            }
        }



