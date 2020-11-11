package com.example.uttam.driver_behaviour.sensors;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.example.uttam.driver_behaviour.R;

public class SensorActivity extends AppCompatActivity implements Orientation.Listener {

  private Orientation mOrientation;
  private AttitudeIndicator mAttitudeIndicator;

  TextView Roll , Pitch ,Xaxis , Yaxis , Zaxis , Yaw ;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_sensors);

    mOrientation = new Orientation(this);
    mAttitudeIndicator = (AttitudeIndicator) findViewById(R.id.attitude_indicator);
    Roll = (TextView)findViewById(R.id.roll);
    Pitch = (TextView)findViewById(R.id.pitch);
    Xaxis = (TextView)findViewById(R.id.xAcc);
    Yaxis = (TextView)findViewById(R.id.yAcc);
    Zaxis = (TextView)findViewById(R.id.zAcc);
    Yaw = (TextView)findViewById(R.id.yaw);



  }

  @Override
  protected void onStart() {
    super.onStart();
    mOrientation.startListening(this);
  }

  @Override
  protected void onStop() {
    super.onStop();
    mOrientation.stopListening();
  }

  @Override
  public void onOrientationChanged(float pitch, float roll ,float yaw ,  float[]accMatrix) {
    mAttitudeIndicator.setAttitude(pitch, roll);
    Roll.setText(" roll =  "+ String.valueOf(roll));
    Pitch.setText(" pitch =  "+String.valueOf(pitch));
    Xaxis.setText(" X_ACC =  " +String.valueOf(accMatrix[0]));
    Yaxis.setText(" Y_ACC =   "+String.valueOf(accMatrix[1]));
    Zaxis.setText(" Z_ACC =  "+String.valueOf(accMatrix[2]));
    Yaw .setText("ي yaw = "+String.valueOf((yaw)));

  }
}
