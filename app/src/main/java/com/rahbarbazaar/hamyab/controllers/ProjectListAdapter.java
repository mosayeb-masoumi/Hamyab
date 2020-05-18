package com.rahbarbazaar.hamyab.controllers;

import android.content.Context;
//import android.support.annotation.NonNull;
//import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rahbarbazaar.hamyab.R;
import com.rahbarbazaar.hamyab.models.dashboard.Project;

import java.util.List;

public class ProjectListAdapter extends  RecyclerView.Adapter<ProjectListViewholder> {


    public List<Project> projects;
    Context context;
    ProjectListAdapter adapter;


    public ProjectListAdapter(List<Project> projects, Context context) {
        this.projects = projects;
        this.context = context;

    }

    @NonNull
    @Override
    public ProjectListViewholder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_projectlist, parent, false);
        return new ProjectListViewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectListViewholder holder, int position) {
        final Project model = projects.get(position);
        holder.bindData(model);
        holder.setOnProjectHolderListener(listener,model ,adapter);
    }

    private ProjectItemInteraction listener = null;
    public void setListener(ProjectItemInteraction listener) {
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return projects.size();
    }
}
