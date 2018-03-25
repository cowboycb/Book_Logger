package com.cowboydadas.book_logger.util;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

import com.cowboydadas.book_logger.R;
import com.cowboydadas.book_logger.adapter.LazyBookAdapter;
import com.cowboydadas.book_logger.model.Book;

import java.io.Serializable;
import java.util.List;

/**
 * Created by ChnEmn on 10.03.2018.
 */


public class SpinnerDialog extends DialogFragment {
    private static final String ARG_DATA_LIST = "data-list";
    private static final String ARG_LISTENER = "listener";

    private List<Book> mList;
    private Spinner mSpinner;
    private String dialogTitle = null;
    private int dialogTitleId = -1;

    public interface DialogListener<T> extends Serializable{
        public void ready(T n);
        public void cancelled();
    }

    private DialogListener mReadyListener;

    public SpinnerDialog() {
    }

    public static SpinnerDialog newInstance(Context context, List<Book> list, DialogListener readyListener) {
        SpinnerDialog fragment = new SpinnerDialog();
        Bundle args = new Bundle();
        args.putSerializable(ARG_DATA_LIST, (Serializable) list);
        args.putSerializable(ARG_LISTENER, readyListener);
        fragment.setArguments(args);
        return fragment;
    }

    public void setDialogTitle(String dialogTitle) {
        this.dialogTitle = dialogTitle;
    }

    public void setDialogTitle(int dialogTitle) {
        this.dialogTitleId = dialogTitle;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_Holo_Dialog_Alert);

        if (getArguments() != null) {
            mList = (List<Book>) getArguments().getSerializable(ARG_DATA_LIST);
            mReadyListener = (DialogListener) getArguments().getSerializable(ARG_LISTENER);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.spinner_dialog, null);

        if (!BookUtility.isNullOrEmpty(dialogTitle)){
            getDialog().setTitle(dialogTitle);
        }else if (dialogTitleId>-1){
            getDialog().setTitle(dialogTitleId);
        }

        mSpinner = (Spinner) view.findViewById (R.id.dialog_spinner);
        //        ArrayAdapter<T> adapter = new ArrayAdapter<T> (mContext, android.R.layout.simple_spinner_dropdown_item, mList);
        //        List<Book> data = BookLoggerDbHelper.getInstance(getContext()).getBookList(null);
        LazyBookAdapter bookListAdapter = new LazyBookAdapter(getContext(), mList, true);
        mSpinner.setAdapter(bookListAdapter);


        Button buttonOK = (Button) view.findViewById(R.id.dialogOK);
        Button buttonCancel = (Button) view.findViewById(R.id.dialogCancel);
        buttonOK.setOnClickListener(new android.view.View.OnClickListener(){
            public void onClick(View v) {
                mReadyListener.ready(mSpinner.getSelectedItem());
                SpinnerDialog.this.dismiss();
            }
        });
        buttonCancel.setOnClickListener(new android.view.View.OnClickListener(){
            public void onClick(View v) {
                mReadyListener.cancelled();
                SpinnerDialog.this.dismiss();
            }
        });

        return view;
    }
}