package com.example.quanto.gatsby;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.TextView;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import java.net.URISyntaxException;

public class MainActivity extends Activity implements SensorEventListener {
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("https://socket-android.glitch.me");
        } catch (URISyntaxException e) {}
    }

    private SensorManager sensorManager;
    double ax,ay,az;   // these are the accelerations in x,y and z
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSocket.connect();
        sensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 1000000);
    }
    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {
    }

    private void attemptSend() {
        String message = "x-axis: "+String.format("%1$,.2f", ax)+" y-axis: "+
        String.format("%1$,.2f", ay)+" z-axis: "+String.format("%1$,.2f", az);
        if (TextUtils.isEmpty(message)) {
            return;
        }
        mSocket.emit("new message", message);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType()==Sensor.TYPE_ACCELEROMETER){
            ax=event.values[0];
            ay=event.values[1];
            az=event.values[2];
        }
        TextView textX = (TextView)findViewById(R.id.x_axis);
        TextView textY = (TextView)findViewById(R.id.y_axis);
        TextView textZ = (TextView)findViewById(R.id.z_axis);
        textX.setText(String.format("%1$,.2f", ax));
        textY.setText(String.format("%1$,.2f", ay));
        textZ.setText(String.format("%1$,.2f", az));
        attemptSend();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();

        mSocket.disconnect();
    }
}
