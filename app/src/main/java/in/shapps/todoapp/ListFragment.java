package in.shapps.todoapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import static in.shapps.todoapp.TaskProvider.LIST_CONTENT_URI;
import static in.shapps.todoapp.TaskProvider.TASK_CONTENT_URI;

public class ListFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>{

    private static final String ARG_PARAM1 = "listId";
    private Cursor cursor;
    private ListView listView;
    private ActionMode mActionMode;
    private String taskId;
    private int selectedItemCount = 0;
    private Button mDeleteButton;
    private String newListName;
    private int mListId;
    private View view;
    private MyCursorAdapter myAdapter;

    private FloatingActionButton fab1;
    private FloatingActionButton fab2;
    private FloatingActionButton fab3;

    private Handler mUiHandler;

    public ListFragment() {
        // Required empty public constructor
    }

    public static ListFragment newInstance(int param1) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mListId = getArguments().getInt(ARG_PARAM1);
        }
        if(mUiHandler==null)
            mUiHandler = new Handler();
        getLoaderManager().restartLoader(1, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_list, container, false);

        final FloatingActionMenu menu1 = (FloatingActionMenu) view.findViewById(R.id.menu);

        final FloatingActionButton programFab1 = new FloatingActionButton(getActivity());
        programFab1.setButtonSize(FloatingActionButton.SIZE_MINI);
        programFab1.setLabelText("Add New List");
        programFab1.setImageResource(R.drawable.ic_add_white_48dp);
        programFab1.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));
        menu1.addMenuButton(programFab1);
        programFab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder newListAlert = new AlertDialog.Builder(getActivity());
                newListAlert.setTitle("Title of the Input Box");
                newListAlert.setMessage("Enter the name of list to create");
                final EditText mListName = new EditText(getActivity().getApplicationContext());
                newListAlert.setView(mListName);
                newListAlert.setPositiveButton("Create", new DialogInterface.OnClickListener() {
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
                                    getActivity(),
                                    "List name cannot be Empty",
                                    Toast.LENGTH_SHORT
                            ).show();

                        } else if (newListName.length() > 10) {
                            //mListName.setError("List size can not be more than 10 characters");
                            dialog.dismiss();
                            Toast.makeText(
                                    getActivity(),
                                    "List size can not be more than 10 characters",
                                    Toast.LENGTH_SHORT
                            ).show();
                        } else {
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(DBHelper.TODO_LIST_NAME, newListName);
                            Uri uri = getActivity().getContentResolver()
                                    .insert(LIST_CONTENT_URI, contentValues);
                            Intent intent = new Intent(getActivity(), MainActivity.class);
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
            }
        });

        menu1.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu1.toggle(true);
            }
        });


        menu1.hideMenuButton(false);

        int delay = 400;
        mUiHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                menu1.showMenuButton(true);
            }
        }, delay);
        delay += 150;

        menu1.setClosedOnTouchOutside(true);


        fab1 = (FloatingActionButton) view.findViewById(R.id.menu_item1);
        fab2 = (FloatingActionButton) view.findViewById(R.id.menu_item2);
        fab3 = (FloatingActionButton) view.findViewById(R.id.menu_item3);

        fab1.setEnabled(false);
        fab1.setOnClickListener(clickListener);
        fab2.setOnClickListener(clickListener);
        fab3.setOnClickListener(clickListener);
        createCustomAnimation();

        listView = (ListView) view.findViewById(R.id.list_view);
        Uri returnUri = ContentUris.withAppendedId(TASK_CONTENT_URI, mListId);
        cursor=getActivity().getApplicationContext()
                .getContentResolver().query(returnUri,null,null,null,null);
        String[] from = new String[]{
                DBHelper.TASK_ID,
                DBHelper.TODO_SUBJECT,
                DBHelper.TODO_DESC,
                DBHelper.TODO_TASK_STATUS,
                DBHelper.TODO_DATETIME
        };
        int[] to = new int[]{R.id.id, R.id.title, R.id.desc,R.id.mark_complete_image,R.id.task_date};
        myAdapter=new MyCursorAdapter(
                getActivity().getApplicationContext(),
                R.layout.activity_view_record,
                cursor,
                from,
                to
        );
        listView.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged();

        getActivity().getSupportLoaderManager().initLoader(1, null, this);
        listView.setEmptyView(view.findViewById(R.id.empty));
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long viewId) {
                TextView id_tv = (TextView) view.findViewById(R.id.id);
                TextView title_tv = (TextView) view.findViewById(R.id.title);
                TextView desc_tv = (TextView) view.findViewById(R.id.desc);
                TextView dueDate = (TextView) view.findViewById(R.id.task_date);

                taskId = id_tv.getText().toString();
                String title = title_tv.getText().toString();
                String desc = desc_tv.getText().toString();

                Intent modify_intent = new Intent(getActivity().getApplicationContext(),
                        ModifyTodoActivity.class);
                modify_intent.putExtra("title", title);
                modify_intent.putExtra("desc", desc);
                modify_intent.putExtra("id", taskId);
                modify_intent.putExtra("dueDate", dueDate.getText().toString());
                startActivity(modify_intent);
            }
        });

        mDeleteButton = (Button) view.findViewById(R.id.delete_button);
        mDeleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Uri returnUri = ContentUris.withAppendedId(LIST_CONTENT_URI, mListId);
                int id = getActivity().getApplicationContext()
                        .getContentResolver().delete(returnUri, null, null);
                Toast.makeText(
                        getActivity().getApplicationContext(),
                        "List deleted with list_id= " + mListId,
                        Toast.LENGTH_SHORT
                ).show();
                Intent intent = new Intent(
                        getActivity().getApplicationContext(), MainActivity.class
                );
                startActivity(intent);
            }
        });

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(
                    ActionMode mode,
                    int position,
                    long id,
                    boolean checked
            ) {
                if (checked == true) {
                    selectedItemCount++;
                } else {
                    selectedItemCount--;
                }


                mode.setTitle(selectedItemCount + " Items Selected");


            }

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
                        SparseBooleanArray checked = listView.getCheckedItemPositions();
                        int size = checked.size(); // number of name-value pairs in the array
                        for (int i = 0; i < size; i++) {
                            int key = checked.keyAt(i);
                            boolean value = checked.get(key);
                            cursor.moveToPosition(key);
                            int id = cursor.getInt(cursor.getColumnIndex("_id"));
                            if (value) {
                                Uri returnUri = ContentUris.withAppendedId(TASK_CONTENT_URI, id);
                                id = getActivity().getApplicationContext()
                                        .getContentResolver().delete(returnUri,null,null);
                            }
                        }
                        Intent home_intent = new Intent(
                                getActivity().getApplicationContext(),
                                MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        );
                        startActivity(home_intent);
                        return true;
                    case R.id.item_selectall:
                        Toast.makeText(
                                getActivity().getApplicationContext(),
                                "select all items",
                                Toast.LENGTH_SHORT
                        ).show();
                        return true;
                    default:
                        Toast.makeText(
                                getActivity().getApplicationContext(),
                                "invalid selection",
                                Toast.LENGTH_SHORT
                        ).show();
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                SparseBooleanArray checked = listView.getCheckedItemPositions();
                selectedItemCount = 0;
                mActionMode = null;
            }

        });

        // Inflate the layout for this fragment
        return view;
    }
    @Override
    public void onDestroyView() {
        mUiHandler=null;
        cursor=null;
        listView=null;
        mActionMode=null;
        taskId=null;
        mDeleteButton=null;
        newListName=null;
        view=null;
        fab1=null;
       fab2=null;
        fab3=null;
        super.onDestroyView();
        System.gc();
    }

    private void createCustomAnimation() {
        final FloatingActionMenu menu3 = (FloatingActionMenu) view.findViewById(R.id.menu);

        AnimatorSet set = new AnimatorSet();

        ObjectAnimator scaleOutX = ObjectAnimator.ofFloat(
                menu3.getMenuIconView(),
                "scaleX", 1.0f, 0.2f
        );
        ObjectAnimator scaleOutY = ObjectAnimator.ofFloat(
                menu3.getMenuIconView(), "scaleY", 1.0f, 0.2f
        );

        ObjectAnimator scaleInX = ObjectAnimator.ofFloat(
                menu3.getMenuIconView(), "scaleX", 0.2f, 1.0f
        );
        ObjectAnimator scaleInY = ObjectAnimator.ofFloat(
                menu3.getMenuIconView(), "scaleY", 0.2f, 1.0f
        );

        scaleOutX.setDuration(50);
        scaleOutY.setDuration(50);

        scaleInX.setDuration(150);
        scaleInY.setDuration(150);

        scaleInX.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                menu3.getMenuIconView().setImageResource(menu3.isOpened()
                        ? R.drawable.ic_arrow_upward_white_24dp :
                        R.drawable.ic_arrow_downward_white_24dp);
            }
        });

        set.play(scaleOutX).with(scaleOutY);
        set.play(scaleInX).with(scaleInY).after(scaleOutX);
        set.setInterpolator(new OvershootInterpolator(2));

        menu3.setIconToggleAnimatorSet(set);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mActionMode.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //String text = "";

            switch (v.getId()) {
                case R.id.menu_item1:
                    final AlertDialog.Builder newListAlert = new AlertDialog.Builder(getActivity());
                    newListAlert.setTitle("Title of the Input Box");
                    newListAlert.setMessage("Enter the name of list to create");
                    final EditText mListName = new EditText(getActivity().getApplicationContext());
                    newListAlert.setView(mListName);
                    newListAlert.setPositiveButton("Create",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            newListName = mListName.getText().toString().trim();
                            if (newListName == null ||
                                    newListName.length() == 0||
                                    (newListName.equals(" ")==true)
                            ) {
                                //mListName.setError("List name cannot be empty");
                                dialog.dismiss();
                                Toast.makeText(
                                        getActivity().getApplicationContext(),
                                        "List name cannot be Empty",
                                        Toast.LENGTH_SHORT
                                ).show();

                            }
                            else if (newListName.length() > 10) {
                                //mListName.setError("List size can not be more than 10 characters");
                                dialog.dismiss();
                                Toast.makeText(
                                        getActivity().getApplicationContext(),
                                        "List size can not be more than 10 characters",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                            else {
                                ContentValues contentValues = new ContentValues();
                                contentValues.put(DBHelper.TODO_LIST_NAME, newListName);
                                Uri uri = getActivity().getApplicationContext()
                                        .getContentResolver()
                                        .insert(LIST_CONTENT_URI, contentValues);
                                Intent intent = new Intent(
                                        getActivity().getApplicationContext(),
                                        MainActivity.class
                                );
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
                    break;
                case R.id.menu_item2:
                    //text = fab2.getLabelText();
                    fab2.setVisibility(View.GONE);
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                    builder1.setMessage("Are you sure you want to delete the current list?");
                    builder1.setCancelable(true);

                    builder1.setPositiveButton(
                            "Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Uri returnUri = ContentUris.withAppendedId(LIST_CONTENT_URI, mListId);
                                    id = getActivity().getApplicationContext()
                                            .getContentResolver().delete(returnUri,null,null );
                                    Toast.makeText(
                                            getActivity(),
                                            "List deleted with list_id= " + mListId,
                                            Toast.LENGTH_SHORT
                                    ).show();
                                    Intent intent = new Intent(
                                            getActivity().getApplicationContext(),
                                            MainActivity.class
                                    );
                                    startActivity(intent);
                                }
                            });

                    builder1.setNegativeButton(
                            "No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alert11 = builder1.create();
                    alert11.show();

                    break;
                case R.id.menu_item3:
                    fab2.setVisibility(View.VISIBLE);
                    Intent add_mem = new Intent(
                            getActivity().getApplicationContext(),
                            AddTodoActivity.class
                    );
                    add_mem.putExtra("IN.SHAPPS.TODOLIST.LISTID", mListId+"");
                    startActivity(add_mem);
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
                    break;
            }
        }
    };

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // This is called when a new Loader needs to be created
        Uri CONTENT_URI=ContentUris.withAppendedId(TASK_CONTENT_URI, mListId);
        return new CursorLoader(getActivity(), CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Swap the new cursor in.  (The framework will take care of closing the
        // old cursor once we return.)
        myAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed.  We need to make sure we are no
        // longer using it.
        myAdapter.swapCursor(null);
    }


}