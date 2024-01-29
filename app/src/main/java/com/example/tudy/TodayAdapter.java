package com.example.tudy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TodayAdapter extends RecyclerView.Adapter<TodayAdapter.TodayViewHolder> {
    Context context;
    List<TodayConstructor> todayConstructorList;

    public TodayAdapter(Context context, List<TodayConstructor> todayConstructorList) {
        this.context = context;
        this.todayConstructorList = todayConstructorList;
    }

    @NonNull
    @Override
    public TodayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.todaycardview,parent,false);


        return new TodayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TodayViewHolder holder, int position) {
     TodayConstructor constructor = todayConstructorList.get(position);
     holder.checkBox.setText(constructor.getTask());

    }

    @Override
    public int getItemCount() {
        return todayConstructorList.size();
    }
    public static class TodayViewHolder extends RecyclerView.ViewHolder{
        CheckBox checkBox;
        View view;


        public TodayViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
            checkBox = view.findViewById(R.id.checkbox);

        }
    }
}
