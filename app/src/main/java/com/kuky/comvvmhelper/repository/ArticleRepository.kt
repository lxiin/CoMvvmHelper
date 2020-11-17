package com.kuky.comvvmhelper.repository

import com.kuky.comvvmhelper.entity.ArticleDetail
import com.kuky.comvvmhelper.helper.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author kuky.
 * @description
 */
class ArticleRepository(private val apiService: ApiService) {

    suspend fun loadArticleByPage(page: Int): MutableList<ArticleDetail> =
        withContext(Dispatchers.IO) {
            apiService.requestArticles(page).data.datas ?: mutableListOf()
        }
}