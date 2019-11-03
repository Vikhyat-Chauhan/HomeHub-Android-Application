package in.thenextmove.homehub;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import helpers.MQTTHelper;

public class DeviceActivity extends AppCompatActivity {
    //get these from calling activity
    String devicename = "Bedroom";
    String messagetext = "Good Evening";
    String switch1name = "Fan";
    String switch2name = "Light";
    String switch1type = "Fan";
    String switch2type = "Light";
    int recievedswitchnumber = 100;
    String recievedswitchname, recievedswitchtype;
    //MQTT Strings
    MQTTHelper mqttHelper;
    String deviceID = "";
    String Publish_Topic = "374104ESP";//"1750986ESP";
    final String serverUri = "tcp://api.sensesmart.in:1883";//"tcp://m12.cloudmqtt.com:12233";
    String clientId = "ExampleAndroidClient";
    String subscriptionTopic = "374104/+"; //"4059933ESP/+";//"374104/+"; //"1675098/+";;
    final String username = "global";
    final String password = "p_global";

    boolean Button_1_State, Button_2_State;

    private TextView devicenameText, statusText, messageText, button1stateText, button2stateText, temperatureText, switch1nameText, switch2nameText;
    private ImageView button1stateImage, button2stateImage;
    private ConstraintLayout button1Layout;
    private ConstraintLayout button2Layout;
    FragmentManager fragmentManager = getSupportFragmentManager();
    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    statusText.setText(R.string.title_home);
                    Publish_Topic = deviceID + "ESP";
                    subscriptionTopic = deviceID + "/+";
                    return true;
                case R.id.navigation_dashboard:
                    statusText.setText(R.string.title_dashboard);
                    return true;
                case R.id.navigation_notifications:
                    statusText.setText(R.string.title_notifications);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //get data from starting activity
        Bundle b = getIntent().getExtras();
        if (b != null) {
            recievedswitchnumber = b.getInt("switchnumber");
            recievedswitchname = b.getString("switchname");
            recievedswitchtype = b.getString("switchtype");
        }

        setContentView(R.layout.activity_device);
        // In Activity's onCreate() for instance
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        BottomNavigationView navView = findViewById(R.id.nav_view);
        //Textviews
        devicenameText = findViewById(R.id.devicename_Textview);
        statusText = findViewById(R.id.status_Textview);
        messageText = findViewById(R.id.message_Textview);
        temperatureText = findViewById(R.id.room_temp);
        button1stateText = findViewById(R.id.button_1_state);
        button2stateText = findViewById(R.id.button_2_state);
        switch1nameText = findViewById(R.id.button_1_name);
        switch2nameText = findViewById(R.id.button_2_name);
        //ImageViews
        button1stateImage = findViewById(R.id.button_1_imageview);
        button2stateImage = findViewById(R.id.button_2_imageview);
        //Layouts
        button1Layout = findViewById(R.id.button_1_layout);
        button2Layout = findViewById(R.id.button_2_layout);
        //Edittext
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        Bundle bundle = new Bundle();
        bundle.putString("status", "blue");
        fragmentTransaction.commit();
        //changing values from incoming variable
        if (recievedswitchnumber != 100) {  //Recieved switch value from last activity
            if (recievedswitchnumber == 1) {
                switch1name = recievedswitchname;
                switch1type = recievedswitchtype;
            }
            if (recievedswitchnumber == 2) {
                switch2name = recievedswitchname;
                switch2type = recievedswitchtype;
            }
        }
        //Setting widget Strings
        devicenameText.setText(devicename);
        messageText.setText(messagetext);
        switch1nameText.setText(switch1name);
        switch2nameText.setText(switch2name);

