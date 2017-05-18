package in.shapps.todoapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TimePicker;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static in.shapps.todoapp.TaskProvider.TASK_CONTENT_URI;


public class AddTodoActivity extends AppCompatActivity {
    private Button addTodoButton;
    private SQLController dbController;
    private EditText subjectEditText;
    private EditText descEdittext;
    private Switch alarmSwitch;
    private DatePicker mDatePicker;
    private TimePicker mTimePicker;
    private String alarmStatus="off";
    private String date1;
    String listId;

    // Fields related to SharedPreferences to persist pendingId
    private int pendingId=0;
    public static final String myPreference = "in.shapps.todoapp.pendingIdPref" ;
    private SharedPreferences sp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_record);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_add_record);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toolbar.setTitle("Add Task");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        Bundle extras = getIntent().getExtras();
        listId= extras.getString("IN.SHAPPS.TODOLIST.LISTID");
        subjectEditText=(EditText) findViewById(R.id.subject_edittext);
        descEdittext=(EditText) findViewById(R.id.description_edittext);
        alarmSwitch=(Switch) findViewById(R.id.alarm_switch);
        alarmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    DatePicker datePicker = (DatePicker) findViewById(R.id.alarm_date);
                    datePicker.setVisibility(View.VISIBLE);
                    TimePicker timePicker = (TimePicker) findViewById(R.id.alarm_time);
                    timePicker.setVisibility(View.VISIBLE);
                    alarmStatus="on";

                } else {
                    DatePicker datePicker = (DatePicker) findViewById(R.id.alarm_date);
                    datePicker.setVisibility(View.GONE);
                    TimePicker timePicker = (TimePicker) findViewById(R.id.alarm_time);
                    timePicker.setVisibility(View.GONE);
                    alarmStatus="off";
                }

            }
        });
        addTodoButton=(Button) findViewById(R.id.add_record);
        dbController=new SQLController(getApplicationContext());
        dbController.open();
        addTodoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.add_record:
                        Task task = new Task();
                        if(listId!=null) {
                            Log.e("HELLO","inside add activity with listId"+listId);
                            task.setListID(Integer.parseInt(listId));
                        }
                        if(subjectEditText.getText().toString().trim().length() == 0 )
                            subjectEditText.setError( "Title is required!" );
                        else {
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(DBHelper.TODO_LIST_ID, listId);
                            contentValues.put(
                                    DBHelper.TODO_SUBJECT,subjectEditText.getText().toString().trim()
                            );
                            contentValues.put(
                                    DBHelper.TODO_DESC,descEdittext.getText().toString().trim()
                            );
                            contentValues.put(DBHelper.TODO_ALARM_STATUS,alarmStatus);
                            if(alarmStatus=="on") {
                                mDatePicker=(DatePicker) findViewById(R.id.alarm_date);
                                mTimePicker=(TimePicker) findViewById(R.id.alarm_time);
                                Calendar calendar=Calendar.getInstance();
                                int day=mDatePicker.getDayOfMonth();
                                int month= mDatePicker.getMonth();
                                int year=mDatePicker.getYear();
                                int hour=mTimePicker.getCurrentHour();
                                int min=mTimePicker.getCurrentMinute();
                                calendar.set(year,month,day,hour,min,0);
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
                                date1=sdf.format(calendar.getTime());
                                Log.d("DEBUG1", "alarm set for datetime " + date1);
                                contentValues.put(DBHelper.TODO_DATETIME, date1);
                                Intent myIntent = new Intent(
                                        AddTodoActivity.this, OnAlarmReceiver.class
                                );
                                myIntent.putExtra(
                                        DBHelper.TODO_DESC,
                                        subjectEditText.getText().toString().trim()
                                );
                                sp=getSharedPreferences(myPreference, Context.MODE_PRIVATE);
                                pendingId=sp.getInt("pendingId",0);
                                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                                        AddTodoActivity.this,
                                        pendingId, myIntent,
                                        PendingIntent.FLAG_UPDATE_CURRENT
                                );
                                pendingId++;
                                SharedPreferences.Editor editor = sp.edit();
                                editor.putInt("pendingId", pendingId);
                                editor.commit();
                                AlarmManager alarmManager = (AlarmManager)getSystemService(
                                        Context.ALARM_SERVICE
                                );
                                alarmManager.set(
                                        AlarmManager.RTC, calendar.getTimeInMillis(), pendingIntent
                                );
                                // Enabling the broadcast receiver programmatically
                                ComponentName receiver = new ComponentName(
                                        getApplicationContext(),
                                        OnAlarmReceiver.class
                                );
                                PackageManager pm = getApplicationContext().getPackageManager();

                                pm.setComponentEnabledSetting(receiver,
                                        PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                                        PackageManager.DONT_KILL_APP);
                            }
                            contentValues.put(DBHelper.TODO_TASK_STATUS, "incomplete");
                            contentValues.put(DBHelper.TODO_TASK_FAVOURTIE,"false");
                            Uri returnUri = ContentUris.withAppendedId(
                                    TASK_CONTENT_URI,
                                    Integer.parseInt(listId)
                            );
                            Uri uri = getContentResolver().insert(returnUri, contentValues);
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class)
                                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                        }
                        break;
                    default:
                        break;
                }
            }
        });
    }

}
