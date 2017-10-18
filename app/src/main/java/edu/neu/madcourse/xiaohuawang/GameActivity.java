package edu.neu.madcourse.xiaohuawang;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import edu.neu.madcourse.xiaohuawang.*;

public class GameActivity extends Activity {

    static private int mLargeIds[] = {R.id.large1, R.id.large2, R.id.large3,
            R.id.large4, R.id.large5, R.id.large6, R.id.large7, R.id.large8,
            R.id.large9,};
    static private int mSmallIds[] = {R.id.small1, R.id.small2, R.id.small3,
            R.id.small4, R.id.small5, R.id.small6, R.id.small7, R.id.small8,
            R.id.small9,};

    private ArrayList<String> targetString = new ArrayList<String>();
    private ArrayList<String> wordString = new ArrayList<String>();
    private ArrayList<String> dictionaryList = new ArrayList<String>();
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Log.d("UT3", "-----------on create game activity---------------");

        InputStream inputStream = getResources().openRawResource(R.raw.wordlist);
        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(inputStream);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        BufferedReader bReader = new BufferedReader(inputStreamReader);
        String line;
        try {
            while ((line = bReader.readLine()) != null) {
                dictionaryList.add(line);
            }
            bReader.close();
            inputStreamReader.close();
            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        //set ninewords to store all the nine words string
        for (String s : dictionaryList) {
            if (s.length() == 9) {
                char[] sarr = s.toCharArray();
                Arrays.sort(sarr);
                StringBuilder strbuilder = new StringBuilder();
                for (char a : sarr) {
                    strbuilder.append(a);
                }
                targetString.add(strbuilder.toString());
            }
        }

        Set<Integer> usedWord = new HashSet<Integer>();
        for (int i = 0; i < 9; i++) {
            int index = (int) Math.floor(Math.random() * targetString.size());

//          for test
//          System.out.println("index= "+index);
            while (usedWord.contains(index)) {
                index = (int) Math.floor(Math.random() * targetString.size());
            }
            usedWord.add(index);


            String c = targetString.get(index);
            wordString.add(c);
        }
        //get the letters
        //Intent intent = getIntent();
        //ninewords_strings = intent.getStringArrayListExtra("ListString");
        loadGame();

        //set toggleButton
        ToggleButton toggleButton = (ToggleButton) findViewById(R.id.button_toggle);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {

                if (isChecked) {
                    mediaPlayer.setVolume(0.6f, 0.6f);
                    mediaPlayer.setLooping(true);
                    mediaPlayer.start();
                } else {
                    mediaPlayer.pause();
                }
            }

        });
    }

    // set 9 char on the button
    public void loadGame() {

        Log.d("UT3", "-----------set inner button text---------------");
        for (int d = 0; d < 9; d++) {
            View outer = (View) findViewById(mLargeIds[d]);
            for (int j = 0; j < 9; j++) {
                Button inner = (Button) outer.findViewById(mSmallIds[j]);
                if (d == 0) {
//                    for test
//                    inner.setText("6");
                    inner.setText(String.valueOf(wordString.get(d).charAt(j)));
                } else {
                    inner.setText(String.valueOf(wordString.get(d).charAt(j)));
                }

            }

        }
    }

    /**
     * on pause stop music
     */
    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.release();
    }

    /**
     * on resume restart music
     */
    @Override
    protected void onResume() {
        super.onResume();
        mediaPlayer = MediaPlayer.create(this, R.raw.mainbgm);
        mediaPlayer.setVolume(0.6f, 0.6f);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    /**
     * when hit the back button
     */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        finish();

    }

}
