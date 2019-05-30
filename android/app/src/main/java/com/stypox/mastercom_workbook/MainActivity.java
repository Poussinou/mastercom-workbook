package com.stypox.mastercom_workbook;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.LinearLayout;

import com.stypox.mastercom_workbook.extractor.AuthenticationCallback;
import com.stypox.mastercom_workbook.extractor.Extractor;
import com.stypox.mastercom_workbook.extractor.FetchSubjectListCallback;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    LinearLayout subjectsLayout;
    SwipeRefreshLayout refreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        subjectsLayout = findViewById(R.id.subjectsLayout);
        refreshLayout = findViewById(R.id.refreshLayout);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                authenticate();
            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    subjectsLayout.addView(new Subject(getApplicationContext(), new JSONObject("{\"nome\": \"Italiano\", \"id\": \"ciao\"}")));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        /*FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Network error", Snackbar.LENGTH_LONG)
                        .setAction("Retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Snackbar.make(v, "Ciao", Snackbar.LENGTH_LONG).show();
                            }
                        }).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);*/
    }

    /////////////
    // NETWORK //
    /////////////

    private void authenticate() {
        Extractor.authenticate("", "", new AuthenticationCallback() {
            @Override
            public void onAuthenticationCompleted(String fullName) {
                MainActivity.this.onAuthenticationCompleted(fullName);
            }

            @Override
            public void onError(String error) {
                Snackbar.make(findViewById(android.R.id.content), error, Snackbar.LENGTH_LONG)
                        .setAction("Retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                authenticate();
                            }
                        }).show();
                refreshLayout.setRefreshing(false);
            }
        });
    }
    private void onAuthenticationCompleted(String fullName) {
        Snackbar.make(findViewById(android.R.id.content), "Authenticated " + fullName, Snackbar.LENGTH_LONG).show();
        refreshLayout.setRefreshing(false);

        fetchSubjects();
    }

    private void fetchSubjects() {
        Extractor.fetchSubjectList(new FetchSubjectListCallback() {
            @Override
            public void onFetchSubjectListCompleted(String first) {
                Snackbar.make(findViewById(android.R.id.content), "Subject " + first, Snackbar.LENGTH_LONG).show();
            }

            @Override
            public void onError(String error) {
                Snackbar.make(findViewById(android.R.id.content), error, Snackbar.LENGTH_LONG).show();
            }
        });
    }

    ////////////////
    // GUI EVENTS //
    ////////////////

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.menu_subjects) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
