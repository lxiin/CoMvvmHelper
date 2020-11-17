package com.kk.android.comvvmhelper.ui

import android.util.SparseIntArray
import androidx.core.util.isNotEmpty
import androidx.recyclerview.widget.DiffUtil
import com.kk.android.comvvmhelper.listener.MultiLayoutImp

/**
 * @author kuky.
 * @description
 */
abstract class BaseMultiLayoutPagingAdapter(
    callback: DiffUtil.ItemCallback<MultiLayoutImp>,
    openDebounce: Boolean = true, debounceDuration: Long = 300
) : BasePagingAdapter<MultiLayoutImp>(callback, openDebounce, debounceDuration) {

    private val mLayouts = SparseIntArray()

    fun registerAdapterItems(viewType: Int, layoutId: Int) {
        mLayouts.put(viewType, layoutId)
    }

    override fun layoutId(viewType: Int): Int {
        check(mLayouts.isNotEmpty()) { "do you have register your viewType and layoutId?" }
        return mLayouts[viewType]
    }

    override fun getItemViewType(position: Int) = getItem(position)?.viewType() ?: -1
}