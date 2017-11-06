package xiaohuawang.madcourse.neu.edu.numad17f_xiaohuawang;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import xiaohuawang.madcourse.neu.edu.numad17f_xiaohuawang.models.GamePlayer;

/**
 * Created by yangyangyy on 11/4/17.
 */

public class SubmitData_Activity extends Activity {

    private static final String SERVER_KEY = "key=AAAARuVgC18:APA91bFrAzk7wRIoTSSCDEQZzB_2CQmP17ECfcRajldc3uewYLi0LMIi0tSCdkfhBcXM2fDhNRjfwfI5ibMJ2cY2MhMYzCjMm87K5CykXTxSI2ZSKgqNP0dvuCa44btct8oUhY8T8eoZ";
    private DatabaseReference mDatabase;
    private String token;
    private String highestscoreWord;
    private int totalScore;
    private int highestScore;
    private int phaseoneScore;
    private TextView score_text;
    private TextView name_text;
    private EditText username;
    private Button button_sumbit;
    private Button button_close;
    private Set<String> current_names;
    private Map<String, List<GamePlayer>> content_map;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit);
        Bundle bundle = getIntent().getExtras();

        //pass data
        totalScore = Integer.valueOf(bundle.getString("totalScore"));
        phaseoneScore = Integer.valueOf(bundle.getString("phaseoneScore"));
        highestScore = Integer.valueOf(bundle.getString("highestScore"));
        highestscoreWord = bundle.getString("highestscoreWord");

        System.out.println("SubmitData on create--------------------");

        button_sumbit = (Button) findViewById(R.id.button_submit);
        button_close = (Button) findViewById(R.id.button_close);
        score_text = (TextView) findViewById(R.id.textView_score);
        name_text = (TextView) findViewById(R.id.textView_name);
        username = (EditText) findViewById(R.id.editText);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        token = FirebaseInstanceId.getInstance().getToken();
        System.out.println("token before= " + token);

//        if(token.contains("\"")){
//            if (token.contains(":")) {
//                Pattern p = Pattern.compile("\"(.+?)\"");
//                Matcher m = p.matcher(token);
//                ArrayList<String> strs = new ArrayList<String>();
//                while (m.find()) {
//                    strs.add(m.group(1));
//                }
//                token = strs.get(1);
//            }
//        }

        Log.d("UT3", "------------------token-------------------");
        System.out.println("token after= " + token);
        if (token.isEmpty()) {
            System.out.println("the token is empty--------------------");
            name_text.setText("Sorry your device is currently offline");
            button_sumbit.setEnabled(false);
        }
        score_text.setText("Congratulations, Total score is " + totalScore + " with " + phaseoneScore + " in phase 1 and " + (totalScore - phaseoneScore) + " in phase 2!");
        Date date = new Date();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        final GamePlayer player = new GamePlayer();
        player.highestScoreWord = highestscoreWord;
        player.phaseoneScore = phaseoneScore;
        player.totalScore = totalScore;
        player.highestScore = highestScore;
        player.highestScoreWord = highestscoreWord;
        player.time = df.format(date);
        mDatabase.child("scoreboardUser").child(token).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<HashMap<String, List<GamePlayer>>> t = new GenericTypeIndicator<HashMap<String, List<GamePlayer>>>() {
                };
                content_map = (Map<String, List<GamePlayer>>) dataSnapshot.getValue(t);
                if (content_map == null) {
                    current_names = new HashSet<>();
                    content_map = new HashMap<>();
                } else
                    current_names = content_map.keySet();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //when click submit button, save the username, compare the username
        button_sumbit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = username.getText().toString();
                if (name.length() < 3) {
                    name_text.setText("User name must be at least three characters");
                    return;
                }
                if (name.length() > 9) {
                    name_text.setText("User name shoule be at most nine characters");
                    return;
                }
                mDatabase.child("leaderboardUser").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        System.out.println("----------------------dataSnapShot= "+dataSnapshot);
                        GenericTypeIndicator<HashMap<String, GamePlayer>> t = new GenericTypeIndicator<HashMap<String, GamePlayer>>() {
                        };
                        Map<String, GamePlayer> name_map = dataSnapshot.getValue(t);
                        if (name_map == null)
                            name_map = new HashMap<>();
                        if (current_names.contains(name)) {
                            content_map.get(name).add(player);
                            mDatabase.child("scoreboardUser").child(token).setValue(content_map);
                            GamePlayer lastInfo = name_map.get(name);
                            if (player.totalScore > lastInfo.totalScore) {
                                //create a thread
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        onGetNewHighScore(totalScore, name);
                                    }
                                }).start();
                                player.token = token;
                                name_map.put(name, player);
                                mDatabase.child("leaderboardUser").setValue(name_map);
                            }
                        } else if (name_map.keySet().contains(name)) {
                            username.setText("");
                            name_text.setText("please choose another name");
                        } else {
                            List<GamePlayer> list = new ArrayList<>();
                            list.add(player);
                            content_map.put(name, list);
                            mDatabase.child("scoreboardUser").child(token).setValue(content_map);
                            player.token = token;
                            name_map.put(name, player);
                            mDatabase.child("leaderboardUser").setValue(name_map);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                Toast.makeText(getApplication(), "Game Data Submitted", Toast.LENGTH_LONG).show();
            }
        });
        button_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SubmitData_Activity.this, MainActivity.class);
                startActivity(i);
            }
        });

    }

    private void onGetNewHighScore(int score, String name) {

        Log.d("UT3", "-----------onGetNew HighScore---------------");
        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        try {
            jNotification.put("title", "Congratulation");
            jNotification.put("body", name + " got a highest score of " + score + "!");
            jNotification.put("sound", "default");
            jNotification.put("badge", "1");
            jNotification.put("click_action", "OPEN_ACTIVITY_1");
//            jPayload.put("to", "/topics/wordgame");
            System.out.println("token= " + token);
            jPayload.put("to", token);
            jPayload.put("priority", "high");
            jPayload.put("notification", jNotification);
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            System.out.println("conn= " + conn);

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", SERVER_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jPayload.toString().getBytes());
            outputStream.close();

            System.out.println("output finish ");
            InputStream inputStream = conn.getInputStream();
            System.out.println("is= " + inputStream);
            final String resp = convertStreamToString(inputStream);

            System.out.println("resp= " + resp);
            Handler h = new Handler(Looper.getMainLooper());
            h.post(new Runnable() {
                @Override
                public void run() {
                    System.out.println("resp is: " + resp);
                }
            });
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    private String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next().replace(",", ",\n") : "";
    }

}
