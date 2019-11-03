package in.thenextmove.homehub;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Calendar;

import helpers.MQTTHelper;

public class DevicechangeActivity extends AppCompatActivity {

    //get these from calling activity
    int switchnumber;
    String switchtype;
    String switchname;
    String devicename;
    String messagetext;
    MQTTHelper mqttHelper;
    String Publish_Topic;
    String serverUri;
    String clientId;
    String subscriptionTopic;
    String username;
    String password;
    //internal variables
    boolean switchtemponstatus,switchtempoffstatus,switchtimeonstatus,switchtimeoffstatus;
    int device1tempon,device1tempoff,device2timeon,device2timeoff;
    //widgets
    private TimePickerDialog picker;
    private TextView statusTextview,messageTextview,devicenameTextview,buttontimeonTextview,buttontimeoffTextview,buttontemponTextview,buttontempoffTextview;
    private EditText devicenameEdittext;
    private Switch devicetypeSwitch;
    private ImageView buttontimeonImageview,buttontimeoffImageview,buttontemponImageview,buttontempoffImageview,buttonsaveImageview;
    private ConstraintLayout buttontimeonLayout,buttontimeoffLayout,buttontemponLayout,buttontempoffLayout;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    return true;
                case R.id.navigation_dashboard:
                    return true;
                case R.id.navigation_notifications:
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
        if(b != null) {
            switchnumber = b.getInt("switchnumber");
            switchname = b.getString("switchname");
            switchtype = b.getString("switchtype");
            devicename = b.getString("devicename");
            messagetext = b.getString("messagetext");
            Publish_Topic = b.getString("Publish_Topic");
            serverUri = b.getString("serverUri");
            clientId = b.getString("clientId");
            subscriptionTopic = b.getString("subscriptionTopic");
            username = b.getString("username");
            password = b.getString("password");
        }
        setContentView(R.layout.activity_devicechange);
        // Remove top bar
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }
        //TextViews
        statusTextview = findViewById(R.id.status_Textview);
        devicenameTextview = findViewById(R.id.devicename_Textview);
        messageTextview = findViewById(R.id.message_Textview);
        buttontimeonTextview = findViewById(R.id.buttontimeon_Textview);
        buttontimeoffTextview = findViewById(R.id.buttontimeoff_Textview);
        buttontemponTextview = findViewById(R.id.buttontempon_Textview);
        buttontempoffTextview = findViewById(R.id.buttontempoff_Textview);
        //Edittext
        devicenameEdittext = findViewById(R.id.devicename_Edittext);
        //Switch
        devicetypeSwitch = findViewById(R.id.devicetype_Switch);
        //ImageView
        buttontimeonImageview = findViewById(R.id.buttontimeon_Imageview);
        buttontimeoffImageview = findViewById(R.id.buttontimeoff_Imageview);
        buttontemponImageview = findViewById(R.id.buttontempon_Imageview);
        buttontempoffImageview = findViewById(R.id.buttontempoff_Imageview);
        buttonsaveImageview = findViewById(R.id.save_Button);
        //Constraint Layout
        buttontimeonLayout = findViewById(R.id.buttontimeon_Layout);
        buttontimeoffLayout = findViewById(R.id.buttontimeoff_Layout);
        buttontemponLayout = findViewById(R.id.buttontempon_Layout);
        buttontempoffLayout = findViewById(R.id.button_tempoff_Layout);
        //bottom Nav Views
        BottomNavigationView navView = findViewById(R.id.nav_view);
        //Set all layout fixed Display values
        devicenameTextview.setText(devicename);
        devicenameEdittext.setText(switchname);
        messageTextview.setText(messagetext);
        if(switchtype == "Light"){Log.d("alpha","light");
            devicetypeSwitch.setText("Light");
            devicetypeSwitch.setChecked(true);
        }
        if(switchtype == "Fan"){Log.d("alpha","fan");
            devicetypeSwitch.setText("Fan");
            devicetypeSwitch.setChecked(false);
        }
        //Switch listeners
        //Time On Switch
        buttontimeonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchtimeonstatus = !switchtimeonstatus;
                buttontimeonLayout.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.white));
                buttontimeonTextview.setText("On");
                buttontimeonImageview.setImageResource(R.drawable.ic_time_on);
                try {
                    mqttHelper.sendData(Publish_Topic,"<dse>"+switchnumber+"X");
                } catch (MqttException e) {
                    e.printStackTrace();
                }
                if(switchtimeonstatus){
                    try {
                        mqttHelper.sendData(Publish_Topic,"<TIO>0X");
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    buttontimeonLayout.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.grey));
                    buttontimeonTextview.setText("Off");
                    buttontimeonImageview.setImageResource(R.drawable.ic_time_off);
                    try {
                        mqttHelper.sendData(Publish_Topic,"<dse>"+switchnumber+"X");
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                    try {
                        mqttHelper.sendData(Publish_Topic,"<TIO>1X");
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        buttontimeonLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);
                // time picker dialog
                picker = new TimePickerDialog(DevicechangeActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                                    @Override
                                    public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                        try {
                                            mqttHelper.sendData(Publish_Topic,"<dse>"+switchnumber+"X");
                                        } catch (MqttException e) {
                                            e.printStackTrace();
                                        }
                                        try {
                                            mqttHelper.sendData(Publish_Topic,"<tho>"+sHour+"X");
                                        } catch (MqttException e) {
                                            e.printStackTrace();
                                        }
                                        try {
                                            mqttHelper.sendData(Publish_Topic,"<tmo>"+sMinute+"X");
                                        } catch (MqttException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }, hour, minutes, true);
                        picker.show();
                return false;
            }
        });
        //Time OFF Switch
        buttontimeoffLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchtimeoffstatus = !switchtimeoffstatus;
                buttontimeoffLayout.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.white));
                buttontimeoffTextview.setText("On");
                buttontimeoffImageview.setImageResource(R.drawable.ic_time_on);
                try {
                    mqttHelper.sendData(Publish_Topic,"<dse>"+switchnumber+"X");
                } catch (MqttException e) {
                    e.printStackTrace();
                }
                if(switchtimeoffstatus){

                    try {
                        mqttHelper.sendData(Publish_Topic,"<TIF>0X");
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    buttontimeoffLayout.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.grey));
                    buttontimeoffTextview.setText("Off");
                    buttontimeoffImageview.setImageResource(R.drawable.ic_time_off);
                    try {
                        mqttHelper.sendData(Publish_Topic,"<TIF>1X");
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        buttontimeoffLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final Calendar cldr = Calendar.getInstance();
                int hour = cldr.get(Calendar.HOUR_OF_DAY);
                int minutes = cldr.get(Calendar.MINUTE);
                // time picker dialog
                picker = new TimePickerDialog(DevicechangeActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker tp, int sHour, int sMinute) {
                                try {
                                    mqttHelper.sendData(Publish_Topic,"<dse>"+switchnumber+"X");
                                } catch (MqttException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    mqttHelper.sendData(Publish_Topic,"<thf>"+sHour+"X");
                                } catch (MqttException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    mqttHelper.sendData(Publish_Topic,"<tmf>"+sMinute+"X");
                                } catch (MqttException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, hour, minutes, true);
                picker.show();
                return false;
            }
        });

        //Temperature ON Switch
        buttontemponLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchtemponstatus = !switchtemponstatus;
                buttontemponLayout.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.white));
                buttontemponTextview.setText("On");
                buttontemponImageview.setImageResource(R.drawable.ic_fader_on);
                try {
                    mqttHelper.sendData(Publish_Topic,"<dse>"+switchnumber+"X");
                } catch (MqttException e) {
                    e.printStackTrace();
                }
                if(switchtemponstatus){
                    try {
                        mqttHelper.sendData(Publish_Topic,"<TEO>0X");
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    buttontemponLayout.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.grey));
                    buttontemponTextview.setText("Off");
                    buttontemponImageview.setImageResource(R.drawable.ic_fader_off);
                    try {
                        mqttHelper.sendData(Publish_Topic,"<TEO>1X");
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        buttontemponLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });

        //Temperature OFF Switch
        buttontempoffLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttontempoffLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        switchtempoffstatus = !switchtempoffstatus;
                        buttontempoffLayout.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.white));
                        buttontempoffTextview.setText("On");
                        buttontempoffImageview.setImageResource(R.drawable.ic_fader_on);
                        try {
                            mqttHelper.sendData(Publish_Topic,"<dse>"+switchnumber+"X");
                        } catch (MqttException e) {
                            e.printStackTrace();
                        }
                        if(switchtempoffstatus){
                            try {
                                mqttHelper.sendData(Publish_Topic,"<TEF>0X");
                            } catch (MqttException e) {
                                e.printStackTrace();
                            }
                        }
                        else{
                            buttontempoffLayout.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.grey));
                            buttontempoffTextview.setText("Off");
                            buttontempoffImageview.setImageResource(R.drawable.ic_fader_off);
                            try {
                                mqttHelper.sendData(Publish_Topic,"<TEF>1X");
                            } catch (MqttException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        });
        buttontempoffLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        });

        devicetypeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    devicetypeSwitch.setText("Light");
                    switchtype = "Light";
                }
                else{
                    devicetypeSwitch.setText("Fan");
                    switchtype = "Fan";
                }
            }
        });
        buttonsaveImageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DevicechangeActivity.this, DeviceActivity.class);
                Bundle b = new Bundle();
                switchname = devicenameEdittext.getText().toString();
                switchtype = devicetypeSwitch.getText().toString();
                b.putInt("switchnumber",switchnumber);
                b.putString("switchname",switchname);
                b.putString("switchtype",switchtype);
                intent.putExtras(b); //Put your id to your next Intent
                finish();
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mqttHelper = new MQTTHelper(getApplicationContext(),serverUri,clientId,username,password,subscriptionTopic,Publish_Topic);
        //mqtt stuff
        mqttHelper.mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                Log.e("Debug"," Device Connected");
                statusTextview.setText("Online");
                statusTextview.setBackgroundResource(R.drawable.rounded_background_blue);
            }

            @Override
            public void connectionLost(Throwable cause) {
                Log.e("Debug","Offline");
                statusTextview.setText("Offline");
                statusTextview.setBackgroundResource(R.drawable.rounded_background_red);
            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                Log.w("Debug",mqttMessage.toString());
                String message = mqttMessage.toString();
                String command = message.substring(1,4);
                String value = message.substring(5,message.indexOf('X'));
                if(command.contentEquals("TEO")){
                    switchtemponstatus = value.equalsIgnoreCase("0");//Returns true if value is 0(which means device on on in esp
                    if(switchtemponstatus){ //true i.e 0
                        buttontemponLayout.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.white));
                        buttontemponTextview.setText("On");
                        buttontemponImageview.setImageResource(R.drawable.ic_fader_on);
                    }
                    else { // false i.e 1
                        buttontemponLayout.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.grey));
                        buttontemponTextview.setText("Off");
                        buttontemponImageview.setImageResource(R.drawable.ic_fader_off);
                    }
                }
                if(command.contentEquals("TEF")){
                    switchtempoffstatus = value.equalsIgnoreCase("0");//Returns true if value is 0(which means device on on in esp
                    if(switchtempoffstatus){ //true i.e 0
                        buttontempoffLayout.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.white));
                        buttontempoffTextview.setText("On");
                        buttontempoffImageview.setImageResource(R.drawable.ic_fader_on);
                    }
                    else { // false i.e 1
                        buttontempoffLayout.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.grey));
                        buttontempoffTextview.setText("Off");
                        buttontempoffImageview.setImageResource(R.drawable.ic_fader_off);
                    }
                }
                if(command.contentEquals("TIO")){
                    switchtimeonstatus = value.equalsIgnoreCase("0");//Returns true if value is 0(which means device on on in esp
                    if(switchtimeonstatus){ //true i.e 0
                        buttontimeonLayout.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.white));
                        buttontimeonTextview.setText("On");
                        buttontimeonImageview.setImageResource(R.drawable.ic_time_on);
                    }
                    else { // false i.e 1
                        buttontimeonLayout.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.grey));
                        buttontimeonTextview.setText("Off");
                        buttontimeonImageview.setImageResource(R.drawable.ic_time_off);
                    }
                }
                if(command.contentEquals("TIF")){
                    switchtimeoffstatus = value.equalsIgnoreCase("0");//Returns true if value is 0(which means device on on in esp
                    if(switchtimeoffstatus){ //true i.e 0
                        buttontimeoffLayout.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.white));
                        buttontimeoffTextview.setText("On");
                        buttontimeoffImageview.setImageResource(R.drawable.ic_time_on);
                    }
                    else { // false i.e 1
                        Log.e("Debug","Device 1 OFF");
                        buttontimeoffLayout.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.grey));
                        buttontimeoffTextview.setText("Off");
                        buttontimeoffImageview.setImageResource(R.drawable.ic_time_off);
                    }
                }
                if(command.contentEquals("TEM")){
                    if(Integer.parseInt(value) < 50) {
                        //temperatureText.setText(value);
                    }
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }
}
