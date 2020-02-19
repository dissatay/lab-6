package com.example.bindservice;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private DemoBindService demoBindService;
    private boolean isBound;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreferences = getSharedPreferences("user_info", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
        Intent intent = new Intent(this, DemoBindService.class);
        intent.putExtra("name", "Tommy");
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        //startService(intent);

    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            DemoBindService.LocalService localService = (DemoBindService.LocalService) service;
            demoBindService = localService.getService();

            //save to shared preferences
            //demoBindService.saveString(sharedPreferences, "Tommy");
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;

        }
    };
    public void setFirstMessage( View view ){
        TextView textView = (TextView) findViewById(R.id.text);
        textView.setText( demoBindService.getFirstMessage() );
    }

    public void setSecondMessage( View view ){
        TextView textView = (TextView) findViewById(R.id.text);
        textView.setText( demoBindService.getSecondMessage() );
    }

    public void displayName( View view ){

        TextView textView = (TextView) findViewById(R.id.text);
        String name = demoBindService.getName();
        textView.setText( name);
        if (name != "") {
            if(isBound) {
                unbindService(serviceConnection);
                isBound = false;
            }
            System.out.println("NEED TO STOP INTENT");
            Intent intent = new Intent(MainActivity.this, DemoBindService.class);
            stopService(intent);
        }
    }

    @Override
    protected void onDestroy(){
        if(isBound){
            unbindService( serviceConnection);
            isBound = false;
        }
        System.out.println("STOPPING MAIN ACTIVITY");
        super.onDestroy();
    }
}
