package com.example.tudy;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.text.style.StrikethroughSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TodayAdapter extends RecyclerView.Adapter<TodayAdapter.TodayViewHolder> {
    Context context;
    Dialog dialog;
    List<TodayConstructor> todayConstructorList;

    public TodayAdapter() {
    }

    public TodayAdapter(Context context, List<TodayConstructor> todayConstructorList) {
        this.context = context;
        this.todayConstructorList = todayConstructorList;
    }

    public static class ThickStrikethroughSpan extends StrikethroughSpan {

        private final float strokeWidthMultiplier;

        public ThickStrikethroughSpan(float strokeWidthMultiplier) {
            this.strokeWidthMultiplier = strokeWidthMultiplier;
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            ds.setStrokeWidth(ds.getTextSize() * strokeWidthMultiplier);
        }
    }

     public void applyStrikethrough(CheckBox checkBox, float strokeWidthMultiplier) {
        SpannableString spannableString = new SpannableString(checkBox.getText());
        spannableString.setSpan(new ThickStrikethroughSpan(strokeWidthMultiplier), 0, spannableString.length(), 0);
        checkBox.setText(spannableString);
    }

    private void removeStrikethrough(CheckBox checkBox) {
        SpannableString spannableString = new SpannableString(checkBox.getText());
        StrikethroughSpan[] strikethroughSpans = spannableString.getSpans(0, spannableString.length(), StrikethroughSpan.class);
        for (StrikethroughSpan strikethroughSpan : strikethroughSpans) {
            spannableString.removeSpan(strikethroughSpan);
        }
        checkBox.setText(spannableString);
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
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        String status = String.valueOf(constructor.getStatus());

        if (status.equals("DONE")) {
            holder.checkBox.setChecked(true); // Set CheckBox as checked
            holder.checkBox.setText(constructor.getTask());
            holder.checkBox.setTextColor(Color.GRAY);
            applyStrikethrough(holder.checkBox, 2f);
        } else {
            holder.checkBox.setChecked(false); // Set CheckBox as unchecked
            holder.checkBox.setText(constructor.getTask());
            holder.checkBox.setTextColor(Color.parseColor("#4C4C4C"));
            removeStrikethrough(holder.checkBox);
        }
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                TodayActivity todayActivity = null;

                if (context instanceof TodayActivity) {
                    todayActivity = (TodayActivity) context;
                }

                if (isChecked) {
                    // Checked state
                    if (todayActivity != null) {
                        todayActivity.totalTask = 0;

                        holder.checkBox.setTextColor(Color.GRAY);
                        applyStrikethrough(holder.checkBox, 2f);
                        todayActivity.todayConstructorArrayList.clear();
                        todayActivity.todayAdapter.notifyDataSetChanged();
                        constructor.setStatus("DONE");
                        todayActivity.totalTask = 0;
                        todayActivity.TOTAL_TASK_TEXTVIEW.setText(String.valueOf(todayActivity.totalTask));
                        String Uid = constructor.uid;

                        updateStatus("DONE", Uid);
                    }
                } else {
                    // Unchecked state
                    if (todayActivity != null) {
                        todayActivity.totalTask = 0;
                        holder.checkBox.setTextColor(Color.parseColor("#4C4C4C"));
                        removeStrikethrough(holder.checkBox);
                        // Update your data here and notify the adapter
                        todayActivity.todayConstructorArrayList.clear();
                        todayActivity.todayAdapter.notifyDataSetChanged();
                        constructor.setStatus("FENDING");
                        todayActivity.totalTask = 0;
                        todayActivity.TOTAL_TASK_TEXTVIEW.setText(String.valueOf(todayActivity.totalTask));
                        String Uid = constructor.uid;
                        updateStatus("FENDING", Uid);
                    }
                }
            }
        });

        holder.checkBox.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                builder.setMessage("Update?")
                        .setPositiveButton("Update", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog = new Dialog(context);
                                ((Dialog) dialog).setContentView(R.layout.todaycreate);
                                ((Dialog) dialog).getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                                Button okButton = ((Dialog) dialog).findViewById(R.id.Done_Button);
                                EditText taskEditText = ((Dialog) dialog).findViewById(R.id.task);
                                TextView taskText = ((Dialog) dialog).findViewById(R.id.Date);
                                String currentDayOfWeek = null;
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O ) {
                                    currentDayOfWeek = LocalDate.now().getDayOfWeek().toString();
                                }
                                taskText.setText(currentDayOfWeek);
                                DialogInterface finalDialog = dialog;
                                okButton.setOnClickListener(new View.OnClickListener() {
                                    @SuppressLint("NotifyDataSetChanged")
                                    @Override
                                    public void onClick(View view) {
                                        String taskText = taskEditText.getText().toString().trim();
                                        String  Uid  =constructor.uid;
                                        update(taskText,Uid);
                                        finalDialog.dismiss();
                                    }
                                });

                                ((Dialog) dialog).show();

                            }
                        })
                        .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String  Uid  =constructor.uid;
                               todayConstructorList.remove(constructor);
                               todayConstructorList.clear();
                                delete(Uid);
                            }
                        }).show();

                return false;
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateStatus(String status, String uid){
        if (!TextUtils.isEmpty(status)) {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            if (userId != null) {
                DatabaseReference users = FirebaseDatabase.getInstance().getReference().child("USER_TODAY_TASK").child(userId).child(uid);
                users.child("Status").setValue(status);
            } else {
                Toast.makeText(context.getApplicationContext(), "User is not authenticated", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context.getApplicationContext(), "Task text cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }
public void  delete(String uid){

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (userId != null) {
            DatabaseReference users = FirebaseDatabase.getInstance().getReference().child("USER_TODAY_TASK").child(userId).child(uid);
            users.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {

                    Toast.makeText(context.getApplicationContext(), "Task deleted", Toast.LENGTH_SHORT).show();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(context.getApplicationContext(), "connection error", Toast.LENGTH_SHORT).show();
                }
            });

            Toast.makeText(context.getApplicationContext(), "Done", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context.getApplicationContext(), "User is not authenticated", Toast.LENGTH_SHORT).show();
        }


}
    public void update(String update , String uid){

        if (!TextUtils.isEmpty(update)) {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            if (userId != null) {

                DatabaseReference users = FirebaseDatabase.getInstance().getReference().child("USER_TODAY_TASK").child(userId).child(uid);

                users.child("Task").setValue(update);
                if (context instanceof TodayActivity) {
                    TodayActivity todayActivity = (TodayActivity) context;
                    todayActivity.todayConstructorArrayList.clear();
                    todayActivity.todayAdapter.notifyDataSetChanged();
                }
            } else {
                Toast.makeText(context.getApplicationContext(), "User is not authenticated", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(context.getApplicationContext(), "Task text cannot be empty", Toast.LENGTH_SHORT).show();
        }

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
