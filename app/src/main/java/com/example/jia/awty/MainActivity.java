package com.example.jia.awty;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
    int time;
    PendingIntent alarmIntent = null;
    String message;
    String phoneNo;
    AlarmManager am = null;
    boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText editText1 = (EditText) findViewById(R.id.message);

        final Button button = (Button) findViewById(R.id.button);
        button.setText("Start");
       final EditText editText2 = (EditText) findViewById(R.id.phoneNo);


        final EditText editText3 = (EditText) findViewById(R.id.time);
        editText3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    time = Integer.parseInt(editText3.getText().toString());
                } catch (NumberFormatException e) {
                    Toast.makeText(MainActivity.this, "Time must be a positive integer.", Toast.LENGTH_SHORT).show();
                }
                if (time <= 0) {
                    Toast.makeText(MainActivity.this, "Time must be a positive integer.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag) {
                    message = editText1.getText().toString();
                    phoneNo = editText2.getText().toString();
                    if (message.length() != 0 && phoneNo .length() != 0 && time > 0) {
                        BroadcastReceiver alarmReceiver = new BroadcastReceiver() {
                            @Override
                            public void onReceive(Context context, Intent intent) {
                                sendMsg(phoneNo, message);
                                Log.i("SMS","message sent.");


                            }
                        };
                        button.setText("Stop");
                        registerReceiver(alarmReceiver, new IntentFilter("Send"));
                        am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                        Intent i = new Intent();
                        i.setAction("Send");
                        alarmIntent = PendingIntent.getBroadcast(MainActivity.this, 0, i, 0);
                        am.setRepeating(AlarmManager.RTC, System.currentTimeMillis(), 1000 * 60 * time, alarmIntent);
                        flag = false;
                    }
                } else{
                    if (am != null) {
                        am.cancel(alarmIntent);
                        alarmIntent.cancel();
                        button.setText("Start");
                        flag = true;
                    }
                }
            }
        });

    }

    private void sendMsg(String number, String message) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(number, null, message, null, null);
    }


    @Override
    public void onDestroy(){
        super.onDestroy();
        if (am != null) {
            am.cancel(alarmIntent);
            alarmIntent.cancel();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
