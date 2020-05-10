package com.rahbarbazaar.hamyab.ui;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.retrofit2.adapter.rxjava2.HttpException;
import com.rahbarbazaar.hamyab.R;
import com.rahbarbazaar.hamyab.api_error.ErrorUtils;
import com.rahbarbazaar.hamyab.api_error.ErrorsMessage422;
import com.rahbarbazaar.hamyab.models.LoginModel;
import com.rahbarbazaar.hamyab.models.dashboard.ProjectList;
import com.rahbarbazaar.hamyab.network.Service;
import com.rahbarbazaar.hamyab.network.ServiceProvider;
import com.rahbarbazaar.hamyab.service.GpsService;
import com.rahbarbazaar.hamyab.utilities.Cache;
import com.rahbarbazaar.hamyab.utilities.CustomBaseActivity;
import com.rahbarbazaar.hamyab.utilities.GeneralTools;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends CustomBaseActivity implements View.OnClickListener {
    GeneralTools tools;
    BroadcastReceiver connectivityReceiver = null;

    EditText edt_name, edt_password;
    Button btn_login;
    AVLoadingIndicatorView avi;


    // for handling422
    private StringBuilder builderName, builderPassword;


    CompositeDisposable disposable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        //check network broadcast reciever
        tools = GeneralTools.getInstance();
        connectivityReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

//                tools.doCheckNetwork(LoginActivity.this, findViewById(R.id.drawer_layout_home));
            }
        };

        initView();

        // event on done keyboard
        edt_password.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                closeKeyboard();
                checkLocationPermission();
                return true;
            }
            return false;
        });
    }


    private void submitLoginRequest() {

        avi.setVisibility(View.VISIBLE);
        btn_login.setVisibility(View.GONE);
        String name = edt_name.getText().toString();
        String password = edt_password.getText().toString();
        String hashed_password = sha1Hash(password);

        Service service = new ServiceProvider(this).getmService();
        disposable.add(service.login(name, hashed_password)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<LoginModel>() {

                    @Override
                    public void onSuccess(LoginModel result) {


                        LoginModel loginModel = new LoginModel();
                        loginModel = result;
                        Cache.setString(LoginActivity.this, "access_token", String.valueOf(loginModel.apiToken));
                        getProjectList();
                    }

                    @Override
                    public void onError(Throwable e) {

                        avi.setVisibility(View.GONE);
                        btn_login.setVisibility(View.VISIBLE);


                        if (e instanceof HttpException) {
                            int error = ((HttpException) e).code();
                            if (error == 422) {
                                builderName = null;
                                builderPassword = null;
                                ErrorsMessage422 apiError = ErrorUtils.parseError422(((HttpException) e).response());

                                if (apiError.name != null) {
                                    builderName = new StringBuilder();
                                    for (String a : apiError.name) {
                                        builderName.append("").append(a).append(" ");
                                    }
                                }

                                if (apiError.password != null) {
                                    builderPassword = new StringBuilder();
                                    for (String b : apiError.password) {
                                        builderPassword.append("").append(b).append(" ");
                                    }
                                }

                                if (builderName != null) {
                                    Toast.makeText(LoginActivity.this, "" + builderName, Toast.LENGTH_SHORT).show();
                                }
                                if (builderPassword != null) {
                                    Toast.makeText(LoginActivity.this, "" + builderPassword, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, "" + getResources().getString(R.string.connectionFaield), Toast.LENGTH_SHORT).show();
                            }


//                        } else if(e instanceof IOException) {
                        } else {
                            Toast.makeText(LoginActivity.this, "" + getResources().getString(R.string.connectionFaield), Toast.LENGTH_SHORT).show();
                        }


                    }
                }));

    }

    private void getProjectList() {

        String api_token = Cache.getString(LoginActivity.this, "access_token");
        Service service = new ServiceProvider(this).getmService();
        disposable.add(service.getProjectList(api_token)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<ProjectList>() {
                    @Override
                    public void onSuccess(ProjectList result) {

                        avi.setVisibility(View.GONE);
                        btn_login.setVisibility(View.VISIBLE);

                        ProjectList projectList = new ProjectList();
                        projectList = result;
                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        intent.putExtra("projectList",projectList);
                        startActivity(intent);
                        finish();

                    }

                    @Override
                    public void onError(Throwable e) {
                        avi.setVisibility(View.GONE);
                        btn_login.setVisibility(View.VISIBLE);
                        if (e instanceof HttpException) {
                            Toast.makeText(LoginActivity.this, "" + getResources().getString(R.string.serverFaield), Toast.LENGTH_SHORT).show();

                        }else {
                            Toast.makeText(LoginActivity.this, "" + getResources().getString(R.string.connectionFaield), Toast.LENGTH_SHORT).show();
                        }

                    }
                }));
    }


    private void closeKeyboard() {

        InputMethodManager imm = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = this.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    private void initView() {
        edt_name = findViewById(R.id.edt_name);
        edt_password = findViewById(R.id.edt_password);
        btn_login = findViewById(R.id.btn_login);
        avi = findViewById(R.id.avi_login);

        btn_login.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_login:
                checkLocationPermission();
                break;
        }
    }

    private void checkLocationPermission() {
        if (hasLocationPermission()) {
            submitLoginRequest();
        }else{
            askLocationPermission();
        }

    }



    private boolean hasLocationPermission() {
        return ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }
    private void askLocationPermission() {
        ActivityCompat.requestPermissions((LoginActivity.this)
                , new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 3);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == 3) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                submitLoginRequest();
            } else {
                Toast.makeText(this, "نیاز به اجازه ی دسترسی لوکیشن", Toast.LENGTH_SHORT).show();
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    String sha1Hash(String toHash) {
        String hash = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            byte[] bytes = toHash.getBytes("UTF-8");
            digest.update(bytes, 0, bytes.length);
            bytes = digest.digest();

            // This is ~55x faster than looping and String.formating()
            hash = bytesToHex(bytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return Objects.requireNonNull(hash).toLowerCase();
    }

    // http://stackoverflow.com/questions/9655181/convert-from-byte-array-to-hex-string-in-java
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(connectivityReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        disposable = new CompositeDisposable();

    }


    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(connectivityReceiver);
        disposable.dispose();
    }


}
