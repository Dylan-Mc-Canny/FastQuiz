package com.example.dissertation_tester;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.dissertation_tester.Fragments.HomeFragment;
import com.example.dissertation_tester.Fragments.ProfileFragment;
import com.example.dissertation_tester.HelperClasses.FirebaseManager;
import com.example.dissertation_tester.Login.LogInPage;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    FirebaseManager manager = new FirebaseManager(this);
    FragmentManager fragmentManager;
    private FirebaseManager firebaseManager;


    public void setDrawerLayout(DrawerLayout drawerLayout) {
        this.drawerLayout = drawerLayout;
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (manager.checkAndRedirectIfNotLoggedIn(MainActivity.this)) {
            // Only continue setup if logged in
            DailyStreakFunctions streakFunctions = new DailyStreakFunctions(this);
            streakFunctions.fetchStreakFromFirebase();

            openFragment(new HomeFragment());

            toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            drawerLayout = findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_nav, R.string.close_nav);
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = findViewById(R.id.navigation_drawer);
            navigationView.setNavigationItemSelectedListener(this);
        }


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId==R.id.nav_home)
        {
            openFragment(new HomeFragment());
        }
        else if (itemId==R.id.nav_settings) {
            openFragment(new ProfileFragment());
        }
        else if (itemId ==R.id.nav_logout)
        {
            boolean result = logout();
            if (result) {
                Intent i = new Intent(MainActivity.this, LogInPage.class);
                startActivity(i);
                finish();
            } else {
                Toast.makeText(this, "Logout failed", Toast.LENGTH_SHORT).show();
            }
        }


        //null safe implemented after testing
        if (drawerLayout != null) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }

        return true;
    }


    void openFragment(Fragment fragment)
    {
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container,fragment);
        transaction.commit();
    }


    public boolean logout() {
        FirebaseAuth.getInstance().signOut();
        // Returns true if user is now logged out (no current user)
        return FirebaseAuth.getInstance().getCurrentUser() == null;
    }

    // Add this setter method to allow injection of a mock FirebaseManager during testing
    public void setFirebaseManager(FirebaseManager firebaseManager) {
        this.firebaseManager = firebaseManager;
    }


}
