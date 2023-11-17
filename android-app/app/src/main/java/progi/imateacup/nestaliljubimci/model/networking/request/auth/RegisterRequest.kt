package progi.imateacup.nestaliljubimci.model.networking.request.auth

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class RegisterRequest (
    @SerialName("username")     val username: String,
    @SerialName("password")     val password: String,
    @SerialName("first_name")   val firstName: String?,
    @SerialName("last_name")    val lastName: String?,
    @SerialName("is_shelter")   val isShelter: Boolean,
    @SerialName("shelter_name") val shelterName: String?,
    @SerialName("email")        val email: String,
    @SerialName("phone_number") val phone: String
)