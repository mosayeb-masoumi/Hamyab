package com.rahbarbazaar.hamyab.controllers;

//import android.support.annotation.NonNull;
//import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rahbarbazaar.hamyab.R;
import com.rahbarbazaar.hamyab.models.dashboard.Project;
import com.rahbarbazaar.hamyab.utilities.Cache;

public class ProjectListViewholder extends RecyclerView.ViewHolder  {

    private TextView txt_title;
    private LinearLayout ll_row;
    private Button btn_start,btn_stop ,btn_register;
    private RelativeLayout rl_register,rl_av_register;
    String state;

    public ProjectListViewholder(@NonNull View itemView) {
        super(itemView);

        txt_title = itemView.findViewById(R.id.row_project_title);
        ll_row = itemView.findViewById(R.id.ll_row);
        btn_stop= itemView.findViewById(R.id.row_btn_stop);
        btn_start= itemView.findViewById(R.id.row_btn_start);
        btn_register=itemView.findViewById(R.id.btn_submit_register);
        rl_register = itemView.findViewById(R.id.rl_register);
        rl_av_register = itemView.findViewById(R.id.rl_av_register);
    }

    public void bindData(Project model) {
        txt_title.setText(model.title);
//
        if(model.title.equals(Cache.getString(itemView.getContext(),"running_project"))){
            btn_stop.setVisibility(View.VISIBLE);
            rl_register.setVisibility(View.VISIBLE);
            btn_start.setVisibility(View.GONE);
        }else{
            btn_start.setVisibility(View.VISIBLE);
            btn_stop.setVisibility(View.GONE);
            rl_register.setVisibility(View.GONE);
        }
    }

    public void setOnProjectHolderListener(ProjectItemInteraction listener, Project model, ProjectListAdapter adapter) {

        btn_stop.setOnClickListener(view -> {
            state = "stop";
            listener.projectListItemOnClick(model , adapter ,state,"",btn_start,btn_stop,btn_register,rl_av_register,rl_register);
        });

        btn_start.setOnClickListener(view -> {
            if(Cache.getString(itemView.getContext(),"running_project") == null){

                state = "start";
                listener.projectListItemOnClick(model , adapter,state,"" ,btn_start,btn_stop,btn_register,rl_av_register,rl_register);
            }else{
                // show dialog
                listener.projectListItemOnClick(model , adapter,state ,model.title,btn_start,btn_stop,btn_register,rl_av_register,rl_register);
            }
        });

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                state = "register";
                listener.projectListItemOnClick(model , adapter,state,"" ,btn_start,btn_stop,btn_register,rl_av_register,rl_register);

            }
        });

    }
}
