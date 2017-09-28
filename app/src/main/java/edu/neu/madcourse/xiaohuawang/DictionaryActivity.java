package edu.neu.madcourse.xiaohuawang;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.StringBuilderPrinter;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by yangyangyy on 9/24/17.
 */

public class DictionaryActivity extends AppCompatActivity {

    private static final String ACTIVITY_TAG = "DictionaryActivity";
    private TextView listTextView;
    private EditText inputEditText;
    private Button clearButton;
    private Button acknowledgmentsButton;
    private ListView inputTextListView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> wordlist;
    private HashSet<String> wordSet = new HashSet<String>();
    private ArrayList<String> searchList = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //set the title bar
        setTitle("Test Dictionary");
        setContentView(R.layout.activity_dictionary);
        getDictionary();

        //EditText change
        inputEditText = (EditText) findViewById(R.id.inputEditText);
        inputEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.v(DictionaryActivity.ACTIVITY_TAG, "onTextChanged");
                String inputText = inputEditText.getText().toString();

                if (isWord(inputText)) {
                    getSound();
                    searchList.add(inputText);
                    adapter = new ArrayAdapter<String>(DictionaryActivity.this, R.layout.activity_word, searchList);
                    inputTextListView = (ListView) findViewById(R.id.inputTextListView);
                    inputTextListView.setFastScrollEnabled(true);
                    inputTextListView.setAdapter(adapter);
                    Log.v(DictionaryActivity.ACTIVITY_TAG, "exist");
                } else {
                    Log.v(DictionaryActivity.ACTIVITY_TAG, "not exist");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //click clearButton
        acknowledgmentsButton = (Button) findViewById(R.id.acknowledgmentsButton);
        acknowledgmentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DictionaryActivity.this, AcknowledgementsActivity.class);
                startActivity(i);
            }
        });

    }

    //clear button
    public void onClick(View view) {
            searchList.clear();
            adapter = new ArrayAdapter<String>(DictionaryActivity.this, R.layout.activity_word, searchList);
            inputTextListView = (ListView) findViewById(R.id.inputTextListView);
            inputTextListView.setAdapter(adapter);
            inputEditText.setText("");
    }

    //save the dictionary txt into hashset
    public void getDictionary() {

        InputStream is = this.getResources().openRawResource(R.raw.wordlist);
        wordSet = new HashSet<String>();

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(is));
            String line = reader.readLine();
            while (line != null) {
                wordSet.add(line);
                line = reader.readLine();
            }
            reader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.v(DictionaryActivity.ACTIVITY_TAG, "finish get dictionary");
    }

    //check if the word belong to the dictionary
    public boolean isWord(String inputText) {

        for (String wordStr : wordSet) {
            if (inputText.equals(wordStr)) {
                return true;
            }
        }
        return false;
    }

    //make a beep
    public void getSound() {
        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
        toneG.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 200);
        toneG.release();
    }

}
