package in.shapps.todoapp;

import android.content.Intent;
import android.database.Cursor;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class TodoActivity extends AppCompatActivity {
    private SQLController dbController;
    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);
        dbController=new SQLController(this);
        dbController.open();
        listView=(ListView) findViewById(R.id.list_view);
        listView.setEmptyView(findViewById(R.id.empty));
        // Add data from database into listview using cursor adapter
        Cursor cursor=dbController.fetchAllTask(1);
        String[] from=new String[] { DBHelper.TASK_ID,DBHelper.TODO_SUBJECT,DBHelper.TODO_DESC};
        int[] to=new int[] { R.id.id, R.id.title,R.id.desc};
        SimpleCursorAdapter adapter=new SimpleCursorAdapter(this,R.layout.activity_view_record,cursor,from,to);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long viewId) {
                TextView id_tv = (TextView) view.findViewById(R.id.id);
                TextView title_tv = (TextView) view.findViewById(R.id.title);
                TextView desc_tv = (TextView) view.findViewById(R.id.desc);

                String id = id_tv.getText().toString();
                String title = title_tv.getText().toString();
                String desc = desc_tv.getText().toString();

                Intent modify_intent = new Intent(getApplicationContext(),
                        ModifyTodoActivity.class);
                modify_intent.putExtra("title", title);
                modify_intent.putExtra("desc", desc);
                modify_intent.putExtra("id", id);
                startActivity(modify_intent);
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add_record) {
            Intent add_mem = new Intent(this, AddTodoActivity.class);
            startActivity(add_mem);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
