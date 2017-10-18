package edu.neu.madcourse.xiaohuawang;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.TextView;
import android.os.Bundle;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class GameFragment extends Fragment {
    static private int mLargeIds[] = {R.id.large1, R.id.large2, R.id.large3,
            R.id.large4, R.id.large5, R.id.large6, R.id.large7, R.id.large8,
            R.id.large9,};
    static private int mSmallIds[] = {R.id.small1, R.id.small2, R.id.small3,
            R.id.small4, R.id.small5, R.id.small6, R.id.small7, R.id.small8,
            R.id.small9,};
    private Set<Tile> mAvailable = new HashSet<Tile>();
    private int soundMiss, soundInvalid, soundAdd, soundSuccess;
    private SoundPool soundPool;
    private float mVolume = 0.5f;

    private TextView timer_tv;
    // current word text view
    private TextView currentWordTv;
    private TextView totalScoreTv;
    private Set<String> dictionary = new HashSet<String>();
    private int currentGroup = -1;
    private String currentChar;
    // the arrayList of the selected text char
    private ArrayList<String> selectedText = new ArrayList<String>();
    private int totalScore = 0;
    // current word
    private String currentWord = "";
    private HashSet<String> wordSet = new HashSet<String>();
    private View rootView;
    private Button phase2;
    private int completedLargeBorad = 0;
    private List<Integer> groupList;
    private List<Integer> preButtonTag;
    private int score_wordhighest;
    private int currentGroup2 = -1;
    // phase 2 current word
    private String currentWord2 = "";
    private int phase1Score;
    private long lastPause;
    // private int count_down = 183;
    private MyTimer myTimer;
    private Chronometer chronometer;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("UT3", "-----------on create---------------");

        // Retain this fragment across configuration changes.
        setRetainInstance(true);
        groupList = new ArrayList<>();
        preButtonTag = new ArrayList<>();

        soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 0);

        soundSuccess = soundPool.load(getActivity(), R.raw.soundsuccess, 1);
        soundAdd = soundPool.load(getActivity(), R.raw.soundadd, 1);
        soundMiss = soundPool.load(getActivity(), R.raw.soundmiss, 1);
        soundInvalid = soundPool.load(getActivity(), R.raw.soundinvalid, 1);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("UT3", "-----------start---------------");


        View rootView = inflater.inflate(R.layout.large_board, container, false);
        this.rootView = rootView;

        InputStream inputStream = getResources().openRawResource(R.raw.wordlist);
        InputStreamReader inputStreamReader = null;

        initViews(rootView);

        return rootView;
    }

    private void initViews(final View rootView) {


        Log.d("UT3", "----------- init view---------------");

        currentWordTv = rootView.findViewById(R.id.text_word);
        totalScoreTv = rootView.findViewById(R.id.text_score);

        chronometer = rootView.findViewById(R.id.chronometer_1);
//        chronometer.setVisibility(View.INVISIBLE);
        chronometer.start();

        myTimer = rootView.findViewById(R.id.timer);
        myTimer.initTime(181);
        myTimer.start();

        phase2 = (Button) rootView.findViewById(R.id.button_phase2);
        phase2.setVisibility(View.INVISIBLE);
//      phase2.setVisibility(View.VISIBLE);


        //when reach stage1 end
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                if (SystemClock.elapsedRealtime() - chronometer.getBase()> 90 * 1000) {
                    chronometer.stop();
                    myTimer.stop();

                    phase2Begin();
                    String phase2Begin = getString(R.string.phase2_begin);
                    Toast.makeText(getActivity(), phase2Begin, Toast.LENGTH_SHORT).show();
                    Toast.makeText(getActivity(), chronometer.getText(), Toast.LENGTH_SHORT).show();

                    for (int i = 0; i < 9; i++) {
                        View outer4 = rootView.findViewById(mLargeIds[i]);
                        for (int j = 0; j < 9; j++) {
                            Button inner = (Button) outer4.findViewById(mSmallIds[j]);
                            inner.setTextSize(20);

//                            System.out.println("current group= " + currentGroup);
//                            System.out.println("get tag= " + (int) inner.getTag() % 10);
                            if (!inner.isSelected() || (int) inner.getTag() % 10 == currentGroup) {
                                inner.setText("");
                            }
                            inner.setEnabled(false);
                        }
                    }
                    selectedText.clear();
                }

//                if (SystemClock.elapsedRealtime() - chronometer.getBase() > 50 * 1000) {
//
//                    String gameEnd = getString(R.string.phase2_end);
//                    Toast.makeText(getActivity(), gameEnd, Toast.LENGTH_LONG).show();
//
////                        rootView.findViewById(R.id.button_phase2).setBackgroundColor(Color.GREEN);
//                    rootView.findViewById(R.id.button_phase2).setClickable(false);
//
//                    //end game
//                    GameEnd();
//                }
////
            }
        });

        //set up what happened when you clicked the small button
        for (int large = 0; large < 9; large++) {
            View outer = rootView.findViewById(mLargeIds[large]);
//            mLargeTiles[large].setView(outer);
            for (int small = 0; small < 9; small++) {
                Button inner = (Button) outer.findViewById(mSmallIds[small]);
                System.out.println("set Tag= " + (large + small * 10));
                inner.setTag(large + small * 10);

//                final Tile smallTile = mSmallTiles[large][small];
//                smallTile.setView(inner);

                inner.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Button currentButton = (Button) view;
                        int group = (int) currentButton.getTag() % 10;
                        // when everyButton has been actived again, come here
                        if (currentGroup == -1) {

//                        smallTile.animate();
//                            System.out.println("int group= " + group);
//                            System.out.println("get tag= " + currentButton.getTag());
//                            System.out.println("get text= " + currentButton.getText());

                            currentGroup = group;
                            currentButton.setSelected(true);
                            currentChar = currentButton.getText().toString();
                            selectedText.add(currentChar);
                            currentWordTv.setText(currentChar);
                            currentButton.setTextSize(20);
                            preButtonTag.add((int) currentButton.getTag() / 10);

                            soundPool.play(soundAdd, mVolume, mVolume, 1, 0, 1f);
                        }
                        // when in the process, you select the same group button, come here
                        else if (currentGroup == group) {
                            currentChar = currentButton.getText().toString();

                            if (currentButton.isSelected()) {
                                //if the button is the last one you selected
                                if (currentChar.equals(selectedText.get(selectedText.size() - 1))) {
                                    //need to play a sound here

//                                    soundPool.play(soundDelete, mVolume, mVolume, 1, 0, 1f);
                                    currentButton.setSelected(false);
                                    selectedText.remove(selectedText.size() - 1);
                                    StringBuilder sb = new StringBuilder();
                                    for (String s : selectedText) {
                                        sb.append(s);
                                    }
                                    currentWord = sb.toString();
                                    currentWordTv.setText(currentWord);
                                    currentButton.setTextSize(14);
                                    preButtonTag.remove(preButtonTag.size() - 1);
                                    if (selectedText.isEmpty()) {
                                        currentGroup = -1;
                                    }
                                    // if the button is already selected and not the last one
                                } else {
                                    // do nothing, play miss sound
                                    soundPool.play(soundMiss, mVolume, mVolume, 1, 0, 1f);
                                }
                                return;
                                // the text in the same group hasn't been selected
                            } else {
                                if (isNextButton((int) currentButton.getTag() / 10, preButtonTag.get(preButtonTag.size() - 1))) {
                                    selectedText.add(currentChar);
                                    StringBuilder sb = new StringBuilder();
                                    for (String s : selectedText) {
                                        sb.append(s);
                                    }
                                    currentWord = sb.toString();
                                    currentWordTv.setText(currentWord);

                                    //need to play a sound
                                    soundPool.play(soundAdd, mVolume, mVolume, 1, 0, 1f);
                                    currentButton.setSelected(true);
                                    currentButton.setTextSize(20);
                                    preButtonTag.add((int) currentButton.getTag() / 10);
                                    System.out.println("the current word is= " + currentWord);

                                } else {
                                    System.out.println("this button is not the near button, can't be selected");
                                    soundPool.play(soundMiss, mVolume, mVolume, 1, 0, 1f);
                                }

                            }
                        }
//                        current_tv.setText(ls.get(0).toString());
//                        pos_list.add(((int) current_button.getTag()) / 10);

                    }
                });

            }
        }

        //when click submit button
        rootView.findViewById(R.id.button_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //get dictionary wordset
                getDictionary();

//                System.out.println("word set.size= " + wordSet.size());
                Log.d("UT3", "-----------when  submitted---------------");

                if (selectedText.size() < 3) {
                    Log.d("UT3", "-----------<3---------------");
                    soundPool.play(soundInvalid, mVolume, mVolume, 1, 0, 1f);
                    return;
                }

//              if (wordSet.contains(currentWord)){
                if (wordSet.contains(currentWord) || wordSet.contains(currentWord2)) {

                    System.out.println("second time click, come here---------");
                    System.out.println("wordset.size= " + wordSet.size());
                    System.out.println("get the correct word");
                    System.out.println("current word= " + currentWord);
                    totalScore += getTotalScore();
                    System.out.println("text score= " + totalScore);
                    System.out.println("----------current group id= " + currentGroup);
                    totalScoreTv.setText(String.valueOf(totalScore));
                    selectedText.clear();

                    completedLargeBorad++;
                    soundPool.play(soundSuccess, mVolume, mVolume, 1, 0, 1f);
                    //reset the currentWord, so when we check the word from phase 2,
                    //only check the phase 2 word.
                    currentWord = "";

                    //phase2 is visible, will end the game
                    if (phase2.getVisibility() == View.VISIBLE) {

                        for (int i = 0; i < 9; i++) {
                            View outer2 = rootView.findViewById(mLargeIds[i]);
                            for (int j = 0; j < 9; j++) {
                                Button inner = (Button) outer2.findViewById(mSmallIds[j]);
                                inner.setTextSize(20);

                                if (!inner.isSelected()) {
                                    inner.setText("");
                                }
                                if (inner.isSelected()) {
                                    inner.setTextColor(Color.GREEN);
                                }
                                inner.setEnabled(false);
                            }
                        }

                        String gameEnd = getString(R.string.phase2_end);
                        Toast.makeText(getActivity(), gameEnd, Toast.LENGTH_LONG).show();

//                        rootView.findViewById(R.id.button_phase2).setBackgroundColor(Color.GREEN);
                        rootView.findViewById(R.id.button_phase2).setClickable(false);

                        //end game
                        System.out.println("come here end game");
                        GameEnd();

                    } else {

                        System.out.println("currentGroup= " + currentGroup);
                        //disable all the other button in the same big board
                        View outer = rootView.findViewById(mLargeIds[currentGroup]);

                        // set the text size when phase 1 end

                        for (int i = 0; i < 9; i++) {
                            Button inner = (Button) outer.findViewById(mSmallIds[i]);
                            inner.setTextSize(20);
                        }

                        for (int i = 0; i < 9; i++) {
                            Button inner = (Button) outer.findViewById(mSmallIds[i]);
                            if (!inner.isSelected()) {
                                inner.setText("");
                                inner.setEnabled(false);
                            }
                            if (inner.isSelected()) {
                                inner.setTextColor(Color.WHITE);
                                inner.setEnabled(false);
                            }
                        }

                        //when completed this amout of the large board, go to phase 2
                        if (completedLargeBorad == 9) {

                            // disable all the unselected key
                            for (int i = 0; i < 9; i++) {
                                View outer3 = rootView.findViewById(mLargeIds[i]);
                                for (int j = 0; j < 9; j++) {
                                    Button inner = (Button) outer3.findViewById(mSmallIds[j]);
                                    inner.setTextSize(20);

                                    if (!inner.isSelected()) {
                                        inner.setText("");
                                    }
                                    inner.setEnabled(false);
                                }
                            }

                            myTimer.stop();
                            chronometer.stop();
                            phase2Begin();
                            String phase2Begin = getString(R.string.phase2_begin);
                            Toast.makeText(getActivity(), phase2Begin, Toast.LENGTH_LONG).show();
                        }
                    }
                    currentGroup = -1;
                } else {
                    currentWordTv.setText("That's not a word!");
                    soundPool.play(soundInvalid, mVolume, mVolume, 1, 0, 1f);
                }
            }
        });

        //when click pause button
        rootView.findViewById(R.id.button_pauseGame).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Button pauseGameButton = (Button) view;
                if (pauseGameButton.getText().equals("pause")) {
                    pauseGameButton.setText("resume");
                    hideScreen(true);
                    myTimer.onPause();
                    // when the button text= resume
                } else {
                    pauseGameButton.setText("pause");
                    hideScreen(false);
                    myTimer.onResume();
                }

            }
        });
    }


    //get the total selected String score
    public int getTotalScore() {

        int score = 0;
        System.out.println("select text size= " + selectedText.size());
        for (String c : selectedText) {
            score += getCharScore(c.charAt(0));
        }
        return score;
    }

    //    1 point: E, A, I, O, N, R, T, L, S;
    //    2 points: D, G;
    //    3 points: B, C, M, P;
    //    4 points: F, H, V, W, Y; 5 points: K;
    //    8 points: J, X; 10 points: Q, Z.
    public int getCharScore(Character c) {

        System.out.println("c= " + c);

        if (c == 'e' || c == 'a' || c == 'i' || c == 'o' || c == 'n' || c == 'r' || c == 't' || c == 'l' || c == 's')
            return 1;

        if (c == 'd' || c == 'g')
            return 2;

        if (c == 'b' || c == 'c' || c == 'm' || c == 'p')
            return 3;

        if (c == 'f' || c == 'h' || c == 'v' || c == 'w' || c == 'y')
            return 4;

        if (c == 'k')
            return 5;

        if (c == 'j' || c == 'x')
            return 8;

        if (c == 'q' || c == 'z')
            return 10;

        System.out.println("where is u???");
        return 0;
    }

    //get all the words into the wordset
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
    }

    //hide the screen if true, resume if false
    public void hideScreen(boolean isHide) {
        Log.d("UT3", "-----------hide the screen---------------");
        for (int i = 0; i < 9; i++) {
            View outer = rootView.findViewById(mLargeIds[i]);
            if (isHide) {
                outer.setVisibility(View.INVISIBLE);
            } else {
                outer.setVisibility(View.VISIBLE);
            }
        }
    }

    //to check if the two button is next to each other (x1-x2)^2+(y1-y2)^2
    public boolean isNextButton(int currentButtonPosition, int lastButtonPosition) {

        Log.d("UT3", "-----------isNextButton---------------");
        int currentButtonX = currentButtonPosition / 3;
        int currentButtonY = currentButtonPosition % 3;
        int lastButtonX = lastButtonPosition / 3;
        int lastButtonY = lastButtonPosition % 3;

        System.out.println("the result= " + ((currentButtonX - lastButtonX) * (currentButtonX - lastButtonX) + (currentButtonY - lastButtonY) * (currentButtonY - lastButtonY)));

        if ((currentButtonX - lastButtonX) * (currentButtonX - lastButtonX) + (currentButtonY - lastButtonY) * (currentButtonY - lastButtonY) <= 2) {
            return true;
        } else {
            return false;
        }
    }

    public void GameEnd() {

        Log.d("UT3", "------------------end game-------------------");
        myTimer.initTime(0);
        myTimer.stop();
        chronometer.stop();
    }

    //
    public void phase2Begin() {

        Log.d("UT3", "------------------begin phase two-------------------");

        phase1Score = totalScore;
        rootView.findViewById(R.id.button_phase2).setVisibility(View.VISIBLE);
        currentWordTv.setText("");
        rootView.findViewById(R.id.button_phase2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                view.setEnabled(false);
                myTimer.initTime(90);
                myTimer.start();
//                chronometer.start();

                for (int i = 0; i < 9; i++) {
                    View outer = rootView.findViewById(mLargeIds[i]);

                    for (int j = 0; j < 9; j++) {
                        final Button inner = (Button) outer.findViewById(mSmallIds[j]);

                        inner.setTag(i);
                        System.out.println("small tag= " + j);

                        //set all the inner button clicked on stage one to size 14, and set enable true
                        if (!inner.getText().toString().equals("")) {
                            inner.setEnabled(true);
                            inner.setTextSize(14);
                            inner.setSelected(false);
//                            inner.setTextColor(Color.parseColor("#A4C639"));
                        }
                        //keep all the button same size
                        if (inner.getText().toString().equals("")) {
                            inner.setTextSize(14);
                        }

//                        set up what happened when we click phase 2 button
                        inner.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Button curButtonPhase2 = (Button) view;
                                System.out.println("phase 2 letter= " + curButtonPhase2.getText());
                                currentChar = curButtonPhase2.getText().toString();
                                int group = (int) inner.getTag();

                                /**
                                 * the following two test line, very important
                                 */
                                System.out.println("the phase 2 button group is= " + group);
                                System.out.println("----------------the phase 2 previous group is= " + currentGroup2);
                                System.out.println("list= " + groupList);

                                //the first time select a phase2 button
                                if (currentGroup2 == -1) {
                                    selectedText.add(currentChar);
                                    currentGroup2 = group;
                                    groupList.add(group);
                                    currentWordTv.setText(currentChar);
                                    curButtonPhase2.setSelected(true);
                                    curButtonPhase2.setTextSize(20);
                                    currentGroup2 = group;

                                    soundPool.play(soundAdd, mVolume, mVolume, 1, 0, 1f);
                                    //if the button we click is in the same group of the last button
                                } else if (group == currentGroup2) {
                                    //if the button is selected
                                    if (curButtonPhase2.isSelected()) {
                                        System.out.println("whats that= " + curButtonPhase2.toString());
                                        // if the button is selected and is the same as the last one
                                        if (currentChar.equals(selectedText.get(selectedText.size() - 1))) {
                                            //we can play a remove sound here

                                            curButtonPhase2.setTextSize(14);
                                            curButtonPhase2.setSelected(false);

                                            selectedText.remove(selectedText.size() - 1);
                                            StringBuilder sb = new StringBuilder();

                                            for (String s : selectedText) {
                                                sb.append(s);
                                            }
                                            currentWord2 = sb.toString();
                                            currentWordTv.setText(currentWord2);
                                            groupList.remove(groupList.size() - 1);

                                            if (groupList.isEmpty()) {
                                                currentGroup2 = -1;
                                            } else {
                                                currentGroup2 = groupList.get(groupList.size() - 1);
                                            }
                                        } else {
                                            // the button is in the same group as the last one and selected , but no the last one we selected
                                            soundPool.play(soundMiss, mVolume, mVolume, 1, 0, 1f);
                                        }
                                    }//if the button is in the same group and not selected
                                    else {
                                        System.out.println("in the same group not selected-------------");
                                        soundPool.play(soundMiss, mVolume, mVolume, 1, 0, 1f);
                                    }
                                    // if the button is not in the same group as the last selected button
                                } else if (group != currentGroup2) {
                                    // if this button is selected and from different group
                                    if (curButtonPhase2.isSelected()) {
                                        soundPool.play(soundMiss, mVolume, mVolume, 1, 0, 1f);
                                        return;
                                        // if this button is not selected and from different group
                                    } else {
                                        selectedText.add(currentChar);
                                        groupList.add(group);
                                        StringBuilder sb = new StringBuilder();

                                        for (String s : selectedText) {
                                            sb.append(s);
                                        }

                                        currentWord2 = sb.toString();
                                        System.out.println("current word2= " + currentWord2);
                                        currentWordTv.setText(currentWord2);

                                        curButtonPhase2.setSelected(true);
                                        curButtonPhase2.setTextSize(20);
                                        currentGroup2 = group;
                                        soundPool.play(soundAdd, mVolume, mVolume, 1, 0, 1f);

                                    }


                                }
                            }
                        });

                    }

                }

            }
        });
    }

}





















