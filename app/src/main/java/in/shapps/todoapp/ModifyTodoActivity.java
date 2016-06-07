package in.shapps.todoapp;
import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ModifyTodoActivity extends AppCompatActivity implements OnClickListener {
    private EditText titleText,descText;
    private Button updateBtn, deleteBtn;
    private long _id;
    private SQLController dbController;
    private TextView dueDateText ;
    private static final String CONTENT_AUTHORITY = "in.shapps.todoapp";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_LIST = "list1";
    public static final String PATH_TASK = "task1";
    private static final Uri LIST_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY+"/"+PATH_LIST);
    private static final Uri TASK_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY+"/"+PATH_TASK);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_todo);
        //Toolbar initialization
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_modify);
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

        //dbController = new SQLController(this);
        //dbController.open();
        titleText = (EditText) findViewById(R.id.subject_edittext);
        descText = (EditText) findViewById(R.id.description_edittext);
        updateBtn = (Button) findViewById(R.id.btn_update);
        deleteBtn = (Button) findViewById(R.id.btn_delete);
        dueDateText=(TextView)findViewById(R.id.due_date);
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        String name = intent.getStringExtra("title");
        String desc = intent.getStringExtra("desc");
        _id = Long.parseLong(id);
        titleText.setText(name);
        if(desc==null)
            desc="";
        descText.setText(desc);
        dueDateText.setText(intent.getStringExtra("dueDate"));
        updateBtn.setOnClickListener(this);
        deleteBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_update:
                if(titleText.getText().toString().trim().length() == 0 )
                    titleText.setError( "Title is required!" );
                /*else if(descText.getText().toString().trim().length() == 0 )
                    titleText.setError( "Description is required!" );*/
                else {
                    /*Task task = new Task();
                    task.setTitle(titleText.getText().toString());
                    task.setDesc(descText.getText().toString());
                    dbController.updateTask(_id, task);*/
                    ContentValues contentValues = new ContentValues();
                    //contentValues.put(DBHelper.TODO_LIST_ID, _id);
                    contentValues.put(DBHelper.TODO_SUBJECT,titleText.getText().toString().trim());
                    contentValues.put(DBHelper.TODO_DESC,descText.getText().toString().trim());
                    //contentValues.put(DBHelper.TODO_ALARM_STATUS,"off");
                    //contentValues.put(DBHelper.TODO_TASK_STATUS,"incomplete");
                    Uri returnUri = ContentUris.withAppendedId(TASK_CONTENT_URI, _id);
                    int id = getContentResolver().update(returnUri, contentValues,null,null);
                    Toast.makeText(this,"Task modified having id"+_id,Toast.LENGTH_SHORT).show();
                    this.returnHome();
                }
                break;
            case R.id.btn_delete:
                //dbController.deleteTask(_id);
                Uri returnUri = ContentUris.withAppendedId(TASK_CONTENT_URI, _id);
                int id = getContentResolver().delete(returnUri,null,null );
                Toast.makeText(this,"Task deleted having id"+_id,Toast.LENGTH_SHORT).show();
                this.returnHome();
                break;
        }
    }

    public void returnHome() {
        Intent home_intent = new Intent(getApplicationContext(),
                MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(home_intent);
    }
}