package sample.laiguanyu.iotdemo;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    DatabaseReference databaseReference;
    EditText edLcd;
    ImageButton ibg,iby,ibr;
    ImageView ivSwitch;
    TextView tvtemp,tvRfid;
    Boolean ledGreen,ledYellow,ledRed;
    LinearLayout bgLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewId();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("onDataChange","onDataChange");
                ledSetup(dataSnapshot);
                mySwitch(dataSnapshot);
                myTemperature(dataSnapshot);
                myRfid(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    //boolean ledSetupOnce = true;
    void ledSetup(DataSnapshot dataSnapshot){
        ledGreen = dataSnapshot.child("green").child("switch").getValue(boolean.class);
        ledYellow = dataSnapshot.child("yellow").child("switch").getValue(boolean.class);
        ledRed = dataSnapshot.child("red").child("switch").getValue(boolean.class);
        if(ledGreen){
            ibg.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
        }else{
            ibg.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
        }
        if(ledYellow){
            iby.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
        }else{
            iby.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
        }
        if(ledRed){
            ibr.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
        }else{
            ibr.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
        }
    }
    void mySwitch(DataSnapshot dataSnapshot){
        if(dataSnapshot.child("switch").getValue(Boolean.class)){
            ivSwitch.setImageResource(R.drawable.open);
        }else {
            ivSwitch.setImageResource(R.drawable.close);
        }
    }
    void myTemperature(DataSnapshot dataSnapshot){
        String temp = dataSnapshot.child("temperature").getValue(String.class);
        tvtemp.setText(temp+getResources().getString(R.string.temperature));
    }
    void myRfid(DataSnapshot dataSnapshot){
        //避免其他資料更動時執行myAlert();
        Boolean change = dataSnapshot.child("rfid").child("change").getValue(Boolean.class);
        if(change){
            String rfid = dataSnapshot.child("rfid").child("data").getValue(String.class);
            if(rfid.equals("illegitimate")){
                tvRfid.setText("illegitimate user");
                myAlert();
            }else{
                tvRfid.setText(rfid);
            }
            databaseReference.child("rfid").child("change").setValue(false);

        }
    }




    void findViewId(){
        edLcd = (EditText)findViewById(R.id.editText);
        ibg = (ImageButton)findViewById(R.id.imageButton);
        iby = (ImageButton)findViewById(R.id.imageButton2);
        ibr = (ImageButton)findViewById(R.id.imageButton3);
        ivSwitch = (ImageView)findViewById(R.id.imageView);
        tvtemp = (TextView)findViewById(R.id.textView);
        tvRfid = (TextView)findViewById(R.id.textView2);
        bgLayout = (LinearLayout)findViewById(R.id.linearLayout);
    }

    public void btnSend(View v){
        String lcdInput = edLcd.getText().toString();
        databaseReference.child("lcd").child("data").setValue("LCD"+lcdInput);
        databaseReference.child("lcd").child("change").setValue(true);
        edLcd.setText("");
    }
    public void btnGreen(View v){
        if(ledGreen){//亮變暗
            ibg.setBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
        }else{
            ibg.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
        }
        ledGreen = !ledGreen;
        databaseReference.child("green").child("switch").setValue(ledGreen);
        databaseReference.child("green").child("change").setValue(true);
    }
    public void btnYellow(View v){
        if(ledYellow){//亮變暗
            iby.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
        }else{
            iby.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light));
        }
        ledYellow = !ledYellow;
        databaseReference.child("yellow").child("switch").setValue(ledYellow);
        databaseReference.child("yellow").child("change").setValue(true);
    }
    public void btnRed(View v){
        if(ledRed){//亮變暗
            ibr.setBackgroundColor(getResources().getColor(android.R.color.holo_red_dark));
        }else{
            ibr.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
        }
        ledRed = !ledRed;
        databaseReference.child("red").child("switch").setValue(ledRed);
        databaseReference.child("red").child("change").setValue(true);

    }
    //boolean alerting = false;
    void myAlert(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                for(int i : new int[]{1,2,3}) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            bgLayout.setBackgroundColor(Color.RED);
                        }
                    });
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            bgLayout.setBackgroundColor(Color.WHITE);
                        }
                    });
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

    }

}
