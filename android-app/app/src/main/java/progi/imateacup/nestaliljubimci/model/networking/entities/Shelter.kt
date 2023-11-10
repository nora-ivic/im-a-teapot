package progi.imateacup.nestaliljubimci.model.networking.entities

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
data class Shelter(
    @SerialName("id") val id: String,
)
