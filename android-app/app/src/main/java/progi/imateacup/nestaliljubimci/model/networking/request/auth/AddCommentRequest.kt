package progi.imateacup.nestaliljubimci.model.networking.request.auth

import kotlinx.serialization.SerialName
@kotlinx.serialization.Serializable
data class AddCommentRequest(
    @SerialName("user_id") val userId: String,
    @SerialName("advert_id") val advertId: String,
    @SerialName("text") val text: String,
    @SerialName("picture_id") val pictureId: String,
    @SerialName("location") val location: String,
)