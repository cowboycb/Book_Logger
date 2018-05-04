package com.cowboydadas.book_logger.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.cowboydadas.book_logger.BookProcessActivity;
import com.cowboydadas.book_logger.R;
import com.cowboydadas.book_logger.activity.BookInfoActivity;
import com.cowboydadas.book_logger.activity.MainActivity;
import com.cowboydadas.book_logger.adapter.LazyBookAdapter;
import com.cowboydadas.book_logger.db.BookLoggerDbHelper;
import com.cowboydadas.book_logger.model.Book;
import com.cowboydadas.book_logger.util.BookUtility;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BookFragment.OnBookFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BookFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookFragment extends Fragment {

    private View fragmentView;
    private ListView listBookView;
    private LazyBookAdapter bookListAdapter;

    private OnBookFragmentInteractionListener mListener;

    public BookFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BookFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BookFragment newInstance() {
        BookFragment fragment = new BookFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void fillListAdapter(){
        List<Book> data = BookLoggerDbHelper.getInstance(getContext()).getBookList(null);
        bookListAdapter = new LazyBookAdapter(getContext(), data, false);
        listBookView.setAdapter(bookListAdapter);
    }

    public void refreshListAdapter(){
        fillListAdapter();
        bookListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fillListAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_book, container, false);
        listBookView = fragmentView.findViewById(R.id.listBook);
        listBookView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long itemId) {
                view.showContextMenu();
            }
        });
        registerForContextMenu(listBookView);
        // Inflate the layout for this fragment
        return fragmentView;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        if (v.getId()==R.id.listBook) {
            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
            Book selBook = ((Book)bookListAdapter.getItem(info.position));
            menu.setHeaderTitle(selBook.getTitle());
            String[] menuItems = getResources().getStringArray(R.array.book_menu);
            for (int i = 0; i<menuItems.length; i++) {
                menu.add(Menu.NONE, i, i, menuItems[i]);
            }
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int listPosition = info.position;
        switch (item.getItemId()){
            case 0:
                Intent intent2 = new Intent(getActivity(), BookInfoActivity.class);
                intent2.putExtra(BookInfoActivity.BOOK_INFO, BookUtility.convertObject2JSON(bookListAdapter.getItem(listPosition)));
                startActivityForResult(intent2, MainActivity.REQUEST_FROM_BOOKINFO_ACTIVITY);
                return true;
            case 1:
                Intent intent = new Intent(getActivity(), BookProcessActivity.class);
                intent.putExtra(BookProcessActivity.BOOK_PROCESS, BookUtility.convertObject2JSON(bookListAdapter.getItem(listPosition)));
                startActivityForResult(intent, MainActivity.REQUEST_FROM_BOOKHISTORY_ACTIVITY);
                return true;

            default: return super.onContextItemSelected(item);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mListener.onActivity4Result(requestCode, resultCode, data);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnBookFragmentInteractionListener) {
            mListener = (OnBookFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnBookFragmentInteractionListener {
        void onActivity4Result(int requestCode, int resultCode, Intent intent);
    }
}
