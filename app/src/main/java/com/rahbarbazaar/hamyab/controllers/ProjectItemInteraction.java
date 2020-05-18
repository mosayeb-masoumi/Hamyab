package com.rahbarbazaar.hamyab.controllers;

import android.widget.Button;
import android.widget.RelativeLayout;

import com.rahbarbazaar.hamyab.models.dashboard.Project;

public interface ProjectItemInteraction {
    void projectListItemOnClick(Project model, ProjectListAdapter adapter, String state,
                                String show_dialog, Button btn_start, Button btn_stop,
                                Button btn_register, RelativeLayout rl_av_register,RelativeLayout rl_register);
}
