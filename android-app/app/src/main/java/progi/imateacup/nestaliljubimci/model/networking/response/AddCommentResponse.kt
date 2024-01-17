package progi.imateacup.nestaliljubimci.model.networking.response

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class AddCommentResponse(
    @SerialName("detail") val detail: String
)