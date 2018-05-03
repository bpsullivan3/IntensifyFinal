package edu.estrauchvillanova.intensify;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class MainActivity extends Activity implements View.OnClickListener {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_layout);

        Button button1 = findViewById(R.id.button1);
        Button button2 = findViewById(R.id.button2);
        Button button3 = findViewById(R.id.button3);
        Button button4 = findViewById(R.id.button4);
        Button button5 = findViewById(R.id.button5);
        Button button6 = findViewById(R.id.button6);

        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        button5.setOnClickListener(this);
        button6.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        switch(v.getId()){
            case R.id.button1:
                Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                intent.putExtra("value_button1", "5");
                startActivity(intent);

                break;

            case R.id.button2:
                Intent intentTwo = new Intent(MainActivity.this, SecondActivity.class);
                intentTwo.putExtra("value_button1", "0");
                startActivity(intentTwo);
                break;

            case R.id.button3:
                Intent intentThree = new Intent(MainActivity.this, SecondActivity.class);
                intentThree.putExtra("value_button1", "1");
                startActivity(intentThree);
                break;

            case R.id.button4:
                Intent intentFour = new Intent(MainActivity.this, SecondActivity.class);
                intentFour.putExtra("value_button1", "4");
                startActivity(intentFour);
                break;
            case R.id.button5:
                Intent intentFive = new Intent(MainActivity.this, SecondActivity.class);
                intentFive.putExtra("value_button1", "2");
                startActivity(intentFive);
                break;

            case R.id.button6:
                Intent intentSix = new Intent(MainActivity.this, SecondActivity.class);
                intentSix.putExtra("value_button1", "3");
                startActivity(intentSix);
                break;


        }

    }

    //called when the click button
    public void pushPage(View view) {
        //what the button does

        startActivity(new Intent(MainActivity.this, SecondActivity.class));

    }


}
