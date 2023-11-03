package progi.imateacup.nestaliljubimci.model.networking.request.auth

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class RegisterRequest (
    @SerialName("username")     val username: String,
    @SerialName("password")     val password: String,           //min len 7
    @SerialName("first_name")   val first_name: String?,         //max len 50
    @SerialName("last_name")    val last_name: String?,          //max len 50
    @SerialName("is_shelter")   val is_shelter: Boolean,
    @SerialName("shelter_name") val shelter_name: String?,      //max len 100
    @SerialName("email")        val email: String,
    @SerialName("phone_number") val phone_number: String        //max len 15, min len 6
)