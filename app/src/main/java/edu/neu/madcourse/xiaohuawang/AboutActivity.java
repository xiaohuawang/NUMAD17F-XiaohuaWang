package edu.neu.madcourse.xiaohuawang;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import edu.neu.madcourse.xiaohuawang.R;

/**
 * Created by yangyangyy on 9/9/17.
 */

public class AboutActivity extends AppCompatActivity {

    private static final int PERMISSIONS_REQUEST_READ_PHONE_STATE = 999;
    private Button buttonClose;
    private TextView uniqueID;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("XiaohuaWang");//set the title bar
        setContentView(R.layout.activity_about);

        buttonClose = (Button) findViewById(R.id.buttonClose);
        buttonClose.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                finish();
            }
        });

        if (checkSelfPermission(Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},
                    PERMISSIONS_REQUEST_READ_PHONE_STATE);
        } else {
            String deviceId = generateId();
            ((TextView) findViewById(R.id.uniqueId)).setText(deviceId);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_PHONE_STATE
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            generateId();
            String deviceId = generateId();
            ((TextView) findViewById(R.id.uniqueId)).setText(deviceId);
        }
    }

    public String generateId() {

//        String deviceId = android.provider.Settings.Secure.getString(this.getContentResolver(),
//                Settings.Secure.ANDROID_ID);

        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String deviceId = tm.getDeviceId();
        if (deviceId == null) {
            deviceId = "no id exist";
        }
        return deviceId;

    }

}