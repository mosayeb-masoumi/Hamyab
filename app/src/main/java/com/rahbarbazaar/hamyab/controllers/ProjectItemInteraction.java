package com.rahbarbazaar.hamyab.controllers;

import android.widget.Button;

import com.rahbarbazaar.hamyab.models.dashboard.Project;

public interface ProjectItemInteraction {
    void projectListItemOnClick(Project model, ProjectListAdapter adapter, String state, String show_dialog, Button btn_start, Button btn_stop);
}