        button1Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button_1_State = !Button_1_State;
                if (Button_1_State) {
                    button1Layout.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.white));
                    button1stateText.setText("On");
                    if (switch1type == "Light") {
                        button1stateImage.setImageResource(R.mipmap.ic_light_on);
                    }
                    if (switch1type == "Fan") {
                        button1stateImage.setImageResource(R.mipmap.ic_fan_on);
                    }
                    try {
                        mqttHelper.sendData(Publish_Topic, "<DE1>0X");
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                } else {
                    button1Layout.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.grey));
                    button1stateText.setText("Off");
                    if (switch1type == "Light") {
                        button1stateImage.setImageResource(R.mipmap.ic_light_off);
                    }
                    if (switch1type == "Fan") {
                        button1stateImage.setImageResource(R.mipmap.ic_fan_off);
                    }
                    try {
                        mqttHelper.sendData(Publish_Topic, "<DE1>1X");
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        button1Layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                try {
                    mqttHelper.sendData(Publish_Topic, "<dse>1X");
                } catch (MqttException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(DeviceActivity.this, DevicechangeActivity.class);
                Bundle b = new Bundle();
                b.putInt("switchnumber", 1);
                b.putString("switchname", switch1name);
                b.putString("switchtype", switch1type);
                b.putString("devicename", devicename);
                b.putString("messagetext", messagetext);
                b.putString("Publish_Topic", Publish_Topic);
                b.putString("serverUri", serverUri);
                b.putString("clientId", clientId);
                b.putString("subscriptionTopic", subscriptionTopic);
                b.putString("username", username);
                b.putString("password", password);
                intent.putExtras(b); //Put your id to your next Intent
                finish();
                startActivity(intent);
                return false;
            }
        });

        button2Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button_2_State = !Button_2_State;
                if (Button_2_State) {
                    button2Layout.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.white));
                    button2stateText.setText("On");
                    if (switch2type == "Light") {
                        button2stateImage.setImageResource(R.mipmap.ic_light_on);
                    }
                    if (switch2type == "Fan") {
                        button2stateImage.setImageResource(R.mipmap.ic_fan_on);
                    }
                    try {
                        mqttHelper.sendData(Publish_Topic, "<DE2>0X");
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                } else {
                    button2Layout.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.grey));
                    button2stateText.setText("Off");
                    if (switch2type == "Light") {
                        button2stateImage.setImageResource(R.mipmap.ic_light_off);
                    }
                    if (switch2type == "Fan") {
                        button2stateImage.setImageResource(R.mipmap.ic_fan_off);
                    }
                    try {
                        mqttHelper.sendData(Publish_Topic, "<DE2>1X");
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        button2Layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                try {
                    mqttHelper.sendData(Publish_Topic, "<dse>2X");
                } catch (MqttException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(DeviceActivity.this, DevicechangeActivity.class);
                Bundle b = new Bundle();
                b.putInt("switchnumber", 2);
                b.putString("switchname", switch2name);
                b.putString("switchtype", switch2type);
                b.putString("devicename", devicename);
                b.putString("messagetext", messagetext);
                b.putString("Publish_Topic", Publish_Topic);
                b.putString("serverUri", serverUri);
                b.putString("clientId", clientId);
                b.putString("subscriptionTopic", subscriptionTopic);
                b.putString("username", username);
                b.putString("password", password);
                intent.putExtras(b); //Put your id to your next Intent
                finish();
                startActivity(intent);
                return false;
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("Debug", "onstart");
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            // TODO: Consider calling
            //  ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        clientId = telephonyManager.getDeviceId();
        mqttHelper = new MQTTHelper(getApplicationContext(),serverUri,clientId,username,password,subscriptionTopic,Publish_Topic);
        //mqtt stuff
        mqttHelper.mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                Log.e("Debug"," Device Connected");
                statusText.setText("Online");
                statusText.setBackgroundResource(R.drawable.rounded_background_blue);
            }

            @Override
            public void connectionLost(Throwable cause) {
                Log.e("Debug","Offline");
                statusText.setText("Offline");
                statusText.setBackgroundResource(R.drawable.rounded_background_red);
            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                Log.w("Debug",mqttMessage.toString());
                String message = mqttMessage.toString();
                String command = message.substring(1,4);
                String value = message.substring(5,message.indexOf('X'));
                if(command.contentEquals("DE1")){
                    Button_1_State = value.equalsIgnoreCase("0");//Returns true if value is 0(which means device on on in esp
                    if(Button_1_State){ //true i.e 0
                        button1Layout.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.white));
                        button1stateText.setText("On");
                        if(switch1type == "Light") {
                            button1stateImage.setImageResource(R.mipmap.ic_light_on);
                        }
                        if(switch1type == "Fan"){
                            button1stateImage.setImageResource(R.mipmap.ic_fan_on);
                        }
                    }
                    else { // false i.e 1
                        button1Layout.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.grey));
                        button1stateText.setText("Off");
                        if(switch1type == "Light"){
                            button1stateImage.setImageResource(R.mipmap.ic_light_off);
                        }
                        if(switch1type == "Fan"){
                            button1stateImage.setImageResource(R.mipmap.ic_fan_off);
                        }
                    }
                }
                if(command.contentEquals("DE2")){
                    Button_2_State = value.equalsIgnoreCase("0");//Returns true if value is 0(which means device on on in esp
                    if(Button_2_State){ //true i.e 0
                        button2Layout.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.white));
                        button2stateText.setText("On");
                        if(switch2type == "Light") {
                            button2stateImage.setImageResource(R.mipmap.ic_light_on);
                        }
                        if(switch2type == "Fan"){
                            button2stateImage.setImageResource(R.mipmap.ic_fan_on);
                        }
                    }
                    else { // false i.e 1
                        button2Layout.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.grey));
                        button2stateText.setText("Off");
                        if(switch2type == "Light") {
                            button2stateImage.setImageResource(R.mipmap.ic_light_off);
                        }
                        if(switch2type == "Fan"){
                            button2stateImage.setImageResource(R.mipmap.ic_fan_off);
                        }
                    }
                }
                if(command.contentEquals("TEM")){
                    if(Integer.parseInt(value) < 50) {
                        temperatureText.setText(value);
                    }
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

}
