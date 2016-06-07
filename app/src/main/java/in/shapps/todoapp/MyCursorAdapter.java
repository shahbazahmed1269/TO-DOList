package in.shapps.todoapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by James on 2/20/2016.
 */
public class MyCursorAdapter extends SimpleCursorAdapter {
    private static final String CONTENT_AUTHORITY = "in.shapps.todoapp";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_LIST = "list1";
    public static final String PATH_TASK = "task1";
    private static final Uri LIST_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY+"/"+PATH_LIST);
    private static final Uri TASK_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY+"/"+PATH_TASK);

    public MyCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
        super(context, layout, c, from, to);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.activity_view_record, parent, false);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView idTextView,titleTextView, descTextView,dateTextView;
        idTextView=(TextView) view.findViewById(R.id.id);
        idTextView.setText(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.TASK_ID)));
        titleTextView=(TextView) view.findViewById(R.id.title);
        titleTextView.setText(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.TODO_SUBJECT)));
        descTextView=(TextView) view.findViewById(R.id.desc);
        descTextView.setText(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.TODO_DESC)));
        final CheckBox completeCheckBox=(CheckBox) view.findViewById(R.id.mark_complete_image);
        //display date and time
        dateTextView=(TextView) view.findViewById(R.id.task_date);
        if(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.TODO_ALARM_STATUS)).equalsIgnoreCase("on")){
            try {
                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ");
                Date date = sdf.parse(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.TODO_DATETIME)));
                SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("dd/MM/yy, hh:mm a");
                String date1 = simpleDateFormat1.format(date);
                dateTextView.setText(date1);
                ImageView alarmIcon=(ImageView) view.findViewById(R.id.alarm_icon);
                alarmIcon.setVisibility(View.VISIBLE);
            }
            catch(Exception e){

            }
        }
        else{
            dateTextView.setText("");
            ImageView alarmIcon=(ImageView) view.findViewById(R.id.alarm_icon);
            alarmIcon.setVisibility(View.GONE);
        }
        String s;
        final Context context1=context;
        /*completeCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(DBHelper.TODO_TASK_STATUS, "complete");
                    Uri returnUri = ContentUris.withAppendedId(TASK_CONTENT_URI, mListId);
                    int id = context1.getContentResolver().update(returnUri, contentValues, null, null);
                    Toast.makeText(context1, "task marked as complete", Toast.LENGTH_SHORT).show();
                } else {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(DBHelper.TODO_TASK_STATUS, "incomplete");
                    Uri returnUri = ContentUris.withAppendedId(TASK_CONTENT_URI, mListId);
                    int id = context1.getContentResolver().update(returnUri, contentValues, null, null);
                    Toast.makeText(context1, "task marked as incomplete", Toast.LENGTH_SHORT).show();
                }

            }
        });*/


        final int taskId=Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.TASK_ID)));
        completeCheckBox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (completeCheckBox.isChecked()) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(DBHelper.TODO_TASK_STATUS, "complete");
                    Uri returnUri = ContentUris.withAppendedId(TASK_CONTENT_URI, taskId);
                    int id = context1.getContentResolver().update(returnUri, contentValues, null, null);
                    //Toast.makeText(context1, "task marked as complete", Toast.LENGTH_SHORT).show();
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context1);
                    Boolean deletePref = sharedPref.getBoolean("example_switch", false);
                    if(deletePref==true){
                        Log.d("DEBUG1", "Deleting completed task");
                        returnUri = ContentUris.withAppendedId(TASK_CONTENT_URI, id);
                        id = context1.getContentResolver().delete(returnUri, null, null);
                        Toast.makeText(context1,"Task deleted having id"+id,Toast.LENGTH_SHORT).show();
                        Intent home_intent = new Intent(context1,
                                MainActivity.class)
                                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        home_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        home_intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                        context1.startActivity(home_intent);
                    }
                } else {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(DBHelper.TODO_TASK_STATUS, "incomplete");
                    Uri returnUri = ContentUris.withAppendedId(TASK_CONTENT_URI, taskId);
                    int id = context1.getContentResolver().update(returnUri, contentValues, null, null);
                    Toast.makeText(context1, "task marked as incomplete"+id, Toast.LENGTH_SHORT).show();
                }
            }
        });
        final CheckBox completeCheckBox1=(CheckBox) view.findViewById(R.id.mark_complete_image);
        s=cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.TODO_TASK_STATUS));
        if (s.equals("complete"))
            completeCheckBox1.setChecked(true);
        else
            completeCheckBox1.setChecked(false);


    }
}