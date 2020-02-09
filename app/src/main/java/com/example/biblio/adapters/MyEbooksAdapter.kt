package com.example.biblio.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.biblio.R
import com.example.biblio.adapters.MyEbooksAdapter.MyEbooksViewHolder
import com.google.android.material.button.MaterialButton
import lrusso96.simplebiblio.core.Ebook

class MyEbooksAdapter(private val ebooks: List<Ebook>, private val itemListener: OnItemListener, private val context: Context) : RecyclerView.Adapter<MyEbooksViewHolder>() {
    private val cropOptions = RequestOptions().centerCrop()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyEbooksViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_my_ebook, parent, false)
        return MyEbooksViewHolder(view, itemListener)
    }

    override fun onBindViewHolder(holder: MyEbooksViewHolder, position: Int) {
        val elem = ebooks[position]
        holder.ebookTitle.text = elem.title
        holder.ebookAuthor.text = elem.author
        val numPages = elem.pages
        holder.ebookPages.text = if (numPages == 0) "-" else "$numPages"
        if (elem.cover != null) Glide.with(context).load(elem.cover.toString()).placeholder(R.drawable.no_image).apply(cropOptions).into(holder.ebookCover) else Glide.with(context).load(R.drawable.no_image).apply(cropOptions).into(holder.ebookCover)
    }

    override fun getItemCount() = ebooks.size

    interface OnItemListener {
        fun onItemClick(position: Int)
        fun onReadButtonClick(position: Int)
    }

    class MyEbooksViewHolder internal constructor(view: View, listener: OnItemListener) : RecyclerView.ViewHolder(view) {
        val ebookTitle: TextView = view.findViewById(R.id.title)
        val ebookAuthor: TextView = view.findViewById(R.id.author)
        val ebookCover: ImageView = view.findViewById(R.id.cover)
        val ebookPages: TextView = view.findViewById(R.id.pages)
        private val readButton: MaterialButton = view.findViewById(R.id.read_btn)
        private val itemListener: OnItemListener = listener

        init {
            readButton.setOnClickListener { itemListener.onReadButtonClick(adapterPosition) }
            view.setOnClickListener { itemListener.onItemClick(adapterPosition) }
        }
    }

}