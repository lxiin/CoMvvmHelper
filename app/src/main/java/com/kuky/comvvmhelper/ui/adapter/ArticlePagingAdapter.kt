package com.kuky.comvvmhelper.ui.adapter

import com.kk.android.comvvmhelper.ui.BasePagingAdapter
import com.kk.android.comvvmhelper.ui.BaseRecyclerViewHolder
import com.kuky.comvvmhelper.R
import com.kuky.comvvmhelper.databinding.RecyclerStringItemBinding
import com.kuky.comvvmhelper.entity.ArticleDetail

/**
 * @author kuky.
 * @description
 */
class ArticlePagingAdapter : BasePagingAdapter<ArticleDetail>() {

    override fun layoutId(viewType: Int) = R.layout.recycler_string_item

    override fun setVariable(data: ArticleDetail, position: Int, holder: BaseRecyclerViewHolder) {
        holder.viewDataBinding<RecyclerStringItemBinding>()?.run {
            text = data.title
        }
    }
}