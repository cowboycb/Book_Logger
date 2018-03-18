package com.cowboydadas.book_logger.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.cowboydadas.book_logger.BookProcessActivity;
import com.cowboydadas.book_logger.R;
import com.cowboydadas.book_logger.adapter.ViewPagerAdapter;
import com.cowboydadas.book_logger.db.BookLoggerDbHelper;
import com.cowboydadas.book_logger.fragment.BookFragment;
import com.cowboydadas.book_logger.fragment.BookHistoryFragment;
import com.cowboydadas.book_logger.model.Book;
import com.cowboydadas.book_logger.util.BookUtility;
import com.cowboydadas.book_logger.util.SpinnerDialog;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements BookFragment.OnBookFragmentInteractionListener {

    public static final int REQUEST_FROM_BOOKINFO_ACTIVITY = 1;
    public static final int REQUEST_FROM_BOOKHISTORY_ACTIVITY = 2;

    private BookFragment bookFragment;
    private BookHistoryFragment bookHistoryFragment;

    private ViewPager viewPager;
    private MenuItem prevMenuItem;
    private FloatingActionButton mAddBook;
    private FloatingActionButton mAddHistory;
    private Map<Integer, Integer> bottomMenuPositions;

    private Context context;
    private BookLoggerDbHelper dbHelper;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomMenuPositions = new Hashtable<>();
        context = this;
        dbHelper = BookLoggerDbHelper.getInstance(getApplicationContext());
        init();
    }

    private void init() {
        viewPager = findViewById(R.id.viewpager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        bookHistoryFragment = new BookHistoryFragment();
        adapter.addFragment(bookHistoryFragment);

        bookFragment = new BookFragment();
        adapter.addFragment(bookFragment);

        viewPager.setAdapter(adapter);

        bottomMenuPositions.put(R.id.action_bookHistory, 0);
        bottomMenuPositions.put(R.id.action_books, 1);

        //Initializing the bottomNavigationView
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnClickListener(new BottomNavigationView.OnClickListener() {

            @Override
            public void onClick(View view) {

            }
        });


        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        viewPager.setCurrentItem(bottomMenuPositions.get(item.getItemId()));
//                        switch (item.getItemId()) {
//                            case R.id.action_books:
//                                viewPager.setCurrentItem(1);
//                                break;
//                            case R.id.action_bookHistory:
//                                viewPager.setCurrentItem(0);
//                                break;
//                        }
                        return true;
                    }
                });

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (prevMenuItem != null) {
                    prevMenuItem.setChecked(false);
                }
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                Log.d("page", "onPageSelected: " + position);
                prevMenuItem = bottomNavigationView.getMenu().getItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        final FloatingActionMenu main_fab_menu = findViewById(R.id.main_fab_menu);

        mAddBook = findViewById(R.id.floating_action_menu_addBook);
        mAddBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                main_fab_menu.close(true);
                Intent intent = new Intent(context, BookInfoActivity.class);
                startActivityForResult(intent, REQUEST_FROM_BOOKINFO_ACTIVITY);
            }
        });

        mAddHistory = findViewById(R.id.floating_action_menu_addHistory);
        mAddHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                main_fab_menu.close(true);
                openSelectBookDialog();



//                Intent intent = new Intent(context, BookInfoActivity.class);
//                startActivityForResult(intent, REQUEST_FROM_BOOKINFO_ACTIVITY);

            }
        });
    }

    private void openSelectBookDialog() {
        List<Book> listBook = BookLoggerDbHelper.getInstance(this).getBookList(null);
        SpinnerDialog mSpinnerDialog = SpinnerDialog.newInstance(this, listBook, new SpinnerDialog.DialogListener<Book>() {

            @Override
            public void ready(Book book) {
                Intent intent = new Intent(getApplicationContext(), BookProcessActivity.class);
                intent.putExtra(BookProcessActivity.BOOK_PROCESS, BookUtility.convertObject2JSON(book));
                startActivityForResult(intent, MainActivity.REQUEST_FROM_BOOKHISTORY_ACTIVITY);
            }

            @Override
            public void cancelled() {

            }
        });
        mSpinnerDialog.setDialogTitle(R.string.bookSelection);
        mSpinnerDialog.show(getSupportFragmentManager(), "spinnerDialog");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_FROM_BOOKINFO_ACTIVITY:
                if (resultCode == Activity.RESULT_OK) {
                    bottomNavigationView.getMenu().getItem(bottomMenuPositions.get(R.id.action_books)).setChecked(true);
                    refreshBookList();
                }
                break;
        }
    }

    private void refreshBookList() {
        Toast.makeText(this, "Main deyiz", Toast.LENGTH_LONG).show();
        bookFragment.refreshListAdapter();
    }

    @Override
    public void onActivity4Result(int requestCode, int resultCode, Intent intent) {
        onActivityResult(requestCode, resultCode, intent);
    }
}
