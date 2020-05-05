package com.rahbarbazaar.hamyab.utilities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class GeneralTools {

    private static GeneralTools instance;

    private GeneralTools() {
    }

    public static GeneralTools getInstance() {

        if (instance == null) {

            return instance = new GeneralTools();
        } else {

            return instance;
        }
    }


    public boolean checkPackageInstalled(String packageName, Context context) {

        PackageManager manager = context.getPackageManager();
        Intent intent = manager.getLaunchIntentForPackage(packageName);
        return intent == null;
    }


    //check network connectivity
    public boolean checkInternetConnection(Context context) {

        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (manager != null) {
            NetworkInfo info = manager.getActiveNetworkInfo();

            if (info != null) {

                return info.isConnected();

            } else {

                return false;
            }

        } else {

            return false;
        }
    }

    //show alert dialog after check
    public void doCheckNetwork(final Context context, final View view) {

        if (!checkInternetConnection(context)) {

            if (context instanceof AppCompatActivity)
                if (((AppCompatActivity) context).isFinishing())
                    return;

            new DialogFactory(context).createNoInternetDialog(new DialogFactory.DialogFactoryInteraction() {
                @Override
                public void onAcceptButtonClicked(String... params) {
                    context.startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
//                    context.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                }

                @Override
                public void onDeniedButtonClicked(boolean cancel_dialog) {

                    if (cancel_dialog) {

                        doCheckNetwork(context, view);
                    } else {
                        Intent intent = new Intent();
                        intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings$DataUsageSummaryActivity"));
                        context.startActivity(intent);
//                        context.startActivity(new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                    }
                }
            }, view);
        }
    }

}
