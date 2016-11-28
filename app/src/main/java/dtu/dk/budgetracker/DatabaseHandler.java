package dtu.dk.budgetracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Kasio on 09.11.2016.
 */

public class DatabaseHandler  extends SQLiteOpenHelper{

    //database info
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "expTrackerProfile";

    //database table fields
    private static final String KEY_ID = "id";
    private static final String KEY_DATE = "date";
    private static final String KEY_AMOUNT = "amount";
    private static final String KEY_SHOP = "shop";
    private static final String KEY_ADDRESS = "address";

    private static final String TABLE_NAME = "expenses";


    public DatabaseHandler (Context context){
        super (context,DATABASE_NAME, null, DATABASE_VERSION );
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //creating a table
        db.execSQL("CREATE TABLE " + TABLE_NAME + "(" + KEY_ID + " INTEGER PRIMARY KEY, "
        + KEY_DATE + " INT, " + KEY_AMOUNT + " INT, " + KEY_SHOP + " TEXT, "
                + KEY_ADDRESS+" TEXT"+")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);

    }
    //creates a table with name profileName
    public void createTable(String profileName){
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("CREATE TABLE" + profileName + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_DATE + " INT," + KEY_AMOUNT + " INT, "+ KEY_SHOP + " TEXT, "
                +KEY_ADDRESS+" TEXT"+ ")" );
    }

    public void addProfileInfo(Expenses expenses){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DATE, expenses.getDate());
        values.put(KEY_AMOUNT, expenses.getAmount());
        values.put(KEY_SHOP, expenses.getShop());
        values.put(KEY_ADDRESS, expenses.getAddress());

        //adding values to the profile
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public void removeProfileInfo(String profile, String selection){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(profile, KEY_ID + " = ?",
                new String[]{String.valueOf(selection)});
        db.close();
    }

    public List<Expenses> getAllRecords(){
        List<Expenses> expensesList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()){
            do{
                Expenses expenses = new Expenses();
                expenses.setId(Integer.parseInt(cursor.getString(0)));
                expenses.setDate(cursor.getString(1));
                expenses.setAmount(Integer.parseInt(cursor.getString(2)));
                expenses.setShop(cursor.getString(3));
                expenses.setAddress(cursor.getString(4));
                expensesList.add(expenses);
            }while (cursor.moveToNext());

        }
        //will return list of expenses object
        return expensesList;
    }

    public List<Expenses> getSpecificRecord(int itemId){
        List<Expenses> itemList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME + " WHERE "
                + KEY_ID + " = " + itemId;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery,null);
        Expenses expenses = new Expenses();
        if(cursor.moveToFirst()){
            do{
                expenses.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ID))));
                expenses.setDate(cursor.getString(cursor.getColumnIndexOrThrow(KEY_DATE)));
                expenses.setAmount(Integer.parseInt(cursor.getString(cursor.getColumnIndexOrThrow(KEY_AMOUNT))));
                expenses.setShop(cursor.getString(cursor.getColumnIndexOrThrow(KEY_SHOP)));
                expenses.setAddress(cursor.getString(cursor.getColumnIndexOrThrow(KEY_ADDRESS)));
                itemList.add(expenses);
            } while (cursor.moveToNext());
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return itemList;
    }
    public void dropProfileTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.close();
    }

    public List<String> getUniqueShops(){
        ArrayList<String> uniqueShops = new ArrayList<>();
        String selectQuery = "SELECT DISTINCT "+ KEY_SHOP + " FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null && cursor.getCount() !=0){
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                uniqueShops.add(cursor.getString(cursor.getColumnIndex(KEY_SHOP)));
                cursor.moveToNext();
            }
        }
        cursor.close();

        return uniqueShops;
    }

    public Map<String, Integer> getSpendings(){
        Map<String, Integer > spendings = new HashMap<>();
        String selectQuery = "SELECT "+KEY_SHOP+", sum(amount) FROM "
                +TABLE_NAME+" GROUP BY "+KEY_SHOP;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if(cursor != null && cursor.getCount() != 0){
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                spendings.put(cursor.getString(cursor.getColumnIndex(KEY_SHOP)),
                        Integer.parseInt(cursor.getString(cursor.getColumnIndex("sum(amount)"))));
                cursor.moveToNext();

            }
        }
        cursor.close();

        return spendings;
    }
    public int getTotalAmount(){
        String[] projection = {KEY_AMOUNT};
        int totalAmount = 0;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(TABLE_NAME, projection, null, null, null, null, null);
        ArrayList<Integer> values = new ArrayList<>();
        if(cursor != null && cursor.getCount() != 0){
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                values.add(cursor.getInt(cursor.getColumnIndex(KEY_AMOUNT)));
                cursor.moveToNext();
            }
        }
        for (int i = 0; i < values.size(); i++) {
            totalAmount = totalAmount+values.get(i);
        }
        cursor.close();


        return totalAmount;
    }


    /*
    public void getAmount (String profile, int id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.query(profile, new String[]{ KEY_ID, KEY_DATE, KEY_AMOUNT}, KEY_ID +
                " =?", new String[]{ String.valueOf(id)}, null, null, null, null);
        if (cursor != null){
            cursor.moveToFirst();
        }
    }
    */
}
