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
import com.example.biblio.adapters.EbooksAdapter.EbooksViewHolder
import lrusso96.simplebiblio.core.Ebook

/**
 * RecyclerView adapter to manage and show ebooks.
 */
class EbooksAdapter(private val ebooks: List<Ebook>, private val itemListener: OnItemListener, private val context: Context) : RecyclerView.Adapter<EbooksViewHolder>() {
    private val cropOptions: RequestOptions = RequestOptions().centerCrop()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EbooksViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_ebook, parent, false)
        return EbooksViewHolder(view, itemListener)
    }

    override fun onBindViewHolder(holder: EbooksViewHolder, position: Int) {
        val elem = ebooks[position]
        holder.ebookTitle.text = elem.title
        holder.ebookAuthor.text = elem.author
        val numPages = elem.pages
        holder.ebookPages.text = if (numPages == 0) "-" else "$numPages"
        if (elem.cover != null) Glide.with(context).load(elem.cover.toString()).placeholder(R.drawable.no_image).apply(cropOptions).into(holder.ebookCover) else Glide.with(context).load(R.drawable.no_image).apply(cropOptions).into(holder.ebookCover)
        holder.ebookSource.text = ebooks[position].providerName
    }

    override fun getItemCount() = ebooks.size

    interface OnItemListener {
        fun onItemClick(position: Int)
    }

    class EbooksViewHolder internal constructor(view: View, listener: OnItemListener) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val ebookTitle: TextView = view.findViewById(R.id.title)
        val ebookAuthor: TextView = view.findViewById(R.id.author)
        val ebookCover: ImageView = view.findViewById(R.id.cover)
        val ebookPages: TextView = view.findViewById(R.id.pages)
        val ebookSource: TextView = view.findViewById(R.id.source)
        private val itemListener: OnItemListener = listener

        override fun onClick(view: View) {
            itemListener.onItemClick(adapterPosition)
        }

        init {
            view.setOnClickListener(this)
        }
    }
}