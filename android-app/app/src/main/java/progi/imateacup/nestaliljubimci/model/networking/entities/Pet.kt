package progi.imateacup.nestaliljubimci.model.networking.entities

import kotlinx.serialization.SerialName
@kotlinx.serialization.Serializable
data class Pet(
    @SerialName("id") val id: String,
    @SerialName("species") val species: String,
    @SerialName("name") val name: String,
    @SerialName("color") val color: String,
    @SerialName("age") val age: String,
    @SerialName("date_time_lost") val dateTimeLost: String,
    @SerialName("location_lost") val locationLost: String,
    @SerialName("description") val description: String,
)
