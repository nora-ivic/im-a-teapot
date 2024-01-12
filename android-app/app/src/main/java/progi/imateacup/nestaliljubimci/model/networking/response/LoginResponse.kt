package progi.imateacup.nestaliljubimci.model.networking.response

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class LoginResponse (
    @SerialName("token") val token: String,
    @SerialName("is_shelter") val isShelter: Boolean,
    @SerialName("current_user_username") val username: String,
    @SerialName("current_user_email") val email: String,
    @SerialName("current_user_phone_number") val phoneNumber: String
)