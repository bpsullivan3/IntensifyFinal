package edu.estrauchvillanova.intensify;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class SecondActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.second_activity_layout);



        Button button7 = findViewById(R.id.button7);
        Button button8 = findViewById(R.id.button8);
        Button button9 = findViewById(R.id.button9);
        Button button10 = findViewById(R.id.button10);
        Button button11 = findViewById(R.id.button11);
        Button button12 = findViewById(R.id.button12);

        button7.setOnClickListener(this);
        button8.setOnClickListener(this);
        button9.setOnClickListener(this);
        button10.setOnClickListener(this);
        button11.setOnClickListener(this);
        button12.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        Bundle bundle = getIntent().getExtras();
        String data_one = bundle.getString("value_button1");

        switch (v.getId()){
            case R.id.button7:
                Intent intent = new Intent(SecondActivity.this, ThirdActivity.class);
                intent.putExtra("value_button2", "2");
                intent.putExtra("value_button1",data_one);
                startActivity(intent);
                break;

            case R.id.button8:
                Intent intentTwo = new Intent(SecondActivity.this, ThirdActivity.class);
                intentTwo.putExtra("value_button2", "0");
                intentTwo.putExtra("value_button1",data_one);
                startActivity(intentTwo);
                break;

            case R.id.button9:
                Intent intentThree = new Intent(SecondActivity.this, ThirdActivity.class);
                intentThree.putExtra("value_button2", "4");
                intentThree.putExtra("value_button1",data_one);
                startActivity(intentThree);
                break;

            case R.id.button10:
                Intent intentFour = new Intent(SecondActivity.this, ThirdActivity.class);
                intentFour.putExtra("value_button2", "5");
                intentFour.putExtra("value_button1",data_one);
                startActivity(intentFour);
                break;
            case R.id.button11:
                Intent intentFive = new Intent(SecondActivity.this, ThirdActivity.class);
                intentFive.putExtra("value_button2", "1");
                intentFive.putExtra("value_button1",data_one);
                startActivity(intentFive);
                break;

            case R.id.button12:
                Intent intentSix = new Intent(SecondActivity.this, ThirdActivity.class);
                intentSix.putExtra("value_button2", "3");
                intentSix.putExtra("value_button1",data_one);
                startActivity(intentSix);
                break;
        }
    }


    public void pushpageThree(View view) {
       startActivity(new Intent(SecondActivity.this, ThirdActivity.class));
    }
}


