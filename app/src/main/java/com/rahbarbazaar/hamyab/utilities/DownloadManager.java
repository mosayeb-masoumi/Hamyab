package com.rahbarbazaar.hamyab.utilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Environment;

import com.rahbarbazaar.hamyab.R;

import java.io.File;

import static android.content.Context.DOWNLOAD_SERVICE;

public class DownloadManager {

    private long DLid;

    public void DownloadUpdateApp(Context context) {
        context.registerReceiver(downloadReceiver, new IntentFilter(android.app.DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        DLid = DownloadData(context);
    }

    private long DownloadData(Context context) {

        long downloadReference;
        android.app.DownloadManager downloadManager =
                (android.app.DownloadManager) context.getSystemService(DOWNLOAD_SERVICE);
        android.app.DownloadManager.Request request =
//                new android.app.DownloadManager.Request(Uri.parse(ClientConfig.Update_URL));
                new android.app.DownloadManager.Request(Uri.parse(Cache.getString(context,"Update_URL")));

        request.setTitle(context.getResources().getString(R.string.app_name));
        request.setDescription(context.getResources().getString(R.string.update_app));
        request.setNotificationVisibility(1);
        request.setNotificationVisibility(android.app.DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setMimeType("application/vnd.android.package-archive");
        request.setVisibleInDownloadsUi(true);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, context.getResources().getString(R.string.app_name) + ".apk");
        downloadReference = downloadManager.enqueue(request);
        return downloadReference;
    }

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long referenceId = intent.getLongExtra(android.app.DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (referenceId == DLid) {
                Uri apkUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                        "Download/" + context.getResources().getString(R.string.app_name) + ".apk"));
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        }
    };
}
