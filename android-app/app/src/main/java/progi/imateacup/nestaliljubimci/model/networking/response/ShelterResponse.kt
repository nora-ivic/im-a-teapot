package progi.imateacup.nestaliljubimci.model.networking.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class ShelterResponse (
    @SerialName("shelter_username") val username: String,
    @SerialName("shelter_name") val name: String,
    @SerialName("shelter_email") val email: String,
    @SerialName("shelter_phone_number") val phone_number: String
)