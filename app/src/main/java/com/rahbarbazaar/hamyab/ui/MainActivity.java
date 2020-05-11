package com.rahbarbazaar.hamyab.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rahbarbazaar.hamyab.R;
import com.rahbarbazaar.hamyab.controllers.ProjectItemInteraction;
import com.rahbarbazaar.hamyab.controllers.ProjectListAdapter;
import com.rahbarbazaar.hamyab.models.dashboard.Project;
import com.rahbarbazaar.hamyab.models.dashboard.ProjectList;
import com.rahbarbazaar.hamyab.service.GpsService;
import com.rahbarbazaar.hamyab.utilities.Cache;
import com.rahbarbazaar.hamyab.utilities.CustomBaseActivity;
import com.rahbarbazaar.hamyab.utilities.DialogFactory;
import com.rahbarbazaar.hamyab.utilities.GeneralTools;
import com.rahbarbazaar.hamyab.utilities.GpsTracker;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends CustomBaseActivity implements View.OnClickListener, ProjectItemInteraction {

    GeneralTools tools;
    BroadcastReceiver connectivityReceiver = null;
    boolean doubleBackToExitPressedOnce = false;
    DrawerLayout drawer_layout_home;
    ImageView image_drawer;
    LinearLayout linear_drawer_about, linear_drawer_invite_friend;
    TextView txt_exit;
    Button btn_start_service, btn_stop_service;
    RecyclerView recyclerView;
    ProjectListAdapter adapter;


    String strProject_id;
    String strRunning_project;


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

        initView();

        Intent intent = getIntent();
        ProjectList projectList = new ProjectList();
        projectList = intent.getParcelableExtra("projectList");
        Cache.setInt(MainActivity.this, "time", projectList.time);
        setRecyclerviwe(projectList);


    }


    private void setRecyclerviwe(ProjectList projectList) {

        List<Project> projects = new ArrayList<>();
        projects.addAll(projectList.project);

        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        adapter = new ProjectListAdapter(projects, MainActivity.this);
        adapter.setListener(this);  // important or else the app will crashed
        recyclerView.setAdapter(adapter);

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
        recyclerView = findViewById(R.id.rv_projectList);
        txt_exit = findViewById(R.id.txt_exit);
        image_drawer.setOnClickListener(this);
        linear_drawer_about.setOnClickListener(this);
        linear_drawer_invite_friend.setOnClickListener(this);
        btn_start_service.setOnClickListener(this);
        btn_stop_service.setOnClickListener(this);
        txt_exit.setOnClickListener(this);

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


            case R.id.txt_exit:
                Cache.setString(MainActivity.this, "access_token", null);
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
                finish();
                break;

        }

    }

    private void turnOnGpsStartSrvice() {

        if (checkGpsON()) {
//                startService(new Intent(MainActivity.this, GpsService.class));
            // ContextCompat choose the best method depend of different ApiAndroid
            ContextCompat.startForegroundService(MainActivity.this, new Intent(this, GpsService.class));
            Cache.setString(MainActivity.this, "service_state", "enable");
            btn_start_service.setVisibility(View.GONE);
            btn_stop_service.setVisibility(View.VISIBLE);
        } else {
//                gpsDialog();
            gpsDialog2();
        }
    }

    private void gpsDialog2() {

        DialogFactory dialogFactory = new DialogFactory(this);
        dialogFactory.createGpsDialog(new DialogFactory.DialogFactoryInteraction() {
            @Override
            public void onAcceptButtonClicked(String... strings) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, 15);
            }

            @Override
            public void onDeniedButtonClicked(boolean cancel_dialog) {
               btn_start_main.setVisibility(View.VISIBLE);
               btn_stop_main.setVisibility(View.GONE);
            }
        }, drawer_layout_home);

    }


    private boolean checkGpsON() {
        final LocationManager manager = (LocationManager) (MainActivity.this).getSystemService(Context.LOCATION_SERVICE);
        return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }


//    private void gpsDialog() {
//        //     show waiting AVI
//        Toast.makeText(this, "برای شروع لازم است GPS خود را روشن نمایید, صبور باشید ...", Toast.LENGTH_SHORT).show();
//        LocationRequest mLocationRequest = LocationRequest.create()
//                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
//                .setInterval(1000)
//                .setNumUpdates(2);
//
//        final LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
//        builder.setAlwaysShow(true);
//        builder.setNeedBle(true);
//        SettingsClient client = LocationServices.getSettingsClient(this);
//        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
//        task.addOnSuccessListener(this, locationSettingsResponse -> {
////            hasLocationPermission();
//            turnOnGpsStartSrvice();
//
//        });
//        task.addOnFailureListener(this, e -> {
//            if (e instanceof ResolvableApiException) {
//                // Location settings are not satisfied, but this can be fixed
//                // by showing the user a dialog.
//                try {
//                    // Show the dialog by calling startResolutionForResult(),
//                    // and check the result in onActivityResult().
//                    ResolvableApiException resolvable = (ResolvableApiException) e;
//                    resolvable.startResolutionForResult(this,
//                            12);
//                } catch (IntentSender.SendIntentException e1) {
//
//                    e.printStackTrace();
//                }
//            }
//        });
//    }




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


    Button btn_start_main;
    Button btn_stop_main;

    @Override
    public void projectListItemOnClick(Project model, ProjectListAdapter adapter, String state,
                                       String show_dialog, Button btn_start, Button btn_stop) {

        strProject_id = model.id;
        strRunning_project = model.title;

        if (!show_dialog.equals("")) {
            showNotallowDialog();
        } else if (state.equals("start")) {

            if (checkGpsON()) {
                Cache.setString(MainActivity.this, "project_id", strProject_id);
                Cache.setString(MainActivity.this, "running_project", strRunning_project);
                btn_start.setVisibility(View.GONE);
                btn_stop.setVisibility(View.VISIBLE);
            } else {
                btn_start_main = btn_start;
                btn_stop_main = btn_stop;
            }
            turnOnGpsStartSrvice();

        } else if (state.equals("stop")) {
            btn_start.setVisibility(View.VISIBLE);
            btn_stop.setVisibility(View.GONE);
            Cache.setString(MainActivity.this, "project_id", null);
            Cache.setString(MainActivity.this, "running_project", null);
            stopService(new Intent(MainActivity.this, GpsService.class));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 15) {

            turnOnGpsStartSrvice();

            if(checkGpsON()){
                Cache.setString(MainActivity.this, "project_id", strProject_id);
                Cache.setString(MainActivity.this, "running_project", strRunning_project);
                btn_start_main.setVisibility(View.GONE);
                btn_stop_main.setVisibility(View.VISIBLE);
            }else{
                btn_start_main.setVisibility(View.VISIBLE);
                btn_stop_main.setVisibility(View.GONE);
            }

        }
    }



    private void showNotallowDialog() {
        String running_project = Cache.getString(this, "running_project");
        DialogFactory dialogFactory = new DialogFactory(this);
        dialogFactory.createNotAllowDialog(new DialogFactory.DialogFactoryInteraction() {
            @Override
            public void onAcceptButtonClicked(String... strings) {

            }

            @Override
            public void onDeniedButtonClicked(boolean cancel_dialog) {

            }
        }, drawer_layout_home, running_project);
    }
}
