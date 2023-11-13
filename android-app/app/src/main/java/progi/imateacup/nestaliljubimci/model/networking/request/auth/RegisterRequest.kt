package progi.imateacup.nestaliljubimci.model.networking.request.auth

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class RegisterRequest (
    @SerialName("username")     val username: String,
    @SerialName("password")     val password: String,           //min len 7
    @SerialName("first_name")   val firstName: String?,         //max len 50
    @SerialName("last_name")    val lastName: String?,          //max len 50
    @SerialName("is_shelter")   val isShelter: Boolean,
    @SerialName("shelter_name") val shelterName: String?,       //max len 100
    @SerialName("email")        val email: String,
    @SerialName("phone_number") val phone: String               //max len 15, min len 6
)