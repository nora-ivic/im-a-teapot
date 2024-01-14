package progi.imateacup.nestaliljubimci.model.networking.response

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class CreateAdvertResponse (
    @SerialName("token") val token: String
)