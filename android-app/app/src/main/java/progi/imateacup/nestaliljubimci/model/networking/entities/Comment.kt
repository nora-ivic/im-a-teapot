package progi.imateacup.nestaliljubimci.model.networking.entities

import kotlinx.serialization.SerialName
@kotlinx.serialization.Serializable
data class Comment(
    @SerialName("id") val id: String,
    @SerialName("user_id") val userId: String,
    @SerialName("advert_id") val advertId: String,
    @SerialName("text") val text: String,
    @SerialName("picture_id") val pictureId: String,
    @SerialName("location") val location: String,
)