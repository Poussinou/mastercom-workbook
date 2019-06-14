package com.stypox.mastercom_workbook;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.LinearLayout;

import com.stypox.mastercom_workbook.data.MarkData;
import com.stypox.mastercom_workbook.data.SubjectData;
import com.stypox.mastercom_workbook.extractor.AuthenticationCallback;
import com.stypox.mastercom_workbook.extractor.Extractor;
import com.stypox.mastercom_workbook.extractor.FetchMarksCallback;
import com.stypox.mastercom_workbook.extractor.FetchSubjectsCallback;
import com.stypox.mastercom_workbook.login.LoginData;
import com.stypox.mastercom_workbook.login.LoginDialog;
import com.stypox.mastercom_workbook.view.MarksActivity;
import com.stypox.mastercom_workbook.view.SubjectItem;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final int requestCodeLoginDialog = 0;

    private int fetchedSubjectsSoFar;
    private boolean areSubjectsLoaded = false;

    private LinearLayout subjectsLayout;
    private SwipeRefreshLayout refreshLayout;
    private ArrayList<SubjectData> subjects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        subjectsLayout = findViewById(R.id.subjectsLayout);
        refreshLayout = findViewById(R.id.refreshLayout);

        if (!LoginData.isLoggedIn(getApplicationContext())) {
            openLoginDialogThenReload();
        } else {
            reloadSubjects();
        }

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                reloadSubjects();
            }
        });

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }


    ////////////////////
    // LOGIN AND LOAD //
    ////////////////////

    private void openLoginDialogThenReload() {
        Intent intent = new Intent(this, LoginDialog.class);
        startActivityForResult(intent, requestCodeLoginDialog); // see onActivityResult
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case requestCodeLoginDialog:
                reloadSubjects();
                break;
        }
    }

    private void reloadSubjects() {
        areSubjectsLoaded = false;
        refreshLayout.setRefreshing(true);
        subjectsLayout.removeAllViews();
        authenticate();
    }
    private void onReloadSubjectsCompleted(ArrayList<SubjectData> subjects) {
        areSubjectsLoaded = true;
        refreshLayout.setRefreshing(false);
        this.subjects = subjects;
    }


    /////////////
    // NETWORK //
    /////////////

    private void authenticate() {
        Extractor.authenticate(LoginData.getUser(getApplicationContext()), LoginData.getPassword(getApplicationContext()), new AuthenticationCallback() {
            @Override
            public void onAuthenticationCompleted(String fullName) {
                MainActivity.this.onAuthenticationCompleted(fullName);
            }

            @Override
            public void onError(Extractor.Error error) {
                Snackbar.make(findViewById(android.R.id.content),
                        error.toString(getApplicationContext()), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.retry), new View.OnClickListener() {
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

        fetchSubjects();
    }

    private void fetchSubjects() {
        Extractor.fetchSubjects(new FetchSubjectsCallback() {
            @Override
            public void onFetchSubjectsCompleted(ArrayList<SubjectData> subjects) {
                MainActivity.this.onFetchSubjectsCompleted(subjects);
            }

            @Override
            public void onError(Extractor.Error error) {
                Snackbar.make(findViewById(android.R.id.content), error.toString(getApplicationContext()), Snackbar.LENGTH_LONG).show();
            }
        });
    }
    private void onFetchSubjectsCompleted(final ArrayList<SubjectData> subjects) {
        fetchedSubjectsSoFar = 0;
        final Runnable onSubjectFetched = new Runnable() {
            @Override
            public void run() {
                ++fetchedSubjectsSoFar;
                if (fetchedSubjectsSoFar == subjects.size()) {
                    onReloadSubjectsCompleted(subjects);
                }
            }
        };

        for(SubjectData subjectData : subjects) {
            final SubjectItem subjectItem = new SubjectItem(getApplicationContext(), subjectData);

            subjectData.fetchMarks(new FetchMarksCallback() {
                @Override
                public void onFetchMarksCompleted(ArrayList<MarkData> marks) {
                    subjectItem.onMarksLoaded(marks);
                    onSubjectFetched.run();
                }

                @Override
                public void onError(Extractor.Error error) {
                    subjectItem.onMarksLoadingError(error);
                    onSubjectFetched.run();
                }
            });

            subjectsLayout.addView(subjectItem);
        }
    }


    ////////////////
    // ACTIVITIES //
    ////////////////

    private void openMarksActivity() {
        if (areSubjectsLoaded) {
            Intent intent = new Intent(this, MarksActivity.class);
            intent.putExtra(MarksActivity.subjectsIntentKey, subjects);
            startActivity(intent);
        } else {
            Snackbar.make(findViewById(android.R.id.content),
                    getString(R.string.error_marks_are_still_loading), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.retry), new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openMarksActivity();
                        }
                    }).show();
        }
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

        if (id == R.id.menu_login) {
            openLoginDialogThenReload();
        } else if (id == R.id.menu_marks) {
            openMarksActivity();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
