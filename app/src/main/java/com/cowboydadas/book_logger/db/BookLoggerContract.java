package com.cowboydadas.book_logger.db;

import android.provider.BaseColumns;

/**
 * Created by admin on 8.01.2018.
 */


/**
 * SQLLITE  : Whenever you create a table without specifying the WITHOUT ROWID option, you get an implicit auto increment column called rowid.
 * SQLite recommends that you should not use AUTOINCREMENT attribute because:
        "The AUTOINCREMENT keyword imposes extra CPU, memory, disk space, and disk I/O overhead and should be avoided if not strictly needed. It is usually not needed."
 */
public class BookLoggerContract {

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private BookLoggerContract() {}

    /* Inner class Table LOGGER */
    public static class BookHistory implements BaseColumns {
        public static final String TABLE_NAME                   = "bookHistory";
        public static final String COLUMN_NAME_BOOKID           = "bookId";
        public static final String COLUMN_NAME_READPAGE         = "readPage";
        public static final String COLUMN_NAME_REMAININGPAGE    = "remainingPage";
        public static final String COLUMN_NAME_IDAY             = "iday";
        public static final String COLUMN_NAME_IMONTH           = "imonth";
        public static final String COLUMN_NAME_IYEAR            = "iyear";
    }


    /* Inner class Table CATEGORY */
    public static class Category implements BaseColumns {
        public static final String TABLE_NAME = "category";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
    }

    /* Inner class Table BOOK */
    public static class Book implements BaseColumns {
        public static final String TABLE_NAME                   = "book";
        public static final String COLUMN_NAME_TITLE            = "title";
        public static final String COLUMN_NAME_DESCRIPTION      = "description";
        public static final String COLUMN_NAME_TOTALPAGE        = "totalPage";
        public static final String COLUMN_NAME_CURRENTPAGE      = "currentPage";
        public static final String COLUMN_NAME_AUTHOR           = "author";
        public static final String COLUMN_NAME_IDATE            = "idate";
        public static final String COLUMN_NAME_UDATE            = "udate";
        public static final String COLUMN_NAME_COVER            = "cover";
    }

    /* Inner class Table SUBJECT */
    public static class Subject implements BaseColumns {
        public static final String TABLE_NAME = "subject";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_CATEGORYID = "categoryId";
    }

    /* Inner class Table GOAL */
    public static class Goal implements BaseColumns {
        public static final String TABLE_NAME               = "goal";
        public static final String COLUMN_NAME_SUBJECTID    = "subjectId";
        public static final String COLUMN_NAME_TYPE         = "type";
        public static final String COLUMN_NAME_NUMBER       = "number";
        public static final String COLUMN_NAME_IDATE        = "idate";
        public static final String COLUMN_NAME_UDATE        = "udate";
    }
}
