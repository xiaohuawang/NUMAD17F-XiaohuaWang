package xiaohuawang.madcourse.neu.edu.numad17f_xiaohuawang;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button aboutButton;
    private Button errorButton;
    private Button dictionaryButton;
    private Button gameButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setTitle("Xiaohua Wang");
        setContentView(R.layout.activity_main);


        dictionaryButton = (Button) findViewById(R.id.dictionaryButton);
        dictionaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, DictionaryActivity.class);
                startActivity(i);
            }
        });

        aboutButton = (Button) findViewById(R.id.aboutButton);
        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(MainActivity.this, AboutActivity.class);
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

        gameButton = (Button) findViewById(R.id.gameButton);
        gameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, MainFragment.class);
                startActivity(i);
            }
        });

    }

}
