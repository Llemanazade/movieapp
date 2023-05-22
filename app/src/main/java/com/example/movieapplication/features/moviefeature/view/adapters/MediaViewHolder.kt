package com.example.movieapplication.features.moviefeature.view.adapters

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

class MediaViewHolder<T> internal constructor(private val binding: ViewBinding, private val experssion:(T, ViewBinding)->Unit)
    : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: T) {
        experssion(item, binding)
    }
}