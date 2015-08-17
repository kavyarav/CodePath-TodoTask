package com.codepath.simpletodo;

import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by karthikravishankar on 8/16/15.
 */
public class TodoDatabaseHelper extends SQLiteOpenHelper {

    private static final String  TAG = "TodoDatabaseHelper";

    //Database data.
    public static final String DATABASE_NAME = "todoDatabase";
    public static final int DATABASE_VERSION = 1;

    //Table
    private static final String TABLE_TODO = "todoTable";
    private static int todoTableSize = 0;

    //Columns
    private static final String KEY_TODO_TEXT = "todoItem";

    private static TodoDatabaseHelper sInstance;

    public static synchronized TodoDatabaseHelper getsInstance(Context ctx) {
        if(sInstance == null) {
            sInstance = new TodoDatabaseHelper(ctx.getApplicationContext());
        }
        return sInstance;
    }

    private TodoDatabaseHelper(Context ctx) {
        super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "onCreate called for db!");
        String CREATE_TODO_TABLE = "CREATE TABLE " + TABLE_TODO +
                "(" +
                KEY_TODO_TEXT + " TEXT PRIMARY KEY" +
                ")";

        db.execSQL(CREATE_TODO_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO);
            onCreate(db);
        }
    }

    public void addTodo(String todo) {
        Log.d(TAG, "addTodo for = " + todo);
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        try {
            ContentValues cv = new ContentValues();
            cv.put(KEY_TODO_TEXT, todo);

            long ret = db.insertOrThrow(TABLE_TODO, null, cv);
            db.setTransactionSuccessful();
        } catch(Exception ex) {
            Log.d("TodoDatabaseHelper", "Error while adding to todo db");
            ex.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    public void deleteTodo(String str) {
        Log.d(TAG, "deleteTodo: " + str);
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            int ret = db.delete(TABLE_TODO, KEY_TODO_TEXT + "='" + str + "'", null);
            db.setTransactionSuccessful();
            Log.e(TAG, "deleteTodo for: " + str + ", ret = " + ret);
        } catch(Exception ex) {
            Log.e(TAG, "Exception while deleting: ", ex);
        } finally {
            db.endTransaction();
        }
    }

    public void deleteAllTodos() {
        Log.d(TAG, "deleteAllTodo!");
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            int ret = db.delete(TABLE_TODO, null, null);
            Log.d(TAG, "deleteAllTodos returns = " + ret);
        } catch(Exception ex) {
            Log.e(TAG, "EXception in deleteTodo");
            ex.printStackTrace();
        } finally {
            db.endTransaction();
        }
    }

    public ArrayList<String> getAllTodo() {
        Log.d(TAG, "getAllTodo called!");
        ArrayList<String> todos = new ArrayList<String>();
        String TODOS_SELECT_QUERY =
                String.format("SELECT * FROM %s",
                        TABLE_TODO);

        SQLiteDatabase db = getReadableDatabase();
        Cursor cr = db.rawQuery(TODOS_SELECT_QUERY, null);
        try {
            if(cr.moveToFirst()) {
                do{
                    String todoString = cr.getString(cr.getColumnIndex(KEY_TODO_TEXT));
                    Log.e(TAG, "getAllTodo: " + todoString);
                    todos.add(todoString);
                } while(cr.moveToNext());
            }
        } catch(Exception ex) {
            Log.e(TAG, "Error while moving across db", ex);
        } finally {
            if(cr != null && !cr.isClosed()) {
                cr.close();
            }
        }
        return todos;
    }
}
