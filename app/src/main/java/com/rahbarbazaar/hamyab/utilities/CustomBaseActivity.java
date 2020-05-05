package com.rahbarbazaar.hamyab.utilities;

import android.content.Context;

public class CustomBaseActivity extends PersianAppcompatActivity {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleManager.setLocale(base));

    }
}
