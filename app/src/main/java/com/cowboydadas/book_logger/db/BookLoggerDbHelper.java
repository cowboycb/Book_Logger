package com.cowboydadas.book_logger.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.cowboydadas.book_logger.model.Book;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 8.01.2018.
 */

public class BookLoggerDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "BookLogger.db";


    private static final String SQL_CREATE_TABLE_BOOK =
            "CREATE TABLE " + BookLoggerContract.Book.TABLE_NAME + " (" +
                    BookLoggerContract.Book._ID + " INTEGER PRIMARY KEY," +
                    BookLoggerContract.Book.COLUMN_NAME_TITLE + " TEXT," +
                    BookLoggerContract.Book.COLUMN_NAME_DESCRIPTION + " TEXT," +
                    BookLoggerContract.Book.COLUMN_NAME_TOTALPAGE + " INTEGER," +
                    BookLoggerContract.Book.COLUMN_NAME_CURRENTPAGE + " INTEGER," +
                    BookLoggerContract.Book.COLUMN_NAME_AUTHOR + " TEXT," +
                    BookLoggerContract.Book.COLUMN_NAME_COVER + " TEXT," +
                    BookLoggerContract.Book.COLUMN_NAME_IDATE + " TEXT," +
                    BookLoggerContract.Book.COLUMN_NAME_UDATE + " TEXT)";

    private static final String SQL_CREATE_TABLE_BOOKHISTORY =
            "CREATE TABLE " + BookLoggerContract.BookHistory.TABLE_NAME + " (" +
                    BookLoggerContract.BookHistory._ID + " INTEGER PRIMARY KEY," +
                    BookLoggerContract.BookHistory.COLUMN_NAME_BOOKID + " INTEGER," +
                    BookLoggerContract.BookHistory.COLUMN_NAME_READPAGE + " INTEGER," +
                    BookLoggerContract.BookHistory.COLUMN_NAME_REMAININGPAGE + " INTEGER," +
                    BookLoggerContract.BookHistory.COLUMN_NAME_IDAY + " INTEGER," +
                    BookLoggerContract.BookHistory.COLUMN_NAME_IMONTH + " INTEGER," +
                    BookLoggerContract.BookHistory.COLUMN_NAME_IYEAR + " INTEGER)";

    public BookLoggerDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_BOOK);
        db.execSQL(SQL_CREATE_TABLE_BOOKHISTORY);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
//        db.execSQL(SQL_DELETE_ENTRIES);
//        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public Long createBook(Book book) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(BookLoggerContract.Book.COLUMN_NAME_TITLE, book.getTitle());
        values.put(BookLoggerContract.Book.COLUMN_NAME_AUTHOR, book.getAuthor());
        values.put(BookLoggerContract.Book.COLUMN_NAME_COVER, book.getCover());
        values.put(BookLoggerContract.Book.COLUMN_NAME_CURRENTPAGE, book.getCurrentPage());
        values.put(BookLoggerContract.Book.COLUMN_NAME_DESCRIPTION, book.getDescription());
        values.put(BookLoggerContract.Book.COLUMN_NAME_TOTALPAGE, book.getTotalPage());
        values.put(BookLoggerContract.Book.COLUMN_NAME_IDATE, book.getIdate());

        long newRowId = db.insert(BookLoggerContract.Book.TABLE_NAME, null, values);
        book.setId(newRowId);

        return newRowId;
    }

    private String[] bookProjection(){
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                BookLoggerContract.Book._ID,
                BookLoggerContract.Book.COLUMN_NAME_TITLE,
                BookLoggerContract.Book.COLUMN_NAME_AUTHOR,
                BookLoggerContract.Book.COLUMN_NAME_COVER,
                BookLoggerContract.Book.COLUMN_NAME_CURRENTPAGE,
                BookLoggerContract.Book.COLUMN_NAME_DESCRIPTION,
                BookLoggerContract.Book.COLUMN_NAME_TOTALPAGE,
                BookLoggerContract.Book.COLUMN_NAME_IDATE,
                BookLoggerContract.Book.COLUMN_NAME_UDATE
        };
        return projection;
    }

    private Book getBookFromCursor(Cursor cursor){
        Book b = new Book();
        b.setId(cursor.getLong(cursor.getColumnIndex(BookLoggerContract.Book._ID)));
        b.setTitle(cursor.getString(cursor.getColumnIndex(BookLoggerContract.Book.COLUMN_NAME_TITLE)));
        b.setDescription(cursor.getString(cursor.getColumnIndex(BookLoggerContract.Book.COLUMN_NAME_DESCRIPTION)));
        b.setTotalPage(cursor.getInt(cursor.getColumnIndex(BookLoggerContract.Book.COLUMN_NAME_TOTALPAGE)));
        b.setAuthor(cursor.getString(cursor.getColumnIndex(BookLoggerContract.Book.COLUMN_NAME_AUTHOR)));
        b.setCover(cursor.getString(cursor.getColumnIndex(BookLoggerContract.Book.COLUMN_NAME_COVER)));
        b.setCurrentPage(cursor.getInt(cursor.getColumnIndex(BookLoggerContract.Book.COLUMN_NAME_CURRENTPAGE)));
        b.setIdate(cursor.getString(cursor.getColumnIndex(BookLoggerContract.Book.COLUMN_NAME_IDATE)));
        b.setUdate(cursor.getString(cursor.getColumnIndex(BookLoggerContract.Book.COLUMN_NAME_UDATE)));

        return b;
    }

    public List<Book> getBookList(Book book) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = "1=1";
        List<String> selArgs = new ArrayList<String>();

        if (book.getTitle() != null) {
            selection += " AND " + BookLoggerContract.Book.COLUMN_NAME_TITLE + " LIKE ?";
            selArgs.add("%" + book.getTitle() + "%");
        }
        if (book.getAuthor() != null) {
            selection += " AND " + BookLoggerContract.Book.COLUMN_NAME_AUTHOR + " = ?";
            selArgs.add(book.getAuthor());
        }
        if (book.getIdate() != null) {
            selection += " AND " + BookLoggerContract.Book.COLUMN_NAME_IDATE + " LIKE ?";
            selArgs.add(book.getIdate() + "%");
        }


        Cursor cursor = db.query(
                BookLoggerContract.Book.TABLE_NAME,       // The table to query
                bookProjection(),                               // The columns to return
                selection,                                // The columns for the WHERE clause
                (String[]) selArgs.toArray(),             // The values for the WHERE clause
                null,                                // don't group the rows
                null,                                 // don't filter by row groups
                null                                 // The sort order
        );

        List<Book> bookList = new ArrayList<>();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                Book b = getBookFromCursor(cursor);

                bookList.add(b);
            } while (cursor.moveToNext());
        }

        return bookList;
    }

    public Book getBookById(Long id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = BookLoggerContract.Book._ID + " = ?";
        String[] selArgs = { String.valueOf(id) };

        Cursor cursor = db.query(
                BookLoggerContract.Book.TABLE_NAME,       // The table to query
                bookProjection(),                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selArgs,                                  // The values for the WHERE clause
                null,                                // don't group the rows
                null,                                 // don't filter by row groups
                null                                 // The sort order
        );

        if (cursor != null && cursor.moveToFirst()) {
            Book b = getBookFromCursor(cursor);

            return b;
        }

        return null;
    }
}