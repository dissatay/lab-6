package com.example.bindservice;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

public class DemoBindService extends Service {
    private final IBinder iBinder = new LocalService();
    SharedPreferences sharedPreferences;
    String name;
    public DemoBindService() {
    }

    final class MyThread implements Runnable {
        int startId;
        public MyThread(int startId) {
            this.startId = startId;

        }
        @Override
        public void run() {
            synchronized (this) {
                try {
                    wait(10000);
                    //insert string into sf

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("name", name);
                    editor.commit();
                    System.out.println("NAME WAS SAVED");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                stopSelf(startId);
            }

        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        saveString(getApplicationContext().getSharedPreferences("user_info", MODE_PRIVATE), intent.getStringExtra("name"));
        startService(intent);
        return iBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(DemoBindService.this, "Service Started", Toast.LENGTH_SHORT).show();
        Thread thread = new Thread(new MyThread(startId));
        thread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    public void onDestroy() {
        Toast.makeText(DemoBindService.this, "Service Stopped", Toast.LENGTH_SHORT).show();
        System.out.println("STOPPING SERVICE");

        super.onDestroy();
    }


/*    public DemoBindService getService() {
        return new DemoBindService();
    }*/

    class LocalService extends Binder {
        public DemoBindService getService() {
            return DemoBindService.this;
        }
    }


    public String getFirstMessage() {
        return "This is the first message";
    }

    public String getSecondMessage() {
        return "This is the second message";
    }

    public void saveString(SharedPreferences sf, String name) {
        sharedPreferences = sf;
        this.name = name;
    }

    public String getName() {
        return sharedPreferences.getString("name", "");
    }

}
