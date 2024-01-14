package progi.imateacup.nestaliljubimci.model.networking.entities

import kotlinx.serialization.SerialName
@kotlinx.serialization.Serializable
data class Comment(
    @SerialName("username") val username: String,
    @SerialName("text") val text: String,
    @SerialName("location") val location: String,
    @SerialName("picture_links") val pictureLinks: List<String>,
)