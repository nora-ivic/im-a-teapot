package progi.imateacup.nestaliljubimci.model.networking.entities

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class LoginResponse (
    @SerialName("token") val token: String
)