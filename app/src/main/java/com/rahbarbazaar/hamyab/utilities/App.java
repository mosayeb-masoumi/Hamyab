package com.rahbarbazaar.hamyab.utilities;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }


    // TO support android below 21
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
