package ar.edu.unlam.mobile.movil22026h2.data.datasources.network.dto

import com.google.gson.annotations.SerializedName

    //(HU-09) Identificador del post. Lo necesitamos para abrir el detalle.
    data class PostDto(
        @SerializedName(value = "id", alternate = ["ID"])
        val id: Int? = null,
        @SerializedName(value = "message", alternate = ["Message"])
        val message: String? = null,
        @SerializedName(value = "parent_id", alternate = ["ParentID", "parentId"])
        val parentId: Int? = null,
        @SerializedName(value = "author", alternate = ["Author"])
        val author: String? = null,
        @SerializedName(value = "avatar_url", alternate = ["AvatarURL", "avatarUrl"])
        val avatarUrl: String? = null,
        @SerializedName(value = "likes", alternate = ["Likes"])
        val likes: Int? = null,
        @SerializedName(value = "liked", alternate = ["Liked"])
        val liked: Boolean? = null,
        @SerializedName(value = "date", alternate = ["Date"])
        val date: String? = null,
    )