package edu.gmu.cs477.lab5_ely4;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

/**
 * Created By: Elvis Ly G00734545
 * Lab 5 - CS477 Mobile Application
 */
public class MainActivity extends AppCompatActivity {

    /** Declare Variable**/
    EditText users_input;
    Button clearButton, addButton;
    ArrayList<String> itemList, marked_done_list, clone_itemList;
    ArrayAdapter<String> adapter;
    ListView listView;
    AlertDialog actions;
    int currentPos;

    /** DatabaseHelper initialization **/
    DatabaseHelper mDatabaseHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        users_input = (EditText) findViewById(R.id.new_task_view);// initialize by finding the view from the activity_main xml layout resource file
        listView = (ListView) findViewById(R.id.to_do_list);//initialize by finding the view from the activity_main xml layout resource file
        itemList = new ArrayList<String>(); //initialize new array list casting it as String inputs only..


        /** Param1 – context, Param2 – row layout (reference to an built-in XML layout document part of Android OS), Param3 – the Array of data.
         * Passes results to the List View Adapter, the result is stored in itemList,
         * which is where we are going to store our list in itemList. Starts
         * out as empty, but we will append each time the user is going to click on add button. **/
        adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, itemList);

        /** create the new database helper **/
        mDatabaseHelper = new DatabaseHelper(this);
        Cursor data = mDatabaseHelper.getData(); // create cursor that reference the database, that references the getdata contents.
        //the cursor    is going to get all the content in the database.


        if(data.getCount() == 0){
            toastMessage("The Database is empty");
        }else{
            while(data.moveToNext()){
                itemList.add(data.getString(1)); //retrieve the items in the database, and place them into the array list.**************
                listView.setAdapter(adapter);//****************************************************************************************
            }
        }

        marked_done_list = new ArrayList<String>();//initialize second array list to add DONE: task.
        //we will be using this second array list to delete all task at once, with our "Delete Done" button.



        /*********************** Define action listener for delete on long click pop up box.  ****************************/
        DialogInterface.OnClickListener actionListener =
                new DialogInterface.OnClickListener() {
                    @Override public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0: // Delete
                                adapter.remove(adapter.getItem(currentPos));
                                break;
                            default:
                                break;
                        }
                    }
                };
        /******************* Build dialog pop up box ************************/
        AlertDialog.Builder builder = new
                AlertDialog.Builder(this);
        builder.setTitle("Are you sure you want to delete this item?");
        String[] options = {"Delete"};
        builder.setItems(options, actionListener);
        builder.setNegativeButton("Cancel", null);
        actions = builder.create();







        /*************** Add Item Button. We want to add new task to our To Do list  **********************/
        addButton = findViewById(R.id.button_add_item); //initialize button. Find id button_add_item.
        AdapterView.OnClickListener ocl;
        ocl = new AdapterView.OnClickListener(){
            public void onClick(View v){
                if(users_input.getText().toString().equals("")){//make sure user input is not empty.
                    Toast.makeText(getApplicationContext(), "Please enter a task", Toast.LENGTH_SHORT).show();
                }//end if statement.
                else{
                    itemList.add(0,users_input.getText().toString());//add task to the beginning of the array list.
                    AddData(users_input.getText().toString()); // adds INTO DATABASE! ****************************************************
                    String text = users_input.getText().toString() + " has been added";
                    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show(); //notify users what has been added to the task list.
                    users_input.setText(""); //empty edit text view.
                    adapter.notifyDataSetChanged(); //update the adapter view
                }//end else statement
            }
        };//end add_button LISTENER.
        addButton.setOnClickListener(ocl);








        /************** Clear Button. We want to clear all items on list that is marked done by the user *****************/
        clearButton = findViewById(R.id.delete_button);
        ocl = new AdapterView.OnClickListener(){
            public void onClick(View v){
                //loop through the arrayList to find MARKED Done activities.

                /** new code added for Lab 5, this portion only is used, when user re-runs the app. When they
                 * click on delete done, the Cursor data - will go to database and grab all data from it
                 * then we will have to go through each data item "task" that is stored it it to fine those
                 * that are marked Done: , after having done so, we add those items to our marked_done_list for
                 * deletion. **/
                //get the data and append to a list
                Cursor data = mDatabaseHelper.getData(); //Initialize Cursor Data, use DatabaseHelper to get Data items.
                //this GETS ALL DATA in the database.

                while(data.moveToNext()){ //while Data has a next, keep getting moving to next data.
                    //get the value from the database in column 1
                    //then add it to the ArrayList

                    if (data.getString(1).substring(0,5).equals("Done:")){
                        toastMessage("ADDING THIS TO THE MARKED DONE LIST = " + data.getString(1));
                        //System.out.println(data.getString(1));
                       // marked_done_list.add(data.getString(1)); //the 1, means that it is getting the data that is stored in Column 1 in the table.
                        marked_done_list.add(data.getString(1));
                    }
                }
                /********************************* END NEW CODE ADDED **************/


                for(int i = 0; i< marked_done_list.size(); i++){
                    for(int j = 0; j<listView.getAdapter().getCount(); j++){
                        //check if marked completed activities are still marked in the itemList (which is where the adapter view is connected to)
                        //if they are equal, we remove them from itemList.
                        if(marked_done_list.get(i).equals(itemList.get(j))){
                            //newly added statement. trying to delete data from database.
                            RemoveData(itemList.get(j)); //delete from Database ****************************************************************************************
                            adapter.remove(itemList.get(j));


                        }//end if statement. go through marked_done_list tasks and compare with itemList, if it is in the list, we remove it from the adapter view.
                    }//end inner for loop.
                }//end outer for loop.


                adapter.notifyDataSetChanged(); //let listView adapter know changes has been made, update listView on Android Views.
                marked_done_list.clear();//empty the marked done array list.
            }//end on click method.
        };//end onClickListener for delete_button
        clearButton.setOnClickListener(ocl);
        /***********************************************END CLEAR BUTTON LISTENER ************************************************************/






        /*********************************************Long click Listener**************************************************************/
        // listView long press deletes items that are in the list view area.
        // allows the user to select multiple items, store the location and when
        // they long press, it will delete all items that they selected.
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long l) {
                currentPos = position;
                actions.show();
                return true;
            }//end on item long click method.
        });//end on Long clicker.
        /**********************************************END LONG CLICK LISTENER *************************************************************/






        /********************* GETS ITEM CLICKED BY USER TO SECOND_LIST SO THAT WE MAY DELETE ALL WHEN FINISHED *******************/
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String activityStr = ((TextView) view).getText().toString();
                mDatabaseHelper.deleteData(itemList.get(i));

                /** Checks if current activity is marked DONE: yet. if it is not we mark it done, add to our second array list to delete all in clear button **/
                if (activityStr.length() < 5 || !activityStr.substring(0,5).equals("Done:")) {
                    String removedItem = itemList.remove(i);  // NEWLY ADDED***************************************************************************
                    toastMessage(removedItem);//***************************************************************************
                    //itemList.remove(i);
                    mDatabaseHelper.deleteData(removedItem);//****************************************************************************************
                    //RemoveData(removedItem);  // NEWLY ADDED*****************************************************************************************
                    String add_Done = "Done:" + activityStr;
                    itemList.add("Done:" + activityStr);
                    AddData("Done:" + activityStr);//NEWLY ADDED***********************************************************************************
                    marked_done_list.add(add_Done);
                    adapter.notifyDataSetChanged();//let listView adapter know changes has been made, update listView on Android Views.

                }
                /** If activity is MARKED done already, we remove it and remove it from out second _arraylist. **/
                else if (activityStr.substring(0,5).equals("Done:")) { //marked as done CURRENTLY
                    //remove prefix and add it back to the TOP of the list
                    String remove_this = itemList.get(i);
                    itemList.remove(i);//remove
                    itemList.add(0,activityStr.substring(5));
                    for(int x = 0; x< marked_done_list.size(); x++){//loop through to find unMarked task, remove them from list.
                        if(marked_done_list.get(x).equals(remove_this)){//check
                            marked_done_list.remove(x);//remove unMarked DONE: tasks.
                        }//end if statement.
                    }//end for loop (goes thru the second array list (done list) and delete those which we marked done in that list.
                    adapter.notifyDataSetChanged();//let listView adapter know changes has been made, update listView on Android Views.
                }//end else if statement.
            }//end void on item click method.
        });//end on item click Listener.
        /************************************************************************************************************/








        EditText elem =  (EditText)findViewById(R.id.new_task_view);

        /*********************** Action Listener for enter button. ****************************************/
        final TextView.OnEditorActionListener mReturnListener =
                new TextView.OnEditorActionListener() {
                    public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                        // If the action is a key up (meaning if user pressed the enter key and released it) event on the return key,
                        // send the message
                        if (actionId == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
                            String input = view.getText().toString();
                            if (input.length() > 1) {
                                adapter.add(input);
                                AddData(input);// newly added ***********************************************************

                                Toast.makeText(getApplicationContext(), "Adding " + input, Toast.LENGTH_SHORT).show();
                            }
                            view.setText("");
                        }
                        return true;
                    }
                };
        elem.setOnEditorActionListener(mReturnListener);


        listView.setAdapter(adapter);

    }// end onCreate






    /** new code added for Lab 5 **/

    /**
     * add data into database.
     * @param newEntry
     */
    public void AddData(String newEntry){
        boolean insertData = mDatabaseHelper.addData(newEntry);//call method from database, pass in string. return a boolean
        //returns true if everything works.

        if(insertData){
            toastMessage("Data Successfully Inserted!");
        }
        else{
            toastMessage("Something went wrong");
        }
    }//end AddData method.

    /**
     * created own toast message method.
     * @param message
     */
    private void toastMessage(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void RemoveData(String item){
        mDatabaseHelper.deleteData(item);
    }

}//end Main.



