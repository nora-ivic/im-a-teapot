package progi.imateacup.nestaliljubimci.model.networking.response

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class ImageUploadResponse(
    @SerialName("link") val link: String
)