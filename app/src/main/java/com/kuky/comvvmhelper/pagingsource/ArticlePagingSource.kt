package com.kuky.comvvmhelper.pagingsource

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.kk.android.comvvmhelper.extension.otherwise
import com.kk.android.comvvmhelper.extension.yes
import com.kk.android.comvvmhelper.utils.LogUtils
import com.kuky.comvvmhelper.entity.ArticleDetail
import com.kuky.comvvmhelper.repository.ArticleRepository

/**
 * @author kuky.
 * @description
 */

class ArticlePagingSource(private val repository: ArticleRepository) : PagingSource<Int, ArticleDetail>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ArticleDetail> {
        val page = params.key ?: 0
        return try {
            val articles = repository.loadArticleByPage(page)
            LoadResult.Page(
                data = articles,
                prevKey = (page == 0).yes { null }.otherwise { page - 1 },
                nextKey = articles.isEmpty().yes { null }.otherwise { page + 1 }
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ArticleDetail>): Int? = state.anchorPosition
}