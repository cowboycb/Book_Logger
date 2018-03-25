package com.cowboydadas.book_logger.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.cowboydadas.book_logger.exception.BookHistoryException;
import com.cowboydadas.book_logger.model.Book;
import com.cowboydadas.book_logger.model.BookHistory;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by admin on 8.01.2018.
 */

public class BookLoggerDbHelper extends SQLiteOpenHelper {

    private static BookLoggerDbHelper sInstance;

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "BookLogger.db";


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

    public static synchronized BookLoggerDbHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new BookLoggerDbHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private BookLoggerDbHelper(Context context) {
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
        DateFormat dfDate = new SimpleDateFormat("yyyyMMddHHmmss");
        String date=dfDate.format(Calendar.getInstance().getTime());
        values.put(BookLoggerContract.Book.COLUMN_NAME_IDATE, date);

        long newRowId = db.insert(BookLoggerContract.Book.TABLE_NAME, null, values);
        book.setId(newRowId);

        return newRowId;
    }

    public int updateBook(Book book) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(BookLoggerContract.Book.COLUMN_NAME_TITLE, book.getTitle());
        values.put(BookLoggerContract.Book.COLUMN_NAME_AUTHOR, book.getAuthor());
        values.put(BookLoggerContract.Book.COLUMN_NAME_COVER, book.getCover());
        values.put(BookLoggerContract.Book.COLUMN_NAME_CURRENTPAGE, book.getCurrentPage());
        values.put(BookLoggerContract.Book.COLUMN_NAME_DESCRIPTION, book.getDescription());
        values.put(BookLoggerContract.Book.COLUMN_NAME_TOTALPAGE, book.getTotalPage());
        DateFormat dfDate = new SimpleDateFormat("yyyyMMddHHmmss");
        String date=dfDate.format(Calendar.getInstance().getTime());
        values.put(BookLoggerContract.Book.COLUMN_NAME_UDATE, date);

        int count = db.update(BookLoggerContract.Book.TABLE_NAME, values, BookLoggerContract.Book._ID + " = ?", new String[]{book.getId().toString()});

        return count;
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

    private String[] bookHistoryProjection(){
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                BookLoggerContract.BookHistory._ID,
                BookLoggerContract.BookHistory.COLUMN_NAME_BOOKID,
                BookLoggerContract.BookHistory.COLUMN_NAME_READPAGE,
                BookLoggerContract.BookHistory.COLUMN_NAME_REMAININGPAGE,
                BookLoggerContract.BookHistory.COLUMN_NAME_IDAY,
                BookLoggerContract.BookHistory.COLUMN_NAME_IMONTH,
                BookLoggerContract.BookHistory.COLUMN_NAME_IYEAR
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

    private BookHistory getBookHistoryFromCursor(Cursor cursor){
        BookHistory b = new BookHistory();
        b.setId(cursor.getLong(cursor.getColumnIndex(BookLoggerContract.BookHistory._ID)));
        b.setBookId(cursor.getLong(cursor.getColumnIndex(BookLoggerContract.BookHistory.COLUMN_NAME_BOOKID)));
        b.setReadPage(cursor.getInt(cursor.getColumnIndex(BookLoggerContract.BookHistory.COLUMN_NAME_READPAGE)));
        b.setRemainingPage(cursor.getInt(cursor.getColumnIndex(BookLoggerContract.BookHistory.COLUMN_NAME_REMAININGPAGE)));
        b.setIday(cursor.getInt(cursor.getColumnIndex(BookLoggerContract.BookHistory.COLUMN_NAME_IDAY)));
        b.setImonth(cursor.getInt(cursor.getColumnIndex(BookLoggerContract.BookHistory.COLUMN_NAME_IMONTH)));
        b.setIyear(cursor.getInt(cursor.getColumnIndex(BookLoggerContract.BookHistory.COLUMN_NAME_IYEAR)));
        return b;
    }

    public List<Book> getBookList(Book book) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = "1=1";
        List<String> selArgs = new ArrayList<String>();

        if (book != null) {
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
        }

        Cursor cursor = db.query(
                BookLoggerContract.Book.TABLE_NAME,       // The table to query
                bookProjection(),                               // The columns to return
                selection,                                // The columns for the WHERE clause
                !selArgs.isEmpty() ? (String[]) selArgs.toArray() : null,             // The values for the WHERE clause
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

    public long insertBookProcess(Book activeBook) throws BookHistoryException {

        BookHistory lastHistory = getLastBookHistory(activeBook.getId());

        int readPage = 0;
        int remainingPage = 0;
        if (lastHistory == null){
            readPage = activeBook.getCurrentPage();
            remainingPage = activeBook.getTotalPage() - readPage;
        }else{
            if (activeBook.getCurrentPage()<lastHistory.getReadPage()){
                throw new BookHistoryException("Lütfen bulunduğunuz sayfayı giriniz");
            }
            readPage = activeBook.getCurrentPage() - (activeBook.getTotalPage() - lastHistory.getRemainingPage());
            remainingPage = activeBook.getTotalPage() - activeBook.getCurrentPage();
        }

        SQLiteDatabase db = this.getWritableDatabase();
        Calendar cal = Calendar.getInstance();
        ContentValues values = new ContentValues();
        values.put(BookLoggerContract.BookHistory.COLUMN_NAME_BOOKID, activeBook.getId());
        values.put(BookLoggerContract.BookHistory.COLUMN_NAME_READPAGE, readPage);
        values.put(BookLoggerContract.BookHistory.COLUMN_NAME_REMAININGPAGE, remainingPage);
        values.put(BookLoggerContract.BookHistory.COLUMN_NAME_IDAY, cal.get(Calendar.DATE));
        values.put(BookLoggerContract.BookHistory.COLUMN_NAME_IMONTH, cal.get(Calendar.MONTH) + 1);
        values.put(BookLoggerContract.BookHistory.COLUMN_NAME_IYEAR, cal.get(Calendar.YEAR));

        long newRowId = db.insert(BookLoggerContract.BookHistory.TABLE_NAME, null, values);

        return newRowId;
    }

    public BookHistory getLastBookHistory(Long bookId){
        SQLiteDatabase db = this.getReadableDatabase();

        String selection = BookLoggerContract.BookHistory.COLUMN_NAME_BOOKID + " = ?";
        String[] selArgs = { String.valueOf(bookId) };

        Cursor cursor = db.query(
                BookLoggerContract.BookHistory.TABLE_NAME,       // The table to query
                bookHistoryProjection(),                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selArgs,                                  // The values for the WHERE clause
                null,                                // don't group the rows
                null,                                 // don't filter by row groups
                BookLoggerContract.BookHistory._ID + " DESC",                                 // The sort order
                "1"
        );

        if (cursor != null && cursor.moveToFirst()) {
            BookHistory b = getBookHistoryFromCursor(cursor);

            return b;
        }

        return null;
    }
}