package com.example.biblio.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.biblio.R;

import org.w3c.dom.Text;

import java.util.List;
import lrusso96.simplebiblio.core.Ebook;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private List<Ebook> mDataset;
    private OnItemListener itemListener;
    private RequestOptions option;
    private Context mContext;

    public MyAdapter(List<Ebook> myDataset, OnItemListener listener, Context context){
        this.mDataset = myDataset;
        this.itemListener = listener;
        this.mContext = context;
        option = new RequestOptions().centerCrop();
    }

    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_row, parent, false);

        MyViewHolder vh = new MyViewHolder(v, itemListener);
        return vh;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Ebook elem = mDataset.get(position);
        holder.mBookTitle.setText(elem.getTitle());
        holder.mBookAuthor.setText(elem.getAuthor());
        Integer num_pages = elem.getPages();
        holder.mBookPages.setText("n° pages : " + ((num_pages == 0) ? "-" : String.valueOf(num_pages)));
        if(elem.getCover() != null)
            Glide.with(mContext).load(elem.getCover().toString()).placeholder(R.drawable.no_image).apply(option).into(holder.mBookCover);
        else
            Glide.with(mContext).load(R.drawable.no_image).apply(option).into(holder.mBookCover);

        holder.mBookSource.setText(mDataset.get(position).getSource());
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView mBookTitle;
        public TextView mBookAuthor;
        public ImageView mBookCover;
        public TextView mBookPages;
        public TextView mBookSource;
        OnItemListener itemListener;


        public MyViewHolder(View v, OnItemListener listener) {
            super(v);
            mBookTitle = v.findViewById(R.id.book_title);
            mBookAuthor = v.findViewById(R.id.book_author);
            mBookCover = v.findViewById(R.id.book_cover);
            mBookPages = v.findViewById(R.id.book_pages);
            mBookSource = v.findViewById(R.id.book_source);
            this.itemListener = listener;

            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            itemListener.onItemClick(getAdapterPosition());
        }
    }

    public interface OnItemListener {
        void onItemClick(int position);
    }

}
