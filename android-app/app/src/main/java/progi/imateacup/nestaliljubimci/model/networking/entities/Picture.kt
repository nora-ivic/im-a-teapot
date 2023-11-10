package progi.imateacup.nestaliljubimci.model.networking.entities

import kotlinx.serialization.SerialName
@kotlinx.serialization.Serializable
data class Picture(
    @SerialName("id") val id: String,
    @SerialName("advert_id") val advertId: String,
    @SerialName("link") val link: String,
    @SerialName("message_id") val messageId: String,
)