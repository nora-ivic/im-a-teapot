package progi.imateacup.nestaliljubimci.model.networking.request.auth

import kotlinx.serialization.SerialName
@kotlinx.serialization.Serializable
data class AddCommentRequest(
    @SerialName("text") val text: String,
    @SerialName("location") val location: String,
    @SerialName("picture_links") val pictureLinks: List<String>,
)