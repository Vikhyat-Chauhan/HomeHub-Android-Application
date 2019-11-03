package in.thenextmove.homehub;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.ArrayList;

import helpers.DBHelper;
import helpers.MQTTHelper;

public class MainActivity extends AppCompatActivity {

    DBHelper dbHelper;
    boolean Button_1_State,Button_2_State;
    private TextView devicename_TEXTVIEW, status_TEXTVIEW, message_TEXTVIEW;
    private EditText chipid_EDITTEXT,username_EDITTEXT,password_EDITTEXT;
    BottomNavigationView navView;
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    status_TEXTVIEW.setText(R.string.title_home);
                    getdata();
                    return true;
                case R.id.navigation_dashboard:
                    status_TEXTVIEW.setText(R.string.title_dashboard);
                    ArrayList<String> chipid = dbHelper.getchipid();
                    for(int i = 0; i<chipid.size(); i++) {
                        Log.i("Debug", chipid.get(i));
                    }
                    return true;
                case R.id.navigation_notifications:
                    status_TEXTVIEW.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        status_TEXTVIEW = findViewById(R.id.status_Textview);
        devicename_TEXTVIEW = findViewById(R.id.devicename_Textview);
        message_TEXTVIEW = findViewById(R.id.message_Textview);
        chipid_EDITTEXT = findViewById(R.id.chipid_editText);
        username_EDITTEXT = findViewById(R.id.username_editText);
        password_EDITTEXT = findViewById(R.id.password_editText);

        dbHelper = new DBHelper(this, null, null, 1);
    }

    protected void getdata(){
        Log.i("Debug","Getting data");
        String chipid = chipid_EDITTEXT.getText().toString();
        String username = username_EDITTEXT.getText().toString();
        String password = password_EDITTEXT.getText().toString();
        if(username.length() >0 & password.length() >0 ) {
            dbHelper.addCredentials(chipid,username,password);
            Log.i("H.O.M.E", "Database Created");
            //Intent myIntent = new Intent(this,SelectHome.class);
            //startActivity(myIntent);
        }
        else{
            if(username.length()<=0){
                Toast.makeText(getApplicationContext(), "Enter Username", Toast.LENGTH_SHORT).show();
            }
            if(password.length()<=0) {
                Toast.makeText(getApplicationContext(), "Enter Password", Toast.LENGTH_SHORT).show();
            }
            chipid_EDITTEXT.setText("");
            username_EDITTEXT.setText("");
            password_EDITTEXT.setText("");
        }
    }
}

