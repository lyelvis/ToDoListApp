package edu.gmu.cs477.lab5_ely4;

import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class ListDataActivity extends AppCompatActivity {
    private static final String TAG = "ListDataActivity";

    DatabaseHelper mDatabaseHelper;
    ListView mListView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView)findViewById(R.id.to_do_list);//find list view in interface.

        mDatabaseHelper = new DatabaseHelper(this);//create DatabaseHelper.

        populateListView();// call method below.

    }

    /**
     * Populate the ListView in UI, re-establishing the database into
     * the UI.
     */
    private void populateListView(){
        Log.d(TAG, "populateListView: Displaying data in the ListView. ");

        //get the data and append to a list
        Cursor data = mDatabaseHelper.getData(); //Initialize Cursor Data, use DatabaseHelper to get Data items.
        //this GETS ALL DATA in the database.

        ArrayList<String> listData = new ArrayList<String>();//Create new ArrayList.

        while(data.moveToNext()){ //while Data has a next, keep getting moving to next data.
            //get the value from the database in column 1
            //then add it to the ArrayList
            listData.add(data.getString(1)); //the 1, means that it is getting the data that is stored in Column 1 in the table.
            // in our case it is the String task names.
        }

        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData);
        mListView.setAdapter(adapter); //populate the array list onto the android layout UI.
    }
}
