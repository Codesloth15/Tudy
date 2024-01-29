package com.example.tudy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TodayActivity extends AppCompatActivity {
    RecyclerView RecyclerView_personal;
    TextView TOTAL_TASK_TEXTVIEW;
    RecyclerView.LayoutManager layoutManager;
    TodayAdapter todayAdapter;
    FloatingActionButton fabAdd;
    ArrayList<TodayConstructor> todayConstructorArrayList;
    Dialog dialog;
    final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseAuth firebaseAuth;
    DatabaseReference ref;
    ImageView BACK_BUTTON ;
    int totalTask = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today);
        initializingVariables();
        loadData();
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setContentView(R.layout.todaycreate);
                dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                Button okButton = dialog.findViewById(R.id.Done_Button);
                Button Cancel_Button = dialog.findViewById(R.id.Cancel_Button);
                EditText taskEditText = dialog.findViewById(R.id.task);
                TextView taskText = dialog.findViewById(R.id.Date);
                String currentDayOfWeek = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O ) {
                    currentDayOfWeek = LocalDate.now().getDayOfWeek().toString();
                }
                Cancel_Button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                taskText.setText(currentDayOfWeek);
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        todayConstructorArrayList.clear();
                        String taskText = taskEditText.getText().toString().trim();
                         totalTask =0;
                        if (!TextUtils.isEmpty(taskText)) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if (user != null) {
                                String currentUser = user.getUid();
                                DatabaseReference userTaskRef = database.getReference().child("USER_TODAY_TASK").child(currentUser);

                                // Get the current day of the week
                                String currentDayOfWeek = null;
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O ) {
                                    currentDayOfWeek = LocalDate.now().getDayOfWeek().toString();
                                }

                                Map<String, Object> taskMap = new HashMap<>();
                                taskMap.put("Week", currentDayOfWeek);
                                taskMap.put("Task", taskText);
                                taskMap.put("Status","FEENDING");


                                userTaskRef.push().setValue(taskMap);

                                dialog.dismiss();

                            } else {
                                Toast.makeText(getApplicationContext(), "User is not authenticated", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Task text cannot be empty", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                dialog.show();
            }
        });
        BACK_BUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                finish();
            }
        });

    }

    public void loadData(){
        todayConstructorArrayList.clear();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        DatabaseReference user = FirebaseDatabase.getInstance().getReference().child("USER_TODAY_TASK").child(userId);
        user.addValueEventListener(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot snapshots : snapshot.getChildren()) {
                    String name = snapshots.getKey();
                    String TASK = snapshots.child("Task").getValue(String.class);
                    String WEEK = snapshots.child("WeeK").getValue(String.class);
                    String STATUS = snapshots.child("Status").getValue(String.class);
                    TodayConstructor constructor = new TodayConstructor(TASK, WEEK, STATUS,name);
                    if(STATUS.equals("FENDING")){
                        totalTask++;
                    }

                    todayConstructorArrayList.add(constructor);
                    todayAdapter.notifyDataSetChanged();
                    loadTotalTaskData(totalTask);
                }

            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {

            }
        });
    }
  public  void loadTotalTaskData(int totalTasks) {
          TOTAL_TASK_TEXTVIEW.setText(String.valueOf(totalTasks +" tasks"));

          // Assuming TOTAL_TEXTVIEW is a static field in HomeActivity
          HomeActivity.TOTAL_TEXTVIEW.setText(String.valueOf(totalTasks+ " tasks"));


  }
    @SuppressLint("WrongViewCast")
    private void initializingVariables() {
        fabAdd = findViewById(R.id.fabAdd);
        todayConstructorArrayList = new ArrayList<TodayConstructor>();
        RecyclerView_personal = findViewById(R.id.RecyclerView_personal);
        layoutManager = new LinearLayoutManager(this);
        RecyclerView_personal.setLayoutManager(layoutManager);
        todayAdapter = new TodayAdapter(this,todayConstructorArrayList);
        RecyclerView_personal.setAdapter(todayAdapter);
        dialog = new Dialog(TodayActivity.this);
        firebaseAuth = FirebaseAuth.getInstance();
        BACK_BUTTON = findViewById(R.id.BACK_BUTTON);
        TOTAL_TASK_TEXTVIEW = findViewById(R.id.TOTAL_TASK_TEXTVIEW);
    }
}