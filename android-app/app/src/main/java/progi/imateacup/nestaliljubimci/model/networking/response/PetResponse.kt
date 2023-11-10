package progi.imateacup.nestaliljubimci.model.networking.response

import kotlinx.serialization.SerialName
import progi.imateacup.nestaliljubimci.model.networking.entities.Pet

@kotlinx.serialization.Serializable
data class PetResponse(
    @SerialName("pet") val pet: Pet
)

