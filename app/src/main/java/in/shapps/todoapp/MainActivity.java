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
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

//import android.widget.AbsListView;
//import android.widget.AdapterView;
//import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private SQLController dbController;
    private Cursor cursor;
    private ListView listView;
    private ActionMode mActionMode;
    private String taskId;
    private int selectedItemCount=0;
    private ViewPager mViewPager;
    private MyPagerAdapter mAdapter;
    private TabLayout mtabLayout;
    private HashMap<Integer,List>listMap;
    private DrawerLayout drawerLayout;
    private NavigationView nav_draw;
    private Button mCreateListButton;
    private String newListName;
    private Fragment mContent;
    final String PREFS_NAME = "MyPrefsFile";
    final String RETRIEVE_FRAGMENT="prevFragmentId";
    private SharedPreferences sharedPreference;
    private final String CURRENT_FRAGMENT_ID="current_frag_id";
    private static final String CONTENT_AUTHORITY = "in.shapps.todoapp";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_LIST = "list1";
    public static final String PATH_TASK = "task1";
    private static final Uri LIST_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY+"/"+PATH_LIST);
    private static final Uri TASK_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY+"/"+PATH_TASK);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       /* if (savedInstanceState != null) {
            mContent = getSupportFragmentManager().getFragment(savedInstanceState, "mContent");
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragment_container, mContent);
            ft.commit();
        }*/
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
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open, R.string.drawer_close){

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawerLayout.setDrawerListener(actionBarDrawerToggle);
        nav_draw.setNavigationItemSelectedListener(this);

        //calling sync state is necessay or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
        //set user's name in the nav drawer
        TextView usernameTextView=(TextView)  findViewById(R.id.username);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String usernamePref = sharedPref.getString("example_text", "User");
        usernamePref="Hello "+usernamePref;
        usernameTextView.setText(usernamePref);
        //if (findViewById(R.id.fragment_container) != null) {

            // However, if we're being restored from a previous state,
            // then we don't need to do anything and should return or else
            // we could end up with overlapping fragments.


           /* // Create a new Fragment to be placed in the activity layout
            ListFragment firstFragment = new ListFragment();

            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            firstFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, firstFragment).commit();*/
            /*dbController = new SQLController(MainActivity.this);
            dbController.open();
            mViewPager=(ViewPager) findViewById(R.id.vpPager);
            cursor=dbController.fetchAllList();*/
            mViewPager=(ViewPager) findViewById(R.id.vpPager);
            cursor=getContentResolver().query(LIST_CONTENT_URI,null,null,null,null);
            cursor.moveToFirst();
            listMap=new HashMap<Integer,List>();
            int i=0;
            while (cursor.isAfterLast() == false) {
                if(listMap==null)
                    listMap=new HashMap<Integer,List>();
                listMap.put((i++),new List(cursor.getInt(0),cursor.getString(1)));
                cursor.moveToNext();
            }
            Log.d("DEBUG1", "In main activity listMap size=" + listMap.size());
            cursor.close();

            mAdapter=new MyPagerAdapter(getSupportFragmentManager(),listMap);
            mAdapter.notifyDataSetChanged();
            mViewPager.setAdapter(mAdapter);
            mtabLayout = (TabLayout) findViewById(R.id.tabs);
            mtabLayout.setupWithViewPager(mViewPager);
            mtabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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
            //retrieve previous fragment when returning to this activity
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            int fragIndex=settings.getInt(RETRIEVE_FRAGMENT, 0);
            mViewPager.setCurrentItem(fragIndex, false);

            //int index=mViewPager.getCurrentItem();
            //Log.d("DEBUG1","mViewPager returning index");
            /*mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

                // This method will be invoked when a new page becomes selected.
                @Override
                public void onPageSelected(int position) {
                    Toast.makeText(MainActivity.this,
                            "Selected page position: " + position, Toast.LENGTH_SHORT).show();
                }

                // This method will be invoked when the current page is scrolled
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    // Code goes here
                }

                // Called when the scroll state changes:
                // SCROLL_STATE_IDLE, SCROLL_STATE_DRAGGING, SCROLL_STATE_SETTLING
                @Override
                public void onPageScrollStateChanged(int state) {
                    // Code goes here
                }
            });*/
        //}

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
            final AlertDialog.Builder newListAlert = new AlertDialog.Builder(MainActivity.this);
            newListAlert.setTitle("Title of the Input Box");
            newListAlert.setMessage("Enter the name of list to create");
            final EditText mListName = new EditText(getApplicationContext());
            mListName.setTextColor(getResources().getColor(R.color.textColorPrimary));
            //mListName.setKeyListener(KeyListener.getInstance("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890 "));
            newListAlert.setView(mListName);
            newListAlert.setPositiveButton("Create",new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    newListName = mListName.getText().toString().trim();
                    if (newListName == null || newListName.length() == 0||(newListName.equals(" ")==true)) {
                        //mListName.setError("List name cannot be empty");
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "List name cannot be Empty", Toast.LENGTH_SHORT).show();

                    }
                    else if (newListName.length() > 10) {
                        //mListName.setError("List size can not be more than 10 characters");
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(), "List size can not be more than 10 characters", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        /*dbController = new SQLController(getApplicationContext());
                        dbController.open();
                        List l = new List();
                        l.setListName(newListName);
                        dbController.insertList(l);*/
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(DBHelper.TODO_LIST_NAME, newListName);
                        Uri uri = getContentResolver().insert(LIST_CONTENT_URI, contentValues);
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                }
            });
            newListAlert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog = newListAlert.create();
            alertDialog.show();

            //return true;
        }
        if(id == R.id.settings){
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
            //Toast.makeText(this, "you pressed settings option",Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        if(menuItem.getItemId() == R.id.settings) {
            //Toast.makeText(this,"u pressed Settings ",Toast.LENGTH_SHORT).show();
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);

        }
        menuItem.setChecked(true);
        drawerLayout.closeDrawers();

        return false;
    }
    private void checkFirstRun() {

        /*final String PREFS_NAME = "MyPrefsFile";
        final String PREF_VERSION_CODE_KEY = "version_code";
        final int DOESNT_EXIST = -1;


        // Get current version code
        int currentVersionCode = 0;
        try {
            currentVersionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (android.content.pm.PackageManager.NameNotFoundException e) {
            // handle exception
            e.printStackTrace();
            return;
        }

        // Get saved version code
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int savedVersionCode = prefs.getInt(PREF_VERSION_CODE_KEY, DOESNT_EXIST);

        // Check for first run or upgrade
        if (currentVersionCode == savedVersionCode) {

            // This is just a normal run
            return;

        } else if (savedVersionCode == DOESNT_EXIST) {
            // TODO This is a new install (or the user cleared the shared preferences)
            FragmentManager fragmentManager = getSupportFragmentManager();
            firstTimeFragment fragment=new firstTimeFragment();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();

        } else if (currentVersionCode > savedVersionCode) {

            // TODO This is an upgrade
            return;
        }

        // Update the shared preferences with the current version code
        prefs.edit().putInt(PREF_VERSION_CODE_KEY, currentVersionCode).commit();*/
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        if (settings.getBoolean("my_first_time", true)) {
            //the app is being launched for first time, do something
            Log.d("Comments", "First time");

            // first time task
            FragmentManager fragmentManager = getSupportFragmentManager();
            firstTimeFragment fragment=new firstTimeFragment();
            fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
            // record the fact that the app has been started at least once
            settings.edit().putBoolean("my_first_time", false).commit();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Save the fragment's instance
        //getSupportFragmentManager().putFragment(outState, "mContent", mContent);
    }

    /*class MyActionCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.contextual_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.item_delete:
                    dbController.delete(Integer.parseInt(taskId));
                    Intent home_intent = new Intent(MainActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(home_intent);
                    Toast.makeText(MainActivity.this, "delete task: "+taskId, Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.item_selectall:
                    Toast.makeText(MainActivity.this, "select all items", Toast.LENGTH_SHORT).show();
                   return true;
                default:
                    Toast.makeText(MainActivity.this, "invalid selection", Toast.LENGTH_SHORT).show();
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
        }
    }*/
    @Override
    protected void onPause(){
        super.onPause();
        int index=mViewPager.getCurrentItem();
        sharedPreference=getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreference.edit();
        editor.putInt(RETRIEVE_FRAGMENT,index);
        editor.commit();
        Log.d("DEBUG1", "onPause-> mViewPager returning index: "+index);
    }
    @Override
    protected void onResume(){
        super.onResume();
        //display name in nav drawer
        TextView usernameTextView=(TextView)  findViewById(R.id.username);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String usernamePref = sharedPref.getString("example_text", "User");
        usernamePref="Hello "+usernamePref;
        usernameTextView.setText(usernamePref);
        //restore original tab
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        int fragIndex=settings.getInt(RETRIEVE_FRAGMENT, 0);
        mViewPager.setCurrentItem(fragIndex, false);
    }
    @Override
    protected void onDestroy() {
        SQLController dbController=null;
        Cursor cursor=null;
        ListView listView=null;
        ActionMode mActionMode=null;
        String taskId=null;
        ViewPager mViewPager=null;
        MyPagerAdapter mAdapter=null;
        TabLayout mtabLayout=null;
        HashMap<Integer,List>listMap=null;
        DrawerLayout drawerLayout=null;
        Button mCreateListButton=null;
        String newListName=null;
        Fragment mContent=null;
        super.onDestroy();
        System.gc();
    }
}

