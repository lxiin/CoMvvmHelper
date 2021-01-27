package com.kk.android.comvvmhelper.ui

import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import com.kk.android.comvvmhelper.listener.OnRecyclerItemClickListener
import com.kk.android.comvvmhelper.listener.OnRecyclerItemLongClickListener

/**
 * @author kuky.
 * @description
 */

abstract class BasePagingAdapter<T : Any> : PagingDataAdapter<T, BaseRecyclerViewHolder> {

    constructor(callback: DiffUtil.ItemCallback<T>) : super(callback)

    constructor(
        areSameItems: (old: T, new: T) -> Boolean = { _, _ -> false },
        areSameContent: (old: T, new: T) -> Boolean = { _, _ -> false }
    ) : this(object : DiffUtil.ItemCallback<T>() {
        override fun areItemsTheSame(oldItem: T, newItem: T) = areSameItems(oldItem, newItem)
        override fun areContentsTheSame(oldItem: T, newItem: T) = areSameContent(oldItem, newItem)
    })

    var onItemClickListener: OnRecyclerItemClickListener? = null
    var onItemLongClickListener: OnRecyclerItemLongClickListener? = null

    override fun onBindViewHolder(holder: BaseRecyclerViewHolder, position: Int) {
        val data = getItem(position) ?: return
        setVariable(data, position, holder)
        holder.binding.executePendingBindings()

        holder.binding.root.let {
            it.setOnClickListener { v ->
                onItemClickListener?.onRecyclerItemClick(position, v)
            }

            it.setOnLongClickListener { v ->
                onItemLongClickListener?.onRecyclerItemLongClick(position, v) ?: false
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseRecyclerViewHolder {
        return BaseRecyclerViewHolder.createHolder(parent, layoutId(viewType))
    }

    abstract fun layoutId(viewType: Int): Int

    abstract fun setVariable(data: T, position: Int, holder: BaseRecyclerViewHolder)

    open fun getItemData(position: Int): T? = getItem(position)
}