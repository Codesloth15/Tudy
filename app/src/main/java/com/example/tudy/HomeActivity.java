package com.example.tudy;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HomeActivity extends AppCompatActivity {
    private CardView CardView_today,CardView_planned,CardView_personal,CardView_work,CardView_shopping;
    private DrawerLayout drawerLayout;
    private NavigationView nav_drawer;
    private ActionBarDrawerToggle drawerToggle;
    private ImageView menu_drawer;
    TextView link , USER_NAME ;
    private FirebaseAuth firebaseAuth;

    private androidx.appcompat.widget.Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initializeVariables();
        onClick();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("newUser").child(userId);

            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String name = snapshot.child("username").getValue(String.class);
                        if (name != null) {
                            USER_NAME.setText("Hello "+name);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle any errors here
                }
            });
        }



    }

    private void onClick() {

        menu_drawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });

        //header link
        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle click event here
                openLink();
            }
        });


        /*Navigation bar*/
        nav_drawer.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.home) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                } else if (item.getItemId() == R.id.about) {
                    showToast("About");
                } else if (item.getItemId() == R.id.profile) {
                    showToast("Profile");
                } else if (item.getItemId() == R.id.fb) {
                    showToast("Facebook");
                } else if (item.getItemId() == R.id.email) {
                    showToast("Email");
                }
                // Add more else-if blocks for other menu items

                // Close the drawer after handling item click
                drawerLayout.closeDrawer(GravityCompat.START);
                return false; // Indicate that the item click is handled
            }
        });

        //Onclick CardView

        CardView_today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,TodayActivity.class));
            }
        });



    }

    private void initializeVariables() {
        drawerLayout = findViewById(R.id.drawerlayout);
        nav_drawer = findViewById(R.id.nav_drawer);
        toolbar = findViewById(R.id.toolbar);

        View headerView = nav_drawer.getHeaderView(0);
        link = headerView.findViewById(R.id.link);
        menu_drawer = findViewById(R.id.menus);

        // Create ActionBarDrawerToggle and set it up
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

        firebaseAuth = FirebaseAuth.getInstance();
        USER_NAME = findViewById(R.id.user);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        //CardView
        CardView_personal = findViewById(R.id.CardView_personal);
        CardView_planned =findViewById(R.id.CardView_planned);
        CardView_work = findViewById(R.id.CardView_work);
        CardView_today = findViewById(R.id.CardView_today);
        CardView_shopping =findViewById(R.id.CardView_shopping);


    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
    private void openLink() {
        // Example: open the link in a browser
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Codesloth15"));
        startActivity(browserIntent);
    }
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
