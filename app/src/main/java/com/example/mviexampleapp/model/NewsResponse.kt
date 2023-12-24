package com.example.mviexampleapp.model

import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.example.mviexampleapp.utils.Constant
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class NewsResponse(
    @SerializedName("status")
    val status: String?,
    @SerializedName("totalResults")
    val totalResults: Int?,
    @SerializedName("articles")
    val articles: List<Articles>?
)

@Serializable
@Entity(tableName = Constant.TABLE_NAME_ARTICLES)
data class Articles(
    @PrimaryKey(autoGenerate = true)
    @SerialName("id")
    val id: Int = 0,
//    @SerialName("source")
//    @SerializedName("source")
//    val source: Source?,
    @SerialName("author")
    @SerializedName("author")
    val author: String? = null,
    @SerialName("title")
    @SerializedName("title")
    val title: String?= null,
    @SerialName("description")
    @SerializedName("description")
    val description: String?= null,
    @SerialName("url")
    @SerializedName("url")
    val url: String?= null,
    @SerialName("urlToImage")
    @SerializedName("urlToImage")
    val urlToImage: String?= null,
    @SerialName("publishedAt")
    @SerializedName("publishedAt")
    val publishedAt: String?= null,
    @SerialName("content")
    @SerializedName("content")
    val content: String?= null,
    @SerialName("isFavourite")
    @SerializedName("isFavourite")
    var isFavourite: Boolean = false
)

//@Serializable
//@Entity
//data class Source(
//    @SerializedName("id")
//    val sourceId: String?,
//    @SerializedName("name")
//    val name: String?
//)