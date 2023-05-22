package com.example.movieapplication.features.moviefeature.view.adapters

import android.annotation.SuppressLint
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import javax.inject.Inject

class MediaAdapter<T : Any> @Inject constructor(): RecyclerView.Adapter<MediaViewHolder<T>>() {
    var listOfItems:MutableList<T>? = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
    var expressionViewHolderBinding: ((T, ViewBinding) -> Unit)? = null
    var expressionOnCreateViewHolder:((ViewGroup)-> ViewBinding)? = null

    private val diffCallBack = object : ItemCallback<T>() {


        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean {
            return newItem==oldItem
        }

        override fun areItemsTheSame(oldItem: T, newItem: T): Boolean {
            return newItem==oldItem
        }
    }

    val differ = AsyncListDiffer(this@MediaAdapter,diffCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaViewHolder<T> {
        return expressionOnCreateViewHolder?.let { it(parent) }?.let { MediaViewHolder(it, expressionViewHolderBinding!!) }!!
    }
    override fun onBindViewHolder(holder: MediaViewHolder<T>, position:Int){
        holder.bind(differ.currentList[position])
    }
    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}
