package com.example.movieapplication.features.moviefeature.view.adapters

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import androidx.viewpager.widget.PagerAdapter
import java.lang.reflect.Array.set
import javax.inject.Inject

class MediaPagerAdapter < T : Any> : PagingDataAdapter<T, MediaViewHolder<T>>(
    object : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
            return oldItem == newItem
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
            return oldItem == newItem
        }

    }
) {
    var expressionViewHolderBinding: ((T, ViewBinding) -> Unit)? = null
    var expressionOnCreateViewHolder:((ViewGroup)-> ViewBinding)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder<T> {
        return expressionOnCreateViewHolder?.let { it(parent) }?.let { MediaViewHolder(it, expressionViewHolderBinding!!) }!!
    }

    override fun onBindViewHolder(holder: MediaViewHolder<T>, position:Int){
        getItem(position)?.let { holder.bind(it) }
    }
}