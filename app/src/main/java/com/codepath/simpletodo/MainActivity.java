package com.codepath.simpletodo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class MainActivity extends Activity {

    ArrayList<String> items;
    CustomAdapter itemsAdapter;
    ListView lvItems;
    private final int REQUEST_CODE = 20;
    TodoDatabaseHelper mTodoDbHelper;

    public void onListViewClick(View v) {
        Log.d("MainActivity", "onListViewClick");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basic);
        //setContentView(R.layout.activity_alternate_list);
        mTodoDbHelper = TodoDatabaseHelper.getsInstance(this);
        lvItems=(ListView) findViewById(R.id.lvItems);
        items= new ArrayList<String>();
        //readItems();
        readItemsDb();
        itemsAdapter=new CustomAdapter(items, this);
        lvItems.setAdapter(itemsAdapter);
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapter, View item, int pos, long id) {
                Log.d("MainActivity", "onItemLongClick");
                String str = items.get(pos);
                items.remove(pos);
                itemsAdapter.notifyDataSetChanged();
                removeItemsDb(str);
                return true;
            }
        });
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View item, int pos, long id) {
                Log.d("MainActivity", "onItemClick");
                Intent i = new Intent(MainActivity.this, EditItemActivity.class);
                i.putExtra("pos", pos);
                // pass arbitrary data to launched activity
                startActivityForResult(i, REQUEST_CODE);
            }
        });
    }

    public void onAddAlarm(View v) {
        Log.d("MainActivity", "KRS: onAddAlarm");
        Toast.makeText(this, "Alarm add button clicked", Toast.LENGTH_SHORT);
    }

    public void onAddItem(View v) {
        Log.d("MainActivity", "onAddItem0");
        EditText editText = (EditText) findViewById(R.id.etNewItem);
        String text=editText.getText().toString();
        if(text != null && !text.equals("") && !items.contains(text)) {
            Log.d("MainActivity", "onAddItem");
            itemsAdapter.add(text);
            itemsAdapter.notifyDataSetChanged();
            editText.setText("");
            writeItemsDb(text);
        } else {
            Log.d("MainActivity", "text = " + text);
        }
    }

    public void onItemClick(View v) {
        Log.d("MainActivity", "onItemClick for: " + v);
    }

    //Read all elements of db into items.
    private void readItemsDb() {
        items = mTodoDbHelper.getAllTodo();
    }

    private void removeItemsDb(String str) {
        mTodoDbHelper.deleteTodo(str);
    }
    //Re-insert all the elements into db.
    private void writeItemsDb(String str) {
        mTodoDbHelper.addTodo(str);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            // Extract name value from result extras
            String name = data.getExtras().getString("name");
            int pos = data.getExtras().getInt("pos",0);
            if(name != null && !name.equals("") && !items.contains(name)) {
                Log.e("MainActivity", "KRS: new name = " + name);
                String oldStr = items.get(pos);
                items.remove(pos);
                items.add(pos, name);
                itemsAdapter.notifyDataSetChanged();
                removeItemsDb(oldStr);
                writeItemsDb(name);
            }
        }
    }
}