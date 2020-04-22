package edu.gmu.cs477.lab5_ely4;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

class DatabaseHelper extends SQLiteOpenHelper {

    /** Initialize Variables. Table Name and Column value names. **/
    private static final String TAG = "DatabaseHelper";
    private static final String TABLE_NAME = "ToDo_list";
    private static final String COL1 = "ID";
    private static final String COL2 = "task_name";

    final private Context context;


    /**
     * DatabaseHelper Constructor
     * @param context
     */
    public DatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
        //context.deleteDatabase(TABLE_NAME); //helps me restart the database. Testing purposes only. Comment out if not needed.
        this.context = context;
    }

    /**
     * Create Database on Create.
     * Giving the table a name and what the primary key is.
     * @param sqLiteDatabase
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT, " + COL2 + " TEXT)";
        //Create a table name = TABLE_NAME
        //ID column is primary key, autoincrement starting with 1...
        //COL2 is a text
        sqLiteDatabase.execSQL(createTable);

        ContentValues values = new ContentValues();//create a context value, to place values into database.

        /** Start database with these values in them, only appears once even if you re-run the app. **/
        values.put(COL2, "Push Trash Bin Out"); // content that you are going to pass into the database, COLUMN_NAME that it going to be place in, and the type.
        sqLiteDatabase.insert(TABLE_NAME, null, values);
        values.put(COL2, "Get Hair Cut");
        sqLiteDatabase.insert(TABLE_NAME, null, values);
        values.put(COL2, "Get Pedicure");
        sqLiteDatabase.insert(TABLE_NAME, null, values);
        values.put(COL2, "Do Lab 4");
        sqLiteDatabase.insert(TABLE_NAME, null, values);
    }

    /**
     * if Database is already created, overwrite the database.
     * is only called when the database file exists but the stored
     * version number is lower than requested in constructor
     * @param sqLiteDatabase
     * @param i
     * @param i1
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);

    }


    /**
     * Add data to Database Method.
     * @param item
     * @return
     */
    public boolean addData(String item){
        SQLiteDatabase db = this.getWritableDatabase(); //creates and declares an SQLite Database objects.
        ContentValues contentValues = new ContentValues();// declare a content value objects, this will help you write to the database.
        contentValues.put(COL2, item); //add first data to content values.

        Log.d(TAG, "addData: Adding " + item + " to " + TABLE_NAME); //log it.

        long result = db.insert(TABLE_NAME, null, contentValues); //create a long variable. to see what return value is.

        /** if data is interrupted it will return -1 **/
        if(result == -1){
            return false;
        }
        else
            return true;
    }//end add method.



    /**
     * Delete Data from Database.
     * @param item
     */
    public void deleteData(String item){
        SQLiteDatabase db = this.getWritableDatabase(); //creates and declares an SQLite Database objects.
        //System.out.println(item);//TESTING purposes
        String query = "DELETE FROM " + TABLE_NAME + " WHERE " +COL2+" = '"+item+"'";
        Log.d(TAG, "deleteName: query: " + query);
        Log.d(TAG, "deleteName: Deleting " + item + " from database.");
        db.execSQL(query);
        db.close();
    }
    /**********************************************************************************************/



    /**
     * Returns all the data from database.
     * getter method to get database contents.
     * @return
     */
    public Cursor getData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME; //select all data from table name.
        Cursor data = db.rawQuery(query, null);//create Cursor Variable, use query to call all items
        //from the TABLE_NAME database. Then return the Cursor data variable.
        return data;
    }

    void deleteDatabase ( ) { //DELETES ENTIRE DATABASE.
        context.deleteDatabase(TABLE_NAME);
    }//delete database, testing purposes.

}
