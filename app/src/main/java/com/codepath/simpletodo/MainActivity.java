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

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class MainActivity extends Activity {

    ArrayList<String> items;
    ArrayAdapter<String> itemsAdapter;
    ListView lvItems;
    private final int REQUEST_CODE = 20;
    View currentView;
    TodoDatabaseHelper mTodoDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mTodoDbHelper = TodoDatabaseHelper.getsInstance(this);
        lvItems=(ListView) findViewById(R.id.lvItems);
        items= new ArrayList<String>();
        //readItems();
        readItemsDb();
        itemsAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,items);
        lvItems.setAdapter(itemsAdapter);
        setupListViewListener();
    }

    public void onAddItem(View v) {
        EditText editText = (EditText) findViewById(R.id.etNewItem);
        String text=editText.getText().toString();
        if(text != null && !text.equals("")) {
            itemsAdapter.add(text);
            editText.setText("");
            //writeItems();
            writeItemsDb(text);
        }
    }

    private void setupListViewListener() {
        lvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapter, View item, int pos, long id) {
                String str = items.get(pos);
                items.remove(pos);
                itemsAdapter.notifyDataSetChanged();
                //writeItems();
                //writeItemsDb();
                removeItemsDb(str);
                return true;
            }
        });
        lvItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapter, View item, int pos, long id) {
                Intent i = new Intent(MainActivity.this, EditItemActivity.class);
                i.putExtra("pos", pos);
                // pass arbitrary data to launched activity
                startActivityForResult(i, REQUEST_CODE);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_resource, menu);
        return true;
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
        //mTodoDbHelper.deleteAllTodos();
        //for(String str: items) {
            mTodoDbHelper.addTodo(str);
        //}
    }

    private void readItems() {
        File filesDir= getFilesDir();
        File file=new File(filesDir,"todo.txt");
        try {
            items = new ArrayList<String>(FileUtils.readLines(file));
        } catch(IOException e) {
            items = new ArrayList<String>();
        }
    }

    private void writeItems() {
        File filesDir= getFilesDir();
        File file=new File(filesDir,"todo.txt");
        try {
              FileUtils.writeLines(file, items);
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // REQUEST_CODE is defined above
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            // Extract name value from result extras
            String name = data.getExtras().getString("name");
            int pos = data.getExtras().getInt("pos",0);
            if(name != null && !name.equals("")) {
                Log.e("MainActivity", "KRS: new name = " + name);
                String oldStr = items.get(pos);
                items.remove(pos);
                items.add(pos, name);
                itemsAdapter.notifyDataSetChanged();
                //writeItems();
                removeItemsDb(oldStr);
                writeItemsDb(name);
            }
        }
    }
}