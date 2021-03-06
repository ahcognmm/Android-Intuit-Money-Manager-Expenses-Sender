package com.expensessender;

import com.expensessender.CategoryManager.CategoryListHelper;

import android.app.Activity;
import android.content.Context;
import android.database.*;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.Context;
public class CategoryManager {
	
	private Context context;
	private static String createTableQuery = "CREATE TABLE " +
					"categories (category varchar(100) not null primary key, `used` int DEFAULT 0);";
	private SQLiteDatabase db;
	private CategoryListHelper adapter;
	private static String databaseName = "thermo";
	private static int version = 5;
			
	
	CategoryManager(Context context) {
		this.context = context;
		this.adapter = new CategoryListHelper(context, databaseName, null, version);

	}
	
	public void incrementUsage(String category) {
		String query = "UPDATE categories SET used=used+1 WHERE category='"+category+"';";
		db.execSQL(query);
	}
	
	public CategoryManager open() throws SQLException {
		db = this.adapter.getWritableDatabase();
		return this;
	}
	
	public void close() {
		db.close();
	}
	
	public void addCategory(String categoryName)
	{
		String insertQuery = "INSERT INTO categories (category) VALUES ('"+categoryName+"');";
		db.execSQL(insertQuery);
	}
	
	public void deleteCategory(String categoryName)
	{
		db.execSQL("DELETE FROM categories WHERE category='"+categoryName+"';");
	}	
	
	public Cursor getAllEntries() {
		
		return db.query("categories", new String[] { "category" }, null, null, null, null,"used DESC", null);
	}
	
	public boolean categoryExists(String categoryName)
	{
		Cursor cursor = db.query("categories", new String[] { "category"} , "category = '"+categoryName+"'", null, null, null, null);
		int count = cursor.getCount();
		cursor.close();
		return (0 < count);
	}
	
	
	public static class CategoryListHelper extends SQLiteOpenHelper {

		private String [] preloadedCategoryList = {
				"Food",
				"Fast Food",
				"Confectionary",
				"Coffee",
				"Transit",
				"Fuel",
				"Electricity",
				"Rent",
				"Mobile Phone",
				"Charity",
				"Wasted"
		};

		public CategoryListHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);

		}
		
		private void initializeData(SQLiteDatabase _db) {
			for (int iter=0;iter<this.preloadedCategoryList.length; iter++) {
				String preloadedSQL = "INSERT INTO categories (category) VALUES ('"+this.preloadedCategoryList[iter]+"')";
				_db.execSQL(preloadedSQL);	
			}
			
		}
		
		@Override
		public void onCreate(SQLiteDatabase _db) {
			_db.execSQL(createTableQuery);
			initializeData(_db);
		
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase _db, int _oldVersion, int _newVersion)
		{
			
		}
		
	}
	
	public String [] getCategories() {
		
		
		Cursor cur = this.getAllEntries();
		int count=0;
		String [] list = new String[cur.getCount()];
		int index = cur.getColumnIndex("category");
		cur.moveToFirst();
		while (count < cur.getCount()) 
		{
			@SuppressWarnings("unused")
			int columns = cur.getColumnCount();

			String val = cur.getString(index);
			
			list[count] = val;
			cur.moveToNext();
			count++;
		}
		cur.close();
				
		return list;
	}
	

	
	

}
