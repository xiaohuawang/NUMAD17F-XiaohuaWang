package edu.neu.madcourse.xiaohuawang;

import android.content.Context;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import edu.neu.madcourse.xiaohuawang.R;

/**
 * Created by yangyangyy on 9/9/17.
 */

public class Aty1 extends AppCompatActivity {

    private Button buttonClose;
    private TextView uniqueID;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("XiaohuaWang");//set the title bar
        setContentView(R.layout.aty1);

        buttonClose = (Button) findViewById(R.id.buttonClose);
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ((TextView) findViewById(R.id.uniqueId)).setText(generateId());

    }

    public String generateId() {

//        String deviceId = android.provider.Settings.Secure.getString(this.getContentResolver(),
//                Settings.Secure.ANDROID_ID);

        TelephonyManager tm=(TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId=tm.getDeviceId();
        return deviceId;

    }

}
