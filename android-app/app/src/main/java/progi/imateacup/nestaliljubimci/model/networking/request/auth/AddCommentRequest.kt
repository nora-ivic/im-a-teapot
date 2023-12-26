package progi.imateacup.nestaliljubimci.model.networking.request.auth

import kotlinx.serialization.SerialName
@kotlinx.serialization.Serializable
data class AddCommentRequest(
    @SerialName("user_id") val userId: Int,
    @SerialName("advert_id") val advertId: Int,
    @SerialName("text") val text: String,
    @SerialName("picture_id") val pictureId: Int,
    @SerialName("location") val location: String,
)