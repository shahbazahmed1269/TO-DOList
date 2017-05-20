package in.shapps.todoapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import static in.shapps.todoapp.TaskProvider.LIST_CONTENT_URI;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Cursor cursor;
    private ViewPager mViewPager;
    private MyPagerAdapter mAdapter;
    private TabLayout mtabLayout;
    private HashMap<Integer, List> listMap;
    private DrawerLayout drawerLayout;
    private NavigationView nav_draw;
    private String newListName;
    final String PREFS_NAME = "MyPrefsFile";
    final String RETRIEVE_FRAGMENT = "prevFragmentId";
    private SharedPreferences sharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        checkFirstRun();
        // Initializing Drawer Layout and ActionBarToggle
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer);
        nav_draw = (NavigationView) findViewById(R.id.main_drawer);
        nav_draw.setNavigationItemSelectedListener(this);
        // To show hamburger icon while toggling nav drawer
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        // Setting the actionbarToggle to drawer layout
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        nav_draw.setNavigationItemSelectedListener(this);

        // Calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
        //  Set user's name in the nav drawer
        TextView usernameTextView = (TextView) findViewById(R.id.username);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String usernamePref = sharedPref.getString("example_text", "User");
        usernamePref = "Welcome " + usernamePref;
        usernameTextView.setText(usernamePref);
        mViewPager = (ViewPager) findViewById(R.id.vpPager);
        cursor = getContentResolver().query(LIST_CONTENT_URI, null, null, null, null);
        cursor.moveToFirst();
        listMap = new HashMap<>();
        int i = 0;
        while (cursor.isAfterLast() == false) {
            if (listMap == null)
                listMap = new HashMap<>();
            listMap.put((i++), new List(cursor.getInt(0), cursor.getString(1)));
            cursor.moveToNext();
        }
        cursor.close();

        mAdapter = new MyPagerAdapter(getSupportFragmentManager(), listMap);
        mAdapter.notifyDataSetChanged();
        mViewPager.setAdapter(mAdapter);
        mtabLayout = (TabLayout) findViewById(R.id.tabs);
        mtabLayout.setupWithViewPager(mViewPager);
        mtabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }

        });
        // Retrieve previous fragment when returning to this activity
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        int fragIndex = settings.getInt(RETRIEVE_FRAGMENT, 0);
        mViewPager.setCurrentItem(fragIndex, false);

    }

    // Initiating Menu XML file (menu.xml)
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add_record) {
            final AlertDialog.Builder newListAlert = new AlertDialog.Builder(
                    this, R.style.DialogTheme
            );

            View dialogView = getLayoutInflater().inflate(R.layout.list_dialog, null);

            final EditText mListName = (EditText) dialogView.findViewById(R.id.et_list_name);
            mListName.setTextColor(ContextCompat.getColor(
                    MainActivity.this, R.color.textColorLight)
            );

            newListAlert.setTitle(getString(R.string.add_list))
                    .setView(dialogView)
                    .setPositiveButton(
                            getString(R.string.create),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    newListName = mListName.getText().toString().trim();
                                    if (newListName == null ||
                                            newListName.length() == 0 ||
                                            (newListName.equals(" ") == true)
                                            ) {
                                        //mListName.setError("List name cannot be empty");
                                        dialog.dismiss();
                                        Toast.makeText(
                                                getApplicationContext(),
                                                "List name cannot be Empty",
                                                Toast.LENGTH_SHORT
                                        ).show();

                                    } else if (newListName.length() > 10) {
                                        //mListName.setError("List size can not be more than 10 characters");
                                        dialog.dismiss();
                                        Toast.makeText(
                                                getApplicationContext(),
                                                "List size can not be more than 10 characters",
                                                Toast.LENGTH_SHORT
                                        ).show();
                                    } else {
                                        ContentValues contentValues = new ContentValues();
                                        contentValues.put(DBHelper.TODO_LIST_NAME, newListName);
                                        Uri uri = getContentResolver().insert(LIST_CONTENT_URI, contentValues);
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(intent);
                                    }
                                }
                            }
                    ).setNegativeButton(
                    getString(R.string.cancel),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }
            );
            AlertDialog alertDialog = newListAlert.create();
            alertDialog.show();
        }
        if (id == R.id.settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == R.id.settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);

        }
        menuItem.setChecked(true);
        drawerLayout.closeDrawers();

        return false;
    }

    private void checkFirstRun() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        if (settings.getBoolean("my_first_time", true)) {
            // The app is being launched for first time. Create a Default list
            ContentValues contentValues = new ContentValues();
            contentValues.put(
                    DBHelper.TODO_LIST_NAME,
                    getResources().getString(R.string.defult_list)
            );
            Uri uri = getContentResolver().insert(LIST_CONTENT_URI, contentValues);
            // Record the fact that the app has been started at least once
            settings.edit().putBoolean("my_first_time", false).commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onPause() {
        super.onPause();
        int index = mViewPager.getCurrentItem();
        sharedPreference = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreference.edit();
        editor.putInt(RETRIEVE_FRAGMENT, index);
        editor.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Display name in nav drawer
        TextView usernameTextView = (TextView) findViewById(R.id.username);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String usernamePref = sharedPref.getString("example_text", "User");
        usernamePref = "Hello " + usernamePref;
        usernameTextView.setText(usernamePref);
        // Restore original tab
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        int fragIndex = settings.getInt(RETRIEVE_FRAGMENT, 0);
        mViewPager.setCurrentItem(fragIndex, false);
    }

    @Override
    protected void onDestroy() {
        cursor = null;
        mViewPager = null;
        mAdapter = null;
        mtabLayout = null;
        listMap = null;
        drawerLayout = null;
        newListName = null;
        super.onDestroy();
        System.gc();
    }
}

