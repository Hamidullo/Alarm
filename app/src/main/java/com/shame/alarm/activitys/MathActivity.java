package com.shame.alarm.activitys;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.shame.alarm.R;
import com.shame.alarm.service.AlarmService;

import java.util.Random;

public class MathActivity extends AppCompatActivity {
    private Button btn1,btn2,btn3,btn4;
    private TextView textMath;
    private int summ = 0;

    private int[] btns = {1,2,3,4};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_math);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        btn4 = findViewById(R.id.btn4);
        textMath = findViewById(R.id.textMath);

        int sum1 = 0;
        int sum2 = 0;

        Intent intent = getIntent();

        Random random = new Random() ;

        if (intent.hasExtra("DIFFICULTY")){
            if (intent.getStringExtra("DIFFICULTY").equals("easy")){
                sum1 = random.nextInt(100);
                sum2 = random.nextInt(100);
                textMath.setText(sum1 + " + " + sum2 + " = ?");
                summ = sum1 + sum2;
            } else if (intent.getStringExtra("DIFFICULTY").equals("average")){
                sum1 = random.nextInt(1000);
                sum2 = random.nextInt(1000);
                textMath.setText(sum1 + " + " + sum2 + " = ?");
                summ = sum1 + sum2;
            } else if (intent.getStringExtra("DIFFICULTY").equals("heavy")){
                sum1 = random.nextInt(100);
                sum2 = random.nextInt(100);
                textMath.setText(sum1 + " * " + sum2 + " = ?");
                summ = sum1 * sum2;
            }
        } else {
            sum1 = random.nextInt(100);
            sum2 = random.nextInt(100);
            textMath.setText(sum1 + " + " + sum2 + " = ?");

            summ = sum1 + sum2;
        }

        Random random1 = new Random();
        int index = random1.nextInt(btns.length);

        if (index == 1){
            btn1.setText(String.valueOf(summ));
            btn2.setText(String.valueOf(random.nextInt(1000)) );
            btn3.setText(String.valueOf(random.nextInt(1000)) );
            btn4.setText(String.valueOf(random.nextInt(1000)) );
        } else if (index == 2){
            btn1.setText(String.valueOf(random.nextInt(1000)) );
            btn2.setText(String.valueOf(summ));
            btn3.setText(String.valueOf(random.nextInt(1000)) );
            btn4.setText(String.valueOf(random.nextInt(1000)) );
        } else if (index == 3){
            btn1.setText(String.valueOf(random.nextInt(1000)) );
            btn2.setText(String.valueOf(random.nextInt(1000)) );
            btn3.setText(String.valueOf(summ));
            btn4.setText(String.valueOf(random.nextInt(1000)) );
        } else {
            btn1.setText(String.valueOf(random.nextInt(1000)) );
            btn2.setText(String.valueOf(random.nextInt(1000)) );
            btn3.setText(String.valueOf(random.nextInt(1000)) );
            btn4.setText(String.valueOf(summ));
        }
    }

    public void Cancel(View view) {
        Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
        getApplicationContext().stopService(intentService);
        finish();
    }

    public void Summ(View view) {

        String sum = String.valueOf(summ);

        if (btn1.getText().toString().equals(sum)){
            Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
            getApplicationContext().stopService(intentService);
            finish();
        }
        if (btn2.getText().toString().equals(sum)){
            Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
            getApplicationContext().stopService(intentService);
            finish();
        }
        if (btn3.getText().toString().equals(sum)){
            Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
            getApplicationContext().stopService(intentService);
            finish();
        }
        if (btn4.getText().toString().equals(sum)){
            Intent intentService = new Intent(getApplicationContext(), AlarmService.class);
            getApplicationContext().stopService(intentService);
            finish();
        }

    }
}