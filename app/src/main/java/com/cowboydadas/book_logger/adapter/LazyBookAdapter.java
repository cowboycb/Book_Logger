package com.cowboydadas.book_logger.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cowboydadas.book_logger.R;
import com.cowboydadas.book_logger.model.Book;
import com.cowboydadas.book_logger.util.BookUtility;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by ChnEmn on 4.03.2018.
 */

public class LazyBookAdapter extends BaseAdapter {

    private final boolean isSpinner;
    private Context context;
    private List<Book> data;
    private static LayoutInflater inflater = null;

    public LazyBookAdapter(Context context, List<Book> data, boolean isSpinner) {
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        this.isSpinner = isSpinner;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return ((Book) getItem(position)).getId();
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
//        if (isSpinner && position==0){
//            TextView tv = new TextView(context);
//            tv.setText(context.getString(R.string.pleaseSelectBook));
//            tv.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
//            return tv;
//        }
//        if (isSpinner && view != null && view instanceof TextView){
//            return view;
//        }


        if (view == null) {
            view = inflater.inflate(R.layout.book_list_row, null);
        }
        TextView txtBookName = view.findViewById(R.id.txtBookName); // title
        TextView txtBookAuthor = view.findViewById(R.id.txtBookAuthor); // artist name
        TextView txtFullCurPage = view.findViewById(R.id.txtFullCurrentPage); // duration
        ImageView book_thumb_image = view.findViewById(R.id.imgBookThumbnail); // thumb image

        Book book = (Book) getItem(position);

        txtBookName.setText(book.getTitle());
        txtBookAuthor.setText(book.getAuthor());
        txtFullCurPage.setText(book.getCurrentPage() + " / " + book.getTotalPage());
        if (!BookUtility.isNullOrEmpty(book.getCover())) {
            Picasso.with(context).setLoggingEnabled(true);
            Picasso.Builder builder = new Picasso.Builder(context);
            builder.listener(new Picasso.Listener() {
                @Override
                public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
//                    Toast.makeText(context, exception.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e(BookUtility.LOG_TAG, "Resim Yüklenemedi", exception);
                    exception.printStackTrace();
                }
            });

            if (book.getCover() != null) {
                Log.e(BookUtility.LOG_TAG, "Resim :"+book.getCover());
                Picasso.with(context).cancelRequest(book_thumb_image);
                builder.build().load("file:" + book.getCover())
                        .resizeDimen(R.dimen.thumbnail_width, R.dimen.thumbnail_height)
                        .centerCrop()
                        .into(book_thumb_image);
//            Picasso.with(context)
//                    .load("file:"+book.getCover())
//                    .resizeDimen(R.dimen.thumbnail_width, R.dimen.thumbnail_height)
//                    .centerCrop()
//                    .into(book_thumb_image);
//            Bitmap image = BookUtility.getReducedImage(book.getCover(), book_thumb_image.getWidth(), book_thumb_image.getHeight());
//            book_thumb_image.setImageBitmap(image);
                book_thumb_image.setTag(book.getCover());
            }
        }
        return view;
    }
}
