package progi.imateacup.nestaliljubimci.model.networking.response

import kotlinx.serialization.SerialName
import progi.imateacup.nestaliljubimci.model.networking.entities.Shelter

@kotlinx.serialization.Serializable
data class ShelterResponse(
    @SerialName("shelter") val shelter: Shelter
)
