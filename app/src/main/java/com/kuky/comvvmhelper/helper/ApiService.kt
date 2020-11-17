package com.kuky.comvvmhelper.helper

import com.kuky.comvvmhelper.entity.ArticleData
import com.kuky.comvvmhelper.entity.BaseResultData
import com.kuky.comvvmhelper.entity.WebsiteData
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * @author kuky.
 * @description
 */
interface ApiService {

    @GET("friend/json")
    suspend fun requestRepositoryInfo(): BaseResultData<MutableList<WebsiteData>>

    @GET("/article/list/{page}/json")
    suspend fun requestArticles(@Path("page") page: Int): BaseResultData<ArticleData>
}