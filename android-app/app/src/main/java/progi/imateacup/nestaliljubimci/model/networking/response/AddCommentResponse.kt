package progi.imateacup.nestaliljubimci.model.networking.response

import kotlinx.serialization.SerialName
import progi.imateacup.nestaliljubimci.model.networking.entities.Comment

@kotlinx.serialization.Serializable
data class AddCommentResponse(
    @SerialName("comment") val comment: Comment
)