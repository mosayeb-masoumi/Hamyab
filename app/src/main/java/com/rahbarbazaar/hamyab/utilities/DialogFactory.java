package com.rahbarbazaar.hamyab.utilities;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
//import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.rahbarbazaar.hamyab.R;

public class DialogFactory {
    private Context context;

    public interface DialogFactoryInteraction {

        void onAcceptButtonClicked(String... strings);

        void onDeniedButtonClicked(boolean cancel_dialog);
    }

    public DialogFactory(Context ctx) {
        this.context = ctx;
    }

    public void createNoInternetDialog(DialogFactoryInteraction listener, View root) {
        View customLayout = LayoutInflater.from(context).inflate(R.layout.sample_dialog2, (ViewGroup) root, false);

        Button btn_wifi_dialog = customLayout.findViewById(R.id.btn1);
        Button btn_data_dialog = customLayout.findViewById(R.id.btn2);
        TextView txt_description = customLayout.findViewById(R.id.txt_description);
        TextView txt_header = customLayout.findViewById(R.id.txt_header);
        ImageView img_close = customLayout.findViewById(R.id.img_close);

        btn_wifi_dialog.setText(context.getResources().getString(R.string.internet_setting));
        btn_data_dialog.setText(context.getResources().getString(R.string.data_setting));
        txt_header.setText(context.getResources().getString(R.string.warning));
        txt_description.setText(context.getResources().getString(R.string.no_internet_connection));


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(customLayout);

        //create dialog and set background transparent
        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        if (dialog.getWindow() != null) {

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        //set click listener for views inside of dialog
        btn_wifi_dialog.setOnClickListener(view -> listener.onAcceptButtonClicked(""));
        btn_data_dialog.setOnClickListener(view -> listener.onDeniedButtonClicked(false));
        img_close.setOnClickListener(v -> {
            dialog.dismiss();
        });

        //if dialog dismissed, this action will be called
        dialog.setOnDismissListener(dialogInterface -> listener.onDeniedButtonClicked(true));

        dialog.show();
    }



    public void createNotAllowDialog(DialogFactoryInteraction listener, View root ,String running_project) {
        View customLayout = LayoutInflater.from(context).inflate(R.layout.sample_dialog, (ViewGroup) root, false);

        Button btn_close = customLayout.findViewById(R.id.btn);
        TextView txt_description = customLayout.findViewById(R.id.txt_description);
        TextView txt_header = customLayout.findViewById(R.id.txt_header);
        ImageView img_close = customLayout.findViewById(R.id.img_close);


        btn_close.setText("بستن");
        txt_header.setText(context.getResources().getString(R.string.warning));

        String description = "ابتدا پروژه "+running_project+" را متوقف نموده و مجددا تلاش کنید.";
        txt_description.setText(description);


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(customLayout);

        //create dialog and set background transparent
        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        if (dialog.getWindow() != null) {

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        img_close.setOnClickListener(v -> dialog.dismiss());
        btn_close.setOnClickListener(view -> dialog.dismiss());


//        //if dialog dismissed, this action will be called
//        dialog.setOnDismissListener(dialogInterface -> listener.onDeniedButtonClicked(true));

        dialog.show();
    }



    public void createGpsDialog(DialogFactoryInteraction listener, View root ) {
        View customLayout = LayoutInflater.from(context).inflate(R.layout.sample_dialog, (ViewGroup) root, false);

        Button btn_setting = customLayout.findViewById(R.id.btn);
        TextView txt_description = customLayout.findViewById(R.id.txt_description);
        TextView txt_header = customLayout.findViewById(R.id.txt_header);
        ImageView img_close = customLayout.findViewById(R.id.img_close);


//        btn_close.setVisibility(View.GONE);
        img_close.setVisibility(View.GONE);

        btn_setting.setText("تنظیمات GPS");

        txt_header.setText(context.getResources().getString(R.string.warning));

        String description = "لطفا GPS دستگاه خود را روشن نمایید.";
        txt_description.setText(description);


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(customLayout);

        //create dialog and set background transparent
        AlertDialog dialog = builder.create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);

        if (dialog.getWindow() != null) {

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        img_close.setOnClickListener(v -> {
            dialog.dismiss();
            listener.onDeniedButtonClicked(false);
        });



        btn_setting.setOnClickListener(view -> {
            listener.onAcceptButtonClicked();
            dialog.dismiss();
        });

        dialog.setCanceledOnTouchOutside(false);

//        //if dialog dismissed, this action will be called
//        dialog.setOnDismissListener(dialogInterface -> listener.onDeniedButtonClicked(true));

        dialog.show();
    }



}
