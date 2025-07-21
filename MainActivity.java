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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.dissertation_tester.Fragments.HomeFragment;
import com.example.dissertation_tester.HelperClasses.FirebaseManager;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    FirebaseManager manager = new FirebaseManager(this);
    FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        openFragment(new HomeFragment());

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.open_nav,R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.navigation_drawer);
        navigationView.setNavigationItemSelectedListener(this);

        manager.checkAndRedirectIfNotLoggedIn(MainActivity.this);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId==R.id.nav_home)
        {
            openFragment(new HomeFragment());
        }
        /*else if (itemId==R.id.nav_settings) {
          //  openFragment(new SettingsFragment());
        }
        else if (itemId ==R.id.nav_about)
        {
            openFragment(new AboutUsFragment());
        }else if (itemId ==R.id.Leaderboard)
        {
            openFragment(new LeaderboardFragment());

        }
         */
        else if (itemId ==R.id.nav_logout)
        {
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(MainActivity.this, LogInPage.class);
            startActivity(i);
            finish();
        }



        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }


    private void openFragment(Fragment fragment)
    {
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container,fragment);
        transaction.commit();
    }
}
