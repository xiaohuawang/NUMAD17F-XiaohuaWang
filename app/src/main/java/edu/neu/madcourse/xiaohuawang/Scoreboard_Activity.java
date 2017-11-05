package edu.neu.madcourse.xiaohuawang;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Scoreboard_Activity extends Activity {
    private DatabaseReference mDatabase;
    private String token;
    private ViewGroup vg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_scoreboard);
        vg = (ViewGroup) findViewById(R.id.activity_scoreboard);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        token = FirebaseInstanceId.getInstance().getToken();

        //check if the device is offline
        if(token.isEmpty()){
            TextView tv = new TextView(this);
            tv.setText("We can't get your device toekn number");
            vg.addView(tv);
            return;
        }else{
            Pattern p = Pattern.compile("\"(.+?)\"");
            Matcher m = p.matcher(token);
            ArrayList<String> strs = new ArrayList<String>();
            while (m.find()) {
                strs.add(m.group(1));
            }
            token=strs.get(1);
        }
        System.out.println("my token is= "+token);

        //modify ui, give all the database information to ui
        mDatabase.child("scoreboardUser").child(token).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<HashMap<String, List<GamePlayer>>> t = new GenericTypeIndicator<HashMap<String, List<GamePlayer>>>() {};
                Map<String, List<GamePlayer>> myMap = dataSnapshot.getValue(t);
                if(myMap == null){
                    System.out.println("my map is null????????????????????");
                    myMap = new HashMap<>();
                }
                int i=0;
                System.out.println("keyset size= "+myMap.keySet().size());
                System.out.println("score user size= "+myMap.keySet().size());
                for(String key : myMap.keySet()) {
                    for(GamePlayer player : myMap.get(key)){
                        View v = LayoutInflater.from(getApplicationContext()).inflate(R.layout.score_item, null);
                        ((TextView)v.findViewById(R.id.username)).setText(key);
                        ((TextView)v.findViewById(R.id.userTime)).setText(player.time);
                        ((TextView)v.findViewById(R.id.userScore)).setText(player.phaseoneScore + "+" + (player.totalScore-player.phaseoneScore) + "=" + player.totalScore);
                        ((TextView)v.findViewById(R.id.userWord)).setText(player.highestScoreWord + ": " + player.highestScore);
                        vg.addView(v);
                        i++;
                    }
                }
                Log.d("UT3", "------------------scoreboard  text view-------------------");
                System.out.println("i= "+i);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}