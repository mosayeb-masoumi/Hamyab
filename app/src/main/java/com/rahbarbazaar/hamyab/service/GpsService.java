package com.rahbarbazaar.hamyab.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.rahbarbazaar.hamyab.R;
import com.rahbarbazaar.hamyab.network.ServiceProvider;
import com.rahbarbazaar.hamyab.utilities.Cache;
import com.rahbarbazaar.hamyab.utilities.GpsTracker;
import java.util.Timer;
import java.util.TimerTask;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GpsService extends Service {

    public static final long NOTIFY_INTERVAL = 7 * 1000; // 7 seconds
    // run on another Thread to avoid crash
    private Handler mHandler = new Handler();
    private Timer mTimer = null;

    private GpsTracker gpsTracker;
    String strLat, strLng;


    private String CHANNEL_ID = "channelId";
    private NotificationManager notifManager;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(this, "service is created", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

           // to run service in foreground
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String offerChannelName = "Service Channel";
            String offerChannelDescription= "Location Channel";
            int offerChannelImportance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel notifChannel = new NotificationChannel(CHANNEL_ID, offerChannelName, offerChannelImportance);
            notifChannel.setDescription(offerChannelDescription);
            notifManager = getSystemService(NotificationManager.class);
            notifManager.createNotificationChannel(notifChannel);

        }

        NotificationCompat.Builder sNotifBuilder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.app_icon)
                .setContentTitle("همادیت")
                .setContentText("سرویس ارسال لوکیشن فعال است");

        Notification servNotification = sNotifBuilder.build();

        startForeground(1, servNotification);

        ////////////////////////////////////////////////////////////////////////


        // cancel if already existed
        if(mTimer != null) {
            mTimer.cancel();
        }else{
            // recreate new
            mTimer = new Timer();
        }

        // schedule task
            mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(), 0, NOTIFY_INTERVAL);
//        return super.onStartCommand(intent, flags, startId);
        return START_STICKY;

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
        Toast.makeText(this, "service stop", Toast.LENGTH_SHORT).show();
    }

    class TimeDisplayTimerTask extends TimerTask {
        @Override
        public void run() {
            // run on another thread
            mHandler.post(() -> {
                // display toast
                Toast.makeText(getApplicationContext(), "service start",
                        Toast.LENGTH_SHORT).show();

                sendLatng();
            });
        }
    }

    private void sendLatng() {
        getLocation();


        String api_token = Cache.getString(GpsService.this,"access_token");

        com.rahbarbazaar.hamyab.network.Service service = new ServiceProvider(GpsService.this).getmService();
//        Call<Boolean> call = service.sendGPS(api_token,strLat,strLng,"1");
        Call<Boolean> call = service.sendGPS(api_token,strLat,strLng,"1");
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if(response.code()==200){
                    Toast.makeText(GpsService.this, ""+strLat+"  "+strLng, Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(GpsService.this, ""+getResources().getString(R.string.serverFaield), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(GpsService.this, ""+getResources().getString(R.string.connectionFaield), Toast.LENGTH_SHORT).show();
            }
        });
    }



    int a = 0;
        public void getLocation() {
            gpsTracker = new GpsTracker(this);
            if (gpsTracker.canGetLocation()) {
                double latitude = gpsTracker.getLatitude();
                double longitude = gpsTracker.getLongitude();
                strLat = (String.valueOf(latitude));
                strLng = (String.valueOf(longitude));
                // to handle getting gps in first calculate after turning on gps
                if(a < 2){
                    a ++;
                    getLocation();
                }
            } else {
                gpsTracker.showSettingsAlert();
            }
        }




}
