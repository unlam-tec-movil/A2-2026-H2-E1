package ar.edu.unlam.mobile.movil22026h2.data.datasources.network.dto

import com.google.gson.annotations.SerializedName

/**
 * Post tal como lo devuelve GET /api/v1/me/feed.
 * La API usa PascalCase, por eso cada campo necesita @SerializedName.
 */
data class FeedPostDto(
    @SerializedName("ID") val id: Int,
    @SerializedName("Message") val message: String,
    @SerializedName("ParentID") val parentId: Int,
    @SerializedName("Author") val author: String,
    @SerializedName("AvatarURL") val avatarUrl: String,
    @SerializedName("Likes") val likes: Int,
    @SerializedName("Liked") val liked: Boolean,
    @SerializedName("Date") val date: String,
)
