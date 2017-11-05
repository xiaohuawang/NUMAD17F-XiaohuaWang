package edu.neu.madcourse.xiaohuawang;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Leaderboard_Activity extends Activity {
    private DatabaseReference mDatabase;
    private String token;
    private static final String SERVER_KEY = "key=AAAAY1Mcv6E:APA91bFNwmTM6OMc-C5fkVtaWpQkP1_WpOkqxogmM-iLe45Qf3UcFJeqV_0GIdEtEQyMhDFeGJ36CvEB2Lki8I2jL6SBN48oG7FC80DBa1nFTe3SKiUu6NKTa8jkSPRlRBUuKbceHZko";
    private ViewGroup vg;
    private String sender;
    private Button buttonCon;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            TextView currentText = (TextView) msg.obj;
        }
    };

    // small top
    public class PlayerInfoComparator implements Comparator<MyEntry<String, GamePlayer>> {
        @Override
        public int compare(MyEntry<String, GamePlayer> player1, MyEntry<String, GamePlayer> player2) {
            if (player1.getValue().totalScore < player2.getValue().totalScore)
                return -1;
            else if (player1.getValue().totalScore == player2.getValue().totalScore)
                return 0;
            return 1;
        }
    }

    // send notification
    private void sendCongrat(String to) {

        System.out.println("come here??????????????----------------------");
        System.out.println("Sender is:" + sender);
        System.out.println("token" + token);
        System.out.println("token_to" + to);

        if (sender.isEmpty()) {
            mDatabase.child("scoreboardUser").child(token).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            GenericTypeIndicator<HashMap<String, List<GamePlayer>>> t = new GenericTypeIndicator<HashMap<String, List<GamePlayer>>>() {
                            };
                            Map<String, List<GamePlayer>> myMap = dataSnapshot.getValue(t);
                            String from = "nobody";
                            if (myMap != null) {
                                for (String name : myMap.keySet()) {
                                    from = name;
                                    break;
                                }
                            }
                            sender = from;
                            System.out.println("sender now:" + sender);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    }
            );
        }
        JSONObject jPayload = new JSONObject();
        JSONObject jNotification = new JSONObject();
        try {
            System.out.println("send?????????????----------------------");
            jNotification.put("title", "Congratulation");
            while (sender.isEmpty()) {
            }
            jNotification.put("body", sender + " sends you a congrats!");
            jNotification.put("sound", "default");
            jNotification.put("badge", "1");
            jNotification.put("click_action", "OPEN_ACTIVITY_1");
            jPayload.put("to", to);
            jPayload.put("priority", "high");
            jPayload.put("notification", jNotification);
            URL url = new URL("https://fcm.googleapis.com/fcm/send");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", SERVER_KEY);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            OutputStream outputStream = conn.getOutputStream();
            outputStream.write(jPayload.toString().getBytes());
            outputStream.close();

            InputStream inputStream = conn.getInputStream();
            System.out.println("is= " + inputStream);

            final String resp = convertStreamToString(inputStream);

            System.out.println("resp= " + resp);
            Handler h = new Handler(Looper.getMainLooper());
            h.post(new Runnable() {
                @Override
                public void run() {
                    System.out.println("resp is: " + resp);
//                    Toast.makeText(Leaderboard_Activity.this, resp, Toast.LENGTH_LONG);
                }
            });
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    //convert from inputstream to string
    private String convertStreamToString(InputStream is) {
        Scanner s = new Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next().replace(",", ",\n") : "";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        vg = (ViewGroup) findViewById(R.id.activity_leaderboard);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        token = FirebaseInstanceId.getInstance().getToken();
        sender = "";
        if (token.isEmpty()) {
            TextView tv = new TextView(this);
            tv.setText("Sorry your device is offline, could not retrieve data");
            vg.addView(tv);
            return;
        } else {
            Pattern p = Pattern.compile("\"(.+?)\"");
            Matcher m = p.matcher(token);
            ArrayList<String> strs = new ArrayList<String>();
            while (m.find()) {
                strs.add(m.group(1));
            }
            token = strs.get(1);
        }

        mDatabase.child("leaderboardUser").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                vg.removeAllViews();
                View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.leader_item, null);
//                vg.addView(view);
                GenericTypeIndicator<HashMap<String, GamePlayer>> t = new GenericTypeIndicator<HashMap<String, GamePlayer>>() {
                };
                Map<String, GamePlayer> myMap = dataSnapshot.getValue(t);
                if (myMap == null) {
                    return;
                }
                Comparator<MyEntry<String, GamePlayer>> comparator = new PlayerInfoComparator();
                PriorityQueue<MyEntry<String, GamePlayer>> q = new PriorityQueue<>(10, comparator);
                for (String key : myMap.keySet()) {
                    if (q.size() == 10)
                        q.poll();
                    q.add(new MyEntry(key, myMap.get(key)));
                }
                List<MyEntry<String, GamePlayer>> list = new ArrayList<>();
                while (!q.isEmpty()) {
                    list.add(q.poll());
                }

                for (int i = list.size() - 1; i >= 0; i--) {
                    View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.leader_item, null);
                    final String token_to = myMap.get(list.get(i).getKey()).token;
                    System.out.println("token to= " + token_to);
                    ((TextView) v.findViewById(R.id.usernameLead)).setText(list.get(i).getKey());
                    buttonCon = (Button) v.findViewById(R.id.buttonConLead);
                    buttonCon.setOnClickListener(new Button.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            System.out.println("-----------------on click--------------");
                            System.out.println("v.get tag= " + v.getTag());
//                            if (v.getTag() != null) {

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    sendCongrat(token_to);
                                }
                            }).start();
                            Message msg = Message.obtain(mHandler);
                            msg.obj = v;
                            mHandler.sendMessageDelayed(msg, 500);
                            return;
                        }
                    });
                    ((TextView) v.findViewById(R.id.userTimeLead)).setText(list.get(i).getValue().time);
                    ((TextView) v.findViewById(R.id.userScoreLead)).setText(list.get(i).getValue().phaseoneScore + "+" + (list.get(i).getValue().totalScore
                            - list.get(i).getValue().phaseoneScore) + "=" + list.get(i).getValue().totalScore);
                    ((TextView) v.findViewById(R.id.userWordLead)).setText(list.get(i).getValue().highestScoreWord + ": " + list.get(i).getValue().highestScore);
                    vg.addView(v);
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    final class MyEntry<K, V> implements Map.Entry<K, V> {
        private final K key;
        private V value;

        public MyEntry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            V old = this.value;
            this.value = value;
            return old;
        }
    }
}
