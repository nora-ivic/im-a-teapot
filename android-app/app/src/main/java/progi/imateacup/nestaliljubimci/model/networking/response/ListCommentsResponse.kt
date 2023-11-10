package progi.imateacup.nestaliljubimci.model.networking.response

import kotlinx.serialization.SerialName
import progi.imateacup.nestaliljubimci.model.networking.entities.Comment

@kotlinx.serialization.Serializable
data class ListCommentsResponse(
    @SerialName("comments") val comments: List<Comment>
)

@kotlinx.serialization.Serializable
data class Comment(
    @SerialName("id") val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("advert_id") val advertId: String,
    @SerialName("text") val text: String,
    @SerialName("picture_id") val pictureId: String,
    @SerialName("location") val location: String,
)