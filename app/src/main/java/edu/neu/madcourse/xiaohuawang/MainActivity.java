package edu.neu.madcourse.xiaohuawang;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import edu.neu.madcourse.xiaohuawang.R;

public class MainActivity extends AppCompatActivity {

    private Button aboutButton;
    private Button errorButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("XiaohuaWang");//set the title bar
        setContentView(R.layout.activity_main);

        aboutButton = (Button) findViewById(R.id.aboutButton);
        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, Aty1.class);
                startActivity(i);
            }

        });

        errorButton = (Button) findViewById(R.id.errorButton);
        errorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                throw new RuntimeException("This is a crash");
//                stackOverflow();
            }
        });
    }

//    public void stackOverflow() {
//        this.stackOverflow();
//
//    }

}
