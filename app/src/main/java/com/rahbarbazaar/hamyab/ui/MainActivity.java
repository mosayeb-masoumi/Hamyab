package com.rahbarbazaar.hamyab.ui;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;
import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.rahbarbazaar.hamyab.R;
import com.rahbarbazaar.hamyab.models.LoginModel;
import com.rahbarbazaar.hamyab.network.Service;
import com.rahbarbazaar.hamyab.network.ServiceProvider;
import com.rahbarbazaar.hamyab.service.GpsService;
import com.rahbarbazaar.hamyab.utilities.Cache;
import com.rahbarbazaar.hamyab.utilities.CustomBaseActivity;
import com.rahbarbazaar.hamyab.utilities.GeneralTools;

import java.util.Objects;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends CustomBaseActivity implements View.OnClickListener {

    GeneralTools tools;
    BroadcastReceiver connectivityReceiver = null;
    boolean doubleBackToExitPressedOnce = false;
    DrawerLayout drawer_layout_home;
    ImageView image_drawer;
    LinearLayout linear_drawer_about, linear_drawer_invite_friend;
//    Switch btn_switch;

    Button btn_start_service, btn_stop_service;



//    String switch_check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //check network broadcast reciever
        tools = GeneralTools.getInstance();
        connectivityReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                tools.doCheckNetwork(MainActivity.this, findViewById(R.id.drawer_layout_home));
            }
        };


//        stopService(new Intent(this,GpsService.class));

        initView();




    }

    private void check_service_state() {

        String service_state = Cache.getString(MainActivity.this, "service_state");
        if (service_state == null) {
        } else if (service_state.equals("enable")) {
            btn_stop_service.setVisibility(View.VISIBLE);
            btn_start_service.setVisibility(View.GONE);
        } else if (service_state.equals("disable")) {
            btn_start_service.setVisibility(View.VISIBLE);
            btn_stop_service.setVisibility(View.GONE);
        }

    }

    private void initView() {
        image_drawer = findViewById(R.id.image_drawer);
        drawer_layout_home = findViewById(R.id.drawer_layout_home);
        linear_drawer_about = findViewById(R.id.linear_drawer_about);
        linear_drawer_invite_friend = findViewById(R.id.linear_drawer_invite_friend);
        btn_start_service = findViewById(R.id.btn_start_service);
        btn_stop_service = findViewById(R.id.btn_stop_service);
//        btn_switch = findViewById(R.id.btn_switch);
        image_drawer.setOnClickListener(this);
        linear_drawer_about.setOnClickListener(this);
        linear_drawer_invite_friend.setOnClickListener(this);
        btn_start_service.setOnClickListener(this);
        btn_stop_service.setOnClickListener(this);
//        btn_switch.setOnCheckedChangeListener(this);

        check_service_state();
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.image_drawer:
                drawer_layout_home.openDrawer(Gravity.END);
                break;
            case R.id.linear_drawer_about:
                Toast.makeText(this, "about", Toast.LENGTH_SHORT).show();
                break;
            case R.id.linear_drawer_invite_friend:
                Toast.makeText(this, "invite", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btn_start_service:
                turnOnGpsStartSrvice();
                break;

            case R.id.btn_stop_service:
                stopService(new Intent(MainActivity.this, GpsService.class));
                Cache.setString(MainActivity.this, "service_state", "disable");
                btn_start_service.setVisibility(View.VISIBLE);
                btn_stop_service.setVisibility(View.GONE);
                break;


        }

    }

    private void turnOnGpsStartSrvice() {

        if (hasLocationPermission()) {
            if (checkGpsON()) {
//                startService(new Intent(MainActivity.this, GpsService.class));
                // ContextCompat choose the best method depend of different ApiAndroid
                ContextCompat.startForegroundService(MainActivity.this,new Intent(this, GpsService.class));
                Cache.setString(MainActivity.this, "service_state", "enable");
                btn_start_service.setVisibility(View.GONE);
                btn_stop_service.setVisibility(View.VISIBLE);
            } else {
                gpsDialog();
            }
        } else {
            askLocationPermission();
        }

    }



    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void askLocationPermission() {
        ActivityCompat.requestPermissions((MainActivity.this)
                , new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 3);
    }

    private boolean checkGpsON() {
        final LocationManager manager = (LocationManager) (MainActivity.this).getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private void gpsDialog() {
        //     show waiting AVI
        Toast.makeText(this, "برای شروع لازم است GPS خود را روشن نمایید, صبور باشید ...", Toast.LENGTH_SHORT).show();
        LocationRequest mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(1000)
                .setNumUpdates(2);

        final LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        builder.setNeedBle(true);
        SettingsClient client = LocationServices.getSettingsClient(this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(this, locationSettingsResponse -> {
            hasLocationPermission();
            turnOnGpsStartSrvice();

        });
        task.addOnFailureListener(this, e -> {
            if (e instanceof ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    resolvable.startResolutionForResult(this,
                            12);
                } catch (IntentSender.SendIntentException e1) {

                    e.printStackTrace();
                }
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 3) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                turnOnGpsStartSrvice();
            } else {
                Toast.makeText(this, "نیاز به اجازه ی دسترسی لوکیشن", Toast.LENGTH_SHORT).show();
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 12) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
               turnOnGpsStartSrvice();
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        drawer_layout_home.closeDrawer(Gravity.END);
        registerReceiver(connectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));


    }


    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(connectivityReceiver);
    }

    @Override
    public void onBackPressed() {

        if (drawer_layout_home.isDrawerOpen(Gravity.END)) {
            drawer_layout_home.closeDrawers();
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                exitApp();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, R.string.text_double_click_exit, Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
        }

    }

    private void exitApp() {
        finish();
        startActivity(new Intent(Intent.ACTION_MAIN).
                addCategory(Intent.CATEGORY_HOME).
                setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finishAndRemoveTask();
        }
        Process.killProcess(Process.myPid());
        super.finish();
    }


}
