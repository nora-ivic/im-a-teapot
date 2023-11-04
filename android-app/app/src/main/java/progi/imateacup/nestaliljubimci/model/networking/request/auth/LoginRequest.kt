package progi.imateacup.nestaliljubimci.model.networking.request.auth

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class LoginRequest (
    @SerialName("username") val username: String,
    @SerialName("password") val password: String
)