package progi.imateacup.nestaliljubimci.model.networking.response

import kotlinx.serialization.SerialName
import progi.imateacup.nestaliljubimci.model.networking.entities.Comment

@kotlinx.serialization.Serializable
data class ListCommentsResponse(
    @SerialName("comments") val comments: List<Comment>
)

@kotlinx.serialization.Serializable
data class Comment(
    @SerialName("username") val username: String,
    @SerialName("text") val text: String,
    @SerialName("location") val location: String,
    @SerialName("picture_link") val pictureLink: String
)