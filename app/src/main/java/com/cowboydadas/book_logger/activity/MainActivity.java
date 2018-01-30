package com.cowboydadas.book_logger.activity;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import com.cowboydadas.book_logger.fragment.BookFragment;
import com.cowboydadas.book_logger.fragment.BookHistoryFragment;
import com.cowboydadas.book_logger.R;
import com.cowboydadas.book_logger.util.ViewPagerAdapter;
import com.github.clans.fab.FloatingActionButton;

import java.util.Hashtable;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private BookFragment bookFragment;
    private BookHistoryFragment bookHistoryFragment;

    private ViewPager viewPager;
    private MenuItem prevMenuItem;
    private FloatingActionButton mAddBook;
    private FloatingActionButton mAddHistory;
    private Map<Integer, Integer> bottomMenuPositions;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bottomMenuPositions = new Hashtable<>();
        context = this;
        init();
    }

    private void init(){
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        bookHistoryFragment = new BookHistoryFragment();
        adapter.addFragment(bookHistoryFragment);

        bookFragment = new BookFragment();
        adapter.addFragment(bookFragment);

        viewPager.setAdapter(adapter);

        bottomMenuPositions.put(R.id.action_bookHistory, 0);
        bottomMenuPositions.put(R.id.action_books, 1);

        //Initializing the bottomNavigationView
        final BottomNavigationView bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottom_navigation);

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
//                else
//                {
                    bottomNavigationView.getMenu().getItem(position).setChecked(true);
//                }
                Log.d("page", "onPageSelected: "+position);
//                bottomNavigationView.getMenu().getItem(position).setChecked(true);
                prevMenuItem = bottomNavigationView.getMenu().getItem(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mAddBook=(FloatingActionButton)findViewById(R.id.floating_action_menu_addBook);
        mAddBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, BookInfoActivity.class);
                startActivity(intent);
            }
        });

        mAddHistory=(FloatingActionButton)findViewById(R.id.floating_action_menu_addHistory);
    }


}
