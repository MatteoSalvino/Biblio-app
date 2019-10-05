package com.example.biblio.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.biblio.R;
import com.example.biblio.models.Book;

import java.util.List;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {
    private List<Book> mDataset;
    private OnItemListener itemListener;

    public MyAdapter(List<Book> myDataset, OnItemListener listener){
        this.mDataset = myDataset;
        this.itemListener = listener;
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
        holder.mBookTitle.setText(mDataset.get(position).getTitle());
        holder.mBookAuthor.setText(mDataset.get(position).getAuthor());
        holder.mBookCover.setImageResource(R.drawable.no_image);
        //holder.mBookCover.setImageURI(Uri.parse(mDataset.get(position).getCover_image()));
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView mBookTitle;
        public TextView mBookAuthor;
        public ImageView mBookCover;
        OnItemListener itemListener;


        public MyViewHolder(View v, OnItemListener listener) {
            super(v);
            mBookTitle = v.findViewById(R.id.book_title);
            mBookAuthor = v.findViewById(R.id.book_author);
            mBookCover = v.findViewById(R.id.book_cover);
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
